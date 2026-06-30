# Paketleme ve Dağıtım (JAR, Multi-Release JAR, jlink, jpackage)

Kodu yazıp derlemek bir şey; onu **başkalarının çalıştırabileceği** bir artefakt olarak paketleyip
dağıtmak başka bir şeydir. Bu konu, Java uygulamalarını paketleme ve dağıtma araçlarını ele alır:
klasik **JAR**'dan, sürüme duyarlı **Multi-Release JAR**'a, özel çalışma zamanı üreten **jlink**'e
ve yerel kurulum paketi yapan **jpackage**'a kadar. Build araçları konusunda bağımlılık
yönetimini gördük; burada "çıktıyı nasıl dağıtırım?" sorusuna odaklanıyoruz.

## JAR (Java ARchive)

Bir JAR, derlenmiş `.class` dosyalarını ve kaynakları (resources: config, görsel, şablon) tek bir
**ZIP tabanlı arşivde** toplar. İçinde bir **`META-INF/MANIFEST.MF`** dosyası bulunur (meta-veri:
ana sınıf, sürüm, classpath).

```bash
jar --create --file uygulama.jar --main-class com.app.Main -C build/classes .
java -jar uygulama.jar          # MANIFEST'teki Main-Class çalışır
```

Sınıflar ister bir dizinden ister bir JAR'dan yüklensin, çalışma anında **nereden geldiği**
görülebilir (`getProtectionDomain().getCodeSource()`). Kaynaklar dosya yoluyla değil,
**ClassLoader** üzerinden okunur (`getResource`/`getResourceAsStream`) — çünkü JAR içindedirler.
Örnek 1 (`./Ornek1.java`) bunu gösterir.

## Fat/Uber JAR

Sıradan bir JAR yalnızca senin sınıflarını içerir; bağımlılıkların ayrıca classpath'te olmalıdır.
**Fat JAR** (uber JAR), tüm bağımlılıkları da içine alır → tek dosyayla çalışır. **Spring Boot'un
çalıştırılabilir JAR'ı** budur: `java -jar uygulama.jar` ile gömülü Tomcat dahil her şey çalışır
(bu portalın backend'i de böyle paketlenebilir). Maven Shade / Gradle Shadow eklentileri üretir.

## Multi-Release JAR (MR-JAR)

Java 9 ile gelen MR-JAR, **tek bir JAR içinde farklı Java sürümleri için farklı sınıflar** tutar:

```
uygulama.jar
├── com/app/Util.class            (varsayılan / eski JDK)
└── META-INF/versions/
    ├── 17/com/app/Util.class     (Java 17 için)
    └── 21/com/app/Util.class     (Java 21 için)
```

JVM, **kendi sürümüne uygun** sınıfı otomatik seçer. Bu, çalışma anında `Runtime.version()` ile
tespit edilen sürüme göre en uygun kodun çalışmasını sağlar (Örnek 2, `./Ornek2.java`). Kütüphaneler
için idealdir: tek artefakt hem eski hem yeni JDK'da en iyi yolu kullanır. MANIFEST'te
`Multi-Release: true` bulunur.

## jlink: özel çalışma zamanı

`jlink` (Java 9+), uygulamanın ihtiyaç duyduğu **yalnızca gerekli modülleri** içeren, küçük ve
bağımsız bir çalışma zamanı (JRE) üretir:

```bash
jlink --add-modules java.base,java.sql --output ozel-runtime
```

Faydası: tam JDK yerine **çok daha küçük** bir imaj → konteynerler (Docker) ve dağıtım için ideal.
(Java 11'den beri ayrı JRE indirmesi olmadığı için, minimal çalışma zamanını jlink ile üretirsin .)

## jpackage: yerel kurulum paketi

`jpackage` (Java 14+), uygulamanı işletim sistemine özel **yerel kurulum paketine** dönüştürür:
Windows'ta `.exe`/`.msi`, macOS'ta `.dmg`/`.pkg`, Linux'ta `.deb`/`.rpm`. İçine bir çalışma zamanı
(jlink ile) gömer, böylece kullanıcının ayrıca Java kurmasına **gerek kalmaz**:

```bash
jpackage --name Uygulamam --input lib --main-jar uygulama.jar --type dmg
```

Masaüstü uygulamaları (Swing/JavaFX) dağıtmak için idealdir.

## Hangi araç ne için?

| Araç | Üretir | Kullanım |
|------|--------|----------|
| `jar` | `.jar` arşivi | Temel paketleme |
| Fat/Shadow JAR | Bağımlılıklı tek JAR | Sunucu uygulamaları (Spring Boot) |
| Multi-Release JAR | Sürüme duyarlı JAR | Çok-JDK destekli kütüphaneler |
| `jlink` | Özel/küçük çalışma zamanı | Konteyner, minimal dağıtım |
| `jpackage` | Yerel kurulum (.exe/.dmg/.deb) | Masaüstü uygulama dağıtımı |

Üretimde bunlar genelde **build araçlarıyla** otomatikleşir (Maven/Gradle eklentileri) ve
konteyner imajlarına (Docker) gömülür.

## Özet

Java uygulamalarını paketleme ve dağıtma araçlarını öğrendik: kaynakları toplayan **JAR** ve
manifest, bağımlılıkları içeren **fat JAR** (Spring Boot); sürüme duyarlı **Multi-Release JAR** ve
`Runtime.version()` (Örnek 1–2); minimal çalışma zamanı üreten **jlink** ve yerel kurulum yapan
**jpackage**. Doğru paketleme, kodun başkalarına kolay ve güvenilir ulaşmasını sağlar.
