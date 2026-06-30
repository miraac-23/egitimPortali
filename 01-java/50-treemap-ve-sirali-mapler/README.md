# TreeMap, LinkedHashMap ve EnumMap

`HashMap` hızlıdır ama elemanları **sırasız** tutar. Çoğu zaman buna ihtiyacın yoktur — ama bazen
anahtarların **sıralı** olması, **ekleme sırasının** korunması veya enum anahtarlar için **özel
hız** gerekir. Bu konuda `Map` ailesinin üç önemli üyesini ele alıyoruz; her biri belirli bir
ihtiyaca yanıt verir.

## TreeMap: sıralı anahtarlar

`TreeMap`, anahtarları her zaman **sıralı** tutar (doğal sıra veya verdiğin `Comparator`). İçte
kırmızı-siyah ağaç kullanır; erişim **O(log n)**'dir (HashMap'ten yavaş ama sıralılık sağlar).
Asıl gücü, sıralı yapının getirdiği **komşuluk ve aralık** sorgularıdır:

```java
map.firstKey(); map.lastKey();           // uçlar
map.floorKey(80);   // <=80 en büyük anahtar
map.ceilingKey(80); // >=80 en küçük
map.higherKey(85); map.lowerKey(85);     // kesin büyük/küçük
map.headMap(85); map.tailMap(85); map.subMap(70, 90); // aralık görünümleri
map.descendingMap();                     // ters sıra
```

Örnek 1 (`./Ornek1.java`) bunların hepsini gösterir. "Şu değere en yakın", "şu aralıktakiler",
"sıralı gez" gibi ihtiyaçlarda `HashMap` yetmez; `TreeMap` gerekir (örn. fiyat aralığı sorguları,
zaman serisi).

## LinkedHashMap: ekleme sırası ve LRU

`LinkedHashMap`, `HashMap`'in hızını korur ama ek olarak **ekleme sırasını** hatırlar. Üzerine
gezdiğinde elemanlar eklendikleri sırada gelir — rapor/çıktı tutarlılığı için kullanışlıdır.

Daha güçlü bir kullanım: yapıcıya `accessOrder=true` verip `removeEldestEntry`'yi override ederek
kolayca bir **LRU (Least Recently Used) önbellek** kurabilirsin:

```java
new LinkedHashMap<>(16, 0.75f, true) {
    protected boolean removeEldestEntry(Map.Entry e) { return size() > 3; }
};
```

Burada her erişim elemanı "en yeni" yapar; kapasite aşılınca **en az kullanılan** atılır. Örnek 2
(`./Ornek2.java`) hem ekleme sırasını hem LRU önbelleği gösterir.

## EnumMap: enum anahtarlar için

Anahtarların bir **enum** olduğu durumlarda `EnumMap` kullan. İçte basit bir **dizi** tutar (enum'un
ordinal'ine göre); bu yüzden `HashMap`'ten **daha hızlı ve daha kompakttır** ve anahtarları enum
tanım sırasında tutar:

```java
EnumMap<Gun, String> program = new EnumMap<>(Gun.class);
program.put(Gun.PZT, "Toplantı");
```

## Hangisini seçmeli?

| İhtiyaç | Sınıf |
|---------|-------|
| Hızlı, sıra önemsiz | `HashMap` |
| Ekleme sırası korunsun | `LinkedHashMap` |
| LRU önbellek | `LinkedHashMap` (accessOrder) |
| Anahtarlar sıralı / aralık sorgusu | `TreeMap` |
| Anahtar bir enum | `EnumMap` |
| Çok thread'li | `ConcurrentHashMap` |

## Özet

Üç özel `Map`'i öğrendik: sıralı anahtarlar ve komşuluk/aralık sorguları için **TreeMap** (Örnek 1);
ekleme sırasını koruyan ve kolay LRU önbellek sağlayan **LinkedHashMap** ile enum anahtarlar için
hızlı **EnumMap** (Örnek 2). Doğru map'i ihtiyaca göre seçmek hem performans hem doğruluk kazandırır.
Sırada, benzersizlik garantisi veren koleksiyonlar: **Set aileleri (HashSet, LinkedHashSet, TreeSet,
EnumSet)**.
