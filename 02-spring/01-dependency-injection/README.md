# Dependency Injection (Bağımlılık Enjeksiyonu)

Önceki bölümde IoC felsefesini ve container'ın nesneleri bizim yerimize oluşturup bağladığını
gördük. Şimdi bu bağlamanın **nasıl** yapıldığına yakından bakıyoruz. Dependency Injection (DI),
bir nesnenin ihtiyaç duyduğu bağımlılıkların ona **dışarıdan verilmesidir**. Spring üç farklı
enjeksiyon türü sunar — ve hangisini seçtiğin, kodunun kalitesini doğrudan etkiler.

## @Autowired ve enjeksiyon noktaları

Spring, bir bean'in bağımlılığını çözmek için `@Autowired` anotasyonunu kullanır (constructor,
setter veya alan üzerinde). Bu, container'a "buraya uygun tipte bir bean enjekte et" der.
Modern Spring'de **tek constructor** varsa `@Autowired` yazmaya bile gerek yoktur; Spring onu
otomatik kullanır.

> Not: Örneklerde, tek dosyalık çalışma senaryosu nedeniyle bean'leri `ctx.register(...)` ile
> doğrudan kaydediyoruz. Gerçek uygulamada bu sınıflar `@ComponentScan` ile otomatik bulunur
> (bir sonraki bölümde göreceğiz).

## 1) Constructor injection (önerilen)

Bağımlılığı constructor parametresi olarak almaktır ve Spring'in **önerdiği** yoldur:

```java
@Service
class UrunServisi {
    private final UrunDeposu depo;        // final olabilir!
    UrunServisi(UrunDeposu depo) { this.depo = depo; }
}
```

Avantajları (Örnek 1, `./Ornek1.java`):

- **Değişmezlik:** Alanlar `final` olabilir → güvenli, durumu değişmeyen nesne.
- **Açık ve zorunlu bağımlılıklar:** Nesne, bağımlılıkları olmadan **var olamaz**; eksik kurulum
  derleme/başlatma anında belli olur, çalışma anında `NullPointerException` ile patlamaz.
- **Test edilebilirlik:** Testte bağımlılığı doğrudan constructor'a verirsin — Spring'e bile
  ihtiyaç kalmaz.

## 2) Setter injection

Bağımlılık, bir setter metodu aracılığıyla, nesne kurulduktan **sonra** enjekte edilir:

```java
@Autowired
void setEposta(EpostaServisi eposta) { this.eposta = eposta; }
```

**Opsiyonel** bağımlılıklar veya çalışma sırasında yeniden yapılandırma gereken durumlar için
uygundur. Dezavantajı: alan `final` olamaz ve nesne, setter çağrılana kadar yarım kurulu kalır.

## 3) Field injection

Bağımlılık doğrudan alana (reflection ile) yazılır:

```java
@Autowired
private EpostaServisi eposta;
```

En kısa görünen ama **en sorunlu** yöntemdir: alan `final` olamaz, bağımlılık "gizlidir"
(constructor'a bakarak göremezsin), ve Spring olmadan birim testi yapmak zorlaşır (reflection
gerekir). Örnek 2 (`./Ornek2.java`) setter ve field injection'ı yan yana gösterir.

> **Pratik kural:** Zorunlu bağımlılıklar için **constructor injection** kullan. Field
> injection'dan kaçın; setter injection'ı yalnızca gerçekten opsiyonel bağımlılıklar için sakla.

## Belirsizliği çözmek: @Primary ve @Qualifier

Bir arayüzün (`Bildirimci`) birden çok uygulaması (e-posta, SMS) bean olarak tanımlıysa, Spring
hangisini enjekte edeceğini bilemez ve hata verir. İki çözüm vardır:

- **`@Primary`**: "Eşit adaylar arasında varsayılan olarak BENİ seç."
- **`@Qualifier("ad")`**: "Tam olarak şu isimli bean'i ver" — açık ve nokta atışı seçim.

```java
@Component @Primary class EpostaBildirimci implements Bildirimci { ... }
@Component("sms")    class SmsBildirimci    implements Bildirimci { ... }

AcilServis(@Qualifier("sms") Bildirimci b) { ... } // açıkça SMS
```

Örnek 3 (`./Ornek3.java`) bir bildirim senaryosunda her ikisini de gösterir: varsayılan servis
`@Primary` olan e-postayı, acil servis ise `@Qualifier` ile SMS'i alır.

## Özet

Dependency Injection'ın üç türünü gördük: constructor (önerilen — final, zorunlu, test
edilebilir), setter (opsiyonel bağımlılıklar) ve field (kaçınılması gereken). Aynı tipte birden
çok bean olduğunda belirsizliği `@Primary` ve `@Qualifier` ile çözdük. Bağımlılıkların nasıl
verildiğini öğrendiğimize göre, sırada bu bean'lerin container içindeki **yaşam döngüsü ve
kapsamları (scope)** var.
