# Spring Boot Servlet Filter

**Filter**, Java web dünyasının (Servlet API'si) en eski ara katman mekanizmasıdır (topic 65). Bir
isteği, daha Spring MVC'ye (DispatcherServlet) ulaşmadan **en dışta** karşılar ve yanıt çıkarken son
olarak görür. Spring Boot, filter'ları kolayca kaydetmeni sağlar. Bu konu, filter'ları ve
interceptor'dan (topic 11) ne zaman ayrıldığını ele alır.

## Filter: doFilter ve zincir

Bir `Filter`, tek metodu `doFilter` ile isteği sarmalar ve **zinciri ilerletir**:

```java
class IzlemeFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        // ÖNCE: istek henüz işlenmedi
        chain.doFilter(req, res);   // zinciri ilerlet (sonraki filter -> DispatcherServlet -> controller)
        // SONRA: yanıt hazır
    }
}
```

`chain.doFilter(...)` çağrısının **öncesi** isteğe, **sonrası** yanıta karşılık gelir. Çağırmazsan
istek hiç işlenmez (isteği burada kesebilirsin). Birden çok filter bir **zincir** oluşturur.

Örnek 1 (`./Ornek1.java`) her isteğe bir **izleme kimliği** (trace id) başlığı ekleyen ve süreyi
loglayan bir filter kurar; self-test bu başlığı yanıtta okur.

## Filter'ı kaydetmek

Spring Boot'ta birkaç yol var:

```java
// 1) FilterRegistrationBean — yol/sıra üzerinde tam kontrol (önerilen)
@Bean FilterRegistrationBean<IzlemeFilter> f() {
    var reg = new FilterRegistrationBean<>(new IzlemeFilter());
    reg.addUrlPatterns("/*");
    reg.setOrder(1);
    return reg;
}

// 2) @Component olarak işaretle — otomatik tüm yollara kaydedilir (basit ama kontrol az)
@Component class IzlemeFilter implements Filter { ... }
```

`FilterRegistrationBean`, **hangi yollara** (`addUrlPatterns`) ve **hangi sırayla** (`setOrder`)
uygulanacağını belirlemeni sağlar.

## Filter vs Interceptor

İkisi de "araya girer" ama farklı katmanlarda:

| | Filter (Servlet) | Interceptor (Spring MVC) |
|---|------------------|--------------------------|
| Seviye | Konteyner — **en dışta** | DispatcherServlet sonrası (MVC içinde) |
| Kapsam | **Her şey** (statik dosyalar, hata sayfaları dahil) | Yalnızca controller'a giden istekler |
| Erişim | Ham `ServletRequest`/`Response` | Handler (controller metodu), model |
| Tipik | CORS, sıkıştırma, kimlik, kodlama, trace id | MVC yetki, model ekleme, süre ölçme |

Sıra: **Filter → DispatcherServlet → Interceptor → Controller** (ve dönüşte ters). Genel/düşük
seviyeli işler filter'a, MVC-bilgisi gereken işler interceptor'a gider.

## Tipik kullanım alanları

- **Trace id / korelasyon kimliği** ekleme (dağıtık loglama).
- **CORS** (genelde Spring'in CORS desteği veya bir CorsFilter ile — sonraki konu).
- **Sıkıştırma**, karakter kodlama, güvenlik başlıkları.
- **Kimlik doğrulama** (Spring Security büyük ölçüde filter zincirine dayanır!).

> **Not:** Spring Security tamamen bir **filter zinciri** üzerine kuruludur (topic 04). Yani her
> güvenli istek, sen yazmasan da bir dizi filter'dan geçer.

## Özet

Servlet Filter'ın isteği en dışta sarmalayan ara katman olduğunu; `doFilter` + zincir mantığını,
Spring Boot'ta `FilterRegistrationBean` ile kaydı (Örnek 1); Filter↔Interceptor farkını (seviye,
kapsam, erişim) ve tipik kullanımları (trace id, CORS, güvenlik) öğrendik. Sırada, tarayıcı
güvenliğinin önemli bir konusu: **CORS desteği**.
