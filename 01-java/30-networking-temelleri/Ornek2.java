// Ornek2: TCP — güvenilir, bağlantı tabanlı iletişim (tek JVM'de echo sunucu + istemci).
// Çalıştırma: java Ornek2.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Ornek2 {

    public static void main(String[] args) throws Exception {
        // ServerSocket'i 0 (ephemeral) port ile açarız: işletim sistemi boş bir port atar.
        ServerSocket sunucu = new ServerSocket(0);
        int port = sunucu.getLocalPort();
        System.out.println("TCP echo sunucusu başladı, port: " + port);

        // Sunucu ayrı bir thread'de tek bir istemciyi karşılar (gelen satırı geri yansıtır).
        Thread sunucuThread = new Thread(() -> {
            try (Socket istemci = sunucu.accept(); // bağlantı bekle (bloklar)
                 BufferedReader in = new BufferedReader(new InputStreamReader(istemci.getInputStream()));
                 PrintWriter out = new PrintWriter(istemci.getOutputStream(), true)) {
                String satir;
                while ((satir = in.readLine()) != null) {
                    System.out.println("  [sunucu] alındı: " + satir);
                    out.println("ECHO: " + satir); // geri gönder
                    if (satir.equals("bitir")) break;
                }
            } catch (IOException e) {
                System.out.println("sunucu hatası: " + e.getMessage());
            }
        });
        sunucuThread.start();

        // İstemci: localhost:port'a bağlanır, mesaj gönderir, cevabı okur.
        try (Socket soket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(soket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(soket.getInputStream()))) {
            for (String mesaj : new String[]{"merhaba", "nasılsın", "bitir"}) {
                out.println(mesaj);
                System.out.println("[istemci] gönderdi: " + mesaj + " | cevap: " + in.readLine());
            }
        }

        sunucuThread.join();
        sunucu.close();
        System.out.println("""

                --- TCP (Transmission Control Protocol) ---
                Bağlantı tabanlı, GÜVENİLİR: veri sırayla, kayıpsız ve doğrulanarak ulaşır.
                Akış: ServerSocket.accept() bağlantı bekler; istemci Socket ile bağlanır.
                Her iki taraf giriş/çıkış akışlarıyla (stream) konuşur — tıpkı dosya gibi.
                HTTP, FTP, veritabanı bağlantıları... hepsi TCP üzerinde çalışır.""");
    }
}
