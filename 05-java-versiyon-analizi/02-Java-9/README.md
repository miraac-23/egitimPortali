# Java 9 (Eylül 2017) — Detaylı Türkçe Analiz

## Sürüm Bilgileri

| Bilgi | Değer |
|-------|-------|
| Sürüm | Java SE 9 (JDK 9) |
| Çıkış Tarihi | 21 Eylül 2017 |
| LTS mi? | **HAYIR** (LTS değildir) |
| Destek Durumu | **SONA ERDİ** — Java 9 kısa ömürlü bir "feature release" idi. Genel destek Mart 2018'de Java 10 çıkınca bitti. Üretim ortamlarında kullanılmamalıdır. |
| Bir Sonraki LTS | Java 11 (Eylül 2018) |
| Kod Adı / En Önemli Proje | **Project Jigsaw** (Modül Sistemi / JPMS) |

> NOT: Java 9, Java tarihindeki en büyük mimari değişikliklerden birini getirdi: **Modül Sistemi (JPMS)**. Bu yüzden "küçük" bir sürüm olmasına rağmen, geçiş açısından en riskli sürümlerden biridir. Java 8'den Java 9'a geçiş, basit bir JDK güncellemesi değil; çoğu zaman bir **uyumluluk projesi** olmuştur.

---

## İçindekiler ve İlgili Dosyalar

| Özellik | Dosya |
|---------|-------|
| Modül Sistemi (JPMS / Project Jigsaw) | [`ModulSistemiOrnek.java`](./ModulSistemiOrnek.java) |
| Collection Factory metotları (`List.of`, `Set.of`, `Map.of`) | [`CollectionFactoryOrnek.java`](./CollectionFactoryOrnek.java) |
| Stream geliştirmeleri (`takeWhile`, `dropWhile`, `ofNullable`, `iterate`) | [`StreamGelistirmeleri.java`](./StreamGelistirmeleri.java) |
| Optional geliştirmeleri (`ifPresentOrElse`, `or`, `stream`) | [`OptionalGelistirmeleri.java`](./OptionalGelistirmeleri.java) |
| Private interface metotları | [`PrivateInterfaceMetot.java`](./PrivateInterfaceMetot.java) |
| JShell (REPL) | [`JShellNotlari.md`](./JShellNotlari.md) |

---

## 1) Modül Sistemi (JPMS — Java Platform Module System / Project Jigsaw)

> Detaylı kod ve `module-info.java` örneği için: [`ModulSistemiOrnek.java`](./ModulSistemiOrnek.java)

### NEDİR?
Java 9'a kadar kodu organize etmenin en üst birimi **JAR dosyasıydı**. JAR'lar aslında sadece sıkıştırılmış `.class` dosyalarıydı; içlerinde "ben hangi paketleri dışarıya açıyorum, hangi başka kütüphanelere bağımlıyım" bilgisi **yoktu**. Modül sistemi, paketlerin üzerine yeni bir katman ekler: **modül**. Bir modül, `module-info.java` adlı özel bir dosya ile kendini tanımlar:
- Hangi paketleri dışarıya açtığını (`exports`),
- Hangi modüllere bağımlı olduğunu (`requires`),
- Hangi servisleri kullandığını/sağladığını (`uses` / `provides`).

### NEDEN GELDİ? (Hangi problem?)
1. **JAR Hell (JAR Cehennemi):** Classpath üzerinde aynı sınıfın iki farklı sürümü bulunabiliyor, hangisinin yükleneceği belirsizdi. Eksik bir bağımlılık ancak **çalışma anında** `NoClassDefFoundError` ile fark ediliyordu.
2. **Güçlü kapsülleme (encapsulation) eksikliği:** `public` olan her şey classpath'teki herkese açıktı. `sun.misc.Unsafe`, `com.sun.*` gibi **dahili (internal) API'ler** herkes tarafından kullanılıyor, JDK ekibi de bunları değiştiremiyordu.
3. **Devasa monolitik JRE:** `rt.jar` tek parça, ~60 MB civarında bir dosyaydı. Küçük bir uygulama için bile tüm JDK gerekiyordu. IoT / küçük cihazlar için bu fazlaydı.

### NE İŞE YARAR?
- **Güvenilir konfigürasyon (reliable configuration):** Eksik modül **başlangıçta** tespit edilir; çalışma anında patlamaz.
- **Güçlü kapsülleme:** Sadece `exports` ile açılan paketler erişilebilir. `public` olsa bile dışarı açılmamış paket görünmez.
- **Ölçeklenebilir platform:** JDK kendisi de ~95 modüle bölündü (`java.base`, `java.sql`, `java.xml` ...). `jlink` aracı ile sadece ihtiyaç duyulan modülleri içeren küçük, özel bir runtime üretilebilir.

### NEREDE KOLAYLIK SAĞLAR?
- Büyük, çok katmanlı kurumsal uygulamalarda mimari sınırların **derleyici tarafından zorlanması**.
- Mikroservis / container imajlarının küçültülmesi (`jlink` ile 30-40 MB'lık runtime).
- Kütüphane geliştiricilerinin "iç" paketlerini gizleyip yalnızca API'yi açabilmesi.

### ESKİ vs YENİ

ESKİ (Java 8 — classpath):
```
javac -cp lib/*.jar src/...
java  -cp app.jar:lib/*.jar com.example.Main
# Tüm public sınıflar herkese açık. Eksik JAR ancak runtime'da patlar.
```

YENİ (Java 9 — modulepath):
```java
// module-info.java
module com.banka.cekirdek {
    requires java.sql;            // bağımlılık açıkça belirtilir
    exports com.banka.cekirdek.api;   // sadece bu paket dışarı açık
    // com.banka.cekirdek.internal -> hiç exports edilmedi => GİZLİ
}
```
```
javac -d out --module-source-path src $(find src -name "*.java")
java  --module-path out -m com.banka.cekirdek/com.banka.cekirdek.Main
```

### GERÇEK HAYAT ÖRNEĞİ
Bir bankacılık uygulamasında `internal` paketinde hassas hesaplama mantığı var. Java 8'de bu paket `public` sınıflar içerdiği için başka takımlar (hatta dışarıdan kütüphaneler) doğrudan ona erişip kendi mantıklarını gömebiliyordu. JPMS ile `internal` paketi `exports` edilmediğinden, derleyici bu erişimi **derleme anında reddeder**. Mimari karar, dokümantasyon ricası olmaktan çıkıp **derleyici garantisine** dönüşür.

---

## 2) Collection Factory Metotları

> Kod: [`CollectionFactoryOrnek.java`](./CollectionFactoryOrnek.java)

### NEDİR?
`List.of(...)`, `Set.of(...)`, `Map.of(...)`, `Map.ofEntries(...)` — küçük, **değiştirilemez (immutable)** koleksiyonları tek satırda oluşturan statik fabrika metotları.

### NEDEN GELDİ?
Java 8'de sabit bir liste oluşturmak külfetliydi ve sonuçların çoğu **değiştirilebilir** kalıyordu (hata kaynağı). "Double-brace initialization" gibi kötü idiomlar gizli iç sınıf ve bellek sızıntısı üretiyordu.

### NE İŞE YARAR / NEREDE KOLAYLIK?
Test verisi, sabit konfigürasyon, lookup tabloları gibi yerlerde kısa ve güvenli (değiştirilemez) koleksiyon üretir.

### ESKİ vs YENİ
```java
// ESKİ
List<String> l = Collections.unmodifiableList(Arrays.asList("a","b","c"));
Map<String,Integer> m = new HashMap<>(); m.put("a",1); m.put("b",2);

// YENİ
List<String> l = List.of("a","b","c");
Map<String,Integer> m = Map.of("a",1,"b",2);
```

### GERÇEK HAYAT ÖRNEĞİ
Bir HTTP cevabında dönecek sabit hata kodları tablosu. `Map.of(...)` ile tek satırda, yanlışlıkla değiştirilemeyecek şekilde tanımlanır.

> DİKKAT: Bu koleksiyonlar `null` eleman **kabul etmez** (`NullPointerException`), `Set.of`/`Map.of` **tekrar eden anahtar/eleman kabul etmez** (`IllegalArgumentException`) ve hepsi **immutable**'dır (`add`/`put` -> `UnsupportedOperationException`).

---

## 3) Stream API Geliştirmeleri

> Kod: [`StreamGelistirmeleri.java`](./StreamGelistirmeleri.java)

### NEDİR?
- `takeWhile(predicate)` — koşul doğru olduğu sürece baştan eleman alır, ilk yanlışta durur.
- `dropWhile(predicate)` — koşul doğru olduğu sürece baştan eleman atar, ilk yanlıştan itibaren kalanı alır.
- `Stream.ofNullable(t)` — `t` null ise boş stream, değilse tek elemanlı stream döner.
- `Stream.iterate(seed, hasNext, next)` — sonlu (predicate'li) iterate. Java 8'deki sonsuz `iterate`e güvenli, `for` döngüsüne benzer bir alternatif.

### NEDEN GELDİ?
`filter` koşulu **tüm** elemanlara uygular; "sıralı veride bir eşiğe kadar al/atla" senaryosunu ifade edemiyordu. Java 8'de `iterate` sonsuzdu, `limit` ile kesmek gerekiyordu.

### ESKİ vs YENİ
```java
// ESKİ: sıralı veride eşiğe kadar al
list.stream().filter(x -> x < 100) // YANLIŞ: 100'den sonra gelen küçükleri de alır

// YENİ
list.stream().takeWhile(x -> x < 100) // doğru: ilk >=100'de durur
```

### GERÇEK HAYAT ÖRNEĞİ
Tarihe göre sıralı log satırlarını okurken belirli bir saatten **sonrasını** atlamak (`takeWhile`) ya da bir başlık bloğunu geçip gövdeye ulaşmak (`dropWhile`).

---

## 4) Optional Geliştirmeleri

> Kod: [`OptionalGelistirmeleri.java`](./OptionalGelistirmeleri.java)

### NEDİR?
- `ifPresentOrElse(action, emptyAction)` — değer varsa bir şey yap, yoksa başka bir şey yap.
- `or(supplier)` — boşsa **başka bir Optional** ile fallback (yeni `Optional` döner).
- `stream()` — `Optional`'ı 0 veya 1 elemanlı bir `Stream`'e çevirir; `flatMap` ile boş olanları eler.

### NEDEN GELDİ?
Java 8'de `ifPresent` vardı ama "yoksa şunu yap" için çirkin `if (opt.isPresent()) ... else ...` yazmak gerekiyordu. `or` ve `stream` ile zincirleme/fonksiyonel akış tamamlandı.

### ESKİ vs YENİ
```java
// ESKİ
if (opt.isPresent()) log(opt.get()); else logEksik();

// YENİ
opt.ifPresentOrElse(this::log, this::logEksik);
```

### GERÇEK HAYAT ÖRNEĞİ
Önce önbellekten (cache), yoksa veritabanından kullanıcıyı bul: `cache.find(id).or(() -> db.find(id))`.

---

## 5) Private Interface Metotları

> Kod: [`PrivateInterfaceMetot.java`](./PrivateInterfaceMetot.java)

### NEDİR?
Interface içinde `private` ve `private static` metot tanımlanabilmesi. Bu metotlar dışarı görünmez; yalnızca aynı interface'in `default`/`static` metotları tarafından kullanılır.

### NEDEN GELDİ?
Java 8 `default` metotları getirdi ama iki `default` metot ortak mantığı paylaşmak istediğinde, ortak kodu ya **kopyalamak** ya da istemeden `public default` olarak dışarı açmak zorundaydık. Bu, kapsüllemeyi bozuyordu.

### ESKİ vs YENİ
```java
// ESKİ: ortak kodu public default olarak açmak zorunda kalmak (sızıntı)
default void log1(){ /* ortak kod kopya */ }
default void log2(){ /* ortak kod kopya */ }

// YENİ
private void ortakLog(String s){ /* ortak kod tek yerde */ }
default void log1(){ ortakLog("1"); }
default void log2(){ ortakLog("2"); }
```

### GERÇEK HAYAT ÖRNEĞİ
Bir `Repository` interface'inde birden çok `default` metot ortak doğrulama/loglama yapar; bu mantık `private` bir yardımcı metotta toplanır, API kirlenmez.

---

## Kısaca Diğer Java 9 Özellikleri

### Try-with-resources Geliştirmesi
Java 7'de `try-with-resources` içinde kaynak için **yeni bir değişken** bildirmek zorunluydu. Java 9'da **zaten effectively final olan mevcut bir değişken** doğrudan kullanılabilir:
```java
// ESKİ (Java 7/8)
try (Reader r2 = r) { ... }   // gereksiz ikinci değişken
// YENİ (Java 9)
final Reader r = ...;
try (r) { ... }               // doğrudan
```

### Process API Geliştirmeleri (`ProcessHandle`)
İşletim sistemi süreçleriyle çalışmak için yeni `java.lang.ProcessHandle` API'si. Süreç PID'sini, sahibini, başlangıç zamanını, alt süreçlerini öğrenme; `onExit()` ile sürecin bitişini `CompletableFuture` olarak bekleme.
```java
ProcessHandle.current().pid();             // kendi PID'imiz
ProcessHandle.allProcesses().count();      // sistemdeki süreç sayısı
```

### HTTP/2 Client (incubator)
`jdk.incubator.http` altında modern, HTTP/2 ve WebSocket destekli, asenkron bir HTTP istemcisi. Java 9'da **incubator (deneysel)** statüsündeydi, paket adı incubator idi. Java 11'de standartlaşıp `java.net.http.HttpClient` oldu. Amaç: eski, kullanışsız `HttpURLConnection`'ı emekliye ayırmak.

### Multi-Release JAR (MRJAR)
Tek bir JAR içinde farklı Java sürümleri için farklı `.class` dosyaları barındırma. `META-INF/versions/9/` altına Java 9'a özel sınıflar konur; eski JVM kök dizindeki sınıfları, Java 9+ JVM ise versiyonlu olanı kullanır. Kütüphane yazarları için: tek JAR ile hem eski hem yeni JVM'i en iyi şekilde destekleme.

---

## Java 8'den Java 9'a GEÇİŞTE NE DEĞİŞTİ?

1. **Modül sistemi geldi.** Mevcut classpath uygulamaları "unnamed module" olarak çalışmaya devam eder; yani **hemen modüle dönmek zorunlu değildir**. Ama JDK'nın kendisi modülerleşti.
2. **Dahili API'ler kapatıldı.** `sun.misc.Unsafe` ve benzeri `com.sun.*` / `sun.*` paketleri artık varsayılan olarak erişilemez. Bunları kullanan eski kütüphaneler **derleme/çalışma anında uyarı veya hata** üretir.
3. **`-` ile başlayan birçok kaldırılmış API:** `jdk.internal.*` gizlendi.
4. **Sürüm dizgisi değişti:** `1.9` değil, artık `9`. `System.getProperty("java.version")` -> `9`. Sürüm string'ini parse eden eski kodlar kırılabilir.
5. **`rt.jar` ve `tools.jar` kaldırıldı.** Bu dosyalara doğrudan referans veren build script'leri bozulur.
6. **`Class.forName` / classloader hiyerarşisi değişti** (bootstrap classloader artık `null` yerine yeni bir yapı).
7. **JShell** (REPL) geldi — bkz. [`JShellNotlari.md`](./JShellNotlari.md).
8. **Underscore (`_`) artık tek başına geçerli bir tanımlayıcı değil** (ileride keyword olacağı için ayrıldı).

---

## Avantajlar / Dezavantajlar / Riskler

### Avantajlar
- Güçlü kapsülleme: mimari sınırlar derleyici tarafından zorlanır.
- Güvenilir konfigürasyon: eksik bağımlılık başlangıçta yakalanır (runtime sürprizi yok).
- `jlink` ile küçük, özel runtime imajları (container/IoT için ideal).
- Daha temiz Stream/Optional/Collection API'leri sayesinde daha az boilerplate.
- JShell ile hızlı prototipleme ve öğrenme.

### Dezavantajlar
- Öğrenme eğrisi: `module-info.java`, `requires/exports/opens` kavramları yeni ve karmaşık.
- Yansıma (reflection) tabanlı framework'ler (Spring, Hibernate, Jackson) `opens` direktifi gerektirebilir; yoksa `InaccessibleObjectException`.
- Build araçları (Maven/Gradle) ve IDE'lerin tam modül desteğine kavuşması zaman aldı.

### Riskler (Geçişte en sık karşılaşılanlar)
- **Internal API erişiminin kapanması:** Eski kütüphaneler `sun.misc.Unsafe` kullanıyorsa kırılır. (Geçici çözüm: `--add-exports`, `--add-opens` JVM bayrakları.)
- **Split package sorunu:** Aynı paketin iki ayrı JAR/modülde bulunması modül sisteminde **yasaktır**; eski projelerde sık görülür ve build'i bozar.
- **Reflection / setAccessible kırılmaları:** Güçlü kapsülleme yüzünden `field.setAccessible(true)` artık her yere işlemez; `opens` gerekir.
- **Sürüm parse eden kod:** `"1.8"` formatı bekleyen string parse mantıkları `"9"` ile patlar.
- **Kaldırılan modüller/araçlar:** `rt.jar`, `tools.jar`, JAXB/JAX-WS gibi Java EE modülleri Java 9'da `deprecated` (ve sonra kaldırıldı) — `--add-modules java.se.ee` gibi çözümler gerekti.

> SONUÇ: Java 9 LTS olmadığından doğrudan üretim hedefi değildi; çoğu kurum onun getirdiği değişiklikleri **Java 11 LTS'e geçerken** sindirdi. Yine de Java 9, modern Java'nın (modüller, jlink, yeni API'ler) temellerini attığı için tarihsel olarak çok önemlidir.
