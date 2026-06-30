# Type Casting (Tip Dönüşümü)

**Type casting**, bir değeri/nesneyi bir tipten başka bir tipe dönüştürmektir. Java güçlü tipli bir
dildir; bu dönüşümler kurallara bağlıdır. İki tamamen farklı bağlam vardır: **ilkel tipler**
arasında (sayısal dönüşüm) ve **nesne referansları** arasında (kalıtım hiyerarşisinde). Her ikisinin
de kendine özgü tuzakları vardır.

## İlkel tip dönüşümleri

İlkel tiplerin bir "büyüklük sırası" vardır: `byte → short → int → long → float → double`
(ve `char → int`).

### Genişletme (widening) — otomatik

Küçük tipten büyük tipe dönüşüm **otomatiktir** ve güvenlidir (veri kaybı yok):

```java
int i = 100;
long l = i;       // int -> long (otomatik)
double d = l;     // long -> double (otomatik)
```

### Daraltma (narrowing) — açık cast, veri kaybı riski

Büyük tipten küçük tipe dönüşüm **açık cast** gerektirir ve **veri kaybı/taşma** olabilir:

```java
double pi = 3.99;
int kesik = (int) pi;     // -> 3  (ondalık ATILIR, yuvarlanmaz!)
byte b = (byte) 300;      // -> taşar (byte aralığı -128..127)
```

Örnek 1 (`./Ornek1.java`) genişletme/daraltmayı, `char ↔ int` dönüşümünü ve önemli bir tuzağı
gösterir:

> **int bölme tuzağı:** `5 / 2 == 2` (ondalık atılır!). Ondalık sonuç için en az bir operand
> `double` olmalı: `5 / 2.0 == 2.5` veya `(double) 5 / 2`.

## Nesne referansı dönüşümleri

Kalıtım hiyerarşisinde iki yön vardır:

### Upcasting (alt → üst) — otomatik, güvenli

```java
Hayvan h = new Kopek();   // Kopek bir Hayvandır -> otomatik
```

Her zaman güvenlidir. Polimorfizmin temelidir: üst tiple programlar, gerçek tip davranır (dinamik
bağlama, topic 40).

### Downcasting (üst → alt) — açık cast, ClassCastException riski

```java
Hayvan h = new Kopek();
Kopek k = (Kopek) h;      // açık cast — nesne gerçekten Kopek'se güvenli
```

Ama nesne gerçekten o tip **değilse** çalışma zamanında **`ClassCastException`** atar. Bu yüzden:

```java
if (h instanceof Kopek k) {   // pattern matching: kontrol + cast + bağlama (Java 16+)
    k.getir();
}
```

Örnek 2 (`./Ornek2.java`) upcasting, güvenli/yanlış downcasting ve pattern matching'i gösterir.

> **Derlenmeyen cast:** Birbiriyle ilgisiz tipler arasında cast **derlenmez** (örn. `String`'i
> `Integer`'a cast edemezsin). Yalnızca aynı hiyerarşideki tipler arasında downcast denenebilir.

## Autoboxing ile ilişki

İlkel ↔ wrapper dönüşümü (`int ↔ Integer`) ayrı bir mekanizmadır (**autoboxing**, topic 76); cast
değildir, otomatik kutulama/kutu-açmadır ve kendi tuzakları vardır (null unboxing, `==`).

## Özet

İki tür tip dönüşümünü öğrendik: **ilkel** tipler arasında genişletme (otomatik) ve daraltma (açık
cast + veri kaybı; `int` bölme tuzağı dahil — Örnek 1); **nesne** referansları arasında upcasting
(güvenli) ve downcasting (`ClassCastException` riski, güvenli yol `instanceof`/pattern matching —
Örnek 2). Tip dönüşümlerini doğru yapmak, hem doğruluk hem güvenlik için kritiktir. Sırada,
karakterlerin altyapısı: **Unicode ve karakter kodlama**.
