// Ornek3: Stereotype anotasyonları ve katmanlı mimari (@Controller/@Service/@Repository).
// Hepsi aslında @Component'tir; fark ANLAMSAL (semantic) ve katmana özel davranışlardır.
// Çalıştırma: java Ornek3.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(UrunController.class, UrunService.class, UrunRepository.class);
        ctx.refresh();

        // Tipik akış: Controller -> Service -> Repository (her katmanın tek bir sorumluluğu var).
        ctx.getBean(UrunController.class).istekGeldi("Klavye");

        // Hepsi @Component olduğundan birer bean'dir:
        System.out.println("\nKatman bean'leri:");
        for (String ad : new String[]{"urunController", "urunService", "urunRepository"}) {
            System.out.println("  " + ad + " -> " + ctx.getBean(ad).getClass().getSimpleName());
        }
        ctx.close();

        System.out.println("""

                --- Stereotype anotasyonları ---
                @Component  : genel amaçlı bean (taban).
                @Service    : iş mantığı katmanı (anlamsal; @Component ile aynı temel).
                @Repository : veri erişim katmanı + DataAccessException'a OTOMATİK çeviri.
                @Controller : web sunum katmanı (@RestController bunun REST türevidir).
                Hepsi @ComponentScan ile otomatik bulunur; ayrım okunabilirlik ve katmana özel davranış içindir.""");
    }
}

@Controller // sunum katmanı (web'de istekleri karşılar)
class UrunController {
    private final UrunService service;
    UrunController(UrunService service) { this.service = service; }
    void istekGeldi(String urun) {
        System.out.println("[Controller] istek alındı: " + urun);
        service.kaydet(urun);
    }
}

@Service // iş mantığı katmanı
class UrunService {
    private final UrunRepository repo;
    UrunService(UrunRepository repo) { this.repo = repo; }
    void kaydet(String urun) {
        System.out.println("[Service]    iş kuralları uygulandı");
        repo.insert(urun);
    }
}

@Repository // veri erişim katmanı
class UrunRepository {
    void insert(String urun) { System.out.println("[Repository] veritabanına yazıldı: " + urun); }
}
