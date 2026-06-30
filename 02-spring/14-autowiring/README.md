# Bean'lerde Otomatik Bağlama (Auto-Wiring)

Şimdiye kadar bağımlılıkları çoğunlukla **elle** bağladık: `@Bean` metoduna parametre verdik ya da
container'a tarifi açıkça yazdık. **Auto-wiring**, bu işi Spring'e devreder: sen sadece "buraya bir
`DepoServisi` lazım" dersin (`@Autowired`), Spring uygun bean'i **kendisi bulup** yerleştirir.
`01-dependency-injection` bölümünde DI'nin türlerini görmüştük; burada Spring'in doğru bean'i
**nasıl seçtiğine**, belirsizlikleri nasıl çözdüğüne ve eksik bağımlılıkları nasıl ele aldığına
odaklanıyoruz.

## Klasik auto-wiring modları (ve modern karşılıkları)

Auto-wiring kavramı XML çağından gelir; o zaman bir `<bean>` için dört mod seçilirdi. Bugün
anotasyonlarla çalışsak da kavramları bilmek faydalıdır (`Ornek1.java`):

| Mod | Anlamı | Modern karşılığı |
| --- | --- | --- |
| **no** | Otomatik bağlama yok; her şey elle | XML varsayılanı (artık nadir) |
| **byType** | Aynı tipte tek bean varsa onu enjekte et | `@Autowired` (fiili standart) |
| **byName** | Property **adıyla** aynı isimli bean'i enjekte et | `@Autowired` + alan/parametre adı eşleşmesi |
| **constructor** | byType'ın constructor parametreleri üzerinden hali | constructor injection (önerilen) |

Modern Spring'de **byType + constructor injection** standarttır. `Ornek1`, aynı serviste üç
enjeksiyon noktasını birden gösterir: constructor (önerilen), setter ve field.

> **Hangi enjeksiyon noktası?** **Constructor** injection'ı tercih et: bağımlılıklar `final`
> olabilir (değişmezlik), nesne hep tam kurulu doğar ve test etmesi kolaydır. Setter, opsiyonel
> bağımlılıklar için; field injection ise kısa ama test/değişmezlik açısından en zayıf seçenek.

## Belirsizlik: aynı tipte birden çok aday

Auto-wiring tipe bakar. Peki aynı tipte **iki** bean varsa Spring hangisini seçer? Hiçbir ipucu
yoksa **`NoUniqueBeanDefinitionException`** fırlatır. `Ornek2.java` çözüm araçlarını ve aralarındaki
**öncelik sırasını** gösterir:

1. **`@Qualifier("ad")`** — en öncelikli. Bean'i adıyla **kesin** seçer; `@Primary`'yi bile ezer.
2. **`@Primary`** — niteleyici yoksa "varsayılan aday" budur. Birden çok adaydan birini öne çıkarır.
3. **Alan/parametre adı eşleşmesi (byName)** — `@Primary` **yoksa**, Spring son çare olarak alan/
   parametre adıyla aynı isimli bean'i seçer.

Yani `@Primary` varken byName düşüşü devreye girmez. `Ornek2`'de e-posta `@Primary` olduğu için
niteleyicisiz tüketici onu alır; SMS'i isteyen tüketiciler `@Qualifier("sms")` kullanır (hem
constructor hem field üzerinde).

> **Pratik öneri:** Belirsizlik olduğunda **`@Qualifier`** kullan. Adıyla seçim, niyeti açık ve
> okunur kılar; alan adı eşleşmesine ("byName düşüşü") güvenmek kırılgandır — birisi alanı yeniden
> adlandırınca sessizce başka bean enjekte olur.

## Eksik olabilen bağımlılıklar

Her bağımlılık zorunlu değildir; bir eklenti ya da opsiyonel özellik **var olmayabilir**. Zorunlu
`@Autowired` böyle bir durumda context'i çökertir. Spring üç esnek alternatif sunar (`Ornek3.java`):

| Yöntem | Bean yoksa | Not |
| --- | --- | --- |
| `@Autowired(required=false)` | Alan **null** kalır | NPE riskine dikkat |
| `Optional<T>` | `Optional.empty()` | null'dan güvenli, açık niyet |
| `ObjectProvider<T>` | `ifAvailable`/`getIfAvailable` ile sessizce geç | **Tembel** + en esnek; çoklu/sıralı erişim de verir |

`ObjectProvider`, ayrıca **tembel** erişim sağlar (bean'i ancak gerçekten kullanınca çözer) ve
`stream()`/`orderedStream()` ile aynı tipteki birden çok bean'e sıralı erişim verir — döngüsel
bağımlılıkları kırmakta da işe yarar.

## Sık yapılan hatalar

- **`NoUniqueBeanDefinitionException`:** İki aday var, ipucu yok. `@Primary` ya da `@Qualifier` ekle.
- **`NoSuchBeanDefinitionException`:** Hiç aday yok. Bean'i tanımlamayı unuttun ya da bileşen taraması
  o paketi görmüyor; veya bağımlılık opsiyonelse `Optional`/`required=false` kullan.
- **Field injection'a aşırı güvenmek:** Hızlı ama test ve değişmezlik açısından zayıf; mümkünse
  constructor injection.
- **byName düşüşüne bel bağlamak:** Alan adını değiştirince sessizce farklı bean gelir; `@Qualifier`
  ile niyeti sabitle.
- **Döngüsel bağımlılık (A↔B):** Constructor injection'da çözülemez ve hata verir; tasarımı gözden
  geçir ya da son çare olarak `@Lazy`/`ObjectProvider` kullan.

## Özet

Auto-wiring ile bağımlılık bağlamayı Spring'e devretmeyi öğrendik: klasik modlar (byType/byName/
constructor) ve modern `@Autowired`; belirsizliği çözen `@Qualifier`/`@Primary` önceliği; ve eksik
olabilen bağımlılıklar için `Optional`/`ObjectProvider`. Buraya kadar Spring çekirdeğini — IoC/DI,
bean tanımı/yaşam döngüsü, post-processor'lar, enjeksiyon ve auto-wiring — bütünüyle kavradık.
Şimdi çekirdeğin üstüne kurulan **web katmanına** bakıyoruz. Sırada: **Spring Web MVC Framework**.
