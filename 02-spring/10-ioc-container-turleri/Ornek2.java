// Ornek2: ApplicationContext TÜRLERİ ve programatik (GenericApplicationContext) kullanım.
// "ApplicationContext" bir arayüzdür; farklı kaynaklardan beslenen birçok uygulaması vardır:
//  - AnnotationConfigApplicationContext : @Configuration / @Component sınıflarından
//  - ClassPathXmlApplicationContext     : classpath'teki XML'den (eski tarz)
//  - FileSystemXmlApplicationContext    : dosya sistemindeki XML'den
//  - GenericApplicationContext          : bean'leri ELLE/lambda ile kaydettiğin esnek taban
// Bu örnek, en esnek olanı — GenericApplicationContext — ile bean'leri kodla kaydeder.
// Çalıştırma: java Ornek2.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class Ornek2 {

    public static void main(String[] args) {
        // 1) GenericApplicationContext: bean tanımlarını functional (lambda) olarak ekle.
        //    XML yok, @Configuration yok — saf kod. Spring 5+ "functional bean registration".
        try (var ctx = new GenericApplicationContext()) {
            ctx.registerBean("depo", Depo.class, BellekDepo::new);
            ctx.registerBean("servis", Servis.class,
                    () -> new Servis(ctx.getBean(Depo.class)));
            ctx.refresh(); // tanımlar bitti; container'ı kullanıma hazırla

            Servis s = ctx.getBean(Servis.class);
            System.out.println("GenericApplicationContext -> " + s.calistir("kayıt-1"));
        }

        // 2) AnnotationConfigApplicationContext: aynı işi anotasyon taramayla da yapabilir.
        //    register() ile sınıf ekleyip refresh edebilirsin (programatik kayıt).
        try (var ctx = new AnnotationConfigApplicationContext()) {
            ctx.registerBean(BellekDepo.class);
            ctx.registerBean(Servis.class);
            ctx.refresh();
            System.out.println("AnnotationConfigApplicationContext -> "
                    + ctx.getBean(Servis.class).calistir("kayıt-2"));
        }

        System.out.println("""

                --- Hangi context ne zaman? ---
                * AnnotationConfig...  : modern standart (Java config + bileşen tarama)
                * GenericApplicationContext : framework/test kodu, programatik kayıt, tam kontrol
                * ...XmlApplicationContext  : eski/legacy XML projeleri (yeni projede önerilmez)
                Spring Boot perde arkasında bunların özelleşmiş bir türünü kullanır.""");
    }
}

interface Depo { String kaydet(String k); }
class BellekDepo implements Depo {
    public String kaydet(String k) { return "[bellek] kaydedildi: " + k; }
}
class Servis {
    private final Depo depo;
    Servis(Depo depo) { this.depo = depo; }
    String calistir(String veri) { return depo.kaydet(veri); }
}
