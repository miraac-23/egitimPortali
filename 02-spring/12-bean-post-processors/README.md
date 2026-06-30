# Bean Post Processor'lar: Container'ın Genişletme Noktaları

Şimdiye kadar bean'leri **tarif edip** container'ın kurmasını izledik. Peki container, bean'leri
kurarken araya girip onları **gözlemlemek, doğrulamak veya değiştirmek** istersen? İşte Spring'in
en güçlü genişletme noktaları burada devreye girer: **BeanPostProcessor** ve
**BeanFactoryPostProcessor**. Bu iki arayüz, Spring'in "sihirli" görünen birçok özelliğinin
(`@Autowired`, `@Transactional`, `@Value`, AOP) gerçekte nasıl çalıştığının cevabıdır. Bu bölümü
kavrarsan, Spring artık bir kara kutu olmaktan çıkar.

## İki farklı kanca, iki farklı an

Container'ın başlatılması iki aşamalıdır ve her aşamanın kendi post-processor'ı vardır:

```
1) Tüm bean TANIMLARI okundu      ─┐
   → BeanFactoryPostProcessor      │  bean'ler HENÜZ oluşmadı; TARİFLERİ değiştirebilirsin
2) Bean'ler tek tek oluşturuluyor ─┘
   her bean için:
     - bağımlılıklar enjekte edilir
     - [BeanPostProcessor.before]  ←  init ÖNCESİ
     - init metodu (@PostConstruct / initMethod)
     - [BeanPostProcessor.after]   ←  init SONRASI (genelde proxy burada sarılır)
```

| | **BeanFactoryPostProcessor** | **BeanPostProcessor** |
| --- | --- | --- |
| Neye dokunur | Bean **tanımına** (`BeanDefinition`) | Bean **nesnesine** (örneğe) |
| Ne zaman | Bean'ler **oluşmadan önce** | Her bean **oluştuktan sonra** |
| Tipik iş | `${...}` property çözümü, tanım düzenleme | doğrulama, ölçüm, **proxy sarma (AOP)** |
| Örnek | `PropertySourcesPlaceholderConfigurer` | `AutowiredAnnotationBeanPostProcessor` |

## BeanPostProcessor — nesneye dokunan kanca

`Ornek1.java`, her bean için `postProcessBeforeInitialization` ve `postProcessAfterInitialization`
metotlarının nasıl çağrıldığını gösterir. Kritik içgörü şudur: **`@Autowired`, `@Value`,
`@PostConstruct` gibi anotasyonları çözen bileşenlerin kendileri birer `BeanPostProcessor`'dır.**
Yani anotasyonların "sihri" tam olarak bu kancadan gelir — Spring, senin yazabileceğin türden
işleyicileri kendi içinde kullanır.

## BeanFactoryPostProcessor — tanıma dokunan kanca

`Ornek2.java`, bean'ler **daha oluşmadan** onların tariflerini değiştirir: `sunucuAyari` tanımındaki
`port` değerini 8080'den 9090'a çeker. Bean sonradan oluşturulduğunda zaten yeni değerle kurulur.
Bu mekanizma sayesinde Spring, `application.properties`'teki `${db.url}` gibi yer tutucuları
çözer — bunu yapan `PropertySourcesPlaceholderConfigurer` da bir `BeanFactoryPostProcessor`'dır.

> **`static` kuralı:** Post-processor'ları `@Configuration` içinde `@Bean` ile tanımlarken metodu
> **`static`** yap. Post-processor'lar container yaşam döngüsünün **çok erken** safhasında gerekir;
> `static` olmayan bir `@Bean` metodu, `@Configuration` sınıfının erkenden örneklenmesini zorlar ve
> Spring uyarı verir. Örneklerde bu yüzden `@Bean static ...` kullandık.

## Gerçek hayat: AOP'nin temeli proxy sarma

`Ornek3.java` en aydınlatıcı örnektir. Bir `BeanPostProcessor`, `after` kancasında bean'i bir
**proxy** ile sarar; çağrılar önce proxy'den geçip loglanır/ölçülür, sonra gerçek nesneye iner —
**iş mantığına hiç dokunmadan**. Spring AOP, `@Transactional`, `@Cacheable`, `@Async` gibi her şey
tam olarak bu desenle çalışır: bir post-processor, ilgili bean'leri uygun bir proxy ile sarmalar.

> **Önceki bölümle bağ:** `04-aop` konusunda AOP'yi `@Aspect`/`@Around` üst seviyesinden görmüştük.
> Burada onun **motorunu** gördük: aspect'leri bean'lere uygulayan şey, perde arkasındaki bir
> `BeanPostProcessor`'dır (`AnnotationAwareAspectJAutoProxyCreator`).

## Sık yapılan hatalar

- **İki kancayı karıştırmak:** Tanımı değiştireceksen `BeanFactoryPostProcessor`, nesneyi
  saracaksan/doğrulayacaksan `BeanPostProcessor`. Yanlış kancada doğru işi yapamazsın.
- **`static` unutmak:** Yukarıdaki uyarı; post-processor `@Bean` metotları `static` olmalı.
- **`after` kancasında farklı tip döndürmek:** Proxy sararken döndürdüğün nesne, beklenen
  **arayüzü** uygulamalı; yoksa enjeksiyon "tip uymuyor" diye patlar (JDK proxy yalnızca arayüz
  proxy'ler, bu yüzden bean'ler arayüz üzerinden enjekte edilmeli — ya da CGLIB sınıf proxy'si).
- **Post-processor'ın kendini işlemesini beklemek:** Post-processor bean'leri, diğer bean'lerden
  önce kurulur; bu yüzden onlara `@Autowired` ile karmaşık bağımlılıklar enjekte etmek sorun olabilir.

## Özet

Spring'in iki temel genişletme noktasını öğrendik: tanımları değiştiren **BeanFactoryPostProcessor**
ve nesneleri işleyen/saran **BeanPostProcessor**. `@Autowired` çözümünden property placeholder'lara,
AOP'den `@Transactional`'a kadar Spring'in "sihrinin" bu kancalardan geldiğini gördük. Container'ın
iç işleyişini artık biliyoruz; şimdi bağımlılıkları enjekte etmenin daha zengin biçimlerine
dönelim. Sırada: **İç bean'ler (inner beans) ve koleksiyon enjeksiyonu**.
