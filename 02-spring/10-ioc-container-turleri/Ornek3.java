// Ornek3: ApplicationContext'in BeanFactory'ye EKLEDİĞİ yetenekler.
// "Neden hep ApplicationContext kullanıyoruz?" sorusunun cevabı: BeanFactory sade bir
// IoC motorudur; ApplicationContext onun üstüne kurumsal yetenekler ekler. Bu örnek
// üç tanesini canlı gösterir: (a) olay yayını, (b) i18n MessageSource, (c) otomatik
// BeanPostProcessor kaydı (BeanFactory'de bunları elle bağlaman gerekirdi).
// Çalıştırma: java Ornek3.java
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

import java.util.Locale;

public class Ornek3 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(YetenekConfig.class)) {

            // (a) Olay yayını — ApplicationContext bir event bus'tır.
            ctx.publishEvent(new SiparisOlayi(ctx, "SP-100"));

            // (b) Uluslararasılaştırma (i18n) — mesajları Locale'e göre çöz.
            var ms = ctx.getBean(StaticMessageSource.class);
            System.out.println("TR: " + ms.getMessage("selam", null, Locale.of("tr")));
            System.out.println("EN: " + ms.getMessage("selam", null, Locale.ENGLISH));

            System.out.println("""

                    --- ApplicationContext'in BeanFactory'ye kattıkları ---
                    1) Olay yayını (publishEvent / @EventListener)
                    2) MessageSource ile i18n
                    3) ResourceLoader (classpath:/file:/URL kaynakları)
                    4) Environment (property/profil çözümü)
                    5) BeanPostProcessor & BeanFactoryPostProcessor'ları OTOMATİK kaydeder
                    Bu yüzden gerçek uygulamalarda DAİMA ApplicationContext kullanılır.""");
        }
    }
}

// Basit bir olay ve onu dinleyen bir bean (ApplicationContext event bus'ı çalışıyor).
class SiparisOlayi extends ApplicationEvent {
    final String no;
    SiparisOlayi(Object kaynak, String no) { super(kaynak); this.no = no; }
}

class SiparisDinleyici implements ApplicationListener<SiparisOlayi> {
    public void onApplicationEvent(SiparisOlayi e) {
        System.out.println("[dinleyici] Sipariş olayı alındı -> " + e.no);
    }
}

@Configuration
class YetenekConfig {
    @Bean SiparisDinleyici siparisDinleyici() { return new SiparisDinleyici(); }

    @Bean StaticMessageSource messageSource() {
        var ms = new StaticMessageSource();
        ms.addMessage("selam", Locale.of("tr"), "Merhaba!");
        ms.addMessage("selam", Locale.ENGLISH, "Hello!");
        return ms;
    }
}
