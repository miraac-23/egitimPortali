// Ornek2: ProcessBuilder — bir dış komut çalıştırıp çıktısını okumak.
// Çalıştırma: java Ornek2.java
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Ornek2 {

    public static void main(String[] args) throws Exception {
        // ProcessBuilder ile dış bir komut çalıştır. (Burada taşınabilir, zararsız bir komut.)
        // Unix'te /bin/echo; çıktıyı ve hata akışını birleştir.
        ProcessBuilder pb = new ProcessBuilder("/bin/echo", "Merhaba ProcessBuilder!");
        pb.redirectErrorStream(true); // stderr -> stdout (tek akışta oku)

        System.out.println("Komut çalıştırılıyor: " + pb.command());
        Process surec = pb.start();

        // Sürecin çıktısını oku:
        try (BufferedReader r = new BufferedReader(new InputStreamReader(surec.getInputStream()))) {
            String satir;
            while ((satir = r.readLine()) != null) {
                System.out.println("  çıktı: " + satir);
            }
        }

        // Bitmesini bekle ve çıkış kodunu al:
        int kod = surec.waitFor();
        System.out.println("Çıkış kodu: " + kod + " (0 = başarılı)");

        System.out.println("""

                --- ProcessBuilder ---
                Dış programları/komutları Java'dan çalıştırmanın yoludur (eski Runtime.exec'in halefi).
                Akış: new ProcessBuilder(komut, arg...).start() -> Process.
                getInputStream/getOutputStream/getErrorStream ile süreçle veri alışverişi.
                redirectErrorStream(true): hata akışını çıktıyla birleştirir. waitFor(): bitmesini bekle + çıkış kodu.
                directory()/environment() ile çalışma dizini ve ortam değişkenleri ayarlanır.
                GÜVENLİK: komutu kullanıcı girdisinden kurma (komut enjeksiyonu); argümanları ayrı ayrı ver.""");
    }
}
