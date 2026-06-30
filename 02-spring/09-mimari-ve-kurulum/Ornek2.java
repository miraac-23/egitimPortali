// Ornek2: SPRING MİMARİSİ — katmanlı modüller ve Core Container'ın kalbi.
// Spring tek bir kütüphane değil; üst üste oturan modüllerden oluşur. Bu örnek,
// en altta duran "Core Container"ın (BeanFactory + ApplicationContext) somut
// yeteneklerini canlı canlı gösterir: Environment, kaynak yükleme, bean üretimi.
// Çalıştırma: java Ornek2.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

public class Ornek2 {

    public static void main(String[] args) throws Exception {
        System.out.println("Spring sürümü: " + SpringVersion.getVersion());

        System.out.println("""

                --- Spring mimarisi: katmanlar (alttan üste) ---
                1) Core Container : Beans, Core, Context, SpEL  -> IoC/DI'nin kalbi
                2) AOP            : kesişen ilgiler (loglama, güvenlik, transaction)
                3) Veri Erişimi   : JDBC, ORM, Transactions
                4) Web            : Spring MVC, WebFlux
                5) Test           : birim/entegrasyon test desteği
                Hepsi Core Container'ın üstüne oturur; bu örnek o çekirdeği gösterir.
                """);

        try (var ctx = new AnnotationConfigApplicationContext(AltyapiConfig.class)) {

            // ApplicationContext, BeanFactory'nin üstüne şu yetenekleri ekler:

            // (a) Environment — sistem/ortam değişkenleri, profiller, property çözümü
            Environment env = ctx.getEnvironment();
            System.out.println("Java sürümü (Environment'tan): " + env.getProperty("java.version"));
            System.out.println("Aktif profiller: " + java.util.Arrays.toString(env.getActiveProfiles())
                    + " (boşsa 'default')");

            // (b) Resource yükleme — dosya/classpath/URL kaynaklarını tek arayüzle okur
            Resource r = ctx.getResource("classpath:org/springframework/core/SpringVersion.class");
            System.out.println("Kaynak bulundu mu? " + r.exists() + " -> " + r.getDescription());

            // (c) Bean üretimi ve bağımlılık enjeksiyonu (Core Container'ın asıl işi)
            var rapor = ctx.getBean(RaporServisi.class);
            System.out.println("\n" + rapor.uret());
        }
    }
}

class ZamanKaynagi {
    String simdi() { return "2026-06-30T10:00 (sahte sabit saat)"; }
}

class RaporServisi {
    private final ZamanKaynagi zaman;
    RaporServisi(ZamanKaynagi zaman) { this.zaman = zaman; }
    String uret() { return "Rapor üretildi @ " + zaman.simdi(); }
}

@Configuration
class AltyapiConfig {
    @Bean ZamanKaynagi zamanKaynagi() { return new ZamanKaynagi(); }
    @Bean RaporServisi raporServisi(ZamanKaynagi z) { return new RaporServisi(z); }
}
