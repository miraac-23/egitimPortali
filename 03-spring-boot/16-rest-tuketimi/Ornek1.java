// Ornek1: REST tüketimi — başka bir servisin API'sini çağırmak (RestClient).
// Uygulama hem bir "uzak" API sunar hem de onu bir istemciyle tüketir (tek JVM'de gösterim).
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.resttuketim;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    record Doviz(String kod, double kur) {}

    // "Uzak" servis (gerçekte başka bir mikroservis/3. parti API olurdu).
    @RestController
    @RequestMapping("/uzak")
    static class UzakApi {
        @GetMapping("/doviz/{kod}") Doviz kur(@PathVariable String kod) {
            return new Doviz(kod.toUpperCase(), kod.equalsIgnoreCase("usd") ? 32.5 : 35.1);
        }
        @GetMapping("/doviz") List<Doviz> hepsi() { return List.of(new Doviz("USD", 32.5), new Doviz("EUR", 35.1)); }
        @PostMapping("/hesapla") Map<String,Object> hesapla(@RequestBody Map<String,Object> istek) {
            double miktar = ((Number) istek.get("miktar")).doubleValue();
            return Map.of("sonuc", miktar * 32.5, "birim", "TRY");
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            // RestClient (Spring 6.1+ önerilen senkron istemci). Bir kez oluştur, paylaş.
            RestClient istemci = RestClient.create("http://localhost:8080");
            System.out.println("\n========= REST TÜKETİMİ SELF-TEST =========");

            // GET + nesneye bağlama
            Doviz usd = istemci.get().uri("/uzak/doviz/usd").retrieve().body(Doviz.class);
            System.out.println("GET tek  -> " + usd);

            // GET liste (tip referansı)
            List<Doviz> hepsi = istemci.get().uri("/uzak/doviz")
                    .retrieve().body(new org.springframework.core.ParameterizedTypeReference<>() {});
            System.out.println("GET liste-> " + hepsi);

            // POST + gövde
            Map<String,Object> cevap = istemci.post().uri("/uzak/hesapla")
                    .body(Map.of("miktar", 100))
                    .retrieve().body(new org.springframework.core.ParameterizedTypeReference<>() {});
            System.out.println("POST     -> " + cevap);

            // HATA yönetimi: 4xx/5xx'i yakala
            try {
                istemci.get().uri("/uzak/yok").retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> {
                            throw new RuntimeException("uzak servis hatası: " + res.getStatusCode());
                        }).body(String.class);
            } catch (Exception e) {
                System.out.println("HATA     -> " + e.getMessage());
            }
            System.out.println("===========================================");
        };
    }
}
