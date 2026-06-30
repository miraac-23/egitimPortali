# Java 14 (Mart 2020) — Detaylı Sürüm Analizi

> Bu doküman Java 14 sürümüyle gelen önemli dil ve platform özelliklerini Türkçe olarak,
> "nedir / neden geldi / ne işe yarar / nerede kolaylık sağlar / eski vs yeni / gerçek hayat"
> başlıklarıyla derinlemesine anlatır. Her özellik için çalışan `.java` örnek dosyalarına referans verilmiştir.

---

## Sürüm Bilgisi

| Bilgi | Değer |
|------|-------|
| Sürüm | Java SE 14 / JDK 14 |
| Çıkış Tarihi | **17 Mart 2020** |
| Tür | Feature Release (6 aylık döngü) |
| LTS mi? | **Hayır** (LTS değil; bir sonraki LTS 17'dir) |
| Önceki sürüm | Java 13 (Eylül 2019) |
| Sonraki sürüm | Java 15 (Eylül 2020) |

### Java 14'ün Tarihsel Önemi

Java 14, dil seviyesinde "modern Java"nın temellerinin atıldığı sürümlerden biridir. Üç büyük olay aynı anda gerçekleşti:

1. **Switch Expressions KALICI oldu** — 12 ve 13'te preview olan özellik artık standart dilin parçası.
2. **Records ilk kez preview olarak geldi** — boilerplate'i yok eden veri taşıyıcı sınıflar.
3. **Pattern Matching for `instanceof` ilk kez preview olarak geldi** — `instanceof` + cast tek adımda.

Ayrıca **Helpful NullPointerExceptions** özelliği geldi ve debugging'i kökten değiştirdi.

---

## Preview → Kalıcı Evrimi (ÇOK ÖNEMLİ)

Java, yeni dil özelliklerini önce "preview" (önizleme) olarak sunar; topluluk geri bildirimiyle olgunlaştırıp sonraki sürümlerde **kalıcı** (standart) hale getirir. Java 14'teki üç dil özelliğinin yaşam döngüsü şöyledir:

| Özellik | Preview Başlangıcı | KALICI Olduğu Sürüm | Java 14'teki Durumu |
|---------|--------------------|--------------------|---------------------|
| **Switch Expressions** | Java 12 (preview), Java 13 (2. preview) | **Java 14** | **KALICI** — `--enable-preview` artık GEREKMEZ |
| **Records** | **Java 14 (1. preview)** | Java 16 | Preview — `--enable-preview --release 14` gerekir |
| **Pattern Matching for instanceof** | **Java 14 (1. preview)** | Java 16 | Preview — `--enable-preview --release 14` gerekir |
| **Text Blocks** (bağlam) | Java 13 (preview) | Java 15 | 2. preview (14'te hâlâ preview) |

> **Preview özelliği nasıl derlenir/çalıştırılır?**
> ```bash
> javac --enable-preview --release 14 RecordsPreview.java
> java  --enable-preview RecordsPreview
> ```
> Bu repodaki örnekler Java 16+ (Records ve pattern matching kalıcı olduktan sonra) ile
> `--enable-preview` OLMADAN da derlenir. Yorumlarda her dosyada Java 14 preview durumu net belirtilmiştir.

---

## Kapsanan Özellikler ve Örnek Dosyalar

| # | Özellik | Durum | Örnek Dosya |
|---|---------|-------|-------------|
| 1 | Switch Expressions | KALICI | [`SwitchExpressionsKalici.java`](./SwitchExpressionsKalici.java) |
| 2 | Records | Preview | [`RecordsPreview.java`](./RecordsPreview.java) |
| 3 | Pattern Matching for instanceof | Preview | [`PatternMatchingInstanceof.java`](./PatternMatchingInstanceof.java) |
| 4 | Helpful NullPointerExceptions | Standart (varsayılan açık) | [`HelpfulNullPointer.java`](./HelpfulNullPointer.java) |
| 5 | Foreign-Memory Access API | Incubator | (yalnızca bu README'de açıklanmıştır) |

---

# 1) Switch Expressions (JEP 361) — Artık KALICI

### NEDİR
Switch'in bir **ifade (expression)** olarak değer döndürebilmesidir. Klasik `switch` bir
"deyim" (statement) idi; akış kontrolü yapardı ama doğrudan bir değer üretmezdi. Switch
Expressions ile `switch` artık bir değer döndürür ve onu bir değişkene atayabilirsiniz.

### NEDEN GELDİ (Hangi problemi çözüyor)
Klasik switch üç büyük dert çıkarıyordu:
1. **`break` unutma hatası (fall-through):** Her `case` sonunda `break` yazmazsanız bir
   sonraki case'e "düşer" (fall-through) ve sessizce yanlış davranış oluşur — en sık görülen
   hatalardan biriydi.
2. **Değer döndürememe:** Bir değişkene değer atamak için switch'ten ÖNCE değişkeni tanımlayıp
   her case içinde elle set etmek gerekiyordu.
3. **Sözdizimi gürültüsü:** Çok satırlı, tekrarlı `case ...: ...; break;` blokları.

### NE İŞE YARAR
- `->` (ok) söz dizimiyle fall-through tamamen ortadan kalkar (otomatik `break`).
- `switch` doğrudan bir değer döndürür; sonucu değişkene atayabilirsiniz.
- Çoklu case tek satırda virgülle: `case PAZARTESI, SALI, CARSAMBA -> ...`.
- Çok satırlı blok gerektiğinde `yield` ile değer döndürülür.

### NEREDE KOLAYLIK SAĞLAR
Enum'lara göre dallanan iş kuralları, durum makineleri (state machine), HTTP durum kodu
eşlemeleri, gün/ay hesaplamaları, fiyat/indirim kuralları — yani "girdiye göre bir değer üret"
senaryolarının tamamında.

### ESKİ vs YENİ

```java
// ESKİ (Java 11 ve öncesi) — break gerektirir, fall-through riski var
int gunSayisi;
switch (ay) {
    case OCAK:
    case MART:
    case MAYIS:
        gunSayisi = 31;
        break;            // unutursan hata!
    case SUBAT:
        gunSayisi = 28;
        break;
    default:
        gunSayisi = 30;
}

// YENİ (Java 14 KALICI) — değer döndürür, break yok, fall-through yok
int gunSayisi = switch (ay) {
    case OCAK, MART, MAYIS -> 31;
    case SUBAT             -> 28;
    default                -> 30;
};
```

### GERÇEK HAYAT ÖRNEĞİ
Bir e-ticaret sisteminde müşteri segmentine göre indirim oranı belirleme; bir bankacılık
uygulamasında işlem tipine göre komisyon hesaplama. Detaylı çalışan örnek:
[`SwitchExpressionsKalici.java`](./SwitchExpressionsKalici.java)

### Avantaj / Dezavantaj / Risk
- **Avantaj:** Daha az kod, fall-through hatası imkânsız, exhaustiveness (enum'da tüm
  değerleri kapsama) kontrolü derleyici tarafından zorlanır.
- **Dezavantaj:** Çok eski Java'ya alışkın ekiplerde okuma alışkanlığı değişimi gerekir.
- **Risk:** Yok denecek kadar az; Java 14'ten itibaren tamamen standart.

---

# 2) Records (JEP 359) — Preview

### NEDİR
`record`, yalnızca **veri taşımak** için tasarlanmış, değişmez (immutable) ve özlü (concise)
bir sınıf türüdür. Tek satırda tanımladığınız bir record, sizin için otomatik olarak şunları
üretir: `private final` alanlar, tüm alanları alan **canonical constructor**, her alan için
**accessor (getter)** metotlar, `equals()`, `hashCode()` ve `toString()`.

```java
public record Nokta(int x, int y) { }   // Hepsi bu kadar!
```

### NEDEN GELDİ (Hangi problemi çözüyor)
Java'da basit bir veri sınıfı (DTO/POJO) yazmak ACI VERİCİ derecede tekrarlıydı. Sadece `x` ve
`y` taşıyan bir sınıf için ~50 satır boilerplate yazmak gerekiyordu: constructor, getter'lar,
`equals`, `hashCode`, `toString`. Bu kod hem yazması sıkıcı hem bakımı tehlikeliydi — yeni bir
alan eklediğinizde `equals`/`hashCode`/`toString`'i güncellemeyi unutmak çok kolaydı ve bu da
sinsi hatalara yol açıyordu.

### NE İŞE YARAR
- **Tek satırda tam bir veri sınıfı** — boilerplate yok.
- Otomatik **immutability** (tüm alanlar `final`).
- Otomatik, değer tabanlı `equals`/`hashCode` (iki record aynı alan değerlerine sahipse eşittir).
- Okunabilir otomatik `toString` (`Nokta[x=3, y=4]`).
- **Compact constructor** ile tek yerde validasyon.

### NEREDE KOLAYLIK SAĞLAR
DTO'lar (Data Transfer Object), API request/response modelleri, değer nesneleri (value object),
metottan birden fazla değer döndürme (tuple yerine), map anahtarları (otomatik `equals`/`hashCode`
sayesinde), event/mesaj payload'ları.

### ESKİ vs YENİ

```java
// ESKİ — basit bir 2D nokta için ~30+ satır boilerplate
public final class NoktaEski {
    private final int x;
    private final int y;
    public NoktaEski(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    @Override public boolean equals(Object o) { /* ... */ }
    @Override public int hashCode() { /* ... */ }
    @Override public String toString() { /* ... */ }
}

// YENİ — aynı işi yapan record
public record Nokta(int x, int y) { }
```

### GERÇEK HAYAT ÖRNEĞİ
Bir REST API'de `KullaniciDTO`, `ApiResponse<T>`, koordinat/para birimi gibi value object'ler.
Compact constructor ile gelen verinin doğrulanması. Derinlemesine çalışan örnek (eski ~50 satır
POJO vs tek satır record karşılaştırmasıyla): [`RecordsPreview.java`](./RecordsPreview.java)

### Avantaj / Dezavantaj / Risk
- **Avantaj:** Devasa boilerplate azalması, immutability, doğru `equals`/`hashCode` garantisi.
- **Dezavantaj:** Record alanları zorunlu olarak `final`'dır; "mutable" varlık (entity) modeli
  için uygun değildir. Kalıtım desteklemez (record'lar implicitly `final`'dır, başka sınıftan
  extend edemez).
- **Risk:** Java 14'te **preview** — `--enable-preview --release 14` gerekir; API/sözdizimi
  ileride değişebilirdi. Java 16'da KALICI oldu, artık risksizdir.

---

# 3) Pattern Matching for `instanceof` (JEP 305) — Preview

### NEDİR
`instanceof` kontrolü ile birlikte, tip uyuyorsa nesneyi otomatik olarak yeni bir değişkene
**bağlayan (bind)** bir özellik. Ayrıca elle cast yapma ihtiyacını ortadan kaldırır.

```java
if (obj instanceof String s) {
    // 's' burada zaten String tipinde; cast'e gerek yok
    System.out.println(s.length());
}
```

### NEDEN GELDİ (Hangi problemi çözüyor)
`instanceof` sonrası neredeyse her zaman aynı tipe cast yapılırdı. Bu üç satırlık kalıp
(`instanceof` kontrolü → değişken tanımı → cast) hem tekrarlıydı hem de cast'i yanlış tipe
yapma riski taşıyordu (gerçi derleyici çoğunu yakalar ama yine de gürültü).

### NE İŞE YARAR
- Tip kontrolü ve cast tek adımda birleşir; "binding variable" otomatik oluşur.
- Kod daha kısa, daha güvenli, daha okunaklı olur.
- Binding değişkeni, koşulun doğru olduğu kapsamda (flow scoping) kullanılabilir.

### NEREDE KOLAYLIK SAĞLAR
`equals()` override etme, generic/`Object` parametre işleme, JSON/`Object` parse, ziyaretçi
(visitor) benzeri tip-temelli dallanmalar, polymorphic veri işleme.

### ESKİ vs YENİ

```java
// ESKİ — kontrol + tanım + cast (3 satır, gereksiz cast)
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// YENİ — tek satır, otomatik bağlama
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

### GERÇEK HAYAT ÖRNEĞİ
Farklı tipte nesneleri işleyen bir metot ve klasik `equals` override'ı. Detaylı çalışan örnek:
[`PatternMatchingInstanceof.java`](./PatternMatchingInstanceof.java)

### Avantaj / Dezavantaj / Risk
- **Avantaj:** Daha az kod, cast hatalarının elenmesi, flow scoping ile temiz kapsam.
- **Dezavantaj:** Çok karmaşık koşullarda binding değişkeninin kapsamını takip etmek zihinsel
  yük getirebilir.
- **Risk:** Java 14'te **preview**. Java 16'da KALICI oldu.

---

# 4) Helpful NullPointerExceptions (JEP 358) — Standart

### NEDİR
NullPointerException (NPE) mesajlarının, **tam olarak hangi değişkenin/metodun null** olduğunu
açıkça söylemesi. Eskiden NPE sadece satır numarası verirdi; o satırda birden çok null adayı
varsa hangisinin patladığını tahmin etmeniz gerekirdi.

### NEDEN GELDİ (Hangi problemi çözüyor)
`a.getB().getC().getD()` gibi zincirleme çağrılarda eski NPE şöyle derdi:
```
Exception in thread "main" java.lang.NullPointerException
    at com.ornek.App.main(App.java:42)
```
Bu satırda `a`, `getB()`'nin sonucu, `getC()`'nin sonucu — hangisi null? Belli değil! Debugging
saatler alabiliyordu.

### NE İŞE YARAR
Java 14'ten itibaren NPE mesajı tam olarak şunu söyler:
```
Cannot invoke "Sehir.getPostaKodu()" because the return value of
"Adres.getSehir()" is null
```
Yani: "`Adres.getSehir()` null döndürdüğü için `Sehir.getPostaKodu()` çağrılamadı." — Hangi
parçanın null olduğu kristal netliğinde.

### NEREDE KOLAYLIK SAĞLAR
İç içe nesne grafikleri (object graph), DTO/entity zincirleri, üretim (production) loglarındaki
NPE'lerin teşhisi, eğitim/öğrenme. Debugger açmadan logdan doğrudan kök nedeni görmek.

### Aktivasyon Notu
- Java 14'te flag: `-XX:+ShowCodeDetailsInExceptionMessages`.
- **Java 15'ten itibaren bu davranış VARSAYILAN olarak AÇIKTIR** (flag'e gerek yok).
- Java 14'te varsayılan deneysel/açıktı; kapatmak için `-XX:-ShowCodeDetailsInExceptionMessages`.

### ESKİ vs YENİ
```text
ESKİ (Java 13):
  java.lang.NullPointerException
      at App.main(App.java:42)

YENİ (Java 14+):
  java.lang.NullPointerException: Cannot invoke "Sehir.getPostaKodu()"
  because the return value of "Adres.getSehir()" is null
      at App.main(App.java:42)
```

### GERÇEK HAYAT ÖRNEĞİ
Kullanıcı → Adres → Şehir → PostaKodu zinciri; bilerek null bırakılan bir halka ve yakalanan
NPE mesajının yazdırılması. Detaylı çalışan örnek: [`HelpfulNullPointer.java`](./HelpfulNullPointer.java)

### Avantaj / Dezavantaj / Risk
- **Avantaj:** Debugging süresinde dramatik kısalma, daha anlaşılır production logları.
- **Dezavantaj:** Çok nadir durumlarda mesaj oluşturmak değişken adlarını bytecode'dan okuyabilir
  (varsayılan kapalıyken gizlilik kaygısı vardı; bu yüzden 14'te opsiyoneldi, 15'te açıldı).
- **Risk:** Yok; tamamen geriye uyumlu.

---

# 5) Foreign-Memory Access API (JEP 370) — Incubator (yalnızca açıklama)

### NEDİR
Java programlarının, JVM yığını (heap) DIŞINDAKİ bellekteki (off-heap / native memory) verilere
**güvenli ve verimli** erişebilmesi için tasarlanmış yeni bir API'dir. `MemorySegment`,
`MemoryAddress` ve `MemoryLayout` gibi soyutlamalar sunar.

### NEDEN GELDİ (Hangi problemi çözüyor)
Java tarihinde off-heap belleğe erişmenin iki yolu vardı ve ikisi de sorunluydu:
1. **`ByteBuffer` (java.nio):** Boyut sınırlı (max ~2GB / Integer.MAX_VALUE), serbest bırakma
   (deallocation) zamanlaması belirsiz (GC'ye bağlı).
2. **`sun.misc.Unsafe`:** Resmi olmayan, tehlikeli, desteklenmeyen, kaldırılması planlanan bir
   iç API. Yanlış kullanım JVM'i çökertebiliyordu.

Foreign-Memory Access API, bu iki kötü seçeneğin yerine **güvenli, deterministik ve performanslı**
bir alternatif getirir.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR
- Büyük native bellek bloklarıyla çalışma (büyük veri, bellek-eşlemeli dosyalar, native kütüphane
  tamponları).
- Deterministik bellek yönetimi (ne zaman serbest bırakılacağı kontrol edilebilir).
- Yüksek performanslı I/O, native interoperability (sonraki sürümlerde Foreign Function & Memory
  API — Project Panama'nın bir parçası — olarak olgunlaştı).

### Durumu
Java 14'te **incubator** modülüdür (`jdk.incubator.foreign`). Incubator, preview'dan daha erken
bir olgunluk seviyesidir; deneme amaçlıdır ve `--add-modules jdk.incubator.foreign` ile
etkinleştirilir. Project Panama kapsamında Java 21'de **Foreign Function & Memory API** olarak
kalıcılaştı (JEP 442/454). Bu repodaki örneklerde incubator modülü için çalışan kod verilmemiştir
(amaca uygun olarak yalnızca kavramsal açıklama).

---

## Java 13'ten 14'e Geçişte Ne Değişti? (Özet)

| Konu | Java 13 | Java 14 |
|------|---------|---------|
| Switch Expressions | Preview (2.) | **KALICI** (artık standart) |
| Records | Yok | **Preview (1.)** |
| Pattern Matching for instanceof | Yok | **Preview (1.)** |
| Helpful NPE | Yok | **Var** (`-XX:+ShowCodeDetailsInExceptionMessages`) |
| Text Blocks | Preview (1.) | Preview (2.) (15'te kalıcı) |
| Foreign-Memory Access | Yok | **Incubator** |
| GC | Mevcut | ZGC ve Shenandoah olgunlaştı; CMS GC kaldırıldı (JEP 363) |

### Geçişte Dikkat Edilecekler
- **CMS Garbage Collector kaldırıldı** (JEP 363): `-XX:+UseConcMarkSweepGC` kullanan eski
  yapılandırmalar artık çalışmaz; G1 (varsayılan) veya ZGC'ye geçilmelidir.
- Switch Expressions kullanıyorsanız `--enable-preview` bayraklarını **kaldırabilirsiniz**
  (14'te kalıcı oldu).
- Records ve pattern matching denemek için `--enable-preview --release 14` gerekir.

---

## Bu Repoyu Derleme / Çalıştırma

Bu repodaki örnekler **Java 16+** (Records ve pattern matching'in kalıcılaştığı sürüm) ile
`--enable-preview` OLMADAN derlenir. Java 21 önerilir:

```bash
cd /Users/miracguntogar/Desktop/test/Java-Versiyon-Analizi/07-Java-14/

# Tek tek derleme + çalıştırma (Java 16+):
javac SwitchExpressionsKalici.java && java SwitchExpressionsKalici
javac RecordsPreview.java          && java RecordsPreview
javac PatternMatchingInstanceof.java && java PatternMatchingInstanceof
javac HelpfulNullPointer.java      && java HelpfulNullPointer
```

**Java 14'te (tarihsel doğruluk için) preview özelliklerini derlemek isterseniz:**
```bash
javac --enable-preview --release 14 RecordsPreview.java
java  --enable-preview RecordsPreview
```

> Helpful NPE Java 14'te flag gerektirir: `java -XX:+ShowCodeDetailsInExceptionMessages ...`
> Java 15+ ile varsayılan açıktır.
