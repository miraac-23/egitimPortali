// Ornek3: Sorgu parametreleriyle filtreleme + özel yanıt başlıkları (header).
// @RequestParam (zorunlu/opsiyonel/varsayılan) ve ResponseEntity ile header döndürme.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.rest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    record Urun(int id, String ad, String kategori, double fiyat) {}

    @RestController
    @RequestMapping("/api/urunler")
    static class UrunController {
        private final List<Urun> urunler = List.of(
                new Urun(1, "Klavye", "aksesuar", 450),
                new Urun(2, "Mouse", "aksesuar", 250),
                new Urun(3, "Monitör", "ekran", 3200),
                new Urun(4, "Laptop", "bilgisayar", 28000));

        // GET /api/urunler/ara?kategori=aksesuar&maxFiyat=400
        // kategori: opsiyonel; maxFiyat: varsayılan değerli; sirala: varsayılan "fiyat".
        @GetMapping("/ara")
        public ResponseEntity<List<Urun>> ara(
                @RequestParam(required = false) String kategori,
                @RequestParam(defaultValue = "1000000") double maxFiyat,
                @RequestParam(defaultValue = "fiyat") String sirala) {

            List<Urun> sonuc = urunler.stream()
                    .filter(u -> kategori == null || u.kategori().equals(kategori))
                    .filter(u -> u.fiyat() <= maxFiyat)
                    .sorted(sirala.equals("ad")
                            ? java.util.Comparator.comparing(Urun::ad)
                            : java.util.Comparator.comparingDouble(Urun::fiyat))
                    .toList();

            // Yanıta özel bir başlık (header) ekliyoruz: sonuç sayısı.
            return ResponseEntity.ok()
                    .header("X-Toplam-Adet", String.valueOf(sonuc.size()))
                    .body(sonuc);
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ FİLTRELEME + HEADER ================");

            var r1 = c.get().uri("/api/urunler/ara?kategori=aksesuar").retrieve().toEntity(String.class);
            System.out.println("kategori=aksesuar       -> X-Toplam-Adet=" + r1.getHeaders().getFirst("X-Toplam-Adet"));
            System.out.println("  " + r1.getBody());

            var r2 = c.get().uri("/api/urunler/ara?maxFiyat=500&sirala=ad").retrieve().toEntity(String.class);
            System.out.println("maxFiyat=500, sirala=ad -> X-Toplam-Adet=" + r2.getHeaders().getFirst("X-Toplam-Adet"));
            System.out.println("  " + r2.getBody());
            System.out.println("=====================================================");
            System.out.println("@RequestParam: required=false (opsiyonel), defaultValue (varsayılan).");
            System.out.println("ResponseEntity ile gövdeye ek olarak özel başlıklar da döndürülebilir.");
        };
    }
}
