// Ornek3: @ResponseStatus ile kısa yol ve ProblemDetail (RFC 7807) ile standart hata gövdesi.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.hata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    // YOL 1: @ResponseStatus — istisnaya doğrudan HTTP kodu iliştir (advice'a gerek yok).
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CONFLICT)
    static class StokYetersizException extends RuntimeException {
        StokYetersizException(String m) { super(m); }
    }

    // YOL 2 için iş istisnası (handler ProblemDetail döndürecek).
    static class SiparisBulunamadiException extends RuntimeException {
        SiparisBulunamadiException(String m) { super(m); }
    }

    @RestController
    static class SiparisController {
        @GetMapping("/api/siparis/{id}")
        public String bul(@PathVariable Long id) {
            if (id == 0) throw new StokYetersizException("Stok yetersiz");
            if (id > 100) throw new SiparisBulunamadiException("Sipariş bulunamadı: id=" + id);
            return "Sipariş #" + id;
        }
    }

    @RestControllerAdvice
    static class HataYonetimi {
        // ProblemDetail: hata gövdeleri için standart (RFC 7807) biçim. Spring 6+ ile gelir.
        @ExceptionHandler(SiparisBulunamadiException.class)
        public ProblemDetail bulunamadi(SiparisBulunamadiException ex) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
            pd.setTitle("Sipariş bulunamadı");
            pd.setProperty("kategori", "siparis");
            return pd;
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ @ResponseStatus + ProblemDetail ================");

            c.get().uri("/api/siparis/0").exchange((req, res) -> {
                System.out.println("GET /0 (@ResponseStatus) -> " + res.getStatusCode());
                return null;
            });

            c.get().uri("/api/siparis/999").exchange((req, res) -> {
                System.out.println("GET /999 (ProblemDetail) -> " + res.getStatusCode());
                System.out.println("  " + new String(res.getBody().readAllBytes()));
                return null;
            });
            System.out.println("=================================================================");
            System.out.println("@ResponseStatus: hızlı, basit. ProblemDetail: standart, makine-okunur, zengin hata gövdesi.");
        };
    }
}
