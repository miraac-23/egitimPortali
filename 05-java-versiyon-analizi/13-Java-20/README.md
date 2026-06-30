# Java 20 (Mart 2023)

## Sürüm Bilgileri

| Özellik | Değer |
|---|---|
| Sürüm | Java 20 (JDK 20) |
| Çıkış Tarihi | 21 Mart 2023 |
| Destek Türü | **Feature Release** (kısa vadeli, LTS DEĞİL) |
| LTS mı? | Hayır. Bir sonraki sürüm olan **Java 21 LTS**'in hemen öncesidir. |
| Rolü | Java 21 LTS'e giden son "cilalama" sürümü — özellikler son preview turlarındadır |

> **Java 20'nin Rolü:** Java 20, neredeyse tamamen Java 21 LTS'in provası gibidir. İçindeki özelliklerin hiçbiri kalıcı değildir; ancak hepsi **son preview/incubator turundadır** ve 6 ay sonra Java 21'de kalıcı (veya bir sonraki preview) hale gelecektir. Bu sürümün asıl değeri, ekiplerin Java 21 öncesinde API'leri son haline çok yakın halleriyle test edebilmesidir.

---

## Genel Bakış

Java 20'deki tüm öne çıkan özellikler "evrim halindeki" özelliklerdir:

1. **JEP 436 — Virtual Threads** (2. Preview) — Project Loom olgunlaşıyor.
2. **JEP 429 — Scoped Values** (1. Incubator) — `ThreadLocal`'a modern, değişmez alternatif.
3. **JEP 432 — Record Patterns** (2. Preview).
4. **JEP 433 — Pattern Matching for switch** (4. Preview).
5. **JEP 434 — Foreign Function & Memory API** (2. Preview).
6. **JEP 437 — Structured Concurrency** (2. Incubator).
7. **JEP 438 — Vector API** (5. Incubator).

---

## Özellik Detayları

### 1. Scoped Values (JEP 429) — 1. INCUBATOR

> Detaylı doküman: [`ScopedValuesNotlari.md`](./ScopedValuesNotlari.md)

#### NEDİR?
`ThreadLocal`'ın modern, değişmez (immutable) ve virtual thread dostu halefi. Bir değeri, dinamik bir kapsam (scope) boyunca, açıkça parametre olarak geçirmeden alt çağrılara güvenli biçimde paylaştırmanın yolu.

#### NEDEN GELDİ?
`ThreadLocal`'ın bilinen sorunları:
- **Değiştirilebilir (mutable):** Kim, ne zaman değiştirdi belli olmaz.
- **Sızıntı riski:** `remove()` çağrılmazsa bellek sızıntısı; thread havuzlarında eski değerler kalır.
- **Kalıtım maliyeti:** `InheritableThreadLocal` ile alt thread'lere kopyalanması pahalı.
- **Virtual thread'lerle uyumsuz:** Milyonlarca virtual thread varken her birinde `ThreadLocal` tutmak ölçeklenmez.

Scoped Values; değişmez, sınırlandırılmış ömürlü (scoped) ve virtual thread'lerle ucuza çalışan bir alternatiftir.

#### ESKİ vs YENİ
```java
// ESKİ — ThreadLocal (mutable, remove unutulursa sızar)
static final ThreadLocal<Kullanici> AKTIF = new ThreadLocal<>();
AKTIF.set(kullanici);
try { isYap(); } finally { AKTIF.remove(); } // remove unutmak yaygın hata

// YENİ (incubator/preview) — ScopedValue (immutable, otomatik kapsam)
static final ScopedValue<Kullanici> AKTIF = ScopedValue.newInstance();
ScopedValue.where(AKTIF, kullanici).run(() -> isYap());
// kapsam bittiğinde değer otomatik kalkar, sızıntı yok
```

#### EVRİM
- **Java 20: 1. incubator (JEP 429)**
- Java 21: 1. preview (JEP 446)
- Java 22+: olgunlaşmaya devam

---

### 2. Record Patterns (JEP 432) — 2. PREVIEW

> İlgili dosya: [`RecordPatternsGelismis.java`](./RecordPatternsGelismis.java)

#### NEDİR?
Java 19'da gelen record yıkımının (destructuring) 2. preview'i. Bu turda `var` ile tür çıkarımı, generic record desteği ve `for` döngülerindeki kullanım gibi konular netleşti.

#### EVRİM
- Java 19: 1. preview
- **Java 20: 2. preview (JEP 432)**
- **Java 21: KALICI (JEP 440)**

#### ESKİ vs YENİ (switch ile birlikte)
```java
sealed interface Sekil permits Daire, Dikdortgen {}
record Daire(double r) implements Sekil {}
record Dikdortgen(double en, double boy) implements Sekil {}

// YENİ (preview) — switch + record pattern yıkımı
double alan = switch (sekil) {
    case Daire(double r)            -> Math.PI * r * r;
    case Dikdortgen(double e, double b) -> e * b;
};
```

> Java 21'deki iç içe ve kalıcı örnek: `14-Java-21/RecordPatternsKalici.java`

---

### 3. Virtual Threads (JEP 436) — 2. PREVIEW

#### NEDİR?
Java 19'daki 1. preview'in devamı. Bu turda küçük API ayarlamaları yapıldı (örn. `Thread.Builder`). Konsept aynıdır: hafif, JVM yönetimli, milyonlarca olabilen thread'ler.

> Derinlemesine anlatım ve kalıcı örnek: `14-Java-21/VirtualThreadsKalici.java`

#### EVRİM
- Java 19: 1. preview
- **Java 20: 2. preview**
- **Java 21: KALICI (JEP 444)**

---

### 4. Pattern Matching for switch (JEP 433) — 4. PREVIEW
Dördüncü ve son preview turu. Bu turda tamlık (exhaustiveness) kuralları ve desen sözdizimi son haline geldi. Kalıcı hali Java 21 (JEP 441).

### 5. Structured Concurrency (JEP 437) — 2. INCUBATOR
2. incubator turu. `StructuredTaskScope` API'si rafine edildi. Kalıcı yolculuk Java 21'de preview ile devam etti.

### 6. Foreign Function & Memory API (JEP 434) — 2. PREVIEW
2. preview. Kalıcı hali Java 22.

### 7. Vector API (JEP 438) — 5. INCUBATOR
Beşinci incubator turu; SIMD hızlandırma.

---

## Geçiş Rehberi: Java 19 -> Java 20'de Ne Değişti?

| Konu | Etki | Aksiyon |
|---|---|---|
| Virtual Threads | 2. preview, API olgunlaştı | Java 21 öncesi son testler için ideal |
| Record Patterns | 2. preview | Sözdizimi neredeyse kesinleşti |
| Pattern Matching switch | 4. (son) preview | Java 21'de kalıcı olacak |
| Scoped Values | Yeni (incubator) | ThreadLocal'dan geçişi planlayın |

### Genel Tavsiye
Java 20, **doğrudan Java 21 LTS'e geçişin son provası** olarak görülmelidir. Bu sürümde test edilen tüm preview API'ler, Java 21'de ya kalıcı olmuştur ya da son haline çok yaklaşmıştır. Üretim hedefiniz Java 20 değil, **Java 21 LTS** olmalıdır.
