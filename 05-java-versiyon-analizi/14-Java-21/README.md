# Java 21 (Eylül 2023) — LTS — YENİ KURUMSAL STANDART

## Sürüm Bilgileri

| Özellik | Değer |
|---|---|
| Sürüm | Java 21 (JDK 21) |
| Çıkış Tarihi | 19 Eylül 2023 |
| Destek Türü | **LTS (Long-Term Support) — UZUN VADELİ DESTEK** |
| LTS mı? | **EVET.** Java 8, 11, 17'den sonraki en yeni LTS. |
| Destek Süresi | Tipik olarak 8+ yıl (sağlayıcıya göre genişletilmiş destek dahil) |
| Önceki LTS | Java 17 (Eylül 2021) |
| Sonraki LTS | Java 25 (Eylül 2025) |
| JEP Sayısı | 15 JEP |

---

## NEDEN JAVA 21 YENİ KURUMSAL STANDART? (GÜÇLÜ VURGU)

Java 21, sadece bir başka sürüm değil; **2024-2028 arası kurumsal Java'nın yeni temel hattıdır.** İşte nedenleri:

### 1. LTS = Uzun Vadeli Destek
Kurumlar üretim sistemlerini ara (feature) sürümlere kuramaz; çünkü onların desteği yalnızca 6 ay sürer. Java 21 LTS, yıllarca güvenlik yaması ve destek alacaktır. **Java 8 ve Java 11'den göç eden devasa kod tabanlarının hedefi artık Java 21'dir** (Java 17 yerine bile doğrudan 21 tercih edilmektedir, çünkü 21 daha fazla olgun özellik içerir).

### 2. Virtual Threads (Project Loom) KALICI
Bu, **son 20 yılın en büyük JVM eşzamanlılık devrimidir.** Yıllardır preview'de olan virtual threads ilk kez Java 21'de **kalıcı** oldu. Bu, sunucu tarafı Java'yı yeniden tanımlar: basit, bloklayıcı kod yazarak milyonlarca eşzamanlı bağlantıyı kaldırabilirsiniz. Reaktif programlamanın (Reactor, RxJava) karmaşıklığına çoğu senaryoda artık gerek kalmaz.

### 3. Modern Dil Özellikleri Olgunlaştı
Pattern Matching for switch ve Record Patterns kalıcı oldu. Bu ikisi birlikte, Java'ya fonksiyonel ve veri-odaklı (data-oriented) programlamanın gücünü kazandırır.

### 4. Sequenced Collections
20+ yıllık bir API boşluğu (koleksiyonun ilk/son elemanına tutarlı erişim) nihayet kapandı.

### 5. Generational ZGC
Çok düşük gecikmeli (sub-millisecond pause) çöp toplayıcı, nesil-bazlı (generational) hale gelerek verimlilik kazandı. Büyük heap'li, gecikmeye duyarlı sistemler için ideal.

> **Özet:** Java 17'den Java 21'e geçiş, modern bir Java ekibi için artık bir tercih değil, bir hedeftir. Virtual Threads tek başına bu geçişi haklı çıkaracak kadar değerlidir.

---

## Genel Bakış — Java 21 Özellikleri

### KALICI (Final) Özellikler
1. **JEP 444 — Virtual Threads** — Project Loom. **EN ÖNEMLİSİ.**
2. **JEP 441 — Pattern Matching for switch** — Desen eşleştirme kalıcı.
3. **JEP 440 — Record Patterns** — İç içe yıkım (destructuring) kalıcı.
4. **JEP 431 — Sequenced Collections** — Sıralı koleksiyon arayüzleri.
5. **JEP 439 — Generational ZGC** — Nesil-bazlı düşük gecikmeli GC.

### PREVIEW / INCUBATOR Özellikler
6. **JEP 430 — String Templates** (1. Preview) — Güvenli string interpolasyonu.
7. **JEP 443 — Unnamed Patterns and Variables** (Preview) — `_` ile isimsiz desen/değişken.
8. **JEP 445 — Unnamed Classes and Instance Main Methods** (Preview) — Yeni başlayanlar için basit `main`.
9. **JEP 453 — Structured Concurrency** (Preview).
10. **JEP 446 — Scoped Values** (Preview).

---

## Özellik Detayları

### 1. Virtual Threads (JEP 444) — KALICI — Project Loom — EN ÖNEMLİSİ

> İlgili dosya: [`VirtualThreadsKalici.java`](./VirtualThreadsKalici.java)

#### NEDİR?
Virtual thread, JVM tarafından yönetilen çok hafif bir iş parçacığıdır. Geleneksel **platform thread**'in (OS thread'inin 1:1 sarmalayıcısı) aksine, virtual thread'ler az sayıda OS thread'i (**taşıyıcı / carrier thread**) üzerinde çoklanır. **Milyonlarca** virtual thread tek bir JVM'de var olabilir.

#### NEDEN GELDİ? (DERİNLEMESİNE)

**Problem 1 — Platform thread'ler pahalıdır:**
Her platform thread bir OS thread'idir; ~1 MB stack tüketir ve OS tarafından zamanlanır. Bir sunucu pratikte birkaç bin platform thread'i aşamaz. 10.000 eşzamanlı kullanıcı = imkansız.

**Problem 2 — Thread-per-request modeli ölçeklenmiyor:**
Java sunucularının klasik ve en okunabilir modeli "her istek için bir thread" idi. Kod basit ve senkrondur:
```
String veri = veritabaniSorgula();   // blokla
String sonuc = baskaServisCagir(veri); // blokla
yanitDon(sonuc);
```
Ama her bloklamada pahalı OS thread'i boşta bekler. Birkaç bin eşzamanlı istekte thread'ler tükenir; sistem yeni istek alamaz. Oysa CPU boştadır — darboğaz thread sayısıdır, işlemci değil.

**Problem 3 — Reaktif programlama karmaşıktır:**
Sektör bu duvarı aşmak için reaktif/asenkron modellere (CompletableFuture, Reactor, RxJava) yöneldi. Bunlar az sayıda thread'le çok iş yapar AMA:
- Kod callback/operatör zincirlerine döner; okunması zordur.
- Stack trace'ler anlamsızlaşır; hata ayıklama kabusa döner.
- `try/catch`, döngü, debugger gibi temel araçlar düzgün çalışmaz.
- "Renk problemi" (function coloring): asenkron fonksiyonlar her şeyi asenkron olmaya zorlar.

**Virtual Threads bu ikilemi ortadan kaldırır:** Basit, senkron, bloklayıcı kod yazarsınız (okunabilirlik) ama reaktif sistemler kadar ölçeklenir (performans). İki dünyanın en iyisi.

#### NASIL ÇALIŞIR? (mount / unmount, carrier thread)

1. Bir virtual thread çalışmak için bir **carrier thread**'e (platform thread) **bindirilir (mount)**.
2. Virtual thread bloklayıcı bir işleme girerse (örn. `socket.read()`, `Thread.sleep`, JDBC çağrısı), JVM virtual thread'in durumunu (stack) heap'e kaydeder ve onu carrier'dan **söker (unmount)**.
3. Boşalan carrier thread hemen **başka bir** virtual thread'i çalıştırmaya başlar.
4. Bloklayıcı işlem tamamlandığında, virtual thread tekrar (herhangi bir) carrier'a **bindirilir** ve kaldığı yerden devam eder.

Sonuç: OS thread'leri **asla boşta beklemez**; her zaman gerçek iş yaparlar. Bloklanan iş, ucuz heap belleğinde "park" eder. Bu yüzden birkaç düzine carrier thread, milyonlarca virtual thread'e hizmet edebilir.

> **Pinning (sabitlenme) uyarısı:** `synchronized` blok içinde bloklanan veya yerel (native) çağrı yapan virtual thread carrier'dan sökülemez ("pinned" olur). Bu, ölçeklenmeyi bozabilir. Çözüm: `synchronized` yerine `ReentrantLock` kullanın. (Java 24 ile pinning büyük ölçüde giderildi.)

#### GERÇEK KAZANIM
- Platform thread: ~birkaç bin eşzamanlı bağlantı tavanı.
- Virtual thread: **milyonlarca** eşzamanlı bağlantı, aynı donanımda.
- Kod ise **eskisi gibi basit ve senkron** kalır.

#### ESKİ vs YENİ
```java
// ESKİ — sabit boyutlu havuz, thread sayısı darboğaz
ExecutorService es = Executors.newFixedThreadPool(200);
// 201. eşzamanlı uzun istek kuyruğa girer

// YENİ (Java 21, KALICI) — her görev için bir virtual thread
try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
    es.submit(() -> { ... });  // milyonlarca olabilir
}
```

#### GERÇEK HAYAT ÖRNEĞİ — Web Sunucu / Mikroservis
Bir mikroservis her isteği işlerken 3 ayrı servise (kullanıcı, envanter, fiyat) bloklayıcı HTTP çağrısı yapar; her çağrı ~100 ms sürer. Yani her istek ~300 ms'nin çoğunu **beklemeyle** geçirir.
- **Platform thread (havuz 200):** Aynı anda en fazla ~200 istek; daha fazlası kuyrukta bekler. Bekleme sırasında 200 pahalı OS thread'i boşta durur. Throughput düşük.
- **Virtual thread:** 50.000 istek aynı anda gelebilir; her biri kendi virtual thread'inde bloklanır, OS thread'leri bu sırada başka istekleri işler. Aynı donanımda çok daha yüksek throughput.

> Spring Boot 3.2+, Tomcat, Helidon, Quarkus gibi framework'ler bir bayrakla virtual thread'leri etkinleştirir. Çoğu zaman kodu değiştirmeden ölçeklenirsiniz.

#### STRUCTURED CONCURRENCY İLE İLİŞKİSİ
Virtual thread'ler ucuz olduğundan, bir isteğin alt görevlerini (yukarıdaki 3 servis çağrısı) ayrı virtual thread'lerde paralel çalıştırmak doğaldır. **Structured Concurrency** (JEP 453) bu alt görevleri tek bir iş birimi gibi yönetir: birlikte başlar, biri patlarsa diğerleri otomatik iptal olur. Bu üçlü (virtual threads + structured concurrency + scoped values) modern Java sunucu mimarisinin temelidir.

#### AVANTAJ / DEZAVANTAJ / RİSK
- **Avantaj:** Basit kod + devasa ölçeklenme; reaktif karmaşıklığa veda.
- **Dezavantaj/Risk:** CPU-yoğun (hesaplama ağırlıklı) işler için fayda sağlamaz — onlar zaten thread'i meşgul eder. `synchronized` ile pinning riski. Havuzlama (pooling) ANTI-PATTERN'dir: virtual thread'leri havuzlamayın, her görev için yenisini açın.

#### NERELERDE KULLANILMALI
Virtual thread'lerin asıl kazancı **bloklayan ama I/O ağırlıklı, yüksek eşzamanlılık** senaryolarındadır:

- **HTTP/REST sunucuları, istek başına thread (thread-per-request) modeli:** Her gelen isteği kendi virtual thread'inde işle. Tipik kurumsal uygulamalar (örn. mikroservisler) zamanlarının çoğunu DB ve harici servisleri beklemekle geçirir — tam hedef kitle.
- **Veritabanı çağrıları ve servisler arası çağrılar:** JDBC, REST/gRPC istemcileri, mesaj kuyruğu tüketicileri — yani thread'in çoğu zaman *beklediği* her şey.
- **Mevcut senkron/bloklayan kodu sadeleştirme:** Reactive (WebFlux, RxJava) ya da callback tabanlı karmaşık kodu **düz senkron koda** çevirip yine yüksek ölçek almak istediğinde. Çoğu durumda reactive'in karmaşıklığına gerek kalmaz; stack trace'ler doğal kalır, debugger çalışır.
- **"Fan-out" / paralel toplama:** Bir istek içinde aynı anda çok sayıda dış çağrı yapıp sonuçları birleştirmek — `StructuredTaskScope` (Structured Concurrency) ile çok temiz olur.

#### NERELERDE KULLANIMDAN KAÇINILMALI
- **CPU-bound (hesaplama ağırlıklı) işler:** Şifreleme, görüntü işleme, ağır matematik, sıkıştırma... Burada thread zaten *beklemediği* için unmount avantajı devreye girmez. Çekirdek sayısı kadar thread'i olan **sınırlı (bounded) bir platform thread havuzu** (`ForkJoinPool`, `Executors.newFixedThreadPool(n)`) daha doğrudur.
- **Eşzamanlılığı sınırlamak için "havuz" arayışı:** Virtual thread'leri **havuzlamayın** (anti-pattern). Aşağı akıştaki bir servisi korumak için eşzamanlılığı kısmak istiyorsan virtual thread havuzu yerine **`Semaphore` (örn. 20 izin)** kullan; her istek yine kendi virtual thread'inde koşsun.
- **Çok ağır `ThreadLocal` kullanımı:** Milyonlarca thread × thread başına büyük `ThreadLocal` verisi = ciddi bellek baskısı. Bağlam taşımak gerekiyorsa **Scoped Values** (JEP 446) daha uygun alternatiftir.
- **Çok kısa ömürlü, hiç bloklamayan minik görevler:** Görev hiç I/O yapmıyor ve mikrosaniyeler sürüyorsa, virtual thread kurulum maliyeti kazancı geçebilir; basit doğrudan çağrı daha iyidir.

#### KULLANIRKEN NELERE DİKKAT EDİLMELİ
- **Pinning (sabitlenme):** Bazı durumlarda virtual thread carrier'ından ayrılamaz; carrier da onunla birlikte bloke kalır. Etkin paralellik düşer, açlık/deadlock bile olabilir.
    - **Java 21–23'te** `synchronized` bir blok/metot içinde bloklayan bir işlem yapmak en yaygın pinning sebebiydi. **Java 21 kullanıyorsan bu hâlâ aktif bir konudur**; sıcak yollardaki I/O içeren `synchronized` bölümleri `ReentrantLock` ile değiştirmek o dönemin standart tavsiyesidir.
    - **Java 24 (JEP 491)** bunu kökten çözdü: monitör sahipliği artık carrier yerine doğrudan virtual thread üzerinden izlendiği için `synchronized` virtual thread'leri **pin etmiyor**. Java 24+'ta `synchronized`'ı `ReentrantLock`'a çevirmek için özel bir neden kalmadı.
    - **Java 24'te bile** **native (JNI)** metot veya **Foreign Function & Memory API** çağrıları sırasında bloklama hâlâ pinning yaratabilir (ayrıca sınıf başlatma gibi nadir durumlar).
    - **Teşhis:** Pinning olaylarını JFR `jdk.VirtualThreadPinned` olayıyla izle. (`-Djdk.tracePinnedThreads` seçeneği Java 24 ile kaldırıldı.)
- **Havuzlama yok:** Virtual thread "üret-kullan-at" mantığındadır; havuzlanmaz. `newVirtualThreadPerTaskExecutor()` kullan, eşzamanlılığı `Semaphore` ile sınırla.
- **Carrier havuzu boyutu:** Taşıyıcı platform thread sayısı varsayılan olarak çekirdek sayısı kadardır (`jdk.virtualThreadScheduler.parallelism` ile ayarlanır, üst sınır 256). CPU-bound veya pinning yapan işler bu havuzu tıkayabilir.
- **`ThreadLocal` yerine Scoped Values:** Bağlam (kullanıcı, trace id vb.) taşıyacaksan immutable ve daha hafif olan Scoped Values'ı değerlendir.
- **Spring Boot entegrasyonu:** Spring Boot **3.2+** ile `spring.threads.virtual.enabled=true` vererek web sunucusunu, `@Async` görevlerini ve uygun yerleri tek satırda virtual thread'e taşıyabilirsin (Java 21+ gerekir).
- **Senkronizasyon hâlâ gerekir:** Virtual thread'ler kodu "thread-safe" yapmaz. Paylaşılan duruma eşzamanlı erişim hâlâ uygun kilitleme/eşzamanlılık yapılarıyla korunmalıdır.
- **Sürüm farkına göre test:** Davranış Java 21 ile 24 arasında ince ama önemli farklar gösterir; kütüphane/sürücü uyumluluğunu (JDBC sürücüleri, HTTP istemcileri) test et, mümkünse Java 24+'a geç.

---

### 2. Pattern Matching for switch (JEP 441) — KALICI

> İlgili dosya: [`PatternMatchingSwitchKalici.java`](./PatternMatchingSwitchKalici.java)

#### NEDİR?
`switch` içinde tür desenleri (type patterns), guarded patterns (`when`), null işleme ve sealed hiyerarşilerle tamlık (exhaustiveness) kontrolü.

#### NEDEN GELDİ?
Uzun `if-else instanceof` zincirleri okunması zor ve hataya açıktı. Veri-odaklı kod (bir nesnenin türüne göre dallanma) için ifade gücü gerekiyordu.

#### ESKİ vs YENİ
```java
// ESKİ
String aciklama;
if (o instanceof Integer i && i > 0) aciklama = "pozitif: " + i;
else if (o instanceof String s) aciklama = "metin: " + s;
else if (o == null) aciklama = "bos";
else aciklama = "diger";

// YENİ (KALICI)
String aciklama = switch (o) {
    case null            -> "bos";
    case Integer i when i > 0 -> "pozitif: " + i;
    case Integer i       -> "pozitif olmayan: " + i;
    case String s        -> "metin: " + s;
    default              -> "diger";
};
```

#### EVRİM
Java 17 (1.) -> 18 (2.) -> 19 (3.) -> 20 (4.) -> **21 (KALICI)**

---

### 3. Record Patterns (JEP 440) — KALICI

> İlgili dosya: [`RecordPatternsKalici.java`](./RecordPatternsKalici.java)

#### NEDİR?
Record'ları eşleştirirken bileşenlerini doğrudan değişkenlere yıkma (destructuring) — özellikle **iç içe (nested)** yıkım.

#### ESKİ vs YENİ
```java
record Adres(String sehir, String ulke) {}
record Kullanici(String ad, Adres adres) {}

// ESKİ
if (o instanceof Kullanici k) {
    String sehir = k.adres().sehir();
    ...
}

// YENİ (KALICI) — iç içe yıkım
if (o instanceof Kullanici(String ad, Adres(String sehir, String ulke))) {
    // ad, sehir, ulke doğrudan kullanılabilir
}
```

#### EVRİM
Java 19 (1.) -> 20 (2.) -> **21 (KALICI)**

---

### 4. Sequenced Collections (JEP 431) — KALICI

> İlgili dosya: [`SequencedCollections.java`](./SequencedCollections.java)

#### NEDİR?
Üç yeni arayüz: `SequencedCollection`, `SequencedSet`, `SequencedMap`. Bunlar, tanımlı bir karşılaşma sırası (encounter order) olan koleksiyonlara **ilk/son elemana tutarlı erişim** ve **ters çevirme** yetenekleri kazandırır:
- `getFirst()`, `getLast()`
- `addFirst(e)`, `addLast(e)`
- `removeFirst()`, `removeLast()`
- `reversed()` — ters sıralı bir görünüm (view)

#### NEDEN GELDİ?
20+ yıllık bir tutarsızlık vardı:
- `List`'in ilk elemanı: `list.get(0)`
- `Deque`'in ilk elemanı: `deque.getFirst()`
- `LinkedHashSet`'in ilk elemanı: ??? — `iterator().next()` ile uğraşmak gerekiyordu
- Son eleman almak için `List`'te `list.get(list.size()-1)`, başka koleksiyonlarda başka yollar.

Sıralı tüm koleksiyonlar için **ortak ve tutarlı** bir API yoktu.

#### ESKİ vs YENİ
```java
// ESKİ — her tür için farklı, çirkin
String ilk = list.get(0);
String son = list.get(list.size() - 1);
String ilkSet = linkedHashSet.iterator().next(); // son eleman çok zor!

// YENİ (KALICI) — her sıralı koleksiyonda aynı
String ilk = list.getFirst();
String son = list.getLast();
List<String> ters = list.reversed();
```

#### GERÇEK HAYAT ÖRNEĞİ
Bir "son görüntülenen ürünler" listesi (`LinkedHashSet`, eklenme sırasını korur). En son ve en eski görüntülenen ürüne erişim artık tek satır: `set.getLast()` / `set.getFirst()`.

---

### 5. Generational ZGC (JEP 439) — KALICI

#### NEDİR?
ZGC (Z Garbage Collector), milisaniye altı duraklama (pause) süreleriyle bilinen düşük gecikmeli bir çöp toplayıcıdır. Java 21 ile **nesil-bazlı (generational)** hale geldi: nesneleri "genç" ve "yaşlı" nesillere ayırır.

#### NEDEN GELDİ?
"Çoğu nesne genç ölür" (weak generational hypothesis) gözlemine göre, genç nesilleri sık ve ucuza toplamak çok daha verimlidir. Eski (non-generational) ZGC bu avantajı kullanmıyordu.

#### NEREDE KOLAYLIK SAĞLAR?
Büyük heap'li (onlarca/yüzlerce GB), gecikmeye duyarlı sistemler (finansal işlem, gerçek zamanlı analitik, büyük web servisleri) için daha az CPU ve bellek ile aynı düşük gecikme.

#### KULLANIM
```bash
# Java 21'de Generational ZGC'yi etkinleştir
java -XX:+UseZGC -XX:+ZGenerational UygulamaSinifi
# (Java 23+ ile generational mod varsayılan oldu)
```

---

### 6. String Templates (JEP 430) — 1. PREVIEW

> İlgili dosya: [`StringTemplatesPreview.java`](./StringTemplatesPreview.java)

#### NEDİR?
Güvenli ve okunabilir string interpolasyonu. `STR."Merhaba \{isim}"` gibi gömülü ifadelerle string oluşturma. Sadece birleştirme değil; SQL/HTML gibi bağlamlarda **kaçış (escaping) ve doğrulama** yapabilen şablon işlemcileri (template processors) de sunar.

#### NEDEN GELDİ?
`+` ile birleştirme dağınık, `String.format` hataya açık (argüman sırası), ve hiçbiri **enjeksiyon güvenliği** sağlamıyordu (SQL injection, XSS).

#### ESKİ vs YENİ
```java
String isim = "Ayşe"; int yas = 30;

// ESKİ
String s1 = "Ad: " + isim + ", Yaş: " + yas;
String s2 = String.format("Ad: %s, Yaş: %d", isim, yas);

// YENİ (preview)
String s3 = STR."Ad: \{isim}, Yaş: \{yas}";
```

> **NOT:** String Templates Java 21 ve 22'de preview kaldı; Java 23'te **kaldırıldı (geri çekildi)** ve yeniden tasarlanmak üzere ertelendi. Bu yüzden üretimde kullanmayın; kavramsal olarak bilin.

#### EVRİM/RİSK
Java 21 (1. preview) -> 22 (2. preview) -> **23: tasarımdan KALDIRILDI.** İleride farklı bir biçimde dönmesi beklenir.

---

### 7. Unnamed Patterns and Variables (JEP 443) — PREVIEW

> İlgili dosya örnekleri: [`RecordPatternsKalici.java`](./RecordPatternsKalici.java) içinde de gösterilmiştir.

#### NEDİR?
`_` (alt çizgi) ile **kullanılmayacak** değişken ve desen bileşenlerini isimsiz bırakma. Niyeti açıkça belirtir: "bu değer var ama umurumda değil."

#### ESKİ vs YENİ
```java
// ESKİ — kullanılmayan değişken yine de isimlendirilmeli
catch (Exception e) { logla(); } // 'e' kullanılmıyor ama isim zorunlu
for (var ignored : liste) sayac++; // anlamsız 'ignored'

// YENİ (preview)
catch (Exception _) { logla(); }
for (var _ : liste) sayac++;
if (o instanceof Nokta(int x, int _)) {...} // y'yi umursamıyoruz
```

#### EVRİM
Java 21 (preview) -> **Java 22: KALICI (JEP 456).**

---

### 8. Unnamed Classes and Instance Main Methods (JEP 445) — PREVIEW

> İlgili dosya: [`UnnamedMainMethod.java`](./UnnamedMainMethod.java)

#### NEDİR?
Yeni başlayanların "Merhaba Dünya"yı yazmak için `public static void main(String[] args)` ve sınıf bildirimi gibi tüm seremoniyi öğrenmesine gerek kalmadan basit `main` yazabilmesi.

#### NEDEN GELDİ?
Java'nın öğrenme eğrisindeki ilk engel, basit bir programın bile çok sayıda kavram (sınıf, public, static, void, String[]) gerektirmesiydi. Bu, eğitimde caydırıcıydı.

#### ESKİ vs YENİ
```java
// ESKİ — tam seremoni
public class Merhaba {
    public static void main(String[] args) {
        System.out.println("Merhaba Dünya");
    }
}

// YENİ (preview) — sınıf gövdesi ve static gerekmez
void main() {
    System.out.println("Merhaba Dünya");
}
```

#### EVRİM
Java 21 (preview) -> 22, 23 (gelişen preview'ler) -> **Java 25: KALICI** (Compact Source Files / Instance Main Methods olarak).

---

### 9. Structured Concurrency (JEP 453) — PREVIEW

> İlgili dosya: [`StructuredConcurrency.java`](./StructuredConcurrency.java)

#### NEDİR?
Eşzamanlı alt görevleri tek bir iş birimi olarak yöneten `StructuredTaskScope`. Alt görevler birlikte başlar, birlikte tamamlanır/iptal olur. Hata yayılımı ve iptal otomatiktir.

#### NEDEN GELDİ?
Manuel `ExecutorService`/`Future` ile alt görev iptali, hata yayılımı ve sızıntı önleme zordu ("unstructured concurrency"). Virtual thread'lerle birlikte, paralel alt görevleri güvenle yönetmek için yapısal bir model gerekiyordu.

#### EVRİM
Java 19 (incubator) -> 20 (incubator) -> 21 (preview) -> ... olgunlaşmaya devam.

---

### 10. Scoped Values (JEP 446) — PREVIEW

#### NEDİR?
`ThreadLocal`'a değişmez, virtual thread dostu alternatif. (Detay: `13-Java-20/ScopedValuesNotlari.md`.)

#### EVRİM
Java 20 (incubator) -> **Java 21 (preview, `java.lang` paketine taşındı).**

---

## Geçiş Rehberi: Java 17 LTS -> Java 21 LTS

Çoğu kurum doğrudan bu sıçramayı yapar (17 -> 21). İşte özet:

| Konu | Etki | Aksiyon |
|---|---|---|
| Virtual Threads | KALICI, devrimsel | Framework bayrağıyla etkinleştir; `synchronized` -> `ReentrantLock` gözden geçir; virtual thread'leri havuzlama |
| Pattern Matching / Record Patterns | KALICI | `if-else instanceof` zincirlerini sadeleştir |
| Sequenced Collections | KALICI | `getFirst/getLast/reversed` ile kodu temizle |
| Generational ZGC | KALICI | Düşük gecikme isteyen sistemlerde `-XX:+UseZGC -XX:+ZGenerational` |
| String Templates | PREVIEW (sonra kaldırıldı) | Üretimde KULLANMA |
| Unnamed/Instance Main | PREVIEW | Sadece eğitim/deneme |
| Deprecation/Removal | Bazı eski API'ler kaldırıldı | `jdeprscan` ile tarayın |

### Avantaj / Dezavantaj / Risk (Genel)
- **Avantaj:** Uzun destek, devrimsel eşzamanlılık, modern dil, performanslı GC. Yeni kurumsal standart.
- **Risk:** Preview özelliklere (String Templates) bağlanmak; `synchronized` pinning; eski kütüphanelerin Java 21 uyumu.
- **Tavsiye:** Java 21'i yeni projeler için varsayılan hedef yapın. Mevcut Java 8/11/17 projelerini buraya taşımayı planlayın.

---

## Bu Klasördeki Çalışan Örnekler

| Dosya | Konu | Preview? |
|---|---|---|
| `VirtualThreadsKalici.java` | Platform vs virtual thread, binlerce eşzamanlı istek | Hayır (kalıcı) |
| `PatternMatchingSwitchKalici.java` | switch + tür/guarded/null desenleri | Hayır (kalıcı) |
| `RecordPatternsKalici.java` | İç içe record yıkımı + isimsiz desen | Hayır (kalıcı; `_` kısmı preview) |
| `SequencedCollections.java` | getFirst/getLast/reversed | Hayır (kalıcı) |
| `StringTemplatesPreview.java` | STR şablon işlemcisi | **Evet (preview)** |
| `UnnamedMainMethod.java` | Basit instance main | **Evet (preview)** |
| `StructuredConcurrency.java` | StructuredTaskScope | **Evet (preview)** |
