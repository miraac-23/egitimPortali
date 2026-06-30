// Ornek1: CORS desteği — tarayıcının çapraz-köken (cross-origin) isteklerine izin.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.cors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // GLOBAL CORS yapılandırması: hangi köken/metot/başlıklara izin verilir.
    @Bean
    WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("https://uygulamam.com")  // izinli köken(ler)
                        .allowedMethods("GET", "POST")
                        .allowedHeaders("*");
            }
        };
    }

    @RestController
    @RequestMapping("/api")
    static class GlobalController {
        @GetMapping("/veri") String veri() { return "global cors verisi"; }
    }

    // Tek bir controller/metot için @CrossOrigin ile yerel CORS (global'den bağımsız).
    @RestController
    @RequestMapping("/acik")
    @CrossOrigin(origins = "*")   // bu controller herkese açık (dikkatli kullan!)
    static class AcikController {
        @GetMapping("/veri") String veri() { return "açık cors verisi"; }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= CORS SELF-TEST =========");

            // İzinli köken: yanıtta Access-Control-Allow-Origin görünür.
            var izinli = c.get().uri("/api/veri").header("Origin", "https://uygulamam.com")
                    .retrieve().toEntity(String.class);
            System.out.println("İzinli köken -> Allow-Origin: "
                    + izinli.getHeaders().getFirst("Access-Control-Allow-Origin"));

            // @CrossOrigin(*) controller: her köken için Allow-Origin döner.
            var acik = c.get().uri("/acik/veri").header("Origin", "https://baska-site.com")
                    .retrieve().toEntity(String.class);
            System.out.println("@CrossOrigin(*) -> Allow-Origin: "
                    + acik.getHeaders().getFirst("Access-Control-Allow-Origin"));
            System.out.println("==================================");
        };
    }
}
