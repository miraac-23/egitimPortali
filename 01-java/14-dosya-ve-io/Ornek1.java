// Ornek1: Klasik I/O — BufferedWriter ile yazma, BufferedReader ile okuma.
// Çalıştırma: java Ornek1.java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Ornek1 {

    public static void main(String[] args) throws IOException {
        // Geçici dosya kullanıyoruz; repo klasörünü kirletmemek için.
        Path dosya = Files.createTempFile("ornek-io-", ".txt");
        System.out.println("Geçici dosya: " + dosya);

        // --- Yazma ---
        // try-with-resources: akış blok bitince otomatik kapanır (flush + close).
        try (BufferedWriter yazici = new BufferedWriter(new FileWriter(dosya.toFile()))) {
            yazici.write("Birinci satır");
            yazici.newLine();
            yazici.write("İkinci satır");
            yazici.newLine();
            yazici.write("Üçüncü satır");
            yazici.newLine();
        }
        System.out.println("Dosyaya 3 satır yazıldı.");

        // --- Okuma ---
        System.out.println("\nDosya içeriği:");
        int satirNo = 0;
        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya.toFile()))) {
            String satir;
            while ((satir = okuyucu.readLine()) != null) { // satır kalmayınca null döner
                satirNo++;
                System.out.println("  " + satirNo + ": " + satir);
            }
        }

        // Temizlik
        Files.deleteIfExists(dosya);
        System.out.println("\nGeçici dosya silindi.");
    }
}
