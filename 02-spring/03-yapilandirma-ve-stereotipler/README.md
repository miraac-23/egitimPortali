# Yapılandırma Yöntemleri ve Stereotype Anotasyonları

Container'ın bean'leri yönettiğini biliyoruz. Peki container'a "hangi bean'ler var?" diye nasıl
söyleriz? Spring bunun için birkaç yol sunar ve modern projelerde genelde ikisini birlikte
kullanırız: kendi sınıflarımızı **stereotype anotasyonlarıyla** işaretler, kontrol etmediğimiz
sınıfları ise **Java config** ile bean yaparız. Bu bölümde bean tanımlamanın yollarını, değer
enjeksiyonunu (`@Value`) ve katmanlı mimarinin stereotype'larını işliyoruz.

## Yapılandırma yöntemleri

### Java tabanlı yapılandırma (@Configuration + @Bean)

Bir `@Configuration` sınıfında `@Bean` metotları yazarsın; her metot bir bean üretir. Oluşturmayı
**tam kontrol** edersin — bu yüzden özellikle **kaynak kodunu değiştiremediğin üçüncü-parti
sınıflar** için idealdir:

```java
@Configuration
class JavaConfig {
    @Bean PdfUretici pdfUretici() { return new PdfUretici(); }
    @Bean RaporServisi raporServisi(PdfUretici u) { return new RaporServisi(u); }
}
```

### Anotasyon tabanlı + bileşen tarama (@Component + @ComponentScan)

Kendi sınıflarını `@Component` (veya türevleri) ile işaretlersin; `@ComponentScan` belirtilen
paketi tarayıp bu sınıfları otomatik bean yapar. En yaygın yöntemdir:

```java
@Service class RaporServisi { ... }   // @ComponentScan otomatik bulur
```

### XML tabanlı yapılandırma

Spring'in en eski yöntemidir (`<bean .../>`). Bugün yeni projelerde tercih edilmez ama eski/legacy
projelerde hâlâ karşına çıkabilir.

Örnek 1 (`./Ornek1.java`) aynı bean'i hem Java config hem stereotype ile tanımlayıp ikisinin de
aynı sonucu verdiğini gösterir.

> Örneklerde tek dosya senaryosu nedeniyle `@ComponentScan` yerine `ctx.register(...)` kullanıyoruz;
> davranış aynıdır (sınıf bir bean tanımına dönüşür).

## @Value ile değer enjeksiyonu

Bean'lere yalnızca başka bean'ler değil, **dış yapılandırma değerleri** de enjekte edilir.
`@Value` bunu sağlar:

```java
@Value("${uygulama.ad}")        String ad;        // property'den
@Value("${uygulama.dil:tr}")    String dil;       // yoksa varsayılan "tr"
@Value("#{ 8 * 1024 }")         int maxKb;        // SpEL ifadesi
```

`${...}` bir **property placeholder**'dır (application.properties, ortam değişkeni, JVM `-D`
parametresinden beslenir; `:` ile varsayılan verilir). `#{...}` ise **SpEL** (Spring Expression
Language) ifadesidir ve çalışma anında hesaplanır. Örnek 2 (`./Ornek2.java`) her ikisini de
gösterir (SpEL ile sistem özelliklerine erişim dahil).

## Stereotype anotasyonları ve katmanlı mimari

Spring, `@Component`'in anlamsal türevlerini sunar. Hepsi temelde aynıdır (container için birer
bean), ama hangi **katmana** ait olduklarını belirtir ve bazıları katmana özel davranış ekler:

| Anotasyon | Katman | Ek davranış |
|-----------|--------|-------------|
| `@Component` | Genel | Taban; özel anlamı yok |
| `@Service` | İş mantığı | Anlamsal (servis katmanı) |
| `@Repository` | Veri erişim | İstisnaları `DataAccessException`'a **otomatik çevirir** |
| `@Controller` | Web sunum | İstek eşleme için (REST'te `@RestController`) |

Tipik kurumsal akış üç katmandır: **Controller → Service → Repository**. Her katmanın tek bir
sorumluluğu vardır; bu, kodu okunur, test edilebilir ve sürdürülebilir kılar. Örnek 3
(`./Ornek3.java`) bu akışı stereotype'larla kurar ve hepsinin birer bean olduğunu gösterir.

> `@Repository`'nin sessiz bir süper gücü vardır: veritabanı sürücüsünün fırlattığı tekniğe özel
> istisnaları, Spring'in tutarlı `DataAccessException` hiyerarşisine çevirir — böylece iş kodun
> JDBC/JPA ayrıntısından soyutlanır.

## @Primary ve @Qualifier ile bean seçimi

Önceki bölümde gördüğümüz gibi, aynı tipte birden çok bean olduğunda `@Primary` (varsayılan
tercih) ve `@Qualifier` (açık seçim) belirsizliği çözer. Bunlar yapılandırmanın ayrılmaz parçasıdır.

## Özet

Bean tanımlamanın yollarını (Java config `@Bean`, stereotype + `@ComponentScan`, eski XML),
`@Value` ile property/SpEL enjeksiyonunu ve katmanlı mimarinin stereotype anotasyonlarını
(`@Component`/`@Service`/`@Repository`/`@Controller`) öğrendik. Container'ın temelini artık
tümüyle biliyorsun. Sırada, iş mantığını "kesişen ilgilerden" (loglama, güvenlik, ölçüm) ayıran
güçlü bir teknik: **AOP**.
