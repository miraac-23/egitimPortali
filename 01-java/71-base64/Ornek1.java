// Ornek1: Base64 — metni/veriyi güvenle taşınabilir metne kodlama ve çözme.
// Çalıştırma: java Ornek1.java
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Ornek1 {

    public static void main(String[] args) {
        String orijinal = "Merhaba Dünya! (Türkçe + özel: @#&)";

        // Temel (standart) Base64 kodlama:
        String kodlu = Base64.getEncoder().encodeToString(orijinal.getBytes(StandardCharsets.UTF_8));
        System.out.println("Orijinal: " + orijinal);
        System.out.println("Base64  : " + kodlu);

        // Çözme (decode):
        byte[] cozulen = Base64.getDecoder().decode(kodlu);
        String geri = new String(cozulen, StandardCharsets.UTF_8);
        System.out.println("Çözülen : " + geri);
        System.out.println("Aynı mı?  " + orijinal.equals(geri));

        // URL-güvenli Base64: '+' ve '/' yerine '-' ve '_' (URL/dosya adında sorun çıkarmaz).
        String url = "veri?ad=ada&tip=a/b+c";
        String urlKodlu = Base64.getUrlEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
        System.out.println("\nURL-güvenli Base64: " + urlKodlu);
        System.out.println("  (standart kodlamada '/' ve '+' olurdu; URL'de sorun çıkarırdı)");

        System.out.println("""

                --- Base64 ---
                İkili (binary) veriyi yalnızca güvenli ASCII karakterlerle (A-Z a-z 0-9 + /) temsil eder.
                getEncoder()/getDecoder(): standart. getUrlEncoder(): URL/dosya-güvenli (-_).
                Kullanım: ikili veriyi metin taşıyan yerlerde göndermek — JSON/XML içinde, e-posta ekleri,
                  data URL'leri (data:image/png;base64,...), HTTP Basic Authorization başlığı.
                ÇOK ÖNEMLİ: Base64 ŞİFRELEME DEĞİLDİR! Herkes kolayca çözer; gizlilik SAĞLAMAZ.""");
    }
}
