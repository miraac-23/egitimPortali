// Ornek2: @RestControllerAdvice ile global hata yönetimi — tutarlı, anlamlı JSON hata gövdeleri.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.hata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    static class UrunDto {
        @NotBlank(message = "ad zorunludur")
        public String ad;
    }

    // Özel iş istisnası.
    static class KaynakBulunamadiException extends RuntimeException {
        KaynakBulunamadiException(String m) { super(m); }
    }

    @RestController
    @RequestMapping("/api/urunler")
    static class UrunController {
        @PostMapping
        public String olustur(@Valid @RequestBody UrunDto dto) { return "oluşturuldu: " + dto.ad; }

        @GetMapping("/{id}")
        public String bul(@PathVariable Long id) {
            if (id > 100) throw new KaynakBulunamadiException("Ürün bulunamadı: id=" + id);
            return "Ürün #" + id;
        }
    }

    // @RestControllerAdvice: TÜM controller'lardaki istisnalar burada tek yerde yakalanır.
    @RestControllerAdvice
    static class GlobalHataYonetimi {

        // Doğrulama hatası -> 400 + alan bazlı temiz mesajlar.
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> dogrulama(MethodArgumentNotValidException ex) {
            Map<String, String> alanlar = new HashMap<>();
            ex.getBindingResult().getFieldErrors()
                    .forEach(fe -> alanlar.put(fe.getField(), fe.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "durum", 400, "hata", "Doğrulama hatası", "alanlar", alanlar));
        }

        // Kaynak bulunamadı -> 404 + anlamlı gövde.
        @ExceptionHandler(KaynakBulunamadiException.class)
        public ResponseEntity<Map<String, Object>> bulunamadi(KaynakBulunamadiException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "durum", 404, "hata", "Bulunamadı", "mesaj", ex.getMessage()));
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ GLOBAL HATA YÖNETİMİ ================");

            c.post().uri("/api/urunler").contentType(MediaType.APPLICATION_JSON).body("{\"ad\":\"\"}")
                    .exchange((req, res) -> {
                        System.out.println("Geçersiz POST -> " + res.getStatusCode());
                        System.out.println("  " + new String(res.getBody().readAllBytes()));
                        return null;
                    });

            c.get().uri("/api/urunler/999").exchange((req, res) -> {
                System.out.println("GET /999      -> " + res.getStatusCode());
                System.out.println("  " + new String(res.getBody().readAllBytes()));
                return null;
            });
            System.out.println("=====================================================");
            System.out.println("Artık tüm hatalar TUTARLI, anlamlı JSON gövdeleriyle dönüyor (tek yerde yönetiliyor).");
        };
    }
}
