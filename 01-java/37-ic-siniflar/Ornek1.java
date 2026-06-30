// Ornek1: Üye (member) iç sınıf — dış nesnenin parçasıdır, dış alanlara erişir.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        Banka banka = new Banka("Ziraat");
        // Üye iç sınıf, DIŞ nesneye bağlıdır: önce dış nesne, sonra .new ile iç nesne.
        Banka.Hesap hesap = banka.new Hesap("Ada", 1000);
        hesap.bilgiYaz();   // iç sınıf, dış nesnenin alanına (bankaAdi) erişebilir
        hesap.paraYatir(500);
        hesap.bilgiYaz();

        System.out.println("""

                --- Üye (member) iç sınıf ---
                Bir dış nesnenin İÇİNDE, ona bağlı olarak yaşar; dış nesnenin (private dahil) üyelerine erişir.
                Oluşturma: disNesne.new Ic(...). Her iç nesnenin gizli bir 'dış nesne' referansı vardır.
                Kullanım: dış sınıfın iç durumuyla sıkı çalışan yardımcı yapılar (ör. iterator).""");
    }
}

class Banka {
    private final String bankaAdi;
    Banka(String bankaAdi) { this.bankaAdi = bankaAdi; }

    // Üye (non-static) iç sınıf: dış nesneye bağlı.
    class Hesap {
        private final String sahip;
        private double bakiye;
        Hesap(String sahip, double bakiye) { this.sahip = sahip; this.bakiye = bakiye; }

        void paraYatir(double t) { bakiye += t; }

        void bilgiYaz() {
            // bankaAdi DIŞ sınıfın private alanı; iç sınıf doğrudan erişebilir.
            System.out.println(bankaAdi + " | " + sahip + " | bakiye: " + bakiye);
        }
    }
}
