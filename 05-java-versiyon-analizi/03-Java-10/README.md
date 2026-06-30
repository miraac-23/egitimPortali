# Java 10 (Mart 2018) — Detayli Surum Analizi

> Bu klasor, Java 10'un getirdigi yenilikleri Turkce, derinlemesine ve calisan kod ornekleriyle anlatir.

---

## Surum Bilgisi

| Ozellik | Deger |
|---|---|
| **Surum** | Java SE 10 (JDK 10) |
| **Cikis Tarihi** | 20 Mart 2018 |
| **Kod Adi** | 18.3 (yeni surum numaralandirmasi denemesi) |
| **LTS mi?** | **HAYIR** — LTS (Long-Term Support) DEGILDIR |
| **Destek Durumu** | Sona erdi (sadece Eylul 2018'e kadar resmi guncelleme aldi) |
| **Onceki LTS** | Java 8 |
| **Sonraki LTS** | Java 11 (Eylul 2018) |

### Yeni 6 Aylik Surum Dongusunun ILK Meyvesi

Java 10, Oracle'in **yeni 6 aylik (yari yillik) surum modelinin ilk uygulamasidir.**

- Java 9'a kadar surumler arasinda **yillar** gecerdi (Java 7 → Java 8 arasi ~3 yil, Java 8 → Java 9 arasi ~3.5 yil).
- Java 9 ile birlikte Oracle, **her 6 ayda bir** yeni bir Java surumu cikarmaya karar verdi (Mart ve Eylul).
- Bu modelde **her surum LTS DEGILDIR.** Sadece belirli surumler (Java 11, 17, 21, ...) uzun donem destek alir.
- Java 10 "feature release" (ozellik surumu) olarak ciktigi icin, sadece bir sonraki surum (Java 11) cikana kadar (yaklasik 6 ay) guncelleme aldi.

**Sonuc:** Java 10'u bugun **uretimde (production) kullanmak onerilmez.** Java 10 ile gelen ozellikler zaten Java 11+ icinde mevcuttur. Java 10'un onemi, getirdigi `var` ve immutable koleksiyon API'lerinin **tum sonraki surumlere tasinmis** olmasidir.

---

## Genel Bakis — Java 10 Ne Getirdi?

| JEP | Ozellik | Bu klasordeki dosya |
|---|---|---|
| **JEP 286** | Local Variable Type Inference (`var`) | `VarTipCikarimi.java` |
| **API** | `Collectors.toUnmodifiable*`, `List/Set/Map.copyOf` | `ImmutableCollections.java` |
| **JEP 307** | Parallel Full GC for G1 | (bu README'de aciklandi) |
| **JEP 310** | Application Class-Data Sharing (AppCDS) | (bu README'de aciklandi) |
| **JEP 304** | Garbage-Collector Interface | (bu README'de aciklandi) |
| **JEP 312** | Thread-Local Handshakes | (kisaca aciklandi) |
| **JEP 314** | Additional Unicode Language-Tag Extensions | (kisaca aciklandi) |
| **JEP 317** | Experimental JIT Compiler (Graal, deneysel) | (kisaca aciklandi) |
| **JEP 296** | JDK kaynak kodu tek depoda birlestirildi | (gelistirici ic yapisi) |

---

## 1) `var` — Yerel Degisken Tip Cikarimi (JEP 286)

> Ilgili dosya: **`VarTipCikarimi.java`**

### NEDIR?
Yerel degiskenlerin tipini, derleyicinin sag taraftaki ifadeden otomatik cikarmasini saglayan ozelliktir. `var` bir **anahtar kelime degil**, "ayrilmis tip adi"dir (bu sayede eski kodlarda `var` adli degiskenler bozulmaz).

### NEDEN GELDI? (Hangi problem?)
Java cok "verbose" (gereksiz uzun) bulunuyordu. Ozellikle uzun generic tiplerde tip adini **iki kez** yazmak zorunluydu ve bu okunabilirligi dusuruyordu:

```java
Map<String, List<Map<Integer, String>>> veri =
    new HashMap<String, List<Map<Integer, String>>>();
```

### NE ISE YARAR?
- Kalip kodu (boilerplate) azaltir.
- Uzun generic tiplerde okunabilirligi artirir.
- Degiskenin **adina** ve **anlamina** odaklanmayi saglar.

### NEREDE KOLAYLIK SAGLAR?
- Karmasik generic tipler
- Stream / Collectors zincirleri
- `for-each` ve klasik `for` donguleri
- `try-with-resources`

### ESKI vs YENI

```java
// ESKI (Java 9 ve oncesi)
ArrayList<String> liste = new ArrayList<String>();
Map<String, List<Integer>> harita = new HashMap<String, List<Integer>>();

// YENI (Java 10+)
var liste = new ArrayList<String>();
var harita = new HashMap<String, List<Integer>>();
```

### NEREDE KULLANILAMAZ? (Cok Onemli)
`var` SADECE **baslatici (initializer) ile yerel degiskende** kullanilir. Su durumlarda KULLANILAMAZ:

- Baslatmasiz: `var x;` ❌
- Null ile: `var y = null;` ❌
- Lambda atamasi: `var f = () -> ...;` ❌ (hedef tip yok)
- Metot referansi: `var g = System.out::println;` ❌
- Dizi kisayolu: `var d = {1,2,3};` ❌ (ama `var d = new int[]{1,2,3};` ✅)
- Birden cok degisken: `var a = 1, b = 2;` ❌
- Sinif alani (field) ❌
- Metot parametresi ❌
- Metot donus tipi ❌
- `catch` parametresi ❌
- **Lambda parametresi** ❌ (Java 10'da YOK; Java 11/JEP 323 ile geldi)

### GERCEK HAYAT ORNEGI
Bir enterprise serviste, repository katmanindan gelen karmasik generic donus tiplerini yazarken:

```java
// ESKI
Map<String, List<SiparisDetay>> gruplu = siparisServisi.gruplaMusteriye();
// YENI
var gruplu = siparisServisi.gruplaMusteriye();
```
Metot adi (`gruplaMusteriye`) anlamli oldugundan tip belirsizlesmez; kod daha temiz olur.

### OKUNABILIRLIK RISKI (Best Practice)
`var` yanlis kullanilirsa kodu **okunamaz** hale getirebilir:

```java
var x = getir();   // getir() ne donduruyor? Okuyan bilemez -> KOTU
```

**Altin kural:** Tip sag taraftan apacik belliyse (`new X()`, literal, anlamli metot adi) `var` kullan; degilse acik tip yaz. Ayrica sayisal tuzaklara dikkat: `var oran = 5;` aslinda `int`'tir, `5.0` yazmazsan ondalik beklerken tam sayi bolmesi yasarsin.

---

## 2) Degistirilemez (Immutable) Koleksiyon Gelistirmeleri

> Ilgili dosya: **`ImmutableCollections.java`**

### NEDIR?
Java 10 iki yetenek ekledi:
1. **`Collectors.toUnmodifiableList/Set/Map`** — Stream sonucunu dogrudan degistirilemez koleksiyon olarak toplar.
2. **`List.copyOf / Set.copyOf / Map.copyOf`** — Var olan bir koleksiyondan degistirilemez bir **kopya** olusturur.

### NEDEN GELDI? (Hangi problem?)
Java 9 `List.of(...)` getirdi ama bu **sifirdan, eleman eleman** olusturur. Elimizde **zaten** bir koleksiyon varsa (orn. metoda parametre olarak geldi) ve degistirilemez kopyasini istiyorsak, eski `Collections.unmodifiableList(new ArrayList<>(...))` kalibini kullanmak zorundaydik.

### `List.of` (Java 9) vs `List.copyOf` (Java 10) FARKI
- `List.of(a, b, c)` → elemanlari **tek tek** verirsin, sifirdan olusturur.
- `List.copyOf(coll)` → **var olan** koleksiyondan bagimsiz, degistirilemez bir **kopya** alir. Orijinal sonradan degisse bile kopya etkilenmez (gercek **defensive copy**).

Her ikisi de `add/remove/set` cagrilirsa `UnsupportedOperationException` firlatir.

### ESKI vs YENI

```java
// ESKI (Java 8/9): once kopya, sonra sar
List<String> sonuc =
    Collections.unmodifiableList(new ArrayList<>(girdi));

// YENI (Java 10): tek satir
var sonuc = List.copyOf(girdi);
```

```java
// ESKI: Stream -> toList -> unmodifiable
List<String> r = Collections.unmodifiableList(
    stream.collect(Collectors.toList()));

// YENI: dogrudan
var r = stream.collect(Collectors.toUnmodifiableList());
```

### GERCEK HAYAT ORNEGI (Defensive Copy)
Bir `Siparis` sinifi disaridan urun listesi alir. Listeyi dogrudan saklarsak, disaridaki kod sonradan onu degistirip ic durumumuzu bozabilir. `List.copyOf` ile degistirilemez kopya saklayarak nesneyi koruruz:

```java
final class Siparis {
    private final List<String> urunler;
    Siparis(List<String> urunler) {
        this.urunler = List.copyOf(urunler); // savunmaci kopya
    }
}
```

---

## 3) JVM / Performans Gelistirmeleri (Kurumsal Sistemler Acisindan)

### JEP 307 — G1 icin Paralel Full GC (Parallel Full GC for G1)

**NEDIR:** G1 (Garbage-First) cop toplayicisinin **full GC** (tam cop toplama) asamasi, Java 10'a kadar **tek is parcacigi (single-threaded)** ile calisiyordu.

**PROBLEM:** G1 normalde dusuk duraklama (low-pause) icin tasarlanmistir, ama bellek baskisi yuksek olup full GC tetiklendiginde tek cekirdek kullandigi icin **uzun "stop-the-world" duraklamalari** olusuyordu.

**COZUM:** Full GC artik **coklu is parcacigi (parallel, mark-sweep-compact)** ile calisir; mevcut tum CPU cekirdeklerini kullanir.

**KURUMSAL FAYDA:** Buyuk heap'li (orn. 32 GB+) sunucu uygulamalarinda full GC duraklamasi belirgin sekilde kisalir. Bu, dusuk gecikme (latency) bekleyen finans/e-ticaret sistemlerinde tepki sureleri ve SLA acisindan kritiktir. Java 9'da G1 varsayilan GC olmustu; Java 10 onun en zayif noktasini iyilestirdi.

### JEP 310 — Application Class-Data Sharing (AppCDS)

**NEDIR:** CDS (Class-Data Sharing) onceden sadece **JDK cekirdek (bootstrap) siniflarini** paylasilan bir arsivde tutabiliyordu. AppCDS bunu **uygulama siniflarina** da genisletti.

**NASIL CALISIR:** Uygulama siniflari bir kez "class-data archive" dosyasina yazilir; sonraki JVM baslangiclarinda bu arsiv **bellek esleme (memory-mapping)** ile dogrudan yuklenir ve **birden cok JVM ornegi arasinda paylasilir**.

**KURUMSAL FAYDA:**
- **Daha hizli baslangic (startup) suresi** — siniflar tekrar tekrar parse/dogrulama yapilmadan yuklenir.
- **Daha dusuk bellek ayak izi** — ayni makinede calisan birden cok JVM ornegi (orn. mikroservis konteynerleri) ortak class metadata'sini paylasir.
- Konteyner/Kubernetes ortamlarinda yogun olcekli (cok ornekli) dagitimlarda toplam RAM tasarrufu saglar; hizli olceklenme (scale-up) onemli oldugunda baslangic gecikmesini azaltir.

### JEP 304 — Garbage-Collector Interface (GC Arayuzu)

**NEDIR:** JVM ic kaynak kodunda cop toplayicilar icin **temiz, izole edilmis bir arayuz (interface)** tanimlandi.

**PROBLEM:** Onceden GC kodu JVM'in her yerine dagilmis durumdaydi; yeni bir GC eklemek veya birini cikarmak cok zordu.

**FAYDA (cogunlukla gelistirici/JVM bakimi):** GC'ler modulerlesti. Bu altyapi sayesinde sonraki surumlerde **ZGC** (Java 11) ve **Shenandoah** gibi yeni, dusuk gecikmeli toplayicilarin eklenmesi kolaylasti. Kurumsal acidan dolayli fayda: gelecekte GC secenekleri daha zengin ve bakimi daha kolay hale geldi.

### Kisaca Diger JEP'ler
- **JEP 312 (Thread-Local Handshakes):** Tum is parcaciklarini durdurmadan, tek tek thread'lerle "el sikisma" yapabilme. Bu da global stop-the-world duraklama ihtiyacini azaltir (GC ve diagnostik islemler icin temel altyapi).
- **JEP 317 (Graal — Deneysel JIT):** Java ile yazilmis Graal derleyicisi, Linux/x64'te **deneysel** JIT olarak kullanilabilir hale geldi.
- **JEP 314 (Unicode dil etiketleri):** Ek Unicode BCP 47 dil-etiketi uzantilari (takvim, sayi sistemi vb.) desteklendi.
- **JEP 316 (Alternatif bellek aygitlari):** Heap'in alternatif bellek aygitlarina (orn. NV-DIMM) yerlestirilebilmesi.

---

## Java 9'dan Java 10'a GECISTE NE DEGISTI?

| Konu | Java 9 | Java 10 |
|---|---|---|
| Yerel degisken tipi | Tipi acikca yazmak zorunlu | `var` ile cikarim |
| Stream → immutable | `Collections.unmodifiableList(...collect(toList()))` | `Collectors.toUnmodifiableList()` |
| Var olan koleksiyondan immutable kopya | Manuel `unmodifiable + new ArrayList` | `List/Set/Map.copyOf(...)` |
| G1 Full GC | Tek is parcacikli (yavas duraklama) | Paralel (coklu cekirdek) |
| Class-Data Sharing | Sadece JDK siniflari | Uygulama siniflari da (AppCDS) |
| Surum modeli | Yeni 6 aylik modelin baslangici | Modelin **ilk** ozellik surumu |

**Genel olarak:** Java 9'dan 10'a gecis **kucuk ama anlamli** bir adimdir. Java 9'un getirdigi devrim niteligindeki modul sistemi (JPMS) gibi buyuk degisiklikler yoktu; Java 10 daha cok **rahatlatma (var)**, **API tamamlama (copyOf)** ve **JVM performans iyilestirmesi** odakliydi. Gecis genellikle sorunsuzdur cunku geriye donuk uyumluluk korunmustur (`var` ayrilmis tip adi oldugu icin eski kodlari bozmaz).

---

## Avantaj / Dezavantaj / Risk

### Avantajlar
- **`var` ile daha temiz kod:** Ozellikle uzun generic tiplerde okunabilirlik ve yazim hizi artar.
- **Tamamlanan immutable API:** `copyOf` ve `toUnmodifiable*` ile savunmaci programlama (defensive copy) cok kolaylasti.
- **G1 paralel full GC:** Buyuk heap'li sunucularda duraklama sureleri kisaldi.
- **AppCDS:** Cok ornekli (mikroservis/konteyner) ortamlarda hizli baslangic ve dusuk bellek.

### Dezavantajlar
- **LTS degil:** Uretim icin uygun degil; destek kisa surede sona erdi.
- **`var` her yerde calismaz:** Field, parametre, donus tipi, lambda parametresi gibi yerlerde kullanilamaz; kurallar akilda tutulmali.
- **Kucuk olcekli surum:** Java 9 ile gelen modul sistemi gibi cigir acan bir yenilik yok.

### Riskler
- **`var` okunabilirlik riski (en onemli risk):** Yanlis kullanilirsa tip belirsizlesir.
  - `var x = servis.getir();` → okuyan kisi `x`'in tipini bilemez.
  - Sayisal tuzak: `var oran = 5;` → `int`! Beklenmeyen tam sayi bolmesine yol acar.
  - Cozum: `var`'i yalnizca sag taraf tipi apacik gosterdiginde kullan; anlamli degisken adlari sec; aksi halde acik tip yaz.
- **6 aylik surum modeli riski:** Her surum LTS olmadigi icin, surekli en yeni Java'yi takip eden ekipler **sik guncelleme** baskisiyla karsilasir. Kurumsal projeler genelde sadece LTS surumlerde (8, 11, 17, 21) kalmayi tercih eder; Java 10 gibi ara surumler **atlanir**. Bu model, "her surumu uretime alma" yerine "LTS'leri takip et, ara surumleri deneme/gelistirme icin kullan" stratejisini zorunlu kildi.
- **Yanlis surum secimi maliyeti:** Java 10'u uretimde secen bir ekip, 6 ay sonra zorunlu olarak Java 11'e gecmek (ve test/dogrulama maliyetine katlanmak) durumunda kalirdi.

---

## Bu Klasordeki Dosyalar

- `README.md` — bu dosya (surum analizi ve tum ozelliklerin aciklamasi)
- `VarTipCikarimi.java` — `var` / yerel degisken tip cikarimi, derinlemesine, kullanilamayan durumlar dahil
- `ImmutableCollections.java` — `Collectors.toUnmodifiable*`, `List/Set/Map.copyOf`, defensive copy senaryosu

### Derleme ve Calistirma (JDK 10+ gerektirir)

```bash
javac VarTipCikarimi.java && java VarTipCikarimi
javac ImmutableCollections.java && java ImmutableCollections
```
> Not: Bu dosyalar `var` ve Java 10 API'lerini kullandigi icin **JDK 10 veya uzeri** ile derlenmelidir. Daha eski bir JDK ile derlenmezler.
