# Lambda İfadeleri ve Functional Interface'ler

Java 8, dile köklü bir yenilik getirdi: davranışı (kodu) bir değer gibi taşıyabilmek.
**Lambda ifadeleri** sayesinde bir metodu, bir değişkene atayıp parametre olarak
geçebiliriz. Bu, koleksiyonları işlemekten olay dinleyicilerine kadar her yerde kodu
kısaltır ve okunabilir kılar. Lambda'ları anlamak, bir sonraki konu olan Stream API'nin de
kapısını açar.

## Functional interface nedir?

Lambda'nın arkasındaki kavram **functional interface**'tir: yalnızca **tek bir soyut metodu**
olan interface. Bir lambda, işte bu tek metodun gövdesini sağlar. `@FunctionalInterface`
anotasyonu, derleyicinin "gerçekten tek soyut metot var mı?" diye kontrol etmesini sağlar:

```java
@FunctionalInterface
interface Hesap {
    int uygula(int a, int b);
}
```

## Lambda sözdizimi

Eskiden bir interface'i kısaca uygulamak için anonim iç sınıf yazardık; lambda aynı işi çok
daha az gürültüyle yapar:

```java
// Anonim sınıf
Hesap topla = new Hesap() {
    public int uygula(int a, int b) { return a + b; }
};
// Lambda — aynısı
Hesap topla = (a, b) -> a + b;
```

Lambda'nın anatomisi: `(parametreler) -> gövde`. Gövde tek ifadeyse süslü parantez ve
`return` gerekmez; çok satırlıysa `{ ... return ...; }` kullanırsın. Örnek 1
(`./Ornek1.java`) anonim sınıftan lambda'ya geçişi ve lambda'yı argüman olarak geçmeyi gösterir.

## Hazır functional interface'ler

`java.util.function` paketi, en sık ihtiyaç duyacağın functional interface'leri hazır sunar;
böylece her seferinde kendin tanımlamazsın:

| Interface | Metot | Anlamı |
|-----------|-------|--------|
| `Predicate<T>` | `boolean test(T)` | Bir koşulu sınar |
| `Function<T,R>` | `R apply(T)` | T'yi R'ye dönüştürür |
| `Consumer<T>` | `void accept(T)` | T'yi alır, yan etki yapar |
| `Supplier<T>` | `T get()` | Girdisiz değer üretir |
| `BiFunction<T,U,R>` | `R apply(T,U)` | İki girdi, bir çıktı |
| `UnaryOperator<T>` | `T apply(T)` | T → T |

Bunların çoğu birleştirilebilir: `predicate.and(...)`, `function.andThen(...)` gibi. Örnek 2
(`./Ornek2.java`) bu beş tipi tek tek çalıştırır ve birleştirme metotlarını gösterir.

## Method reference

Lambda yalnızca var olan bir metodu çağırıyorsa, onu daha da kısaltabilirsin: **method
reference**. `s -> System.out.println(s)` yerine `System.out::println` yazarsın:

```java
liste.forEach(System.out::println);
Comparator.comparingInt(Kisi::yas);
```

Dört biçimi vardır: `Tip::statikMetot`, `nesne::metot`, `Tip::örnekMetot`, `Tip::new`
(constructor). Okunabilirliği bozmadığı sürece method reference daha temizdir.

## Pratik: Comparator ile sıralama

Lambda ve method reference'ın en sık kullanıldığı yerlerden biri sıralamadır.
`Comparator.comparing(...)` bir alana göre karşılaştırıcı üretir; `thenComparing(...)` ile
eşitlik durumunda ikinci ölçüt eklersin, `reversed()` ile sırayı tersine çevirirsin:

```java
kisiler.sort(Comparator.comparingInt(Kisi::yas).thenComparing(Kisi::ad));
```

Örnek 3 (`./Ornek3.java`) kişileri yaşa, sonra ada; ardından şehre ve azalan yaşa göre
sıralayarak çok ölçütlü karşılaştırıcıları gösterir.

## Özet

Lambda'ların davranışı bir değer gibi taşımamızı sağladığını, functional interface'lerin
bunun temeli olduğunu öğrendik. `java.util.function` paketindeki hazır tipleri, method
reference'ı ve `Comparator` ile pratik sıralamayı gördük. Bu temel, hemen sıradaki konuda —
**Stream API**'de — verileri akış halinde işlerken doğrudan işine yarayacak.
