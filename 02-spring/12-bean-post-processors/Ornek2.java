// Ornek2: BeanFactoryPostProcessor — bean TANIMLARINI (definition) değiştirme kancası.
// BeanPostProcessor bean NESNESİNE dokunur; BeanFactoryPostProcessor ise bean'ler daha
// OLUŞMADAN, onların TARİFLERİNE (BeanDefinition) dokunur. Bu yüzden değerleri/property'leri
// instantiation ÖNCESİNDE değiştirebilir. (PropertySourcesPlaceholderConfigurer böyle çalışır.)
// Çalıştırma: java Ornek2.java
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek2 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            // Tanım instantiation'dan önce değiştirildiği için, bean ZATEN yeni değerle kurulur.
            System.out.println("\nSonuç -> " + ctx.getBean(SunucuAyari.class));
            System.out.println("""

                    --- BeanFactoryPostProcessor vs BeanPostProcessor ---
                    * BeanFactoryPostProcessor: bean'ler OLUŞMADAN ÖNCE, TANIMLARI değiştirir.
                    * BeanPostProcessor       : bean'ler OLUŞTUKTAN sonra, NESNELERE dokunur.
                    Property placeholder çözümü (${...}), @Configuration işleme gibi temel
                    mekanizmalar birer BeanFactoryPostProcessor'dır.""");
        }
    }
}

class SunucuAyari {
    private int port = 8080; // varsayılan
    public void setPort(int p) { this.port = p; }
    public String toString() { return "SunucuAyari(port=" + port + ")"; }
}

// Tanımları gezip 'port' property'sini 9090'a çeken bir factory post-processor.
class PortDegistiren implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) {
        var bd = bf.getBeanDefinition("sunucuAyari");
        System.out.println("[BFPP] 'sunucuAyari' tanımı bulundu; port 8080 -> 9090 olarak değiştiriliyor.");
        bd.getPropertyValues().add("port", 9090); // bean HENÜZ oluşmadı; tarife yazıyoruz
    }
}

@Configuration
class Config {
    // post-processor @Bean metodu 'static' olmalı (container yaşam döngüsü uyarısını önler).
    @Bean static PortDegistiren portDegistiren() { return new PortDegistiren(); }
    @Bean SunucuAyari sunucuAyari() { return new SunucuAyari(); }
}
