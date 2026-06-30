# API Dokümantasyonu (SpringDoc OpenAPI / Swagger UI)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** SpringDoc OpenAPI ek bağımlılık gerektirir;
> portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Bir REST API yazdın — peki onu **kullananlar** hangi uçların olduğunu, hangi parametreleri aldığını,
ne döndürdüğünü nasıl öğrenecek? Elle dokümantasyon hızla eskir. **OpenAPI** (eski adıyla Swagger),
API'yi makine-okunur bir formatta tanımlayan standarttır; **SpringDoc**, Spring Boot uygulamandan bu
dokümanı **otomatik** üretir ve gezilebilir, **denenebilir** bir arayüz (**Swagger UI**) sunar.

## SpringDoc kurulumu

Eski `springfox` artık bakımsızdır; modern çözüm **SpringDoc OpenAPI**'dir:

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
```

Bağımlılığı eklemen **yeter**: SpringDoc, controller'larını tarayıp OpenAPI dokümanını üretir.

- **Swagger UI:** `http://localhost:8080/swagger-ui.html` — gezilebilir, uçları **canlı deneme**.
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs` — makine-okunur tanım (kod üretimi,
  Postman içe aktarma vb.).

## Dokümanı zenginleştirme

Anotasyonlarla açıklama, örnek ve şema bilgisi eklenir:

```java
@RestController
@RequestMapping("/api/urunler")
@Tag(name = "Ürünler", description = "Ürün yönetimi uçları")
class UrunController {

    @Operation(summary = "Ürün getir", description = "ID'ye göre tek ürün döndürür")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bulundu"),
        @ApiResponse(responseCode = "404", description = "Ürün yok")
    })
    @GetMapping("/{id}")
    Urun getir(@Parameter(description = "Ürün ID") @PathVariable Long id) { ... }
}
```

Genel bilgi (başlık, sürüm, iletişim) bir `@Bean OpenAPI` ile verilir:

```java
@Bean
OpenAPI apiBilgisi() {
    return new OpenAPI().info(new Info()
        .title("Eğitim Portalı API").version("1.0")
        .description("Ürün ve sipariş yönetimi"));
}
```

## Neden değerli?

- **Her zaman güncel:** Doküman koddan üretilir; kod değişince doküman da değişir (eskimez).
- **Denenebilir:** Swagger UI'dan uçları tarayıcıda çağırırsın (Postman'e gerek kalmadan).
- **Sözleşme (contract):** OpenAPI JSON'dan istemci SDK'ları, sunucu iskeletleri, testler
  **otomatik üretilebilir** (code generation). Ekipler arası "API sözleşmesi" olur.
- **Keşfedilebilirlik:** Yeni geliştirici API'yi tek bakışta anlar.

## Güvenlik notu

- Swagger UI'ı ve `/v3/api-docs`'u **üretimde** genelde kapatır veya kimlikle korursun (API yapını
  herkese açmamak için).
- JWT/OAuth2 kullanıyorsan, Swagger UI'a "Authorize" düğmesi eklemek için güvenlik şemasını tanımla
  (`@SecurityScheme`), böylece korumalı uçlar UI'dan denenebilir.

## Özet

SpringDoc OpenAPI'nin, Spring Boot API'nden **otomatik, her zaman güncel ve denenebilir**
dokümantasyon (Swagger UI + OpenAPI JSON) ürettiğini öğrendik: kurulum (tek bağımlılık),
`@Operation`/`@Tag`/`@ApiResponse` ile zenginleştirme, `OpenAPI` bean'i ile genel bilgi; kod üretimi
ve güvenlik notları. Sırada, çalışan uygulamayı izleme: **Spring Boot Admin**.
