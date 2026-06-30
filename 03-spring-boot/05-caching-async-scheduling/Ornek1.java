// Ornek1: @Cacheable — pahalı bir işlemin sonucunu önbelleğe alıp tekrar tekrar hesaplamamak.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır.
package com.egitim.springboot.cache;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableCaching // önbellekleme altyapısını açar (Boot basit bir bellek-içi cache yapılandırır)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    @Service
    public static class UrunServisi {
        // @Cacheable: aynı id ile ilk çağrıda metot çalışır ve sonuç "urunler" cache'ine yazılır;
        // sonraki çağrılarda metot HİÇ çalışmaz, sonuç doğrudan cache'ten döner.
        @Cacheable("urunler")
        public String detayGetir(Long id) {
            yavasIslem(); // pahalı işlemi (DB/ağ) taklit eder
            return "Ürün-" + id + " detayları";
        }

        private void yavasIslem() {
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        }
    }

    @org.springframework.context.annotation.Bean
    CommandLineRunner selfTest(UrunServisi servis) {
        return args -> {
            System.out.println("\n================ @Cacheable ================");
            System.out.println("1. çağrı (id=1): " + olcveCagir(servis, 1L));
            System.out.println("2. çağrı (id=1): " + olcveCagir(servis, 1L) + "  <- cache'ten, anında!");
            System.out.println("3. çağrı (id=2): " + olcveCagir(servis, 2L) + "  <- yeni id, tekrar hesaplandı");
            System.out.println("4. çağrı (id=1): " + olcveCagir(servis, 1L) + "  <- yine cache'ten");
            System.out.println("============================================");
            System.out.println("@Cacheable, pahalı (DB/ağ/hesap) işlemleri tekrarlamamayı sağlar. Redis ile dağıtık da olabilir.");
        };
    }

    static String olcveCagir(UrunServisi s, Long id) {
        long t0 = System.currentTimeMillis();
        String sonuc = s.detayGetir(id);
        return "(" + (System.currentTimeMillis() - t0) + " ms) " + sonuc;
    }
}
