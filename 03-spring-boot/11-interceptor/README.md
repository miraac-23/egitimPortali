# Spring Boot Interceptor (HandlerInterceptor)

Birçok istekte tekrar eden işler vardır: loglama, süre ölçme, kimlik/yetki kontrolü, istek
sayacı, dil ayarı. Bunları her controller metoduna tek tek yazmak hem tekrar hem hata kaynağıdır.
**Interceptor**, Spring MVC seviyesinde isteğin **önüne ve arkasına** girerek bu ortak işleri tek
yerde toplar. Servlet Filter'a benzer ama Spring MVC'ye özeldir (sonraki konuda farkı göreceğiz).

## HandlerInterceptor: üç kanca

`HandlerInterceptor` üç metot sunar:

```java
class ZamanlamaInterceptor implements HandlerInterceptor {
    boolean preHandle(req, res, handler)   { ... return true; }  // controller'dan ÖNCE
    void postHandle(req, res, handler, mav) { ... }              // controller SONRASI, view öncesi
    void afterCompletion(req, res, handler, ex) { ... }          // her şey bitince (hata dahil)
}
```

- **`preHandle`**: Controller metodu çağrılmadan **önce**. `false` dönerse istek controller'a
  **hiç ulaşmaz** (örn. yetki yoksa burada kes). İstek başlangıç zamanını koymak, kimlik kontrolü.
- **`postHandle`**: Controller başarıyla döndükten sonra, yanıt render edilmeden önce.
- **`afterCompletion`**: İstek tamamen bittiğinde (başarı veya hata) — kaynak temizleme, süre/durum
  loglama.

Örnek 1 (`./Ornek1.java`) her isteğin süresini ve durum kodunu ölçen bir interceptor kurar.

## Interceptor'ı kaydetmek

Bir `WebMvcConfigurer` ile interceptor'ı ekler ve **hangi yollara** uygulanacağını belirlersin:

```java
@Bean WebMvcConfigurer config() {
    return new WebMvcConfigurer() {
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new ZamanlamaInterceptor())
                    .addPathPatterns("/api/**")        // bu yollara uygula
                    .excludePathPatterns("/api/health"); // bunları hariç tut
        }
    };
}
```

## Interceptor vs Filter

İkisi de "araya girer" ama farklı seviyelerde (sonraki konu Filter'ı ayrıntılı ele alır):

| | Interceptor | Filter (Servlet) |
|---|-------------|------------------|
| Seviye | Spring MVC (DispatcherServlet sonrası) | Servlet konteyneri (daha önce/dışta) |
| Erişim | Handler (controller metodu) bilgisine erişir | Yalnızca ham request/response |
| Kullanım | MVC'ye özgü: yetki, loglama, model ekleme | Genel: CORS, sıkıştırma, kimlik, kodlama |
| Spring bean | Doğal (Spring yönetir) | Bean olabilir ama servlet seviyesinde |

Genel kural: Spring MVC bağlamına (handler, model) ihtiyaç varsa **interceptor**; tüm isteklerde
ham seviyede genel bir iş varsa **filter**.

## Tipik kullanım alanları

- İstek/yanıt loglama ve süre ölçme (gözlemlenebilirlik).
- Kimlik/yetki kontrolü (`preHandle`'da `false` ile erken kesme).
- İstek sayacı / hız sınırlama (rate limiting).
- Ortak model/başlık ekleme, dil (locale) belirleme.

## Özet

Interceptor'ın Spring MVC seviyesinde isteğin önüne/arkasına girdiğini; üç kancasını (`preHandle`
ile erken kesme, `postHandle`, `afterCompletion`) ve `WebMvcConfigurer` ile yol-bazlı kaydı (Örnek 1);
Filter ile farkını ve tipik kullanımları öğrendik. Sırada, daha düşük seviyede (servlet) araya giren
yapı: **Servlet Filter**.
