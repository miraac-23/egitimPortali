# Spring Web MVC Framework

Çekirdeği (IoC/DI, bean'ler, AOP) öğrendik. Şimdi Spring'in **web katmanına** geçiyoruz. Spring
Web MVC, HTTP isteklerini işleyip yanıt üreten olgun bir çatıdır. Adındaki **MVC**, klasik
**Model-View-Controller** desenine işaret eder: **Controller** isteği karşılar, **Model** veriyi
taşır, **View** sonucu (HTML/JSON) üretir. Bu bölümde MVC'nin mimarisini ve bir isteğin baştan
sona yolculuğunu, **çalışan bir örnekle** inceliyoruz.

> **Önemli not (portal):** Web MVC bir **servlet container** (Tomcat gibi) gerektirir; düz tek-dosya
> Java ile çalışmaz. Bu yüzden `Ornek1.java` minimal bir **Spring Boot** uygulamasıdır — Boot,
> gömülü Tomcat'i ve DispatcherServlet'i bizim için kurar. Klasik (Boot'suz) Spring MVC'de bu kurulum
> elle yapılırdı; aşağıda her iki dünyayı da göreceğiz. Web tarafının tamamını **Spring Boot**
> bölümünde derinleştiriyoruz; burada **MVC çatısının çekirdek mimarisini** kavrıyoruz.

## Front Controller: DispatcherServlet

Spring MVC'nin kalbinde **`DispatcherServlet`** vardır. Bu, "Front Controller" tasarım desenidir:
uygulamaya gelen **tüm** HTTP istekleri önce tek bir merkezi servlet'ten geçer; o da isteği uygun
işleyiciye (handler) yönlendirir. Bir isteğin tipik yolculuğu:

```
HTTP İstek
   │
   ▼
DispatcherServlet  (Front Controller — tek giriş kapısı)
   │   1) HandlerMapping: hangi @Controller metodu bu URL'i karşılar?
   │   2) HandlerAdapter: metodu çağırır, parametreleri bağlar (@PathVariable, @RequestParam...)
   │   3) Controller iş mantığını çalıştırır, bir sonuç (Model/nesne/ResponseEntity) döndürür
   │   4a) @RestController ise -> HttpMessageConverter sonucu JSON'a çevirir  ─┐
   │   4b) @Controller ise     -> ViewResolver view'ı (JSP/Thymeleaf) bulur, HTML üretir
   ▼
HTTP Yanıt
```

`Ornek1.java`, bu akışı canlı gösterir: üç farklı endpoint'e istek atılır ve her birinin nasıl
bağlandığı/yanıtlandığı çıktıda görülür.

## İstek bağlama (request binding)

Controller metoduna gelen verileri farklı kaynaklardan **otomatik bağlarsın**:

| Anotasyon | Nereden bağlar | Örnek |
| --- | --- | --- |
| `@PathVariable` | URL yolundaki değişken | `GET /urunler/42` → `id=42` |
| `@RequestParam` | Sorgu parametresi | `?kelime=klavye&sayfa=2` |
| `@RequestBody` | İstek gövdesi (JSON) | POST/PUT JSON → nesne |
| `@RequestHeader` | HTTP başlığı | `Authorization`, `Accept`... |

`@RequestParam`'a `defaultValue` verilebilir (parametre yoksa kullanılır). Dönüş tarafında
**`ResponseEntity`** ile yalnızca gövdeyi değil **HTTP durum kodunu** ve başlıkları da kontrol
edersin (`Ornek1`'de `POST` 201 Created döndürür).

## @Controller vs @RestController

| | `@Controller` | `@RestController` |
| --- | --- | --- |
| Dönüş değeri | **View adı** (HTML üretilir) | **Veri** (JSON/XML'e çevrilir) |
| Tipik kullanım | Sunucu-render HTML sayfalar | REST API'ler |
| Eşdeğeri | — | `@Controller` + `@ResponseBody` |

`@RestController`, her metoda `@ResponseBody` eklemenin kısayoludur: dönen nesne, `HttpMessageConverter`
(genelde Jackson) ile **doğrudan yanıt gövdesine** (JSON) yazılır. Klasik `@Controller` ise bir **view
adı** döndürür; `ViewResolver` bu adı bir şablona (JSP, Thymeleaf) çözer ve HTML üretir.

## Klasik kurulum vs Spring Boot

**Klasik Spring MVC** (Boot'suz) kurulumu elle yapardın:

```java
// web.xml yerine Java tabanlı kurulum (Servlet 3.0+):
public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    protected Class<?>[] getRootConfigClasses()    { return new Class[]{ KokConfig.class }; }
    protected Class<?>[] getServletConfigClasses() { return new Class[]{ WebConfig.class }; }
    protected String[] getServletMappings()        { return new String[]{ "/" }; }
}

@Configuration @EnableWebMvc @ComponentScan("com.ornek.web")
class WebConfig { /* ViewResolver, interceptor, converter tanımları */ }
```

Yani: bir `WAR` üret, harici bir Tomcat'e dağıt, `DispatcherServlet`'i ve `@EnableWebMvc`'yi elle
yapılandır. **Spring Boot** bunların hepsini ortadan kaldırır: `spring-boot-starter-web` bağımlılığı,
gömülü Tomcat'i ve DispatcherServlet'i otomatik kurar; `main` metodundan `JAR` olarak çalışır.
`Ornek1` bu yüzden birkaç satırda ayağa kalkıyor.

## Sık yapılan hatalar

- **`@Controller` ile JSON beklemek:** View adı döndürür; JSON için `@RestController` ya da
  `@ResponseBody` kullan. (Klasik tuzak: metot "home" döndürür, Spring "home.jsp" arar ve 404 verir.)
- **`@PathVariable` adı uyuşmazlığı:** `@GetMapping("/{id}")` ile `@PathVariable Long urunId`
  yazarsan ad tutmaz; ya adı eşle ya `@PathVariable("id")` de.
- **`@RequestBody` için `Content-Type` eksikliği:** JSON gövde gönderirken `Content-Type:
  application/json` yoksa converter devreye girmez.
- **`@EnableWebMvc`'yi Boot'ta kullanmak:** Boot zaten MVC'yi otomatik yapılandırır; `@EnableWebMvc`
  eklemek bu otomatik yapılandırmayı **kapatır** ve şaşırtıcı davranışlara yol açar.

## Özet

Spring Web MVC'nin mimarisini öğrendik: merkezde **DispatcherServlet** (Front Controller), istekleri
**HandlerMapping** ile controller'lara yönlendirme, parametreleri `@PathVariable`/`@RequestParam`/
`@RequestBody` ile bağlama, `ResponseEntity` ile durum kodu kontrolü ve `@Controller` (view) ile
`@RestController` (JSON) ayrımı. Klasik kurulumun (WAR + harici Tomcat + `@EnableWebMvc`) Spring
Boot'ta nasıl sıfıra indiğini de gördük. Web tarafının tamamını (REST, validation, exception
handling, güvenlik) **Spring Boot** bölümünde derinleştiriyoruz. Sırada, son çekirdek konusu:
**Log4J ile loglama**.
