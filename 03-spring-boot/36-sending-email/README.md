# Spring Boot ile E-posta Gönderme

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** `spring-boot-starter-mail` + bir SMTP sunucusu
> gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır. (E-postanın genel/saf Java tarafı için
> Java bölümündeki "E-posta Gönderme" konusuna da bakabilirsin.)

Kayıt onayı, parola sıfırlama, bildirim, fatura... Spring Boot, e-posta gönderimini
**`spring-boot-starter-mail`** ve **`JavaMailSender`** ile çok kolaylaştırır: yapılandırmayı
`application.yml`'e yazarsın, gönderici otomatik hazırlanır.

## Kurulum ve yapılandırma

```gradle
implementation 'org.springframework.boot:spring-boot-starter-mail'
```

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USER}          # ortam değişkeninden (sırrı koda gömme!)
    password: ${MAIL_PASSWORD}      # Gmail'de "uygulama parolası"
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

Spring Boot, bu ayarlardan bir **`JavaMailSender`** bean'i otomatik oluşturur.

## Basit metin e-posta

```java
@Service
class EpostaServisi {
    private final JavaMailSender sender;     // Spring enjekte eder
    EpostaServisi(JavaMailSender sender) { this.sender = sender; }

    public void gonder(String kime, String konu, String govde) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(kime);
        m.setSubject(konu);
        m.setText(govde);
        sender.send(m);
    }
}
```

## HTML e-posta ve ekler (MimeMessageHelper)

Zengin içerik (HTML, ek, gömülü görsel) için `MimeMessage` + `MimeMessageHelper`:

```java
public void htmlGonder(String kime, String konu, String html) throws MessagingException {
    MimeMessage mesaj = sender.createMimeMessage();
    MimeMessageHelper h = new MimeMessageHelper(mesaj, true, "UTF-8");  // multipart=true
    h.setTo(kime);
    h.setSubject(konu);
    h.setText(html, true);                                    // true -> HTML
    h.addAttachment("fatura.pdf", new ClassPathResource("fatura.pdf"));
    sender.send(mesaj);
}
```

## Şablonlu e-posta (Thymeleaf)

HTML'i koda gömmek yerine bir şablon motoruyla üret (topiklerde Thymeleaf):

```java
Context ctx = new Context();
ctx.setVariable("ad", "Ada");
String html = templateEngine.process("hosgeldin-email", ctx);  // hosgeldin-email.html şablonu
h.setText(html, true);
```

Böylece tasarım (HTML) ile kod ayrılır; e-posta görünümü kolayca düzenlenir.

## Üretim pratikleri

- **Asenkron gönder:** E-posta gönderimi **yavaştır** (SMTP gidiş-dönüşü). Ana isteği bekletmemek
  için `@Async` (topic 05) veya bir mesaj kuyruğu (Kafka/RabbitMQ — topic 32) ile arka planda
  gönder.
- **Sırları koruma:** SMTP parolası/anahtarı ortam değişkeni veya secret yöneticisinden gelsin;
  `application.yml`'e düz metin yazma.
- **Uygulama parolası / OAuth2:** Gmail gibi sağlayıcılarda normal parola değil, "uygulama
  parolası" ya da OAuth2 gerekir.
- **E-posta servisleri:** Üretimde doğrudan SMTP yerine genelde **SendGrid / Amazon SES / Mailgun**
  kullanılır (teslimat oranı, spam yönetimi, ölçek, geri bildirim).
- **Hata/yeniden deneme:** Gönderim başarısız olabilir; retry + ölü mektup (dead letter) mantığı kur.

## Özet

Spring Boot'ta e-posta gönderimini öğrendik: `spring-boot-starter-mail` + `JavaMailSender`
yapılandırması, basit metin (`SimpleMailMessage`), HTML/ek (`MimeMessageHelper`) ve Thymeleaf
şablonlu e-posta; asenkron gönderim, sır yönetimi ve e-posta servisleri (SES/SendGrid) gibi üretim
pratikleri. Sırada, dağıtık sistemlerde dayanıklılık: **devre kesici (Resilience4j / Hystrix)**.
