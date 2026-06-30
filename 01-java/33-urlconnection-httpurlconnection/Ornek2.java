// Ornek2: HttpURLConnection ile POST — sunucuya veri göndermek.
// Yerel sunucu gelen gövdeyi geri yansıtır (echo). (İnternet gerekmez.)
// Çalıştırma: java Ornek2.java
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Ornek2 {

    public static void main(String[] args) throws Exception {
        HttpServer sunucu = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        int port = sunucu.getAddress().getPort();
        // POST handler: gelen gövdeyi okuyup "alındı: ..." olarak geri döner.
        sunucu.createContext("/siparis", exchange -> {
            String gelen = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String cevap = "{\"durum\":\"alindi\",\"echo\":" + gelen + "}";
            byte[] govde = cevap.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(201, govde.length); // 201 Created
            try (OutputStream os = exchange.getResponseBody()) { os.write(govde); }
        });
        sunucu.start();

        URI uri = URI.create("http://localhost:" + port + "/siparis");
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setDoOutput(true); // gövde göndereceğiz -> çıkış akışını aç

        String istekGovdesi = "{\"urun\":\"Klavye\",\"adet\":2}";
        try (OutputStream os = con.getOutputStream()) {
            os.write(istekGovdesi.getBytes(StandardCharsets.UTF_8)); // gövdeyi yaz
        }

        System.out.println("Gönderilen: " + istekGovdesi);
        System.out.println("Durum kodu: " + con.getResponseCode() + " " + con.getResponseMessage());
        String cevap = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Yanıt     : " + cevap);
        con.disconnect();
        sunucu.stop(0);

        System.out.println("""

                --- HttpURLConnection ile POST ---
                setDoOutput(true) ile gövde gönderimi açılır; getOutputStream()'e istek gövdesi yazılır.
                Content-Type başlığı, gönderdiğin verinin türünü belirtir (burada JSON).
                Sunucu 201 Created döndürür ve gövdemizi yansıtır.
                PUT/DELETE için setRequestMethod("PUT"/"DELETE") kullanılır.""");
    }
}
