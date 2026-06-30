# REST API Geliştirme

İlk Boot uygulamamızda bir GET endpoint'i yazdık. Şimdi REST API'lerin tüm mekaniğini ele
alıyoruz: kaynakları oluşturmak, okumak, güncellemek ve silmek (CRUD); doğru **HTTP metotlarını**
ve **durum kodlarını** kullanmak; istek verisini (`@RequestBody`, `@PathVariable`, `@RequestParam`)
karşılamak ve yanıtı `ResponseEntity` ile tam kontrol etmek. İyi tasarlanmış bir REST API, bir
sözleşmedir: istemci, ne olduğunu yanıtın kodundan ve yapısından anlar.

## REST ve HTTP metotları

REST'te bir **kaynak** (ör. görev, ürün) bir URL ile temsil edilir; üzerinde yapılan işlem ise
HTTP **metoduyla** belirtilir:

| Metot | Anlam | Tipik durum kodu |
|-------|-------|------------------|
| `GET /api/gorevler` | Listele | 200 OK |
| `GET /api/gorevler/{id}` | Tek kaynağı oku | 200 OK / 404 Not Found |
| `POST /api/gorevler` | Yeni kaynak oluştur | 201 Created |
| `PUT /api/gorevler/{id}` | Güncelle (tümünü) | 200 OK |
| `DELETE /api/gorevler/{id}` | Sil | 204 No Content |

Spring'de bu metotlar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` ile
karşılanır. İstek verisi üç kaynaktan gelir:

- **`@RequestBody`** — istek gövdesindeki JSON'u bir nesneye bağlar (POST/PUT için).
- **`@PathVariable`** — URL'deki yol değişkenini (`/{id}`) parametreye bağlar.
- **`@RequestParam`** — sorgu parametresini (`?kategori=...`) parametreye bağlar.

## Tam CRUD

Örnek 1 (`./Ornek1.java`) bellek-içi bir depo üzerinde tam bir CRUD API kurar ve açılışta kendi
endpoint'lerini sırayla çağırır: POST ile oluşturur, GET ile okur, PUT ile günceller, DELETE ile
siler. JSON↔nesne dönüşümünü Spring Boot (Jackson) otomatik yapar; sen yalnızca metotları yazarsın:

```java
@PostMapping public Gorev olustur(@RequestBody Gorev g) { ... }
@GetMapping("/{id}") public Gorev bul(@PathVariable Long id) { ... }
@PutMapping("/{id}") public Gorev guncelle(@PathVariable Long id, @RequestBody Gorev g) { ... }
@DeleteMapping("/{id}") public void sil(@PathVariable Long id) { ... }
```

## ResponseEntity ve durum kodları

Bir metottan doğrudan nesne döndürürsen Spring 200 OK varsayar. Ama doğru REST semantiği için
duruma göre farklı kodlar dönmelisin: oluşturmada **201**, bulunamadıkta **404**, silmede **204**.
**`ResponseEntity`** yanıtın gövdesini, durum kodunu ve başlıklarını tam kontrol etmeni sağlar:

```java
return ResponseEntity.created(URI.create("/api/urunler/" + id)).body(u); // 201 + Location
return (u != null) ? ResponseEntity.ok(u) : ResponseEntity.notFound().build(); // 200 / 404
return ResponseEntity.noContent().build(); // 204
```

Örnek 2 (`./Ornek2.java`) bunları canlı gösterir: POST → 201 (+ `Location` başlığı), var olan
kaynak → 200, olmayan → 404, silme → 204. (Doğru durum kodu döndürmek, istemcinin hata yönetimini
kolaylaştırır.)

> En sık kullanılan kodlar: **2xx** başarı (200 OK, 201 Created, 204 No Content), **4xx** istemci
> hatası (400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found), **5xx** sunucu hatası.

## Filtreleme ve özel başlıklar

Listeleme endpoint'leri genelde filtreleme/sıralama parametreleri alır. `@RequestParam` opsiyonel
(`required = false`) veya varsayılan değerli (`defaultValue = "..."`) olabilir. Yanıta gövdeye ek
olarak **özel başlıklar** da koyabilirsin (ör. toplam sonuç sayısı):

```java
@GetMapping("/ara")
public ResponseEntity<List<Urun>> ara(
        @RequestParam(required = false) String kategori,
        @RequestParam(defaultValue = "1000000") double maxFiyat) {
    return ResponseEntity.ok().header("X-Toplam-Adet", ...).body(sonuc);
}
```

Örnek 3 (`./Ornek3.java`) kategori/fiyat filtresi, sıralama ve özel `X-Toplam-Adet` başlığını
gösterir.

## İyi REST tasarımı için ipuçları

- **Kaynak odaklı URL'ler:** `/api/gorevler/{id}` (fiil değil, isim). İşlemi metot belirtir.
- **Doğru durum kodları:** Her zaman 200 dönme; 201/204/404/400'ü anlamına göre kullan.
- **DTO kullan:** Entity'leri doğrudan dışarı açma; istek/yanıt için ayrı DTO'lar daha güvenlidir
  (bunu katmanlı mimari ve JPA bölümlerinde derinleştireceğiz).
- **Tutarlılık:** Tarih formatı, hata gövdesi, isimlendirme API genelinde aynı olsun.

## Özet

REST'in HTTP metotları ve durum kodlarıyla nasıl çalıştığını; tam CRUD'u (`@RequestBody`,
`@PathVariable`); `ResponseEntity` ile durum/başlık kontrolünü (201/200/404/204) ve `@RequestParam`
ile filtrelemeyi gerçek, canlı bir API üzerinde gördük. Şimdiye kadar veriyi bellekte tuttuk;
sırada onu kalıcı bir veritabanına yazmanın modern yolu: **Spring Data JPA**.
