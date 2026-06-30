// Ornek1: İstek-cevap protokolü — basit bir "hesap makinesi" TCP sunucusu.
// İstemci "a op b" gönderir, sunucu hesaplayıp sonucu döner. (Tek JVM.)
// Çalıştırma: java Ornek1.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Ornek1 {

    public static void main(String[] args) throws Exception {
        ServerSocket sunucu = new ServerSocket(0);
        int port = sunucu.getLocalPort();
        System.out.println("Hesap sunucusu, port: " + port);

        // Sunucu: her satırı "a op b" olarak ayrıştırıp sonucu geri yazar (basit bir PROTOKOL).
        Thread st = new Thread(() -> {
            try (Socket c = sunucu.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                 PrintWriter out = new PrintWriter(c.getOutputStream(), true)) {
                String satir;
                while ((satir = in.readLine()) != null) {
                    if (satir.equals("KAPAT")) break;
                    out.println(hesapla(satir));
                }
            } catch (IOException e) { System.out.println("sunucu: " + e.getMessage()); }
        });
        st.start();

        // İstemci: birkaç istek gönderir, cevapları okur.
        try (Socket s = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            for (String istek : new String[]{"5 + 3", "10 * 4", "20 / 0", "9 - 12", "KAPAT"}) {
                if (istek.equals("KAPAT")) { out.println(istek); break; }
                out.println(istek);
                System.out.println("  " + istek + "  ->  " + in.readLine());
            }
        }

        st.join();
        sunucu.close();
        System.out.println("""

                --- İstek-cevap protokolü ---
                Soketin üstünde KENDİ metin protokolünü tanımladık: "sayı op sayı" -> sonuç.
                Sunucu satır satır okur (readLine), işler, cevabı yazar (println). İstemci tersini yapar.
                HTTP de aslında bunun gelişmişidir: metin tabanlı istek/cevap satırları.""");
    }

    static String hesapla(String ifade) {
        try {
            String[] p = ifade.split(" ");
            double a = Double.parseDouble(p[0]), b = Double.parseDouble(p[2]);
            return switch (p[1]) {
                case "+" -> "= " + (a + b);
                case "-" -> "= " + (a - b);
                case "*" -> "= " + (a * b);
                case "/" -> b == 0 ? "HATA: sıfıra bölme" : "= " + (a / b);
                default -> "HATA: bilinmeyen işlem";
            };
        } catch (Exception e) { return "HATA: geçersiz ifade"; }
    }
}
