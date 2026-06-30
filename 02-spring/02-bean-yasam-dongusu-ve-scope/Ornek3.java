// Ornek3: Gerçek tuzak — singleton bir bean, prototype bir bean'e ihtiyaç duyarsa ne olur?
// Çözüm: ObjectProvider ile her seferinde TAZE prototype almak.
// Çalıştırma: java Ornek3.java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(Gorev.class, YanlisIsleyici.class, DogruIsleyici.class);
        ctx.refresh();

        // TUZAK: singleton servis, prototype'ı constructor'da BİR KEZ aldı.
        // Aynı prototype örneği tekrar tekrar kullanılır -> "taze nesne" beklentisi boşa çıkar.
        System.out.println("Yanlış (prototype bir kez enjekte edildi):");
        YanlisIsleyici yanlis = ctx.getBean(YanlisIsleyici.class);
        System.out.println("  görev id: " + yanlis.calistir());
        System.out.println("  görev id: " + yanlis.calistir() + "   <- AYNI nesne (tuzak!)");

        // ÇÖZÜM: ObjectProvider ile her ihtiyaçta container'dan TAZE prototype iste.
        System.out.println("\nDoğru (ObjectProvider ile her seferinde taze):");
        DogruIsleyici dogru = ctx.getBean(DogruIsleyici.class);
        System.out.println("  görev id: " + dogru.calistir());
        System.out.println("  görev id: " + dogru.calistir() + "   <- FARKLI nesne (doğru)");

        ctx.close();

        System.out.println("""

                --- Çıkarım ---
                Singleton bir bean, prototype'ı SADECE BİR KEZ (kurulumda) enjekte ederse,
                prototype'ın 'her seferinde yeni' avantajı kaybolur.
                Çözümlerden biri: ObjectProvider<T> (veya @Lookup) ile ihtiyaç anında taze örnek almak.""");
    }
}

@Component
@Scope("prototype")
class Gorev {
    private final int id = System.identityHashCode(this);
    int id() { return id; }
}

@Service // singleton
class YanlisIsleyici {
    private final Gorev gorev; // prototype ama yalnızca BİR KEZ enjekte edilir
    YanlisIsleyici(Gorev gorev) { this.gorev = gorev; }
    int calistir() { return gorev.id(); }
}

@Service // singleton
class DogruIsleyici {
    private final ObjectProvider<Gorev> gorevSaglayici; // her çağrıda taze üretebilir
    DogruIsleyici(ObjectProvider<Gorev> gorevSaglayici) { this.gorevSaglayici = gorevSaglayici; }
    int calistir() { return gorevSaglayici.getObject().id(); } // TAZE prototype
}
