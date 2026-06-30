# Statik ve Dinamik Bağlama (Static & Dynamic Binding)

Bir metot çağrısı yazdığında — `nesne.metot()` — Java'nın hangi gerçek metodu çalıştıracağına
karar vermesi gerekir. Bu karara **bağlama (binding)** denir ve iki zamanda olabilir: **derleme
anında (statik/early binding)** veya **çalışma anında (dinamik/late binding)**. Bu ayrımı anlamak,
polimorfizmin nasıl çalıştığını ve bazı sinsi tuzakları (alan gizleme gibi) kavramak için
şarttır — ve teknik mülakatların klasik sorularındandır.

## Statik bağlama (derleme anında)

Derleyici, çağrılacak metodu **derleme anında**, **referans tipine** bakarak belirler. Şunlar
statik bağlanır:

- **Overloaded (aşırı yüklenmiş) metotlar:** Hangi sürümün çağrılacağı, argümanın derleme-zamanı
  tipine göre seçilir.
- **`static` metotlar**
- **`private` metotlar** (alt sınıftan görünmez)
- **`final` metotlar** (ezilemez)

```java
static void yaz(int x) {...}    static void yaz(String x) {...}
yaz(5);      // derleyici int sürümünü seçer (statik)
yaz("beş");  // String sürümü
```

Örnek 1 (`./Ornek1.java`) overloading'i ve `static` metotların referans tipine göre çözüldüğünü
gösterir. Bunlar **polimorfik değildir**; alt sınıf bir `static` metodu "ezemez", yalnızca
**gizler (hides)**.

## Dinamik bağlama (çalışma anında)

**Override edilmiş (instance) metotlar** ise **çalışma anında**, nesnenin **gerçek tipine** göre
çözülür. Bu, polimorfizmin motorudur: JVM, her nesnenin gerçek sınıfına ait metodu bir "sanal
metot tablosu" (vtable) üzerinden bulur.

```java
Sekil s = new Daire(2);   // referans: Sekil, gerçek: Daire
s.alan();                 // Daire.alan() çalışır (gerçek tipe göre — dinamik)
```

Örnek 2 (`./Ornek2.java`) üst tiple (`Sekil`) yazılan kodun her alt tipin (`Daire`/`Kare`) kendi
`alan()` davranışını çalıştırdığını gösterir. Bu sayede "üst tiple programla, alt tip davransın"
mümkün olur.

## Klasik tuzak: alan gizleme (field hiding)

İşte en sık karıştırılan nokta: **alanlar polimorfik değildir.** Alan erişimi **statik bağlanır**
(referans tipine göre), instance metotlar ise **dinamik bağlanır** (gerçek tipe göre):

```java
class Ata  { String tip = "ata";  String tipMetot() { return "ata"; } }
class Cocuk extends Ata { String tip = "cocuk"; String tipMetot() { return "cocuk"; } }

Ata ref = new Cocuk();
ref.tip;        // "ata"   <- ALAN: referans tipine göre (statik)
ref.tipMetot(); // "cocuk" <- METOT: gerçek tipe göre (dinamik)
```

Örnek 3 (`./Ornek3.java`) bu tuzağı canlı gösterir. **Kural:** Alanları override etmeye çalışma;
davranış için her zaman metot kullan ve alanları `private` tut (zaten erişim metotlarından geçer,
tuzak oluşmaz).

## Karşılaştırma

| | Statik bağlama | Dinamik bağlama |
|---|----------------|-----------------|
| Ne zaman | Derleme anında | Çalışma anında |
| Neye göre | Referans tipi | Nesnenin gerçek tipi |
| Kapsam | overload, static, private, final, **alanlar** | override edilmiş **instance metotlar** |
| Polimorfik mi | Hayır | Evet |
| Performans | Biraz daha hızlı | Çok küçük ek maliyet (vtable) |

## Özet

İki bağlama türünü öğrendik: derleme-anında, referans-tipine göre **statik bağlama** (overloading,
static/private/final metotlar ve alanlar — Örnek 1) ve çalışma-anında, gerçek-tipe göre **dinamik
bağlama** (override edilmiş instance metotlar — polimorfizm; Örnek 2). Alan gizleme tuzağını
(alanlar statik, metotlar dinamik — Örnek 3) gördük. Bu kavram, polimorfizmin "kaputun altında"
nasıl çalıştığını açıklar. Sırada, nesne kurulurken kodun hangi sırayla çalıştığı: **başlatma
blokları**.
