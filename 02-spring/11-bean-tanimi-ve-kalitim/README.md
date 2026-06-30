# Bean Definition ve Bean Definition Inheritance

Container'a "şu bean'i oluştur" derken aslında ona bir **tarif** verirsin: hangi sınıf, nasıl
üretilecek, hangi scope'ta, hangi bağımlılıklarla. Spring bu tarifi bir **`BeanDefinition`**
nesnesinde saklar. Bu bölümde önce bir bean tanımının anatomisini, sonra birden çok tanımın ortak
yapılandırmayı **miras** alabilmesini (definition inheritance) öğreniyoruz. Bu, container'ın "iç
defterini" görmek demektir — Spring'in neden bu kadar esnek olduğunu burada anlarsın.

## Bean Definition: bir bean'in kimlik kartı

Her bean'in arkasında bir `BeanDefinition` vardır. Bu tarif şunları içerir (`Ornek1.java` bunları
canlı okur):

| Alan | Açıklama | Java config karşılığı |
| --- | --- | --- |
| **class / üretim yolu** | Hangi sınıf; constructor mu, factory metodu mu | `@Bean` metodu, dönüş tipi |
| **scope** | singleton, prototype, request, session... | `@Scope("prototype")` |
| **lazy-init** | Erken mi tembel mi kurulsun | `@Lazy` |
| **init / destroy** | Yaşam döngüsü kancaları | `@Bean(initMethod=..., destroyMethod=...)` |
| **constructor args / properties** | Bağımlılık değerleri | metot parametreleri / setter'lar |
| **depends-on** | Önce kurulması gereken bean'ler | `@DependsOn` |

> **Önemli ayrım:** Sen `@Bean`/`@Component` yazarsın; Spring bunları okuyup **senin için**
> `BeanDefinition` üretir. Yani anotasyonlar, tarif yazmanın **kullanışlı yüzüdür**; perde arkasında
> hep bu metadata nesnesi vardır. `Ornek1`'de `@Bean` metotlarının birer **factory metodu** olarak
> kaydedildiğini (class alanı boş, üretim metoda devredilmiş) görürsün.

## Bean'i üretmenin yolları

Bir bean üç şekilde üretilebilir:

1. **Constructor ile** (en yaygın): `new Servis(...)`.
2. **Statik factory metodu ile:** `UrunFabrikasi.uret()` — `Ornek1`'deki `fabrikaUrunu` böyle üretilir.
3. **Instance factory metodu ile:** başka bir bean'in metodundan (`fabrikaBean.uret()`).

Factory metotları, nesne oluşturmanın karmaşık olduğu (önbellek, havuz, koşullu seçim) durumlarda
işe yarar. `@Bean` metodun kendisi zaten bir factory metodudur.

## Bean Definition Inheritance (tanım kalıtımı)

Birden çok bean **aynı** yapılandırmayı paylaşıyorsa, bunu her tanımda tekrarlamak DRY ilkesini
çiğner. Çözüm: ortak ayarları bir **şablon (parent) tanımda** topla; çocuk tanımlar ondan **miras**
alsın. `Ornek2.java` bunu gösterir.

```text
temelAyar (abstract şablon)         -> ulke=TR, paraBirimi=TL
   ├── satisAyar     (miras + ekle) -> + bolum=Satış            => TR, TL, Satış
   └── muhasebeAyar  (miras + EZ)   -> + bolum=Muhasebe, paraBirimi=USD => TR, USD, Muhasebe
```

- **`abstract` şablon:** `setAbstract(true)` — kendisi örneklenmez, yalnızca miras için vardır.
- **Çocuk:** `setParentName("temelAyar")` ile şablonun property'lerini **devralır**, kendininkini
  ekler, gerekirse **ezer (override)**.

Klasik XML'de bu, `<bean parent="temelAyar">` ile yapılırdı. Anotasyon dünyasında bu mekanizmayı
nadiren elle kullanırsın; ama Spring'in iç yapısı, FactoryBean'ler ve bazı kütüphaneler buna dayanır.

> **Dikkat — bu NESNE kalıtımı DEĞİLDİR.** `Ayar` sınıfı hiçbir sınıftan `extends` etmez. Miras
> alınan şey **bean tarifindeki değerlerdir** (property'ler, scope, init metodu...), Java sınıf
> hiyerarşisi değil. İkisini karıştırmak klasik bir kavram hatasıdır.

## Programatik tanım: BeanDefinitionBuilder

Bean'leri her zaman anotasyonla tanımlamazsın; bazen **çalışma zamanında, kodla** üretmen gerekir
(dinamik sayıda bean, eklenti sistemleri, framework kodu). `Ornek3.java` bunun modern yolunu —
`BeanDefinitionBuilder` — gösterir:

```java
ctx.registerBeanDefinition("uygulama",
    BeanDefinitionBuilder.genericBeanDefinition(Uygulama.class)
        .addConstructorArgValue("Sipariş Servisi")
        .addDependsOn("altyapi")   // önce 'altyapi' kurulsun
        .getBeanDefinition());
```

`depends-on`, aralarında **doğrudan bağımlılık olmayan** ama yine de belirli bir **sırada** kurulması
gereken bean'ler için kullanılır (örn. bir bean, başka bir bean'in yan etkisine — tablo oluşturma,
cache ısıtma — güvenir).

## Sık yapılan hatalar

- **Tanım kalıtımını sınıf kalıtımı sanmak:** Yukarıda vurguladık; tarif mirası ≠ `extends`.
- **`abstract` tanımı bean gibi istemek:** `getBean("temelAyar")` çağırmak hata verir; o yalnızca
  şablondur.
- **`depends-on`'u bağımlılık enjeksiyonu yerine kullanmak:** Bean'e başka bir bean **lazımsa**
  onu enjekte et; `depends-on` yalnızca **sıra** garantisi içindir, referans vermez.
- **Factory metodunun `static` olmaması:** Statik factory bekleniyorsa metot `static` olmalı; değilse
  Spring instance factory bean arar.

## Özet

Bir bean'in arkasındaki **`BeanDefinition`** tarifini (sınıf, scope, lazy, init/destroy, üretim yolu)
ve bu tarifin **miras** alınabildiğini gördük. Anotasyonların aslında bu metadata'yı üretmenin
kullanışlı yüzü olduğunu, tanımları kodla da (`BeanDefinitionBuilder`) üretebildiğimizi öğrendik.
Tanımı bir kez kaydedildikten sonra Spring onu kurarken araya girip **değiştirebilen** özel
bileşenler vardır. Sırada tam da onlar var: **Bean Post Processor'lar**.
