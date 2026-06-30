# Docker İmajı Oluşturma

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Docker, bir konteyner ortamı gerektirir; portalda
> çalışmaz. Komutlar/dosyalar tanıtım amaçlıdır.

Modern dağıtımın standardı **konteynerlerdir**: uygulamayı, çalışması için gereken her şeyle (JDK,
bağımlılıklar, yapılandırma) birlikte tek bir **imaja** paketlersin; bu imaj her yerde (geliştirici
makinesi, test, üretim, bulut) **aynı** şekilde çalışır — "bende çalışıyordu" sorununu bitirir.
Spring Boot'un gömülü sunucusu (topic 22) bunu mükemmel destekler: imaj = uygulama + sunucu.

## Neden konteyner?

- **Tutarlılık:** Aynı imaj her ortamda aynı çalışır (taşınabilirlik).
- **İzolasyon:** Her uygulama kendi kabında; bağımlılık çakışması yok.
- **Ölçeklenme:** Aynı imajdan onlarca kopya (Kubernetes ile) hızlıca açılır.
- **Spring Boot uyumu:** Fat JAR (topic 94) + gömülü Tomcat → `java -jar` ile çalışan tek artefakt,
  konteyner için ideal.

## Yol 1: Elle Dockerfile

```dockerfile
# Çok aşamalı (multi-stage) build: derleme ve çalıştırma ayrı katmanlarda
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre        # çalıştırma için sadece JRE (küçük imaj)
WORKDIR /app
COPY --from=build /app/build/libs/*.jar uygulama.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "uygulama.jar"]
```

```bash
docker build -t egitim-portali:1.0 .
docker run -p 8080:8080 egitim-portali:1.0
```

## Yol 2: Buildpacks (Dockerfile'sız — önerilen)

Spring Boot, **Cloud Native Buildpacks** ile Dockerfile yazmadan optimize bir imaj üretir:

```bash
./gradlew bootBuildImage        # veya: mvn spring-boot:build-image
```

Bu, katmanlı (layered), güvenli ve üretime hazır bir imaj oluşturur — JDK seçimi, katman optimizasyonu
gibi en iyi pratikleri otomatik uygular. (Alternatif: Google **Jib** eklentisi — Docker daemon'a
bile gerek yok.)

## Katmanlı JAR (layered) ile hızlı imaj

Spring Boot fat JAR'ı **katmanlara** ayırır (bağımlılıklar / uygulama kodu ayrı). Bağımlılıklar
nadir değişir, kod sık değişir → Docker katman önbelleği sayesinde her değişiklikte sadece kod
katmanı yeniden oluşur (hızlı build, küçük transfer). Buildpacks bunu otomatik yapar; elle
Dockerfile'da `layertools` ile yapılandırılır.

## Yapılandırma ve ortam

Konteynerde ayarlar genelde **ortam değişkenleriyle** verilir (12-factor app):

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/app \
  egitim-portali:1.0
```

Spring Boot, `SPRING_DATASOURCE_URL` gibi ortam değişkenlerini `spring.datasource.url` özelliğine
otomatik eşler (relaxed binding).

## docker-compose ile çoklu servis

Uygulama + veritabanı + diğer servisleri birlikte ayağa kaldırmak için:

```yaml
# docker-compose.yml
services:
  uygulama:
    image: egitim-portali:1.0
    ports: ["8080:8080"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/app
    depends_on: [db]
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: app
```

```bash
docker compose up
```

## Kubernetes'e doğru

Tek konteynerden sonraki adım **Kubernetes**: imajları çok sayıda kopya halinde çalıştırma, otomatik
ölçekleme, sağlık kontrolleri (Actuator `/health` → K8s liveness/readiness probe — topic 06),
yapılandırma (ConfigMap/Secret), servis keşfi (K8s Service). Spring Boot imajı, K8s için doğal
biçimde hazırdır.

## Özet

Spring Boot uygulamasını Docker imajına paketlemeyi öğrendik: neden konteyner (tutarlılık/izolasyon/
ölçek), elle **Dockerfile** (çok aşamalı build) ve Dockerfile'sız **Buildpacks** (`bootBuildImage`),
katmanlı JAR ile hızlı build, ortam değişkeniyle yapılandırma, **docker-compose** ile çoklu servis ve
Kubernetes'e geçiş; gömülü sunucu + fat JAR'ın bu modele neden ideal olduğu. Bununla API-dok &
gözlemlenebilirlik batch'i tamamlandı.
