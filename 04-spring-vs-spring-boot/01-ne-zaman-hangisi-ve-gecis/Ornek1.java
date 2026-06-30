// Ornek1: Boot kullanırken bile TAM kontrol sende — varsayılanı geçersiz kılmak.
// Boot otomatik bir bean sağlar; ama sen kendi bean'ini tanımlarsan Boot GERİ ÇEKİLİR
// (@ConditionalOnMissingBean). Yani Boot esnekliği elinden almaz.
// Çalıştırma: portal derleyip gömülü ortamla başlatır.
package com.egitim.karsilastirma.gecis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

public class Ornek1 {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Ornek1.class).web(WebApplicationType.NONE).run(args);
    }

    interface SelamlamaStratejisi { String selamla(String ad); }

    // KENDİ bean'imizi tanımlıyoruz. Eğer bir auto-configuration aynı tipte bir bean
    // sağlıyor olsaydı, @ConditionalOnMissingBean sayesinde GERİ ÇEKİLİR, bizimki kullanılırdı.
    @Bean
    SelamlamaStratejisi selamlama() {
        return ad -> "Merhaba " + ad + "! (ÖZEL strateji — Boot'un varsayılanı değil)";
    }

    @Bean
    CommandLineRunner selfTest(SelamlamaStratejisi strateji) {
        return args -> {
            System.out.println("\n================ KONTROL SENDE ================");
            System.out.println(strateji.selamla("Ada"));
            System.out.println("""

                    --- Önemli ilke ---
                    Spring Boot 'fikir sahibidir' (opinionated) ama 'dayatmacı' değildir:
                    - Bir bean tanımlamazsan Boot makul bir varsayılan sağlar.
                    - Tanımlarsan SENİNKİ kazanır (auto-config @ConditionalOnMissingBean ile geri çekilir).
                    - İstemediğin auto-config'i @SpringBootApplication(exclude=...) ile kapatabilirsin.
                    - Her ayarı application.yml / @Bean ile değiştirebilirsin.
                    Yani Boot'a geçmek, Spring'in kontrolünü KAYBETMEK değildir; sadece varsayılanlar hazır gelir.""");
        };
    }
}
