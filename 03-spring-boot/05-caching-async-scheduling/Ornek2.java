// Ornek2: @Async — uzun süren işleri ayrı thread'lerde, bloklamadan çalıştırmak.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.cache;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableAsync // @Async metotları ayrı thread havuzunda çalıştırır
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    @Service
    public static class RaporServisi {
        // @Async: bu metot çağrıldığında HEMEN döner; iş arka planda (başka thread'de) çalışır.
        // CompletableFuture döndürerek sonucu sonra toplayabiliriz.
        @Async
        public CompletableFuture<String> raporUret(int no) {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {} // ağır iş
            String thread = Thread.currentThread().getName();
            return CompletableFuture.completedFuture("Rapor-" + no + " (thread: " + thread + ")");
        }
    }

    @Bean
    CommandLineRunner selfTest(RaporServisi servis) {
        return args -> {
            System.out.println("\n================ @Async ================");
            long t0 = System.currentTimeMillis();

            // Üç raporu AYNI ANDA tetikle (her biri ~500 ms). Çağrılar hemen döner.
            CompletableFuture<String> r1 = servis.raporUret(1);
            CompletableFuture<String> r2 = servis.raporUret(2);
            CompletableFuture<String> r3 = servis.raporUret(3);

            // Hepsinin bitmesini bekle ve sonuçları yazdır.
            CompletableFuture.allOf(r1, r2, r3).join();
            System.out.println("  " + r1.get());
            System.out.println("  " + r2.get());
            System.out.println("  " + r3.get());

            long sure = System.currentTimeMillis() - t0;
            System.out.println("Toplam süre: ~" + sure + " ms (sıralı olsaydı ~1500 ms olurdu)");
            System.out.println("========================================");
            System.out.println("@Async: e-posta gönderme, rapor üretme gibi işleri arka plana atıp isteği bekletmemek için.");
        };
    }
}
