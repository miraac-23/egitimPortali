# Spring Boot'ta HTTPS Etkinleştirme

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** HTTPS bir TLS sertifikası/keystore gerektirir;
> portalda çalışmaz. Yapılandırma tanıtım amaçlıdır.

HTTP trafiği **açık metindir**: araya giren biri (ortadaki adam / MITM) parolaları, token'ları,
kişisel veriyi okuyabilir. **HTTPS** (HTTP over TLS), bu trafiği **şifreler** ve sunucunun kimliğini
doğrular. Üretimde her web uygulaması HTTPS kullanmalıdır (özellikle JWT/kimlik taşıyorsan — topic
20). Bu konu, Spring Boot'ta HTTPS'i nasıl etkinleştireceğini ele alır.

## TLS sertifikası ve keystore

HTTPS, bir **TLS sertifikası** gerektirir. Sertifika + özel anahtar bir **keystore** dosyasında
(PKCS12 `.p12` veya JKS) tutulur:

```bash
# Geliştirme için kendinden imzalı (self-signed) sertifika üret:
keytool -genkeypair -alias uygulama -keyalg RSA -keysize 2048 \
        -storetype PKCS12 -keystore keystore.p12 -validity 365
```

> Geliştirmede self-signed yeterli (tarayıcı uyarır). **Üretimde**, güvenilir bir otoriteden (CA)
> sertifika alınır — **Let's Encrypt** ücretsizdir ve otomatikleştirilebilir.

## Gömülü Tomcat'te HTTPS

`application.yml` ile gömülü sunucuda HTTPS açılır:

```yaml
server:
  port: 8443                          # HTTPS standart portu (genelde 443)
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}    # sırrı ortam değişkeninden
    key-store-type: PKCS12
    key-alias: uygulama
```

Artık uygulama `https://localhost:8443` üzerinden TLS ile çalışır.

## HTTP'yi HTTPS'e yönlendirme

Genelde HTTP (80) isteklerini HTTPS'e (443) **yönlendirmek** istersin. Bir
`WebServerFactoryCustomizer`/ek connector ile ya da bir filter ile sağlanır; veya bu iş gateway/
reverse proxy katmanına bırakılır.

## TLS sonlandırmayı (termination) nereye koymalı?

Üretimde HTTPS'i uygulamada bitirmek tek yol değildir — çoğu zaman **önündeki bir katman** yapar:

| Yer | Açıklama |
|-----|----------|
| Uygulama (gömülü Tomcat) | Basit/tek servis; sertifikayı uygulama yönetir |
| **Reverse proxy** (Nginx, HAProxy) | Yaygın: proxy TLS'i bitirir, arkaya HTTP konuşur |
| **API Gateway** (topic 25) | Mikroservislerde tek noktada TLS |
| **Load balancer / Ingress** (bulut/K8s) | AWS ALB, K8s Ingress — sertifikayı platform yönetir |

Mikroservis/bulut ortamlarında TLS genelde **kenar katmanında** (load balancer/ingress/gateway)
sonlandırılır; iç servisler arası trafik ayrı ele alınır (mTLS / service mesh).

## İyi uygulamalar

- **Sırları koru:** Keystore parolasını ortam değişkeni/secret yöneticisinden al; koda/git'e koyma.
- **Güçlü TLS:** Eski protokolleri (TLS 1.0/1.1) kapat; güçlü şifre takımları (cipher suites) kullan.
- **HSTS:** `Strict-Transport-Security` başlığıyla tarayıcıya "hep HTTPS kullan" de.
- **Let's Encrypt + otomatik yenileme:** Sertifikalar süreli; otomatik yenile (certbot vb.).

## Özet

HTTPS'in trafiği şifreleyip sunucu kimliğini doğruladığını; Spring Boot'ta gömülü Tomcat için
keystore + `server.ssl.*` yapılandırmasıyla HTTPS açmayı, self-signed (geliştirme) vs CA/Let's
Encrypt (üretim) sertifikalarını ve TLS sonlandırmanın nerede yapılacağını (uygulama vs reverse
proxy/gateway/ingress) öğrendik; HSTS/sır yönetimi gibi pratikler. Sırada, SMS/iletişim entegrasyonu:
**Twilio**.
