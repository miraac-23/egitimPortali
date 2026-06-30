# İç Bean'ler (Inner Beans) ve Koleksiyon Enjeksiyonu

`01-dependency-injection` bölümünde tek bir bağımlılığı enjekte etmeyi öğrendik. Gerçek
uygulamalarda ise enjeksiyon çoğu zaman daha zengindir: bazen bir bağımlılık **yalnızca tek bir
yere aittir** (iç bean), bazen de bir bean'e **bir değer demeti** (liste, küme, harita) ya da
**aynı tipteki tüm bean'ler** verilmek istenir. Bu bölüm, enjeksiyonun bu üç pratik biçimini
işler — TutorialsPoint'in "Injecting Inner Beans" + "Injecting Collection" başlıklarını birlikte.

## İç bean (inner bean): paylaşılmayan, gömülü bağımlılık

Bir bağımlılık yalnızca tek bir bean tarafından kullanılacaksa ve başka kimsenin ona erişmesine
gerek yoksa, onu container'a **ayrı bir bean olarak kaydetmek gereksizdir**. Bunun yerine sahibinin
**içinde anonim olarak** yaratırsın. `Ornek1.java` farkı gösterir:

```java
@Bean Motor paylasilanMotor() { return new Motor("V8"); }          // top-level: paylaşılır
@Bean Araba arabaPaylasilan() { return new Araba(paylasilanMotor()); }
@Bean Araba arabaIcBean()     { return new Araba(new Motor("Elektrik")); } // İÇ BEAN
```

`Elektrik` motoru bir **iç bean**'dir: sahibinin içinde `new` ile yaratıldı, container'a
kaydedilmedi. Bu yüzden `getBeanNamesForType(Motor.class)` yalnızca `paylasilanMotor`'u listeler;
elektrik motoruna ne isimle ne de `getBean` ile dışarıdan erişilebilir.

| | Top-level bean | İç bean |
| --- | --- | --- |
| Container'da kayıtlı | Evet (isim + tip ile erişilir) | Hayır (anonim) |
| Paylaşılır mı | Birçok bean enjekte edebilir | Yalnızca sahibi kullanır |
| Ne zaman | Birden çok yerde gerekli | Tek yere ait, kapsüllenmeli |

> **Klasik XML karşılığı:** İç bean, XML'de `<bean>` etiketinin içine gömülü başka bir `<bean>`
> ile yazılırdı (id'siz). Java config'te bu, doğrudan `new` çağrısına ya da `private @Bean`'e karşılık
> gelir. Fikir aynıdır: **kapsülleme** — kullanılmayacak bir bağımlılığı global isim alanına sokma.

## Koleksiyon enjeksiyonu: List, Set, Map, Properties

Bir bean'e tek değer değil, bir **değer demeti** gerekebilir: izinli IP listesi, rol kümesi, HTTP
başlık haritası, ayar çiftleri... Spring dört koleksiyon tipini de enjekte eder (`Ornek2.java`):

| Tip | Özellik | Tipik kullanım |
| --- | --- | --- |
| `List` | Sıralı, tekrar olabilir | İşleme sırası önemli filtreler/adımlar |
| `Set` | Sırasız, tekrarsız | Roller, benzersiz etiketler |
| `Map` | Anahtar→değer | HTTP başlıkları, kod→etiket eşlemesi |
| `Properties` | String→String | Klasik konfigürasyon çiftleri |

Spring, koleksiyon elemanlarını **hedef tipe dönüştürerek** doldurur (örn. `"100"` metnini
`Map<String,Integer>` için `100`'e). XML'de bunlar `<list>`, `<set>`, `<map>`, `<props>` etiketleriydi;
Java config'te doğrudan `List.of(...)`, `Map.of(...)` gibi koleksiyon nesneleri verirsin.

## Otomatik koleksiyon: bir tipteki TÜM bean'leri toplama

En güçlü biçim budur (`Ornek3.java`). Bir bean'in bağımlılığı `List<Dogrulayici>` ya da
`Map<String,Dogrulayici>` ise, Spring o tipi **uygulayan tüm bean'leri** otomatik toplayıp enjekte
eder:

- `List<Dogrulayici>` → tüm `Dogrulayici` bean'leri (sıra için `@Order` kullanılabilir).
- `Map<String,Dogrulayici>` → anahtar **bean adı**, değer bean'in kendisi.

Bu, **strateji desenini (strategy pattern) container düzeyinde** uygular. `KayitServisi`,
doğrulayıcıları tek tek bilmez; yeni bir kural eklemek için tek yapman gereken **yeni bir `@Bean
Dogrulayici` tanımlamaktır** — servis kodu hiç değişmez. Açık/Kapalı İlkesi (Open/Closed) tam olarak
budur: yeni davranışa **açık**, mevcut koda dokunmaya **kapalı**.

> **Gerçek hayat:** Spring'in kendisi bunu yoğun kullanır. Örneğin Spring MVC, tüm
> `HandlerInterceptor` ya da `Converter` bean'lerini böyle toplar; Spring Security tüm filtreleri
> böyle sıraya dizer.

## Sık yapılan hatalar

- **İç bean'i sonra aramaya çalışmak:** İç bean container'da kayıtlı değildir; `getBean` ile
  erişmeye çalışmak `NoSuchBeanDefinitionException` verir. Erişmen gerekiyorsa onu top-level bean yap.
- **`List<T>` enjeksiyonunda sıra varsaymak:** Varsayılan sıra tanım sırasıdır ama buna güvenme;
  sıra önemliyse `@Order` veya `Ordered` kullan.
- **Hiç bean yokken `List<T>` beklemek:** O tipte bean yoksa Spring **boş liste** enjekte eder (hata
  vermez) — bunu gözden kaçırıp "neden çalışmadı?" dersin.
- **`Map<String,T>` anahtarını yanlış sanmak:** Anahtar, bean **adıdır**; nesnenin bir alanı değil.

## Özet

Enjeksiyonun üç pratik biçimini gördük: yalnızca tek bir yere ait **iç bean'ler**, değer demetlerini
veren **koleksiyon enjeksiyonu** (List/Set/Map/Properties) ve bir tipin tüm bean'lerini toplayan
**otomatik koleksiyon** (strateji deseni). Şimdiye kadar bağımlılıkları hep elle (`@Bean` metoduyla,
açıkça) bağladık. Peki Spring'in bunu **kendiliğinden** yapmasını — doğru bean'i tipe/isime bakıp
otomatik bulmasını — istersek? Sırada tam da bu var: **Bean'lerde Otomatik Bağlama (Auto-Wiring)**.
