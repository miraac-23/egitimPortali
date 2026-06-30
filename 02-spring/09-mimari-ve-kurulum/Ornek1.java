// Ornek1: SPRING "MERHABA DÜNYA" — en küçük çalışan Spring uygulaması.
// Amaç: bir IoC container ayağa kaldırmak, ondan bir bean istemek ve metodunu çağırmak.
// Çalıştırma: java Ornek1.java   (Spring classpath portal tarafından sağlanır)
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek1 {

    public static void main(String[] args) {
        // 1) Container'ı, yapılandırma sınıfını vererek başlat. Bu tek satır:
        //    - sınıf yolundaki @Configuration'ı okur,
        //    - @Bean metotlarını çalıştırıp bean'leri oluşturur,
        //    - bağımlılıkları enjekte eder ve container'ı kullanıma hazırlar.
        try (var ctx = new AnnotationConfigApplicationContext(MerhabaConfig.class)) {

            // 2) Container'dan tip üzerinden bir bean iste (new YOK).
            MesajServisi servis = ctx.getBean(MesajServisi.class);

            // 3) Kullan.
            System.out.println(servis.selamla("Dünya"));

            System.out.println("\nContainer ayakta. Yönettiği bean'ler:");
            for (String ad : ctx.getBeanDefinitionNames()) {
                System.out.println("  - " + ad);
            }
        }
        // try-with-resources kapanınca container kapanır (bean'ler düzgünce temizlenir).
    }
}

// "Ne" yapılacağını tarif eden arayüz (soyutlama).
interface MesajServisi {
    String selamla(String kim);
}

// "Nasıl" yapılacağını veren somut uygulama.
class TurkceMesajServisi implements MesajServisi {
    public String selamla(String kim) {
        return "Merhaba, " + kim + "! (Spring container'ından geldim.)";
    }
}

// Yapılandırma: container'a "hangi bean'ler var" diye söyler.
@Configuration
class MerhabaConfig {
    @Bean
    MesajServisi mesajServisi() {
        return new TurkceMesajServisi();
    }
}
