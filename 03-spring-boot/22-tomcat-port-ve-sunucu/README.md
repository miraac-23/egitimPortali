# Gömülü Sunucu Yapılandırması (Tomcat Port & Sunucu Ayarları)

Spring Boot'un en büyük kolaylıklarından biri **gömülü web sunucusudur**: uygulaman bir `.jar`
olarak çalışır ve içinde **Tomcat** (varsayılan), Jetty veya Undertow bulunur — ayrıca bir sunucu
kurup `.war` dağıtmana gerek yoktur. `java -jar uygulama.jar` dersin, sunucu da başlar. Bu konu,
gömülü sunucuyu (port, context-path, sunucu ayarları) nasıl yapılandıracağını ele alır.

## Port ve context-path

En sık ayarlananlar `application.properties` ile:

```properties
server.port=9090                       # dinlenecek port (varsayılan 8080)
server.servlet.context-path=/app       # tüm uçlar bu önek altında (örn. /app/api/...)
server.port=0                          # 0 -> rastgele boş port (test için kullanışlı)
```

> **Not:** Bu portal örnekleri **8080**'de çalıştırır (runner böyle sabitler); bu yüzden portu
> değiştirmiyoruz. Örnek 1 (`./Ornek1.java`) bunun yerine **context-path**'i programatik `/app`
> yapar ve uçlara `/app/...` altından erişir.

## Programatik yapılandırma: WebServerFactoryCustomizer

Daha ileri ayarlar için (kod ile) `WebServerFactoryCustomizer` kullanılır:

```java
@Bean
WebServerFactoryCustomizer<ConfigurableWebServerFactory> ozellestir() {
    return factory -> {
        factory.setContextPath("/app");
        factory.setPort(9090);          // (portal'da runner 8080'i zorlar)
    };
}
```

Tomcat'e özel ince ayar için `TomcatServletWebServerFactory` ile bağlantı (connector), thread
havuzu, zaman aşımı gibi parametreler ayarlanır.

## Sık kullanılan sunucu ayarları

```properties
server.tomcat.threads.max=200             # iş parçacığı havuzu
server.tomcat.connection-timeout=20s
server.tomcat.max-connections=8192
server.compression.enabled=true           # yanıt sıkıştırma (gzip)
server.error.include-message=always       # hata yanıtında mesaj
server.servlet.session.timeout=30m
server.shutdown=graceful                  # nazik kapanış (mevcut istekleri bitir)
```

## Sunucuyu değiştirmek: Jetty / Undertow

Varsayılan Tomcat yerine başka bir sunucu kullanabilirsin — `spring-boot-starter-web`'den Tomcat'i
hariç tutup ilgili starter'ı eklersin:

```gradle
implementation('org.springframework.boot:spring-boot-starter-web') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
}
implementation 'org.springframework.boot:spring-boot-starter-jetty'  // veya -undertow
```

## Gömülü sunucu vs harici (WAR) dağıtım

| | Gömülü (jar) | Harici (war) |
|---|--------------|--------------|
| Çalıştırma | `java -jar app.jar` | Bir Tomcat'e `.war` dağıt |
| Sunucu | Uygulamanın içinde | Ayrı kurulu |
| Modern tercih | **Evet** (mikroservis, konteyner) | Eski/kurumsal ortamlar |

Gömülü model, konteyner (Docker) ve mikroservis dünyasının standardıdır: "uygulama = sunucu dahil
tek artefakt". (Harici WAR dağıtımı bir geçiş senaryosu olarak hâlâ desteklenir; ayrı bir konu.)

## Özet

Spring Boot'un gömülü sunucusunu öğrendik: port/context-path yapılandırması, programatik
`WebServerFactoryCustomizer` (Örnek 1 — context-path `/app`), thread/timeout/sıkıştırma gibi sunucu
ayarları ve Tomcat yerine Jetty/Undertow seçimi; gömülü (jar) vs harici (war) dağıtım farkı. Gömülü
sunucu, "tek artefakt" modeliyle modern dağıtımın temelidir. Sırada — sonraki turdan itibaren —
mikroservis ve cloud konuları (Eureka, Gateway, Config, Kafka, Docker…) kavramsal olarak işlenecek.
