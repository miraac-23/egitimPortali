# JSP'ye Giriş (JavaServer Pages)

**JSP (JavaServer Pages)**, HTML içine dinamik Java verisi gömmenin klasik yoludur. Servlet'le saf
Java içinden HTML yazmak (`out.write("<h1>...")`) zahmetlidir; JSP bunu tersine çevirir: bir HTML
sayfası yazarsın, içine özel etiketlerle dinamik kısımları gömersin. JSP, sunum (view) katmanının
ilk Java teknolojisidir.

> **Not:** Gerçek JSP bir **sunucu konteyneri** (Tomcat) gerektirir; portal headless olduğu için
> JSP'nin temel fikrini (şablon + model → HTML) kendi mini render mantığımızla taklit ediyoruz.
> Gerçek JSP kodu aşağıdadır.

## JSP nasıl çalışır?

Bir JSP sayfası aslında **bir servlet'e dönüştürülür**: konteyner, ilk istekte `.jsp` dosyasını bir
Java servlet sınıfına çevirir (translation), derler ve çalıştırır. Yani JSP, "ters yazılmış bir
servlet"tir — çoğunluğu HTML, araya gömülü dinamik kısımlar.

Sayfa **sunucuda işlenir** ve sonuç **düz HTML** olarak tarayıcıya gider; tarayıcı hiçbir zaman JSP
etiketlerini görmez.

## Expression Language (EL): `${...}`

Dinamik veriyi HTML'e gömmenin modern JSP yolu **EL**'dir: `${ad}` gibi ifadeler sunucuda modelden
doldurulur:

```html
<h1>Merhaba, ${ad}!</h1>
<p>Sepette ${adet} ürün, toplam ${tutar} TL.</p>
```

Örnek 1 (`./Ornek1.java`) bu doldurma mantığını taklit eder: `${anahtar}` ifadelerini bir model
`Map`'inden değerlerle değiştirir.

## JSTL: döngü ve koşul

Saf HTML statiktir; dinamik listeler ve koşullar için JSP, **JSTL** etiket kütüphanesini kullanır:

```html
<table>
  <c:forEach items="${urunler}" var="u">
    <tr><td>${u.ad}</td><td>${u.fiyat} TL</td></tr>
  </c:forEach>
</table>
```

- **`<c:forEach>`**: döngü (liste → satırlar)
- **`<c:if>` / `<c:choose>`**: koşullu gösterim
- **`<c:out>`**: güvenli (XSS'e karşı kaçışlı) yazdırma

Örnek 2 (`./Ornek2.java`) bir ürün listesini HTML tablo satırlarına çeviren `c:forEach` mantığını
Stream ile taklit eder.

## Gerçek JSP + Servlet akışı

Tipik MVC akışı: Servlet (controller) veriyi hazırlar, isteği JSP'ye (view) yönlendirir; JSP HTML
üretir:

```java
// Servlet (controller)
@WebServlet("/urunler")
public class UrunServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ... {
        req.setAttribute("urunler", urunServisi.hepsi());     // modeli koy
        req.getRequestDispatcher("/urunler.jsp").forward(req, resp); // JSP'ye yönlendir
    }
}
```

```html
<!-- urunler.jsp (view) -->
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html><body>
  <table>
    <c:forEach items="${urunler}" var="u">
      <tr><td>${u.ad}</td><td>${u.fiyat}</td></tr>
    </c:forEach>
  </table>
</body></html>
```

> **Eski özellik — scriptlet:** Erken JSP'lerde `<% Java kodu %>` (scriptlet) ile sayfaya doğrudan
> Java gömülürdü. Bu, HTML ile mantığı karıştırdığı için artık **önerilmez**; yerine EL + JSTL
> kullanılır.

## JSP bugün: modern alternatifler

JSP güçlüdür ama günümüzde yeni projelerde azalmıştır. Yaygın alternatifler:

- **Thymeleaf**: Spring Boot'un önerdiği modern şablon motoru (doğal HTML, tasarımcı dostu).
- **REST API + JS frontend**: Backend JSON döner, arayüzü React/Vue/Angular çizer — **bu eğitim
  portalının mimarisi** (Spring Boot API + React).

Yine de "sunucuda şablon + model → HTML" fikri evrenseldir ve her şablon motorunda aynıdır.

## Özet

JSP'nin HTML içine dinamik veri gömen, servlet'e dönüştürülen bir sunum teknolojisi olduğunu;
**EL (`${...}`)** ile model doldurmayı (Örnek 1) ve **JSTL (`c:forEach`)** ile döngü/koşulu (Örnek 2)
öğrendik; gerçek Servlet+JSP MVC akışını, scriptlet'in neden terk edildiğini ve modern
alternatifleri (Thymeleaf, REST+JS) gördük. Bununla **framework tanıtımları bölümü (AWT/Swing/
Servlet/JSP)** tamamlandı.
