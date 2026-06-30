import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ============================================================================
 *  JAVA 11 - Files.readString / Files.writeString - JEP 330 ile birlikte gelen
 *  pratik dosya I/O metotlari
 * ============================================================================
 *
 *  NEDIR?
 *  ------
 *  Java 11, java.nio.file.Files sinifina iki cok kullanisli metot ekledi:
 *    - Files.readString(Path)      -> dosyanin TUM icerigini tek String'e okur
 *    - Files.writeString(Path, CharSequence) -> bir String'i dosyaya yazar
 *  Her ikisinin de Charset alan asiri yuklemeleri (overload) vardir.
 *  Varsayilan charset UTF-8'dir.
 *
 *  NEDEN GELDI? (Hangi problem?)
 *  -----------------------------
 *  Bir dosyanin tum icerigini tek bir String olarak okumak/yazmak gunluk
 *  hayatta cok sik yapilan bir is. Ama Java'da bunu yapmanin "tek satirlik"
 *  basit bir yolu yoktu. Insanlar ya:
 *    - BufferedReader ile satir satir okur (cok kod),
 *    - Files.readAllBytes(path) + new String(bytes, UTF_8) yazar (charset
 *      hatasi yapmaya acik),
 *    - Files.lines(...).collect(joining("\n")) yazar (satir sonu kaybi),
 *    - ya da Apache Commons IO / Guava gibi 3. parti kutuphane kullanirdi.
 *
 *  NE ISE YARAR / NEREDE KOLAYLIK SAGLAR?
 *  --------------------------------------
 *    - Konfigurasyon dosyalari (.properties, .json, .yaml) okuma
 *    - Template (sablon) dosyalari okuyup icini doldurma
 *    - Kucuk metin dosyalari, SQL script'leri, HTML parcalari okuma
 *    - Log/rapor yazma, ciktiyi dosyaya kaydetme
 *
 *  GERCEK HAYAT ORNEGI:
 *  --------------------
 *    Bir uygulama, e-posta sablonunu "email_template.html" dosyasindan okuyup
 *    icindeki {isim} gibi yer tutuculari degistirip gonderir. Tek satirda
 *    String oku, replace et, gonder. Eskiden bu 10+ satirdi.
 *
 *  DIKKAT (RISK): readString tum dosyayi BELLEGE alir. Cok buyuk dosyalarda
 *  (orn. yuzlerce MB) OutOfMemoryError riski vardir. Buyuk dosyalar icin hala
 *  BufferedReader / Files.lines (stream) tercih edilmelidir.
 * ============================================================================
 */
public class FilesReadWriteString {

    public static void main(String[] args) {
        try {
            // ================================================================
            // Gecici bir dosya olusturuyoruz (ornek gercekten calissin diye)
            // ================================================================
            Path geciciDosya = Files.createTempFile("java11-ornek", ".txt");
            System.out.println("Gecici dosya olusturuldu: " + geciciDosya);

            String icerik = "Merhaba Java 11!\n"
                    + "Bu dosya Files.writeString ile yazildi.\n"
                    + "Turkce karakterler: cgiosu CGIOSU\n"
                    + "Ucuncu satir.";

            // ================================================================
            // 1) YENI YONTEM: Files.writeString ile yazma (UTF-8)
            // ================================================================
            // Charset belirtmek istersek StandardCharsets.UTF_8 verebiliriz.
            Files.writeString(geciciDosya, icerik, StandardCharsets.UTF_8);
            System.out.println("\n>>> Dosyaya yazildi (Files.writeString).");

            // ================================================================
            // 2) YENI YONTEM: Files.readString ile okuma (UTF-8)
            // ================================================================
            String okunan = Files.readString(geciciDosya, StandardCharsets.UTF_8);
            System.out.println("\n>>> Dosyadan okunan icerik (Files.readString):");
            System.out.println("------------------------------------------------");
            System.out.println(okunan);
            System.out.println("------------------------------------------------");
            System.out.println("Okunan karakter sayisi: " + okunan.length());

            // ================================================================
            // 3) Java 11 lines() ile birlikte kullanim (BONUS)
            // ================================================================
            System.out.println("\n>>> Satir satir isleme (String.lines()):");
            okunan.lines()
                  .filter(satir -> !satir.isBlank())
                  .forEach(satir -> System.out.println("   Satir: " + satir));

            // Temizlik: gecici dosyayi sil
            Files.deleteIfExists(geciciDosya);
            System.out.println("\nGecici dosya silindi.");

        } catch (IOException e) {
            System.out.println("Dosya islemi sirasinda hata: " + e.getMessage());
        }
    }
}

/*
 * ============================================================================
 *  ESKI vs YENI KARSILASTIRMA
 * ============================================================================
 *
 *  --- ESKI YONTEM 1: Files.readAllBytes + new String ---
 *  byte[] bytes = Files.readAllBytes(path);
 *  String icerik = new String(bytes, StandardCharsets.UTF_8);
 *  // Charset unutursan platform varsayilani kullanilir -> Turkce bozulur!
 *
 *  --- ESKI YONTEM 2: BufferedReader ile satir satir ---
 *  StringBuilder sb = new StringBuilder();
 *  try (BufferedReader br = Files.newBufferedReader(path, UTF_8)) {
 *      String line;
 *      while ((line = br.readLine()) != null) {
 *          sb.append(line).append(System.lineSeparator());
 *      }
 *  }
 *  String icerik = sb.toString();   // ~7 satir + try-with-resources
 *
 *  --- ESKI YONTEM 3: Files.lines (stream) ---
 *  String icerik;
 *  try (Stream<String> s = Files.lines(path, UTF_8)) {
 *      icerik = s.collect(Collectors.joining("\n"));
 *      // Sondaki satir sonu kaybolur, orijinal birebir korunmaz
 *  }
 *
 *  --- ESKI YAZMA YONTEMI ---
 *  try (BufferedWriter bw = Files.newBufferedWriter(path, UTF_8)) {
 *      bw.write(icerik);
 *  }
 *
 *  --- YENI YONTEM (Java 11) ---
 *  String icerik = Files.readString(path);              // OKUMA - tek satir
 *  Files.writeString(path, icerik);                     // YAZMA - tek satir
 *
 *  OZET TABLO:
 *  | Islem | Eski (satir sayisi) | Yeni Java 11 |
 *  |-------|---------------------|--------------|
 *  | Okuma | 5-7 satir           | 1 satir      |
 *  | Yazma | 3-4 satir           | 1 satir      |
 *  | Charset hata riski | Yuksek | Dusuk (UTF-8 varsayilan) |
 * ============================================================================
 */
