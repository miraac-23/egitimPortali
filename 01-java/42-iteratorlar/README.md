# Iteratorlar (Iterators)

Bir koleksiyonun elemanlarını tek tek gezmek, programlamanın en sık yaptığı işlerden biridir.
Ama farklı koleksiyonların (liste, küme, kuyruk...) iç yapıları çok farklıdır. **Iterator**, bu
farkı gizleyen ortak bir gezinti arayüzüdür: koleksiyonun tipinden bağımsız, standart bir "sıradaki
elemanı ver" mekanizması. Aslında her gün kullandığın `for-each` döngüsü de perde arkasında
Iterator'a dayanır.

## Iterator: standart gezinti

`Iterator` üç metot sunar: `hasNext()` (daha eleman var mı?), `next()` (sıradakini ver) ve
isteğe bağlı `remove()` (son döneni sil):

```java
Iterator<String> it = liste.iterator();
while (it.hasNext()) {
    String e = it.next();
    ...
}
```

`for (String e : liste)` yazdığında derleyici bunu tam olarak bu Iterator döngüsüne çevirir. Örnek
1 (`./Ornek1.java`) temel gezintiyi ve kritik bir konuyu gösterir: **gezerken güvenli silme**.

## fail-fast ve gezerken değiştirme

Bir koleksiyonu `for-each` ile gezerken aynı anda `liste.remove(...)` çağırırsan
**`ConcurrentModificationException`** alırsın. Bu "fail-fast" davranıştır: koleksiyon, gezinti
sırasında yapısal olarak değiştiğini fark eder ve hemen hata verir (sessiz, bozuk sonuç yerine).

Doğru yollar:

- Gezerken silmek için **`iterator.remove()`** kullan (güvenli).
- Daha temizi: koşullu silme için **`removeIf(predicate)`** (iç tarafta iterator kullanır).

```java
it.remove();                       // güvenli, gezinti sırasında
liste.removeIf(s -> s.isEmpty());  // en temiz
```

## ListIterator: çift yönlü ve güçlü

`List`'ler ayrıca **`ListIterator`** sunar: ileri **ve geri** gidebilir, gezerken **güncelleyebilir**
(`set`) ve **ekleyebilir** (`add`), konumu sorgulayabilir (`nextIndex`/`previousIndex`):

```java
ListIterator<Integer> it = list.listIterator();
while (it.hasNext()) { int x = it.next(); it.set(x * x); } // yerinde güncelle
while (it.hasPrevious()) { ... }                            // geri git
```

Örnek 2 (`./Ornek2.java`) bir listeyi `set` ile yerinde güncellemeyi, geri gezintiyi ve `add` ile
ekleme yapmayı gösterir.

## Kendi Iterable'ını yazmak

Kendi sınıfını `for-each` ile gezilebilir yapmak istiyorsan **`Iterable<T>`** uygula: tek metodu
`iterator()`'dır ve bir `Iterator<T>` döndürür:

```java
class Aralik implements Iterable<Integer> {
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            public boolean hasNext() { ... }
            public Integer next() { ... }
        };
    }
}
for (int s : new Aralik(1, 5)) { ... }   // artık for-each çalışır
```

Örnek 3 (`./Ornek3.java`) `1..n` aralığını for-each ile gezilebilir bir `Iterable` olarak yazar.
Bu, kendi veri yapılarını (ağaç, grafik, sayfalı akış...) dilin doğal döngüsüne bağlamanı sağlar.

## Iterator vs Enumeration

Iterator, eski **`Enumeration`**'ın (Java 1.0) halefidir; daha kısa metot adları ve `remove()`
desteği getirir. Eski API'lerde (`Vector`, `Hashtable`) hâlâ Enumeration görürsün; onu ayrı bir
konuda ele alıyoruz.

## Özet

Koleksiyonları tipinden bağımsız gezmenin standart yolu olan **Iterator**'ı (Örnek 1); fail-fast
davranışını ve gezerken güvenli silmeyi (`iterator.remove`/`removeIf`); çift yönlü ve güncelleyebilen
**ListIterator**'ı (Örnek 2) ve kendi sınıfını for-each'e açan **Iterable**'ı (Örnek 3) öğrendik.
Sırada, nesneleri sıralamanın temeli — bir nesnenin "doğal sırasını" tanımlamak: **Comparable
arayüzü**.
