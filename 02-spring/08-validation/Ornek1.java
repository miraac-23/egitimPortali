// Ornek1: Bean Validation — kuralları anotasyonla bildir, Validator ile topluca kontrol et.
// Gerçek senaryo: bir kayıt formundaki tüm hataları tek seferde toplamak.
// Çalıştırma: portal Spring + hibernate-validator classpath'iyle çalıştırır.
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Ornek1 {

    // Kurallar, anotasyonlarla doğrudan alanların üstünde TANIMLANIR (bildirimsel).
    static class KullaniciKaydi {
        @NotBlank(message = "ad boş olamaz")
        String ad;

        @Email(message = "geçerli bir e-posta girin")
        String eposta;

        @Min(value = 18, message = "yaş en az 18 olmalı")
        @Max(value = 120, message = "yaş en fazla 120 olabilir")
        int yas;

        @Size(min = 8, message = "parola en az 8 karakter olmalı")
        String parola;

        KullaniciKaydi(String ad, String eposta, int yas, String parola) {
            this.ad = ad; this.eposta = eposta; this.yas = yas; this.parola = parola;
        }
    }

    public static void main(String[] args) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        var gecerli = new KullaniciKaydi("Ada", "ada@site.com", 30, "guclu-parola");
        var hatali = new KullaniciKaydi("", "gecersiz-eposta", 15, "123");

        System.out.println("Geçerli kayıt -> ihlal sayısı: " + validator.validate(gecerli).size());

        System.out.println("\nHatalı kayıt -> ihlaller:");
        // validate() TÜM ihlalleri tek seferde döndürür (ilk hatada durmaz).
        validator.validate(hatali).forEach(v ->
                System.out.println("  - " + v.getPropertyPath() + ": " + v.getMessage()));

        System.out.println("""

                --- Bean Validation (JSR-380) ---
                Kuralları if/else ile elle yazmak yerine anotasyonla bildirirsin: @NotBlank, @Email,
                @Min/@Max, @Size, @NotNull, @Pattern, @Positive...
                Validator tüm nesneyi gezip ihlalleri toplar. Spring MVC'de bu işi @Valid otomatik yapar
                ve hataları 400 Bad Request'e çevirir (Spring Boot bölümünde göreceğiz).""");
    }
}
