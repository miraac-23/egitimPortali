# IoC Container Türleri: BeanFactory ve ApplicationContext

Spring'in kalbinde **IoC container** vardır: bean'leri oluşturan, bağlayan ve yaşam döngülerini
yöneten motor. Ama "container" tek bir sınıf değildir; iki temel arayüz ve birçok somut uygulama
vardır. Bu bölümde "container nedir, hangi türleri var, hangisini ne zaman seçerim?" sorularını
çalışan örneklerle yanıtlıyoruz. Spring'i gerçekten anlamak, bu iki arayüzü ayırt etmekle başlar.

## İki temel arayüz

```
BeanFactory  (sade IoC motoru)
     ▲
     │  genişletir
     │
ApplicationContext  (kurumsal süper-küme)
```

- **`BeanFactory`** — en alttaki, en sade container. Bean tanımlarını tutar ve **istendiğinde**
  (lazy) bean üretir. Bağımlılık enjeksiyonu yapar, o kadar. Hafif olduğu için çoğunlukla
  framework'lerin iç kısımlarında ve bellek-kısıtlı ortamlarda kullanılır.
- **`ApplicationContext`** — `BeanFactory`'yi **genişleten** zengin container. IoC'nin tüm
  yeteneklerine ek olarak: olay yayını, i18n (MessageSource), kaynak yükleme (ResourceLoader),
  ortam/property çözümü (Environment) ve **post-processor'ların otomatik kaydı**. Gerçek
  uygulamalarda **daima** bunu kullanırsın.

## Fark 1 — Tembel mi, erken mi? (lazy vs eager)

En görünür davranış farkı budur, `Ornek1.java` bunu kanıtlar:

- **`BeanFactory` tembeldir:** Singleton bean'leri bile **ancak sen isteyince** oluşturur. Avantaj:
  başlangıç hızlı. Dezavantaj: yapılandırma hatası (eksik bağımlılık, kötü değer) **çok sonra**,
  bean ilk istendiğinde ortaya çıkar.
- **`ApplicationContext` erkendir (eager):** `refresh()` anında tüm singleton'ları hemen kurar.
  Avantaj: yapılandırma hataları **uygulama açılırken** patlar — "erken patla" (fail-fast)
  ilkesi, üretim için çok değerlidir.

> **Gerçek hayat analojisi:** `BeanFactory`, siparişi ancak müşteri kapıya gelince pişiren bir
> mutfaktır. `ApplicationContext`, açılışta tüm hazırlığı yapan, sorun varsa daha servis
> başlamadan fark eden bir restorandır.

## Fark 2 — ApplicationContext'in eklediği yetenekler

`Ornek3.java` bunları canlı gösterir. `ApplicationContext`, `BeanFactory`'nin üstüne şunları katar:

| Yetenek | Ne işe yarar |
| --- | --- |
| Olay yayını | `publishEvent` / `@EventListener` ile bileşenler gevşek bağlı haberleşir |
| MessageSource | Çok dilli (i18n) mesaj çözümü |
| ResourceLoader | `classpath:` / `file:` / URL kaynaklarını tek arayüzle okuma |
| Environment | property ve **profil** çözümü |
| Post-processor kaydı | `BeanPostProcessor` & `BeanFactoryPostProcessor`'ları **otomatik** bağlar |

Son madde kritiktir: `BeanFactory` kullansaydın, `@Autowired`'ı çözen `AutowiredAnnotationBeanPostProcessor`
gibi işleyicileri bile **elle** kaydetmen gerekirdi. `ApplicationContext` bunu senin için yapar —
bu yüzden anotasyonlar "sihirli" biçimde çalışır.

## ApplicationContext uygulamaları (somut türler)

`ApplicationContext` bir arayüzdür; nereden beslendiğine göre farklı uygulamaları vardır
(`Ornek2.java` ilk ikisini kullanır):

- **`AnnotationConfigApplicationContext`** — `@Configuration` / `@Component` sınıflarından. **Modern
  standart.**
- **`GenericApplicationContext`** — bean'leri **kodla/lambda ile** (functional) kaydettiğin en esnek
  taban. Framework ve test kodu için idealdir.
- **`ClassPathXmlApplicationContext`** / **`FileSystemXmlApplicationContext`** — XML'den. **Eski/legacy**
  projeler; yeni projede önerilmez.
- **`WebApplicationContext`** — web uygulamaları için özelleşmiş tür (Spring MVC kullanır).

> **Spring Boot bağlantısı:** Boot, web uygulamaları için `AnnotationConfigServletWebServerApplicationContext`
> gibi özelleşmiş bir `ApplicationContext` kurar ve `refresh()`'i senin yerine çağırır. Yani Boot'taki
> "sihrin" altında yine bu container hiyerarşisi vardır.

## Sık yapılan hatalar

- **Üretimde `BeanFactory` kullanmak:** Neredeyse hiçbir zaman doğru değildir; otomatik
  post-processor kaydını ve fail-fast davranışını kaybedersin.
- **`refresh()` çağırmayı unutmak:** `GenericApplicationContext`/`AnnotationConfigApplicationContext`'i
  boş constructor'la kurup bean kaydedersen, `refresh()` çağırmadan bean isteyemezsin.
- **Birden çok context yaratmak:** Aynı uygulamada gereksiz yere ikinci bir context açmak, bean'lerin
  iki kez oluşmasına ve şaşırtıcı hatalara yol açar.

## Özet

IoC container'ın iki yüzünü gördük: sade ve tembel `BeanFactory` ile zengin ve erken-kuran
`ApplicationContext`. Aralarındaki lazy/eager farkını ve `ApplicationContext`'in kattığı kurumsal
yetenekleri (olay, i18n, kaynak, environment, otomatik post-processor) örneklerle kanıtladık. Artık
container'a "hangi bean'ler var ve nasıl kurulur?" diye anlatan **bean tanımının** anatomisine
inebiliriz. Sırada: **Bean Definition ve Bean Definition Inheritance**.
