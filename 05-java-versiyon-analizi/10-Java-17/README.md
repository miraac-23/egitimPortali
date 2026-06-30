# Java 17 (Eylül 2021) — LTS — Detaylı Türkçe Dokümantasyon

> **Java 17, Java 8 ve Java 11'den sonra kurumsal dünyanın en çok hedeflediği
> LTS sürümüdür. Bu klasör, bu repo'nun EN DETAYLI bölümüdür.**

## Sürüm Bilgisi

| Özellik | Değer |
|---|---|
| Sürüm | Java SE 17 / JDK 17 |
| Çıkış Tarihi | 14 Eylül 2021 |
| LTS mi? | **EVET — Long-Term Support (Uzun Süreli Destek)** |
| Destek Takvimi | Oracle premier destek ~2026, genişletilmiş destek ~2029'a kadar (dağıtıma göre değişir). OpenJDK dağıtımları (Adoptium/Temurin, Amazon Corretto, Azul, Red Hat) genellikle 2027-2029+ destek sunar. |
| Önceki LTS | Java 11 (2018), Java 8 (2014) |
| Sonraki LTS | Java 21 (2023) |

---

## LTS Neden Bu Kadar Önemli? (Kurumsal Bakış)

**LTS (Long-Term Support)** sürümleri, çok yıllı güvenlik yamaları ve hata
düzeltmeleri alır. Kurumlar üretim sistemlerini LTS sürümleri üzerine kurar
çünkü:

1. **Uzun destek penceresi:** Her 6 ayda bir sürüm yükseltmek zorunda kalmazsınız.
2. **Üretim standardı:** Üçüncü parti kütüphaneler, framework'ler (Spring Boot 3.x,
   Jakarta EE 10) ve bulut sağlayıcılar baseline olarak Java 17'yi hedefler.
   - **Spring Boot 3.x, MİNİMUM Java 17 gerektirir** — bu tek başına devasa bir
     kurumsal geçiş dalgası yaratmıştır.
3. **Güvenlik:** Uzun süre CVE yamaları gelir.
4. **Kararlılık:** Java 9-16 arasında biriken tüm modern özellikler (records,
   sealed, pattern matching, switch expressions, text blocks, var, NPE mesajları)
   tek, kararlı ve desteklenen bir pakette toplanır.

**Sonuç:** Java 8/11'de kalmış kurumlar için bir sonraki doğal durak Java 17'dir.

---

## Genel Bakış — Bu Sürümde Neler Var?

| Özellik | Durum | Dosya |
|---|---|---|
| Sealed Classes | **KALICI (JEP 409)** | `SealedClassesKalici.java` |
| Pattern Matching for `switch` | Preview (JEP 406) | `PatternMatchingSwitch.java` |
| Records + Sealed + Pattern (ADT) | Birleşim | `CebirselVeriTipleri.java` |
| Enhanced Pseudo-Random Generators | **KALICI (JEP 356)** | `RandomGenerator.java` |
| Yeni macOS Rendering Pipeline | Kalıcı (JEP 382) | — |
| macOS/AArch64 Portu | Kalıcı (JEP 391) | — |
| Applet API'nin kaldırılmaya hazırlanması | Deprecation (JEP 398) | — |
| Security Manager deprecation | Deprecation (JEP 411) | — |
| Strong Encapsulation (varsayılan) | Kalıcı (JEP 403) | — |
| Foreign Function & Memory API | Incubator (JEP 412) | — |
| Vector API | Incubator (JEP 414) | — |

---

## 1) Sealed Classes (Mühürlü Sınıflar) — KALICI (JEP 409)

**Dosya:** `SealedClassesKalici.java`

### NEDİR?
Bir sınıf/arayüzün, KENDİSİNDEN HANGİ sınıfların türeyebileceğini `permits` ile açıkça kısıtlamasıdır.

### NEDEN GELDİ?
Java'da bir tip ya `final` (kimse türetemez) ya da tamamen açıktı. Arada "sadece şu belirli sınıflar türesin" seçeneği yoktu. Sealed bunu sağlar.

### Anahtar Kelimeler
- `sealed` — mühürlü; alt tipleri `permits` ile sınırlar
- `permits` — izinli alt tipleri listeler (aynı dosyadaysa çıkarılabilir)
- `final` — alt tip artık kapalı (yaprak)
- `non-sealed` — mührü tekrar AÇAR (o daldan herkes türeyebilir)

> **Kural:** Mühürlü bir tipin her izinli alt tipi `final`, `sealed` veya `non-sealed` olmak ZORUNDADIR. `record`'lar örtük `final` olduğu için mühürlü hiyerarşilerde mükemmel yapraktır.

### ESKİ vs YENİ
```java
// ESKİ: ya tamamen final ya tamamen açık; "sadece şunlar türesin" diyemezdik
public abstract class Sekil { }   // herkes türetebilir, kontrol yok

// YENİ: kontrollü, kapalı hiyerarşi
public sealed interface Sekil permits Daire, Kare, Dikdortgen { }
```

### GÜCÜ NEREDE? — Pattern Matching ile Exhaustiveness
Alt tip kümesi sonlu ve bilinir olduğu için, `switch` tüm alt tipleri kapsadığında derleyici `default` dalı İSTEMEZ ve eksik dal varsa **derleme hatası** verir.

### Gerçek Hayat
Durum makineleri, ödeme yöntemleri, mesaj tipleri, AST düğümleri.

---

## 2) Pattern Matching for `switch` — Preview (JEP 406)

**Dosya:** `PatternMatchingSwitch.java`

> **Java 17'de PREVIEW'dir.** Derleme/çalıştırma bayrakları gerekir:
> ```bash
> javac --release 17 --enable-preview PatternMatchingSwitch.java
> java  --enable-preview PatternMatchingSwitch
> ```
> (Özellik **Java 21'de kalıcı** oldu.)

### NEDİR?
`switch`'in artık sabit değerler yerine bir nesnenin **tipine** göre dallanabilmesi: `case String s ->`.

### ESKİ vs YENİ
```java
// ESKİ: uzun if-else-if instanceof zinciri
if (o instanceof Integer i) { ... }
else if (o instanceof String s) { ... }

// YENİ: sade, okunabilir switch
switch (o) {
    case Integer i -> ...;
    case String s  -> ...;
    default        -> ...;
}
```

### Ek Yetenekler
- **Guarded pattern:** `case Integer i when i < 10 ->` (tip + ek koşul)
- **null güvenliği:** `case null ->` (artık switch null'da NPE atmaz)

---

## 3) Records + Sealed + Pattern Matching = CEBİRSEL VERİ TİPLERİ — Modern Java'nın Kalbi

**Dosya:** `CebirselVeriTipleri.java`

> Bu dosya bu repo'nun **en önemli** örneğidir. `--enable-preview` gerektirir
> (switch pattern matching kullanır).

### NEDİR? (Algebraic Data Types / ADT)
- **SEALED** = "sum type": tip, sonlu varyantların toplamıdır (`Sekil = Daire | Kare | Ucgen`)
- **RECORD** = "product type": her varyantın alanları (`Nokta = x VE y`)
- **PATTERN MATCHING** = varyantlara göre güvenli, eksiksiz dallanma

### NEDEN DEVRİMSEL? (Kurumsal Güvenlik)
1. **Geçersiz durumlar temsil edilemez** hale gelir → tip güvenliği.
2. **Exhaustiveness:** Yeni bir varyant eklediğinizde, onu işlemeyen **HER** `switch` derleme hatası verir. "Bir yeri güncellemeyi unuttum" hatası **imkânsız** olur.
3. Domain'i kodda neredeyse doğal dil gibi ifade edersiniz.

### Dosyadaki Gerçek Hayat Senaryoları
- **A) Ödeme sistemi:** `Odeme = KrediKarti | Havale | KapidaOdeme`; her tipin farklı komisyon kuralı (exhaustiveness ile güvenli).
- **B) Geometrik şekiller:** alan/çevre hesabı (Heron formülü dahil).
- **C) JSON düğüm tipleri:** `Json = JNull | JBool | JSayi | JMetin | JDizi | JObje` — özyinelemeli (recursive) serileştirme.
- **Bonus) Result tipi:** `Sonuc = Basari | Hata` — exception yerine tip olarak hata yönetimi.

### Klasik OOP vs ADT (Expression Problem)
- Klasik OOP: yeni **alt tip** eklemek kolay, yeni **işlem** eklemek zor.
- ADT + switch: yeni **işlem** eklemek kolay; yeni **varyant** eklediğinizde derleyici eksik switch'leri yakalar.

---

## 4) Enhanced Pseudo-Random Number Generators — KALICI (JEP 356)

**Dosya:** `RandomGenerator.java`

### NEDİR / NEDEN GELDİ?
`java.util.random.RandomGenerator` ortak arayüzü altında tüm rastgele üreticileri toplar. `RandomGeneratorFactory` ile algoritmayı **isimle** seçebilirsiniz (örn. `L64X128MixRandom`). Eski `java.util.Random` tek, zayıf bir LCG algoritmasıyla sınırlıydı; değiştirilebilir (pluggable) değildi.

### ESKİ vs YENİ
```java
// ESKİ
var r = new java.util.Random();
// YENİ - algoritma seçilebilir
RandomGenerator g = RandomGeneratorFactory.of("L64X128MixRandom").create();
```

### Gerçek Hayat Notu
- Güvenlik (token/şifre/anahtar) için **daima** `SecureRandom`.
- Paralel simülasyonlarda **splittable/jumpable** üreticiler.

---

## 5) Yeni macOS Rendering Pipeline (JEP 382) ve macOS/AArch64 Portu (JEP 391)
- **JEP 382:** Eski OpenGL temelli Java 2D pipeline'ı, Apple'ın yeni **Metal** API'sine taşındı (OpenGL macOS'ta deprecated edildiği için). Daha iyi grafik performansı ve geleceğe uyum.
- **JEP 391:** Java, **Apple Silicon (M1/M2 — AArch64)** üzerinde yerel olarak çalışır. Apple donanımındaki geliştiriciler/sunucular için yüksek performans ve enerji verimliliği.

---

## 6) Deprecation / Kaldırma — GEÇİŞ RİSKİ
- **Applet API (JEP 398):** Kaldırılmak üzere işaretlendi. Tarayıcılar zaten plugin'i bıraktığından pratikte ölü teknoloji. Eski applet kodu varsa modernleştirin.
- **Security Manager (JEP 411):** `SecurityManager` kaldırılmak üzere deprecate edildi. Güvenlik politikalarını `SecurityManager`'a dayandıran (eski) kodlar gelecekte bozulacak; alternatif izolasyon mekanizmalarına (konteyner, modül sistemi) geçilmesi önerilir.

---

## 7) Strong Encapsulation Varsayılan Açık (JEP 403)
JDK iç API'lerine (`sun.*`, dahili paketler) reflection erişimi artık **varsayılan olarak kapalı** ve `--illegal-access` ile **açılamaz** (bayrak etkisiz). Yalnızca `--add-opens`/`--add-exports` ile noktasal açılabilir.
- **Risk:** İç API'lere dayanan ÇOK ESKİ kütüphaneler kırılabilir.
- **Çözüm:** Bağımlılıkları güncelleyin; geçici olarak `--add-opens` kullanın.

---

## Java 8 / 11'den Java 17'ye Geçiş Rehberi

### Avantajlar
- LTS güvencesi + uzun destek.
- Spring Boot 3.x, modern framework'ler ve bulut platformları için baseline.
- 9-16 arası tüm modern dil özellikleri tek kararlı pakette.
- Daha iyi GC (ZGC/Shenandoah olgunlaştı), daha iyi container farkındalığı, daha iyi performans.

### Riskler ve Dikkat Edilecekler
1. **Strong encapsulation:** İç API'lere dayanan eski kütüphaneler kırılabilir → bağımlılıkları güncelleyin, gerekirse `--add-opens`.
2. **Kaldırılan/değişen API'ler:** Java 8 → 11 geçişinde `javax.xml.bind` (JAXB), `java.activation`, CORBA gibi paketler JDK'dan çıkarılmıştı; bunları ayrı bağımlılık olarak ekleyin.
3. **Security Manager** kullanan kod → alternatif izolasyona geçin.
4. **Applet** kullanan kod → modernleştirin.
5. **Derleme bayrakları:** `--release 17` ile derleyin; tüm bağımlılıkların Java 17 uyumlu sürümlerine yükseltin.

### Önerilen Geçiş Adımları
1. Önce Java 11'e geçin (ara durak), sonra Java 17.
2. Tüm bağımlılıkları en güncel sürümlere çıkarın.
3. `jdeps` ile iç API kullanımını analiz edin.
4. Test ortamında preview/deprecation uyarılarını giderin.
5. Üretim öncesi performans ve GC profilini doğrulayın.

---

## Bu Klasördeki Dosyalar ve Derleme

| Dosya | Preview gerekir mi? |
|---|---|
| `SealedClassesKalici.java` | Hayır |
| `RandomGenerator.java` | Hayır |
| `PatternMatchingSwitch.java` | **Evet** |
| `CebirselVeriTipleri.java` | **Evet** |

```bash
# Preview gerektirmeyenler
javac SealedClassesKalici.java && java SealedClassesKalici
javac RandomGenerator.java     && java RandomGenerator

# Preview gerektirenler (switch pattern matching - Java 17)
javac --release 17 --enable-preview PatternMatchingSwitch.java
java  --enable-preview PatternMatchingSwitch

javac --release 17 --enable-preview CebirselVeriTipleri.java
java  --enable-preview CebirselVeriTipleri
```
