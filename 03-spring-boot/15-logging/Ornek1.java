// Ornek1: Logging — SLF4J + Logback (Spring Boot'un yerleşik günlükleme altyapısı).
// Çalıştırma: portal gömülü Tomcat ile başlatır, log çıktıları görünür.
package com.egitim.springboot.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    // Her sınıf kendi logger'ını alır (sınıf adıyla). System.out.println YERİNE bunu kullan.
    private static final Logger log = LoggerFactory.getLogger(Ornek1.class);

    public static void main(String[] args) {
        // Log seviyesini programatik ayarla (gerçekte: logging.level.com.egitim=DEBUG application.properties'te).
        new SpringApplicationBuilder(Ornek1.class)
                .properties("logging.level.com.egitim.springboot.logging=DEBUG")
                .run(args);
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            System.out.println("\n========= LOGGING SELF-TEST =========");

            // Log SEVİYELERİ (en ayrıntılıdan en kritiğe): TRACE < DEBUG < INFO < WARN < ERROR
            log.trace("TRACE: çok ayrıntılı (varsayılan gizli)");
            log.debug("DEBUG: geliştirme ayrıntısı (seviye DEBUG'a çekildi -> görünür)");
            log.info("INFO: normal akış bilgisi — uygulama {} modunda başladı", "üretim");
            log.warn("WARN: dikkat — disk %{} dolu", 85);
            log.error("ERROR: hata — bağlantı kurulamadı: {}", "timeout");

            // PARAMETRELİ loglama ({}): string birleştirmeden DAHA İYİ (tembel, hızlı, güvenli).
            String kullanici = "ada"; int deneme = 3;
            log.info("Kullanıcı {} {}. kez giriş yaptı", kullanici, deneme);

            // İstisna loglama: mesaj + exception (stack trace otomatik basılır).
            try { throw new IllegalStateException("örnek hata"); }
            catch (Exception e) { log.error("İşlem başarısız oldu", e); }

            System.out.println("=====================================");
        };
    }
}
