# Nesne Yönelimli Programlama: Temeller

Şimdiye kadar veriyi (değişkenler, diziler) ve davranışı (metotlar) ayrı ayrı düşündük.
Nesne yönelimli programlama (OOP) bunları bir araya getirir: gerçek dünyadaki bir varlığı,
hem **durumu** (verisi) hem de **davranışı** (metotları) olan tek bir paket — yani bir
**nesne** — olarak modelleriz. Bu bölüm, OOP'nin kalbidir; sonraki iki konu (kalıtım,
soyutlama) bunun üzerine kurulacak.

## Sınıf ve nesne

**Sınıf (class)** bir şablondur: hangi alanların ve metotların olacağını tanımlar.
**Nesne (object)** ise o şablondan `new` ile üretilen somut bir örnektir. Bir "Araba"
sınıfın varsa, garajındaki her araba o sınıftan bir nesnedir:

```java
class Araba {
    String marka;          // alan (durum)
    int hiz;
    void hizlan(int m) {   // metot (davranış)
        hiz += m;
    }
}

Araba a = new Araba();
a.marka = "Toyota";
a.hizlan(40);
```

Her nesnenin kendi alan değerleri vardır; bir arabayı hızlandırmak diğerini etkilemez.
Örnek 1 (`./Ornek1.java`) iki ayrı `Araba` nesnesi oluşturup bağımsız durumlarını gösterir.

## Kapsülleme (encapsulation)

İyi tasarlanmış bir nesne, iç verisini dışarıya açık bırakmaz. Alanları `private` yapar ve
erişimi kontrollü metotlarla sağlar. Buna **kapsülleme** denir ve amacı, nesnenin her zaman
geçerli bir durumda kalmasını garantilemektir.

Veriyi okumak için **getter**, yazmak için (gerekiyorsa) **setter** veya iş kuralı içeren
metotlar yazarız:

```java
private double bakiye;
public double getBakiye() { return bakiye; }
public void paraCek(double tutar) {
    if (tutar > bakiye) { /* engelle */ }
    else bakiye -= tutar;
}
```

Bakiye `private` olduğundan kimse onu doğrudan `hesap.bakiye = -100` yapamaz; tüm
değişiklikler kuralları uygulayan metotlardan geçer. Örnek 2 (`./Ornek2.java`) bir banka
hesabı üzerinde kapsüllemenin geçersiz işlemleri nasıl engellediğini gösterir.

## Constructor, this ve super

**Constructor**, nesne `new` ile oluşturulurken çalışan özel bir metottur; başlangıç
değerlerini kurar. Sınıf adıyla aynı isme sahiptir ve dönüş tipi yoktur:

```java
public BankaHesabi(String sahip, double baslangic) {
    this.sahip = sahip;
    this.bakiye = baslangic;
}
```

`this`, "şu anki nesne" anlamına gelir. En çok, parametre ile alanın aynı isme sahip
olduğu durumda ayrımı netleştirmek için kullanılır (`this.sahip = sahip`). `super` ise üst
sınıfa atıfta bulunur; onu bir sonraki konu olan **kalıtım**da kullanacağız.

## static ve final

İki anahtar kelime nesne tasarımında sık karşına çıkar:

- **`static`**: Üyeyi nesneye değil, **sınıfın kendisine** bağlar. `static` bir alan tüm
  nesneler arasında paylaşılan tek bir kopyadır (örneğin üretilen nesne sayısını tutan bir
  sayaç). `static` bir metot nesne olmadan, sınıf adıyla çağrılır (`Math.max(...)` gibi).
- **`final`**: Değerin bir kez atandıktan sonra değişmeyeceğini söyler. `final` bir değişken
  sabittir, `final` bir metot ezilemez (override edilemez), `final` bir sınıftan
  türetilemez. Sabitleri genelde `public static final` olarak tanımlarız (`KDV_ORANI` gibi).

Örnek 3 (`./Ornek3.java`) paylaşılan bir nesne sayacı (`static`), bir KDV yardımcı metodu
(`static`) ve değişmez bir oran/ad (`final`) ile bu ikisini bir arada gösterir.

## Özet

OOP'nin dört temel taşından ilk ikisini attık: nesneleri sınıflarla modelledik ve
kapsülleme ile durumlarını koruduk. Constructor/`this` ile nesneleri doğru kurmayı,
`static`/`final` ile sınıf düzeyi ve değişmez üyeleri öğrendik. Sırada, var olan kodu
yeniden kullanmamızı sağlayan **kalıtım** ve **çok biçimlilik** var.
