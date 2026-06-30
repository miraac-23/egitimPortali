# Spring Boot ile Twilio (SMS / İletişim)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Twilio SDK + bir Twilio hesabı (API anahtarı)
> gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Uygulamalar e-posta dışında **SMS**, WhatsApp veya sesli arama da göndermek isteyebilir: doğrulama
kodu (OTP), sipariş bildirimi, iki adımlı kimlik (2FA), uyarılar. **Twilio**, bu iletişim
kanallarını basit bir API ile sunan popüler bir bulut iletişim platformudur. Spring Boot'tan
Twilio'nun Java SDK'sıyla kolayca kullanılır.

## Kurulum ve yapılandırma

```gradle
implementation 'com.twilio.sdk:twilio:10.x.x'
```

```yaml
twilio:
  account-sid: ${TWILIO_SID}        # Twilio panosundan (sır -> ortam değişkeni!)
  auth-token: ${TWILIO_TOKEN}
  from-number: "+1234567890"        # Twilio'dan aldığın gönderici numara
```

SDK başlangıçta kimlikle ilklenir:

```java
@Configuration
class TwilioConfig {
    TwilioConfig(@Value("${twilio.account-sid}") String sid,
                 @Value("${twilio.auth-token}") String token) {
        Twilio.init(sid, token);
    }
}
```

## SMS gönderme

```java
@Service
class SmsServisi {
    @Value("${twilio.from-number}") String gonderen;

    public void smsGonder(String kime, String mesaj) {
        Message.creator(
            new PhoneNumber(kime),          // alıcı: "+90555..."
            new PhoneNumber(gonderen),       // gönderen (Twilio numaran)
            mesaj
        ).create();
    }
}
```

WhatsApp için numara `whatsapp:+90...` biçiminde verilir; sesli arama için `Call.creator(...)`.

## Tipik kullanım: doğrulama kodu (OTP)

```java
public void dogrulamaKoduGonder(String telefon) {
    String kod = String.format("%06d", new java.security.SecureRandom().nextInt(1_000_000));
    kodDeposu.kaydet(telefon, kod, Duration.ofMinutes(5));   // kısa ömürlü sakla
    smsServisi.smsGonder(telefon, "Doğrulama kodunuz: " + kod);
}
```

(Twilio'nun hazır **Verify** API'si OTP üretimi/doğrulamasını kendisi de yapar.)

## Üretim pratikleri

- **Sırları koru:** Account SID / Auth Token ortam değişkeni/secret yöneticisinden gelsin.
- **Asenkron gönder:** SMS gönderimi yavaş ve hata-eğilimlidir; `@Async`/kuyruk ile arka planda
  (topic 05, 32).
- **Maliyet ve hız sınırı:** SMS ücretlidir; kötüye kullanımı (OTP bombardımanı) hız sınırı (rate
  limit) ile engelle.
- **Webhook'lar:** Twilio, teslimat durumu/gelen mesajları senin bir uca (callback URL) bildirir;
  bunları işlemek için bir endpoint kur.
- **Alternatifler:** Amazon SNS, Vonage (Nexmo), MessageBird — benzer modelde SMS/iletişim servisleri.

## Özet

Twilio ile Spring Boot'tan **SMS/WhatsApp/sesli** iletişim göndermeyi öğrendik: SDK kurulumu ve
kimlik (`Twilio.init`), `Message.creator(...)` ile SMS gönderme, doğrulama kodu (OTP) senaryosu;
asenkron gönderim, sır/maliyet/hız-sınırı ve webhook gibi üretim pratikleri. Sırada, bulut platformu
entegrasyonu: **Google Cloud Platform**.
