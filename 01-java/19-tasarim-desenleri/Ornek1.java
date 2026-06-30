// Ornek1: Yaratımsal (creational) desenler — Singleton, Factory Method, Builder.
// Gerçek senaryo: bir bildirim sistemi (e-posta/SMS/push) ve mesaj kurulumu.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // --- SINGLETON: tüm uygulamada tek yapılandırma örneği ---
        Yapilandirma.getInstance().ayarla("gonderen", "destek@site.com");
        System.out.println("Singleton gönderen: " + Yapilandirma.getInstance().oku("gonderen"));

        // --- FACTORY METHOD: tipe göre doğru bildirim nesnesini üret ---
        // Çağıran kod somut sınıfları (EmailBildirim vb.) bilmez; sadece tip söyler.
        System.out.println("\nFactory ile bildirimler:");
        for (String kanal : new String[]{"email", "sms", "push"}) {
            Bildirim b = BildirimFabrikasi.olustur(kanal);
            b.gonder("Siparişiniz kargoda!");
        }

        // --- BUILDER: çok alanlı bir mesajı okunaklı, adım adım kur ---
        System.out.println("\nBuilder ile e-posta:");
        Eposta eposta = new Eposta.Builder("ada@site.com")
                .konu("Hoş geldin")
                .govde("Aramıza katıldığın için teşekkürler.")
                .onemli(true)
                .build();
        System.out.println("  " + eposta);
    }
}

// Singleton: private constructor + tek statik örnek (thread-safe, sınıf yüklenince oluşur).
class Yapilandirma {
    private static final Yapilandirma TEK = new Yapilandirma();
    private final java.util.Map<String, String> degerler = new java.util.HashMap<>();
    private Yapilandirma() {}
    static Yapilandirma getInstance() { return TEK; }
    void ayarla(String k, String v) { degerler.put(k, v); }
    String oku(String k) { return degerler.get(k); }
}

// Factory Method: nesne üretim kararını tek yerde toplar.
interface Bildirim { void gonder(String mesaj); }
class EmailBildirim implements Bildirim { public void gonder(String m) { System.out.println("  [e-posta] " + m); } }
class SmsBildirim implements Bildirim { public void gonder(String m) { System.out.println("  [SMS] " + m); } }
class PushBildirim implements Bildirim { public void gonder(String m) { System.out.println("  [push] " + m); } }

class BildirimFabrikasi {
    static Bildirim olustur(String kanal) {
        return switch (kanal) {
            case "email" -> new EmailBildirim();
            case "sms" -> new SmsBildirim();
            case "push" -> new PushBildirim();
            default -> throw new IllegalArgumentException("Bilinmeyen kanal: " + kanal);
        };
    }
}

// Builder: opsiyonel alanları çok olan nesneyi zincirleme, okunaklı kur.
class Eposta {
    private final String alici, konu, govde;
    private final boolean onemli;
    private Eposta(Builder b) { this.alici = b.alici; this.konu = b.konu; this.govde = b.govde; this.onemli = b.onemli; }
    @Override public String toString() {
        return (onemli ? "[ÖNEMLİ] " : "") + alici + " | " + konu + " | " + govde;
    }
    static class Builder {
        private final String alici;
        private String konu = "(konusuz)";
        private String govde = "";
        private boolean onemli = false;
        Builder(String alici) { this.alici = alici; }
        Builder konu(String k) { this.konu = k; return this; }
        Builder govde(String g) { this.govde = g; return this; }
        Builder onemli(boolean o) { this.onemli = o; return this; }
        Eposta build() { return new Eposta(this); }
    }
}
