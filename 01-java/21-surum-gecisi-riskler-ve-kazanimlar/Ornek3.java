// Ornek3: Geçişin SOMUT KAZANCI — platform thread'leri vs sanal thread'ler (Java 21).
// G/Ç bekleyen çok sayıda görevde sanal thread'ler çok daha ölçeklenir.
// Çalıştırma: java Ornek3.java   (JDK 21 gerektirir)
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Ornek3 {

    static final int GOREV = 1000; // her biri ~50 ms G/Ç bekleyen görev

    public static void main(String[] args) throws InterruptedException {
        System.out.println(GOREV + " adet, her biri ~50 ms bekleyen G/Ç görevi.\n");

        // 1) Eski yaklaşım: küçük, sabit boyutlu platform thread havuzu.
        // Görevler kuyruğa girer; aynı anda yalnızca havuz boyutu kadarı çalışır -> yavaş.
        try (ExecutorService platform = Executors.newFixedThreadPool(16)) {
            long ms = sureOlc(platform);
            System.out.printf("Platform thread havuzu (16) : ~%5d ms%n", ms);
        }

        // 2) Java 21: her göreve bir sanal thread. Milyonlarcası ucuzdur; G/Ç beklerken
        // işletim sistemi thread'ini bloke etmezler -> neredeyse hepsi aynı anda ilerler.
        try (ExecutorService sanal = Executors.newVirtualThreadPerTaskExecutor()) {
            long ms = sureOlc(sanal);
            System.out.printf("Sanal thread'ler (21)       : ~%5d ms%n", ms);
        }

        System.out.println("\nKazanım: G/Ç ağırlıklı yükte sanal thread'ler, kod neredeyse aynı kalırken");
        System.out.println("çok daha yüksek eşzamanlılık sağlar. Spring Boot 3.2+ bunu destekler.");
    }

    static long sureOlc(ExecutorService exec) throws InterruptedException {
        AtomicInteger biten = new AtomicInteger();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < GOREV; i++) {
            exec.submit(() -> {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                biten.incrementAndGet();
            });
        }
        exec.shutdown();
        exec.awaitTermination(60, TimeUnit.SECONDS);
        return System.currentTimeMillis() - t0;
    }
}
