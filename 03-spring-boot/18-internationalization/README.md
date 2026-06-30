# Uluslararasılaştırma (i18n)

Uygulaman birden çok dilde kullanıcıya hitap ediyorsa, metinleri koda gömmek yerine **dile göre
dışarıdan** sağlamalısın. **i18n (internationalization — i + 18 harf + n)**, uygulamayı farklı dil
ve bölgelere uyarlayabilir hale getirme işidir. Spring Boot bunu `MessageSource` ve dosya tabanlı
mesaj paketleriyle (`messages_xx.properties`) destekler.

## MessageSource ve mesaj paketleri

Metinleri **anahtar → dile göre değer** olarak tutarsın. Gerçek projede bunlar dosyalardadır:

```properties
# messages.properties (varsayılan)
selam=Hello, {0}!
# messages_tr.properties
selam=Merhaba, {0}!
# messages_en.properties
selam=Hello, {0}!
```

`{0}`, `{1}` yer tutucuları çalışma anında doldurulur. Koddan:

```java
messageSource.getMessage("selam", new Object[]{ad}, locale);
```

> **Not:** Bu portal tek dosya çalıştırır (`.properties` dosyaları yok); bu yüzden örnek, mesajları
> `StaticMessageSource` ile **programatik** ekler. Gerçek projede `messages_tr.properties` gibi
> dosyalar ve `ResourceBundleMessageSource` kullanılır (Spring Boot bunu otomatik yapılandırır).

## İsteğin dilini belirlemek: Locale

Hangi dilin kullanılacağını **`Locale`** belirler. Spring Boot varsayılan olarak
**`AcceptHeaderLocaleResolver`** kullanır: isteğin **`Accept-Language`** başlığına bakar. Bir
controller metoduna `Locale` parametresi eklersen, Spring onu otomatik enjekte eder:

```java
@GetMapping("/selam")
String selam(@RequestParam String ad, Locale locale) {
    return messageSource.getMessage("selam", new Object[]{ad}, locale);
}
```

Örnek 1 (`./Ornek1.java`) aynı ucun `Accept-Language: tr` ile "Merhaba, Ada!", `Accept-Language:
en` ile "Hello, Ada!" döndürdüğünü gösterir.

## Locale çözümleme stratejileri

`AcceptHeaderLocaleResolver` dışında:

- **`SessionLocaleResolver`**: Dili oturumda saklar (kullanıcı seçer).
- **`CookieLocaleResolver`**: Dili çerezde saklar.
- **`LocaleChangeInterceptor`**: `?lang=tr` parametresiyle dili değiştirmeye izin verir.

```java
@Bean LocaleResolver localeResolver() {
    var r = new SessionLocaleResolver();
    r.setDefaultLocale(Locale.of("tr"));
    return r;
}
```

## Nerede kullanılır?

- **Web UI metinleri** (Thymeleaf şablonlarında `#{anahtar}`).
- **Hata/doğrulama mesajları:** Bean Validation mesajları i18n'lenebilir (`{javax...}`).
- **REST API yanıtları:** Hata mesajlarını istemcinin diline göre döndürme.
- **Tarih/sayı/para biçimleri:** `Locale`'e göre biçimlendirme (topic 88'deki `DateTimeFormatter`,
  `NumberFormat`).

## İyi uygulamalar

- Metinleri **asla koda gömme**; hepsi mesaj paketlerinde olsun (çevirmen koda dokunmasın).
- Varsayılan bir dil/paket (`messages.properties`) bulundur (eksik çeviri için yedek).
- Yer tutucu sırasına dikkat (`{0}` farklı dillerde farklı yerde olabilir).
- UTF-8 kullan (Türkçe/özel karakterler — topic 87).

## Özet

i18n ile uygulamayı çok dilli yapmayı öğrendik: `MessageSource` ve mesaj paketleri (anahtar→dile
göre değer, `{0}` yer tutucular); isteğin dilini `Accept-Language` ile çözen `Locale` ve
controller'a otomatik enjeksiyonu (Örnek 1); Locale çözümleme stratejileri (session/cookie/param)
ve iyi uygulamalar. Bununla yapılandırma & temel web batch'i tamamlandı. Sırada — sonraki turda —
test/güvenlik/DB derinleştirme ve mikroservis/cloud konuları.
