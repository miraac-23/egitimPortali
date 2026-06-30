# Scoped Values (JEP 429) — Java 20 (1. Incubator)

> Bu doküman Java 20 ile incubator olarak gelen **Scoped Values** özelliğini detaylı anlatır. `ThreadLocal`'a modern, değişmez ve virtual thread dostu bir alternatiftir.

---

## NEDİR?

`ScopedValue`, bir değeri **dinamik bir kapsam (scope) boyunca**, açıkça parametre olarak elden ele geçirmeden, çağrı zincirindeki tüm alt metotlara güvenli biçimde paylaştırmanın yoludur.

- Değer **değişmezdir (immutable):** Bir kez bağlanır, kapsam boyunca aynı kalır.
- Değer yalnızca belirli bir kod bloğu (kapsam) içinde görünür; blok bitince **otomatik kalkar**.
- Virtual thread'lerle **çok ucuza** çalışır.

---

## NEDEN GELDİ? — `ThreadLocal`'ın Dertleri

`ThreadLocal` 20+ yıldır "çağrı zinciri boyunca veri taşıma" ihtiyacını karşılıyordu (örn. oturum açan kullanıcı, istek kimliği, transaction bağlamı). Ancak ciddi sorunları var:

1. **Değiştirilebilir (mutable):** Çağrı zincirindeki herhangi bir kod `set()` ile değeri değiştirebilir. Kim, nerede değiştirdi izlenemez.

2. **Bellek sızıntısı:** Değer `remove()` ile temizlenmezse, özellikle **thread havuzlarında** thread tekrar kullanıldığında eski değer kalır. Bu hem yanlış veri hem bellek sızıntısı demektir. `try/finally` içinde `remove()` çağırmak unutulması çok kolay bir disiplindir.

3. **Kalıtım maliyeti:** Alt thread'lere değer aktarmak için `InheritableThreadLocal` gerekir; bu da her yeni thread'de değerlerin kopyalanmasına yol açar — pahalıdır.

4. **Virtual thread'lerle ölçeklenmez:** Java artık **milyonlarca** virtual thread destekliyor. Her birinde `ThreadLocal` haritası tutmak bellek açısından felakettir.

---

## NASIL ÇALIŞIR?

```java
// 1) Bir ScopedValue tanımla (genellikle static final)
static final ScopedValue<Kullanici> AKTIF_KULLANICI = ScopedValue.newInstance();

// 2) Bir değer bağla ve sadece o kapsamda çalıştır
ScopedValue.where(AKTIF_KULLANICI, kullanici).run(() -> {
    // bu blok ve çağırdığı TÜM alt metotlar AKTIF_KULLANICI.get() ile değere ulaşır
    isMantigi();
});
// blok bitti -> değer otomatik olarak ortadan kalkar (sızıntı yok)

// 3) Derinlerdeki bir metotta okuma
void detay() {
    Kullanici k = AKTIF_KULLANICI.get(); // parametre geçmeden eriştik
}
```

Anahtar fark: `ScopedValue` değeri **kapsam ağacına** bağlar. Kapsamdan çıkıldığında değer kesin olarak kaybolur; `remove()` çağırmaya gerek yoktur, unutma riski yoktur.

---

## ESKİ vs YENİ

```java
// ===== ESKİ: ThreadLocal =====
static final ThreadLocal<Kullanici> AKTIF = new ThreadLocal<>();

void istekIsle(Kullanici k) {
    AKTIF.set(k);
    try {
        isYap();          // AKTIF.get() ile kullanıcıya erişir
    } finally {
        AKTIF.remove();   // UNUTULURSA sızıntı / yanlış veri!
    }
}

// ===== YENİ: ScopedValue (Java 20 incubator) =====
static final ScopedValue<Kullanici> AKTIF = ScopedValue.newInstance();

void istekIsle(Kullanici k) {
    ScopedValue.where(AKTIF, k).run(() -> isYap());
    // finally/remove yok; kapsam bitince değer otomatik kalkar
}
```

---

## GERÇEK HAYAT ÖRNEĞİ

**Senaryo:** Bir web mikroservisi her isteği işlerken, gelen istekteki "kullanıcı kimliği" ve "izleme (trace) kimliği" bilgisinin servis katmanlarının (controller -> service -> repository -> logger) hepsine ulaşması gerekir.

- **ThreadLocal yaklaşımı:** Her istekte `set`, her bitişte `remove`. Thread havuzu + virtual thread karışımında sızıntı riski yüksek.
- **ScopedValue yaklaşımı:** İstek başında `ScopedValue.where(...).run(...)` ile kapsam açılır. Tüm katmanlar `.get()` ile erişir. İstek bitince bağlam kesinlikle temizlenir. Milyonlarca virtual thread'de bile ucuzdur.

Bu, özellikle **virtual thread tabanlı yüksek eşzamanlılıklı sunucularda** bağlam taşımanın doğru yoludur.

---

## VIRTUAL THREADS İLE İLİŞKİSİ (ÖNEMLİ)

Scoped Values, Project Loom'un (virtual threads) tamamlayıcısıdır. Virtual thread'ler milyonlarca olabildiğinden, her birinde değiştirilebilir `ThreadLocal` haritası tutmak mantıksızdır. ScopedValue:
- Değişmezdir (paylaşımı güvenli),
- Kapsam ağacı boyunca ucuza miras alınır (Structured Concurrency ile çok iyi çalışır),
- Bellekte iz bırakmaz.

Bu yüzden Java 21+ ekosisteminde "virtual thread + structured concurrency + scoped values" üçlüsü birlikte anılır.

---

## AVANTAJ / DEZAVANTAJ / RİSK

### Avantajlar
- Değişmez ve güvenli; veri "kim değiştirdi" sorunu yok.
- Sızıntı yok; `remove()` disiplinine gerek yok.
- Virtual thread'lerle ölçeklenir.

### Dezavantajlar / Sınırlamalar
- Değer kapsam içinde **değiştirilemez** (bu çoğu zaman avantajdır ama bazı eski kullanımları doğrudan karşılamaz; yeniden bağlama `where(...)` ile yapılır).
- Java 20'de **incubator**'dır: `jdk.incubator.concurrent` modülünden gelir, API değişebilir.

### EVRİM ve RİSK
- Java 20: 1. incubator (paket: `jdk.incubator.concurrent`)
- Java 21: 1. preview (paket `java.lang`'e taşındı, `--enable-preview`)
- Sonraki sürümlerde olgunlaşmaya devam.
- **RİSK:** Henüz kalıcı olmadığı için sürümler arası API/paket değişiklikleri vardır. Üretimde dikkatli kullanın.

---

## DERLEME / ÇALIŞTIRMA NOTU (Java 20)

Java 20'de incubator modülünden geldiği için derleme/çalıştırmada modül eklenmelidir:

```bash
javac --add-modules jdk.incubator.concurrent ScopedDeneme.java
java  --add-modules jdk.incubator.concurrent ScopedDeneme
```

Java 21'de `java.lang` paketine taşındığı ve preview olduğu için orada `--enable-preview` kullanılır.
