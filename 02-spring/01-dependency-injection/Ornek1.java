// Ornek1: Constructor injection — Spring'in ÖNERDİĞİ enjeksiyon türü.
// @Service/@Repository sınıflarını container'a kaydedip otomatik bağlamasını izliyoruz.
// Çalıştırma: java Ornek1.java   (Spring classpath portal tarafından sağlanır)
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public class Ornek1 {

    public static void main(String[] args) {
        // @ComponentScan yerine sınıfları doğrudan kaydediyoruz (tek dosya senaryosu).
        // Gerçek uygulamada @ComponentScan bu sınıfları otomatik bulur.
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(UrunServisi.class, UrunDeposu.class);
        ctx.refresh();

        UrunServisi servis = ctx.getBean(UrunServisi.class);
        servis.urunEkle("Klavye");
        servis.listele();

        ctx.close();

        System.out.println("""

                --- Neden constructor injection? ---
                1) Alanlar 'final' olabilir -> değişmez (immutable), güvenli nesne.
                2) Zorunlu bağımlılıklar AÇIKTIR: nesne, eksiksiz kurulmadan var olamaz.
                3) Test kolaydır: testte bağımlılığı doğrudan constructor'a verirsin (Spring bile gerekmez).
                Spring 4.3+ ile tek constructor varsa @Autowired yazmaya bile gerek yoktur.""");
    }
}

@Repository
class UrunDeposu {
    private final java.util.List<String> kayitlar = new java.util.ArrayList<>();
    void kaydet(String urun) { kayitlar.add(urun); }
    java.util.List<String> hepsi() { return kayitlar; }
}

@Service
class UrunServisi {
    // CONSTRUCTOR INJECTION: bağımlılık final, zorunlu ve açık.
    private final UrunDeposu depo;

    // Tek constructor olduğu için @Autowired ZORUNLU değil; Spring otomatik enjekte eder.
    UrunServisi(UrunDeposu depo) {
        this.depo = depo;
        System.out.println("UrunServisi kuruldu; depo enjekte edildi: " + (depo != null));
    }

    void urunEkle(String urun) { depo.kaydet(urun); System.out.println("Eklendi: " + urun); }
    void listele() { System.out.println("Tüm ürünler: " + depo.hepsi()); }
}
