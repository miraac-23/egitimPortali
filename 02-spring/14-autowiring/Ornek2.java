// Ornek2: BELİRSİZLİK (ambiguity) — aynı tipte birden çok aday bean olunca ne olur?
// Auto-wiring tipe bakar. Peki aynı tipte İKİ bean varsa Spring hangisini seçsin?
// Çözüm araçları: @Primary (varsayılan aday) ve @Qualifier (adıyla kesin seçim).
// Çalıştırma: java Ornek2.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

public class Ornek2 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(EpostaMesajci.class, SmsMesajci.class,
                VarsayilanKullanan.class, CtorQualifierKullanan.class, FieldQualifierKullanan.class);
        ctx.refresh();

        ctx.getBean(VarsayilanKullanan.class).gonder();    // @Primary -> e-posta
        ctx.getBean(CtorQualifierKullanan.class).gonder(); // @Qualifier("sms") -> SMS
        ctx.getBean(FieldQualifierKullanan.class).gonder();// field @Qualifier("sms") -> SMS

        System.out.println("""

                --- Belirsizlik nasıl çözülür? (öncelik sırası) ---
                1) @Qualifier("ad") : EN ÖNCELİKLİ; adıyla kesin seçer, @Primary'yi de ezer.
                2) @Primary         : niteleyici yoksa 'varsayılan' aday budur.
                3) Alan/parametre adı eşleşmesi (byName) : @Primary YOKSA, Spring bean adına bakar.
                Yani @Primary varken alan adı eşleşmesi devreye girmez (bu örnekte e-posta primary).
                Pratik öneri: belirsizlikte @Qualifier kullan — niyet açık ve tutarlı olur.""");
        ctx.close();
    }
}

interface Mesajci { String gonderImpl(); }

@Component("eposta")
@Primary // birden çok Mesajci varsa VARSAYILAN budur
class EpostaMesajci implements Mesajci {
    public String gonderImpl() { return "[e-posta] gönderildi"; }
}

@Component("sms")
class SmsMesajci implements Mesajci {
    public String gonderImpl() { return "[SMS] gönderildi"; }
}

// (1) Niteleyici yok -> @Primary olan (e-posta) gelir.
@Component
class VarsayilanKullanan {
    private final Mesajci m;
    VarsayilanKullanan(Mesajci m) { this.m = m; }
    void gonder() { System.out.println("Varsayılan(@Primary) -> " + m.gonderImpl()); }
}

// (2) Constructor parametresinde @Qualifier -> SMS (Primary'yi ezer).
@Component
class CtorQualifierKullanan {
    private final Mesajci m;
    CtorQualifierKullanan(@Qualifier("sms") Mesajci m) { this.m = m; }
    void gonder() { System.out.println("Ctor @Qualifier(sms) -> " + m.gonderImpl()); }
}

// (3) Field üzerinde @Qualifier -> SMS (field injection de niteleyiciyi destekler).
@Component
class FieldQualifierKullanan {
    @Autowired @Qualifier("sms")
    private Mesajci m;
    void gonder() { System.out.println("Field @Qualifier(sms) -> " + m.gonderImpl()); }
}
