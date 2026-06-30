// Ornek3: Modern HttpClient (Java 11) — HttpURLConnection'a temiz, güçlü alternatif.
// Çalıştırma: java Ornek3.java
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class Ornek3 {

    public static void main(String[] args) throws Exception {
        HttpServer sunucu = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        int port = sunucu.getAddress().getPort();
        sunucu.createContext("/api", exchange -> {
            String metot = exchange.getRequestMethod();
            String gelen = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String cevap = "{\"metot\":\"" + metot + "\",\"echo\":\"" + gelen + "\"}";
            byte[] b = cevap.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, b.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(b); }
        });
        sunucu.start();
        String taban = "http://localhost:" + port + "/api";

        // HttpClient bir kez oluşturulur, yeniden kullanılır (HTTP/2, bağlantı havuzu, timeout...).
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // --- GET ---
        HttpRequest get = HttpRequest.newBuilder(URI.create(taban))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> getCevap = client.send(get, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET  -> " + getCevap.statusCode() + " | " + getCevap.body());

        // --- POST ---
        HttpRequest post = HttpRequest.newBuilder(URI.create(taban))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"urun\":\"Mouse\"}"))
                .build();
        HttpResponse<String> postCevap = client.send(post, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST -> " + postCevap.statusCode() + " | " + postCevap.body());

        // --- Asenkron istek (sendAsync + CompletableFuture) ---
        client.sendAsync(get, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(b -> System.out.println("Async GET -> " + b))
                .join();

        sunucu.stop(0);
        System.out.println("""

                --- Modern HttpClient (Java 11+) ---
                HttpURLConnection'a göre çok daha temiz: builder API, HTTP/2, timeout, asenkron (sendAsync).
                HttpClient (bir kez oluştur, paylaş) + HttpRequest (istek) + HttpResponse (yanıt).
                BodyHandlers/BodyPublishers ile gövde okuma/yazma. Yeni projelerde tercih edilen yol budur.
                (Spring tarafında bunun karşılığı RestClient/WebClient/RestTemplate'tir.)""");
    }
}
