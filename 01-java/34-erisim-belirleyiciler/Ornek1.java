// Ornek1: Erişim belirleyiciler — public, protected, default (paket), private.
// Bir üyeye NEREDEN erişilebileceğini belirler. (Aynı dosyadaki sınıflar aynı pakettedir.)
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        Hesap h = new Hesap("Ada", 1000);

        // public: her yerden erişilebilir
        System.out.println("public sahip       : " + h.sahip);

        // default (paket-özel) ve protected: AYNI paketteyiz, erişilebilir
        System.out.println("default sube        : " + h.sube);
        System.out.println("protected subeKodu  : " + h.subeKodu);

        // private: SADECE Hesap sınıfının içinden erişilebilir; buradan ERİŞİLEMEZ.
        // System.out.println(h.bakiye);  // <-- derlenmez: bakiye has private access

        // private alana yalnızca sınıfın kendi metotları (public arayüz) üzerinden erişilir.
        System.out.println("private bakiye (getter ile): " + h.getBakiye());
        h.paraYatir(250);
        System.out.println("Yeni bakiye: " + h.getBakiye());
    }
}

class Hesap {
    public String sahip;       // her yerden
    String sube = "Merkez";    // default: yalnızca aynı paketten
    protected String subeKodu = "0001"; // aynı paket + alt sınıflar
    private double bakiye;      // yalnızca bu sınıf

    public Hesap(String sahip, double bakiye) {
        this.sahip = sahip;
        this.bakiye = bakiye;
    }

    // private alanı dışarıya kontrollü açan public metotlar (kapsülleme).
    public double getBakiye() { return bakiye; }
    public void paraYatir(double tutar) {
        if (tutar > 0) bakiye += tutar;
    }

    // private yardımcı metot: yalnızca sınıf içi kullanım.
    private boolean gecerliMi(double tutar) { return tutar > 0; }
}
