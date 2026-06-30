// Ornek1: SPRING WEB MVC — DispatcherServlet akışı ve istek bağlama (request binding).
// Spring MVC, "Front Controller" desenini uygular: TÜM istekler önce tek bir DispatcherServlet'e
// gelir; o da isteği doğru @Controller metoduna (handler) yönlendirir. Bu örnek, bir isteğin
// nasıl bağlandığını (path değişkeni, query param, JSON gövde) ve yanıtın (ResponseEntity)
// nasıl üretildiğini canlı gösterir. Portal, gömülü Tomcat ile başlatıp self-test çıktısı alır.
// Çalıştırma: portal derler ve gömülü Tomcat ile başlatır.
package com.egitim.spring.webmvc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        SpringApplication.run(Ornek1.class, args);
    }

    // @RestController = @Controller + @ResponseBody: dönüş değeri doğrudan yanıt gövdesi olur
    // (JSON'a çevrilir). Klasik @Controller ise bir VIEW adı döndürür (JSP/Thymeleaf ile HTML).
    @RestController
    @RequestMapping("/urunler")
    static class UrunController {

        // (1) @PathVariable: yol parçasını parametreye bağlar.  GET /urunler/42
        @GetMapping("/{id}")
        public Map<String, Object> getir(@PathVariable Long id) {
            return Map.of("id", id, "ad", "Ürün-" + id, "kaynak", "@PathVariable");
        }

        // (2) @RequestParam: sorgu parametresini bağlar.  GET /urunler/ara?kelime=klavye&sayfa=2
        @GetMapping("/ara")
        public Map<String, Object> ara(@RequestParam String kelime,
                                       @RequestParam(defaultValue = "1") int sayfa) {
            return Map.of("kelime", kelime, "sayfa", sayfa, "kaynak", "@RequestParam");
        }

        // (3) @RequestBody: JSON gövdesini nesneye bağlar; ResponseEntity ile DURUM KODU + gövde.
        @PostMapping
        public ResponseEntity<Map<String, Object>> olustur(@RequestBody Map<String, Object> govde) {
            Map<String, Object> sonuc = Map.of("olusturuldu", govde, "kaynak", "@RequestBody");
            return ResponseEntity.status(HttpStatus.CREATED).body(sonuc); // 201 Created
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========== SPRING MVC — DispatcherServlet akışı ==========");
            System.out.println("Front Controller: tüm istekler tek DispatcherServlet'ten geçer,");
            System.out.println("o da uygun @Controller metoduna (handler) yönlendirir.\n");

            System.out.println("GET /urunler/42          -> " +
                    c.get().uri("/urunler/42").retrieve().body(String.class));
            System.out.println("GET /urunler/ara?kelime= -> " +
                    c.get().uri("/urunler/ara?kelime=klavye&sayfa=2").retrieve().body(String.class));

            var yanit = c.post().uri("/urunler").contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("ad", "Mouse")).retrieve().toEntity(String.class);
            System.out.println("POST /urunler            -> HTTP " + yanit.getStatusCode().value()
                    + " | " + yanit.getBody());
            System.out.println("==========================================================");
        };
    }
}
