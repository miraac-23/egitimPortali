# Sürüm Geçişi: Riskler ve Kazanımlar

Yeni bir Java sürümüne geçmek genellikle kârlıdır — ama "derle, çalıştır, bitti" kadar basit
değildir. Her büyük geçişin somut **kazanımları** (performans, güvenlik, dil özellikleri)
olduğu kadar **riskleri** de (kaldırılan API'ler, kütüphane uyumsuzlukları, davranış
değişiklikleri) vardır. Bu bölümde önce genel geçiş prensiplerini, sonra bugün en yaygın iki
taban olan **17 ve 21'den en yeni sürümlere** geçişte seni nelerin beklediğini konuşuyoruz.

## Kazanımlar (neden geçilir?)

- **Güvenlik:** Eski sürümler bir noktada güvenlik yamaları almayı bırakır. Desteklenen bir
  LTS'te kalmak, bilinen açıklara karşı en temel korumadır.
- **Performans:** Her sürüm JIT ve çöp toplayıcı (GC) iyileştirmeleri getirir. 17→21 geçişi
  **generational ZGC** ile düşük gecikme sağlar; genel olarak daha az bellek, daha hızlı başlangıç.
- **Eşzamanlılık:** Java 21'in **sanal thread'leri**, G/Ç ağırlıklı sunucularda kodu neredeyse
  hiç değiştirmeden çok daha yüksek eşzamanlılık sağlar (Örnek 3'te ~50x fark gördük).
- **Dil ergonomisi:** record, sealed, pattern matching, switch expression... daha az kod, daha
  az hata. Modern API'ler (`java.time`, `HttpClient`, `Stream.toList()`) eski, hatalı olanların
  yerini alır.

Örnek 1 (`./Ornek1.java`) `Runtime.version()` ile çalışılan JDK'yı tanır ve hangi özelliklerin
mevcut olduğunu bir "feature flag" mantığıyla raporlar — geçiş sırasında kodun sürüme göre
nazikçe uyum sağlaması (graceful degradation) için kullanışlı bir desen.

## Riskler (geçişte neye dikkat?)

### 1) Kaldırılan / kapsüllenen API'ler

- **Java 11:** Java EE/CORBA modülleri **kaldırıldı** (JAXB, JAX-WS, CORBA). Bunlara bağlı eski
  projeler, kütüphaneleri ayrıca eklemeden derlenemez.
- **Java 16/17:** JDK iç bileşenleri **güçlü kapsüllendi** (JEP 396). `sun.misc.*`,
  `--illegal-access` ile JDK içine sızan kütüphaneler (eski Hibernate, bazı serileştirme/proxy
  araçları) kırıldı. Geçişten önce `--add-opens`/`--add-exports` ihtiyacını ve kütüphane
  sürümlerini gözden geçirmek şart.
- **Deprecation (kullanımdan kaldırma):** `SecurityManager` (17'den itibaren kaldırılmaya
  işaretli), `Applet`, finalization, `new Integer()`/`new Double()` (kaldırılmaya işaretli)...
  Bugün "uyarı" olan şey, yarın "derlenmiyor" olabilir.

### 2) Davranış değişiklikleri (sessiz kırılmalar)

Bunlar en sinsileridir; kod derlenir ama farklı davranır:

- **Java 18:** Varsayılan karakter kümesi **UTF-8** oldu. Platform varsayılanına güvenen dosya
  okuma/yazma kodu, farklı sonuç üretebilir.
- GC varsayılanları ve değerleri sürümlerle değişir; performans profili kayabilir.
- Bazı tarih/sayı biçimlendirme yerelleştirmeleri (CLDR güncellemeleri) çıktıları değiştirebilir.

### 3) Üçüncü parti uyumluluğu (en yaygın gerçek engel)

Uygulamanın kendisi geçişe hazır olsa bile, **bağımlılıkların** yeni JDK'yı desteklemesi
gerekir. Özellikle:
- Eski **Spring Boot 2.x** yalnızca Java 8–17 destekler; **Java 21 için Spring Boot 3.2+**,
  güvenli sanal thread desteği için 3.2+ gerekir. Java 25 için en güncel sürümlere bakılmalı.
- Hibernate, Lombok, bytecode üreten/işleyen araçlar (ASM, ByteBuddy, mockito) sıklıkla JDK'ya
  duyarlıdır; eski sürümleri yeni JDK'da kırılır.
- Build araçları da güncellenmeli: yeni JDK genelde **yeni bir Gradle/Maven** sürümü ister.

### 4) Güvensiz/eski desenlerin gün yüzüne çıkması

Geçiş, mevcut gizli hataları görünür kılabilir. Örnek 2 (`./Ornek2.java`) bunun klasiğini
gösterir: **paylaşımlı `SimpleDateFormat` thread-safe değildir** ve çok thread'li ortamda
sessizce yanlış sonuç üretir veya hata fırlatır (örnekte on binlerce anomali!). Modern
`java.time` (`DateTimeFormatter`, `LocalDate`) değişmezdir ve güvenle paylaşılır. Geçiş, bu tür
eski API'leri modernleriyle değiştirmek için iyi bir fırsattır.

## Bugünkü tablo: 17 ve 21'den geçiş

### Java 17 → 21 (düşük risk, yüksek kazanım)

- **Risk:** Genelde düşüktür; 17'de zaten güçlü kapsülleme yapıldığı için büyük kırılmalar
  arkada kaldı. Asıl dikkat edilecek: framework sürümleri (Spring Boot 3.2+), build aracı
  sürümü ve sanal thread kullanacaksan **`synchronized` bloklarındaki "pinning"** (sanal
  thread'in taşıyıcı thread'e sabitlenmesi) — kritik bölümlerde `ReentrantLock` tercih et.
- **Kazanım:** Sanal thread'ler, record patterns, sequenced collections, generational ZGC.
  Çoğu projede 17→21 "neredeyse bedava" bir yükseltmedir.

### Java 21 → 25 (ölç, kütüphaneleri doğrula)

- **Risk:** Önizleme (preview) olarak gelen bazı özellikler 25'te standartlaştı; davranışları
  ince ayar görmüş olabilir. Kütüphane/araç uyumu yine en büyük belirleyicidir — geçmeden önce
  Spring/Hibernate/build aracı sürümlerinin 25'i desteklediğini doğrula. Kaldırılmaya işaretli
  API'ler (SecurityManager vb.) bu hatta tamamen gidebilir.
- **Kazanım:** Stream Gatherers (özel akış işlemleri), sadeleştirilmiş giriş noktası ve modül
  import bildirimleri, scoped values, sürekli iyileşen GC/JIT performansı, en güncel güvenlik.

### Java 8/11 → 17+ (en büyük sıçrama)

Hâlâ 8 veya 11'deyse bir proje, en zorlu ama en kârlı geçiş budur: kaldırılan EE modülleri,
güçlü kapsülleme ve eski kütüphanelerin hepsi aynı anda karşına çıkar. Adım adım (8→11→17→21)
ilerlemek, riski yönetilebilir parçalara böler.

## Pratik geçiş kontrol listesi

1. **Bağımlılıkları yükselt:** Önce kütüphaneleri/framework'ü hedef JDK'yı destekleyen
   sürümlere çıkar (çoğu kırılma buradan gelir).
2. **Build aracını güncelle:** Gradle/Maven'ı yeni JDK'yı destekleyen sürüme al;
   `--release <hedef>` ile derle.
3. **Uyarıları temizle:** `-Xlint:all -Werror` ile deprecation/removal uyarılarını ciddiye al.
4. **Test, test, test:** Kapsamlı test paketi geçişin sigortasıdır; davranış değişikliklerini
   (UTF-8, biçimlendirme, GC) testlerle yakala.
5. **Aşamalı geçir:** Önce staging/canary ortamında çalıştır, performans ve hata metriklerini
   izle, sonra üretime al.
6. **`--add-opens`/`--add-exports` ihtiyacını gözden geçir** ve mümkünse bu geçici çözümleri
   kütüphane yükseltmesiyle ortadan kaldır.

## Özet

Sürüm geçişinin kazanımlarını (güvenlik, performans, sanal thread'ler, dil ergonomisi) ve
risklerini (kaldırılan/kapsüllenen API'ler, davranış değişiklikleri, kütüphane uyumu, gizli
hataların açığa çıkması) gerçek örneklerle gördük; bugün kullanılan 17 ve 21'den en yeni
sürümlere geçişte ne beklemen gerektiğini ve bir kontrol listesini ele aldık. Çıkarım net:
**desteklenen bir LTS'te kal, bağımlılıkları önce yükselt, testlere güven, aşamalı geç.**

Bu, Java bölümünün son konusudur. Artık dile hâkimsin, ekosistemini ve evrimini biliyorsun.
Sırada, tüm bu temellerin üzerine kurulu kurumsal dünya: **Spring ve Spring Boot**.
