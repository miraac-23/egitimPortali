# Başlatma Blokları (Initializer Blocks)

Bir nesne `new` ile oluşturulurken veya bir sınıf ilk kez yüklenirken, alanların belirli
değerlerle hazırlanması gerekir. Çoğu zaman bunu constructor'da veya alan tanımında yaparız. Ama
Java iki ek araç sunar: **örnek başlatma bloğu (instance initializer block)** ve **statik başlatma
bloğu (static initializer block)**. Bunlar, basit atamalarla yapılamayan başlatma mantığını temiz
bir şekilde toplamayı sağlar. Bu konu aynı zamanda "nesne kurulurken kod hangi sırayla çalışır?"
sorusunu netleştirir.

## Instance initializer block

Süslü parantezler içinde, herhangi bir metot dışında yazılan koddur (`{ ... }`). **Her `new`'de**,
constructor gövdesinden **önce** çalışır:

```java
class Urun {
    private int x;
    { x = hesapla(); System.out.println("instance blok"); } // her new'de
    Urun() { System.out.println("constructor"); }
}
```

Ne işe yarar? **Birden çok constructor'ın paylaştığı** başlatma kodunu tek yerde toplamak. Üç
farklı constructor'ın hepsinde tekrar edilecek kodu, bir instance bloğa koyarsın; blok hepsinden
önce çalışır. Örnek 1 (`./Ornek1.java`) bunu ve tam başlatma sırasını gösterir.

## Static initializer block

`static { ... }` bloğu, sınıf **ilk yüklendiğinde bir kez** çalışır (tüm static alanlardan sonra,
herhangi bir nesne oluşturulmadan veya static metot çağrılmadan önce). Tek satırda atanamayan
**karmaşık statik başlatma** içindir:

```java
class DovizTablosu {
    private static final Map<String,Double> KURLAR;
    static {                                  // bir kez, sınıf yüklenince
        Map<String,Double> m = new HashMap<>();
        m.put("USD", 32.50); m.put("EUR", 35.10);
        KURLAR = Collections.unmodifiableMap(m);
    }
}
```

Kullanım: sabit tablolar/haritalar kurmak, yapılandırma/kaynak yüklemek, hesaplanmış sabitler
üretmek. Basit atamalar için gerek yoktur (`static int X = 5;` yeter). Örnek 2 (`./Ornek2.java`)
bir döviz tablosunu static blokta kurar.

## Tam başlatma sırası

Bir nesne oluşturulurken kod şu sırayla çalışır:

1. **(Yalnızca ilk kez, sınıf yüklenince)** static alan başlatmaları + static bloklar — yazıldıkları
   sırada, **bir kez**.
2. **Her `new`'de:**
   - Önce **üst sınıf** kurulur (`super(...)`).
   - Sonra **instance alan başlatmaları + instance initializer blokları**, kodda yazıldıkları
     **sırayla**.
   - En son **constructor gövdesi**.

```
[static blok]      <- ilk new'den önce, bir kez
[instance blok]    <- her new'de, constructor'dan önce
[constructor]      <- en son
```

Örnek 1 iki nesne oluşturarak static bloğun bir kez, instance bloğun ise her seferinde çalıştığını
kanıtlar.

## Ne zaman kullanmalı, ne zaman kaçınmalı?

- **Static blok:** Karmaşık statik veri kurulumu için iyi. Alternatif: statik bir fabrika metodu.
  Static blokta atılan exception'lar `ExceptionInInitializerError`'a yol açar ve sınıf yüklenemez;
  dikkatli ol.
- **Instance blok:** Constructor'lar arası paylaşılan kod için kullanışlı; ancak çoğu durumda
  **ortak bir private başlatma metodu** veya **constructor zincirleme** (`this(...)`) daha okunaklı
  olabilir. Record'larda instance initializer block **kullanılamaz** (compact constructor kullan).

## Özet

İki başlatma bloğunu öğrendik: her `new`'de constructor'dan önce çalışan ve constructor'lar arası
ortak kodu toplayan **instance initializer block** (Örnek 1) ve sınıf yüklenince bir kez çalışıp
karmaşık statik kurulum yapan **static initializer block** (Örnek 2). Ayrıca nesne kurulumunun tam
sırasını (static → instance blok → constructor) netleştirdik. Bu, OOP eksik konuları bölümünü
tamamlıyor.
