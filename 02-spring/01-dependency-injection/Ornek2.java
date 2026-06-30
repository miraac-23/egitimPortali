// Ornek2: Setter ve field injection — alternatifler ve neden constructor kadar iyi olmadıkları.
// Çalıştırma: java Ornek2.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class Ornek2 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(EpostaServisi.class, SetterServisi.class, FieldServisi.class);
        ctx.refresh();

        System.out.println("Setter injection:");
        ctx.getBean(SetterServisi.class).calistir();

        System.out.println("\nField injection:");
        ctx.getBean(FieldServisi.class).calistir();

        ctx.close();

        System.out.println("""

                --- Karşılaştırma ---
                Setter injection: bağımlılık SONRADAN değiştirilebilir (opsiyonel bağımlılıklar için uygun)
                                  ama alan 'final' olamaz ve nesne yarım kurulu bir an yaşayabilir.
                Field injection : en kısa görünür AMA en sorunlusudur:
                                  - alan 'final' olamaz, gizli bağımlılık (constructor'a bakınca görünmez),
                                  - Spring olmadan test etmek zordur (reflection gerekir).
                ÖNERİ: Zorunlu bağımlılıklar için CONSTRUCTOR injection kullan (Örnek 1).""");
    }
}

@Component
class EpostaServisi {
    void gonder(String m) { System.out.println("  [e-posta] " + m); }
}

@Service
class SetterServisi {
    private EpostaServisi eposta; // final OLAMAZ

    // SETTER INJECTION: Spring, kurulumdan sonra bu setter'ı çağırıp enjekte eder.
    @Autowired
    void setEposta(EpostaServisi eposta) { this.eposta = eposta; }

    void calistir() { eposta.gonder("setter ile enjekte edildi"); }
}

@Service
class FieldServisi {
    // FIELD INJECTION: Spring değeri doğrudan alana (reflection ile) yazar.
    @Autowired
    private EpostaServisi eposta; // final OLAMAZ, gizli bağımlılık

    void calistir() { eposta.gonder("field ile enjekte edildi"); }
}
