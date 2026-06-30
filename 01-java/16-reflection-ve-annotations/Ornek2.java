// Ornek2: Reflection ile MİNİ bir IoC (Inversion of Control) container.
// Spring'in @Autowired/constructor injection sihrinin küçük bir benzeri:
// container, bir sınıfın constructor bağımlılıklarını otomatik bulup oluşturur.
// Çalıştırma: java Ornek2.java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Ornek2 {

    public static void main(String[] args) {
        MiniContainer container = new MiniContainer();
        // Arayüz -> uygulama eşlemelerini kaydet.
        container.kaydet(MesajGonderici.class, EpostaGonderici.class);
        container.kaydet(Depo.class, BellekDepo.class);
        container.kaydet(SiparisServisi.class, SiparisServisi.class);

        // Container, SiparisServisi'nin constructor'ındaki bağımlılıkları
        // (Depo, MesajGonderici) reflection ile çözüp otomatik enjekte eder.
        SiparisServisi servis = container.coz(SiparisServisi.class);
        servis.siparisOlustur("Klavye");
        servis.siparisOlustur("Mouse");

        System.out.println("\nNot: Spring'in IoC container'ı tam olarak bunu yapar (çok daha fazlasıyla).");
    }
}

// @Inject: hangi constructor'ın enjeksiyon için kullanılacağını işaretler.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@interface Inject {}

class MiniContainer {
    private final Map<Class<?>, Class<?>> eslemeler = new HashMap<>();

    void kaydet(Class<?> arayuz, Class<?> uygulama) {
        eslemeler.put(arayuz, uygulama);
    }

    // Bir tipi çözer: uygulamasını bulur, constructor bağımlılıklarını
    // özyinelemeli (recursive) çözer ve nesneyi oluşturur.
    @SuppressWarnings("unchecked")
    <T> T coz(Class<T> tip) {
        Class<?> uygulama = eslemeler.getOrDefault(tip, tip);
        try {
            Constructor<?> ctor = enjeksiyonCtor(uygulama);
            Class<?>[] paramTipleri = ctor.getParameterTypes();
            Object[] paramlar = new Object[paramTipleri.length];
            for (int i = 0; i < paramTipleri.length; i++) {
                paramlar[i] = coz(paramTipleri[i]); // her bağımlılığı özyinelemeli çöz
            }
            System.out.println("[container] oluşturuluyor: " + uygulama.getSimpleName());
            return (T) ctor.newInstance(paramlar);
        } catch (Exception e) {
            throw new RuntimeException("Çözülemedi: " + tip.getSimpleName(), e);
        }
    }

    // @Inject işaretli constructor varsa onu, yoksa ilk constructor'ı kullan.
    private Constructor<?> enjeksiyonCtor(Class<?> uygulama) {
        for (Constructor<?> ctor : uygulama.getDeclaredConstructors()) {
            if (ctor.isAnnotationPresent(Inject.class)) return ctor;
        }
        return uygulama.getDeclaredConstructors()[0];
    }
}

interface MesajGonderici { void gonder(String m); }
interface Depo { void kaydet(String k); }

class EpostaGonderici implements MesajGonderici {
    public void gonder(String m) { System.out.println("  [e-posta] " + m); }
}
class BellekDepo implements Depo {
    public void kaydet(String k) { System.out.println("  [depo] kaydedildi: " + k); }
}

class SiparisServisi {
    private final Depo depo;
    private final MesajGonderici gonderici;

    // Container bu constructor'ı bulup Depo ve MesajGonderici'yi enjekte eder.
    @Inject
    SiparisServisi(Depo depo, MesajGonderici gonderici) {
        this.depo = depo;
        this.gonderici = gonderici;
    }

    void siparisOlustur(String urun) {
        depo.kaydet(urun);
        gonderici.gonder("Siparişiniz alındı: " + urun);
    }
}
