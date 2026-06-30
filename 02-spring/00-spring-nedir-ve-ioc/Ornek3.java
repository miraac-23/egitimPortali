// Ornek3: ÇÖZÜM — Spring IoC container. Nesneleri ve bağlantıları artık Spring yönetir.
// Çalıştırma: java Ornek3.java   (Spring classpath portal tarafından sağlanır)
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek3 {

    public static void main(String[] args) {
        // IoC = Inversion of Control (kontrolün tersine çevrilmesi):
        // nesneleri SEN değil, CONTAINER oluşturur ve birbirine bağlar.
        // Sen sadece "ne istediğini" tarif edersin (yapılandırma); "nasıl"ı Spring halleder.
        try (var ctx = new AnnotationConfigApplicationContext(UygulamaConfig.class)) {

            // Container'dan hazır, bağımlılıkları enjekte edilmiş servisi isteriz.
            SiparisServisi servis = ctx.getBean(SiparisServisi.class);
            servis.siparisVer("Kulaklık");

            // Bean'lerin container tarafından yönetildiğini görelim:
            System.out.println("\nContainer'daki bazı bean'ler:");
            for (String ad : new String[]{"depo", "bildirimci", "siparisServisi"}) {
                System.out.println("  - " + ad + " -> " + ctx.getBean(ad).getClass().getSimpleName());
            }

            System.out.println("""

                    --- Spring ne kazandırdı? ---
                    1) Nesneleri 'new' ile biz oluşturmadık; container oluşturdu.
                    2) Bağımlılıkları (Depo, Bildirimci) container OTOMATİK enjekte etti.
                    3) Uygulamayı değiştirmek için yalnızca YAPILANDIRMAYI değiştirmek yeter.
                    Bu, gevşek bağlılığın ve test edilebilirliğin en üst seviyesidir.""");
        }
    }
}

// Aynı arayüzler ve uygulamalar (Örnek 2 ile aynı iş mantığı).
interface Depo { void kaydet(String kayit); }
interface Bildirimci { void gonder(String mesaj); }

class PostgresDepo implements Depo {
    public void kaydet(String k) { System.out.println("  [PostgreSQL] kaydedildi: " + k); }
}
class SmsBildirimci implements Bildirimci {
    public void gonder(String m) { System.out.println("  [SMS] " + m); }
}

class SiparisServisi {
    private final Depo depo;
    private final Bildirimci bildirimci;
    SiparisServisi(Depo depo, Bildirimci bildirimci) { this.depo = depo; this.bildirimci = bildirimci; }
    void siparisVer(String urun) {
        depo.kaydet(urun);
        bildirimci.gonder("Siparişiniz alındı: " + urun);
    }
}

// Yapılandırma: "hangi bean'ler var ve nasıl kurulur" — container bunu okur.
@Configuration
class UygulamaConfig {
    @Bean Depo depo() { return new PostgresDepo(); }
    @Bean Bildirimci bildirimci() { return new SmsBildirimci(); }

    // Spring, depo() ve bildirimci() bean'lerini bu metoda OTOMATİK enjekte eder.
    @Bean SiparisServisi siparisServisi(Depo depo, Bildirimci bildirimci) {
        return new SiparisServisi(depo, bildirimci);
    }
}
