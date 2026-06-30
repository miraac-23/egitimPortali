// Ornek2: Olay (event) mekanizması — bileşenleri gevşek bağlamak.
// Gerçek senaryo: sipariş oluşunca e-posta VE analitik, birbirinden habersiz tepki versin.
// Çalıştırma: portal Spring classpath'iyle çalıştırır.
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class Ornek2 {

    // Olay nesnesi: "ne oldu" bilgisini taşır (basit bir record yeterli).
    record SiparisOlusturuldu(String urun, double tutar) {}

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(SiparisServisi.class, EpostaDinleyici.class, AnalitikDinleyici.class);
        ctx.refresh();

        // Servis siparişi oluşturur ve bir OLAY yayınlar; kime gittiğini BİLMEZ.
        ctx.getBean(SiparisServisi.class).siparisOlustur("Klavye", 450);

        ctx.close();

        System.out.println("""

                --- Olay mekanizması ne kazandırır? ---
                SiparisServisi, e-posta veya analitik bileşenlerini HİÇ tanımıyor; sadece olay yayınlıyor.
                Yeni bir tepki (ör. SMS, fatura) eklemek için SERVİSİ değiştirmene gerek yok;
                sadece yeni bir @EventListener eklersin. Bu, Observer deseninin Spring'e gömülü hâlidir.
                (@EventListener ayrıca @Async ile asenkron da çalıştırılabilir.)""");
    }
}

@Service
class SiparisServisi {
    private final ApplicationEventPublisher yayinci;
    // Spring, olay yayınlamak için ApplicationEventPublisher'ı enjekte eder.
    SiparisServisi(ApplicationEventPublisher yayinci) { this.yayinci = yayinci; }

    void siparisOlustur(String urun, double tutar) {
        System.out.println("[Servis] sipariş kaydedildi: " + urun);
        // Olayı yayınla — dinleyenler kendileri tepki verir.
        yayinci.publishEvent(new Ornek2.SiparisOlusturuldu(urun, tutar));
    }
}

@Component
class EpostaDinleyici {
    // Bu tipte bir olay yayınlanınca Spring bu metodu otomatik çağırır.
    @EventListener
    void siparisOlunca(Ornek2.SiparisOlusturuldu olay) {
        System.out.println("[E-posta] '" + olay.urun() + "' siparişi için onay e-postası gönderildi.");
    }
}

@Component
class AnalitikDinleyici {
    @EventListener
    void siparisOlunca(Ornek2.SiparisOlusturuldu olay) {
        System.out.println("[Analitik] ciroya " + olay.tutar() + " TL eklendi.");
    }
}
