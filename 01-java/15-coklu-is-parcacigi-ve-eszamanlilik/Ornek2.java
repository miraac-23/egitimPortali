// Ornek2: Gerçek senaryo — konser bileti satışında yarış durumu (race condition).
// 100 bilet var, 4 satış noktası aynı anda satıyor. Korumasız kod "fazla satış" yapar!
// Çalıştırma: java Ornek2.java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Ornek2 {

    static final int TOPLAM_BILET = 100;
    static final int SATIS_NOKTASI = 4;
    static final int DENEME = 50; // her nokta 50 kez satmayı dener (toplam 200 > 100)

    public static void main(String[] args) throws InterruptedException {
        System.out.println(TOPLAM_BILET + " bilet, " + SATIS_NOKTASI + " satış noktası, her biri "
                + DENEME + " kez deniyor.\n");

        // --- 1) Korumasız: race condition -> fazla satış ---
        GuvensizGise guvensiz = new GuvensizGise();
        calistir(() -> guvensiz.satisDene());
        System.out.println("Korumasız gişe   -> satılan: " + guvensiz.satilan
                + (guvensiz.satilan > TOPLAM_BILET ? "  ❗ FAZLA SATIŞ!" : ""));

        // --- 2) synchronized ile koruma ---
        SyncGise sync = new SyncGise();
        calistir(() -> sync.satisDene());
        System.out.println("synchronized gişe -> satılan: " + sync.satilan + "  (doğru, en fazla " + TOPLAM_BILET + ")");

        // --- 3) ReentrantLock ile koruma (daha esnek kilit) ---
        LockGise lock = new LockGise();
        calistir(() -> lock.satisDene());
        System.out.println("Lock gişe        -> satılan: " + lock.satilan + "  (doğru)");

        // --- 4) AtomicInteger ile kilitsiz koruma ---
        AtomicGise atomik = new AtomicGise();
        calistir(() -> atomik.satisDene());
        System.out.println("Atomic gişe      -> satılan: " + atomik.satilan() + "  (doğru)");
    }

    // Verilen satış işini SATIS_NOKTASI kadar thread'de paralel çalıştırır.
    static void calistir(Runnable satisDene) throws InterruptedException {
        Thread[] threadler = new Thread[SATIS_NOKTASI];
        for (int i = 0; i < SATIS_NOKTASI; i++) {
            threadler[i] = new Thread(() -> {
                for (int j = 0; j < DENEME; j++) satisDene.run();
            });
            threadler[i].start();
        }
        for (Thread t : threadler) t.join();
    }

    // Korumasız: kontrol et + artır iki ayrı adım; araya başka thread girebilir.
    static class GuvensizGise {
        int satilan = 0;
        void satisDene() {
            if (satilan < TOPLAM_BILET) {   // <-- birden çok thread aynı anda "var" görebilir
                Thread.yield();             // yarış penceresini genişletir (hatayı görünür kılar)
                satilan++;                  // <-- hepsi satar -> fazla satış / tutarsız sayı
            }
        }
    }

    static class SyncGise {
        int satilan = 0;
        synchronized void satisDene() {     // tüm "kontrol+artır" tek seferde, tek thread
            if (satilan < TOPLAM_BILET) satilan++;
        }
    }

    static class LockGise {
        int satilan = 0;
        private final ReentrantLock kilit = new ReentrantLock();
        void satisDene() {
            kilit.lock();                   // kilidi al
            try {
                if (satilan < TOPLAM_BILET) satilan++;
            } finally {
                kilit.unlock();             // her durumda bırak
            }
        }
    }

    static class AtomicGise {
        private final AtomicInteger satilan = new AtomicInteger(0);
        void satisDene() {
            // compareAndSet ile atomik "kontrol et ve artır" döngüsü.
            int mevcut;
            while ((mevcut = satilan.get()) < TOPLAM_BILET) {
                if (satilan.compareAndSet(mevcut, mevcut + 1)) return;
            }
        }
        int satilan() { return satilan.get(); }
    }
}
