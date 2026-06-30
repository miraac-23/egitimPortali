// Ornek2: ResponseEntity ve HTTP durum kodları — doğru REST semantiği.
// 201 Created (+ Location), 200 OK, 404 Not Found, 204 No Content.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.rest;

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

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    static class Urun {
        public Long id;
        public String ad;
        public Urun() {}
        public Urun(String ad) { this.ad = ad; }
    }

    @RestController
    @RequestMapping("/api/urunler")
    static class UrunController {
        private final Map<Long, Urun> depo = new ConcurrentHashMap<>();
        private final AtomicLong sayac = new AtomicLong();

        // 201 CREATED + Location başlığı (yeni kaynağın adresi).
        @PostMapping
        public ResponseEntity<Urun> olustur(@RequestBody Urun u) {
            u.id = sayac.incrementAndGet();
            depo.put(u.id, u);
            return ResponseEntity.created(URI.create("/api/urunler/" + u.id)).body(u);
        }

        // 200 OK (bulundu) veya 404 NOT FOUND (yok).
        @GetMapping("/{id}")
        public ResponseEntity<Urun> bul(@PathVariable Long id) {
            Urun u = depo.get(id);
            return (u != null) ? ResponseEntity.ok(u) : ResponseEntity.notFound().build();
        }

        // 204 NO CONTENT (silindi, gövde yok).
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> sil(@PathVariable Long id) {
            depo.remove(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ DURUM KODLARI ================");

            // POST -> 201 + Location
            var created = c.post().uri("/api/urunler").contentType(MediaType.APPLICATION_JSON)
                    .body(new Urun("Klavye")).retrieve().toEntity(Urun.class);
            System.out.println("POST            -> " + created.getStatusCode()
                    + " | Location: " + created.getHeaders().getLocation());

            // GET mevcut -> 200
            var bulundu = c.get().uri("/api/urunler/1").retrieve().toEntity(Urun.class);
            System.out.println("GET /1 (var)    -> " + bulundu.getStatusCode());

            // GET olmayan -> 404 (RestClient 4xx'te exception fırlatır; durumu yakalıyoruz)
            HttpStatus durum404 = c.get().uri("/api/urunler/999")
                    .exchange((req, res) -> HttpStatus.valueOf(res.getStatusCode().value()));
            System.out.println("GET /999 (yok)  -> " + durum404);

            // DELETE -> 204
            var silindi = c.delete().uri("/api/urunler/1").retrieve().toBodilessEntity();
            System.out.println("DELETE /1       -> " + silindi.getStatusCode());
            System.out.println("===============================================");
            System.out.println("Doğru durum kodları, REST API'nin sözleşmesidir: istemci ne olduğunu koddan anlar.");
        };
    }
}
