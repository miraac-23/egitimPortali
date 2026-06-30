# Düzenli İfadeler (Regular Expressions / Regex)

**Regex**, metin içinde desen aramak, doğrulamak ve değiştirmek için kullanılan güçlü bir mini
dildir. "Bu metin geçerli bir e-posta mı?", "şu logdaki tüm tarihleri bul", "kart numarasını
maskele" gibi işleri tek satırda yapar. Java'da `java.util.regex` paketi (`Pattern` ve `Matcher`)
ile kullanılır; ayrıca `String.matches`/`replaceAll`/`split` kısayolları vardır.

## Pattern ve Matcher

İki temel sınıf:

- **`Pattern`**: Derlenmiş bir regex kalıbı (`Pattern.compile("\\d+")`).
- **`Matcher`**: Kalıbı belirli bir metne uygulayan motor (`pattern.matcher(metin)`).

```java
Pattern p = Pattern.compile("\\d+");
Matcher m = p.matcher("oda 12, kat 3");
while (m.find()) System.out.println(m.group());  // 12, 3
```

İki temel arama yöntemi:

- **`matches()`**: **Tüm** metin kalıba uyuyor mu? (doğrulama için)
- **`find()`**: Metnin **içinde** kalıbı arar (tüm eşleşmeleri gezer).

## Sık kullanılan metakarakterler

| Sembol | Anlamı | | Sembol | Anlamı |
|--------|--------|---|--------|--------|
| `\d` | rakam | | `+` | bir veya çok |
| `\w` | harf/rakam/`_` | | `*` | sıfır veya çok |
| `\s` | boşluk | | `?` | sıfır veya bir |
| `.` | herhangi karakter | | `{n,m}` | n–m tekrar |
| `^` `$` | başlangıç/bitiş | | `[...]` | karakter kümesi |
| `(...)` | yakalama grubu | | `|` | veya |

> **Java tuzağı:** Regex'teki `\` Java string'inde `\\` yazılır: `"\\d+"` (regex `\d+`).

## Gruplar

Parantezler alt-parçaları **yakalar**; `group(n)` ile erişilir. İsimli gruplar okunabilirliği
artırır:

```java
Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4})");   // group(1)=gün, group(2)=ay...
Pattern.compile("(?<kullanici>\\w+)@(?<alan>[\\w.]+)"); // group("kullanici")
```

Örnek 1 (`./Ornek1.java`) matches/find, numaralı ve isimli grupları gösterir.

## Doğrulama, değiştirme, bölme

- **Doğrulama:** `pattern.matcher(metin).matches()` (e-posta, telefon formatı).
- **`replaceAll(kalip, yenisi)`:** Geri referanslarla (`$1`, `$2`) değiştirme — maskeleme,
  biçimleme, boşluk temizleme.
- **`split(kalip)`:** Metni regex'e göre parçalara ayırma (CSV, boşluk).

Örnek 2 (`./Ornek2.java`) e-posta/telefon doğrulama, kart maskeleme (`$1`), boşluk sadeleştirme ve
CSV bölme gösterir.

## Performans ve güvenlik

- **Önceden derle:** Aynı kalıbı tekrar kullanacaksan `Pattern.compile`'ı **bir kez** yap (alan
  olarak sakla); her seferinde derlemek pahalıdır. `String.matches` her çağrıda derler — döngüde
  kullanma.
- **Catastrophic backtracking:** Bazı kalıplar (iç içe `(a+)+` gibi) belirli girdilerde üstel
  yavaşlar. Kullanıcıdan gelen regex'lere veya karmaşık kalıplara dikkat et.

## Özet

Regex'in metin arama/doğrulama/değiştirme dili olduğunu; `Pattern`/`Matcher` ile `matches`/`find`,
metakarakterleri ve grupları (Örnek 1); doğrulama, `replaceAll` ile maskeleme ve `split`'i (Örnek 2)
öğrendik; performans (önceden derleme) ve güvenlik (backtracking) notlarına değindik. Sırada,
nesneleri byte'lara çevirip saklama/iletme: **Serialization**.
