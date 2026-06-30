# Base64 Kodlama

**Base64**, ikili (binary) veriyi yalnızca güvenli ASCII karakterleriyle (A–Z, a–z, 0–9, `+`, `/`)
temsil eden bir **kodlama** şemasıdır. Amaç, yalnızca metin taşıyabilen kanallardan (JSON, XML,
e-posta, URL, HTTP başlıkları) ikili veriyi sorunsuz geçirmektir. Java 8'den beri `java.util.Base64`
sınıfıyla temiz bir API sunulur. En kritik nokta: **Base64 şifreleme değildir** — gizlilik sağlamaz.

## Temel kodlama ve çözme

```java
String kodlu = Base64.getEncoder().encodeToString(metin.getBytes(StandardCharsets.UTF_8));
byte[] cozulen = Base64.getDecoder().decode(kodlu);
String geri = new String(cozulen, StandardCharsets.UTF_8);
```

Örnek 1 (`./Ornek1.java`) metin kodlama/çözme döngüsünü gösterir. Kodlama kayıpsızdır: çözünce
orijinali birebir geri alırsın.

## Üç varyant (encoder türü)

| Encoder | Ne yapar | Nerede |
|---------|----------|--------|
| `getEncoder()` | Standart Base64 (`+`, `/`) | Genel amaçlı |
| `getUrlEncoder()` | URL-güvenli (`-`, `_`) | URL, dosya adı, JWT |
| `getMimeEncoder()` | 76 karakterde satır kırar | E-posta ekleri, PEM sertifikaları |

- **URL-güvenli:** Standart Base64'teki `+` ve `/` karakterleri URL'de ve dosya adlarında sorun
  çıkarır; URL encoder bunları `-` ve `_` yapar. JWT token'ları bunu kullanır.
- **MIME:** Uzun çıktıyı 76 karakterlik satırlara böler (eski e-posta/sertifika formatları için).

Örnek 1 URL-güvenli kodlamayı, Örnek 2 (`./Ornek2.java`) ikili veri ve MIME kodlamayı gösterir.

## Gerçek kullanım örnekleri

- **HTTP Basic Authentication:** `Authorization: Basic base64("kullanıcı:parola")`. Örnek 2 bunu
  üretir. Çözülebilir olduğu için **HTTPS zorunludur**.
- **Data URL'leri:** `data:image/png;base64,iVBOR...` — küçük resimleri HTML/CSS içine gömme.
- **JSON/XML içinde ikili veri:** Bir resmi/dosyayı JSON alanında taşımak.
- **JWT:** Token'ın bölümleri URL-güvenli Base64 ile kodlanır.

## Önemli uyarılar

> **Base64 ŞİFRELEME DEĞİLDİR.** Herkes anında çözebilir; hiçbir gizlilik sağlamaz. Hassas veri için
> gerçek şifreleme (AES vb.) kullan. Base64 yalnızca "ikili veriyi metin kanalından geçirme"
> işidir.

> **Boyut artışı:** Base64, veriyi yaklaşık **%33 büyütür** (her 3 bayt → 4 karakter). Sıkıştırma
> değil, kodlamadır; bant genişliği/depolama hesabında bunu unutma.

## Özet

Base64'ün ikili veriyi güvenli ASCII metnine çeviren bir kodlama olduğunu; temel kodlama/çözmeyi
ve üç varyantı — standart, URL-güvenli, MIME (Örnek 1–2) — öğrendik; Basic Auth, data URL, JWT gibi
gerçek kullanımları ve iki kritik uyarıyı (şifreleme değildir, %33 büyütür) gördük. Bununla Misc
bölümünün bu turdaki kısmı (Regex, Serialization, Recursion, Process API, Base64) tamamlandı.
