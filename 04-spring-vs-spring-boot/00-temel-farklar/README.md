# Spring ve Spring Boot: Temel Farklar

Bu eğitimde önce Spring Framework'ün çekirdeğini, sonra Spring Boot'u öğrendik. Şimdi ikisini
yan yana koyup farkı net görelim — çünkü "Spring mi Spring Boot mu?" sorusu yeni başlayanların en
sık karıştırdığı konudur. Kısa cevap: **rakip değiller; Spring Boot, Spring'in üzerine kurulu bir
kolaylık katmanıdır.** Aynı işi her ikisiyle de yapıp aradaki farkı somutlaştıracağız.

## İlişki: rakip değil, katman

- **Spring Framework**, temel yapı taşlarını sağlar: IoC container, DI, AOP, web (MVC), veri
  erişimi, güvenlik. Güçlüdür ama bu parçaları **sen** birleştirir, yapılandırır, sürümlerini
  uyumlar ve (web ise) bir sunucu kurarsın.
- **Spring Boot**, Spring'in üzerine oturur ve kurulum yükünü neredeyse sıfıra indirir:
  otomatik yapılandırma, starter bağımlılıkları, gömülü sunucu, üretim araçları.

> Spring Boot'u kullanırken **hâlâ Spring'i** kullanıyorsun. Boot sadece "tekrar eden kurulum
> kararlarını" senin yerine, makul varsayılanlarla veriyor.

## Aynı iş, iki yöntem

Bu farkı en iyi, aynı görevi her ikisiyle yaparak görürsün. Görev: bir H2 veritabanına bağlanıp
ürün kaydetmek.

**Düz Spring (Örnek 1, `./Ornek1.java`):** Her şeyi elle yapılandırırsın —
`ApplicationContext`'i kur, `DataSource`'u tanımla, `JdbcTemplate`'i oluştur, bean'leri bağla:

```java
@Bean DataSource dataSource() { /* sürücü, URL, kullanıcı elle */ }
@Bean JdbcTemplate jdbcTemplate(DataSource ds) { return new JdbcTemplate(ds); }
```

**Spring Boot (Örnek 2, `./Ornek2.java`):** `DataSource` veya `JdbcTemplate` **tanımlamazsın**;
Boot, classpath'te H2 + spring-jdbc görüp ikisini de otomatik kurar. Sen sadece enjekte edip
kullanırsın:

```java
CommandLineRunner selfTest(JdbcTemplate jdbc) { /* doğrudan kullan */ }
```

Aynı sonuç, çok daha az kod. Örnek 3 (`./Ornek3.java`) bunu sayılarla gösterir: birkaç satır kod
yazarsın ama container'da yüzlerce bean (Tomcat, Jackson, DataSource, JPA...) **otomatik**
oluşturulur — üstelik çalışan bir web sunucun olur, hiç sunucu kurmadan.

## Karşılaştırma tablosu

| Konu | Spring Framework | Spring Boot |
|------|------------------|-------------|
| Yapılandırma | Manuel (XML/Java config) | Otomatik (auto-configuration) |
| Bağımlılıklar | Tek tek seç + sürüm uyumla | Starter'lar (uyumlu set) |
| Sunucu | Harici Tomcat kur/yapılandır | Gömülü (jar içinde) |
| Çalıştırma | WAR + sunucu | `java -jar` |
| Başlangıç | Çok kurulum | `start.spring.io` → dakikalar |
| Varsayılanlar | Sen verirsin | Makul varsayılanlar hazır |
| Üretim araçları | Elle ekle | Actuator vb. hazır |
| Esneklik | Tam kontrol | Varsayılanları geçersiz kılabilirsin |

## "Sihir" değil, akıllı varsayılanlar

Spring Boot'un otomatik yapılandırması sihir değildir: classpath'e bakar (`@ConditionalOnClass`,
`@ConditionalOnMissingBean` gibi koşullarla) ve "H2 var ama DataSource tanımlı değilse, bir H2
DataSource kur" gibi kararlar verir. Sen kendi bean'ini tanımladığın an, Boot geri çekilir
(senin tanımın kazanır). Yani kontrol her zaman sende kalır; Boot sadece sen susunca makul olanı
yapar.

## Özet

Spring ve Spring Boot'un rakip değil, katman ilişkisinde olduğunu; aynı işi düz Spring'de elle
(Örnek 1), Boot'ta otomatik (Örnek 2) yaptığımızı ve Boot'un ne kadar altyapıyı üstlendiğini
(Örnek 3) gördük. Bir sonraki konuda kararı netleştiriyoruz: **hangi durumda hangisini seçmeli ve
düz Spring'den Boot'a nasıl geçilir?**
