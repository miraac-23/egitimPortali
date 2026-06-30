# Spring Boot Logging (Günlükleme)

`System.out.println` hata ayıklamak için yeterli görünür ama üretimde işe yaramaz: seviye yok,
zaman damgası yok, kapatılamaz, dosyaya yazılamaz, performansı bozar. Profesyonel uygulamalar
**logging** kullanır. Spring Boot, **SLF4J** (cephe API) + **Logback** (uygulama) ile hazır,
yapılandırılmış bir günlükleme altyapısı sunar — hiçbir bağımlılık eklemene gerek kalmadan.

## SLF4J ile logger almak

Her sınıf kendi logger'ını alır (genelde sınıf adıyla):

```java
private static final Logger log = LoggerFactory.getLogger(Ornek1.class);
```

`SLF4J` bir **cephe** (facade) API'sidir; arkasındaki uygulama (Logback) değişse de kodun
değişmez. (Lombok kullanıyorsan `@Slf4j` anotasyonu bu satırı otomatik üretir.)

## Log seviyeleri

En ayrıntılıdan en kritiğe doğru bir hiyerarşi vardır; ayarlanan seviye ve **üstü** görünür:

```
TRACE  <  DEBUG  <  INFO  <  WARN  <  ERROR
```

| Seviye | Ne zaman |
|--------|----------|
| `TRACE` | Çok ayrıntılı iz (nadiren) |
| `DEBUG` | Geliştirme/teşhis ayrıntısı |
| `INFO` | Normal akış olayları (varsayılan görünür seviye) |
| `WARN` | Beklenmedik ama kurtarılabilir durum |
| `ERROR` | Hata; işlem başarısız |

Örnek 1 (`./Ornek1.java`) tüm seviyeleri, parametreli loglamayı ve istisna loglamayı gösterir.

## Parametreli loglama: `{}`

String birleştirme yerine **yer tutucu** kullan:

```java
log.info("Kullanıcı {} {}. kez giriş yaptı", kullanici, deneme);   // İYİ
log.info("Kullanıcı " + kullanici + " giriş yaptı");                // KÖTÜ
```

Neden? `{}` **tembeldir**: log seviyesi kapalıysa string hiç oluşturulmaz (performans). Ayrıca
daha okunaklı ve güvenlidir. İstisnayı son argüman olarak verirsen stack trace otomatik basılır:
`log.error("Başarısız", e)`.

## Yapılandırma

`application.properties` ile seviyeleri ve çıktıyı ayarlarsın:

```properties
logging.level.root=INFO                         # genel seviye
logging.level.com.egitim=DEBUG                  # belirli paket için DEBUG
logging.level.org.springframework.web=WARN
logging.file.name=uygulama.log                  # dosyaya yaz
logging.pattern.console=%d{HH:mm:ss} %-5level %logger - %msg%n
```

Örnek 1, `logging.level.com.egitim.springboot.logging=DEBUG` ile DEBUG mesajlarını görünür kılar.

## Üretimde günlükleme

- **Yapılandırılmış (JSON) loglama:** Log toplama sistemleri (ELK, Loki, Datadog) için JSON
  formatı; sorgulama/filtreleme kolaylaşır.
- **Korelasyon (trace) kimliği:** Her isteğe bir id ekle (MDC / filter — topic 12) → dağıtık
  sistemde bir isteğin tüm loglarını birbirine bağla.
- **Seviye yönetimi:** Üretimde `INFO`/`WARN`; sorun anında belirli paketi `DEBUG`'a çek (Actuator
  ile çalışırken bile değiştirilebilir — topic 06).
- **Hassas veri loglama!** Parola, token, kişisel veri **loglanmaz**.

## Özet

Spring Boot'un SLF4J + Logback ile hazır günlükleme altyapısını öğrendik: logger alma, log
seviyeleri (TRACE→ERROR), tembel **parametreli loglama (`{}`)** ve istisna loglama (Örnek 1);
`application.properties` ile yapılandırma ve üretim pratiklerini (JSON, trace id, hassas veri)
gördük. `println` yerine her zaman logger kullan. Sırada, başka servislerin API'lerini çağırmak:
**REST tüketimi (RestClient/RestTemplate)**.
