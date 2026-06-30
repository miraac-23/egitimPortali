# Java & Spring Eğitim / Araştırma Çalışması

Bu depo, klasik bir PDF rapor yerine **çalıştırılabilir, interaktif bir eğitim portalı** olarak
hazırlanmış kapsamlı bir öğrenme kaynağıdır. Java'dan Spring'e, Spring Boot'tan üretim ve teste
kadar uçtan uca bir yolculuk sunar. Her konu, deneyimli bir eğitmen üslubuyla **akıcı bir
anlatım** (README) ve **çalıştırılabilir gerçek örneklerle** (`Ornek1/2/3.java`) işlenir;
çoğu konu problem→çözüm yaklaşımıyla, gerçek hayat senaryolarıyla anlatılır.

## İçerik (4 ana başlık)

| Klasör | Başlık | Kapsam |
|--------|--------|--------|
| [`01-java/`](01-java/) | **Java** (98 konu) | Temeller, kavramlar (JVM/JDK-JRE-JVM/casting/Unicode/tarih), operatörler, OOP (tüm konular), koleksiyonlar & class references, generics, modern Java (lambda/stream/Optional), I/O, eşzamanlılık, networking, reflection, JDBC, tasarım desenleri, sürüm-sürüm yenilikler (Java 8→25), framework tanıtımları (AWT/Swing/Servlet/JSP), ileri konular (CompletableFuture/GC-JIT/Process API/Base64…) |
| [`02-spring/`](02-spring/) | **Spring** (9 konu) | IoC/DI, bean yaşam döngüsü ve scope, yapılandırma/stereotipler, AOP, SpEL/events/profiles, JdbcTemplate, transaction, validation |
| [`03-spring-boot/`](03-spring-boot/) | **Spring Boot** (45 konu) | İlk uygulama/auto-config, REST API & ara katmanlar (interceptor/filter/CORS), yapılandırma/logging/i18n, Spring Data JPA, validation/exception, Security & JWT/OAuth2, caching/async/scheduling, Actuator/Admin/tracing/OpenAPI, test, mikroservis (Eureka/Gateway/Config), entegrasyon (Kafka/WebSocket/Batch/Flyway/Email/Resilience4j), dağıtım (Docker/WAR/Buildpacks), dağıtık transaction & Saga |
| [`04-spring-vs-spring-boot/`](04-spring-vs-spring-boot/) | **Spring vs Spring Boot** (2 konu) | Temel farklar (aynı iş, iki yöntem), ne zaman hangisi ve geçiş |

Her konu klasörünün yapısı:

```
<konu>/
├── README.md      # Akıcı, eğitmen üslubunda anlatım (problem → çözüm)
├── Ornek1.java    # Çalıştırılabilir örnek
├── Ornek2.java    # (gittikçe derinleşen)
└── Ornek3.java    # gerçek hayat senaryoları
```

## 🖥️ İnteraktif Portal (önerilen kullanım)

Bu çalışmanın en güzel yanı, [`web-app/`](web-app/) altındaki **React + Spring Boot** portalıdır.
Tüm konuları tarayıcıdan gezebilir, READMEleri ve örnek kodları syntax-highlight ile okuyabilir
ve **her örneği "Çalıştır" düğmesiyle backend'de gerçekten çalıştırıp çıktısını anında**
görebilirsin (Spring/Spring Boot örnekleri canlı bir context/sunucu ile koşar).

```bash
# 1) Backend (içerik + kod çalıştırıcı API)
cd web-app/server && ./gradlew bootRun        # http://localhost:8085

# 2) Frontend
cd web-app/client && npm install && npm run dev   # http://localhost:5173
```

Kurulum ve mimari ayrıntıları: [`web-app/README.md`](web-app/README.md).

## Örnekleri doğrudan (portalsız) çalıştırma

**Saf Java örnekleri** (`01-java/*`) tek dosyalık kaynak kodudur, JDK 21 ile derlemeden çalışır:

```bash
java 01-java/12-stream-api/Ornek1.java
```

**Spring / Spring Boot örnekleri** (`02-spring/*`, `03-spring-boot/*`, `04-*`) Spring bağımlılıkları
gerektirir; en kolay yol bunları **portaldaki "Çalıştır"** ile koşmaktır (gerekli classpath ve
çalıştırma ortamı otomatik sağlanır).

## Önerilen okuma sırası

1. **Java** (`01-java/00` → `21`): dilin temelleri, OOP, modern özellikler ve ileri konular.
2. **Spring** (`02-spring/00` → `08`): IoC/DI felsefesinden AOP ve veri/transaction/validation'a.
3. **Spring Boot** (`03-spring-boot/00` → `07`): ilk uygulamadan REST/JPA/güvenlik/üretim/teste.
4. **Spring vs Spring Boot** (`04-*`): ikisini karşılaştır, kararı netleştir.

Her konunun sonundaki "Sırada..." cümlesi seni bir sonraki adıma taşır.

---
*Hazırlık ortamı: JDK 21 (Temurin). Belge dili: Türkçe; kod ve API isimleri İngilizce. Örnekler
problem→çözüm yaklaşımıyla, gerçek hayat senaryolarıyla yazılmıştır.*
