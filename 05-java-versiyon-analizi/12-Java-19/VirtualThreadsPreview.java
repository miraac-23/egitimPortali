import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JAVA 19 - VIRTUAL THREADS (JEP 425) - 1. PREVIEW (Project Loom)
 * ===============================================================
 *
 * Bu dosya, Virtual Threads'in Java 19'daki ILK preview halini gosterir.
 * (Kalici ve cok daha derin anlatim icin: 14-Java-21/VirtualThreadsKalici.java)
 *
 * --- KAVRAMLAR ---
 *  PLATFORM THREAD (klasik):
 *    - Bir OS thread'inin dogrudan sarmalayicisidir.
 *    - ~1 MB stack tuketir, OS kaynagidir, sayisi sinirlidir (~binlerce).
 *  VIRTUAL THREAD (yeni):
 *    - JVM tarafindan yonetilir, cok hafiftir (~birkac yuz byte baslangic).
 *    - Az sayida "carrier" (tasiyici) OS thread uzerinde coklanir.
 *    - Bloklayici I/O'da JVM thread'i tasiyicidan SOKER (unmount), tasiyiciyi
 *      baska bir virtual thread'e verir. Boylece OS thread bosta beklemez.
 *    - MILYONLARCA virtual thread olusturulabilir.
 *
 * --- DERLEME / CALISTIRMA (Java 19'da PREVIEW oldugu icin gerekli) ---
 *   javac --release 19 --enable-preview VirtualThreadsPreview.java
 *   java  --enable-preview VirtualThreadsPreview
 *
 *   NOT: Java 21'de bu ozellik KALICI oldu; orada --enable-preview gerekmez.
 */
public class VirtualThreadsPreview {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== JAVA 19: VIRTUAL THREADS (1. PREVIEW) ===\n");

        ornek1_tekVirtualThread();
        ornek2_cokSayidaVirtualThread();
        ornek3_executorIleVirtualThread();

        System.out.println("\n=== Ozet ===");
        System.out.println("""
                * Virtual thread = cok hafif, JVM yonetimli thread.
                * Bloklayici (senkron) kod yaz; reaktif olceklenebilirligi al.
                * Java 19'da PREVIEW; Java 21'de KALICI oldu.
                """);
    }

    /** En basit kullanim: tek bir virtual thread baslat. */
    private static void ornek1_tekVirtualThread() throws InterruptedException {
        System.out.println("--- Ornek 1: Tek virtual thread ---");

        // Thread.ofVirtual() -> virtual thread fabrikasi
        Thread vt = Thread.ofVirtual().name("ilk-vt").start(() ->
                System.out.println("  Merhaba virtual thread'ten! isVirtual="
                        + Thread.currentThread().isVirtual()));

        vt.join(); // bitmesini bekle
        System.out.println();
    }

    /**
     * Cok sayida virtual thread: 10.000 thread. Platform thread'le bu sayi
     * cogu sistemde OutOfMemoryError verir; virtual thread'le sorunsuzdur.
     */
    private static void ornek2_cokSayidaVirtualThread() throws InterruptedException {
        System.out.println("--- Ornek 2: 10.000 virtual thread ---");
        int adet = 10_000;
        AtomicInteger sayac = new AtomicInteger();

        Thread[] threadler = new Thread[adet];
        for (int i = 0; i < adet; i++) {
            threadler[i] = Thread.ofVirtual().start(() -> {
                try {
                    // Bloklayici bir islemi taklit et (orn. ag/DB cagrisi)
                    Thread.sleep(Duration.ofMillis(100));
                    sayac.incrementAndGet();
                } catch (InterruptedException ignored) { }
            });
        }
        for (Thread t : threadler) t.join();

        System.out.println("  " + sayac.get() + " virtual thread tamamlandi.");
        System.out.println("  (Platform thread ile 10.000 adet cogu makinede patlardi.)\n");
    }

    /**
     * En yaygin uretim deseni: her gorev icin yeni bir virtual thread acan
     * bir executor. Web sunucularda "her istek icin bir virtual thread"
     * modelinin temeli budur.
     */
    private static void ornek3_executorIleVirtualThread() throws InterruptedException {
        System.out.println("--- Ornek 3: newVirtualThreadPerTaskExecutor ---");

        // try-with-resources: executor kapaninca tum gorevlerin bitmesini bekler
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                int gorevNo = i;
                executor.submit(() -> {
                    System.out.println("  Gorev " + gorevNo + " calisiyor ("
                            + Thread.currentThread() + ")");
                    return gorevNo;
                });
            }
        } // executor.close() -> tum gorevler bitene kadar bloklar
        System.out.println();
    }
}
