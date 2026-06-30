# Spring Mimarisi, Kurulum ve "Merhaba Dünya"

İlk üç bölümde Spring'in **neden** var olduğunu (sıkı bağlılık → IoC/DI) ve enjeksiyonun nasıl
yapıldığını gördük. Bu bölüm pratik bir ara duraktır: Spring'i bir projeye **nasıl eklersin**,
mimarisi **hangi katmanlardan** oluşur ve en küçük çalışan uygulaman neye benzer? TutorialsPoint'in
"Architecture + Environment Setup + Hello World" üçlüsünü tek, bütünlüklü bir konu olarak işliyoruz.

## Spring'in mimarisi: üst üste oturan modüller

Spring "tek parça" bir kütüphane değildir; ihtiyaç duyduğun kadarını alıp kullandığın **modüler**
bir ekosistemdir. Katmanlar, alttan üste şöyle yığılır:

| Katman | Modüller | Görevi |
| --- | --- | --- |
| **Core Container** | Beans, Core, Context, SpEL | IoC/DI'nin kalbi — bean üretir, bağlar, yönetir |
| **AOP** | AOP, Aspects | Kesişen ilgiler (loglama, güvenlik, transaction) |
| **Veri Erişimi** | JDBC, ORM, Transactions | Veritabanı erişimini basitleştirir |
| **Web** | Web MVC, WebFlux | HTTP uygulamaları ve REST API'ler |
| **Test** | Test | Birim/entegrasyon test desteği |

En altta her zaman **Core Container** vardır. Üstteki her şey (AOP, veri, web) onun ürettiği
ve yönettiği bean'lerin üzerine kurulur. Bu yüzden Spring öğrenmek = önce Core Container'ı
(IoC/DI) kavramaktır. `Ornek2.java` bu çekirdeği canlı gösterir: `SpringVersion`, `Environment`
(ortam/property çözümü) ve `Resource` yükleme — hepsi `ApplicationContext`'in yetenekleridir.

> **BeanFactory vs ApplicationContext:** Çekirdekte iki container arayüzü vardır. `BeanFactory`
> en sade IoC motorudur; `ApplicationContext` onun zengin halefidir (olay yayını, i18n, kaynak
> yükleme, ortam/property çözümü, otomatik post-processor kaydı). Modern uygulamalar **daima**
> `ApplicationContext` kullanır. İkisinin farkını bir sonraki bölümde (`10-ioc-container-turleri`)
> ayrıntılı işleyeceğiz.

## Kurulum (Environment Setup)

Pratikte Spring'i elle JAR indirerek değil, bir **derleme aracı** (Maven veya Gradle) ile
bağımlılık olarak eklersin. Çekirdek için gereken tek şey `spring-context`'tir; o, `spring-core`,
`spring-beans` ve `spring-aop`'u geçişli olarak getirir.

**Maven (`pom.xml`):**

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.14</version>
</dependency>
```

**Gradle (`build.gradle`):**

```groovy
implementation 'org.springframework:spring-context:6.1.14'
```

Gereksinimler: **JDK 17+** (Spring Framework 6.x için zorunlu) ve bir IDE (IntelliJ IDEA, Eclipse
ya da VS Code). Eskiden XML yapılandırma ve `web.xml` zorunluydu; bugün **anotasyon + Java config**
standarttır, XML neredeyse hiç gerekmez.

> **Spring vs Spring Boot kurulumu:** Düz Spring'te sürümleri ve bağımlılıkları sen seçersin.
> Spring Boot ise `spring-boot-starter-*` "başlangıç paketleri" ve sürüm yönetimi (BOM) ile bu
> kurulumu neredeyse sıfıra indirir. Boot'u ileride göreceğiz; burada "çıplak" Spring'i öğreniyoruz
> ki Boot'un altında ne olduğunu bilelim.

## "Merhaba Dünya": en küçük çalışan uygulama

`Ornek1.java`, bir Spring uygulamasının iskeletini gösterir; üç adımdan ibarettir:

1. **Container'ı başlat:** `new AnnotationConfigApplicationContext(MerhabaConfig.class)`
2. **Bean'i iste:** `ctx.getBean(MesajServisi.class)` — `new` yok, bağımlılıklar hazır.
3. **Kullan:** metodu çağır.

Dikkat et: hiçbir yerde nesneleri elle `new`'lemiyor, birbirine elle bağlamıyoruz. Tarif
(`@Configuration` + `@Bean`) veriyoruz; gerisini container yapıyor. Bütün Spring, bu desenin
büyütülmüş halidir.

## Kurulumun doğru olduğunu kanıtlamak

`Ornek3.java` bir "sağlık kontrolü"dür: context ayağa kalkıyor mu, Spring sürümü okunuyor mu,
bean üretiliyor mu ve **yaşam döngüsü kancaları** (`initMethod`/`destroyMethod`) tetikleniyor mu?
Çıktıda `[init]` ve `[destroy]` satırlarını görmen, container'ın bean'in tüm yaşam döngüsünü
(oluşturma → kullanım → temizleme) yönettiğini kanıtlar. Bu konuyu `02-bean-yasam-dongusu-ve-scope`
bölümünde derinleştirmiştik.

## Sık yapılan hatalar

- **Yanlış container tipi:** XML config için `ClassPathXmlApplicationContext`, Java config için
  `AnnotationConfigApplicationContext` kullanılır. Karıştırmak "bean bulunamadı" hatası verir.
- **Context'i kapatmamak:** `ApplicationContext` bir kaynaktır. `try-with-resources` ya da
  `ctx.close()` ile kapatmazsan `destroy` kancaları çalışmaz. (Boot bunu senin için yapar.)
- **JDK uyumsuzluğu:** Spring 6 / Boot 3, JDK 17+ ister; JDK 8/11 ile derlenmez.
- **`getBean(String)` ile tip karışıklığı:** İsimle bean isterken dönüş tipi `Object`'tir; mümkünse
  `getBean(Tip.class)` kullan, tip güvenli olur.

## Özet

Spring'in **katmanlı mimarisini** (her şeyin altında Core Container), bir projeye **nasıl
kurulduğunu** (Maven/Gradle + `spring-context`, JDK 17+) ve en küçük **çalışan uygulamayı** gördük.
Artık çekirdeğin kalbine, container'ın kendisine yakından bakabiliriz. Sırada: **IoC Container
türleri** — `BeanFactory` ile `ApplicationContext` arasındaki fark ve hangi container ne zaman.
