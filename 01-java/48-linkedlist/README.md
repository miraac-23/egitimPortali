# LinkedList Sınıfı

`LinkedList`, `ArrayList`'e alternatif bir `List` uygulamasıdır; ama iç yapısı tamamen farklıdır.
Dizi yerine **çift yönlü bağlı liste** (doubly linked list) kullanır: her eleman bir "düğümdür" ve
kendinden önceki ve sonraki düğümleri işaret eder. Bu yapı, baş/son ekleme-silmede hız kazandırır;
ama indeksli erişimde kaybettirir. Ayrıca `LinkedList` aynı zamanda bir **Deque** (çift uçlu kuyruk)
olduğundan kuyruk ve yığın olarak da kullanılabilir.

## İç yapı: bağlı düğümler

```
null <- [baş] <-> [orta] <-> [son] -> null
```

Her düğüm değeri + önceki/sonraki işaretçileri tutar. Sonuçları:

- **Baş/son ekleme-silme: O(1)** — sadece işaretçiler güncellenir.
- **İndeksli erişim `get(i)`: O(n)** — istenen düğüme ulaşmak için baştan/sondan gezilir.
- **Bellek:** Her eleman için ekstra iki işaretçi (ArrayList'ten daha fazla bellek).

## Hem List hem Deque

`LinkedList`, iki arayüzü birden uygular:

```java
// List gibi
list.add("x"); list.get(0); list.addFirst("a"); list.addLast("z");

// Deque (kuyruk) gibi — FIFO
q.offer(1); q.poll(); q.peek();

// Deque (yığın) gibi — LIFO
s.push(1); s.pop();
```

Örnek 1 (`./Ornek1.java`) `LinkedList`'i List, kuyruk (FIFO) ve yığın (LIFO) olarak kullanır.

## ArrayList vs LinkedList: hangisi ne zaman?

Bu, klasik bir karşılaştırmadır. Örnek 2 (`./Ornek2.java`) iki senaryoyu **ölçer**:

| İşlem | ArrayList | LinkedList |
|-------|-----------|------------|
| İndeksli erişim `get(i)` | **O(1)** hızlı | O(n) yavaş |
| Sona ekleme | amortize O(1) | O(1) |
| Başa ekleme-silme | O(n) (kaydırma) | **O(1)** |
| Bellek | Az (kompakt dizi) | Çok (düğüm + işaretçiler) |
| Önbellek dostu (cache locality) | **Evet** | Hayır |

**Pratik kural:** Çoğu durumda **ArrayList** daha iyidir — modern donanımda dizinin önbellek
dostluğu, LinkedList'in teorik avantajlarını çoğu zaman gölgede bırakır. LinkedList'i yalnızca
gerçekten baş/son ekleme-silme ağırlıklı ve indeksli erişim yapmayan senaryolarda düşün.

> **Önemli:** Kuyruk veya yığın gerekiyorsa, `LinkedList` yerine genelde **`ArrayDeque`** daha
> hızlıdır (daha az bellek, daha iyi önbellek davranışı). Bunu Queue/Deque konusunda görüyoruz.

## Özet

`LinkedList`'in çift yönlü bağlı liste yapısını; hem List hem Deque olarak kullanımını (Örnek 1);
ArrayList ile performans karşılaştırmasını — baş ekleme LinkedList'te, rastgele erişim ArrayList'te
hızlı (Örnek 2) — öğrendik. "Varsayılan olarak ArrayList; kuyruk/yığın için ArrayDeque" pratik
kuralını benimse. Sırada, anahtar-değer eşlemesinin temeli: **HashMap**.
