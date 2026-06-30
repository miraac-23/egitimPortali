# Spring Boot Admin (Server / Client)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Boot Admin ek bağımlılık + birden çok
> servis gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Actuator (topic 06), uygulamanın sağlık/metrik bilgilerini **ham JSON** olarak sunar. Ama onlarca
servisin sağlığını tek tek JSON uçlarından izlemek zordur. **Spring Boot Admin** (codecentric'in
açık kaynak projesi), Actuator verilerini **görsel bir panoda** toplar: tüm servislerin sağlığı,
metrikleri, logları, ortam değişkenleri tek ekranda. (Spring takımının resmi ürünü değildir ama çok
yaygındır.)

## Mimari: Server + Client

```
        ┌──────────── Admin Server (görsel pano) ────────────┐
        │   tüm servislerin sağlığı/metrikleri/logları         │
        └───────▲────────────────▲────────────────▲───────────┘
        kaydol  │       kaydol    │       kaydol    │
   ┌────────────┴─┐  ┌────────────┴─┐  ┌────────────┴─┐
   │ Servis A      │  │ Servis B      │  │ Servis C      │  (Admin Client + Actuator)
   └───────────────┘  └───────────────┘  └───────────────┘
```

## Admin Server kurulumu

```gradle
implementation 'de.codecentric:spring-boot-admin-starter-server:3.3.x'
```

```java
@SpringBootApplication
@EnableAdminServer            // bu uygulamayı Admin panosu yapar
public class AdminServerApp {
    public static void main(String[] args) { SpringApplication.run(AdminServerApp.class, args); }
}
```

`http://localhost:8080` (veya ayarladığın port) adresinde görsel pano açılır.

## Admin Client kurulumu (izlenecek servisler)

Her izlenecek servise client eklenir (+ Actuator açık olmalı):

```gradle
implementation 'de.codecentric:spring-boot-admin-starter-client:3.3.x'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

```yaml
spring:
  boot:
    admin:
      client:
        url: http://localhost:8080      # Admin Server adresi
management:
  endpoints:
    web:
      exposure:
        include: "*"                     # Admin'in göreceği actuator uçları
  endpoint:
    health:
      show-details: always
```

> **Eureka ile:** Servisler zaten Eureka'ya kayıtlıysa (topic 23-24), Admin Server onları Eureka'dan
> **otomatik keşfeder**; client eklemen bile gerekmez.

## Panoda neler var?

- **Sağlık (health):** UP/DOWN durumu, alt bileşenler (DB, disk, Redis...).
- **Metrikler:** JVM bellek, CPU, GC, HTTP istek sayıları/süreleri (Micrometer).
- **Loglar:** Canlı log görüntüleme ve **çalışırken log seviyesi değiştirme** (DEBUG'a çekme).
- **Ortam:** Yapılandırma özellikleri, ortam değişkenleri.
- **Thread/heap dump**, planlanmış görevler, HTTP izleri.
- **Bildirimler:** Bir servis DOWN olunca e-posta/Slack/Teams uyarısı.

## Üretim bağlamı

- **Güvenlik:** Admin panosu hassas bilgi gösterir (ortam, thread dump); mutlaka **kimlikle koru**
  (Spring Security).
- **Alternatifler:** Daha büyük ölçekte gözlemlenebilirlik için **Prometheus + Grafana** (metrik),
  **ELK/Loki** (log), **Zipkin/Jaeger** (tracing) tercih edilir. Spring Boot Admin, hızlı ve hafif
  bir operasyonel pano olarak değerlidir.

## Özet

Spring Boot Admin'in, Actuator verilerini **görsel bir panoda** toplayarak çok servisli sistemleri
izlemeyi kolaylaştırdığını öğrendik: Server (`@EnableAdminServer`) + Client (Actuator açık) mimarisi,
Eureka ile otomatik keşif, panodaki yetenekler (sağlık/metrik/log/ortam, çalışırken log seviyesi,
bildirimler) ve güvenlik/üretim alternatifleri (Prometheus+Grafana). Sırada, bir isteğin servisler
arası yolculuğunu izleme: **dağıtık tracing**.
