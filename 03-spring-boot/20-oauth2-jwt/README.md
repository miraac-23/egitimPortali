# Spring Boot OAuth2 ve JWT

Modern API'ler **durumsuz (stateless)** kimlik doğrulama kullanır: sunucu oturum (session) tutmaz;
istemci her istekte kim olduğunu kanıtlayan bir **token** taşır. Bu token genelde bir **JWT (JSON
Web Token)**'dır ve **OAuth2** akışlarının merkezindedir. Bu konu, JWT'nin nasıl çalıştığını
çalışan bir örnekle gösterir ve OAuth2'nin daha geniş resmini açıklar. (Temel güvenlik — BCrypt,
roller — topic 04'tedir; bu konu token tabanlı durumsuz kimliğe odaklanır.)

## JWT nedir?

JWT, üç parçadan oluşan, imzalı bir token'dır: `header.payload.signature` (Base64URL kodlu, topic 71):

- **Header:** Algoritma (örn. HS256).
- **Payload (claims):** Veri — `sub` (kim), `exp` (son kullanma), roller, özel iddialar.
- **Signature:** Gizli anahtarla imza. Token **kurcalanırsa** imza doğrulaması bozulur.

JWT **şifreli değildir, imzalıdır** — içeriği herkes okuyabilir (Base64), ama **değiştiremez**
(imza). Bu yüzden hassas veri koyma; sadece kimlik/yetki bilgisi.

## JWT üretme ve doğrulama (jjwt)

```java
SecretKey anahtar = Keys.hmacShaKeyFor(gizliByte);   // HS256 için >=256 bit

// Üret (login sırasında)
String token = Jwts.builder()
    .subject("ada").claim("roller", List.of("USER"))
    .issuedAt(new Date()).expiration(new Date(now + 3600_000))
    .signWith(anahtar).compact();

// Doğrula (her istekte)
Claims c = Jwts.parser().verifyWith(anahtar).build()
    .parseSignedClaims(token).getPayload();   // imza + süre geçersizse exception
c.getSubject();
```

Örnek 1 (`./Ornek1.java`) bir `/login` (token üretir) ve korumalı `/profil` (token doğrular) ucu
kurar; self-test: login → token ile erişim (200), token olmadan ve sahte token ile **reddedilir
(401)**.

## Akış (bearer token)

```
1. POST /login (kullanıcı/parola)  ->  sunucu JWT üretir, döndürür
2. İstemci token'ı saklar (genelde bellek/secure storage)
3. Her istekte: Authorization: Bearer <token>
4. Sunucu token'ı doğrular (imza + süre) -> kim olduğunu bilir (DB'ye gitmeden, stateless)
```

## OAuth2: daha büyük resim

JWT bir token **formatıdır**; **OAuth2** ise token'ların nasıl alınıp kullanılacağını tanımlayan
bir **yetkilendirme çerçevesidir**. Roller:

- **Authorization Server:** Token üreten sunucu (Google, Auth0, Keycloak veya **Spring Authorization
  Server**).
- **Resource Server:** Token'ı doğrulayıp korunan kaynağı sunan API'n.
- **Client:** Token alıp kullanan uygulama.
- **Access token / Refresh token:** Kısa ömürlü erişim + uzun ömürlü yenileme; **scope**'lar
  yetki kapsamını belirler.

Gerçek Spring kurulumunda bunu elle yapmazsın; `spring-boot-starter-oauth2-resource-server` ile
birkaç satırda kurulur:

```java
// build.gradle: spring-boot-starter-oauth2-resource-server
@Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(a -> a.anyRequest().authenticated())
        .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));  // JWT'yi otomatik doğrular
    return http.build();
}
```

```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri: https://accounts.google.com
```

> **Not:** OAuth2 resource server ve Google Sign-In ek bağımlılık + dış sağlayıcı (Google/Auth0)
> gerektirir; bu portalda çalıştırılamaz. Örnek 1, JWT mekaniğini jjwt ile **gerçek çalıştırarak**
> gösterir; yukarıdaki Spring kurulumu üretimde tercih edilen yoldur.

## Güvenlik pratikleri

- **Gizli anahtarı koruma:** Ortam değişkeni/secret yöneticisi; koda gömme. Asimetrik (RS256)
  imzada özel anahtar yalnızca authorization server'da.
- **Kısa ömür + refresh:** Access token kısa (dakikalar), yenileme için refresh token.
- **HTTPS şart:** Token açık metinde taşınır; araya girme (MITM) için HTTPS zorunlu.
- **Hassas veri koyma:** Payload herkesçe okunur (sadece imzalı).
- **`exp` doğrula:** Süresi geçmiş token reddedilmeli (jjwt bunu otomatik yapar).

## Özet

Durumsuz kimlik için JWT'yi öğrendik: yapısı (header/payload/signature, imzalı ama şifresiz),
jjwt ile üretme/doğrulama ve bearer-token akışı (Örnek 1 — login→token→korumalı erişim, 401
senaryoları); OAuth2'nin rolleri (authorization/resource server, access/refresh token, scope) ve
Spring'in `oauth2-resource-server` ile gerçek kurulumu; güvenlik pratikleri. Sırada, Spring Boot'un
anotasyon ekosistemine genel bakış: **annotations**.
