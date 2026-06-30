# Aggregation ve Composition (HAS-A İlişkisi)

Nesneler tek başına yaşamaz; birbirleriyle ilişkilidir. Kalıtım bölümünde **IS-A** ("bir
tür ... olma": Köpek bir Hayvandır) ilişkisini gördük. Ama nesneler arası en yaygın ilişki aslında
**HAS-A** ("sahip olma": Araba bir Motora sahiptir) ilişkisidir. Bu ilişkiyi modellemenin iki
biçimi vardır — **aggregation** (gevşek) ve **composition** (güçlü) — ve doğru olanı seçmek temiz
tasarımın anahtarıdır.

## HAS-A: nesneleri birleştirmek

Bir sınıf, başka bir sınıf tipinde alanlar içerdiğinde HAS-A ilişkisi kurar:

```java
class Araba {
    private Motor motor;      // Araba HAS-A Motor
    private List<Tekerlek> tekerlekler;
}
```

Bu, kodu küçük, tek-sorumluluklu parçalardan inşa etmeyi sağlar. HAS-A'nın iki türü, parçanın
bütüne ne kadar **bağımlı** olduğuna göre ayrılır.

## Aggregation (gevşek HAS-A)

Parça, bütünden **bağımsız** var olabilir. Bütün yok olsa bile parça yaşamaya devam eder; hatta
aynı parça birden çok bütüne ait olabilir. Tipik olarak parça **dışarıda yaratılır** ve sınıfa
referans olarak verilir:

```java
Oyuncu o = new Oyuncu("Ada");   // oyuncu bağımsız var
takim.oyuncuEkle(o);            // takım ona referans tutar
```

Örnek 1 (`./Ornek1.java`): Bir `Takim`, `Oyuncu`'lara sahiptir ama oyuncular takımdan bağımsızdır
— takım dağılsa bile oyuncular yaşar ve başka takıma geçebilir. UML'de içi boş elmas (`<>--`) ile
gösterilir.

## Composition (güçlü HAS-A)

Parça, bütüne **sıkıca** bağlıdır; ömrü bütünle başlar ve biter. Bütün olmadan parça anlamsızdır.
Tipik olarak parça, sınıfın **içinde yaratılır** ve dışarı sızdırılmaz:

```java
class Ev {
    private final List<Oda> odalar;
    Ev(int n) { /* odaları İÇERİDE yarat */ }   // odalar ev olmadan var olamaz
}
```

Örnek 2 (`./Ornek2.java`): Bir `Ev`, `Oda`'lardan oluşur; odalar ev oluşturulurken içeride yaratılır
ve ev yok olursa anlamını yitirir. UML'de içi dolu elmas (`*--`) ile gösterilir.

| | Aggregation | Composition |
|---|-------------|-------------|
| Bağ | Gevşek | Güçlü |
| Parçanın ömrü | Bağımsız | Bütüne bağlı |
| Parça nasıl gelir | Dışarıdan (referans) | İçeride yaratılır |
| Örnek | Takım–Oyuncu | Ev–Oda, İnsan–Kalp |
| UML | İçi boş elmas | İçi dolu elmas |

## "Kalıtım yerine kompozisyon" (composition over inheritance)

OOP'nin en değerli pratik kurallarından biri: **mümkün olduğunca kalıtım yerine kompozisyonu
tercih et.** Neden?

- **Esneklik:** Davranışı çalışma zamanında değiştirebilirsin (farklı bir parça enjekte ederek).
  Kalıtım derleme zamanında sabittir.
- **Gevşek bağlılık:** Alt sınıf, üst sınıfın iç ayrıntısına sıkıca bağlıdır (kırılgan taban sınıf
  problemi). Kompozisyonda yalnızca arayüze bağlanırsın.
- **Çoklu davranış:** Java tek kalıtıma izin verir; ama bir sınıf birçok parçaya sahip olabilir.

Örneğin "loglayan bir liste" istiyorsan, `ArrayList`'ten türetmek yerine (kalıtım), bir `List`'i
**içeren** ve ona delege eden bir sınıf yazmak (kompozisyon) çok daha sağlamdır. Spring'in
dependency injection'ı da özünde kompozisyondur: nesneler bağımlılıklarını (parçalarını) dışarıdan
alır.

## Özet

Nesneler arası HAS-A ilişkisini; gevşek **aggregation** (Takım–Oyuncu, parça bağımsız) ile güçlü
**composition**'ı (Ev–Oda, parça bütüne bağlı) ve "kalıtım yerine kompozisyon" ilkesini öğrendik.
Bu ilişkiler, esnek ve sürdürülebilir nesne tasarımının temelidir. Sırada, bir sınıfın içine başka
sınıflar yerleştirmenin yolu: **iç sınıflar (inner classes)**.
