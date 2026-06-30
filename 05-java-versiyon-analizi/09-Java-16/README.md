# Java 16 (Mart 2021) — Detaylı Türkçe Dokümantasyon

## Sürüm Bilgisi

| Özellik | Değer |
|---|---|
| Sürüm | Java SE 16 / JDK 16 |
| Çıkış Tarihi | 16 Mart 2021 |
| LTS mi? | **HAYIR** — kısa destekli ("feature release") sürümdür |
| Destek Durumu | Java 17 (Eylül 2021) çıkınca destek sona erdi |
| Önemi | Java 17 LTS'e giden yolun son taşı. Records ve Pattern Matching burada **kalıcı** oldu |

> **Not:** Java 16 üretimde uzun süre kullanılacak bir sürüm değildi. Asıl önemi,
> birçok modern dil özelliğini "preview"den çıkarıp **stabil** hale getirmesi ve
> hemen ardından gelen **Java 17 LTS** için zemin hazırlamasıdır.

---

## Genel Bakış — Bu Sürümde Neler Var?

| Özellik | Durum | Dosya |
|---|---|---|
| Records (Kayıtlar) | **Kalıcı** | `RecordsKalici.java` |
| Pattern Matching for `instanceof` | **Kalıcı** | `PatternMatchingKalici.java` |
| `Stream.toList()` | **Kalıcı** | `StreamToList.java` |
| Sealed Classes | İkinci Preview | (Java 17'de kalıcı — `10-Java-17/`) |
| Day Period Support | Kalıcı | — (aşağıda anlatılır) |
| JDK Internals'ın güçlü kapsüllenmesi | Varsayılan açık | — (geçiş riski!) |
| Unix-Domain Socket Channels | Kalıcı | — (aşağıda anlatılır) |
| Foreign Linker API | Incubator | — (deneysel) |

---

## 1) Records (Kayıtlar) — KALICI

**Dosya:** `RecordsKalici.java`

### NEDİR?
Yalnızca veri taşıyan (data carrier) değişmez (immutable) sınıflar için gelen özel bir sınıf türü. `record Nokta(int x, int y) {}` tek satırı; private final alanları, constructor'ı, erişimcileri (`x()`, `y()`), `equals()`, `hashCode()` ve `toString()` metotlarını otomatik üretir.

### NEDEN GELDİ?
Java'da on yıllardır yazdığımız DTO/POJO sınıfları aşırı tekrar eden kod (boilerplate) içeriyordu. İki alanlık bir sınıf için bile 40+ satır yazmak gerekiyordu. Records bunu ortadan kaldırır.

### NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR?
- API request/response (DTO) modelleri
- Map anahtarları (`equals`/`hashCode` hazır gelir)
- Stream pipeline'larında ara veri yapıları
- **Sealed Classes + Pattern Matching** ile birlikte cebirsel veri tipleri (bkz. Java 17)

### ESKİ vs YENİ
```java
// ESKİ: ~40 satır (alanlar, ctor, getterlar, equals, hashCode, toString)
final class KlasikNokta { private final int x; ... }

// YENİ: tek satır
record Nokta(int x, int y) {}
```

### Records'ta Neler Yapılabilir / Yapılamaz?
- **Yapılabilir:** compact constructor (doğrulama), custom/static metotlar, interface implement, generic record, accessor override, nested/local record.
- **Yapılamaz:** `extends` ile türetilmek/türetmek (record örtük `final`), ek instance alanı eklemek, alanları mutable yapmak.

### Gerçek Hayat
`RecordsKalici.java` içinde e-ticaret `SiparisOzeti` örneği: iç içe record'lar, compact constructor ile savunmacı kopya (`List.copyOf`), ve `toplamTutar()` gibi hesaplanmış metotlar gösterilir.

---

## 2) Pattern Matching for `instanceof` — KALICI

**Dosya:** `PatternMatchingKalici.java`

### NEDİR?
`if (o instanceof String s)` ifadesiyle tip kontrolü, cast ve değişkene atamayı **tek adımda** birleştirir.

### ESKİ vs YENİ
```java
// ESKİ: 3 adım
if (o instanceof String) {
    String s = (String) o;   // ayrı cast
    use(s);
}
// YENİ: tek satır, cast yok
if (o instanceof String s) { use(s); }
```

### NEREDE KOLAYLIK SAĞLAR?
- `equals()` metotlarını sadeleştirir
- Karışık tipli (heterojen) verilerde tip yönlendirmesi
- `&&` ile koşul birleştirme: `if (o instanceof String s && s.length() > 5)`

---

## 3) `Stream.toList()` — KALICI

**Dosya:** `StreamToList.java`

### NEDİR / NEDEN GELDİ?
Bir stream'i listeye çevirmenin kısa yolu. Artık `.collect(Collectors.toList())` yerine doğrudan `.toList()`.

### ESKİ vs YENİ
```java
// ESKİ
list.stream().filter(...).collect(Collectors.toList());
// YENİ
list.stream().filter(...).toList();
```

### ÖNEMLİ FARK
`.toList()` **değişmez (unmodifiable)** liste döndürür. `add`/`remove` denenirse `UnsupportedOperationException` fırlatır. Eğer sonradan değiştirmeniz gerekiyorsa eski yöntemi (veya `Collectors.toCollection(ArrayList::new)`) kullanın.

---

## 4) Day Period Support (JDK-8247781)
`DateTimeFormatter` artık günün bölümlerini ("sabah", "öğleden sonra", "akşam", "gece") biçimlendirebilir (`B` deseni). CLDR verisine dayanır. Yerelleştirilmiş, insan-dostu zaman gösterimi sağlar.

---

## 5) JDK Internals'ın Güçlü Kapsüllenmesi (Strong Encapsulation) — GEÇİŞ RİSKİ!
- Java 9'dan beri uyarı verilen iç API'lere (`sun.misc.Unsafe`, dahili paketler) erişim Java 16'da **varsayılan olarak engellendi**.
- `--illegal-access=permit` artık varsayılan değil; reflection ile dahili sınıflara erişen eski kütüphaneler **bozulabilir**.
- **Risk:** Eski Hibernate, eski Mockito, bazı serileştirme kütüphaneleri çalışmayabilir. Geçici çözüm `--add-opens` bayrakları; kalıcı çözüm kütüphaneleri güncellemek.
- `sun.misc.Unsafe` gibi kritik API'ler hâlâ erişilebilir kaldı ama bu eğilim kapanma yönündedir.

---

## 6) Unix-Domain Socket Channels (JEP 380)
`SocketChannel` / `ServerSocketChannel` artık Unix-domain soketlerini destekler. Aynı makinedeki süreçler arası iletişimde (IPC) TCP/IP'ye göre daha hızlı ve güvenlidir (örn. veritabanı/uygulama sunucusu aynı makinedeyse).

---

## 7) Foreign Linker API (JEP 389) — Incubator (Deneysel)
JNI'a modern, güvenli bir alternatif: Java'dan yerel (native) C kütüphanelerini çağırma. Henüz deneysel; ilerleyen sürümlerde "Foreign Function & Memory API" olarak olgunlaştı.

---

## Java 8/11'den Geçiş Notları
- **Avantaj:** Records ve pattern matching artık stabil — kod tabanınızı sadeleştirin.
- **Risk:** Güçlü kapsülleme nedeniyle dahili API'lere dayanan eski bağımlılıklar kırılabilir. Önce bağımlılıkları güncelleyin, `--add-opens`'ı geçici çözüm olarak kullanın.
- **Öneri:** Java 16'da kalmayın; **Java 17 LTS'e** geçin. Java 16'daki tüm kalıcı özellikler 17'de de vardır, üstüne LTS güvencesi gelir.

---

## Bu Klasördeki Dosyalar
- `RecordsKalici.java` — Records'ın derinlemesine kullanımı
- `PatternMatchingKalici.java` — `instanceof` kalıp eşleştirme
- `StreamToList.java` — `Stream.toList()`

### Derleme ve Çalıştırma
```bash
javac RecordsKalici.java && java RecordsKalici
javac PatternMatchingKalici.java && java PatternMatchingKalici
javac StreamToList.java && java StreamToList
```
