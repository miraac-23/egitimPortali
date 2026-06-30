# Spring Boot ve Google Cloud Platform (GCP)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** GCP, bir Google Cloud hesabı/kimlik bilgisi +
> Spring Cloud GCP bağımlılıkları gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Bir Spring Boot uygulamasını bir buluta dağıtmak ve bulutun servislerini (veritabanı, mesajlaşma,
depolama, gizli yönetimi) kullanmak yaygın bir ihtiyaçtır. **Google Cloud Platform (GCP)**, büyük
bulut sağlayıcılardan biridir; Spring, **Spring Cloud GCP** ile entegrasyonu kolaylaştırır. (Aynı
kavramlar AWS/Azure için de benzerdir.)

## Spring Boot uygulamasını GCP'ye dağıtma seçenekleri

| Seçenek | Açıklama | Ne zaman |
|---------|----------|----------|
| **Cloud Run** | Konteyner (Docker imajı — topic 31) çalıştırır, otomatik ölçekler (0'a kadar) | Modern, sunucusuz konteyner (en yaygın) |
| **App Engine** | Yönetilen PaaS; kodu/JAR'ı dağıt, gerisini Google yönetir | Hızlı, altyapıyla uğraşmadan |
| **GKE (Kubernetes)** | Tam Kubernetes | Büyük/karmaşık mikroservis sistemleri |
| **Compute Engine** | Ham sanal makine | Tam kontrol gerektiğinde |

Çoğu Spring Boot uygulaması için **Cloud Run** idealdir: bir Docker imajı yapıp (`bootBuildImage`),
Cloud Run'a verirsin; HTTPS, ölçekleme, yük dengeleme otomatik gelir.

## Spring Cloud GCP ile servisleri kullanma

```gradle
implementation platform('com.google.cloud:spring-cloud-gcp-dependencies:5.x.x')
implementation 'com.google.cloud:spring-cloud-gcp-starter'
```

Spring Cloud GCP, GCP servislerini Spring soyutlamalarına bağlar:

- **Cloud SQL** (yönetilen PostgreSQL/MySQL): Spring Data JPA ile (topic 02) doğrudan kullanılır.
- **Pub/Sub** (mesajlaşma): Spring Integration/`PubSubTemplate` — Kafka benzeri (topic 32).
- **Cloud Storage** (nesne deposu): dosyalar için (topic 17'deki "üretimde nesne deposu").
- **Secret Manager**: sırları (`spring.config.import: sm://...`) ile yapılandırmaya enjekte etme.
- **Cloud Trace / Monitoring**: tracing (topic 30) ve metriklerin GCP'ye akması.

```java
// Pub/Sub'a mesaj yayınlama (Kafka'ya benzer)
@Autowired PubSubTemplate pubSub;
pubSub.publish("siparis-konusu", "OLUSTURULDU");
```

## Kimlik ve yapılandırma

- **Kimlik (auth):** GCP'de **Application Default Credentials (ADC)** — uygulama Cloud Run/GKE'de
  çalışırken hizmet hesabı (service account) kimliğini otomatik alır; yerelde `gcloud auth` ile.
  API anahtarını koda gömmezsin.
- **Yapılandırma:** Ortam değişkenleri + Secret Manager (12-factor; topic 14).
- **Gözlemlenebilirlik:** Actuator (topic 06) + Micrometer → Cloud Monitoring/Trace.

## Bulut-bağımsız kalmak

GCP'ye sıkı bağlanmamak için, mümkün olduğunca **standart soyutlamalar** kullan: JPA (herhangi bir
SQL), standart mesajlaşma arayüzleri, OpenTelemetry (tracing). Böylece sağlayıcı değişse (AWS/Azure)
geçiş kolaylaşır. Spring'in soyutlamaları bu taşınabilirliği büyük ölçüde sağlar.

## Özet

Spring Boot uygulamasını GCP'ye dağıtma seçeneklerini (Cloud Run/App Engine/GKE/Compute) ve **Spring
Cloud GCP** ile bulut servislerini (Cloud SQL, Pub/Sub, Storage, Secret Manager, Trace) kullanmayı
öğrendik; kimlik (ADC/service account), yapılandırma ve bulut-bağımsız kalma stratejisini gördük.
(AWS/Azure için kavramlar benzerdir.) Sırada, son konu — sosyal giriş: **Google OAuth2 Sign-In**.
