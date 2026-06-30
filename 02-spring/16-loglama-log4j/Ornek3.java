// Ornek3: PRATİK LOGLAMA — sınıf-başına logger, seviye koruması ve MDC (bağlamsal loglama).
// Gerçek uygulamada loglama yalnızca "yazdırmak" değildir: hangi isteğe ait olduğunu izlemek
// (MDC), pahalı log mesajlarını gereksiz kurmamak (isDebugEnabled) ve her sınıfa kendi
// kategorisini vermek önemlidir. Bir Spring context'i ile gerçekçi bir servis loglaması.
// Çalıştırma: java Ornek3.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek3 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            SiparisServisi servis = ctx.getBean(SiparisServisi.class);

            // MDC (Mapped Diagnostic Context): bu thread'deki TÜM loglara bağlam ekler.
            // Tipik kullanım: her HTTP isteğine bir 'istekId' koy; tüm loglar onunla etiketlensin.
            MDC.put("istekId", "REQ-7AF3");
            try {
                servis.isle("SP-2002", 1500);
                servis.isle("SP-2003", -5); // hatalı tutar -> WARN
            } finally {
                MDC.clear(); // bağlamı mutlaka temizle (thread havuzunda sızmasın)
            }

            System.out.println("""

                    --- Pratik loglama ipuçları ---
                    * Logger'ı sınıf başına al: LoggerFactory.getLogger(Sinif.class) -> kategori netleşir.
                    * Pahalı mesajları koru: if (log.isDebugEnabled()) ... -> gereksiz hesap yapma.
                    * MDC ile bağlam ekle (istekId, kullanıcı) -> dağıtık sistemde izlenebilirlik.
                    * Seviyeyi/biçimi KOD DEĞİL yapılandırma (logback.xml / log4j2.xml) belirler.""");
        }
    }
}

class SiparisServisi {
    private static final Logger log = LoggerFactory.getLogger(SiparisServisi.class);

    void isle(String no, int tutar) {
        // Seviye koruması: debug kapalıysa aşağıdaki string hiç kurulmaz.
        if (log.isDebugEnabled()) {
            log.debug("İşleme başlıyor (ayrıntı): no={}, tutar={}", no, tutar);
        }
        if (tutar < 0) {
            log.warn("Geçersiz tutar, sipariş atlandı: no={}, tutar={}", no, tutar);
            return;
        }
        log.info("Sipariş işlendi: no={}, tutar={}", no, tutar);
    }
}

@Configuration
class Config {
    @Bean SiparisServisi siparisServisi() { return new SiparisServisi(); }
}
