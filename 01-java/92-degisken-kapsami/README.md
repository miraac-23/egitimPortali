# Değişken Kapsamı (Variable Scope)

Bir değişkenin **kapsamı (scope)**, ona nereden erişilebileceğini ve ne kadar süre yaşayacağını
belirler. Yanlış kapsam seçimi, "değişken bulunamıyor" derleme hatalarından, "değer beklediğim gibi
değişmedi" mantık hatalarına kadar birçok soruna yol açar. Java'da dört temel değişken türü ve
kapsamı vardır; bunları net ayırmak temiz ve hatasız kod yazmanın anahtarıdır.

## Dört değişken türü ve kapsamı

| Tür | Nerede tanımlı | Kapsam (erişim) | Ömür | Varsayılan değer |
|-----|----------------|-----------------|------|------------------|
| **Yerel (local)** | Metot/yapıcı içinde | Yalnızca o metot | Metot çağrısı boyunca | **Yok** (atanmalı) |
| **Blok** | `{ }` içinde (for/if...) | Yalnızca o blok | Blok boyunca | Yok (atanmalı) |
| **Örnek (instance)** | Sınıf gövdesinde (static değil) | Tüm sınıf (nesne üzerinden) | Nesne yaşadıkça | Var (0/null/false) |
| **Statik (static)** | Sınıf gövdesinde (`static`) | Tüm sınıf (paylaşılan) | Program boyunca | Var (0/null/false) |

```java
class Sayac {
    static int toplam;     // STATİK: tüm nesneler paylaşır (tek kopya)
    int deger;             // ÖRNEK: her nesnenin kendi kopyası
    void m() {
        int yerel = 5;     // YEREL: yalnızca m() içinde
        for (int i = 0; i < 3; i++) {
            int blok = i;  // BLOK: yalnızca döngü içinde
        }
        // i ve blok burada ERİŞİLEMEZ
    }
}
```

Örnek 1 (`./Ornek1.java`) dört türü birlikte gösterir: statik alan paylaşılır (sayaç),
örnek alanı nesneye özeldir, yerel/blok değişkenleri dar kapsamlıdır.

> **Önemli fark:** Yerel değişkenlerin **varsayılan değeri yoktur** — kullanmadan önce atanmalıdır
> (yoksa derleme hatası). Alanlar (instance/static) ise otomatik varsayılan alır (sayılar 0,
> nesneler null, boolean false).

## Gölgeleme (shadowing)

Bir yerel değişken veya parametre, aynı isimli bir **alanla** aynı kapsamdaysa onu **gölgeler**:
o isim artık yereli gösterir. Alana erişmek için **`this.`** gerekir:

```java
class Kisi {
    private int yas;
    void setYas(int yas) {      // parametre 'yas', alanı gölgeler
        this.yas = yas;         // this.yas = ALAN, yas = PARAMETRE
    }
}
```

> **Klasik hata:** `this` unutmak. `yas = yas;` parametreyi kendine atar — **alan değişmez!**
> Doğrusu `this.yas = yas;`. Bu, setter/constructor'larda en sık yapılan hatalardandır (IDE'ler
> uyarır). Örnek 2 (`./Ornek2.java`) bu tuzağı canlı gösterir.

> İki **yerel** değişken aynı kapsamda aynı ada sahip **olamaz** (derleme hatası). Gölgeleme
> yalnızca yerel/parametre ↔ alan arasında olur.

## İyi uygulamalar

- **En dar kapsamı kullan:** Değişkeni, kullanılacağı yere **en yakın** tanımla (mümkünse blok
  içinde). Bu, okunabilirliği artırır ve hataları azaltır.
- **Gölgelemeyi bilinçli kullan** (constructor/setter parametreleri için yaygın) ve her zaman
  `this.` ile alana eriş.
- **Statik durumdan kaçın:** Paylaşılan değiştirilebilir statik alanlar, çok thread'li ortamda
  yarış koşullarına ve test zorluğuna yol açar (gerçekten paylaşılması gerekmiyorsa örnek alanı
  kullan).

## Özet

Dört değişken türünü ve kapsamlarını — yerel, blok, örnek (instance), statik — ömürleri ve
varsayılan değerleriyle (Örnek 1); gölgeleme (shadowing) kavramını ve `this` unutma tuzağını
(Örnek 2) öğrendik; "en dar kapsam" ilkesini ve statik durum uyarısını gördük. Sırada, nesneleri
doğru başlatmanın yolu: **constructor'lar**.
