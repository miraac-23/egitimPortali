// Ornek2: @SpringBootApplication ne yapar + application properties / @Value.
// Otomatik yapılandırma (auto-configuration) ve dış yapılandırma değerlerinin enjeksiyonu.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.giris;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek2 {

    public static void main(String[] args) {
        // Properties normalde application.properties/yml'den gelir; burada gösterim için
        // SpringApplicationBuilder ile programatik veriyoruz.
        new SpringApplicationBuilder(Ornek2.class)
                .properties("uygulama.ad=Eğitim Portalı", "uygulama.surum=2.0")
                .run(args);
    }

    @RestController
    static class BilgiController {
        // @Value ile dış yapılandırma değerleri enjekte edilir (:varsayilan ile yedek).
        @Value("${uygulama.ad}")        String ad;
        @Value("${uygulama.surum}")     String surum;
        @Value("${uygulama.dil:tr}")    String dil; // tanımlı değil -> varsayılan

        @GetMapping("/bilgi")
        String bilgi() {
            return "ad=" + ad + ", sürüm=" + surum + ", dil=" + dil;
        }
    }

    @Bean
    CommandLineRunner selfTest(ApplicationContext ctx) {
        return args -> {
            System.out.println("\n================ AUTO-CONFIG + PROPERTIES ================");
            System.out.println("@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan");
            // Auto-configuration sayesinde otomatik oluşturulan bean sayısı (Tomcat, Jackson, ...).
            System.out.println("Container'daki bean sayısı: " + ctx.getBeanDefinitionCount()
                    + " (çoğu otomatik yapılandırıldı)");

            RestClient client = RestClient.create("http://localhost:8080");
            System.out.println("GET /bilgi -> " + client.get().uri("/bilgi").retrieve().body(String.class));
            System.out.println("==========================================================");
        };
    }
}
