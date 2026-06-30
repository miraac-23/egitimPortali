# Kalıtım ve Çok Biçimlilik

OOP'nin temellerinde nesneleri sınıflarla modellemeyi gördük. Şimdi iki güçlü mekanizmaya
geçiyoruz: **kalıtım** sayesinde var olan bir sınıfın özelliklerini yeniden kullanır,
**çok biçimlilik** sayesinde farklı nesneleri tek bir tip üzerinden, ortak bir dille
yönetiriz. Bu ikisi, esnek ve genişletilebilir tasarımın anahtarıdır.

## Kalıtım (inheritance)

Birbirine benzeyen sınıflar yazarken aynı kodu tekrarlamak istemeyiz. Ortak alan ve
davranışları bir **üst sınıfta** toplar, özel olanları **alt sınıflarda** tanımlarız. Alt
sınıf, üst sınıfı `extends` ile genişletir ve onun her şeyini miras alır:

```java
class Hayvan {
    protected String ad;
    void nefesAl() { ... }
}
class Kopek extends Hayvan {
    Kopek(String ad, int yas) { super(ad, yas); }
}
```

`super`, üst sınıfa erişmenin yoludur: `super(...)` üst sınıfın constructor'ını çağırır,
`super.metot()` ise üst sınıftaki bir metodu çağırır. `protected` erişim belirleyicisi,
bir alanın alt sınıflar tarafından görülebilmesini sağlar.

### Metot ezme (override) ve Object metotları

Alt sınıf, miras aldığı bir metodu kendi ihtiyacına göre yeniden yazabilir; buna
**override** denir ve `@Override` ile işaretleriz. Java'daki her sınıf gizlice `Object`'ten
türer; en sık ezdiğimiz `Object` metodu `toString()`'tir. Onu ezdiğinde, nesneyi
yazdırdığında okunabilir bir metin alırsın:

```java
@Override
public String toString() {
    return "Kopek{ad=" + ad + "}";
}
```

Örnek 1 (`./Ornek1.java`) `Hayvan` üst sınıfından `Kopek` ve `Kedi` türetir; `super` ile
constructor çağırır, `sesCikar()` metodunu ezer ve `toString()` ile anlamlı çıktı üretir.

## Çok biçimlilik (polymorphism)

Çok biçimlilik, "tek arayüz, çok davranış" demektir. Bir üst tip referansıyla farklı alt
tipleri tutabilirsin; bir metodu çağırdığında ise nesnenin **gerçek tipine** ait sürüm
çalışır. Buna **dinamik bağlama** denir:

```java
List<Sekil> sekiller = List.of(new Daire(3), new Kare(4));
for (Sekil s : sekiller) {
    s.alan(); // Daire için daire alanı, Kare için kare alanı çalışır
}
```

Burada döngü `Sekil` ile konuşur ama her nesne kendi `alan()` hesabını yapar. Yeni bir şekil
eklemek istediğinde döngüyü değiştirmen gerekmez — yalnızca yeni bir sınıf eklersin. Örnek 2
(`./Ornek2.java`) şekillerin alanını çok biçimli biçimde toplar.

## instanceof ve pattern matching

Bazen elindeki üst tip referansının hangi alt tip olduğunu öğrenmen gerekir. `instanceof`
bunu kontrol eder; modern **pattern matching** (Java 16+) ise kontrol ve dönüştürmeyi tek
adımda yapıp otomatik bir değişken tanımlar:

```java
if (o instanceof KrediKarti kk) {
    // kk burada doğrudan KrediKarti tipinde kullanılabilir
    System.out.println(kk.kartNo());
}
```

### equals ve hashCode

`==` iki referansın **aynı nesne** olup olmadığına bakar. Çoğu zaman istediğimiz ise
**mantıksal eşitlik**: "değerleri aynı mı?". Bunun için `equals()` (ve onunla uyumlu
`hashCode()`) metotlarını ezeriz. `Objects.hash(...)` ve `Objects.equals(...)` bu işi
güvenli ve kısa hale getirir. Örnek 3 (`./Ornek3.java`) ödeme türlerinde pattern matching'i
ve değer bazlı eşitliği gösterir.

## Özet

Kalıtımla kodu yeniden kullandık, `super` ve `@Override` ile üst/alt sınıf ilişkisini
yönettik. Çok biçimlilikle farklı nesneleri tek tip üzerinden çalıştırdık; `instanceof`
pattern matching ve `equals/hashCode` ile tip ve eşitlik denetimini öğrendik. Sırada,
"ne yapılacağını" tanımlayıp "nasıl"ı alt sınıflara bırakan **soyutlama** var.
