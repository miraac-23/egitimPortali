// Ornek3: OTOMATİK KOLEKSİYON — bir tipteki TÜM bean'leri tek listede/haritada toplama.
// Spring'in en zarif yeteneklerinden biri: bir arayüzü uygulayan TÜM bean'leri
// List<Tip> ya da Map<String,Tip> olarak otomatik enjekte eder. Bu, "strateji deseni"ni
// (strategy pattern) container düzeyinde uygular: yeni bir strateji eklemek = yeni bir bean.
// Çalıştırma: java Ornek3.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

public class Ornek3 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            KayitServisi servis = ctx.getBean(KayitServisi.class);
            servis.kaydet("  Ankara  ");
            servis.kaydet("");
            System.out.println("""

                    --- Otomatik koleksiyonun gücü ---
                    KayitServisi, Dogrulayici'ları tek tek BİLMEZ; Spring tüm Dogrulayici
                    bean'lerini List olarak enjekte etti. Yeni bir kural eklemek için tek
                    yapman gereken yeni bir @Bean Dogrulayici tanımlamak — servis kodu DEĞİŞMEZ.
                    Map<String,Dogrulayici> ise bean ADINI anahtar yaparak isimli erişim verir.""");
        }
    }
}

interface Dogrulayici {
    void dogrula(String girdi);
}

class BosDegilDogrulayici implements Dogrulayici {
    public void dogrula(String g) {
        if (g == null || g.isBlank()) throw new IllegalArgumentException("boş olamaz");
    }
}

class UzunlukDogrulayici implements Dogrulayici {
    public void dogrula(String g) {
        if (g != null && g.strip().length() < 2) throw new IllegalArgumentException("çok kısa");
    }
}

class KayitServisi {
    private final List<Dogrulayici> hepsi;       // TÜM Dogrulayici bean'leri
    private final Map<String, Dogrulayici> isimli; // bean adı -> Dogrulayici

    KayitServisi(List<Dogrulayici> hepsi, Map<String, Dogrulayici> isimli) {
        this.hepsi = hepsi;
        this.isimli = isimli;
        System.out.println("Enjekte edilen doğrulayıcı sayısı: " + hepsi.size()
                + " | isimler: " + isimli.keySet());
    }

    void kaydet(String veri) {
        try {
            for (Dogrulayici d : hepsi) d.dogrula(veri);
            System.out.println("KAYIT OK -> '" + veri.strip() + "'");
        } catch (IllegalArgumentException e) {
            System.out.println("REDDEDİLDİ ('" + veri + "') -> " + e.getMessage());
        }
    }
}

@Configuration
class Config {
    @Bean Dogrulayici bosDegilDogrulayici() { return new BosDegilDogrulayici(); }
    @Bean Dogrulayici uzunlukDogrulayici() { return new UzunlukDogrulayici(); }

    // Spring, yukarıdaki TÜM Dogrulayici bean'lerini List ve Map olarak enjekte eder.
    @Bean KayitServisi kayitServisi(List<Dogrulayici> hepsi, Map<String, Dogrulayici> isimli) {
        return new KayitServisi(hepsi, isimli);
    }
}
