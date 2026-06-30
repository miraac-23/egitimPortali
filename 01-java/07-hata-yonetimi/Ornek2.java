// Ornek2: checked/unchecked, throw/throws ve özel (custom) exception.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    // throws: bu metot bir checked exception fırlatabilir, çağıran ilgilenmeli.
    static void paraCek(double bakiye, double tutar) throws YetersizBakiyeException {
        if (tutar > bakiye) {
            // throw: bir exception nesnesini elle fırlat.
            throw new YetersizBakiyeException(
                    "Yetersiz bakiye: istenen " + tutar + ", mevcut " + bakiye);
        }
        System.out.println(tutar + " TL çekildi. Kalan: " + (bakiye - tutar));
    }

    public static void main(String[] args) {
        double bakiye = 500;

        // Checked exception derleyici tarafından zorunlu kılınır: ya yakala ya da bildir.
        try {
            paraCek(bakiye, 200);
            paraCek(bakiye, 800); // bu satır YetersizBakiyeException fırlatır
        } catch (YetersizBakiyeException e) {
            System.out.println("İşlem reddedildi: " + e.getMessage());
        }

        // Unchecked (RuntimeException) yakalamak zorunlu değildir ama yakalanabilir.
        try {
            int x = Integer.parseInt("abc"); // NumberFormatException (unchecked)
            System.out.println(x);
        } catch (NumberFormatException e) {
            System.out.println("Sayı çevrilemedi: geçersiz format.");
        }
    }
}

// Özel checked exception: Exception'dan türetilir.
// (RuntimeException'dan türetseydik unchecked olurdu.)
class YetersizBakiyeException extends Exception {
    public YetersizBakiyeException(String mesaj) {
        super(mesaj);
    }
}
