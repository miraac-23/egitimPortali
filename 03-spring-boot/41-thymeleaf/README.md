# Spring Boot Thymeleaf (Sunucu Tarafı Şablonlar)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** `spring-boot-starter-thymeleaf` bağımlılığı
> gerektirir (portal classpath'inde yok); portalda çalışmaz. Kod tanıtım amaçlıdır.

Bir uygulama her zaman REST API (JSON) sunmaz; bazen sunucuda **HTML üretip** tarayıcıya tam sayfa
göndermek istersin (klasik web uygulaması, yönetim paneli, e-posta şablonu). **Thymeleaf**, Spring
Boot'un önerdiği modern şablon motorudur. JSP'nin (topic 66) yerini almıştır; en büyük avantajı
"doğal şablon" (natural template) olmasıdır: şablonlar **geçerli HTML**'dir, tarayıcıda doğrudan
açılabilir.

## REST vs sunucu tarafı render

| | REST API (`@RestController`) | Sunucu render (`@Controller` + Thymeleaf) |
|---|------------------------------|-------------------------------------------|
| Döndürür | JSON/veri | Tam HTML sayfa |
| Arayüz | Ayrı frontend (React/Vue) çizer | Sunucu çizer, tarayıcı gösterir |
| Kullanım | SPA, mobil, mikroservis | Klasik web app, admin panel, e-posta |

> Bu eğitim portalının kendisi **REST + React** mimarisini kullanır (sunucu JSON döner, React
> çizer). Thymeleaf, "sunucu HTML üretsin" istediğin senaryolar içindir.

## Kurulum ve temel akış

```gradle
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

```java
@Controller                          // @RestController DEĞİL — view adı döndürür
class UrunWebController {
    @GetMapping("/urunler")
    String urunler(Model model) {
        model.addAttribute("urunler", urunServisi.hepsi());  // şablona veri aktar
        return "urunler";            // -> templates/urunler.html
    }
}
```

`@Controller` metodu bir **view adı** (`"urunler"`) döndürür; Spring bunu
`src/main/resources/templates/urunler.html` ile eşler ve `Model`'deki verilerle render eder.

## Thymeleaf söz dizimi

Şablon geçerli HTML'dir; `th:*` öznitelikleriyle dinamikleşir:

```html
<!-- templates/urunler.html -->
<table>
  <tr th:each="u : ${urunler}">              <!-- döngü -->
    <td th:text="${u.ad}">Örnek Ad</td>      <!-- metin (tarayıcıda 'Örnek Ad' görünür, render'da gerçek) -->
    <td th:text="${u.fiyat} + ' TL'}">0 TL</td>
  </tr>
</table>

<p th:if="${urunler.isEmpty()}">Ürün yok.</p>   <!-- koşul -->
<a th:href="@{/urunler/{id}(id=${u.id})}">Detay</a>  <!-- URL üretimi -->
<div th:text="#{hosgeldin.mesaji}">...</div>     <!-- i18n mesajı (topic 18) -->
```

- **`th:text`**: Elemanın içeriğini değiştir.
- **`th:each`**: Döngü (liste → satırlar).
- **`th:if` / `th:unless`**: Koşullu gösterim.
- **`th:href` / `@{...}`**: Bağlam-farkında URL üretimi.
- **`${...}`** değişken, **`#{...}`** i18n mesajı, **`@{...}`** URL, **`*{...}`** form nesnesi.

## Doğal şablon (natural template) avantajı

Thymeleaf şablonu **geçerli HTML** olduğundan, sunucu çalışmadan bile tarayıcıda açılır
(`th:text="${u.ad}">Örnek Ad`'daki "Örnek Ad" tasarımcıya statik önizleme verir). Tasarımcı ve
geliştirici aynı dosya üzerinde çalışabilir — JSP'nin (scriptlet/etiket karışıklığı) çözmediği bir
sorun.

## Form işleme

Thymeleaf, form binding ile güçlüdür: `th:object` + `th:field` ile form nesnesine iki yönlü bağ,
`BindingResult` ile doğrulama hataları (`th:errors`) gösterilir — Spring MVC form akışıyla
entegre.

## Layout ve parçalar

Tekrarı önlemek için `th:fragment` ile ortak parçalar (header/footer) tanımlanır ve
`th:replace`/`th:insert` ile sayfalara eklenir; Thymeleaf Layout Dialect ile şablon kalıtımı kurulur.

## Özet

Thymeleaf'in Spring Boot'un sunucu tarafı HTML şablon motoru olduğunu (JSP'nin modern halefi);
`@Controller` + `Model` + view-adı akışını, `th:text`/`th:each`/`th:if`/`@{...}` söz dizimini,
"doğal şablon" avantajını ve form/layout yeteneklerini öğrendik; REST+SPA ile ne zaman ayrıldığını
gördük. Sırada, güvenli iletişim: **HTTPS etkinleştirme**.
