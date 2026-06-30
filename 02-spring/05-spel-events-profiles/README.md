# SpEL, Olaylar (Events) ve Profiller

Spring çekirdeğinin son durağında, container'ı daha esnek ve güçlü kılan üç yardımcı yeteneği
işliyoruz: ifadeleri çalışma anında değerlendiren **SpEL**, bileşenleri gevşek bağlayan **olay
mekanizması** ve uygulamayı ortama göre yapılandıran **profiller**. Bunların üçü de, Spring'le
yazdığın gerçek uygulamalarda sık sık karşına çıkacak.

## SpEL (Spring Expression Language)

SpEL, metin olarak yazılmış ifadeleri çalışma anında değerlendiren güçlü bir dildir. Onu zaten
gizliden gizliye kullandın: `@Value("#{...}")` bir SpEL ifadesidir. Doğrudan da kullanılabilir:

```java
ExpressionParser parser = new SpelExpressionParser();
parser.parseExpression("2 + 3 * 4").getValue();              // 14
parser.parseExpression("tutar > 1000 ? tutar * 0.1 : 0")     // bir nesne üzerinde
       .getValue(new StandardEvaluationContext(sepet), Double.class);
```

SpEL; aritmetik, mantıksal, ternary, metot çağrısı, koleksiyon projeksiyonu/filtreleme
(`.![...]`, `.?[...]`) ve nesne özelliklerine erişimi destekler. Örnek 1 (`./Ornek1.java`) bunu
gerçek bir senaryoda kullanır: **indirim kurallarını koda gömmek yerine** ifade olarak tutar ve
bir sepet üzerinde değerlendirir. Böylece kuralları yeniden derlemeden (örneğin bir
yapılandırmadan okuyarak) değiştirebilirsin.

> SpEL Spring'in birçok yerinde karşına çıkar: `@Value`, Spring Security ifadeleri
> (`@PreAuthorize("hasRole('ADMIN')")`), `@Cacheable` koşulları, Spring Integration yönlendirme.

## Olaylar (Application Events)

Bazen bir şey olduğunda (sipariş oluştu, kullanıcı kaydoldu) birden çok bileşenin tepki vermesi
gerekir: e-posta gönder, analitiğe yaz, fatura kes... Bunları doğrudan servise gömmek, servisi
o bileşenlere **sıkıca bağlar**. Spring'in olay mekanizması bu bağı koparır.

Yayıncı (publisher) bir olay nesnesi yayınlar; dinleyiciler (`@EventListener`) bağımsızca tepki
verir. Yayıncı, kimin dinlediğini **bilmez**:

```java
class SiparisServisi {
    private final ApplicationEventPublisher yayinci;
    void siparisOlustur(...) {
        yayinci.publishEvent(new SiparisOlusturuldu(urun, tutar)); // kime gittiğini bilmez
    }
}
@Component class EpostaDinleyici {
    @EventListener void on(SiparisOlusturuldu e) { /* e-posta gönder */ }
}
```

Örnek 2 (`./Ornek2.java`) bir sipariş olayına e-posta ve analitik bileşenlerinin nasıl bağımsızca
tepki verdiğini gösterir. Yeni bir tepki eklemek için servisi değil, yalnızca yeni bir dinleyici
eklersin. Bu, daha önce gördüğümüz **Observer deseninin** Spring'e gömülü, hazır hâlidir.

> İhtiyaç hâlinde `@EventListener` + `@Async` ile olay dinleyicileri asenkron (ayrı thread'de) de
> çalıştırılabilir; ayrıca `@TransactionalEventListener` ile olay, transaction commit'ine
> bağlanabilir.

## Profiller (@Profile)

Aynı uygulama farklı ortamlarda (geliştirme, test, üretim) farklı davranmalıdır: geliştirmede
sahte/bellek-içi bir ödeme servisi yeterken, üretimde gerçek banka entegrasyonu gerekir.
**Profiller** bunu yönetir:

```java
@Component @Profile("dev")  class SahteOdeme  implements OdemeServisi { ... }
@Component @Profile("prod") class GercekOdeme implements OdemeServisi { ... }
```

Aktif profil belirlendiğinde Spring yalnızca ona ait bean'leri oluşturur; geri kalanlar hiç
yüklenmez. Aktif profil genelde `spring.profiles.active=prod` (properties), ortam değişkeni veya
JVM parametresiyle verilir. Örnek 3 (`./Ornek3.java`) `dev` ve `prod` profillerinde aynı kodun
nasıl farklı bir `OdemeServisi` aldığını gösterir.

> Profiller; ortama özel veritabanı yapılandırması, sahte/gerçek entegrasyonlar ve test
> izolasyonu için günlük olarak kullanılır. Spring Boot'ta `application-dev.yml` /
> `application-prod.yml` gibi profil-bazlı yapılandırma dosyaları da vardır (Boot bölümünde).

## Diğer çekirdek yetenekler

Bu bölümdeki üçü dışında, Spring çekirdeğinin günlük kullanımda işine yarayacak başka araçları da
vardır: bean tanımlarını veya bean'leri özelleştirmek için `BeanFactoryPostProcessor` /
`BeanPostProcessor`, kaynak (resource) yükleme soyutlaması (`Resource`, `ResourceLoader`) ve
property kaynakları (`@PropertySource`). Bunları ihtiyaç doğdukça keşfedeceksin.

## Özet

SpEL ile ifadeleri çalışma anında değerlendirmeyi (ve kuralları koddan ayırmayı), olay
mekanizmasıyla bileşenleri gevşek bağlamayı ve profillerle ortama göre yapılandırmayı öğrendik.
Bununla Spring çekirdeğini — IoC/DI, bean yaşam döngüsü ve scope, yapılandırma ve stereotype'lar,
AOP ve bu yardımcı yetenekleri — tamamlamış olduk. Bu sağlam temelin üzerine, sıradaki adımlarda
**veri erişimi, transaction, validation ve güvenlik** gibi Spring'in uygulama katmanlarını
ekleyeceğiz; ardından **Spring Boot** ile hepsini üretim hızında bir araya getireceğiz.
