// Ornek2: Base64 ile ikili veri, MIME kodlama ve gerçek kullanım (Basic Auth başlığı).
// Çalıştırma: java Ornek2.java
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Ornek2 {

    public static void main(String[] args) {
        // 1) İkili (binary) veriyi kodlama — örn. bir dosyanın baytları.
        byte[] ikili = { (byte) 0x00, (byte) 0xFF, (byte) 0x10, (byte) 0x80, 65, 66, 67 };
        String kodlu = Base64.getEncoder().encodeToString(ikili);
        System.out.println("İkili veri Base64: " + kodlu);
        byte[] geri = Base64.getDecoder().decode(kodlu);
        System.out.println("İlk bayt geri: " + (geri[0] & 0xFF) + ", uzunluk: " + geri.length);

        // 2) MIME kodlama: 76 karakterde bir satır kırar (e-posta/sertifika formatları için).
        byte[] uzun = "Bu çok uzun bir metin; MIME kodlama 76 karakterde bir satır başı koyar. ".repeat(3)
                .getBytes(StandardCharsets.UTF_8);
        String mime = Base64.getMimeEncoder().encodeToString(uzun);
        System.out.println("\nMIME kodlama (satırlara bölünmüş):");
        System.out.println(mime);

        // 3) Gerçek kullanım: HTTP Basic Authorization başlığı = base64("kullanici:parola")
        String kimlik = "ada:parola123";
        String basic = "Basic " + Base64.getEncoder().encodeToString(kimlik.getBytes(StandardCharsets.UTF_8));
        System.out.println("\nAuthorization başlığı: " + basic);
        System.out.println("  (sunucu bunu çözer; bu yüzden HTTPS şart — base64 gizlilik DEĞİL!)");

        System.out.println("""

                --- İkili veri, MIME ve gerçek kullanım ---
                Base64, ikili veriyi (resim, dosya, ham bayt) metin kanallarından geçirmek için kodlar.
                getMimeEncoder(): satırları 76 karakterde böler (e-posta ekleri, PEM sertifikaları).
                Gerçek örnek: HTTP Basic Auth başlığı base64(kullanıcı:parola)'dur — bu yüzden HTTPS zorunludur.
                Boyut: Base64, veriyi ~%33 BÜYÜTÜR (3 bayt -> 4 karakter). Sıkıştırma değildir, kodlamadır.
                Tekrar: Base64 ŞİFRELEME DEĞİLDİR; gizlilik için şifreleme (AES vb.) kullan.""");
    }
}
