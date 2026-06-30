# Date & Time (java.time)

Tarih ve saatle çalışmak, neredeyse her uygulamada gereklidir: kayıt zamanı, doğum tarihi, randevu,
zaman damgası, geri sayım. Java 8'e kadar bu, hatalı ve sinir bozucu `Date`/`Calendar` sınıflarıyla
yapılırdı. Java 8, **`java.time`** paketini getirdi: değişmez, açık ve doğru tasarlanmış modern bir
tarih/saat API'si. Bu konu, tarih/saatle güvenle çalışmayı baştan sona ele alır.

## Neden eski Date/Calendar değil?

Eski API'nin sorunları efsanevidir: `Date` **değişebilir** (mutable — thread-safe değil),
**aylar 0'dan başlar** (Ocak = 0!), yıl 1900'den sayılır, `SimpleDateFormat` thread-safe değildir.
`java.time` bunların hepsini çözer: **değişmez**, açık adlandırma, mantıklı değerler.

## Temel tipler

| Tip | Ne tutar | Örnek |
|-----|----------|-------|
| `LocalDate` | Yalnızca tarih | 2026-06-23 |
| `LocalTime` | Yalnızca saat | 14:30 |
| `LocalDateTime` | Tarih + saat (bölgesiz) | 2026-06-23T14:30 |
| `ZonedDateTime` | Tarih + saat + bölge | ...Europe/Istanbul |
| `Instant` | Zaman çizgisinde bir an (UTC) | epoch tabanlı |
| `Period` | Tarih farkı (yıl/ay/gün) | 30 yıl 1 ay |
| `Duration` | Zaman farkı (saat/dk/sn) | 8 saat 30 dk |

## Oluşturma ve aritmetik

```java
LocalDate t = LocalDate.of(2026, Month.JUNE, 23);   // sabit
LocalDate bugun = LocalDate.now();                   // şu an
t.plusDays(10); t.minusMonths(2); t.plusYears(1);    // YENİ nesne (immutable!)
t.getDayOfWeek();                                    // gün adı
dun.isBefore(t); t.isAfter(dun);                     // karşılaştırma
```

> **Değişmezlik:** Tüm `java.time` tipleri değişmezdir; `plus`/`minus` orijinali **değiştirmez**,
> yeni bir nesne döndürür. `t.plusDays(10);` tek başına bir işe yaramaz — sonucu kullanmalısın.

Örnek 1 (`./Ornek1.java`) temel tipleri, aritmetiği, `Period`/`Duration` ve `ChronoUnit.between` ile
fark hesabını gösterir.

## Biçimlendirme ve ayrıştırma

`DateTimeFormatter`, tarih/saat ↔ String dönüşümünü yapar:

```java
DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
dt.format(f);                          // tarih -> "23.06.2026 14:30"
LocalDateTime.parse("15.08.2026 09:45", f);  // String -> tarih
```

Desen harfleri: `yyyy` yıl, `MM` ay (sayı), `MMMM` ay (ad), `dd` gün, `HH` saat (24'lük), `mm`
dakika, `EEEE` gün adı. `Locale` ile dile özgü (Türkçe ay/gün adları) biçim alınır. Örnek 2
(`./Ornek2.java`) biçimlendirme ve ayrıştırmayı gösterir.

> **Thread-safety:** Eski `SimpleDateFormat`'ın aksine, `DateTimeFormatter` **thread-safe**'dir;
> bir kez oluşturup paylaşabilirsin.

## Zaman dilimleri ve Instant

Küresel uygulamalarda zaman dilimi kritiktir:

```java
ZonedDateTime ist = ZonedDateTime.of(dt, ZoneId.of("Europe/Istanbul"));
ZonedDateTime ny = ist.withZoneSameInstant(ZoneId.of("America/New_York")); // aynı an, farklı yerel saat
Instant an = ist.toInstant();          // mutlak an (UTC, epoch)
```

- **`ZonedDateTime`**: Belirli bir bölgedeki tarih/saat (yaz saati dahil).
- **`Instant`**: Zaman çizgisindeki mutlak bir an (UTC, epoch'tan beri). Loglar, zaman damgaları,
  makineler arası iletişim için.

Örnek 2 İstanbul ↔ New York dönüşümünü ve `Instant`'ı gösterir.

> **Altın kural:** Zamanı **UTC olarak sakla/ilet** (`Instant`), yalnızca **kullanıcıya gösterirken**
> yerel bölgeye çevir. Bu, zaman dilimi ve yaz saati hatalarını önler.

## Spring/JPA ile

Modern Java/Spring `java.time` tiplerini doğrudan destekler: JPA `@Entity` alanları `LocalDate`/
`LocalDateTime`/`Instant` olabilir; Jackson bunları JSON'a (ISO-8601) çevirir; veritabanı sürücüleri
eşler. Yani eski `Date`'e dönmene gerek yoktur.

## Özet

Modern `java.time` API'sini öğrendik: neden eski `Date`/`Calendar`'dan üstün olduğunu; temel tipleri
(`LocalDate`/`Time`/`DateTime`), değişmez aritmetiği, `Period`/`Duration` (Örnek 1); biçimlendirme/
ayrıştırmayı (`DateTimeFormatter`) ve zaman dilimlerini (`ZonedDateTime`/`Instant`; Örnek 2) gördük;
"UTC sakla, yerelde göster" kuralını ve Spring/JPA entegrasyonunu ele aldık. Bununla temel/kavramsal
derin konular batch'i tamamlandı.
