// Ornek2: protected ve kalıtım — alt sınıf protected üyelere erişir, private'a erişemez.
// Çalıştırma: java Ornek2.java
public class Ornek2 {
    public static void main(String[] args) {
        Yonetici y = new Yonetici("Burak", 30000, 15000);
        y.bilgiYaz();
        System.out.println("Toplam maaş: " + y.toplamMaas());
    }
}

class Calisan {
    protected String ad;        // alt sınıflar erişebilir
    protected double tabanMaas; // alt sınıflar erişebilir
    private String sicilNo = "GIZLI"; // alt sınıf bile erişemez

    public Calisan(String ad, double tabanMaas) {
        this.ad = ad;
        this.tabanMaas = tabanMaas;
    }

    // private alana erişim yalnızca bu sınıftan; alt sınıfa kontrollü açmak için protected/public metot.
    protected String maskeliSicil() { return "****" ; }
}

class Yonetici extends Calisan {
    private double prim;

    public Yonetici(String ad, double tabanMaas, double prim) {
        super(ad, tabanMaas);
        this.prim = prim;
    }

    double toplamMaas() {
        // protected alanlara DOĞRUDAN erişebiliriz (kalıtım).
        return tabanMaas + prim;
    }

    void bilgiYaz() {
        // ad, tabanMaas -> protected (erişilir); sicilNo -> private (ERİŞİLEMEZ).
        System.out.println(ad + " | taban: " + tabanMaas + " | sicil: " + maskeliSicil());
        // System.out.println(sicilNo); // <-- derlenmez: sicilNo private
    }
}
