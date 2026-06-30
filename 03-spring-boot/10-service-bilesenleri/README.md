# Servis Bileşenleri ve Katmanlı Mimari

Bir Spring Boot uygulaması büyüdükçe, tüm mantığı controller'lara doldurmak sürdürülemez hale gelir.
Sağlam uygulamalar **katmanlı mimari** kullanır: her katmanın tek bir sorumluluğu vardır. Bu konu,
Spring'in stereotip anotasyonlarıyla (`@Controller`/`@RestController`, `@Service`, `@Repository`)
bu katmanları nasıl kurduğunu ve neden önemli olduğunu ele alır.

## Üç klasik katman

```
İstemci → [Controller]  →  [Service]  →  [Repository]  →  Veritabanı
            (HTTP)         (iş mantığı)   (veri erişimi)
```

- **`@RestController` / `@Controller`** (sunum katmanı): HTTP isteklerini karşılar, parametreleri
  alır, servise delege eder, yanıtı döndürür. **İş mantığı içermez.**
- **`@Service`** (iş katmanı): Uygulamanın **iş kurallarını** ve mantığını barındırır (doğrulama,
  hesaplama, orkestrasyon, transaction sınırları). Asıl "ne yapılacağı" buradadır.
- **`@Repository`** (veri katmanı): Veritabanı/depo erişimi. (Spring Data JPA ile çoğu zaman bir
  arayüz; ek olarak `@Repository`, JDBC istisnalarını Spring'in `DataAccessException`'larına çevirir.)

Örnek 1 (`./Ornek1.java`) bir ürün yönetimini üç katmanla kurar: Controller yalnızca HTTP'yi
servise bağlar, Service iş kuralını (negatif fiyat reddi) uygular, Repository kaydı tutar.

## Stereotip anotasyonları

Hepsi aslında `@Component`'in özelleşmiş halleridir — Spring onları bileşen taramasında (component
scan) bulup **bean** olarak yönetir:

| Anotasyon | Katman | Özel davranış |
|-----------|--------|---------------|
| `@Component` | Genel | Temel bean |
| `@Controller`/`@RestController` | Sunum | Web istek eşleme (MVC) |
| `@Service` | İş | (Anlamsal; özel davranış yok ama niyeti belgeler) |
| `@Repository` | Veri | Persistence istisnası çevirme |

Anlamsal olarak doğru anotasyonu seçmek, kodun **niyetini** belgeler ve araçların/AOP'nin katmana
özel davranması (örn. yalnızca servislerde transaction) için zemin hazırlar.

## Neden katmanlı mimari?

- **Tek sorumluluk:** Her katman bir işe odaklanır; değişiklik etkisi sınırlı kalır.
- **Test edilebilirlik:** Servisi, controller ve veritabanı olmadan **birim test** edebilirsin
  (repository'yi sahteyle/mock ile değiştirerek — topic 07).
- **Yeniden kullanım:** Aynı servis hem REST controller hem zamanlanmış görev hem mesaj
  dinleyicisi tarafından çağrılabilir.
- **Bağımlılık enjeksiyonu:** Katmanlar birbirine **constructor injection** ile bağlanır (Spring
  bağımlılıkları sağlar); somut sınıf yerine arayüze bağlanmak gevşek bağ sağlar.

## İyi uygulamalar

- Controller "ince" olsun: yalnızca HTTP eşleme + servise delege. Mantık servise gitsin.
- İş kuralları ve transaction sınırları (`@Transactional`) **servis** katmanında.
- Katmanlar arası veri taşırken **DTO**'lar (record'lar) kullan; entity'leri doğrudan dışarı sızdırma.
- Bağımlılıkları **constructor** ile enjekte et (alan enjeksiyonu yerine — test ve değişmezlik için).

## Özet

Katmanlı mimariyi ve Spring'in stereotip bileşenlerini öğrendik: HTTP'yi karşılayan
**`@RestController`**, iş mantığını barındıran **`@Service`**, veri erişimini yapan **`@Repository`**
(Örnek 1); bunların `@Component` türevleri olduğunu, neden katmanlı çalıştığımızı (test, yeniden
kullanım, tek sorumluluk) ve iyi uygulamaları gördük. Sırada, isteğin önüne girip ortak işleri yapan
yapı: **interceptor**.
