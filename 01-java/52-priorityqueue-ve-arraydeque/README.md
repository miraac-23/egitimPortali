# PriorityQueue ve ArrayDeque

Bazı problemler "sıradaki elemanı" özel bir kurala göre ister: ya **uçlardan** (baş/son) erişim
(yığın, kuyruk) ya da **önceliğe** göre erişim. Java bu iki ihtiyaca iki güçlü sınıfla yanıt verir:
çift uçlu kuyruk **`ArrayDeque`** ve öncelik kuyruğu **`PriorityQueue`**. İkisi de `Queue` arayüzünü
uygular ama tamamen farklı işler için tasarlanmıştır.

## ArrayDeque: yığın ve kuyruk

`ArrayDeque` (array deque = çift uçlu kuyruk), her iki uçtan da **O(1)** ekleme/çıkarma yapar. Tek
sınıfla hem yığın hem kuyruk kurarsın:

```java
// Yığın (LIFO) — son giren ilk çıkar
deque.push(x);  deque.pop();  deque.peek();

// Kuyruk (FIFO) — ilk giren ilk çıkar
deque.offer(x); deque.poll(); deque.peek();

// Çift uçlu
deque.addFirst(x); deque.addLast(x); deque.peekFirst(); deque.peekLast();
```

Örnek 1 (`./Ornek1.java`) `ArrayDeque`'i yığın, kuyruk ve çift uçlu olarak kullanır.

> **Neden `ArrayDeque`?**
> - **Yığın için:** Eski `Stack` sınıfı `Vector` tabanlıdır (senkronize, yavaş, eski). Modern
>   Java'da yığın gerekiyorsa `ArrayDeque` **önerilir**.
> - **Kuyruk için:** `LinkedList` de `Queue`'dur ama `ArrayDeque` daha hızlı ve az bellek kullanır
>   (dizi tabanlı, önbellek dostu).
> - **Dikkat:** `null` eleman kabul etmez (null, "eleman yok" sinyaliyle karışmasın diye).

## PriorityQueue: öncelik kuyruğu

`PriorityQueue`, elemanları ekleme sırasına göre **değil**, **önceliğe** göre verir. İçte bir
**heap** (yığın ağacı) tutar; `peek`/`poll` her zaman **en öncelikli** elemanı döndürür:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();        // min-heap (en küçük önce)
PriorityQueue<Integer> max = new PriorityQueue<>(Comparator.reverseOrder()); // max-heap
PriorityQueue<Gorev> sch = new PriorityQueue<>(Comparator.comparingInt(Gorev::oncelik));
```

Maliyetler: `add`/`poll` O(log n), `peek` O(1). Örnek 2 (`./Ornek2.java`) min-heap, max-heap ve
gerçek bir **görev zamanlayıcı** (en acil iş önce) gösterir.

Kullanım alanları: görev/iş zamanlama, **Dijkstra/A\*** gibi graf algoritmaları, "en yakın/en
büyük K eleman" problemleri, olay simülasyonu.

> **Önemli:** `PriorityQueue`'yu `for-each`/iterator ile gezmek **sıralı sonuç vermez** — sıra
> yalnızca `poll` ile birer birer çıkarınca ortaya çıkar. Tümünü sıralı istiyorsan ya hepsini
> `poll` et ya da bir listeye alıp `sort` uygula.

## Karşılaştırma

| Sınıf | Erişim kuralı | Tipik kullanım |
|-------|---------------|----------------|
| `ArrayDeque` (yığın) | LIFO (son giren ilk çıkar) | Geri-al, ifade ayrıştırma, DFS |
| `ArrayDeque` (kuyruk) | FIFO (ilk giren ilk çıkar) | İş kuyruğu, BFS |
| `PriorityQueue` | Önceliğe göre | Zamanlayıcı, Dijkstra, top-K |

## Özet

İki uç-erişim koleksiyonunu öğrendik: her iki uçtan hızlı erişen ve modern yığın/kuyruk seçimi olan
**ArrayDeque** (Örnek 1) ile elemanları önceliğe göre veren heap tabanlı **PriorityQueue** (Örnek 2).
"Yığın/kuyruk → ArrayDeque, önceliğe göre → PriorityQueue" kuralını benimse. Sırada, metni verimli
biçimde inşa etmenin yolu: **StringBuilder**.
