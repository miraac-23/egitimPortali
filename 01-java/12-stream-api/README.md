# Stream API

Bir koleksiyondaki verileri işlerken genelde aynı şeyleri yaparız: filtrele, dönüştür,
sırala, topla. Geleneksel `for` döngüleriyle bu işler hem uzar hem de "ne yapıldığı"
"nasıl yapıldığı"nın içinde kaybolur. **Stream API** (Java 8+), veriyi bir **akış** olarak
düşünüp bu işlemleri okunabilir, zincirleme bir biçimde ifade etmemizi sağlar. Lambda'larla
birleşince Java'da veri işlemenin modern yüzü ortaya çıkar.

## Stream nedir?

Stream, bir veri kaynağı (koleksiyon, dizi, üreteç) üzerinden geçen bir **eleman akışıdır**.
Veriyi saklamaz; üzerinde işlemler tanımlarsın. Bir pipeline üç parçadan oluşur:

1. **Kaynak**: `list.stream()`, `Arrays.stream(...)`, `IntStream.range(...)`
2. **Ara işlemler** (intermediate): `filter`, `map`, `sorted`, `distinct`, `limit` — yeni bir
   stream döndürür, **tembeldir** (lazy).
3. **Sonlandırıcı** (terminal): `collect`, `forEach`, `count`, `reduce`, `sum` — akışı tüketir
   ve sonucu üretir.

```java
List<String> sonuc = isimler.stream()
        .filter(ad -> ad.startsWith("a"))
        .map(String::toUpperCase)
        .sorted()
        .collect(Collectors.toList());
```

Ara işlemler ancak bir sonlandırıcı çağrıldığında çalışır; buna **tembel değerlendirme**
denir ve gereksiz işlemden kaçınmayı sağlar. Örnek 1 (`./Ornek1.java`) temel pipeline'ı,
`forEach`, `count`, `allMatch`, `distinct` ve `limit`'i gösterir.

## reduce ve ilkel akışlar

`reduce`, bir akışı **tek bir değere** indirger (toplam, çarpım, en büyük...):

```java
int toplam = sayilar.stream().reduce(0, Integer::sum);
```

Sayısal işlemlerde `IntStream` / `LongStream` / `DoubleStream` kullanmak daha verimlidir
(kutulama maliyeti yoktur) ve hazır yardımcılar sunar: `sum()`, `average()`, `max()` ve hepsini
tek geçişte veren `summaryStatistics()`:

```java
IntSummaryStatistics ist = sayilar.stream()
        .mapToInt(Integer::intValue).summaryStatistics();
ist.getAverage(); ist.getMax(); // ...
```

Örnek 2 (`./Ornek2.java`) `reduce`, `IntStream`, `summaryStatistics` ve `IntStream.rangeClosed`
ile sayı üretimini gösterir.

## Collectors: toplama ve gruplama

`collect`, akışın sonucunu bir yapıya dönüştürür. `Collectors` sınıfı bunun için güçlü
yardımcılar sunar:

- `toList()` / `toSet()` — listeye/kümeye topla
- `joining(", ", "[", "]")` — metinleri birleştir
- `groupingBy(...)` — bir ölçüte göre `Map` halinde grupla
- `summingDouble(...)`, `counting()`, `averagingInt(...)` — gruplarda toplama
- `partitioningBy(...)` — bir koşula göre ikiye ayır (true/false)

```java
Map<String, Double> kategoriToplam = urunler.stream()
        .collect(Collectors.groupingBy(Urun::kategori,
                 Collectors.summingDouble(Urun::fiyat)));
```

Örnek 3 (`./Ornek3.java`) ürünleri kategoriye göre gruplar, kategori bazında toplam fiyatı
hesaplar, `joining` ile birleştirir ve `partitioningBy` ile pahalı/uygun olarak ayırır.

## Ne zaman stream, ne zaman döngü?

Stream'ler okunabilirliği artırır ve niyeti netleştirir; özellikle filtreleme/dönüştürme/
gruplama zincirlerinde parlar. Ama her şey için zorlamana gerek yok: basit bir sayaç veya
yan etkili bir döngü için klasik `for` daha açık olabilir. Ayrıca stream'ler tek kullanımlıktır
(bir kez tüketilince yeniden kullanılamaz).

## Özet

Stream API ile veriyi akış olarak düşünüp filtreleme, dönüştürme, sıralama, indirgeme ve
gruplama işlemlerini zincirleme yazmayı öğrendik. `Collectors` ile sonucu listeye/haritaya
topladık. Bu, modern Java kodunun her yerinde karşına çıkacak. Sırada, `null` ile başa çıkmanın
güvenli yolu: **Optional**.
