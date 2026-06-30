# Dağıtık Transaction ve Saga Pattern

`@Transactional` ile tek bir veritabanında "ya hepsi ya hiçbiri" garantisini öğrendik. Ama
mikroservis mimarisinde bir iş akışı **birden çok servise ve veritabanına** yayılır: sipariş
servisi, stok servisi, ödeme servisi, kargo servisi — her biri kendi veritabanına sahip. Burada
tek bir `@Transactional` işe yaramaz; bir servisin transaction'ı diğerinin veritabanını kapsayamaz.
Bu bölüm, dağıtık dünyada tutarlılığı nasıl sağladığımızı derinlemesine ele alır.

## Sorun: yerel transaction yetmez

Klasik transaction (ACID), **tek bir kaynak** (veritabanı) içinde güçlü tutarlılık sağlar. Ama
"stok düş + ödeme al + kargo oluştur" adımları farklı servislerdeyse, biri başarılı diğeri
başarısız olabilir ve sistem tutarsız kalır (para alındı ama kargo yok gibi). İki temel yaklaşım
vardır: **2PC (güçlü tutarlılık)** ve **Saga (eventual consistency)**.

## Yol 1: Two-Phase Commit (2PC / XA)

2PC, dağıtık bir "ya hepsi ya hiçbiri" protokolüdür. Bir **koordinatör** iki fazda çalışır:

1. **Prepare (hazırlık):** Tüm katılımcılara "commit'e hazır mısın?" sorulur. Her katılımcı
   kaynaklarını **kilitler** ve oy verir (evet/hayır).
2. **Commit/Abort:** Hepsi "evet" derse koordinatör **commit**, biri "hayır" derse **abort** emri
   verir.

Örnek 1 (`./Ornek1.java`) bunu simüle eder: bir senaryoda hepsi hazır → commit; diğerinde biri
reddeder → hepsi abort.

**Neden mikroservislerde tercih edilmez?**

- ✅ Güçlü, anlık tutarlılık.
- ❌ **Bloklama:** Katılımcılar prepare ile commit arasında kaynakları kilitler; tek bir yavaş
  servis tüm işlemi bekletir.
- ❌ **Koordinatör çökerse** katılımcılar "kararsız" (in-doubt) ve kilitli kalır.
- ❌ Tüm servislerin XA desteği ve sıkı bağ gerektirir; ölçeklenmez, dayanıklılığı düşürür.

Bu nedenle modern dağıtık sistemler genelde 2PC yerine **Saga**'yı seçer.

## Yol 2: Saga Pattern

Saga, dağıtık işlemi **bir dizi yerel transaction'a** böler. Her adım kendi veritabanında
commit eder; bir adım başarısız olursa, daha önce tamamlanan adımlar **telafi (compensating)
transaction**'larıyla geri alınır. Dağıtık kilit yoktur; sonuç **eventual consistency**'dir
(anlık değil, kısa süre sonra tutarlı).

> **Telafi (compensation):** Yapılan bir işlemi mantıksal olarak geri alan iş işlemi. Örneğin
> "ödeme al"ın telafisi "ödeme iade et"tir. Dikkat: telafi gerçek bir "rollback" değildir; ödeme
> zaten commit edilmiştir, sen bir iade işlemi yaparsın.

Saga'nın iki uygulama biçimi vardır:

### a) Orchestration (orkestrasyon) — merkezi koordinatör

Bir **orkestratör** adımları sırayla çağırır ve hata olunca telafileri tetikler:

```
StokRezerve -> Ödeme -> Kargo        (ileri)
   ↑ hata olursa: telafi TERS sırada (Kargo iptal <- Ödeme iade <- Stok iade)
```

Örnek 2 (`./Ornek2.java`) bir sipariş sagasını orkestratörle yürütür: başarılı senaryoda tüm
adımlar commit; kargo adımı başarısız olunca tamamlanan adımlar (ödeme, stok) ters sırada telafi
edilir.

- ✅ Akış tek yerde, net ve izlenebilir.
- ❌ Orkestratör merkezî bir bağımlılık/karmaşıklık noktasıdır.

### b) Choreography (koreografi) — olay tabanlı

Merkezi koordinatör **yoktur**. Her servis ilgili **olayı dinler**, işini yapar ve **yeni bir
olay yayınlar**. Telafi de olaylarla tetiklenir:

```
SiparisOlusturuldu -> StokRezerveEdildi -> OdemeAlindi -> Gonderildi
                              ↑ OdemeReddedildi olayını Stok dinler -> rezervasyonu geri alır
```

Örnek 3 (`./Ornek3.java`) bir olay veriyolu (Kafka/RabbitMQ benzeri) üzerinden bunu simüle eder:
mutlu yolda olaylar zinciri akar; ödeme reddedilince stok servisi telafi yapar.

- ✅ Gevşek bağlılık, tek hata noktası yok, kolay ölçeklenir.
- ❌ Uçtan uca akışı izlemek zordur (olaylar dağınık).

## Saga'yı güvenli kılan yardımcı desenler

- **Outbox Pattern:** "Veritabanına yaz + olay yayınla" iki ayrı sistemdir; biri başarılı diğeri
  başarısız olursa tutarsızlık doğar. Çözüm: olayı, iş verisiyle **aynı yerel transaction'da** bir
  `outbox` tablosuna yaz; ayrı bir süreç bu tablodan okuyup mesaj kuyruğuna güvenle iletir.
- **Idempotency (etkisizlik):** Mesajlar tekrar teslim edilebilir; her işleyici aynı mesajı iki
  kez alsa da sonucu değiştirmemeli (örn. işlenen mesaj id'lerini takip et).
- **TCC (Try-Confirm/Cancel):** Saga'nın bir varyantı; her adım önce "try" (rezervasyon) sonra
  "confirm" veya "cancel" yapar.
- **Eventual consistency'yi tasarıma yansıt:** Kullanıcıya "siparişiniz işleniyor" de; durum
  birkaç saniye/dakika içinde kesinleşir.

## Spring/Java ekosisteminde Saga

Spring'in `@Transactional`'ı **yerel** transaction içindir; dağıtık saga için hazır bir anotasyon
yoktur. Pratikte:

- **Mesajlaşma:** Spring Boot + RabbitMQ/Kafka (Spring for Apache Kafka, Spring AMQP) ile
  choreography kurarsın; `@KafkaListener`/`@RabbitListener` ile olayları dinlersin.
- **Outbox:** Debezium (CDC) veya manuel outbox tablosu.
- **Hazır framework'ler:** Axon Framework, Eventuate Tram Saga, Camunda/Temporal (workflow
  orkestrasyonu) saga'yı yönetmeyi kolaylaştırır.
- **Dayanıklılık:** Resilience4j (retry, circuit breaker) saga adımlarını güvenceye alır.

## Hangisini seçmeli?

| Durum | Öneri |
|-------|-------|
| Tek veritabanı, tek servis | Klasik `@Transactional` (saga'ya gerek yok) |
| Birkaç adımlı, net iş akışı | Saga **Orchestration** |
| Çok sayıda servis, gevşek bağ, yüksek ölçek | Saga **Choreography** |
| Mutlak anlık tutarlılık şart (nadiren) | 2PC/XA (maliyetine katlanarak) |

## Özet

Dağıtık transaction'da neden yerel `@Transactional`'ın yetmediğini; **2PC/XA**'nın güçlü ama
bloklayıcı olduğunu (Örnek 1); **Saga** ile işi yerel transaction'lara bölüp telafi
transaction'larıyla tutarlılığı sağladığımızı — hem **orchestration** (Örnek 2) hem
**choreography** (Örnek 3) biçimiyle — gördük; Outbox, idempotency, TCC gibi yardımcı desenleri ve
Spring/Java ekosistemindeki araçları ele aldık. Dağıtık dünyada hedef "anlık" değil **eventual
consistency**'dir; doğru desen, işin doğasına göre seçilir.
