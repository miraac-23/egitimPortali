# Spring (Framework)

Bu bölüm, Spring Framework'ün **çekirdeğini** derinlemesine öğretir. Yaklaşımımız problem
odaklıdır: her konuda önce Spring'in çözdüğü gerçek sorunu görür, sonra Spring'in zarif çözümünü
çalışan örneklerle inceleriz. Java bölümünde öğrendiğin nesneler, arayüzler, gevşek bağlılık,
reflection ve tasarım desenleri burada hayat bulur — çünkü Spring, aslında bu fikirlerin büyük ve
olgun bir uygulamasıdır.

Her konu klasöründe akıcı bir anlatım (`README.md`) ve çalıştırılabilir örnekler
(`Ornek1.java`, `Ornek2.java`, ...) vardır. Örnekler tek dosyalık Spring programlarıdır; bu
portaldaki **Çalıştır** düğmesiyle backend'de gerçek bir Spring context'i ayağa kaldırıp çıktısını
anında görebilirsin. (Web MVC örneği gömülü bir Tomcat ile çalışır.)

## Konu haritası

**Spring çekirdeği — temel akış**
- `00-spring-nedir-ve-ioc` — Spring nedir, çözdüğü sorun (sıkı bağlılık), IoC felsefesi
- `01-dependency-injection` — DI türleri (constructor/setter/field), @Qualifier/@Primary
- `02-bean-yasam-dongusu-ve-scope` — bean lifecycle, singleton/prototype kapsamları
- `03-yapilandirma-ve-stereotipler` — @Bean/@Component, @Value, anotasyon & Java tabanlı yapılandırma
- `04-aop` — kesişen ilgiler, @Before/@After/@Around (proxy tabanlı AOP)
- `05-spel-events-profiles` — SpEL, olay mekanizması (events) ve özel olaylar, profiller

**Uygulama katmanları**
- `06-veri-erisimi-jdbctemplate` — JDBC Framework, JdbcTemplate ile veri erişimi
- `07-transaction-yonetimi` — @Transactional, propagation, rollback
- `08-validation` — Bean Validation, özel kurallar, @Validated

**Çekirdeğin derin ve klasik konuları**
- `09-mimari-ve-kurulum` — Spring mimarisi (katmanlar), Maven/Gradle kurulumu, Merhaba Dünya
- `10-ioc-container-turleri` — IoC container'lar: BeanFactory vs ApplicationContext (lazy/eager)
- `11-bean-tanimi-ve-kalitim` — Bean Definition metadata + Bean Definition Inheritance
- `12-bean-post-processors` — BeanPostProcessor & BeanFactoryPostProcessor (anotasyon/AOP'nin motoru)
- `13-ic-bean-ve-koleksiyon-enjeksiyonu` — iç bean'ler + List/Set/Map/Properties ve otomatik koleksiyon
- `14-autowiring` — Beans Auto-Wiring (byType/byName/constructor, @Qualifier/@Primary, ObjectProvider)
- `15-web-mvc` — Spring Web MVC: DispatcherServlet, @Controller/@RestController, istek bağlama
- `16-loglama-log4j` — Log4J / Logback / SLF4J cephesi, seviyeler, parametreli loglama, MDC

## TutorialsPoint başlıkları → bu bölümdeki yeri

Klasik "Spring Tutorial" başlıklarını bu portaldaki konularla eşleyen hızlı tablo:

| TutorialsPoint başlığı | Burada |
| --- | --- |
| Spring – Overview | `00-spring-nedir-ve-ioc`, `09-mimari-ve-kurulum` |
| Spring – Architecture | `09-mimari-ve-kurulum` |
| Spring – Environment Setup | `09-mimari-ve-kurulum` |
| Spring – Hello World Example | `09-mimari-ve-kurulum` (Ornek1) |
| Spring – IoC Containers | `10-ioc-container-turleri` |
| Spring – Bean Definition | `11-bean-tanimi-ve-kalitim` |
| Spring – Bean Scopes | `02-bean-yasam-dongusu-ve-scope` |
| Spring – Bean Life Cycle | `02-bean-yasam-dongusu-ve-scope` |
| Spring – Bean Post Processors | `12-bean-post-processors` |
| Spring – Bean Definition Inheritance | `11-bean-tanimi-ve-kalitim` (Ornek2) |
| Spring – Dependency Injection | `01-dependency-injection` |
| Spring – Injecting Inner Beans | `13-ic-bean-ve-koleksiyon-enjeksiyonu` (Ornek1) |
| Spring – Injecting Collection | `13-ic-bean-ve-koleksiyon-enjeksiyonu` (Ornek2-3) |
| Spring – Beans Auto-Wiring | `14-autowiring` |
| Annotation Based Configuration | `03-yapilandirma-ve-stereotipler`, `14-autowiring` |
| Spring – Java Based Configuration | `03-yapilandirma-ve-stereotipler` (@Configuration/@Bean) |
| Spring – Event Handling in Spring | `05-spel-events-profiles` |
| Spring – Custom Events in Spring | `05-spel-events-profiles` |
| Spring – AOP with Spring Framework | `04-aop`, `12-bean-post-processors` (motor) |
| Spring – JDBC Framework | `06-veri-erisimi-jdbctemplate` |
| Spring – Transaction Management | `07-transaction-yonetimi` |
| Spring – Web MVC Framework | `15-web-mvc` |
| Spring – Logging with Log4J | `16-loglama-log4j` |

> **Not:** `00`–`08` problem-odaklı bir öğrenme akışıdır; `09`–`16` ise klasik referans başlıklarını
> tek tek, derinlemesine karşılar. İkisi birbirini tamamlar: önce "neden ve nasıl"ı yaşayarak öğren,
> sonra her mekanizmayı ayrıntılı incele. Web/REST ve güvenliğin **üretim** hâlini **Spring Boot**
> bölümünde gerçek bir uygulama üzerinde göreceğiz.

## Önerilen sıra

Numaralar önerilen okuma sırasıdır. `00`'dan başla; her konunun sonundaki "Sırada..." cümlesi
seni bir sonraki adıma taşır. `00`–`08`'i bitirdiğinde Spring'in nasıl "düşündüğünü" kavramış
olur; `09`–`16` ile çekirdeği derinleştirir, ardından **Spring Boot** bölümüne sağlam bir temelle
geçersin.
