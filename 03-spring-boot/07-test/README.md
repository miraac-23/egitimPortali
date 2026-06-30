# Test

Çalışan bir kod yazmak yetmez; **çalışmaya devam ettiğini** garanti etmek gerekir. Testler, kodun
doğruluğunu otomatik olarak doğrular; bir değişiklik bir şeyi bozduğunda anında haber verir. Bu
güven, projeyi korkusuzca geliştirmenin temelidir. Spring Boot, hızlı birim testlerinden tam
entegrasyon testlerine kadar zengin bir test desteği sunar.

> Testler normalde `gradle test` veya IDE tarafından çalıştırılır (test sınıflarının `main`'i
> yoktur). Bu portalda görebilmen için örnekler, içlerindeki `@Test` sınıflarını **JUnit Platform
> Launcher** ile programatik çalıştırıp sonucu (geçen/kalan) yazar.

## Test piramidi

İyi bir test stratejisi katmanlıdır:

- **Birim testler (çok ve hızlı):** Tek bir sınıf/metodu yalıtılmış test eder (bağımlılıklar
  sahte). Saniyenin altında çalışır.
- **Dilim (slice) testleri:** Uygulamanın bir katmanını yükler (`@WebMvcTest` web, `@DataJpaTest`
  veri).
- **Entegrasyon testleri (az ve yavaş):** Tüm uygulamayı ayağa kaldırır (`@SpringBootTest`).

## Birim test: JUnit 5 + AssertJ

Temel araç **JUnit 5**'tir: `@Test` ile test metodu, `@BeforeEach`/`@AfterEach` ile kurulum/temizlik.
İddialar için **AssertJ** akıcı ve okunaklı bir API sunar:

```java
@Test void indirim() {
    sepet.ekle(200);
    assertThat(sepet.indirimliToplam(0.10)).isEqualTo(180);
}
@Test void negatif() {
    assertThatThrownBy(() -> sepet.ekle(-5)).isInstanceOf(IllegalArgumentException.class);
}
```

Örnek 1 (`./Ornek1.java`) bir `Sepet` sınıfının üç davranışını test eder ve "3/3 GEÇTİ" özetini yazar.

## Yalıtılmış test: Mockito

Bir servisi test ederken bağımlılıklarını (repository, dış API) gerçek nesnelerle çağırmak
istemezsin (yavaş, kırılgan). **Mockito** ile sahte (mock) bağımlılık oluşturur, davranışını
programlar ve etkileşimi doğrularsın:

```java
UrunRepository sahte = mock(UrunRepository.class);
when(sahte.adBul(1)).thenReturn(Optional.of("Klavye")); // davranış
assertThat(servis.adGetir(1)).isEqualTo("Klavye");
verify(sahte).adBul(1);                                  // etkileşim doğrulama
```

Örnek 2 (`./Ornek2.java`) bir servisi sahte repository ile yalıtılmış test eder. Spring'de bir
bean'i sahteyle değiştirmek için **`@MockBean`** kullanılır.

## Web katmanı testi: MockMvc

Bir controller'ı test etmek için gerçek bir sunucu başlatmaya gerek yok. **MockMvc**, HTTP
isteklerini taklit eder ve yanıtı doğrular:

```java
MockMvc mvc = MockMvcBuilders.standaloneSetup(new UrunController()).build();
mvc.perform(get("/urun/1"))
   .andExpect(status().isOk())
   .andExpect(content().string(containsString("Klavye")));
```

Örnek 3 (`./Ornek3.java`) bir controller'ı sunucusuz, MockMvc ile test eder.

## Spring Boot test anotasyonları

Gerçek projelerde testleri Spring'in test desteğiyle yazarsın:

- **`@SpringBootTest`** — Tüm uygulama context'ini yükler; uçtan uca entegrasyon testi. İsteğe
  bağlı `webEnvironment = RANDOM_PORT` ile gerçek sunucu + `TestRestTemplate`/`WebTestClient`.
- **`@WebMvcTest(UrunController.class)`** — Yalnızca web katmanını yükler (controller + MockMvc),
  servis/repository `@MockBean` ile sahtelenir. Hızlı web testi.
- **`@DataJpaTest`** — Yalnızca JPA katmanını + gömülü bir veritabanını yükler; repository'leri
  gerçek sorgularla test eder, sonunda rollback yapar.
- **`@MockBean` / `@SpyBean`** — Context'teki bir bean'i sahte/casus ile değiştirir.

```java
@WebMvcTest(UrunController.class)
class UrunControllerTest {
    @Autowired MockMvc mvc;
    @MockBean UrunService service;   // servis sahtelenir
    @Test void test() throws Exception { mvc.perform(get("/urun/1")).andExpect(status().isOk()); }
}
```

## Testcontainers

Entegrasyon testlerinde gömülü H2 yerine **gerçek** bir veritabanına (PostgreSQL, MySQL) karşı
test etmek istiyorsan, **Testcontainers** testin başında Docker ile gerçek bir veritabanı
konteyneri başlatır, sonunda kapatır. "Üretimdeki gibi" test ortamı sağlar.

## Özet

Test piramidini; JUnit 5 + AssertJ ile birim testi (Örnek 1), Mockito ile yalıtılmış testi
(Örnek 2) ve MockMvc ile web katmanı testini (Örnek 3) çalışan biçimde gördük; `@SpringBootTest`,
`@WebMvcTest`, `@DataJpaTest`, `@MockBean` ve Testcontainers kavramlarını ele aldık. Testler,
yazdığın her şeyin güvencesidir.

Sırada, mikroservis dünyasının en zorlu konularından biri var: birden çok servise yayılan
işlemlerde tutarlılık — **dağıtık transaction ve Saga pattern**.
