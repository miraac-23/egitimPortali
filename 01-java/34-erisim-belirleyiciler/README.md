# Erişim Belirleyiciler (Access Modifiers)

Nesne yönelimli tasarımın kalbinde **kapsülleme** (encapsulation) yatar: bir sınıfın iç
ayrıntılarını gizleyip dışarıya yalnızca güvenli, kontrollü bir arayüz sunmak. Bunu sağlayan
araç **erişim belirleyicileridir**. Her alan, metot ve sınıf için "buna nereden erişilebilir?"
sorusunu yanıtlarlar. Doğru erişim belirleyicisini seçmek, kodunu hem güvenli hem de sürdürülebilir
kılar.

## Dört erişim düzeyi

Java'da dört erişim düzeyi vardır; en genişten en dara:

| Belirleyici | Aynı sınıf | Aynı paket | Alt sınıf (farklı paket) | Her yer |
|-------------|:----------:|:----------:|:------------------------:|:-------:|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| _(default)_ paket-özel | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

- **`public`**: Her yerden erişilir. Sınıfın dış dünyaya sunduğu API (genelde metotlar).
- **`protected`**: Aynı paket + (farklı paketteki) alt sınıflar. Kalıtım için açılan üyeler.
- **_default_ (belirleyici yazılmazsa)**: Yalnızca aynı paket. "Paket-özel" (package-private).
- **`private`**: Yalnızca tanımlandığı sınıf. İç ayrıntılar, durum (state), yardımcı metotlar.

> **Not:** Bu portalın örnekleri tek dosyada (default pakette) çalışır; bu yüzden default ve
> protected, farklı paket davranışını birebir gösteremez. Tablodaki "farklı paket" davranışını
> akılda tut; örnekler aynı-paket ve kalıtım senaryolarını gösterir.

## private: kapsüllemenin temeli

Bir nesnenin **durumunu** (alanlarını) `private` yapıp dışarıya yalnızca metotlarla (getter/setter
veya iş metotları) açmak, nesnenin her zaman geçerli kalmasını sağlar:

```java
class Hesap {
    private double bakiye;                 // dışarıdan doğrudan değiştirilemez
    public double getBakiye() { return bakiye; }
    public void paraYatir(double t) { if (t > 0) bakiye += t; } // kural burada
}
```

Böylece kimse `hesap.bakiye = -100` yapamaz; tüm değişiklikler kuralları uygulayan metotlardan
geçer. Örnek 1 (`./Ornek1.java`) dört düzeyi bir hesap sınıfında gösterir: `public` her yerden,
`default`/`protected` aynı paketten erişilir, `private` yalnızca sınıf içinden.

## protected ve kalıtım

`protected`, "dış dünyaya kapalı ama alt sınıflara açık" demektir. Bir üst sınıfın, alt
sınıfların kullanmasını istediği ama dışarıya açmak istemediği üyeler için idealdir:

```java
class Calisan { protected double tabanMaas; private String sicilNo; }
class Yonetici extends Calisan {
    double toplam() { return tabanMaas + prim; } // protected'a erişir
    // sicilNo'ya ERİŞEMEZ (private)
}
```

Örnek 2 (`./Ornek2.java`) bir `Yonetici`'nin üst sınıftaki `protected` alanlara erişip `private`
alana erişemediğini gösterir.

## Sınıf düzeyinde erişim

Top-level (en dış) sınıflar yalnızca `public` veya _default_ olabilir (`private`/`protected`
olamaz). Bir dosyada en fazla **bir** `public` top-level sınıf olabilir ve adı dosya adıyla
eşleşmelidir. İç (nested) sınıflar ise dört düzeyin hepsini kullanabilir.

## İyi uygulama: en dar erişimi seç

Altın kural: **gereken en dar erişim düzeyini kullan.** 
- Alanları varsayılan olarak `private` yap; gerekiyorsa metotlarla aç.
- Yalnızca dış dünyanın kullanması gereken metotları `public` yap.
- İç yardımcı metotları `private` tut.
- `protected`'i bilinçli kullan (kalıtım sözleşmesinin parçası olur).

Bu yaklaşım "bilgi gizleme" (information hiding) ilkesidir: ne kadar az şey açığa çıkarsa, sınıfı
o kadar rahat değiştirebilirsin (dış kod etkilenmeden).

## Özet

Dört erişim düzeyini (`public`/`protected`/_default_/`private`) ve hangisinin nereden erişilebilir
olduğunu; `private` ile kapsüllemeyi (Örnek 1) ve `protected` ile kalıtım arasındaki ilişkiyi
(Örnek 2) öğrendik. "En dar erişimi seç" ilkesi, sağlam ve sürdürülebilir tasarımın temelidir.
Sırada, sınıfları düzenlemenin ve isim çakışmalarını önlemenin yolu: **paketler**.
