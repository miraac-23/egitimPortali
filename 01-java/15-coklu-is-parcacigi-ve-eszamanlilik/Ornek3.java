// Ornek3: Gerçek senaryo — birden çok mağazadan paralel fiyat sorgulama.
// ExecutorService + Callable + Future + invokeAll, ardından CompletableFuture ile async akış.
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Ornek3 {

    record Teklif(String magaza, double fiyat) {}

    // Bir mağazanın fiyat servisini taklit eder (her biri farklı gecikme + fiyat).
    static Teklif fiyatSorgula(String magaza, int gecikmeMs, double fiyat) {
        try { Thread.sleep(gecikmeMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return new Teklif(magaza, fiyat);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Thread havuzu: sınırlı sayıda thread, görevleri sırayla beslenir.
        ExecutorService havuz = Executors.newFixedThreadPool(3);

        // --- invokeAll: tüm görevleri gönder, hepsinin bitmesini bekle ---
        List<Callable<Teklif>> gorevler = List.of(
                () -> fiyatSorgula("Mağaza-A", 200, 1450),
                () -> fiyatSorgula("Mağaza-B", 350, 1399),
                () -> fiyatSorgula("Mağaza-C", 150, 1500),
                () -> fiyatSorgula("Mağaza-D", 300, 1375)
        );

        long t0 = System.currentTimeMillis();
        List<Future<Teklif>> futureler = havuz.invokeAll(gorevler);

        List<Teklif> teklifler = new ArrayList<>();
        for (Future<Teklif> f : futureler) {
            teklifler.add(f.get()); // sonucu al (hazır değilse bekler)
        }
        long sure = System.currentTimeMillis() - t0;

        System.out.println("Gelen teklifler (" + sure + " ms'de paralel toplandı):");
        teklifler.forEach(t -> System.out.printf("  %-9s %.2f TL%n", t.magaza(), t.fiyat()));

        Teklif enUcuz = teklifler.stream().min((a, b) -> Double.compare(a.fiyat(), b.fiyat())).orElseThrow();
        System.out.printf("%nEn ucuz: %s -> %.2f TL%n", enUcuz.magaza(), enUcuz.fiyat());

        // --- CompletableFuture: async akış (sorgula -> kargo ekle -> birleştir) ---
        System.out.println("\n--- CompletableFuture ile async akış ---");
        CompletableFuture<Double> a = CompletableFuture
                .supplyAsync(() -> fiyatSorgula("Mağaza-A", 100, 1450).fiyat(), havuz)
                .thenApply(fiyat -> fiyat + 50); // kargo ekle (dönüşüm)

        CompletableFuture<Double> b = CompletableFuture
                .supplyAsync(() -> fiyatSorgula("Mağaza-B", 120, 1399).fiyat(), havuz)
                .thenApply(fiyat -> fiyat + 30);

        // İki async sonucu birleştir (thenCombine): hangisi daha avantajlı?
        CompletableFuture<String> karar = a.thenCombine(b, (fiyatA, fiyatB) ->
                fiyatA <= fiyatB
                        ? "A daha avantajlı: " + fiyatA + " TL (kargo dahil)"
                        : "B daha avantajlı: " + fiyatB + " TL (kargo dahil)");

        System.out.println(karar.get()); // akış tamamlanınca sonucu yazdır

        // Havuzu düzgün kapat.
        havuz.shutdown();
        havuz.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("\nThread havuzu kapatıldı.");
    }
}
