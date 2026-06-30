# Servlet'e Giriş

**Servlet**, sunucuya gelen bir HTTP isteğini işleyip yanıt üreten bir Java sınıfıdır. Java'nın
web teknolojilerinin **temelidir**: bugün kullandığın Spring MVC, JSF ve neredeyse tüm Java web
çatıları, perde arkasında servlet API'sinin üzerine kuruludur. Servlet'leri anlamak, modern
çatıların "sihrini" görmeni sağlar.

> **Not:** Gerçek servlet API'si (`jakarta.servlet`) bir **sunucu konteyneri** (Tomcat, Jetty)
> gerektirir; portal headless olduğu için burada servlet **modelini** kendi mini sınıflarımızla
> taklit ediyoruz. Gerçek servlet kodu aşağıda gösterilir.

## Servlet modeli: istek → işleme → yanıt

Bir web sunucusunun temel döngüsü şudur:

1. Tarayıcı bir HTTP isteği gönderir (metot + yol + parametreler/başlıklar).
2. **Konteyner** (Tomcat) isteği, URL eşlemesine göre **doğru servlet'e** yönlendirir.
3. Servlet, isteği işler (`doGet`/`doPost`) ve bir yanıt yazar (durum kodu + gövde + içerik tipi).

Örnek 1 (`./Ornek1.java`) bunu simüle eder: bir "konteyner" yol→servlet eşlemesi tutar; gelen
isteği ilgili servlet'e yönlendirir; bulunmazsa 404 döner. `/merhaba` ve `/topla` "servlet"leri
parametre okuyup yanıt üretir.

## Servlet yaşam döngüsü ve filtreler

Konteyner her servlet'i belirli bir yaşam döngüsüyle yönetir:

- **`init()`**: Servlet ilk kez yüklenirken **bir kez** (kaynak hazırlama).
- **`service()` → `doGet`/`doPost`**: **Her istekte** (asıl iş).
- **`destroy()`**: Uygulama kapanırken **bir kez** (temizlik).

Ayrıca **filtreler (Filter)** vardır: istek servlet'e ulaşmadan (veya yanıt dönmeden) araya giren
ara katmanlar — loglama, kimlik doğrulama, sıkıştırma, CORS. Bir **zincir** halinde çalışırlar.
Örnek 2 (`./Ornek2.java`) yaşam döngüsünü ve bir filtre zincirini (log → kimlik → servlet) simüle
eder.

## Gerçek servlet kodu (Tomcat/Jetty ile)

```java
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/merhaba")                       // URL eşlemesi
public class MerhabaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ad = req.getParameter("ad");    // ?ad=Ada
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write("<h1>Merhaba, " + (ad != null ? ad : "Misafir") + "!</h1>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) { /* ... */ }
}
```

Bu sınıf bir `.war` olarak paketlenip Tomcat'e dağıtılır; Tomcat `/merhaba` isteklerini bu
servlet'e yönlendirir.

## Servlet'ten Spring'e

Servlet API'si güçlüdür ama düşük seviyelidir (parametre ayrıştırma, JSON dönüşümü, hata yönetimi
elle). Spring MVC bunu sarmalar:

```java
@RestController
public class MerhabaController {
    @GetMapping("/merhaba")
    public String merhaba(@RequestParam(defaultValue="Misafir") String ad) {
        return "Merhaba, " + ad + "!";
    }
}
```

Spring'in `DispatcherServlet`'i aslında **tek bir servlet'tir**; tüm istekleri alır ve
`@GetMapping`/`@PostMapping` ile işaretlenmiş metotlara yönlendirir. Yani Spring MVC, servlet
modelinin üzerine kurulu konforlu bir katmandır (bu portalın backend'i de böyle çalışır).

## Özet

Servlet'in sunucuya gelen HTTP isteğini işleyen Java sınıfı olduğunu; **istek→işleme→yanıt**
modelini ve konteynerin yönlendirmesini (Örnek 1); **yaşam döngüsünü** (init/service/destroy) ve
**filtreleri** (Örnek 2) öğrendik; gerçek `HttpServlet`/`@WebServlet` kodunu ve bunun Spring MVC ile
ilişkisini gördük. Sırada, servlet'lerin içine HTML gömmeyi kolaylaştıran sunum teknolojisi: **JSP**.
