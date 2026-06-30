// Ornek1: CompletableFuture — asenkron görevler ve sonuçları zincirleme.
// Çalıştırma: java Ornek1.java
import java.util.concurrent.CompletableFuture;

public class Ornek1 {

    public static void main(String[] args) throws Exception {
        long t0 = System.currentTimeMillis();

        // supplyAsync: bir görevi ARKA PLANDA (ayrı thread) çalıştır, bir sonuç döndür.
        CompletableFuture<Integer> gelecek = CompletableFuture
                .supplyAsync(() -> {
                    yavasIs(200);
                    return 10;                      // ham veri
                })
                .thenApply(n -> {                   // sonucu DÖNÜŞTÜR (n -> n*2)
                    System.out.println("  thenApply: " + n + " -> " + (n * 2));
                    return n * 2;
                })
                .thenApply(n -> n + 5);             // tekrar dönüştür

        // join: sonucun gelmesini bekle (bloklar). get() de benzer (checked exception atar).
        Integer sonuc = gelecek.join();
        System.out.println("Sonuç: " + sonuc + "  (~" + (System.currentTimeMillis() - t0) + " ms)");

        // thenCompose: bir asenkron işin sonucuyla BAŞKA bir asenkron iş başlat (zincir).
        CompletableFuture<String> zincir = CompletableFuture
                .supplyAsync(() -> "kullanici42")
                .thenCompose(Ornek1::profilGetir);  // önceki sonuç -> yeni CompletableFuture
        System.out.println("thenCompose: " + zincir.join());

        // thenAccept: sonucu TÜKET (yan etki, değer döndürmez)
        CompletableFuture.supplyAsync(() -> "rapor hazır")
                .thenAccept(r -> System.out.println("thenAccept: " + r))
                .join();

        System.out.println("""

                --- CompletableFuture ---
                Asenkron (bloklamayan) görevleri ve sonuçlarının dönüşümünü zincirler.
                supplyAsync: arka planda çalış+sonuç döndür; runAsync: sonuçsuz görev.
                thenApply (dönüştür) / thenAccept (tüket) / thenCompose (sonuçla yeni async iş başlat).
                join()/get(): sonucu bekle. Varsayılan ForkJoinPool.commonPool'da çalışır (executor verilebilir).""");
    }

    static void yavasIs(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
    static CompletableFuture<String> profilGetir(String id) {
        return CompletableFuture.supplyAsync(() -> { yavasIs(100); return "Profil(" + id + ")"; });
    }
}
