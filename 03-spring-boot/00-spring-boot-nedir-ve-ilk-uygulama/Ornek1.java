// Ornek1: İlk Spring Boot uygulaması — birkaç satırla çalışan bir web sunucusu.
// @SpringBootApplication + bir @RestController; uygulama açılınca KENDİ endpoint'ini çağırır.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır, çıktıyı alıp durdurur.
package com.egitim.springboot.giris;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
// (Bu basit örnekte veritabanı gerekmediği için DataSource otomatik yapılandırmasını kapatıyoruz.)
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        // Tek satır: Spring Boot context'i kurar, gömülü Tomcat'i 8080'de başlatır.
        SpringApplication.run(Ornek1.class, args);
    }

    // @RestController: metotların dönüş değeri doğrudan HTTP yanıt gövdesi olur.
    @RestController
    static class SelamController {
        @GetMapping("/selam")
        String selam() {
            return "Merhaba Spring Boot! (gömülü Tomcat, sıfır XML yapılandırma)";
        }

        @GetMapping("/saat")
        String saat() {
            return "Sunucu çalışıyor.";
        }
    }

    // Uygulama ayağa kalkınca kendi endpoint'ini çağırıp sonucu yazdırır.
    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient client = RestClient.create("http://localhost:8080");
            System.out.println("\n================ SELF-TEST ================");
            System.out.println("GET /selam -> " + client.get().uri("/selam").retrieve().body(String.class));
            System.out.println("GET /saat  -> " + client.get().uri("/saat").retrieve().body(String.class));
            System.out.println("===========================================");
            System.out.println("Sunucu 8080'de açık. (curl http://localhost:8080/selam ile de denenebilir.)");
        };
    }
}
