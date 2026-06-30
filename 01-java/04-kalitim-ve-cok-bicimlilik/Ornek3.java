// Ornek3: instanceof pattern matching ve equals/hashCode.
// Çalıştırma: java Ornek3.java
import java.util.Objects;

public class Ornek3 {

    public static void main(String[] args) {
        Odeme[] odemeler = {
                new KrediKarti("1234", 150.0),
                new Havale("TR55", 300.0),
                new KrediKarti("9999", 75.0)
        };

        // instanceof pattern matching (Java 16+):
        // tip kontrolü ve dönüştürme tek adımda; ayrıca otomatik değişken tanımlanır.
        for (Odeme o : odemeler) {
            if (o instanceof KrediKarti kk) {
                System.out.println("Kredi kartı (" + kk.kartNo() + ") -> " + kk.tutar() + " TL");
            } else if (o instanceof Havale h) {
                System.out.println("Havale (" + h.iban() + ") -> " + h.tutar() + " TL");
            }
        }

        // equals/hashCode: iki nesnenin "mantıksal eşitliğini" tanımlar.
        KrediKarti a = new KrediKarti("1234", 150.0);
        KrediKarti b = new KrediKarti("1234", 150.0);
        System.out.println("\na == b ?      " + (a == b));        // false: farklı nesneler
        System.out.println("a.equals(b) ? " + a.equals(b));       // true: aynı değerler
        System.out.println("hashCode eşit ? " + (a.hashCode() == b.hashCode()));
    }
}

abstract class Odeme {
    private final double tutar;
    Odeme(double tutar) { this.tutar = tutar; }
    double tutar() { return tutar; }
}

class KrediKarti extends Odeme {
    private final String kartNo;
    KrediKarti(String kartNo, double tutar) { super(tutar); this.kartNo = kartNo; }
    String kartNo() { return kartNo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KrediKarti other)) return false;
        return Double.compare(tutar(), other.tutar()) == 0 && kartNo.equals(other.kartNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kartNo, tutar());
    }
}

class Havale extends Odeme {
    private final String iban;
    Havale(String iban, double tutar) { super(tutar); this.iban = iban; }
    String iban() { return iban; }
}
