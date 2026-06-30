// Ornek1: Servis bileşenleri ve katmanlı mimari — Controller -> Service -> Repository.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    record Urun(Long id, String ad, double fiyat) {}

    // 1) REPOSITORY katmanı: veri erişimi (burada bellek-içi; gerçekte JPA/JDBC).
    @Repository
    static class UrunRepository {
        private final Map<Long, Urun> depo = new LinkedHashMap<>();
        private final AtomicLong sayac = new AtomicLong();
        Urun kaydet(String ad, double fiyat) {
            long id = sayac.incrementAndGet();
            Urun u = new Urun(id, ad, fiyat);
            depo.put(id, u);
            return u;
        }
        List<Urun> hepsi() { return new ArrayList<>(depo.values()); }
    }

    // 2) SERVICE katmanı: İŞ MANTIĞI burada (kurallar, hesaplama). Controller'dan ayrı.
    @Service
    static class UrunServisi {
        private final UrunRepository repo; // bağımlılık enjekte edilir (constructor injection)
        UrunServisi(UrunRepository repo) { this.repo = repo; }

        Urun ekle(String ad, double fiyat) {
            if (fiyat < 0) throw new IllegalArgumentException("fiyat negatif olamaz"); // iş kuralı
            return repo.kaydet(ad, fiyat);
        }
        double toplamStokDegeri() { return repo.hepsi().stream().mapToDouble(Urun::fiyat).sum(); }
        List<Urun> listele() { return repo.hepsi(); }
    }

    // 3) CONTROLLER katmanı: yalnızca HTTP'yi servise bağlar (iş mantığı YOK).
    @RestController
    @RequestMapping("/urunler")
    static class UrunController {
        private final UrunServisi servis;
        UrunController(UrunServisi servis) { this.servis = servis; }

        @PostMapping Urun ekle(@RequestParam String ad, @RequestParam double fiyat) { return servis.ekle(ad, fiyat); }
        @GetMapping List<Urun> hepsi() { return servis.listele(); }
        @GetMapping("/toplam") double toplam() { return servis.toplamStokDegeri(); }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= KATMANLI MİMARİ SELF-TEST =========");
            c.post().uri("/urunler?ad=Klavye&fiyat=450").retrieve().toBodilessEntity();
            c.post().uri("/urunler?ad=Mouse&fiyat=250").retrieve().toBodilessEntity();
            System.out.println("POST x2 -> Controller -> Service (iş kuralı) -> Repository (kayıt)");
            System.out.println("GET /urunler      -> " + c.get().uri("/urunler").retrieve().body(String.class));
            System.out.println("GET /urunler/toplam-> " + c.get().uri("/urunler/toplam").retrieve().body(String.class) + " TL");
            System.out.println("=============================================");
        };
    }
}
