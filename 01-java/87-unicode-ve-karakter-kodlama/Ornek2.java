// Ornek2: Karakter kodlama (charset) — UTF-8 vs diğerleri ve "mojibake" (bozuk metin).
// Çalıştırma: java Ornek2.java
import java.nio.charset.StandardCharsets;

public class Ornek2 {

    public static void main(String[] args) {
        String metin = "Şçğ İÖ"; // Türkçe karakterler

        // Bir String'i byte'lara çevirmek KODLAMA gerektirir; UTF-8 standarttır.
        byte[] utf8 = metin.getBytes(StandardCharsets.UTF_8);
        byte[] latin1 = metin.getBytes(StandardCharsets.ISO_8859_1); // Türkçe'yi tam karşılamaz

        System.out.println("Metin: " + metin);
        System.out.println("UTF-8 byte sayısı   : " + utf8.length + " (Türkçe harfler 2 byte)");
        System.out.println("ISO-8859-1 byte sayısı: " + latin1.length + " (her harf 1 byte ama bilgi kaybı)");

        // DOĞRU: aynı kodlamayla geri çöz -> orijinali al
        String geriUtf8 = new String(utf8, StandardCharsets.UTF_8);
        System.out.println("\nUTF-8 yaz + UTF-8 oku: '" + geriUtf8 + "' (doğru)");

        // YANLIŞ: UTF-8 yazıp BAŞKA kodlamayla okumak -> MOJIBAKE (bozuk karakterler)
        String bozuk = new String(utf8, StandardCharsets.ISO_8859_1);
        System.out.println("UTF-8 yaz + ISO-8859-1 oku: '" + bozuk + "' (MOJIBAKE - bozuk!)");

        // Sayısal kod (code point) -> karakter
        System.out.println("\nU+20AC = '" + new String(Character.toChars(0x20AC)) + "' (€ avro işareti)");

        System.out.println("""

                --- Karakter kodlama (charset) ---
                Karakterler bellekte kod noktasıdır; DOSYAYA/AĞA giderken byte'lara KODLANIR (charset).
                UTF-8: evrensel standart. ASCII ile uyumlu; Türkçe/emoji dahil her şeyi değişken uzunlukta kodlar.
                  (ASCII 1 byte, Türkçe ~2 byte, emoji ~4 byte.) Web ve dosyalarda VARSAYILAN tercih.
                MOJIBAKE: bir kodlamayla yazıp BAŞKA kodlamayla okumak -> bozuk karakterler (Ã§ gibi).
                KURAL: her zaman kodlamayı AÇIKÇA belirt (getBytes(UTF_8), new String(bytes, UTF_8),
                  Files.readString varsayılan UTF-8). Platform varsayılanına güvenme.""");
    }
}
