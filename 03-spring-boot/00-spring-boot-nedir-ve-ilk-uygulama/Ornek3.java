// Ornek3: İlk REST + JSON — nesne döndür, Spring Boot otomatik JSON'a çevirsin.
// @PathVariable, @RequestParam ve nesne->JSON (Jackson) dönüşümü.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.giris;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek3 {

    public static void main(String[] args) {
        SpringApplication.run(Ornek3.class, args);
    }

    // Domain nesnesi: dönüş değeri otomatik olarak JSON'a serileştirilir (Jackson).
    record Urun(int id, String ad, double fiyat) {}

    @RestController
    static class UrunController {
        private final List<Urun> urunler = List.of(
                new Urun(1, "Klavye", 450), new Urun(2, "Mouse", 250), new Urun(3, "Monitör", 3200));

        // GET /urunler -> tüm liste (JSON dizisi)
        @GetMapping("/urunler")
        List<Urun> hepsi() { return urunler; }

        // GET /urunler/{id} -> @PathVariable ile yol değişkeni
        @GetMapping("/urunler/{id}")
        Urun bul(@PathVariable int id) {
            return urunler.stream().filter(u -> u.id() == id).findFirst().orElse(null);
        }

        // GET /ara?kelime=... -> @RequestParam ile sorgu parametresi
        @GetMapping("/ara")
        List<Urun> ara(@RequestParam String kelime) {
            return urunler.stream().filter(u -> u.ad().toLowerCase().contains(kelime.toLowerCase())).toList();
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ REST + JSON ================");
            System.out.println("GET /urunler      -> " + c.get().uri("/urunler").retrieve().body(String.class));
            System.out.println("GET /urunler/2    -> " + c.get().uri("/urunler/2").retrieve().body(String.class));
            System.out.println("GET /ara?kelime=mo-> " + c.get().uri("/ara?kelime=mo").retrieve().body(String.class));
            System.out.println("=============================================");
            System.out.println("Dikkat: nesneleri elle JSON'a çevirmedik; Spring Boot (Jackson) otomatik yaptı.");
        };
    }
}
