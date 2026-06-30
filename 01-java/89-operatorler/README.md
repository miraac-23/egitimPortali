# Operatörler

**Operatörler**, değerler üzerinde işlem yapan sembollerdir: toplama, karşılaştırma, mantıksal
birleştirme... Programlamanın en temel yapı taşlarıdır ve her satırda kullanılırlar. Bu konu, Java
operatörlerini kategori kategori ele alır ve özellikle yeni başlayanların takıldığı noktaları
(int bölme, önek/sonek, kısa devre, öncelik) netleştirir.

## Aritmetik operatörler

```java
+  -  *  /  %        // toplama, çıkarma, çarpma, bölme, mod (kalan)
```

> **int bölme tuzağı:** `17 / 5 == 3` (ondalık atılır!). Ondalık için en az bir operand `double`
> olmalı. `%` (mod) kalanı verir: `17 % 5 == 2` — çift/tek kontrolü, döngüsel indeks
> gibi işlerde çok kullanılır.

## Artırma/azaltma: ++ ve --

Önek (`++x`) ve sonek (`x++`) **farklıdır**:

```java
int x = 5; System.out.println(x++);  // 5 yazar, SONRA x=6 (önce kullan)
int y = 5; System.out.println(++y);  // 6 yazar (önce artır)
```

## Atama operatörleri

```java
=  +=  -=  *=  /=  %=        // a += b  ==  a = a + b
```

## İlişkisel (karşılaştırma) operatörler

```java
==  !=  <  >  <=  >=         // sonuç boolean
```

> **Tuzak:** Nesnelerde `==` **referansı** karşılaştırır, içeriği değil. İçerik için `.equals()`
> kullan (`String`, wrapper'lar).

Örnek 1 (`./Ornek1.java`) aritmetik, atama ve ilişkisel operatörleri gösterir.

## Mantıksal operatörler ve kısa devre

```java
&&  (ve)    ||  (veya)    !  (değil)
```

**Kısa devre (short-circuit)** çok önemlidir: `&&` ilk operand `false` ise ikinciyi
**değerlendirmez**; `||` ilk operand `true` ise ikinciyi değerlendirmez. Bu hem performans hem de
**güvenlik** sağlar:

```java
if (s != null && s.length() > 0) { ... }   // s null ise length() ÇAĞRILMAZ -> NPE önlenir
```

Örnek 2 (`./Ornek2.java`) kısa devreyi (yan etkili metotlarla) gösterir.

## Üçlü (ternary) operatör

`kosul ? a : b` — bir **değer** döndüren mini if-else:

```java
String durum = (yas >= 18) ? "yetişkin" : "reşit değil";
int max = (a > b) ? a : b;
```

Kısa koşullu atamalar için idealdir; ama iç içe üçlü operatörler okunabilirliği bozar (o zaman
`if-else` kullan).

## Operatör önceliği (precedence)

Operatörler belirli bir sırayla değerlendirilir:

```
() > ++/-- /! > * / % > + - > < > <= >= > == != > && > || > ?: > = += ...
```

```java
2 + 3 * 4 == 14    // * önce
(2 + 3) * 4 == 20  // parantez önceliği değiştirir
```

> **Altın kural:** Önceliği ezberlemeye çalışma; **şüphedeysen parantez kullan.** Parantez hem
> doğruluğu garanti eder hem okunabilirliği artırır.

## Diğer operatörler

- **`instanceof`**: Tip kontrolü (+ pattern matching, topic 86).
- **Bit operatörleri** (`& | ^ ~ << >> >>>`): Bit düzeyinde — sonraki konu (topic 90).
- **`+` (String):** İki String'i veya String+değeri birleştirir (`"x" + 5`).

## Özet

Java operatörlerini kategori kategori öğrendik: aritmetik (int bölme/mod tuzağı), önek/sonek
`++/--`, atama ve ilişkisel (Örnek 1); mantıksal operatörler, **kısa devre** (güvenlik!), üçlü
operatör ve **öncelik** ("şüphede parantez"; Örnek 2). Sırada, bit düzeyinde çalışan güçlü ama az
bilinen operatörler: **bitwise operatörler**.
