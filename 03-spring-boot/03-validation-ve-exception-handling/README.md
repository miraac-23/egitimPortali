# Validation ve Global Exception Handling

Bir API'nin dış dünyaya bakan iki kritik sorumluluğu vardır: gelen veriyi **doğrulamak** ve hata
olduğunda istemciye **anlamlı, tutarlı** bir cevap dönmek. Spring Framework bölümünde Bean
Validation'ı öğrendik; şimdi onu bir web API'sinde `@Valid` ile otomatik çalıştırmayı ve tüm
hataları tek yerden, zarif JSON gövdeleriyle yönetmeyi (`@RestControllerAdvice`) görüyoruz.

## @Valid ile otomatik doğrulama

Spring MVC'de gelen JSON gövdesini bir DTO'ya bağlarken `@Valid` eklersen, Spring controller
metodunu çalıştırmadan **önce** kuralları kontrol eder. İhlal varsa
`MethodArgumentNotValidException` fırlar ve Spring Boot otomatik olarak **400 Bad Request** döner:

```java
record KullaniciDto(@NotBlank String ad, @Email String eposta, @Min(18) int yas) {}

@PostMapping("/api/kullanicilar")
public String kaydet(@Valid @RequestBody KullaniciDto dto) { ... } // geçersizse buraya HİÇ girilmez
```

Örnek 1 (`./Ornek1.java`) geçerli bir isteğin 200, geçersiz bir isteğin (boş ad, kötü e-posta,
yaş<18) 400 döndüğünü canlı gösterir. Geçersiz veri controller'a hiç ulaşmaz — ama Boot'un
**varsayılan** hata gövdesi kabadır ve API'ne özel değildir. Bunu düzeltmek için global bir hata
yöneticisi gerekir.

## @RestControllerAdvice ile global hata yönetimi

`@RestControllerAdvice`, tüm controller'lardaki istisnaları **tek bir yerde** yakalamanı sağlar.
Her istisna tipi için bir `@ExceptionHandler` yazar, istediğin durum kodu ve gövdeyi dönersin.
Böylece hata yanıtların tutarlı ve anlamlı olur:

```java
@RestControllerAdvice
class GlobalHataYonetimi {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> dogrulama(MethodArgumentNotValidException ex) {
        // alan bazlı temiz mesajlar -> 400
    }
    @ExceptionHandler(KaynakBulunamadiException.class)
    ResponseEntity<?> bulunamadi(KaynakBulunamadiException ex) {
        // anlamlı gövde -> 404
    }
}
```

Örnek 2 (`./Ornek2.java`) doğrulama hatalarını alan bazlı temiz bir JSON'a (400) ve özel bir
"kaynak bulunamadı" istisnasını anlamlı bir gövdeye (404) çevirir. Artık API genelinde hatalar
**aynı yapıda** döner — istemci için öngörülebilir bir sözleşme.

## Kısa yol ve standart: @ResponseStatus ve ProblemDetail

İki ek teknik:

- **`@ResponseStatus`**: Bir istisna sınıfına doğrudan HTTP kodu iliştirirsin; o istisna
  fırladığında Boot o kodu döner — advice yazmana bile gerek kalmaz (basit durumlar için hızlı).
  ```java
  @ResponseStatus(HttpStatus.CONFLICT) class StokYetersizException extends RuntimeException {}
  ```
- **`ProblemDetail`** (RFC 7807, Spring 6+): Hata gövdeleri için **standart**, makine-okunur bir
  biçim (`type`, `title`, `status`, `detail` + özel alanlar). Mikroservislerde tutarlı hata
  sözleşmesi için idealdir.
  ```java
  ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, mesaj);
  pd.setTitle("Sipariş bulunamadı");
  ```

Örnek 3 (`./Ornek3.java`) her ikisini de gösterir.

## İyi uygulama notları

- **DTO'ları doğrula, entity'leri değil:** İstek gövdesi için ayrı DTO kullan; entity'leri
  doğrudan dışarı açma.
- **İç içe nesneler:** `@Valid`'i iç içe alanlara da koyarsan derinlemesine doğrulanır.
- **Tutarlı hata sözleşmesi:** Tüm endpoint'ler aynı hata yapısını dönsün (advice veya
  ProblemDetail ile). İstemci tek bir formatı işler.
- **Hassas bilgi sızdırma:** Hata gövdesinde stack trace / iç ayrıntı döndürme (üretimde).

## Özet

`@Valid` ile gelen veriyi otomatik doğrulamayı ve geçersiz isteklerin 400 döndüğünü (Örnek 1),
`@RestControllerAdvice` ile tüm hataları tek yerden tutarlı JSON'a çevirmeyi (Örnek 2) ve
`@ResponseStatus`/`ProblemDetail` ile kısa yol ve standart hata gövdelerini (Örnek 3) canlı bir
API üzerinde gördük. API'nin giriş ve çıkış kapıları artık sağlam. Sırada, uygulamayı yetkisiz
erişime karşı koruyan kritik konu: **Spring Security**.
