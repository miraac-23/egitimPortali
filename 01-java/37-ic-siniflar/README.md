# İç Sınıflar (Inner Classes)

Java'da bir sınıfın içine başka bir sınıf yerleştirebilirsin. Bu **iç sınıflar (nested classes)**,
yalnızca bir yerde kullanılan yardımcı yapıları, ait oldukları sınıfın yanında tutmanı sağlar —
kod hem düzenli hem de kapsüllenmiş olur. İç sınıfların dört türü vardır; her birinin kendine özgü
kuralları ve kullanım yeri vardır.

## Dört tür

1. **Üye (member) iç sınıf** — `static` olmayan, dış nesneye bağlı.
2. **Static nested sınıf** — `static`, dış nesneye bağlı değil.
3. **Yerel (local) sınıf** — bir metodun içinde tanımlı.
4. **Anonim (anonymous) sınıf** — isimsiz, anında tanımlanan (ayrı bir konu).

## 1) Üye iç sınıf

Dış nesnenin bir parçasıdır ve onun (private dahil) üyelerine **doğrudan erişir**. Her üye iç
nesnenin, gizli bir "dış nesne" referansı vardır; bu yüzden oluşturmak için önce bir dış nesne
gerekir:

```java
Banka banka = new Banka("Ziraat");
Banka.Hesap hesap = banka.new Hesap(...);   // disNesne.new Ic(...)
```

Örnek 1 (`./Ornek1.java`): Bir `Banka` içindeki `Hesap` iç sınıfı, dış nesnenin `private bankaAdi`
alanına erişir. Bu tür sınıflar, dış sınıfın iç durumuyla sıkı çalışan yardımcılar (ör. bir
koleksiyonun `Iterator`'ı) için idealdir.

## 2) Static nested sınıf

`static` olduğundan dış nesneye **bağlı değildir**; doğrudan `Dis.StatikIc` ile oluşturulur. Dış
sınıfın yalnızca `static` üyelerine erişebilir. En yaygın kullanımı **Builder** ve yardımcı veri
yapılarıdır:

```java
Mesaj m = new Mesaj.Builder().kime("...").konu("...").build();
```

Örnek 2 (`./Ornek2.java`) bir static nested `Builder` ve sade bir static nested sınıf gösterir.

> **İpucu:** Bir iç sınıfın dış nesneye ihtiyacı yoksa onu **`static` yap**. Aksi halde her iç
> nesne gereksiz yere bir dış-nesne referansı taşır (ve bellek sızıntısına yol açabilir).

## 3) Yerel (local) sınıf

Bir metodun (veya bloğun) içinde tanımlanır ve yalnızca orada görünür. Metodun
**effectively final** yerel değişkenlerine erişebilir:

```java
void m() {
    String onek = "[LOG] ";
    class Loglayici { void log(String s) { System.out.println(onek + s); } }
    new Loglayici().log("...");
}
```

Çok dar kapsamlı, tek seferlik yardımcılar içindir. Örnek 2 bunu da gösterir. Pratikte yerel sınıf
yerine genelde daha kısa olan **lambda** veya **anonim sınıf** tercih edilir.

## 4) Anonim sınıf

İsmi olmayan, bir arayüzü/abstract sınıfı anında uygulayan iç sınıftır. Tek seferlik
uygulamalar için kullanılır ve genelde **lambda**'ya dönüşür. Bu, kendi başına bir konudur
(bir sonraki bölüm).

## Ne zaman hangisi?

| Tür | Dış nesne gerekir mi? | Kullanım |
|-----|:---------------------:|----------|
| Üye iç sınıf | Evet | Dış durumla sıkı çalışan yardımcı (Iterator) |
| Static nested | Hayır | Builder, yardımcı veri yapısı |
| Yerel sınıf | (metoda göre) | Tek metotta kullanılan dar yardımcı |
| Anonim sınıf | (duruma göre) | Tek seferlik arayüz uygulaması (genelde lambda) |

## Özet

İç sınıfların dört türünü öğrendik: dış nesneye bağlı **üye iç sınıf** (Örnek 1), bağımsız
**static nested** sınıf (Builder; Örnek 2) ve metot-içi **yerel sınıf**. "Dış nesne gerekmiyorsa
static yap" kuralı önemlidir. İç sınıflar, ilgili yardımcı yapıları ait oldukları yerde, kapsüllü
tutmanı sağlar. Sırada, iç sınıfların en kısa ve en sık kullanılan biçimi: **anonim sınıflar**.
