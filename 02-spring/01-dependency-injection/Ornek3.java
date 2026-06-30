// Ornek3: Aynı tipte birden çok bean — belirsizliği @Primary ve @Qualifier ile çözmek.
// Gerçek senaryo: e-posta ve SMS aynı arayüzü uygular; Spring hangisini enjekte etsin?
// Çalıştırma: java Ornek3.java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(EpostaBildirimci.class, SmsBildirimci.class,
                VarsayilanServis.class, AcilServis.class);
        ctx.refresh();

        // VarsayilanServis, @Primary olan bildirimciyi (e-posta) alır.
        System.out.print("Varsayılan servis -> ");
        ctx.getBean(VarsayilanServis.class).bildir("Hoş geldiniz");

        // AcilServis, @Qualifier ile AÇIKÇA SMS'i ister.
        System.out.print("Acil servis       -> ");
        ctx.getBean(AcilServis.class).bildir("Sunucu çöktü!");

        ctx.close();

        System.out.println("""

                --- Belirsizlik (ambiguity) ---
                Bildirimci tipinde İKİ bean var (e-posta, SMS). Spring hangisini seçeceğini bilemez.
                Çözüm:
                  @Primary  -> "eşit adaylar varsa BENİ seç" (varsayılan tercih).
                  @Qualifier-> "tam olarak ŞU isimli bean'i ver" (açık seçim).""");
    }
}

interface Bildirimci { void gonder(String mesaj); }

@Component
@Primary // birden çok aday olduğunda varsayılan olarak bu seçilir
class EpostaBildirimci implements Bildirimci {
    public void gonder(String m) { System.out.println("[e-posta] " + m); }
}

@Component("sms") // bean adı: "sms"
class SmsBildirimci implements Bildirimci {
    public void gonder(String m) { System.out.println("[SMS] " + m); }
}

@Service
class VarsayilanServis {
    private final Bildirimci bildirimci;
    VarsayilanServis(Bildirimci bildirimci) { this.bildirimci = bildirimci; } // @Primary geldi
    void bildir(String m) { bildirimci.gonder(m); }
}

@Service
class AcilServis {
    private final Bildirimci bildirimci;
    // @Qualifier ile tam olarak "sms" bean'ini istiyoruz.
    AcilServis(@Qualifier("sms") Bildirimci bildirimci) { this.bildirimci = bildirimci; }
    void bildir(String m) { bildirimci.gonder(m); }
}
