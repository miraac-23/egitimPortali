# Spring Cloud Config Client (Merkezi Ayarı Tüketme)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Cloud Config + çalışan bir Config Server
> gerektirir; portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Önceki konuda Config Server'ı (merkezi yapılandırma sunucusu) kurduk. Şimdi bir mikroservisin bu
sunucudan ayarlarını **nasıl çektiğini** ve **çalışırken nasıl yenilediğini** ele alıyoruz.

## Config Client kurulumu

Her mikroservise config client eklenir:

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-config'
```

```yaml
# application.yml — config server'a nasıl bağlanılacağı
spring:
  application:
    name: siparis-servisi          # hangi yapılandırma dosyası çekilecek (config repo'daki ad)
  config:
    import: "optional:configserver:http://localhost:8888"   # config server adresi
  profiles:
    active: dev                    # hangi profil (siparis-servisi-dev.yml)
```

Servis açılınca, daha kendi ayarlarını yüklemeden önce Config Server'a gider; `siparis-servisi.yml`
+ `siparis-servisi-dev.yml` + ortak `application.yml`'i çeker ve birleştirir. Sonrası normaldir:
`@Value` ve `@ConfigurationProperties` (topic 14) bu değerleri görür.

## Ayarları kullanma

Config server'dan gelen değerler, yerel ayarlar gibi kullanılır:

```java
@Value("${odeme.zaman-asimi:30}")        // config repo'daki siparis-servisi.yml'den
private int zamanAsimi;

@ConfigurationProperties(prefix = "odeme")
record OdemeAyarlari(int zamanAsimi, String saglayici) {}
```

## Çalışırken yenileme (refresh)

Config server'daki bir ayar değişti — servisi **yeniden başlatmadan** güncellemek ister misin?
`@RefreshScope` + Actuator `/actuator/refresh` ucu bunu sağlar:

```java
@RestController
@RefreshScope                              // /refresh çağrılınca bu bean yeniden oluşturulur
class AyarController {
    @Value("${mesaj.banner}") String banner;
    @GetMapping("/banner") String banner() { return banner; }
}
```

```
1. Config repo'da mesaj.banner değiştir (git push)
2. POST http://servis/actuator/refresh
3. @RefreshScope bean'leri yeni değerle yeniden oluşur (yeniden başlatma YOK)
```

Tüm servisleri aynı anda yenilemek için **Spring Cloud Bus** (RabbitMQ/Kafka üzerinden yayın)
kullanılır: bir `/busrefresh` ile tüm örnekler güncellenir.

## Yapılandırma çözümleme sırası

Bir servis için ayarlar şu öncelikle birleşir (sonraki öncekini ezer): komut satırı / ortam
değişkenleri → Config Server (servis-özel + profil + ortak) → yerel `application.yml`. Yani Config
Server merkezi kaynaktır ama acil durumda ortam değişkeni ile ezilebilir.

## İyi uygulamalar

- **Önyükleme dayanıklılığı:** `optional:configserver:` ile config server erişilemezse uygulama
  yine de (yerel varsayılanlarla) başlayabilir; ya da Config Server'ı yüksek erişilebilir kur.
- **Sırlar:** Parolaları düz metin koyma; Config Server şifreleme (`{cipher}`) veya Vault kullan.
- **Yenileme kapsamı:** Yalnızca değişebilen değerleri `@RefreshScope`'a koy; her şeyi değil.

## Özet

Bir mikroservisin Config Server'dan ayarlarını **`spring.config.import` ile çektiğini**, bu
değerleri `@Value`/`@ConfigurationProperties` ile kullandığını ve **`@RefreshScope` + `/refresh`**
(veya Spring Cloud Bus) ile çalışırken yenilediğini öğrendik; yapılandırma çözümleme sırası ve sır
yönetimi pratiklerini gördük. Bununla mikroservis çekirdeği batch'i (Eureka, kayıt, Gateway, Config
Server/Client) tamamlandı. Sırada — sonraki turlarda — API dokümantasyonu, izleme, entegrasyon ve
dağıtım konuları.
