// Ornek2: Bean scope'ları — singleton (varsayılan) vs prototype.
// Çalıştırma: java Ornek2.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class Ornek2 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(AyarServisi.class, SepetNesnesi.class);
        ctx.refresh();

        // --- SINGLETON (varsayılan): container ömrü boyunca TEK örnek ---
        AyarServisi a1 = ctx.getBean(AyarServisi.class);
        AyarServisi a2 = ctx.getBean(AyarServisi.class);
        System.out.println("Singleton:");
        System.out.println("  a1 == a2 ? " + (a1 == a2) + "   (aynı nesne)");
        System.out.println("  a1.id = " + a1.id() + ", a2.id = " + a2.id());

        // --- PROTOTYPE: her getBean()'de YENİ örnek ---
        SepetNesnesi s1 = ctx.getBean(SepetNesnesi.class);
        SepetNesnesi s2 = ctx.getBean(SepetNesnesi.class);
        System.out.println("\nPrototype:");
        System.out.println("  s1 == s2 ? " + (s1 == s2) + "  (farklı nesneler)");
        System.out.println("  s1.id = " + s1.id() + ", s2.id = " + s2.id());

        ctx.close();

        System.out.println("""

                --- Ne zaman hangisi? ---
                singleton : durumsuz (stateless) servisler, repository'ler, yapılandırma.
                            (Spring'de bean'lerin VARSAYILAN kapsamı budur.)
                prototype : her kullanımda taze, durumlu (stateful) nesneler (ör. bir sepet, bir form).
                Web'de ayrıca 'request' ve 'session' kapsamları da vardır (her istek/oturum için bir bean).""");
    }
}

// Varsayılan kapsam singleton'dır; @Scope yazmaya gerek yok.
@Component
class AyarServisi {
    private final int id = System.identityHashCode(this);
    int id() { return id; }
}

@Component
@Scope("prototype") // her istekte yeni nesne
class SepetNesnesi {
    private final int id = System.identityHashCode(this);
    int id() { return id; }
}
