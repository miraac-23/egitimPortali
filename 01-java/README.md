# Java

Bu bölüm, Java'yı sıfırdan sağlam bir temele oturtmak için tasarlandı. Konular birbirinin
üzerine inşa olacak şekilde sıralandı: önce dilin söz dizimi ve akışı, sonra nesne yönelimli
düşünme, ardından günlük araçlar (koleksiyonlar, metin, hatalar), sonra modern Java
(lambda, stream, Optional) ve nihayet dış dünyayla konuşan ileri konular (I/O, eşzamanlılık,
reflection, JDBC) ile ekosistem (build araçları, tasarım desenleri).

Her konu klasöründe **akıcı bir anlatım** (`README.md`) ve **çalıştırılabilir üç örnek**
(`Ornek1.java`, `Ornek2.java`, `Ornek3.java`) bulunur. Örnekler tek dosyalık saf Java'dır;
`java Ornek1.java` ile çalışır. Bu portalda her örneğin yanındaki **Çalıştır** düğmesiyle
kodu backend'de çalıştırıp çıktısını anında görebilirsin.

## Konu haritası

**Dile giriş ve akış**
- `00-temeller` — Java/JVM, değişkenler, tipler, operatörler, çıktı
- `01-kontrol-yapilari` — if/switch, döngüler, break/continue
- `02-metotlar-ve-diziler` — metotlar, overloading, varargs, diziler

**Nesne yönelimli programlama**
- `03-oop-temelleri` — sınıf/nesne, kapsülleme, constructor, static/final
- `04-kalitim-ve-cok-bicimlilik` — extends, super, override, polymorphism
- `05-soyutlama-interface` — abstract sınıflar ve interface'ler

**Günlük araçlar**
- `06-string-ve-metin` — String, StringBuilder
- `07-hata-yonetimi` — exception'lar, try/catch, custom exception
- `08-collections` — List, Set, Map
- `09-generics` — jenerikler, bounded type, wildcard
- `10-enum-ve-wrapper` — enum, wrapper, autoboxing

**Modern Java**
- `11-lambda-ve-functional-interface` — lambda, java.util.function, method reference
- `12-stream-api` — akışlarla veri işleme
- `13-optional` — null güvenliği

**İleri konular ve ekosistem**
- `14-dosya-ve-io` — I/O ve NIO.2
- `15-coklu-is-parcacigi-ve-eszamanlilik` — thread, synchronized, Executor
- `16-reflection-ve-annotations` — çalışma zamanı inceleme ve meta-veri
- `17-jdbc` — veritabanı bağlantısı
- `18-build-araclari-ve-bagimlilik` — Maven/Gradle, SemVer
- `19-tasarim-desenleri` — yaratımsal/yapısal/davranışsal desenler

**Sürümler ve geçiş**
- `20-surum-evrimi-java8-25` — Java 8'den 25'e sürüm farkları ve özellikler
- `21-surum-gecisi-riskler-ve-kazanimlar` — geçiş avantaj/dezavantajları, 17/21 → en yeni riskleri

## Önerilen sıra

Numaralar önerilen okuma sırasıdır. Başlangıç seviyesindeysen `00`'dan başla; her konunun
sonundaki "Sırada..." cümlesi seni bir sonraki adıma taşır. Bu bölümü bitirdiğinde, **Spring**
ve **Spring Boot** bölümlerine geçmeye hazır olacaksın — çünkü onların altında yatan tüm
fikirleri (nesneler, arayüzler, gevşek bağlılık, reflection, desenler) burada görmüş olacaksın.
