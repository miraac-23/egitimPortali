import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JAVA 18 - UTF-8 VARSAYILAN CHARSET (JEP 400)
 * ============================================
 *
 * Bu dosya, Java 18 ile gelen "UTF-8 by Default" degisikligini gosterir.
 *
 * NEDIR?
 *   Java 17 ve oncesinde, charset belirtilmeden yapilan dosya/akis islemleri
 *   isletim sisteminin "varsayilan charset"ini kullanirdi. Bu deger platforma
 *   ve bolge ayarina (locale) gore degisirdi:
 *     - Windows (Turkce): Cp1254 / windows-1254
 *     - Linux/macOS (modern): UTF-8
 *   Java 18 ile bu varsayilan HER PLATFORMDA UTF-8 oldu.
 *
 * NEDEN ONEMLI (TURKCE ICIN)?
 *   ç, ğ, ş, ü, ö, ı, İ gibi karakterler ASCII disindadir. Yanlis charset ile
 *   okunduklarinda bozulurlar ("Çığlık" -> "Çýglýk"). Java 18 bu sinifi
 *   ortadan kaldirir cunku her ortam ayni (UTF-8) varsayilani kullanir.
 *
 * DERLEME / CALISTIRMA:
 *   javac UTF8Varsayilan.java
 *   java UTF8Varsayilan
 *
 *   (Preview ozellik DEGILDIR; --enable-preview gerektirmez.)
 *
 *   Eski davranisi (geriye uyumluluk) test etmek icin:
 *     java -Dfile.encoding=Cp1254 UTF8Varsayilan
 */
public class UTF8Varsayilan {

    public static void main(String[] args) throws IOException {

        System.out.println("=== JAVA 18: UTF-8 VARSAYILAN CHARSET ===\n");

        // 1) Mevcut varsayilan charset'i goster
        // Java 18+ ile, file.encoding ozellikle ayarlanmadiysa bu UTF-8 doner.
        Charset varsayilan = Charset.defaultCharset();
        System.out.println("Bu JVM'in varsayilan charset'i : " + varsayilan);
        System.out.println("file.encoding sistem ozelligi  : "
                + System.getProperty("file.encoding"));
        System.out.println("native.encoding (OS charset'i) : "
                + System.getProperty("native.encoding")); // Java 18'de eklendi
        System.out.println();

        if (varsayilan.equals(StandardCharsets.UTF_8)) {
            System.out.println("-> Varsayilan UTF-8. Java 18+ tipik davranisi.\n");
        } else {
            System.out.println("-> Varsayilan UTF-8 DEGIL (" + varsayilan
                    + "). Muhtemelen -Dfile.encoding ile eski davranis zorlandi.\n");
        }

        // 2) Turkce icerikli bir dosya yazip okuyalim
        Path dosya = Files.createTempFile("turkce-ornek", ".txt");
        String turkceMetin = "Ürün: Büyük Çığlık — Şehir İstanbul, fiyat 1.299₺";

        // ESKI YONTEM (RISKLI): charset belirtilmeden yazma/okuma.
        // Java 17'de platforma bagliydi; Java 18'de her yerde UTF-8.
        riskliYazVeOku(dosya, turkceMetin);

        // EN IYI PRATIK: charset'i HER ZAMAN acikca belirt (her surumde guvenli)
        guvenliYazVeOku(dosya, turkceMetin);

        // Temizlik
        Files.deleteIfExists(dosya);

        // 3) Ozet ve gecis uyarisi
        ozetVeUyari();
    }

    /**
     * Charset belirtmeden yazip okur.
     * Java 18+ ile artik her platformda UTF-8 oldugu icin guvenlidir,
     * ANCAK eski JVM'lerde veya -Dfile.encoding ile zorlandiginda bozulabilir.
     */
    private static void riskliYazVeOku(Path dosya, String metin) throws IOException {
        System.out.println("--- Yontem 1: Charset BELIRTILMEDEN (varsayilana guvenerek) ---");

        // Charset'siz PrintWriter -> varsayilan charset kullanir
        try (PrintWriter yazici = new PrintWriter(Files.newBufferedWriter(dosya))) {
            yazici.println(metin);
        }

        // Charset'siz okuma -> yine varsayilan charset
        String okunan = Files.readString(dosya).strip();
        System.out.println("Yazilan : " + metin);
        System.out.println("Okunan  : " + okunan);
        System.out.println("Eslesti mi? " + metin.equals(okunan));
        System.out.println();
    }

    /**
     * Charset'i acikca UTF-8 olarak belirtir. Hangi JVM/platform olursa olsun
     * dogru calisir. ONERILEN yontem budur.
     */
    private static void guvenliYazVeOku(Path dosya, String metin) throws IOException {
        System.out.println("--- Yontem 2: Charset ACIKCA UTF-8 (ONERILEN) ---");

        Files.writeString(dosya, metin, StandardCharsets.UTF_8);
        String okunan = Files.readString(dosya, StandardCharsets.UTF_8);

        System.out.println("Yazilan : " + metin);
        System.out.println("Okunan  : " + okunan);
        System.out.println("Eslesti mi? " + metin.equals(okunan));

        // Byte seviyesinde de gosterelim: "ç" UTF-8'de 2 byte (0xC3 0xA7)
        byte[] cBytes = "ç".getBytes(StandardCharsets.UTF_8);
        System.out.print("'c' karakterinin UTF-8 byte'lari: ");
        for (byte b : cBytes) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println("\n");
    }

    private static void ozetVeUyari() {
        System.out.println("=== OZET ===");
        System.out.println("""
                * Java 18+ : varsayilan charset = UTF-8 (her platformda ayni).
                * Avantaj  : 'benim makinemde calisiyor' sinifi charset hatalari biter.
                * Risk     : Eski araclar uretilen dosyalari onceki OS charset'i ile
                             okumaya bel baglamis olabilir. Gecislerde dikkat!
                * Tavsiye  : Charset'i HER ZAMAN acikca belirtin (StandardCharsets.UTF_8).
                             Boylece kod hangi Java surumunde olursa olsun guvenli kalir.
                * Geri uyum: Eski davranisi gecici olarak su sekilde zorlayabilirsiniz:
                             java -Dfile.encoding=Cp1254 SinifAdi
                """);
    }
}
