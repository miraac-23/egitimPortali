// Ornek3: İSTEĞE BAĞLI ve ESNEK bağımlılıklar — required=false, Optional, ObjectProvider.
// Bazen bir bağımlılık VAR OLMAYABİLİR (eklenti, opsiyonel özellik). Zorunlu @Autowired
// böyle bir durumda uygulamayı çökertir. Spring esnek alternatifler sunar.
// Çalıştırma: java Ornek3.java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        // DİKKAT: 'OdemeServisi' BİLEREK kaydedilmedi (yok). Tüketici buna rağmen ayağa kalkmalı.
        ctx.register(Rapor.class);
        ctx.refresh();

        ctx.getBean(Rapor.class).yazdir();

        System.out.println("""

                --- Eksik olabilen bağımlılık nasıl ele alınır? ---
                * @Autowired(required=false) : bean yoksa alan null kalır (NPE riskine dikkat).
                * Optional<T>                : bean yoksa Optional.empty(); null'dan güvenli.
                * ObjectProvider<T>          : tembel erişim + ifAvailable/getIfAvailable; en esnek.
                ObjectProvider ayrıca çoklu/sıralı erişim (stream/orderedStream) da sağlar.""");
        ctx.close();
    }
}

// Var olmayan opsiyonel bir servis (kasıtlı olarak bean yapılmadı).
interface OdemeServisi { String ode(); }

@Component
class Rapor {
    @Autowired(required = false)
    private OdemeServisi zorunluDegil;             // bean yoksa null kalır

    private final Optional<OdemeServisi> opsiyonel; // bean yoksa empty
    private final ObjectProvider<OdemeServisi> saglayici; // tembel & en esnek

    Rapor(Optional<OdemeServisi> opsiyonel, ObjectProvider<OdemeServisi> saglayici) {
        this.opsiyonel = opsiyonel;
        this.saglayici = saglayici;
    }

    void yazdir() {
        System.out.println("required=false alanı null mı? " + (zorunluDegil == null));
        System.out.println("Optional mevcut mu? " + opsiyonel.isPresent());
        // ObjectProvider: bean varsa kullan, yoksa sessizce geç.
        saglayici.ifAvailable(s -> System.out.println("Ödeme: " + s.ode()));
        OdemeServisi bulunan = saglayici.getIfAvailable(); // bean yoksa null döner (istisna yok)
        System.out.println("ObjectProvider.getIfAvailable -> "
                + (bulunan == null ? "(ödeme servisi yok — varsayılan davranışa geçilir)" : bulunan.ode()));
    }
}
