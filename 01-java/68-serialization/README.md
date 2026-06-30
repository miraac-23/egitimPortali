# Serialization (Serileştirme)

**Serialization**, bir Java nesnesini **byte dizisine** çevirme; **deserialization** ise byte
dizisini geri nesneye dönüştürme işlemidir. Bu sayede bir nesneyi diske kaydedebilir, ağ üzerinden
gönderebilir veya bir oturumda saklayabilirsin. Java'nın yerleşik serileştirmesi kolaydır ama
modern uygulamalarda yerini büyük ölçüde JSON gibi taşınabilir formatlara bırakmıştır — bu konuda
hem nasıl çalıştığını hem de neden dikkatli kullanılması gerektiğini ele alıyoruz.

## Serializable: işaretleyici arayüz

Bir nesnenin serileştirilebilmesi için sınıfı **`Serializable`** arayüzünü uygulamalıdır. Bu bir
"işaretleyici" (marker) arayüzdür — metodu yoktur, sadece "bu sınıf serileştirilebilir" der:

```java
class Kullanici implements Serializable { ... }

// Serialize: nesne -> byte[]
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(kullanici);

// Deserialize: byte[] -> nesne
ObjectInputStream ois = new ObjectInputStream(bis);
Kullanici k = (Kullanici) ois.readObject();
```

Örnek 1 (`./Ornek1.java`) bir nesneyi `ByteArrayOutputStream`'e serileştirip geri okur.

> Bir sınıfın tüm (transient olmayan) alanları da serileştirilebilir olmalıdır; değilse
> `NotSerializableException` alınır.

## transient: serileştirmeyi atla

`transient` anahtar kelimesi, bir alanın **serileştirilmemesini** sağlar. Parolalar, önbellekler,
türetilmiş/geçici veriler veya serileştirilemeyen kaynaklar (bağlantılar) için kullanılır:

```java
private transient String parola;   // serileşmez; deserialize sonrası null olur
```

Örnek 1'de `parola` transient'tir; geri yüklendiğinde `null` gelir.

## serialVersionUID

Her serileştirilebilir sınıfın bir **sürüm kimliği** vardır. Veri okunurken, serileşmiş verideki
UID ile sınıfın güncel UID'si karşılaştırılır; **uyuşmazsa `InvalidClassException`** atılır.

```java
private static final long serialVersionUID = 1L;
```

> **Önemli:** Bu alanı **elle tanımla.** Tanımlamazsan derleyici otomatik üretir; ama sınıfta en
> küçük değişiklik (yeni metot bile) bu otomatik değeri değiştirebilir ve **eski serileşmiş
> veriler okunamaz** hale gelir. Elle sabit bir UID, sürümler arası uyumu kontrol etmeni sağlar.

## İç içe nesneler ve koleksiyonlar

Bir nesnenin içindeki nesneler ve koleksiyonlar **otomatik olarak** serileştirilir (graf halinde),
yeter ki hepsi `Serializable` olsun. Örnek 2 (`./Ornek2.java`) iç içe `Adres` ve bir `List` içeren
bir `Siparis`'i serileştirip geri yükler; `serialVersionUID`'i gösterir.

## Neden modern projelerde dikkatli kullanılır?

Java'nın yerleşik serileştirmesinin ciddi sorunları vardır:

- **Güvenlik:** Güvenilmeyen kaynaktan gelen veriyi deserialize etmek, ünlü "deserialization"
  açıklarına yol açabilir (kötü amaçlı nesne grafiği → kod çalıştırma).
- **Kırılganlık:** Sınıf değişiklikleri eski verileri okunamaz yapabilir.
- **Taşınabilirlik yok:** Yalnızca Java okur; başka diller/sistemler anlayamaz.

**Modern alternatifler:** Servisler arası ve depolamada genelde **JSON** (Jackson/Gson) veya
**Protobuf**/Avro tercih edilir. Bunlar taşınabilir, sürüm-dostu ve daha güvenlidir. (Bu portalda
Spring tarafında verileri JSON ile taşıyoruz.) Java serileştirmesini yalnızca tam kontrolün olduğu,
JVM-içi senaryolarda (örn. bazı önbellek/oturum durumları) kullan.

## Özet

Serileştirmenin nesneyi byte'lara çevirip geri kurma olduğunu; `Serializable`, `ObjectOutputStream`/
`ObjectInputStream` ve `transient`'i (Örnek 1); `serialVersionUID`'in sürüm uyumundaki rolünü ve iç
içe nesne/koleksiyon serileştirmesini (Örnek 2) öğrendik; güvenlik/kırılganlık nedeniyle modern
projelerde neden JSON/Protobuf tercih edildiğini gördük. Sırada, kendini çağıran metotlar:
**Recursion (özyineleme)**.
