# Spring Cloud Config Server (Merkezi Yapılandırma)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Cloud Config + bir Git deposu + birden çok
> servis gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Onlarca mikroservisin her birinde ayrı `application.yml` tutmak kâbusa döner: bir veritabanı
adresi değiştiğinde onlarca dosyayı tek tek güncellemek, sırları her serviste tekrar etmek, ortamlar
arası tutarsızlık... **Spring Cloud Config Server**, tüm servislerin yapılandırmasını **merkezi bir
yerden** (genelde bir Git deposundan) sunar. Servisler ayarlarını başlangıçta buradan çeker.

## Sorun: dağınık yapılandırma

```
siparis-servisi/application.yml   (db.url, redis, ...)
odeme-servisi/application.yml     (db.url tekrar, ...)
kargo-servisi/application.yml     (db.url yine, ...)
   -> bir değişiklik = onlarca dosya, tutarsızlık riski, sır tekrarı
```

## Çözüm: merkezi config server

```
            ┌── Git deposu (yapılandırma dosyaları) ──┐
            │  siparis-servisi.yml                     │
            │  odeme-servisi.yml                       │
            │  application.yml (ortak)                 │
            └───────────────▲──────────────────────────┘
                            │ okur
                  ┌─────────┴──────────┐
                  │  Config Server     │  (8888)
                  └─────────▲──────────┘
          ayarı çeker       │
        ┌─────────┬─────────┴─────────┐
   siparis-servisi  odeme-servisi  kargo-servisi
```

## Config Server kurulumu

```gradle
implementation 'org.springframework.cloud:spring-cloud-config-server'
```

```java
@SpringBootApplication
@EnableConfigServer            // bu uygulamayı bir yapılandırma sunucusu yapar
public class ConfigServerApp {
    public static void main(String[] args) { SpringApplication.run(ConfigServerApp.class, args); }
}
```

```yaml
# application.yml
server.port: 8888
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/sirket/config-repo   # yapılandırmaların bulunduğu Git deposu
          default-label: main
```

## Yapılandırma deposu yapısı

Git deposunda her servis için dosyalar bulunur:

```
config-repo/
├── application.yml                 # TÜM servisler için ortak ayarlar
├── siparis-servisi.yml             # siparis-servisi'ne özel
├── siparis-servisi-dev.yml         # siparis-servisi, dev profili
└── odeme-servisi.yml
```

Config Server bunları bir HTTP API olarak sunar:
`GET http://localhost:8888/siparis-servisi/dev` → siparis-servisi'nin dev ayarları (JSON).

## Faydaları

- **Tek kaynak:** Bir ayar tek yerde; değişiklik tüm servislere yansır.
- **Sürümleme:** Git geçmişi → yapılandırma değişikliklerinin izi, geri alma.
- **Ortam ayrımı:** Profillerle (dev/prod) ortam-özel ayarlar.
- **Şifreleme:** Hassas değerler `{cipher}...` ile şifreli saklanabilir.
- **Çalışırken yenileme:** `@RefreshScope` + Actuator `/refresh` (veya Spring Cloud Bus) ile
  servisi yeniden başlatmadan ayar güncelleme.

## Modern bağlam

- **Kubernetes:** K8s ortamlarında yapılandırma genelde **ConfigMap** ve **Secret** ile sağlanır;
  Spring Cloud Config alternatif/tamamlayıcı olabilir.
- Alternatifler: **HashiCorp Vault** (özellikle sırlar), **Consul** config.

## Özet

Spring Cloud Config Server'ın, dağınık servis yapılandırmalarını Git tabanlı **merkezi bir yerden**
sunarak yönettiğini öğrendik: `@EnableConfigServer`, Git deposu yapısı (ortak + servis-özel +
profil), HTTP API olarak sunum ve faydaları (tek kaynak, sürümleme, şifreleme, çalışırken
yenileme); K8s ConfigMap/Vault alternatifleri. Sırada, bu sunucudan ayar çeken taraf: **Cloud Config
Client**.
