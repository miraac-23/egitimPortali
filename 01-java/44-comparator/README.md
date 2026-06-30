# Comparator (Esnek ve Çoklu Sıralama)

`Comparable` bir tipe **tek bir doğal sıra** kazandırır. Ama gerçek hayatta aynı veriyi farklı
zamanlarda farklı ölçütlere göre sıralamak isteriz: ürünleri kâh fiyata, kâh ada, kâh stoğa göre.
Sınıfın içine her sıralamayı yazmak mümkün değil. İşte **`Comparator`** tam da bunun içindir:
sıralama mantığını **sınıfın dışında**, ayrı bir nesne olarak tanımlar; böylece aynı tip için
istediğin kadar alternatif sıra üretirsin — hiç sınıfı değiştirmeden.

## compare: iki nesneyi karşılaştır

`Comparator<T>` tek soyut metot ister: `int compare(T a, T b)` — dönüş işareti `compareTo` ile
aynı kuralı izler (negatif `a<b`, 0 eşit, pozitif `a>b`). Functional interface olduğu için
lambda ile yazılır. Ama asıl güç, **hazır fabrika metotlarındadır**:

```java
Comparator.comparing(Urun::ad)               // ada göre
Comparator.comparingDouble(Urun::fiyat)      // fiyata göre (primitive, kutulamasız)
Comparator.comparingDouble(Urun::fiyat).reversed()  // azalan
```

Örnek 1 (`./Ornek1.java`) aynı ürün listesini ada, fiyata (artan/azalan) ve stoğa göre sıralar —
hepsi sınıfı değiştirmeden.

## Çok seviyeli sıralama: thenComparing

Birden çok ölçüt gerektiğinde `thenComparing` ile **zincir** kurarsın: ilk ölçüt eşitse ikinciye
geçilir. Her seviyeyi ayrı ayrı ters çevirebilirsin:

```java
Comparator<Calisan> sira = Comparator
    .comparing(Calisan::departman)                              // 1) departman ↑
    .thenComparing(Comparator.comparingInt(Calisan::maas).reversed()) // 2) maaş ↓
    .thenComparing(Calisan::ad);                                // 3) ad ↑
```

Örnek 2 (`./Ornek2.java`) bunu ve **null güvenliğini** gösterir: `nullsFirst`/`nullsLast`, null
içeren listelerde `NullPointerException`'ı önler; `naturalOrder()`/`reverseOrder()` hazır sıralardır.

## Comparable ile birlikte

İkisi birbirini tamamlar:

- **`Comparable`** → tipin **varsayılan** sırası. `stream.sorted()` (argümansız) ve
  `Collections.sort(list)` bunu kullanır.
- **`Comparator`** → o anki ihtiyaca göre varsayılanı **ezen** esnek sıra. `stream.sorted(cmp)`,
  `list.sort(cmp)` ve `TreeSet`/`TreeMap` yapıcısına verilir.

Örnek 3 (`./Ornek3.java`) puana göre doğal sıralı bir `Oyuncu` tanımlar, sonra bir `Comparator`'la
"puan ↓, süre ↑" liderlik sırasına geçer ve `TreeMap`'e özel sıra verir.

## Nerede kullanılır?

- **Stream:** `.sorted(comparator)` — veri işleme hatlarında.
- **Koleksiyonlar:** `list.sort(cmp)`, `TreeSet`/`TreeMap` (cmp ver), `PriorityQueue` (öncelik sırası).
- **min/max:** `list.stream().max(Comparator.comparing(...))`.
- **Gruplama sonrası:** rapor/tablo sıralamaları (en çok satan, en yeni...).

| | Comparable | Comparator |
|---|-----------|------------|
| Nerede | Sınıfın içinde (`compareTo`) | Sınıfın dışında (ayrı nesne) |
| Kaç sıra | Tek (doğal) | Sınırsız (alternatif) |
| Sınıfı değiştirir mi | Evet | Hayır |
| Tipik | Varsayılan sıra | Özel/çoklu sıra |

## Özet

`Comparator` ile sınıfı değiştirmeden esnek ve çoklu sıralamayı: fabrika metotlarını
(`comparing`/`comparingInt`/`reversed`) (Örnek 1); çok seviyeli `thenComparing` ve null güvenliğini
(Örnek 2); `Comparable` ile birlikte stream/TreeMap kullanımını (Örnek 3) öğrendik. "Tek doğal sıra
→ Comparable, çok/özel sıra → Comparator" kuralı işini görür. Sırada, Iterator'ın atası ve eski
koleksiyonlar: **Enumeration ve legacy yapılar**.
