# Java Sürüm Karşılaştırması (Java 8 → Java 25)

Bu doküman, tüm Java sürümlerinin özelliklerini **yan yana** karşılaştırır; "hangi özellik hangi sürümde geldi", "preview'dan kalıcıya geçiş ne zaman oldu" ve "sürümler arası temel farklar nelerdir" sorularını tek bakışta yanıtlamayı amaçlar.

---

## 1. Sürüm Takvimi ve Destek Durumu

| Sürüm | Çıkış Tarihi | LTS mi? | Premier/Genişletilmiş Destek (yaklaşık) | Notlar |
|-------|--------------|---------|------------------------------------------|--------|
| Java 8 | Mart 2014 | ✅ | Uzun yıllar (hâlâ yaygın) | Lambda devrimi; en uzun süre kullanılan sürüm |
| Java 9 | Eylül 2017 | ❌ | Kısa | 6 aylık takvimin başlangıcı; JPMS |
| Java 10 | Mart 2018 | ❌ | Kısa | `var` |
| Java 11 | Eylül 2018 | ✅ | ~2026'ya kadar | İlk "modern" LTS; Java 8 sonrası kurumsal hedef |
| Java 12-16 | 2019-2021 | ❌ | Kısa | Preview özellik laboratuvarı |
| Java 17 | Eylül 2021 | ✅ | ~2029'a kadar | Çok yaygın kurumsal standart; Spring Boot 3 tabanı |
| Java 18-20 | 2022-2023 | ❌ | Kısa | Loom/pattern matching olgunlaşması |
| Java 21 | Eylül 2023 | ✅ | ~2031'e kadar | Virtual Threads ile yeni çağ |
| Java 22-24 | 2024-2025 | ❌ | Kısa | FFM, Stream Gatherers olgunlaşması |
| Java 25 | Eylül 2025 | ✅ | ~2033'e kadar | En güncel LTS |

> Destek tarihleri sağlayıcıya göre değişir (Oracle, Eclipse Temurin/Adoptium, Amazon Corretto, Azul Zulu, Microsoft Build of OpenJDK). Genişletilmiş destekler bazı dağıtımlarda daha uzundur.

---

## 2. Büyük Özelliklerin "Preview → Kalıcı" Evrim Çizelgesi

Java'nın modern özellik geliştirme modeli şöyledir: bir özellik önce **incubator** (deneme modülü) veya **preview** (dil önizlemesi) olarak çıkar, birkaç sürüm boyunca topluluk geri bildirimiyle iyileştirilir, sonra **kalıcı (standart)** hale gelir. Aşağıdaki tablo bu yolculuğu gösterir:

| Özellik | İlk Görünüm | Ara Aşamalar | Kalıcı (Standart) |
|---------|-------------|--------------|-------------------|
| Switch Expressions | 12 (preview) | 13 (preview, `yield`) | **14** |
| Text Blocks (`"""`) | 13 (preview) | 14 (preview) | **15** |
| Records | 14 (preview) | 15 (preview) | **16** |
| Pattern Matching for `instanceof` | 14 (preview) | 15 (preview) | **16** |
| Sealed Classes | 15 (preview) | 16 (preview) | **17** |
| Pattern Matching for `switch` | 17 (preview) | 18, 19, 20 (preview) | **21** |
| Record Patterns | 19 (preview) | 20 (preview) | **21** |
| Virtual Threads | 19 (preview) | 20 (preview) | **21** |
| Foreign Function & Memory API | 19 (preview) | 20, 21 (preview) | **22** |
| Stream Gatherers | 22 (preview) | 23 (preview) | **24** |
| Scoped Values | 20 (incubator) | 21-24 (preview) | **25** (kalıcı yolunda/kalıcı) |
| Flexible Constructor Bodies | 22 (preview) | 23, 24 (preview) | **25** |
| Compact Source Files & Instance Main | 21 (preview) | 22, 23, 24 (preview) | **25** |
| Module Import Declarations | 23 (preview) | 24 (preview) | **25** |
| String Templates | 21 (preview) | 22 (preview) | ❌ **Geri çekildi** (23'te kaldırıldı, tasarım yeniden ele alınıyor) |

> **String Templates uyarısı:** Java 21 ve 22'de preview olarak geldi ancak tasarım sorunları nedeniyle **Java 23'te kaldırıldı**. Java 25'te standart dilde yoktur. Bu, "preview" etiketinin neden önemli olduğunun en iyi örneğidir: preview özellikler garanti değildir, değişebilir veya iptal edilebilir.

---

## 3. Paradigma ve Yazım Stili Değişimi (Aynı İşin Sürümlere Göre Hâli)

Aynı basit görevin —"bir listedeki aktif kullanıcıların adlarını büyük harfe çevirip topla"— sürümlere göre nasıl evrildiğine bakalım:

**Java 7 ve öncesi (imperatif):**
```java
List<String> sonuc = new ArrayList<>();
for (Kullanici k : kullanicilar) {
    if (k.isAktif()) {
        sonuc.add(k.getAd().toUpperCase());
    }
}
```

**Java 8 (fonksiyonel — Stream API):**
```java
List<String> sonuc = kullanicilar.stream()
    .filter(Kullanici::isAktif)
    .map(k -> k.getAd().toUpperCase())
    .collect(Collectors.toList());
```

**Java 16+ (`Stream.toList()` ile daha kısa):**
```java
var sonuc = kullanicilar.stream()
    .filter(Kullanici::isAktif)
    .map(k -> k.getAd().toUpperCase())
    .toList();
```

Bir "ödeme türüne göre işlem" örneği — tip kontrolünün evrimi:

**Java 8 (eski instanceof + cast):**
```java
String mesaj;
if (odeme instanceof KrediKarti) {
    KrediKarti kk = (KrediKarti) odeme;
    mesaj = "Kart: " + kk.getKartNo();
} else if (odeme instanceof Havale) {
    Havale h = (Havale) odeme;
    mesaj = "IBAN: " + h.getIban();
} else {
    mesaj = "Bilinmeyen";
}
```

**Java 21 (sealed + record patterns + pattern matching switch):**
```java
String mesaj = switch (odeme) {
    case KrediKarti(var kartNo, var sahip) -> "Kart: " + kartNo;
    case Havale(var iban)                  -> "IBAN: " + iban;
    // sealed olduğu için 'default' gerekmez — derleyici tüm halleri kontrol eder
};
```

Bu üç örnek, Java'nın **daha az kod (boilerplate), daha çok güvenlik (derleyici denetimi)** yönündeki temel yönelimini özetler.

---

## 4. Kategori Bazında Özellik Haritası

### Dil Söz Dizimi (Syntax)
| Özellik | Sürüm |
|---------|-------|
| Lambda ifadeleri, method references | 8 |
| `var` yerel değişken | 10 |
| Switch expressions | 14 |
| Text blocks | 15 |
| Records | 16 |
| Pattern matching (`instanceof`) | 16 |
| Sealed classes | 17 |
| Pattern matching (`switch`) | 21 |
| Record patterns (destructuring) | 21 |
| Unnamed variables (`_`) | 22 |
| Flexible constructor bodies | 25 |
| Compact source files & `void main()` | 25 |

### Eşzamanlılık (Concurrency)
| Özellik | Sürüm |
|---------|-------|
| `CompletableFuture` | 8 |
| Virtual Threads | 21 |
| Structured Concurrency | 21+ (olgunlaşma 25) |
| Scoped Values | 25 |

### API / Kütüphane
| Özellik | Sürüm |
|---------|-------|
| Stream API, Optional, `java.time` | 8 |
| `List.of`/`Set.of`/`Map.of` | 9 |
| `takeWhile`/`dropWhile` | 9 |
| Yeni HTTP Client (`java.net.http`) | 11 |
| `String.strip/isBlank/lines/repeat` | 11 |
| `Files.readString/writeString` | 11 |
| `Stream.toList()` | 16 |
| Sequenced Collections | 21 |
| Foreign Function & Memory API | 22 |
| Stream Gatherers | 24 |

### Platform / Araçlar / JVM
| Özellik | Sürüm |
|---------|-------|
| PermGen → Metaspace | 8 |
| Modül sistemi (JPMS) | 9 |
| JShell (REPL) | 9 |
| Tek dosya çalıştırma | 11 |
| Helpful NPE mesajları | 14 |
| UTF-8 varsayılan charset | 18 |
| Simple Web Server (`jwebserver`) | 18 |
| Generational ZGC | 21 |
| Class-File API | 24 |
| Kuantum dirençli kriptografi | 24 |

---

## 5. Hangi Sürümü Seçmeli? (Özet Tavsiye)

- **Yeni proje (2026):** **Java 25 LTS**. En uzun destek, en modern özellikler. Ekibin/kütüphanelerin uyumu varsa ilk tercih.
- **Java 25'e geçmemiş kurumsal proje:** **Java 21 LTS** çok güvenli bir orta nokta — Virtual Threads dahil tüm modern özelliklere sahip, ekosistem desteği olgun.
- **Eski sistem (legacy) modernizasyonu:** Önce **Java 17 LTS**'e taşıyın (en geniş kütüphane/araç desteği), sonra 21/25'e adım adım ilerleyin.
- **Hâlâ Java 8'de olanlar:** En kritik teknik borç budur. En azından Java 11 veya 17'ye taşıma planı yapılmalıdır (bkz. [`GECIS-REHBERI.md`](GECIS-REHBERI.md)).

Detaylı geçiş senaryoları, riskler ve kontrol listeleri için [`GECIS-REHBERI.md`](GECIS-REHBERI.md) dosyasına bakın.
