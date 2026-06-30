# Javadoc (Kod Dokümantasyonu)

Kod yalnızca çalışması için değil, **başkalarının (ve gelecekteki senin)** anlaması için de
yazılır. **Javadoc**, Java'nın standart dokümantasyon sistemidir: kaynak koddaki özel yorumlardan
profesyonel HTML API dokümanları üretir. JDK'nın ve Spring'in resmi dokümanları (docs.oracle.com)
işte bu Javadoc yorumlarından üretilir. İyi Javadoc yazmak, kütüphane ve takım kodunun olmazsa
olmazıdır.

## Javadoc yorumu nedir?

Normal yorumlardan farklı olarak, Javadoc yorumu **`/** ... */`** ile başlar (çift yıldız) ve bir
sınıf/metot/alanın **hemen üstüne** yazılır:

```java
/**
 * İki tam sayıyı güvenli biçimde böler.
 *
 * @param bolunen pay
 * @param bolen   payda; {@code 0} olamaz
 * @return tam sayı bölümü
 * @throws ArithmeticException bolen sıfır ise
 */
public static int bol(int bolunen, int bolen) { ... }
```

İlk cümle **özettir** (metot listelerinde bu görünür) — kısa ve net olmalı. Örnek 1
(`./Ornek1.java`) belgelenmiş metotları, Örnek 2 (`./Ornek2.java`) belgelenmiş bir sınıf/alanları
gösterir (sınıflar çalışır; yorumlar `javadoc` aracıyla HTML olur).

## Blok etiketleri

| Etiket | Ne belgeler |
|--------|-------------|
| `@param ad açıklama` | Her parametre |
| `@return açıklama` | Dönüş değeri |
| `@throws Tip açıklama` | Fırlatılan istisnalar |
| `@since sürüm` | Hangi sürümde eklendi |
| `@deprecated açıklama` | Neden kullanılmamalı + alternatif |
| `@see`, `@author`, `@version` | İlgili öğe, yazar, sürüm |

## Satır içi etiketler ve HTML

Yorum metni içinde:

- **`{@code x}`**: Kod biçiminde gösterir (HTML kaçışı yapar): `{@code List<String>}`.
- **`{@link Sınıf#metot}`**: Başka bir öğeye **tıklanabilir** bağlantı (IDE'de gezinme sağlar).
- **`{@literal}`**, **`{@value}`**: Özel karakter/sabit değer.
- **HTML:** `<p>`, `<ul>`, `<b>` gibi etiketler kullanılabilir (yorum HTML'e çevrildiği için).

## @deprecated: hem Javadoc hem anotasyon

Bir öğeyi kullanımdan kaldırırken **ikisini birlikte** kullan: `@Deprecated` anotasyonu (derleyici
uyarısı, topic 81) + `@deprecated` Javadoc etiketi (neden ve alternatif):

```java
/** @deprecated Bunun yerine {@link #getBakiye()} kullanın. */
@Deprecated(since = "2.0")
public long getBakiyeKurus() { ... }
```

## Doküman üretme

```bash
javadoc -d docs *.java        # docs/ klasörüne HTML üret
```

Üretimde bu, build araçlarıyla otomatikleşir (Maven `maven-javadoc-plugin`, Gradle `javadoc`
görevi). Sonuç, gezilebilir bir HTML API sitesidir.

## İyi Javadoc yazmak

- **NE yaptığını ve sözleşmesini** anlat (ön koşullar, son koşullar, yan etkiler) — **NASIL**
  yapıldığını değil (o kodun işi).
- İlk cümleyi özet olarak, fiil ile başlat ("Hesaba para yatırır.").
- Her `@param`/`@return`/`@throws`'u doldur (genel/public API'de).
- Bariz olanı tekrarlama (`@param x x değeri` gibi gürültüden kaçın).

## Özet

Javadoc'un kaynak yorumlarından HTML API dokümanı üreten standart sistem olduğunu; `/** */` söz
dizimini, özet cümlesini, blok etiketlerini (`@param`/`@return`/`@throws`/`@since`) ve satır içi
etiketleri (`{@code}`/`{@link}`) (Örnek 1–2); `@deprecated`'in anotasyonla birlikte kullanımını ve
doküman üretmeyi öğrendik. İyi dokümantasyon, kodun bakımını ve paylaşılabilirliğini kökten
artırır. Bununla operatörler ve dokümantasyon batch'i tamamlandı.
