// Ornek3: Soket üzerinden NESNE aktarımı — ObjectOutputStream / ObjectInputStream.
// Metin yerine yapılandırılmış Java nesneleri gönderip almak.
// Çalıştırma: java Ornek3.java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Ornek3 {

    // Ağdan geçecek nesneler Serializable olmalı (byte dizisine çevrilebilmeli).
    record SiparisIstegi(int urunId, int adet) implements Serializable {}
    record SiparisCevabi(boolean basarili, String mesaj, double tutar) implements Serializable {}

    public static void main(String[] args) throws Exception {
        ServerSocket sunucu = new ServerSocket(0);
        int port = sunucu.getLocalPort();
        System.out.println("Nesne sunucusu, port: " + port);

        Thread st = new Thread(() -> {
            try (Socket c = sunucu.accept();
                 ObjectInputStream in = new ObjectInputStream(c.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream())) {
                // İstek nesnesini doğrudan oku (deserialize).
                SiparisIstegi istek = (SiparisIstegi) in.readObject();
                System.out.println("  [sunucu] istek: ürün=" + istek.urunId() + ", adet=" + istek.adet());
                double tutar = istek.adet() * 450.0;
                // Cevap nesnesini gönder (serialize).
                out.writeObject(new SiparisCevabi(true, "sipariş alındı", tutar));
            } catch (Exception e) { System.out.println("sunucu: " + e.getMessage()); }
        });
        st.start();

        try (Socket s = new Socket("localhost", port);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(new SiparisIstegi(101, 3));      // nesne gönder
            SiparisCevabi cevap = (SiparisCevabi) in.readObject(); // nesne al
            System.out.println("[istemci] cevap: " + cevap.mesaj() + ", tutar: " + cevap.tutar() + " TL");
        }

        st.join();
        sunucu.close();
        System.out.println("""

                --- Nesne aktarımı (object serialization over socket) ---
                ObjectOutputStream nesneyi byte'lara çevirip (serialize) gönderir; ObjectInputStream
                karşıda nesneye geri çevirir (deserialize). Taraflar yapılandırılmış veriyle konuşur.
                Uyarı: Java serileştirme güvenlik/uyumluluk açısından kırılgandır; servisler arası
                iletişimde günümüzde genelde JSON/Protobuf gibi taşınabilir formatlar tercih edilir.""");
    }
}
