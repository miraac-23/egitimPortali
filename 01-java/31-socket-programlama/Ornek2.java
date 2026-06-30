// Ornek2: Çok istemcili sunucu — her bağlantı ayrı thread'de (thread-per-client).
// Bloklayan accept()/read(), birden çok istemciyi aynı anda karşılamak için thread ister.
// Çalıştırma: java Ornek2.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Ornek2 {

    public static void main(String[] args) throws Exception {
        ServerSocket sunucu = new ServerSocket(0);
        int port = sunucu.getLocalPort();
        int istemciSayisi = 3;
        System.out.println("Çok istemcili sunucu, port: " + port);

        // Sunucu döngüsü: her accept'te yeni bir thread başlat -> istemciler paralel hizmet alır.
        Thread kabulDongusu = new Thread(() -> {
            try {
                for (int i = 0; i < istemciSayisi; i++) {
                    Socket c = sunucu.accept();         // bağlantı bekle
                    new Thread(() -> istemciyiHandle(c)).start(); // ayrı thread'de işle
                }
            } catch (IOException ignored) {}
        });
        kabulDongusu.start();

        // 3 istemciyi AYNI ANDA başlat; her biri kendi sayısının karesini ister.
        CountDownLatch bitti = new CountDownLatch(istemciSayisi);
        for (int i = 1; i <= istemciSayisi; i++) {
            final int sayi = i;
            new Thread(() -> {
                try (Socket s = new Socket("localhost", port);
                     PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    out.println(sayi);
                    System.out.println("  istemci-" + sayi + " cevap: " + in.readLine());
                } catch (IOException e) {
                    System.out.println("istemci-" + sayi + " hata: " + e.getMessage());
                } finally { bitti.countDown(); }
            }).start();
        }

        bitti.await();
        sunucu.close();
        System.out.println("""

                --- Çok istemcili sunucu ---
                accept() ve read() BLOKLAR; tek thread'le aynı anda yalnızca bir istemciye hizmet edilir.
                Çözüm: her bağlantıyı ayrı bir thread'de (veya thread havuzunda) işlemek.
                Üretimde 'thread-per-client' yerine genelde thread havuzu (ExecutorService) kullanılır;
                ON BİNLERCE eşzamanlı bağlantı için Java 21 SANAL THREAD'leri idealdir.""");
    }

    static void istemciyiHandle(Socket c) {
        try (c;
             BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
             PrintWriter out = new PrintWriter(c.getOutputStream(), true)) {
            String satir = in.readLine();
            int n = Integer.parseInt(satir.trim());
            out.println(n + "^2 = " + (n * n) + " (thread: " + Thread.currentThread().getName() + ")");
        } catch (Exception e) {
            System.out.println("handle hatası: " + e.getMessage());
        }
    }
}
