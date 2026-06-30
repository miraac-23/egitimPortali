# URLConnection, HttpURLConnection ve HttpClient

URL'leri ayrıştırmayı öğrendik; şimdi onlara **bağlanıp veri alıp vermeyi** ele alıyoruz. Bir
mobil uygulamanın API'ye istek atması, bir servisin başka bir servisi çağırması — hepsi HTTP
üzerinden olur. Java bunu üç katmanda sunar: düşük seviyeli `URLConnection`, HTTP'ye özel
`HttpURLConnection` (klasik) ve modern `java.net.http.HttpClient` (Java 11+). Örneklerin hepsi,
internet gerektirmesin diye **aynı JVM'de yerel bir HTTP sunucusu** (JDK'nin `HttpServer`'ı)
başlatıp ona bağlanır — yani gördüğün her şey gerçek bir HTTP istek/cevabıdır.

## HttpURLConnection (klasik)

Java'nın uzun yıllar standart HTTP istemcisi. Akış: `URI → URL → openConnection()`. GET için:

```java
URI uri = URI.create("http://localhost:" + port + "/merhaba");
HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
con.setRequestMethod("GET");
con.setRequestProperty("Accept", "application/json"); // istek başlığı
int kod = con.getResponseCode();                       // 200, 404 ...
String govde = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
con.disconnect();
```

Örnek 1 (`./Ornek1.java`) yerel bir sunucuya GET isteği atar; durum kodunu, `Content-Type`
başlığını ve JSON yanıt gövdesini okur.

### POST (veri gönderme)

Gövde göndermek için `setDoOutput(true)` ile çıkış akışı açılır ve istek gövdesi yazılır:

```java
con.setRequestMethod("POST");
con.setRequestProperty("Content-Type", "application/json");
con.setDoOutput(true);
try (OutputStream os = con.getOutputStream()) {
    os.write("{\"urun\":\"Klavye\"}".getBytes(StandardCharsets.UTF_8));
}
```

Örnek 2 (`./Ornek2.java`) bir JSON gövdesini POST eder; sunucu `201 Created` döndürüp gövdeyi
yansıtır. PUT/DELETE için `setRequestMethod("PUT"/"DELETE")` kullanılır.

> `HttpURLConnection` işini görür ama hantaldır: stream yönetimi, hata gövdesini ayrı okuma
> (`getErrorStream`), zaman aşımı ayarları... Modern projelerde `HttpClient` tercih edilir.

## HttpClient (modern, Java 11+)

Java 11 ile gelen `java.net.http.HttpClient`, temiz bir builder API'si, HTTP/2 desteği, zaman
aşımı ve **asenkron** istek sunar. Üç ana tip: `HttpClient` (bir kez oluştur, paylaş),
`HttpRequest` (istek), `HttpResponse` (yanıt):

```java
HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

HttpRequest get = HttpRequest.newBuilder(URI.create(url)).GET().build();
HttpResponse<String> cevap = client.send(get, HttpResponse.BodyHandlers.ofString());
cevap.statusCode(); cevap.body();

HttpRequest post = HttpRequest.newBuilder(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("{...}"))
        .build();
```

`sendAsync(...)` ise bloklamadan, bir `CompletableFuture` döndürür. Örnek 3 (`./Ornek3.java`)
GET, POST ve asenkron GET'i yerel sunucuya karşı gösterir.

| | HttpURLConnection | HttpClient (11+) |
|---|-------------------|------------------|
| API | Eski, hantal | Temiz, builder |
| HTTP/2 | Yok | Var |
| Asenkron | Yok | `sendAsync` + CompletableFuture |
| Timeout | Sınırlı | Yerleşik |
| Öneri | Legacy | **Yeni projeler** |

## Bu katman ile uygulama çatıları

Bu sınıflar HTTP'nin "ham" Java karşılığıdır. Üretimde genelde daha üst seviye istemciler
kullanılır: Spring'de **RestClient** (yeni), **WebClient** (reaktif) veya **RestTemplate** (klasik);
bu araçlar JSON↔nesne dönüşümünü, hata yönetimini ve yapılandırmayı senin için yapar. Ama hepsinin
altında işte bu `HttpClient`/`HttpURLConnection` mekanizması vardır.

## Önemli ayrıntılar

- **Hata gövdesi:** `HttpURLConnection`'da 4xx/5xx durumunda `getInputStream()` exception atar;
  hata gövdesini `getErrorStream()` ile okursun. `HttpClient` bu ayrımı yapmaz (gövdeyi her durumda
  verir).
- **Zaman aşımı:** Üretimde mutlaka bağlantı ve okuma zaman aşımı ayarla; aksi halde yavaş bir
  sunucu uygulamanı kilitleyebilir.
- **Kaynak kapatma:** `HttpURLConnection`'da stream'leri kapat / `disconnect()` çağır.

## Özet

HTTP kaynaklarına bağlanmayı üç yolla gördük: klasik `HttpURLConnection` ile GET (Örnek 1) ve POST
(Örnek 2), modern `HttpClient` ile GET/POST/asenkron (Örnek 3) — hepsi yerel bir HTTP sunucusuna
karşı, gerçek istek/cevaplarla. Yeni projelerde `HttpClient`'ı (ve uygulama düzeyinde Spring'in
RestClient/WebClient'ını) tercih et. Bu, Java ağ programlama bölümünü tamamlıyor: adresleri
çözmekten (InetAddress), soketlerle ham iletişime (TCP/UDP), URL'leri ayrıştırmaya ve HTTP
istemcilerine kadar.
