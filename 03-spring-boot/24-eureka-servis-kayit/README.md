# Eureka'ya Servis Kaydı ve Servisler Arası Çağrı

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Cloud + Eureka + birden çok servis
> gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Önceki konuda Eureka Server'ı (kayıt defteri) kurduk. Şimdi servisleri bu deftere **kaydetmeyi** ve
bir servisin başka bir servisi **adıyla** (IP yazmadan) çağırmasını ele alıyoruz.

## Servisi Eureka'ya kaydetmek (Eureka Client)

Her mikroservise Eureka istemcisi eklenir:

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
```

```yaml
# odeme-servisi'nin application.yml'i
spring:
  application:
    name: odeme-servisi          # bu ADLA kaydolur (önemli!)
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/   # Eureka Server adresi
```

Servis açılınca otomatik olarak Eureka'ya `ODEME-SERVISI` adıyla kaydolur ve düzenli heartbeat
gönderir. (Modern Spring Cloud'da `@EnableEurekaClient` artık gerekmez; bağımlılık yeter.)

## Başka servisi adıyla çağırmak

Artık IP/port değil, **servis adı** kullanılır. İki yaygın yol:

### 1) Load-balanced RestClient/RestTemplate

```java
@Bean
@LoadBalanced                        // servis adını Eureka'dan çözer + yük dengeler
RestClient.Builder restClientBuilder() { return RestClient.builder(); }

// Kullanım: host yerine SERVİS ADI
Odeme o = restClient.get()
    .uri("http://odeme-servisi/api/odeme/{id}", id)   // "odeme-servisi" -> Eureka çözer
    .retrieve().body(Odeme.class);
```

`@LoadBalanced` sayesinde `odeme-servisi` adı, Eureka'daki güncel örneklerden birine (yük
dengelenerek) yönlendirilir.

### 2) OpenFeign (bildirimsel istemci — çok yaygın)

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
```

```java
@FeignClient(name = "odeme-servisi")           // Eureka'daki servis adı
public interface OdemeIstemci {
    @GetMapping("/api/odeme/{id}")
    Odeme getir(@PathVariable Long id);          // sadece arayüz — gövde Feign tarafından üretilir
}

// Kullanım: normal bir bean gibi enjekte et ve çağır
odemeIstemci.getir(42);   // arka planda HTTP + Eureka çözümleme + yük dengeleme
```

OpenFeign, servisler arası çağrıyı **bir arayüz** kadar basit yapar (HTTP detayını gizler).

## Akış (uçtan uca)

```
1. Ödeme Servisi açılır -> Eureka'ya "ODEME-SERVISI @ 10.0.0.5:8082" diye kaydolur
2. Sipariş Servisi de kaydolur ve Eureka registry'sini önbelleğe alır
3. Sipariş Servisi "http://odeme-servisi/api/odeme/42" çağırır
4. @LoadBalanced/Feign, "odeme-servisi"ni Eureka'dan çözer -> 10.0.0.5:8082
5. İstek gider; birden çok örnek varsa aralarında yük dengelenir
```

## Dayanıklılık (resilience)

Servisler arası çağrılar ağ üzerindedir → başarısız olabilir. Üretimde:

- **Zaman aşımı** + **yeniden deneme (retry)**.
- **Devre kesici (circuit breaker)** — Resilience4j: sürekli hata veren servise çağrıyı keser,
  yedek (fallback) döner (eski Hystrix'in yerine — ayrı konu).
- **Yük dengeleme:** Spring Cloud LoadBalancer (istemci tarafı).

## Özet

Bir servisi Eureka'ya **`spring.application.name` ile kaydetmeyi** ve başka servisi **adıyla**
çağırmayı öğrendik: `@LoadBalanced` RestClient ve bildirimsel **OpenFeign** (`@FeignClient`) ile —
ikisi de Eureka'dan adres çözüp yük dengeler; uçtan uca akış ve dayanıklılık (retry/circuit breaker)
pratikleri. Sırada, tüm bu servislerin önündeki tek giriş kapısı: **API Gateway**.
