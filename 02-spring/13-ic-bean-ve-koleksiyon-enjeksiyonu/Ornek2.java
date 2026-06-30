// Ornek2: KOLEKSİYON ENJEKSİYONU — List, Set, Map ve Properties enjekte etme.
// Bir bean'e tek bir değer değil, bir DEĞER LİSTESİ/HARİTASI vermek gerekebilir
// (izinli IP'ler, HTTP başlıkları, rol kümeleri, ayar çiftleri...). Spring bunların
// hepsini enjekte edebilir. (XML'de <list>/<set>/<map>/<props>; Java'da koleksiyon nesnesi.)
// Çalıştırma: java Ornek2.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

public class Ornek2 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            System.out.println(ctx.getBean(SunucuAyari.class).ozet());
            System.out.println("""

                    --- Hangi koleksiyon ne zaman? ---
                    * List  : sıralı, tekrar olabilir (örn. işleme sırası önemli filtreler)
                    * Set   : sırasız, tekrarsız (örn. roller, benzersiz etiketler)
                    * Map   : anahtar-değer (örn. HTTP başlıkları, kod->etiket eşlemesi)
                    * Properties : String->String ayar çiftleri (klasik konfigürasyon)
                    Spring değerleri uygun tipe dönüştürerek koleksiyona doldurur.""");
        }
    }
}

class SunucuAyari {
    private final List<String> izinliIpler;
    private final Set<String> roller;
    private final Map<String, Integer> limitler;
    private final Properties ayarlar;

    SunucuAyari(List<String> izinliIpler, Set<String> roller,
                Map<String, Integer> limitler, Properties ayarlar) {
        this.izinliIpler = izinliIpler;
        this.roller = roller;
        this.limitler = limitler;
        this.ayarlar = ayarlar;
    }

    String ozet() {
        return "İzinli IP'ler (List): " + izinliIpler
                + "\nRoller (Set)        : " + roller
                + "\nLimitler (Map)      : " + limitler
                + "\nAyarlar (Properties): " + ayarlar;
    }
}

@Configuration
class Config {
    @Bean
    SunucuAyari sunucuAyari() {
        Properties props = new Properties();
        props.setProperty("zaman.asimi", "30s");
        props.setProperty("gzip", "acik");
        return new SunucuAyari(
                List.of("10.0.0.1", "10.0.0.2", "10.0.0.1"),   // List: tekrar korunur
                Set.of("ADMIN", "USER"),                        // Set: tekrarsız
                Map.of("istek/sn", 100, "baglanti", 20),        // Map: anahtar->sayı
                props);                                         // Properties: String->String
    }
}
