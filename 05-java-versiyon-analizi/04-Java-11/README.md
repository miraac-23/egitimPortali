# Java 11 (Eylül 2018) — LTS Sürüm Detaylı Analizi

> **Çıkış Tarihi:** Eylül 2018
> **Sürüm Tipi:** **LTS (Long Term Support — Uzun Süreli Destek)** ⭐
> **Önceki LTS:** Java 8 (Mart 2014)
> **Sonraki LTS:** Java 17 (Eylül 2021)

---

## 🏆 Neden LTS ÇOK Önemli? (Kurumsal Bakış)

Java 11, Java 8'den sonraki **ilk LTS sürümüdür** ve bu yüzden kurumsal dünyada
devasa bir öneme sahiptir. Java 9 ve 10 "feature release" (özellik sürümü) idi
ve sadece **6 ay** destek aldı. Java 11 ise **yıllarca** destek alır.

### LTS Nedir, Neden Hayati?

Oracle, Java 9'dan itibaren **6 aylık sürüm takvimi**ne geçti. Yani her 6 ayda
bir yeni Java sürümü çıkıyor (9, 10, 11, 12, ...). Ancak bu sürümlerin çoğu
sadece **6 ay** güncelleme/güvenlik yaması alır. **LTS sürümler ise istisnadır:**

| Konu | Normal (Feature) Sürüm | **LTS Sürüm (Java 11)** |
|------|------------------------|-------------------------|
| Destek penceresi | ~6 ay | **Yıllarca** (genelde 8+ yıl genişletilmiş destek) |
| Güvenlik yaması | Bir sonraki sürüm çıkınca biter | Uzun süre devam eder |
| Kurumsal uygunluk | Düşük (sürekli yükseltme baskısı) | **Yüksek (stabil zemin)** |
| Üretim ortamı önerisi | Genelde önerilmez | **Kesinlikle önerilir** |

### Neden Şirketler Java 8'den Sonra En Çok 11'e Geçti?

1. **Üretim Ortamı Stabilitesi:** Bir bankanın, sigortanın veya e-ticaret
   devinin sistemini her 6 ayda bir Java sürümü yükseltmesi mümkün değildir.
   Test, sertifikasyon, regresyon süreçleri aylar sürer. LTS, "bir kez geç,
   yıllarca rahat et" demektir.

2. **Uzun Destek Penceresi:** Java 11, çıkışından yıllar sonra bile güvenlik
   güncellemesi alır. Bu, denetim/uyumluluk (audit/compliance) gereksinimi olan
   kurumlar için zorunludur.

3. **Güvenlik Güncellemeleri:** LTS olmayan sürümlerde, 6 ay sonra güvenlik açığı
   bulunsa bile yama gelmez; bir üst sürüme geçmek zorunda kalırsın. LTS'te bu
   risk yoktur.

4. **Ekosistem Desteği:** Spring, Hibernate, Tomcat, build araçları (Maven,
   Gradle) ve bulut sağlayıcıları (AWS, Azure) öncelikle LTS sürümleri hedef
   alır ve uzun süre destekler.

5. **Lisans/Dağıtım Netliği:** Java 11 döneminde OpenJDK dağıtımları (Adoptium/
   Eclipse Temurin, Amazon Corretto, Azul Zulu, Red Hat) yaygınlaştı; ücretsiz
   ve uzun destekli LTS yapıları kurumlar için cazip oldu.

> **Özet:** Java 8 → Java 11 geçişi, "bir sonraki güvenli liman"a geçiş olarak
> görüldü. Çoğu kurum 9 ve 10'u tamamen atlayıp doğrudan 11'e taşındı.

---

## 📋 Genel Bakış — Java 11 Yenilikleri

| # | Özellik | JEP | Örnek Dosya |
|---|---------|-----|-------------|
| 1 | Standart HTTP Client (`java.net.http`) | JEP 321 | `YeniHttpClient.java` |
| 2 | Yeni String metotları (strip, isBlank, lines, repeat) | JEP 327/(çeşitli) | `StringYeniMetotlar.java` |
| 3 | `Files.readString` / `Files.writeString` | (API) | `FilesReadWriteString.java` |
| 4 | Lambda parametrelerinde `var` | JEP 323 | `VarLambdaParametre.java` |
| 5 | Tek dosya kaynak kodu çalıştırma | JEP 330 | `TekDosyaCalistirma.java` |
| 6 | `Collection.toArray(IntFunction)` | (API) | `StringYeniMetotlar.java` içinde |
| 7 | Epsilon GC (deneysel) | JEP 318 | (README) |
| 8 | ZGC (deneysel) | JEP 333 | (README) |
| 9 | Flight Recorder | JEP 328 | (README) |

---

## 1) Standart HTTP Client — `java.net.http` (JEP 321)

📄 Örnek: **`YeniHttpClient.java`**

### NEDIR?
Java 11 ile `java.net.http` paketi **standart (kararlı)** hale geldi. Java 9'da
`jdk.incubator.http` adıyla **incubator (kuluçka/deneysel)** modül olarak gelmişti.
Java 11'de incubator'dan çıkıp tam JDK API'si oldu. `HttpClient`, `HttpRequest`,
`HttpResponse` üçlüsü ile modern HTTP istekleri atılır.

### NEDEN GELDI? (Hangi problem)
Eski `HttpURLConnection` (1997, Java 1.1) çok düşük seviyeli, **HTTP/2 desteği
yok**, **asenkron desteği yok**, WebSocket yok ve aşırı boilerplate kod
gerektiriyordu. Bu yüzden herkes 3. parti kütüphanelere (OkHttp, Apache
HttpClient) mecbur kalıyordu.

### NE ISE YARAR?
HTTP/1.1 + HTTP/2, senkron (`send`) ve asenkron (`sendAsync`), WebSocket,
Reactive Streams tabanlı gövde işleme, builder pattern ile temiz kod.

### NEREDE KOLAYLIK SAGLAR?
Mikroservisler arası REST çağrıları, 3. parti API entegrasyonları, paralel/async
yüksek performanslı istekler — hepsi harici kütüphane olmadan.

### ESKI vs YENI
```java
// ESKI (HttpURLConnection) — ~20+ satır:
URL url = new URL("https://api.com/data");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
String line; StringBuilder sb = new StringBuilder();
while ((line = in.readLine()) != null) sb.append(line);
in.close(); con.disconnect();
String body = sb.toString();

// YENI (Java 11) — 3 satır:
HttpClient client = HttpClient.newHttpClient();
HttpResponse<String> r = client.send(request, BodyHandlers.ofString());
String body = r.body();
```

### GERCEK HAYAT ORNEGI
E-ticaret ürün sayfası için stok, fiyat ve yorum servislerine **paralel async**
istek atıp toplam süreyi 300ms'den ~100ms'ye düşürmek.

---

## 2) Yeni String Metotları

📄 Örnek: **`StringYeniMetotlar.java`**

### NEDIR?
`strip()`, `stripLeading()`, `stripTrailing()`, `isBlank()`, `lines()`,
`repeat(int)`.

### NEDEN GELDI?
- **strip vs trim:** Eski `trim()` yalnızca `<= U+0020` karakterleri siler,
  Unicode boşlukları (örn. U+00A0, U+2003) **tanımaz**. `strip()` ise
  `Character.isWhitespace()` kullanır ve tüm Unicode boşlukları temizler.
- **isBlank:** `isEmpty()` sadece `""` için true; içi boşluk dolu `"   "` için
  false döner. `isBlank()` "boş veya sadece boşluk" sorusunu doğru cevaplar.
- **lines:** `split("\n")` Windows `\r\n` ile sorun çıkarır; `lines()` tüm satır
  sonlarını doğru tanır ve lazy `Stream<String>` verir.
- **repeat:** Bir metni N kez tekrarlamak için döngü/StringBuilder yazma derdini
  bitirir.

### ESKI vs YENI
```java
// ESKI:                              // YENI (Java 11):
s.replaceAll("^\\s+|\\s+$", "");      s.strip();
s == null || s.trim().isEmpty();      s.isBlank();
s.split("\\r?\\n");                   s.lines();
/* StringBuilder döngüsü */           "=".repeat(40);
```

### GERCEK HAYAT ORNEGI
Form verisi temizleme, log dosyası satır işleme, konsol raporu/tablo çizme.

---

## 6) `Collection.toArray(IntFunction)` — BONUS

📄 Örnek: `StringYeniMetotlar.java` içinde (Bölüm 5)

### NEDIR?
Bir koleksiyonu tipli diziye çevirirken, boş dizi nesnesi geçmek yerine bir
**method reference** verilebilir:
```java
// ESKI:  String[] dizi = list.toArray(new String[0]);
// YENI:  String[] dizi = list.toArray(String[]::new);
```
Daha okunabilir ve niyet açısından daha nettir.

---

## 3) `Files.readString` / `Files.writeString`

📄 Örnek: **`FilesReadWriteString.java`**

### NEDIR?
`Files.readString(Path)` dosyanın tüm içeriğini tek bir String'e okur;
`Files.writeString(Path, CharSequence)` bir String'i dosyaya yazar. Varsayılan
charset **UTF-8**'dir; Charset alan overload'lar da vardır.

### NEDEN GELDI?
Tüm dosyayı tek String okumak/yazmak çok yaygın ama eskiden tek satırlık yolu
yoktu: `Files.readAllBytes + new String`, `BufferedReader` döngüsü ya da
`Files.lines().collect(joining)` gerekiyordu — hepsi uzun ve charset hatasına
açıktı.

### ESKI vs YENI
```java
// ESKI:
byte[] b = Files.readAllBytes(path);
String s = new String(b, StandardCharsets.UTF_8);  // charset unutulursa Türkçe bozulur
// YENI:
String s = Files.readString(path);                 // tek satır, UTF-8 varsayılan
Files.writeString(path, s);
```

### NEREDE KOLAYLIK SAGLAR?
Config dosyaları (.properties/.json/.yaml), şablon dosyaları, SQL script'leri,
küçük metin dosyaları okuma/yazma.

### RISK
`readString` tüm dosyayı belleğe alır → çok büyük dosyalarda OOM riski. Büyük
dosyalar için hâlâ `Files.lines` (stream) tercih edilmeli.

---

## 4) Lambda Parametrelerinde `var` (JEP 323)

📄 Örnek: **`VarLambdaParametre.java`**

### NEDIR?
Java 10'da gelen `var` (yerel değişken tip çıkarımı) Java 11'de **lambda
parametrelerinde** de kullanılabilir: `(var a, var b) -> a + b`.

### NEDEN GELDI?
Tek başına çok kısaltma sağlamaz (zaten `(a,b) -> a+b` yazılabiliyor). Asıl
fayda: lambda parametresine **annotation** (örn. `@NonNull`) eklemek için bir
tipe ihtiyaç var; `var` bunu sağlarken uzun tip adını yazmaktan kurtarır:
```java
(@NonNull var a, @NonNull var b) -> a + b   // var ile mümkün
```

### KURALLAR
1. **Ya hepsi `var` ya hiçbiri** — karıştırılamaz (`(var a, b)` ❌,
   `(var a, Integer b)` ❌).
2. **Parantez zorunlu** — tek parametrede bile (`var x -> x` ❌, `(var x) -> x` ✓).

### ESKI (Java 10) vs YENI (Java 11)
```java
// Java 10: (a, b) -> a + b           // var YAZILAMAZ
// Java 11: (var a, var b) -> a + b   // var YAZILABILIR
```

---

## 5) Tek Dosya Kaynak Kodu Çalıştırma (JEP 330)

📄 Örnek: **`TekDosyaCalistirma.java`**

### NEDIR?
Bir `.java` dosyası **önceden derlemeden** doğrudan çalıştırılabilir:
```bash
java TekDosyaCalistirma.java
```
Java dosyayı **bellekte** derler ve çalıştırır; disk'e `.class` **yazmaz**.

### NEDEN GELDI?
Öğrenenler ve hızlı denemeler için iki adımlı süreç (`javac` → `java`)
gereksiz bir engeldi. Python/Ruby gibi "yaz-çalıştır" deneyimi getirildi.

### ESKI vs YENI
```bash
# ESKI (iki adım):
javac Merhaba.java   # .class oluşur
java Merhaba

# YENI (tek adım):
java Merhaba.java    # bellekte derle + çalıştır
```

### SHEBANG (#!) ile Script
Uzantısız bir dosyada ilk satıra `#!/usr/bin/java --source 11` konup
`chmod +x` ile çalıştırma izni verilirse, dosya bash/python script'i gibi
`./dosya` şeklinde çalıştırılabilir. (Shebang dosyası `.java` uzantılı olamaz.)

### NEREDE KOLAYLIK SAGLAR?
Öğrenme, hızlı prototipleme, küçük script'ler, API deneme.

---

## 7) Epsilon GC — "No-Op" Garbage Collector (JEP 318, deneysel)

### NEDIR?
Epsilon, **çöp toplamayan** (no-op) deneysel bir GC'dir. Bellek ayırır ama hiç
**geri toplamaz**; bellek dolunca uygulama durur.

### NEDEN / NEREDE?
- **Performans testi:** GC gürültüsü olmadan saf uygulama performansını ölçmek.
- **Çok kısa ömürlü işler:** Belleğin asla dolmayacağı kısa programlar (GC
  maliyetini sıfırlamak).
- **Bellek baskısı testi:** Bir uygulamanın gerçekten ne kadar bellek tükettiğini
  görmek.

Açma: `-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC`

---

## 8) ZGC — Z Garbage Collector (JEP 333, deneysel)

### NEDIR?
Çok düşük gecikmeli (low-latency), **ölçeklenebilir** bir çöp toplayıcı. Hedefi:
**duraklama (pause) sürelerini 10ms altında** tutmak — heap boyutundan (MB'den
TB'ye) bağımsız olarak.

### NEDEN / NEREDE?
Büyük heap'li, düşük gecikme isteyen uygulamalar (finansal işlem sistemleri,
büyük önbellekler, gerçek zamanlı analiz). Java 11'de Linux/x64'te **deneysel**
olarak geldi (sonraki sürümlerde production-ready oldu).

Açma: `-XX:+UnlockExperimentalVMOptions -XX:+UseZGC`

---

## 9) Java Flight Recorder — JFR (JEP 328)

### NEDIR?
JFR, JVM içinde çalışan **düşük maliyetli** bir profilleme/teşhis (profiling/
diagnostics) aracıdır. Önceden ticari (Oracle) bir özellikti; Java 11 ile
**OpenJDK'ye açık kaynak** olarak eklendi.

### NEDEN / NEREDE?
Üretim ortamında, neredeyse sıfır ek yük (overhead ~%1) ile uygulamanın iç
verilerini (GC, thread, I/O, bellek, kilitler) kaydeder. Sonradan JDK Mission
Control (JMC) ile analiz edilir. Performans sorunu/bellek sızıntısı teşhisi için
kurumsal ortamlarda paha biçilemez.

Örnek: `java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=kayit.jfr Uygulama`

---

## 🔄 Java 10'dan (ve Java 8'den) Java 11'e GEÇİŞTE NE DEĞİŞTİ?

### Java 8 → Java 11 (Büyük Sıçrama — en yaygın kurumsal geçiş)
- **Modül sistemi (JPMS)** Java 9'da gelmişti; 11'de olgunlaştı. `module-info.java`
  ve modüler classpath kavramları geldi.
- **Java EE / CORBA modülleri KALDIRILDI** (en kritik kırılma — aşağıda).
- Yeni HTTP Client, `var`, yeni String/Files metotları, tek dosya çalıştırma.
- `javafx` artık JDK ile gelmiyor (ayrı bağımlılık — OpenJFX).
- Birçok deprecated API kaldırıldı; bazı internal API'ler (sun.misc.*) erişimi
  kısıtlandı.

### Java 10 → Java 11 (Küçük ama önemli)
- `var` artık **lambda parametrelerinde** de kullanılabiliyor (JEP 323).
- Yeni String/Files metotları, standart HTTP Client, JFR, ZGC, Epsilon GC.
- Java EE/CORBA modülleri 10'da hâlâ `--add-modules` ile kullanılabilirken 11'de
  **tamamen kaldırıldı**.

---

## ⚖️ Avantaj / Dezavantaj / RISK

### ✅ Avantajlar
- **LTS** → uzun destek, üretim için ideal, kurumsal stabilite.
- Modern, harici kütüphanesiz HTTP Client.
- Daha temiz String/Files API'leri → daha az boilerplate.
- Yeni GC seçenekleri (ZGC, Epsilon) ve güçlü teşhis (JFR).
- Tek dosya çalıştırma ile hızlı deneme/öğrenme.
- Performans iyileştirmeleri ve daha küçük çalışma zamanı (jlink ile özel runtime).

### ⚠️ Dezavantajlar
- Java 8'den geçiş, kaldırılan modüller nedeniyle **kod değişikliği** gerektirebilir.
- Modül sistemi öğrenme eğrisi getirir.
- Bazı eski/3. parti kütüphaneler Java 11'de uyumsuz olabilir (özellikle internal
  API kullananlar).

### 🚨 RISK — Java EE ve CORBA Modüllerinin KALDIRILMASI (JEP 320)

**Bu, Java 8 → Java 11 geçişinde en sık karşılaşılan ve en sancılı sorundur.**

Java 11 ile aşağıdaki **Java EE ve CORBA** modülleri JDK'den **tamamen kaldırıldı**:

| Kaldırılan Paket / Modül | İşlevi |
|--------------------------|--------|
| `java.xml.bind` (JAXB — `javax.xml.bind.*`) | XML ↔ Java nesne bağlama (marshalling) |
| `java.xml.ws` (JAX-WS — `javax.xml.ws.*`, `javax.jws.*`) | SOAP web servisleri |
| `java.activation` (JAF — `javax.activation.*`) | Veri tipi yönetimi (JAXB/JAX-WS bağımlılığı) |
| `java.corba` (`org.omg.CORBA.*`) | CORBA dağıtık nesne mimarisi |
| `java.transaction` (`javax.transaction.*` — kısmi) | İşlem (transaction) API'si |
| `java.xml.ws.annotation` (`javax.annotation.*` — örn. `@PostConstruct`) | Ortak anotasyonlar |

#### Bu Neden Kurumsal Geçişte Sorun Yaratır?
Java 8 döneminde yazılmış birçok kurumsal uygulama, **farkında bile olmadan** bu
modülleri kullanıyordu (JDK ile geldikleri için classpath'te otomatik vardılar).
Örneğin JAXB ile XML işleyen, SOAP servisine bağlanan, `@PostConstruct` kullanan
kodlar... Java 11'e geçince bu sınıflar **artık JDK'de olmadığı için** çalışma
zamanında patlar:

```
java.lang.NoClassDefFoundError: javax/xml/bind/JAXBException
java.lang.ClassNotFoundException: javax.xml.bind.JAXBContext
NoClassDefFoundError: javax/annotation/PostConstruct
```

> Kod Java 8'de **sorunsuz derlenir ve çalışır**; Java 11'de **aynı kod
> NoClassDefFoundError verir**. Bu yüzden geçiş öncesi mutlaka bağımlılık
> taraması yapılmalıdır.

#### ÇÖZÜM — Eksik Modülleri Bağımlılık Olarak Ekleyin
Kaldırılan modüller artık **bağımsız kütüphaneler** olarak (Maven Central'dan)
eklenir. Örnek Maven bağımlılıkları:

```xml
<!-- JAXB API + Runtime -->
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>2.3.3</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.3</version>
</dependency>

<!-- JAX-WS (SOAP) -->
<dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-rt</artifactId>
    <version>2.3.3</version>
</dependency>

<!-- JavaBeans Activation Framework -->
<dependency>
    <groupId>com.sun.activation</groupId>
    <artifactId>javax.activation</artifactId>
    <version>1.2.0</version>
</dependency>

<!-- Common Annotations (@PostConstruct vb.) -->
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

> **CORBA için:** CORBA'nın resmî bir standalone yedeği yoktur (`org.omg.*`).
> CORBA kullanan eski sistemler ya GlassFish CORBA gibi 3. parti
> implementasyonlara taşınmalı ya da daha modern bir iletişim (REST/gRPC) ile
> yeniden yazılmalıdır. Bu, geçişin en maliyetli kısmı olabilir.

#### Geçiş Kontrol Listesi
1. `jdeps` aracıyla bağımlılıkları tara: `jdeps --jdk-internals uygulama.jar`
2. `javax.xml.bind`, `javax.xml.ws`, `javax.activation`, `javax.annotation`,
   `org.omg.*` kullanımlarını ara.
3. Eksik modülleri yukarıdaki gibi açık bağımlılık olarak ekle.
4. Tüm test paketini Java 11 altında çalıştır (sadece derleme yetmez — runtime'da
   patlar!).
5. 3. parti kütüphanelerin Java 11 uyumlu sürümlerine yükselt.
