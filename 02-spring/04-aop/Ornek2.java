// Ornek2: ÇÖZÜM — AOP ile loglamayı bir Aspect'e taşımak (@Before / @After).
// İş mantığı artık TERTEMİZ; loglama tek bir yerde, otomatik uygulanıyor.
// Çalıştırma: portal bu dosyayı derleyip Spring classpath'iyle çalıştırır.
package com.egitim.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Ornek2 {

    // İş mantığı TERTEMİZ — hiçbir log/ölçüm kodu yok.
    // public static: Spring AOP'un proxy (CGLIB) üretebilmesi için sınıf erişilebilir olmalı.
    public static class SiparisServisi {
        public void siparisOlustur(String urun) { System.out.println("   -> sipariş kaydedildi: " + urun); }
        public void siparisIptal(int id) { System.out.println("   -> sipariş iptal edildi: #" + id); }
    }

    // Kesişen ilgi (loglama) tek bir Aspect'te toplandı.
    @Aspect
    public static class LoglamaAspect {
        // bean(...) pointcut'ı: "siparisServisi" bean'inin tüm metotları (paket adından bağımsız).
        @Before("bean(siparisServisi)")
        void once(JoinPoint jp) {
            System.out.println("[LOG] başlıyor: " + jp.getSignature().getName()
                    + " argümanlar=" + java.util.Arrays.toString(jp.getArgs()));
        }

        @After("bean(siparisServisi)")
        void sonra(JoinPoint jp) {
            System.out.println("[LOG] bitti  : " + jp.getSignature().getName());
        }
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class AopConfig {
        @Bean SiparisServisi siparisServisi() { return new SiparisServisi(); }
        @Bean LoglamaAspect loglamaAspect() { return new LoglamaAspect(); }
    }

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AopConfig.class);

        // Çağrı proxy üzerinden geçtiği için aspect otomatik devreye girer.
        SiparisServisi servis = ctx.getBean(SiparisServisi.class);
        servis.siparisOlustur("Klavye");
        servis.siparisIptal(42);

        ctx.close();

        System.out.println("""

                --- AOP terimleri ---
                Aspect    : kesişen ilgiyi içeren modül (LoglamaAspect).
                Advice    : ne zaman çalışacağı (@Before, @After, @Around...).
                Pointcut  : nerede çalışacağı (hangi metotlar).
                JoinPoint : advice'ın uygulandığı an/nokta (çağrılan metot).
                Sonuç: iş mantığı loglama kodundan tamamen arındı; loglama TEK yerde.""");
    }
}
