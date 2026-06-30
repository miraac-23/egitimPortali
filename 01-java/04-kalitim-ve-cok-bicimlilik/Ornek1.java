// Ornek1: Kalıtım (extends), super ve toString() override.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        Kopek k = new Kopek("Karabaş", 3);
        Kedi c = new Kedi("Tekir", 2);

        // Alt sınıflar, üst sınıftaki ortak davranışı (nefesAl) miras alır.
        k.nefesAl();
        k.sesCikar();
        c.sesCikar();

        // toString override edildiği için nesneyi yazdırınca anlamlı metin görürüz.
        System.out.println(k);
        System.out.println(c);
    }
}

// Üst (ana/base) sınıf: ortak alan ve davranışlar burada.
class Hayvan {
    protected String ad;   // protected: alt sınıflar erişebilir
    protected int yas;

    public Hayvan(String ad, int yas) {
        this.ad = ad;
        this.yas = yas;
    }

    public void nefesAl() {
        System.out.println(ad + " nefes alıyor.");
    }

    public void sesCikar() {
        System.out.println(ad + " bir ses çıkardı.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ad=" + ad + ", yas=" + yas + "}";
    }
}

// Kopek, Hayvan'ı genişletir (extends): onun her şeyini miras alır.
class Kopek extends Hayvan {
    public Kopek(String ad, int yas) {
        super(ad, yas); // üst sınıfın constructor'ını çağırır
    }

    @Override
    public void sesCikar() {
        System.out.println(ad + ": Hav hav!");
    }
}

class Kedi extends Hayvan {
    public Kedi(String ad, int yas) {
        super(ad, yas);
    }

    @Override
    public void sesCikar() {
        System.out.println(ad + ": Miyav!");
    }
}
