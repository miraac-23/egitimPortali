// Ornek3: UDP — hızlı, bağlantısız iletişim (tek JVM'de datagram gönder/al).
// Çalıştırma: java Ornek3.java
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Ornek3 {

    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();

        // Sunucu tarafı: bir UDP soketi aç, bir paket bekle, cevap gönder.
        DatagramSocket sunucu = new DatagramSocket(0); // ephemeral port
        int port = sunucu.getLocalPort();
        System.out.println("UDP sunucusu, port: " + port);

        Thread sunucuThread = new Thread(() -> {
            try {
                byte[] tampon = new byte[1024];
                DatagramPacket gelen = new DatagramPacket(tampon, tampon.length);
                sunucu.receive(gelen); // paket bekle (bloklar)
                String mesaj = new String(gelen.getData(), 0, gelen.getLength());
                System.out.println("  [sunucu] alındı: " + mesaj);

                // Cevabı, paketin geldiği adrese/porta geri gönder.
                byte[] cevap = ("ECHO: " + mesaj).getBytes();
                sunucu.send(new DatagramPacket(cevap, cevap.length, gelen.getAddress(), gelen.getPort()));
            } catch (Exception e) {
                System.out.println("sunucu hatası: " + e.getMessage());
            }
        });
        sunucuThread.start();

        // İstemci tarafı: bir paket gönder, cevabı al.
        try (DatagramSocket istemci = new DatagramSocket()) {
            byte[] veri = "merhaba UDP".getBytes();
            istemci.send(new DatagramPacket(veri, veri.length, loopback, port));

            byte[] tampon = new byte[1024];
            DatagramPacket cevap = new DatagramPacket(tampon, tampon.length);
            istemci.receive(cevap);
            System.out.println("[istemci] cevap: " + new String(cevap.getData(), 0, cevap.getLength()));
        }

        sunucuThread.join();
        sunucu.close();
        System.out.println("""

                --- UDP (User Datagram Protocol) ---
                Bağlantısız, HIZLI ama GÜVENCESİZ: paket sırası/teslimi garanti edilmez.
                Bağlantı kurma (handshake) yok; her paket (datagram) bağımsız gönderilir.
                Kullanım: canlı video/ses, oyunlar, DNS — hızın, az kaybın önemli olmadığı yerler.
                TCP vs UDP: TCP güvenilir+sıralı (web/dosya); UDP hızlı+kayıplı (gerçek zamanlı).""");
    }
}
