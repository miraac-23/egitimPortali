# Properties Sınıfı

**`Properties`**, anahtar-değer çiftlerinden oluşan bir yapılandırma deposudur ve hem anahtarlar
hem değerler **String**'tir. Java uygulamalarında ayarları (veritabanı adresi, port, özellik
bayrakları) ve uluslararasılaştırma (i18n) metinlerini tutmanın klasik yoludur. `.properties` metin
formatını okuyup yazar; `Hashtable`'dan türer ama özellikle yapılandırma için tasarlanmıştır.

## Temel kullanım

```java
Properties config = new Properties();
config.setProperty("server.port", "8080");
config.getProperty("server.port");          // "8080"
config.getProperty("timeout", "30");        // yoksa varsayılan "30"
```

`getProperty`'nin **varsayılan değerli** sürümü çok kullanışlıdır: anahtar yoksa `null` yerine
verdiğin değeri döndürür. Örnek 1 (`./Ornek1.java`) ayarlama, okuma ve varsayılanı gösterir.

## Dosyaya yazma/okuma: store / load

`Properties`, `.properties` metin formatını okuyup yazabilir:

```java
config.store(writer, "yorum");   // anahtar=değer satırları + tarih/yorum
props.load(reader);              // .properties metnini geri oku
```

`.properties` formatı basittir:

```properties
# yorum satırı
db.url=jdbc:postgresql://localhost/app
db.user=admin
server.port=8080
```

XML formatı için `storeToXML`/`loadFromXML` da vardır. Örnek 1 `store`/`load` döngüsünü gösterir
(dosya yerine `StringWriter`/`StringReader` ile, ama davranış aynıdır).

## Sistem özellikleri (System Properties)

JVM, çalışma ortamı hakkında hazır özellikler sunar; bunlara `System.getProperty` ile erişilir:

```java
System.getProperty("java.version");   // JVM sürümü
System.getProperty("os.name");        // işletim sistemi
System.getProperty("user.dir");       // çalışma dizini
System.getProperty("ayar", "varsayılan");
System.setProperty("uygulama.modu", "uretim");  // çalışma anında ayarla
```

Komut satırından da verilebilir: `java -Duygulama.modu=test Uygulama`. Örnek 2 (`./Ornek2.java`)
sistem özelliklerini ve **ortam değişkenlerini** (`System.getenv("PATH")`) gösterir.

> **Property vs env var:** System property JVM'e özeldir ve değiştirilebilir; ortam değişkeni
> (`getenv`) işletim sisteminden gelir ve salt okunurdur.

## Properties'ten modern yapılandırmaya

`Properties` hâlâ geçerlidir ama modern uygulamalarda genelde üst seviye araçlar kullanılır:

- **Spring Boot:** `application.properties` veya `application.yml` ile yapılandırma; `@Value` ve
  `@ConfigurationProperties` ile tipli, doğrulanmış ayarlar (bu portalın backend'i de böyle
  yapılandırılır). Spring, perde arkasında bu `Properties` mekanizmasının üzerine kuruludur.
- **Ortam değişkenleri / .env:** Konteyner (Docker/K8s) dünyasında ayarlar genelde env var ile
  enjekte edilir (12-factor app).

## Özet

`Properties`'in String anahtar-değer yapılandırma deposu olduğunu; `setProperty`/`getProperty`
(varsayılanlı) ve `store`/`load` ile `.properties` okuma-yazmayı (Örnek 1); sistem özelliklerini ve
ortam değişkenlerini (Örnek 2) öğrendik; Spring Boot yapılandırmasıyla ilişkisine değindik. Sırada,
matematiksel işlemler: **Math sınıfı**.
