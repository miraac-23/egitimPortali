// Ornek1: AUTO-WIRING (otomatik bağlama) — bağımlılığı Spring'in kendisi bulup enjekte etmesi.
// Önceki bölümlerde bağımlılıkları @Bean metoduna ELLE parametre vererek bağladık. Auto-wiring'de
// ise sadece @Autowired dersin; Spring uygun bean'i TİPE bakıp bulur ve yerleştirir.
// Üç enjeksiyon noktası: constructor (önerilen), setter ve field.
// Çalıştırma: java Ornek1.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

public class Ornek1 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        // @Component sınıflarını kaydet; Spring aralarındaki @Autowired bağlarını kendisi kurar.
        ctx.register(EpostaServisi.class, DepoServisi.class, SiparisServisi.class);
        ctx.refresh();

        ctx.getBean(SiparisServisi.class).siparisVer("Klavye");

        System.out.println("""

                --- Klasik auto-wiring modları (XML kökenli) ve modern karşılıkları ---
                * no        : otomatik bağlama yok, her şeyi elle bağlarsın (varsayılan XML).
                * byType    : aynı tipte tek bean varsa onu enjekte et   -> @Autowired (modern varsayılan)
                * byName    : property ADIYLA aynı isimli bean'i enjekte et -> @Autowired + alan adı eşleşmesi
                * constructor: byType'ın constructor parametreleri üzerinden hali -> constructor @Autowired
                Modern Spring'de 'byType' (constructor injection) fiili standarttır.""");
        ctx.close();
    }
}

@Component
class EpostaServisi {
    void gonder(String m) { System.out.println("  [e-posta] " + m); }
}

@Component
class DepoServisi {
    void kaydet(String k) { System.out.println("  [depo] kaydedildi: " + k); }
}

@Component
class SiparisServisi {
    private final DepoServisi depo;     // constructor ile bağlanacak
    private EpostaServisi eposta;       // setter ile bağlanacak

    // CONSTRUCTOR injection: tek constructor varsa @Autowired yazmana bile gerek yok.
    SiparisServisi(DepoServisi depo) {
        this.depo = depo;
        System.out.println("[ctor] DepoServisi constructor ile enjekte edildi.");
    }

    // SETTER injection: Spring, context'i kurarken bu setter'ı tipe göre çağırır.
    @Autowired
    void setEposta(EpostaServisi eposta) {
        this.eposta = eposta;
        System.out.println("[setter] EpostaServisi setter ile enjekte edildi.");
    }

    void siparisVer(String urun) {
        depo.kaydet(urun);
        eposta.gonder("Siparişiniz alındı: " + urun);
    }
}
