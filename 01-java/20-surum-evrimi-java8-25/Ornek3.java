// Ornek3: JAVA 14-17 — records, switch expression, pattern matching, sealed (öncesi vs sonrası).
// Çalıştırma: java Ornek3.java
import java.util.Objects;

public class Ornek3 {

    // ============ record (Java 16): "öncesi" tam bir sınıf, "sonrası" tek satır ============
    // ÖNCESİ: değişmez bir veri sınıfı için onlarca satır boilerplate gerekirdi.
    static final class NoktaEski {
        private final int x, y;
        NoktaEski(int x, int y) { this.x = x; this.y = y; }
        int x() { return x; } int y() { return y; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof NoktaEski n)) return false;
            return x == n.x && y == n.y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
        @Override public String toString() { return "NoktaEski[x=" + x + ", y=" + y + "]"; }
    }
    // SONRASI (Java 16): tek satır — equals/hashCode/toString/getter'lar OTOMATİK.
    record Nokta(int x, int y) {}

    sealed interface Sekil permits Daire, Kare {}        // Java 17: kapalı hiyerarşi
    record Daire(double r) implements Sekil {}
    record Kare(double k) implements Sekil {}

    public static void main(String[] args) {
        System.out.println("record (eski sınıf vs record): "
                + new NoktaEski(1, 2).equals(new NoktaEski(1, 2)) + " / " + new Nokta(1, 2).equals(new Nokta(1, 2)));
        System.out.println("record toString: " + new Nokta(3, 4));

        // ============ switch: statement (eski) vs expression (Java 14) ============
        int gun = 6;
        // ESKİ: switch statement — break, mutable değişken, hataya açık
        String tipEski;
        switch (gun) {
            case 6: case 7: tipEski = "hafta sonu"; break;
            default: tipEski = "hafta içi";
        }
        // YENİ (Java 14): switch expression — değer döndürür, break yok, eksiksizlik denetimi
        String tipYeni = switch (gun) {
            case 6, 7 -> "hafta sonu";
            default -> "hafta içi";
        };
        System.out.println("\nswitch (statement vs expression): " + tipEski + " == " + tipYeni);

        // ============ pattern matching for instanceof (Java 16) ============
        Object o = "merhaba";
        // ESKİ: instanceof + ayrı cast
        if (o instanceof String) {
            String s = (String) o;
            System.out.println("\neski instanceof+cast: uzunluk " + s.length());
        }
        // YENİ: tek adımda kontrol + bağlama
        if (o instanceof String s) {
            System.out.println("yeni pattern matching: uzunluk " + s.length());
        }

        // ============ sealed + switch pattern (Java 17) ============
        Sekil sekil = new Daire(2);
        // sealed olduğu için switch EKSİKSİZ (default gerekmez); derleyici tüm tipleri zorlar.
        double alan = switch (sekil) {
            case Daire d -> Math.PI * d.r() * d.r();
            case Kare k -> k.k() * k.k();
        };
        System.out.printf("%nsealed + switch: alan = %.2f%n", alan);

        System.out.println("""

                --- Java 14-17 NE getirdi? ---
                switch expression (14), text block (15), records (16), pattern matching instanceof (16),
                sealed sınıflar (17, LTS). Etki: çok daha az boilerplate, daha güvenli ve okunaklı modelleme.
                Java 17 bugün hâlâ çok yaygın bir üretim tabanıdır.""");
    }
}
