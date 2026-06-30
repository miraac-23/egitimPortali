# Başlangıç: Initializr, CLI, Bootstrapping ve Araçlar

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Anlatılan araçlar (Initializr, CLI, STS) proje
> oluşturma/geliştirme ortamıyla ilgilidir; portalda çalıştırılmaz. Tanıtım amaçlıdır. (Bu konu
> listendeki "Quick Start using CLI", "Bootstrapping" ve "Spring Tool Suite" başlıklarını birlikte
> ele alır.)

Bir Spring Boot projesine başlamanın birkaç yolu vardır. Bu konu, proje oluşturma araçlarını
(Initializr, CLI), uygulamanın nasıl önyüklendiğini (bootstrapping) ve geliştirme ortamını (IDE)
ele alır.

## Spring Initializr (en yaygın)

**[start.spring.io](https://start.spring.io)** — bir web sihirbazı. Bağımlılıkları, build aracını
(Maven/Gradle), Java sürümünü, paket adını seçer; hazır bir proje iskeleti `.zip` indirir veya
IDE'den doğrudan üretir.

- IntelliJ IDEA / VS Code / STS bu servisi **entegre** kullanır ("New Spring Boot Project").
- `curl https://start.spring.io/starter.zip -d dependencies=web,data-jpa -o proje.zip` ile komut
  satırından da üretilebilir.

İskelet şunları içerir: `build.gradle`/`pom.xml`, ana sınıf (`@SpringBootApplication`),
`application.properties`, test sınıfı.

## Spring Boot CLI

**Spring Boot CLI**, komut satırından hızlı prototip/script için bir araçtır (Groovy tabanlı
çalıştırma):

```bash
spring init --dependencies=web,data-jpa benim-proje   # Initializr'ı CLI'dan çağır
spring run app.groovy                                   # bir Groovy script'i Spring app olarak çalıştır
```

CLI, hızlı denemeler içindir; gerçek projeler genelde Initializr + IDE ile başlar. (Kurulumu:
SDKMAN, Homebrew vb.)

## Bootstrapping: uygulama nasıl başlar?

Her Spring Boot uygulamasının kalbi **ana sınıftır**:

```java
@SpringBootApplication       // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class UygulamaApp {
    public static void main(String[] args) {
        SpringApplication.run(UygulamaApp.class, args);   // ÖNYÜKLEME (bootstrap) burada başlar
    }
}
```

`SpringApplication.run(...)` çağrıldığında olanlar (önyükleme akışı):

1. **ApplicationContext** (IoC konteyneri) oluşturulur.
2. **Bileşen tarama** (`@ComponentScan`): ana sınıfın paketinden aşağısı taranır, bean'ler bulunur.
3. **Otomatik yapılandırma** (`@EnableAutoConfiguration`): classpath'e göre (örn. `spring-web` varsa
   Tomcat + DispatcherServlet) bean'ler otomatik kurulur.
4. **Gömülü sunucu** başlatılır (web uygulamasıysa — topic 22).
5. **Runner'lar** çalışır (topic 09) ve uygulama hazır olur.

> **Önemli:** Ana sınıfın konumu kritiktir — `@ComponentScan` onun **paketinden aşağısını** tarar.
> Bu yüzden ana sınıf, kök pakette olmalıdır (kod yapısı — topic 39).

## Geliştirme ortamı (IDE) ve Spring Tool Suite

- **Spring Tool Suite (STS):** Eclipse tabanlı, Spring'e özel araçlarla donatılmış IDE (bean
  görselleştirme, Initializr entegrasyonu, Actuator desteği).
- **IntelliJ IDEA:** En yaygın tercih (Ultimate sürümde güçlü Spring desteği).
- **VS Code:** Spring Boot eklentileriyle hafif alternatif.

Hepsinde ortak kolaylık: **Spring Boot DevTools** (`spring-boot-devtools`) — kod değişince otomatik
yeniden başlatma (hot reload), canlı şablon yenileme; geliştirme hızını artırır.

## Özet

Spring Boot projesine başlamanın yollarını öğrendik: web sihirbazı **Initializr** (en yaygın),
hızlı prototip için **CLI**, IDE seçenekleri (**STS**/IntelliJ/VS Code) ve DevTools; ayrıca
**bootstrapping** akışını — `SpringApplication.run` → context → bileşen tarama → otomatik
yapılandırma → gömülü sunucu → runner'lar. Sırada, build araçları ve proje yapısı: **Build Systems,
Starters ve Code Structure**.
