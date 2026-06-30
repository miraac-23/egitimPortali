// Ornek3: Reflection + özel annotation'larla MİNİ bir doğrulama (validation) framework'ü.
// Spring/Hibernate'in @NotBlank, @Min, @Size gibi anotasyonlarını @Valid ile nasıl
// işlediğinin küçük bir benzeri.
// Çalıştırma: java Ornek3.java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Ornek3 {

    public static void main(String[] args) throws IllegalAccessException {
        KullaniciFormu gecerli = new KullaniciFormu("Ada", "ada@site.com", 30, "guclu-parola");
        KullaniciFormu hatali = new KullaniciFormu("", "gecersiz-eposta", 15, "123");

        System.out.println("Geçerli form -> ihlaller: " + Dogrulayici.dogrula(gecerli));
        System.out.println("\nHatalı form -> ihlaller:");
        for (String ihlal : Dogrulayici.dogrula(hatali)) {
            System.out.println("  - " + ihlal);
        }
    }
}

// --- Annotation'lar (kurallar) ---
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface NotBlank { String mesaj() default "boş olamaz"; }

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface Min { int deger(); }

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface Length { int min() default 0; int max() default Integer.MAX_VALUE; }

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface Email {}

// --- Doğrulanacak veri sınıfı (anotasyonlarla işaretli) ---
class KullaniciFormu {
    @NotBlank(mesaj = "ad zorunludur")
    String ad;

    @NotBlank @Email
    String eposta;

    @Min(deger = 18)
    int yas;

    @Length(min = 8, max = 32)
    String parola;

    KullaniciFormu(String ad, String eposta, int yas, String parola) {
        this.ad = ad; this.eposta = eposta; this.yas = yas; this.parola = parola;
    }
}

// --- Doğrulayıcı: anotasyonları reflection ile okuyup kuralları uygular ---
class Dogrulayici {
    static List<String> dogrula(Object nesne) throws IllegalAccessException {
        List<String> ihlaller = new ArrayList<>();
        for (Field f : nesne.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object deger = f.get(nesne);

            if (f.isAnnotationPresent(NotBlank.class)) {
                if (deger == null || deger.toString().isBlank()) {
                    ihlaller.add(f.getName() + ": " + f.getAnnotation(NotBlank.class).mesaj());
                }
            }
            if (f.isAnnotationPresent(Email.class) && deger != null) {
                if (!deger.toString().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
                    ihlaller.add(f.getName() + ": geçerli bir e-posta değil");
                }
            }
            if (f.isAnnotationPresent(Min.class) && deger instanceof Integer sayi) {
                int min = f.getAnnotation(Min.class).deger();
                if (sayi < min) ihlaller.add(f.getName() + ": en az " + min + " olmalı (mevcut " + sayi + ")");
            }
            if (f.isAnnotationPresent(Length.class) && deger != null) {
                Length l = f.getAnnotation(Length.class);
                int uz = deger.toString().length();
                if (uz < l.min() || uz > l.max()) {
                    ihlaller.add(f.getName() + ": uzunluk " + l.min() + ".." + l.max() + " arasında olmalı (mevcut " + uz + ")");
                }
            }
        }
        return ihlaller;
    }
}
