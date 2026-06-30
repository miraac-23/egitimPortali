# Marker Interface (İşaretleyici Arayüz)

**Marker (işaretleyici) interface**, içinde **hiç metot olmayan** bir arayüzdür. Tuhaf görünür —
metot yoksa ne işe yarar? Amacı bir sınıfa **etiket** koymaktır: "bu tip şu yeteneğe/izne sahiptir".
Derleyici veya çalışma zamanı bu etikete bakarak davranış değiştirir. Java'nın en ünlü
örneklerinden biri `Serializable`'dır (topic 68): metodu yoktur ama "bu sınıf serileştirilebilir"
der.

## Nasıl çalışır?

```java
interface Arsivlenebilir {}    // metot yok — sadece etiket

record Fatura(String no) implements Arsivlenebilir {}

void arsivle(Object o) {
    if (o instanceof Arsivlenebilir) { /* arşivle */ }   // etiket kontrolü
    else { /* reddet */ }
}
```

Kod, `instanceof` ile "bu nesne işaretli mi?" diye sorar ve ona göre davranır. Örnek 1
(`./Ornek1.java`) bir `Arsivlenebilir` etiketi tanımlar; arşiv servisi yalnızca işaretli nesneleri
kabul eder.

JDK'daki yerleşik marker interface'ler:

- **`Serializable`**: `ObjectOutputStream` bunu arar; yoksa `NotSerializableException`.
- **`Cloneable`**: `Object.clone()` bunu arar; yoksa `CloneNotSupportedException`.
- **`RandomAccess`**: "Bu liste hızlı indeksli erişim sunar" (ArrayList evet, LinkedList hayır);
  algoritmalar buna göre strateji seçer.

## Marker interface vs marker annotation

Modern Java'da aynı "etiketleme" işi genelde **anotasyonla** yapılır (`@Entity`, `@Component`,
`@Test`). İkisini karşılaştıralım:

| | Marker **interface** | Marker **annotation** |
|---|----------------------|------------------------|
| Tip güvenliği | **Var** (derleyici zorlar) | Yok (çalışma zamanı) |
| Kontrol | `instanceof` (hızlı) | reflection (`isAnnotationPresent`) |
| Esneklik | Yalnızca tipe | Tip/metot/alan/parametre |
| Meta-veri | Taşıyamaz | **Taşıyabilir** (alanlar) |
| Kalıtım | Alt tiplere geçer; tek-kalıtıma dahil | Bağımsız |

Örnek 2 (`./Ornek2.java`) aynı etiketi bir anotasyonla yapar ve reflection ile kontrol eder.

**Ne zaman hangisi?**

- **Marker interface:** Etiketli tiplerin yalnızca belirli metotlara parametre olmasını **derleme
  zamanında** zorlamak istiyorsan (tip güvenliği). Örn. `void kaydet(Serializable s)`.
- **Marker annotation:** Esneklik, meta-veri (`@Column(name="...")` gibi) veya metot/alan
  işaretleme gerekiyorsa. Modern framework'ler (Spring, JPA, JUnit) bu yolu kullanır.

> Joshua Bloch (*Effective Java*): "Eğer işaretlenen şey yalnızca bir tipse ve onu bir metoda
> parametre olarak kısıtlamak istiyorsan marker interface; aksi halde annotation tercih et."

## Özet

Marker interface'in metotsuz bir **etiket** olduğunu; `instanceof` ile kontrol edilip davranış
değiştirdiğini ve `Serializable`/`Cloneable`/`RandomAccess` gibi yerleşik örneklerini (Örnek 1);
marker annotation alternatifini, ikisinin tip-güvenliği/esneklik karşılaştırmasını ve ne zaman
hangisinin seçileceğini (Örnek 2) öğrendik. Sırada, en çok kullanılan yerleşik anotasyon ve
arkadaşları: **@Deprecated ve anotasyonlar**.
