# Comparable Arayüzü (Doğal Sıralama)

Nesneleri sıralamak istediğimizde, Java'nın "hangisi önce gelir?" sorusunu yanıtlaması gerekir.
Sayılar için bu açıktır (küçükten büyüğe), `String` için alfabetiktir. Peki kendi sınıfların —
`Urun`, `Kisi`? İşte burada **`Comparable`** devreye girer: bir sınıfa **doğal sıra (natural
ordering)** kazandırır, yani "bu tipin varsayılan sıralaması şudur" der.

## compareTo: tek metot

`Comparable<T>` tek metot ister: `int compareTo(T o)`. Dönüş değerinin **işareti** önemlidir:

- **negatif** → bu nesne, `o`'dan önce gelir (`this < o`)
- **sıfır** → eşit sıradalar
- **pozitif** → bu nesne, `o`'dan sonra gelir (`this > o`)

```java
class Urun implements Comparable<Urun> {
    private double fiyat;
    public int compareTo(Urun o) {
        return Double.compare(this.fiyat, o.fiyat);  // doğal sıra: fiyat artan
    }
}
```

> **İpucu:** Karşılaştırmayı elle `this.x - o.x` ile yapma (taşma/`NaN` riski). Bunun yerine
> `Integer.compare`, `Double.compare`, `String.compareTo` gibi hazır metotları kullan.

## Doğal sırayı kim kullanır?

`Comparable` uygulayan bir tip, Java'nın tüm sıralama altyapısıyla otomatik çalışır:

- `Collections.sort(list)` ve `list.sort(null)`
- `Arrays.sort(dizi)`
- `TreeSet` ve `TreeMap` (elemanları sıralı tutarlar)
- `Collections.min/max`, sıralı stream işlemleri

Örnek 1 (`./Ornek1.java`) fiyata göre doğal sıralı bir `Urun` tanımlar; `sort`, `TreeSet.first()/
last()` ile en ucuz/en pahalıyı bulur.

## Çok alanlı sıralama

Genelde tek alan yetmez: önce soyada, eşitse ada, o da eşitse yaşa göre sırala. Bunu `compareTo`
içinde **zincirleme** yaparsın — ilk sıfır-olmayan sonuç kazanır:

```java
public int compareTo(Kisi o) {
    int s = soyad.compareTo(o.soyad);  if (s != 0) return s;
    int a = ad.compareTo(o.ad);        if (a != 0) return a;
    return Integer.compare(yas, o.yas);
}
```

Örnek 2 (`./Ornek2.java`) üç alana göre sıralar.

## Sözleşme (contract) — ihlal etme!

`compareTo` belirli kurallara uymalıdır, yoksa sıralama ve `TreeSet`/`TreeMap` bozulur:

- **Anti-simetri:** `a.compareTo(b)` ile `b.compareTo(a)` ters işaretli olmalı.
- **Geçişlilik:** `a<b` ve `b<c` ise `a<c`.
- **equals ile uyum (önerilir):** `compareTo == 0` olduğunda `equals == true` olmalı. Çünkü
  `TreeSet`/`TreeMap` eşitliği `equals` ile değil **`compareTo` ile** belirler; uyumsuzluk
  "kümede var ama bulunamıyor" gibi sürprizlere yol açar. Bu yüzden Örnek 2'de `equals/hashCode`,
  `compareTo` ile aynı alanları kullanır.

## Comparable mı, Comparator mı?

- **`Comparable`**: Tipin **tek, varsayılan** sırası. Sınıfın içine yazılır (`compareTo`). "Bu
  tipin doğal sırası budur."
- **`Comparator`**: **Dışarıdan, çok sayıda alternatif** sıra. Sınıfı değiştirmeden farklı
  ölçütlerle sıralamak için (bir sonraki konu). Örn. ürünleri kâh fiyata kâh ada göre sıralamak.

İkisi birlikte kullanılır: doğal sıra `Comparable` ile, özel/çoklu sıralar `Comparator` ile.

## Özet

`Comparable` ile bir tipe doğal sıra kazandırmayı: `compareTo`'nun işaret kuralını, hazır
`compare` metotlarını, doğal sırayı kullanan altyapıyı (`sort`, `TreeSet`) (Örnek 1); çok alanlı
zincirleme karşılaştırmayı ve `equals` ile uyum sözleşmesini (Örnek 2) öğrendik. Sırada, sınıfı
değiştirmeden esnek ve çoklu sıralama: **Comparator**.
