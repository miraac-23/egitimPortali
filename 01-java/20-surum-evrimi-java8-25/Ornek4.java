// Ornek4: JAVA 21 LTS — record patterns, sequenced collections, virtual threads (öncesi vs sonrası).
// Çalıştırma: java Ornek4.java   (JDK 21 gerektirir)
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Ornek4 {

    sealed interface Olay permits Tikla, Yaz {}
    record Tikla(int x, int y) implements Olay {}
    record Yaz(String metin) implements Olay {}

    public static void main(String[] args) throws InterruptedException {
        // ============ record patterns (Java 21): iç içe instanceof vs desen ayrıştırma ============
        Olay olay = new Tikla(120, 45);

        // ÖNCESİ (Java 16-17): instanceof + alanları elle çekme
        String eski;
        if (olay instanceof Tikla t) {
            eski = "tık (" + t.x() + "," + t.y() + ")";
        } else if (olay instanceof Yaz y) {
            eski = "yaz: " + y.metin();
        } else { eski = "?"; }

        // SONRASI (Java 21): switch + RECORD PATTERN — alanlar doğrudan parçalanır
        String yeni = switch (olay) {
            case Tikla(int x, int y) -> "tık (" + x + "," + y + ")";
            case Yaz(String m) -> "yaz: " + m;
        };
        System.out.println("record patterns (eski vs yeni): " + eski + " == " + yeni);

        // ============ Sequenced Collections (Java 21): get(0)/get(size-1) vs getFirst/getLast ============
        List<String> liste = new ArrayList<>(List.of("giriş", "işlem", "çıkış"));
        // ESKİ
        String ilkEski = liste.get(0);
        String sonEski = liste.get(liste.size() - 1);
        // YENİ (Java 21)
        String ilkYeni = liste.getFirst();
        String sonYeni = liste.getLast();
        System.out.println("\nSequenced (eski vs yeni): ilk " + ilkEski + "/" + ilkYeni
                + ", son " + sonEski + "/" + sonYeni);
        System.out.println("reversed(): " + liste.reversed());

        // ============ Virtual Threads (Java 21): platform thread vs sanal thread ============
        System.out.println("\n--- 10.000 G/Ç görevi: platform vs sanal thread ---");
        long platform = sureOlc(Executors.newFixedThreadPool(50));   // sınırlı havuz -> yavaş
        long sanal = sureOlc(Executors.newVirtualThreadPerTaskExecutor()); // her göreve sanal thread
        System.out.printf("Platform havuzu (50): ~%4d ms%n", platform);
        System.out.printf("Sanal thread'ler   : ~%4d ms%n", sanal);

        System.out.println("""

                --- Java 21 LTS NE getirdi? ---
                Virtual threads (yüksek eşzamanlılık), record patterns + switch pattern matching,
                sequenced collections, generational ZGC. Çoğu projede 17->21 düşük riskli, yüksek kazançlı geçiştir.
                (Java 22-25 için README'ye bak: FFM API, Stream Gatherers, sade main, modül import...)""");
    }

    static long sureOlc(java.util.concurrent.ExecutorService exec) throws InterruptedException {
        AtomicInteger biten = new AtomicInteger();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 10_000; i++) {
            exec.submit(() -> {
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                biten.incrementAndGet();
            });
        }
        exec.shutdown();
        exec.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS);
        return System.currentTimeMillis() - t0;
    }
}
