# Actuator ve Üretim Araçları

Bir uygulamayı yazmak işin yarısıdır; onu **üretimde çalışır, izlenebilir ve yönetilebilir**
tutmak diğer yarısıdır. "Uygulama ayakta mı? Belleği ne durumda? Saniyede kaç sipariş geliyor?"
sorularını cevaplamak gerekir. Spring Boot **Actuator**, bunları hazır endpoint'lerle sağlar; bu
bölümde Actuator'ı ve uygulamayı üretime taşıyan diğer araçları (API dokümantasyonu, Docker,
mikroservisler) ele alıyoruz.

## Actuator: hazır izleme endpoint'leri

`spring-boot-starter-actuator` bağımlılığı, uygulamaya bir dizi yönetim endpoint'i ekler. Bunlar
güvenlik için varsayılan olarak kısıtlıdır; hangilerinin açılacağını yapılandırırsın:

```yaml
management:
  endpoints.web.exposure.include: health,info,metrics
  endpoint.health.show-details: always
```

En önemlileri:

- **`/actuator/health`** — uygulama sağlıklı mı? Load balancer ve Kubernetes liveness/readiness
  probe'ları bunu kullanır (`UP`/`DOWN`).
- **`/actuator/metrics`** — JVM belleği, GC, HTTP istekleri, veritabanı havuzu gibi yüzlerce
  ölçüm.
- **`/actuator/info`** — uygulama/build bilgisi (sürüm vb.).
- **`/actuator/env`, `/loggers`, `/threaddump`, `/heapdump`** — ortam, log seviyeleri, teşhis.

Örnek 1 (`./Ornek1.java`) health/info/metrics endpoint'lerini açar ve self-test ile çağırır:
`/actuator/health` → `{"status":"UP",...}`, `/actuator/metrics/jvm.memory.used` → bellek değeri.

## Özel HealthIndicator

Uygulamanın sağlığı, bağlı olduğu dış sistemlere de bağlıdır (ödeme sağlayıcı, mesaj kuyruğu...).
Kendi sağlık göstergeni `HealthIndicator` ile eklersin; bileşen `/actuator/health` altında görünür:

```java
@Component
class OdemeSaglayiciHealth implements HealthIndicator {
    public Health health() {
        return erisilebilir
            ? Health.up().withDetail("gecikme_ms", 42).build()
            : Health.down().withDetail("hata", "bağlantı yok").build();
    }
}
```

Herhangi bir bileşen `DOWN` olursa genel sağlık `DOWN` olur ve orkestrasyon katmanı (k8s) trafiği
kesebilir. Örnek 2 (`./Ornek2.java`) özel bir ödeme sağlayıcı sağlık göstergesi ekler.

## Özel metrikler (Micrometer)

Actuator'ın metrik altyapısı **Micrometer**'dır — "metrikler için SLF4J" gibi düşün: tek bir API
yazarsın, Prometheus/Datadog/Graphite gibi sistemlere aktarılır. İş olaylarını ölçmek için kendi
metriklerini tanımlarsın:

```java
Counter siparisSayaci = Counter.builder("siparis.olusturulan").register(registry);
siparisSayaci.increment(); // her siparişte
```

Ölçer türleri: **Counter** (artan sayaç), **Timer** (süre/oran), **Gauge** (anlık değer),
**DistributionSummary** (dağılım). Örnek 3 (`./Ornek3.java`) bir sipariş sayacı tanımlar, 3
sipariş oluşturur ve `/actuator/metrics/siparis.olusturulan` üzerinden değeri okur.

## Üretime taşıma: diğer araçlar

Gerçek bir üretim dağıtımında Actuator'ın yanında şunlar da kullanılır:

- **API dokümantasyonu (OpenAPI/Swagger):** `springdoc-openapi` bağımlılığı eklenince,
  controller'larından otomatik bir OpenAPI şeması ve `/swagger-ui.html` arayüzü üretilir;
  istemciler API'ni keşfeder.
- **Yapılandırılmış loglama:** Üretimde JSON formatında log + merkezi toplama (ELK/Loki).
- **Docker ile konteynerleştirme:** Uygulamayı bir imaja paketlersin. Spring Boot, `bootBuildImage`
  (Cloud Native Buildpacks) ile Dockerfile yazmadan imaj üretebilir:
  ```dockerfile
  FROM eclipse-temurin:21-jre
  COPY build/libs/uygulama.jar app.jar
  ENTRYPOINT ["java","-jar","/app.jar"]
  ```
- **Profiller ve dışsal yapılandırma:** `application-prod.yml`, ortam değişkenleri ve secret
  yöneticileriyle ortam-bazlı ayar.
- **Prometheus/Grafana:** `micrometer-registry-prometheus` ile `/actuator/prometheus` endpoint'i
  açılır; metrikler Prometheus'a toplanır, Grafana'da görselleştirilir.

## Mikroservislere giriş

Tek bir büyük uygulama (monolit) yerine, sistemi küçük, bağımsız servislere bölmek mikroservis
mimarisidir. Spring ekosistemi bunu **Spring Cloud** ile destekler:

- **Eureka / Service Discovery** — servisler birbirini isimle bulur.
- **API Gateway** (Spring Cloud Gateway) — tek giriş noktası, yönlendirme, kimlik.
- **Config Server** — merkezi yapılandırma.
- **Resilience4j** — devre kesici (circuit breaker), yeniden deneme.
- **Mesajlaşma** (RabbitMQ/Kafka) — servisler arası asenkron iletişim.

Actuator burada da kritiktir: her servisin sağlığı ve metrikleri merkezi olarak izlenir.

## Özet

Actuator ile health/metrics/info endpoint'lerini (Örnek 1), özel `HealthIndicator` ile uygulamaya
özel sağlık kontrolünü (Örnek 2) ve Micrometer ile özel iş metriklerini (Örnek 3) gördük; API
dokümantasyonu, Docker, Prometheus/Grafana ve mikroservislere giriş kavramlarını ele aldık.
Uygulaman artık yalnızca çalışmıyor; **gözlemlenebilir ve üretime hazır**. Sırada, tüm bu
yazdıklarımızın doğru çalıştığını garanti eden konu: **test**.
