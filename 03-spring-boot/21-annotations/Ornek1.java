// Ornek1: Spring Boot anotasyonları — bileşen tarama, DI, @Primary/@Qualifier, @Bean, @Value.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.annotations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Ornek1.class).properties("uygulama.surum=1.0").run(args);
    }

    interface BildirimGonderici { String gonder(String mesaj); }

    // @Service + @Primary: aynı tipten birden çok bean varsa VARSAYILAN bu seçilir.
    @Service @Primary
    static class EpostaGonderici implements BildirimGonderici {
        public String gonder(String m) { return "[E-POSTA] " + m; }
    }

    // @Service + isim: @Qualifier ile özellikle bu istenebilir.
    @Service("sms")
    static class SmsGonderici implements BildirimGonderici {
        public String gonder(String m) { return "[SMS] " + m; }
    }

    // @Component: genel bir bean. Bağımlılıkları CONSTRUCTOR ile enjekte edilir (önerilen).
    @Component
    static class BildirimServisi {
        private final BildirimGonderici varsayilan;   // @Primary olan (Eposta) gelir
        private final BildirimGonderici sms;           // @Qualifier ile SMS gelir
        @Value("${uygulama.surum}") String surum;      // yapılandırmadan değer

        BildirimServisi(BildirimGonderici varsayilan, @Qualifier("sms") BildirimGonderici sms) {
            this.varsayilan = varsayilan;
            this.sms = sms;
        }
        String duyur(String m) { return varsayilan.gonder(m) + " / " + sms.gonder(m) + " (v" + surum + ")"; }
    }

    // @Bean: bir nesneyi (kendi sınıfın olmasa bile) bean olarak kaydet (fabrika metodu).
    @Bean
    java.time.Clock saat() { return java.time.Clock.systemDefaultZone(); }

    @Bean
    CommandLineRunner selfTest(BildirimServisi servis, java.time.Clock saat) {
        return args -> {
            System.out.println("\n========= ANNOTATIONS SELF-TEST =========");
            System.out.println("Enjekte edilen servis: " + servis.duyur("Sipariş hazır"));
            System.out.println("  -> @Primary 'Eposta'yı, @Qualifier(\"sms\") 'Sms'i seçti.");
            System.out.println("@Bean ile kaydedilen Clock var mı? " + (saat != null));
            System.out.println("=========================================");
        };
    }
}
