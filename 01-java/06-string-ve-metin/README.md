# String İşlemleri ve StringBuilder

Metinlerle çalışmak, neredeyse her programın bir parçasıdır: kullanıcı adları, dosya
içerikleri, mesajlar... Java'da metin denince akla `String` gelir. Bu bölümde `String`'in
nasıl çalıştığını, en sık kullanılan metotlarını ve çok sayıda birleştirme gerektiğinde
neden `StringBuilder`'a geçmen gerektiğini öğreneceğiz.

## String ve değişmezlik (immutability)

`String`'in en kritik özelliği **değişmez (immutable)** olmasıdır: bir `String` nesnesi bir
kez oluşturulduktan sonra içeriği asla değişmez. `toUpperCase()`, `replace()`, `trim()` gibi
metotlar orijinali değiştirmez; **yeni** bir `String` döndürür:

```java
String s = "merhaba";
s.toUpperCase();        // sonucu kullanmazsan boşa gider!
String b = s.toUpperCase(); // doğrusu: dönen değeri al
```

Bu davranış güvenlik ve performans (string havuzu) için faydalıdır ama bir tuzağı vardır:
döngü içinde sürekli birleştirme yaparsan her adımda yeni nesne yaratılır.

### Sık kullanılan metotlar

`length`, `charAt`, `substring`, `indexOf`, `contains`, `startsWith`, `replace`,
`toUpperCase/toLowerCase`, `trim/strip` ve `split` günlük hayatta en çok kullandıkların
arasındadır. `split` bir metni verilen ayraca göre parçalara bölüp dizi döndürür:

```java
"elma,armut,kiraz".split(",");  // ["elma", "armut", "kiraz"]
```

Örnek 1 (`./Ornek1.java`) bu metotları tek tek çalıştırır.

### equals vs ==

Bu, yeni başlayanların en sık yaptığı hatadır. `==` iki referansın **aynı nesne** olup
olmadığına bakar; metinlerin **içeriğini** karşılaştırmak için `equals()` kullanılır:

```java
String a = "merhaba";
String c = new String("merhaba");
a == c;        // false (farklı nesneler)
a.equals(c);   // true  (aynı içerik)
```

> **Kural:** String'leri her zaman `equals()` ile karşılaştır. Büyük/küçük harf önemsizse
> `equalsIgnoreCase()` kullan.

## StringBuilder

`String` değişmez olduğundan, bir metni adım adım kurarken (özellikle döngüde) `String +=`
kullanmak yavaştır: her `+` yeni bir nesne üretir. **`StringBuilder`** değiştirilebilir bir
tampon sunar; `append`, `insert`, `reverse`, `delete` gibi metotlarla aynı nesneyi büyütür:

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
String sonuc = sb.toString();
```

Örnek 2 (`./Ornek2.java`) `StringBuilder` metotlarını gösterir ve aynı işi `String +=` ile
`StringBuilder` ile yaparak süre farkını ölçer — aradaki uçurumu kendi gözünle göreceksin.

> Birkaç parçayı birleştiriyorsan `+` gayet iyidir; derleyici onu zaten optimize eder.
> Fark, **döngüde çok sayıda** birleştirmede ortaya çıkar.

## Biçimlendirme ve text block

Düzenli çıktı için `String.format(...)` (veya `printf`) kullanılır; biçim belirteçleri
`%d`, `%s`, `%.2f` gibidir. Çok satırlı metinler için Java 15+ ile gelen **text block**
okunabilirliği artırır:

```java
String json = """
    {
      "ad": "Ada",
      "yas": 30
    }
    """;
```

## Pratik: küçük bir metin aracı

Örnek 3 (`./Ornek3.java`) öğrendiklerini birleştirir: bir cümledeki kelimeleri sayar,
kelime sırasını ters çevirir ve bir metnin palindrom olup olmadığını kontrol eder. `split`,
`charAt`, `replaceAll` ve `StringBuilder`'ı bir arada kullanır.

## Özet

`String`'in değişmez olduğunu ve bunun sonuçlarını, en sık metotları, `equals`/`==` farkını
ve döngüde `StringBuilder` kullanmanın önemini gördük. Metin artık senin için bir araç
kutusu. Sırada, programların kaçınılmaz gerçeğiyle baş etmeyi öğreniyoruz: **hatalar ve
istisnalar**.
