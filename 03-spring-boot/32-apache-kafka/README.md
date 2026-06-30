# Spring Boot ile Apache Kafka

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Kafka, çalışan bir Kafka broker'ı + ek bağımlılık
> gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Mikroservisler birbirine iki şekilde bağlanır: **senkron** (REST çağrısı, topic 16) ve **asenkron**
(mesajlaşma). Asenkron mesajlaşma, servisleri **gevşek bağlı** kılar: gönderen, alıcının o anda
çalışıyor olmasını beklemez; mesajı bir kuyruğa/akışa bırakır, alıcı hazır olunca işler. **Apache
Kafka**, yüksek hacimli olay akışları (event streaming) için en yaygın platformdur; Spring,
**Spring for Apache Kafka** ile bunu kolaylaştırır. (Saga choreography'de — topic 08 — bu mantığı
simüle etmiştik.)

## Kafka kavramları

- **Topic:** Mesajların yayınlandığı isimli kanal (örn. `siparis-olaylari`).
- **Producer:** Topic'e mesaj **yayınlayan** (publish).
- **Consumer:** Topic'ten mesaj **okuyan** (subscribe).
- **Partition:** Topic, paralellik ve ölçek için bölümlere ayrılır.
- **Consumer group:** Aynı gruptaki consumer'lar partition'ları paylaşır (yük dengeleme).
- **Offset:** Bir consumer'ın bir partition'da nereye kadar okuduğu (kalıcı; tekrar okunabilir).

Kafka'nın farkı: mesajlar **kalıcıdır** (bir log gibi saklanır), birden çok tüketici aynı veriyi
bağımsız okuyabilir, çok yüksek hacme ölçeklenir.

## Kurulum

```gradle
implementation 'org.springframework.kafka:spring-kafka'
```

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: siparis-grubu
      auto-offset-reset: earliest
```

## Producer (mesaj yayınlama)

```java
@Service
class SiparisYayinci {
    private final KafkaTemplate<String, String> kafka;
    SiparisYayinci(KafkaTemplate<String, String> kafka) { this.kafka = kafka; }

    void siparisOlustu(String siparisId) {
        kafka.send("siparis-olaylari", siparisId, "OLUSTURULDU");  // topic, key, value
    }
}
```

## Consumer (mesaj dinleme)

```java
@Component
class StokDinleyici {
    @KafkaListener(topics = "siparis-olaylari", groupId = "stok-grubu")
    void dinle(String mesaj) {
        // sipariş olayını işle: stok düş
        System.out.println("Stok servisi olay aldı: " + mesaj);
    }
}
```

Aynı topic'i birden çok servis (farklı `groupId`) bağımsız dinleyebilir: stok düşer, fatura
kesilir, bildirim gönderilir — gönderen hiçbirini bilmez (gevşek bağ).

## Ne zaman Kafka (asenkron) vs REST (senkron)?

| | Senkron (REST) | Asenkron (Kafka) |
|---|----------------|------------------|
| Bağ | Sıkı (alıcı ayakta olmalı) | Gevşek (mesaj beklerde durur) |
| Yanıt | Anında gerekir | Gerekmez ("ateşle ve unut") |
| Kullanım | Sorgu, anlık ihtiyaç | Olay yayını, iş kuyruğu, log/metrik akışı |
| Dayanıklılık | Alıcı çökerse istek kaybolur | Mesaj saklanır, sonra işlenir |

## Önemli desenler

- **Event-driven mimari:** Servisler olaylarla haberleşir (Saga choreography — topic 08).
- **Outbox pattern:** "DB'ye yaz + olay yayınla" atomikliğini sağlamak için (topic 08).
- **Idempotency:** Kafka mesajı tekrar teslim edilebilir; tüketici aynı mesajı iki kez işlese de
  sonuç değişmemeli.
- **Dead Letter Topic:** İşlenemeyen mesajları ayrı bir topic'e gönderip sonra incele.

## Alternatifler

- **RabbitMQ** (Spring AMQP): Geleneksel mesaj kuyruğu; yönlendirme esnek, hacim Kafka'dan düşük.
- **Kafka:** Yüksek hacimli olay akışı, log/stream işleme, olay kaynağı (event sourcing).

## Özet

Asenkron mesajlaşmanın servisleri gevşek bağladığını; Kafka kavramlarını (topic/producer/consumer/
partition/consumer group/offset), Spring Kafka ile producer (`KafkaTemplate`) ve consumer
(`@KafkaListener`) kurulumunu, senkron-asenkron seçimini ve desenleri (event-driven, outbox,
idempotency, DLT) öğrendik; RabbitMQ alternatifine değindik. Sırada, gerçek zamanlı çift yönlü
iletişim: **WebSocket**.
