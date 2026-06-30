# Spring Boot CORS Desteği

Modern web uygulamalarında frontend (örn. `https://uygulamam.com`) ve backend API (örn.
`https://api.uygulamam.com`) genelde **farklı kökenlerde** (origin) çalışır. Tarayıcılar güvenlik
gereği, bir sayfanın **kendi kökeni dışındaki** bir API'ye JavaScript ile istek atmasını
varsayılan olarak engeller (Same-Origin Policy). **CORS (Cross-Origin Resource Sharing)**, sunucunun
"şu kökenlere izin veriyorum" demesini sağlayan mekanizmadır. Bu konu, Spring Boot'ta CORS'u nasıl
yapılandıracağını ele alır. (Bu portalın kendisi de React frontend `:5173` → backend `:8085` için
CORS kullanır.)

## CORS nasıl çalışır?

Tarayıcı çapraz-köken bir istek atarken `Origin` başlığı ekler. Sunucu, izin veriyorsa yanıta
**`Access-Control-Allow-Origin`** başlığını koyar; tarayıcı bu başlığı görüp yanıtı JavaScript'e
verir. Karmaşık istekler için tarayıcı önce bir **preflight** (`OPTIONS`) isteği gönderir ve
izinli metot/başlıkları sorar.

> **Önemli:** CORS bir **tarayıcı** mekanizmasıdır. Sunucudan sunucuya veya Postman/curl
> isteklerinde geçerli değildir (oralarda Same-Origin Policy yoktur). Yani CORS bir "güvenlik
> duvarı" değil, tarayıcıya "bu kökene izin var" diyen bir işarettir.

## Global CORS yapılandırması

Tüm uygulama için merkezi yapılandırma — `WebMvcConfigurer.addCorsMappings`:

```java
@Bean WebMvcConfigurer corsConfig() {
    return new WebMvcConfigurer() {
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedOrigins("https://uygulamam.com")   // izinli köken(ler)
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(true);                    // çerez/kimlik gönderimi
        }
    };
}
```

## Tekil CORS: @CrossOrigin

Belirli bir controller/metot için yerel CORS:

```java
@RestController
@CrossOrigin(origins = "https://uygulamam.com")
class ApiController { ... }
```

Örnek 1 (`./Ornek1.java`) hem global yapılandırmayı hem `@CrossOrigin`'i kurar; self-test, `Origin`
başlığı göndererek yanıttaki `Access-Control-Allow-Origin` başlığını okur.

## Güvenlik uyarıları

- **`allowedOrigins("*")` dikkat:** Herkese açmak, kötü amaçlı sitelerin API'ne tarayıcıdan istek
  atmasına izin verir. Üretimde **kesin köken listesi** ver.
- **`allowCredentials(true)` + `*` birlikte olmaz:** Kimlik (çerez/Authorization) gönderiliyorsa
  joker köken yasaktır; kökenleri açıkça belirtmelisin (`allowedOriginPatterns` ile desen
  verilebilir).
- **Spring Security ile:** Security devredeyse CORS'u Security yapılandırmasında da etkinleştirmen
  gerekir (`http.cors(...)`), aksi halde filter zinciri isteği CORS'tan önce reddedebilir.

## Yapılandırma öncelikleri

- Tüm API aynı politikayı paylaşıyorsa → **global** (`WebMvcConfigurer`).
- Yalnızca birkaç uç farklıysa → **`@CrossOrigin`**.
- İkisi birlikte kullanılabilir; tekil ayar ilgili uçta geçerli olur.

## Özet

CORS'un tarayıcının çapraz-köken güvenlik politikası (Same-Origin) için sunucunun verdiği izin
mekanizması olduğunu; `Access-Control-Allow-Origin` akışını; global (`WebMvcConfigurer`) ve tekil
(`@CrossOrigin`) yapılandırmayı (Örnek 1) ve güvenlik tuzaklarını (`*` + credentials, Security ile
entegrasyon) öğrendik. Bu, frontend-backend ayrı çalışan her modern uygulamada gereklidir. Bununla
Spring Boot derinleştirmesinin ilk batch'i (web ara katmanları) tamamlandı.
