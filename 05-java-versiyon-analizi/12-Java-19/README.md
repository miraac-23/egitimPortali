# Java 19 (Eylül 2022)

## Sürüm Bilgileri

| Özellik | Değer |
|---|---|
| Sürüm | Java 19 (JDK 19) |
| Çıkış Tarihi | 20 Eylül 2022 |
| Destek Türü | **Feature Release** (kısa vadeli, LTS DEĞİL) |
| LTS mı? | Hayır |
| Tarihsel Önemi | **Project Loom (Virtual Threads) ilk kez preview olarak çıktı** — son 20 yılın en büyük JVM eşzamanlılık değişimidir |

> **Tarihsel Dönüm Noktası:** Java 19, ileride Java 21 LTS'in damga vuracağı **Virtual Threads** özelliğinin halka açık ilk preview'ini içerir. Bu yüzden Java 19, "geçici bir ara sürüm" olmasının çok ötesinde bir öneme sahiptir. Bu sürüm, "thread-per-request" modelinin ölçeklenme problemine getirilen devrimsel çözümün başlangıcıdır.

---

## Genel Bakış

Java 19, tamamı ileride Java 21 LTS'te olgunlaşacak özelliklerin bir "preview/incubator" galerisidir:

1. **JEP 425 — Virtual Threads** (1. Preview) — Project Loom. **EN ÖNEMLİSİ.**
2. **JEP 428 — Structured Concurrency** (1. Incubator) — eşzamanlı görevleri tek bir iş birimi gibi yönetme.
3. **JEP 427 — Pattern Matching for switch** (3. Preview).
4. **JEP 405 — Record Patterns** (1. Preview) — record'ları yıkımla (destructuring) eşleştirme.
5. **JEP 424 — Foreign Function & Memory API** (1. Preview).
6. **JEP 426 — Vector API** (4. Incubator).

---

## Özellik Detayları

### 1. Virtual Threads (JEP 425) — 1. PREVIEW — Project Loom

> İlgili dosya: [`VirtualThreadsPreview.java`](./VirtualThreadsPreview.java)

#### NEDİR?
Virtual Thread (sanal iş parçacığı), JVM tarafından yönetilen, son derece hafif bir thread türüdür. İşletim sisteminin (OS) ağır thread'leriyle 1:1 eşleşmez; bunun yerine az sayıda OS thread'i (taşıyıcı / carrier thread) üzerinde çoklanır (multiplex).

#### NEDEN GELDİ? (Derinlemesine)
Klasik Java thread'i (platform thread), bir OS thread'inin doğrudan sarmalayıcısıdır. Her biri yaklaşık **1 MB yığın (stack)** belleği tüketir ve OS kaynağı olduğundan sayıları sınırlıdır (tipik olarak birkaç bin).

Geleneksel sunucu mimarisi olan **"thread-per-request"** (her istek için bir thread) modelinde:
- 10.000 eşzamanlı bağlantı = 10.000 thread = ~10 GB sadece stack belleği. Pratikte imkansız.
- Thread'ler bir veritabanı veya HTTP çağrısında **bloklandığında** (blocking I/O), pahalı OS thread'i boşta bekler ama yine de kaynak tutar.

Bu duvarı aşmak için sektör **reaktif programlamaya** (Reactor, RxJava, CompletableFuture zincirleri) yöneldi. Ancak reaktif kod:
- Okunması ve hata ayıklaması zordur (callback/operatör zincirleri).
- Yığın izleri (stack trace) anlamsızlaşır.
- `try/catch`, döngüler, debugger gibi temel araçlarla uyumsuzdur.

Virtual Threads bu ikilemi çözer: **basit, senkron (blocking) kod yaz, ama reaktif ölçeklenebilirliği al.**

#### NASIL ÇALIŞIR?
Bir virtual thread bloklayıcı bir işleme (örn. `socket.read()`) girdiğinde, JVM onu taşıyıcı OS thread'inden **söker (unmount)** ve taşıyıcıyı başka bir virtual thread'e verir. İşlem tamamlandığında virtual thread tekrar bir taşıyıcıya **bindirilir (mount)**. Böylece OS thread'i hiçbir zaman boşta beklemez.

#### GERÇEK HAYAT ÖRNEĞİ
Bir mikroservis, gelen her isteği işlerken 3 farklı servise HTTP çağrısı yapıyor. Platform thread'lerle 200 eşzamanlı isteği zar zor kaldıran sunucu, virtual thread'lerle **on binlerce** isteği aynı donanımda kaldırabilir.

> Java 21'deki KALICI ve çok daha derin anlatım için: `14-Java-21/VirtualThreadsKalici.java`

---

### 2. Structured Concurrency (JEP 428) — 1. INCUBATOR

#### NEDİR?
Bir görevin alt görevlerini (eşzamanlı çalışan) tek bir mantıksal iş birimi olarak ele alma modeli. `StructuredTaskScope` ile alt görevler birlikte başlar, birlikte tamamlanır veya birlikte iptal edilir.

#### NEDEN GELDİ?
Manuel `ExecutorService` + `Future` kullanımında, alt görevlerden biri başarısız olursa diğerlerini iptal etmek, sızıntıları (leak) önlemek, hata yaymak çok zahmetlidir. Kontrol akışı dağılır ("unstructured").

#### ESKİ vs YENİ
```java
// ESKİ — manuel, sızıntıya açık
ExecutorService es = Executors.newFixedThreadPool(2);
Future<String> f1 = es.submit(() -> kullaniciGetir());
Future<Integer> f2 = es.submit(() -> siparisSayisiGetir());
String k = f1.get(); int s = f2.get(); // biri patlarsa diğeri sızar

// YENİ (incubator/preview) — yapısal, otomatik iptal
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var k = scope.fork(() -> kullaniciGetir());
    var s = scope.fork(() -> siparisSayisiGetir());
    scope.join().throwIfFailed();
    // ikisi de başarılı; biri patlarsa diğeri otomatik iptal
}
```

> Java 21'deki gelişmiş örnek: `14-Java-21/StructuredConcurrency.java`

---

### 3. Record Patterns (JEP 405) — 1. PREVIEW

> İlgili dosya: [`RecordPatternsPreview.java`](./RecordPatternsPreview.java)

#### NEDİR?
Bir record'u `instanceof` veya `switch` içinde eşleştirirken bileşenlerini doğrudan değişkenlere "yıkma" (destructuring) imkanı.

#### NEDEN GELDİ?
Pattern matching ile bir nesnenin türünü öğrenebiliyorduk ama sonra alanlarına tek tek erişmemiz gerekiyordu. Record patterns, türü eşleştirir ve aynı anda alanları çıkarır.

#### ESKİ vs YENİ
```java
record Nokta(int x, int y) {}

// ESKİ
if (nesne instanceof Nokta n) {
    int x = n.x();
    int y = n.y();
}

// YENİ (preview) — yıkımla
if (nesne instanceof Nokta(int x, int y)) {
    // x ve y doğrudan kullanılabilir
}
```

#### EVRİM
- **Java 19: 1. preview (JEP 405)**
- Java 20: 2. preview (JEP 432)
- **Java 21: KALICI (JEP 440)**

> Java 21'deki iç içe yıkım örneği: `14-Java-21/RecordPatternsKalici.java`

---

### 4. Pattern Matching for switch (JEP 427) — 3. PREVIEW
Java 17 (1.), Java 18 (2.) sonrası üçüncü preview. Bu sürümde tamlık (exhaustiveness) ve null işleme rafine edildi. Kalıcı hali Java 21.

### 5. Foreign Function & Memory API (JEP 424) — 1. PREVIEW
Incubator aşamasından preview'e terfi etti. Kalıcı hali Java 22.

### 6. Vector API (JEP 426) — 4. INCUBATOR
SIMD hızlandırma; hâlâ incubator.

---

## Geçiş Rehberi: Java 18 -> Java 19'da Ne Değişti?

| Konu | Etki | Aksiyon |
|---|---|---|
| Virtual Threads | Preview — devrimsel ama henüz deneysel | `--enable-preview` ile DENEYIN, üretime almayın |
| Structured Concurrency | Incubator | API değişebilir; sadece deneme |
| Record Patterns | Preview | Java 21'de kalıcı olacak; öğrenin |
| Pattern Matching switch | 3. preview | Olgunlaşıyor |

### Genel Tavsiye
Java 19'un asıl değeri: **Virtual Threads ve Record Patterns'i erkenden öğrenmek.** Bu özellikler Java 21 LTS'te kalıcı olacağı için, ekiplerin şimdiden tanışması büyük avantaj sağlar. Üretim hedefi olarak Java 19 yerine doğrudan Java 21 LTS planlanmalıdır.
