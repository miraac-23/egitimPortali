# Java 18 (Mart 2022)

## Sürüm Bilgileri

| Özellik | Değer |
|---|---|
| Sürüm | Java 18 (JDK 18) |
| Çıkış Tarihi | 22 Mart 2022 |
| Destek Türü | **Feature Release** (kısa vadeli, LTS DEĞİL) |
| LTS mı? | Hayır. Sonraki LTS Java 21'dir (Eylül 2023). |
| Önceki LTS | Java 17 (Eylül 2021) |
| JEP Sayısı | 9 JEP |

> **Önemli Not:** Java 18 bir "ara sürümdür". Üretim sistemlerinde nadiren doğrudan hedeflenir; çünkü 6 ayda bir gelen feature release'lerin desteği yalnızca bir sonraki sürüme kadar (6 ay) sürer. Buna rağmen Java 18, daha sonra **Java 21 LTS'te kalıcı hale gelecek** özelliklerin (Pattern Matching for switch, Foreign Function & Memory API) olgunlaşma yolundaki önemli bir basamağıdır.

---

## Genel Bakış

Java 18, görece "sakin" bir sürüm olmakla birlikte, gündelik geliştiriciyi doğrudan etkileyen **çok kritik bir değişiklik** getirdi: **UTF-8'in tüm platformlarda varsayılan karakter kodlaması olması**. Bu, yıllardır süren "Windows'ta çalışıyor, Linux'ta bozuk karakter" sınıfı hataların kökünü kurutan bir adımdır.

Bu sürümün kapsadığı başlıca özellikler:

1. **JEP 400 — UTF-8 by Default** (KALICI): Varsayılan charset artık platformdan bağımsız UTF-8.
2. **JEP 408 — Simple Web Server** (KALICI): `jwebserver` komutu ile tek satırda statik dosya sunucusu.
3. **JEP 413 — Code Snippets in Java API Documentation**: Javadoc içinde `@snippet` etiketi.
4. **JEP 420 — Pattern Matching for switch** (İkinci Preview): `switch` ifadelerinde desen eşleştirme.
5. **JEP 419 — Foreign Function & Memory API** (İkinci Incubator): JNI'nin modern halefi.
6. **JEP 417 — Vector API** (Üçüncü Incubator): SIMD donanım hızlandırma.
7. **JEP 416 — Reimplement Core Reflection with Method Handles**: İçsel iyileştirme.
8. **JEP 418 — Internet-Address Resolution SPI**: DNS çözümleme için eklenebilir servis.

---

## Özellik Detayları

### 1. UTF-8 Varsayılan Charset (JEP 400) — KALICI

> İlgili dosya: [`UTF8Varsayilan.java`](./UTF8Varsayilan.java)

#### NEDİR?
Java'da `new FileReader(...)`, `new String(byte[])`, `PrintStream` gibi pek çok API açıkça bir charset verilmediğinde "varsayılan charset"i kullanır. Java 17 ve öncesinde bu varsayılan, **işletim sisteminin ve bölge ayarının (locale)** belirlediği bir değerdi:
- Windows (Türkçe): `windows-1254` veya `Cp1252`
- Linux/macOS (modern): genellikle `UTF-8`

Java 18 ile `file.encoding` sistem özelliği belirtilmediğinde varsayılan **her platformda `UTF-8`** olur.

#### NEDEN GELDİ?
Onlarca yıllık "benim makinemde çalışıyor" probleminin temel kaynaklarından biriydi. Bir geliştirici UTF-8 makinede yazdığı dosyayı, başka bölge ayarına sahip bir sunucuda okuduğunda Türkçe karakterler (ç, ğ, ş, ü, ö, ı) bozuluyordu. Sorun sinsiydi: kod doğru görünüyor ama çıktı ortama göre değişiyordu.

#### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR?
- Aynı kod, geliştirme makinesinde de CI sunucusunda da üretim sunucusunda da **aynı** sonucu üretir.
- Charset'i her yerde elle belirtme zorunluluğu pratikte ortadan kalkar (ama yine de en iyi pratik açıkça belirtmektir).
- Türkçe gibi ASCII dışı karakter içeren diller için davranış öngörülebilir hale gelir.

#### ESKİ vs YENİ

```java
// ESKİ (Java 17 ve öncesi) — RİSKLİ: charset platforma bağlı
try (FileReader fr = new FileReader("veri.txt")) { ... }
// Windows-TR: Cp1254, Linux: UTF-8 -> aynı dosya farklı okunur!

// YENİ (Java 18+) — varsayılan her yerde UTF-8
try (FileReader fr = new FileReader("veri.txt")) { ... }
// Her platformda UTF-8

// EN İYİ PRATİK (her sürümde güvenli) — charset'i açıkça belirt
import java.nio.charset.StandardCharsets;
Files.readString(Path.of("veri.txt"), StandardCharsets.UTF_8);
```

#### GERÇEK HAYAT ÖRNEĞİ
Bir e-ticaret ekibi, ürün açıklamalarını CSV'den içe aktaran bir batch işi yazar. Geliştirici (Mac, UTF-8) testlerde sorun görmez. Üretim sunucusu (eski Windows Server, Cp1254) dosyayı içe aktarınca "Büyük Çığlık" ürünü veritabanına "Büyük Çýglýk" olarak yazılır. Java 18 ile her iki ortam da UTF-8 kullandığından bu sınıf hata kaybolur.

#### AVANTAJ / DEZAVANTAJ / RİSK
- **Avantaj:** Taşınabilirlik, öngörülebilirlik, Türkçe içerikte güvenlik.
- **Risk (Geçişte):** Eski sistemler, üretilen dosyaların *önceki* platform charset'i (örn. Cp1254) ile okunmasına bel bağlamış olabilir. Java 18'e geçince eski araçlar bu dosyaları yanlış okuyabilir. Geçici çözüm: `-Dfile.encoding=COMPAT` veya açık charset.
- **Tespit:** `System.out.println(Charset.defaultCharset())` çıktısını sürüm geçişinden önce/sonra karşılaştırın.

---

### 2. Simple Web Server (JEP 408) — KALICI

> Detaylı anlatım: [`SimpleWebServer.md`](./SimpleWebServer.md)

#### NEDİR?
JDK ile birlikte gelen `jwebserver` komut satırı aracı ve `com.sun.net.httpserver` paketindeki programatik API. Tek bir komutla, içinde bulunduğunuz dizini statik dosya sunucusu olarak yayınlar.

#### NEDEN GELDİ?
Geliştiriciler hızlı bir statik dosya sunucusu için çoğu zaman Python'un `python -m http.server`'ına başvuruyordu. Java ekosisteminde böyle "pil dahil" bir araç yoktu. Eğitim, prototipleme, test ve geçici dosya paylaşımı senaryoları için hafif bir çözüm gerekiyordu.

#### NE İŞE YARAR?
- Statik HTML/CSS/JS prototiplerini saniyeler içinde yayınlamak.
- Bir REST istemcisini test ederken sahte (mock) dosyalar sunmak.
- Eğitimde HTTP kavramlarını göstermek.

#### ESKİ vs YENİ
```bash
# ESKİ: Python'a bağımlılık veya tam teşekküllü bir sunucu yazmak
python3 -m http.server 8000

# YENİ (Java 18+): JDK içinde gelir
jwebserver -p 8000
```

#### GERÇEK HAYAT ÖRNEĞİ
Bir frontend ekibi, build edilmiş statik dosyaları (`dist/` klasörü) hızlıca bir test cihazında göstermek ister. `jwebserver -b 0.0.0.0 -p 8080` ile dosyalar anında ağ üzerinden erişilebilir.

#### NOT
Üretim için TASARLANMAMIŞTIR: sadece `GET` ve `HEAD` destekler, HTTPS yoktur, kimlik doğrulama yoktur. Yalnızca geliştirme/test içindir.

---

### 3. Code Snippets in Java API Documentation (JEP 413)

#### NEDİR?
Javadoc içinde `@snippet` etiketi ile kod örneklerini gömme imkanı. Eski `<pre>{@code ...}</pre>` yönteminin modern, doğrulanabilir ve sözdizimi vurgulu halefi.

#### NEDEN GELDİ?
Javadoc içindeki kod örnekleri "ölü metin"di: derlenmez, vurgulanmaz, harici dosyadan alınamazdı. Bu yüzden dokümandaki örnekler kolayca eskir ve hatalı hale gelirdi.

#### ESKİ vs YENİ
```java
// ESKİ
/**
 * <pre>{@code
 *   List<String> liste = List.of("a", "b");
 * }</pre>
 */

// YENİ (Java 18+) — doğrulanabilir, vurgulanır, harici dosyadan alınabilir
/**
 * {@snippet :
 *   List<String> liste = List.of("a", "b"); // @highlight substring="of"
 * }
 */
```

#### NEREDE KOLAYLIK SAĞLAR?
Kütüphane yazarları için: örnek kodlar gerçek kaynak dosyalardan çekilebildiğinden, örnekler her zaman derlenir ve güncel kalır.

---

### 4. Pattern Matching for switch (JEP 420) — İKİNCİ PREVIEW

#### NEDİR?
`switch` ifadelerinde tür deseni (type pattern) eşleştirme. Java 17'deki ilk preview'in geliştirilmiş hali.

#### EVRİM (ÇOK ÖNEMLİ)
Bu özellik bir **evrim hikayesinin** parçasıdır:
- Java 17: 1. preview (JEP 406)
- **Java 18: 2. preview (JEP 420)** — guarded pattern sözdizimi `&&` yerine `when` olarak değişti
- Java 19: 3. preview (JEP 427)
- Java 20: 4. preview (JEP 433)
- **Java 21: KALICI (JEP 441)**

#### ESKİ vs YENİ
```java
// ESKİ
String tanim;
if (nesne instanceof Integer i) tanim = "tam sayı: " + i;
else if (nesne instanceof String s) tanim = "metin: " + s;
else tanim = "bilinmeyen";

// YENİ (preview)
String tanim = switch (nesne) {
    case Integer i -> "tam sayı: " + i;
    case String s  -> "metin: " + s;
    default        -> "bilinmeyen";
};
```

> Tam ve kalıcı kullanım örneği için Java 21 klasöründeki `PatternMatchingSwitchKalici.java` dosyasına bakın.

---

### 5. Foreign Function & Memory API (JEP 419) — İKİNCİ INCUBATOR

#### NEDİR?
JVM dışındaki yerel (native) kod ve bellekle güvenli, performanslı etkileşim. Eski **JNI**'nin (Java Native Interface) modern, daha güvenli halefi.

#### NEDEN GELDİ?
JNI kırılgan, hataya açık ve yazması zordu. C kütüphanelerini (örn. OpenSSL, SQLite, makine öğrenmesi kütüphaneleri) çağırmak büyük zahmet gerektiriyordu.

#### EVRİM
- Java 14-17: Incubator (Foreign Memory)
- Java 18: 2. incubator
- Java 19: Preview
- Java 21: 3. preview (JEP 442)
- **Java 22: KALICI (JEP 454)**

> Bu özellik Java 18'de hâlâ incubator olduğundan örnek dosya eklenmemiştir; kalıcı haline ulaşması Java 22'yi bulmuştur.

---

### 6. Vector API (JEP 417) — ÜÇÜNCÜ INCUBATOR

#### NEDİR?
Hesaplamaları modern CPU'ların SIMD (Single Instruction, Multiple Data) komutlarına derleyen bir API. Örneğin iki dizinin elemanlarını tek seferde paralel toplamak.

#### NEREDE KOLAYLIK SAĞLAR?
Bilimsel hesaplama, makine öğrenmesi, görüntü işleme, finansal modelleme gibi sayısal yoğun işlerde önemli hız artışı.

---

## Geçiş Rehberi: Java 17 -> Java 18'de Ne Değişti?

| Konu | Etki | Önerilen Aksiyon |
|---|---|---|
| UTF-8 varsayılan | Dosya/akış okuma davranışı değişebilir | Charset'i her zaman açıkça belirtin; `Charset.defaultCharset()` çıktısını test edin |
| `jwebserver` | Yeni araç | Geliştirme/test akışına dahil edilebilir |
| Pattern matching switch | Hâlâ preview | Üretimde kullanmayın; `--enable-preview` gerektirir |
| FFM & Vector API | Incubator | API'ler değişebilir; üretim için erken |

### Genel Tavsiye
Java 18'i doğrudan üretim hedefi yapmak yerine, **Java 17 LTS'ten Java 21 LTS'e** geçerken yol üzerindeki bir öğrenme/test sürümü olarak değerlendirin. Java 18'in en kalıcı mirası UTF-8 varsayılanıdır.
