# Character ve Boolean Sınıfları

İki temel ilkel tipin — `char` ve `boolean` — wrapper sınıfları olan **`Character`** ve
**`Boolean`**, küçük ama günlük kodda sık kullanılan yardımcılar sunar. `Character`, metin
ayrıştırma ve doğrulamanın temelidir; `Boolean` ise mantıksal değerlerle güvenli çalışmayı sağlar.
İkisinin de kendine özgü tuzakları vardır.

## Character: karakter sınıflandırma

`Character`'ın statik metotları bir karakterin **ne olduğunu** söyler veya onu **dönüştürür**:

```java
Character.isDigit('7');         // rakam mı?
Character.isLetter('A');        // harf mi?
Character.isLetterOrDigit(c);
Character.isWhitespace(' ');    // boşluk mu?
Character.isUpperCase(c); Character.isLowerCase(c);
Character.toUpperCase(c); Character.toLowerCase(c);
Character.getNumericValue('9'); // -> 9 (sayısal değeri)
```

Bu metotlar; **girdi doğrulama**, **ayrıştırıcı (parser)** yazma ve **metin analizi** için
temeldir. Örnek 1 (`./Ornek1.java`) bir metni karakter karakter analiz eder (harf/rakam/boşluk
sayımı) ve `char`'ın aslında bir Unicode kodu olduğunu (`'A' == 65`) gösterir.

> **char ↔ int:** Karakterler sayısal kodlardır. `(int) 'A'` → 65; `(char) (c + 1)` → sonraki
> karakter. Bu, karakter aritmetiğini (şifreleme, dönüşüm) mümkün kılar.

## Boolean: mantıksal değerler

```java
Boolean.parseBoolean("true");   // true
Boolean.parseBoolean("evet");   // false! (yalnızca "true" -> true)
Boolean.logicalAnd(a, b);
Boolean.logicalOr(a, b);
Boolean.logicalXor(a, b);
```

> **`parseBoolean` tuzağı:** Yalnızca `"true"` (büyük/küçük harf duyarsız) `true` döndürür; **başka
> her şey** (`"1"`, `"evet"`, `"yes"`, boş string) `false` olur. Kendi formatların varsa elle
> ayrıştır.

## Üç-değerli mantık ve null tuzağı

İlkel `boolean` yalnızca `true`/`false` olabilir. Ama wrapper **`Boolean` `null` olabilir** — bu,
"henüz bilinmiyor" gibi **üç-değerli** durumları modellemeye yarar (örn. veritabanından gelen,
ayarlanmamış bir onay alanı):

```java
Boolean onayli = null;   // bilinmiyor
```

Ancak buradaki tuzak ölümcüldür: `null` bir `Boolean`'ı `if`/`while` koşulunda kullanmak, unboxing
sırasında **`NullPointerException`** atar:

```java
Boolean onayli = null;
if (onayli) { ... }      // NPE! (null.booleanValue())
```

Güvenli yollar:

```java
if (Boolean.TRUE.equals(onayli)) { ... }   // null -> false, NPE yok
if (onayli != null && onayli) { ... }       // önce null kontrolü
```

Örnek 2 (`./Ornek2.java`) `parseBoolean`'ı, mantıksal metotları ve null Boolean tuzağını gösterir.

## Özet

`Character`'ın karakter sınıflandırma/dönüştürme metotlarını ve metin analizindeki rolünü (Örnek 1);
`Boolean`'ın `parseBoolean` tuzağını, mantıksal yardımcılarını ve özellikle **null Boolean'ı
koşulda kullanmanın NPE riskini** (Örnek 2) öğrendik. Bununla Built-in Classes ve kalan Class
References bölümü büyük ölçüde tamamlandı.
