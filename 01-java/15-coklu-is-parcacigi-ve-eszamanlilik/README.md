# Çoklu İş Parçacığı ve Eşzamanlılık

Modern bilgisayarların birden çok çekirdeği var ve gerçek uygulamalar aynı anda birçok iş
yapar: bir gösterge paneli onlarca servisten veri toplar, bir e-ticaret sitesi binlerce
isteği aynı anda karşılar. **Çoklu iş parçacığı (multithreading)** bu paralelliği sağlar; ama
paylaşılan veriye aynı anda dokunulduğunda sinsi hatalar doğar. Bu yüzden hem paralelliği
kurmayı hem de onu **güvenli** kılan eşzamanlılık araçlarını öğrenmek gerekir. Bu konu güçlü
ama dikkat ister — gerçek senaryolarla adım adım ilerleyeceğiz.

> Bu bölümün örnekleri `join`/`awaitTermination` ile thread'leri bekleyerek **düzenli ve
> tekrarlanabilir** çıktı üretir.

## Process ve thread

**Process**, çalışan bir programdır ve kendi belleğine sahiptir. **Thread (iş parçacığı)** ise
bir process içindeki bağımsız bir yürütme akışıdır. Aynı process'in thread'leri **aynı belleği
paylaşır** — bu, hızlı veri paylaşımı sağlar ama "iki thread aynı veriyi aynı anda değiştirirse
ne olur?" sorusunu da doğurur.

## Neden paralellik? Gerçek bir kazanç

Diyelim ki bir gösterge paneli üç ayrı servisten (satış, stok, müşteri) veri çekiyor ve her
biri ~300 ms sürüyor. Sırayla beklersen ~900 ms; üçünü **paralel** çalıştırırsan toplam süre
en yavaş servis kadar, yani ~300 ms olur. Örnek 1 (`./Ornek1.java`) bunu canlı ölçer:
servisleri ayrı thread'lerde çalıştırır, sonuçları thread-safe bir `ConcurrentHashMap`'e yazar
ve `join` ile hepsinin bitmesini bekler.

### Thread oluşturma

İki yol vardır; tercih edilen, işi bir `Runnable` (genelde lambda) olarak verip `Thread`'e
geçmektir:

```java
Runnable is = () -> sonuc.put("satis", servisCagir());
Thread t = new Thread(is, "satis-thread");
t.start();   // YENİ bir iş parçacığı başlatır (run() değil!)
t.join();    // bu thread bitene kadar BEKLE
```

`start()` yeni bir thread'de çalışır; doğrudan `run()` çağırırsan paralellik olmaz. `join()`,
"sonuçlar hazır olana dek bekle" demenin yoludur.

## Yarış durumu (race condition): en kritik konu

Asıl tehlike burada. Bir konser için 100 bilet var ve 4 satış noktası aynı anda satıyor.
"Bilet var mı? Varsa sat" kodu masum görünür ama **iki adımdır**: önce kontrol, sonra artırma.
İki thread aynı anda "var" görüp ikisi de satarsa **fazla satış** olur — gerçek hayatta felaket.

Örnek 2 (`./Ornek2.java`) bu hatayı önce korumasız kodla **gösterir** (yarış durumu yüzünden
çoğu çalıştırmada 100'den fazla bilet satar veya tutarsız bir sayı üretir), sonra üç farklı
yolla düzeltir:

- **`synchronized`**: Bir metodu/bloğu aynı anda yalnızca tek thread'in çalıştırmasını
  garantiler (karşılıklı dışlama).
  ```java
  synchronized void satisDene() { if (satilan < TOPLAM) satilan++; }
  ```
- **`ReentrantLock`**: `synchronized`'in daha esnek hali; `tryLock`, zaman aşımı, adil kilit
  gibi seçenekler sunar. `lock()`/`unlock()`'u her zaman `try/finally` ile kullan.
- **`AtomicInteger`**: Kilit kullanmadan, donanım destekli atomik işlemler (`compareAndSet`,
  `incrementAndGet`). Sayaçlar için hem doğru hem hızlıdır.

### Görünürlük (visibility) ve `volatile`

Race condition'ın az bilinen bir akrabası **görünürlük** sorunudur: bir thread'in yazdığı
değeri başka bir thread, önbellekleme yüzünden hemen görmeyebilir. Bir bayrağı (`boolean
calisiyor`) thread'ler arasında paylaşırken `volatile` anahtar kelimesi, her okumanın güncel
değeri görmesini sağlar. `volatile` görünürlüğü çözer ama "kontrol+artır" gibi bileşik
işlemleri atomik yapmaz — onlar için yine `synchronized`/atomik tipler gerekir.

### Deadlock (kilitlenme)

İki thread, birbirinin tuttuğu kilidi beklerse sonsuza dek bekler — buna **deadlock** denir.
Kaçınmanın klasik yolu: kilitleri her zaman **aynı sırada** almak.

## ExecutorService: thread havuzları

Her iş için elle `Thread` yaratmak pahalıdır ve yönetimi zordur. Gerçek uygulamalar bunun
yerine bir **thread havuzu** kullanır: `ExecutorService` görevleri sınırlı sayıda thread
üzerinde çalıştırır, thread'leri yeniden kullanır.

- `Runnable` değer döndürmez; **`Callable<T>`** bir değer döndürür (ve hata fırlatabilir).
- `submit(callable)` hemen bir **`Future<T>`** döndürür; sonucu `future.get()` ile alırsın.
- `invokeAll(görevler)` tüm görevleri gönderip hepsinin bitmesini bekler.
- İş bitince `shutdown()` + `awaitTermination(...)` ile havuzu düzgün kapatırsın.

```java
ExecutorService havuz = Executors.newFixedThreadPool(3);
List<Future<Teklif>> sonuc = havuz.invokeAll(gorevler);
havuz.shutdown();
```

### CompletableFuture: modern async akış

`CompletableFuture`, asenkron işleri **zincirleyerek** ifade etmeni sağlar: bir sonucu üret
(`supplyAsync`), dönüştür (`thenApply`), iki sonucu birleştir (`thenCombine`). Bloklamadan,
okunabilir async akışlar kurarsın. Örnek 3 (`./Ornek3.java`) birden çok mağazadan paralel
fiyat sorgular (`invokeAll` + `Future`), en ucuzu bulur, sonra `CompletableFuture` ile iki
mağazanın kargo dahil fiyatını async olarak birleştirip karar verir.

## Pratik notlar

- Mümkünse paylaşılan değişebilir durumdan kaçın; **değişmez (immutable)** veriyle çalışmak
  eşzamanlılık hatalarını kökten azaltır.
- Elle `Thread` yerine `ExecutorService`/`CompletableFuture` gibi yüksek seviyeli araçları
  tercih et.
- Paylaşılan koleksiyon gerekiyorsa `ConcurrentHashMap`, `CopyOnWriteArrayList` gibi
  eşzamanlı (concurrent) koleksiyonları kullan.
- Spring'de `@Async` ile bir metodu ayrı bir thread havuzunda çalıştırabilirsin; altında işte
  bu mekanizmalar vardır.

## Özet

Paralelliğin gerçek kazancını (Örnek 1), yarış durumunu ve onu `synchronized`/`ReentrantLock`/
`AtomicInteger` ile çözmeyi (Örnek 2), `ExecutorService`/`Callable`/`Future` ve
`CompletableFuture` ile havuz tabanlı async işlemeyi (Örnek 3) gerçek senaryolarla gördük.
Görünürlük (`volatile`) ve deadlock kavramlarına değindik. Eşzamanlılık güçlüdür ama disiplin
ister: paylaşılan durumu koru, mümkünse ondan kaç. Sırada, çalışma zamanında kodu inceleyip
yönlendiren **reflection ve annotations** var.
