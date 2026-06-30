# Enum ve Wrapper Sınıfları

Bu bölümde iki küçük ama günlük hayatta sık kullanılan konuyu ele alıyoruz. **Enum**, sabit
ve sınırlı bir değer kümesini tip güvenli biçimde temsil eder (haftanın günleri, sipariş
durumu, yönler). **Wrapper sınıfları** ise ilkel tiplerin (`int`, `double`...) nesne
karşılığıdır ve koleksiyonlarla, generics'le çalışırken kaçınılmazdır.

## Enum

Bir değer yalnızca belirli birkaç seçenekten biri olabiliyorsa, onu `String` veya `int` ile
temsil etmek hataya açıktır ("TODO" mu "Todo" mu?). `enum` bu seçenekleri sabit, tip güvenli
bir küme olarak tanımlar:

```java
enum Yon { KUZEY, GUNEY, DOGU, BATI }
Yon y = Yon.DOGU;          // sadece tanımlı değerler atanabilir
```

Enum'lar `switch` ile çok iyi çalışır (case'lerde adı sade yazarsın), `values()` ile tüm
değerleri dizi olarak verir, `valueOf("...")` ile metinden enum'a çevirir, `name()` ve
`ordinal()` ile adını ve sırasını okursun. Örnek 1 (`./Ornek1.java`) bunları gösterir.

### Davranışlı enum'lar

Java'da enum sadece bir etiket değildir; tam bir sınıf gibi **alan, constructor ve metot**
içerebilir. Hatta her sabit kendi davranışını verebilir. Bu, "her seçeneğin kendine özgü bir
işi var" durumlarını çok zarif modeller:

```java
enum Islem {
    TOPLA("+") { public double uygula(double a, double b) { return a + b; } },
    CARP("*")  { public double uygula(double a, double b) { return a * b; } };
    private final String sembol;
    Islem(String sembol) { this.sembol = sembol; }
    public abstract double uygula(double a, double b);
}
```

Örnek 2 (`./Ornek2.java`) dört aritmetik işlemi davranışlı bir enum olarak tanımlar ve hepsini
tek döngüde uygular — `if/switch` yığını yerine temiz, genişletilebilir bir yapı.

## Wrapper sınıfları

Her ilkel tipin bir nesne karşılığı (wrapper) vardır: `int`→`Integer`, `double`→`Double`,
`boolean`→`Boolean`, `char`→`Character`... Neden gerekir? Çünkü koleksiyonlar ve generics
nesne tutar, ilkel tip tutamaz: `List<int>` yazamazsın, `List<Integer>` yazarsın.

### Autoboxing ve unboxing

Java, ilkel tip ile wrapper arasında otomatik dönüşüm yapar:

```java
List<Integer> liste = new ArrayList<>();
liste.add(5);          // autoboxing: int -> Integer
int ilk = liste.get(0); // unboxing: Integer -> int
```

Bu kolaylık genelde sorunsuzdur ama performans-kritik döngülerde gereksiz kutulama maliyet
yaratabilir; orada `int[]` veya `IntStream` tercih edilir.

### Parsing ve yardımcılar

Wrapper sınıfları metinden sayıya çevirmenin ve faydalı sabitlerin evidir:

```java
Integer.parseInt("123");   // 123
Double.parseDouble("3.14"); // 3.14
Integer.MAX_VALUE;          // en büyük int
```

### Dikkat: Integer cache tuzağı

Bu, ünlü bir tuzaktır. Java, `-128..127` arası `Integer` nesnelerini önbelleğe alır. Bu
aralıkta `==` aynı nesneyi gösterir (true), ama aralık dışında farklı nesneler oluşur:

```java
Integer a = 100, b = 100;   a == b;  // true  (önbellek)
Integer c = 200, d = 200;   c == d;  // false (farklı nesne!)
c.equals(d);                          // true  (doğrusu bu)
```

> **Kural:** Wrapper'ları **değer** olarak karşılaştırırken her zaman `equals()` kullan;
> `==` referans karşılaştırır ve seni yanıltır. Örnek 3 (`./Ornek3.java`) bunu canlı gösterir.

## Özet

Enum ile sabit değer kümelerini tip güvenli, hatta davranışlı biçimde modellemeyi; wrapper
sınıfları ile ilkel tipleri nesne dünyasına taşımayı, autoboxing/unboxing'i, parsing'i ve
`Integer` önbellek tuzağını öğrendik. Sırada, modern Java'nın en sevilen özelliklerinden
biri: **lambda ifadeleri ve functional interface'ler**.
