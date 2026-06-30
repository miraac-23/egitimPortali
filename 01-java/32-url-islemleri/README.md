# URL ve URL İşlemleri

Soket programlamada ham TCP/UDP kanallarıyla çalıştık. Ama web dünyasında kaynaklara genelde
**adresleriyle** (URL) erişiriz: `https://site.com/urunler?kategori=java`. Java, bu adresleri
ayrıştırmak, parçalarına ayırmak, güvenli biçimde kodlamak ve göreli adresleri çözmek için zengin
araçlar sunar (`java.net.URI` ve `URL`). Bu bölümde adreslerle çalışmayı; bir sonraki bölümde ise
bu adreslere bağlanıp veri alıp vermeyi göreceğiz.

## URL'in anatomisi

Bir URL birkaç parçadan oluşur:

```
<scheme>://<userInfo>@<host>:<port><path>?<query>#<fragment>
https://ada@www.site.com:8443/urunler/ara?kategori=java&sayfa=2#sonuclar
```

| Parça | Örnek | Anlamı |
|-------|-------|--------|
| scheme | `https` | Protokol (http/https/ftp/file) |
| userInfo | `ada` | Kimlik bilgisi (nadiren) |
| host | `www.site.com` | Sunucu adı/IP |
| port | `8443` | Kapı numarası (yoksa varsayılan: http=80, https=443) |
| path | `/urunler/ara` | Sunucudaki kaynak yolu |
| query | `kategori=java&sayfa=2` | Parametreler (`?` sonrası, `&` ile ayrılır) |
| fragment | `sonuclar` | Sayfa içi konum (`#` sonrası) |

Java'da bunu ayrıştırmak için **`URI`** kullanılır:

```java
URI uri = URI.create("https://www.site.com:8443/ara?kategori=java#sonuclar");
uri.getScheme(); uri.getHost(); uri.getPort(); uri.getPath(); uri.getQuery(); uri.getFragment();
```

Örnek 1 (`./Ornek1.java`) tam donanımlı bir URL'i parçalarına ayırır.

> **URI vs URL:** `URI` bir kaynağı **tanımlar/ayrıştırır**; `URL` ona **nasıl erişileceğini** de
> içerir (bağlantı açabilir). Modern Java'da `new URL(String)` constructor'ı **deprecated**'tır
> (Java 20+); ayrıştırma için `URI`, bağlantı için `uri.toURL()` veya `HttpClient` tercih edilir.

## URL kodlama (encoding)

URL'de yalnızca belirli karakterler güvenlidir. Boşluk, Türkçe harfler (ç, ü, ı...), `&`, `=`,
`?`, `#` gibi karakterler özeldir; doğrudan konursa URL bozulur veya yanlış ayrıştırılır. Çözüm
**yüzde kodlamasıdır** (`%XX`): `URLEncoder` kodlar, `URLDecoder` çözer:

```java
URLEncoder.encode("java & spring", StandardCharsets.UTF_8); // "java+%26+spring"
URLDecoder.decode(kodlu, StandardCharsets.UTF_8);
```

Örnek 2 (`./Ornek2.java`) özel karakterli bir aramayı kodlar/çözer, bir `Map`'ten güvenli bir
sorgu string'i inşa eder ve geri ayrıştırır. **Kural: sorgu parametrelerini her zaman kodla** —
özellikle kullanıcı girdisinden geliyorsa.

## Göreli URL'ler: resolve, relativize, normalize

Web sayfalarındaki linkler genelde **göreli**dir (`sayfa.html`, `../resimler/`). Bir temel adrese
göre bunları tam URL'e çevirmek `resolve` ile yapılır — tarayıcıların linkleri çözmesi gibi:

```java
URI temel = URI.create("https://site.com/docs/java/");
temel.resolve("giris.html");   // https://site.com/docs/java/giris.html
temel.resolve("../spring/");   // https://site.com/docs/spring/
temel.resolve("/iletisim");    // https://site.com/iletisim  (kökten)
```

- **`relativize`**: İki mutlak URL arasındaki göreli yolu bulur (`resolve`'un tersi).
- **`normalize`**: Yoldaki `.` ve `..` parçalarını sadeleştirir.

Örnek 3 (`./Ornek3.java`) bunları gösterir. Bu işlemler web tarayıcıları, HTML link çözümleme,
sitemap üretimi ve API istemcilerinde sık kullanılır.

## Özet

URL'in parçalarını (`URI` ile ayrıştırma), URL kodlamayı (`URLEncoder`/`URLDecoder` ile güvenli
sorgu parametreleri) ve göreli URL işlemlerini (`resolve`/`relativize`/`normalize`) öğrendik;
`URI` ve `URL` ayrımına ve `new URL(String)`'in neden artık önerilmediğine değindik. Adresleri
artık güvenle ele alabiliyorsun. Sırada bu adreslere **bağlanıp** veri alıp vermek var:
**URLConnection ve HttpURLConnection (ve modern HttpClient)**.
