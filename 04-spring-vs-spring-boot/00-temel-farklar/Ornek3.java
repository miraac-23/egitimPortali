// Ornek3: Boot'un "sihrini" sayılarla görmek — otomatik yapılandırmanın ölçeği.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.karsilastirma;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    // Yazdığımız TEK kod: küçük bir controller.
    @RestController
    static class SelamController {
        @GetMapping("/selam") String selam() { return "merhaba"; }
    }

    @Bean
    CommandLineRunner selfTest(ApplicationContext ctx) {
        return args -> {
            System.out.println("\n================ BOOT'UN SİHRİ (sayılarla) ================");
            // Bizim yazdığımız: 1 controller + 1 runner. Geri kalan her şey otomatik.
            System.out.println("Container'daki TOPLAM bean sayısı: " + ctx.getBeanDefinitionCount());
            System.out.println("(Tomcat, DispatcherServlet, Jackson, DataSource, JdbcTemplate, JPA,");
            System.out.println(" hata yönetimi, metrikler... yüzlerce bean OTOMATİK yapılandırıldı.)");

            // Üstelik çalışan bir web sunucumuz var — hiç sunucu kurmadan:
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\nGET /selam -> " + c.get().uri("/selam").retrieve().body(String.class));

            System.out.println("""
                    ==========================================================
                    Düz Spring'de bunların HEPSİNİ elle yapılandırman gerekirdi:
                      sunucu, servlet, JSON dönüştürücü, veri kaynağı, sürüm uyumu...
                    Spring Boot, classpath'e bakıp makul varsayılanlarla bunları otomatik kurar.
                    Sen iş mantığına odaklanırsın; altyapı Boot'un işidir.""");
        };
    }
}
