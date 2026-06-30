# Spring Boot Nedir ve İlk Uygulama

Spring Framework'ün gücünü gördük; ama saf Spring'le bir web uygulaması kurmak hâlâ epey
yapılandırma (sunucu kurulumu, bağımlılık uyumu, XML/Java config) gerektirir. **Spring Boot**,
bu kurulum yükünü neredeyse sıfıra indirir: "convention over configuration" (yapılandırma yerine
makul varsayımlar) felsefesiyle, birkaç satırda çalışan, üretime hazır bir uygulama elde edersin.
Bu bölümde Boot'un ne olduğunu ve ilk web uygulamamızı kuruyoruz.

## Spring vs Spring Boot

Spring **Framework**, IoC/DI, web, veri, güvenlik gibi yapı taşlarını sunar — ama bunları
birleştirmek, sürümlerini uyumlamak ve bir sunucu kurmak sana kalır. **Spring Boot**, Spring'in
üzerine kurulu bir katmandır ve şunları getirir:

- **Otomatik yapılandırma (auto-configuration):** Classpath'e bakıp makul varsayılanları kurar
  (web varsa gömülü Tomcat + Jackson, H2 varsa bir DataSource...).
- **Starter bağımlılıklar:** `spring-boot-starter-web` gibi tek bir bağımlılık, uyumlu
  kütüphane setini birlikte getirir.
- **Gömülü sunucu:** Ayrı bir Tomcat kurmana gerek yok; uygulama kendi sunucusunu içinde taşır
  (`java -jar` ile çalışır).
- **Üretim özellikleri:** Actuator (sağlık/metrik), dışsal yapılandırma, profiller.

> Kısaca: **Spring "ne" sağlar, Spring Boot onu "hızlı ve hazır" hâle getirir.**

## İlk uygulama

Bir Spring Boot uygulamasının kalbi `@SpringBootApplication` ile işaretli bir sınıf ve bir
`main` metodudur:

```java
@SpringBootApplication
public class Ornek1 {
    public static void main(String[] args) {
        SpringApplication.run(Ornek1.class, args); // context + gömülü Tomcat
    }
    @RestController
    static class SelamController {
        @GetMapping("/selam") String selam() { return "Merhaba Spring Boot!"; }
    }
}
```

`SpringApplication.run(...)` tek satırda: Spring context'i kurar, otomatik yapılandırmayı
çalıştırır ve gömülü Tomcat'i başlatır. `@RestController` içindeki metotların dönüş değeri
doğrudan HTTP yanıtı olur. Örnek 1 (`./Ornek1.java`) bu uygulamayı kurar ve açılışta kendi
`/selam` endpoint'ini `RestClient` ile çağırıp sonucu yazar.

> Bu portalda Boot örnekleri gömülü Tomcat ile **gerçekten başlatılır**, kendilerini çağırıp
> çıktı üretir, ardından sunucu açık kaldığı için otomatik durdurulur. Yani gördüğün çıktı,
> canlı çalışan bir web uygulamasından gelir.

## @SpringBootApplication ne yapar?

Bu tek anotasyon, aslında üçünü birleştirir:

- **`@Configuration`** — sınıf bir yapılandırma kaynağıdır.
- **`@EnableAutoConfiguration`** — Boot'un otomatik yapılandırma sihrini açar.
- **`@ComponentScan`** — bu sınıfın paketini ve alt paketlerini tarar, bean'leri bulur.

Örnek 2 (`./Ornek2.java`) container'da otomatik oluşturulan bean sayısını yazdırır (çoğu
auto-configuration ile gelir) ve **dış yapılandırma değerlerini** `@Value` ile enjekte eder.

### Dış yapılandırma: application.properties / @Value

Spring Boot, ayarları koddan ayırır. Değerler `application.properties` veya `application.yml`
dosyasından, ortam değişkenlerinden veya komut satırı argümanlarından gelir; koda `@Value` ile
enjekte edilir:

```java
@Value("${uygulama.ad}")     String ad;
@Value("${uygulama.dil:tr}") String dil; // yoksa varsayılan "tr"
```

```yaml
# application.yml örneği
uygulama:
  ad: Eğitim Portalı
server:
  port: 8080
```

## İlk REST ve JSON

Spring Boot, nesneleri elle JSON'a çevirmeni gerektirmez; bir nesne (ör. bir `record`)
döndürürsün, Boot (Jackson ile) onu otomatik JSON'a serileştirir:

```java
@GetMapping("/urunler") List<Urun> hepsi() { return urunler; } // -> JSON dizisi
@GetMapping("/urunler/{id}") Urun bul(@PathVariable int id) { ... }
@GetMapping("/ara") List<Urun> ara(@RequestParam String kelime) { ... }
```

Örnek 3 (`./Ornek3.java`) `@PathVariable`, `@RequestParam` ve nesne→JSON dönüşümünü canlı
endpoint'lerle gösterir. Bir sonraki bölümde REST'i (POST/PUT/DELETE, durum kodları,
`ResponseEntity`) derinleştireceğiz.

## Proje yapısı ve Initializr

Gerçek bir Boot projesi genelde **Spring Initializr** (start.spring.io) ile oluşturulur:
gerekli starter'ları seçersin, hazır bir Maven/Gradle projesi iner. Tipik yapı:

```
src/main/java/...        # kod (Application sınıfı, controller/service/repository)
src/main/resources/
  application.yml        # yapılandırma
  static/, templates/    # statik içerik / şablonlar
src/test/java/...        # testler
```

## Özet

Spring Boot'un Spring üzerine kurduğu kolaylıkları (auto-configuration, starter'lar, gömülü
sunucu) öğrendik; `@SpringBootApplication` ile ilk web uygulamamızı kurduk, `@Value` ile dış
yapılandırmayı okuduk ve nesne→JSON dönüşümüyle ilk REST endpoint'lerimizi yazdık. Sırada, REST
API geliştirmeyi tüm detaylarıyla ele alıyoruz: **HTTP metotları, ResponseEntity ve durum kodları**.
