# Enumeration ve Legacy (Eski) Yapılar

Java'nın koleksiyon dünyası iki katmanlıdır: 1996'daki ilk sürümlerden kalan **eski (legacy)**
sınıflar (`Vector`, `Stack`, `Hashtable`, `Enumeration`) ve Java 1.2 ile gelen modern **Collections
Framework** (`ArrayList`, `ArrayDeque`, `HashMap`, `Iterator`). Yeni kod modern olanları kullanır;
ama eski API'lerde (ve bazı kütüphanelerde) hâlâ eskilerine rastlarsın. Bu konu, ikisi arasındaki
farkı ve neyi ne zaman kullanacağını netleştirir.

## Enumeration: Iterator'ın atası

`Enumeration` (Java 1.0), bir koleksiyonu gezmenin ilk yoluydu. İki metodu vardır:

```java
Enumeration<String> e = vector.elements();
while (e.hasMoreElements()) {
    System.out.println(e.nextElement());
}
```

`Iterator` (Java 1.2) bunun halefidir ve üç iyileştirme getirir:

| | Enumeration (1.0) | Iterator (1.2) |
|---|-------------------|----------------|
| Metotlar | `hasMoreElements()`, `nextElement()` | `hasNext()`, `next()`, `remove()` |
| Silme | Yok | Var (`remove()`) |
| Fail-fast | Hayır | Evet |
| İsimlendirme | Uzun | Kısa |

Örnek 1 (`./Ornek1.java`) aynı `Vector`'ı hem Enumeration hem Iterator ile gezer ve `Hashtable`'ın
Enumeration döndüren metotlarını gösterir. Bugün Enumeration'ı yalnızca eski API'lerde görürsün:
`Vector`, `Hashtable`, `StringTokenizer`, Servlet'lerde `request.getHeaderNames()` gibi.

## Eski koleksiyonlar ve modern karşılıkları

| Eski (1.0–1.1) | Modern (1.2+) | Fark |
|----------------|---------------|------|
| `Vector` | `ArrayList` | Vector her metotta `synchronized` (yavaş) |
| `Stack` | `ArrayDeque` | Stack, Vector'dan türer; ArrayDeque daha hızlı yığın/kuyruk |
| `Hashtable` | `HashMap` | Hashtable senkronize, `null` anahtar/değer kabul etmez |
| `Enumeration` | `Iterator` | yukarıdaki tablo |

Örnek 2 (`./Ornek2.java`) bu çiftleri yan yana gösterir.

## Neden modern olanları tercih ediyoruz?

Eski sınıfların ortak sorunu: **hepsi her metotta kilit alır (synchronized)**. Çok thread'li
erişimde bu güvenlik sağlar gibi görünse de:

- **Tek thread'de gereksiz yavaşlıktır** (kilit maliyeti, çoğu kod tek thread).
- **Çok thread'de bile yetersizdir:** Tek tek metotlar kilitli olsa da, "oku-sonra-yaz" gibi
  bileşik işlemler yine yarış koşuluna açıktır.

Doğru yaklaşım:

- **Tek thread / çoğu durum:** `ArrayList`, `ArrayDeque`, `HashMap` (senkronsuz, hızlı).
- **Eş zamanlılık gerçekten gerekiyorsa:** `java.util.concurrent` paketini kullan —
  `ConcurrentHashMap`, `CopyOnWriteArrayList`, `ConcurrentLinkedQueue` — bunlar Vector/Hashtable'dan
  çok daha iyi ölçeklenir. Ya da `Collections.synchronizedList(...)` ile sarmala.

> **Kural:** Yeni kodda `Vector`/`Stack`/`Hashtable` **kullanma**. Modern koleksiyonları seç;
> eş zamanlılık için `java.util.concurrent`'a git.

## StringTokenizer notu

Eski `StringTokenizer` de metni parçalara ayırmak için Enumeration benzeri bir gezinti sunar.
Bugün yerine `String.split(regex)` veya `Scanner` tercih edilir.

## Özet

`Enumeration`'ın Iterator'ın atası olduğunu (hasMoreElements/nextElement, silme yok) ve eski
API'lerde nerede karşına çıktığını (Örnek 1); eski koleksiyonların (`Vector`/`Stack`/`Hashtable`)
modern karşılıklarını ve neden senkronize tasarımları yüzünden artık tercih edilmediklerini
(Örnek 2) öğrendik. Bu, Data Structures bölümünü tamamlıyor: kavramsal olarak Iterator,
Comparable, Comparator ve eski yapılar artık net. Sırada — bir sonraki büyük bölüm — sık kullanılan
**sınıf referansları** (Scanner, ArrayList, HashMap...) başlıyor.
