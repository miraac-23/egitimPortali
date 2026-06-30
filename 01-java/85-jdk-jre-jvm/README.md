# JDK vs JRE vs JVM

Java dünyasına yeni girenlerin en çok karıştırdığı üç kısaltma: **JDK**, **JRE**, **JVM**. Üçü de
ilişkilidir ama farklı şeylerdir ve birbirinin içine geçer. Bu ayrımı netleştirmek, "neyi
kurmalıyım?", "üretim sunucusunda ne gerekir?" gibi pratik soruları yanıtlamanı sağlar.

## Üç kavram

```
┌─────────────────────────── JDK (geliştirme) ───────────────────────────┐
│  javac, jar, javadoc, jdb, jshell, jlink, jpackage ...  (araçlar)       │
│  ┌──────────────────────── JRE (çalıştırma) ─────────────────────────┐  │
│  │  Standart kütüphaneler (java.*, javax.*)                          │  │
│  │  ┌──────────────────── JVM (yürütme) ────────────────────────┐    │  │
│  │  │  Class Loader + Bellek Alanları + Execution Engine         │    │  │
│  │  └────────────────────────────────────────────────────────────┘   │  │
│  └─────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
       JDK ⊃ JRE ⊃ JVM
```

- **JVM (Java Virtual Machine):** Bytecode'u **çalıştıran** soyut makine. Tek başına
  dağıtılmaz; her zaman bir JRE/JDK'nın parçasıdır.
- **JRE (Java Runtime Environment):** JVM + Java programlarını çalıştırmak için gereken **standart
  kütüphaneler**. Yalnızca **çalıştırmak** için yeterlidir — derleyici **yoktur**.
- **JDK (Java Development Kit):** JRE + **geliştirme araçları** (`javac` derleyici, `jar`,
  `javadoc`, `jdb`, `jlink`...). Java **geliştirmek/derlemek** için gereklidir.

## Pratikte ne gerekir?

| İhtiyaç | Gereken |
|---------|---------|
| Java kodu **yazmak/derlemek** | **JDK** |
| Bir Java uygulamasını yalnızca **çalıştırmak** | JRE (veya JDK) |
| Bytecode'u **yürütmek** (motor) | JVM (zaten JRE/JDK içinde) |

Örnek 1 (`./Ornek1.java`) çalışan ortamı tanır (sürüm, satıcı, `java.home`). Örnek 2
(`./Ornek2.java`) farkı **kanıtlar**: `ToolProvider.getSystemJavaCompiler()` bir JDK'da non-null
(derleyici var), salt JRE'de null olurdu. (Bu portal bir JDK üzerinde çalışır; örnekleri
`java Dosya.java` ile derleyip koşar.)

## JDK araçları

JDK'nın getirdiği başlıca komut satırı araçları:

- **`javac`**: Kaynağı (.java) bytecode'a (.class) derler.
- **`java`**: Bytecode'u çalıştırır (ve Java 11+'da tek dosyayı doğrudan derleyip koşar).
- **`jar`**: Sınıfları bir `.jar` arşivine paketler.
- **`javadoc`**: Kaynak yorumlarından API dokümanı üretir.
- **`jshell`**: Etkileşimli REPL (Java 9+).
- **`jlink` / `jpackage`**: Özel/küçük çalışma zamanı ve yerel kurulum paketi üretir.

## Sürüm ve dağıtım notları

- **Java 11'den beri** Oracle ayrı bir "JRE" indirmesi sunmuyor; tek indirme JDK'dır. Üretim için
  ihtiyacın olan minimal çalışma zamanını **`jlink`** ile kendin üretirsin (daha küçük imaj,
  konteynerler için ideal).
- **Dağıtımlar:** OpenJDK temel alınarak birçok sağlayıcı vardır (Eclipse **Temurin** — bu projenin
  kullandığı, Amazon Corretto, Azul Zulu, Microsoft Build of OpenJDK...). Hepsi aynı standardı
  uygular; destek/araç farkları olabilir.

## Özet

Üç kavramı netleştirdik: bytecode'u yürüten **JVM**, çalıştırma için kütüphaneleri ekleyen **JRE**
ve geliştirme araçlarını ekleyen **JDK** (`JDK ⊃ JRE ⊃ JVM`); pratikte ne zaman hangisinin
gerektiğini, JDK araçlarını ve derleyicinin varlığıyla farkın kanıtını (Örnek 1–2) öğrendik. Sırada,
tipler arası dönüşüm: **Type Casting**.
