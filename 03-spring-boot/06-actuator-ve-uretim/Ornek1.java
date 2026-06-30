// Ornek1: Spring Boot Actuator — uygulamayı izlemek için hazır endpoint'ler (health, metrics, info).
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.actuator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        // Actuator endpoint'leri varsayılan olarak yalnızca /health açıktır; diğerlerini açıyoruz.
        // (Gerçekte bu ayarlar application.yml'de management.* altında yapılır.)
        new SpringApplicationBuilder(Ornek1.class)
                .properties(
                        "management.endpoints.web.exposure.include=health,info,metrics",
                        "management.endpoint.health.show-details=always",
                        "management.info.env.enabled=true",
                        "info.app.ad=Eğitim Portalı",
                        "info.app.surum=1.0")
                .run(args);
    }

    @RestController
    static class ApiController {
        @GetMapping("/selam") String selam() { return "merhaba"; }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ ACTUATOR ================");

            // /actuator/health: uygulama sağlıklı mı? (load balancer / k8s probe'ları bunu kullanır)
            System.out.println("GET /actuator/health -> " + al(c, "/actuator/health"));

            // /actuator/info: uygulama hakkında bilgi (sürüm vb.)
            System.out.println("GET /actuator/info   -> " + al(c, "/actuator/info"));

            // /actuator/metrics: mevcut metrik adları
            String metrikler = al(c, "/actuator/metrics");
            System.out.println("GET /actuator/metrics-> " + metrikler.substring(0, Math.min(160, metrikler.length())) + "...");

            // Belirli bir metrik: JVM bellek kullanımı
            System.out.println("GET .../jvm.memory.used -> " + al(c, "/actuator/metrics/jvm.memory.used"));
            System.out.println("==========================================");
            System.out.println("Actuator: health (sağlık), metrics (ölçüm), info, env, loggers... üretim izlemenin temeli.");
        };
    }

    // Güvenli GET: 4xx'te exception fırlatmaz; 200 ise gövdeyi, değilse durum kodunu döndürür.
    static String al(RestClient c, String uri) {
        return c.get().uri(uri).exchange((req, res) -> {
            int s = res.getStatusCode().value();
            String body = new String(res.getBody().readAllBytes());
            return s == 200 ? body : ("HTTP " + s);
        });
    }
}
