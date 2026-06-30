# Paketler (Packages)

Bir proje büyüdükçe yüzlerce, binlerce sınıf olur. Bunları tek bir klasöre yığmak hem kaosa yol
açar hem de isim çakışmalarına (iki farklı `Date` sınıfı gibi) neden olur. **Paketler**, sınıfları
mantıksal gruplara ayıran ve her birine benzersiz bir isim alanı (namespace) veren mekanizmadır.
Java'nın kendi kütüphanesi de paketlerle düzenlenmiştir: `java.util`, `java.io`, `java.net`...

## Paket nedir?

Paket, ilişkili sınıfları bir araya getiren isimlendirilmiş bir gruptur. Bir sınıfın paketini,
dosyanın başındaki `package` bildirimi belirler ve bu, klasör yapısıyla eşleşir:

```java
package com.egitim.uygulama.servis;  // dosya: com/egitim/uygulama/servis/SiparisServisi.java
public class SiparisServisi { ... }
```

Bir sınıfın **tam nitelikli adı (fully-qualified name, FQN)** = paket + sınıf adı:
`com.egitim.uygulama.servis.SiparisServisi`.

> **İsimlendirme geleneği:** Paket adları, çakışmayı önlemek için kurumun ters domain'iyle
> başlar: `com.sirket.proje.modul`. Hepsi küçük harftir.

> **Not:** Bu portalın örnekleri tek dosyada (default pakette) çalışır; bu yüzden kendi
> paketlerimizi oluşturmak yerine, JDK'nin paketlerini (java.util, java.math...) kullanarak
> import ve FQN kavramlarını gösteriyoruz.

## import: kısa adla kullanım

Başka bir paketteki sınıfı kullanmak için ya tam adını yazarsın ya da `import` ile kısa adını
getirirsin:

```java
import java.util.List;          // tek sınıf
import java.util.*;             // paketteki tüm sınıflar (yıldız)
List<String> liste = ...;       // artık kısa adla
```

`java.lang` paketi (String, System, Math, Integer...) **otomatik** içe aktarılır; onları import
etmen gerekmez. Örnek 1 (`./Ornek1.java`) import'u, tam nitelikli adı ve bir **isim çakışmasının**
(`java.util.Date` vs `java.sql.Date`) FQN ile nasıl çözüldüğünü gösterir.

## static import

Normal `import` bir **sınıfı** getirir; `static import` ise bir sınıfın **statik üyelerini**
(alan/metot) doğrudan kullanmanı sağlar:

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
double alan = PI * sqrt(r);   // Math.PI / Math.sqrt yerine
```

Matematik ağırlıklı kod ve testlerde (`assertEquals`) okunabilirliği artırır. Örnek 2
(`./Ornek2.java`) `Math` ve `Collectors`'tan static import gösterir. Aşırı kullanım "bu metot
nereden geliyor?" karışıklığı yarattığından ölçülü kullanılmalıdır.

## Paket-özel erişim ve düzen

Erişim belirleyiciler bölümünde gördüğümüz **default (paket-özel)** erişim, paketlerle birlikte
anlam kazanır: belirleyici yazılmayan üyeler yalnızca aynı paketten erişilir. Bu, bir paketin
"iç" sınıflarını dışarıya kapatmak için kullanılır.

`package-info.java` adlı özel bir dosya ile bir pakete dokümantasyon ve paket düzeyi anotasyonlar
eklenebilir.

## Derleme, classpath ve JAR

- Paketler, derlenmiş `.class` dosyalarının klasör yapısına yansır (`com/egitim/...`).
- **CLASSPATH**, JVM'in sınıfları nerede arayacağını söyler.
- Bir kütüphane, paketler hâlinde düzenlenmiş sınıfların bir **JAR** dosyasında paketlenmiş
  halidir (Build araçları bölümünde gördük).
- **Modül sistemi (JPMS, Java 9+)**, paketlerin üstünde bir katman daha ekler: hangi paketlerin
  dışa açılacağını (`exports`) `module-info.java` ile belirtirsin (ileri konu).

## Özet

Paketlerin sınıfları nasıl gruplayıp isim çakışmalarını önlediğini; `package` bildirimini ve FQN'i;
`import` ile kısa kullanımı ve isim çakışması çözümünü (Örnek 1); `static import`'u (Örnek 2);
paket-özel erişimi, classpath ve JAR ilişkisini öğrendik. Düzenli paket yapısı, büyük projeleri
yönetilebilir kılar. Sırada, nesneler arası "sahip olma" (HAS-A) ilişkisi: **aggregation ve
composition**.
