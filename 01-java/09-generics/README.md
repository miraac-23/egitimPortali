# Generics (Jenerikler)

Bir önceki bölümde `List<String>`, `Map<String, Integer>` gibi yazımlar gördün. Açılı
parantez içindeki tipler işte **generics**'tir. Generics, bir sınıf veya metodu belirli bir
tipe bağlamadan, "hangi tiple çalışacağı sonradan belirlensin" diyerek yazmamızı sağlar.
Faydası ikidir: **tip güvenliği** (yanlış tip hatasını derleme anında yakalarsın) ve
**yeniden kullanılabilirlik** (aynı kodu her tiple kullanırsın).

## Neden generics?

Generics olmadan koleksiyonlar `Object` tutardı; her okumada elle dönüştürme (cast) yapman
ve hata riskini taşıman gerekirdi:

```java
List liste = new ArrayList();
liste.add("metin");
String s = (String) liste.get(0); // elle cast, hataya açık
liste.add(42);                     // derleyici engellemez!
```

Generics ile derleyici tipi bilir; yanlış ekleme **derlenmez**, okurken cast gerekmez:

```java
List<String> liste = new ArrayList<>();
liste.add("metin");
String s = liste.get(0); // cast yok
// liste.add(42);        // derleme hatası
```

## Generic sınıf ve generic metot

Kendi generic tiplerini yazabilirsin. Tip parametresini açılı parantezle bildirirsin
(geleneksel olarak `T`, `E`, `K`, `V` harfleri kullanılır):

```java
class Kutu<T> {
    private T icerik;
    T al() { return icerik; }
}
Kutu<String> k = new Kutu<>("merhaba");
```

Bir metodu da generic yapabilirsin; tip parametresini dönüş tipinden önce bildirirsin:

```java
static <T> T ilk(T[] dizi) { return dizi[0]; }
```

Örnek 1 (`./Ornek1.java`) generic bir `Kutu<T>` sınıfı ve generic `yazdir`/`ilk` metotlarını
farklı tiplerle kullanır.

## Sınırlı tipler (bounded types)

Bazen tip parametresinin "herhangi bir tip" değil, belirli bir tipin alt tipi olmasını
isteriz. `<T extends Number>` der ki: "T, `Number` veya alt tipi olmalı." Bu sınır sayesinde
T üzerinde `Number` metotlarını (`doubleValue()` gibi) çağırabilirsin:

```java
static <T extends Number> double toplam(List<T> liste) {
    double t = 0;
    for (T e : liste) t += e.doubleValue();
    return t;
}
```

Birden çok sınır da koyabilirsin: `<T extends Number & Comparable<T>>`. Örnek 2
(`./Ornek2.java`) sayı listeleri üzerinde toplam, ortalama ve en büyüğü hesaplar; `String`
listesi göndermenin neden derlenmediğini açıklar.

## Wildcard'lar: ? extends ve ? super

Metot parametrelerinde esneklik için `?` (wildcard) kullanırız:

- **`? extends T`** — "T veya alt tipi". Listeden **okumak** için uygundur (Producer).
  `List<? extends Number>` parametresi `List<Integer>` ve `List<Double>` kabul eder.
- **`? super T`** — "T veya üst tipi". Listeye **yazmak** için uygundur (Consumer).
  `List<? super Integer>` parametresi `List<Integer>`, `List<Number>`, `List<Object>` kabul eder.

Bu seçimi hatırlamanın yolu **PECS** kuralıdır: **P**roducer **E**xtends, **C**onsumer
**S**uper. Veriyi üreten (okuduğun) kaynak için `extends`, tükettiğin (yazdığın) hedef için
`super`. Örnek 3 (`./Ornek3.java`) ikisini de uygular ve farkı gösterir.

## Type erasure'a kısa bir not

Java generics'i **type erasure** ile uygular: tip bilgisi derleme sırasında kullanılır,
çalışma zamanında "silinir". Yani çalışma anında `List<String>` ve `List<Integer>` aslında
aynı `List` tipidir. Bunun pratik sonucu: `new T[]` yazamazsın, `instanceof List<String>`
yapamazsın. Günlük kullanımda bunları nadiren dert edersin ama farkında olmak iyidir.

## Özet

Generics ile tip güvenli ve yeniden kullanılabilir kod yazmayı öğrendik: generic sınıf/metot,
sınırlı tipler (`extends`) ve wildcard'lar (`? extends`/`? super`) ile PECS kuralı. Bu bilgi,
koleksiyonları ve birazdan göreceğin Stream API'yi rahatça kullanmanın anahtarıdır. Sırada,
sabit değer kümeleri için **enum** ve ilkel tiplerin nesne karşılığı **wrapper** sınıfları var.
