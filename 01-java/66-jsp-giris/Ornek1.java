// Ornek1: JSP'nin "EL" (Expression Language) mantığı — ${...} yer tutucularını modelden doldurma.
// (Gerçek JSP bir konteyner gerektirir; burada JSP'nin temel fikrini taklit ediyoruz. Gerçek JSP README'de.)
// Çalıştırma: java Ornek1.java
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ornek1 {

    // JSP EL'in basit taklidi: şablondaki ${anahtar} ifadelerini model değerleriyle değiştirir.
    static String render(String sablon, Map<String, Object> model) {
        Matcher m = Pattern.compile("\\$\\{(\\w+)\\}").matcher(sablon);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            Object deger = model.getOrDefault(m.group(1), "");
            m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(deger)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        // JSP sayfasının gövdesi gibi bir HTML şablonu (içinde ${} ifadeleri):
        String sablon = """
                <html>
                <body>
                  <h1>Merhaba, ${ad}!</h1>
                  <p>Sepetinizde ${adet} ürün var, toplam ${tutar} TL.</p>
                </body>
                </html>""";

        // Sunucu tarafında model (gerçekte controller/servlet doldurur):
        Map<String, Object> model = Map.of("ad", "Ada", "adet", 3, "tutar", 1250.0);

        String html = render(sablon, model);
        System.out.println("Üretilen HTML:\n" + html);

        System.out.println("""

                --- JSP ve Expression Language (EL) ---
                JSP, HTML içine dinamik veri gömmenin yoludur: ${ad} gibi ifadeler SUNUCUDA modelden doldurulur.
                Sonuç düz HTML olarak tarayıcıya gider; tarayıcı ${} görmez, dolu halini görür.
                Bu örnek o doldurma mantığını taklit eder; gerçek JSP'de bunu konteyner otomatik yapar.""");
    }
}
