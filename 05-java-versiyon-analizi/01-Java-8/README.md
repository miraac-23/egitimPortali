# Java 8 — Detaylı Sürüm Analizi ve Örnekler

> Bu klasör, **Java 8** sürümünün getirdiği tüm önemli özellikleri derinlemesine
> anlatan Türkçe bir dokümantasyon ve her özellik için çalışan, derlenebilir
> örnek `.java` dosyaları içerir.

---

## İçindekiler

1. [Sürüm Bilgisi](#sürüm-bilgisi)
2. [Java 8 Neden Devrim Niteliğindedir?](#java-8-neden-devrim-niteliğindedir)
3. [Özellikler](#özellikler)
   - [1. Lambda İfadeleri](#1-lambda-i̇fadeleri)
   - [2. Fonksiyonel Arayüzler](#2-fonksiyonel-arayüzler)
   - [3. Method References](#3-method-references)
   - [4. Stream API](#4-stream-api)
   - [5. Optional Sınıfı](#5-optional-sınıfı)
   - [6. Default ve Static Metotlar](#6-default-ve-static-metotlar)
   - [7. Yeni Date/Time API (java.time)](#7-yeni-datetime-api-javatime)
   - [8. Nashorn JavaScript Engine](#8-nashorn-javascript-engine)
   - [9. Collectors ve Gruplama](#9-collectors-ve-gruplama)
   - [10. CompletableFuture (Asenkron)](#10-completablefuture-asenkron-programlama)
   - [11. StringJoiner ve String.join](#11-stringjoiner-ve-stringjoin)
   - [12. Repeatable & Type Annotations](#12-repeatable--type-annotations)
   - [13. Arrays.parallelSort ve PermGen→Metaspace](#13-arraysparallelsort-ve-permgen--metaspace)
4. [Java 8'e Geçiş: Avantajlar, Dezavantajlar, Riskler](#java-8e-geçiş-avantajlar-dezavantajlar-riskler)
5. [Dosya Listesi ve Çalıştırma](#dosya-listesi-ve-çalıştırma)

---

## Sürüm Bilgisi

| Bilgi | Değer |
|-------|-------|
| **Sürüm** | Java SE 8 (JDK 1.8) |
| **Çıkış Tarihi** | **18 Mart 2014** |
| **LTS Durumu** | **Evet — Uzun Vadeli Destek (LTS).** Java'nın en uzun ömürlü, en yaygın kullanılan sürümlerinden biridir. |
| **Önemli Sürümler** | Oracle JDK ve OpenJDK olarak dağıtıldı. Oracle kamu güncellemelerini 2019'da sonlandırdı (ticari destek devam etti). OpenJDK tabanlı dağıtımlar (Adoptium/Temurin, Amazon Corretto, Azul Zulu, Red Hat) destek vermeye devam ediyor. |
| **JLS / JVM** | Java Language Specification 8, ayrıca PermGen kaldırılıp Metaspace getirildi. |

### Oracle / OpenJDK Notu
Java 8 döneminde **Oracle JDK** ile **OpenJDK** arasında pratikte çok az fark
vardı. 2019 sonrası Oracle, ticari kullanımda lisans (abonelik) modeline geçti;
bu yüzden bugün çoğu kurum **OpenJDK tabanlı ücretsiz dağıtımlar** (Eclipse
Temurin, Amazon Corretto vb.) kullanır. Java 8, hâlâ kurumsal dünyada en çok
production'da çalışan sürümlerden biridir.

---

## Java 8 Neden Devrim Niteliğindedir?

Java 8, dilin tarihindeki **en büyük paradigma değişimini** getirdi:
**fonksiyonel programlama** unsurlarını ana akım Java'ya soktu.

Java 8 öncesinde Java tamamen **nesne yönelimli (OOP)** ve **emir kipi
(imperative)** bir dildi: "bilgisayara adım adım NASIL yapacağını söyle".
Java 8 ile birlikte **deklaratif (bildirimsel)** programlama mümkün oldu:
"NE istediğini söyle, nasıl yapılacağıyla kütüphane ilgilensin".

Bu değişimi mümkün kılan üç temel taş:

1. **Lambda ifadeleri** — davranışı (kod) bir veri gibi geçirebilmek.
2. **Fonksiyonel arayüzler** — lambdaların "tipi".
3. **Stream API** — koleksiyonlar üzerinde fonksiyonel boru hatları.

Bunların yanında **Optional** (null güvenliği), **java.time** (modern tarih),
**default metotlar** (arayüz evrimi) ve **CompletableFuture** (asenkron) gibi
yenilikler, Java'yı modern bir dil seviyesine taşıdı. İşte bu yüzden Java 8,
"öncesi ve sonrası" diye anılan bir dönüm noktasıdır.

---

## Özellikler

Her özellik için: **NEDİR / NEDEN GELDİ / NE İŞE YARAR / NEREDE KOLAYLIK /
ESKİ vs YENİ / GERÇEK HAYAT** başlıklarını bulacaksınız ve ilgili `.java`
dosyasına referans verilmiştir.

---

### 1. Lambda İfadeleri

**Bkz: `LambdaOrnekleri.java`**

- **NEDİR?** İsimsiz (anonim) bir fonksiyondur. `(parametreler) -> { gövde }`
  sözdizimiyle, bir davranışı (kod parçasını) doğrudan değer gibi geçirmemizi
  sağlar.
- **NEDEN GELDİ?** Java 8 öncesinde bir davranış geçirmek için **anonim iç
  sınıf** yazmak zorundaydık. 1 satırlık iş için 5-6 satırlık "tören kodu"
  (boilerplate) gerekiyordu.
- **NE İŞE YARAR / NEREDE KOLAYLIK?** Sıralama (Comparator), olay dinleyicileri,
  Stream işlemleri, callback'ler. Kodu kısaltır ve okunabilir kılar.

**ESKİ YÖNTEM (anonim iç sınıf):**
```java
Collections.sort(isimler, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});
```

**YENİ YÖNTEM (lambda):**
```java
Collections.sort(isimler, (a, b) -> a.compareTo(b));
```

- **GERÇEK HAYAT:** Bir e-ticaret sitesinde çalışan listesini önce departmana,
  sonra isme göre sıralamak; bir kullanıcı listesini `forEach` ile dolaşmak.

---

### 2. Fonksiyonel Arayüzler

**Bkz: `FonksiyonelArayuzler.java`**

- **NEDİR?** İçinde **tek bir soyut metot** bulunan arayüz (SAM — Single
  Abstract Method). Lambdaların ve method reference'ların "tipi" budur.
  `@FunctionalInterface` anotasyonu, ikinci soyut metot eklenirse derleme
  hatası vererek güvence sağlar.
- **NEDEN GELDİ?** Lambdaların atanabileceği bir tip gerekiyordu. Java 8,
  `java.util.function` paketinde hazır, genel amaçlı arayüzler getirdi; böylece
  her ihtiyaç için yeni arayüz tanımlamaya gerek kalmadı.
- **NE İŞE YARAR:** Davranışı parametre olarak geçirmenin standart yolunu sunar.

**En çok kullanılanlar:**

| Arayüz | Girdi → Çıktı | Metot | Örnek kullanım |
|--------|---------------|-------|----------------|
| `Function<T,R>` | T → R | `apply` | Dönüştürme (map) |
| `Consumer<T>` | T → void | `accept` | Yan etki (yazdırma) |
| `Supplier<T>` | () → T | `get` | Tembel (lazy) üretim |
| `Predicate<T>` | T → boolean | `test` | Filtreleme koşulu |
| `BiFunction<T,U,R>` | (T,U) → R | `apply` | İki girdili işlem |
| `UnaryOperator<T>` | T → T | `apply` | Aynı tipe dönüştürme |
| `BinaryOperator<T>` | (T,T) → T | `apply` | İki aynı tipi birleştirme |

- **GERÇEK HAYAT:** E-ticarette `Predicate<Urun> stokta` ve
  `Predicate<Urun> pahali` kurallarını `.and()` ile birleştirip "stokta VE
  pahalı" ürünleri filtrelemek; `Function<Urun, Double>` ile KDV'li fiyat
  hesaplamak.

---

### 3. Method References

**Bkz: `MethodReferences.java`**

- **NEDİR?** Lambda'nın daha da kısa hâli. Eğer bir lambda yalnızca var olan
  bir metodu çağırıyorsa, `::` operatörüyle doğrudan metodu işaret ederiz.
- **NEDEN GELDİ?** `x -> Sinif.metot(x)` gibi sadece delege eden lambdalar hâlâ
  gürültülüydü. Method reference niyeti netleştirir ve okunabilirliği artırır.

**Dört türü:**

| Tür | Söz dizimi | Lambda karşılığı |
|-----|-----------|------------------|
| 1. Static metot | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| 2. Belirli nesnenin metodu | `System.out::println` | `s -> System.out.println(s)` |
| 3. Rastgele nesnenin örnek metodu | `String::toUpperCase` | `s -> s.toUpperCase()` |
| 4. Constructor referansı | `Kullanici::new` | `ad -> new Kullanici(ad)` |

**ESKİ vs YENİ:**
```java
// LAMBDA
list.stream().map(s -> s.toUpperCase());
// METHOD REFERENCE
list.stream().map(String::toUpperCase);
```

- **GERÇEK HAYAT:** Çalışanları maaşa göre sıralarken
  `Comparator.comparing(Calisan::getMaas)`; isim listesini toplu olarak
  nesneye çevirmek için `.map(Kullanici::new)`.

---

### 4. Stream API

**Bkz: `StreamApiOrnekleri.java`**

- **NEDİR?** Koleksiyonlar üzerinde **deklaratif** işlem yapmayı sağlayan bir
  veri akışı (pipeline) API'si: `kaynak → ara işlemler → sonlandırıcı`.
- **NEDEN GELDİ?** Elle yazılan iç içe `for` döngüleri ve geçici listeler,
  filtrele-dönüştür-topla gibi işlemleri okunması zor ve hataya açık hâle
  getiriyordu. Stream bunları tek bir akıcı zincire indirger.
- **NE İŞE YARAR:** `filter`, `map`, `reduce`, `collect`, `flatMap`, `sorted`,
  `limit`, `distinct`, istatistik, ve **paralel stream** ile çoklu çekirdek.

**Önemli kavramlar:**
- **Lazy (tembel):** Ara işlemler, terminal işlem çağrılana kadar çalışmaz.
- **Tek kullanımlık:** Bir stream tüketildikten sonra tekrar kullanılamaz.
- **Paralel:** `.parallelStream()` veya `.parallel()` ile çekirdeklere dağılır.

**ESKİ YÖNTEM:**
```java
List<String> sonuc = new ArrayList<>();
for (Urun u : urunler) {
    if (u.getKategori().equals("Elektronik") && u.getFiyat() > 1000) {
        sonuc.add(u.getAd().toUpperCase());
    }
}
```

**YENİ YÖNTEM (Stream):**
```java
List<String> sonuc = urunler.stream()
    .filter(u -> u.getKategori().equals("Elektronik"))
    .filter(u -> u.getFiyat() > 1000)
    .map(u -> u.getAd().toUpperCase())
    .collect(Collectors.toList());
```

- **GERÇEK HAYAT:** E-ticarette "en pahalı 3 ürünü bul", "tüm siparişlerin
  kalemlerini `flatMap` ile düzleştir", "toplam stok değerini `reduce` ile
  hesapla", 1 milyon kaydı paralel stream ile topla.

---

### 5. Optional Sınıfı

**Bkz: `OptionalOrnekleri.java`**

- **NEDİR?** "Değer olabilir VEYA olmayabilir" durumunu temsil eden bir
  kapsayıcı (`Optional<T>`). Amaç: `null` yerine, "değer yok" durumunu **tip
  seviyesinde** açıkça belirtmek.
- **NEDEN GELDİ?** `NullPointerException` (NPE), Java'nın en sık hatasıdır.
  Tony Hoare null'ı "milyar dolarlık hata" diye anar. Optional, bir metodun
  "değer döndürmeyebilirim" demesini imza seviyesinde gösterir.
- **NE İŞE YARAR:** NPE riskini azaltır, niyeti netleştirir, varsayılan değer/
  alternatif akış yönetimini kolaylaştırır.

**ESKİ vs YENİ:**
```java
// ESKİ: null kontrolleri iç içe, unutulursa NPE
Kullanici k = bul(id);
if (k != null && k.getEmail() != null) {
    System.out.println(k.getEmail().toUpperCase());
}

// YENİ: zincirleme, güvenli
String email = bul(id)
    .map(Kullanici::getEmail)
    .map(String::toUpperCase)
    .orElse("EMAIL YOK");
```

Önemli metotlar: `of`, `ofNullable`, `empty`, `isPresent`, `ifPresent`,
`map`, `flatMap`, `filter`, `orElse`, `orElseGet`, `orElseThrow`.

> **İyi pratik:** Optional'ı **dönüş tipi** olarak kullanın; alan (field) veya
> metot parametresi olarak kullanmaktan kaçının.

- **GERÇEK HAYAT:** Banka hesabı bulunamazsa `orElse(0.0)` ile güvenli bakiye;
  kullanıcının e-postası yoksa zincirin güvenle "EMAIL YOK" döndürmesi.

---

### 6. Default ve Static Metotlar

**Bkz: `DefaultStaticMetotlar.java`**

- **NEDİR?** Arayüzlerin artık **gövdeli** metot içerebilmesi:
  `default` (varsayılan uygulama) ve `static` (arayüz yardımcısı).
- **NEDEN GELDİ?** **Arayüz evrimi (interface evolution)** problemi. Bir
  arayüze yeni soyut metot eklersen, onu uygulayan tüm mevcut sınıflar bozulur.
  Asıl tetikleyici: Stream API. `Collection` arayüzüne `stream()`, `forEach()`,
  `removeIf()` eklenmesi gerekiyordu; bunlar normal metot olsaydı dünyadaki tüm
  Collection uygulamaları kırılırdı. **Default metotlar** sayesinde varsayılan
  gövdeyle eklendiler ve hiçbir mevcut kod bozulmadı.
- **NE İŞE YARAR:** Kütüphane geliştiricilere, kullanıcıları kırmadan arayüze
  yetenek ekleme imkânı.

**Diamond problem:** İki arayüzde aynı isimli default metot varsa, uygulayan
sınıf bunu override etmek **zorundadır**; `A.super.metot()` ile birini
seçebilir.

- **GERÇEK HAYAT:** `OdemeYontemi` arayüzünde tüm ödeme tiplerinin ücretsiz
  miras aldığı ortak `logla()` default metodu; `komisyonHesapla()` static
  yardımcısı.

---

### 7. Yeni Date/Time API (java.time)

**Bkz: `YeniTarihSaatApi.java`**

- **NEDİR?** `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`,
  `Duration`, `Period`, `Instant` gibi modern tarih/saat sınıfları (JSR-310,
  Joda-Time'dan ilham).
- **NEDEN GELDİ? (Eski Date/Calendar'ın sorunları):**
  1. **Değiştirilebilir (mutable):** `Date` thread-safe değildi.
  2. **Kafa karıştırıcı API:** `Calendar`'da **ay 0'dan başlardı** (Ocak = 0!),
     yıl 1900 tabanlıydı. Sayısız hataya yol açtı.
  3. **Zayıf zaman dilimi yönetimi.**
  4. **Zahmetli tarih aritmetiği.**
  
  `java.time` hepsini çözer: **değişmez (immutable)**, thread-safe, akıcı.

**ESKİ vs YENİ:**
```java
// ESKİ: ay 0-tabanlı! Bu aslında HAZIRAN demek, MAYIS değil
Calendar cal = Calendar.getInstance();
cal.set(2026, 5, 24);

// YENİ: insan mantığıyla, Haziran = 6
LocalDate tarih = LocalDate.of(2026, 6, 24);
LocalDate gelecek = tarih.plusDays(10).plusMonths(2); // immutable, yeni nesne
```

- **NE İŞE YARAR:** Yaş hesaplama (`Period`), mesai süresi (`Duration`), zaman
  dilimi dönüşümü (`ZonedDateTime`), formatlama/parse (`DateTimeFormatter`).
- **GERÇEK HAYAT:** Fatura kesim ve son ödeme tarihi hesaplama, doğum gününden
  yaş çıkarma, İstanbul–New York–Tokyo saat dönüşümü.

---

### 8. Nashorn JavaScript Engine

**Bkz: `NashornOrnekleri.java`**

- **NEDİR?** JVM üzerinde JavaScript çalıştıran yüksek performanslı motor
  (`javax.script` / JSR-223 üzerinden).
- **NEDEN GELDİ?** Eski "Rhino" motoru yavaş ve eskimişti. Nashorn,
  `invokedynamic` bytecode'unu kullanarak çok daha hızlı çalışır; uygulamalara
  gömülü scriptleme yeteneği kazandırır.
- **NE İŞE YARAR:** Java'yı yeniden derlemeden dinamik kural/yapılandırma;
  kural motorları; `jjs` komut satırı aracı.

> **ÖNEMLİ:** Nashorn **Java 11'de deprecated**, **Java 15'te tamamen
> kaldırıldı.** Modern projelerde **GraalVM JavaScript** tercih edilir. Bu
> örnek tarihsel/öğretici amaçlıdır ve Java 8–14 ile çalışır.

- **GERÇEK HAYAT:** İndirim kuralının JavaScript ile tanımlanıp Java kodu
  değişmeden güncellenebildiği bir kural motoru.

---

### 9. Collectors ve Gruplama

**Bkz: `CollectorsGruplama.java`**

- **NEDİR?** Stream'in `collect()` işleminde kullanılan hazır toplayıcı
  "reçeteleri": `toList`, `toSet`, `toMap`, `groupingBy`, `partitioningBy`,
  `joining`, `counting`, `averagingDouble`, `summingDouble`, `mapping`,
  `summarizingDouble`.
- **NEDEN GELDİ?** "Kategoriye göre grupla" gibi işlemler için elle Map yönetip
  "anahtar var mı, yoksa yeni liste aç" mantığını tekrar tekrar yazmak
  gerekiyordu. Collectors bunu tek satıra indirger.

**Örnek:**
```java
// Departman bazlı çalışan sayısı
Map<String, Long> sayilar = calisanlar.stream()
    .collect(Collectors.groupingBy(c -> c.getDepartman(), Collectors.counting()));

// 50.000 üstü / altı ikiye bölme
Map<Boolean, List<Calisan>> bolme = calisanlar.stream()
    .collect(Collectors.partitioningBy(c -> c.getMaas() >= 50000));
```

- **GERÇEK HAYAT:** Departman bazlı maaş ortalaması, çok seviyeli gruplama
  (departman → 40 yaş üstü mü → isimler), müşteri segmentasyonu, raporlama.

---

### 10. CompletableFuture (Asenkron Programlama)

**Bkz: `CompletableFutureOrnekleri.java`**

- **NEDİR?** "İleride tamamlanacak bir hesabın sonucunu" temsil eden sınıf.
  Asenkron işleri başlatır ve sonuçlarını **callback'lerle zincirler**.
- **NEDEN GELDİ? (Eski `Future`'ın sorunları):** `get()` ile **bloklanmak**
  gerekiyordu; iki future birleştirilemiyordu; otomatik callback ve zincirleme
  hata yönetimi yoktu. CompletableFuture bunların hepsini çözer.
- **NE İŞE YARAR:** Web servisi/DB/dosya gibi I/O işlerini paralel yürütüp
  sonuçları birleştirmek; bloklamadan akış kurmak.

**Temel metotlar:** `supplyAsync`, `thenApply`, `thenCompose`, `thenCombine`,
`thenAccept`, `exceptionally`, `allOf`, `anyOf`.

```java
CompletableFuture<Double> toplam = fiyatF.thenCombine(vergiF,
    (fiyat, vergi) -> fiyat + vergi);
```

- **GERÇEK HAYAT:** 3 farklı mikroservisi aynı anda çağırıp (paralel) hepsi
  bitince sonuçları birleştirmek — toplam süre, en yavaş servis kadar olur
  (sıralı çağrının toplamı değil).

---

### 11. StringJoiner ve String.join

**Bkz: `StringJoinerOrnekleri.java`**

- **NEDİR?** `String.join(...)` statik metodu ve `StringJoiner` sınıfı; parçaları
  ayırıcı (delimiter), önek (prefix) ve sonek (suffix) ile birleştirir.
- **NEDEN GELDİ?** "a, b, c" gibi birleştirme çok yaygındı ama standart yolu
  yoktu. Geliştiriciler `StringBuilder` + "son elemana virgül koyma" mantığını
  elle yazıyordu — tekrar eden ve hataya açık (fazladan virgül) kod.

**ESKİ vs YENİ:**
```java
// ESKİ
StringBuilder sb = new StringBuilder();
for (int i = 0; i < list.size(); i++) {
    sb.append(list.get(i));
    if (i < list.size() - 1) sb.append(", ");
}

// YENİ
String s = String.join(", ", list);
```

- **GERÇEK HAYAT:** CSV satırı üretme, SQL `IN (...)` listesi, URL query string
  (`?k=v&...`), rapor etiket listeleri.

---

### 12. Repeatable & Type Annotations

**Bkz: `AnnotationOrnekleri.java`**

- **Repeatable Annotations:**
  - **NEDİR?** Aynı anotasyonu aynı eleman üzerinde birden fazla kez kullanma.
  - **NEDEN GELDİ?** Önceden bir anotasyon bir elemana yalnızca **bir kez**
    konabilirdi; birden fazlası için elle "kapsayıcı (container)" dizi yazmak
    gerekiyordu (`@Schedules({@Schedule(...), @Schedule(...)})`). `@Repeatable`
    bunu temizler.
- **Type Annotations (TYPE_USE):**
  - **NEDİR?** Anotasyonların artık tipin **kullanıldığı her yere** konabilmesi
    (`@NonNull String`, `List<@NonNull String>`).
  - **NEDEN GELDİ?** Daha güçlü statik analiz / null güvenliği araçları (örn.
    Checker Framework) için.

- **GERÇEK HAYAT:** Bir görevin birden fazla çalışma zamanını deklaratif olarak
  tanımlamak (`@Zamanlama` anotasyonunu Pazartesi/Çarşamba/Cuma için tekrarlama)
  — cron benzeri zamanlama.

---

### 13. Arrays.parallelSort ve PermGen → Metaspace

**Bkz: `JvmVeYardimcilar.java`**

**A) `Arrays.parallelSort`**
- **NEDİR?** Diziyi paralel (fork/join havuzu) ile sıralayan metot.
- **NEDEN GELDİ?** `Arrays.sort` tek iş parçacığında çalışır; büyük dizilerde
  çok çekirdeği kullanmak için `parallelSort` eklendi.
- **DİKKAT:** Küçük dizilerde ek yük nedeniyle normal `sort` daha hızlı olabilir.

**B) PermGen Kaldırıldı → Metaspace Geldi (JVM değişikliği)**
- **ESKİ (Java 7 ve öncesi):** Sınıf metadata'sı **sabit boyutlu PermGen**'de
  tutulurdu. Çok sınıf yükleyen uygulamalar (uygulama sunucuları, sık deploy
  edenler) sıkça **`OutOfMemoryError: PermGen space`** alırdı. `-XX:MaxPermSize`
  ile boyut elle ayarlanırdı, ayarlamak zordu.
- **YENİ (Java 8):** PermGen tamamen **kaldırıldı**, yerine **Metaspace** geldi.
  - Metaspace heap'te değil, **native (yerel) bellekte** tutulur.
  - Varsayılan olarak **otomatik büyür** (sınırı işletim sistemi belleği;
    `-XX:MaxMetaspaceSize` ile sınırlanabilir).
  - Sonuç: "PermGen space" hatası tarihe karıştı; metadata yönetimi daha esnek.

---

## Java 8'e Geçiş: Avantajlar, Dezavantajlar, Riskler

### Avantajlar
- **Daha az kod, daha okunabilir:** Lambda + Stream ile boilerplate ciddi azalır.
- **Fonksiyonel paradigma:** Deklaratif, niyet-odaklı kod.
- **NPE azalması:** Optional ile daha güvenli API tasarımı.
- **Modern tarih/saat:** java.time, eski Date/Calendar dertlerini bitirir.
- **Asenkron kolaylığı:** CompletableFuture ile bloklamadan paralel akış.
- **JVM iyileştirmesi:** Metaspace ile PermGen hataları yok; performans artışları.
- **Geniş ekosistem:** En yaygın LTS olduğu için kütüphane/topluluk desteği güçlü.

### Dezavantajlar / Öğrenme Eğrisi
- **Zihniyet değişimi:** OOP'den fonksiyonele geçiş, ekipler için adaptasyon ister.
- **Aşırı kullanım riski:** Her şeyi tek satır stream'e sıkıştırmak okunabilirliği
  bozabilir; karmaşık zincirler debug edilmesi zor olabilir.
- **Stack trace okunabilirliği:** Lambda/stream hataları bazen kafa karıştırıcı
  stack trace üretir.

### Riskler (Geriye Dönük Uyumluluk & Performans)
- **Paralel stream tuzağı:** Küçük veride veya paylaşılan değişebilir durumda
  (shared mutable state) paralel stream **yanlış sonuç veya yavaşlık** doğurabilir.
  Ortak `ForkJoinPool.commonPool`'u tükettiği için dikkatli kullanılmalı.
- **Optional'ı yanlış kullanma:** Field/parametre olarak kullanmak anti-pattern'dir.
- **Nashorn bağımlılığı:** Nashorn'a bağlı kod, Java 15+'a geçişte **kırılır**
  (kaldırıldı). Yeni projelerde kaçınılmalı.
- **Geçiş uyumu:** Java 8 kodu genelde yeni sürümlere taşınabilir; ancak iç API
  (`sun.misc.*`), eski tarih API alışkanlıkları ve PermGen flag'leri
  (`-XX:MaxPermSize` artık yok sayılır) gibi noktalarda dikkat gerekir.
- **Performans:** Lambda ilk çağrıda `invokedynamic` ile bir miktar başlatma
  maliyeti getirir; sıcak yollarda (hot path) ölçüm yapmak önerilir.

---

## Dosya Listesi ve Çalıştırma

| Dosya | Konu |
|-------|------|
| `LambdaOrnekleri.java` | Lambda ifadeleri |
| `FonksiyonelArayuzler.java` | Function/Consumer/Supplier/Predicate/BiFunction + @FunctionalInterface |
| `MethodReferences.java` | Method reference'ların 4 türü |
| `StreamApiOrnekleri.java` | Stream API (map/filter/reduce/collect/flatMap/parallel) |
| `OptionalOrnekleri.java` | Optional ile NPE yönetimi |
| `DefaultStaticMetotlar.java` | Interface'lerde default/static metotlar, diamond problem |
| `YeniTarihSaatApi.java` | java.time (LocalDate, Duration, Period, ZonedDateTime) |
| `NashornOrnekleri.java` | Nashorn JavaScript Engine |
| `CollectorsGruplama.java` | groupingBy/partitioningBy/joining ve toplayıcılar |
| `CompletableFutureOrnekleri.java` | Asenkron programlama |
| `StringJoinerOrnekleri.java` | StringJoiner ve String.join |
| `AnnotationOrnekleri.java` | Repeatable & Type Annotations |
| `JvmVeYardimcilar.java` | Arrays.parallelSort ve PermGen→Metaspace |

### Derleme ve Çalıştırma (her dosya bağımsızdır)

```bash
# Tek bir dosyayı derleyip çalıştırma:
javac LambdaOrnekleri.java
java LambdaOrnekleri

# Tüm dosyaları derleme:
javac *.java

# Örnek çalıştırmalar:
java StreamApiOrnekleri
java YeniTarihSaatApi
java CollectorsGruplama
```

> **Not:** `NashornOrnekleri.java` yalnızca **Java 8–14** ile anlamlı çıktı
> verir (Nashorn Java 15'te kaldırıldı). Diğer tüm dosyalar Java 8 ve üzerinde
> çalışır.
