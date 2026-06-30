# HashMap Sınıfı

`HashMap`, bir **anahtarı bir değere eşleyen** koleksiyondur (key → value) ve `ArrayList`'ten sonra
en çok kullanılan veri yapısıdır. "Kullanıcı adından kullanıcıya", "ürün kodundan stoğa", "kelimeden
sayısına"... gibi her türlü eşleme için kullanılır. En güçlü yanı hızıdır: doğru kurulmuş bir
`HashMap`'te anahtarla erişim **ortalama O(1)**'dir.

## Nasıl çalışır? (hash mantığı)

`HashMap`, anahtarın `hashCode()` değerini kullanarak değeri bir "kovaya" (bucket) yerleştirir.
Aradığında aynı hash'i hesaplayıp doğrudan o kovaya gider — bu yüzden çok hızlıdır. Bu mekanizmanın
düzgün çalışması için anahtar nesnelerin **`equals()` ve `hashCode()`** metotları doğru
tanımlanmalıdır. `String`, `Integer` gibi tipler bunu zaten doğru yapar; kendi sınıfını anahtar
yapacaksan ikisini birlikte override et (record'lar bunu otomatik sağlar).

## Temel işlemler

```java
Map<String,Integer> m = new HashMap<>();
m.put("a", 1);                 // ekle/güncelle (aynı anahtar değeri değiştirir)
m.get("a");                    // eriş (yoksa null)
m.getOrDefault("x", 0);        // yoksa varsayılan döndür (null'dan kurtulur)
m.containsKey("a");            // anahtar var mı?
m.containsValue(1);            // değer var mı?
m.remove("a");                 // sil
m.size();                      // kayıt sayısı
```

Gezinme — en verimlisi `entrySet()` (anahtar+değer birlikte):

```java
for (Map.Entry<String,Integer> e : m.entrySet()) { e.getKey(); e.getValue(); }
m.keySet();  m.values();  m.forEach((k,v) -> ...);
```

Örnek 1 (`./Ornek1.java`) bunların hepsini bir stok haritasıyla gösterir.

> **Sıra garantisi yoktur:** `HashMap` elemanları hash sırasına göre tutar (öngörülemez). Ekleme
> sırası gerekiyorsa `LinkedHashMap`, sıralı anahtar gerekiyorsa `TreeMap` kullan (sonraki konu).
> `HashMap` bir `null` anahtara ve birden çok `null` değere izin verir.

## Güçlü metotlar (Java 8+)

`HashMap`'in asıl gücü bu metotlardadır; çok yazılan kalıpları tek satıra indirir:

```java
m.merge(k, 1, Integer::sum);                       // sayaç: yoksa 1, varsa topla
m.computeIfAbsent(k, x -> new ArrayList<>()).add(v); // gruplama: Map<K,List<V>>
m.compute(k, (key, val) -> ...);                   // değeri yeniden hesapla
m.putIfAbsent(k, v);                               // yalnızca yoksa koy
```

Örnek 2 (`./Ornek2.java`) iki klasik gerçek senaryoyu çözer: **kelime frekansı** (`merge`) ve
**gruplama** (`computeIfAbsent` ile `Map<Integer, List<String>>`). Bu kalıpları çok kullanacaksın.

## HashMap aile fertleri

| Sınıf | Özellik | Ne zaman |
|-------|---------|----------|
| `HashMap` | Hızlı, sırasız | Varsayılan eşleme |
| `LinkedHashMap` | Ekleme/erişim sırasını korur | Sıra önemliyse, LRU önbellek |
| `TreeMap` | Anahtarları sıralı tutar | Sıralı/aralık sorguları |
| `EnumMap` | Enum anahtarlar için çok hızlı | Anahtar bir enum'sa |
| `ConcurrentHashMap` | Thread-safe | Çok thread'li erişim |

Sıralı ve özel map'leri (`TreeMap`, `LinkedHashMap`, `EnumMap`) bir sonraki konuda ele alıyoruz.

## Özet

`HashMap`'in anahtar-değer eşlemesini, hash tabanlı O(1) erişimini ve `equals`/`hashCode`
gereksinimini; temel işlemleri ve gezinmeyi (Örnek 1); `merge`/`computeIfAbsent` gibi güçlü
metotlarla frekans sayma ve gruplama gerçek senaryolarını (Örnek 2) öğrendik. Sırada, anahtarları
sıralı veya belirli bir düzende tutan map'ler: **TreeMap, LinkedHashMap ve EnumMap**.
