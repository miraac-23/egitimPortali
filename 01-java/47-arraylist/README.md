# ArrayList Sınıfı

`ArrayList`, Java'da açık ara en çok kullanılan koleksiyondur. İçte boyutu otomatik büyüyen bir
dizi tutar; böylece sabit boyutlu dizilerin (`int[]`) aksine eleman ekledikçe genişler. İndeksli
hızlı erişim, kolay ekleme/silme ve zengin yardımcı metotlarıyla "varsayılan liste" seçimidir.
Collections bölümünde genel hatlarıyla gördük; burada en sık kullanılan metotlarını ve performans
inceliklerini odaklı biçimde ele alıyoruz.

## İç yapı ve performans

`ArrayList` arka planda bir **dizi** (`Object[]`) tutar. Bu, davranışını belirler:

- **İndeksli erişim `get(i)`/`set(i)`: O(1)** — diziye doğrudan erişim, çok hızlı.
- **Sona ekleme `add(e)`: amortize O(1)** — dizi dolunca daha büyük bir diziye kopyalanır
  (genelde ~1.5 kat), ama ortalama maliyet sabittir.
- **Ortaya/başa ekleme-silme: O(n)** — sonraki tüm elemanlar kaydırılır.

Çok sayıda eleman ekleyeceğini biliyorsan başlangıç kapasitesi ver: `new ArrayList<>(1000)` —
gereksiz yeniden boyutlandırmayı önler.

## Temel işlemler

```java
List<String> l = new ArrayList<>();
l.add("a");           // sona ekle
l.add(1, "b");        // indekse ekle
l.get(0);             // eriş
l.set(0, "x");        // güncelle
l.remove(1);          // indekse göre sil
l.remove("x");        // değere göre sil
l.contains("a");      // var mı?
l.indexOf("a");       // konumu
l.size();             // eleman sayısı
```

Örnek 1 (`./Ornek1.java`) bunların hepsini gösterir ve önemli bir tuzağa dikkat çeker:

> **Tuzak:** `remove(int)` **indeksi**, `remove(Object)` **değeri** siler. `List<Integer>`'da
> `list.remove(2)` indeks 2'yi siler; değer 2'yi silmek için `list.remove(Integer.valueOf(2))`
> kullan.

## Toplu işlemler ve dönüşümler

```java
l.addAll(digerListe);          // toplu ekle
l.removeAll(c); l.retainAll(c); // toplu sil / kesişim
l.removeIf(x -> x.isEmpty());   // koşullu sil
l.sort(Comparator...);          // sırala
l.subList(0, 3);                // alt görünüm
l.toArray(new T[0]);            // diziye çevir
l.stream().map(...).toList();   // stream ile dönüştür
```

`Collections` yardımcıları da çok kullanılır: `sort`, `reverse`, `shuffle`, `max`, `min`,
`frequency`. Örnek 2 (`./Ornek2.java`) toplu işlemleri, sıralamayı, `subList`, `toArray` ve stream
dönüşümünü gösterir.

> **subList uyarısı:** `subList`, ana listenin bir **görünümüdür** (view); üzerinde yapılan
> değişiklik ana listeyi etkiler. Bağımsız kopya istiyorsan `new ArrayList<>(l.subList(...))`.

## ArrayList vs Array (dizi)

| | `int[]` (dizi) | `ArrayList<Integer>` |
|---|----------------|----------------------|
| Boyut | Sabit | Dinamik (büyür) |
| Tip | İlkel veya nesne | Yalnızca nesne (kutulama) |
| Metotlar | Yok (Arrays yardımcı) | Zengin API |
| Performans | En hızlı, kutulamasız | Çok hızlı, küçük ek yük |

İlkel tiplerle yoğun sayısal iş yapıyorsan dizi daha verimlidir; esneklik ve API istiyorsan
ArrayList.

## ArrayList vs LinkedList

İkisi de `List`'tir ama iç yapıları farklıdır: ArrayList dizi tabanlı (hızlı erişim), LinkedList
bağlı düğümler (hızlı baş/son ekleme). Çoğu durumda ArrayList daha iyidir; ayrıntıyı bir sonraki
konuda karşılaştırıyoruz.

## Özet

ArrayList'in dizi tabanlı iç yapısını ve performans özelliklerini; temel işlemleri ve
`remove(int)` vs `remove(Object)` tuzağını (Örnek 1); toplu işlemleri, sıralamayı, `subList` ve
dönüşümleri (Örnek 2) öğrendik. "Varsayılan liste ArrayList" kuralı çoğu durumda doğrudur. Sırada,
bağlı liste tabanlı alternatif ve karşılaştırma: **LinkedList**.
