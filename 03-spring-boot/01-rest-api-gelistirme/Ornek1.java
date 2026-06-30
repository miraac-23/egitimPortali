// Ornek1: Tam CRUD REST API — GET, POST, PUT, DELETE (bellek-içi depo).
// @RequestBody ile JSON gövdesi nesneye, @PathVariable ile yol değişkeni parametreye bağlanır.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.rest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        SpringApplication.run(Ornek1.class, args);
    }

    // Mutable görev (JSON'dan bağlanabilmesi için get/set'li basit bir sınıf).
    static class Gorev {
        public Long id;
        public String baslik;
        public boolean tamamlandi;
        public Gorev() {}
        public Gorev(String baslik) { this.baslik = baslik; }
    }

    @RestController
    @RequestMapping("/api/gorevler")
    static class GorevController {
        private final Map<Long, Gorev> depo = new ConcurrentHashMap<>();
        private final AtomicLong sayac = new AtomicLong();

        @GetMapping                                   // GET /api/gorevler
        public java.util.Collection<Gorev> hepsi() { return depo.values(); }

        @GetMapping("/{id}")                          // GET /api/gorevler/{id}
        public Gorev bul(@PathVariable Long id) { return depo.get(id); }

        @PostMapping                                  // POST /api/gorevler (yeni kayıt)
        public Gorev olustur(@RequestBody Gorev g) {
            g.id = sayac.incrementAndGet();
            depo.put(g.id, g);
            return g;
        }

        @PutMapping("/{id}")                          // PUT /api/gorevler/{id} (güncelle)
        public Gorev guncelle(@PathVariable Long id, @RequestBody Gorev g) {
            g.id = id;
            depo.put(id, g);
            return g;
        }

        @DeleteMapping("/{id}")                       // DELETE /api/gorevler/{id}
        public void sil(@PathVariable Long id) { depo.remove(id); }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ CRUD SELF-TEST ================");

            // CREATE (POST)
            Gorev olusan = c.post().uri("/api/gorevler").contentType(MediaType.APPLICATION_JSON)
                    .body(new Gorev("Spring Boot öğren")).retrieve().body(Gorev.class);
            System.out.println("POST   -> oluşturuldu id=" + olusan.id + ", baslik=" + olusan.baslik);

            c.post().uri("/api/gorevler").contentType(MediaType.APPLICATION_JSON)
                    .body(new Gorev("REST API yaz")).retrieve().body(Gorev.class);

            // READ (GET all)
            System.out.println("GET    -> " + c.get().uri("/api/gorevler").retrieve().body(String.class));

            // UPDATE (PUT)
            Gorev guncel = new Gorev("Spring Boot öğren"); guncel.tamamlandi = true;
            c.put().uri("/api/gorevler/" + olusan.id).contentType(MediaType.APPLICATION_JSON)
                    .body(guncel).retrieve().body(Gorev.class);
            System.out.println("PUT    -> id=" + olusan.id + " güncellendi (tamamlandi=true)");
            System.out.println("GET/{id}-> " + c.get().uri("/api/gorevler/" + olusan.id).retrieve().body(String.class));

            // DELETE
            c.delete().uri("/api/gorevler/" + olusan.id).retrieve().toBodilessEntity();
            System.out.println("DELETE -> id=" + olusan.id + " silindi");
            System.out.println("GET    -> " + c.get().uri("/api/gorevler").retrieve().body(String.class));
            System.out.println("================================================");
        };
    }
}
