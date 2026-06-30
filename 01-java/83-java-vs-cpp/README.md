# Java vs C++

Java, 1990'larda büyük ölçüde C++'ın **karmaşıklığına ve tehlikelerine** bir yanıt olarak
tasarlandı. C++'ın C-benzeri sözdizimini ve nesne yönelimli yapısını korurken; elle bellek
yönetimi, işaretçiler ve çoklu kalıtım gibi "ayağına sıkma" risklerini ortadan kaldırdı. İki dili
karşılaştırmak, Java'nın neden bu kadar yaygın (özellikle kurumsal/sunucu tarafı) olduğunu ve
tasarım felsefesini anlamanı sağlar.

## Temel felsefe farkı

- **C++:** Maksimum kontrol ve performans. Donanıma yakın, elle bellek yönetimi, "sıfır maliyet
  soyutlamaları". Güçlü ama hata yapması kolay.
- **Java:** Güvenlik, taşınabilirlik ve üretkenlik. Yönetilen (managed) çalışma zamanı (JVM), çöp
  toplayıcı, güçlü tip ve bellek güvenliği. "Bir kez yaz, her yerde çalıştır."

## Bellek ve güvenlik

| | C++ | Java |
|---|-----|------|
| Bellek yönetimi | Elle (`new`/`delete`, `malloc`/`free`) | Otomatik (Garbage Collector) |
| İşaretçiler | Var (işaretçi aritmetiği) | Yok (yalnızca referanslar) |
| Dizi sınır denetimi | Yok (tanımsız davranış) | Var (`ArrayIndexOutOfBoundsException`) |
| null erişimi | Çökme/segfault olabilir | Kontrollü `NullPointerException` |
| Bellek sızıntısı | Kolay (unutulan `delete`) | Nadir (GC), ama mümkün (tutulan referans) |

Örnek 1 (`./Ornek1.java`) bu güvenlik özelliklerini çalışan kanıtla gösterir: dizi sınır denetimi
exception atar, `delete`/`free` yoktur, null erişimi kontrollü hata verir (segfault değil).

## Nesne modeli

| | C++ | Java |
|---|-----|------|
| Kalıtım | Çoklu sınıf kalıtımı ("elmas problemi") | Tek sınıf + çoklu **interface** |
| Operatör aşırı yükleme | Var (`operator+`) | **Yok** (yalnızca `+` sayı/String) |
| Serbest fonksiyon | Var (global) | Yok (her şey sınıf içinde; statik metot) |
| Header/.cpp ayrımı | Var | Yok (tek dosya, JVM derler/yükler) |
| Şablonlar/jenerikler | Template (derleme zamanı, güçlü) | Generics (tip silme/erasure ile) |

Örnek 2 (`./Ornek2.java`) tek kalıtım + çoklu interface'i, operatör aşırı yüklemenin yokluğunu
(toplama için metot yazılır) ve "her şey sınıf içinde" ilkesini gösterir.

## Performans

- **C++:** Doğrudan makine koduna derlenir; dikkatli yazılırsa en yüksek ham performans ve
  öngörülebilir gecikme (gerçek zamanlı sistemler, oyun motorları, sürücüler).
- **Java:** JIT derleyici sayesinde **çoğu sunucu yükünde C++'a yakın** performans verir; ama JVM
  başlangıç süresi ve GC duraklamaları vardır. Üretkenlik ve güvenlik avantajı çoğu uygulamada bu
  farkı önemsiz kılar.

## Hangisi ne için?

- **C++:** İşletim sistemleri, gömülü/gerçek-zamanlı sistemler, oyun motorları, yüksek frekanslı
  ticaret, donanıma yakın performans-kritik kod.
- **Java:** Kurumsal/sunucu uygulamaları, web backend'leri (Spring), Android (tarihsel), büyük
  ekipli uzun ömürlü projeler, taşınabilirlik ve bakım kolaylığı önemli olan her yer.

## Özet

Java'nın C++'a kıyasla **güvenlik ve taşınabilirliği** önceleyen tasarımını öğrendik: otomatik
bellek (GC), işaretçi yokluğu, dizi sınır denetimi ve kontrollü null (Örnek 1); tek kalıtım + çoklu
interface, operatör aşırı yüklemesinin olmayışı ve "her şey sınıf içinde" nesne modeli (Örnek 2);
performans dengelerini ve kullanım alanlarını gördük. Sırada, Java'nın "her yerde çalış" sözünü
mümkün kılan motor: **JVM mimarisi**.
