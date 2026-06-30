// Ornek1: BeanPostProcessor — her bean'in kuruluşuna ARAYA GİRME kancası.
// Container, her bean'i oluşturup bağımlılıklarını enjekte ettikten sonra, init metodundan
// HEMEN ÖNCE ve HEMEN SONRA tüm BeanPostProcessor'lara haber verir. Böylece bean'leri
// merkezi bir yerden gözlemleyebilir, doğrulayabilir veya sarmalayabilirsin.
// Çalıştırma: java Ornek1.java
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek1 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            System.out.println("\nContext hazır. Bean'i kullanıyoruz:");
            System.out.println(ctx.getBean(Servis.class).selamla());
            System.out.println("""

                    --- BeanPostProcessor ne işe yarar? ---
                    * Her bean için Before/After init kancası verir (init'ten önce ve sonra).
                    * @Autowired, @Value, @PostConstruct gibi anotasyonları ÇÖZEN işleyiciler
                      birer BeanPostProcessor'dır — yani anotasyon 'sihri' buradan gelir.
                    * Tipik kullanım: doğrulama, ölçüm, ve bean'i bir proxy ile sarmalama (AOP).""");
        }
    }
}

class Servis {
    void baslat() { System.out.println("  [init] Servis.baslat() çağrıldı."); }
    String selamla() { return "Servis çalışıyor."; }
}

// Her bean'in kuruluşunu gözlemleyen bir post-processor.
class GunlukleyenPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String ad) {
        if (bean instanceof Servis) System.out.println("[BPP] '" + ad + "' init ÖNCESİ");
        return bean; // bean'i olduğu gibi (veya sarmalanmış olarak) döndür
    }
    public Object postProcessAfterInitialization(Object bean, String ad) {
        if (bean instanceof Servis) System.out.println("[BPP] '" + ad + "' init SONRASI");
        return bean;
    }
}

@Configuration
class Config {
    // BeanPostProcessor'ın kendisi de bir bean'dir; container onu otomatik tanır ve kaydeder.
    // ÖNEMLİ: post-processor @Bean metotları 'static' olmalıdır — aksi halde container'ın çok
    // erken kurulması gerektiğinden Spring uyarı verir. (static olunca @Configuration örneklenmeden çağrılır.)
    @Bean static GunlukleyenPostProcessor gunlukleyenPostProcessor() { return new GunlukleyenPostProcessor(); }

    @Bean(initMethod = "baslat") Servis servis() { return new Servis(); }
}
