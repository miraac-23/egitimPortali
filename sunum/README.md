# Java & Spring — Kapsamlı Eğitim Sunumu

Modern, etkileşimli, tarayıcı tabanlı bir eğitim sunumu. Hiçbir kurulum/build gerektirmez.

## Nasıl Açılır?

**En kolay yol:** `index.html` dosyasına çift tıkla — varsayılan tarayıcında açılır.

> İnternet bağlantısı varsa yazı tipleri (Google Fonts) yüklenir; yoksa sistem yazı tipiyle yine sorunsuz çalışır.

İstersen yerel sunucuyla da açabilirsin:

```bash
cd sunum
python3 -m http.server 8000
# Tarayıcıda: http://localhost:8000
```

## Kontroller (dinamik yönetim)

| Tuş / Eylem | İşlev |
|-------------|-------|
| `→` `Space` `PageDown` | Sonraki slayt |
| `←` `PageUp` | Önceki slayt |
| `Home` / `End` | İlk / son slayt |
| **`M`** veya ☰ butonu | İçindekiler menüsü (herhangi bir konuya atla) |
| **`O`** veya ▦ butonu | Genel bakış (tüm slaytlar ızgara hâlinde) |
| **`F`** veya ⤢ butonu | Tam ekran |
| `Esc` | Açık paneli kapat |
| Dokunmatik | Sağa/sola kaydır |

Adres çubuğundaki `#sayfa-no` (ör. `index.html#27`) ile doğrudan bir slayta da gidebilirsin.

## İçerik Düzeni

5 ana bölüm, **83 slayt**, **138 Soru–Cevap**. Her konuda *nedir / ne işe yarar / nasıl kullanılır*, **gerçek hayat örneği** ve **Soru–Cevap**:

1. **Java Çekirdek & İleri** — çoklu iş parçacığı (4 slayt), virtual threads (3 slayt), reflection, design patterns, JDK/JRE/JVM, JVM mimarisi, GC & JIT
2. **Java Sürüm Analizi** — Java 8, 11, 17, 21, 25 ve geçiş kazanımları (öncesi/sonrası kodlarla)
3. **Spring** — IoC/DI, bean yaşam döngüsü & scope, transaction (distributed/Saga dâhil), Web MVC
4. **Spring Boot** — `03-spring-boot` klasöründeki **45 konunun tamamı** tek tek, gerçek örneklerle
5. **Spring vs Spring Boot** — karşılaştırma, seçim ve geçiş

## Düzenleme

İçerik bölüm bölüm ayrı dosyalardadır (kolay yönetim için):

- `js/slides.js` — çekirdek (kapak, kapanış)
- `js/sec-java.js`, `sec-version.js`, `sec-spring.js`, `sec-boot.js`, `sec-compare.js` — bölüm içerikleri

Her dosya `PRESENTATION.sections.push({...})` ile bir bölüm ekler. Görünüm `css/style.css`, davranış `js/engine.js` dosyasındadır.

Blok tipleri: `lead`, `heading`, `framework`, `bullets`, `twocol`, `code`, `table`, `callout` (variant: `info`/`warn`/`tip`/`key`/`real`), `tags`, `stats`, `split`, **`qa`** (Soru–Cevap).
