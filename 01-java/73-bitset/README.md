# BitSet Sınıfı

**`BitSet`**, otomatik büyüyen bir **bit dizisidir**: her bayrak (true/false) yalnızca **1 bit** yer
kaplar. Çok sayıda açık/kapalı durumu (izinler, varlık kümeleri, "görüldü mü" işaretleri) tutmak
için `boolean[]`'dan çok daha kompakt ve hızlıdır; üstelik bit düzeyinde küme işlemleri sunar. Bir
`boolean[]` her eleman için ~1 bayt harcarken, `BitSet` 8 bayrağı tek bir bayta sığdırır.

## Temel bit işlemleri

```java
BitSet b = new BitSet(64);   // 64 bitlik (gerektiğinde otomatik büyür)
b.set(3);                    // 3. biti aç
b.get(3);                    // 3. bit açık mı?
b.clear(3);                  // kapat
b.flip(2);                   // tersine çevir
b.cardinality();             // açık bit sayısı
b.nextSetBit(0);             // belirli konumdan sonraki açık bit
b.nextClearBit(0);           // sonraki kapalı bit
```

Örnek 1 (`./Ornek1.java`) bunları gösterir.

## Küme işlemleri (çok hızlı)

İki `BitSet` arasında bit düzeyinde mantıksal işlemler — küme cebiri gibi davranır:

```java
a.and(b);     // kesişim (AND)
a.or(b);      // birleşim (OR)
a.xor(b);     // simetrik fark (XOR)
a.andNot(b);  // fark (a \ b)
```

Bu işlemler kelime (word) düzeyinde yapıldığından, büyük kümelerde `HashSet<Integer>`'dan çok daha
hızlı ve kompakttır. Örnek 1 AND/OR/XOR'u gösterir.

## Gerçek senaryo: Eratosthenes Eleği

`BitSet`'in klasik kullanımı **asal sayı eleğidir**: N'e kadar her sayının asal/bileşik durumunu N
bitte tutar. 2'den başlayıp her asalın katlarını "bileşik" işaretler; işaretlenmeyenler asaldır:

```java
BitSet bilesik = new BitSet(N + 1);
for (int i = 2; (long) i*i <= N; i++)
    if (!bilesik.get(i))
        for (int k = i*i; k <= N; k += i) bilesik.set(k);
// asallar = bilesik.nextClearBit(...) ile gezilen, işaretlenmeyenler
```

Örnek 2 (`./Ornek2.java`) bunu uygular. `boolean[N]` yerine `BitSet` kullanmak ~8 kat az bellek
harcar ve `nextClearBit` ile asalları hızlı gezeriz.

## Ne zaman BitSet?

- Çok sayıda (binlerce/milyonlarca) **boolean bayrak** tutman gerektiğinde.
- Yoğun **küme işlemleri** (kesişim/birleşim) yaptığında — özellikle anahtarlar yoğun küçük
  tamsayılarsa.
- Bit-maske/izin sistemleri, bloom-filter benzeri yapılar, grafik algoritmalarında "ziyaret
  edildi" işaretleri.

> Anahtarlar enum ise `EnumSet` (içte yine bit-maske) daha tip-güvenlidir; rastgele/büyük nesne
> kümeleri için `HashSet` uygundur. `BitSet`, **yoğun tamsayı indeksli** durumlar için idealdir.

## Özet

`BitSet`'in bit başına 1 bayrak tutan kompakt yapısını; temel bit işlemlerini (`set`/`clear`/
`flip`/`cardinality`) ve hızlı küme işlemlerini (`and`/`or`/`xor`) (Örnek 1); gerçek bir uygulama
olan Eratosthenes eleğini (Örnek 2) öğrendik. Sırada, yapılandırma ve anahtar-değer çiftleri için
klasik sınıf: **Properties**.
