// Ornek2: @Value ile değer/property enjeksiyonu ve SpEL.
// Çalıştırma: java Ornek2.java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

public class Ornek2 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AyarConfig.class);
        ctx.getBean(AyarYazici.class).yaz();
        ctx.close();

        System.out.println("""

                --- @Value nereden beslenir? ---
                - application.properties / application.yml dosyalarından,
                - ortam değişkenleri ve JVM -D parametrelerinden,
                - varsayılan değerlerle: ${anahtar:varsayilan}
                SpEL ifadeleri için: @Value("#{...}")  (ör. matematik, başka bean'e erişim).""");
    }
}

@Configuration
class AyarConfig {
    // Bu örnekte property'leri programatik veriyoruz; gerçekte application.properties'ten gelir.
    @Bean
    static PropertySourcesPlaceholderConfigurer pspc() {
        var c = new PropertySourcesPlaceholderConfigurer();
        var p = new Properties();
        p.setProperty("uygulama.ad", "Eğitim Portalı");
        p.setProperty("uygulama.surum", "1.0");
        c.setProperties(p);
        return c;
    }

    @Bean AyarYazici ayarYazici() { return new AyarYazici(); }
}

@Component
class AyarYazici {
    // ${...}: property'den enjekte; :varsayilan ile yedek değer.
    @Value("${uygulama.ad}")
    private String ad;

    @Value("${uygulama.surum}")
    private String surum;

    @Value("${uygulama.dil:tr}") // tanımlı değil -> varsayılan "tr"
    private String dil;

    // #{...}: SpEL ifadesi (çalışma anında hesaplanır).
    @Value("#{ 8 * 1024 }")
    private int maxBoyutKb;

    // SpEL ile sistem özelliklerine de erişilebilir (çalışan JVM sürümü).
    @Value("#{ systemProperties['java.specification.version'] }")
    private String javaSurumu;

    void yaz() {
        System.out.println("uygulama.ad    = " + ad);
        System.out.println("uygulama.surum = " + surum);
        System.out.println("uygulama.dil   = " + dil + "  (varsayılan kullanıldı)");
        System.out.println("maxBoyutKb     = " + maxBoyutKb + "  (SpEL ile hesaplandı)");
        System.out.println("javaSurumu     = " + javaSurumu + "  (SpEL + systemProperties)");
    }
}
