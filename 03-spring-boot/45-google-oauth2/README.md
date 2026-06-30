# Google OAuth2 ile Giriş (Social Sign-In)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** `spring-boot-starter-oauth2-client` + Google'da
> kayıtlı bir OAuth uygulaması (client id/secret) gerektirir; portalda çalışmaz. Kod/yapı tanıtım
> amaçlıdır.

"Google ile Giriş Yap" düğmesini her yerde görürsün. Kullanıcılar uygulamana ayrı bir parola
oluşturmak yerine mevcut Google (veya GitHub/Facebook) hesabıyla giriş yapar. Bu, **OAuth2
Authorization Code** akışıdır ve uygulamanın parola tutma yükünü/riskini ortadan kaldırır. Spring
Security, bunu **`spring-boot-starter-oauth2-client`** ile neredeyse sıfır kodla sağlar. (OAuth2/JWT
temelleri için topic 20'ye bak; bu konu **sosyal giriş** tarafıdır.)

## OAuth2 Authorization Code akışı

```
1. Kullanıcı "Google ile giriş" tıklar
2. Uygulaman kullanıcıyı GOOGLE'ın giriş sayfasına yönlendirir
3. Kullanıcı Google'da giriş yapar + izin verir
4. Google, kullanıcıyı bir "authorization code" ile uygulamana geri yönlendirir
5. Uygulaman bu kodu Google'la token'a çevirir (arka planda, güvenli)
6. Token'dan kullanıcı bilgisi (e-posta, ad) alınır -> oturum açılır
```

Önemli: Uygulaman kullanıcının **parolasını asla görmez** — kimlik doğrulamayı Google yapar.

## Kurulum

```gradle
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
implementation 'org.springframework.boot:spring-boot-starter-security'
```

Önce Google Cloud Console'da bir **OAuth client** oluşturup `client-id`/`client-secret` ve izinli
yönlendirme URI'sini alırsın. Sonra:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:                          # Spring 'google' sağlayıcısını hazır tanır
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
```

## Güvenlik yapılandırması

```java
@Bean
SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(a -> a
            .requestMatchers("/", "/login**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(Customizer.withDefaults());   // "Google ile giriş" akışını otomatik kurar
    return http.build();
}
```

`oauth2Login()` sayesinde `/oauth2/authorization/google` adresine gitmek tüm akışı başlatır; Spring
yönlendirmeleri, kod-token değişimini ve oturumu yönetir.

## Kullanıcı bilgisine erişim

```java
@GetMapping("/profil")
public Map<String, Object> profil(@AuthenticationPrincipal OAuth2User kullanici) {
    return Map.of(
        "ad", kullanici.getAttribute("name"),
        "eposta", kullanici.getAttribute("email"));
}
```

## OAuth2 Login vs Resource Server

İki farklı OAuth2 rolünü karıştırma (topic 20):

| | OAuth2 **Login** (bu konu) | OAuth2 **Resource Server** (topic 20) |
|---|----------------------------|----------------------------------------|
| Ne yapar | Kullanıcıyı Google ile **giriş** yaptırır | Gelen **JWT'yi doğrular** (API koruması) |
| Bağımlılık | `oauth2-client` | `oauth2-resource-server` |
| Kullanım | Web uygulaması, sosyal giriş | Stateless API, mikroservis |

## Üretim pratikleri

- **Sırları koru:** `client-secret` ortam değişkeni/secret yöneticisinden.
- **Yönlendirme URI'leri:** Google Console'da üretim/dev URI'lerini doğru tanımla.
- **Birden çok sağlayıcı:** Aynı anda Google + GitHub + ... eklenebilir (her biri `registration`
  altında).
- **Kullanıcı eşleme:** Sosyal kimliği kendi kullanıcı tablona eşle (ilk girişte kayıt oluştur).

## Özet

Google OAuth2 ile sosyal girişi öğrendik: Authorization Code akışı (parolayı Google doğrular),
`spring-boot-starter-oauth2-client` + `oauth2Login()` ile neredeyse sıfır-kod kurulum, kullanıcı
bilgisine `@AuthenticationPrincipal OAuth2User` ile erişim; OAuth2 Login vs Resource Server farkı ve
üretim pratikleri. **Bununla Spring Boot bölümünün tamamı (verdiğin ~55 başlık) tamamlandı!**
