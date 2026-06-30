// Ornek3: Spring entegrasyonu — @Validated ile metot parametrelerinin OTOMATİK doğrulanması.
// @Validated proxy tabanlıdır; bu yüzden portal bu dosyayı derleyip çalıştırır.
package com.egitim.spring.validation;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(Config.class);
        SiparisServisi servis = ctx.getBean(SiparisServisi.class);

        // Geçerli çağrı: sorunsuz.
        servis.siparisVer("Klavye", 2);

        // Geçersiz çağrılar: Spring, metoda GİRMEDEN parametreleri doğrular ve hata fırlatır.
        denemeHatali(() -> servis.siparisVer("", 2), "boş ürün adı");
        denemeHatali(() -> servis.siparisVer("Mouse", 0), "adet < 1");

        ctx.close();
        System.out.println("""

                --- @Validated metot doğrulaması ---
                @Validated + MethodValidationPostProcessor ile metot parametrelerindeki kısıtlar
                (@NotBlank, @Min...) çağrı anında OTOMATİK kontrol edilir; ihlalde ConstraintViolationException.
                Spring MVC'de @Valid + @RequestBody ile gelen DTO'lar da aynı şekilde doğrulanır
                ve hata 400 Bad Request'e çevrilir (Spring Boot bölümünde).""");
    }

    static void denemeHatali(Runnable r, String aciklama) {
        try {
            r.run();
            System.out.println("  (beklenmedik: hata fırlamadı) " + aciklama);
        } catch (ConstraintViolationException e) {
            System.out.println("  Reddedildi (" + aciklama + "): " + e.getConstraintViolations().iterator().next().getMessage());
        }
    }

    @Configuration
    public static class Config {
        // Bu post-processor, @Validated bean'lerin metotlarını doğrulayan proxy'yi kurar.
        @Bean static MethodValidationPostProcessor mvpp() { return new MethodValidationPostProcessor(); }
        @Bean SiparisServisi siparisServisi() { return new SiparisServisi(); }
    }

    // @Validated: bu bean'in metot parametreleri doğrulamaya tabi olsun.
    @Validated
    public static class SiparisServisi {
        public void siparisVer(@NotBlank(message = "ürün adı zorunlu") String urun,
                               @Min(value = 1, message = "adet en az 1 olmalı") int adet) {
            System.out.println("Sipariş alındı: " + adet + " adet " + urun);
        }
    }
}
