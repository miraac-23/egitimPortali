// Ornek2: Özel (custom) doğrulama kuralı — kendi @Constraint anotasyonun ve ConstraintValidator'ın.
// Gerçek senaryo: "güçlü parola" kuralını yeniden kullanılabilir bir anotasyona dönüştürmek.
// Çalıştırma: portal bu dosyayı derleyip Spring + hibernate-validator classpath'iyle çalıştırır.
package com.egitim.spring.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Ornek2 {

    // 1) Özel anotasyon: hangi validator'ın kontrol edeceğini @Constraint ile belirtiriz.
    @Constraint(validatedBy = GucluParolaValidator.class)
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GucluParola {
        String message() default "parola en az 8 karakter, bir büyük harf ve bir rakam içermeli";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // 2) Kuralın asıl mantığı: ConstraintValidator. (public: doğrulayıcı reflection ile örneklenir.)
    public static class GucluParolaValidator implements ConstraintValidator<GucluParola, String> {
        @Override
        public boolean isValid(String deger, ConstraintValidatorContext ctx) {
            if (deger == null) return false;
            boolean uzun = deger.length() >= 8;
            boolean buyukVar = deger.chars().anyMatch(Character::isUpperCase);
            boolean rakamVar = deger.chars().anyMatch(Character::isDigit);
            return uzun && buyukVar && rakamVar;
        }
    }

    // 3) Kullanım: artık tek anotasyonla uygulanır.
    static class Hesap {
        @GucluParola
        String parola;
        Hesap(String parola) { this.parola = parola; }
    }

    public static void main(String[] args) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        String[] denemeler = {"abc", "uzunamarakamyok", "Guclu123", "ZAYIF123"};
        for (String p : denemeler) {
            var ihlaller = validator.validate(new Hesap(p));
            String sonuc = ihlaller.isEmpty() ? "GEÇERLİ"
                    : "GEÇERSİZ (" + ihlaller.iterator().next().getMessage() + ")";
            System.out.printf("  %-18s -> %s%n", "'" + p + "'", sonuc);
        }

        System.out.println("""

                --- Özel kural ne kazandırır? ---
                'Güçlü parola' kuralını bir kez yazıp her yerde @GucluParola ile uygularsın (DRY).
                @Constraint + ConstraintValidator, standart anotasyonların (@Email vb.) aynı mekanizmasıdır;
                yani kendi domain kurallarını (TC kimlik, IBAN, ürün kodu...) standart sisteme eklersin.""");
    }
}
