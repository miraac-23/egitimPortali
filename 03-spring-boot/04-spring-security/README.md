# Spring Security

Bir uygulamayı dünyaya açtığın an, "kimsin?" ve "ne yapmaya yetkilisin?" sorularını cevaplamak
zorundasın. **Spring Security**, kimlik doğrulama (authentication) ve yetkilendirme
(authorization) için Java dünyasının fiili standardıdır. Güçlüdür ama çok katmanlıdır; bu yüzden
adım adım, çalışan örneklerle ilerleyeceğiz: parola güvenliği ve temel kimlik doğrulamadan, rol
bazlı erişime ve modern token tabanlı (JWT) güvenliğe.

> İki kavramı baştan ayır: **authentication** "kim olduğunu" doğrular (401 = doğrulanmadın),
> **authorization** "neye yetkin olduğunu" denetler (403 = yetkin yok).

## Parola güvenliği ve kimlik doğrulama

İlk kural: **parolalar asla düz metin saklanmaz.** Tek yönlü, tuzlanmış (salted) bir hash
fonksiyonuyla saklanır. Spring'in önerdiği `BCryptPasswordEncoder` bunu yapar; ilginç biçimde
**aynı parola her hash'lemede farklı çıktı verir** (rastgele tuz), ama `matches` ile doğrulama
çalışır:

```java
PasswordEncoder enc = new BCryptPasswordEncoder();
String h1 = enc.encode("sifre123");
enc.matches("sifre123", h1); // true
```

Kimlik doğrulamayı bir `SecurityFilterChain` ile yapılandırırsın: hangi yollar açık, hangileri
korumalı; kimlik nasıl alınacak (HTTP Basic, form login...):

```java
@Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(a -> a
            .requestMatchers("/acik").permitAll()
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

Kullanıcılar bir `UserDetailsService`'ten gelir (örnekte bellek-içi; gerçekte veritabanından).
Örnek 1 (`./Ornek1.java`) BCrypt'i ve HTTP Basic ile korumayı gösterir: açık endpoint 200,
korumalı endpoint kimliksiz/yanlış parolayla **401**, doğru kimlikle **200** döner.

## Yetkilendirme: rol bazlı erişim

Kimliği doğrulanmış her kullanıcı her şeyi yapamamalı. Roller (ROLE_USER, ROLE_ADMIN) ile
yetkilendirme yaparsın:

```java
.authorizeHttpRequests(a -> a
    .requestMatchers("/yonetici").hasRole("ADMIN")
    .requestMatchers("/kullanici").hasAnyRole("USER", "ADMIN")
    .anyRequest().authenticated())
```

Örnek 2 (`./Ornek2.java`) iki kullanıcıyla (USER ve ADMIN) bunu gösterir: `ada` kullanıcı alanına
girer (200) ama yönetici alanına giremez (**403 Forbidden**); `admin` her ikisine de erişir.
Metot seviyesinde de `@PreAuthorize("hasRole('ADMIN')")` ile aynı denetim yapılabilir (proxy
tabanlı; AOP bölümündeki mantık).

## Token tabanlı güvenlik: JWT

Geleneksel oturum (session) tabanlı güvenlikte sunucu, giriş yapan kullanıcının durumunu bellekte
tutar. Mikroservis ve ölçeklenen sistemlerde bu zordur. **JWT (JSON Web Token)** durumsuz
(stateless) bir alternatif sunar: kullanıcı giriş yapınca sunucu **imzalı bir token** verir;
istemci sonraki her isteğinde bu token'ı `Authorization: Bearer <token>` başlığında taşır. Sunucu
oturum tutmaz; sadece token'ın imzasını doğrular.

Akış:
1. `POST /giris` — kimlik doğrula, başarılıysa imzalı bir JWT döndür.
2. Her istekte bir **filtre** `Bearer` token'ını okur, imzasını doğrular ve `SecurityContext`'e
   kimliği yerleştirir.
3. Korumalı endpoint, geçerli token olmadan **401** döner.

```java
String token = Jwts.builder().subject(kullanici)
        .expiration(new Date(now + 3600_000)).signWith(KEY).compact();
// doğrulama:
Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
```

Örnek 3 (`./Ornek3.java`) uçtan uca JWT akışını kurar: `/giris` token üretir, bir
`OncePerRequestFilter` token'ı doğrular, `SecurityFilterChain` durumsuz (`STATELESS`) yapılandırılır.
Self-test: giriş → token; token'sız `/profil` → reddedilir (kimlik yok); token ile → 200.

## Güvenlik filtre zinciri (filter chain) mantığı

Spring Security, gelen her isteği bir **filtre zincirinden** geçirir. Her filtre bir sorumluluk
üstlenir (kimlik çözme, yetki denetimi, CSRF, CORS...). Senin eklediğin JWT filtresi de bu
zincire katılır (`addFilterBefore`). İstek, controller'a ulaşmadan önce bu zincirden geçer; bu
yüzden güvenlik, iş mantığından tamamen ayrıdır.

## İyi uygulama notları

- Parolayı her zaman `BCrypt` (veya Argon2) ile hash'le; düz metin/MD5/SHA-1 kullanma.
- Üretimde kullanıcıları veritabanından (`UserDetailsService` implementasyonu) yükle.
- JWT secret'ını güvenli sakla (ortam değişkeni/secret yöneticisi), token'a kısa ömür ver,
  hassas veri koyma (JWT imzalıdır ama **şifreli değildir**, içeriği okunabilir).
- CSRF'i durum bilgisine göre yönet: stateless JWT API'lerde genelde kapatılır; form/oturum
  tabanlı uygulamalarda açık tutulur.

## Özet

Parola güvenliğini (BCrypt) ve kimlik doğrulamayı (Örnek 1), rol bazlı yetkilendirmeyi (Örnek 2)
ve durumsuz JWT akışını (Örnek 3) gerçek, çalışan uygulamalarla gördük; 401/403 farkını ve
güvenlik filtre zincirini öğrendik. Uygulaman artık koruma altında. Sırada, uygulamayı izlemek
ve üretime hazırlamak: **Actuator ve üretim araçları**.
