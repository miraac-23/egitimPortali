// Ornek2: Çok biçimlilik (polymorphism) ve dinamik bağlama.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        // Üst tip (Sekil) referansıyla farklı alt tipleri tutabiliriz.
        // Bu, çok biçimliliğin temelidir: tek tip, çok davranış.
        List<Sekil> sekiller = List.of(
                new Daire(3),
                new Kare(4),
                new Daire(1.5),
                new Kare(2.5)
        );

        double toplamAlan = 0;
        for (Sekil s : sekiller) {
            // s.alan() çağrısı, nesnenin GERÇEK tipine göre çözülür (dinamik bağlama).
            System.out.printf("%-6s alan = %.2f%n", s.ad(), s.alan());
            toplamAlan += s.alan();
        }
        System.out.printf("%nToplam alan = %.2f%n", toplamAlan);
    }
}

abstract class Sekil {
    abstract double alan();      // her şekil kendi alanını farklı hesaplar
    abstract String ad();
}

class Daire extends Sekil {
    private final double yaricap;
    Daire(double yaricap) { this.yaricap = yaricap; }

    @Override double alan() { return Math.PI * yaricap * yaricap; }
    @Override String ad() { return "Daire"; }
}

class Kare extends Sekil {
    private final double kenar;
    Kare(double kenar) { this.kenar = kenar; }

    @Override double alan() { return kenar * kenar; }
    @Override String ad() { return "Kare"; }
}
