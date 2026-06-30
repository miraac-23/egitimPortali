# Markdown Documentation Comments (JEP 467) — Detaylı Anlatım

> **JEP 467** — Java 23'te **kalıcı (final)** olarak geldi (preview değil).
> Yani `--enable-preview` gerektirmez, doğrudan kullanılabilir.

---

## 1. NEDİR?

Java 23 öncesinde tüm Javadoc yorumları **HTML tabanlıydı** ve `/** ... */`
blok yorumu ile yazılırdı. İçinde paragraflar için `<p>`, listeler için
`<ul><li>`, kod için `<code>` / `<pre>` gibi HTML etiketleri kullanılırdı.

Java 23 ile birlikte Javadoc yorumlarını **Markdown** ile yazabilirsiniz.
Yeni yorum biçimi `///` (üç eğik çizgi) ile başlar — tıpkı satır yorumu gibi,
ama art arda gelen `///` satırları bir Javadoc yorumu oluşturur.

```java
/// Bu bir Markdown Javadoc yorumudur.
///
/// Burada **kalın**, *italik* ve `kod` yazabilirsiniz.
public class Merhaba { }
```

> **Önemli:** `///` ile başlayan Markdown Javadoc, klasik `/** */` ile aynı
> yerlerde (sınıf, metot, alan, vs.) kullanılır. İkisini aynı eleman üzerinde
> karıştıramazsınız — bir eleman için ya HTML-blok ya Markdown-satır seçersiniz.

---

## 2. NEDEN GELDİ? (Motivasyon)

1. **Okunabilirlik:** HTML etiketleri kaynak kodda dağınık görünür. Markdown
   düz metne çok yakındır ve etiket gürültüsü yoktur.
2. **Tanıdıklık:** Geliştiriciler README, issue, PR açıklamalarında zaten
   Markdown kullanıyor. Aynı sözdizimini Javadoc'ta kullanmak öğrenme eğrisini
   düşürür.
3. **Kaçış (escaping) derdinin azalması:** HTML'de `<`, `>`, `&` karakterleri
   `&lt;`, `&gt;`, `&amp;` olarak kaçışlanmalıydı. Markdown'da bu yük azalır
   (özellikle generic tiplerde, örn. `List<String>`).

---

## 3. SÖZDİZİMİ (Sentaks) DETAYLARI

### 3.1 Temel kurallar
- Her satır `///` ile başlar (önünde sadece boşluk olabilir).
- İçerik **CommonMark** Markdown lehçesiyle işlenir.
- Javadoc `@param`, `@return`, `@throws` gibi blok etiketleri **hâlâ** kullanılır.
- Diğer öğelere bağlantı için `[...]` köşeli parantez referansları kullanılır.

### 3.2 Bağlantılar (link)
Klasik Javadoc'taki `{@link ...}` yerine Markdown bağlantı sözdizimi:

```java
/// Bir [String] döndürür ve [#yardimci()] metodunu çağırır.
/// Detay: [java.util.List]
```

- `[String]` → `String` sınıfına bağlantı (otomatik çözümlenir).
- `[#yardimci()]` → aynı sınıftaki `yardimci` metoduna bağlantı.
- Özel metin ile: `[buraya bak][String]` veya `[metin](https://...)` (harici URL).

### 3.3 Kod blokları
````java
/// Örnek kullanım:
///
/// ```java
/// var liste = List.of(1, 2, 3);
/// liste.forEach(System.out::println);
/// ```
````

---

## 4. ESKİ TARZ vs YENİ TARZ (Yan Yana)

### Örnek A — Sınıf dokümantasyonu

#### ESKİ (HTML tabanlı, `/** */`)
```java
/**
 * Basit bir hesap makinesi.
 * <p>
 * Bu sınıf dört temel işlemi destekler:
 * </p>
 * <ul>
 *   <li>Toplama</li>
 *   <li>Çıkarma</li>
 *   <li>Çarpma</li>
 *   <li>Bölme</li>
 * </ul>
 * <p>Örnek kullanım:</p>
 * <pre>{@code
 * Hesap h = new Hesap();
 * int sonuc = h.topla(2, 3); // 5
 * }</pre>
 *
 * @see java.lang.Math
 */
public class Hesap { }
```

#### YENİ (Markdown tabanlı, `///`)
```java
/// Basit bir hesap makinesi.
///
/// Bu sınıf dört temel işlemi destekler:
///
/// - Toplama
/// - Çıkarma
/// - Çarpma
/// - Bölme
///
/// Örnek kullanım:
///
/// ```java
/// Hesap h = new Hesap();
/// int sonuc = h.topla(2, 3); // 5
/// ```
///
/// @see java.lang.Math
public class Hesap { }
```

> Görüldüğü gibi `<p>`, `<ul>`, `<li>`, `<pre>{@code ...}` gürültüsü kayboldu.
> Liste ve kod bloğu çok daha okunabilir.

---

### Örnek B — Metot dokümantasyonu

#### ESKİ (HTML tabanlı)
```java
/**
 * İki sayıyı böler.
 * <p>
 * Bölen <code>0</code> ise hata fırlatır. Sonuç bir
 * {@link java.lang.Double} olarak döner.
 * </p>
 *
 * @param bolunen bölünen sayı
 * @param bolen   bölen sayı (0 olamaz)
 * @return bölme sonucu
 * @throws java.lang.ArithmeticException bölen 0 ise
 */
public double bol(double bolunen, double bolen) {
    if (bolen == 0) throw new ArithmeticException("Sifira bolme");
    return bolunen / bolen;
}
```

#### YENİ (Markdown tabanlı)
```java
/// İki sayıyı böler.
///
/// Bölen `0` ise hata fırlatır. Sonuç bir [Double] olarak döner.
///
/// @param bolunen bölünen sayı
/// @param bolen   bölen sayı (0 olamaz)
/// @return bölme sonucu
/// @throws java.lang.ArithmeticException bölen 0 ise
public double bol(double bolunen, double bolen) {
    if (bolen == 0) throw new ArithmeticException("Sifira bolme");
    return bolunen / bolen;
}
```

> Dikkat: `@param`, `@return`, `@throws` etiketleri **aynı kaldı**. Sadece
> serbest metin Markdown oldu, `<code>` → `` `0` ``, `{@link Double}` → `[Double]`.

---

### Örnek C — Generic tip içeren açıklama (kaçış avantajı)

#### ESKİ — `<` ve `>` HTML'de sorun çıkarır
```java
/**
 * {@code Map<String, List<Integer>>} tipinde bir harita döndürür.
 * <p>Düz metinde &lt;String&gt; yazmak için kaçış gerekirdi.</p>
 */
public Map<String, List<Integer>> verileriGetir() { return null; }
```

#### YENİ — Markdown'da `code span` içinde kaçış gerekmez
```java
/// `Map<String, List<Integer>>` tipinde bir harita döndürür.
///
/// Düz metinde `<String>` yazmak artık çok daha rahat.
public Map<String, List<Integer>> verileriGetir() { return null; }
```

---

### Örnek D — Tablo (Markdown'da çok daha kolay)

#### ESKİ — HTML tablosu (uzun ve dağınık)
```java
/**
 * Durum kodları:
 * <table>
 *   <tr><th>Kod</th><th>Anlam</th></tr>
 *   <tr><td>200</td><td>Başarılı</td></tr>
 *   <tr><td>404</td><td>Bulunamadı</td></tr>
 * </table>
 */
```

#### YENİ — Markdown tablosu
```java
/// Durum kodları:
///
/// | Kod | Anlam      |
/// |-----|------------|
/// | 200 | Başarılı   |
/// | 404 | Bulunamadı |
```

---

## 5. NEREDE KOLAYLIK SAĞLAR?

| Senaryo | Faydası |
|---|---|
| Açık kaynak kütüphane API'si | Daha temiz, bakımı kolay dokümantasyon |
| Dahili ekip kodu | Hızlı yazılan, okunabilir yorumlar |
| Listeler / tablolar / kod örnekleri | HTML etiket gürültüsü olmadan |
| Generic / `<>` içeren açıklamalar | Kaçış karakterleriyle uğraşmak yok |

---

## 6. KARŞILAŞTIRMA ÖZETİ

| Konu | ESKİ (HTML Javadoc) | YENİ (Markdown Javadoc) |
|---|---|---|
| Yorum başlangıcı | `/** ... */` | `///` (her satır) |
| Format | HTML | CommonMark Markdown |
| Paragraf | `<p>...</p>` | Boş satır |
| Liste | `<ul><li>...</li></ul>` | `- madde` |
| Kod (satır içi) | `<code>x</code>` / `{@code x}` | `` `x` `` |
| Kod bloğu | `<pre>{@code ... }</pre>` | ` ```java ... ``` ` |
| Bağlantı | `{@link Tip}` | `[Tip]` |
| Tablo | `<table>...` | Markdown tablo `\| ... \|` |
| Blok etiketleri | `@param`, `@return`, ... | Aynen korunur |
| Preview gerekir mi? | — | **Hayır** (kalıcı) |

---

## 7. AVANTAJ / DEZAVANTAJ / RİSK

### Avantajlar
- Çok daha okunabilir kaynak kod.
- Markdown bilen herkes hemen kullanabilir.
- HTML kaçış derdi büyük ölçüde biter.
- Kalıcı özellik — risk yok, `--enable-preview` gerekmez.

### Dezavantajlar / Dikkat
- Eski kod tabanları HTML Javadoc ile dolu; geçiş tek seferde olmaz (kademeli
  geçilebilir, ikisi farklı elemanlarda bir arada bulunabilir).
- Çok karmaşık HTML düzenleri (özel inline HTML) Markdown'da bire bir
  karşılanmayabilir; CommonMark içinde sınırlı HTML hâlâ yazılabilir.
- Bir eleman için HTML ve Markdown **karıştırılamaz** (ya `/** */` ya `///`).

---

## 8. GERÇEK HAYAT ÖRNEĞİ

Bir HTTP istemci kütüphanesi geliştiriyorsunuz. `İstek` sınıfının dokümantasyonu:

```java
/// HTTP isteğini temsil eder.
///
/// Akıcı (fluent) API ile oluşturulur:
///
/// ```java
/// var istek = Istek.builder()
///     .url("https://api.ornek.com/v1/kullanicilar")
///     .metot("GET")
///     .baslik("Authorization", "Bearer " + token)
///     .build();
/// ```
///
/// Desteklenen metotlar:
///
/// | Metot  | Gövde alır mı? |
/// |--------|----------------|
/// | GET    | Hayır          |
/// | POST   | Evet           |
/// | PUT    | Evet           |
/// | DELETE | Opsiyonel      |
///
/// İlgili: [Yanit], [#gonder()]
///
/// @param url hedef adres
/// @return oluşturulan istek
public final class Istek {
    /// İsteği gönderir ve bir [Yanit] döndürür.
    ///
    /// @return sunucu yanıtı
    /// @throws java.io.IOException ağ hatası olursa
    public Yanit gonder() throws java.io.IOException { /* ... */ return null; }
}
```

> Aynı dokümantasyonu HTML Javadoc ile yazsaydık `<p>`, `<pre>`, `<table>`,
> `<tr>`, `<td>`, `{@link}` etiketleriyle iki katı uzunlukta ve okunması zor
> olurdu.

---

## 9. NASIL DERLENİR / JAVADOC ÜRETİLİR?

Markdown Javadoc **kalıcı** olduğu için özel bayrak gerekmez:

```bash
# Normal derleme
javac Istek.java

# Javadoc HTML üretimi (Java 23 javadoc aracı Markdown'ı anlar)
javadoc -d cikti Istek.java
```

> `javadoc` aracı `///` yorumlarını okuyup, Markdown'ı HTML'e çevirerek
> standart Javadoc HTML çıktısını üretir.

---

*Bu doküman Java 23 / JEP 467 baz alınarak hazırlanmıştır.*
