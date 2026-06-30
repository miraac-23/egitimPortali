// Ornek2: Dinamik bağlama (dynamic/late binding) — ÇALIŞMA anında çözülür.
// Override edilmiş (instance) metotlar, nesnenin GERÇEK tipine göre çağrılır (polimorfizm).
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        // Referans tipi Sekil olsa da, ÇAĞRILAN metot nesnenin GERÇEK tipine göre seçilir.
        List<Sekil> sekiller = List.of(new Daire(2), new Kare(3), new Daire(1));
        for (Sekil s : sekiller) {
            // s.alan() -> Daire mi Kare mi olduğuna ÇALIŞMA anında karar verilir (dinamik bağlama).
            System.out.printf("%-6s alan = %.2f%n", s.ad(), s.alan());
        }

        Sekil s = new Daire(5);
        s = new Kare(4);             // referans aynı, gerçek tip değişti
        System.out.println("\nSon nesnenin alanı: " + s.alan() + " (Kare — gerçek tipe göre)");

        System.out.println("""

                --- Dinamik bağlama (late binding) ---
                Override edilmiş instance metotlar, REFERANS tipine değil NESNENİN GERÇEK tipine göre çağrılır.
                Karar ÇALIŞMA anında verilir (sanal metot tablosu / vtable). Polimorfizmin motorudur.
                Bu sayede üst tiple (Sekil) yazılan kod, her alt tipin kendi davranışını çalıştırır.""");
    }
}

abstract class Sekil {
    abstract double alan();      // override edilecek -> dinamik bağlanır
    abstract String ad();
}
class Daire extends Sekil {
    private final double r; Daire(double r) { this.r = r; }
    @Override double alan() { return Math.PI * r * r; }
    @Override String ad() { return "Daire"; }
}
class Kare extends Sekil {
    private final double k; Kare(double k) { this.k = k; }
    @Override double alan() { return k * k; }
    @Override String ad() { return "Kare"; }
}
