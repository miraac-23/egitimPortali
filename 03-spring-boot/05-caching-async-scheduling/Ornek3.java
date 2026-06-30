// Ornek3: @Scheduled — belirli aralıklarla otomatik çalışan görevler (zamanlama).
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır; birkaç saniye boyunca tetiklenir.
package com.egitim.springboot.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling // zamanlanmış görevleri etkinleştirir
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    @Component
    public static class ZamanlanmisGorevler {
        private final AtomicInteger sayac = new AtomicInteger();
        private static final DateTimeFormatter SAAT = DateTimeFormatter.ofPattern("HH:mm:ss");

        // fixedRate: her 1500 ms'de bir çalışır (önceki bitişe bakmadan, sabit aralıkla).
        @Scheduled(fixedRate = 1500)
        public void periyodikGorev() {
            System.out.println("  [zamanlanmış] tik #" + sayac.incrementAndGet()
                    + " @ " + LocalTime.now().format(SAAT));
        }

        // initialDelay + fixedRate: ilk çalışma 500 ms gecikmeli başlar.
        @Scheduled(initialDelay = 500, fixedRate = 2000)
        public void digerGorev() {
            System.out.println("  [zamanlanmış-2] yedekleme kontrolü @ " + LocalTime.now().format(SAAT));
        }
    }

    // Not: Bu örnekte ana çıktı, arka planda çalışan @Scheduled metotlarından gelir.
    // Uygulama açık kaldığı süre boyunca (birkaç saniye) görevler tetiklenir, sonra durdurulur.
    @org.springframework.context.annotation.Bean
    org.springframework.boot.CommandLineRunner aciklama() {
        return args -> {
            System.out.println("\n================ @Scheduled ================");
            System.out.println("Zamanlanmış görevler başladı. Aşağıdaki tikler OTOMATİK, arka planda üretilir:");
            System.out.println("(@Scheduled(fixedRate=1500) ve initialDelay'li ikinci görev)");
            System.out.println("--------------------------------------------");
        };
    }
}
