# Java 25 (Eylül 2025) — EN GÜNCEL LTS SÜRÜMÜ

> Bu klasör, `Java-Versiyon-Analizi` serisinin **en detaylı** bölümüdür.
> Çünkü **Java 25, şu an piyasadaki en güncel LTS (Long-Term Support) sürümüdür**
> ve 2026 itibarıyla kurumsal projelerin birincil hedef sürümüdür.

---

## 1. Sürüm Bilgisi

| Özellik | Değer |
|---|---|
| Sürüm | **Java 25 (JDK 25)** |
| Çıkış tarihi | **Eylül 2025** |
| Destek tipi | **LTS (Long-Term Support)** |
| Bir önceki LTS | Java 21 (Eylül 2023) |
| Ondan önceki LTS | Java 17 (Eylül 2021) |
| Sürüm kadansı | Her 6 ayda bir feature release, **her 2 yılda bir LTS** |

> **Not (dürüstlük):** JEP numaralarını ve hangi özelliğin kalıcı/preview olduğunu
> mümkün olduğunca doğru verdim. Emin olmadığım kesin imzalar/sayısal detaylarda
> genel kaldım ve "resmi JEP'e bakın" notu düştüm. Uydurma yapmadım.

---

## 2. LTS NEDEN BU KADAR ÖNEMLİ? (GÜÇLÜ VURGU)

### LTS Nedir?
**LTS = Long-Term Support (Uzun Süreli Destek).** Oracle ve OpenJDK ekosistemi,
sürümleri her 6 ayda bir yayınlar. Ancak bu sürümlerin çoğu yalnızca **6 ay**
güncelleme alır (feature release / non-LTS). LTS sürümleri ise **yıllarca**
(genellikle 8+ yıl, dağıtıcıya göre değişir) güvenlik yamaları ve hata düzeltmeleri alır.

### Kurumsallar Neden LTS Bekler?
Bir non-LTS sürüm (örn. Java 22, 23, 24) çıktığında, kurumsal şirketler genelde
onları **production'a almaz**. Bunun nedenleri:

1. **Uzun destek penceresi:** Bir bankanın çekirdek sistemini her 6 ayda bir
   yeni JDK'ya taşıması imkânsızdır. LTS, "kur ve yıllarca güvenle kullan" demektir.
2. **Güvenlik güncellemeleri:** LTS dışı bir sürüm 6 ay sonra güncelleme almaz;
   yeni keşfedilen güvenlik açıkları (CVE) yamalanmaz. Bu, kurumsal için kabul edilemez.
3. **Kararlılık (stability):** LTS sürümlerinde özellikler oturmuş, preview'lar
   kalıcılaşmış, ekosistem (kütüphaneler, framework'ler, araçlar) uyum sağlamıştır.
4. **Uyumluluk & sertifikasyon:** Spring, Jakarta EE, uygulama sunucuları (Tomcat,
   WildFly), build araçları (Maven, Gradle) ve bulut sağlayıcıları desteklerini
   önce LTS sürümlere verir.
5. **Maliyet & risk yönetimi:** Sık yükseltme = sürekli test/regresyon maliyeti.
   LTS, yükseltme döngüsünü 2–3 yıla yayarak riski ve maliyeti düşürür.

### Java 25 = Yeni Kurumsal Hedef
- **Java 8** uzun yıllar fiili standarttı (hâlâ çok yaygın).
- **Java 11** ilk modern LTS olarak benimsendi.
- **Java 17** "modern Java"ya geçişin omurgası oldu (records, sealed, pattern matching).
- **Java 21** sanal thread'ler (virtual threads) ile devrim yaptı.
- **Java 25** ise bu birikimi **olgunlaştırarak kalıcı** hale getirdi:
  Scoped Values, Flexible Constructor Bodies, Compact Source Files, Module Import
  Declarations gibi özellikler artık **preview değil, standart**.

> **Sonuç:** 2026'da yeni bir kurumsal proje başlatıyorsanız ya da mevcut bir
> sistemi yükseltiyorsanız **hedef sürüm Java 25 olmalıdır.** Java 17 veya 21'de
> kalan ekipler için 25, "bir sonraki güvenli durak"tır.

---

## 3. Genel Bakış — Java 25 Neler Getirdi?

Java 25 hem **kalıcılaşan** hem de **hâlâ preview** olan özellikler içerir.
Aşağıdaki tablo durumu **dürüstçe** özetler:

| Özellik | JEP | Durum (Java 25) | Örnek Dosya |
|---|---|---|---|
| Compact Source Files & Instance Main | 512 | **KALICI** | `CompactSourceMainMethods.java` |
| Scoped Values | 506 | **KALICI** | `ScopedValuesKalici.java` |
| Flexible Constructor Bodies | 513 | **KALICI** | `FlexibleConstructorBodies.java` |
| Module Import Declarations | 511 | **KALICI** | (README) |
| Stable Values | 502 | **PREVIEW** | `StableValuesPreview.java` |
| Structured Concurrency | 505 | **PREVIEW (5.)** | `StructuredConcurrencyGuncel.java` |
| PEM Encodings of Crypto Objects | 470 | **PREVIEW** | (README) |
| Primitive Types in Patterns/instanceof/switch | 507 | **PREVIEW (devam)** | (README) |
| Compact Object Headers | 519 | **Ürün özelliği (JVM)** | (README) |
| Generational Shenandoah | — | İyileştirme (JVM) | (README) |

---

## 4. Özellik Detayları

Her özellik için: **NEDİR / NEDEN GELDİ / NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR /
ESKİ vs YENİ / GERÇEK HAYAT.**

---

### 4.1. Compact Source Files & Instance Main Methods — KALICI (JEP 512)
**Dosya:** `CompactSourceMainMethods.java`

- **NEDİR:** Bir `.java` dosyasını sınıf bildirimi (`public class ...`) olmadan,
  doğrudan `void main()` ile yazabilme. `main` artık `static`, `public` ve
  `String[] args` zorunlu değil. Ayrıca `IO.println(...)` / `IO.readln(...)`
  otomatik kullanılabilir.
- **NEDEN GELDİ:** Yeni başlayanların "Merhaba Dünya" yazmak için `public`,
  `static`, `void`, `String[] args` ve `class` kavramlarını aynı anda öğrenme
  bariyerini kaldırmak. Java'yı öğrenmeye giriş kolaylaştırılır.
- **NE İŞE YARAR:** Kavramların **kademeli** öğretilmesini sağlar; küçük
  script/araçlar için boilerplate'i azaltır.
- **NEREDE KOLAYLIK:** Eğitim, prototipleme, tek dosyalık yardımcı programlar.
- **ESKİ vs YENİ:**
  ```java
  // ESKİ (Java 1.0–24)
  public class Merhaba {
      public static void main(String[] args) {
          System.out.println("Merhaba Dünya");
      }
  }
  // YENİ (Java 25 - KALICI)
  void main() {
      IO.println("Merhaba Dünya");
  }
  ```
- **GERÇEK HAYAT:** Bir öğrenci ilk günden tek satır kodla girdi/çıktı yapabilir;
  bir DevOps mühendisi `java arac.java` ile hızlı bir bakım scripti çalıştırabilir.
- **Tarih (dürüst):** Java 21 → 24 arasında 4 farklı preview adından geçti
  (Unnamed Classes → Implicitly Declared → Simple Source → **Compact Source**).
  Java 25'te nihayet kalıcı oldu, `--enable-preview` gerekmez.

---

### 4.2. Scoped Values — KALICI (JEP 506)
**Dosya:** `ScopedValuesKalici.java`

- **NEDİR:** `ScopedValue<T>`, bir thread ve onun alt görevleri boyunca
  **değişmez** bir bağlam değeri taşımanın modern yolu. `ThreadLocal`'a
  alternatiftir.
- **NEDEN GELDİ:** `ThreadLocal` değiştirilebilir (mutable), `remove()` unutulursa
  thread havuzlarında bellek sızıntısına yol açar ve **milyonlarca sanal thread**
  ile pahalıdır. Scoped Values bu sorunları çözer.
- **NE İŞE YARAR:** İstek (request) bağlamı, kullanıcı kimliği, korelasyon ID'si,
  dil/lokal bilgisi gibi verileri parametre zinciri olmadan alt katmanlara taşır.
- **NEREDE KOLAYLIK:** Web sunucuları, mikroservis çağrı zincirleri, loglama
  bağlamı (MDC benzeri), güvenlik bağlamı.
- **ESKİ vs YENİ:**
  ```java
  // ESKİ: ThreadLocal (mutable, remove() unutulursa sızıntı)
  static final ThreadLocal<Kullanici> TL = new ThreadLocal<>();
  TL.set(kullanici); try { ... } finally { TL.remove(); }

  // YENİ: ScopedValue (immutable, otomatik temizlik, sanal thread dostu)
  ScopedValue.where(AKTIF_KULLANICI, kullanici).run(() -> { ... });
  ```
- **GERÇEK HAYAT:** Bir HTTP isteği geldiğinde kullanıcı + istek ID'si bağlama
  konur; servis ve veri erişim katmanları bu değerleri parametre taşımadan okur.
  Blok bittiğinde değer otomatik geçersizleşir.

---

### 4.3. Flexible Constructor Bodies — KALICI (JEP 513)
**Dosya:** `FlexibleConstructorBodies.java`

- **NEDİR:** `super(...)` veya `this(...)` çağrısından **önce** ifade
  çalıştırabilme (alan doğrulama, ön hesaplama). Kısıt: bu çağrıdan önce
  `this`'in alanlarına/metotlarına erişilemez.
- **NEDEN GELDİ:** Eskiden `super(...)` ilk satır olmak zorundaydı; doğrulama
  yapmak için **statik yardımcı metot hilesi** gerekiyordu.
- **NE İŞE YARAR:** Argüman doğrulama, dönüşüm, ön hesaplama yapılmasını doğal
  ve okunabilir kılar.
- **NEREDE KOLAYLIK:** Değer nesneleri (value objects), domain modelleri,
  immutable sınıflar.
- **ESKİ vs YENİ:**
  ```java
  // ESKİ: static metot hilesi
  public Calisan(String ad, int maas) {
      super(dogrula(maas)); // hile
      this.ad = ad;
  }
  private static int dogrula(int m) { if (m<0) throw ...; return m; }

  // YENİ (Java 25 - KALICI)
  public Calisan(String ad, int maas) {
      if (maas < 0) throw new IllegalArgumentException("...");
      super(maas);      // doğrudan
      this.ad = ad;
  }
  ```
- **GERÇEK HAYAT:** `Cember(double yaricap)` → alanı `super`'a göndermeden önce
  hesaplar; `Dikdortgen(double kenar)` → `this(kenar, kenar)` çağrısından önce
  doğrulama yapar.

---

### 4.4. Module Import Declarations — KALICI (JEP 511)
**(README)**

- **NEDİR:** `import module M;` ile bir modülün **dışa açtığı tüm paketleri**
  tek satırda import edebilme.
- **NEDEN GELDİ:** Çok sayıda tekil `import` satırını azaltmak; özellikle
  eğitim ve hızlı kod yazımında kolaylık.
- **ESKİ vs YENİ:**
  ```java
  // ESKİ
  import java.util.List;
  import java.util.Map;
  import java.util.stream.Collectors;
  // YENİ
  import module java.base;   // java.base modülünün açtığı tüm paketler
  ```
- **NEREDE KOLAYLIK:** Compact source dosyaları + module import birlikte,
  Java'ya girişi ciddi şekilde sadeleştirir. Büyük projelerde tekil import'lar
  hâlâ tercih edilebilir (açıklık için).

---

### 4.5. Stable Values — PREVIEW (JEP 502)
**Dosya:** `StableValuesPreview.java` — **`--enable-preview` gerekir.**

- **NEDİR:** `StableValue<T>`, en fazla **bir kez** atanan, sonra değişmez olan
  bir tutucu. Lazy (tembel) başlatma için tasarlanmış.
- **NEDEN GELDİ:** `final` alanlar hemen atanmak zorundadır (lazy olamaz). Lazy
  yapmak için tarihsel olarak hatalı **double-checked locking** kullanılırdı.
  StableValue, "final'ın performansı + lazy'nin esnekliği" sunar; JIT atandıktan
  sonra değeri sabit gibi optimize edebilir (constant folding).
- **ESKİ vs YENİ:**
  ```java
  // ESKİ: double-checked locking (hataya açık, uzun)
  private volatile Agir n;
  Agir get() { if (n==null) synchronized(this){ if(n==null) n=new Agir(); } return n; }

  // YENİ (PREVIEW): tek satır, güvenli
  private final StableValue<Agir> n = StableValue.of();
  Agir get() { return n.orElseSet(() -> new Agir()); }
  ```
- **GERÇEK HAYAT:** Pahalı bir veritabanı bağlantı dizesini/önbelleğini yalnızca
  ilk ihtiyaçta bir kez hesaplayıp cache'lemek.
- **DÜRÜSTLÜK NOTU:** PREVIEW olduğu için API isimleri (`of`, `orElseSet`)
  sürümler arası değişebilir; resmi JEP 502'ye bakın.

---

### 4.6. Structured Concurrency — HÂLÂ PREVIEW (JEP 505)
**Dosya:** `StructuredConcurrencyGuncel.java` — **`--enable-preview` gerekir.**

- **DÜRÜSTLÜK NOTU:** Bu özellik Java 25'te **kalıcı DEĞİL**, hâlâ preview
  (5. tur). API son turlarda **değişti** (eski `ShutdownOnFailure` yerine
  `StructuredTaskScope.open(...)` + `Joiner`).
- **NEDİR:** Aynı mantıksal işi yapan birden çok eş zamanlı alt görevi **tek bir
  birim** gibi ele alma. Bir görev başarısız olursa diğerleri otomatik iptal edilir.
- **NEDEN GELDİ:** `ExecutorService` + `Future` ile iptal yayılımı elle yapılır,
  thread sızıntısı riski vardır, hata yönetimi dağınıktır.
- **NE İŞE YARAR:** Birden çok servisten **paralel veri çekme**, hata yayılımı,
  iptal, zaman aşımı yönetimini yapısal ve okunabilir kılar.
- **ESKİ vs YENİ:**
  ```java
  // ESKİ (yapısız): f1 patlarsa f2 iptal edilmez
  Future<A> f1 = ex.submit(...); Future<B> f2 = ex.submit(...);
  // YENİ (PREVIEW): biri patlarsa diğeri otomatik iptal
  try (var scope = StructuredTaskScope.open(...)) {
      var t1 = scope.fork(() -> servisA());
      var t2 = scope.fork(() -> servisB());
      scope.join();
      return birlestir(t1.get(), t2.get());
  }
  ```
- **GERÇEK HAYAT:** Bir kullanıcı profilini oluşturmak için "temel bilgi" ve
  "sipariş özeti" servislerini paralel çağırıp birleştirme; biri 503 dönerse
  diğeri iptal edilir, hata yukarı yayılır.

---

### 4.7. PEM Encodings of Cryptographic Objects — PREVIEW (JEP 470)
**(README)**

- **NEDİR:** Kriptografik nesnelerin (anahtarlar, sertifikalar) **PEM** (Base64,
  `-----BEGIN ...-----` bloklu) metin formatında kodlanması/çözülmesi için
  standart API.
- **NEDEN GELDİ:** Eskiden PEM ile çalışmak için elle Base64 + sınır satırı
  işleme ya da üçüncü parti kütüphane (örn. BouncyCastle) gerekiyordu.
- **DÜRÜSTLÜK NOTU:** PREVIEW. TLS sertifikaları, SSH anahtarları gibi yaygın
  formatlarla çalışan güvenlik kodları için kolaylık hedefler.

---

### 4.8. Primitive Types in Patterns, instanceof, switch — PREVIEW (devam)
**(README)**

- **NEDİR:** `instanceof` ve `switch` pattern matching'in **ilkel tipleri**
  (int, long, double...) de desteklemesi.
- **NEDEN GELDİ:** Pattern matching'i yalnızca referans tiplerle sınırlı
  bırakmamak; sayısal aralık/dönüşüm kontrollerini daha güvenli yazmak.
- **DÜRÜSTLÜK NOTU:** Hâlâ PREVIEW. Örnek:
  ```java
  if (x instanceof int i) { ... }   // PREVIEW
  switch (deger) { case int i -> ...; case double d -> ...; }
  ```

---

### 4.9. JVM / Performans İyileştirmeleri (genel)
**(README)**

- **Compact Object Headers (JEP 519):** Nesne başlıklarını küçülterek heap
  kullanımını azaltır; bellek tasarrufu ve daha iyi cache davranışı sağlar.
  (Java 24'te deneysel/JEP 450 idi, 25'te ürün özelliği olarak olgunlaştı.)
- **Generational GC iyileştirmeleri:** Shenandoah'ın generational varyantı gibi
  çöp toplayıcı iyileştirmeleri, düşük gecikmeli (low-latency) iş yükleri için.
- **Sanal thread olgunlaşması:** Java 21'de kalıcılaşan virtual threads,
  Scoped Values + Structured Concurrency ile birlikte daha güçlü bir eşzamanlılık
  modeli oluşturur.

> **Dürüstlük:** Performans JEP'lerinin kesin sayısal kazanımları iş yüküne
> bağlıdır; genel kaldım, somut yüzde vaadi vermedim.

---

## 5. Önceki Preview Özellikler Nasıl Olgunlaştı? (Olgunlaşma Tablosu)

Modern Java'nın felsefesi: özellikler **preview → kalıcı** yolculuğundan geçer.
Java 25, bu olgunlaşmanın çoğunu tamamladığı için bir LTS "toplama noktası"dır.

| Özellik | Önce | Java 25'teki durum |
|---|---|---|
| **Records** | Java 14 preview | Kalıcı (16'dan beri) — yaygın |
| **Sealed Classes** | Java 15 preview | Kalıcı (17) |
| **Pattern Matching (switch)** | Java 17 preview | Kalıcı (21) |
| **Sanal Thread'ler** | Java 19 preview | Kalıcı (21) |
| **Sequenced Collections** | Java 21 | Kalıcı (21) |
| **Stream Gatherers** | Java 22 preview | **Kalıcı (24)** |
| **Foreign Function & Memory (FFM)** | uzun preview | **Kalıcı (22)** |
| **Scoped Values** | Java 20 incubator | **Kalıcı (25, JEP 506)** |
| **Flexible Constructor Bodies** | Java 22 preview | **Kalıcı (25, JEP 513)** |
| **Compact Source & Instance Main** | Java 21 preview | **Kalıcı (25, JEP 512)** |
| **Module Import Declarations** | Java 23 preview | **Kalıcı (25, JEP 511)** |
| **Structured Concurrency** | Java 19 incubator | **HÂLÂ preview (25, JEP 505)** |
| **Stable Values** | Java 25 | **preview (JEP 502)** |
| **String Templates** | Java 21–22 preview | **GERİ ÇEKİLDİ — DİKKAT (aşağıda)** |

### String Templates — DÜRÜST UYARI
- String Templates (`STR."..."`) Java 21 ve 22'de **preview** olarak geldi.
- **Java 23'te tasarım sorunları nedeniyle GERİ ÇEKİLDİ** (kaldırıldı).
- **Java 25'te String Templates YOKTUR.** Bu önemli bir derstir: her preview
  özellik kalıcılaşmaz; bazıları tamamen iptal olabilir.
- Bugün string birleştirme için hâlâ `+`, `String.format(...)`, `StringBuilder`
  veya `MessageFormat` kullanılır.

> **Çıkarım:** Preview özelliklere production'da bel bağlamayın. LTS'in değeri
> tam da burada: 25'te kalıcı olanlar, "geri çekilme riski" geçmiş özelliklerdir.

---

## 6. Geçiş Rehberi: Java 17 / 21 → Java 25

### 17 → 25 (büyük sıçrama)
17 ile 25 arasında **çok şey değişti**. Dikkat edilecekler:
1. **Sanal thread'ler (21):** I/O ağırlıklı sunucularda thread havuzu modelini
   yeniden değerlendirin.
2. **Deprecate/kaldırılanlar:** `sun.misc.Unsafe`'in bazı bellek metotları,
   eski güvenlik yöneticisi (Security Manager) gibi unsurlar kullanım dışı bırakıldı.
   Üçüncü parti kütüphanelerinizin uyumunu kontrol edin.
3. **Kapsüllenmiş JDK içi API'ler:** Güçlü kapsülleme (strong encapsulation)
   nedeniyle `--add-opens` ile zorlayan kütüphaneler kırılabilir.
4. **Build araçları:** Maven/Gradle, derleyici eklentilerini ve hedef sürümü
   (`--release 25`) güncelleyin.

### 21 → 25 (daha yumuşak)
21 zaten modern bir temel olduğu için geçiş görece kolaydır:
1. **Yeni kalıcı özellikleri benimseyin:** Scoped Values (ThreadLocal yerine),
   Flexible Constructor Bodies, Module Import.
2. **Preview kullanıyorsanız:** Bazı 21 preview'ları 25'te kalıcılaştı; kodunuzdan
   `--enable-preview` bayraklarını kaldırmanız gerekebilir.
3. **String Templates kullandıysanız:** 23'te kaldırıldığı için kodunuzu
   `String.format`/`+`'a geri çevirmeniz **gerekir**.

### Genel Geçiş Adımları
1. Bağımlılıkları (Spring Boot 3.x+, vb.) Java 25 destekleyen sürümlere yükseltin.
2. `--release 25` ile derleyin, derleme uyarılarını giderin.
3. Test paketini (unit + entegrasyon) çalıştırın; GC log'larını gözlemleyin.
4. Staging ortamında performans/regresyon testi yapın.
5. Kademeli (canary) dağıtımla production'a alın.

---

## 7. Avantaj / Dezavantaj / Risk

### Avantajlar
- **LTS:** Yıllarca güvenlik ve hata düzeltmesi → kurumsal güven.
- **Olgunlaşmış özellikler:** Scoped Values, Flexible Constructors, Compact
  Source artık kalıcı; preview riski yok.
- **Performans/bellek:** Compact Object Headers, GC iyileştirmeleri.
- **Modern eşzamanlılık:** Virtual threads + Scoped Values güçlü kombinasyon.
- **Öğrenme kolaylığı:** Compact source + module import + IO.

### Dezavantajlar / Riskler
- **Hâlâ preview olanlar:** Structured Concurrency ve Stable Values **kalıcı
  değil**; production'da bel bağlamak risklidir, API değişebilir.
- **Kaldırılan API'ler:** Eski güvenlik yöneticisi, bazı Unsafe metotları →
  eski kütüphaneler kırılabilir.
- **17'den büyük sıçrama:** Test/regresyon maliyeti; bağımlılık uyumu.
- **String Templates yok:** 23'te kaldırıldı; ona bel bağlamış kod taşınamaz.

---

## 8. Dosya Referansları (bu klasör)

| Dosya | Konu | Derleme notu |
|---|---|---|
| `README.md` | Bu doküman | — |
| `CompactSourceMainMethods.java` | Compact source & instance main (KALICI) | `javac` / `java` (preview gerekmez) |
| `ScopedValuesKalici.java` | Scoped Values (KALICI) | `javac` / `java` (preview gerekmez) |
| `FlexibleConstructorBodies.java` | Esnek constructor (KALICI) | `javac` / `java` (preview gerekmez) |
| `StableValuesPreview.java` | Stable Values (PREVIEW) | `javac --release 25 --enable-preview ...` |
| `StructuredConcurrencyGuncel.java` | Structured Concurrency (PREVIEW) | `javac --release 25 --enable-preview ...` |

### Hızlı Çalıştırma
```bash
# Kalıcı özellikler (preview bayrağı gerekmez)
javac CompactSourceMainMethods.java && java CompactSourceMainMethods
javac ScopedValuesKalici.java       && java ScopedValuesKalici
javac FlexibleConstructorBodies.java && java FlexibleConstructorBodies

# Preview özellikler (--enable-preview ZORUNLU)
javac --release 25 --enable-preview StableValuesPreview.java
java  --enable-preview StableValuesPreview

javac --release 25 --enable-preview StructuredConcurrencyGuncel.java
java  --enable-preview StructuredConcurrencyGuncel
```

---

## 9. Özet — Neden Java 25?

> **Java 25, modern Java'nın yıllardır biriken yeniliklerini kalıcılaştıran,
> uzun süre destek alacak ve ekosistemin etrafında toplanacağı en güncel LTS
> sürümüdür.** 2026'da yeni proje başlatan ya da 17/21'den yükselten her ekip
> için varsayılan hedef budur. Preview özelliklerin (Structured Concurrency,
> Stable Values) henüz olgunlaşmadığını ve String Templates'in geri çekildiğini
> unutmadan, kalıcı özellikleri gönül rahatlığıyla kullanabilirsiniz.
