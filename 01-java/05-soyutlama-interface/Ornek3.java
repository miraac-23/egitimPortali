// Ornek3: Abstract sınıf ve interface'i birlikte kullanan gerçekçi senaryo.
// Bir bildirim sistemi: farklı kanallar (e-posta, SMS) ortak bir arayüzü uygular.
// Çalıştırma: java Ornek3.java
import java.util.List;

public class Ornek3 {

    public static void main(String[] args) {
        // Tüm kanalları tek bir tip (Bildirimci) üzerinden yönetiyoruz.
        List<Bildirimci> kanallar = List.of(
                new EmailBildirimci("destek@site.com"),
                new SmsBildirimci("+90555")
        );

        String mesaj = "Siparişiniz kargoya verildi.";
        for (Bildirimci k : kanallar) {
            k.gonder(mesaj); // log + asıl gönderim (Template Method benzeri akış)
        }
    }
}

// Interface: bir kanalın "gönderebilmesi" sözleşmesi.
interface Bildirimci {
    void gonder(String mesaj);
}

// Abstract sınıf: ortak iskeleti (loglama + biçimlendirme) sağlar,
// asıl gönderimi alt sınıfa bırakır.
abstract class TemelBildirimci implements Bildirimci {
    protected final String hedef;
    protected TemelBildirimci(String hedef) { this.hedef = hedef; }

    @Override
    public final void gonder(String mesaj) {
        System.out.println("[LOG] " + kanalAdi() + " gönderimi hazırlanıyor...");
        ilet(bicimlendir(mesaj));
    }

    protected String bicimlendir(String mesaj) {
        return mesaj.trim();
    }

    // Alt sınıfların doldurması gereken parçalar:
    protected abstract String kanalAdi();
    protected abstract void ilet(String mesaj);
}

class EmailBildirimci extends TemelBildirimci {
    EmailBildirimci(String adres) { super(adres); }
    @Override protected String kanalAdi() { return "E-posta"; }
    @Override protected void ilet(String mesaj) {
        System.out.println("E-posta -> " + hedef + " : " + mesaj);
    }
}

class SmsBildirimci extends TemelBildirimci {
    SmsBildirimci(String numara) { super(numara); }
    @Override protected String kanalAdi() { return "SMS"; }
    @Override protected void ilet(String mesaj) {
        // SMS'te kısa tutmak için ilk 30 karakter.
        String kisa = mesaj.length() > 30 ? mesaj.substring(0, 30) + "..." : mesaj;
        System.out.println("SMS -> " + hedef + " : " + kisa);
    }
}
