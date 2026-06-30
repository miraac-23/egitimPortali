# Singleton Sınıf

Bazı nesnelerden uygulamada **tek bir tane** olmalıdır: bir yapılandırma yöneticisi, bir log
sistemi, bir veritabanı bağlantı havuzu. Bunlardan birden çok oluşturmak hem kaynak israfı hem de
tutarsızlık (farklı ayar kopyaları) demektir. **Singleton deseni**, bir sınıfın tek örneğini
garanti eder ve ona global, kontrollü bir erişim noktası sunar. Tasarım desenleri bölümünde kısaca
gördük; burada tüm varyantlarını ve tuzaklarını derinlemesine ele alıyoruz.

## Singleton'ın iki kuralı

1. **Dışarıdan `new` engellenir:** Constructor `private` yapılır.
2. **Tek erişim noktası:** Sınıf, kendi tek örneğini `static` bir metot/alanla sunar.

```java
class Ayarlar {
    private static final Ayarlar TEK = new Ayarlar();
    private Ayarlar() {}                       // new engellendi
    public static Ayarlar getInstance() { return TEK; }
}
```

## Eager vs Lazy

- **Eager (erken):** Örnek, sınıf yüklenirken hemen oluşturulur. Basit ve thread-safe; ama
  hiç kullanılmasa bile oluşturulur.
- **Lazy (tembel):** Örnek ilk `getInstance()` çağrısında oluşturulur. Kaynak tasarrufu sağlar
  ama çok thread'li ortamda dikkat ister.

Örnek 1 (`./Ornek1.java`) eager bir `Ayarlar` ve basit (tek-thread) lazy bir `Logger` gösterir.

## Thread güvenliği: lazy singleton'ın tuzağı

Basit lazy singleton **çok thread'de risklidir**: iki thread aynı anda `if (ornek == null)` koşulunu
geçip **iki ayrı örnek** yaratabilir. Çözümler:

### Double-checked locking

`volatile` bir alan + iki kez null kontrolü (biri kilitsiz hızlı, biri kilit içinde):

```java
private static volatile Baglanti ornek;
public static Baglanti getInstance() {
    if (ornek == null) {                  // hızlı yol (kilitsiz)
        synchronized (Baglanti.class) {
            if (ornek == null) ornek = new Baglanti();  // güvenli yaratım
        }
    }
    return ornek;
}
```

### Holder idiom (en zarif)

İç `static` bir sınıf, JVM'in sınıf-yükleme garantisini kullanır: örnek, `Holder` ilk erişildiğinde
**thread-safe ve kilitsiz** oluşur:

```java
private static class Holder { static final Onbellek ORNEK = new Onbellek(); }
public static Onbellek getInstance() { return Holder.ORNEK; }
```

Örnek 2 (`./Ornek2.java`) 20 thread'le tek örnek garantisini test eder ve her iki yöntemi gösterir.

## Enum singleton (önerilen)

Joshua Bloch'un *Effective Java*'da önerdiği en güvenli yol: tek elemanlı bir `enum`.

```java
public enum VeriTabani {
    INSTANCE;
    public void sorgu(String sql) { ... }
}
// kullanım: VeriTabani.INSTANCE.sorgu(...)
```

Avantajları (Örnek 3, `./Ornek3.java`):

- **Thread-safe + lazy**, ekstra kod yok (JVM enum'ları güvenle yükler).
- **Serileştirmeye karşı güvenli:** Normal singleton'lar deserialize edilince ikinci bir örnek
  oluşabilir; enum'da bu olmaz.
- **Reflection'a karşı güvenli:** Enum constructor'ı reflection ile çağrılamaz, ikinci örnek
  yaratılamaz.

Tek kısıtı: bir sınıfı uzatamaz (enum zaten `Enum`'u uzatır). Çoğu durumda en iyi seçimdir.

## Singleton'a eleştiriler ve modern bakış

Singleton güçlü ama dikkatli kullanılmalı:

- **Global durum** yaratır; gizli bağımlılıklar ve test zorluğu doğurabilir.
- **Test edilebilirlik:** Singleton'a sıkıca bağlı kod, testte sahteyle değiştirilemez.

Modern çözüm: **dependency injection**. Spring'de bir bean'i `@Component`/`@Bean` yaparsın; Spring
onu zaten **singleton kapsamında** yönetir ve sana **enjekte eder** — sen elle singleton yazmazsın.
Yani "tek örnek" ihtiyacını framework, gizli global durum yaratmadan, test edilebilir biçimde çözer.

## Özet

Singleton'ın iki kuralını; eager/lazy ayrımını (Örnek 1); lazy'nin thread tuzağını ve çözümlerini
— double-checked locking, holder idiom (Örnek 2) — ve en güvenli yol olan **enum singleton**'ı
(Örnek 3) öğrendik; singleton'ın global-durum eleştirisini ve modern alternatifi (dependency
injection) ele aldık. Sırada, metot çağrılarının hangi anda çözüldüğü: **statik ve dinamik
bağlama**.
