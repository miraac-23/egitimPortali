# Socket Programlama

Önceki bölümde ağın temel kavramlarını ve TCP/UDP'yi tanıdık. Şimdi TCP soketleriyle **gerçek
istemci-sunucu uygulamaları** yazmayı derinleştiriyoruz. Soket, iki makine (veya aynı makinedeki
iki süreç) arasındaki iletişim kanalının iki ucundan biridir. Java'da soketle çalışmak, dosyayla
çalışmaya çok benzer: bir giriş akışından okur, bir çıkış akışına yazarsın. Örneklerin hepsi tek
bir JVM'de (loopback üzerinde) çalışır; internet gerektirmez.

## Soketin anatomisi

İki temel sınıf vardır:

- **`ServerSocket`** (sunucu tarafı): Bir portu **dinler**. `accept()` çağrısı bir istemci
  bağlanana kadar **bloklar** ve bağlandığında o istemciyle konuşmak için bir `Socket` döndürür.
- **`Socket`** (istemci tarafı ve sunucunun istemci-başına soketi): İki uç arasındaki bağlantı.
  `getInputStream()` ve `getOutputStream()` ile veri akar.

```java
// Sunucu
ServerSocket sunucu = new ServerSocket(port);
Socket istemci = sunucu.accept();           // bağlantı bekle (bloklar)
// İstemci
Socket soket = new Socket("localhost", port); // bağlan
```

Akışları metin için `BufferedReader`/`PrintWriter` ile sarmak en pratik yoldur (satır satır
oku/yaz). Ham byte için doğrudan stream'leri, nesne için `ObjectStream`'leri kullanırsın.

## Bir protokol tanımlamak

Soket sana ham bir kanal verir; üzerine **kendi protokolünü** (mesaj formatını) tanımlarsın. En
basit haliyle satır tabanlı bir istek-cevap olabilir. Örnek 1 (`./Ornek1.java`) bir "hesap
makinesi" sunucusu kurar: istemci `"5 + 3"` gönderir, sunucu ayrıştırıp `"= 8"` döner; geçersiz
ifade ve sıfıra bölme gibi hataları da yönetir. HTTP bile özünde böyle metin tabanlı bir istek/
cevap protokolüdür — sadece çok daha zengin kuralları vardır.

## Çok istemciye aynı anda hizmet: thread'ler

Burada kritik bir gerçek var: `accept()` ve `read()` **bloklar**. Tek thread'le yazarsan, bir
istemciye hizmet ederken diğerleri bekler. Çözüm, her bağlantıyı **ayrı bir thread'de** işlemektir:

```java
while (true) {
    Socket c = sunucu.accept();              // bağlantı bekle
    new Thread(() -> istemciyiHandle(c)).start(); // ayrı thread'de işle
}
```

Örnek 2 (`./Ornek2.java`) aynı anda bağlanan 3 istemciyi thread-per-client modeliyle karşılar; her
istemci kendi thread'inde yanıt alır.

> **Üretimde:** Her bağlantı için yeni `Thread` oluşturmak pahalıdır; bunun yerine bir **thread
> havuzu** (`ExecutorService`) kullanılır. Ve **on binlerce** eşzamanlı bağlantıda Java 21'in
> **sanal thread'leri** (her bağlantıya ucuz bir sanal thread) oyunu değiştirir — bloklayan basit
> kodla yüksek eşzamanlılık.

## Nesne aktarımı

Metin dışında, yapılandırılmış **Java nesnelerini** de gönderebilirsin. `ObjectOutputStream`
nesneyi byte'lara çevirir (serialize), `ObjectInputStream` karşıda geri kurar (deserialize):

```java
out.writeObject(new SiparisIstegi(101, 3));
SiparisCevabi cevap = (SiparisCevabi) in.readObject();
```

Örnek 3 (`./Ornek3.java`) soket üzerinden bir istek/cevap nesne çifti aktarır. Nesnelerin
`Serializable` olması gerekir.

> **Uyarı:** Java'nın yerleşik serileştirmesi güvenlik ve sürüm-uyumluluğu açısından kırılgandır.
> Farklı sistemler/diller arası iletişimde günümüzde **JSON** veya **Protobuf** gibi taşınabilir
> formatlar tercih edilir (bunu Spring/REST tarafında JSON ile gördük).

## Kaynak yönetimi

Soketler ve akışlar birer kaynaktır; mutlaka kapatılmalıdır. `try-with-resources` bunu otomatik
yapar (örneklerde `try (Socket s = ...)`). Kapatılmayan soketler port/bağlantı sızıntısına yol açar.

## Özet

`ServerSocket`/`Socket` ile istemci-sunucu kurmayı; soket üzerine kendi metin protokolünü
tanımlamayı (Örnek 1); çok istemciye thread'lerle eşzamanlı hizmeti (Örnek 2) ve nesne aktarımını
(Örnek 3) öğrendik; thread havuzu/sanal thread ve serileştirme uyarılarına değindik. Bu, tüm ağ
uygulamalarının (web sunucuları dahil) altında yatan mekanizmadır. Sırada, daha yüksek seviyeli
bir soyutlama: adreslerle çalışmak için **URL ve URL işlemleri**.
