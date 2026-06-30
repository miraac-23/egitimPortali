# Anonim Sınıflar (Anonymous Classes)

Bazen bir arayüzü veya soyut sınıfı **yalnızca bir kez**, sadece o anda kullanmak için
uygulamak istersin; ona ayrı bir isim/dosya vermek gereksizdir. **Anonim sınıflar** tam da bunun
içindir: bir sınıfı tanımladığın yerde, isim vermeden oluşturursun. Java 8 öncesinin "her yerde"
kullandığı bu yapı, bugün çoğu yerde lambda'lara dönüşmüş olsa da hâlâ önemli bir yere sahiptir.

## Söz dizimi: tanımla ve anında oluştur

Anonim sınıf, `new Tip() { ... }` biçimindedir: belirtilen arayüzü **uygular** veya sınıfı
**uzatır**, gövdeyi yazar ve aynı anda bir nesne **oluşturur**:

```java
Selamlayici s = new Selamlayici() {        // arayüzü anında uygula
    @Override public String selamla(String ad) { return "Sayın " + ad; }
};

Hayvan h = new Hayvan() {                   // abstract sınıfı anında uzat
    @Override String ses() { return "Hav"; }
};
```

Klasik kullanımları: `Comparator`, `Runnable`, olay dinleyicileri (event listeners), `Iterator`.
Örnek 1 (`./Ornek1.java`) bir arayüzü, bir abstract sınıfı ve bir `Comparator`'ı anonim sınıfla
uygular.

## Anonim sınıf mı, lambda mı?

Java 8'den beri, **tek soyut metotlu** (functional) arayüzler için lambda çok daha kısadır ve
tercih edilir:

```java
// Anonim sınıf
Function<Integer,Integer> kare = new Function<>() {
    public Integer apply(Integer x) { return x * x; }
};
// Lambda — aynısı
Function<Integer,Integer> kare = x -> x * x;
```

Ama her durumda lambda kullanılamaz. **Anonim sınıf gerekir** eğer:

- Bir **abstract sınıfı** uzatman gerekiyorsa (lambda yalnızca tek-metotlu arayüzler içindir),
- Arayüzde **birden çok metot** varsa,
- Ek **alan/durum** (state) tutman gerekiyorsa,
- `this`'in **anonim nesneyi** göstermesi gerekiyorsa.

> **`this` farkı (önemli incelik):** Anonim sınıfta `this`, anonim nesnenin kendisidir. Lambda'da
> ise `this`, **çevreleyen** nesnedir (lambda yeni bir kapsam/`this` açmaz). Örnek 2
> (`./Ornek2.java`) hem lambda-anonim karşılaştırmasını hem de bu `this` farkını gösterir.

## Değişken yakalama

Anonim sınıflar (ve lambda'lar), çevreleyen metodun **effectively final** (bir kez atanıp
değişmeyen) yerel değişkenlerini "yakalar" (capture). Bu yüzden bir döngü değişkenini doğrudan
değiştirip yakalamaya çalışmak derleme hatası verir; bunun yerine final bir kopya kullanılır.

## Nerede karşına çıkar?

- **GUI/olay yönetimi:** "butona tıklanınca şunu yap" (Swing/AWT — ileride).
- **Eşzamanlılık:** `new Thread(new Runnable() { ... })` (bugün `new Thread(() -> ...)`).
- **Sıralama/filtreleme:** `Comparator`, `Predicate` (bugün lambda).
- **Geri çağırma (callback):** tek seferlik davranış enjeksiyonu.

## Özet

Anonim sınıfların bir arayüzü/abstract sınıfı isim vermeden, tek kullanımlık biçimde uygulamayı
sağladığını (Örnek 1); functional arayüzlerde lambda'nın daha kısa olduğunu ama abstract sınıf
uzatma, çok metot, durum ve `this` davranışı gerektiğinde anonim sınıfın şart olduğunu (Örnek 2)
öğrendik. Anonim sınıflar, iç sınıfların en yoğun ve en pratik biçimidir. Sırada, tüm uygulamada
tek bir örnek garantileyen klasik desen: **Singleton sınıf**.
