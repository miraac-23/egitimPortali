# Java Sürüm Geçiş Rehberi — Avantajlar, Dezavantajlar, Riskler ve Kazanımlar

Bu doküman, bir projenin Java sürümünü yükseltirken (migration) karşılaşacağı **avantaj ve dezavantajları**, **somut riskleri** ve **kazanımları** ele alır. Ayrıca en yaygın geçiş senaryoları için adım adım yol haritaları ve kontrol listeleri sunar.

---

## 1. Genel Bakış: Neden ve Neden Olmasın?

### Sürüm yükseltmenin genel KAZANIMLARI
- **Performans:** Her LTS sürümü, JVM ve çöp toplayıcı (GC) iyileştirmeleriyle gelir. Aynı kod, daha yeni bir JVM'de kod değişikliği olmadan daha hızlı/daha az bellekle çalışabilir. Örneğin G1 iyileştirmeleri, ZGC, Generational ZGC gecikmeyi (latency) ciddi düşürür.
- **Güvenlik:** LTS dışı veya destek dışı sürümlerde kritik güvenlik yamaları gelmez. Güncel sürüm = yamalı sürüm.
- **Üretkenlik:** Lambda, Stream, Records, Pattern Matching, Text Blocks gibi özellikler kodu kısaltır, okunabilir kılar ve hata yüzeyini azaltır.
- **Ekosistem uyumu:** Modern kütüphaneler (örn. Spring Boot 3.x **Java 17+** ister) yeni sürüm gerektirir. Geride kalmak, kütüphane güncelleyememek demektir.
- **İşe alım ve moral:** Geliştiriciler modern bir teknoloji yığınında çalışmayı tercih eder.

### Sürüm yükseltmenin genel RİSK ve MALİYETLERİ
- **Kırılan değişiklikler (breaking changes):** Kaldırılan API'ler, güçlü kapsülleme (strong encapsulation), kaldırılan modüller.
- **Bağımlılık uyumu:** Eski kütüphaneler yeni JVM'de çalışmayabilir (özellikle bytecode manipülasyonu yapan: eski Lombok, Hibernate, ASM, CGLIB, Mockito sürümleri).
- **Build/araç zinciri:** Maven/Gradle eklentileri, CI/CD imajları, IDE sürümleri güncellenmeli.
- **Test ve doğrulama eforu:** Regresyon testleri, performans testleri tekrar koşulmalı.
- **Davranış değişiklikleri:** Varsayılan charset (Java 18'de UTF-8), GC varsayılanları, tarih/saat davranışları gibi ince farklar.

---

## 2. Sürüm Bazında Kritik "Kırılma" Noktaları

Geçiş planlarken aşağıdaki kırılma noktaları en çok soruna yol açanlardır:

| Sürüm | Dikkat edilmesi gereken kırılma / risk |
|-------|-----------------------------------------|
| **9** | Modül sistemi (JPMS) ile içsel (internal) API'lara erişim kısıtlandı; `sun.*`, `com.sun.*` erişimleri uyarı/hata verir. Sınıf yolu (classpath) ve modül yolu (module-path) ayrımı. |
| **11** | **Java EE ve CORBA modülleri kaldırıldı** (`java.xml.bind`/JAXB, `java.activation`, `java.transaction`, `java.corba`...). Bunları kullanan kod artık derlenmez; bağımlılık olarak harici eklenmeli. Applet API kullanım dışı. |
| **17** | **Güçlü kapsülleme varsayılan oldu** — `--illegal-access` bayrağı kaldırıldı; JDK içsel API'larına yansıma (reflection) ile erişen eski kütüphaneler patlar. Security Manager kullanım dışı (deprecated). |
| **18** | **Varsayılan charset UTF-8 oldu.** Daha önce işletim sisteminin charset'ine güvenen dosya/akış okuma kodları farklı davranabilir (özellikle Türkçe karakterler, Windows-1254/ISO-8859-9). |
| **21** | Çoğunlukla eklemeli (kırılma az). Eski `finalize()` kullanım dışı ilerliyor. |
| **23** | **String Templates kaldırıldı.** Java 21/22'de bu preview özelliği kullandıysanız 23+'a geçişte kod kırılır. |
| **25** | Eklemeli; preview özelliklere (Stable Values, vb.) bağımlılık varsa bayrak yönetimi gerekir. |

---

## 3. En Yaygın Geçiş Senaryoları

### Senaryo A: Java 8 → Java 11 (LTS → LTS)
**Neden:** Java 8 desteği daralıyor; ilk modern adım.
**Kazanımlar:** Yeni HTTP Client, gelişmiş String/Files API'leri, `var`, modül sistemi (opsiyonel), daha iyi GC seçenekleri (G1 varsayılan).
**Başlıca riskler:**
- Kaldırılan Java EE/CORBA modülleri (JAXB, JAX-WS) → harici bağımlılık olarak eklenmeli.
- JPMS nedeniyle içsel API erişim uyarıları.
- Eski kütüphane sürümleri (Spring 4, eski Hibernate) uyumsuz olabilir.
**Yaklaşım:** Modül sistemine geçmek **zorunlu değildir**; classpath modunda kalıp sadece JDK 11'e geçilebilir (en düşük riskli yol).

### Senaryo B: Java 8 → Java 17 (büyük sıçrama)
**Neden:** En geniş modern ekosistem desteği; Spring Boot 3 tabanı.
**Kazanımlar:** A senaryosundaki her şey + Records, Sealed Classes, Pattern Matching, Text Blocks, Switch Expressions, çok daha iyi GC'ler (ZGC, Shenandoah).
**Ek riskler (11'e ek olarak):**
- **Güçlü kapsülleme:** Yansıma kullanan eski kütüphaneler (eski Lombok, Mockito, Hibernate, Spring) güncellenmeli.
- Daha fazla davranış değişikliği biriktiği için kapsamlı regresyon testi şart.
**Yaklaşım:** Tek seferde değil, mümkünse **8 → 11 → 17** kademeli; her adımda derle, testleri koş, bağımlılıkları güncelle.

### Senaryo C: Java 11 → Java 17 (LTS → LTS)
**Neden:** Java 11 desteği biterken doğal sonraki adım.
**Kazanımlar:** Tüm dil özellikleri (records, sealed, pattern matching, text blocks), modern GC'ler.
**Riskler:** Esas olarak güçlü kapsülleme ve charset (18 değil ama yol üstünde) — görece düşük risk; en çok yapılan ve en oturmuş geçişlerden biridir.

### Senaryo D: Java 17 → Java 21 (LTS → LTS)
**Neden:** Virtual Threads ve modern eşzamanlılık.
**Kazanımlar:**
- **Virtual Threads:** "thread-per-request" modelini ölçeklenebilir kılar; reaktif programlamanın karmaşıklığına gerek kalmadan yüksek eşzamanlılık (bkz. [14-Java-21/VirtualThreadsKalici.java](14-Java-21/VirtualThreadsKalici.java)).
- Pattern matching for switch + record patterns (kalıcı), Sequenced Collections.
**Riskler:** Çok düşük; büyük oranda eklemeli. Dikkat: `synchronized` blokları içinde uzun süre bloklayan virtual thread'ler "pinning" yapabilir — kütüphanelerin Loom-uyumlu sürümleri tercih edilmeli.

### Senaryo E: Java 21 → Java 25 (LTS → LTS, en güncel)
**Neden:** En uzun destek, olgunlaşmış Scoped Values / Structured Concurrency, dile giriş kolaylığı.
**Kazanımlar:** Olgunlaşan eşzamanlılık API'leri, Stream Gatherers (kalıcı), basitleştirilmiş `void main()`/compact source files, Module Import Declarations.
**Riskler:** Düşük; preview özelliklere bağlı kod varsa bayrak yönetimi.

---

## 4. Genel Geçiş Kontrol Listesi (Adım Adım)

1. **Mevcut durumu envanterle:** Şu anki JDK sürümü, kullanılan kütüphaneler ve sürümleri, build aracı sürümleri.
2. **Hedef LTS'i belirle:** 17, 21 veya 25. (Ara sürümleri üretimde hedefleme.)
3. **Bağımlılık uyum matrisi çıkar:** Her kütüphanenin hedef JDK'yı destekleyen en düşük sürümünü bul. Özellikle: Spring/Spring Boot, Hibernate, Jackson, Lombok, Mockito, ASM/ByteBuddy, Logback/Log4j.
4. **Build araçlarını güncelle:** Maven (`maven-compiler-plugin` `release` ayarı), Gradle (`sourceCompatibility`/`targetCompatibility` veya toolchains), CI imajları.
5. **Önce derle:** Eski JDK hedefiyle değil, **yeni JDK ile derleyip** uyarı/hataları topla. `jdeprscan` ve `jdeps` araçlarıyla kaldırılan/deprecated API kullanımını tara.
6. **Kaldırılan API'leri değiştir:** JAXB, JAX-WS, CORBA gibi modüller için harici bağımlılık ekle veya alternatif kullan.
7. **Testleri koş:** Birim + entegrasyon + (mümkünse) yük/performans testleri. Charset'e duyarlı testlere (özellikle Türkçe karakterli dosya I/O) dikkat.
8. **Çalışma zamanı bayraklarını gözden geçir:** Eski GC bayrakları, `--illegal-access` gibi kaldırılmış bayraklar.
9. **Aşamalı yayına al (canary/staging):** Önce test/staging, sonra kademeli üretim.
10. **İzle:** GC davranışı, bellek, gecikme, hata oranları. Yeni GC varsayılanları metrikleri değiştirebilir.

---

## 5. Karar Özeti (Hızlı Bakış)

| Bulunduğun yer | Önerilen hedef | Aciliyet | Ana zorluk |
|----------------|----------------|----------|------------|
| Java 8 | 17 (veya 21) | **Yüksek** (destek/güvenlik) | Kaldırılan modüller + güçlü kapsülleme |
| Java 11 | 17 veya 21 | Orta-Yüksek | Güçlü kapsülleme, kütüphane sürümleri |
| Java 17 | 21 veya 25 | Orta | Düşük risk, ağırlıklı kazanım |
| Java 21 | 25 | Düşük | Düşük risk; uzun destek kazanımı |

**Altın kural:** Geçiş, "büyük patlama" yerine **kademeli, test odaklı ve LTS'ten LTS'e** yapılmalıdır. Her adımda önce derle, sonra test et, sonra bağımlılık güncelle ve aşamalı yayına al. Detaylı özellik açıklamaları için ilgili sürüm klasörlerindeki `README.md` dosyalarına, özellik-sürüm eşleşmeleri için [`SURUM-KARSILASTIRMA.md`](SURUM-KARSILASTIRMA.md) dosyasına bakın.
