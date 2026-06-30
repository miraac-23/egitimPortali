# Java 24 — Detaylı Sürüm Analizi (Türkçe)

> Bu doküman, Java 24 ile gelen önemli özellikleri "NEDİR / NEDEN GELDİ / NE İŞE YARAR /
> NEREDE KOLAYLIK SAĞLAR / ESKİ vs YENİ / GERÇEK HAYAT ÖRNEĞİ" başlıkları altında
> Türkçe olarak açıklar. Eğitim ve referans amaçlıdır.

---

## Sürüm Bilgisi

| Özellik | Değer |
|--------|-------|
| Sürüm | Java 24 (JDK 24) |
| Çıkış Tarihi | Mart 2025 |
| Destek Tipi | **Non-LTS** (özellik sürümü) — bir sonraki LTS Java 25'tir (Eylül 2025) |
| Geliştirme süreci | OpenJDK, 6 aylık sürüm kadansı |
| JEP sayısı | Bu sürümde çok sayıda JEP onaylandı (24'ün üzerinde) |

> **Not (LTS uyarısı):** Java 24 non-LTS bir sürümdür. Üretim ortamlarında uzun vadeli
> destek isteyen ekipler genellikle LTS sürümleri (Java 17, 21 ve gelecekte 25) tercih eder.
> Java 24, yeni özellikleri erken denemek ve LTS'ye geçişe hazırlanmak için idealdir.

---

## Genel Bakış — Java 24'ün Ana Temaları

Java 24, birkaç büyük olgunlaşma hattını bir arada taşır:

1. **Olgunlaşan modern API'ler:** Stream Gatherers artık **kalıcı/standart** (preview değil).
2. **Performans ve başlatma hızı:** Project Leyden'ın ilk somut meyvesi olan
   Ahead-of-Time (AOT) Class Loading & Linking; G1 için Late Barrier Expansion;
   deneysel Generational Shenandoah.
3. **Kriptografi modernizasyonu:** Kuantum-dirençli algoritmalar standart kütüphaneye
   girdi — ML-KEM (anahtar kapsülleme) ve ML-DSA (dijital imza).
4. **Dil ergonomisi:** Flexible Constructor Bodies preview olarak olgunlaşmaya devam ediyor.
5. **Project Loom & FFM olgunluğu:** Sanal iş parçacıkları (virtual threads) ve
   Foreign Function & Memory API gibi daha önce kalıcılaşmış özelliklerin üzerine inşa.

Aşağıda her özellik tek tek ele alınıyor.

---

## 1. Stream Gatherers (JEP 485) — ARTIK KALICI / STANDART

> İlgili örnek dosya: [`StreamGatherersKalici.java`](./StreamGatherersKalici.java)

### NEDİR
Stream Gatherers, `java.util.stream.Stream` arayüzüne eklenen `gather(Gatherer)` ara
işlemidir (intermediate operation). `Collector`'ın terminal (sonlandırıcı) işlemler için
yaptığını, Gatherer **ara işlemler** için yapar: özel, yeniden kullanılabilir, durum
tutabilen (stateful) akış dönüşümleri yazmamıza izin verir.

`Gatherer` API'si dört bileşenden oluşur:
- **initializer** — durum (state) nesnesi oluşturur (opsiyonel),
- **integrator** — her elemanı işler, downstream'e iter, kısa devre (short-circuit) yapabilir,
- **combiner** — paralel akışlarda durumları birleştirir (opsiyonel),
- **finisher** — akış bittiğinde kalan elemanları iter (opsiyonel).

### NEDEN GELDİ
Standart kütüphanedeki ara işlemler (`map`, `filter`, `limit`, `takeWhile`...) sınırlıdır.
"Pencereleme (windowing)", "kayan birikim (scan)", "ardışık tekilleştirme", "N'erli gruplama"
gibi yaygın ihtiyaçlar için ya stream'i bırakıp döngü yazmak ya da karmaşık `Collector`
hileleri yapmak gerekiyordu. Gatherers bu boşluğu doldurur.

### NE İŞE YARAR
- Akışı **sabit boyutlu pencerelere** bölme (`Gatherers.windowFixed(n)`).
- **Kayan pencere** (`Gatherers.windowSliding(n)`).
- **Birikimli/kümülatif hesap** (`Gatherers.scan(...)`) — örn. yürüyen toplam, koşan bakiye.
- **Eşzamanlı/asenkron eşleme** (`Gatherers.mapConcurrent(...)`) — sanal iş parçacıklarıyla
  paralel I/O.
- **Tamamen özel** durum tutan dönüşümler (`Gatherer.ofSequential(...)` ile).

### NEREDE KOLAYLIK SAĞLAR
Veri akışı işleme (batch'leme), gerçek zamanlı telemetri/IoT verisi gruplama, finansal
hesaplamalar (koşan bakiye), log işleme, ETL pipeline'ları. Akış zincirini bozmadan,
okunabilir ve birleştirilebilir kalmasını sağlar.

### ESKİ vs YENİ
**Eski (Java 23 ve öncesi — preview veya manuel döngü):**
```java
// N'erli batch'leme için elle döngü
List<List<Order>> batches = new ArrayList<>();
List<Order> current = new ArrayList<>();
for (Order o : orders) {
    current.add(o);
    if (current.size() == 100) { batches.add(current); current = new ArrayList<>(); }
}
if (!current.isEmpty()) batches.add(current);
```
**Yeni (Java 24 — kalıcı, preview bayrağı YOK):**
```java
List<List<Order>> batches = orders.stream()
        .gather(Gatherers.windowFixed(100))
        .toList();
```

### GERÇEK HAYAT ÖRNEĞİ
Bir ödeme sisteminde gelen işlemleri 100'erli paketler halinde toplu API'ye gönderme
(`windowFixed`), bir hesabın işlem geçmişinden koşan bakiye üretme (`scan`), sensör
verisinden hareketli ortalama hesaplama (`windowSliding`). Detaylar
[`StreamGatherersKalici.java`](./StreamGatherersKalici.java) içindedir.

### ÖNEMLİ: Preview bayrağı artık gerekmiyor
Java 22 ve 23'te Gatherers preview idi ve `--enable-preview` ile derlenip çalıştırılması
gerekiyordu. **Java 24'te kalıcılaştı:** standart derleme/çalıştırma yeterli.
```bash
javac StreamGatherersKalici.java        # --enable-preview YOK
java  StreamGatherersKalici
```

---

## 2. Class-File API (JEP 484) — STANDART

> İlgili not dosyası: [`ClassFileApiNotlari.md`](./ClassFileApiNotlari.md)

### NEDİR
`.class` dosyalarını (Java bytecode) **okumak, yazmak ve dönüştürmek** için JDK'nın
standart, resmi API'sidir (`java.lang.classfile` paketi). JEP 484 ile kalıcılaştı.

### NEDEN GELDİ
JDK'nın kendi içinde, bytecode üretmek/işlemek için **gömülü (shaded) bir ASM kopyası**
taşınıyordu. Bu kopya, yeni class dosyası formatı sürümlerinin gerisinde kalabiliyor ve
bakım yükü yaratıyordu. Standart bir API, JDK'nın iç ASM bağımlılığını ortadan kaldırır
ve class dosyası formatıyla **her zaman senkron** kalır.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
- Bytecode üretimi (framework'ler, ORM proxy'leri, derleyiciler).
- Java agent'ları ve enstrümantasyon (instrumentation) araçları.
- Statik analiz, bytecode dönüşümü/optimizasyonu.
- Dış ASM/Javassist gibi kütüphane bağımlılığı olmadan, JDK ile gelen API'yi kullanabilme.

### ESKİ vs YENİ
- **Eski:** ASM, Javassist, BCEL gibi 3. parti kütüphaneler veya yansıma (reflection).
- **Yeni:** JDK'nın yerleşik, değişmez (immutable) ağaç tabanlı modeli; format ile
  her zaman uyumlu. Detaylar [`ClassFileApiNotlari.md`](./ClassFileApiNotlari.md).

---

## 3. Ahead-of-Time (AOT) Class Loading & Linking (JEP 483) — Project Leyden Başlangıcı

### NEDİR
Uygulamanın daha önceki bir çalıştırmasında **gözlemlenen sınıfların yüklenmiş ve
bağlanmış (linked) halini** bir önbelleğe (AOT cache) kaydedip, sonraki çalıştırmalarda
bu hazır durumu yükleyerek başlatmayı hızlandıran mekanizmadır. **Project Leyden**'ın
ilk üretim-kalitesindeki adımıdır.

### NEDEN GELDİ
JVM başlangıcının önemli bir kısmı, sınıfların okunması, doğrulanması (verification) ve
bağlanmasıyla geçer. Özellikle büyük framework'lerde (Spring vb.) bu maliyet yüksektir.
Mikroservis ve serverless dünyasında **soğuk başlatma (cold start)** süresi kritiktir.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
- Başlatma süresini kısaltır (özellikle büyük uygulamalarda kayda değer iyileşme).
- Kubernetes/serverless ortamlarında daha hızlı ölçeklenme.

### ESKİ vs YENİ (kavramsal)
- **Eski:** Her başlatmada sınıflar sıfırdan yüklenir/doğrulanır/bağlanır
  (CDS/AppCDS kısmi yardım sağlıyordu).
- **Yeni:** Eğitim (training) çalıştırmasıyla bir AOT önbelleği üretilir; sonraki
  çalıştırmalar bu önbellekten faydalanır.

> **Dürüst not:** AOT cache üretme/kullanma komut satırı bayraklarının tam söz dizimi
> JDK sürümleri arasında ayarlanıyor olabilir; üretimde kullanmadan önce kurulu JDK'nın
> resmi belgelerine bakılması önerilir.

---

## 4. Generational Shenandoah (Deneysel)

### NEDİR
Düşük gecikmeli (low-pause) Shenandoah çöp toplayıcısının (GC) **kuşak ayrımlı
(generational)** sürümüdür. Nesneleri yaşına göre genç/yaşlı kuşaklara ayırır.

### NEDEN GELDİ
"Çoğu nesne genç ölür" (weak generational hypothesis) varsayımı, kuşak ayrımı yapan
toplayıcıların genellikle daha verimli olmasını sağlar. Shenandoah'a bu yetenek
kazandırılarak hem düşük gecikme hem daha iyi verimlilik (throughput) hedeflenir.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
Tahmin edilebilir, kısa GC duraklamaları isteyen gecikmeye duyarlı sistemlerde
(finans, oyun sunucuları, gerçek zamanlı hizmetler) bellek yönetimini iyileştirir.

> **Durum:** Bu sürümde **deneyseldir**; üretimde dikkatli değerlendirilmelidir.
> Etkinleştirme için ilgili `-XX` deneysel bayrakları gerekir.

---

## 5. Quantum-Resistant Kriptografi: ML-KEM (JEP 496) & ML-DSA (JEP 497)

> İlgili not dosyası: [`KuantumDirencliKripto.md`](./KuantumDirencliKripto.md)

### NEDİR
- **ML-KEM (JEP 496):** Module-Lattice tabanlı **Anahtar Kapsülleme Mekanizması**
  (Key Encapsulation Mechanism). NIST'in FIPS 203 standardı (CRYSTALS-Kyber tabanlı).
- **ML-DSA (JEP 497):** Module-Lattice tabanlı **Dijital İmza Algoritması**.
  NIST'in FIPS 204 standardı (CRYSTALS-Dilithium tabanlı).

### NEDEN GELDİ
Yeterince güçlü bir kuantum bilgisayar, Shor algoritması ile RSA ve ECC gibi klasik
açık anahtarlı şemaları kırabilir. **"Harvest now, decrypt later"** (şimdi topla, sonra
çöz) tehdidi nedeniyle bugün şifrelenen veriler gelecekte risk altındadır. Bu yüzden
kuantum-dirençli (post-quantum) algoritmaların **bugünden** standart kütüphaneye girmesi
gerekiyordu.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
TLS, VPN, güvenli mesajlaşma, yazılım imzalama, uzun ömürlü gizli veriler. Detaylar ve
kavramsal API örnekleri [`KuantumDirencliKripto.md`](./KuantumDirencliKripto.md) içinde.

### ESKİ vs YENİ
- **Eski:** RSA / ECC / ECDSA — kuantum bilgisayara karşı kırılgan.
- **Yeni:** ML-KEM ve ML-DSA — kafes (lattice) problemlerinin zorluğuna dayanır,
  kuantuma dirençlidir.

---

## 6. Flexible Constructor Bodies (JEP 492 — Üçüncü Preview)

### NEDİR
Bir kurucu metotta (constructor), `super(...)` veya `this(...)` **çağrısından ÖNCE**
ifade çalıştırmaya (örn. argüman doğrulama, alanların erken ilklendirilmesi) izin verir.

### NEDEN GELDİ
Klasik Java kuralında `super(...)`/`this(...)` çağrısı kurucunun **ilk** ifadesi olmak
zorundaydı. Bu, argümanları üst sınıfa göndermeden önce doğrulamayı veya hazırlamayı
zorlaştırıyordu (çoğunlukla yardımcı statik metotlarla dolanılıyordu).

### ESKİ vs YENİ
```java
// ESKİ: doğrulama için statik yardımcı metot hilesi
public Hesap(BigDecimal bakiye) {
    super(dogrula(bakiye));
}
private static BigDecimal dogrula(BigDecimal b) { /* ... */ return b; }

// YENİ (preview): super'den önce doğrulama
public Hesap(BigDecimal bakiye) {
    if (bakiye.signum() < 0) throw new IllegalArgumentException("Negatif bakiye olamaz");
    super(bakiye);
}
```

> **Durum:** Bu sürümde hâlâ **preview**'dır (`--enable-preview` gerekir). Kalıcılaşma
> yolundadır ancak henüz standart değildir.

---

## 7. Late Barrier Expansion for G1 (JEP 475)

### NEDİR
G1 çöp toplayıcısının kullandığı **GC bariyerlerinin** (write barrier kodu) JIT derleme
hattında **daha geç** bir aşamada üretilmesini sağlayan bir iç (internal) iyileştirmedir.

### NEDEN GELDİ
Bariyerler derleme hattının erken aşamalarında genişletilince, C2 JIT derleyicisinin
işi artıyor ve derleme süresi/karmaşıklığı yükseliyordu. Bariyerleri geç genişletmek
derleyici yükünü azaltır.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
Geliştiriciler için **şeffaftır** — kod değişikliği gerektirmez. JIT derleme süresini
ve C2'nin bakım karmaşıklığını azaltır; G1 kullanan tüm uygulamalar dolaylı fayda görür.

---

## Dosya Referansları

| Dosya | İçerik |
|------|--------|
| [`README.md`](./README.md) | Bu genel bakış dokümanı |
| [`StreamGatherersKalici.java`](./StreamGatherersKalici.java) | Çalışan, derlenebilir Gatherers örnekleri |
| [`ClassFileApiNotlari.md`](./ClassFileApiNotlari.md) | Class-File API notları ve ASM karşılaştırması |
| [`KuantumDirencliKripto.md`](./KuantumDirencliKripto.md) | ML-KEM / ML-DSA notları, PQC karşılaştırması |

---

## Avantajlar / Dezavantajlar / Riskler

### Avantajlar
- **Stream Gatherers kalıcı:** Preview bayrağı olmadan, üretimde güvenle kullanılabilir.
- **Class-File API standart:** Dış ASM bağımlılığı olmadan bytecode işleme.
- **Kuantum-dirençli kripto standart kütüphanede:** PQC'ye geçiş için sağlam temel.
- **Başlatma hızı (AOT):** Soğuk başlatma maliyetini düşürür.
- **Şeffaf performans iyileştirmeleri:** Late Barrier Expansion gibi değişiklikler kod
  değişikliği gerektirmez.

### Dezavantajlar / Riskler
- **Non-LTS:** Uzun vadeli destek yok; üretim için LTS (17/21/25) genelde daha güvenli.
- **Deneysel özellikler:** Generational Shenandoah deneyseldir; davranışı değişebilir.
- **Preview özellikler:** Flexible Constructor Bodies hâlâ preview; API değişebilir,
  `--enable-preview` gerekir, üretimde tavsiye edilmez.
- **PQC olgunluk:** Post-quantum algoritmalar yeni standartlaştı; ekosistem (kütüphane,
  donanım, birlikte çalışabilirlik) hâlâ olgunlaşıyor. Genellikle **hibrit** (klasik+PQC)
  yaklaşım önerilir.
- **AOT cache bakımı:** Eğitim çalıştırması ve önbellek yönetimi ek operasyonel adım getirir.

---

> Bu doküman eğitim amaçlıdır. JEP detaylarının ve komut satırı bayraklarının kesin
> hâli için kurulu JDK 24'ün resmi OpenJDK belgeleri esas alınmalıdır.
