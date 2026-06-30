# @Deprecated ve Anotasyonlar

**Anotasyonlar (annotations)**, koda **meta-veri** ekler: `@Override`, `@Deprecated`, `@Entity`,
`@Test`... Kodun davranışını doğrudan değiştirmezler ama derleyici, araçlar ve framework'ler
bunları okuyup ona göre davranır. Reflection bölümünde (topic 16) kendi anotasyonunu tanımlamayı
gördük; burada Java'nın **yerleşik** anotasyonlarına ve özellikle `@Deprecated`'a odaklanıyoruz.

## @Override

Bir metodun üst tipteki bir metodu **ezdiğini** (override) belirtir. Zorunlu değildir ama
**kullanmalısın**: metot imzasını yanlış yazarsan (override yerine yeni metot oluşturursan)
derleyici **hata** verir — sessiz bir hatayı yakalar:

```java
@Override public String selam() { ... }   // imza üst tiple uyuşmazsa derleme hatası
```

## @Deprecated

"Bu öğeyi artık **kullanma**, daha iyisi var" der. Java 9'dan beri iki yararlı alan taşır:

```java
@Deprecated(since = "2.0", forRemoval = true)
public int hesaplaEski(int x) { ... }
```

- **`since`**: Hangi sürümden beri kullanımdan kaldırıldı.
- **`forRemoval = true`**: Gelecekte **silinecek** (sadece "önerilmiyor" değil; geçişi planla).

Deprecated bir öğeyi çağıran kod **derlenir** ama derleyici **uyarı** verir; IDE'ler üstü çizili
gösterir. Kütüphane evriminde, eski API'leri kırmadan kullanıcıları yeniye yönlendirmenin standart
yoludur. Örnek 1 (`./Ornek1.java`) `@Override`, `@Deprecated` ve `@SuppressWarnings`'i gösterir.

## @SuppressWarnings

Belirli derleyici uyarılarını **bilinçli olarak** bastırır: `@SuppressWarnings("deprecation")`,
`@SuppressWarnings("unchecked")`. Dikkatli kullan — uyarı çoğu zaman gerçek bir sorunu işaret eder;
yalnızca bilerek ve kapsamı dar tutarak bastır.

## @FunctionalInterface ve @SafeVarargs

- **`@FunctionalInterface`**: Bir arayüzün **tek soyut metotlu** kalmasını garanti eder (lambda
  hedefi). İkinci bir soyut metot eklersen derleme hatası verir — niyeti belgeler ve korur.
- **`@SafeVarargs`**: Jenerik varargs metotlarındaki "unchecked / heap pollution" uyarısını, kodun
  güvenli olduğunu bildiğinde bastırır.

Örnek 2 (`./Ornek2.java`) bunları ve aşağıdaki meta-anotasyonları gösterir.

## Meta-anotasyonlar (anotasyon tanımlarken)

Kendi anotasyonunu yazarken kullanılan anotasyonlar:

| Meta-anotasyon | Ne belirler |
|----------------|-------------|
| `@Retention` | Anotasyon ne kadar yaşar: `SOURCE` / `CLASS` / `RUNTIME` (reflection için RUNTIME) |
| `@Target` | Nereye uygulanabilir: `TYPE`, `METHOD`, `FIELD`, `PARAMETER`... |
| `@Inherited` | Alt sınıflara miras geçer mi |
| `@Documented` | Javadoc'a dahil edilsin mi |
| `@Repeatable` | Aynı yere birden çok kez uygulanabilir mi |

Kendi anotasyonunu tanımlama ve reflection ile okuma (`isAnnotationPresent`, `getAnnotation`)
ayrıntısı **Reflection ve Annotations** konusundadır (topic 16).

## Anotasyonların gücü: framework'ler

Anotasyonlar modern Java'nın bel kemiğidir. Spring (`@Component`, `@Autowired`, `@GetMapping`),
JPA (`@Entity`, `@Column`), JUnit (`@Test`) — hepsi anotasyon okuyup davranış üretir. Yani
"yapılandırmayı koda gömme" felsefesinin aracıdır (bu portalın backend'i baştan sona anotasyonlarla
yapılandırılmıştır).

## Özet

Yerleşik anotasyonları öğrendik: imza güvenliği sağlayan `@Override`, kullanımdan kaldırmayı
`since`/`forRemoval` ile bildiren `@Deprecated`, uyarı bastıran `@SuppressWarnings` (Örnek 1);
lambda hedefini koruyan `@FunctionalInterface`, `@SafeVarargs` ve meta-anotasyonlar (Örnek 2);
anotasyonların framework'lerdeki merkezi rolüne değindik. Sırada, JVM'in performansını şekillendiren
iki mekanizma: **Garbage Collection ve JIT**.
