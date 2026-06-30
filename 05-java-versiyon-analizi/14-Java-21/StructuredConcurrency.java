import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

/**
 * JAVA 21 - STRUCTURED CONCURRENCY (JEP 453) - PREVIEW
 * =====================================================
 *
 * Bu dosya, eszamanli alt gorevleri TEK BIR IS BIRIMI gibi yoneten
 * StructuredTaskScope'u gosterir. Virtual Threads ile mukemmel calisir.
 *
 * NEDIR?
 *   Bir gorevin alt gorevleri birlikte baslar, birlikte tamamlanir veya
 *   birlikte iptal olur. Hata yayilimi ve iptal OTOMATIKTIR.
 *
 * NEDEN GELDI?
 *   Manuel ExecutorService + Future kullaniminda:
 *     - bir alt gorev patlarsa digerlerini iptal etmek zor,
 *     - sizinti (leak) onlemek zor,
 *     - kontrol akisi dagilir ("unstructured concurrency").
 *   StructuredTaskScope bu sorunlari yapisal olarak cozer. Her alt gorev bir
 *   VIRTUAL THREAD'de calisir (cok ucuz), bu yuzden Loom'un dogal tamamlayicisidir.
 *
 * --- DERLEME / CALISTIRMA (Java 21'de PREVIEW oldugu icin GEREKLI) ---
 *   javac --release 21 --enable-preview StructuredConcurrency.java
 *   java  --enable-preview StructuredConcurrency
 *
 *   NOT: StructuredTaskScope API'si surumler arasinda degisebilir (preview).
 */
public class StructuredConcurrency {

    public static void main(String[] args) throws Exception {
        System.out.println("=== JAVA 21: STRUCTURED CONCURRENCY (PREVIEW) ===\n");

        basariliSenaryo();
        System.out.println();
        hataliSenaryo();
    }

    /**
     * GERCEK HAYAT: Bir kullanici profil sayfasi icin 3 farkli servisten
     * paralel veri cekiyoruz. ShutdownOnFailure: biri patlarsa digerleri iptal.
     */
    static void basariliSenaryo() throws Exception {
        System.out.println("--- Basarili senaryo (3 servis paralel) ---");
        long t0 = System.currentTimeMillis();

        // try-with-resources: scope kapaninca tum alt gorevler temizlenir
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Her fork() bir VIRTUAL thread'de calisir
            var kullaniciF  = scope.fork(() -> servisCagir("Kullanici servisi", 150, "Ayse"));
            var siparisF    = scope.fork(() -> servisCagir("Siparis servisi", 200, "42 siparis"));
            var oneriF      = scope.fork(() -> servisCagir("Oneri servisi", 100, "5 oneri"));

            scope.join();              // hepsinin bitmesini bekle
            scope.throwIfFailed();     // biri patladiysa istisna firlat

            // Hepsi basarili -> sonuclari birlestir
            System.out.println("  SONUC: " + kullaniciF.get()
                    + " | " + siparisF.get() + " | " + oneriF.get());
        }

        long sure = System.currentTimeMillis() - t0;
        System.out.println("  Toplam sure: " + sure + " ms");
        System.out.println("  (Seri olsa ~450 ms surerdi; paralel oldugu icin ~en yavas "
                + "gorev kadar = ~200 ms.)");
    }

    /**
     * Bir alt gorev hata firlatirsa: ShutdownOnFailure DIGER alt gorevleri
     * OTOMATIK iptal eder ve hatayi yukari yayar. Sizinti olmaz.
     */
    static void hataliSenaryo() throws InterruptedException {
        System.out.println("--- Hatali senaryo (bir servis patliyor) ---");
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> servisCagir("Hizli servis", 50, "OK"));
            scope.fork(() -> {
                Thread.sleep(Duration.ofMillis(80));
                throw new RuntimeException("Odeme servisi cevap vermedi!");
            });

            scope.join();
            scope.throwIfFailed(); // RuntimeException buradan firlar
            System.out.println("  Buraya ulasilmaz.");
        } catch (Exception e) {
            // Diger alt gorevler otomatik iptal edildi
            System.out.println("  YAKALANAN HATA: " + e.getCause().getMessage());
            System.out.println("  (Diger alt gorevler otomatik iptal edildi; sizinti yok.)");
        }
    }

    /** Bloklayici bir servis cagrisini taklit eder. */
    static String servisCagir(String ad, int ms, String sonuc) throws InterruptedException {
        Thread.sleep(Duration.ofMillis(ms));
        System.out.println("    [" + ad + "] tamamlandi (" + ms + " ms)");
        return sonuc;
    }
}
