// Ornek1: Abstract sınıf ve abstract metot.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Abstract sınıftan doğrudan nesne oluşturulamaz:
        // Calisan c = new Calisan(...);  // <-- derlenmez

        Calisan[] kadro = {
                new Muhendis("Ada", 30_000),
                new Satisci("Burak", 20_000, 12_000)
        };

        for (Calisan c : kadro) {
            // maasHesapla() üst sınıfta soyut; her alt sınıf kendi kuralını uygular.
            System.out.printf("%-6s -> aylık maaş: %,.2f TL%n", c.getAd(), c.maasHesapla());
            c.iseGel(); // ortak (somut) davranış üst sınıfta tanımlı
        }
    }
}

// Abstract sınıf: bazı kısımları hazır (somut), bazıları alt sınıfa bırakılmış (soyut).
abstract class Calisan {
    private final String ad;
    protected final double tabanMaas;

    protected Calisan(String ad, double tabanMaas) {
        this.ad = ad;
        this.tabanMaas = tabanMaas;
    }

    public String getAd() { return ad; }

    // Soyut metot: gövdesi yok; alt sınıflar ZORUNLU olarak doldurur.
    public abstract double maasHesapla();

    // Somut metot: tüm çalışanlar için ortak.
    public void iseGel() {
        System.out.println("   " + ad + " işe geldi.");
    }
}

class Muhendis extends Calisan {
    public Muhendis(String ad, double tabanMaas) { super(ad, tabanMaas); }

    @Override
    public double maasHesapla() {
        return tabanMaas * 1.10; // %10 mühendislik zammı
    }
}

class Satisci extends Calisan {
    private final double prim;
    public Satisci(String ad, double tabanMaas, double prim) {
        super(ad, tabanMaas);
        this.prim = prim;
    }

    @Override
    public double maasHesapla() {
        return tabanMaas + prim; // taban + satış primi
    }
}
