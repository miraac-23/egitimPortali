# Java Sürüm Analizi (Java 8 → Java 25)

Bu çalışma, **Java 8'den Java 25'e** kadar tüm sürümlerin getirdiği önemli özellikleri, bu özelliklerin **neden geldiğini**, **hangi problemi çözdüğünü**, **ne işe yaradığını** ve **gerçek hayatta nerede kolaylık sağladığını** derinlemesine inceler. Her sürüm için ayrı bir klasör vardır; her klasörde detaylı bir `README.md` ve özelliği bizzat gösteren **çalışan `.java` örnek dosyaları** bulunur.

> Hazırlık tarihi: 2026 — En güncel LTS sürümü Java 25 (Eylül 2025) baz alınmıştır.

---

## Bu Dokümantasyonun Amacı

Bir yazılım projesinde Java sürümü seçmek veya mevcut bir projeyi yeni bir sürüme taşımak; teknik, finansal ve operasyonel sonuçları olan stratejik bir karardır. Bu çalışma şu dört temel soruya yanıt verir:

1. **Java sürümleri arasındaki temel farklar nelerdir?**
2. **Her sürümle gelen yeni özellikler tam olarak ne işe yarar?** (örneklerle)
3. **Versiyon geçişlerinin avantaj ve dezavantajları nelerdir?**
4. **Proje açısından geçiş riskleri ve kazanımları nelerdir?**

---

## Nasıl Okunmalı?

- **Java'ya yeni başlıyorsanız veya temelleri tazelemek istiyorsanız:** `01-Java-8` ile başlayın. Java 8, modern Java'nın temelini (lambda, stream, Optional) attığı için en kritik sürümdür.
- **Bir geçiş (migration) planlıyorsanız:** Önce [`GECIS-REHBERI.md`](GECIS-REHBERI.md) ve [`SURUM-KARSILASTIRMA.md`](SURUM-KARSILASTIRMA.md) dosyalarını okuyun, sonra hedef LTS sürümünün klasörüne odaklanın.
- **Belirli bir özelliği merak ediyorsanız:** Aşağıdaki "Özellik → Sürüm" indeksinden ilgili klasöre gidin.

---

## İçindekiler (Sürüm Klasörleri)

| # | Klasör | Sürüm | Çıkış | LTS? | Öne Çıkan Özellik(ler) |
|---|--------|-------|-------|------|------------------------|
| 01 | [`01-Java-8`](01-Java-8/) | Java 8 | Mart 2014 | ✅ LTS | Lambda, Stream API, Optional, java.time |
| 02 | [`02-Java-9`](02-Java-9/) | Java 9 | Eylül 2017 | ❌ | Modül Sistemi (JPMS), JShell, `List.of` |
| 03 | [`03-Java-10`](03-Java-10/) | Java 10 | Mart 2018 | ❌ | `var` (yerel tip çıkarımı) |
| 04 | [`04-Java-11`](04-Java-11/) | Java 11 | Eylül 2018 | ✅ LTS | Yeni HTTP Client, String metotları, tek dosya çalıştırma |
| 05 | [`05-Java-12`](05-Java-12/) | Java 12 | Mart 2019 | ❌ | Switch Expressions (preview), Teeing Collector |
| 06 | [`06-Java-13`](06-Java-13/) | Java 13 | Eylül 2019 | ❌ | Text Blocks (preview) |
| 07 | [`07-Java-14`](07-Java-14/) | Java 14 | Mart 2020 | ❌ | Records (preview), Helpful NPE, Switch Expr. (kalıcı) |
| 08 | [`08-Java-15`](08-Java-15/) | Java 15 | Eylül 2020 | ❌ | Sealed Classes (preview), Text Blocks (kalıcı) |
| 09 | [`09-Java-16`](09-Java-16/) | Java 16 | Mart 2021 | ❌ | Records (kalıcı), Pattern Matching `instanceof` (kalıcı), `Stream.toList()` |
| 10 | [`10-Java-17`](10-Java-17/) | Java 17 | Eylül 2021 | ✅ LTS | Sealed Classes (kalıcı), cebirsel veri tipleri |
| 11 | [`11-Java-18`](11-Java-18/) | Java 18 | Mart 2022 | ❌ | UTF-8 varsayılan, Simple Web Server |
| 12 | [`12-Java-19`](12-Java-19/) | Java 19 | Eylül 2022 | ❌ | Virtual Threads (preview), Record Patterns (preview) |
| 13 | [`13-Java-20`](13-Java-20/) | Java 20 | Mart 2023 | ❌ | Scoped Values (incubator), preview iyileştirmeleri |
| 14 | [`14-Java-21`](14-Java-21/) | Java 21 | Eylül 2023 | ✅ LTS | **Virtual Threads (kalıcı)**, Pattern Matching switch (kalıcı), Sequenced Collections |
| 15 | [`15-Java-22`](15-Java-22/) | Java 22 | Mart 2024 | ❌ | FFM API (kalıcı), Unnamed Variables, Stream Gatherers (preview) |
| 16 | [`16-Java-23`](16-Java-23/) | Java 23 | Eylül 2024 | ❌ | Markdown JavaDoc, Module Import Declarations |
| 17 | [`17-Java-24`](17-Java-24/) | Java 24 | Mart 2025 | ❌ | Stream Gatherers (kalıcı), Class-File API, kuantum dirençli kripto |
| 18 | [`18-Java-25`](18-Java-25/) | Java 25 | Eylül 2025 | ✅ LTS | **Scoped Values & yapısal eşzamanlılık olgunlaşması, Compact Source Files** |

> **LTS (Long-Term Support):** Uzun süreli destek alan, kurumsal projeler için önerilen sürümlerdir. Java dünyasında LTS sürümleri: **8, 11, 17, 21, 25**. Üretim ortamları neredeyse her zaman bir LTS sürümünde çalışır.

---

## En Üst Seviye Dokümanlar

| Doküman | İçerik |
|---------|--------|
| [`SURUM-KARSILASTIRMA.md`](SURUM-KARSILASTIRMA.md) | Tüm sürümlerin özellik bazında yan yana karşılaştırması, "hangi özellik hangi sürümde geldi" tablosu, preview→kalıcı evrim çizelgesi |
| [`GECIS-REHBERI.md`](GECIS-REHBERI.md) | Sürüm geçişlerinin avantaj/dezavantaj/riskleri, proje bazlı geçiş senaryoları (8→11, 8→17, 11→17, 17→21, 21→25), kontrol listeleri |

---

## Özellik → Sürüm Hızlı İndeksi

Belirli bir özelliği arıyorsanız, hangi sürümle geldiğini buradan bulabilirsiniz:

- **Lambda İfadeleri, Stream API, Optional, `java.time`** → [Java 8](01-Java-8/)
- **Modül Sistemi (JPMS), `List.of`/`Set.of`/`Map.of`, JShell** → [Java 9](02-Java-9/)
- **`var` (yerel değişken tip çıkarımı)** → [Java 10](03-Java-10/)
- **Yeni HTTP Client, `String.strip/isBlank/lines/repeat`, tek dosya çalıştırma** → [Java 11](04-Java-11/)
- **Switch Expressions** → [Java 12 (preview)](05-Java-12/), [Java 14 (kalıcı)](07-Java-14/)
- **Text Blocks (`"""`)** → [Java 13 (preview)](06-Java-13/), [Java 15 (kalıcı)](08-Java-15/)
- **Records** → [Java 14 (preview)](07-Java-14/), [Java 16 (kalıcı)](09-Java-16/)
- **Pattern Matching for `instanceof`** → [Java 14 (preview)](07-Java-14/), [Java 16 (kalıcı)](09-Java-16/)
- **Helpful NullPointerExceptions** → [Java 14](07-Java-14/)
- **Sealed Classes** → [Java 15 (preview)](08-Java-15/), [Java 17 (kalıcı)](10-Java-17/)
- **Pattern Matching for `switch`** → [Java 17 (preview)](10-Java-17/), [Java 21 (kalıcı)](14-Java-21/)
- **Virtual Threads (Project Loom)** → [Java 19 (preview)](12-Java-19/), [Java 21 (kalıcı)](14-Java-21/)
- **Record Patterns (destructuring)** → [Java 19 (preview)](12-Java-19/), [Java 21 (kalıcı)](14-Java-21/)
- **Sequenced Collections** → [Java 21](14-Java-21/)
- **Foreign Function & Memory API** → [Java 22 (kalıcı)](15-Java-22/)
- **Stream Gatherers** → [Java 22 (preview)](15-Java-22/), [Java 24 (kalıcı)](17-Java-24/)
- **Scoped Values, Yapısal Eşzamanlılık (olgunlaşma)** → [Java 25](18-Java-25/)
- **Compact Source Files & `void main()` (basitleştirilmiş giriş)** → [Java 25](18-Java-25/)

---

## Örnekleri Çalıştırma

Örnek dosyalar Türkçe yorumlarla, gerçek hayat senaryolarıyla (Calisan, Urun, Siparis, Hesap gibi domain sınıfları) yazılmıştır ve `main` metodu içerir.

**Java 11+ ile tek dosya çalıştırma (derlemeye gerek yok):**
```bash
java 01-Java-8/StreamApiOrnekleri.java
```

**Klasik derle-çalıştır:**
```bash
javac 01-Java-8/StreamApiOrnekleri.java
java -cp 01-Java-8 StreamApiOrnekleri
```

**Preview özellik içeren dosyalar için** (örn. Java 21'de String Templates):
```bash
javac --release 21 --enable-preview StringTemplatesPreview.java
java --enable-preview StringTemplatesPreview
```
> Her dosyanın başında, gerektiğinde hangi sürüm ve bayrakla derleneceği yorum olarak belirtilmiştir.

---

## Önemli Bir Not: Sürümleme Modeli

Java 9'dan itibaren Oracle **6 aylık sabit yayın takvimine** (release cadence) geçti. Yani her yıl Mart ve Eylül'de yeni bir sürüm çıkar. Bu sürümlerin çoğu kısa ömürlüdür; **her 2-3 yılda bir** bir sürüm **LTS** olarak işaretlenir ve yıllarca güvenlik/hata düzeltmesi desteği alır.

Bu nedenle pratikte projeler ara sürümlere değil, LTS sürümlerine (8 → 11 → 17 → 21 → 25) geçer. Ara sürümler ise yeni özelliklerin **preview (önizleme)** olarak denendiği, geri bildirim toplanıp olgunlaştırıldığı laboratuvar görevi görür. Bu "preview → kalıcı" evrimi bu dokümantasyonun ana temalarından biridir.
