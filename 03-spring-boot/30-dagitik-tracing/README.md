# Dağıtık İzleme (Distributed Tracing)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Micrometer Tracing + Zipkin/Tempo + birden çok
> servis gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Tek bir uygulamada bir hatayı log'tan bulmak kolaydır. Ama bir istek **5 servisten** geçiyorsa
(Gateway → Sipariş → Ödeme → Stok → Kargo) ve yavaşsa/hata veriyorsa, hangi serviste, nerede sorun
olduğunu nasıl anlarsın? **Dağıtık izleme (distributed tracing)**, bir isteğin servisler arası
**tüm yolculuğunu** uçtan uca takip etmeni sağlar. Spring Boot'ta bu, **Micrometer Tracing** ile
yapılır.

## Temel kavramlar: Trace ve Span

- **Trace:** Bir isteğin uçtan uca tüm yolculuğu. Benzersiz bir **trace id** ile işaretlenir.
- **Span:** Bu yolculuğun tek bir adımı (bir servisteki işlem, bir DB sorgusu, bir HTTP çağrısı).
  Her span'ın bir **span id**'si ve süresi vardır; span'lar bir ağaç oluşturur.

```
Trace (traceId=abc123)
├─ Span: Gateway (2ms)
│   └─ Span: Sipariş Servisi (45ms)
│       ├─ Span: Ödeme Servisi HTTP çağrısı (30ms)  ← yavaş!
│       └─ Span: DB sorgusu (5ms)
```

Bu ağaca bakıp "gecikme Ödeme Servisi çağrısında" dersin — tek bakışta.

## Kurulum (Micrometer Tracing)

Spring Boot 3'te Sleuth'un yerini **Micrometer Tracing** aldı:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-tracing-bridge-brave'   // veya -otel (OpenTelemetry)
implementation 'io.zipkin.reporter2:zipkin-reporter-brave'        // span'ları Zipkin'e gönder
```

```yaml
management:
  tracing:
    sampling:
      probability: 1.0          # isteklerin %100'ünü izle (üretimde örnekleme: 0.1 gibi)
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans   # Zipkin sunucusu
```

Kurulumdan sonra Spring **otomatik** olarak: gelen/giden HTTP isteklerine trace/span ekler, bunları
servisler arasında **yayar (propagate)** ve toplayıcıya (Zipkin) gönderir.

## Bağlam yayılımı (context propagation)

Sihir, izleme bilgisinin servisler arasında taşınmasıdır: bir servis başka bir servisi çağırırken,
trace/span id'leri **HTTP başlıklarına** (örn. `traceparent` / `X-B3-TraceId`) eklenir. Sonraki
servis bunu okuyup aynı trace'e devam eder. Böylece tüm zincir tek bir trace altında birleşir.

## Log korelasyonu

Micrometer Tracing, trace/span id'lerini **loglara** da ekler (MDC üzerinden):

```
2026-06-23 INFO [siparis-servisi,abc123,d4e5f6] Sipariş işleniyor...
                                 ▲       ▲
                              traceId  spanId
```

Aynı `traceId`'yi tüm servislerin loglarında arayarak (merkezi log sisteminde — ELK/Loki) bir
isteğin **tüm loglarını** birbirine bağlarsın. (Filter ile elle trace id eklemeyi topic 12'de
görmüştük; burada framework otomatik yapar.)

## İzleme arka uçları (backend)

Span'lar bir toplayıcıya gönderilir ve orada görselleştirilir:

- **Zipkin** — hafif, kolay kurulum.
- **Jaeger** — CNCF, büyük ölçek.
- **Grafana Tempo** — Grafana ekosistemi (loglar/metriklerle birlikte).
- **OpenTelemetry (OTel):** Tracing/metrik/log için **standart**; `-otel` köprüsüyle herhangi bir
  OTel-uyumlu arka uca gönderebilirsin (satıcı bağımsızlığı).

## Gözlemlenebilirliğin üç ayağı

Dağıtık tracing, **gözlemlenebilirliğin (observability)** bir parçasıdır:

| Ayak | Ne | Araç |
|------|-----|------|
| **Metrikler** | Sayısal ölçümler (istek/sn, gecikme) | Micrometer → Prometheus + Grafana |
| **Loglar** | Olay kayıtları | SLF4J → ELK/Loki |
| **İzler (traces)** | İstek yolculuğu | Micrometer Tracing → Zipkin/Tempo |

Üçü trace id ile birbirine bağlanınca, bir sorunu metrikten fark edip → trace ile yerini bulup →
log ile detayını görürsün.

## Özet

Dağıtık izlemenin, bir isteğin servisler arası tüm yolculuğunu (trace + span ağacı) takip ederek
gecikme/hata yerini bulmayı sağladığını öğrendik: Micrometer Tracing kurulumu, otomatik bağlam
yayılımı (HTTP başlıkları), log korelasyonu (traceId/spanId) ve arka uçlar (Zipkin/Jaeger/Tempo/
OTel); gözlemlenebilirliğin üç ayağı (metrik/log/iz). Sırada, uygulamayı paketleyip dağıtma:
**Docker imajı oluşturma**.
