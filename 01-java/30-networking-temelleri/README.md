# Ağ Programlama (Networking) Temelleri

Bugün yazdığımız uygulamaların neredeyse hiçbiri yalnız çalışmaz: bir mobil uygulama sunucuya
istek atar, bir web sitesi veritabanına bağlanır, mikroservisler birbirleriyle konuşur. Bütün
bunların altında **ağ programlama** vardır. İyi haber şu ki Java, ağ iletişimini standart
kütüphanesi (`java.net`) ile baştan destekler; üstelik bunu dosya okur gibi, akışlarla (stream)
yapmana izin verir. Bu bölümde ağın temel kavramlarını ve Java'nın bunları nasıl modellediğini,
**internet gerektirmeden, kendi makinende çalışan** örneklerle öğreneceğiz.

## Ağın temel kavramları

Bir ağ iletişimini anlamak için birkaç kavramı netleştirelim:

- **IP adresi:** Ağdaki her makinenin kimliği. IPv4 (`192.168.1.5`) veya IPv6 (`2001:db8::1`).
- **Port:** Bir makinedeki belirli bir servisin "kapı numarası" (0–65535). Web sunucusu 80/443,
  veritabanı 5432/3306 gibi. Bir IP + port = bir uç nokta (endpoint).
- **localhost / loopback (`127.0.0.1`, `::1`):** "Bu makinenin kendisi". Ağ kartına bile gitmeden,
  makinenin kendi içinde iletişim. Test için idealdir (ve internet gerektirmez).
- **DNS:** İnsan-okunur adları (`example.com`) IP adreslerine çeviren "telefon rehberi".
- **İstemci / Sunucu (client/server):** Sunucu bir portu **dinler** ve bağlantı bekler; istemci
  o porta **bağlanır**. İstek-cevap bu kanal üzerinden akar.

## Adresleri tanımak: InetAddress

Java'da bir makineyi/IP'yi `InetAddress` temsil eder. Ad↔IP çözümleme, loopback erişimi ve
ulaşılabilirlik testi sağlar:

```java
InetAddress loopback = InetAddress.getLoopbackAddress(); // 127.0.0.1
InetAddress localhost = InetAddress.getByName("localhost");
InetAddress benim = InetAddress.getLocalHost();          // bu makine
loopback.isReachable(1000);                               // ulaşılabilir mi?
```

Örnek 1 (`./Ornek1.java`) bunları çalıştırır (loopback ve yerel makineyle; internet gerekmez) ve
IPv4/IPv6 ayrımını gösterir.

## İki taşıma protokolü: TCP ve UDP

Veriyi ağ üzerinden taşımanın iki temel yolu vardır ve seçim işin doğasına bağlıdır:

### TCP (Transmission Control Protocol)

**Bağlantı tabanlı** ve **güvenilir**: önce bir bağlantı kurulur (handshake), sonra veri
**sırayla, kayıpsız ve doğrulanarak** akar. Java'da:

- Sunucu: `ServerSocket` bir portu dinler, `accept()` ile bağlantı bekler (bloklar).
- İstemci: `Socket` ile sunucuya bağlanır.
- İki taraf da giriş/çıkış akışlarıyla (`getInputStream`/`getOutputStream`) konuşur — tıpkı
  dosya okur/yazar gibi.

```java
ServerSocket sunucu = new ServerSocket(port);
Socket istemci = sunucu.accept();      // bağlantı bekle
// ... in/out akışlarıyla konuş ...
Socket soket = new Socket("localhost", port); // istemci bağlanır
```

Örnek 2 (`./Ornek2.java`) tek bir JVM'de bir TCP **echo** sunucusu (gelen satırı geri yansıtan)
ve ona bağlanan bir istemci kurar. HTTP, FTP, veritabanı bağlantıları... hepsi TCP üzerindedir.

### UDP (User Datagram Protocol)

**Bağlantısız** ve **hızlı** ama **güvencesiz**: handshake yoktur, her paket (datagram) bağımsız
gönderilir; sıra ve teslim garanti edilmez. Java'da `DatagramSocket` ve `DatagramPacket` kullanılır.

```java
DatagramSocket soket = new DatagramSocket();
soket.send(new DatagramPacket(veri, veri.length, adres, port));
soket.receive(paket);
```

Örnek 3 (`./Ornek3.java`) loopback üzerinde bir UDP gönder/al döngüsü gösterir. UDP; canlı
video/ses, çevrim içi oyunlar ve DNS gibi **hızın az kayıptan önemli** olduğu yerlerde kullanılır.

| | TCP | UDP |
|---|-----|-----|
| Bağlantı | Var (handshake) | Yok |
| Güvenilirlik | Yüksek (sıralı, kayıpsız) | Düşük (kayıp olabilir) |
| Hız | Görece yavaş | Hızlı |
| Kullanım | Web, dosya, DB | Video/ses, oyun, DNS |

## Bloklama (blocking) ve thread'ler

Klasik soket API'si **bloklayıcıdır**: `accept()` bağlantı gelene, `read()` veri gelene kadar
bekler. Bu yüzden gerçek sunucular her istemciyi ayrı bir thread'de (veya bir thread havuzunda)
karşılar — böylece bir istemci diğerlerini bekletmez. Bunu bir sonraki konuda (Socket Programlama)
çok istemcili bir sunucuyla göreceğiz. (Çok sayıda eşzamanlı bağlantı için Java 21'in **sanal
thread'leri** burada büyük avantaj sağlar.)

## Özet

Ağ programlamanın temel kavramlarını (IP, port, localhost/loopback, DNS, istemci/sunucu),
adresleri tanımak için `InetAddress`'i ve iki taşıma protokolünü — güvenilir **TCP** ile hızlı
**UDP** — kendi makinende çalışan örneklerle öğrendik. Sırada, TCP soketleriyle gerçek istemci-
sunucu uygulamaları yazmayı derinleştiriyoruz: **Socket Programlama**.
