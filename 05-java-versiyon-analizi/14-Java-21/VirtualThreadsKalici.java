import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JAVA 21 - VIRTUAL THREADS (JEP 444) - KALICI - Project Loom
 * ===========================================================
 *
 * Bu dosya, Java 21'in BAYRAK ozelligi olan Virtual Threads'i derinlemesine
 * gosterir. Klasik (platform) thread havuzu ile virtual thread modelini,
 * COK SAYIDA esZAMANLI BLOKLAYICI ISTEK senaryosunda kiyaslar.
 *
 * ==================== KAVRAMSAL OZET ====================
 * PLATFORM THREAD:
 *   - OS thread'inin 1:1 sarmalayicisi. ~1 MB stack. OS kaynagi.
 *   - Sayisi sinirli (~binlerce). Bloklaninca pahali OS thread bosta bekler.
 * VIRTUAL THREAD:
 *   - JVM yonetimli, cok hafif. Milyonlarca olabilir.
 *   - Az sayida "carrier" OS thread uzerinde coklanir.
 *   - Bloklayici I/O'da carrier'dan SOKULUR (unmount); carrier baska virtual
 *     thread'e gecer. Boylece OS thread asla bosta beklemez (MOUNT/UNMOUNT).
 *
 * NEDEN DEVRIM:
 *   "thread-per-request" modelinde basit/senkron kod yazarsiniz ama platform
 *   thread'lerle olceklenmez. Reaktif programlama olceklenir ama kod karmasik
 *   olur. Virtual threads = BASIT KOD + DEVASA OLCEKLENME.
 *
 * ==================== DERLEME / CALISTIRMA ====================
 * Virtual Threads Java 21'de KALICIDIR; --enable-preview GEREKMEZ.
 *   javac VirtualThreadsKalici.java
 *   java  VirtualThreadsKalici
 *
 * Tasiyici (carrier) thread sayisini gozlemlemek isterseniz:
 *   java -Djdk.virtualThreadScheduler.parallelism=4 VirtualThreadsKalici
 */
public class VirtualThreadsKalici {

    /** Her "istek" ~200 ms bloklayici I/O (DB/HTTP cagrisi) taklit eder. */
    static final Duration ISTEK_GECIKMESI = Duration.ofMillis(200);

    public static void main(String[] args) throws Exception {
        System.out.println("=== JAVA 21: VIRTUAL THREADS (KALICI) ===\n");
        System.out.println("Mevcut islemci sayisi: "
                + Runtime.getRuntime().availableProcessors() + "\n");

        // Senaryo: 10.000 eszamanli "istek". Her biri 200 ms bloklayici bekleme.
        // Ideal sure ~200 ms olmali (hepsi paralel beklerse).
        int istekSayisi = 10_000;

        System.out.println("--- KIYAS: " + istekSayisi
                + " eszamanli bloklayici istek ---\n");

        // 1) KLASIK: sabit boyutlu PLATFORM thread havuzu (200 thread)
        //    -> ayni anda en fazla 200 istek; geri kalanlar KUYRUKTA bekler.
        //    -> bu yuzden toplam sure cok daha uzun olur.
        platformHavuzuIleCalistir(istekSayisi, 200);

        // 2) MODERN: her gorev icin bir VIRTUAL thread
        //    -> 10.000 istek AYNI ANDA bloklanir; OS thread'leri bu sirada
        //       baska isteklere hizmet eder. Toplam sure ~tek istek suresine yakin.
        virtualThreadIleCalistir(istekSayisi);

        // 3) Virtual thread'in ic yapisini gosteren mini ornek
        icYapiOrnegi();

        // 4) Onemli uyarilar
        uyarilar();
    }

    /**
     * KLASIK MODEL: Sabit havuz. 200 platform thread, 10.000 gorev.
     * Gorevler 200'erli partiler halinde isler -> ~ (10000/200) * 200ms = ~10 sn.
     */
    static void platformHavuzuIleCalistir(int istekSayisi, int havuzBoyutu)
            throws InterruptedException {
        System.out.println(">> 1) KLASIK PLATFORM THREAD HAVUZU (boyut=" + havuzBoyutu + ")");
        AtomicLong tamamlanan = new AtomicLong();

        Instant baslangic = Instant.now();
        try (ExecutorService es = Executors.newFixedThreadPool(havuzBoyutu)) {
            for (int i = 0; i < istekSayisi; i++) {
                es.submit(() -> {
                    bloklayiciIstekTaklit();          // ~200 ms bekle
                    tamamlanan.incrementAndGet();
                });
            }
        } // close() -> tum gorevler bitene kadar bekler
        Duration sure = Duration.between(baslangic, Instant.now());

        System.out.println("   Tamamlanan istek : " + tamamlanan.get());
        System.out.println("   Toplam sure      : " + sure.toMillis() + " ms");
        System.out.println("   (Sadece 200 thread oldugu icin istekler partiler "
                + "halinde islendi; sure uzadi.)\n");
    }

    /**
     * MODERN MODEL: Her gorev icin bir virtual thread.
     * 10.000 istek ayni anda bloklanir; toplam sure ~200 ms'ye yakin olur.
     */
    static void virtualThreadIleCalistir(int istekSayisi) throws InterruptedException {
        System.out.println(">> 2) VIRTUAL THREAD (her gorev icin bir tane)");
        AtomicLong tamamlanan = new AtomicLong();

        Instant baslangic = Instant.now();
        // newVirtualThreadPerTaskExecutor: her submit yeni bir virtual thread acar
        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < istekSayisi; i++) {
                es.submit(() -> {
                    bloklayiciIstekTaklit();          // ~200 ms bekle (carrier'dan sokulur)
                    tamamlanan.incrementAndGet();
                });
            }
        }
        Duration sure = Duration.between(baslangic, Instant.now());

        System.out.println("   Tamamlanan istek : " + tamamlanan.get());
        System.out.println("   Toplam sure      : " + sure.toMillis() + " ms");
        System.out.println("   (10.000 istek AYNI ANDA bekledi; az sayida OS thread "
                + "bu beklemeleri coklayarak yonetti.)\n");
    }

    /** Bloklayici bir ag/DB cagrisini taklit eder (CPU degil, BEKLEME aginlikli). */
    static void bloklayiciIstekTaklit() {
        try {
            Thread.sleep(ISTEK_GECIKMESI);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Virtual thread'in kimligini ve carrier davranisini gosteren mini ornek. */
    static void icYapiOrnegi() throws Exception {
        System.out.println(">> 3) VIRTUAL THREAD IC YAPISI");

        List<Future<String>> sonuclar = new ArrayList<>();
        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 3; i++) {
                int no = i;
                sonuclar.add(es.submit(() -> {
                    Thread t = Thread.currentThread();
                    // isVirtual() -> true; thread adi tipik olarak bossa "VirtualThread[#...]"
                    return "  Gorev " + no + " | virtual mi? " + t.isVirtual()
                            + " | " + t;
                }));
            }
        }
        for (Future<String> f : sonuclar) System.out.println(f.get());
        System.out.println();
    }

    static void uyarilar() {
        System.out.println(">> 4) ONEMLI UYARILAR");
        System.out.println("""
                * Virtual thread'leri HAVUZLAMAYIN. Onlar ucuzdur; her gorev icin
                  YENISINI acin (newVirtualThreadPerTaskExecutor). Havuzlamak anti-pattern'dir.
                * CPU-yogun (hesaplama agirlikli) islerde fayda saglamaz; onlar thread'i
                  zaten mesgul tutar. Virtual thread'in kazanci I/O BEKLEMESINDEDIR.
                * 'synchronized' icinde bloklanmak PINNING'e yol acabilir (virtual thread
                  carrier'dan sokulemez). Cozum: ReentrantLock kullanin.
                * Java 21'de KALICIDIR: --enable-preview gerekmez. Spring Boot 3.2+, Tomcat,
                  Helidon, Quarkus tek bir bayrakla virtual thread'i etkinlestirir.
                """);
    }
}
