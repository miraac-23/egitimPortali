# Application Properties ve Yapılandırma

Bir uygulamanın davranışını koda gömmek yerine **dışarıdan yapılandırmak**, profesyonel
geliştirmenin temelidir: veritabanı adresi, port, özellik bayrakları, dış servis anahtarları...
Aynı kod farklı ortamlarda (geliştirme/test/üretim) farklı ayarlarla çalışır. Spring Boot bunu
`application.properties`/`application.yml` dosyaları ve güçlü bir bağlama mekanizmasıyla sağlar.

> **Not:** Bu portal örnekleri tek dosya çalıştırır (gerçek `application.properties` yoktur). Bu
> yüzden değerleri `SpringApplicationBuilder.properties(...)` ile programatik veriyoruz; gerçek
> projede bunlar `application.properties`/`yml` dosyasından gelir. Davranış aynıdır.

## Yapılandırma kaynakları

Spring Boot ayarları birçok kaynaktan, bir **öncelik sırasıyla** okur (üstteki alttakini ezer):

1. Komut satırı argümanları (`--server.port=9090`)
2. Ortam değişkenleri (`SERVER_PORT=9090`)
3. `application-{profil}.properties` (profil-özel)
4. `application.properties` / `application.yml`
5. Koddaki varsayılanlar

`application.properties` (anahtar=değer) ve `application.yml` (hiyerarşik) en yaygınıdır:

```properties
uygulama.ad=Eğitim Portalı
uygulama.surum=2.1
uygulama.ozellikler=arama,kod-calistirma,tema
server.port=8080
```

## İki bağlama yolu

### @Value — tekil değer

Tek bir ayarı doğrudan bir alana enjekte eder (varsayılanla):

```java
@Value("${mesaj.karsilama:merhaba}")   // 'karsilama' yoksa 'merhaba'
private String karsilama;
```

### @ConfigurationProperties — tipli, gruplu bağlama (önerilen)

Bir önek (`uygulama.*`) altındaki tüm ayarları bir **nesneye/record'a** bağlar; tip dönüşümünü
(string→`int`/`boolean`/`List`) otomatik yapar:

```java
@ConfigurationProperties(prefix = "uygulama")
record UygulamaAyarlari(String ad, String surum, List<String> ozellikler, boolean bakim) {}
```

Örnek 1 (`./Ornek1.java`) her iki yolu gösterir: virgüllü string `List<String>`'e, "true" `boolean`'a
otomatik dönüşür. `@ConfigurationProperties`, çok sayıda ilişkili ayar için **tercih edilen**
yoldur (tipli, doğrulanabilir, IDE otomatik tamamlamalı).

## Profiller (ortamlar)

Farklı ortamlar için farklı ayar dosyaları:

```
application.properties            # ortak
application-dev.properties        # geliştirme
application-prod.properties       # üretim
```

Aktif profil: `--spring.profiles.active=prod` (veya `SPRING_PROFILES_ACTIVE=prod`). `@Profile("dev")`
ile bean'ler profile göre koşullu yüklenir. (Spring profilleri topic 02-spring/05'te de işlendi.)

## İyi uygulamalar

- **Sırları koda/git'e gömme:** Parolalar, API anahtarları ortam değişkeni veya secret yöneticisi
  (Vault, K8s Secrets) ile gelsin. `application.properties`'e yazma.
- **`@ConfigurationProperties` + doğrulama:** Bean Validation anotasyonlarıyla (`@NotNull`,
  `@Min`) ayarları başlangıçta doğrula (`@Validated`).
- **YAML hiyerarşi:** Çok seviyeli ayarlarda `.yml` daha okunaklıdır.
- **`@ConfigurationProperties` tercih et** `@Value` yerine (gruplama, tip güvenliği, test
  kolaylığı).

## Özet

Uygulamayı dışarıdan yapılandırmayı öğrendik: yapılandırma kaynakları ve öncelik sırası; tekil
`@Value` ve tipli/gruplu `@ConfigurationProperties` bağlama (otomatik tip dönüşümüyle; Örnek 1);
profillerle ortam-bazlı ayar ve sır yönetimi gibi iyi uygulamalar. Sırada, uygulama davranışını
izlemenin temeli: **logging**.
