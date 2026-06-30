# JVM: Garbage Collection ve JIT

Java'nın "yaz bir kez, her yerde çalış" gücü ve performansının arkasında **JVM (Java Virtual
Machine)** vardır. JVM'in iki kritik mekanizması, programının belleğini ve hızını otomatik olarak
yönetir: **Garbage Collection** (çöp toplama — belleği) ve **JIT derleyici** (hızı). İkisini de
sen elle yönetmezsin; ama nasıl çalıştıklarını anlamak, performanslı ve sızıntısız kod yazmanı
sağlar.

## Garbage Collection (çöp toplama)

C/C++'ta belleği elle ayırır ve `free` ile boşaltırsın — unutursan sızıntı, erken boşaltırsan
çökme olur. Java'da bunu **GC** yapar: artık **ulaşılamayan** (hiçbir referansı kalmayan) nesneleri
otomatik tespit edip belleği geri kazanır.

```java
List<byte[]> tutucu = new ArrayList<>();
tutucu.add(new byte[100_000]);   // bellek kullanılır
tutucu.clear();                   // referans gitti -> nesneler çöp
System.gc();                      // GC ÖNERİSİ (garanti değil)
```

Örnek 1 (`./Ornek1.java`) `Runtime` ile bellek kullanımını izler, çöp üretip GC'nin topladığını
gösterir ve **`WeakReference`** ile zayıf referansları gösterir (yalnızca weak referans kalan nesne
GC tarafından toplanabilir — önbellekler için kullanılır).

Önemli noktalar:

- **`System.gc()` yalnızca öneridir** — JVM ne zaman/toplayıp toplamayacağına kendi karar verir.
  Üretimde elle çağırma.
- **Referans türleri:** güçlü (normal), `WeakReference` ("varsa kullan"), `SoftReference` (bellek
  baskısında atılır), `PhantomReference` (temizlik kancası).
- **GC türleri:** **G1** (Java'nın varsayılanı), **ZGC**/**Shenandoah** (çok düşük gecikme; topic
  20'de değindik). Her biri gecikme/verim dengesini farklı kurar.
- **Bellek sızıntısı (Java'da da olur!):** İstemeden tutulan referanslar — büyüyen statik
  koleksiyonlar, kapatılmayan kaynaklar, dinleyici (listener) birikmesi. GC ulaşılabilir nesneyi
  toplayamaz.

## JIT (Just-In-Time) derleyici

JVM, Java kodunu önce platformdan bağımsız **bytecode**'a derler (`javac`). Çalışma anında bu
bytecode **yorumlanır** (interpret). Ama JVM, **sık çalışan ("hot")** metotları tespit edip onları
çalışma anında **yerel makine koduna** derler — işte **JIT** budur. Sonuç: büyük hızlanma.

Bunun gözlemlenebilir sonucu **"ısınma" (warmup)**: bir metot ilk çağrılarda (yorumlanırken)
genelde yavaş, çok çağrıldıktan sonra (JIT derledikten sonra) genelde hızlıdır. JIT ayrıca **satır
içi alma (inlining)**, **ölü kod eleme**, **döngü açma** gibi agresif optimizasyonlar yapar.

Örnek 2 (`./Ornek2.java`) aynı işi tur tur ölçer ve önemli bir gerçeği gösterir: **sayılar tutarlı
bir "hep azalan" eğri vermeyebilir!** İlk tur genelde en yavaştır ama sonraki turlar GC
duraklamaları, OS zamanlaması ve JIT'in farklı anlarda devreye girmesi yüzünden dalgalanır.

> **Asıl ders — mikro-benchmark tuzağı:** Güvenilir performans ölçümü `System.nanoTime()` ile elle
> **yapılmaz**. Warmup, GC gürültüsü ve JIT optimizasyonları (ölü kod eleme, sabit katlama, inlining)
> sonucu çarpıtır; bu yüzden naif ölçümler yanıltıcıdır. Doğru ölçüm için **JMH (Java Microbenchmark
> Harness)** kullanılır — warmup, çoklu iterasyon ve istatistiksel analizi o doğru yapar.

## Bellek alanları (kısa)

JVM belleği başlıca: **Heap** (nesneler — GC burada çalışır; genç/yaşlı kuşak), **Stack** (her
thread'in metot çağrıları ve yerel değişkenleri), **Metaspace** (sınıf meta-verisi). `OutOfMemoryError`
(heap dolu) ve `StackOverflowError` (stack dolu — topic 69) bu alanlarla ilgilidir.

## Özet

JVM'in iki otomatik mekanizmasını öğrendik: ulaşılamayan nesneleri toplayan **Garbage Collection**
(referans türleri, GC türleri, sızıntı nedenleri; Örnek 1) ve sık çalışan kodu makine koduna
derleyip hızlandıran **JIT** (warmup etkisi, mikro-benchmark tuzağı; Örnek 2). Bunları anlamak,
bellek-dostu ve performanslı Java yazmanın temelidir. Bu, ileri başlıklar batch'ini tamamlıyor.
