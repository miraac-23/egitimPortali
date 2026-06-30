// Ornek1: Bean yaşam döngüsü — başlatma (init) ve yok etme (destroy) geri çağrıları.
// Gerçek senaryo: bir bağlantı havuzu init'te açılır, context kapanınca kapatılır.
// Çalıştırma: java Ornek1.java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

public class Ornek1 {

    public static void main(String[] args) {
        System.out.println(">> Container başlatılıyor...");
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(BaglantiHavuzu.class);
        ctx.refresh(); // bean'ler oluşturulur ve init geri çağrıları tetiklenir

        System.out.println("\n>> Uygulama çalışıyor:");
        ctx.getBean(BaglantiHavuzu.class).baglantiAl();

        System.out.println("\n>> Container kapatılıyor...");
        ctx.close(); // destroy geri çağrıları tetiklenir

        System.out.println("""

                --- Yaşam döngüsü sırası ---
                1) Constructor       (nesne oluşturulur)
                2) Bağımlılık enjeksiyonu
                3) @PostConstruct / afterPropertiesSet() (başlatma)
                4) ... bean kullanılır ...
                5) @PreDestroy / destroy()              (temizlik, context kapanınca)""");
    }
}

@Component
class BaglantiHavuzu implements InitializingBean, DisposableBean {

    BaglantiHavuzu() {
        System.out.println("1) Constructor: havuz nesnesi oluşturuldu (henüz açık değil).");
    }

    // jakarta.annotation: en yaygın, framework-bağımsız başlatma kancası.
    @PostConstruct
    void baslat() {
        System.out.println("3a) @PostConstruct: bağlantı havuzu AÇILDI.");
    }

    // Spring arayüzü: aynı işi yapar (@PostConstruct'tan sonra çağrılır).
    @Override
    public void afterPropertiesSet() {
        System.out.println("3b) afterPropertiesSet(): havuz hazır.");
    }

    void baglantiAl() {
        System.out.println("   -> havuzdan bağlantı verildi.");
    }

    @PreDestroy
    void kapat() {
        System.out.println("5a) @PreDestroy: bağlantı havuzu KAPATILIYOR.");
    }

    @Override
    public void destroy() {
        System.out.println("5b) destroy(): tüm kaynaklar serbest bırakıldı.");
    }
}
