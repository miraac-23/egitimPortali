# Collections Framework

Dizilerle sabit sayıda veriyi tutmayı öğrendik. Ama gerçek programlarda veri sürekli büyür,
küçülür, sıralanır, gruplanır. **Collections Framework**, Java'nın bu ihtiyaçlar için sunduğu
zengin veri yapıları kütüphanesidir: dinamik listeler, tekrarsız kümeler, anahtar-değer
haritaları ve onlarla çalışan hazır algoritmalar. Bir kez öğrenince, neredeyse her programda
kullanacaksın.

## Genel resim

Koleksiyonlar üç ana aileye ayrılır:

- **List** — Sıralı, indeksli, tekrara izin veren dizi benzeri yapı. (`ArrayList`, `LinkedList`)
- **Set** — Tekrar etmeyen elemanlar kümesi. (`HashSet`, `LinkedHashSet`, `TreeSet`)
- **Map** — Anahtar → değer eşlemesi. (`HashMap`, `LinkedHashMap`, `TreeMap`)

> Map teknik olarak `Collection` arayüzünü uygulamaz ama framework'ün ayrılmaz parçasıdır.

## List

`List`, elemanları ekleme sırasına göre tutar, indeksle erişime izin verir ve tekrar eden
değerleri kabul eder. En sık kullanılanı `ArrayList`'tir:

```java
List<String> sehirler = new ArrayList<>();
sehirler.add("İzmir");
sehirler.get(0);
sehirler.remove("İzmir");
sehirler.size();
```

Sıralamak için `Collections.sort(list)` (doğal sıra) veya `list.sort(Comparator...)` (özel
sıra) kullanırsın. `Comparator.comparing(...)` ile bir alana göre kolayca sıralayabilirsin.
Örnek 1 (`./Ornek1.java`) liste işlemlerini ve iki farklı sıralamayı gösterir.

> `ArrayList` indeksli erişimde hızlıdır; baştan/ortadan çok sık ekleme/silme yapıyorsan
> `LinkedList` düşünülebilir. Pratikte çoğu zaman `ArrayList` doğru seçimdir.

## Set

`Set`, **tekrar etmeyen** elemanlar tutar; aynı elemanı iki kez eklersen ikincisi yok sayılır.
Bir listeyi tekilleştirmenin en kolay yolu onu bir `Set`'e koymaktır:

- `HashSet` — en hızlısı, sıra garantisi yok.
- `LinkedHashSet` — ekleme sırasını korur.
- `TreeSet` — elemanları sıralı tutar.

## Map

`Map`, bir **anahtarı bir değere** eşler — bir sözlük gibi. Aynı anahtara tekrar `put`
yaparsan değer güncellenir:

```java
Map<String, Integer> stok = new HashMap<>();
stok.put("klavye", 12);
stok.get("klavye");                 // 12
stok.getOrDefault("yok", 0);        // 0
stok.containsKey("klavye");         // true
```

`getOrDefault` ve `computeIfAbsent`, sayma/gruplama işlerinde hayat kurtarır. `TreeMap`
anahtarları sıralı tutar, `LinkedHashMap` ekleme sırasını korur. Örnek 2 (`./Ornek2.java`)
`Set` ile tekilleştirmeyi ve `Map` ile kelime frekansı saymayı gösterir.

## Hangisini ne zaman?

| İhtiyaç | Seçim |
|---------|-------|
| Sıralı, indeksli, tekrar olabilir | `List` (`ArrayList`) |
| Tekrarsız, hızlı üyelik testi | `Set` (`HashSet`) |
| Tekrarsız + sıralı | `TreeSet` |
| Anahtar → değer | `Map` (`HashMap`) |
| Anahtarları sıralı istiyorum | `TreeMap` |
| Ekleme sırası korunsun | `Linked...` türleri |

## Hepsini bir araya getirmek

Gerçek problemlerde bu yapıları birlikte kullanırsın. Örnek 3 (`./Ornek3.java`) bir öğrenci
listesini not ortalamasına göre sıralar, ardından `Map<String, List<Ogrenci>>` ile bölümlere
göre gruplayıp her bölümün ortalamasını hesaplar. `record`, `Comparator` zinciri ve
`computeIfAbsent` burada bir arada çalışır.

## Özet

`List`, `Set` ve `Map`'i; hangi durumda hangisinin uygun olduğunu; ekleme, erişim, sıralama
ve gruplama işlemlerini gördük. Bu yapılar günlük programlamanın bel kemiğidir. Sırada,
koleksiyonları tip güvenli yapan mekanizma: **Generics**.
