# API Gateway (Spring Cloud Gateway)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Cloud Gateway + birden çok servis
> gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Onlarca mikroservisin olduğu bir sistemde, istemci (web/mobil) her servisin adresini bilmek ve her
biriyle ayrı ayrı konuşmak zorunda kalmamalıdır. **API Gateway**, tüm sisteme **tek giriş kapısıdır**:
istemci yalnızca gateway'i bilir; gateway gelen isteği arkadaki doğru servise **yönlendirir** ve
ortak işleri (kimlik, hız sınırı, loglama, CORS) tek yerde halleder. Spring'in çözümü **Spring Cloud
Gateway**'dir.

## Neden gateway?

Gateway olmadan istemci her servisle ayrı konuşur — adresleri bilmek, her serviste kimlik/CORS/log
tekrarı, güvenlik dağınıklığı. Gateway ile:

```
İstemci ──► [API Gateway] ──┬──► Sipariş Servisi
   (tek adres)              ├──► Ödeme Servisi
                            └──► Kargo Servisi
   (kimlik, hız sınırı, log, CORS, yönlendirme hepsi gateway'de)
```

## Kurulum ve yönlendirme (routing)

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
```

```yaml
# application.yml — bildirimsel route tanımı
spring:
  cloud:
    gateway:
      routes:
        - id: siparis-route
          uri: lb://siparis-servisi          # lb:// -> Eureka'dan yük dengeli çözüm
          predicates:
            - Path=/api/siparis/**            # bu yola gelen istekler
          filters:
            - StripPrefix=1                   # /api'yi at, servise /siparis/** olarak gönder
        - id: odeme-route
          uri: lb://odeme-servisi
          predicates:
            - Path=/api/odeme/**
```

- **Route:** Bir kural — "şu koşula uyan istek, şu servise gitsin".
- **Predicate (koşul):** Path, Method, Header, Host, zaman, query... ("ne zaman eşleşir").
- **Filter:** İstek/yanıtı dönüştürme — başlık ekle/çıkar, prefix at, yeniden yaz, hız sınırı,
  yetki ("eşleşince ne yap").
- **`lb://servis-adi`:** Eureka ile entegre; servis adını yük dengeli çözer (topic 23-24).

## Java ile route (RouteLocator)

YAML yerine programatik:

```java
@Bean
RouteLocator routes(RouteLocatorBuilder b) {
    return b.routes()
        .route("siparis", r -> r.path("/api/siparis/**")
            .filters(f -> f.stripPrefix(1).addRequestHeader("X-Gateway", "true"))
            .uri("lb://siparis-servisi"))
        .build();
}
```

## Gateway'in üstlendiği ortak (cross-cutting) işler

Tek yerde, tüm servisler için:

- **Kimlik/yetki:** JWT doğrulama gateway'de yapılır; arkadaki servisler güvenli ağda kalır.
- **Hız sınırlama (rate limiting):** İstemci başına istek sınırı (Redis ile).
- **CORS:** Tek noktada (topic 13).
- **Loglama / tracing:** Her isteğe korelasyon kimliği (topic 12).
- **Devre kesici (circuit breaker):** Yavaş/çöken servise yönlendirmeyi kes, fallback dön.
- **Yeniden yazma (rewrite), yük dengeleme, retry.**

## Reaktif temel

Spring Cloud Gateway, **reaktif** (Spring WebFlux + Netty) üzerine kuruludur — bloklamayan,
yüksek eşzamanlılıklı. Bu yüzden gateway projesinde `spring-boot-starter-web` (Tomcat) değil,
WebFlux bağımlılığı kullanılır.

## Modern bağlam

- **Kubernetes/Service Mesh:** K8s ortamlarında giriş (ingress) ve yönlendirme genelde **Ingress
  Controller** (NGINX, Traefik) veya **service mesh** (Istio) ile de yapılır; Spring Cloud Gateway
  uygulama-seviyesi gateway olarak yine kullanılır.
- Eski **Zuul** (Netflix) yerine artık Spring Cloud Gateway önerilir.

## Özet

API Gateway'in tüm mikroservis sistemine tek giriş kapısı olduğunu; **route + predicate + filter**
ile yönlendirmeyi (YAML ve programatik), Eureka ile `lb://` entegrasyonunu ve gateway'in üstlendiği
ortak işleri (kimlik, hız sınırı, CORS, tracing, circuit breaker) öğrendik; reaktif temeline ve
K8s/ingress bağlamına değindik. Sırada, tüm servislerin yapılandırmasını merkezden yönetme:
**Cloud Config Server**.
