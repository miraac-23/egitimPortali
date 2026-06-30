// Ornek2: LOG4J 2 API — "Logging with Log4J" konusunun doğrudan karşılığı.
// Log4J, Java'nın en eski ve etkili loglama kütüphanesidir. Bugün kullanılan sürüm Log4j 2'dir
// (Log4j 1.x kullanım dışıdır). Burada Log4j 2'nin KENDİ API'siyle (org.apache.logging.log4j)
// log yazıyoruz. NOT: bu portal ortamında 'log4j-to-slf4j' köprüsü var; yani Log4j 2 çağrıları
// SLF4J üzerinden Logback'e yönlendirilir — çıktıyı yine Logback üretir. Üretimde Log4j2'yi
// asıl motor olarak da seçebilirsin (log4j-core ile).
// Çalıştırma: java Ornek2.java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.SpringVersion;

public class Ornek2 {

    // Log4j 2'nin kendi Logger'ı (SLF4J'inkiyle aynı kavram, farklı paket).
    private static final Logger log = LogManager.getLogger(Ornek2.class);

    public static void main(String[] args) {
        System.out.println("Log4j 2 API ile loglama (Spring " + SpringVersion.getVersion() + ")\n");

        // Log4j 2 de aynı seviyeleri ve parametreli ('{}') loglamayı destekler.
        log.info("Sipariş işleniyor: no={}", "SP-1001");
        log.warn("Stok azalıyor: ürün={}, kalan={}", "Klavye", 4);
        log.error("Ödeme reddedildi: no={}", "SP-1001");

        try {
            throw new IllegalStateException("kart limiti yetersiz");
        } catch (IllegalStateException e) {
            log.error("Ödeme hatası: no={}", "SP-1001", e); // mesaj + stack trace
        }

        System.out.println("""

                --- Log4J ailesi ve modern manzara ---
                * Log4j 1.x : tarihsel; ARTIK KULLANILMAMALI (güvenlik/idame yok).
                * Log4j 2   : modern, yüksek başarımlı (asenkron logger, lazy '{}'), aktif geliştirilir.
                * Logback   : Log4j'in yaratıcısının yeni nesil motoru; Spring Boot'un VARSAYILANI.
                * SLF4J     : bunların hepsinin önündeki CEPHE (Ornek1). Kodun cepheye yazsın,
                              motoru bağımlılıkla seç. Bu ortamda Log4j2 -> SLF4J -> Logback akıyor.""");
    }
}
