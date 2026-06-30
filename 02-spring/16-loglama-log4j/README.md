# Log4J ile Loglama (ve Modern Loglama Manzarası)

Çekirdek Spring turumuzun son durağı **loglama**dır. Her ciddi uygulama, ne olup bittiğini görmek
için log üretir: hangi istek geldi, hangi hata oluştu, sistem ne durumda? Bu konunun klasik adı
"Logging with Log4J"dir; ancak Java'da loglama yıllar içinde evrildi. Bu bölümde hem **Log4J**'i
hem de bugün fiilen standart olan **SLF4J cephesi + Logback/Log4j2** yaklaşımını, çalışan
örneklerle öğreniyoruz.

## Loglama neden `System.out.println`'den iyidir?

`println` ile de "loglayabilirsin" ama bir loglama çatısı sana şunları verir:

- **Seviyeler:** Mesajın önemini ayır (TRACE/DEBUG/INFO/WARN/ERROR) ve üretimde yalnızca önemlileri göster.
- **Yapılandırılabilir çıktı:** Konsol, dosya, döndürülen (rotating) dosya, uzak sunucu — **kodu
  değiştirmeden**.
- **Bağlam:** Zaman damgası, thread, sınıf adı, istek kimliği (MDC) otomatik eklenir.
- **Performans:** Kapalı seviyedeki pahalı mesajlar hiç kurulmaz; asenkron yazım mümkündür.

## Java loglama manzarası: cephe vs motor

Karışıklığın kaynağı şudur: Java'da hem **loglama API'leri (cephe)** hem **motorlar
(implementation)** vardır.

```
   KOD
    │  (loglama çağrısı)
    ▼
  SLF4J  ← CEPHE (facade): kodun buna yazar, motoru bilmez
    │
    ├─► Logback   (motor)  ← Spring Boot'un VARSAYILANI
    └─► Log4j 2   (motor)  ← yüksek başarımlı alternatif
```

| Bileşen | Rolü | Durum |
| --- | --- | --- |
| **Log4j 1.x** | Eski motor | **Kullanılmamalı** (idame/güvenlik yok) |
| **java.util.logging (JUL)** | JDK'nin gömülü motoru | Sınırlı; nadiren tercih edilir |
| **Log4j 2** | Modern, hızlı motor | Aktif; asenkron logger, lazy `{}` |
| **Logback** | Modern motor | **Spring Boot varsayılanı** |
| **SLF4J** | **Cephe** (hepsinin önünde) | Fiili standart; koda bunu yazarsın |
| **Jakarta Commons Logging (JCL)** | Eski cephe | Spring çekirdeği iç loglarında köprüyle kullanır |

**Altın kural:** Uygulama kodun **SLF4J cephesine** yazar; hangi **motorun** çalışacağını yalnızca
**bağımlılık seçerek** belirlersin. Böylece motoru değiştirmek (Logback → Log4j2) tek satır
bağımlılık değişikliğidir; iş koduna dokunmazsın.

## SLF4J cephesi (modern standart) — `Ornek1`

`Ornek1.java`, bugün yazman gereken biçimi gösterir:

```java
private static final Logger log = LoggerFactory.getLogger(Ornek1.class); // sınıf-başına logger
log.info("Giriş denemesi: kullanıcı={}, deneme={}", kullanici, deneme);  // parametreli ({})
log.error("Sayı ayrıştırılamadı: '{}'", girdi, e);                       // son arg Throwable -> stack trace
```

İki kritik alışkanlık:
- **Parametreli loglama (`{}`):** String'i `+` ile birleştirme. `{}` ile, seviye kapalıysa mesaj
  **hiç kurulmaz** (performans) ve kod okunur kalır.
- **İstisnayı son argüman ver:** `log.error("mesaj", e)` — Spring/SLF4J yığın izini (stack trace)
  otomatik basar.

Bu ortamda SLF4J → **Logback**'e bağlıdır; çıktıyı Logback üretir. Spring'in **kendi iç logları**
da aynı cepheden akar, bu yüzden tüm log akışı tek tiptir.

## Log4J 2 API'siyle loglama — `Ornek2`

Konunun adını hakkıyla karşılamak için `Ornek2.java`, doğrudan **Log4j 2**'nin kendi API'siyle
(`org.apache.logging.log4j`) log yazar. Kullanım, SLF4J'e çok benzer (aynı seviyeler, aynı `{}`
biçimi) — yalnızca paket farklıdır. **Not:** Bu portalda `log4j-to-slf4j` köprüsü olduğu için Log4j 2
çağrıları SLF4J üzerinden Logback'e yönlendirilir; çıktıyı yine Logback üretir. Üretimde
`log4j-core` ekleyerek Log4j2'yi **asıl motor** olarak da çalıştırabilirsin.

## Pratik loglama: seviye koruması ve MDC — `Ornek3`

`Ornek3.java` gerçekçi bir Spring servisinde loglamayı gösterir:

- **Seviye koruması:** `if (log.isDebugEnabled()) { ... }` — pahalı bir log mesajını, seviye kapalıyken
  boşuna hesaplama. (Parametreli `{}` çoğu durumu zaten halleder; bu, mesajı kurmanın kendisi pahalıysa
  gereklidir.)
- **MDC (Mapped Diagnostic Context):** `MDC.put("istekId", "REQ-7AF3")` ile o thread'deki **tüm**
  loglara bağlam iliştirirsin. Dağıtık sistemlerde bir isteği uçtan uca izlemenin anahtarıdır.
  `finally` içinde `MDC.clear()` ile mutlaka temizle (thread havuzunda başka isteğe sızmasın).

> **Not:** MDC değerinin log satırında **görünmesi** için, çıktı biçiminde (pattern) `%X{istekId}`
> bulunmalıdır. Portalın varsayılan Logback biçimi bunu içermediğinden örnekte MDC etiketi satırda
> basılmaz; ama bağlam doğru biçimde set/clear edilir. Gerçek projede `logback.xml` pattern'ine
> `%X{istekId}` eklersin.

## Yapılandırma kod değil, dosyadır

Loglamanın gücü, davranışının **koddan ayrı** olmasıdır. Seviyeyi, çıktı hedefini ve biçimi bir
yapılandırma dosyası belirler:

- **Logback:** `logback.xml` (Spring Boot'ta `application.properties` ile de: `logging.level.com.ornek=DEBUG`)
- **Log4j 2:** `log4j2.xml`
- **Log4j 1.x (eski):** `log4j.properties`

Örnek bir Logback yapılandırması (kavramsal):

```xml
<configuration>
  <appender name="KONSOL" class="ch.qos.logback.core.ConsoleAppender">
    <encoder><pattern>%d{HH:mm:ss} [%thread] %-5level %logger{36} %X{istekId} - %msg%n</pattern></encoder>
  </appender>
  <root level="INFO"><appender-ref ref="KONSOL"/></root>
  <logger name="com.ornek.servis" level="DEBUG"/>  <!-- belirli paketi ayrıntılı logla -->
</configuration>
```

## Sık yapılan hatalar

- **`System.out.println` ile loglamak:** Seviye, hedef ve bağlam kontrolünü kaybedersin; üretimde kapatamazsın.
- **String birleştirme:** `log.debug("x=" + pahaliHesap())` — debug kapalı olsa bile `pahaliHesap()`
  çalışır. `log.debug("x={}", ...)` kullan.
- **Çift köprü / çatışan motor:** Hem Logback hem Log4j2-core'u aynı anda eklemek "birden çok SLF4J
  binding" uyarısı verir. Tek motor + uygun köprüler kuralı.
- **Log4j 1.x kullanmak:** Terk edilmiştir; Log4j 2 veya Logback'e geç.
- **MDC'yi temizlememek:** Thread havuzunda eski bağlam sonraki isteğe sızar; `finally`'de `clear()`.

## Özet ve çekirdeğin sonu

Java loglama manzarasını netleştirdik: koda **SLF4J cephesine** yaz, motoru (**Logback**/**Log4j2**)
bağımlılıkla seç; **Log4j 1.x**'i terk et. Seviyeler, parametreli loglama (`{}`), istisna loglama,
seviye koruması ve **MDC** ile bağlamsal loglamayı çalışan örneklerle gördük; davranışın koddan
değil **yapılandırmadan** geldiğini vurguladık.

Bununla **Spring çekirdeği** turunu tamamladık: IoC/DI'den bean yaşam döngüsüne, post-processor'lardan
auto-wiring'e, AOP'den veri erişimi, transaction, web MVC ve loglamaya kadar. Artık tüm bunların
üretim uygulamalarına nasıl dönüştüğünü görmeye hazırsın — sırada **Spring Boot** bölümü var.
