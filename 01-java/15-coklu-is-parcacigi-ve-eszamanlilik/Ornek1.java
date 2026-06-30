// Ornek1: Gerçek senaryo — bir gösterge panelinin (dashboard) verilerini paralel toplamak.
// Üç ayrı "servis" (satış, stok, müşteri) sırayla mı yoksa paralel mi daha hızlı?
// Çalıştırma: java Ornek1.java
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Ornek1 {

    // Yavaş bir uzak servis çağrısını taklit eder (ağ gecikmesi gibi).
    static String servisCagir(String ad, int gecikmeMs) {
        uyu(gecikmeMs);
        return ad + "-verisi";
    }

    public static void main(String[] args) throws InterruptedException {
        // --- 1) Sıralı (sequential): her servis sırayla beklenir ---
        long t0 = System.currentTimeMillis();
        servisCagir("satis", 300);
        servisCagir("stok", 300);
        servisCagir("musteri", 300);
        long sirali = System.currentTimeMillis() - t0;
        System.out.println("Sıralı süre   : ~" + sirali + " ms (yaklaşık toplam)");

        // --- 2) Paralel: üç servisi aynı anda çağır ---
        // Her thread sonucunu ortak, thread-safe bir haritaya yazar.
        Map<String, String> sonuclar = new ConcurrentHashMap<>();

        Runnable satisIsi = () -> sonuclar.put("satis", servisCagir("satis", 300));
        Runnable stokIsi = () -> sonuclar.put("stok", servisCagir("stok", 300));
        Runnable musteriIsi = () -> sonuclar.put("musteri", servisCagir("musteri", 300));

        long t1 = System.currentTimeMillis();
        Thread t_satis = new Thread(satisIsi, "satis-thread");
        Thread t_stok = new Thread(stokIsi, "stok-thread");
        Thread t_musteri = new Thread(musteriIsi, "musteri-thread");

        t_satis.start();
        t_stok.start();
        t_musteri.start();

        // join: üç thread de bitene kadar bekle. Olmazsa sonuçlar henüz hazır olmayabilir.
        t_satis.join();
        t_stok.join();
        t_musteri.join();
        long paralel = System.currentTimeMillis() - t1;

        System.out.println("Paralel süre  : ~" + paralel + " ms (en yavaş servis kadar)");
        System.out.println("Toplanan veri : " + sonuclar);
        System.out.printf("%nKazanç: paralel çalıştırma yaklaşık %.1fx daha hızlı.%n",
                (double) sirali / Math.max(1, paralel));
    }

    static void uyu(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
