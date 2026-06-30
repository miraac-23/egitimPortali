# Class-File API (JEP 484) — Java 24 Notları

> Bu doküman, Java 24 ile **standartlaşan** Class-File API'sini açıklar.
> Eğitim amaçlıdır; kesin paket/metot adları için kurulu JDK 24'ün resmi
> `java.lang.classfile` belgeleri esas alınmalıdır.

---

## NEDİR

Class-File API, Java `.class` dosyalarını (yani JVM bytecode'unu) **okumak, yazmak ve
dönüştürmek** için JDK içine konulmuş standart, resmi bir kütüphanedir. Ana paket:
`java.lang.classfile` (ve alt paketleri).

Temel fikir, class dosyasını **değişmez (immutable), ağaç benzeri bir model** olarak ele
almaktır:
- Bir `.class` dosyası `ClassModel` olarak ayrıştırılır (parse).
- İçindeki yapılar `MethodModel`, `FieldModel`, `CodeModel`, öznitelikler (attributes),
  sabitler havuzu (constant pool) gibi öğelerle temsil edilir.
- Yeni class dosyaları **builder** desenleriyle üretilir.
- Dönüşüm (transform) için, bir class dosyasını okuyup öğelerini seçici biçimde değiştirip
  yeniden yazan **transform** mekanizmaları vardır.

> Kavramsal bir bakış (sözde-kod, kesin API'yi temsil etmez):
> ```java
> // OKUMA (kavramsal)
> ClassFile cf = ClassFile.of();
> ClassModel model = cf.parse(byteDizisi);
> for (var method : model.methods()) {
>     System.out.println(method.methodName().stringValue());
> }
>
> // YAZMA / URETME (kavramsal)
> byte[] yeniSinif = cf.build(ClassDesc.of("ornek.Merhaba"), clb -> {
>     clb.withMethodBody("selamla", MethodTypeDesc.of(...), ...,
>         cob -> { /* bytecode talimatlari */ });
> });
> ```
> Not: Yukarıdaki imzalar **kavramsaldır**; sürümler arası ayrıntılar değişebilir.

---

## NEDEN GELDİ

JDK'nın kendi içinde, bytecode üretmek ve işlemek için **gömülü (shaded) bir ASM kopyası**
bulunuyordu (örneğin lambda/invokedynamic altyapısı, jlink, çeşitli araçlar). Bunun sorunları:

1. **Senkronizasyon gecikmesi:** Her yeni Java sürümü class dosyası formatının yeni bir
   sürümünü getirir. Gömülü ASM bu formatın gerisinde kalabiliyordu; JDK'nın kendi
   ihtiyaçları için ASM güncellemesini beklemek gerekiyordu.
2. **Bakım yükü:** Üçüncü parti bir kütüphanenin kopyasını JDK içinde taşımak ve
   güncellemek maliyetliydi.
3. **Standart eksikliği:** JDK dışındaki geliştiriciler bytecode işlemek için hep dış
   kütüphanelere (ASM, Javassist, BCEL) muhtaçtı.

JEP 484, JDK'nın **iç ASM bağımlılığını ortadan kaldıran**, class dosyası formatıyla
**her zaman senkron** kalan standart bir API getirir.

---

## NE İŞE YARAR — KULLANIM SENARYOLARI

1. **Bytecode üretimi**
   - Framework'ler (DI konteynerleri, ORM'ler) çalışma zamanında proxy/sınıf üretir.
   - Şablon motorları, derleyiciler, dil araçları kod üretir.

2. **Java agent'ları ve enstrümantasyon (instrumentation)**
   - APM/profil araçları (ölçüm, izleme) sınıfları yükleme anında dönüştürür.
   - `java.lang.instrument` ile birlikte, yüklenen sınıflara bytecode enjeksiyonu.

3. **Framework ve kütüphane geliştirme**
   - Anotasyon işleme sonrası kod üretimi, AOP (aspect-oriented) dönüşümleri.

4. **Statik analiz ve dönüşüm/optimizasyon**
   - Bytecode düzeyinde tarama, yeniden yazma, ölü kod eleme, ölçüm ekleme.

5. **Eğitim/araştırma**
   - JVM bytecode'unun nasıl çalıştığını anlamak için resmi, güvenilir bir model.

---

## NEREDE KOLAYLIK SAĞLAR

- **Dış bağımlılık yok:** ASM/Javassist gibi kütüphaneleri projeye eklemeden, JDK ile
  gelen API ile bytecode işleyebilirsiniz.
- **Format uyumu:** API, çalıştığınız JDK'nın class dosyası formatıyla her zaman uyumludur;
  "kütüphanem yeni formatı tanımıyor" sorunu yaşanmaz.
- **Güvenli model:** Değişmez (immutable) ağaç modeli ve builder yaklaşımı, hatalı/tutarsız
  durumları azaltır.

---

## ESKİ vs YENİ KARŞILAŞTIRMA

| Boyut | Eski (ASM / Javassist / BCEL / yansıma) | Yeni (Class-File API, JEP 484) |
|------|------------------------------------------|--------------------------------|
| Bağımlılık | Dış kütüphane gerekir | JDK'nın içinde, ek bağımlılık yok |
| Format uyumu | Kütüphane sürümüne bağlı; geride kalabilir | JDK ile her zaman senkron |
| Model | Çoğunlukla visitor (ASM) veya yüksek seviye (Javassist) | Değişmez ağaç modeli + builder/transform |
| Yansıma (reflection) ile fark | Yansıma yalnızca **yüklenmiş** sınıfı **okur**, yazamaz | `.class` dosyasını yüklemeden okur, yazar, dönüştürür |
| Bakım | Geliştirici dış kütüphaneyi günceller | JDK ile birlikte güncellenir |
| Öğrenme | ASM düşük seviyeli, dik öğrenme eğrisi | Daha tutarlı, modern API tasarımı |

> **Yansıma (Reflection) neden yetmez?** Yansıma yalnızca JVM'e **zaten yüklenmiş** bir
> sınıfın yapısını okumanıza yarar; yeni bytecode üretemez, mevcut `.class` dosyasını
> dönüştüremez. Class-File API ise diske/belleğe ait `.class` baytlarıyla doğrudan çalışır.

---

## ASM Hâlâ Kullanılır mı?

Evet. ASM olgun, yaygın ve birçok aracın altyapısıdır. Ancak:
- **JDK içi kullanım** için artık Class-File API tercih edilir (gömülü ASM kaldırılır/azaltılır).
- **JDK dışı projeler** için Class-File API, dış bağımlılık istemeyen ve format uyumu
  garantili modern bir alternatif sunar. ASM'den Class-File API'ye geçiş, ekosistemde
  zamanla artması beklenen bir yöneliştir.

---

## Özet

- Class-File API, `.class` dosyalarını okuma/yazma/dönüştürme için **standart JDK API'sidir**.
- JDK'nın **iç ASM bağımlılığını** ortadan kaldırmak ve formatla **senkron** kalmak için geldi.
- Bytecode üretimi, agent/enstrümantasyon, framework geliştirme ve statik analizde işe yarar.
- Yansıma yalnızca okur; Class-File API üretir ve dönüştürür de.
