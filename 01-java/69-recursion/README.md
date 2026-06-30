# Recursion (Özyineleme)

**Özyineleme (recursion)**, bir metodun **kendini çağırması**dır. İlk bakışta tuhaf görünse de,
doğası gereği "kendine benzeyen" problemler için (ağaç gezme, böl-ve-fethet, geri izleme) son derece
doğal ve okunaklı bir araçtır. Bir problemi, aynı problemin daha küçük bir hâli cinsinden ifade
edersin; bu küçülme bir "taban duruma" ulaşınca durur.

## İki olmazsa olmaz parça

Her özyinelemeli çözümün iki bileşeni vardır:

1. **Taban durum (base case):** Özyinelemeyi **durduran** koşul. Olmazsa metot sonsuza kadar
   kendini çağırır ve **`StackOverflowError`** atar.
2. **Özyinelemeli adım:** Problemi **daha küçük** bir hâline indirgeyip kendini çağırma.

```java
long faktoriyel(int n) {
    if (n <= 1) return 1;              // taban durum
    return n * faktoriyel(n - 1);     // özyinelemeli adım (daha küçük probleme)
}
```

Örnek 1 (`./Ornek1.java`) faktöriyel ve fibonacci ile bunu gösterir.

## Performans tuzağı ve memoization

Saf (naive) özyineleme bazen **aynı alt problemleri tekrar tekrar** hesaplar. Klasik örnek
fibonacci: `fib(40)` naive hâliyle milyonlarca tekrar çağrı yapar (üstel). Çözüm **memoization**:
hesaplanan sonuçları sakla, tekrar isteneni doğrudan döndür (doğrusal):

```java
long fib(int n, Map<Integer,Long> bellek) {
    if (n < 2) return n;
    if (bellek.containsKey(n)) return bellek.get(n);
    long r = fib(n-1, bellek) + fib(n-2, bellek);
    bellek.put(n, r);
    return r;
}
```

Örnek 1, naive ile memoized fibonacci'yi **ölçerek** karşılaştırır; aradaki fark dramatiktir.

## Ağaç/iç içe yapıları gezmek

Özyinelemenin en doğal kullanımı **ağaç** yapılarıdır: dosya sistemi, JSON, DOM, organizasyon
şeması... Her düğüm için "kendini + çocuklarını işle" dersin:

```java
long toplamBoyut(Klasor k) {
    long toplam = k.dosyaBoyutu;
    for (Klasor alt : k.altKlasorler) toplam += toplamBoyut(alt); // her çocuk için kendini çağır
    return toplam;
}
```

Örnek 2 (`./Ornek2.java`) bir klasör ağacını gezip toplam boyutu hesaplar ve girintili yazdırır.

## Sınırlar: yığın ve iteratif alternatif

Her özyinelemeli çağrı **çağrı yığınında (call stack)** yer kaplar. Çok derin (veya sonsuz)
özyineleme yığını taşırır → **`StackOverflowError`**. Önemli bir gerçek:

> **Java, tail-call optimizasyonu (kuyruk özyineleme) YAPMAZ.** Yani "son işlem özyinelemeli çağrı"
> olsa bile yığın büyür. Çok derin durumlarda **iteratif** (döngü + gerekirse açık bir yığın/`Deque`)
> çözüm tercih edilir.

Örnek 2 hem `StackOverflowError`'ı (taban durumsuz çağrı) hem iteratif faktöriyel alternatifini
gösterir.

## Ne zaman özyineleme, ne zaman döngü?

- **Özyineleme:** Problem doğal olarak özyinelemeli (ağaç/grafik, böl-ve-fethet, backtracking) ve
  derinlik makulse — kod çok daha okunaklı olur.
- **İterasyon:** Derinlik büyük/kontrolsüzse, basit doğrusal işse veya performans kritikse — yığın
  taşma riski yoktur.

## Özet

Özyinelemenin taban durum + özyinelemeli adımdan oluştuğunu; performans tuzağını ve **memoization**
çözümünü (Örnek 1); ağaç gezmedeki doğal kullanımını, `StackOverflowError` sınırını ve iteratif
alternatifi (Örnek 2) öğrendik; Java'nın tail-call optimizasyonu yapmadığını vurguladık. Sırada,
dış programları çalıştırma: **Process API**.
