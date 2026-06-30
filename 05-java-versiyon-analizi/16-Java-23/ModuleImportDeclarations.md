# Module Import Declarations (JEP 476) — Detaylı Anlatım

> **JEP 476** — Java 23'te **preview** özelliğidir.
> Derleme/çalıştırmada `--enable-preview` gerekir.

---

## 1. NEDİR?

Normalde Java'da bir sınıfı kullanmak için onu paket bazında import ederiz:

```java
import java.util.List;
import java.util.Map;
```

**Module Import Declarations** ile bir modülün **export ettiği tüm paketleri**
tek satırda içe aktarabilirsiniz:

```java
import module java.base;
```

Bu satır, `java.base` modülünün dışa açtığı (export) **bütün** paketlerdeki
public tipleri (örn. `java.util.List`, `java.io.File`, `java.util.stream.Stream`,
`java.time.LocalDate`, ...) tek seferde kullanılabilir hale getirir.

> Sözdizimi: `import module <ModulAdi>;`
> `<ModulAdi>` bir JPMS (Java Platform Module System) modülüdür
> (örn. `java.base`, `java.sql`, `java.net.http`).

---

## 2. NEDEN GELDİ? (Motivasyon)

1. **Çok sayıda import satırı:** Özellikle çok farklı paketten sınıf kullanan
   dosyalarda onlarca `import` satırı birikir. Bu gürültüyü azaltmak istenir.
2. **Eğitim / yeni başlayanlar:** Yeni öğrenenler için "hangi sınıf hangi
   pakette?" sorusu ve import yönetimi engel oluşturur. Tek satırla geniş bir
   API yüzeyi açmak öğrenmeyi kolaylaştırır.
3. **Script benzeri / tek dosya programlar:** `java Ornek.java` ile doğrudan
   çalıştırılan tek dosyalık programlarda (JEP 330 / 458) hızlı yazım için ideal.
4. **Modüler ekosistemle uyum:** Java modül sistemi (JPMS, Java 9) zaten paketleri
   modüllerde gruplar; modül seviyesinde import bu yapıyla tutarlıdır.

---

## 3. NASIL ÇALIŞIR?

`import module M;` bildirimi, derleyici tarafından şuna eşdeğer kabul edilir:
"M modülünün **export ettiği** her paket için bir `import paket.*;` bildirimi".

Örneğin (kavramsal):

```java
import module java.base;

// kabaca şuna denk gelir (export edilen tüm paketler için):
// import java.util.*;
// import java.io.*;
// import java.util.stream.*;
// import java.time.*;
// import java.lang.*;        (zaten örtük)
// ... (java.base'in export ettiği tüm paketler)
```

### Geçişli (transitive) bağımlılıklar
Bir modül `requires transitive` ile başka modülleri yeniden export edebilir.
`import module M;` bu geçişli olarak okunabilen paketleri de kapsar. Yani
ek modüllerin export ettiği paketler de erişilebilir hale gelebilir.

---

## 4. ESKİ vs YENİ (Tek Tek Import vs Module Import)

### Örnek 1 — Tipik bir veri işleme dosyası

#### ESKİ (tek tek import)
```java
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Rapor {
    // ... 11 satır import
}
```

#### YENİ (module import — preview)
```java
import module java.base;   // java.util, java.util.stream, java.time,
                           // java.io, java.nio.file ... hepsi geldi

public class Rapor {
    // tek satır import
}
```

> `java.base` modülü; `java.util`, `java.io`, `java.nio.*`, `java.time`,
> `java.util.stream`, `java.lang` (zaten örtük) gibi pek çok temel paketi
> export eder. Bunların hepsi tek satırla gelir.

---

### Örnek 2 — Birden çok modül

```java
import module java.base;       // temel JDK
import module java.net.http;   // HttpClient, HttpRequest, HttpResponse
import module java.sql;        // Connection, ResultSet, DriverManager

public class App {
    public static void main(String[] args) throws Exception {
        var istemci = java.net.http.HttpClient.newHttpClient(); // import module sayesinde erişilebilir
        // ...
    }
}
```

#### Aynısı eski tarzda (kısmen)
```java
import java.util.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
// ...
```

---

## 5. İSİM ÇAKIŞMALARI (Önemli Risk)

Geniş import yaptığınızda, **farklı paketlerde aynı isimli sınıflar** çakışabilir.
Klasik örnek:

- `java.util.List` ile `java.awt.List`
- `java.util.Date` ile `java.sql.Date`

```java
import module java.base;     // java.util.List getirir
import module java.desktop;  // java.awt.List getirir → ÇAKIŞMA!

List liste; // HANGİSİ? Derleyici belirsizlik (ambiguity) hatası verir.
```

### Çözüm: çakışan tip için açık (single-type) import
Tekil tip importu, modül importuna göre **önceliklidir**:

```java
import module java.base;
import module java.desktop;
import java.util.List;   // Belirsizliği giderir: List = java.util.List

List liste = new ArrayList<>(); // artık net
```

> **Kural:** Tek-tip import (`import paket.Tip;`), modül importundan gelen aynı
> isimli tipi **gölgeler (override eder)**. Çakışma olduğunda istediğiniz tipi
> açıkça import edin ya da tam nitelikli (`java.util.List`) ad kullanın.

---

## 6. NEREDE KOLAYLIK SAĞLAR?

| Senaryo | Fayda |
|---|---|
| Eğitim / ders / öğrenme | Import yönetimi derdi olmadan kodlamaya odaklanma |
| Hızlı prototip / deneme | Birçok API'yi anında kullanma |
| Tek dosya programlar (`java X.java`) | Minimum boilerplate |
| Veri işleme / script benzeri kod | Onlarca import yerine birkaç satır |

### Nerede DİKKATLİ olunmalı?
| Senaryo | Neden |
|---|---|
| Büyük production kod tabanı | Açık importlar bağımlılıkları görünür kılar; takım okunabilirliği |
| İsim çakışması ihtimali yüksek dosyalar | Belirsizlik hataları |
| Statik analiz / "unused import" temizliği | Modül importu hangi tiplerin gerçekten kullanıldığını gizler |

---

## 7. AVANTAJ / DEZAVANTAJ / RİSK

### Avantajlar
- Çok daha az import satırı, daha temiz dosya başlığı.
- Öğrenme ve prototipleme hızlanır.
- Modül sistemiyle kavramsal uyum.

### Dezavantajlar / Riskler
- **Preview:** `--enable-preview` gerekir; API/sözdizimi değişebilir.
- **İsim çakışmaları:** Belirsizlik hataları; çözmek için yine açık import gerekir.
- **Okunabilirlik tartışması:** Bazı ekipler açık importları (hangi tip nereden
  geliyor) tercih eder; modül importu bunu gizler.
- **Bağımlılık görünürlüğü:** "Hangi paketleri gerçekten kullanıyorum?"
  sorusunun cevabı kaynaktan okunamaz hale gelebilir.

---

## 8. DERLEME VE ÇALIŞTIRMA

Preview özelliği olduğu için bayraklar gerekir:

```bash
# Derleme
javac --release 23 --enable-preview Rapor.java

# Çalıştırma
java --enable-preview Rapor

# Tek dosya başlatma modunda (kaynaktan doğrudan çalıştırma)
java --release 23 --enable-preview Rapor.java
```

> Not: Preview kullanılan sınıflar yalnızca aynı sürümün JVM'inde
> `--enable-preview` ile çalışır; başka sürümde çalışmaz.

---

## 9. GERÇEK HAYAT ÖRNEĞİ

Bir öğretmen, derste basit bir "kelime sayma" programı gösterecek. Klasik
yöntemde tahtaya 6–7 import satırı yazması gerekir ve öğrenciler "bunlar ne?"
diye takılır. Module import ile:

```java
import module java.base;   // preview

public class KelimeSayar {
    public static void main(String[] args) {
        var metin = "java java python java python go";

        // java.util.Map, java.util.stream.Collectors, java.util.Arrays
        // hepsi tek import'tan geldi:
        var sayim = java.util.Arrays.stream(metin.split(" "))
            .collect(java.util.stream.Collectors.groupingBy(
                k -> k,
                java.util.stream.Collectors.counting()));

        sayim.forEach((kelime, adet) ->
            System.out.println(kelime + " -> " + adet));
    }
}
```

> Burada dersin odağı "import nereden?" değil, akış (stream) ve gruplama
> mantığı olur. Module import dikkat dağıtıcı boilerplate'i kaldırır.

---

## 10. ÖZET

| Konu | Açıklama |
|---|---|
| Sözdizimi | `import module M;` |
| Ne yapar | M modülünün export ettiği tüm paketleri içe aktarır |
| Durum | **Preview** (Java 23) — `--enable-preview` gerekir |
| En çok faydalı | Eğitim, prototip, tek dosya programlar |
| En büyük risk | İsim çakışmaları (belirsizlik) ve okunabilirlik tartışması |
| Çakışma çözümü | İlgili tip için açık `import paket.Tip;` (öncelikli) |

---

*Bu doküman Java 23 / JEP 476 baz alınarak hazırlanmıştır.*
