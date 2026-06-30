# Set Aileleri (HashSet, LinkedHashSet, TreeSet, EnumSet)

`Set` (küme), **benzersiz elemanlar** topluluğudur: aynı eleman iki kez bulunamaz. Bir listede
yinelenenleri temizlemek, "bu öğe daha önce görüldü mü?" diye sormak veya matematiksel küme
işlemleri yapmak için kullanılır. `Map` ailesinde olduğu gibi, `Set`'in de farklı ihtiyaçlara yanıt
veren dört temel uygulaması vardır.

## HashSet: hızlı ve sırasız

En çok kullanılan `Set`. Hash tabanlıdır; `add`/`contains`/`remove` ortalama **O(1)**. Elemanları
**sırasız** tutar. İki klasik kullanımı:

```java
// 1) Yinelenenleri temizle
Set<Integer> benzersiz = new HashSet<>(tekrarliListe);

// 2) Üyelik testi (çok hızlı "var mı?")
if (gorulenler.contains(x)) { ... }
```

> Benzersizlik, elemanların `equals()` ve `hashCode()` metotlarına dayanır; kendi sınıfını Set'e
> koyacaksan ikisini doğru tanımla (record'lar otomatik sağlar).

## Küme işlemleri (set algebra)

İki kümeyle matematiksel işlemler, standart metotlarla yapılır:

```java
birlesim.addAll(b);    // A ∪ B  (birleşim)
kesisim.retainAll(b);  // A ∩ B  (kesişim)
fark.removeAll(b);     // A \ B  (fark)
```

Örnek 1 (`./Ornek1.java`) benzersizliği, yinelenen temizlemeyi, küme işlemlerini ve ekleme
sırasını koruyan **LinkedHashSet**'i gösterir.

## LinkedHashSet: ekleme sırası

`HashSet` hızındadır ama elemanları **eklendikleri sırada** tutar. "Benzersiz ama sıralı görünsün"
(örn. yinelenenleri at ama orijinal sırayı koru) gerektiğinde idealdir.

## TreeSet: sıralı küme

Elemanları **sıralı** tutar (doğal sıra veya `Comparator`). Erişim O(log n). `TreeMap`'in küme
karşılığıdır ve aynı **komşuluk/aralık** metotlarını sunar:

```java
set.first(); set.last();
set.floor(40); set.ceiling(40);          // <=40 / >=40
set.headSet(50); set.tailSet(50); set.subSet(20, 65); // aralıklar
```

Örnek 2 (`./Ornek2.java`) bunları gösterir. "Sıralı benzersiz" veya "şu aralıktaki benzersizler"
gerektiğinde kullanılır.

## EnumSet: enum'lar için özel

Elemanlar bir **enum** ise `EnumSet` kullan. İçte bir **bit-maske** tutar; bu yüzden inanılmaz
hızlı ve kompakttır. Yetki/bayrak (flag) kümeleri için biçilmiş kaftandır:

```java
EnumSet<Yetki> editor = EnumSet.of(Yetki.OKU, Yetki.YAZ);
EnumSet<Yetki> admin = EnumSet.allOf(Yetki.class);
EnumSet.complementOf(editor);  // tümleyen
```

## Hangisini seçmeli?

| İhtiyaç | Sınıf |
|---------|-------|
| Hızlı, sıra önemsiz | `HashSet` |
| Ekleme sırası korunsun | `LinkedHashSet` |
| Sıralı / aralık sorgusu | `TreeSet` |
| Elemanlar enum | `EnumSet` |
| Çok thread'li | `ConcurrentHashMap.newKeySet()` / `CopyOnWriteArraySet` |

## Özet

`Set`'in benzersizlik garantisini; hızlı **HashSet** ile küme işlemlerini ve sırayı koruyan
**LinkedHashSet**'i (Örnek 1); sıralı **TreeSet** ile enum'lara özel **EnumSet**'i (Örnek 2)
öğrendik. `Map` ailesindeki seçim mantığının aynısı `Set` için de geçerlidir. Sırada, öncelik ve
uç-erişim koleksiyonları: **PriorityQueue ve ArrayDeque**.
