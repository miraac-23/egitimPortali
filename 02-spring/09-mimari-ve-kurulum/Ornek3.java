// Ornek3: KURULUM DOĞRULAMA — "ortam hazır mı?" testi.
// Gerçek bir projede Spring'i Maven/Gradle bağımlılığı olarak eklersin (README'ye bak).
// Bu örnek, kurulumun doğru olduğunu KANITLAR: context ayağa kalkıyor, sürüm okunuyor,
// bir bean üretiliyor ve init/destroy yaşam döngüsü kancaları tetikleniyor.
// Çalıştırma: java Ornek3.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;

public class Ornek3 {

    public static void main(String[] args) {
        System.out.println("Spring çekirdeği classpath'te mi? -> sürüm: " + SpringVersion.getVersion());

        // initMethod/destroyMethod ile container'ın bean yaşam döngüsünü yönettiğini doğrula.
        try (var ctx = new AnnotationConfigApplicationContext(KurulumConfig.class)) {
            var saglik = ctx.getBean(SaglikKontrol.class);
            System.out.println("Sağlık kontrolü: " + saglik.durum());
            System.out.println("\nKurulum BAŞARILI. Spring uygulaması yazmaya hazırsın.");
        }
        // Buradan sonra "container kapandı" mesajını init/destroy'dan göreceksin.
    }
}

class SaglikKontrol {
    void baslat() { System.out.println("  [init] SaglikKontrol bean'i container tarafından kuruldu."); }
    void kapat()  { System.out.println("  [destroy] SaglikKontrol bean'i container tarafından temizlendi."); }
    String durum() { return "AYAKTA"; }
}

@Configuration
class KurulumConfig {
    // initMethod/destroyMethod: Spring, bean'i oluşturduktan sonra baslat()'ı,
    // container kapanırken kapat()'ı otomatik çağırır. Yaşam döngüsünü container yönetir.
    @Bean(initMethod = "baslat", destroyMethod = "kapat")
    SaglikKontrol saglikKontrol() {
        return new SaglikKontrol();
    }
}
