// Ornek1: BeanFactory vs ApplicationContext — TEMBELLİK (lazy) farkı.
// İki container da IoC yapar ama davranışları farklıdır:
//  - BeanFactory  : bean'i SEN isteyene kadar OLUŞTURMAZ (tembel / lazy).
//  - ApplicationContext: refresh anında singleton'ları HEMEN oluşturur (eager).
// Bu farkı çıktıdaki "[kuruluyor]" satırlarının NE ZAMAN belirdiğine bakarak görürüz.
// Çalıştırma: java Ornek1.java
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek1 {

    public static void main(String[] args) {
        System.out.println("=== 1) BeanFactory (tembel) ===");
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("agirBean", new RootBeanDefinition(AgirBean.class));
        System.out.println("BeanFactory hazır. (Dikkat: 'agirBean' HENÜZ kurulmadı.)");
        System.out.println("Şimdi bean'i istiyoruz ->");
        bf.getBean(AgirBean.class);   // ANCAK ŞİMDİ oluşturulur
        System.out.println("BeanFactory: bean yalnızca istenince kuruldu.\n");

        System.out.println("=== 2) ApplicationContext (erken/eager) ===");
        System.out.println("Context'i başlatıyoruz ->");
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            // Yukarıdaki satır biter bitmez singleton zaten kurulmuştur:
            System.out.println("Context hazır. (Dikkat: 'agirBean' ZATEN kuruldu, biz istemeden.)");
            ctx.getBean(AgirBean.class); // ikinci kez kurulmaz — aynı singleton döner
            System.out.println("ApplicationContext: singleton'lar başlangıçta hazırlandı.");
        }

        System.out.println("""

                --- Sonuç ---
                BeanFactory tembeldir: hata/kaynak sorunları ANCAK bean istenince patlar.
                ApplicationContext erken kurar: yapılandırma hataları UYGULAMA AÇILIRKEN
                ortaya çıkar (üretimde tercih edilen davranış — 'erken patla').""");
    }
}

class AgirBean {
    AgirBean() { System.out.println("  [kuruluyor] AgirBean örneği oluşturuldu."); }
}

@Configuration
class Config {
    @Bean AgirBean agirBean() { return new AgirBean(); }
}
