// Ornek2: Özel HealthIndicator — kendi sağlık kontrolünü /actuator/health'e eklemek.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.actuator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek2 {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Ornek2.class)
                .properties("management.endpoints.web.exposure.include=health",
                        "management.endpoint.health.show-details=always")
                .run(args);
    }

    // Özel sağlık göstergesi: dış bir bağımlılığın (ör. ödeme sağlayıcı) durumunu raporlar.
    // Bean adı "odemeSaglayici" -> /actuator/health'te "odemeSaglayici" bileşeni olarak görünür.
    @Component
    static class OdemeSaglayiciHealth implements HealthIndicator {
        @Override
        public Health health() {
            boolean erisilebilir = dahiliKontrol(); // gerçekte sağlayıcıya ping atılır
            if (erisilebilir) {
                return Health.up().withDetail("saglayici", "AcmePay").withDetail("gecikme_ms", 42).build();
            }
            return Health.down().withDetail("saglayici", "AcmePay").withDetail("hata", "bağlantı yok").build();
        }

        private boolean dahiliKontrol() { return true; } // demo: sağlıklı
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ ÖZEL HEALTH INDICATOR ================");
            // /actuator/health artık dahili (diskSpace, ping) + bizim "odemeSaglayici" bileşenini içerir.
            System.out.println("GET /actuator/health ->");
            System.out.println("  " + c.get().uri("/actuator/health").retrieve().body(String.class));
            System.out.println("=======================================================");
            System.out.println("Bir bileşen DOWN olursa genel health DOWN olur; k8s/load balancer trafiği kesebilir.");
        };
    }
}
