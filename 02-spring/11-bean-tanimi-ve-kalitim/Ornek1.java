// Ornek1: BEAN DEFINITION (bean tanımı) — bir bean'in "kimlik kartı".
// Container, her bean için bir BeanDefinition tutar: hangi sınıf, hangi scope, tembel mi,
// hangi init/destroy metodu, nasıl üretilecek (constructor mu factory metodu mu)...
// Bu örnek, tanımın metadata'sını OKUYARAK gösterir.
// Çalıştırma: java Ornek1.java
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;

public class Ornek1 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(TanimConfig.class)) {

            // Container'ın her bean için tuttuğu tanımı (metadata) okuyalım.
            for (String ad : new String[]{"tekil", "prototip", "tembel", "fabrikaUrunu"}) {
                BeanDefinition bd = ctx.getBeanFactory().getBeanDefinition(ad);
                System.out.printf("%-14s scope=%-10s lazy=%-5s class=%s%n",
                        ad,
                        bd.getScope().isEmpty() ? "singleton" : bd.getScope(),
                        bd.isLazyInit(),
                        bd.getBeanClassName() == null ? "(factory metodu)" : kisaAd(bd.getBeanClassName()));
            }

            System.out.println("\nfabrikaUrunu nasıl üretildi? -> " + ctx.getBean("fabrikaUrunu"));
            System.out.println("""

                    --- Bir BeanDefinition neleri tarif eder? ---
                    * sınıf / üretim yolu (constructor veya factory metodu)
                    * scope (singleton, prototype, request, session...)
                    * lazy-init (erken mi tembel mi kurulsun)
                    * init / destroy metotları (yaşam döngüsü kancaları)
                    * constructor argümanları ve property değerleri
                    * depends-on (önce kurulması gereken bean'ler)
                    Bunların hepsini @Bean, @Scope, @Lazy gibi anotasyonlarla tarif edersin.""");
        }
    }

    static String kisaAd(String fqcn) { return fqcn.substring(fqcn.lastIndexOf('.') + 1); }
}

class Urun {
    final String kaynak;
    Urun(String kaynak) { this.kaynak = kaynak; }
    public String toString() { return "Urun(kaynak=" + kaynak + ")"; }
}

// Statik bir fabrika: bean, constructor yerine bu metottan üretilebilir.
class UrunFabrikasi {
    static Urun uret() { return new Urun("statik-fabrika-metodu"); }
}

@Configuration
class TanimConfig {
    @Bean Object tekil() { return new Object(); }                 // varsayılan: singleton

    @Bean @Scope("prototype") Object prototip() { return new Object(); }

    @Bean @Lazy Object tembel() { return new Object(); }          // ilk istenince kurulur

    @Bean Urun fabrikaUrunu() { return UrunFabrikasi.uret(); }    // factory metoduyla üretim
}
