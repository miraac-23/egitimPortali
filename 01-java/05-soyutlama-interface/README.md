# Soyutlama: Abstract Sınıflar ve Interface'ler

Soyutlama, OOP'nin dördüncü temel taşıdır. Fikir basit ama güçlüdür: bir nesneyle çalışırken
onun **ne yaptığına** odaklanır, **nasıl yaptığını** gizleriz. Bir televizyonun uzaktan
kumandasını kullanırken içindeki elektroniği bilmene gerek yoktur; sana sadelleştirilmiş bir
arayüz sunulur. Java'da bunu iki araçla yaparız: **abstract sınıflar** ve **interface'ler**.

## Abstract sınıflar

Bazen bir üst sınıf, tek başına anlamlı bir nesne değildir. "Çalışan" derken aslında bir
mühendisi veya satışçıyı kastederiz; soyut bir "çalışan" nesnesi oluşturmak mantıksızdır.
Böyle durumlarda sınıfı `abstract` ilan ederiz: ondan doğrudan nesne üretilemez, yalnızca
alt sınıfları için bir şablon görevi görür.

Abstract sınıf, içinde hem **somut** (gövdeli) hem de **soyut** (gövdesiz) metotlar
barındırabilir. Soyut metot, "bu iş yapılacak ama nasıl yapılacağına alt sınıf karar
verecek" demektir:

```java
abstract class Calisan {
    public abstract double maasHesapla();   // soyut: alt sınıf doldurur
    public void iseGel() { ... }            // somut: ortak davranış
}
```

Alt sınıflar soyut metotları doldurmak **zorundadır**. Örnek 1 (`./Ornek1.java`) mühendis ve
satışçının maaşı farklı hesaplamasını, ortak "işe gel" davranışını üst sınıfta paylaşmasını
gösterir.

## Interface'ler

Interface, bir **sözleşmedir**: "bu sözleşmeyi imzalayan her sınıf şu metotları sağlamak
zorundadır." Bir yetenek/rol tanımlar. Klasik olarak interface'lerdeki metotlar gövdesizdir:

```java
interface Yuzebilir { void yuz(); }
```

Bir sınıf bir interface'i `implements` ile uygular. En önemli farklardan biri: bir sınıf
yalnızca **tek** bir sınıftan türeyebilir ama **birden çok** interface'i aynı anda
uygulayabilir. Böylece "hem yüzen hem yürüyen" gibi çoklu yetenekler modellenebilir:

```java
class Amfibi implements Yuzebilir, Surulebilir { ... }
```

### default ve static metotlar

Java 8'den beri interface'ler gövdeli **default** metotlar da içerebilir. Bu, uygulayan
sınıfları bozmadan arayüze hazır bir davranış eklemeyi sağlar; sınıf isterse ezer, istemezse
hazır sürümü kullanır:

```java
interface Surulebilir {
    void yuru();
    default void tanit() { System.out.println("Karada hareket eder."); }
}
```

Örnek 2 (`./Ornek2.java`) çoklu interface uygulamasını ve default metodu gösterir.

## Abstract sınıf mı, interface mi?

İkisi de soyutlama sağlar ama farklı işler için uygundur:

| | Abstract sınıf | Interface |
|---|---|---|
| Amaç | "is-a" (bir tür ... olma) | "can-do" (bir yeteneğe sahip olma) |
| Çoklu | Tek sınıftan miras | Birden çok uygulanabilir |
| Durum (alan) | Tutabilir | (Genelde) tutmaz, sabit hariç |
| Ortak kod | Bolca paylaşır | default metotlarla sınırlı |

Pratikte ikisi birlikte kullanılır: interface sözleşmeyi tanımlar, abstract sınıf ortak
iskeleti sağlar, somut sınıflar boşlukları doldurur. Örnek 3 (`./Ornek3.java`) bir bildirim
sisteminde bunu uygular: `Bildirimci` interface'i sözleşmeyi, `TemelBildirimci` abstract
sınıfı loglama/biçimlendirme iskeletini verir; e-posta ve SMS kanalları yalnızca kendi
gönderim ayrıntısını yazar.

## Özet

Soyutlamayla "ne"yi "nasıl"dan ayırdık. Abstract sınıflar ortak iskeleti paylaşıp eksik
parçaları alt sınıfa bırakır; interface'ler ise rolleri/yetenekleri tanımlayan
sözleşmelerdir ve çoklu uygulanabilir. Bu yaklaşım, kodu gevşek bağlı ve genişletilebilir
kılar — ki bu, ileride Spring'de göreceğin "bağımlılığı arayüze ver" felsefesinin de
temelidir. Sırada metin işlemenin günlük aracı: **String ve StringBuilder**.
