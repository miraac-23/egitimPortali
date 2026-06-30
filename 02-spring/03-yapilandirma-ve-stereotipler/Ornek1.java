// Ornek1: Bean tanımlamanın iki yolu — Java config (@Bean) vs stereotype (@Component).
// Çalıştırma: java Ornek1.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

public class Ornek1 {

    public static void main(String[] args) {
        // YOL 1: Java tabanlı yapılandırma — @Configuration sınıfında @Bean metotları.
        // Bean oluşturmayı SEN kontrol edersin (3. parti sınıflar için idealdir).
        System.out.println("--- Java config (@Bean) ---");
        var ctx1 = new AnnotationConfigApplicationContext(JavaConfig.class);
        ctx1.getBean(RaporServisi.class).rapor();
        ctx1.close();

        // YOL 2: Stereotype + bileşen tarama — sınıfı @Component/@Service ile işaretlersin,
        // Spring onu otomatik bulur. (Tek dosyada register ile; gerçekte @ComponentScan.)
        System.out.println("\n--- Stereotype (@Service) ---");
        var ctx2 = new AnnotationConfigApplicationContext();
        ctx2.register(PdfUretici.class, RaporServisi2.class);
        ctx2.refresh();
        ctx2.getBean(RaporServisi2.class).rapor();
        ctx2.close();

        System.out.println("""

                --- Hangisi ne zaman? ---
                @Bean (Java config): kaynak kodunu DEĞİŞTİREMEDİĞİN sınıfları (3. parti) bean yapmak;
                                     oluşturmayı tam kontrol etmek istediğinde.
                @Component/@Service : kendi yazdığın sınıflar; @ComponentScan ile otomatik bulunur (en yaygın).
                Eski projelerde bir de XML tabanlı yapılandırma vardır; bugün tercih edilmez.""");
    }
}

// --- YOL 1: Java config ---
class PdfUretici { String uret() { return "PDF raporu üretildi"; } }
class RaporServisi {
    private final PdfUretici uretici;
    RaporServisi(PdfUretici uretici) { this.uretici = uretici; }
    void rapor() { System.out.println("  " + uretici.uret()); }
}

@Configuration
class JavaConfig {
    @Bean PdfUretici pdfUretici() { return new PdfUretici(); }
    @Bean RaporServisi raporServisi(PdfUretici u) { return new RaporServisi(u); }
}

// --- YOL 2: Stereotype ---
@Service
class RaporServisi2 {
    private final PdfUretici uretici;
    RaporServisi2(PdfUretici uretici) { this.uretici = uretici; }
    void rapor() { System.out.println("  " + uretici.uret() + " (stereotype ile)"); }
}
