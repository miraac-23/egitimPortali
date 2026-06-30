# Build Araçları ve Bağımlılık Yönetimi (Maven / Gradle)

Tek dosyalık örneklerden gerçek projelere geçtiğinde yeni bir ihtiyaç doğar: onlarca kaynak
dosyayı derlemek, başkalarının yazdığı kütüphaneleri (bağımlılıkları) indirip projeye eklemek,
testleri çalıştırmak ve sonunda dağıtılabilir bir paket (jar) üretmek. Bunları elle yapmak
imkânsıza yakındır. İşte **build araçları** — Java dünyasında **Maven** ve **Gradle** — bu işi
otomatikleştirir.

## Neden build aracı?

Diyelim ki projende JSON işlemek için bir kütüphane kullanmak istiyorsun. Build aracı olmadan:
jar'ı bul, indir, doğru klasöre koy, classpath'e ekle, onun da bağımlılıklarını bul... Build
aracıyla ise tek bir satır yazarsın; gerisini o halleder:

- **Bağımlılık yönetimi:** Kütüphaneleri (ve onların bağımlılıklarını) otomatik indirir.
- **Derleme ve test:** Kaynakları derler, testleri çalıştırır.
- **Paketleme:** Çalıştırılabilir jar/war üretir.
- **Yaşam döngüsü:** Tüm bu adımları standart, tekrarlanabilir hale getirir.

Kütüphaneler merkezi bir depodan (genelde **Maven Central**) çekilir ve `groupId:artifactId:version`
koordinatıyla tanımlanır (örn. `com.google.code.gson:gson:2.11.0`).

## Maven

Maven, XML tabanlı `pom.xml` dosyasıyla yapılandırılır ve katı bir yaşam döngüsü (compile →
test → package → install) izler:

```xml
<project>
  <groupId>com.egitim</groupId>
  <artifactId>uygulamam</artifactId>
  <version>1.0.0</version>
  <dependencies>
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
      <version>2.11.0</version>
    </dependency>
  </dependencies>
</project>
```

Sık komutlar: `mvn compile`, `mvn test`, `mvn package`.

## Gradle

Gradle daha esnek ve kısadır; Groovy veya Kotlin DSL kullanır. Bu eğitim deposundaki Spring
örnekleri de Gradle ile çalışır:

```gradle
plugins { id 'java' }
repositories { mavenCentral() }
dependencies {
    implementation 'com.google.code.gson:gson:2.11.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}
```

Sık komutlar: `gradle build`, `gradle test`, `gradle run`. Gradle artımlı (incremental) derleme
ve build cache ile büyük projelerde hız avantajı sunar.

## Semantic Versioning (SemVer)

Bağımlılıkların sürümleri rastgele değildir; çoğu kütüphane **MAJOR.MINOR.PATCH** kuralını izler:

- **MAJOR**: Geriye dönük uyumluluğu **kıran** değişiklik (örn. `1.x` → `2.0`).
- **MINOR**: Geriye uyumlu **yeni özellik** (`1.2` → `1.3`).
- **PATCH**: Geriye uyumlu **hata düzeltmesi** (`1.2.0` → `1.2.1`).

Bu kural, build aracının "hangi sürüm güvenle yükseltilebilir?" sorusunu yanıtlamasını sağlar.
Örnek 1 (`./Ornek1.java`) SemVer karşılaştırmasını ve `^1.2.0` türü uyumluluk kuralını
modelleyen küçük bir program içerir.

## Modüller, bağımlılıklar ve bağlama

Bir bağımlılık, aslında "başkasının yazdığı, yeniden kullandığın bir modül"dür. Örnek 2
(`./Ornek2.java`) bunu bir adım küçükte gösterir: kendi `MetinAraclari` yardımcı sınıfını bir
"kütüphane" gibi yazıp kullanır. Gerçek projede bu sınıf ayrı bir jar olur ve build aracı onu
projene eklerdi; mantık aynıdır — hazır, test edilmiş kodu yeniden kullanmak.

Projeyi oluştururken modülleri birbirine **bağlamak** (wiring) gerekir. Örnek 3
(`./Ornek3.java`) elle bağımlılık enjeksiyonunu gösterir: üst seviye bir servis, alt seviye
modüllere somut sınıflar yerine **arayüzler** üzerinden bağlanır ve bağımlılıklarını
constructor'dan alır. Bu "gevşek bağlılık" fikri, az sonra Spring'de IoC container'ın
otomatikleştirdiği şeyin ta kendisidir.

## Özet

Build araçlarının neden vazgeçilmez olduğunu; Maven (`pom.xml`) ve Gradle (`build.gradle`)
yaklaşımlarını; bağımlılık koordinatlarını, Maven Central'ı ve Semantic Versioning'i öğrendik.
Modülleri/bağımlılıkları arayüzler üzerinden bağlamanın gevşek bağlı, sürdürülebilir tasarımın
anahtarı olduğunu gördük. Sırada, tecrübeyle damıtılmış çözüm kalıpları: **tasarım desenleri**.
