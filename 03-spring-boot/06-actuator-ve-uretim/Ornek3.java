// Ornek3: Özel metrik (Micrometer) — iş olaylarını ölçmek ve /actuator/metrics'ten okumak.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek3 {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Ornek3.class)
                .properties("management.endpoints.web.exposure.include=metrics")
                .run(args);
    }

    @RestController
    static class SiparisController {
        private final Counter siparisSayaci;

        // MeterRegistry enjekte edilir; üzerinde özel bir Counter (sayaç) tanımlarız.
        SiparisController(MeterRegistry registry) {
            this.siparisSayaci = Counter.builder("siparis.olusturulan")
                    .description("Oluşturulan toplam sipariş sayısı")
                    .register(registry);
        }

        @PostMapping("/api/siparis")
        public String olustur() {
            siparisSayaci.increment(); // her siparişte iş metriğini artır
            return "sipariş oluşturuldu";
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ ÖZEL METRİK (Micrometer) ================");

            // 3 sipariş oluştur -> sayaç 3 olmalı.
            for (int i = 0; i < 3; i++) {
                c.post().uri("/api/siparis").retrieve().toBodilessEntity();
            }

            // Özel metriği Actuator'dan oku.
            System.out.println("3 sipariş oluşturuldu.");
            String metrik = c.get().uri("/actuator/metrics/siparis.olusturulan").exchange((req, res) -> {
                int s = res.getStatusCode().value();
                String body = new String(res.getBody().readAllBytes());
                return s == 200 ? body : ("HTTP " + s);
            });
            System.out.println("GET /actuator/metrics/siparis.olusturulan ->");
            System.out.println("  " + metrik);
            System.out.println("==========================================================");
            System.out.println("Özel metrikler (Counter/Timer/Gauge) iş olaylarını ölçer; Prometheus/Grafana ile izlenir.");
        };
    }
}
