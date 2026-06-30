# E-posta Gönderme

Birçok uygulama e-posta gönderir: kayıt onayı, parola sıfırlama, bildirim, fatura. Java'da bu,
standart kütüphanede **değil**, **Jakarta Mail** (eski adıyla JavaMail) kütüphanesiyle yapılır;
Spring kullanıyorsan **`JavaMailSender`** bunu daha da kolaylaştırır. Bu konu, e-posta gönderim
akışını ve gerçek kodu ele alır.

> **Not:** Gerçek e-posta gönderimi bir **SMTP sunucusu** (Gmail, kurumsal mail, SendGrid...) ve
> harici bir kütüphane gerektirir; portalda bunu çalıştıramayız. Bu yüzden örnek, **akışı taklit
> eder**; gerçek kod aşağıdadır.

## E-posta gönderim akışı

1. **Mesajı oluştur:** Kimden, kime (bir veya çok alıcı, CC/BCC), konu, gövde (düz metin veya
   HTML), varsa ekler.
2. **Bir göndericiyle ilet:** Bir SMTP sunucusuna bağlan, kimlik doğrula, mesajı gönder.

Örnek 1 (`./Ornek1.java`) bu akışı sahte bir SMTP göndericisiyle simüle eder (düz metin, HTML, çok
alıcı, hata durumu).

## Gerçek kod 1: Jakarta Mail (saf Java)

```java
// build.gradle: implementation 'org.eclipse.angus:angus-mail:2.0.x'  (Jakarta Mail uygulaması)
Properties props = new Properties();
props.put("mail.smtp.host", "smtp.gmail.com");
props.put("mail.smtp.port", "587");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");

Session session = Session.getInstance(props, new Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("kullanici", "uygulama-parolasi");
    }
});

Message mesaj = new MimeMessage(session);
mesaj.setFrom(new InternetAddress("ben@site.com"));
mesaj.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ada@site.com"));
mesaj.setSubject("Merhaba");
mesaj.setText("E-posta gövdesi");          // HTML için: setContent(html, "text/html; charset=utf-8")
Transport.send(mesaj);
```

## Gerçek kod 2: Spring Boot (JavaMailSender)

Spring Boot, e-posta gönderimini çok kolaylaştırır:

```yaml
# application.yml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: kullanici
    password: uygulama-parolasi
    properties.mail.smtp.starttls.enable: true
```

```java
@Service
public class EpostaServisi {
    private final JavaMailSender sender;   // Spring otomatik sağlar
    public EpostaServisi(JavaMailSender sender) { this.sender = sender; }

    public void gonder(String kime, String konu, String govde) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(kime); m.setSubject(konu); m.setText(govde);
        sender.send(m);                    // HTML/ek için MimeMessageHelper kullan
    }
}
```

## Önemli pratikler

- **Asenkron gönder:** E-posta gönderimi **yavaş** ve hata-eğilimlidir; ana isteği bekletmemek için
  `@Async` veya bir mesaj kuyruğu (RabbitMQ/Kafka) ile arka planda gönder.
- **Uygulama parolası / API anahtarı:** Gmail gibi sağlayıcılarda normal parola değil, **uygulama
  parolası** veya OAuth gerekir. Sırları koda gömme — ortam değişkeni/secret yöneticisi kullan.
- **Şablonlar:** HTML e-postaları Thymeleaf/Freemarker şablonlarıyla üret.
- **Teslimat:** Üretimde doğrudan SMTP yerine genelde **e-posta servisleri** (SendGrid, Amazon SES,
  Mailgun) kullanılır — teslimat oranı, spam yönetimi ve ölçeklenme için.

## Özet

E-posta gönderim akışını (mesaj oluştur → SMTP ile ilet) simülasyonla (Örnek 1) ve gerçek kodu —
hem saf **Jakarta Mail** hem Spring'in **`JavaMailSender`**'ı — öğrendik; asenkron gönderim, sır
yönetimi ve e-posta servisleri gibi üretim pratiklerine değindik. Bu, standart kütüphane dışı ama
çok yaygın bir ihtiyaçtır.
