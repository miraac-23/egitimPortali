// Ornek3: Profiller (@Profile) — ortama göre farklı bean'ler.
// Gerçek senaryo: dev ortamında sahte/bellek-içi servis, prod ortamında gerçek servis.
// Çalıştırma: portal Spring classpath'iyle çalıştırır.
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

public class Ornek3 {

    public static void main(String[] args) {
        // Hangi ortamdayız? Aktif profili genelde application.properties / ortam değişkeni belirler.
        System.out.println("=== Profil: dev ===");
        calistir("dev");

        System.out.println("\n=== Profil: prod ===");
        calistir("prod");

        System.out.println("""

                --- Profiller ne işe yarar? ---
                @Profile("dev"/"prod"/"test") ile bean'ler ortama göre seçilir.
                Aktif profil: spring.profiles.active=prod (properties) veya ortam değişkeni ile belirlenir.
                Tipik kullanım: dev'de sahte/bellek-içi bağımlılık, prod'da gerçek servis;
                test profilinde ise test'e özel yapılandırma.""");
    }

    static void calistir(String profil) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profil); // aktif profili seç (refresh'ten ÖNCE)
        ctx.register(SahteOdeme.class, GercekOdeme.class, OdemeKullanici.class);
        ctx.refresh();

        // Aktif profile göre Spring DOĞRU OdemeServisi bean'ini enjekte eder.
        ctx.getBean(OdemeKullanici.class).ode(199.90);
        ctx.close();
    }
}

interface OdemeServisi { void ode(double tutar); }

@Component
@Profile("dev") // yalnızca "dev" profili aktifken bu bean oluşturulur
class SahteOdeme implements OdemeServisi {
    public void ode(double tutar) { System.out.println("  [SAHTE] " + tutar + " TL ödendi (gerçek tahsilat yok)."); }
}

@Component
@Profile("prod") // yalnızca "prod" profilinde
class GercekOdeme implements OdemeServisi {
    public void ode(double tutar) { System.out.println("  [GERÇEK] " + tutar + " TL banka üzerinden tahsil edildi."); }
}

@Component
class OdemeKullanici {
    private final OdemeServisi odeme; // aktif profile göre doğru uygulama enjekte edilir
    OdemeKullanici(OdemeServisi odeme) { this.odeme = odeme; }
    void ode(double tutar) { odeme.ode(tutar); }
}
