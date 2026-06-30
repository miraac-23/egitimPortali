// Ornek1: E-posta gönderme — mesaj oluşturma ve gönderim akışı SİMÜLASYONU.
// (Gerçek e-posta jakarta.mail kütüphanesi + SMTP sunucusu gerektirir; portalda modeli taklit ediyoruz.
//  Gerçek kod README'de.)
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.List;

public class Ornek1 {

    // Gerçek MimeMessage'ın minik karşılığı:
    record Eposta(String kimden, List<String> kime, String konu, String govde, boolean html) {}

    // Gerçek Transport/JavaMailSender'ın yerine geçen sahte gönderici.
    static class SahteSmtp {
        private final List<Eposta> gonderilenler = new ArrayList<>();
        void gonder(Eposta e) {
            // Gerçekte: SMTP sunucusuna bağlan, kimlik doğrula, mesajı ilet.
            if (e.kime().isEmpty()) throw new IllegalArgumentException("alıcı yok");
            gonderilenler.add(e);
            System.out.println("  [SMTP] gönderildi -> " + e.kime() + " | konu: " + e.konu()
                    + " | " + (e.html() ? "HTML" : "düz metin"));
        }
        int gonderilenSayisi() { return gonderilenler.size(); }
    }

    public static void main(String[] args) {
        SahteSmtp smtp = new SahteSmtp();

        // 1) Basit düz metin e-posta
        smtp.gonder(new Eposta("sistem@site.com", List.of("ada@site.com"),
                "Hoş geldiniz", "Kaydınız tamamlandı.", false));

        // 2) Birden çok alıcıya HTML e-posta
        smtp.gonder(new Eposta("sistem@site.com", List.of("ada@site.com", "burak@site.com"),
                "Haftalık bülten", "<h1>Yenilikler</h1><p>...</p>", true));

        // 3) Hata: alıcısız
        try {
            smtp.gonder(new Eposta("x@site.com", List.of(), "boş", "...", false));
        } catch (IllegalArgumentException ex) {
            System.out.println("  hata: " + ex.getMessage());
        }

        System.out.println("\nToplam gönderilen: " + smtp.gonderilenSayisi());

        System.out.println("""

                --- E-posta gönderme (model) ---
                Akış: mesajı oluştur (kimden/kime/konu/gövde) -> bir gönderici (SMTP) ile ilet.
                Gerçekte bir SMTP sunucusuna (Gmail, kurumsal mail, SendGrid...) bağlanılır; kimlik doğrulanır.
                Bu örnek o akışı taklit eder; gerçek kod jakarta.mail (JavaMail) veya Spring'in JavaMailSender'ıdır (README).
                İpucu: e-posta gönderimi YAVAŞ ve hata-eğilimlidir; üretimde ASENKRON (@Async/kuyruk) yapılır.""");
    }
}
