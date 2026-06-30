# Sayı Wrapper Sınıfları ve Autoboxing

Java'da iki tür veri vardır: **ilkel tipler** (`int`, `double`, `boolean`, `char`...) ve
**nesneler**. İlkel tipler hızlı ve kompakttır ama nesne değildir — metotları yoktur, `null`
olamaz ve koleksiyonlarda (`List<int>` yazılamaz) kullanılamaz. Her ilkel tipin bir **wrapper
(sarmalayıcı) sınıfı** vardır (`Integer`, `Double`, `Boolean`...) ve Java, ikisi arasında otomatik
dönüşüm (**autoboxing/unboxing**) yapar. Bu konu hem wrapper'ların gücünü hem de autoboxing'in
sinsi tuzaklarını ele alır.

## Wrapper sınıfları

| İlkel | Wrapper | | İlkel | Wrapper |
|-------|---------|---|-------|---------|
| `int` | `Integer` | | `boolean` | `Boolean` |
| `long` | `Long` | | `char` | `Character` |
| `double` | `Double` | | `byte` | `Byte` |
| `float` | `Float` | | `short` | `Short` |

Wrapper'lar **nesne** olduğundan metot ve sabit taşır:

```java
Integer.parseInt("123");        // String -> int
Integer.valueOf(42);            // int -> Integer
Integer.MIN_VALUE; Integer.MAX_VALUE;
Integer.toBinaryString(255);    // "11111111"
Integer.parseInt("1010", 2);    // ikilik tabandan -> 10
Integer.compare(a, b); Integer.sum(a, b);
```

Neden gerekli? **Koleksiyonlar ve jenerikler yalnızca nesne tutar** (`List<Integer>`,
`Map<String,Integer>`); ayrıca wrapper `null` olabilir ("değer yok" durumu). Örnek 1
(`./Ornek1.java`) wrapper metotlarını gösterir.

## Autoboxing ve unboxing

Java, ilkel ↔ wrapper dönüşümünü **otomatik** yapar:

```java
Integer kutulu = 5;        // autoboxing: int -> Integer
int ilkel = kutulu;        // unboxing: Integer -> int
list.add(10);              // autoboxing (koleksiyon nesne tutar)
int x = list.get(0);       // unboxing
```

Bu kolaylıktır — ama iki tehlikeli tuzağı vardır.

## Tuzak 1: `==` ile wrapper karşılaştırma (Integer cache)

`Integer.valueOf`, **−128..127** arasındaki değerler için bir **önbellek** kullanır (aynı nesneyi
döndürür); bu aralığın dışında her seferinde **yeni nesne** üretir. Sonuç, `==` ile karşılaştırma
**yanıltıcı** olur:

```java
Integer a = 100, b = 100;   a == b  // true  (önbellek)
Integer c = 200, d = 200;   c == d  // FALSE (yeni nesneler!)
c.equals(d)                          // true  (DOĞRU yol)
```

> **Kural:** Wrapper nesnelerini **her zaman `.equals()`** ile karşılaştır, asla `==` ile.
> (`==` referans karşılaştırır; ilkel `int` için sorun yoktur ama wrapper için tuzaktır.)

## Tuzak 2: null unboxing → NullPointerException

Wrapper `null` olabilir; bir `null` wrapper'ı unbox etmek (ilkel'e çevirmek) **NPE** atar:

```java
Integer belkiNull = null;
int x = belkiNull;    // unboxing -> null.intValue() -> NullPointerException
```

Özellikle veritabanından/Map'ten gelen, `null` olabilen değerleri ilkel'e atarken dikkat et. Örnek
2 (`./Ornek2.java`) her iki tuzağı canlı gösterir.

## Performans notu

Sıkı döngülerde gereksiz autoboxing maliyetlidir (`Long toplam = 0L; toplam += i;` her adımda
kutu/kutudan-çıkar). Yoğun sayısal işte **ilkel tip** kullan (`long toplam`), wrapper'ı yalnızca
gerektiğinde (koleksiyon, null).

## Özet

İlkel tiplerin wrapper karşılıklarını ve yardımcı metotlarını (Örnek 1); autoboxing/unboxing
kolaylığını ve iki kritik tuzağını — **`==` ile wrapper karşılaştırma** (çözüm: `equals`) ve **null
unboxing NPE'si** — (Örnek 2) öğrendik; performans notuna değindik. Sırada, karakter ve mantıksal
veri sınıfları: **Character ve Boolean**.
