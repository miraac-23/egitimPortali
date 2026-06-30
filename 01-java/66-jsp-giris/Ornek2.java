// Ornek2: JSP'nin "JSTL c:forEach" döngü mantığı — bir listeyi HTML tablo satırlarına çevirme.
// Çalıştırma: java Ornek2.java
import java.util.List;
import java.util.stream.Collectors;

public class Ornek2 {

    record Urun(String ad, double fiyat, int stok) {}

    public static void main(String[] args) {
        List<Urun> urunler = List.of(
                new Urun("Klavye", 450, 12),
                new Urun("Mouse", 250, 40),
                new Urun("Monitör", 3200, 5));

        // JSTL <c:forEach items="${urunler}" var="u"> ... </c:forEach> mantığının taklidi:
        // her ürün için bir <tr> satırı üret.
        String satirlar = urunler.stream()
                .map(u -> "    <tr><td>%s</td><td>%.2f TL</td><td>%d</td></tr>"
                        .formatted(u.ad(), u.fiyat(), u.stok()))
                .collect(Collectors.joining("\n"));

        String html = """
                <table>
                  <thead><tr><th>Ürün</th><th>Fiyat</th><th>Stok</th></tr></thead>
                  <tbody>
                %s
                  </tbody>
                </table>""".formatted(satirlar);

        System.out.println("Üretilen tablo:\n" + html);

        System.out.println("""

                --- JSP + JSTL (döngü/koşul) ---
                Saf HTML statiktir; dinamik listeleri göstermek için JSP, JSTL etiketlerini kullanır:
                  <c:forEach> (döngü), <c:if>/<c:choose> (koşul), <c:out> (güvenli yazdırma).
                Sunucu, listeyi dolaşıp her eleman için HTML üretir; tarayıcıya hazır tablo gider.
                Bu örnek o üretimi Stream ile taklit eder. Modern projelerde JSP yerine genelde
                Thymeleaf veya bir REST API + JS frontend (bu portal gibi) tercih edilir.""");
    }
}
