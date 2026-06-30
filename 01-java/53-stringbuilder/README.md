# StringBuilder Sınıfı

`String` Java'da **değişmezdir** (immutable): bir kez oluşturulan metin asla değişmez; "değiştirme"
gibi görünen her işlem aslında **yeni bir String** üretir. Bu, güvenlik ve thread-güvenliği için
harikadır ama bir metni parça parça **inşa ederken** ciddi bir performans sorununa dönüşür. İşte
`StringBuilder` tam da bunun için vardır: **değiştirilebilir** bir metin tamponu.

## String neden yetmez?

```java
String s = "";
for (int i = 0; i < 50000; i++) s += i;   // her tur YENİ String kopyalar -> O(n^2)
```

Her `+=`, eski içeriği yeni bir String'e kopyalayıp yenisini ekler. N tur için bu, toplamda
**O(n²)** iştir — büyük N'de fark edilir biçimde yavaşlar ve bellek çöpü üretir.

## StringBuilder: değiştirilebilir tampon

`StringBuilder` aynı, gerektiğinde büyüyen tampon üzerinde çalışır; kopyalama yapmaz:

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 50000; i++) sb.append(i);  // O(n)
String sonuc = sb.toString();
```

Örnek 2 (`./Ornek2.java`) iki yaklaşımı **ölçer**; aradaki fark büyük N'de çarpıcıdır.

> **İncelik:** Tek satırlık sabit birleştirmelerde (`a + b + c`) derleyici **zaten** arka planda
> `StringBuilder` kullanır; orada elle yazmana gerek yok. Asıl fark **döngülerde** ortaya çıkar —
> döngüde metin kuruyorsan `StringBuilder` kullan.

## Temel metotlar

```java
sb.append(x);            // sona ekle (her tipi kabul eder: String, int, char, ...)
sb.insert(pos, x);       // konuma ekle
sb.delete(from, to);     // aralığı sil
sb.deleteCharAt(i);
sb.replace(from, to, s); // aralığı değiştir
sb.reverse();            // ters çevir
sb.setCharAt(i, c); sb.charAt(i); sb.length();
sb.toString();           // String'e çevir
```

Metotların çoğu `StringBuilder`'ın kendisini döndürür; bu yüzden **zincirleme** (method chaining)
yazılır: `sb.append("[").append(x).append("]")`. Örnek 1 (`./Ornek1.java`) tüm bu işlemleri ve
zincirlemeyi gösterir.

## StringBuilder vs StringBuffer

İkisi **aynı API**'ye sahiptir; tek fark:

| | `StringBuilder` | `StringBuffer` |
|---|-----------------|----------------|
| Thread-safe | Hayır | Evet (metotlar `synchronized`) |
| Hız | Daha hızlı | Daha yavaş (kilit yükü) |
| Ne zaman | Varsayılan (tek thread) | Paylaşılan değiştirilebilir metin (nadir) |

**Kural:** Neredeyse her zaman `StringBuilder` kullan. `StringBuffer`'ı yalnızca aynı tampon birden
çok thread tarafından gerçekten paylaşılıyorsa düşün (çok nadir; genelde tasarımı değiştirmek daha
iyidir).

## Modern alternatifler

- **`String.join(",", liste)`**: Bir koleksiyonu ayraçla birleştirmek için.
- **`Collectors.joining(",", "[", "]")`**: Stream'de birleştirme.
- **`String.format` / metin blokları**: Biçimli/çok satırlı sabit metinler için.

Bunlar okunabilirlik sağlar; ama yoğun, koşullu, döngüsel metin inşasında hâlâ `StringBuilder`
en esnek ve en hızlı yoldur.

## Özet

`String`'in değişmezliğinin döngüsel metin inşasında neden yavaş olduğunu; `StringBuilder`'ın
değiştirilebilir tamponla bunu nasıl çözdüğünü (Örnek 2); temel metotları ve zincirlemeyi (Örnek 1)
ve `StringBuffer` ayrımını öğrendik. "Döngüde metin kuruyorsan StringBuilder" altın kuraldır. Bu,
Class References bölümünün bu turdaki kısmını tamamlıyor.
