# Spring Boot WebSocket (Gerçek Zamanlı İletişim)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** WebSocket starter + bir tarayıcı/istemci
> gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Klasik HTTP **istek-yanıt** modelidir: istemci sorar, sunucu yanıtlar, bağlantı kapanır. Ama bazı
uygulamalar **gerçek zamanlı, çift yönlü** iletişim ister: sohbet, canlı bildirim, oyun, borsa
fiyatları, işbirlikli editör. Sunucunun, istemci sormadan **anında veri itmesi (push)** gerekir.
**WebSocket**, tarayıcı ile sunucu arasında kalıcı, çift yönlü bir kanal açar; Spring Boot bunu
destekler.

## HTTP vs WebSocket

| | HTTP | WebSocket |
|---|------|-----------|
| Model | İstek-yanıt | Çift yönlü, sürekli açık kanal |
| Başlatan | Hep istemci | Her iki taraf da mesaj gönderebilir |
| Bağlantı | Her istekte aç-kapa | Bir kez açılır, açık kalır |
| Kullanım | CRUD, sayfa yükleme | Sohbet, canlı bildirim, oyun, borsa |

WebSocket bağlantısı bir HTTP **upgrade** isteğiyle başlar (`ws://` / `wss://`), sonra kanal açık
kalır.

## Kurulum

```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
```

## STOMP + SockJS (yaygın yaklaşım)

Ham WebSocket yerine genelde **STOMP** (mesaj protokolü) + mesaj broker kullanılır — yayınla/abone
ol (pub/sub) modeli:

```java
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    public void registerStompEndpoints(StompEndpointRegistry r) {
        r.addEndpoint("/ws").withSockJS();        // bağlantı noktası
    }
    public void configureMessageBroker(MessageBrokerRegistry r) {
        r.enableSimpleBroker("/konu");            // sunucu -> istemci yayını (abone olunan)
        r.setApplicationDestinationPrefixes("/uygulama");  // istemci -> sunucu
    }
}
```

```java
@Controller
class SohbetController {
    @MessageMapping("/sohbet")          // istemci /uygulama/sohbet'e gönderince
    @SendTo("/konu/mesajlar")           // /konu/mesajlar'a abone HERKESE yayınla
    public Mesaj gonder(Mesaj m) {
        return new Mesaj(m.kullanici(), m.metin());
    }
}
```

İstemci (tarayıcı), `/konu/mesajlar`'a abone olur; biri mesaj gönderince **tüm aboneler** anında
alır.

## Sunucudan istemciye itme (push)

`SimpMessagingTemplate` ile sunucu, istemci sormadan istediği an mesaj iter:

```java
@Service
class BildirimServisi {
    private final SimpMessagingTemplate messaging;
    BildirimServisi(SimpMessagingTemplate m) { this.messaging = m; }

    void bildirimGonder(String kullanici, String mesaj) {
        messaging.convertAndSendToUser(kullanici, "/konu/bildirim", mesaj);  // belirli kullanıcıya
    }
}
```

## Güvenlik ve ölçek

- **Kimlik:** WebSocket handshake sırasında kimlik doğrula (Spring Security + WebSocket).
- **Ölçek:** Birden çok sunucu örneğinde, basit broker yerine **harici broker** (RabbitMQ/ActiveMQ
  STOMP) kullan — böylece bir örneğe bağlı kullanıcıya başka örnekten mesaj iletilebilir.
- **Alternatif (SSE):** Yalnızca sunucu→istemci tek yön gerekiyorsa (canlı akış, bildirim),
  **Server-Sent Events** daha basittir (`text/event-stream`, tek yönlü).

## Özet

WebSocket'in gerçek zamanlı, çift yönlü iletişim için kalıcı bir kanal açtığını; HTTP ile farkını;
Spring Boot kurulumunu, **STOMP + pub/sub** modeliyle (`@MessageMapping`/`@SendTo`) sohbet/yayın ve
`SimpMessagingTemplate` ile sunucudan itme (push) yapmayı öğrendik; güvenlik, ölçek (harici broker)
ve tek-yön alternatifi (SSE) konularına değindik. Sırada, büyük hacimli toplu işleme: **Batch
Service**.
