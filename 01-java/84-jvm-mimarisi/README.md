# JVM Mimarisi

**JVM (Java Virtual Machine)**, Java'nın "bir kez yaz, her yerde çalıştır" (write once, run
anywhere) sözünü mümkün kılan motordur. Java kodu doğrudan makine koduna değil, platformdan
bağımsız **bytecode**'a derlenir; JVM bu bytecode'u her işletim sisteminde çalıştırır. JVM'in iç
yapısını anlamak, bellek davranışını, performansı ve hata türlerini (`OutOfMemoryError`,
`StackOverflowError`) kavramanı sağlar.

## Derleme ve çalıştırma akışı

```
Merhaba.java  --(javac: derleme)-->  Merhaba.class (bytecode)  --(JVM: yükle+yürüt)-->  çalışma
```

Bytecode **platformdan bağımsızdır**: aynı `.class` dosyası Windows, Linux, macOS'ta — o platformun
JVM'i varsa — aynı şekilde çalışır. Taşınabilirliğin sırrı budur.

## JVM'in üç ana bileşeni

1. **Class Loader (Sınıf Yükleyici):** `.class` dosyalarını JVM'e yükler — gecikmeli (lazy), yani
   ihtiyaç duyulunca. Hiyerarşiktir:
   - **Bootstrap:** JDK çekirdek sınıfları (`java.lang.*` — `String` gibi). `getClassLoader()`
     bunlar için `null` döndürür.
   - **Platform:** Diğer standart kütüphaneler.
   - **Application:** Senin classpath'indeki sınıflar.

2. **Runtime Data Areas (Bellek Alanları):** Çalışma sırasında belleği bölümler:
   - **Heap:** Tüm nesnelerin yaşadığı **paylaşılan** alan; **GC** burada çalışır (genç/yaşlı
     kuşak). Dolarsa `OutOfMemoryError`.
   - **Stack:** Her **thread'e özel**; metot çağrıları ve yerel değişkenler. Çok derin özyineleme
     `StackOverflowError` verir.
   - **Metaspace:** Sınıf meta-verileri (yapı, metot bilgileri).
   - **PC Register & Native Method Stack:** Yürütme konumu ve yerel (JNI) çağrılar.

3. **Execution Engine (Yürütme Motoru):**
   - **Interpreter:** Bytecode'u yorumlar (başlangıçta).
   - **JIT Compiler:** Sık çalışan kodu makine koduna derler — hızlandırır.
   - **Garbage Collector:** Ulaşılamayan nesneleri temizler.

Örnek 1 (`./Ornek1.java`) JVM'i tanır (vm adı/sürüm), heap bellek kullanımını ve çekirdek sayısını
gösterir. Örnek 2 (`./Ornek2.java`) bir sınıfın `Class` nesnesini, sınıf yükleyiciyi (bootstrap →
`null`) ve bytecode/yükleme kavramını gösterir.

## Stack tabanlı sanal makine

JVM, gerçek bir CPU'yu taklit eder ama **yığın tabanlı** (stack-based) bir komut seti yürütür
(register tabanlı gerçek CPU'ların aksine). Bytecode komutları operandları bir yığına itip çeker
(`iload`, `iadd`, `invokevirtual`...). Bu tasarım, bytecode'u kompakt ve platformdan bağımsız kılar;
JIT ise bunu çalışma anında platforma özel register tabanlı makine koduna çevirir.

## Neden bu kadar önemli?

- **Performans:** Heap/GC ayarları (`-Xmx`, GC seçimi) uygulama davranışını belirler.
- **Hata teşhisi:** `OutOfMemoryError` (heap), `StackOverflowError` (stack), `ClassNotFoundException`
  (class loader) — hangi bileşenin sorun çıkardığını anlarsın.
- **Çok dillilik:** JVM yalnızca Java değil, Kotlin, Scala, Groovy, Clojure gibi dilleri de
  çalıştırır — hepsi bytecode'a derlenir.

## Özet

JVM'in bytecode'u çalıştıran, platformdan bağımsızlık sağlayan motor olduğunu; üç ana bileşenini —
**Class Loader**, **Runtime Data Areas** (heap/stack/metaspace), **Execution Engine** (interpreter
+ JIT + GC) — ve çalışma akışını (Örnek 1–2) öğrendik; stack-tabanlı tasarımı ve neden önemli
olduğunu gördük. Sırada, sık karıştırılan üç kavramın netleştirilmesi: **JDK vs JRE vs JVM**.
