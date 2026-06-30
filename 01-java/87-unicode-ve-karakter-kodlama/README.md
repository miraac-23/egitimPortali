# Unicode ve Karakter Kodlama

Metin işlemenin altında, çoğu zaman görmezden geldiğimiz ama hata yaptığında baş ağrıtan bir
katman vardır: **karakter kodlama**. "Türkçe karakterler neden bozuk çıktı?", "emoji'nin uzunluğu
neden 2?", "bu dosya neden anlaşılmıyor?" gibi soruların yanıtı buradadır. Bu konu, Unicode'un ne
olduğunu, Java'nın karakterleri nasıl tuttuğunu ve metni byte'lara çevirirken neye dikkat etmen
gerektiğini ele alır.

## Unicode ve kod noktası (code point)

**Unicode**, dünyadaki her karaktere benzersiz bir sayı — **kod noktası (code point)** — atayan
evrensel standarttır:

- `'A'` → U+0041, `'ç'` → U+00E7, `'€'` → U+20AC, `'😀'` → U+1F600

Java'da `\uXXXX` kaçışıyla bir karakter kodla yazılabilir: `'ç'` → `'ç'`.

## char vs code point: kritik fark

Java'da **`char` 16 bittir** ve bir "kod birimini" (code unit) temsil eder:

- **BMP (U+0000–U+FFFF):** Türkçe dahil çoğu karakter tek `char`'a sığar.
- **Astral (U+FFFF üstü):** Emoji ve bazı semboller **iki `char`** ile temsil edilir (surrogate
  pair)!

Bunun çarpıcı sonucu:

```java
"😀".length()                       // 2  (char/kod birimi sayısı — KARAKTER DEĞİL!)
"😀".codePointCount(0, 2)           // 1  (gerçek karakter sayısı)
"ab😀cd".length()                   // 6
"ab😀cd".codePoints().count()       // 5  (gerçek karakter)
```

> **Kural:** `String.length()` **karakter sayısı değil, `char` (kod birimi) sayısıdır.** Gerçek
> karakter sayısı veya astral karakterlerle güvenli işlem için `codePointCount` / `codePoints()`
> kullan.

Örnek 1 (`./Ornek1.java`) bunu gösterir.

## Karakter kodlama (charset): metin → byte

Karakterler bellekte kod noktasıdır; ama bir dosyaya yazılırken veya ağdan geçerken **byte'lara
kodlanmalıdır**. Bu dönüşümü bir **charset** belirler:

- **UTF-8:** Evrensel standart. ASCII ile uyumlu (ASCII karakterler 1 byte), Türkçe ~2 byte, emoji
  ~4 byte (değişken uzunluk). Web, dosya ve API'lerde **varsayılan** tercih.
- **ISO-8859-1 (Latin-1):** Her karakter 1 byte ama yalnızca Batı Avrupa karakterlerini kapsar
  (Türkçe'nin bazı harflerini tam karşılamaz).
- **UTF-16:** Java'nın `char`'ının iç temsili.

```java
byte[] b = metin.getBytes(StandardCharsets.UTF_8);
String s = new String(b, StandardCharsets.UTF_8);   // aynı charset ile geri çöz
```

## Mojibake: en sık yapılan hata

Bir metni **bir kodlamayla yazıp başka bir kodlamayla okumak**, "mojibake" denen bozuk karakterlere
yol açar (`Ã§`, `Ä±` gibi):

```java
byte[] utf8 = metin.getBytes(UTF_8);
new String(utf8, ISO_8859_1);   // YANLIŞ -> bozuk metin
```

Örnek 2 (`./Ornek2.java`) UTF-8/ISO-8859-1 farkını ve mojibake'yi canlı gösterir.

> **Altın kural:** Kodlamayı **her zaman açıkça belirt**. `getBytes(UTF_8)`, `new String(bytes,
> UTF_8)`, `Files.readString` (varsayılan UTF-8). **Platform varsayılan kodlamasına güvenme** —
> Windows ve Linux'ta farklı olabilir, taşınabilirliği bozar. (Java 18'den beri varsayılan charset
> UTF-8 oldu, ama yine de açık belirtmek en güvenlisidir.)

## Özet

Unicode'un her karaktere bir kod noktası atadığını; Java'da `char`'ın 16-bit kod birimi olduğunu
ve astral karakterlerin (emoji) iki `char` ile temsil edildiğini — bu yüzden `length()`'in karakter
sayısı olmadığını (Örnek 1); metni byte'lara çeviren charset'leri, UTF-8'in önemini ve mojibake
hatasını (Örnek 2) öğrendik. "Kodlamayı her zaman açıkça belirt" kuralı, metin hatalarının çoğunu
önler. Sırada, tarih ve zamanla çalışmak: **Date & Time (java.time)**.
