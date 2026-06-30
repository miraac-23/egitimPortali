// Ornek3: @Around advice — metodu SARAR; öncesini/sonrasını kontrol eder, hatta engelleyebilir.
// Gerçek senaryo: süre ölçümü + yetki (authorization) kontrolü, tek bir aspect'te.
// Çalıştırma: portal bu dosyayı derleyip Spring classpath'iyle çalıştırır.
package com.egitim.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Ornek3 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(Config.class);
        BankaServisi banka = ctx.getBean(BankaServisi.class);

        System.out.println("--- Yetkili kullanıcı ---");
        Guvenlik.giris("admin");
        banka.paraTransfer(500);

        System.out.println("\n--- Yetkisiz kullanıcı ---");
        Guvenlik.giris("misafir");
        banka.paraTransfer(1000); // @Around bunu ENGELLER

        ctx.close();

        System.out.println("""

                --- @Around neden güçlü? ---
                @Around, metodu SARAR: proceed() ÇAĞIRMADAN önce/sonra kod çalıştırır,
                hatta proceed()'i hiç çağırmayarak metodu ENGELLEYEBİLİR.
                Tek bir aspect'le hem süre ölçümü hem yetki denetimi yaptık; iş mantığı tertemiz kaldı.
                Spring'de @Transactional, @Cacheable, method security — hepsi @Around mantığıyla çalışır.""");
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean BankaServisi bankaServisi() { return new BankaServisi(); }
        @Bean DenetimAspect denetimAspect() { return new DenetimAspect(); }
    }

    // İş mantığı tertemiz: ne süre ölçümü ne yetki kontrolü içerir.
    public static class BankaServisi {
        public void paraTransfer(int tutar) {
            System.out.println("   -> " + tutar + " TL transfer edildi.");
        }
    }

    // Basit bir "oturum" — gerçekte SecurityContext olurdu.
    public static class Guvenlik {
        static String kullanici = "anonim";
        static void giris(String k) { kullanici = k; System.out.println("(giriş: " + k + ")"); }
    }

    @Aspect
    public static class DenetimAspect {
        @Around("bean(bankaServisi)")
        Object denetle(ProceedingJoinPoint pjp) throws Throwable {
            // 1) ÖNCE: yetki kontrolü — yetkisizse metodu hiç çalıştırma.
            if (!"admin".equals(Guvenlik.kullanici)) {
                System.out.println("[GÜVENLİK] erişim reddedildi (kullanıcı: " + Guvenlik.kullanici + ")");
                return null; // proceed() çağrılmaz -> metot ENGELLENDİ
            }
            // 2) Süre ölç ve metodu çalıştır.
            long t0 = System.nanoTime();
            Object sonuc = pjp.proceed(); // asıl metodu çağır
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("[ÖLÇÜM] " + pjp.getSignature().getName() + " " + ms + " ms sürdü.");
            return sonuc;
        }
    }
}
