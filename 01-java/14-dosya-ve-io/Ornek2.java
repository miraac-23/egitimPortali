// Ornek2: Modern NIO.2 — Path ve Files ile kısa dosya işlemleri.
// Çalıştırma: java Ornek2.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) throws IOException {
        Path dosya = Files.createTempFile("ornek-nio-", ".txt");
        System.out.println("Geçici dosya: " + dosya.getFileName());

        // NIO ile yazma: tek satırda tüm listeyi yaz.
        List<String> satirlar = List.of("elma", "armut", "kiraz", "muz");
        Files.write(dosya, satirlar);
        System.out.println("Yazılan satır sayısı: " + satirlar.size());

        // NIO ile okuma: tüm satırları tek seferde listeye al.
        List<String> okunan = Files.readAllLines(dosya);
        System.out.println("Okunan: " + okunan);

        // Sona ekleme (append)
        Files.writeString(dosya, "çilek\n", java.nio.file.StandardOpenOption.APPEND);
        System.out.println("Ekleme sonrası satır sayısı: " + Files.readAllLines(dosya).size());

        // Dosya bilgileri
        System.out.println("\nVar mı?    : " + Files.exists(dosya));
        System.out.println("Boyut (byte): " + Files.size(dosya));

        // Path ile yol işlemleri (dosya oluşturmadan, sadece metin üzerinde)
        Path ornekYol = Path.of("/home/kullanici/belgeler/rapor.pdf");
        System.out.println("\nPath örneği: " + ornekYol);
        System.out.println("  dosya adı : " + ornekYol.getFileName());
        System.out.println("  üst dizin : " + ornekYol.getParent());

        Files.deleteIfExists(dosya);
        System.out.println("\nGeçici dosya silindi.");
    }
}
