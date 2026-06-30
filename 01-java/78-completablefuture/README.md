# CompletableFuture (Asenkron Programlama)

Modern uygulamalar sürekli **bekler**: bir API'den yanıt, veritabanından sonuç, dosyadan veri.
Bu beklemeleri tek tek, sırayla yapmak (senkron) zaman kaybıdır — özellikle birbirinden bağımsız
işleri **paralel** yürütebilecekken. **`CompletableFuture`** (Java 8), asenkron görevleri başlatma,
sonuçlarını dönüştürme, birleştirme ve hataları yönetme için zengin, zincirlenebilir bir API sunar.
Concurrency bölümünde thread'leri gördük; burada onların üzerine kurulu yüksek seviyeli asenkron
modeli ele alıyoruz.

## Asenkron görev başlatma ve dönüştürme

```java
CompletableFuture.supplyAsync(() -> hesapla())   // arka planda çalış, sonuç döndür
    .thenApply(x -> x * 2)                        // sonucu dönüştür
    .thenAccept(System.out::println);             // sonucu tüket (yan etki)
```

- **`supplyAsync(fn)`**: Bir görevi arka planda (ayrı thread) çalıştırır ve bir sonuç üretir.
- **`runAsync(r)`**: Sonuç üretmeyen asenkron görev.
- **`thenApply(fn)`**: Sonucu **dönüştürür** (yeni değer döndürür).
- **`thenAccept(c)`**: Sonucu **tüketir** (yan etki, değer yok).
- **`thenCompose(fn)`**: Bir asenkron işin sonucuyla **başka bir asenkron iş** başlatır (zincir
  düzleştirme — `flatMap` gibi).
- **`join()` / `get()`**: Sonucun gelmesini bekler (`get` checked exception atar).

Örnek 1 (`./Ornek1.java`) `supplyAsync` → `thenApply` → `thenCompose` → `thenAccept` zincirini
gösterir.

## Paralel birleştirme

`CompletableFuture`'ın asıl gücü, **bağımsız işleri paralel** yürütüp sonuçlarını birleştirmektir:

```java
f1.thenCombine(f2, (a, b) -> a + b);   // iki işi PARALEL çalıştır, sonuçları birleştir
CompletableFuture.allOf(f1, f2, f3).join();  // hepsinin bitmesini bekle
CompletableFuture.anyOf(f1, f2).join();      // ilk biteni bekle
```

`thenCombine` ile iki 200 ms'lik iş **toplam ~200 ms** sürer (paralel), sırayla yapılsa 400 ms
olurdu. Örnek 2 (`./Ornek2.java`) `thenCombine`, `allOf` ve hata yönetimini gösterir.

## Hata yönetimi

Asenkron zincirde hatalar `try-catch` ile yakalanamaz (farklı thread'de olur). Bunun yerine:

```java
future.exceptionally(ex -> yedekDeger);          // hata olursa yedek değer
future.handle((sonuc, ex) -> ex != null ? ... : sonuc);  // hem başarı hem hata
future.whenComplete((sonuc, ex) -> { ... });     // tamamlanınca (sonucu değiştirmez)
```

Örnek 2 `exceptionally` ile bir servis hatasını yakalayıp yedek değer döndürür.

## Executor seçimi

Varsayılan olarak görevler **`ForkJoinPool.commonPool()`**'da çalışır. Bu, **CPU yoğun** işler için
uygundur ama **G/Ç yoğun** (bloklayan) işlerde havuzu tıkayabilir. Bu durumda kendi executor'ını
ver:

```java
ExecutorService exec = Executors.newFixedThreadPool(10);
CompletableFuture.supplyAsync(() -> apiCagir(), exec);
```

> Java 21 ile **sanal thread'ler** (`Executors.newVirtualThreadPerTaskExecutor()`) G/Ç yoğun
> asenkron işler için ideal bir executor olur — binlerce bloklayan görevi ucuza yönetir.

## Nerede kullanılır?

- Bağımsız API çağrılarını **paralel** yapıp toplam gecikmeyi düşürmek (mikroservisler).
- Bir sonucu birden çok adımda asenkron işlemek (pipeline).
- Spring'in `@Async` metotları `CompletableFuture` döndürebilir; reaktif programlama (WebFlux) bunun
  daha ileri bir modelidir.

## Özet

`CompletableFuture` ile asenkron görev başlatma ve dönüştürmeyi (`supplyAsync`/`thenApply`/
`thenCompose`; Örnek 1); paralel birleştirme (`thenCombine`/`allOf`) ve hata yönetimini
(`exceptionally`; Örnek 2) öğrendik; executor seçimi ve sanal thread bağlantısına değindik.
Asenkron programlama, modern yüksek-performanslı uygulamaların temelidir. Sırada, programa dışarıdan
veri vermek: **komut satırı argümanları**.
