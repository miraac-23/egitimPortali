# Doğrulama (Validation)

Bir uygulamanın en kırılgan sınırı, dış dünyadan veri aldığı yerdir: kullanıcı formları, API
istekleri, dosya içerikleri. "Yaş negatif olmasın", "e-posta geçerli olsun", "ad boş olmasın"
gibi kuralları her yerde elle `if/else` ile kontrol etmek hem yorucu hem de hataya açıktır.
**Bean Validation** (JSR-380), bu kuralları anotasyonlarla bildirimsel olarak tanımlamanı ve
tek bir mekanizmayla uygulamanı sağlar. Spring bunu derinlemesine destekler.

## Bildirimsel kurallar

Kuralları, doğrulanacak alanların üstüne anotasyon olarak koyarsın:

```java
class KullaniciKaydi {
    @NotBlank(message = "ad boş olamaz")   String ad;
    @Email                                  String eposta;
    @Min(18) @Max(120)                      int yas;
    @Size(min = 8)                          String parola;
}
```

En sık kullanılan kısıtlar: `@NotNull`, `@NotBlank`, `@NotEmpty`, `@Size`, `@Min`/`@Max`,
`@Email`, `@Pattern`, `@Positive`/`@Negative`, `@Past`/`@Future`. Doğrulamayı bir `Validator`
çalıştırır ve **tüm** ihlalleri tek seferde toplar (ilk hatada durmaz):

```java
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<KullaniciKaydi>> ihlaller = validator.validate(kayit);
```

Örnek 1 (`./Ornek1.java`) geçerli ve hatalı bir kayıt formunu doğrular ve hatalı olanın tüm
ihlallerini (alan + mesaj) listeler.

> Bean Validation bir **standarttır** (API); en yaygın uygulaması **Hibernate Validator**'dır.
> Spring Boot, `spring-boot-starter-validation` ile bunu hazır getirir.

## Özel (custom) kurallar

Standart kısıtlar yetmediğinde — "güçlü parola", "TC kimlik no", "IBAN", "ürün kodu formatı" gibi
domain kuralları — kendi anotasyonunu yazabilirsin. İki parça gerekir: `@Constraint` ile
işaretli bir **anotasyon** ve mantığı içeren bir **`ConstraintValidator`**:

```java
@Constraint(validatedBy = GucluParolaValidator.class)
@Target(FIELD) @Retention(RUNTIME)
@interface GucluParola { String message() default "..."; /* groups, payload */ }

class GucluParolaValidator implements ConstraintValidator<GucluParola, String> {
    public boolean isValid(String v, ConstraintValidatorContext c) { /* kural */ }
}
```

Artık `@GucluParola` tek anotasyonla her yerde kullanılır (DRY) ve standart `@Email` ile aynı
mekanizmaya dahildir. Örnek 2 (`./Ornek2.java`) bir "güçlü parola" kuralını uçtan uca yazar.

## Spring entegrasyonu: @Validated ve @Valid

Spring, doğrulamayı manuel `Validator` çağırmadan otomatikleştirir:

- **Metot doğrulaması:** Bir bean'i `@Validated` ile işaretler, metot parametrelerine kısıt
  koyarsan, Spring her çağrıda parametreleri otomatik doğrular ve ihlalde
  `ConstraintViolationException` fırlatır. Örnek 3 (`./Ornek3.java`) bunu gösterir
  (`MethodValidationPostProcessor` + `@Validated`).
- **Web/controller doğrulaması:** Spring MVC'de bir `@RequestBody` DTO'yu `@Valid` ile
  işaretlersin; Spring gelen JSON'u doğrular, hata varsa `MethodArgumentNotValidException` ile
  isteği **400 Bad Request**'e çevirir. Bunu Spring Boot bölümünde, gerçek bir REST endpoint'inde
  göreceğiz.

```java
@PostMapping("/kullanici")
public ... kayit(@Valid @RequestBody KullaniciKaydi k) { ... } // otomatik doğrulama
```

> `@Validated` ve metot doğrulaması proxy tabanlıdır (AOP); bu yüzden self-invocation tuzağı
> burada da geçerlidir. `@Valid` ise iç içe (nested) nesneleri de doğrulamak için kullanılır.

## Özet

Bean Validation ile kuralları anotasyonla bildirmeyi ve `Validator` ile tüm ihlalleri toplamayı
(Örnek 1), `@Constraint` + `ConstraintValidator` ile özel kurallar yazmayı (Örnek 2) ve
`@Validated` ile Spring'in metot parametrelerini otomatik doğrulamasını (Örnek 3) öğrendik. Bu,
uygulamanın "giriş kapısını" temiz ve güvenli tutmanın standart yoludur.

Bununla Spring'in temel uygulama katmanlarını (veri erişimi, transaction, validation) tamamlamış
olduk. Bundan sonraki bölümlerde (`09`–`16`) çekirdeğin daha derin ve klasik konularını —
mimari/kurulum, IoC container türleri, bean tanımı ve kalıtımı, post-processor'lar, iç bean ve
koleksiyon enjeksiyonu, auto-wiring, web MVC ve loglama — bütünleyici biçimde işliyoruz. Güvenlik
(Spring Security) bir web bağlamında çok daha anlamlı olduğundan, onu **Spring Boot** bölümünde
gerçek bir uygulama üzerinde ele alacağız. Sırada: **Spring mimarisi, kurulum ve Merhaba Dünya**.
