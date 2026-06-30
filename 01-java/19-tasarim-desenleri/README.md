# Tasarım Desenleri (Design Patterns)

Yazılımcılar yıllar içinde aynı problemlerle defalarca karşılaştı: "tek bir örnek nasıl
garanti edilir?", "nesne oluşturma mantığı nasıl gizlenir?", "uyumsuz iki arayüz nasıl
birleştirilir?", "bir algoritma çalışma zamanında nasıl değiştirilir?". **Tasarım desenleri**,
bu tekrarlayan problemlere verilmiş, denenmiş ve isimlendirilmiş çözüm kalıplarıdır. Onları
bilmek hem daha sağlam kod yazmanı hem de diğer geliştiricilerle ortak bir dil konuşmanı sağlar.
Bu bölümde her üç kategoriden, gerçek hayat senaryolarıyla toplam dokuz deseni göreceğiz.

> Desenler birer "kopyala-yapıştır kod" değil, **fikirdir**; probleme göre uyarlarsın. Ayrıca
> her yere desen serpiştirmek de hatadır — deseni ihtiyaç doğunca, yerinde kullan.

## Üç kategori

Klasik (Gang of Four) sınıflandırma üç gruptur:

- **Yaratımsal (creational):** Nesne oluşturmayı yönetir — Singleton, Factory Method, Builder.
- **Yapısal (structural):** Nesneleri/sınıfları bir araya getirir — Adapter, Decorator, Facade.
- **Davranışsal (behavioral):** Nesneler arası iletişimi düzenler — Strategy, Observer, Command.

## Yaratımsal desenler

Örnek 1 (`./Ornek1.java`) bir bildirim sistemi üzerinden üç deseni gösterir:

- **Singleton:** Bir sınıfın tüm uygulamada **tek örneği** olmasını garanti eder; yapılandırma,
  loglama, bağlantı havuzu gibi durumlarda kullanılır. Anahtarı `private` constructor ve tek
  statik örnektir. *(Spring'de bean'ler zaten varsayılan olarak singleton'dır; framework bunu
  senin yerine yönetir.)*
- **Factory Method:** Nesne oluşturma kararını tek yerde toplar; çağıran kod somut sınıfları
  bilmez, sadece "bana bir `email` bildirimi ver" der.
- **Builder:** Çok sayıda (özellikle opsiyonel) alanı olan nesneyi okunaklı, zincirleme kurar
  ve uzun/karışık constructor'lardan kurtarır.

## Yapısal desenler

Örnek 2 (`./Ornek2.java`) üç yapısal deseni gerçek senaryolarla gösterir:

- **Adapter:** Uyumsuz bir arayüzü, beklediğin arayüze çevirir. Elindeki eski/üçüncü-parti bir
  ödeme API'sini (`makePayment(kurus)`) yeni arayüzüne (`ode(tutar, paraBirimi)`) uyarlar.
  Eski kodu değiştiremediğinde hayat kurtarır.
- **Decorator:** Bir nesneye, sınıfını değiştirmeden çalışma zamanında davranış/özellik ekler.
  Sade kahveyi süt ve şekerle "sarmalayarak" hem açıklamasını hem fiyatını büyütürüz.
  *(Java'nın I/O akışları — `BufferedReader(new FileReader(...))` — bu desenin ta kendisidir.)*
- **Facade:** Karmaşık bir alt sistemi (stok + ödeme + kargo) tek, basit bir arayüzün
  (`siparisVer`) arkasına gizler; istemciyi ayrıntıdan korur.

## Davranışsal desenler

Örnek 3 (`./Ornek3.java`) üç davranışsal deseni uygular:

- **Strategy:** Bir algoritmayı, onu kullanan koddan ayırıp çalışma zamanında değiştirilebilir
  kılar. Kargo ücreti hesabını `if/else` yığınına gömmek yerine her stratejiyi ayrı tutarız;
  functional interface ve lambda'lar bunu neredeyse bedava hale getirir.
- **Observer:** Bir nesnenin durumu değişince ona **abone** olanların otomatik haberdar
  olmasını sağlar. Stok tükenince/gelince tüm aboneler (müşteri, analitik) bilgilendirilir.
  *(Spring'in `ApplicationEvent`/`@EventListener` mekanizması da özünde Observer'dır.)*
- **Command:** Bir işlemi **nesneye** dönüştürür; böylece onu saklayabilir, sıraya koyabilir ve
  **geri alabilirsin (undo)**. Metin düzenleyicide her yazma bir komuttur ve geçmişten geri
  alınabilir.

## Desenler ve Spring

Dikkat ettiysen, neredeyse her desenin yanında "Spring'de şöyle kullanılır" notu var. Bu
tesadüf değil: Spring, bu desenlerin büyük ve zarif bir uygulamasıdır — bean'ler Singleton,
context Factory, `@EventListener` Observer, AOP proxy'leri Proxy/Decorator, `RestTemplate`
yapılandırması Builder mantığıyla çalışır. Bu desenleri tanımak, Spring'i "sihir" olarak değil,
tanıdık mühendislik fikirleri olarak görmeni sağlar.

## Özet

Üç kategoriden dokuz deseni gerçek senaryolarla gördük: **Singleton, Factory, Builder**
(yaratımsal); **Adapter, Decorator, Facade** (yapısal); **Strategy, Observer, Command**
(davranışsal). Ortak temaları, **değişimi yalıtmak** ve **gevşek bağlılık** kurmaktır — OOP ve
soyutlama bölümlerinde başlayan yolculuğun doğal devamı.

Bu bölümle Java'nın temelini ve ekosistemini büyük ölçüde tamamladın. Artık nesneler,
arayüzler, gevşek bağlılık, reflection ve desenler senin için tanıdık. Sırada, dilin yıllar
içinde nasıl evrildiğine bakıyoruz: **Java sürüm evrimi (8 → 25)**.
