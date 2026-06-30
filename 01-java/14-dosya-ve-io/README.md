# Dosya İşlemleri ve I/O

Şimdiye kadar veriler programın içinde, bellekte yaşadı. Ama gerçek uygulamalar dış dünyayla
konuşur: dosya okur/yazar, ağdan veri alır, kayıt tutar. Bu bölümde Java'da girdi/çıktı
(I/O) işlemlerini, hem klasik akış (stream) yaklaşımını hem de modern ve çok daha kısa olan
NIO.2 API'sini öğreniyoruz.

> Bu bölümün örnekleri repo klasörünü kirletmemek için **geçici dosyalar**
> (`Files.createTempFile`) kullanır ve iş bitince onları siler. Kendi projelerinde elbette
> kalıcı yollar kullanırsın.

## Klasik I/O: akışlar (streams)

Java'nın orijinal I/O modeli **akışlar** üzerine kuruludur: veriyi byte byte veya karakter
karakter okuyup yazarsın. Verimlilik için bunları **tamponlu (buffered)** sarmalayıcılarla
kullanırız:

- Yazma: `FileWriter` → `BufferedWriter` (`write`, `newLine`)
- Okuma: `FileReader` → `BufferedReader` (`readLine`)

```java
try (BufferedWriter w = new BufferedWriter(new FileWriter(dosya))) {
    w.write("satır");
    w.newLine();
}
try (BufferedReader r = new BufferedReader(new FileReader(dosya))) {
    String satir;
    while ((satir = r.readLine()) != null) { ... }
}
```

Dikkat: akışlar birer kaynaktır ve mutlaka kapatılmalıdır. `try-with-resources` bunu otomatik
yapar (blok bitince `close()` + tampon boşaltma). Örnek 1 (`./Ornek1.java`) tamponlu yazma ve
satır satır okumayı gösterir.

## Modern I/O: NIO.2 (Path ve Files)

Java 7 ile gelen `java.nio.file` paketi, sık yapılan işleri tek satıra indirir. `Path` bir
dosya yolunu temsil eder; `Files` ise yüksek seviyeli yardımcı metotlar sunar:

```java
Files.write(path, List.of("a", "b", "c"));   // listeyi satır satır yaz
List<String> satirlar = Files.readAllLines(path); // tümünü oku
Files.writeString(path, "ek\n", StandardOpenOption.APPEND); // sona ekle
Files.exists(path); Files.size(path); Files.delete(path);
```

`Path` ayrıca yol parçalarıyla çalışmayı kolaylaştırır (`getFileName`, `getParent`). Örnek 2
(`./Ornek2.java`) NIO ile yazma/okuma/ekleme ve dosya bilgisi almayı gösterir.

> Küçük dosyalarda `readAllLines` pratiktir; **çok büyük** dosyalarda ise tümünü belleğe
> almak yerine satır satır akıtan `Files.lines(...)` tercih edilir.

## Files.lines + Stream

`Files.lines(path)` bir `Stream<String>` döndürür; böylece dosyayı satır satır, tembel biçimde
ve Stream API'nin tüm gücüyle işleyebilirsin. Bu stream bir kaynak tuttuğundan
`try-with-resources` içinde kullanılmalıdır:

```java
try (Stream<String> satirlar = Files.lines(path)) {
    satirlar.filter(s -> s.startsWith("ERROR")).forEach(System.out::println);
}
```

Örnek 3 (`./Ornek3.java`) küçük bir log dosyası oluşturur, seviyelere (`INFO`/`WARN`/`ERROR`)
göre kaç kayıt olduğunu `groupingBy` ile sayar ve yalnızca hata satırlarını süzer — dosya
işleme ile Stream API'yi bir araya getirir.

## byte vs karakter akışları

İki tür akış vardır: **byte akışları** (`InputStream`/`OutputStream`) her tür ikili veri
(resim, ses, zip) için; **karakter akışları** (`Reader`/`Writer`) metin için kullanılır.
Metinle çalışırken karakter akışlarını (veya NIO'nun metin metotlarını) tercih edersin;
böylece karakter kodlaması (UTF-8 gibi) doğru ele alınır.

## Özet

Klasik tamponlu akışlarla dosya okuyup yazmayı, modern NIO.2 (`Path`/`Files`) ile aynı işleri
çok daha kısa yapmayı ve `Files.lines` ile dosyaları Stream halinde işlemeyi öğrendik.
`try-with-resources` ile kaynakları güvenle kapattık. Sırada, programları aynı anda birden
çok iş yapabilir hale getiren konu: **çoklu iş parçacığı ve eşzamanlılık**.
