// Ornek1: Servlet modeli — istek/yanıt ve yol-eşleme (dispatch) SİMÜLASYONU.
// (Gerçek jakarta.servlet API'si bir sunucu konteyneri gerektirir; portal headless olduğu için
//  modeli kendi mini sınıflarımızla taklit ediyoruz. Gerçek kod README'de.)
// Çalıştırma: java Ornek1.java
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Ornek1 {

    // Gerçek HttpServletRequest/Response'un minik karşılıkları:
    record Istek(String metot, String yol, Map<String, String> parametreler) {}
    static class Yanit {
        int durum = 200; String govde = ""; String tip = "text/html";
        void yaz(String s) { govde += s; }
    }

    // Bir "servlet": belirli bir yola gelen isteği işler (gerçekte doGet/doPost).
    interface Servlet extends BiConsumer<Istek, Yanit> {}

    // Mini konteyner: yol -> servlet eşlemesi + dispatch.
    static class Konteyner {
        private final Map<String, Servlet> rotalar = new HashMap<>();
        void ekle(String yol, Servlet s) { rotalar.put(yol, s); }
        Yanit istekGonder(Istek istek) {
            Yanit y = new Yanit();
            Servlet s = rotalar.get(istek.yol());
            if (s == null) { y.durum = 404; y.yaz("404 Bulunamadı: " + istek.yol()); }
            else s.accept(istek, y);   // ilgili servlet'i çalıştır
            return y;
        }
    }

    public static void main(String[] args) {
        Konteyner konteyner = new Konteyner();

        // "/merhaba" servlet'i — gerçekte doGet'in yaptığı iş:
        konteyner.ekle("/merhaba", (istek, yanit) -> {
            String ad = istek.parametreler().getOrDefault("ad", "Misafir");
            yanit.yaz("<h1>Merhaba, " + ad + "!</h1>");
        });
        // "/topla" servlet'i
        konteyner.ekle("/topla", (istek, yanit) -> {
            int a = Integer.parseInt(istek.parametreler().getOrDefault("a", "0"));
            int b = Integer.parseInt(istek.parametreler().getOrDefault("b", "0"));
            yanit.tip = "text/plain";
            yanit.yaz("Sonuç: " + (a + b));
        });

        // İstekleri "gönder" (tarayıcı yerine biz simüle ediyoruz):
        yazdir(konteyner.istekGonder(new Istek("GET", "/merhaba", Map.of("ad", "Ada"))));
        yazdir(konteyner.istekGonder(new Istek("GET", "/topla", Map.of("a", "7", "b", "5"))));
        yazdir(konteyner.istekGonder(new Istek("GET", "/yok", Map.of())));

        System.out.println("""

                --- Servlet modeli ---
                Servlet = sunucuya gelen HTTP isteğini işleyip yanıt üreten Java sınıfı.
                Konteyner (Tomcat/Jetty) isteği DOĞRU servlet'e yönlendirir (URL eşlemesi) ve doGet/doPost'u çağırır.
                İstek: metot + yol + parametreler/başlıklar. Yanıt: durum kodu + gövde + içerik tipi.
                Spring MVC'deki @GetMapping/@RestController, bu servlet modelinin üzerine kurulu üst katmandır.""");
    }

    static void yazdir(Yanit y) {
        System.out.println("[" + y.durum + " | " + y.tip + "] " + y.govde);
    }
}
