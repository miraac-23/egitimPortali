# Process API (Süreç Yönetimi)

Bazen Java programının **dışındaki** programları çalıştırman gerekir: bir sistem komutu, başka bir
betik, harici bir araç. Veya çalışan süreçler hakkında bilgi almak istersin (PID, başlangıç zamanı,
ebeveyn). Java'nın **Process API**'si bunları sağlar: komut çalıştırmak için `ProcessBuilder`,
süreçleri incelemek/yönetmek için `ProcessHandle` (Java 9+).

## ProcessHandle: süreçleri incelemek (Java 9)

`ProcessHandle`, yeni bir süreç **başlatmadan**, çalışan süreçler hakkında bilgi verir:

```java
ProcessHandle benim = ProcessHandle.current();   // bu JVM süreci
benim.pid();                                      // işletim sistemi PID'i
benim.info().command();  benim.info().startInstant();  benim.info().user();
benim.parent();  benim.children();  benim.isAlive();  benim.destroy();
ProcessHandle.allProcesses();                     // sistemdeki tüm süreçler (akış)
```

Örnek 1 (`./Ornek1.java`) bu JVM sürecinin PID'ini, bilgilerini ve ebeveynini gösterir. (Hangi
bilgilerin dolu geleceği işletim sistemine göre değişir.) İzleme, yönetim panelleri ve süreç
ağaçlarını incelemek için kullanılır.

## ProcessBuilder: dış komut çalıştırmak

`ProcessBuilder`, dış bir program/komut çalıştırmanın modern yoludur (eski `Runtime.exec`'in
halefi):

```java
ProcessBuilder pb = new ProcessBuilder("/bin/echo", "merhaba");
pb.redirectErrorStream(true);              // stderr -> stdout
Process p = pb.start();
// p.getInputStream() ile çıktıyı oku...
int kod = p.waitFor();                     // bitmesini bekle + çıkış kodu
```

Önemli metotlar:

- **`command(...)`**: Komut ve argümanları (her argüman **ayrı** verilir).
- **`start()`**: Süreci başlatır, bir `Process` döndürür.
- **`getInputStream`/`getOutputStream`/`getErrorStream`**: Süreçle veri alışverişi.
- **`redirectErrorStream(true)`**: Hata akışını çıktıyla birleştirir.
- **`directory(file)`** / **`environment()`**: Çalışma dizini ve ortam değişkenleri.
- **`waitFor()`**: Sürecin bitmesini bekler, çıkış kodunu (0 = başarılı) döndürür.

Örnek 2 (`./Ornek2.java`) bir `echo` komutunu çalıştırıp çıktısını okur ve çıkış kodunu alır.

## Çıktı akışlarını yönetmek (deadlock tuzağı)

Bir sürecin çıktısını okumadan büyük veri üretmesini beklersen, sürecin çıktı tamponu dolar ve
**kilitlenme (deadlock)** olabilir. Bu yüzden:

- Çıktı/hata akışlarını **okumalısın** (veya `redirect...` ile yönlendirmelisin).
- Büyük çıktılarda akışları ayrı thread'lerde oku, ya da `redirectOutput`/`redirectError` ile
  dosyaya/PIPE'a yönlendir.

## Güvenlik: komut enjeksiyonu

Komutu **asla** kullanıcı girdisinden string birleştirerek kurma (`"rm " + kullaniciGirdisi`
gibi) — komut enjeksiyonu açığıdır. Argümanları `ProcessBuilder`'a **ayrı ayrı** ver; kabuk (shell)
yorumlamasından kaçın. Mümkünse beyaz liste (whitelist) ile sınırla.

## Özet

Java'nın Process API'sini öğrendik: çalışan süreçleri başlatmadan inceleyen **`ProcessHandle`**
(PID, info, parent; Örnek 1) ve dış komut çalıştıran **`ProcessBuilder`** (start, akışlar, waitFor;
Örnek 2); çıktı akışı deadlock tuzağına ve komut enjeksiyonu güvenliğine değindik. Sırada, ikili
veriyi metne çevirme standardı: **Base64**.
