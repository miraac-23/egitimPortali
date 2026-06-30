# Dayanıklılık: Resilience4j (ve Hystrix)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Resilience4j bağımlılığı + (anlamlı olması için)
> uzak servisler gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Mikroservislerde bir servis, başka servisleri ağ üzerinden çağırır (topic 16, 24). Ağ ise
**güvenilmezdir**: servis yavaşlayabilir, çökebilir, zaman aşımına uğrayabilir. Bir servisin
çökmesi, onu çağıran servisleri de bekletip **zincirleme çökmeye (cascading failure)** yol açabilir.
**Dayanıklılık (resilience) desenleri** bunu önler. Bugünün standardı **Resilience4j**'dir (eski
Netflix **Hystrix**'in halefi; Hystrix bakım modundadır).

## Sorun: zincirleme çökme

```
Kargo Servisi yavaşladı/çöktü
   -> Sipariş Servisi onu çağırırken bekliyor (thread'ler tükeniyor)
      -> Sipariş Servisi de yanıt veremiyor
         -> Gateway/istemci de bekliyor... TÜM SİSTEM kilitlenir
```

Bir servisin sorunu, tüm sisteme yayılmamalı. Dayanıklılık desenleri bu izolasyonu sağlar.

## Resilience4j desenleri

### Circuit Breaker (devre kesici)

Bir servis sürekli hata veriyorsa, ona çağrı yapmayı **durdur** (devreyi "aç") ve hemen bir yedek
(fallback) dön — boşuna bekleme. Bir süre sonra "yarı-açık" deneme yapıp düzeldiyse devreyi kapatır.

```java
@CircuitBreaker(name = "kargoServisi", fallbackMethod = "kargoFallback")
public Kargo kargoBilgisi(Long id) {
    return restClient.get().uri("http://kargo-servisi/{id}", id).retrieve().body(Kargo.class);
}
// Devre açıkken veya hata olunca bu çalışır:
public Kargo kargoFallback(Long id, Throwable t) {
    return new Kargo(id, "BİLİNMİYOR");   // makul bir yedek
}
```

### Retry (yeniden deneme)

Geçici hatalarda (anlık ağ sorunu) çağrıyı birkaç kez tekrarla:

```java
@Retry(name = "kargoServisi", fallbackMethod = "kargoFallback")
```

### Rate Limiter / Bulkhead / Time Limiter

- **Rate Limiter:** Belirli sürede en fazla N çağrı (aşırı yükü önle).
- **Bulkhead:** Bir servise ayrılan eşzamanlı çağrı sayısını sınırla (kaynakları izole et — bir
  servisin tüm thread havuzunu yememesi için).
- **Time Limiter:** Çağrıya zaman sınırı koy (yavaş servise takılma).

## Kurulum

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j'
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      kargoServisi:
        failure-rate-threshold: 50          # %50 hata -> devreyi aç
        wait-duration-in-open-state: 10s    # 10sn sonra yarı-açık dene
        sliding-window-size: 10
```

Anotasyonlar (`@CircuitBreaker`, `@Retry`...) AOP ile çalışır (topic 02-spring/04).

## Hystrix neden artık kullanılmıyor?

**Netflix Hystrix**, bu desenleri popülerleştiren ilk kütüphaneydi (`@HystrixCommand`). Ancak
2018'de **bakım moduna** alındı. Yerine **Resilience4j** geldi: daha hafif, fonksiyonel, Java 8+
için tasarlanmış, modüler. Yeni projelerde Hystrix kullanma; eski kodda görürsen Resilience4j'ye
geçiş düşün.

## Gözlemlenebilirlikle birlikte

Devre kesici durumları (açık/kapalı/yarı-açık), retry sayıları Actuator/Micrometer ile metrik
olarak yayılır (topic 06, 30) — Grafana'da izleyip alarm kurabilirsin.

## Özet

Dağıtık sistemlerde **dayanıklılık** desenlerini öğrendik: zincirleme çökme sorununu ve çözümlerini —
**circuit breaker** (sürekli hata veren servise çağrıyı kes + fallback), **retry**, **rate limiter**,
**bulkhead**, **time limiter** — Resilience4j ile (`@CircuitBreaker`/`@Retry` + yapılandırma); eski
**Hystrix**'in neden Resilience4j ile değiştiğini ve gözlemlenebilirlik bağlantısını gördük. Bununla
entegrasyon batch'i tamamlandı. Sırada — son batch — kalan temel/dağıtım konuları.
