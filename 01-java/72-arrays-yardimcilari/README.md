# Arrays Yardımcı Sınıfı

Diziler (`int[]`, `String[]`...) Java'nın en temel veri yapısıdır: sabit boyutlu, hızlı, bellek
açısından kompakt. Ama dizilerin **metotları yoktur** — sıralamak, aramak, kopyalamak,
karşılaştırmak için `java.util.Arrays` yardımcı sınıfını kullanırsın. Bu konu, dizilerle günlük
çalışmayı kolaylaştıran bu metotları ve çok boyutlu dizi inceliklerini ele alır.

## Tek boyutlu dizi işlemleri

```java
Arrays.sort(dizi);                  // yerinde sırala
Arrays.binarySearch(dizi, x);       // SIRALI dizide hızlı ara (O(log n))
Arrays.copyOf(dizi, yeniBoyut);     // kopya (boyut değiştirebilir)
Arrays.copyOfRange(dizi, from, to); // dilim [from, to)
Arrays.fill(dizi, deger);           // tümünü doldur
Arrays.equals(a, b);                // İÇERİK karşılaştırması
Arrays.toString(dizi);              // okunaklı yazdırma
Arrays.asList(dizi);                // List görünümü
Arrays.stream(dizi);                // Stream'e çevir
```

Örnek 1 (`./Ornek1.java`) hepsini gösterir. İki kritik tuzak:

> **`==` vs `Arrays.equals`:** Diziler nesnedir; `a == b` **referansları** karşılaştırır (içerik
> aynı olsa bile false olabilir). İçerik için **`Arrays.equals(a, b)`** kullan.

> **`toString` tuzağı:** `dizi.toString()` çöp basar (`[I@1b6d3586`). Okunaklı çıktı için
> **`Arrays.toString(dizi)`** kullan.

> **`asList` sınırı:** `Arrays.asList`, **sabit boyutlu** bir görünüm döndürür (`add`/`remove`
> atar). Değiştirilebilir liste için `new ArrayList<>(Arrays.asList(...))`.

## Çok boyutlu diziler

Java'da çok boyutlu dizi aslında **"dizilerin dizisidir"**: `int[][]`, her elemanı bir `int[]`
olan bir dizidir. Bunun iki sonucu var:

- Satırlar **farklı uzunlukta** olabilir (jagged / düzensiz dizi).
- Yazdırma/karşılaştırma için **derin (deep)** sürümler gerekir:

```java
Arrays.deepToString(matris);   // iç içe dizileri doğru yazdırır
Arrays.deepEquals(m1, m2);     // içerik karşılaştırması (derin)
```

Örnek 2 (`./Ornek2.java`) bir matrisi gezer, `deepToString`/`deepEquals`'i ve düzensiz diziyi
gösterir. (Sıradan `toString`/`equals` çok boyutluda yanıltıcıdır — iç dizilerin adresine bakar.)

## Dizi mi, koleksiyon mu?

| | Dizi (`T[]`) | `ArrayList<T>` |
|---|--------------|----------------|
| Boyut | Sabit | Dinamik |
| İlkel tip | Destekler (`int[]`) | Hayır (kutulama) |
| API | Yok (Arrays yardımcı) | Zengin |
| Performans | En kompakt/hızlı | Çok yakın, esnek |

İlkel tiplerle yoğun sayısal iş veya sabit boyut için dizi; esneklik ve API için `ArrayList`
(topic 47).

## Performans notu

Çok büyük dizilerde **`Arrays.parallelSort`**, birden çok çekirdek kullanarak `sort`'tan daha hızlı
sıralayabilir. Küçük dizilerde fark yoktur (hatta ek yük getirir).

## Özet

`Arrays` sınıfının dizilerle çalışmayı kolaylaştıran metotlarını — `sort`/`binarySearch`/`copyOf`/
`fill`/`equals`/`toString`/`asList`/`stream` (Örnek 1) — ve `==` vs `equals`, `toString` tuzaklarını
öğrendik; çok boyutlu dizileri, `deepToString`/`deepEquals` ve düzensiz dizileri (Örnek 2) gördük.
Sırada, bit düzeyinde verimli bayrak yönetimi: **BitSet**.
