# Spring Nedir ve IoC Felsefesi

Spring'i öğrenmenin en iyi yolu, onun **hangi sorunu çözdüğünü** anlamaktır. Çünkü Spring bir
"sihir" değil; uzun yıllar boyunca kurumsal Java uygulamalarında tekrar tekrar karşılaşılan bir
acıya verilmiş, çok zarif bir cevaptır. Bu bölümde önce o acıyı kendi gözünle göreceğiz, sonra
elle nasıl hafifletilebileceğini, en sonunda da Spring'in onu nasıl kökünden çözdüğünü adım adım
izleyeceğiz. Bu üç adım, bütün Spring'in temelidir.

## Spring nedir?

Spring, Java için bir **uygulama çatısıdır (framework)**. Çekirdeğinde, nesnelerin yaşam
döngüsünü ve birbirleriyle olan bağımlılıklarını yöneten bir **IoC container** bulunur. Spring
tek bir kütüphane değil, modüler bir ekosistemdir: çekirdek (Core/Context), web (MVC), veri
(Data/JPA), güvenlik (Security), AOP ve daha fazlası. Hepsinin altında aynı felsefe yatar:
**senin işin iş mantığını yazmaktır; "su tesisatını" (nesne oluşturma, bağlama, yönetme) Spring
üstlenir.**

## Asıl sorun: sıkı bağlılık (tight coupling)

Bir sınıf, ihtiyaç duyduğu diğer sınıfları kendi içinde `new` ile yaratırsa ne olur? Örnek 1
(`./Ornek1.java`) tam da bunu gösterir: bir `SiparisServisi`, `MySqlDepo` ve `EpostaGonderici`
nesnelerini içeride oluşturur.

```java
class SiparisServisi {
    private final MySqlDepo depo = new MySqlDepo();             // sıkıca bağlı!
    private final EpostaGonderici gonderici = new EpostaGonderici();
}
```

Bu kod çalışır, ama kırılgandır:

- **Değiştirilemez:** Veritabanını PostgreSQL'e ya da bildirimi SMS'e çevirmek için `SiparisServisi`
  sınıfının **içini** değiştirmen gerekir.
- **Test edilemez:** Birim testinde gerçek e-posta/veritabanı devreye girer; sahte (mock)
  bağımlılık veremezsin.
- **Yeniden kullanılamaz:** Servis, somut sınıflara mıhlanmıştır.

Bu, kurumsal yazılımın klasik kâbusudur: her şey her şeye doğrudan bağlı, küçük bir değişiklik
zincirleme kırılmalara yol açar.

## İlk adım: arayüzler + elle enjeksiyon

Çözümün ilk yarısı, OOP bölümünde öğrendiğin **soyutlamadır**. Somut sınıflara değil
**arayüzlere** (`Depo`, `Bildirimci`) bağlanır ve bağımlılıkları içeride yaratmak yerine
**dışarıdan** (constructor ile) alırız. Buna **Dependency Injection (DI)** denir:

```java
class SiparisServisi {
    SiparisServisi(Depo depo, Bildirimci bildirimci) { ... } // dışarıdan verilir
}
```

Örnek 2 (`./Ornek2.java`) bunu uygular. Artık uygulamayı değiştirmek için servisi değil, yalnızca
**kurulum satırını** değiştiriyoruz; testte sahte bağımlılık vermek bir lambda kadar kolay. Ama
bir bedeli var: tüm nesneleri ve aralarındaki bağlantıları **elle** kurmak zorundayız. Uygulama
büyüdükçe bu "kurulum kodu" devasa ve hataya açık bir hale gelir.

## Çözüm: IoC ve Spring container

İşte Spring'in devreye girdiği yer burası. **IoC (Inversion of Control — Kontrolün Tersine
Çevrilmesi)** fikri şudur: nesneleri oluşturma ve bağlama kontrolünü **sen değil, container**
üstlenir. Sen yalnızca "hangi bean'ler var ve nasıl kurulur" diye **tarif edersin**; gerisini
Spring yapar.

```java
var ctx = new AnnotationConfigApplicationContext(UygulamaConfig.class);
SiparisServisi servis = ctx.getBean(SiparisServisi.class); // hazır, bağımlılıkları enjekte edilmiş
```

Örnek 3 (`./Ornek3.java`) Örnek 2'deki **aynı iş mantığını** alır ama nesne kurulumunu Spring'e
devreder. `@Configuration` sınıfı container'a bean tariflerini verir; container, `siparisServisi`
bean'ine `depo` ve `bildirimci` bean'lerini **otomatik enjekte eder**. Sen artık `new` yazmaz,
bağlantıları elle kurmazsın.

> **Dependency Injection**, IoC'nin en bilinen uygulama biçimidir. "Kontrolün tersine çevrilmesi"
> daha geniş bir ilkedir; DI ise onu bağımlılıklar özelinde hayata geçirir.

## Spring container ve bean'ler

Birkaç temel terim:

- **Bean:** Spring container'ın oluşturduğu ve yönettiği nesne. (Senin `SiparisServisi`'n bir bean'dir.)
- **Container / ApplicationContext:** Bean'leri oluşturan, bağlayan ve yaşam döngülerini yöneten
  Spring'in kalbi. `ApplicationContext`, `BeanFactory`'nin zengin halefidir (olay yayını,
  uluslararasılaştırma, kaynak yükleme gibi ek yeteneklerle).
- **Configuration metadata:** Container'a "hangi bean'ler var?" diye söyleyen tarif (Java config,
  anotasyonlar veya eski XML).

## Spring ekosistemine kısa bakış

- **Core / Context:** IoC container, DI, bean yönetimi (bu bölümün konusu).
- **AOP:** Kesişen ilgileri (loglama, güvenlik, transaction) iş mantığından ayırma.
- **Data Access / JDBC / ORM / Data JPA:** Veritabanı erişimini kolaylaştırma.
- **Web / MVC:** HTTP üzerinden çalışan uygulamalar ve REST API'ler.
- **Security:** Kimlik doğrulama ve yetkilendirme.

Bu eğitimde önce **çekirdeği** (IoC/DI, bean'ler, yapılandırma, AOP) derinlemesine işleyeceğiz;
ardından Spring Boot bölümünde tüm bunların üretim uygulamalarına nasıl dönüştüğünü göreceğiz.

## Özet

Spring'in var oluş sebebini bizzat yaşayarak gördük: sıkı bağlılık problemini (Örnek 1),
arayüzler + elle DI ile kısmi çözümü (Örnek 2) ve Spring IoC container ile tam çözümü (Örnek 3).
Çıkarım net: **bağımlılıkları kendin yaratma; container onları senin için oluşturup enjekte
etsin.** Bu felsefe, bundan sonraki her Spring konusunun altında yatıyor. Sırada, bu enjeksiyonun
nasıl yapıldığına yakından bakıyoruz: **Dependency Injection türleri**.
