# Bitwise (Bit Düzeyi) Operatörler

**Bit operatörleri**, sayıları bir bütün olarak değil, onları oluşturan tek tek **bitler** (0/1)
üzerinde işler. Günlük iş kodunda nadir görünseler de; izin/bayrak sistemleri, performans-kritik
kod, grafik, ağ protokolleri ve gömülü sistemlerde vazgeçilmezdirler. Ayrıca teknik mülakatların
sevdiği konulardandır. Bu konu, bit operatörlerini ve gerçek kullanım kalıplarını ele alır.

## Mantıksal bit operatörleri

| Operatör | Ad | Kural |
|----------|-----|-------|
| `&` | AND | Her iki bit de 1 ise 1 |
| `\|` | OR | En az biri 1 ise 1 |
| `^` | XOR | Bitler farklıysa 1 |
| `~` | NOT | Tüm bitleri ters çevir |

```java
0b1100 & 0b1010 == 0b1000   // AND
0b1100 | 0b1010 == 0b1110   // OR
0b1100 ^ 0b1010 == 0b0110   // XOR
~12 == -13                   // NOT (tüm bitler ters)
```

Örnek 1 (`./Ornek1.java`) bunları ikilik gösterimle yan yana gösterir. Hızlı bir hile: `(n & 1) == 1`
sayının **tek** olduğunu söyler (son bit).

> **Bit `&|` vs mantıksal `&&||`:** Karıştırma! `&&`/`||` boolean üzerinde çalışır ve **kısa devre**
> yapar (topic 89). `&`/`|` bit düzeyinde çalışır (ve boolean'larda kısa devre yapmaz).

## Kaydırma (shift) operatörleri

```java
x << n    // sola n bit kaydır  -> her kaydırma x'i 2 ile ÇARPAR
x >> n    // işaretli sağa kaydır -> işareti koruyarak BÖLER (/2^n)
x >>> n   // işaretsiz sağa kaydır -> üst bitlere 0 koyar
```

```java
1 << 4 == 16     // 2^4
32 >> 2 == 8     // 32 / 4
```

Kaydırma, `* 2` / `/ 2` işlemlerinin çok hızlı halidir (eskiden performans için kullanılırdı;
bugün derleyici zaten optimize eder, ama bit kalıpları için hâlâ gerekli).

## Gerçek kullanım 1: izin/bayrak maskeleri

En yaygın kullanım: birçok boolean bayrağı **tek bir int'te** tutmak. Her izin bir bittir:

```java
static final int OKU = 1, YAZ = 1<<1, SIL = 1<<2, YONET = 1<<3;

int izin = OKU | YAZ;            // birden çok izni birleştir
boolean yazabilir = (izin & YAZ) != 0;  // bit açık mı? (kontrol)
izin |= SIL;                     // izin EKLE (bit aç)
izin &= ~YAZ;                    // izin KALDIR (bit kapat)
```

Bu kalıp; Unix dosya izinleri (rwx), `EnumSet` (içte bit-maske), olay maskeleri ve birçok API'de
kullanılır. Örnek 2 (`./Ornek2.java`) bir izin sistemi kurar.

## Gerçek kullanım 2: veri paketleme (RGB)

Birden çok küçük değeri tek bir sayıya sığdırmak için kaydırma + OR; geri çözmek için kaydırma +
AND maskesi:

```java
int rgb = (r << 16) | (g << 8) | b;     // 3 byte -> tek int (paketle)
int kirmizi = (rgb >> 16) & 0xFF;        // geri çöz
```

Renkler, ağ paketleri ve kompakt veri formatlarında yaygındır. Örnek 2 RGB paketleme/çözmeyi
gösterir.

## Diğer bit hileleri

- `n & 1` → tek/çift kontrolü.
- `n & (n-1)` → en sağdaki 1 bitini siler (set bit sayma — Kernighan).
- `a ^ b ^ a == b` → XOR ile takas / tekrarlı elemanı bulma.
- `Integer.bitCount(n)`, `Integer.highestOneBit(n)` → hazır yardımcılar.

## Özet

Bit operatörlerini öğrendik: mantıksal bit işlemleri `& | ^ ~` (ikilik gösterimle; Örnek 1) ve
kaydırma `<< >> >>>` ile iki güçlü gerçek kullanım — **izin/bayrak maskeleri** ve **veri paketleme
(RGB)** (Örnek 2); bit `&|` ile mantıksal `&&||` farkına ve faydalı bit hilelerine değindik. Sırada,
kodunu belgelemenin standart yolu: **Javadoc**.
