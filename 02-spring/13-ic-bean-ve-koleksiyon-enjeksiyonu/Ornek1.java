// Ornek1: İÇ BEAN (inner bean) — yalnızca tek bir bean'e ait, paylaşılmayan bağımlılık.
// Bazen bir bağımlılık SADECE tek bir bean tarafından kullanılır ve başka kimsenin
// erişmesine gerek yoktur. Onu container'a ayrı bir bean olarak kaydetmek yerine, sahibinin
// İÇİNDE anonim olarak yaratırsın. (XML'de <bean> içine gömülü <bean>; Java'da new ile.)
// Çalıştırma: java Ornek1.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Ornek1 {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Config.class)) {

            System.out.println(ctx.getBean("arabaPaylasilan", Araba.class));
            System.out.println(ctx.getBean("arabaIcBean", Araba.class));

            // Container'da TİPİ Motor olan KAÇ bean var? Sadece paylaşılan olan.
            System.out.println("\nContainer'daki Motor bean'leri: "
                    + java.util.Arrays.toString(ctx.getBeanNamesForType(Motor.class)));

            System.out.println("""

                    --- İç bean nedir? ---
                    'arabaIcBean'in motoru (Elektrik) bir İÇ BEAN'dir: sahibinin içinde yaratıldı,
                    container'a ayrı kaydEDİLMEDİ. Bu yüzden Motor tipinde yalnızca 'paylasilanMotor'
                    görünüyor; elektrik motoruna dışarıdan ne isimle ne de getBean ile erişilebilir.
                    Ne zaman? Bağımlılık tek bir yere ait ve paylaşılmayacaksa — kapsülleme artar.""");
        }
    }
}

class Motor {
    final String tip;
    Motor(String tip) { this.tip = tip; }
}

class Araba {
    final Motor motor;
    Araba(Motor motor) { this.motor = motor; }
    public String toString() { return "Araba(motor=" + motor.tip + ")"; }
}

@Configuration
class Config {
    // PAYLAŞILAN (top-level) bean: başkaları da enjekte edebilir, isimle erişilebilir.
    @Bean Motor paylasilanMotor() { return new Motor("V8"); }

    @Bean Araba arabaPaylasilan() { return new Araba(paylasilanMotor()); }

    // İÇ BEAN: motor doğrudan içeride 'new' ile yaratıldı; container'a kaydedilmedi.
    @Bean Araba arabaIcBean() { return new Araba(new Motor("Elektrik")); }
}
