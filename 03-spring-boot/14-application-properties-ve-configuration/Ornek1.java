// Ornek1: Yapılandırma — @ConfigurationProperties (tipli) ve @Value (tekil).
// Portal'da application.properties yok; değerleri SpringApplicationBuilder ile programatik veriyoruz.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(UygulamaAyarlari.class)
public class Ornek1 {

    public static void main(String[] args) {
        // application.properties YOKKEN değerleri programatik enjekte ediyoruz (gerçekte bu dosyadan gelir):
        new SpringApplicationBuilder(Ornek1.class)
                .properties(
                        "uygulama.ad=Eğitim Portalı",
                        "uygulama.surum=2.1",
                        "uygulama.ozellikler=arama,kod-calistirma,tema",
                        "uygulama.bakim=true",
                        "mesaj.karsilama=Hoş geldiniz")
                .run(args);
    }

    @Value("${mesaj.karsilama:merhaba}") // tekil değer + varsayılan ('karsilama' yoksa 'merhaba')
    private String karsilama;

    @Bean
    CommandLineRunner selfTest(UygulamaAyarlari ayar) {
        return args -> {
            System.out.println("\n========= YAPILANDIRMA SELF-TEST =========");
            System.out.println("@ConfigurationProperties (tipli bağlama):");
            System.out.println("  ad        : " + ayar.ad());
            System.out.println("  surum     : " + ayar.surum());
            System.out.println("  ozellikler: " + ayar.ozellikler() + "  (virgüllü string -> List<String>)");
            System.out.println("  bakim     : " + ayar.bakim() + "  (string -> boolean)");
            System.out.println("@Value (tekil): karsilama = " + karsilama);
            System.out.println("==========================================");
        };
    }
}

// Tipli yapılandırma: 'uygulama.*' anahtarları bu record'a OTOMATİK bağlanır (tip dönüşümüyle).
@ConfigurationProperties(prefix = "uygulama")
record UygulamaAyarlari(String ad, String surum, List<String> ozellikler, boolean bakim) {}
