# Java 23 — Sürüm Analizi ve Özellikler

> Bu doküman Java 23 sürümünü Türkçe olarak detaylı şekilde açıklar. Hedef kitle:
> Java geliştiricileri, eğitmenler ve sürümler arası geçişi planlayanlar.

---

## 1. Sürüm Bilgisi

| Özellik | Değer |
|---|---|
| **Sürüm** | Java SE 23 (JDK 23) |
| **Çıkış Tarihi** | 17 Eylül 2024 |
| **Destek Türü** | **non-LTS** (Long Term Support DEĞİL) |
| **Önceki LTS** | Java 21 (Eylül 2023) |
| **Sonraki LTS** | Java 25 (Eylül 2025 — planlanan) |
| **Destek Süresi** | Genellikle 6 ay (bir sonraki feature sürümüne kadar) |
| **JDK Şeması** | 6 aylık feature-release kadansı devam ediyor |

> **Önemli not — non-LTS ne demek?**
> Java 23 bir "feature release" (özellik sürümü) olup yalnızca bir sonraki
> sürüm (Java 24) çıkana kadar (yaklaşık 6 ay) güncelleme alır. Production
> ortamları için genellikle LTS sürümleri (21, 25, ...) tercih edilir.
> Java 23, yeni özellikleri denemek ve preview API'lerle çalışmak için idealdir.

### Preview Durumu Açıklaması

Java'da bir özellik birden çok aşamadan geçer:

1. **Preview (1., 2., 3. tur)** — Tasarımı tamamlanmış ama henüz kalıcı olmayan,
   geri bildirimle değişebilen özellikler. Kullanmak için derleme ve çalıştırmada
   `--enable-preview` bayrağı gerekir.
2. **Final / Standart** — Kalıcı hale gelmiş, bayrak gerektirmeyen özellikler.

Java 23'te preview özelliklerini derlemek için:

```bash
# Derleme
javac --release 23 --enable-preview Ornek.java

# Çalıştırma
java --enable-preview Ornek
```

---

## 2. Java 23 JEP'leri (Genel Bakış)

| JEP | Özellik | Durum |
|---|---|---|
| **JEP 467** | Markdown Documentation Comments | **Final / Kalıcı** (preview değil) |
| **JEP 473** | Stream Gatherers | 2. Preview |
| **JEP 480** | Structured Concurrency | 3. Preview |
| **JEP 481** | Scoped Values | 3. Preview |
| **JEP 455** | Primitive Types in Patterns, instanceof, switch | Preview |
| **JEP 474** | ZGC: Generational Mode by Default | Final (davranış değişikliği) |
| **JEP 476** | Module Import Declarations | Preview |
| **JEP 482** | Flexible Constructor Bodies | 2. Preview |
| **JEP 471** | Deprecate the Memory-Access Methods in `sun.misc.Unsafe` | Final |

> Bu README her bir önemli özelliği aşağıda detaylandırır. Bazı özellikler
> kendi ayrı dosyalarında derinlemesine ele alınmıştır:
>
> - **Markdown Javadoc** → [`MarkdownJavadoc.md`](./MarkdownJavadoc.md)
> - **Stream Gatherers (gelişmiş örnekler)** → [`StreamGatherersGelismis.java`](./StreamGatherersGelismis.java)
> - **Module Import Declarations** → [`ModuleImportDeclarations.md`](./ModuleImportDeclarations.md)

---

## 3. JEP 467 — Markdown Documentation Comments (Final)

> Detaylı örnekler için bkz. [`MarkdownJavadoc.md`](./MarkdownJavadoc.md)

### NEDİR?
Javadoc yorumlarını artık HTML + `@tag` karışımı yerine **Markdown** ile
yazabilmenizi sağlayan özellik. Yeni yorum sözdizimi `///` (üç eğik çizgi) ile
başlar.

```java
/// Bu bir **Markdown** Javadoc yorumudur.
/// - Madde 1
/// - Madde 2
///
/// Bağlantı: [String]
public class Ornek { }
```

### NEDEN GELDİ?
Klasik Javadoc HTML tabanlıdır; `<p>`, `<ul>`, `<code>` gibi etiketler okunması
zor ve hata yapmaya açıktır. Markdown çok daha okunabilir ve geliştiricilerin
zaten README'lerde kullandığı bir formattır.

### NE İŞE YARAR?
- Dokümantasyon yorumlarını kaynak kodda daha okunabilir hale getirir.
- HTML kaçış karakterleriyle (`&lt;`, `&gt;`) uğraşmayı azaltır.
- Kod blokları, listeler, tablolar, bağlantılar daha kolay yazılır.

### NEREDE KOLAYLIK SAĞLAR?
API dokümantasyonu yazan kütüphane geliştiricileri, dahili kod dokümantasyonu
tutan ekipler.

### ESKİ vs YENİ (kısa)
```java
// ESKİ (HTML tabanlı)
/**
 * Toplama yapar.
 * <p>Örnek: <code>topla(2, 3)</code> → 5</p>
 * <ul><li>Negatif sayı kabul eder</li></ul>
 */

// YENİ (Markdown tabanlı)
/// Toplama yapar.
///
/// Örnek: `topla(2, 3)` → 5
///
/// - Negatif sayı kabul eder
```

### GERÇEK HAYAT ÖRNEĞİ
Bir açık kaynak kütüphane (örn. bir JSON parser) API dokümantasyonunu
Markdown'a geçirerek hem kaynak kodda hem de üretilen HTML'de daha temiz
dokümantasyon sunar.

---

## 4. JEP 473 — Stream Gatherers (2. Preview)

> Çalıştırılabilir gelişmiş örnekler için bkz. [`StreamGatherersGelismis.java`](./StreamGatherersGelismis.java)

### NEDİR?
`Stream` API'sine **özel ara (intermediate) operasyonlar** tanımlama yeteneği
ekleyen özellik. `stream.gather(...)` metodu ile `Gatherer` arayüzünü kullanırız.
`Collectors`'ın terminal operasyonlar için yaptığını, `Gatherer` ara operasyonlar
için yapar.

### NEDEN GELDİ?
`map`, `filter`, `flatMap` gibi yerleşik ara operasyonlar her ihtiyacı
karşılamıyordu. Örneğin "kayan pencere" (sliding window), "çalışan toplam"
(running sum), gruplayıp tekilleştirme gibi durumsal (stateful) operasyonları
standart API ile yazmak zordu.

### NE İŞE YARAR?
- Durumlu (stateful) ara operasyonlar yazılabilir.
- Yerleşik fabrikalar: `Gatherers.windowFixed(n)`, `Gatherers.windowSliding(n)`,
  `Gatherers.fold(...)`, `Gatherers.scan(...)`, `Gatherers.mapConcurrent(...)`.
- Birden çok elemanı tek bir elemana ya da bir elemanı birden çoğa dönüştürebilir.

### NEREDE KOLAYLIK SAĞLAR?
Veri işleme hatları (data pipelines), zaman serisi analizi (hareketli ortalama),
olay akışı işleme, batch'leme.

### ESKİ vs YENİ
```java
// ESKİ: Kayan pencere için elle index yönetimi gerekirdi
List<List<Integer>> pencereler = new ArrayList<>();
for (int i = 0; i + 3 <= liste.size(); i++) {
    pencereler.add(liste.subList(i, i + 3));
}

// YENİ: Tek satır
var pencereler = liste.stream()
    .gather(Gatherers.windowSliding(3))
    .toList();
```

### GERÇEK HAYAT ÖRNEĞİ
Borsa fiyatlarının 5 günlük hareketli ortalamasını hesaplamak:
`fiyatlar.stream().gather(Gatherers.windowSliding(5)).map(ortalama).toList()`.
Detaylı örnek `.java` dosyasındadır.

### AVANTAJ / DEZAVANTAJ / RİSK
- **Avantaj:** Çok daha okunabilir, yeniden kullanılabilir akış operasyonları.
- **Dezavantaj/Risk:** Hâlâ **preview** — API imzaları değişebilir,
  `--enable-preview` gerektirir, production'da dikkatli kullanılmalı.

---

## 5. JEP 480 — Structured Concurrency (3. Preview)

### NEDİR?
Birden fazla eşzamanlı görevi tek bir iş birimi gibi yöneten API.
`java.util.concurrent.StructuredTaskScope` ile alt görevler bir kapsam (scope)
içinde başlatılır, hepsi birlikte tamamlanır veya birlikte iptal edilir.

### NEDEN GELDİ?
Klasik `ExecutorService` + `Future` kullanımında görev sızıntısı (leak),
iptal yönetimi ve hata yayılımı (error propagation) hataya açıktır. Bir görev
başarısız olsa bile diğerleri arka planda boşuna çalışmaya devam edebilir.

### NE İŞE YARAR?
- Alt görevlerin yaşam döngüsünü kod bloğunun yapısına bağlar
  (try-with-resources gibi).
- "Birini başaranı bekle" (`ShutdownOnSuccess`) ya da "ilk hatada hepsini iptal et"
  (`ShutdownOnFailure`) gibi politikalar sunar.

### NEREDE KOLAYLIK SAĞLAR?
Mikroservis çağrılarının paralel yürütülmesi, fan-out/fan-in desenleri,
birden çok kaynaktan veri toplama.

### ESKİ vs YENİ
```java
// ESKİ
ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor();
Future<String> kullanici = ex.submit(() -> kullaniciGetir());
Future<Integer> siparis  = ex.submit(() -> siparisGetir());
// İptal/hata yönetimi elle, sızıntı riski yüksek

// YENİ (preview)
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var kullanici = scope.fork(() -> kullaniciGetir());
    var siparis   = scope.fork(() -> siparisGetir());
    scope.join().throwIfFailed();
    return birlestir(kullanici.get(), siparis.get());
}
```

### GERÇEK HAYAT ÖRNEĞİ
Bir ürün detay sayfası; ürün bilgisi, stok durumu ve yorumları üç ayrı servisten
paralel çeker. Biri başarısız olursa hepsi iptal edilir, kaynak israfı olmaz.

> **Risk:** Preview API. `StructuredTaskScope` imzası sürümler arasında
> değişebilir (nitekim ileri sürümlerde değişti). `--enable-preview` şarttır.

---

## 6. JEP 481 — Scoped Values (3. Preview)

### NEDİR?
Bir veriyi, onu açıkça parametre olarak geçirmeden, belirli bir yürütme kapsamı
(ve onun alt görevleri) içinde paylaşmayı sağlayan değişmez (immutable) yapı.
`ScopedValue` sınıfı kullanılır.

### NEDEN GELDİ?
`ThreadLocal`'ın problemleri vardı: değiştirilebilir (mutable), temizlenmesi
zor (memory leak riski), milyonlarca sanal iş parçacığında (virtual thread)
maliyetli. `ScopedValue` bu sorunları çözer.

### NE İŞE YARAR?
- Değer değişmezdir; `where(...).run(...)` bloğu boyunca geçerlidir.
- Blok bittiğinde otomatik temizlenir.
- Sanal iş parçacıklarıyla verimli çalışır, structured concurrency ile uyumludur.

### NEREDE KOLAYLIK SAĞLAR?
İstek bağlamı (request context), kullanıcı kimliği, transaction ID gibi
"ortam" bilgisini metot zincirleri boyunca taşımak.

### ESKİ vs YENİ
```java
// ESKİ: ThreadLocal
static final ThreadLocal<User> CURRENT = new ThreadLocal<>();
CURRENT.set(user);
try { isYap(); } finally { CURRENT.remove(); } // unutulursa leak!

// YENİ: ScopedValue (preview)
static final ScopedValue<User> CURRENT = ScopedValue.newInstance();
ScopedValue.where(CURRENT, user).run(() -> isYap()); // otomatik temizlik
```

### GERÇEK HAYAT ÖRNEĞİ
Web sunucusunda gelen her HTTP isteği için kimliği doğrulanmış kullanıcıyı
`ScopedValue` ile tutmak; istek işleme zincirindeki tüm metotlar bu değere
güvenli ve değişmez şekilde erişir.

> **Risk:** Preview. `--enable-preview` gerekir.

---

## 7. JEP 455 — Primitive Types in Patterns, instanceof, switch (Preview)

### NEDİR?
İlkel (primitive) tiplerin (`int`, `long`, `double`, ...) artık `instanceof`,
desen eşleştirme (pattern matching) ve `switch` içinde kullanılabilmesi.

### NEDEN GELDİ?
Önceki desen eşleştirme yalnızca referans tipleri destekliyordu. İlkel tipler
için tip kontrolü ve güvenli dönüşüm (özellikle daralma — narrowing) dil
seviyesinde yoktu.

### NE İŞE YARAR?
- `if (x instanceof int i)` gibi ilkel tip kontrolü.
- `switch` içinde ilkel desenler ve daralma kontrolleri
  (örn. bir `int` değer `byte` aralığına sığıyor mu?).

### ESKİ vs YENİ
```java
// ESKİ: elle aralık kontrolü
int x = 200;
if (x >= Byte.MIN_VALUE && x <= Byte.MAX_VALUE) {
    byte b = (byte) x;
}

// YENİ (preview): desen ile güvenli daralma
if (x instanceof byte b) {
    // x, byte aralığına sığıyorsa b atanır
}
```

### GERÇEK HAYAT ÖRNEĞİ
Bir veri dönüştürme katmanında, gelen sayısal değerlerin hedef ilkel tipe güvenli
şekilde sığıp sığmadığını desenlerle kontrol etmek.

> **Risk:** Preview. `--enable-preview` gerekir.

---

## 8. JEP 474 — ZGC: Generational Mode by Default (Final)

### NEDİR?
Z Garbage Collector (ZGC) artık **varsayılan olarak generational (kuşaksal)**
modda çalışır. Eski (non-generational) mod hâlâ seçilebilir ama önerilmez.

### NEDEN GELDİ?
Generational hipotezi ("çoğu nesne genç ölür") sayesinde nesneleri genç (young)
ve yaşlı (old) kuşaklara ayırarak GC daha az CPU ve bellek harcar. Java 21'de
generational ZGC eklenmişti; Java 23'te bu artık varsayılan.

### NE İŞE YARAR?
- Çok büyük heap'lerde (TB seviyesi) düşük duraklama (low pause) süreleri.
- Daha az bellek ve CPU yükü, daha yüksek verim (throughput).

### ESKİ vs YENİ
```bash
# Java 21–22: generational ZGC açıkça istenirdi
java -XX:+UseZGC -XX:+ZGenerational uygulama

# Java 23: -XX:+UseZGC zaten generational; non-generational deprecated
java -XX:+UseZGC uygulama
```

### NEREDE KOLAYLIK SAĞLAR?
Büyük bellekli, düşük gecikme gerektiren sunucu uygulamaları (örn. büyük cache,
gerçek zamanlı işleme).

> **Geçişte ne değişti:** `-XX:+ZGenerational` bayrağı artık gereksiz (ve
> deprecated). ZGC kullanan uygulamalar otomatik olarak kuşaksal modda çalışır.

---

## 9. JEP 476 — Module Import Declarations (Preview)

> Detaylı anlatım için bkz. [`ModuleImportDeclarations.md`](./ModuleImportDeclarations.md)

### NEDİR?
`import module M;` sözdizimi ile bir modülün **export ettiği tüm paketleri** tek
satırda içe aktarma. Örneğin `import module java.base;` ile `java.util`,
`java.io`, `java.util.stream` vb. tüm paketler kullanılabilir hale gelir.

### NEDEN GELDİ?
Özellikle eğitim, prototipleme ve script benzeri kullanımda onlarca `import`
satırı yazmak yorucuydu. Modül seviyesinde toplu import bunu basitleştirir.

### ESKİ vs YENİ
```java
// ESKİ
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

// YENİ (preview)
import module java.base;
```

### GERÇEK HAYAT ÖRNEĞİ
Yeni başlayanların ilk programları, hızlı prototipler veya tek dosyalık
çalıştırılabilir kaynak (`java Ornek.java`) senaryoları.

> **Risk:** Preview. İsim çakışmaları (aynı isimli sınıflar farklı paketlerde)
> belirsizlik yaratabilir; bu durumda tek tek import gerekir. `--enable-preview`
> gerekir. Detaylar `ModuleImportDeclarations.md` dosyasında.

---

## 10. JEP 482 — Flexible Constructor Bodies (2. Preview)

### NEDİR?
Bir yapıcıda (constructor) `super(...)` veya `this(...)` çağrısından **önce**
deyimler (statement) çalıştırabilme imkânı. Önceden bu çağrı yapıcının ilk
deyimi olmak zorundaydı.

### NEDEN GELDİ?
`super()` çağrısından önce argümanları doğrulamak (validation), hazırlamak ya da
normalize etmek mümkün değildi; bu yüzden statik yardımcı metotlar gibi geçici
çözümler (workaround) kullanılırdı.

### NE İŞE YARAR?
- `super(...)`/`this(...)` öncesinde argüman doğrulama yapılabilir
  ("prologue" / ön bölge).
- Hatalı argümanlar üst sınıf yapıcısı çalışmadan yakalanır — daha güvenli.

### ESKİ vs YENİ
```java
// ESKİ: doğrulama super'den sonra ya da statik metotla
class Kullanici extends Kisi {
    Kullanici(String ad) {
        super(ad);                 // önce zorunlu
        if (ad == null) throw ...; // doğrulama sonra (geç!)
    }
}

// YENİ (preview): super'den önce doğrulama
class Kullanici extends Kisi {
    Kullanici(String ad) {
        if (ad == null || ad.isBlank())   // önce doğrula
            throw new IllegalArgumentException("ad bos olamaz");
        super(ad);                         // sonra üst yapıcı
    }
}
```

> Not: `super(...)` öncesindeki "prologue" bölgesinde henüz `this` örneğinin
> alanlarına/metotlarına erişilemez (örnek tam oluşmadığı için), sadece
> hesaplama/doğrulama yapılabilir.

### GERÇEK HAYAT ÖRNEĞİ
Bir `PositiveNumber extends Number` sınıfında, üst yapıcıya değer verilmeden
önce negatif değerin reddedilmesi.

> **Risk:** Preview. `--enable-preview` gerekir.

---

## 11. Geçiş Notları (Java 21/22 → Java 23)

### Avantajlar
- Daha temiz dokümantasyon (Markdown Javadoc — **kalıcı**, risksiz).
- ZGC kullananlar için otomatik performans iyileştirmesi (generational varsayılan).
- Yeni dil özelliklerini (gatherers, module import, primitive patterns) deneme imkânı.

### Dezavantajlar / Riskler
- **non-LTS:** Yalnızca ~6 ay destek; production için 25'i (LTS) beklemek mantıklı.
- Birçok yeni özellik **preview** — API değişebilir, `--enable-preview` zorunlu,
  production'da kullanımı önerilmez.
- `sun.misc.Unsafe` bellek erişim metotları **deprecated** (JEP 471) — bunlara
  bağımlı eski kütüphaneler ileride sorun yaşayabilir; alternatif olarak
  `VarHandle` / `MemorySegment` (FFM API) önerilir.

### Genel Tavsiye
- **Öğrenme/deneme:** Java 23 mükemmel — yeni özellikleri deneyin.
- **Production:** Stabil iş yükleri için LTS (21 veya 25) tercih edin; preview
  özelliklere bel bağlamayın.

---

## 12. İlgili Dosyalar

| Dosya | İçerik |
|---|---|
| [`MarkdownJavadoc.md`](./MarkdownJavadoc.md) | Markdown Javadoc (JEP 467) detaylı, eski/yeni yan yana |
| [`StreamGatherersGelismis.java`](./StreamGatherersGelismis.java) | Stream Gatherers gelişmiş, çalıştırılabilir örnekler |
| [`ModuleImportDeclarations.md`](./ModuleImportDeclarations.md) | Module Import Declarations (JEP 476) detaylı |

---

*Hazırlanma tarihi: 2026 — Java 23 (Eylül 2024) baz alınmıştır.*
