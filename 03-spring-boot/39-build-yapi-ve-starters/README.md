# Build Systems, Starter'lar ve Kod Yapısı

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Build araçları/proje yapısıyla ilgili kavramsal
> bir konudur. (Listendeki "Build Systems", "Starters" ve "Code Structure" başlıklarını birlikte
> ele alır. Build araçlarının Java tarafı için topic 18-java/build-araclari'na da bak.)

Bir Spring Boot projesini ayağa kalkıp çalıştıran üç temel: bağımlılıkları/derlemeyi yöneten **build
aracı**, bağımlılık kümelerini paketleyen **starter'lar** ve sınıfların yerleşimini belirleyen
**kod yapısı**. Üçü birlikte projenin sağlam ve sürdürülebilir olmasını sağlar.

## Build sistemleri: Maven ve Gradle

Spring Boot iki büyük build aracını destekler:

- **Maven** (`pom.xml`, XML): Olgun, yaygın, öngörülebilir; kurumsal dünyada baskın.
- **Gradle** (`build.gradle`, Groovy/Kotlin DSL): Daha kısa, esnek, hızlı (artımlı build, build
  cache); bu projenin kullandığı araç.

Her ikisinde de Spring Boot bir **eklenti** ve **bağımlılık yönetimi** sağlar:

```gradle
// build.gradle (bu projenin kullandığı)
plugins {
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'   // sürümleri Spring Boot yönetir
}
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'   // sürüm yazmaya gerek yok!
}
```

> **Bağımlılık yönetimi (BOM):** Spring Boot, birbiriyle **uyumlu** kütüphane sürümlerini bir BOM
> (Bill of Materials) ile sabitler. Bu yüzden starter'lara sürüm numarası yazmazsın — Spring Boot
> sürümü, tüm bağımlılıkların uyumlu sürümlerini belirler ("sürüm cehennemi"ni önler).

## Starter'lar

**Starter**, belirli bir yetenek için gereken bağımlılıkları **tek bir pakette** toplayan kolaylık
modülüdür. `spring-boot-starter-web` eklediğinde, Spring MVC + Tomcat + Jackson + validation gibi
onlarca uyumlu bağımlılık birlikte gelir — hepsini tek tek eklemekle uğraşmazsın.

| Starter | Getirdiği |
|---------|-----------|
| `spring-boot-starter-web` | REST/MVC, gömülü Tomcat, Jackson |
| `spring-boot-starter-data-jpa` | JPA, Hibernate, transaction |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-validation` | Bean Validation |
| `spring-boot-starter-actuator` | Üretim metrikleri/sağlık |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, MockMvc |

(Bu projenin `build.gradle`'ı tam da bu starter'ları kullanır.) Starter eklemen + **otomatik
yapılandırma** (topic 38), "yapılandırma yerine kural" (convention over configuration) felsefesinin
özüdür: bağımlılığı ekle, çalışsın.

## Kod yapısı (code structure)

Spring Boot kod yapısı için kuralcıdır — özellikle **ana sınıfın yeri**:

```
com.egitim.uygulama           <- ana sınıf BURADA (kök paket)
├── UygulamaApp.java          (@SpringBootApplication)
├── controller/               <- web katmanı (@RestController)
├── service/                  <- iş katmanı (@Service)
├── repository/               <- veri katmanı (@Repository)
├── model/ (veya domain/)     <- entity/DTO'lar
└── config/                   <- @Configuration sınıfları
```

> **Kritik kural:** `@SpringBootApplication`'lı ana sınıf **kök pakette** olmalı; çünkü
> `@ComponentScan` onun paketinden **aşağısını** tarar. Ana sınıfı alt bir pakete koyarsan, üst/
> kardeş paketlerdeki bean'ler bulunamaz (sık yapılan hata).

Katman bazlı (yukarıdaki) yapı küçük/orta projeler için iyidir; büyük projelerde **özellik bazlı
(feature/package-by-feature)** yapı (her özellik kendi paketinde controller+service+repo) tercih
edilebilir.

## Özet

Bir Spring Boot projesinin üç temelini öğrendik: **build araçları** (Maven/Gradle + Spring Boot
eklentisi + BOM ile sürüm yönetimi), **starter'lar** (uyumlu bağımlılık kümeleri — "ekle ve çalış")
ve **kod yapısı** (katmanlı yerleşim + ana sınıfın kök pakette olması kuralı). Bunlar "convention
over configuration" felsefesinin somut hâlidir. Sırada, gömülü sunucu yerine harici sunucuya
dağıtım: **WAR (Tomcat) dağıtımı**.
