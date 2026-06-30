# Simple Web Server (JEP 408) — Java 18

> Bu doküman Java 18 ile gelen **Simple Web Server** özelliğini detaylı anlatır. Komut satırı aracı `jwebserver` ve programatik API'yi kapsar.

---

## NEDİR?

Java 18, JDK içinde iki şey getirir:

1. **`jwebserver`** — Komut satırı aracı. İçinde bulunduğunuz dizini bir HTTP statik dosya sunucusu olarak yayınlar.
2. **`com.sun.net.httpserver`** paketindeki yeni `SimpleFileServer` API'si — sunucuyu Java kodundan programatik olarak başlatma/yapılandırma.

Bu, "pil dahil" (batteries included) felsefesinin bir parçasıdır: ufak bir statik sunucu için artık harici araca veya tam bir web framework'üne gerek yok.

---

## NEDEN GELDİ?

Java geliştiricileri uzun süredir basit bir statik sunucu için Python'un `python -m http.server` komutuna ya da Node.js'in `http-server` paketine başvuruyordu. JDK kendi başına böyle bir kolaylık sunmuyordu. Oysa:

- Eğitimde HTTP kavramlarını canlı göstermek gerekir.
- Statik bir frontend build'ini hızlıca denemek gerekir.
- Bir REST istemcisini test ederken sahte yanıt dosyaları sunmak gerekir.
- Geçici dosya paylaşımı gerekir.

Bu küçük ama yaygın ihtiyaçlar için ağır çözümler abartılıydı.

---

## NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR?

- **Sıfır kurulum:** JDK 18+ varsa araç zaten orada.
- **Tek komut:** Anında çalışan bir sunucu.
- **Cross-platform:** Aynı komut Windows/macOS/Linux'ta aynı çalışır.
- **Eğitim:** Öğrencilere HTTP istek/yanıt döngüsünü göstermek için ideal.

---

## KULLANIM — Komut Satırı (`jwebserver`)

### En basit kullanım
```bash
# İçinde bulunulan dizini varsayılan olarak localhost:8000'de yayınlar
jwebserver
```

Çıktı örneği:
```
Binding to loopback by default. For all interfaces use "-b 0.0.0.0" or "-b ::".
Serving /Users/kullanici/proje and subdirectories on 127.0.0.1 port 8000
URL http://127.0.0.1:8000/
```

### Yaygın seçenekler
```bash
# Port belirt
jwebserver -p 9000

# Tüm ağ arayüzlerine bağlan (başka cihazlardan erişim için)
jwebserver -b 0.0.0.0 -p 8080

# Belirli bir dizini yayınla
jwebserver -d /Users/kullanici/proje/dist

# Çıktı ayrıntı düzeyi (none | info | verbose)
jwebserver -o verbose
```

| Seçenek | Anlamı | Varsayılan |
|---|---|---|
| `-p` | Port | 8000 |
| `-b` | Bağlanılacak adres (bind) | 127.0.0.1 (loopback) |
| `-d` | Yayınlanacak dizin | Geçerli dizin |
| `-o` | Çıktı düzeyi | info |

---

## ESKİ vs YENİ

```bash
# ESKİ — Python'a bağımlı (Java geliştiricisinde Python olmayabilir)
python3 -m http.server 8000

# ESKİ — Node.js ekosistemi
npx http-server -p 8000

# ESKİ — Java'da tam bir web sunucusu (Jetty/Tomcat) yazmak: aşırı ağır

# YENİ (Java 18+) — JDK'nın bir parçası
jwebserver -p 8000
```

---

## PROGRAMATİK KULLANIM (API)

Sadece statik dosya sunmakla kalmaz; özel istek işleyiciler (handler) de tanımlanabilir.

```java
import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class StatikSunucu {
    public static void main(String[] args) {
        // 1) Sadece statik dosya sunucusu olarak başlat
        var sunucu = SimpleFileServer.createFileServer(
                new InetSocketAddress(8080),
                Path.of("/Users/kullanici/proje/dist").toAbsolutePath(),
                OutputLevel.INFO);
        sunucu.start();
        System.out.println("Sunucu http://localhost:8080 adresinde calisiyor");
    }
}
```

Daha gelişmiş senaryolarda, statik dosya işleyicisini özel bir işleyiciyle birleştirebilirsiniz (`HttpServer` + `SimpleFileServer.createFileHandler(...)`).

---

## GERÇEK HAYAT ÖRNEĞİ

**Senaryo:** Bir frontend ekibi, derlenmiş (build edilmiş) bir SPA'yı (`dist/` klasörü) test cihazlarında hızlıca denemek istiyor.

```bash
cd dist
jwebserver -b 0.0.0.0 -p 8080
```

Artık aynı Wi-Fi'deki bir telefon `http://192.168.1.x:8080` ile uygulamayı açabilir. Ne Python ne Node kurmaya gerek var.

**Senaryo 2:** Bir backend geliştirici, üçüncü parti bir servisin JSON yanıtlarını taklit etmek istiyor. Yanıt dosyalarını bir klasöre koyup `jwebserver` ile yayınlar; istemci kodunu bu sahte uçlara yönlendirir.

---

## AVANTAJ / DEZAVANTAJ / RİSK

### Avantajlar
- Sıfır bağımlılık, anında kullanım.
- Çapraz platform tutarlılık.
- Hem CLI hem API.

### Dezavantajlar / Sınırlamalar
- **Yalnızca `GET` ve `HEAD`** destekler. `POST`/`PUT` yoktur.
- **HTTPS yoktur.**
- **Kimlik doğrulama / yetkilendirme yoktur.**
- Dizin listeleme dışında gelişmiş yapılandırma sınırlıdır.

### RİSK / UYARI
> **ASLA üretimde kullanmayın.** Bu araç açıkça yalnızca **geliştirme, test ve eğitim** içindir. Güvenlik sertleştirmesi (hardening) yapılmamıştır. İnternete açık ortamlarda kullanılması güvenlik açığı yaratır.

---

## GEÇİŞ NOTU
Bu özellik Java 18'de **KALICI** olarak geldi. Java 18 ve sonraki tüm sürümlerde `jwebserver` mevcuttur. `--enable-preview` gerektirmez.
