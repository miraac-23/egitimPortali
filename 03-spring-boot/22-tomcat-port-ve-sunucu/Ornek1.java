// Ornek1: Gömülü sunucu (Tomcat) yapılandırması — port, context-path, WebServerFactoryCustomizer.
// Çalıştırma: portal gömülü Tomcat ile başlatır (port 8080); self-test context-path ile çağırır.
package com.egitim.springboot.sunucu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // WebServerFactoryCustomizer: gömülü sunucuyu PROGRAMATİK yapılandır (port, context-path, sıkıştırma...).
    // (Burada uygulamayı /app altına alıyoruz; portu 8080'de bırakıyoruz — portal öyle başlatıyor.)
    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> sunucuOzellestir() {
        return factory -> {
            factory.setContextPath("/app");   // tüm uçlar artık /app altında
        };
    }

    @RestController
    static class BilgiController {
        @Value("${server.port:8080}") String port;
        @GetMapping("/bilgi")
        Map<String, Object> bilgi() {
            return Map.of("mesaj", "gömülü Tomcat çalışıyor", "port", port, "contextPath", "/app");
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            // Context-path /app olduğu için uçlar http://localhost:8080/app/... altında.
            RestClient c = RestClient.create("http://localhost:8080/app");
            System.out.println("\n========= GÖMÜLÜ SUNUCU SELF-TEST =========");
            System.out.println("GET /app/bilgi -> " + c.get().uri("/bilgi").retrieve().body(String.class));
            System.out.println("(Sunucu context-path '/app' altında; port 8080.)");
            System.out.println("===========================================");
        };
    }
}
