# Spring Boot Annotations (Anotasyonlara Genel Bakış)

Spring Boot, "yapılandırmayı koda gömme" felsefesini **anotasyonlarla** hayata geçirir. XML
yapılandırma dosyaları yerine, sınıflarını ve metotlarını anotasyonlarla işaretlersin; Spring
bunları okuyup bean'leri oluşturur, bağlar ve davranışları uygular. Bu konu, en çok kullanılan
Spring Boot anotasyonlarını gruplar halinde, çalışan bir örnekle ele alır. (İlgili konular: stereotip
bileşenler topic 10, yapılandırma topic 14, Java anotasyon temelleri topic 81.)

## Önyükleme (bootstrap)

- **`@SpringBootApplication`**: Ana sınıf. Üç anotasyonu birleştirir:
  - `@SpringBootConfiguration` (yapılandırma sınıfı),
  - `@EnableAutoConfiguration` (otomatik yapılandırma — classpath'e göre),
  - `@ComponentScan` (bileşen tarama — bu paketten aşağısı).

## Bileşen (stereotype) anotasyonları

`@Component` ve özelleşmeleri — Spring bunları tarayıp **bean** yapar:

| Anotasyon | Katman |
|-----------|--------|
| `@Component` | Genel bean |
| `@Service` | İş mantığı (topic 10) |
| `@Repository` | Veri erişimi |
| `@RestController` / `@Controller` | Web (topic 01) |
| `@Configuration` + `@Bean` | Manuel bean tanımı |

## Bağımlılık enjeksiyonu (DI)

- **Constructor injection** (önerilen): Spring, constructor parametrelerini otomatik enjekte eder
  (tek constructor'da `@Autowired` bile gerekmez).
- **`@Autowired`**: Alan/setter enjeksiyonu (constructor tercih edilir).
- **`@Qualifier("ad")`**: Aynı tipten birden çok bean varsa hangisini istediğini belirt.
- **`@Primary`**: Aynı tipten çok bean varsa **varsayılan** olanı işaretle.

```java
@Service @Primary class EpostaGonderici implements BildirimGonderici { ... }
@Service("sms") class SmsGonderici implements BildirimGonderici { ... }

@Component class Servis {
    Servis(BildirimGonderici varsayilan,                    // @Primary -> Eposta
           @Qualifier("sms") BildirimGonderici sms) { ... } // -> Sms
}
```

Örnek 1 (`./Ornek1.java`) `@Primary`/`@Qualifier` ile seçimi, constructor injection'ı, `@Value` ve
`@Bean` fabrika metodunu çalışır biçimde gösterir.

## Web anotasyonları (topic 01)

`@RequestMapping`, `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`, `@RequestBody`,
`@PathVariable`, `@RequestParam`, `@RequestHeader`, `@ResponseStatus`, `@RestControllerAdvice`,
`@CrossOrigin`.

## Yapılandırma ve koşullu wiring

- **`@Value("${anahtar}")`**: Tekil yapılandırma değeri (topic 14).
- **`@ConfigurationProperties`**: Tipli/gruplu yapılandırma (topic 14).
- **`@Profile("prod")`**: Bean'i yalnızca belirli profilde yükle.
- **`@Conditional...`** (`@ConditionalOnProperty`, `@ConditionalOnMissingBean`...): Otomatik
  yapılandırmanın kalbi — koşula göre bean oluştur (Spring Boot'un "akıllı varsayılanları" bunlarla
  çalışır).

## Davranışsal anotasyonlar

`@Transactional` (transaction — topic 02-spring/07), `@Cacheable`/`@Async`/`@Scheduled` (topic 05),
`@Valid`/`@Validated` (doğrulama — topic 03), `@PreAuthorize`/`@Secured` (yetki — topic 04).

## Özet

Spring Boot anotasyonlarını gruplar halinde öğrendik: önyükleme (`@SpringBootApplication` ve üç
bileşeni), stereotip bileşenler, DI (`@Autowired`/`@Qualifier`/`@Primary`/constructor injection —
Örnek 1), web, yapılandırma/koşullu (`@Value`/`@ConfigurationProperties`/`@Conditional`) ve
davranışsal (`@Transactional`/`@Cacheable`/`@Async`) anotasyonlar. Anotasyonlar Spring Boot'un
"sihrinin" arkasındaki mekanizmadır. Sırada, gömülü sunucu yapılandırması: **Tomcat port ve sunucu
ayarları**.
