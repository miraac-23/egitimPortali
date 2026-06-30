# Java 15 (Eylül 2020) — Detaylı Sürüm Analizi

> Bu doküman Java 15 sürümünü Türkçe olarak, her özellik için
> **NEDİR / NEDEN GELDİ / NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR /
> ESKİ vs YENİ / GERÇEK HAYAT ÖRNEĞİ** başlıklarıyla derinlemesine açıklar.

---

## 0. Sürüm Bilgisi (Genel Bakış)

| Bilgi | Değer |
|-------|-------|
| Sürüm | **Java SE 15 / JDK 15** |
| Çıkış Tarihi | **Eylül 2020** |
| Destek Türü | **Non-LTS** (kısa destekli ara sürüm) |
| Bir Önceki | Java 14 (Mart 2020) |
| Bir Sonraki | Java 16 (Mart 2021) |

Java 15, "feature release" (özellik sürümü) modelinin parçası olan 6 aylık ara
sürümlerden biridir. LTS (uzun süreli destek) değildir — LTS sürümleri 8, 11,
**17**, 21'dir. Buna rağmen Java 15, dil tarihinin çok önemli bir kavşağıdır:
**Text Blocks burada kalıcı oldu** ve **Sealed Classes burada ilk kez göründü.**

### Özelliklerin Olgunluk (Preview → Kalıcı) Durumu

| Özellik | Java 13 | Java 14 | **Java 15** | Java 16 | Java 17 |
|---------|---------|---------|-------------|---------|---------|
| **Text Blocks** | Preview | Preview | **KALICI ✅** | Kalıcı | Kalıcı |
| **Records** | — | 1. Preview | **2. Preview** | Kalıcı ✅ | Kalıcı |
| **Pattern Matching for `instanceof`** | — | 1. Preview | **2. Preview** | Kalıcı ✅ | Kalıcı |
| **Sealed Classes** | — | — | **1. Preview ⭐** | 2. Preview | Kalıcı ✅ |

> **Önemli ayrım:**
> - **Text Blocks**: 13–14'te preview idi, **15'te KALICI oldu** → artık `--enable-preview` GEREKMEZ.
> - **Sealed Classes**: **15'te İLK kez (preview)** geldi → 15/16'da `--enable-preview` gerekir, **17'de kalıcı**.
> - **Records**: **15'te İKİNCİ preview** → 16'da kalıcı.
> - **Pattern Matching for `instanceof`**: **15'te İKİNCİ preview** → 16'da kalıcı.

### Bu Repodaki Çalışan Örnek Dosyalar

| Dosya | Konu |
|-------|------|
| [`TextBlocksKalici.java`](./TextBlocksKalici.java) | Text Blocks (kalıcı) — JSON/SQL/HTML örnekleri |
| [`SealedClassesPreview.java`](./SealedClassesPreview.java) | Sealed Classes — Şekil ve Ödeme hiyerarşileri |
| [`RecordSealedBirlikte.java`](./RecordSealedBirlikte.java) | Records + Sealed birlikte — Cebirsel veri tipleri (ADT) |

---

## 1. Text Blocks — ARTIK KALICI (Standart) ✅

İlgili dosya: [`TextBlocksKalici.java`](./TextBlocksKalici.java)

### NEDİR
Üç çift tırnakla (`"""`) başlayıp biten **çok satırlı string** yazma biçimidir.
JEP 378 ile Java 15'te kalıcılaştı.

### NEDEN GELDİ (Hangi problemi çözüyor)
Java'da çok satırlı metin yazmak tarihsel olarak acı vericiydi:
- Her satır sonuna elle `\n` eklemek,
- Satırları `+` ile birleştirmek,
- Metnin içindeki her `"` karakterini `\"` olarak kaçırmak (özellikle JSON/HTML'de).

Buna topluca **"kaçış karakteri cehennemi"** denirdi. Kod, içerdiği metnin
gerçek halini hiç andırmazdı; okunması ve bakımı zordu.

### NE İŞE YARAR
Metni, bir editöre yazar gibi doğal, hizalı ve kaçışsız yazmamızı sağlar.
Satır sonları otomatik eklenir, gereksiz girinti otomatik temizlenir.

### NEREDE KOLAYLIK SAĞLAR
- **JSON** gövdeleri (REST istek/yanıt örnekleri, test verileri)
- **SQL** sorguları (çok satırlı, hizalı, okunaklı)
- **HTML / XML** şablonları (e-posta, rapor)
- **JSON Schema, GraphQL, regex açıklamaları, çok satırlı loglar**

### ESKİ vs YENİ

```java
// ESKİ (kaçış cehennemi)
String json = "{\n" +
              "  \"ad\": \"Ahmet\",\n" +
              "  \"yas\": 30\n" +
              "}";

// YENİ (Java 15 — kalıcı Text Block)
String json = """
        {
          "ad": "Ahmet",
          "yas": 30
        }""";
```

### Özel davranışlar (dosyada örnekli)
- **Otomatik girinti temizliği** (incidental white space): en az girintili satıra
  ve kapanış `"""` konumuna göre soldaki ortak boşluk silinir.
- **`\`** (satır sonunda): bir sonraki satırla **birleştirir** (yeni satır eklemez).
- **`\s`**: tek bir boşluk; satır sonundaki boşlukların korunmasını sağlar.
- Java 15 ayrıca `String.formatted(...)`, `String.stripIndent()`,
  `String.translateEscapes()` metotlarını da getirdi.

### GERÇEK HAYAT ÖRNEĞİ
Bir e-ticaret uygulamasında **sipariş onayı HTML e-postası** üretmek. Eskiden
HTML şablonu Java string olarak yazmak imkansıza yakındı; Text Block + `formatted`
ile şablon, gerçek bir `.html` dosyası gibi okunur (bkz. `ornek5_GercekHayat`).

---

## 2. Sealed Classes (Mühürlü Sınıflar) — İLK PREVIEW ⭐

İlgili dosya: [`SealedClassesPreview.java`](./SealedClassesPreview.java)

> **Sürüm:** Java 15'te **ilk preview** (JEP 360). 15/16'da derlemek için
> `javac --release 15 --enable-preview ...` gerekir. **Java 17'de kalıcı** oldu;
> 17+ ile bayraksız derlenir. (Dosyamız 17+ uyumlu yazıldı.)

### NEDİR
Bir sınıf/arayüzün **kendisini kimin extend/implement edebileceğini** açıkça
sınırlandırmasıdır. Bu kontrol `sealed` ve `permits` anahtar kelimeleriyle yapılır.

Dört anahtar kelime:
- **`sealed`**: "Mühürlüyüm; sadece izin verdiklerim alt tip olabilir."
- **`permits`**: İzin verilen alt tiplerin listesi.
- **`final`**: Alt tip kapatılır; ondan kimse türeyemez.
- **`non-sealed`**: Alt tip mührü açar; ondan herkes türeyebilir.

> Kural: Mühürlü bir tipi extend eden her alt sınıf **`final`, `sealed` veya
> `non-sealed`** üçünden birini seçmek zorundadır.

### NEDEN GELDİ (Hangi problemi çözüyor)
Eskiden bir hiyerarşiyi "yalnızca şu sınıflar genişletebilsin" diye kapatmanın
temiz bir yolu yoktu:
- **`final`** → hiç kimse türetemez (çok katı).
- **package-private constructor hilesi** → sadece aynı pakette türetilebilir;
  ama gerçek bir kontrol değil, niyeti belirsiz ve API tüketicisini şaşırtır.
- Hiçbir yöntem derleyiciye "bu hiyerarşi KAPALI ve alt tipler şunlardır"
  bilgisini veremiyordu → **exhaustive (tüm durumları kapsayan) switch yapılamıyordu.**

### NE İŞE YARAR
- Alan modelini (domain) **kasıtlı ve bilinen bir küme** olarak modellemek.
- Derleyicinin tüm alt tipleri bilmesi sayesinde **eksiksiz `switch`** yazmak;
  yeni bir alt tip eklenip de bir `switch` güncellenmezse **derleme hatası** almak.

### NEREDE KOLAYLIK SAĞLAR
- API kütüphaneleri: dışarıdan istenmeyen alt tip türetilmesini engelleme.
- Durum makineleri, AST/ifade ağaçları, ödeme/komut/olay türleri gibi
  **sonlu seçenek kümeleri.**

### ESKİ vs YENİ

```java
// ESKİ — niyet belirsiz, gerçek kontrol yok
abstract class Sekil {
    Sekil() {}              // package-private "hile" constructor
}

// YENİ — net ve derleyici denetimli
sealed abstract class Sekil permits Daire, Kare, Ucgen {}
final class Daire extends Sekil { ... }       // kapalı
final class Kare  extends Sekil { ... }       // kapalı
non-sealed class Ucgen extends Sekil { ... }  // bu dal kasıtlı açık
```

### GERÇEK HAYAT ÖRNEĞİ
**Ödeme türü hiyerarşisi.** Bir ödeme sisteminde yöntemler sabit ve bilinen bir
küme olmalıdır: `KrediKarti`, `Havale`, `Nakit`. Üçüncü partinin sisteme
"BitcoinÖdeme" gibi keyfi bir tür eklemesi iş kuralları açısından tehlikelidir.
`sealed interface Odeme permits ...` ile bu küme mühürlenir; `islemUcreti()`
içindeki `switch` her durumu kapsamak zorunda kalır (bkz. `odemeDemosu`).

---

## 3. Records — İKİNCİ PREVIEW

İlgili dosya (kullanım): [`RecordSealedBirlikte.java`](./RecordSealedBirlikte.java)

> **Sürüm:** Java 14'te ilk preview, **Java 15'te ikinci preview** (JEP 384),
> Java 16'da kalıcı.

### NEDİR
Yalnızca veri taşıyan, **değişmez (immutable)** sınıfları tek satırda tanımlamayı
sağlayan tip: `record Nokta(int x, int y) {}`. Derleyici otomatik olarak
constructor, alan erişimcileri, `equals`, `hashCode` ve `toString` üretir.

### NEDEN GELDİ
Klasik "POJO/DTO" sınıfları onlarca satırlık tekrarlı kalıp kod (boilerplate)
gerektiriyordu: özel alanlar, getter'lar, `equals`/`hashCode`/`toString`. Bu kod
hem yorucuydu hem de hataya açıktı.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
- DTO'lar, API yanıt/istek modelleri, değer nesneleri (value objects).
- Çoklu dönüş değerleri, harita anahtarları, kayıt ağaçları.
- 15'teki ikinci preview ile **yerel (local) record'lar** ve anotasyon davranışı
  iyileştirildi.

### ESKİ vs YENİ

```java
// ESKİ — ~30 satır boilerplate
public final class Nokta {
    private final int x, y;
    public Nokta(int x, int y){ this.x=x; this.y=y; }
    public int x(){ return x; } public int y(){ return y; }
    // equals, hashCode, toString ...
}

// YENİ
record Nokta(int x, int y) {}
```

### GERÇEK HAYAT ÖRNEĞİ
İfade ağacındaki her düğüm (`Sayi`, `Toplama`, `Carpma`) tek satırlık record'tur;
immutable ve eşitliği otomatik (bkz. bölüm 5 ve `RecordSealedBirlikte.java`).

---

## 4. Pattern Matching for `instanceof` — İKİNCİ PREVIEW

İlgili kullanım: `SealedClassesPreview.java` içindeki `aciklama(...)` metodu.

> **Sürüm:** Java 14'te ilk preview, **Java 15'te ikinci preview** (JEP 375),
> Java 16'da kalıcı.

### NEDİR
`instanceof` kontrolünün, başarılı olduğunda otomatik olarak doğru tipte bir
değişken bağlamasıdır (binding variable).

### NEDEN GELDİ
Klasik `instanceof` her zaman ardından **manuel cast** gerektiriyordu: önce
tip kontrol et, sonra aynı tipe elle dönüştür. Bu, tekrarlı ve hataya açıktı.

### ESKİ vs YENİ

```java
// ESKİ
if (obj instanceof String) {
    String s = (String) obj;   // manuel cast
    System.out.println(s.length());
}

// YENİ
if (obj instanceof String s) {  // s otomatik bağlanır
    System.out.println(s.length());
}
```

### NEREDE KOLAYLIK SAĞLAR
`equals` implementasyonları, tip ayrıştırma zincirleri ve (ileride) sealed +
record ile birleşince **eksiksiz pattern matching switch**'in temelini oluşturur.

---

## 5. Records + Sealed Birlikte — Cebirsel Veri Tipleri (ADT)

İlgili dosya: [`RecordSealedBirlikte.java`](./RecordSealedBirlikte.java)

> **Sürüm notu:** Java 15'te **hem records (2. preview) hem sealed (1. preview)**
> preview idi. O dönemde `--release 15 --enable-preview` gerekiyordu. Java 17+
> ile (sealed kalıcı, records kalıcı) bayraksız derlenir.

### NEDİR / NEDEN GÜÇLÜ
Fonksiyonel dillerden gelen **Cebirsel Veri Tipleri** desenini Java'ya getirir:

- `sealed interface` → **"bu tip şu seçeneklerden BİRİDİR"** (sum / toplam tip)
- `record` → **"her seçenek şu alanlardan oluşur"** (product / çarpım tip)
- `pattern matching switch` → her seçeneği güvenle ayrıştırma

```java
sealed interface Sekil permits Daire, Dikdortgen, Ucgen {}
record Daire(double yaricap) implements Sekil {}
record Dikdortgen(double en, double boy) implements Sekil {}
record Ucgen(double taban, double yukseklik) implements Sekil {}

static double alan(Sekil s) {
    return switch (s) {                 // 'default' GEREKMEZ — sealed sayesinde
        case Daire d        -> Math.PI * d.yaricap() * d.yaricap();
        case Dikdortgen d   -> d.en() * d.boy();
        case Ucgen u        -> u.taban() * u.yukseklik() / 2.0;
    };
}
```

Üçlünün birleşimi modern Java'da şunu sağlar: **az kod, değişmezlik, ve en
önemlisi derleyici garantili eksiksizlik.** Yeni bir alt tip eklenip de `switch`
güncellenmezse derleme başarısız olur — yani "unutulan durum" hatası daha
çalıştırmadan, derleme aşamasında yakalanır.

### GERÇEK HAYAT ÖRNEĞİ
**İfade ağacı (Expression Tree) / hesap makinesi.** Bir matematik ifadesi ya bir
sayıdır, ya iki ifadenin toplamı, ya çarpımıdır. `(2 + 3) * 4` ifadesi
`Carpma(Toplama(Sayi(2), Sayi(3)), Sayi(4))` olarak modellenir ve yinelemeli
`hesapla(...)` ile değerlendirilir (bkz. `ifadeAgaciDemosu`).

---

## 6. Sadece README'de Açıklanan Diğer Özellikler

### 6.1 Hidden Classes (Gizli Sınıflar) — JEP 371
- **NEDİR:** Çalışma zamanında (runtime) üretilen, normal sınıf yükleyiciler
  tarafından **bulunamayan/keşfedilemeyen** sınıflardır. Bytecode'dan dinamik
  olarak oluşturulur, başka kodlardan doğrudan referans verilemez.
- **NEDEN GELDİ:** Spring, Hibernate, mockito, dinamik proxy/lambda altyapıları
  gibi framework'ler çalışma zamanında sürekli sınıf üretir. Eski yöntem
  (`Unsafe.defineAnonymousClass`) desteklenmeyen bir iç API idi.
- **NE İŞE YARAR:** Framework'lere, çöp toplayıcı (GC) tarafından
  **gerektiğinde boşaltılabilen**, izole ve güvenli sınıf üretme imkanı verir.
  Bellek sızıntısı ve sınıf yükleyici şişmesini azaltır.
- **NOT:** Bu, son kullanıcı geliştirici için değil, **çerçeve (framework)
  yazarları** için bir altyapı özelliğidir; bu repoda kod örneği yoktur.

### 6.2 ZGC ve Shenandoah — ÜRETİME HAZIR (Production-Ready)
- **ZGC (JEP 377):** Düşük gecikmeli (çok kısa duraklamalı), terabaytlara kadar
  ölçeklenen çöp toplayıcı. Java 11'de deneysel gelmişti; **Java 15'te üretime
  hazır** ilan edildi.
- **Shenandoah (JEP 379):** Yine düşük duraklamalı GC; Java 12'de deneysel,
  **Java 15'te üretime hazır.**
- **NE İŞE YARAR:** GC duraklamalarının (pause) milisaniyeler değil
  mikrosaniyeler seviyesine inmesi gereken, büyük heap'li, gecikmeye duyarlı
  uygulamalarda (ticaret platformları, oyun sunucuları, büyük önbellekler).
- Kullanım: `java -XX:+UseZGC ...` veya `java -XX:+UseShenandoahGC ...`
  (artık `-XX:+UnlockExperimentalVMOptions` gerekmez).

### 6.3 Nashorn JavaScript Engine — KALDIRILDI (JEP 372) ⚠️
- **NEDİR:** Nashorn, JVM içinde JavaScript çalıştıran motordu (Java 8'de gelmiş,
  Java 11'de "deprecated" işaretlenmişti). **Java 15'te tamamen kaldırıldı.**
- **NEDEN:** ECMAScript'in hızlı evrimine ayak uydurmak maliyetliydi; ekosistemde
  GraalVM JavaScript gibi daha güçlü alternatifler oluştu.
- **MİGRASYON RİSKİ ⚠️:** `jdk.nashorn.*` paketlerini veya `ScriptEngineManager`
  ile `"nashorn"` motorunu kullanan kodlar **Java 15'te ÇALIŞMAZ.**
  `jjs` komut satırı aracı da kaldırıldı. Bu tür kodların **GraalVM JavaScript**
  veya başka bir script motoruna **taşınması** gerekir. Java 11'den 15'e geçişte
  en önemli "bozucu değişiklik"lerden (breaking change) biridir.

---

## 7. Java 14'ten Java 15'e Geçişte Ne Değişti?

| Konu | Java 14 | Java 15 |
|------|---------|---------|
| Text Blocks | Preview (bayrak gerekli) | **Kalıcı** (bayraksız) ✅ |
| Records | 1. Preview | 2. Preview (iyileştirildi) |
| Pattern Matching `instanceof` | 1. Preview | 2. Preview |
| Sealed Classes | Yok | **1. Preview (yeni!)** ⭐ |
| ZGC / Shenandoah | Deneysel | **Üretime hazır** |
| Nashorn | Deprecated | **Kaldırıldı** ⚠️ |
| Yeni String metotları | — | `formatted`, `stripIndent`, `translateEscapes` |

---

## 8. Avantajlar / Dezavantajlar / Riskler

### ✅ Avantajlar
- **Text Blocks artık kalıcı:** JSON/SQL/HTML içeren kod radikal biçimde okunaklı.
- **Sealed Classes (ön izleme):** Alan modelini güvenle kapatma; modern ADT'ye kapı.
- **Records + Sealed + Pattern Matching** birlikte: az kodla, derleyici garantili,
  eksiksiz (exhaustive) modelleme — Java'yı fonksiyonel dillere yaklaştırır.
- **ZGC/Shenandoah üretime hazır:** Büyük, gecikmeye duyarlı sistemler için ciddi
  performans seçeneği.

### ⚠️ Dezavantajlar
- **Java 15 LTS değildir.** Üretim için genelde 11 veya **17 (LTS)** tercih edilir;
  15 daha çok yeni özellikleri erken denemek içindir.
- Sealed/Records/Pattern Matching bu sürümde **preview** — API/sözdizimi sonraki
  sürümlerde değişebilir; preview kodu üretime almak risklidir.
- Preview özellikleri **`--enable-preview` bayrağı ve sürüm kilidi** ile gelir;
  derlenen sınıflar yalnızca aynı JDK sürümünde çalışır.

### 🚨 Riskler (özellikle migrasyonda)
- **Nashorn kaldırıldı:** `jdk.nashorn.*`, `"nashorn"` script motoru veya `jjs`
  kullanan kodlar **bozulur**. Geçiş öncesi bağımlılık taraması şart.
- Preview özellik kullanan kod, bir sonraki sürüme (16/17) geçerken sözdizimi/API
  değişiklikleri nedeniyle güncelleme gerektirebilir.
- 15 non-LTS olduğu için güvenlik güncellemeleri kısa sürelidir; uzun ömürlü
  projelerde doğrudan 15'e bağlanmak önerilmez.

---

## 9. Derleme ve Çalıştırma

```bash
# Text Blocks — Java 15+ (kalıcı, bayraksız)
javac TextBlocksKalici.java
java  TextBlocksKalici

# Sealed Classes — Java 15/16'da preview bayrağı gerekir:
#   javac --release 15 --enable-preview SealedClassesPreview.java
#   java  --enable-preview SealedClassesPreview
# Java 17+ ile bayraksız:
javac SealedClassesPreview.java
java  SealedClassesPreview

# Records + Sealed — Java 15'te ikisi de preview idi:
#   javac --release 15 --enable-preview RecordSealedBirlikte.java
#   java  --enable-preview RecordSealedBirlikte
# Java 17+ ile bayraksız (switch pattern matching için Java 21+ önerilir):
javac RecordSealedBirlikte.java
java  RecordSealedBirlikte
```

> Bu repodaki `.java` dosyaları, modern bir JDK (17+, tercihen 21) ile preview
> bayrağı olmadan derlenip çalıştırılabilecek şekilde yazılmış ve test edilmiştir.
