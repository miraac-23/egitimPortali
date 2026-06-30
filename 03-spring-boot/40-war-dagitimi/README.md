# WAR Dağıtımı (Harici Tomcat'e Dağıtım)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Harici bir Tomcat sunucusu gerektirir; portalda
> çalışmaz. Kod/yapı tanıtım amaçlıdır.

Spring Boot'un varsayılan ve önerilen yolu **gömülü sunucudur** (JAR + içinde Tomcat — topic 22):
`java -jar uygulama.jar` ile çalışır. Ancak bazı kurumsal ortamlar, uygulamaların **önceden kurulu,
ayrı bir uygulama sunucusuna** (harici Tomcat, WildFly, WebSphere) **WAR** olarak dağıtılmasını
ister. Spring Boot bunu da destekler. Bu konu, ne zaman ve nasıl WAR dağıtımı yapılacağını ele alır.

## JAR (gömülü) vs WAR (harici)

| | Çalıştırılabilir JAR (gömülü) | WAR (harici sunucu) |
|---|-------------------------------|---------------------|
| Sunucu | Uygulamanın içinde (Tomcat gömülü) | Ayrı, önceden kurulu |
| Çalıştırma | `java -jar app.jar` | Sunucunun `webapps/` dizinine WAR koy |
| Modern tercih | **Evet** (mikroservis, konteyner) | Kurumsal/legacy, paylaşılan sunucu |
| Bağımsızlık | Tek artefakt, taşınabilir | Sunucuya bağımlı |

**Bugünün önerisi gömülü JAR'dır** (özellikle konteyner/Kubernetes ile). WAR'ı yalnızca kurumsal
politika veya mevcut bir uygulama sunucusu altyapısı gerektiriyorsa kullan.

## Spring Boot'u WAR'a dönüştürmek

İki değişiklik gerekir:

### 1) Ana sınıfı SpringBootServletInitializer'dan türet

```java
@SpringBootApplication
public class UygulamaApp extends SpringBootServletInitializer {   // harici sunucu için

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UygulamaApp.class);   // harici sunucu uygulamayı böyle başlatır
    }

    public static void main(String[] args) {          // gömülü çalıştırma için yine durur
        SpringApplication.run(UygulamaApp.class, args);
    }
}
```

### 2) Paketlemeyi WAR yap + gömülü Tomcat'i 'provided' yap

```gradle
plugins { id 'war' }                  // WAR paketle

dependencies {
    // Gömülü Tomcat'i 'provided' yap: WAR'a DAHİL ETME (harici sunucu zaten sağlıyor)
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
}
```

(Maven'de `<packaging>war</packaging>` + Tomcat starter `<scope>provided</scope>`.)

`./gradlew bootWar` (veya `mvn package`) bir `.war` üretir; bunu harici Tomcat'in `webapps/`
dizinine koparsın.

> **Neden 'provided'?** Harici sunucuda zaten bir Tomcat var; gömülü Tomcat'i de WAR'a koyarsan
> çakışma olur. `provided`, "derlemede gerekli ama paketlemede DAHİL ETME" demektir.

## Context path

WAR olarak dağıtınca uygulama, WAR dosya adıyla bir **context path** altında çalışır (örn.
`uygulama.war` → `http://sunucu:8080/uygulama/...`). `ROOT.war` ise kökte çalışır.

## Karar rehberi

- **Gömülü JAR seç:** Yeni projeler, mikroservisler, Docker/Kubernetes, bulut. (Standart.)
- **WAR seç:** Kurumsal politika paylaşılan uygulama sunucusu zorunlu kılıyorsa, veya mevcut bir
  WebSphere/WildFly altyapısına entegre oluyorsan.

## Özet

Spring Boot'un hem gömülü JAR hem harici **WAR** dağıtımını desteklediğini; WAR için iki adımı
(`SpringBootServletInitializer`'dan türetme + WAR paketleme + gömülü Tomcat'i `provided` yapma) ve
context-path davranışını öğrendik; gömülü JAR'ın neden modern varsayılan olduğunu ve WAR'ın hangi
durumlarda gerektiğini gördük. Sırada, sunucu tarafı HTML üretimi: **Thymeleaf**.
