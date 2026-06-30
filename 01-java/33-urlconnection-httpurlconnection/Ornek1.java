// Ornek1: HttpURLConnection ile GET — bir HTTP kaynağını okumak.
// İnternet gerekmesin diye, aynı JVM'de yerel bir HTTP sunucusu (JDK'nin HttpServer'ı) başlatıp ona bağlanırız.
// Çalıştırma: java Ornek1.java
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Ornek1 {

    public static void main(String[] args) throws Exception {
        // --- Yerel HTTP sunucusu (gerçek bir web sunucusunu taklit eder) ---
        HttpServer sunucu = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        int port = sunucu.getAddress().getPort();
        sunucu.createContext("/merhaba", exchange -> {
            String cevap = "{\"mesaj\":\"Merhaba HTTP!\"}";
            byte[] govde = cevap.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, govde.length); // 200 OK + içerik uzunluğu
            try (OutputStream os = exchange.getResponseBody()) { os.write(govde); }
        });
        sunucu.start();
        System.out.println("Yerel HTTP sunucusu: http://localhost:" + port + "/merhaba\n");

        // --- HttpURLConnection ile GET isteği ---
        URI uri = URI.create("http://localhost:" + port + "/merhaba");
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json"); // istek başlığı (header)

        int kod = con.getResponseCode();
        System.out.println("Durum kodu     : " + kod + " " + con.getResponseMessage());
        System.out.println("Content-Type   : " + con.getHeaderField("Content-Type"));

        String govde = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Yanıt gövdesi  : " + govde);
        con.disconnect();

        sunucu.stop(0);
        System.out.println("""

                --- HttpURLConnection ile GET ---
                Akış: URI -> URL -> openConnection() -> HttpURLConnection.
                setRequestMethod("GET"), setRequestProperty(...) ile başlık eklenir.
                getResponseCode() durum kodunu, getInputStream() yanıt gövdesini verir.
                Bu, Java'nın klasik HTTP istemcisidir; biraz hantaldır (sonraki örnekte modern HttpClient).""");
    }
}
