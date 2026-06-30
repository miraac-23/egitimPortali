# Kaldırılmış ve Eski (Legacy) Özellikler

Java 25+ yıllık bir dildir ve geriye dönük uyumluluğa büyük önem verir. Yine de zamanla bazı
özellikler **kullanımdan kaldırılır** (deprecated) ve sonunda **silinir** (removed). Bir
geliştirici olarak, eski öğretici/kod örneklerinde bunlara rastlayabilir ve "neden çalışmıyor?"
diye şaşırabilirsin. Bu konu, tarihsel ama artık önerilmeyen/kaldırılmış özellikleri ve modern
karşılıklarını topluca ele alır — böylece neyi **kullanmayacağını** bilirsin.

> Bu konunun örneği, kaldırmaları **çalışan kanıtla** gösterir (örn. Nashorn motorunun artık
> bulunmadığı). Applet gibi tamamen kaldırılan özellikler çalıştırılamaz; yalnızca tarihsel olarak
> anlatılır.

## Tamamen kaldırılanlar (artık çalışmaz)

| Özellik | Neydi | Ne zaman | Modern karşılığı |
|---------|-------|----------|------------------|
| **Applet** (`java.applet`) | Tarayıcıda çalışan Java | Kullanımdan kalktı, JDK'dan kaldırıldı | Web (Spring Boot + JS), masaüstü için JavaFX/Swing |
| **Nashorn** | JDK'nın JavaScript motoru | Java 11 deprecated, **15 kaldırıldı** | GraalVM JavaScript (ayrı bağımlılık) |
| **CORBA, Java EE modülleri** (JAXB, JAX-WS) | Dağıtık/XML standartları | **Java 11 kaldırıldı** | Ayrı Maven/Gradle bağımlılıkları |
| **`Thread.stop/suspend/resume`** | Thread kontrolü | Çok eski, deprecated | `interrupt()` + bayrak (topic 15) |

Örnek 1 (`./Ornek1.java`) `ScriptEngineManager` ile Nashorn'un artık **olmadığını** kanıtlar
(motor `null`, yerleşik motor sayısı genelde 0).

## Deprecated (çalışır ama kullanma)

- **`finalize()`**: Nesne çöpe atılmadan önce çağrılırdı; **öngörülemez** (ne zaman/çağrılır mı
  belirsiz), performansı bozar. Java 9'da deprecated. → **`try-with-resources`** (`AutoCloseable`)
  veya `java.lang.ref.Cleaner`.
- **Security Manager**: Java 17'de deprecated (kaldırılacak).
- **İlkel wrapper constructor'ları** (`new Integer(5)`): deprecated. → `Integer.valueOf(5)` veya
  autoboxing.

## "Çalışır ama eski" (modern karşılığı tercih edilir)

Bunlar kaldırılmadı ama yeni kodda **kullanılmaması** önerilir:

| Eski | Modern (tercih) | Konu |
|------|-----------------|------|
| `Date`, `Calendar`, `SimpleDateFormat` | `java.time` (`LocalDate`...) | topic 88 |
| `Vector`, `Stack`, `Hashtable` | `ArrayList`, `ArrayDeque`, `HashMap` | topic 45 |
| `Enumeration` | `Iterator` / for-each | topic 45 |
| `new URL(String)` | `URI` + `toURL()` | topic 32 |
| Java serileştirme | JSON (Jackson) / Protobuf | topic 68 |

## Neden özellikler kaldırılır?

- **Güvenlik:** Applet'ler büyük güvenlik açıklarıydı.
- **Bakım yükü:** Az kullanılan ama bakımı pahalı özellikler (Nashorn).
- **Daha iyi alternatif:** `java.time`, `Cleaner` gibi modern, doğru tasarlanmış API'ler.
- **Modülerlik:** Java EE modülleri çekirdekten çıkarılıp bağımsız projelere taşındı.

## Pratik ders

- Bir API'nin **var olması**, onu kullanman gerektiği anlamına gelmez. Deprecated işaretine ve
  IDE uyarılarına dikkat et (topic 81).
- **Sürüm notlarını izle:** LTS'ler arası geçişte (8→11→17→21) kaldırılan özellikler en sık geçiş
  sorunudur (topic 21).
- Eski örnek/öğretici görürsen, modern karşılığını tercih et (bu portaldaki konular hep modern yolu
  gösterir).

## Özet

Java'nın zamanla kaldırdığı/önermediği özellikleri öğrendik: tamamen kaldırılanlar (Applet, Nashorn,
CORBA/Java EE — kanıtla, Örnek 1), deprecated olanlar (`finalize`, Security Manager) ve "çalışır ama
eski" olanlar (`Date`, `Vector`, `Enumeration`...) ve hepsinin modern karşılıkları; neden
kaldırıldıklarını ve pratik dersi gördük. Bu, listedeki tarihsel/niş başlıkları kapsayarak Java
bölümünün kapsamını tamamlıyor.
