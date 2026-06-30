# Reflection ve Annotations

Şimdiye kadar yazdığın kod, derleme anında neyle çalışacağını biliyordu. Ama Spring,
Hibernate, Jackson, JUnit gibi araçlar bir "sihir" yapıyormuş gibi görünür: sınıflarını tanır,
nesnelerini oluşturur, alanlarını okur, `@Autowired`/`@Entity`/`@Test` gibi etiketlere göre
davranır. Bu sihrin ardında iki mekanizma yatar: **reflection** (kodu çalışma zamanında
inceleme ve yönlendirme) ve **annotations** (koda makine tarafından okunabilir meta-veri
ekleme). Bu bölümde ikisini de, bu framework'lerin **minyatür** versiyonlarını kendi elinle
yazarak öğreneceksin.

## Reflection

Reflection, çalışma zamanında bir sınıfın yapısını (alanlar, metotlar, constructor'lar,
anotasyonlar) keşfetmeni ve onlarla etkileşmeni sağlar. Başlangıç noktası `Class` nesnesidir:

```java
Class<?> c = nesne.getClass();      // bir nesneden
Class<?> c2 = Urun.class;           // tip adından
c.getDeclaredFields();              // alanlar
c.getDeclaredMethods();             // metotlar
c.getSuperclass();                  // üst sınıf
```

### Gerçek kullanım: genel amaçlı serializer

Reflection'ın en yaygın kullanımı, "herhangi bir tip" için çalışan genel araçlardır. Bir
JSON kütüphanesi nesneni nasıl metne çevirir? Alanlarını reflection ile okuyarak. Örnek 1
(`./Ornek1.java`) tam da bunu yapar: herhangi bir nesneyi alanlarıyla bir `Map`'e çeviren bir
`nesneToMap` ve her sınıf için çalışan genel bir `toString` üreticisi yazar. `setAccessible(true)`
ile `private` alanlara bile erişebildiğine dikkat et — bu güç, framework'lerin nesnelerine
nüfuz etmesini sağlar.

### Dinamik oluşturma ve çağırma

Reflection sadece okumakla kalmaz; nesne **oluşturabilir**, metot **çağırabilir**, alan
**değiştirebilir** — hem de isimler/tipler çalışma zamanında verildiğinde:

```java
Constructor<?> ctor = sinif.getDeclaredConstructor(String.class);
Object nesne = ctor.newInstance("değer");
Method m = sinif.getDeclaredMethod("calistir");
m.invoke(nesne);
```

## Annotations

Annotation, koda eklediğimiz **meta-veridir**: davranışı doğrudan değiştirmez, "bu eleman
şöyledir" bilgisini taşır. Zaten bazılarını kullandın: `@Override`, `@Deprecated`,
`@FunctionalInterface`. Framework'ler ise kendi anotasyonlarını tanımlar ve reflection ile
okuyup davranışa çevirir.

Kendi annotation'ını tanımlarken iki şey kritiktir:

- **`@Retention(RUNTIME)`**: Reflection ile okunabilmesi için çalışma zamanında saklanmalı.
- **`@Target`**: Nereye konabileceği (alan, metot, constructor, sınıf...).

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank { String mesaj() default "boş olamaz"; }
```

## İki gerçek senaryo: kendi framework'ünü yaz

Reflection ve annotations'ın asıl gücünü, onları birleştiren iki minyatür framework'le
göreceğiz.

### 1) Mini IoC container (Spring'in kalbi)

Spring'in en temel işi, nesnelerini senin yerine oluşturup bağımlılıklarını **enjekte**
etmektir. Örnek 2 (`./Ornek2.java`) bunun küçük bir versiyonunu kurar: arayüz → uygulama
eşlemelerini kaydedersin, `coz(SiparisServisi.class)` dediğinde container constructor'a bakar,
`@Inject` işaretli constructor'ın bağımlılıklarını (`Depo`, `MesajGonderici`) **özyinelemeli**
çözer ve nesneyi otomatik kurar:

```java
container.kaydet(Depo.class, BellekDepo.class);
SiparisServisi s = container.coz(SiparisServisi.class); // bağımlılıklar otomatik enjekte
```

Bu, Spring'in `@Autowired` + constructor injection mekanizmasının özüdür. Gerçeği çok daha
fazlasını yapar (kapsam yönetimi, döngü tespiti, proxy'ler) ama fikir aynıdır.

### 2) Mini doğrulama framework'ü (Bean Validation'ın kalbi)

Spring/Hibernate'te bir DTO'yu `@NotBlank`, `@Min`, `@Size` ile işaretleyip `@Valid` dersin;
çatı senin yerine kuralları kontrol eder. Örnek 3 (`./Ornek3.java`) bunun minyatürünü yazar:
`@NotBlank`, `@Email`, `@Min`, `@Length` anotasyonlarını tanımlar; bir `Dogrulayici`, nesnenin
alanlarını reflection ile gezip her alandaki anotasyonlara göre kuralları uygular ve tüm
ihlalleri toplar:

```java
@NotBlank(mesaj = "ad zorunludur") String ad;
@Min(deger = 18) int yas;
List<String> ihlaller = Dogrulayici.dogrula(form);
```

## Maliyet ve dikkat

Reflection güçlüdür ama bedeli vardır: derleyici tip denetimini atlar (hatalar çalışma
zamanına kayar), normal çağrılardan yavaştır ve kodu kırılganlaştırabilir. Bu yüzden günlük
**iş mantığında** nadiren gerekir; asıl yeri, "her tiple çalışması gereken" genel amaçlı
kütüphane ve framework'lerdir. Sen genelde bu framework'leri *kullanırsın*; ama nasıl
çalıştıklarını bilmek, onları doğru ve bilinçli kullanmanı sağlar.

## Özet

Reflection ile kodu çalışma zamanında inceleyip ona müdahale etmeyi; annotations ile koda
meta-veri eklemeyi öğrendik. Daha da önemlisi, ikisini birleştirerek **mini bir IoC container**
ve **mini bir doğrulama framework'ü** yazdık — yani Spring ve Hibernate'in altında yatan
mekanizmayı kendi elinle kurdun. Bu, ilerideki Spring konularını "sihir" değil, anlaşılır bir
mühendislik olarak görmeni sağlayacak. Sırada, programını gerçek bir veritabanına bağlamak:
**JDBC**.
