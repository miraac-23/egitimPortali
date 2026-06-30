// Ornek3: GERÇEK HAYAT — BeanPostProcessor ile otomatik PROXY sarmalama (AOP'nin temeli).
// Spring AOP, @Transactional, @Cacheable gibi özelliklerin HEPSİ aynı fikre dayanır:
// bir BeanPostProcessor, init SONRASINDA bean'i bir PROXY ile sarmalar; çağrılar önce
// proxy'den geçer (loglama, ölçüm, transaction...) sonra gerçek bean'e ulaşır.
// Bu örnek, o mekanizmayı sıfırdan, JDK dinamik proxy ile gösterir.
// Çalıştırma: java Ornek3.java
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

public class Ornek3 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {
            // Container'dan aldığımız nesne GERÇEK Hesaplayici değil, onu saran PROXY'dir.
            Hesaplayici h = ctx.getBean(Hesaplayici.class);
            System.out.println("Dönen nesne proxy mi? " + Proxy.isProxyClass(h.getClass()));
            System.out.println("Sonuç: " + h.topla(7, 5));
            System.out.println("""

                    --- Ne oldu? ---
                    BeanPostProcessor, Hesaplayici bean'ini init SONRASINDA bir proxy ile sardı.
                    'topla' çağrısı önce proxy'ye gitti (log + süre ölçümü), sonra gerçek nesneye.
                    İŞ MANTIĞINA HİÇ DOKUNMADAN davranış ekledik. Spring AOP tam olarak budur.""");
        }
    }
}

interface Hesaplayici {
    int topla(int a, int b);
}

class BasitHesaplayici implements Hesaplayici {
    public int topla(int a, int b) { return a + b; }
}

// Hesaplayici uygulayan bean'leri, çağrıları loglayan bir proxy ile saran post-processor.
class LoglayanProxyPostProcessor implements BeanPostProcessor {
    public Object postProcessAfterInitialization(Object bean, String ad) {
        if (bean instanceof Hesaplayici) {
            return Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    new Class<?>[]{Hesaplayici.class},
                    (proxy, method, args) -> {
                        long t0 = System.nanoTime();
                        System.out.println("[proxy] " + method.getName() + " çağrıldı, argümanlar="
                                + java.util.Arrays.toString(args));
                        Object sonuc = method.invoke(bean, args); // gerçek bean'e devret
                        System.out.println("[proxy] " + method.getName() + " bitti ("
                                + (System.nanoTime() - t0) / 1000 + " µs)");
                        return sonuc;
                    });
        }
        return bean;
    }
}

@Configuration
class Config {
    // post-processor @Bean metodu 'static' olmalı (container yaşam döngüsü uyarısını önler).
    @Bean static LoglayanProxyPostProcessor loglayanProxyPostProcessor() { return new LoglayanProxyPostProcessor(); }
    @Bean Hesaplayici hesaplayici() { return new BasitHesaplayici(); }
}
