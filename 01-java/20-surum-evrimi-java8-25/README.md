# Java Sürüm Evrimi (8 → 25)

Java, 2014'teki 8 sürümünden bu yana her **6 ayda bir** yeni sürüm, her **2 yılda bir** uzun
destekli (LTS) sürüm çıkarıyor. LTS'ler: **8, 11, 17, 21, 25**. Bu bölümün amacı sadece "şu özellik
hangi sürümde geldi" demek değil; **her sürümde neyin önemli olduğunu** ve **bir sürümden diğerine
geçerken kodun nasıl değiştiğini** karşılaştırmalı, çalışan örneklerle göstermek. Örnekler bu
ortamdaki JDK 21 ile uyumludur; her örnek "öncesi vs sonrası" stiliyle yazılmıştır (iki stil de
21'de derlenir, böylece farkı doğrudan görürsün).

## Java 8 (2014, LTS) — Dili yeniden tanımlayan devrim

En önemli sürüm. Java'yı modern, fonksiyonel-etkili bir dile dönüştürdü:

| Özellik | Ne işe yarar | Neyi değiştirdi |
|---------|--------------|-----------------|
| **Lambda** | Davranışı değer gibi taşıma | Anonim iç sınıf gürültüsü bitti |
| **Stream API** | Bildirimsel veri işleme | `for` döngüleri yerini `filter/map/collect`'e bıraktı |
| **Optional** | `null` güvenliği | "milyar dolarlık hata"ya tip düzeyinde önlem |
| **java.time** | Tarih/saat | Hatalı, değişebilen `Date/Calendar`'ın halefi |
| **default metotlar** | Interface'e gövdeli metot | API'leri bozmadan genişletme |

Örnek 1 (`./Ornek1.java`) dört işi **Java 8 öncesi ve sonrası** yan yana yapar: anonim
Comparator→lambda, döngü→stream, elle null kontrolü→Optional, `Date`→`java.time`. Aynı sonuç, çok
daha temiz kod.

> **Geçiş notu:** 8'e geçişin pratikte dezavantajı yoktu; bugün hâlâ 8'de kalan projeler güvenlik
> ve performans güncellemelerini kaçırır.

## Java 9–11 — Modülerlik, `var` ve araçlar

- **Java 9 (2017):** Modül sistemi (**JPMS/Jigsaw**), `List/Map.of` fabrikaları, `jshell`, Stream
  eklentileri (`takeWhile`, `dropWhile`, 3-argümanlı `iterate`).
- **Java 10 (2018):** **`var`** — yerel değişken tip çıkarımı.
- **Java 11 (2018, LTS):** Yeni `String` metotları (`strip`, `isBlank`, `repeat`, `lines`),
  standart **HttpClient**, **tek dosya çalıştırma** (`java Dosya.java` — bu portalın temeli!).

Örnek 2 (`./Ornek2.java`) koleksiyon fabrikalarını (3 adım vs `List.of`), `var`'ı, Java 11 String
metotlarını ve Java 9 Stream eklentilerini öncesi/sonrası karşılaştırır.

> **Geçiş notu (8→11):** En büyük tuzak, 11'de **kaldırılan Java EE/CORBA modülleridir** (JAXB,
> JAX-WS). Bunları kullanan projeler bağımlılıkları ayrıca eklemek zorunda kaldı.

## Java 14–17 — Dil ergonomisi olgunlaşıyor

Bu aralık, kodu belirgin biçimde kısaltan özellikler getirdi:

| Özellik (sürüm) | Öncesi | Sonrası |
|-----------------|--------|---------|
| **switch expression** (14) | `break`'li statement, mutable değişken | Değer döndüren, eksiksizlik denetimli ifade |
| **text block** (15) | `"\n"` + `+` cehennemi | `"""..."""` çok satırlı metin |
| **records** (16) | ~30 satır boilerplate veri sınıfı | Tek satır (`record`) |
| **pattern matching instanceof** (16) | `instanceof` + ayrı cast | Tek adımda kontrol + bağlama |
| **sealed sınıflar** (17, LTS) | Kontrolsüz kalıtım | Kapalı, denetlenebilir hiyerarşi |

Örnek 3 (`./Ornek3.java`) bir `record`'u elle yazılmış sınıfla, switch expression'ı statement'la,
pattern matching'i `instanceof`+cast ile ve `sealed`+switch'i karşılaştırır.

## Java 18–21 — Eşzamanlılıkta sıçrama

- **Java 18 (2022):** UTF-8 varsayılan karakter kümesi, basit web sunucusu (`jwebserver`).
- **Java 21 (2023, LTS):** Önceki sürümlerde önizleme olan büyük özellikler **standartlaştı**:
  - **Virtual threads** — on binlerce eşzamanlı görevi ucuza
  - **Record patterns** + **switch pattern matching** — veriyi switch'te parçalama
  - **Sequenced Collections** — `getFirst/getLast/reversed`
  - **Generational ZGC** — düşük gecikmeli çöp toplama

Örnek 4 (`./Ornek4.java`) record patterns'ı iç içe `instanceof` ile, sequenced collections'ı
`get(0)/get(size-1)` ile ve sanal thread'leri platform thread havuzuyla karşılaştırır (G/Ç
ağırlıklı yükte sanal thread'lerin farkını ölçer).

> **Geçiş notu (17→21):** Genelde **düşük riskli, yüksek kazançlı** bir geçiştir. Sanal thread
> kullanacaksan `synchronized` bloklarındaki "pinning"e dikkat (kritik bölümlerde `ReentrantLock`).

## Java 22–25 — En yeni (JDK 25 gerektirir, burada çalışmaz)

Yalnızca tanıtım amaçlı (bu ortamdaki JDK 21'de derlenmez):

- **Java 22 (2024):** **Foreign Function & Memory API** (JNI halefi) standartlaştı; isimsiz
  değişkenler/desenler (`_`).
- **Java 24 (2025):** **Stream Gatherers** (`Stream.gather(...)`) ile özel ara işlemler.
- **Java 25 (2025, LTS):** Sadeleştirilmiş giriş noktası (`void main()`), **modül import
  bildirimleri** (`import module java.base;`), scoped values, esnek constructor gövdeleri.

```java
// Java 25: sade kaynak dosya + instance main (örnek; JDK 25 gerektirir)
void main() {
    IO.println("Merhaba"); // "public static void main(String[])" şart değil
}
```

> Not: 22–25'teki bazı özelliklerin "önizleme → standart" yolculuğu sürümden sürüme değişebilir;
> kesin durum için resmî sürüm notlarına bak.

## Sürüm-sürüm derin anlatım (ayrı konular)

Bu konu genel resmi ve dönemsel karşılaştırmaları verir. Her sürümün **ayrı, derin** anlatımı ve
"öncesi vs sonrası" örnekleri için şu konulara bak:

- **Java 8 → 16:** `54-java8` … `62-java16` (her biri çalışan karşılaştırmalı örneklerle)
- **Java 17 → 21:** `97-java17` … `101-java21` (LTS'ler dahil; örnekler bu ortamda çalışır)
- **Java 22 → 25:** `102-java22` … `105-java25` (en yeni; JDK 21'de çalışmadığından kavramsal anlatım)

Yani her sürüm için "o sürümde ne önemli + bir önceki sürümden ne değişti" sorusu, ilgili konuda
örneklerle yanıtlanır.

## Sürüm seçerken pratik kural

- **Üretim için her zaman bir LTS** (17, 21 veya 25). Ara sürümler kısa desteklidir.
- Yeni projede bugün **21 (veya destekleniyorsa 25)** mantıklı varsayılandır.
- Çoğu zaman seçimini **kütüphane/çerçeve uyumu** belirler (kullandığın Spring/Hibernate hangi
  JDK'yı destekliyor?).

## Özet

Java'nın 8'deki lambda/Stream devriminden 11'in modülerlik/araçlarına, 17'nin record/sealed
ergonomisine ve 21'in sanal thread'lerine uzanan evrimini **öncesi/sonrası karşılaştırmalı**
örneklerle gördük; 22–25'in en yeni eklerine değindik. Her geçişin kazanımları genelde maliyetinden
büyüktür — ama riskler de vardır. Sırada tam olarak bunu konuşuyoruz: **sürüm geçişi: riskler ve
kazanımlar** (özellikle 17/21 → en yeni).
