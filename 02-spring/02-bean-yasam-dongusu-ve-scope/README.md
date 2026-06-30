# Bean Yaşam Döngüsü ve Scope

Container'ın bean'leri oluşturup bağladığını biliyoruz. Peki bu bean'ler tam olarak **ne zaman**
oluşturulur, **ne zaman** yok edilir ve container içinde **kaç tane** kopyaları olur? Bu sorular
yaşam döngüsü (lifecycle) ve kapsam (scope) ile cevaplanır. Bunları anlamak; kaynakları doğru
açıp kapatmak ve durumlu/durumsuz nesneleri doğru yönetmek için kritiktir.

## Bean yaşam döngüsü

Bir bean dünyaya gelip giderken belirli aşamalardan geçer ve sen bu aşamalara kanca (callback)
takabilirsin. Tipik sıra şudur:

1. **Constructor** çağrılır (nesne oluşur).
2. **Bağımlılıklar enjekte edilir.**
3. **Başlatma (init):** `@PostConstruct` → `afterPropertiesSet()` (InitializingBean) → `@Bean`
   `initMethod`.
4. Bean **kullanılır.**
5. **Yok etme (destroy):** Container kapanırken `@PreDestroy` → `destroy()` (DisposableBean) →
   `@Bean` `destroyMethod`.

```java
@Component
class BaglantiHavuzu {
    @PostConstruct void baslat() { /* kaynağı aç */ }
    @PreDestroy   void kapat()  { /* kaynağı kapat */ }
}
```

`@PostConstruct`/`@PreDestroy` (jakarta.annotation) en yaygın ve framework-bağımsız yoldur;
`InitializingBean`/`DisposableBean` arayüzleri Spring'e özeldir; `@Bean(initMethod=..., 
destroyMethod=...)` ise üçüncü-parti sınıflar için kullanışlıdır. Örnek 1 (`./Ornek1.java`) bir
bağlantı havuzunu modelleyerek tüm sırayı çıktıyla gösterir — `@PostConstruct`'ta açılır,
context kapanınca `@PreDestroy`'da kapanır.

> Destroy geri çağrılarının çalışması için context'in düzgün **kapatılması** gerekir
> (`ctx.close()`). `prototype` kapsamındaki bean'lerde Spring destroy'u yönetmez (aşağıya bak).

## Bean scope'ları

Scope, container'ın bir bean tanımından **kaç örnek** oluşturacağını ve bunların ömrünü belirler.

### singleton (varsayılan)

Spring'de bean'lerin **varsayılan** kapsamıdır: container ömrü boyunca **tek bir örnek** oluşturulur
ve her istekte aynı nesne döner.

```java
@Component class AyarServisi { } // varsayılan: singleton
```

Durumsuz (stateless) servisler, repository'ler ve yapılandırma için idealdir — ki uygulamadaki
bean'lerin büyük çoğunluğu böyledir.

### prototype

Her `getBean()` (veya her enjeksiyon) çağrısında **yeni** bir örnek oluşturulur. Durumlu
(stateful), her kullanımda taze olması gereken nesneler içindir.

```java
@Component @Scope("prototype") class SepetNesnesi { }
```

Örnek 2 (`./Ornek2.java`) ikisini kanıtlar: singleton'da `a1 == a2` (aynı nesne), prototype'ta
`s1 != s2` (farklı nesneler).

### Web kapsamları

Web uygulamalarında ek kapsamlar vardır: **request** (her HTTP isteği için bir bean) ve
**session** (her kullanıcı oturumu için bir bean). Bunları Spring Boot/web bölümünde göreceğiz.

| Scope | Örnek sayısı | Tipik kullanım |
|-------|--------------|----------------|
| singleton | Container'da tek | Durumsuz servis, repository (varsayılan) |
| prototype | Her istekte yeni | Durumlu, kısa ömürlü nesne |
| request | Her HTTP isteğinde | İstek bazlı veri (web) |
| session | Her oturumda | Kullanıcıya özel veri (web) |

## Klasik tuzak: singleton içinde prototype

Çok yapılan bir hata: bir **singleton** servis, bir **prototype** bean'i constructor'da bir kez
enjekte eder. O prototype yalnızca **bir kez** oluşturulduğundan, "her seferinde taze nesne"
beklentisi boşa çıkar. Örnek 3 (`./Ornek3.java`) bu tuzağı gösterir ve çözer: `ObjectProvider<T>`
(veya `@Lookup`) ile prototype'ı **ihtiyaç anında** container'dan taze isteyerek.

```java
@Service class DogruIsleyici {
    private final ObjectProvider<Gorev> saglayici;
    int calistir() { return saglayici.getObject().id(); } // her çağrıda TAZE prototype
}
```

## Özet

Bean yaşam döngüsünü (constructor → enjeksiyon → init → kullanım → destroy) ve buna kanca takmayı
(`@PostConstruct`/`@PreDestroy`); scope'ları (varsayılan singleton, prototype ve web kapsamları)
ve singleton-içinde-prototype tuzağını `ObjectProvider` ile çözmeyi öğrendik. Sırada, bu bean'leri
container'a tanıtmanın farklı yolları: **yapılandırma yöntemleri ve stereotype anotasyonları**.
