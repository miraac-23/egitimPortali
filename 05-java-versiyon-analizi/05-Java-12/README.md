# Java 12 (Mart 2019) — Detaylı Sürüm Analizi

> **Çıkış Tarihi:** 19 Mart 2019
> **Sürüm Tipi:** Standart (feature) sürüm — **LTS DEĞİL**
> **Destek Modeli:** Java 12, 6 aylık sürüm döngüsünün bir parçasıdır. Java 13 (Eylül 2019) çıktığında resmî destek bitmiştir. Yani Java 12 kısa ömürlü bir "ara sürümdür"; üretimde uzun süre kalması beklenmez. Bu sürümdeki kalıcı özellikler sonraki LTS olan **Java 17**'ye taşınmıştır.

---

## İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [Java 11'den Java 12'ye Geçişte Ne Değişti?](#java-11den-java-12ye-geçişte-ne-değişti)
3. [Özellik 1: Switch Expressions (Preview)](#özellik-1-switch-expressions-preview)
4. [Özellik 2: Compact Number Formatting](#özellik-2-compact-number-formatting)
5. [Özellik 3: Teeing Collector](#özellik-3-teeing-collector)
6. [Özellik 4: Shenandoah GC (Deneysel)](#özellik-4-shenandoah-gc-deneysel)
7. [Özellik 5: JVM Constants API](#özellik-5-jvm-constants-api)
8. [Preview → Kalıcı Evrimi](#preview--kalıcı-evrimi)
9. [Avantaj / Dezavantaj / Risk Tablosu](#avantaj--dezavantaj--risk-tablosu)
10. [Dosya Listesi](#bu-klasördeki-dosyalar)

---

## Genel Bakış

Java 12, 6 aylık hızlı sürüm modelinin üçüncü adımıdır (Java 9 → 10 → 11 → **12**). Java 11 büyük bir LTS sürümüydü; Java 12 ise daha çok **geliştirici ergonomisi** (kod yazma kolaylığı) ve **çöp toplayıcı (GC) iyileştirmeleri** üzerine odaklandı.

Bu sürümün en dikkat çeken yeniliği, ilk kez **"Preview Feature" (önizleme özelliği)** kavramının uygulamaya konmasıdır. **Switch Expressions** bu mekanizmayla geldi: özellik tam olgunlaşmadan önce topluluğun denemesi ve geri bildirim vermesi sağlandı.

Java 12'de gelen başlıca yenilikler:

| # | Özellik | JEP / Tip | Durum (Java 12'de) |
|---|---------|-----------|--------------------|
| 1 | Switch Expressions | JEP 325 | **Preview** (önizleme) |
| 2 | Compact Number Formatting | JDK-8188147 | **Kalıcı** |
| 3 | Teeing Collector | JDK-8209685 | **Kalıcı** |
| 4 | Shenandoah GC | JEP 189 | **Deneysel (Experimental)** |
| 5 | JVM Constants API | JEP 334 | **Kalıcı** |
| 6 | Default CDS Archives | JEP 341 | Kalıcı (performans) |
| 7 | Abortable Mixed Collections for G1 | JEP 344 | Kalıcı (G1 iyileştirme) |
| 8 | Promptly Return Unused Committed Memory from G1 | JEP 346 | Kalıcı (G1 iyileştirme) |

Bu dokümanda istenen 5 ana özellik (1–5) detaylandırılmıştır.

---

## Java 11'den Java 12'ye Geçişte Ne Değişti?

| Konu | Java 11 | Java 12 |
|------|---------|---------|
| `switch` | Yalnızca klasik **statement** (deyim), `break` zorunlu, fall-through riski | Yeni **expression** söz dizimi (`->`, `yield`) — **preview** olarak eklendi |
| Sayı kısaltma | El ile bölme + string birleştirme | `NumberFormat.getCompactNumberInstance()` ile dile duyarlı kısaltma |
| Stream'den çoklu özet | İki ayrı stream geçişi veya özel collector | `Collectors.teeing()` ile tek geçişte iki sonuç |
| Düşük gecikmeli GC | ZGC (deneysel, JDK 11) | ZGC + **Shenandoah** (deneysel olarak eklendi) |
| Dinamik sabitler | Yok | `java.lang.constant` paketi (JVM Constants API) |
| Başlangıç hızı | CDS arşivi manuel oluşturulmalı | **Varsayılan CDS arşivi** ile daha hızlı başlangıç |

**Geçiş notu:** Java 11 → 12 geçişi çoğu uygulama için **kaynak-uyumludur** (source-compatible). Var olan kod büyük ihtimalle değişiklik gerektirmeden derlenir. Tek dikkat edilecek nokta, eğer preview özellik (Switch Expressions) kullanacaksanız `--enable-preview` bayrağının gerekmesidir.

---

## Özellik 1: Switch Expressions (Preview)

> İlgili dosya: **[`SwitchExpressionsPreview.java`](./SwitchExpressionsPreview.java)**

### NEDİR?
`switch` yapısının, bir **deyim (statement)** olmanın yanı sıra artık bir **ifade (expression)** olarak da kullanılabilmesidir; yani doğrudan bir **değer üretip döndürebilir**. Yeni söz dizimi `->` (ok) operatörünü, çoklu `case` etiketlerini (`case 1, 2, 3 ->`) ve çok satırlı bloklarda değer döndürmek için `yield` anahtar sözcüğünü getirir.

### NEDEN GELDİ? (Hangi problemi çözüyor)
Klasik `switch` üç büyük sorun barındırıyordu:
1. **Fall-through riski:** Her `case` sonunda `break` yazmayı unutmak, sessiz ve bulunması zor bir hataya yol açar.
2. **Değer üretememe:** `switch` doğrudan değer döndüremediği için, her seferinde ekstra bir değişken tanımlayıp her case içinde ona atama yapmak gerekirdi (gürültülü, tekrarlı kod).
3. **Eksik kapsama uyarısı yok:** Klasik `switch` tüm durumların ele alındığını derleme zamanında kontrol etmez.

### NE İŞE YARAR?
- `break` ihtiyacını ortadan kaldırır → fall-through hatası imkânsız hale gelir.
- `switch`'i sağ tarafa yazıp doğrudan bir değişkene atayabilirsiniz: `int x = switch(...) { ... };`
- Birden çok etiketi tek satırda gruplayabilirsiniz: `case PAZARTESI, SALI, CARSAMBA -> ...`

### NEREDE KOLAYLIK SAĞLAR?
Çok dallı eşleme (mapping) mantıklarında: enum → davranış, HTTP kodu → mesaj, gün → çalışma saati, durum makineleri (state machine). Kod daha kısa, daha okunur ve daha güvenli olur.

### ESKİ vs YENİ Kod Karşılaştırması

**ESKİ (Java 11 ve öncesi — statement):**
```java
int calismaSaati;
switch (gun) {
    case 1: case 2: case 3: case 4: case 5:
        calismaSaati = 8;
        break;            // <-- unutulursa fall-through!
    case 6:
        calismaSaati = 4;
        break;
    case 7:
        calismaSaati = 0;
        break;
    default:
        calismaSaati = -1;
}
```

**YENİ (Java 12 preview — expression):**
```java
int calismaSaati = switch (gun) {
    case 1, 2, 3, 4, 5 -> 8;   // coklu case, break yok
    case 6             -> 4;
    case 7             -> 0;
    default            -> -1;
};                              // <-- expression, sonunda ';'
```

Çok satırlı işlem gerektiğinde `yield`:
```java
String aciklama = switch (gun) {
    case 6 -> {
        int saat = 4;
        yield "Cumartesi - " + saat + " saat";   // yield ile deger dondur
    }
    default -> "Diger";
};
```

### GERÇEK HAYAT ÖRNEĞİ
Bir REST API'de gelen HTTP durum kodunu kullanıcıya gösterilecek Türkçe mesaja çevirmek (`200 → "Başarılı"`, `404 → "Bulunamadı"`, `500 → "Sunucu hatası"`). Tek `switch` ifadesiyle, ekstra değişken ve `break` olmadan temiz bir şekilde çözülür. Detaylı çalışan örnek için `SwitchExpressionsPreview.java` dosyasına bakın.

### Derleme Notu (PREVIEW)
Java 12'de bu özellik preview olduğu için:
```bash
javac --release 12 --enable-preview SwitchExpressionsPreview.java
java  --enable-preview SwitchExpressionsPreview
```
Java 14+ (örn. sisteminizdeki Java 21) ile özellik kalıcı olduğundan **hiçbir bayrak gerekmeden** derlenir.

---

## Özellik 2: Compact Number Formatting

> İlgili dosya: **[`CompactNumberFormat.java`](./CompactNumberFormat.java)**

### NEDİR?
`NumberFormat.getCompactNumberInstance(Locale, Style)` ile büyük sayıları kısa, insan-dostu biçimde gösterme yeteneğidir. `1000 → "1K"`, `1000000 → "1M"` gibi. İki stil vardır: `SHORT` ("1K") ve `LONG` ("1 thousand").

### NEDEN GELDİ? (Hangi problemi çözüyor)
Sosyal medya beğeni sayısı, takipçi, görüntülenme, dosya boyutu gibi değerleri "1.2K", "3.4M" şeklinde göstermek çok yaygındır. Önceden bunu el ile yapmak gerekiyordu ve bu yaklaşım:
- **Dile duyarlı değildi** (Türkçe'de "bin/milyon", İngilizce'de "K/M" gerekir),
- yuvarlama ve çoğullaşma kurallarını yanlış yönetiyordu,
- her projede yeniden yazılıyordu (kod tekrarı).

### NE İŞE YARAR?
Sayıyı, verdiğiniz **locale**'in (dilin) kurallarına göre otomatik olarak kısaltır ve doğru eki (K/M/B veya bin/milyon/milyar) seçer.

### NEREDE KOLAYLIK SAĞLAR?
Dashboard/istatistik ekranları, sosyal medya uygulamaları, analitik panelleri, dosya yöneticileri, uluslararası (i18n) uygulamalar.

### ESKİ vs YENİ Kod Karşılaştırması

**ESKİ:**
```java
String kisalt(long sayi) {
    if (sayi < 1_000)            return String.valueOf(sayi);
    else if (sayi < 1_000_000)   return (sayi / 1_000) + "K";
    else if (sayi < 1_000_000_000) return (sayi / 1_000_000) + "M";
    else                         return (sayi / 1_000_000_000) + "B";
}
// Sorun: locale bilmez, yuvarlama kaba, "1.234.567" -> "1M" (ondalik kaybi)
```

**YENİ:**
```java
NumberFormat nf = NumberFormat.getCompactNumberInstance(
        Locale.forLanguageTag("tr-TR"), NumberFormat.Style.SHORT);
nf.setMaximumFractionDigits(1);
nf.format(1_234_567);   // -> "1,2 Mn"  (Turkce, dile duyarli!)
```

### GERÇEK HAYAT ÖRNEĞİ
- **Sosyal medya:** `1.234.567` takipçi → "1,2 Mn" (Türkçe) veya "1.2M" (İngilizce).
- **Dosya boyutu:** `5.242.880` byte → "5.2MB".

`CompactNumberFormat.java` dosyası SHORT/LONG stillerini ve Türkçe/İngilizce locale'leri yan yana gösterir.

### Derleme Notu
Bu özellik Java 12'de **kalıcıdır** (preview değil); ekstra bayrak gerekmez.

---

## Özellik 3: Teeing Collector

> İlgili dosya: **[`TeeingCollector.java`](./TeeingCollector.java)**

### NEDİR?
`Collectors.teeing(downstream1, downstream2, merger)`: Bir stream'in elemanlarını **aynı anda iki ayrı collector'a** gönderir, her ikisi de kendi sonucunu üretir, sonra bir `merger` (BiFunction) bu iki sonucu **tek bir sonuca birleştirir**. Adı tesisattaki "T-bağlantısı"ndan (tee) gelir: tek akış ikiye ayrılır, işlenir, tekrar birleşir.

### NEDEN GELDİ? (Hangi problemi çözüyor)
Bir koleksiyondan iki farklı özet (örn. hem ortalama hem toplam) hesaplamak için önceden ya stream **iki kez** dolaşılırdı (iki kat iş yükü) ya da karmaşık özel bir collector yazılırdı. `teeing` bu işi **tek geçişte** ve okunabilir biçimde çözer.

### NE İŞE YARAR?
Tek bir veri geçişiyle iki bağımsız istatistiği hesaplayıp birleştirmenizi sağlar: ortalama + toplam, min + max, geçen sayısı + ortalama, vb.

### NEREDE KOLAYLIK SAĞLAR?
Raporlama, analitik, finansal hesaplamalar; özellikle **büyük veri setlerinde** çift geçişi tek geçişe indirerek performans kazandırır.

### ESKİ vs YENİ Kod Karşılaştırması

**ESKİ (iki geçiş):**
```java
double ortalama = urunler.stream().mapToDouble(Urun::fiyat).average().orElse(0);
double toplam   = urunler.stream().mapToDouble(Urun::fiyat).sum();   // 2. gecis!
```

**YENİ (tek geçiş, teeing):**
```java
var ozet = urunler.stream().collect(
    Collectors.teeing(
        Collectors.averagingDouble(Urun::fiyat),   // 1. collector
        Collectors.summingDouble(Urun::fiyat),     // 2. collector
        (ort, top) -> "Ortalama=" + ort + ", Toplam=" + top   // merger
    )
);
```

### GERÇEK HAYAT ÖRNEĞİ
Bir sınıfın not listesinden **tek geçişte** hem sınıf ortalamasını hem de 50 ve üzeri alan (geçen) öğrenci sayısını hesaplayıp bir rapor nesnesinde birleştirmek. `TeeingCollector.java` dosyasında ürün fiyatları, öğrenci notları ve min/max fark örnekleri vardır.

### Derleme Notu
Bu özellik Java 12'de **kalıcıdır**; ekstra bayrak gerekmez.

---

## Özellik 4: Shenandoah GC (Deneysel)

> Bu özellik için ayrı bir `.java` dosyası yoktur; çünkü GC seçimi kodla değil, **JVM bayraklarıyla** yapılır.

### NEDİR?
Shenandoah, Red Hat tarafından geliştirilen **düşük duraklama süreli (low-pause-time)** bir çöp toplayıcıdır. Java 12'de **deneysel (experimental)** olarak eklendi.

### NEDEN GELDİ? (Hangi problemi çözüyor)
Klasik GC'lerde (Parallel, G1) "stop-the-world" duraklamaları, heap (yığın) büyüdükçe uzar. 100+ GB heap'lerde bu duraklamalar saniyeler sürebilir; düşük gecikme isteyen uygulamalar (oyun sunucuları, finans, gerçek zamanlı sistemler) için kabul edilemez.

### NE İŞE YARAR?
Shenandoah, çöp toplama işinin büyük kısmını (özellikle nesnelerin taşınması/evacuation) **uygulama thread'leriyle eş zamanlı (concurrent)** olarak yapar. Böylece duraklama süresi **heap boyutundan büyük ölçüde bağımsız** ve çok kısa (genellikle birkaç milisaniye) kalır.

### NEREDE KOLAYLIK SAĞLAR?
Büyük heap'li, düşük gecikme kritik uygulamalar.

### ZGC ile Karşılaştırma
Java'da iki düşük-gecikme GC vardır: **ZGC** (Java 11) ve **Shenandoah** (Java 12). İkisi de eş zamanlı çalışır; Shenandoah daha çok OpenJDK/Red Hat ekosisteminde öne çıkar.

### Nasıl Etkinleştirilir? (Java 12'de deneysel olduğu için)
```bash
java -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC MyApp
```
> **Not:** Shenandoah, Java 15'te (JEP 379) **production-ready (kalıcı)** hale geldi ve artık `-XX:+UseShenandoahGC` tek başına yeterlidir.

### Avantaj / Dezavantaj
- **Avantaj:** Çok kısa, öngörülebilir duraklama; heap boyutundan bağımsız gecikme.
- **Dezavantaj:** Eş zamanlı çalışma CPU ve toplam throughput (iş hacmi) maliyeti getirir; throughput öncelikli batch işler için ideal değildir.

---

## Özellik 5: JVM Constants API

> Bu özellik için ayrı `.java` dosyası yoktur; çünkü doğrudan uygulama geliştiricileri değil, daha çok **derleyici, çerçeve (framework) ve bytecode araçları** geliştirenler için bir altyapı API'sidir.

### NEDİR?
`java.lang.constant` paketi (JEP 334) ile gelen, **constant pool** (sabit havuzu) girdilerini sembolik olarak temsil eden bir API'dir. Başlıca tipler: `ConstantDesc`, `ClassDesc`, `MethodTypeDesc`, `MethodHandleDesc`, `DynamicConstantDesc`.

### NEDEN GELDİ? (Hangi problemi çözüyor)
JVM'in class dosyalarındaki constant pool, sınıf, metot ve yöntem tipi referanslarını tutar. Bytecode üreten/işleyen araçlar (derleyiciler, ASM benzeri kütüphaneler, dinamik dil çalışma zamanları) bu sabitleri güvenli ve doğrulanabilir biçimde temsil etmek istiyordu. Önceden bunun standart, güvenli bir yolu yoktu; ham `Class` nesneleri yüklemeyi zorunlu kılıyor, bu da hatalara ve gereksiz sınıf yüklemelerine yol açıyordu.

### NE İŞE YARAR?
Bir sabiti (örneğin "java.lang.String sınıfı") gerçekten o sınıfı **yüklemeden**, salt sembolik (nominal) bir tanımlayıcıyla ifade etmeyi sağlar. Bu, özellikle `invokedynamic` ve `constantdynamic` (JEP 309, Java 11) gibi düşük seviye mekanizmalarla çalışan araçlar için kritiktir.

### NEREDE KOLAYLIK SAĞLAR?
- Derleyici ve bytecode kütüphaneleri (örn. javac, jlink, dinamik dil run-time'ları).
- Çoğu uygulama geliştiricisi bu API'yi **doğrudan kullanmaz**; bu daha çok bir "JDK iç altyapısı / araç geliştirici" API'sidir.

### Küçük Örnek (kavramsal)
```java
import java.lang.constant.ClassDesc;

// "java.lang.String" sinifini YUKLEMEDEN sembolik olarak tanimla:
ClassDesc stringDesc = ClassDesc.of("java.lang.String");
System.out.println(stringDesc.descriptorString());  // -> "Ljava/lang/String;"
```

---

## Preview → Kalıcı Evrimi

Java 12'nin en önemli kavramsal katkılarından biri **Preview Feature** mekanizmasıdır. Bir özellik önce "preview" olarak yayınlanır, topluluk dener ve geri bildirim verir, gerekirse söz dizimi düzeltilir, sonra "kalıcı (standard)" olur.

**Switch Expressions evrimi:**

| Java Sürümü | Tarih | Durum | Not |
|-------------|-------|-------|-----|
| **Java 12** | Mart 2019 | **Preview** (JEP 325) | İlk hali; `->`, çoklu case, blok bloklarında `break <deger>` |
| **Java 13** | Eylül 2019 | **2. Preview** (JEP 354) | `break <deger>` yerine **`yield`** anahtar sözcüğü getirildi |
| **Java 14** | Mart 2020 | **KALICI** (JEP 361) | Standart hale geldi; artık `--enable-preview` gerekmez |

> **Önemli ders:** Java 12'de `break <değer>;` ile değer döndürülüyordu; Java 13'te bu, daha okunur olan **`yield`** ile değiştirildi. Bu, preview mekanizmasının neden değerli olduğunu gösterir: söz dizimi, kalıcı olmadan önce iyileştirilebildi. Bu klasördeki `SwitchExpressionsPreview.java` dosyası, taşınabilirlik için (Java 14+ ile bayraksız derlensin diye) `yield` söz dizimini kullanır.

**Diğer preview özelliklerin de benzer yolu izlediğini** unutmayın (örn. Text Blocks: Java 13 preview → Java 15 kalıcı; Records: Java 14 preview → Java 16 kalıcı; Pattern Matching for switch: Java 17 preview → Java 21 kalıcı).

---

## Avantaj / Dezavantaj / Risk Tablosu

### Avantajlar
- **Switch Expressions:** Daha kısa, daha güvenli (fall-through yok), değer döndürebilen, derleyici kapsama kontrolü yapabilen kod.
- **Compact Number Formatting:** Dile duyarlı, standart, tekrar yazılmayan sayı kısaltma.
- **Teeing Collector:** Tek geçişte iki istatistik → performans ve okunabilirlik.
- **Shenandoah GC:** Çok düşük, öngörülebilir GC duraklamaları.
- **Daha hızlı başlangıç:** Varsayılan CDS arşivi (JEP 341) ile JVM daha hızlı açılır.

### Dezavantajlar
- **Switch Expressions Java 12'de henüz preview** — üretimde kullanmak risklidir (söz dizimi değişebilir, ki gerçekten değişti).
- **Shenandoah ve ZGC deneyseldir** ve eş zamanlı çalışma throughput maliyeti getirir.
- **JVM Constants API** sıradan uygulama geliştiricisine doğrudan fayda sağlamaz.

### Riskler
- **Preview kullanımı:** `--enable-preview` ile derlenen sınıflar yalnızca **aynı major sürümün** çalışma zamanında çalışır ve sürümler arası taşınamaz. Bu yüzden preview özellikler **production'da önerilmez**.
- **Kısa ömür:** Java 12 LTS değildir; Java 13 çıkınca desteği bitmiştir. Üretim için **Java 17 / Java 21 gibi LTS** sürümler tercih edilmelidir. Java 12'nin kalıcı özellikleri zaten bu LTS sürümlerde mevcuttur.

---

## Bu Klasördeki Dosyalar

| Dosya | Açıklama |
|-------|----------|
| `README.md` | Bu doküman |
| [`SwitchExpressionsPreview.java`](./SwitchExpressionsPreview.java) | Switch Expressions (preview): eski statement vs yeni `->`/`yield`; gün→çalışma saati ve HTTP kodu→mesaj örnekleri |
| [`TeeingCollector.java`](./TeeingCollector.java) | `Collectors.teeing`: ürün fiyatı (ortalama+toplam), öğrenci notu (ortalama+geçen sayısı), min/max fark örnekleri |
| [`CompactNumberFormat.java`](./CompactNumberFormat.java) | `getCompactNumberInstance`: SHORT/LONG stilleri, Türkçe/İngilizce locale, sosyal medya ve dosya boyutu örnekleri |

### Derleme ve Çalıştırma

```bash
# Java 14+ (örn. Java 21) ile - hicbir ek bayrak gerekmez:
javac SwitchExpressionsPreview.java TeeingCollector.java CompactNumberFormat.java
java SwitchExpressionsPreview
java TeeingCollector
java CompactNumberFormat

# Java 12 ile Switch Expressions'i derlemek isterseniz (preview):
javac --release 12 --enable-preview SwitchExpressionsPreview.java
java  --enable-preview SwitchExpressionsPreview
```

> Bu klasördeki tüm `.java` dosyaları Java 21 ile derlenip çalıştırılarak doğrulanmıştır.
