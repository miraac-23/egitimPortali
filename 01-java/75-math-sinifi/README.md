# Math Sınıfı

**`Math`**, matematiksel işlemler için statik metotlar ve sabitler sunan yardımcı sınıftır. Mutlak
değer, üs/kök, yuvarlama, trigonometri, logaritma... hepsi buradadır. Ayrıca bu konu, sayılarla
çalışırken her geliştiricinin er geç karşılaştığı iki kritik tuzağı — **kayan nokta hatası** ve
**tam sayı taşması** — ve çözümlerini ele alır.

## Temel metotlar

```java
Math.abs(-7);          // mutlak değer
Math.max(a, b); Math.min(a, b);
Math.pow(2, 10);       // üs (2^10) — double döndürür
Math.sqrt(144); Math.cbrt(27);  // kare/küp kök
Math.round(3.67);      // en yakın tam sayı -> 4
Math.floor(3.67);      // aşağı -> 3.0
Math.ceil(3.67);       // yukarı -> 4.0
Math.hypot(3, 4);      // hipotenüs -> 5.0
Math.random();         // [0.0, 1.0) rastgele double
Math.PI, Math.E;       // sabitler
```

Trigonometri (`sin`/`cos`/`tan`) radyan ile çalışır; `toRadians`/`toDegrees` ile dönüştür. Örnek 1
(`./Ornek1.java`) bunları gösterir.

> **Dikkat:** `pow` ve çoğu metot **`double`** döndürür. Tam sayı sonuç istiyorsan `(int)` cast'le
> veya `Math.round` kullan. Daha kaliteli rastgelelik için `java.util.Random` /
> `ThreadLocalRandom`.

## Tuzak 1: Kayan nokta hatası

`double`/`float`, sayıları **ikili tabanda** tutar ve `0.1` gibi ondalık kesirleri **tam olarak**
gösteremez. Sonuç:

```java
0.1 + 0.2 == 0.3   // FALSE!  (sonuç 0.30000000000000004)
```

Bu bir Java hatası değil, IEEE 754 kayan nokta aritmetiğinin doğasıdır. Çözümler:

- **Para/finans/hassas hesap:** `BigDecimal` kullan — **ve mutlaka `String` constructor ile**
  (`new BigDecimal("0.1")`; `new BigDecimal(0.1)` yine hatalı double'dan başlar).
- **Karşılaştırma:** İki `double`'ı `==` ile karşılaştırma; küçük bir tolerans (epsilon) kullan:
  `Math.abs(a - b) < 1e-9`.

```java
new BigDecimal("0.1").add(new BigDecimal("0.2"));  // tam 0.3
fiyat.setScale(2, RoundingMode.HALF_UP);           // 2 ondalık, yuvarlama kuralı
```

## Tuzak 2: Tam sayı taşması

`int`/`long` sınırlıdır. `Integer.MAX_VALUE`'yu aşan işlem **sessizce sarmalanır** (negatife döner)
— hiçbir uyarı vermez:

```java
Integer.MAX_VALUE + 1   // -2147483648  (taştı!)
```

Çözümler:

- **`Math.addExact`/`multiplyExact`/`subtractExact`:** Taşmada `ArithmeticException` atar (sessiz
  bozuk sonuç yerine açık hata).
- **Daha geniş tip:** `long` (veya çok büyük sayılar için `BigInteger`).

Örnek 2 (`./Ornek2.java`) her iki tuzağı ve çözümlerini canlı gösterir.

## StrictMath ve performans

`Math`, platforma göre donanım hızlandırması kullanabilir (sonuçlar platformlar arası çok küçük
farklılık gösterebilir). Bit-bit aynı sonuç gereken nadir durumlarda `StrictMath` kullanılır
(daha yavaş ama tam tekrarlanabilir).

## Özet

`Math` sınıfının matematiksel metotlarını ve sabitlerini (Örnek 1); sayılarla çalışırken iki
kritik tuzağı — **kayan nokta hatası** (çözüm: `BigDecimal` + epsilon) ve **tam sayı taşması**
(çözüm: `Math.addExact`/`long`) — (Örnek 2) öğrendik. Bu tuzakları bilmek, özellikle finansal
hesaplarda hata yapmanı önler. Sırada, sayı tipleri ve otomatik kutulama: **wrapper sınıfları ve
autoboxing**.
