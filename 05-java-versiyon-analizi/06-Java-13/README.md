# Java 13 (Eylül 2019) — Detaylı Sürüm Analizi

> Bu doküman, Java 13 ile gelen yenilikleri **Türkçe**, derinlemesine ve gerçek
> hayat örnekleriyle anlatır. İlgili çalışan kod için bkz. `TextBlocksPreview.java`.

---

## Sürüm Bilgisi

| Özellik | Değer |
|---|---|
| **Sürüm adı** | Java SE 13 / JDK 13 |
| **Çıkış tarihi** | **17 Eylül 2019** |
| **Sürüm türü** | Feature Release (özellik sürümü, LTS DEĞİL) |
| **Destek durumu** | Kısa süreli (6 ay). LTS değildir; üretimde uzun süreli destek için Java 11 veya 17 tercih edilir. |
| **Önceki sürüm** | Java 12 (Mart 2019) |
| **Sonraki sürüm** | Java 14 (Mart 2020) |

Java 13, **6 aylık sürüm temposunun** bir parçasıdır. Yani devrim niteliğinde
büyük bir değişiklik değil, kademeli (incremental) iyileştirmeler getirir.
En çok konuşulan yeniliği **Text Blocks**'tur.

### Java 13'teki JEP'ler (genel bakış)

| JEP | Başlık | Durum (Java 13'te) |
|---|---|---|
| **JEP 355** | Text Blocks | **Preview (Önizleme)** |
| **JEP 354** | Switch Expressions | **Preview (ikinci tur)** — `yield` eklendi |
| **JEP 350** | Dynamic CDS Archives | Kalıcı |
| **JEP 351** | ZGC: Uncommit Unused Memory | Kalıcı (deneysel GC içinde iyileştirme) |
| JEP 353 | Reimplement the Legacy Socket API | Kalıcı (iç altyapı) |

---

## Preview (Önizleme) Kavramı — Çok Önemli

Java, büyük dil özelliklerini doğrudan "kalıcı" yapmak yerine önce **preview**
olarak yayınlar. Amaç: topluluktan geri bildirim toplamak ve özelliği
olgunlaştırmak. Preview özellikleri:

- Üretimde kullanılması **tavsiye edilmez** (sözdizimi değişebilir).
- Derlemek için özel bayrak gerekir: `javac --enable-preview --release 13 ...`
- Çalıştırmak için de bayrak gerekir: `java --enable-preview ...`

### Bu sürümdeki iki özelliğin preview → kalıcı evrimi (NET)

```
TEXT BLOCKS
  Java 13  -> Preview (1. tur, JEP 355)
  Java 14  -> Preview (2. tur, JEP 368)  [+ \ ve \s kaçış dizileri eklendi]
  Java 15  -> KALICI / STANDARD (JEP 378)

SWITCH EXPRESSIONS
  Java 12  -> Preview (1. tur, JEP 325)  [ok-etiketli "case ->" söz dizimi]
  Java 13  -> Preview (2. tur, JEP 354)  [değer döndürmede "break value" KALDIRILDI,
                                          yerine "yield" anahtar kelimesi GELDİ]
  Java 14  -> KALICI / STANDARD (JEP 361)
```

Bu evrimi anlamak kritik: **Java 13'ün en büyük katkısı yeni bir özellik icat
etmek değil, Java 12'de başlayan iki özelliği olgunlaştırmaktır.**

---

# ÖZELLİK 1: Text Blocks (Önizleme — JEP 355)

> İlgili dosya: **`TextBlocksPreview.java`**

### NEDİR?

Text Blocks, **çok satırlı string** (string literal) yazmayı sağlayan yeni bir
söz dizimidir. Üç çift tırnak (`"""`) ile başlar ve biter:

```java
String metin = """
        Birinci satır
        İkinci satır
        """;
```

### NEDEN GELDİ? (Hangi problemi çözüyor?)

Java'da en eski ve en can sıkıcı sorunlardan biri, çok satırlı veya içinde tırnak
geçen metinleri yazmaktı. Buna **"kaçış karakteri cehennemi" (escape hell)** denir:

- Her satır ayrı bir `"..."` literal olmak zorundaydı.
- Satırları `+` ile birleştirmek gerekiyordu.
- Yeni satır için her satıra `\n` eklemek gerekiyordu.
- Metnin içinde geçen her `"` işaretini `\"` olarak kaçırmak gerekiyordu.

Sonuç: Bir JSON, SQL veya HTML parçasını okumak/yazmak/bakımını yapmak işkenceye
dönüşüyordu. Kod, asıl temsil ettiği metne hiç benzemiyordu.

### NE İŞE YARAR?

- Çok satırlı metni **tam göründüğü gibi** yazmayı sağlar.
- `\n`, `\"`, `+` birleştirme **ihtiyacını ortadan kaldırır**.
- Girintiyi otomatik yönetir (incidental whitespace temizliği).

### NEREDE KOLAYLIK SAĞLAR?

- **JSON** gövdeleri (REST API istek/yanıt örnekleri, test verisi)
- **SQL** sorguları (uygulama içine gömülü uzun sorgular)
- **HTML / XML** parçaları (e-posta şablonları, sayfa parçaları)
- **JSON Schema, GraphQL, Regex açıklamaları, çok satırlı log mesajları**

### ESKİ vs YENİ — JSON Karşılaştırması

**ESKİ (Java 12 ve öncesi):**
```java
String json =
    "{\n" +
    "    \"ad\": \"Ahmet\",\n" +
    "    \"yas\": 30,\n" +
    "    \"aktif\": true\n" +
    "}";
```

**YENİ (Java 13+ Text Block):**
```java
String json = """
        {
            "ad": "Ahmet",
            "yas": 30,
            "aktif": true
        }""";
```

Görüldüğü gibi `\n` yok, `\"` yok, `+` yok. Metin tam JSON gibi okunuyor.

### ESKİ vs YENİ — SQL Karşılaştırması

**ESKİ:**
```java
String sql =
    "SELECT m.ad, s.tutar " +      // <- satır sonundaki boşluğa dikkat!
    "FROM musteriler m " +
    "JOIN siparisler s ON m.id = s.musteri_id " +
    "WHERE s.tutar > 1000";
```
Burada en büyük tuzak: satır sonlarına boşluk koymayı unutursanız
`siparislerWHERE` gibi kelimeler yapışır ve sorgu bozulur.

**YENİ:**
```java
String sql = """
        SELECT m.ad, s.tutar
        FROM musteriler m
        JOIN siparisler s ON m.id = s.musteri_id
        WHERE s.tutar > 1000
        """;
```

### GERÇEK HAYAT ÖRNEĞİ

Bir e-ticaret uygulamasında, sipariş onayı e-postası için HTML şablonu, bir
JDBC sorgusu ve bir REST API'ye gönderilecek JSON gövdesi düşünün. Üçü de çok
satırlı ve içinde tırnak barındıran metinlerdir. Text Blocks ile bu üç durumun
da kodu, neredeyse bir editörden kopyala-yapıştır yapılmış kadar temiz olur.
Bu, hem yazma süresini hem hata oranını ciddi şekilde düşürür.

> `TextBlocksPreview.java` dosyasında bu üç durumun (JSON, SQL, HTML) ESKİ/YENİ
> karşılaştırması, girinti yönetimi ve kaçış dizileri çalışan örneklerle var.

### Detaylar (kod örneğinde gösterilir)

- **Incidental whitespace (tesadüfi boşluk):** Derleyici, sadece kodu hizalamak
  için var olan sol boşlukları otomatik siler. Kapanış `"""` konumu, kaç boşluğun
  kırpılacağını belirler.
- **`\` (satır devamı):** Java **14**'te geldi. Kaynak kodda satırı bölersiniz ama
  çıktıda tek satır olur.
- **`\s` (boşluk koruma):** Java **14**'te geldi. Sondaki boşlukların kırpılmasını
  engeller (bir boşluk karakteri yerine geçer).

> NOT: `\` ve `\s` Java 13'te YOKTUR; Java 14'te eklenmiştir. `TextBlocksPreview.java`
> bu özellikleri kullandığı için en sorunsuz şekilde Java 14+ ile derlenir.

---

# ÖZELLİK 2: Switch Expressions Geliştirmesi — `yield` (Preview, JEP 354)

### NEDİR?

Java 12'de gelen "switch expression" (switch'i bir ifade/değer üreten yapı olarak
kullanma) özelliği, Java 13'te güncellendi. Değer döndürmek için kullanılan
`break <değer>;` söz dizimi **kaldırıldı**, yerine **`yield`** anahtar kelimesi
getirildi.

### NEDEN GELDİ?

Java 12'de bir switch bloğundan değer döndürmek için `break 5;` gibi bir söz dizimi
vardı. Bu kafa karıştırıcıydı çünkü `break` kelimesi tarihsel olarak "döngüden/
switch'ten çık" anlamına gelir; ona bir de "değer döndür" anlamı yüklemek belirsizdi.
Topluluk geri bildirimiyle bu ayrıştırıldı: `yield` sadece "bu bloğun ürettiği
değer budur" demek için kullanılır.

### NE İŞE YARAR?

`case ... ->` ok söz diziminde tek satır yetmediğinde, blok `{ }` açıp içinde
hesaplama yapıp `yield` ile değer döndürmeyi sağlar.

### ESKİ vs YENİ

**Java 12 (eski preview):**
```java
int sonuc = switch (gun) {
    case PAZARTESI, SALI -> 6;
    case CARSAMBA -> {
        int hesap = ...;
        break hesap;   // <- ESKİ: break ile değer döndürme
    }
    default -> 0;
};
```

**Java 13+ (yeni):**
```java
int sonuc = switch (gun) {
    case PAZARTESI, SALI -> 6;
    case CARSAMBA -> {
        int hesap = ...;
        yield hesap;   // <- YENİ: yield ile değer döndürme (daha net)
    }
    default -> 0;
};
```

`yield`, geleneksel `case x:` (iki nokta) söz diziminde de kullanılabilir:
```java
int sonuc = switch (gun) {
    case PAZARTESI:
        yield 6;
    default:
        yield 0;
};
```

### GERÇEK HAYAT ÖRNEĞİ

Bir HTTP durum kodunu kullanıcı dostu mesaja çevirirken, bazı durumlar için tek
satır (`case 404 -> "Bulunamadı"`), bazıları için loglama + mesaj üretimi gibi
çok adımlı işlem gerekebilir. Çok adımlı durumlarda blok açıp `yield` ile sonucu
döndürürsünüz; kod hem ifade hem deyim avantajını birlikte sunar.

> NOT: Bu özellik Java 13'te hâlâ preview'dir; Java 14'te kalıcı olmuştur.

---

# ÖZELLİK 3: Dynamic CDS Archives — Dinamik Sınıf Verisi Paylaşımı (JEP 350)

> Sadece kavramsal açıklama (örnek kod dosyası yok).

### NEDİR?

CDS (Class Data Sharing), sık kullanılan sınıfların ayrıştırılmış (parse edilmiş)
hâlini bir **arşiv dosyasına** kaydedip, JVM başlatılırken bu arşivi bellek
eşlemesiyle (memory-mapping) hızlıca yüklemeyi sağlayan bir tekniktir.

**Dynamic CDS**, Java 13'ün getirdiği yenilik: arşivi artık uygulamanın
**çalışması bittiğinde otomatik olarak** oluşturabilirsiniz. Eskiden bu süreç
çok adımlı ve manueldi (önce sınıf listesi çıkar, sonra arşiv üret).

### NEDEN GELDİ / NE İŞE YARAR?

- **Başlatma süresini (startup time) kısaltır.** Sınıfları her seferinde diskten
  okuyup ayrıştırmak yerine, hazır arşivi belleğe eşler.
- Mikroservis ve serverless gibi sık başlatılan ortamlarda kazanç önemlidir.

### NASIL KULLANILIR (kavramsal)

```bash
# Uygulama çıkışında dinamik arşiv oluştur:
java -XX:ArchiveClassesAtExit=uygulama.jsa -jar uygulama.jar

# Sonraki çalıştırmalarda arşivi kullan:
java -XX:SharedArchiveFile=uygulama.jsa -jar uygulama.jar
```

### AVANTAJ / RİSK

- **Avantaj:** Daha hızlı soğuk başlatma (cold start).
- **Risk:** Arşiv, üretildiği classpath'e bağlıdır; classpath değişirse arşiv
  geçersiz olur ve JVM sessizce arşivsiz devam eder (performans kazancı kaybolur).

---

# ÖZELLİK 4: ZGC — Kullanılmayan Belleği OS'e Geri Verme (JEP 351)

> Sadece kavramsal açıklama (örnek kod dosyası yok).

### NEDİR?

ZGC (Z Garbage Collector), düşük gecikmeli (low-latency) bir çöp toplayıcıdır.
Java 13'te ZGC'ye, **kullanmadığı belleği işletim sistemine geri verme**
(uncommit unused memory) yeteneği eklendi.

### NEDEN GELDİ / NE İŞE YARAR?

Eskiden ZGC, bir kez işletim sisteminden aldığı belleği uygulama o belleği
kullanmasa bile **elinde tutuyordu**. Bu, özellikle:

- Konteyner ortamlarında (bellek limiti olan Docker/Kubernetes pod'ları),
- Birden çok uygulamanın aynı makineyi paylaştığı senaryolarda

israfa yol açıyordu. Artık ZGC, uzun süre boşta kalan bellek bölgelerini
OS'e iade eder; böylece o bellek başka süreçlerce kullanılabilir.

### AVANTAJ / RİSK

- **Avantaj:** Daha iyi bellek ayak izi (memory footprint), konteynerlerde daha
  ekonomik kaynak kullanımı.
- **Not:** Java 13'te ZGC hâlâ **deneyseldi** ve yalnızca Linux/x64'te
  destekleniyordu. Üretim için olgunlaşması sonraki sürümlerde tamamlandı.

---

# Java 12 → Java 13 Geçişinde Ne Değişti? (Özet)

| Konu | Java 12 | Java 13 |
|---|---|---|
| Switch expression değer döndürme | `break <değer>;` | `yield <değer>;` (daha net) |
| Çok satırlı string | Yok (string birleştirme + `\n`) | **Text Blocks (preview)** |
| CDS arşivi | Statik / manuel | **Dinamik (çıkışta otomatik)** |
| ZGC bellek davranışı | Belleği elinde tutar | **Boşta belleği OS'e iade eder** |

### Avantajlar
- Text Blocks ile JSON/SQL/HTML kodu çok daha okunaklı ve hatasız.
- `yield` ile switch ifadeleri anlam olarak netleşti.
- Daha hızlı başlatma (Dynamic CDS) ve daha az bellek israfı (ZGC).

### Dezavantajlar / Riskler
- **Text Blocks ve Switch geliştirmeleri Java 13'te hâlâ PREVIEW'dir.** Üretimde
  kullanmak risklidir; söz dizimi sonraki sürümde değişebilir (nitekim 14'te `\`
  ve `\s` eklendi).
- Preview özellikler `--enable-preview` bayrağı zorunluluğu getirir; bu, build
  ve dağıtım ayarlarını karmaşıklaştırabilir.
- Java 13 **LTS değildir** (6 ay destek). Uzun vadeli projeler için doğrudan
  13'e bağlanmak yerine 11 veya 17 gibi LTS sürümler tercih edilmelidir.

---

# Derleme ve Çalıştırma

`TextBlocksPreview.java`, `\` ve `\s` (Java 14 özellikleri) kullandığı için en
sorunsuz şekilde **Java 15+ ile preview bayrağı OLMADAN** derlenir:

```bash
javac TextBlocksPreview.java
java  TextBlocksPreview
```

Java 13/14 ile preview olarak denemek isterseniz:

```bash
javac --enable-preview --release 14 TextBlocksPreview.java
java  --enable-preview TextBlocksPreview
```

(Java 13'te `\` ve `\s` desteklenmediği için ilgili örnek satırları o sürümde
derlenmez; bu kaçış dizileri 14'te eklenmiştir.)
