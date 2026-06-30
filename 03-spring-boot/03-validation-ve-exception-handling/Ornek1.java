// Ornek1: @Valid ile istek gövdesini doğrulama — geçersiz veri otomatik 400 Bad Request olur.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.hata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // Gelen JSON'un uyması gereken kurallar DTO üzerinde tanımlı.
    static class KullaniciDto {
        @NotBlank(message = "ad zorunludur")
        public String ad;
        @Email(message = "geçerli bir e-posta girin")
        public String eposta;
        @Min(value = 18, message = "yaş en az 18 olmalı")
        public int yas;
    }

    @RestController
    static class KullaniciController {
        // @Valid: Spring, gövdeyi controller'a girmeden önce doğrular.
        // İhlal varsa MethodArgumentNotValidException -> Boot otomatik 400 Bad Request döner.
        @PostMapping("/api/kullanicilar")
        public String kaydet(@Valid @RequestBody KullaniciDto dto) {
            return "Kullanıcı kaydedildi: " + dto.ad;
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ @Valid DOĞRULAMA ================");

            // Geçerli istek -> 200
            String body = "{\"ad\":\"Ada\",\"eposta\":\"ada@site.com\",\"yas\":30}";
            var ok = c.post().uri("/api/kullanicilar").contentType(MediaType.APPLICATION_JSON)
                    .body(body).retrieve().toEntity(String.class);
            System.out.println("Geçerli  -> " + ok.getStatusCode() + " | " + ok.getBody());

            // Geçersiz istek -> 400 (status + gövdeyi exchange ile okuyoruz, RestClient 4xx'te fırlatır)
            String kotu = "{\"ad\":\"\",\"eposta\":\"gecersiz\",\"yas\":15}";
            c.post().uri("/api/kullanicilar").contentType(MediaType.APPLICATION_JSON).body(kotu)
                    .exchange((req, res) -> {
                        System.out.println("Geçersiz -> " + res.getStatusCode());
                        String govde = new String(res.getBody().readAllBytes());
                        System.out.println("  gövde (Boot varsayılan hata): "
                                + govde.substring(0, Math.min(140, govde.length())) + "...");
                        return null;
                    });
            System.out.println("==================================================");
            System.out.println("@Valid sayesinde geçersiz veri controller'a HİÇ ulaşmadı; Boot 400 döndü.");
            System.out.println("Ama varsayılan hata gövdesi kabadır -> Örnek 2'de @RestControllerAdvice ile düzelteceğiz.");
        };
    }
}
