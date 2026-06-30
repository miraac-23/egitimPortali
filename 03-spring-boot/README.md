# Spring Boot

Spring Framework bölümünde çekirdeği (IoC/DI, AOP, veri erişimi, transaction, validation)
öğrendik. **Spring Boot**, tüm bunları üretim hızında bir araya getirir: otomatik yapılandırma,
starter bağımlılıkları ve gömülü sunucu ile birkaç satırda çalışan, dağıtıma hazır uygulamalar
yazarsın. Bu bölüm, öğrendiğin her şeyin gerçek bir web uygulamasında nasıl birleştiğini gösterir.

Bu bölümün örnekleri **gerçek Spring Boot uygulamalarıdır**: portal onları gömülü Tomcat ile
başlatır, açılışta kendi endpoint'lerini çağırıp çıktı üretirler, ardından sunucu açık kaldığı
için otomatik durdurulur. Yani gördüğün çıktı, canlı çalışan bir uygulamadan gelir.

## Konu haritası

- `00-spring-boot-nedir-ve-ilk-uygulama` — Spring vs Boot, auto-configuration, @SpringBootApplication, ilk REST + JSON
- `01-rest-api-gelistirme` — tam CRUD, ResponseEntity, HTTP durum kodları, filtreleme
- `02-spring-data-jpa` — @Entity, JpaRepository, türetilmiş sorgular, @Query, ilişkiler
- `03-validation-ve-exception-handling` — @Valid, @RestControllerAdvice, ProblemDetail
- `04-spring-security` — kimlik doğrulama (BCrypt/Basic), rol bazlı yetkilendirme, JWT
- `05-caching-async-scheduling` — @Cacheable, @Async, @Scheduled
- `06-actuator-ve-uretim` — Actuator, özel health/metrik, OpenAPI/Docker/mikroservis kavramları
- `07-test` — JUnit 5/AssertJ, Mockito, MockMvc; @SpringBootTest/@WebMvcTest/@DataJpaTest
- `08-dagitik-transaction-ve-saga` — 2PC/XA, Saga (orchestration & choreography), Outbox, TCC

## Önerilen sıra

Numaralar önerilen okuma sırasıdır. `00`'dan başla; her konunun sonundaki "Sırada..." cümlesi
seni bir sonraki adıma taşır.
