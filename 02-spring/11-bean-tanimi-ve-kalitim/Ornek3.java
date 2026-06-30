// Ornek3: PROGRAMATİK bean tanımı (BeanDefinitionBuilder) ve depends-on sırası.
// Bazen bean'leri @Bean/anotasyon yerine KODLA tanımlamak gerekir (örn. dinamik sayıda
// bean üretmek). BeanDefinitionBuilder bunun akıcı (fluent) yoludur. Ayrıca 'depends-on'
// ile bir bean'in BAŞKA bir bean'den ÖNCE kurulmasını garanti ederiz.
// Çalıştırma: java Ornek3.java
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Ornek3 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext()) {

            // 1) "altyapi" bean'i — başkalarının ona bağımlı olduğu temel bileşen.
            ctx.registerBeanDefinition("altyapi",
                    BeanDefinitionBuilder.genericBeanDefinition(Altyapi.class).getBeanDefinition());

            // 2) "uygulama" bean'i — constructor argümanı + depends-on ile tanımlanır.
            //    depends-on: Spring, 'uygulama'yı kurmadan ÖNCE 'altyapi'yı kurar.
            ctx.registerBeanDefinition("uygulama",
                    BeanDefinitionBuilder.genericBeanDefinition(Uygulama.class)
                            .addConstructorArgValue("Sipariş Servisi")
                            .addDependsOn("altyapi")
                            .getBeanDefinition());

            ctx.refresh(); // tüm tanımlar bitti; kuruluş bu anda gerçekleşir

            System.out.println("\n" + ctx.getBean("uygulama"));
            System.out.println("""

                    --- Programatik tanımın gücü ---
                    * BeanDefinitionBuilder: bean'leri çalışma zamanında, döngüyle, koşullu üretebilirsin.
                    * addConstructorArgValue / addPropertyValue: bağımlılıkları kodla bağla.
                    * addDependsOn: kuruluş SIRASINI garanti et (önce altyapı, sonra uygulama).
                    Spring Boot'un otomatik yapılandırması da perde arkasında bunu yoğun kullanır.""");
        }
    }
}

class Altyapi {
    Altyapi() { System.out.println("[1] Altyapi kuruldu (önce)."); }
}

class Uygulama {
    private final String ad;
    Uygulama(String ad) { this.ad = ad; System.out.println("[2] Uygulama kuruldu (sonra): " + ad); }
    public String toString() { return "Uygulama(ad=" + ad + ") hazır."; }
}
