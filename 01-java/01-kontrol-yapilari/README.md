# Kontrol Yapıları

Bir program, satırları yukarıdan aşağıya çalıştırır. Ama gerçek hayattaki problemler bu
kadar düz değildir: bazen bir koşula göre farklı bir yol seçmemiz, bazen bir işi defalarca
tekrarlamamız gerekir. İşte **kontrol yapıları** programın akışını yönlendirmemizi sağlar.
Bu bölümde kararlar (koşullar) ve tekrarlar (döngüler) ile programı canlandırıyoruz.

## Koşul ifadeleri

### if / else if / else

En temel karar yapısı `if`'tir. Parantez içindeki koşul `true` ise blok çalışır; değilse
`else` bloğuna düşeriz. Birden fazla durumu sırayla denemek için `else if` zincirleriz:

```java
if (puan >= 90)       harf = 'A';
else if (puan >= 80)  harf = 'B';
else                  harf = 'F';
```

Koşullar yukarıdan aşağıya denenir ve **ilk** uyan blok çalışır. Bu yüzden sıralama
önemlidir: daha dar (özel) koşulu üste yazarsın.

### Ternary (üçlü) operatör

Tek bir değeri iki seçenekten birine göre belirleyeceksen, kısa bir yazım vardır:

```java
String durum = (harf == 'F') ? "Kaldı" : "Geçti";
```

`koşul ? doğruysa : yanlışsa` — okunabilirliği bozmadığı sürece kısa atamalar için idealdir.

### switch

Bir değişkeni birçok sabit değere karşı kontrol edeceksen `switch` daha okunaklıdır.
Modern **switch expression** (Java 14+) ok (`->`) sözdizimiyle hem `break` derdini ortadan
kaldırır hem de doğrudan bir değer üretebilir:

```java
String gunAdi = switch (gunNo) {
    case 1 -> "Pazartesi";
    case 6, 7 -> "Hafta sonu"; // birden çok etiket gruplanabilir
    default -> "Geçersiz gün";
};
```

Örnek 1 (`./Ornek1.java`) `if/else`, ternary ve `switch`'i bir not değerlendirme ve gün
adı bulma senaryosunda bir arada gösterir.

## Döngüler

Aynı işi tekrar tekrar yapmak için döngüler kullanırız. Üç temel döngü vardır ve hangisini
seçeceğin işin doğasına bağlıdır.

### for

Tekrar sayısını baştan bildiğin durumlarda en uygunudur. Tek satırda başlangıç, koşul ve
artışı bir arada tanımlar:

```java
for (int i = 1; i <= 10; i++) {
    System.out.println(5 * i);
}
```

### while

Tekrar sayısını bilmiyorsan ama bir koşul sağlandığı sürece dönmek istiyorsan `while`
kullanırsın. Koşul **önce** kontrol edilir; ilk seferde sağlanmazsa gövde hiç çalışmaz.

### do-while

`while`'ın aksine gövde **en az bir kez** çalışır, koşul sonra kontrol edilir. Menü
gösterme gibi "önce yap, sonra devam edip etmeyeceğine bak" durumlarında kullanışlıdır.

Örnek 2 (`./Ornek2.java`) çarpım tablosu, toplam ve faktöriyel hesaplarıyla üç döngü
türünü de karşılaştırmalı gösterir.

## break ve continue

Döngülerin akışını içeriden de yönetebiliriz:

- **`break`**: Döngüyü hemen sonlandırır, dışarı çıkar.
- **`continue`**: O anki adımı atlar, döngünün bir sonraki adımına geçer.

İç içe döngülerde `break` yalnızca **içindeki** döngüden çıkar. Örnek 3 (`./Ornek3.java`)
tek sayıları seçmek için `continue`, bir koşulu sağlayan ilk sayıyı bulmak ve asal sayı
testini erken bitirmek için `break` kullanır; ayrıca iç içe döngülerle bir yıldız deseni
çizer.

## Özet

Kararlar için `if/else`, ternary ve `switch`'i; tekrarlar için `for`, `while` ve
`do-while`'ı gördük. `break` ve `continue` ile döngü akışını inceden inceye yönetmeyi
öğrendik. Bu yapılar, az sonra göreceğimiz **metotlar** ile birleşince kodun gerçekten
güçlenmeye başlar.
