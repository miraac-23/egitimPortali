# Constructor'lar (Yapıcılar)

**Constructor (yapıcı)**, bir nesne `new` ile oluşturulurken otomatik çağrılan özel bir metottur.
Görevi, nesneyi **geçerli bir başlangıç durumuna** getirmektir — alanları anlamlı değerlerle
doldurmak. İyi tasarlanmış constructor'lar, bir nesnenin oluştuğu andan itibaren tutarlı (geçersiz
duruma asla düşmeyen) olmasını sağlar. OOP temellerinde değindik; burada tüm biçimlerini ve
inceliklerini derinlemesine ele alıyoruz.

## Constructor'ın özellikleri

- Adı **sınıf adıyla aynıdır**.
- **Dönüş tipi yoktur** (`void` bile değil) — onu metottan ayıran budur.
- `new` ile nesne oluşturulurken **otomatik** çağrılır.

```java
class Urun {
    String ad;
    Urun(String ad) { this.ad = ad; }   // constructor
}
new Urun("Klavye");                      // constructor çağrılır
```

## Varsayılan constructor

Hiç constructor yazmazsan, derleyici parametresiz, boş bir **varsayılan constructor** ekler. Ama
**parametreli** bir constructor yazarsan, derleyici artık varsayılanı **eklemez** — gerekiyorsa
kendin yazmalısın:

```java
class A { }                  // derleyici A() ekler
class B { B(int x){} }        // B() YOK; new B() derlenmez (kendin eklemelisin)
```

## Aşırı yükleme (overloading) ve this()

Farklı parametre listeleriyle **birden çok** constructor tanımlayabilirsin (overloading). Kod
tekrarını önlemek için, biri diğerini **`this(...)`** ile çağırır (constructor zincirleme):

```java
Urun() { this("İsimsiz", 0, 0); }          // tam constructor'a delege
Urun(String ad, double f) { this(ad, f, 0); }
Urun(String ad, double f, int stok) { ... }  // asıl başlatma TEK yerde
```

`this(...)` constructor'ın **ilk satırı** olmalıdır. Örnek 1 (`./Ornek1.java`) varsayılan/
parametreli/aşırı yüklenmiş constructor'ları ve `this()` zincirlemesini gösterir.

## Kalıtımda super()

Kalıtımda nesne kurulurken **önce üst sınıf** constructor'ı çalışır (taban hazır olmadan alt sınıf
kurulamaz). **`super(...)`** üst sınıf constructor'ını çağırır:

```java
class Otomobil extends Arac {
    Otomobil(String marka, int kapi) {
        super(marka);          // ÜST constructor — ilk satır
        this.kapi = kapi;
    }
}
```

- `super(...)` constructor'ın **ilk satırı** olmalıdır.
- Yazmazsan derleyici **parametresiz `super()`'i örtük ekler**. Üst sınıfta parametresiz
  constructor **yoksa** hata alırsın — o zaman açıkça `super(...)` çağırmalısın.

Örnek 2 (`./Ornek2.java`) `super()` zincirini (önce üst, sonra alt) ve bir **kopya
constructor**'ını (var olan nesneden bağımsız kopya) gösterir.

## Başlatma sırası (tam)

Bir nesne kurulurken :

1. (İlk kez) üst → alt **static** alanlar + static bloklar (bir kez).
2. Üst sınıf: alan başlatmaları + instance bloklar → üst constructor gövdesi.
3. Alt sınıf: alan başlatmaları + instance bloklar → alt constructor gövdesi.

## İyi uygulamalar

- Nesneyi her zaman **geçerli** bir durumda oluştur (zorunlu alanları constructor parametresi yap;
  doğrulamayı constructor'da yap).
- Çok sayıda/opsiyonel parametre varsa **Builder** deseni kullan.
- Değişmez (immutable) nesneler için tüm alanları `final` yapıp constructor'da ata.
- Constructor içinde **override edilebilir metot çağırma** (alt sınıf henüz kurulmamışken
  çalışabilir — sinsi hata).

## Özet

Constructor'ların nesneyi geçerli başlangıç durumuna getirdiğini; varsayılan/parametreli/aşırı
yüklenmiş biçimleri ve `this()` zincirlemesini (Örnek 1); kalıtımda `super()` zincirini, başlatma
sırasını ve kopya constructor'ı (Örnek 2) öğrendik; iyi uygulamalara (geçerlilik, Builder,
immutability) değindik. Nesne başlatmayı doğru yapmak, sağlam OOP'nin temelidir.
