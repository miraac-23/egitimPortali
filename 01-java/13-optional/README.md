# Optional ile Null Güvenliği

`null`, programlamanın en sık hata kaynaklarından biridir; öyle ki onu icat eden Tony Hoare
buna "milyar dolarlık hata" demiştir. Bir metot `null` döndürdüğünde, çağıran taraf bunu
kontrol etmeyi unutursa kaçınılmaz sonuç `NullPointerException`'dır. Java 8, bu probleme
zarif bir çözüm getirdi: **`Optional`**. Optional, "burada bir değer olabilir ya da
olmayabilir" durumunu **tip düzeyinde, açıkça** ifade eder.

## Optional oluşturma

Üç temel fabrika metodu vardır:

```java
Optional<String> dolu = Optional.of("merhaba");        // değer kesin var (null verilemez)
Optional<String> bos  = Optional.empty();              // değer yok
Optional<String> belki = Optional.ofNullable(deger);   // null ise empty olur
```

Bir metodun "değer bulamayabilir" olduğunu belirtmek için `null` döndürmek yerine
`Optional` döndürmek çok daha açıktır; çağıran taraf değerin yokluğunu **görmezden gelemez**.

## Değere güvenli erişim

Optional'ın amacı `isPresent()`/`get()` ikilisini elle yazmak *değildir* (bu, `null` kontrolünün
süslü hali olurdu). Asıl güç, değerin yokluğunu zarifçe ele alan metotlardadır:

- `ifPresent(consumer)` — değer varsa çalıştır
- `orElse(varsayilan)` — yoksa hazır bir değer döndür
- `orElseGet(supplier)` — yoksa değeri **tembel** üret (pahalı varsayılanlar için)
- `orElseThrow(...)` — yoksa anlamlı bir exception fırlat
- `ifPresentOrElse(varsa, yoksa)` — iki ayrı davranış

```java
String ad = bul(id).orElse("bilinmiyor");
Urun u = bul(id).orElseThrow(() -> new NoSuchElementException("yok"));
```

Örnek 1 (`./Ornek1.java`) oluşturma ve erişim metotlarını tek tek gösterir.

## Dönüşümler: map, filter, flatMap

Optional'ı asıl güçlü kılan, içindeki değeri **açmadan** dönüştürebilmendir. Değer yoksa
işlemler sessizce atlanır; `null` kontrolü zincirinden kurtulursun:

- `map(fn)` — değer varsa dönüştürür, yoksa empty kalır
- `filter(koşul)` — koşul sağlanmazsa empty'ye düşürür
- `flatMap(fn)` — dönüşümün kendisi `Optional` döndürüyorsa iç içe Optional'ı düzleştirir

```java
Optional.of("  Ada  ")
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(String::toUpperCase)
        .orElse("(yok)");
```

Örnek 2 (`./Ornek2.java`) `map`/`filter`/`flatMap`'i, alanı `null` olabilen bir kullanıcı
üzerinde gösterir.

## Pratik: repository deseni

Optional'ın en doğal kullanımı, "bir şey bulmaya çalışan ama bulamayabilen" metotlardır.
Spring Data JPA'nın `findById` metodunun `Optional` döndürmesi tesadüf değildir. Örnek 3
(`./Ornek3.java`) bir ürün deposunda id'ye göre arama yapar; bulunca alanına erişir,
bulamayınca `orElse`/`orElseThrow` ile zarifçe davranır.

## Anti-pattern'ler (kaçın!)

- `optional.get()`'i kontrolsüz çağırma — değer yoksa `NoSuchElementException` atar; bu,
  `null` kontrolünü atlamaktan farksızdır. `orElse...` ailesini kullan.
- Optional'ı **alan**, **metot parametresi** veya **koleksiyon elemanı** yapma. O, esas olarak
  **dönüş tipi** için tasarlandı. (`Optional<List<T>>` yerine boş liste döndür.)
- `if (opt.isPresent()) opt.get()` kalıbı yerine `map`/`ifPresent`/`orElse` kullan.

## Özet

Optional ile değerin yokluğunu tip düzeyinde açıkça ifade etmeyi, `null` kontrolü zincirinden
`map`/`filter`/`flatMap` ile kurtulmayı ve `orElse`/`orElseThrow` ile zarif varsayılan/hata
davranışı kurmayı öğrendik. Bu, hem daha güvenli hem de daha okunabilir kod demektir. Buraya
kadar dilin çekirdeğini ve modern özelliklerini gördük; sırada dış dünyayla konuşmaya
başlıyoruz: **dosya işlemleri ve I/O**.
