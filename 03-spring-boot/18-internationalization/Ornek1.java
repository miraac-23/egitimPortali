// Ornek1: Uluslararasılaştırma (i18n) — MessageSource ile dile göre mesaj.
// İstek dilini Accept-Language başlığı belirler (varsayılan AcceptHeaderLocaleResolver).
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.i18n;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // Mesaj kaynağı: anahtar + dile göre metin. Gerçekte messages_tr.properties / messages_en.properties dosyaları.
    @Bean
    MessageSource messageSource() {
        StaticMessageSource ms = new StaticMessageSource();
        ms.addMessage("selam", Locale.of("tr"), "Merhaba, {0}!");
        ms.addMessage("selam", Locale.ENGLISH, "Hello, {0}!");
        ms.addMessage("sepet", Locale.of("tr"), "Sepetinizde {0} ürün var.");
        ms.addMessage("sepet", Locale.ENGLISH, "You have {0} items in your cart.");
        ms.setUseCodeAsDefaultMessage(true); // anahtar bulunamazsa anahtarı döndür
        return ms;
    }

    @RestController
    static class SelamController {
        private final MessageSource mesajlar;
        SelamController(MessageSource mesajlar) { this.mesajlar = mesajlar; }

        // 'Locale locale' parametresi, isteğin Accept-Language başlığından OTOMATİK gelir.
        @GetMapping("/selam")
        String selam(@RequestParam String ad, Locale locale) {
            return mesajlar.getMessage("selam", new Object[]{ad}, locale);
        }
        @GetMapping("/sepet")
        String sepet(@RequestParam int adet, Locale locale) {
            return mesajlar.getMessage("sepet", new Object[]{adet}, locale);
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= i18n SELF-TEST =========");
            // Accept-Language başlığına göre AYNI uç farklı dilde yanıt verir.
            System.out.println("Accept-Language: tr -> "
                    + c.get().uri("/selam?ad=Ada").header("Accept-Language", "tr").retrieve().body(String.class));
            System.out.println("Accept-Language: en -> "
                    + c.get().uri("/selam?ad=Ada").header("Accept-Language", "en").retrieve().body(String.class));
            System.out.println("sepet (tr) -> "
                    + c.get().uri("/sepet?adet=3").header("Accept-Language", "tr").retrieve().body(String.class));
            System.out.println("sepet (en) -> "
                    + c.get().uri("/sepet?adet=3").header("Accept-Language", "en").retrieve().body(String.class));
            System.out.println("==================================");
        };
    }
}
