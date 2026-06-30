// Ornek2: CompletableFuture — paralel birleştirme (thenCombine, allOf) ve hata yönetimi.
// Çalıştırma: java Ornek2.java
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Ornek2 {

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();

        // thenCombine: İKİ bağımsız asenkron işi PARALEL çalıştır, sonuçlarını birleştir.
        CompletableFuture<Integer> fiyat = CompletableFuture.supplyAsync(() -> { uyu(200); return 100; });
        CompletableFuture<Integer> adet = CompletableFuture.supplyAsync(() -> { uyu(200); return 3; });
        CompletableFuture<Integer> toplam = fiyat.thenCombine(adet, (f, a) -> f * a);
        System.out.println("thenCombine (paralel) toplam: " + toplam.join()
                + "  (~" + (System.currentTimeMillis() - t0) + " ms, ~200 ms çünkü PARALEL)");

        // allOf: birden çok işi başlat, HEPSİNİN bitmesini bekle.
        List<CompletableFuture<String>> isler = List.of(
                CompletableFuture.supplyAsync(() -> { uyu(100); return "A"; }),
                CompletableFuture.supplyAsync(() -> { uyu(150); return "B"; }),
                CompletableFuture.supplyAsync(() -> { uyu(120); return "C"; }));
        CompletableFuture.allOf(isler.toArray(new CompletableFuture[0])).join();
        String hepsi = isler.stream().map(CompletableFuture::join).collect(Collectors.joining(", "));
        System.out.println("allOf sonuçları: " + hepsi);

        // exceptionally: hata olursa yedek değer döndür (asenkron try-catch gibi).
        CompletableFuture<Integer> hatali = CompletableFuture
                .supplyAsync(() -> { if (true) throw new RuntimeException("servis çöktü"); return 1; })
                .exceptionally(ex -> {
                    System.out.println("exceptionally: hata yakalandı -> " + ex.getMessage());
                    return -1;   // yedek değer
                });
        System.out.println("hata sonrası değer: " + hatali.join());

        System.out.println("""

                --- Birleştirme ve hata yönetimi ---
                thenCombine(diger, fn): iki bağımsız işi PARALEL çalıştırıp sonuçları birleştirir (toplam süre = en uzun iş).
                allOf(...): birçok işi başlat, hepsinin bitmesini bekle (anyOf: ilk biteni bekler).
                exceptionally(fn): zincirde hata olursa yedek değer üret (handle/whenComplete de var).
                Faydası: G/Ç ağırlıklı işleri (API çağrıları, DB) PARALEL yürütüp toplam süreyi düşürmek.""");
    }

    static void uyu(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
