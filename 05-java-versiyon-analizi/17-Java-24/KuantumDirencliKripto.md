# Kuantum-Dirençli Kriptografi — ML-KEM (JEP 496) & ML-DSA (JEP 497)

> Java 24 ile post-quantum (kuantum-dirençli) kriptografi standart kütüphaneye girdi.
> Bu doküman eğitim amaçlıdır; API'lerin kesin imzaları için kurulu JDK 24'ün resmi
> `javax.crypto` / `java.security` belgeleri esas alınmalıdır.

---

## NEDİR

Java 24 iki yeni, kuantum-dirençli algoritmayı standart Java Kriptografi Mimarisi'ne (JCA)
ekler:

- **ML-KEM (JEP 496)** — *Module-Lattice-Based Key-Encapsulation Mechanism*
  - Kuantum-dirençli **anahtar kapsülleme** (key encapsulation) mekanizması.
  - NIST standardı: **FIPS 203** (CRYSTALS-Kyber tabanlı).
  - Görevi: iki taraf arasında güvenli bir **ortak gizli anahtar** (shared secret)
    oluşturmak. Bu ortak gizli, daha sonra simetrik şifreleme (örn. AES) için kullanılır.

- **ML-DSA (JEP 497)** — *Module-Lattice-Based Digital Signature Algorithm*
  - Kuantum-dirençli **dijital imza** algoritması.
  - NIST standardı: **FIPS 204** (CRYSTALS-Dilithium tabanlı).
  - Görevi: bir verinin/mesajın bütünlüğünü ve gönderenin kimliğini imza ile doğrulamak.

Her ikisi de **kafes (lattice)** tabanlı matematiksel problemlerin zorluğuna dayanır.

---

## NEDEN GELDİ — Kuantum Tehdidi ve "Harvest Now, Decrypt Later"

### Kuantum bilgisayar tehdidi
Klasik açık anahtarlı kriptografi (RSA, ECC/ECDSA, Diffie-Hellman) güvenliğini, klasik
bilgisayarlar için zor olan matematik problemlerine (büyük sayı çarpanlarına ayırma,
eliptik eğri ayrık logaritması) dayandırır. Yeterince güçlü, hataya dayanıklı bir
**kuantum bilgisayar**, **Shor algoritması** ile bu problemleri pratikte çözebilir ve
RSA/ECC'yi kırabilir.

> Simetrik şifreleme (AES) ve hash'ler için durum farklıdır: Grover algoritması yalnızca
> "karekök" düzeyinde hızlandırma sağlar; anahtar boyunu büyüterek (örn. AES-256) bu
> tehdit büyük ölçüde telafi edilebilir. Asıl risk **açık anahtarlı** şemalardadır.

### "Harvest Now, Decrypt Later" (Şimdi Topla, Sonra Çöz)
Bir saldırgan **bugün** ağdan şifreli trafiği yakalayıp saklayabilir ve **gelecekte**
yeterince güçlü bir kuantum bilgisayar elde edince çözebilir. Yani:

- Uzun ömürlü gizliliği gereken veriler (sağlık kayıtları, devlet sırları, finansal
  arşivler, kimlik bilgileri) **bugünden** risk altındadır.
- Bu yüzden post-quantum algoritmalara geçiş **bugün** başlamalıdır; "kuantum bilgisayar
  gelince geçeriz" demek geç kalmaktır.

NIST, yıllar süren bir yarışma sonunda **PQC (Post-Quantum Cryptography)** standartlarını
2024'te yayımladı (FIPS 203/204/205). Java 24, bunların ilk ikisini (ML-KEM, ML-DSA)
standart kütüphaneye getirir.

---

## NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR

- **TLS / güvenli iletişim:** Anahtar değişiminde ML-KEM ile kuantum-dirençli ortak gizli.
- **VPN ve güvenli kanallar.**
- **Yazılım/belge imzalama:** ML-DSA ile kuantum-dirençli imza.
- **Uzun ömürlü gizli veriler:** "harvest now, decrypt later" riskine karşı koruma.
- **Standart JCA içinde:** Dış kütüphane (ör. Bouncy Castle) eklemeden, tanıdık
  `KeyPairGenerator`, `KEM`, `Signature` soyutlamalarıyla kullanım.

---

## KAVRAMSAL API KULLANIMI

> Aşağıdaki kod parçaları **kavramsaldır**: amaç, akışı ve hangi sınıfların rol aldığını
> göstermektir. Kesin algoritma adları ve parametreler için JDK 24 belgelerine bakın.

### 1) ML-KEM ile anahtar kapsülleme (shared secret üretimi)

```java
import javax.crypto.KEM;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

// 1. Alici bir ML-KEM anahtar cifti uretir.
KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-KEM"); // örn. "ML-KEM-768"
KeyPair aliciAnahtarCifti = kpg.generateKeyPair();

// 2. Gonderen, alicinin ACIK anahtariyla bir ortak gizli "kapsuller".
KEM kem = KEM.getInstance("ML-KEM");
KEM.Encapsulator enc = kem.newEncapsulator(aliciAnahtarCifti.getPublic());
KEM.Encapsulated kapsul = enc.encapsulate();
byte[] gonderenSecret   = kapsul.key().getEncoded(); // gonderen tarafindaki ortak gizli
byte[] sifreliKapsul    = kapsul.encapsulation();    // alici'ya gonderilecek

// 3. Alici, KENDI gizli anahtariyla kapsulu "acar" ve AYNI ortak gizliye ulasir.
KEM.Decapsulator dec = kem.newDecapsulator(aliciAnahtarCifti.getPrivate());
byte[] aliciSecret = dec.decapsulate(sifreliKapsul).getEncoded();

// gonderenSecret == aliciSecret  -> artik AES gibi simetrik sifrelemede kullanilir.
```

### 2) ML-DSA ile dijital imza

```java
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

// 1. Imzalayan bir ML-DSA anahtar cifti uretir.
KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-DSA"); // örn. "ML-DSA-65"
KeyPair imzaCifti = kpg.generateKeyPair();

byte[] mesaj = "Onayli sozlesme metni".getBytes();

// 2. GIZLI anahtarla imzala.
Signature imzala = Signature.getInstance("ML-DSA");
imzala.initSign(imzaCifti.getPrivate());
imzala.update(mesaj);
byte[] imza = imzala.sign();

// 3. ACIK anahtarla dogrula.
Signature dogrula = Signature.getInstance("ML-DSA");
dogrula.initVerify(imzaCifti.getPublic());
dogrula.update(mesaj);
boolean gecerliMi = dogrula.verify(imza); // true beklenir
```

> Akış, mevcut RSA/ECDSA imza ve KEM API'leriyle **tanıdık** kalır — sadece algoritma
> adı değişir. Bu, geçişi kolaylaştırmak için bilinçli bir tasarım tercihidir.

---

## KLASİK (RSA / ECC) vs POST-QUANTUM (ML-KEM / ML-DSA)

| Boyut | RSA / ECC (klasik) | ML-KEM / ML-DSA (PQC) |
|------|--------------------|------------------------|
| Matematiksel temel | Çarpanlara ayırma / ayrık logaritma | Kafes (lattice) problemleri |
| Kuantum bilgisayara karşı | **Kırılgan** (Shor algoritması) | **Dirençli** (bilinen kuantum saldırısı yok) |
| Anahtar/imza boyutları | Genelde küçük | Genellikle **daha büyük** (bant genişliği/depolama maliyeti) |
| Olgunluk | Onyıllardır kullanımda, çok olgun | Yeni standartlaştı, ekosistem olgunlaşıyor |
| Standart | PKCS, NIST eğri standartları | NIST FIPS 203 (KEM), FIPS 204 (imza) |
| Tipik kullanım | TLS, imza, anahtar değişimi | Aynı senaryolar, kuantum-dirençli |

### Önemli pratik notlar
- **Daha büyük boyutlar:** PQC anahtarları ve imzaları klasik muadillerinden büyük olma
  eğilimindedir; protokol/depolama tasarımında bunu hesaba katmak gerekir.
- **Hibrit yaklaşım önerilir:** Geçiş döneminde yaygın tavsiye, klasik (örn. ECDH) ile
  PQC'yi (ML-KEM) **birlikte** kullanmaktır. Böylece, PQC'de ileride bir zayıflık çıkarsa
  klasik katman, klasik kırılırsa PQC katmanı korur. Ekosistem (TLS, kütüphaneler,
  donanım) PQC'ye tam olgunlaşana dek bu daha güvenli kabul edilir.
- **KEM ≠ doğrudan şifreleme:** ML-KEM doğrudan veri şifrelemez; bir **ortak gizli**
  üretir. Asıl veri, bu gizliyle türetilen simetrik anahtarla (örn. AES-GCM) şifrelenir.

---

## Özet

- **ML-KEM (FIPS 203):** kuantum-dirençli anahtar kapsülleme — güvenli ortak gizli üretir.
- **ML-DSA (FIPS 204):** kuantum-dirençli dijital imza — bütünlük ve kimlik doğrular.
- **Neden şimdi?** Kuantum bilgisayar tehdidi + "harvest now, decrypt later" riski;
  uzun ömürlü gizli veriler bugünden korunmalı.
- **Kolaylık:** Standart JCA içinde, tanıdık `KeyPairGenerator` / `KEM` / `Signature`
  soyutlamalarıyla; dış kütüphaneye gerek yok.
- **Dikkat:** Daha büyük boyutlar; ekosistem henüz olgunlaşıyor; geçişte **hibrit**
  yaklaşım önerilir.
