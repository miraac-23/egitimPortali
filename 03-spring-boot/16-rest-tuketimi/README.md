# REST Tüketimi (RestClient / RestTemplate / WebClient)

Bir uygulama sık sık **başka servislerin** API'lerini çağırır: bir ödeme servisi, bir döviz kuru
API'si, başka bir mikroservis. Bu konuya "REST tüketimi" (consuming REST) denir. Önceki konuda
(topic 01) API **sunmayı** öğrendik; burada başka API'leri **çağırmayı** ele alıyoruz. Spring'in üç
istemcisi vardır; modern seçim **RestClient**'tır.

## Üç istemci

| İstemci | Tür | Durum |
|---------|-----|-------|
| **RestClient** | Senkron, akıcı (fluent) | **Önerilen** (Spring 6.1+) |
| `RestTemplate` | Senkron, klasik | Bakım modunda (eski projeler) |
| `WebClient` | Asenkron/reaktif | Reaktif (WebFlux) veya asenkron gerektiğinde |

## RestClient ile çağrı

`RestClient` akıcı bir API sunar; bir kez oluşturulup paylaşılır:

```java
RestClient istemci = RestClient.create("http://localhost:8080");

// GET + nesneye bağlama
Doviz d = istemci.get().uri("/uzak/doviz/usd").retrieve().body(Doviz.class);

// GET liste (jenerik tip için ParameterizedTypeReference)
List<Doviz> hepsi = istemci.get().uri("/uzak/doviz")
        .retrieve().body(new ParameterizedTypeReference<>() {});

// POST + gövde
var cevap = istemci.post().uri("/uzak/hesapla").body(Map.of("miktar", 100))
        .retrieve().body(SomeType.class);
```

Örnek 1 (`./Ornek1.java`) bir "uzak" API sunar ve onu RestClient ile tüketir: GET (tek/liste),
POST (gövdeli) ve hata yönetimi.

## Hata yönetimi

Varsayılan olarak 4xx/5xx durumlar istisna atar. Özelleştirmek için `onStatus`:

```java
istemci.get().uri("/uzak/yok").retrieve()
    .onStatus(HttpStatusCode::isError, (req, res) -> {
        throw new RuntimeException("uzak servis hatası: " + res.getStatusCode());
    })
    .body(String.class);
```

## RestTemplate (klasik)

Eski projelerde hâlâ yaygın:

```java
RestTemplate rt = new RestTemplate();
Doviz d = rt.getForObject("http://.../doviz/usd", Doviz.class);
ResponseEntity<Doviz> resp = rt.getForEntity(url, Doviz.class);
rt.postForObject(url, istekGovdesi, CevapTipi.class);
```

`RestTemplate` bakım modundadır (yeni özellik almıyor); yeni kodda **RestClient** tercih et.

## WebClient (reaktif/asenkron)

WebFlux veya bloklamayan G/Ç gerektiğinde:

```java
WebClient wc = WebClient.create("http://...");
Mono<Doviz> mono = wc.get().uri("/doviz/usd").retrieve().bodyToMono(Doviz.class);
```

## Üretim pratikleri

- **Zaman aşımı** ayarla (bağlantı + okuma); yavaş bir uzak servis uygulamanı kilitlemesin.
- **Yeniden deneme + devre kesici (circuit breaker):** Geçici hatalar için Resilience4j (retry,
  circuit breaker) ile çağrıyı güvenceye al.
- **Bağlantı havuzu:** RestClient/WebClient bir HTTP istemci kütüphanesi (Apache HttpClient, Reactor
  Netty) ile yapılandırılır.
- **Sanal thread'ler (Java 21):** Senkron `RestClient` çağrıları, sanal thread'lerle bloklama
  maliyeti olmadan ölçeklenir (topic 101).

## Özet

Başka servislerin API'lerini tüketmeyi öğrendik: modern **RestClient** ile GET/POST, jenerik tip
bağlama ve hata yönetimi (`onStatus`; Örnek 1); klasik `RestTemplate` ve reaktif `WebClient`
alternatifleri; zaman aşımı, retry/circuit breaker ve sanal thread gibi üretim pratikleri. Sırada,
dosya yükleme/indirme: **File Handling**.
