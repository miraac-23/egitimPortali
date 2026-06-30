// Ornek1: SLF4J CEPHESİ (facade) — modern Java/Spring loglamasının standardı.
// SLF4J bir "cephe"dir: sen SLF4J API'sine yazarsın, ARKADA hangi motorun (Logback, Log4j2)
// çalıştığını bilmen gerekmez. Spring'in kendisi de iç loglarını bu cepheye yazar.
// Bu ortamda SLF4J -> Logback'e bağlıdır (çıktıyı Logback üretir).
// Çalıştırma: java Ornek1.java   (Spring classpath portal tarafından sağlanır)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.SpringVersion;

public class Ornek1 {

    // Konvansiyon: her sınıf, KENDİ adına bir Logger alır (kategori = sınıf adı).
    private static final Logger log = LoggerFactory.getLogger(Ornek1.class);

    public static void main(String[] args) {
        System.out.println("Spring sürümü: " + SpringVersion.getVersion()
                + " (Spring de iç loglarını SLF4J'e yazar)\n");

        // 1) Log SEVİYELERİ (en az önemliden en kritiğe): TRACE < DEBUG < INFO < WARN < ERROR.
        //    Yapılandırılan eşik INFO ise, TRACE/DEBUG bastırılır (bu ortamda varsayılan eşik INFO).
        log.trace("TRACE — en ayrıntılı, genelde kapalı");
        log.debug("DEBUG — geliştirme ayrıntısı, genelde kapalı");
        log.info("INFO  — normal akış olayı (görünür)");
        log.warn("WARN  — dikkat: anormal ama kurtarılabilir durum (görünür)");
        log.error("ERROR — hata: işlem başarısız (görünür)");

        // 2) PARAMETRELİ loglama: '+' ile string birleştirme YERİNE {} kullan.
        //    Avantaj: seviye kapalıysa string hiç kurulmaz (performans) + okunur.
        String kullanici = "ayse";
        int deneme = 3;
        log.info("Giriş denemesi: kullanıcı={}, deneme={}", kullanici, deneme);

        // 3) İSTİSNA loglama: son argüman Throwable ise yığın izi (stack trace) basılır.
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            log.error("Sayı ayrıştırılamadı: '{}'", "abc", e); // hem mesaj hem stack trace
        }

        System.out.println("""

                --- Neden SLF4J? ---
                * Cephe deseni: kod SLF4J'e bağımlı; loglama motorunu (Logback/Log4j2) sonradan
                  TEK BİR bağımlılık değiştirerek seçebilirsin — uygulama koduna dokunmadan.
                * Parametreli loglama ({}) hem hızlı hem güvenli.
                * Spring, Hibernate ve çoğu kütüphane zaten SLF4J'e yazar -> tek tip log akışı.""");
    }
}
