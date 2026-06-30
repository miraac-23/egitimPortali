import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  CompletableFuture - ASENKRON PROGRAMLAMA - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   CompletableFuture<T>, "ileride tamamlanacak bir hesabın sonucunu" temsil
 *   eden bir sınıftır. Asenkron (eş zamanlı olmayan) işleri başlatır ve
 *   sonuçlarını ZİNCİRLEYEREK (callback'lerle) işlemeyi sağlar.
 *
 * NEDEN GELDİ? (Eski Future'ın sorunları)
 *   Java 5'teki java.util.concurrent.Future yetersizdi:
 *     - Sonucu almak için get() ile BLOKLANMAK gerekiyordu (thread bekler).
 *     - İki future'ı birleştirmek (combine) mümkün değildi.
 *     - Tamamlandığında otomatik callback çalıştırılamıyordu.
 *     - Hata yönetimi (exception handling) zincirleme yapılamıyordu.
 *
 *   CompletableFuture bunların hepsini çözer: bloklamadan, zincirleyerek,
 *   hataları yöneterek asenkron akışlar kurarsın.
 *
 * NE İŞE YARAR: Web servis çağrıları, veritabanı sorguları, dosya okuma gibi
 *   I/O ağırlıklı işleri paralel yürütüp sonuçları birleştirme.
 *
 * TEMEL METOTLAR:
 *   supplyAsync  : asenkron başlat (değer üretir)
 *   thenApply    : sonucu dönüştür (map gibi)
 *   thenCompose  : başka bir future ile zincirle (flatMap gibi)
 *   thenCombine  : iki future'ı birleştir
 *   thenAccept   : sonucu tüket (Consumer)
 *   exceptionally: hata olursa yedek değer
 *   allOf/anyOf  : birden fazla future'ı bekle
 */
public class CompletableFutureOrnekleri {

    public static void main(String[] args) throws Exception {

        System.out.println("=== 1. supplyAsync + thenApply + thenAccept ===\n");
        CompletableFuture<String> akis = CompletableFuture
                .supplyAsync(() -> {
                    uyut(300);
                    return "ham veri";
                })
                .thenApply(veri -> veri.toUpperCase())     // dönüştür
                .thenApply(veri -> "[islenmis] " + veri);  // tekrar dönüştür
        // thenAccept ile sonucu tüket (geri dönüş yok)
        akis.thenAccept(sonuc -> System.out.println("Sonuc: " + sonuc));
        akis.join(); // demo için ana thread'in bitmesini beklet

        System.out.println("\n=== 2. thenCombine : İki asenkron işi birleştir ===\n");
        // Senaryo: Fiyat servisi ve vergi servisi paralel calisir, sonra toplanir
        CompletableFuture<Double> fiyatF = CompletableFuture.supplyAsync(() -> {
            uyut(200);
            System.out.println("   Fiyat servisi tamam");
            return 1000.0;
        });
        CompletableFuture<Double> vergiF = CompletableFuture.supplyAsync(() -> {
            uyut(250);
            System.out.println("   Vergi servisi tamam");
            return 180.0;
        });
        CompletableFuture<Double> toplamF = fiyatF.thenCombine(vergiF,
                (fiyat, vergi) -> fiyat + vergi);
        System.out.println("Toplam (fiyat+vergi): " + toplamF.get() + " TL");

        System.out.println("\n=== 3. thenCompose : Bağımlı asenkron zincir ===\n");
        // Önce kullanıcı id'sini al, SONRA o id ile siparişleri çek (sıralı bağımlılık)
        CompletableFuture<String> siparisler = kullaniciIdGetir("ahmet")
                .thenCompose(id -> siparisleriGetir(id));
        System.out.println(siparisler.get());

        System.out.println("\n=== 4. exceptionally : Hata yönetimi ===\n");
        CompletableFuture<Integer> hataliAkis = CompletableFuture
                .<Integer>supplyAsync(() -> {
                    throw new RuntimeException("Servis cokmesi!");
                })
                .exceptionally(ex -> {
                    System.out.println("   Hata yakalandi: " + ex.getMessage());
                    return -1; // yedek (fallback) deger
                });
        System.out.println("Hatadan sonra deger: " + hataliAkis.get());

        System.out.println("\n=== 5. allOf : Birden fazla işi paralel bekle ===\n");
        // 3 farklı servisi aynı anda çağır, hepsi bitince devam et
        long baslangic = System.currentTimeMillis();
        CompletableFuture<String> s1 = servisCagir("Servis-A", 300);
        CompletableFuture<String> s2 = servisCagir("Servis-B", 200);
        CompletableFuture<String> s3 = servisCagir("Servis-C", 400);

        CompletableFuture<Void> hepsi = CompletableFuture.allOf(s1, s2, s3);
        hepsi.join(); // hepsi bitene kadar bekle

        // Sonuçları topla
        List<String> sonuclar = Arrays.asList(s1, s2, s3).stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        long sure = System.currentTimeMillis() - baslangic;
        System.out.println("Tum servisler tamam: " + sonuclar);
        // 300+200+400=900ms degil; paralel oldugu icin ~400ms (en yavasi kadar)
        System.out.println("Toplam sure (paralel): ~" + sure + " ms (sirali olsa ~900ms)");

        System.out.println("\n=== 6. Özel ExecutorService ile ===\n");
        ExecutorService havuz = Executors.newFixedThreadPool(2);
        CompletableFuture<String> ozel = CompletableFuture.supplyAsync(() -> {
            return "Ozel thread havuzunda calisti: " + Thread.currentThread().getName();
        }, havuz);
        System.out.println(ozel.get());
        havuz.shutdown();
    }

    static CompletableFuture<String> kullaniciIdGetir(String kullaniciAdi) {
        return CompletableFuture.supplyAsync(() -> {
            uyut(150);
            return "ID-" + kullaniciAdi.hashCode();
        });
    }

    static CompletableFuture<String> siparisleriGetir(String id) {
        return CompletableFuture.supplyAsync(() -> {
            uyut(150);
            return id + " icin siparisler: [S100, S101, S102]";
        });
    }

    static CompletableFuture<String> servisCagir(String ad, int gecikme) {
        return CompletableFuture.supplyAsync(() -> {
            uyut(gecikme);
            return ad + "(" + gecikme + "ms)";
        });
    }

    static void uyut(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
