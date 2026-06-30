# Komut Satırı Argümanları

Bir programa çalışırken dışarıdan veri vermenin en temel yolu **komut satırı argümanlarıdır**.
`main` metodunun `String[] args` parametresi, programı başlatırken verdiğin değerleri taşır.
Komut satırı araçları (CLI), betikler ve toplu işler (batch jobs) bu mekanizmayla yapılandırılır.

> **Not:** Bu portal programları **argümansız** çalıştırır (`args.length == 0`). Bu yüzden
> örnekler hem gerçek `args`'ı kontrol eder hem de örnek bir diziyle ayrıştırmayı gösterir.

## main(String[] args)

```java
public static void main(String[] args) { ... }
```

`java Program a b c` komutunda `args = ["a", "b", "c"]` olur.

> **Önemli:** C/C++'ın aksine, Java'da `args` **program adını içermez** — ilk eleman doğrudan ilk
> gerçek argümandır.

Argümanlar her zaman **`String`**'tir; sayı gerekiyorsa dönüştürürsün:

```java
int yas = Integer.parseInt(args[1]);
```

Örnek 1 (`./Ornek1.java`) argümanları gezer, sayıya çevirir ve **eksik argüman kontrolünü** gösterir
(bu kontrol şarttır: eksik argüman `ArrayIndexOutOfBoundsException`, hatalı sayı
`NumberFormatException` atar).

## Bayrak ve seçenek ayrıştırma

Gerçek CLI araçları üç tür argüman alır:

| Tür | Örnek | Anlamı |
|-----|-------|--------|
| Bayrak (flag) | `-v`, `--verbose` | Değersiz; var/yok (aç/kapa) |
| Seçenek (option) | `--cikti rapor.txt` | Anahtar + değer |
| Konumsal (positional) | `girdi.txt` | Sırasıyla anlamlı |

Basit bir ayrıştırıcı elle yazılabilir: `--` ile başlayanlar seçenek (sonraki eleman değeri),
tek `-` bayrak, gerisi konumsal. Örnek 2 (`./Ornek2.java`) bunu uygular ve bir `Map`'e doldurur.

## Üretimde: CLI kütüphaneleri

Elle ayrıştırma küçük araçlar için yeterlidir; ama karmaşık CLI'lerde (otomatik yardım metni, tip
dönüşümü, zorunlu/opsiyonel doğrulama, alt komutlar) kütüphane kullanılır:

- **picocli** (en popüler, anotasyon tabanlı, Spring Boot ile iyi çalışır)
- **JCommander**, **Apache Commons CLI**

```java
@Command(name = "araç")
class Arac implements Runnable {
    @Option(names = "--cikti") String cikti;
    @Parameters String girdi;
    public void run() { ... }
}
```

## Argümanların alternatifleri

Komut satırı argümanları tek yol değildir; yapılandırma için ayrıca:

- **Ortam değişkenleri** (`System.getenv`) — konteyner dünyasında yaygın.
- **Sistem özellikleri** (`-Dkey=value`, `System.getProperty`) — topic 74.
- **Yapılandırma dosyaları** (`.properties`/`.yml`) — Spring Boot.

Genelde bunlar bir öncelik sırasıyla birleştirilir (argüman > env > dosya > varsayılan).

## Özet

`main(String[] args)` ile programa dışarıdan veri vermeyi; argümanların String olduğunu, program
adını içermediğini ve eksik/hatalı argüman kontrolünün gerekliliğini (Örnek 1); bayrak/seçenek/
konumsal argüman ayrıştırmayı (Örnek 2) öğrendik; üretimde CLI kütüphanelerini ve argümanların
yapılandırma alternatiflerini gördük. Sırada, metotsuz "etiketleyen" arayüzler: **Marker
Interface**.
