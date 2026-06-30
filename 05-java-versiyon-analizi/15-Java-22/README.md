# Java 22 (Mart 2024) — Detaylı Türkçe Analiz

## Sürüm Bilgisi

| Özellik            | Değer                                                        |
|--------------------|--------------------------------------------------------------|
| Sürüm              | Java SE 22 / JDK 22                                          |
| Çıkış tarihi       | 19 Mart 2024                                                |
| Tür                | **Feature release (non-LTS)** — kısa destekli ara sürüm     |
| Önceki LTS         | Java 21 (Eylül 2023)                                        |
| Sonraki LTS        | Java 25 (Eylül 2025)                                        |
| Toplam JEP sayısı  | 12                                                          |

> **Not — non-LTS ne demek?** Java 22, üretim ortamı için uzun vadeli destek
> (LTS) almayan bir ara sürümdür. Genellikle yeni özellikleri denemek,
> kütüphane uyumluluğunu test etmek ve bir sonraki LTS'e (Java 25) hazırlanmak
> için kullanılır. Üretimde uzun ömür isteyen ekipler LTS sürümlerde
> (17, 21, 25 ...) kalmayı tercih eder.

### Bu dokümandaki özellikler ve durumları

| JEP | Özellik                                  | Durum (Java 22)        | İlgili dosya                         |
|-----|------------------------------------------|------------------------|--------------------------------------|
| 454 | Foreign Function & Memory API            | **KALICI (stable)**    | `ForeignFunctionMemoryApi.java`      |
| 456 | Unnamed Variables & Patterns             | **KALICI (stable)**    | `UnnamedVariables.java`              |
| 461 | Stream Gatherers                         | **Preview (önizleme)** | `StreamGatherersPreview.java`        |
| 447 | Statements before super(...)             | Preview (önizleme)     | (sadece bu README'de)                |
| 458 | Launch Multi-File Source-Code Programs   | **KALICI (stable)**    | (sadece bu README'de)                |
| 423 | Region Pinning for G1                    | **KALICI (stable)**    | (sadece bu README'de)                |

---

## Genel Bakış

Java 22'nin öne çıkan başlığı, **Project Panama**'nın olgunlaşmasıyla
**Foreign Function & Memory (FFM) API'nin artık kalıcı/standart** hale
gelmesidir. Bu, JNI'nin (Java Native Interface) modern, güvenli ve performanslı
alternatifidir ve C/C++ kütüphanelerini çağırmayı köklü biçimde
kolaylaştırır.

Bunun yanında **Unnamed Variables & Patterns** (alt çizgi `_`) kalıcı hale
gelerek kullanılmayan değişkenleri ifade etmeyi standartlaştırdı.
**Stream Gatherers** ise Stream API'ye özel ara operasyon yazma yeteneği
getiren (henüz preview) güçlü bir eklemedir.

---

## 1. Foreign Function & Memory API — JEP 454 (KALICI)

### NEDİR?
`java.lang.foreign` paketi altında, **Java kodundan yerel (native) kod ve
kütüphaneleri çağırmayı** ve **JVM yığını dışındaki (off-heap) belleği güvenli
biçimde yönetmeyi** sağlayan API'dir. Project Panama'nın ürünüdür.

### NEDEN GELDİ?
Onlarca yıldır native kod çağırmanın tek "resmi" yolu **JNI** idi. JNI:
- Ayrı C/C++ ara katman (glue) kodu yazmayı zorunlu kılar,
- Bu kodu derleyip ayrı bir paylaşımlı kütüphane (.so/.dll/.dylib) üretmeyi
  gerektirir,
- Kırılgan, hata yapmaya açık (manuel referans yönetimi, çökme riski),
- Bakımı pahalı ve platforma bağımlıdır.

FFM bu adımların tamamını ortadan kaldırır: ara C kodu **YAZMADAN**, doğrudan
Java'dan native fonksiyon çağrılır.

### NE İŞE YARAR?
- İşletim sisteminin C kütüphanesi (libc) fonksiyonlarını çağırma,
- Üçüncü parti C/C++ kütüphanelerini (OpenSSL, SQLite, BLAS, CUDA sarmalayıcı
  vb.) kullanma,
- Büyük/uzun ömürlü veriyi GC baskısı olmadan off-heap bellekte tutma.

### NEREDE KOLAYLIK SAĞLAR?
- Yüksek performanslı yerel kütüphane entegrasyonları,
- Büyük veri tamponları (görüntü/ses işleme, ağ tamponları),
- Mevcut C ekosistemini Java'ya köprülemek.

### Temel Bileşenler
| Bileşen          | Görevi                                                         |
|------------------|----------------------------------------------------------------|
| `Linker`         | Java ↔ native ABI köprüsü (`Linker.nativeLinker()`)            |
| `SymbolLookup`   | Kütüphanedeki fonksiyon sembollerinin (adreslerinin) bulunması |
| `FunctionDescriptor` | Native fonksiyon imzasının (dönüş + parametre) tanımı      |
| `MethodHandle`   | Çağrılabilir handle — **downcall** (Java→native)              |
| `upcallStub`     | Java metodunu native callback'e çevirir — **upcall**          |
| `Arena`          | Bellek yaşam döngüsü yönetimi (try-with-resources ile güvenli) |
| `MemorySegment`  | Off-heap bellek bloğu (pointer'ın güvenli soyutlaması)         |
| `ValueLayout`    | Bellek yerleşimi/boyutu (`JAVA_INT`, `JAVA_LONG`, `ADDRESS`...) |

### ESKİ (JNI) vs YENİ (FFM)

**ESKİ — JNI ile `strlen` çağırmak için gereken adımlar:**
```c
/* native.c — AYRICA derlenip .so/.dll yapılması gerekir */
#include <jni.h>
#include <string.h>
JNIEXPORT jlong JNICALL Java_Demo_strlen(JNIEnv *env, jobject obj, jstring s) {
    const char *cstr = (*env)->GetStringUTFChars(env, s, NULL);
    jlong len = (jlong) strlen(cstr);
    (*env)->ReleaseStringUTFChars(env, s, cstr);  // unutulursa memory leak!
    return len;
}
```
```java
// Java tarafı
public class Demo {
    static { System.loadLibrary("native"); }   // .so/.dll yüklenmeli
    native long strlen(String s);
}
```
> + `javac`, `javah`/`-h`, `gcc -shared`, doğru `java.library.path` derdi...

**YENİ — FFM ile (sadece Java, ara C kodu YOK):**
```java
Linker linker = Linker.nativeLinker();
MethodHandle strlen = linker.downcallHandle(
        linker.defaultLookup().find("strlen").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

try (Arena arena = Arena.ofConfined()) {
    MemorySegment cString = arena.allocateUtf8String("Merhaba");
    long len = (long) strlen.invoke(cString);   // bellek otomatik serbest kalır
}
```

### FFM'nin JNI'ye Göre Avantajları
- **Güvenli bellek erişimi:** `MemorySegment` sınır kontrolü yapar; rastgele
  pointer aritmetiği yerine denetimli erişim.
- **Yaşam döngüsü yönetimi:** `Arena` (özellikle `ofConfined`) try-with-resources
  ile kapanınca tüm bellek **deterministik ve güvenli** serbest bırakılır —
  use-after-free ve leak riski azalır.
- **Ara C kodu yok:** Derleme/bağlama zinciri ortadan kalkar.
- **İki yönlü:** downcall (Java→native) ve upcall (native→Java callback) destekli.

### GERÇEK HAYAT ÖRNEĞİ
Bir görüntü işleme uygulaması, hızlı bir C kütüphanesindeki (ör. libjpeg/libpng)
çözme fonksiyonunu çağırıp sonucu büyük bir off-heap `MemorySegment`'te tutar;
böylece GC'yi büyük byte dizileriyle yormaz. `ForeignFunctionMemoryApi.java`
dosyasında bunun çekirdeği gösterilmiştir: libc'den `strlen`, `abs` (downcall)
ve `qsort` (Java karşılaştırıcıyı **upcall** olarak vererek) çağrılır.

➡️ İlgili dosya: **`ForeignFunctionMemoryApi.java`**
Derleme: `javac --release 22 ForeignFunctionMemoryApi.java`
Çalıştırma: `java --enable-native-access=ALL-UNNAMED ForeignFunctionMemoryApi`

### Geçişte Ne Değişti / Avantaj / Dezavantaj / Risk
- **Değişti:** Java 19–21'de preview olan API artık Java 22'de `--enable-preview`
  GEREKTİRMEZ. Paket/sınıf adları stabil; ileri uyumlu kullanılabilir.
- **Avantaj:** Daha az kod, daha güvenli bellek, daha kolay bakım, daha iyi
  performans potansiyeli.
- **Dezavantaj/Risk:** Native çağrı doğası gereği hâlâ "güvensiz" bir sınırdır;
  yanlış imza/yerleşim JVM çökmesine yol açabilir. Çalışma zamanında
  `--enable-native-access` ile izin gerekir (aksi halde uyarı). Platform/ABI
  farklılıkları taşınabilirliği etkileyebilir.

---

## 2. Unnamed Variables & Patterns — JEP 456 (KALICI)

### NEDİR?
Tek alt çizgi `_` ile **isimsiz değişken** ve **isimsiz pattern** bildirme
yeteneği. "Bunu bilerek kullanmıyorum" niyetini sözdiziminde açıkça ifade eder.

### NEDEN GELDİ?
Çoğu zaman bir değişkeni **bildirmek zorunda kalır ama kullanmayız**: catch'te
yakalanan exception, lambda'nın ikinci parametresi, sadece sayım için dönen
döngü değişkeni, record'un ilgilenmediğimiz bileşeni... Bunlara `unused`,
`ignored` gibi sahte isimler vermek hem gürültü yaratır hem de derleyici/IDE
"kullanılmıyor" uyarısı doğurur.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR?
Niyeti netleştirir, gürültüyü ve yanlış uyarıları azaltır. Kullanım yerleri:
- Yerel değişken atamaları, `for` döngüleri,
- `catch (Exception _)`,
- `try (var _ = ...)`,
- Lambda parametreleri `(k, _) -> ...`,
- Pattern matching'de **unnamed pattern**: `case Point(int x, _)`.

> Aynı kapsamda **birden fazla `_`** kullanılabilir; çünkü `_` bir isim değildir
> ve ona erişilemez — bu yüzden çakışmaz.

### ESKİ vs YENİ
```java
// ESKİ
try { ... }
catch (NumberFormatException e) { /* e kullanılmıyor ama bildirmek zorunda */ }

map.forEach((key, value) -> kullan(key));   // value gereksiz isim, uyarı verir

if (obj instanceof Point(int x, int y)) { kullan(x); }  // y boşuna

// YENİ (Java 22)
try { ... }
catch (NumberFormatException _) { }              // niyet açık

map.forEach((key, _) -> kullan(key));            // value yok, uyarı yok

if (obj instanceof Point(int x, _)) { kullan(x); }  // y atlandı
```

### GERÇEK HAYAT ÖRNEĞİ
Bir konfigürasyon ayrıştırıcıda, sözlüğün sadece anahtarlarıyla ilgilenip
değerleri yok saymak, ya da bir koordinat record'unun yalnızca bir bileşenini
çekmek. `UnnamedVariables.java` 7 farklı kullanım senaryosunu gösterir.

➡️ İlgili dosya: **`UnnamedVariables.java`**
Derleme: `javac --release 22 UnnamedVariables.java` (preview gerekmez)

### Geçişte Ne Değişti / Avantaj / Dezavantaj / Risk
- **Değişti:** Java 21'de preview olan özellik Java 22'de kalıcı.
- **Avantaj:** Daha temiz, niyeti açık kod; daha az uyarı.
- **Risk:** Çok eski kodda `_` bir **değişken adı** olarak kullanılmış olabilir;
  Java 9'dan beri `_`'ın yalnız başına identifier olması zaten yasaklanmıştı, bu
  yüzden pratikte çakışma çok düşüktür.

---

## 3. Stream Gatherers — JEP 461 (PREVIEW)

### NEDİR?
`Stream.gather(Gatherer)` ile **özel ara (intermediate) stream operasyonları**
yazma yeteneği. Terminal taraftaki `Collector`'ın ara-operasyon karşılığı gibidir.

### NEDEN GELDİ?
Stream API'nin ara operasyon kümesi sabitti (`map`, `filter`, `flatMap`,
`limit`...). Kayan pencere, ardışık tekrarları eleme, koşullu erken durdurma
gibi ihtiyaçlar için bunları zincirlemek zor veya imkânsızdı. Gatherer,
durum (state) tutabilen, eleman yayabilen, hatta akışı erken sonlandırabilen
**genişletilebilir** bir mekanizma sunar.

### NE İŞE YARAR — Hazır gatherer'lar
- `windowFixed(n)` — ardışık n'li bloklar (batch),
- `windowSliding(n)` — n boyutlu kayan pencere (moving average vb.),
- `fold(init, fn)` — tek sonuca katlama,
- `scan(init, fn)` — kümülatif/running ara sonuçlar.

### Gatherer'ın 4 bileşeni
`initializer` (state kur) · `integrator` (her elemanı işle, downstream'e yay) ·
`combiner` (paralel state birleştirme) · `finisher` (akış sonunda kalanı yay).

### ESKİ vs YENİ — kayan pencere
```java
// ESKİ: elle indeksleme, hata yapmaya açık döngü
for (int i = 0; i + 3 <= list.size(); i++) {
    var pencere = list.subList(i, i + 3);
    ...
}

// YENİ (Java 22, preview):
stream.gather(Gatherers.windowSliding(3)).map(...)...
```

### GERÇEK HAYAT ÖRNEĞİ
Finans/IoT verisinde **hareketli ortalama**, ölçümleri **batch'lere bölme**,
**kümülatif toplam**, veya log akışında **ardışık yinelenen** satırları
sıkıştırma. `StreamGatherersPreview.java` hem hazır gatherer'ları hem de iki
**özel** gatherer (ardışık tekrarsız + eşik aşılınca erken durdurma) içerir.

➡️ İlgili dosya: **`StreamGatherersPreview.java`**
Derleme: `javac --release 22 --enable-preview StreamGatherersPreview.java`
Çalıştırma: `java --enable-preview StreamGatherersPreview`

### Geçişte Ne Değişti / Avantaj / Dezavantaj / Risk
- **Durum:** Java 22'de **preview** (Java 23'te 2. preview, Java 24'te kalıcı).
- **Avantaj:** Stream'i genişletilebilir kılar; tekrar kullanılabilir özel
  operasyonlar.
- **Dezavantaj/Risk:** Preview olduğu için API ileride değişebilir; üretimde
  dikkatli kullanılmalı. `--enable-preview` ile derlenen sınıflar yalnızca aynı
  sürümün JVM'inde ve yine `--enable-preview` ile çalışır.

---

## 4. Statements before super(...) — JEP 447 (PREVIEW)

### NEDİR? (kısa)
Bir kurucu (constructor) içinde, `super(...)` veya `this(...)` çağrısından
**önce** çalışan ifadelere (argümanları doğrulama, hazırlama, dönüştürme) izin
verir — yan etki `this`'e dokunmadığı sürece.

### NEDEN / NE İŞE YARAR
Eskiden `super(...)` kurucunun ilk ifadesi olmak zorundaydı. Argüman doğrulaması
gibi mantıkları ya yardımcı `static` metoda taşımak ya da iç içe ifadelere
sıkıştırmak gerekiyordu.

```java
// ESKİ: doğrulama, super argümanının içine gömülür
Foo(int x) { super(dogrula(x)); }   // ayrı static metot gerekir

// YENİ (preview): super'den önce ifade
Foo(int x) {
    if (x < 0) throw new IllegalArgumentException("x negatif olamaz");
    super(x);
}
```
> Durum: Java 22'de preview. Derleme `--enable-preview` gerektirir.

---

## 5. Launch Multi-File Source-Code Programs — JEP 458 (KALICI)

### NEDİR? (kısa)
`java` başlatıcısının (launcher) "kaynak-dosya modu" artık **birden fazla
.java dosyasına** yayılan programları, önceden `javac` ile derlemeye gerek
kalmadan çalıştırabilir.

```bash
# Tek komut: birinci dosya ana sınıf; bağımlı .java dosyaları otomatik derlenir
java Main.java
```
> Hızlı prototipleme, öğretim ve küçük araçlar için derle-çalıştır adımını
> kaldırır. Java 11'deki tek-dosya kaynak modunun çok-dosyaya genişlemişidir.

---

## 6. Region Pinning for G1 — JEP 423 (KALICI)

### NEDİR? (kısa)
G1 çöp toplayıcısında, JNI kritik bölgeleri (ör. `GetPrimitiveArrayCritical`)
sırasında **GC'nin tamamen durmasını engelleyen** bölge "pinleme" (sabitleme)
iyileştirmesi.

### NE İŞE YARAR
Eskiden bir thread native kod için bir diziyi "kritik" olarak kilitlediğinde G1,
çakışmamak için GC'yi geciktirebiliyordu (gecikme/latency artışı). Region
pinning ile yalnızca ilgili bölgeler sabitlenir; GC çalışmaya devam edebilir.
> **Avantaj:** JNI ağırlıklı uygulamalarda daha düşük GC duraklaması ve
> daha az verim kaybı. Kod değişikliği gerektirmez — JVM içi iyileştirmedir.

---

## Derleme/Çalıştırma Özeti

| Dosya                              | Derleme                                                    | Çalıştırma                                              |
|------------------------------------|------------------------------------------------------------|---------------------------------------------------------|
| `ForeignFunctionMemoryApi.java`    | `javac --release 22 ForeignFunctionMemoryApi.java`         | `java --enable-native-access=ALL-UNNAMED ForeignFunctionMemoryApi` |
| `UnnamedVariables.java`            | `javac --release 22 UnnamedVariables.java`                 | `java UnnamedVariables`                                 |
| `StreamGatherersPreview.java`      | `javac --release 22 --enable-preview StreamGatherersPreview.java` | `java --enable-preview StreamGatherersPreview`    |

> Önizleme (preview) özellikler için derleme ve çalıştırmada `--enable-preview`
> şarttır. Kalıcı özellikler (FFM, Unnamed) için gerekmez.

---

## Özet

Java 22, **FFM API'yi kalıcı yaparak** native entegrasyonu JNI'siz, güvenli ve
modern bir hale getirdi; **Unnamed Variables**'ı standartlaştırarak kod
netliğini artırdı; **Stream Gatherers** ile Stream API'yi genişletilebilir
kıldı (preview). Bir non-LTS sürüm olarak, asıl hedef bu yenilikleri Java 25
LTS'ten önce olgunlaştırmaktı.
