import java.util.List;

/**
 * JAVA 20 - RECORD PATTERNS (JEP 432) - 2. PREVIEW (Gelismis)
 * ===========================================================
 *
 * Bu dosya, Record Patterns'in Java 20'deki 2. preview halini, switch ile
 * birlikte ve ic ice (nested) yikim ornekleriyle gosterir.
 *
 * EVRIM:
 *   Java 19: 1. preview  ->  Java 20: 2. preview  ->  Java 21: KALICI
 *
 * BU TURDA NETLESENLER:
 *   - switch + record pattern kombinasyonu
 *   - ic ice yikim (nested destructuring)
 *   - generic record'larda tur cikarimi
 *   - 'var' ile alan turlerini cikarim
 *
 * --- DERLEME / CALISTIRMA (Java 20'de PREVIEW) ---
 *   javac --release 20 --enable-preview RecordPatternsGelismis.java
 *   java  --enable-preview RecordPatternsGelismis
 *
 *   NOT: Java 21'de bu ozellik KALICI oldu; orada --enable-preview gerekmez.
 */
public class RecordPatternsGelismis {

    // sealed (muhurlu) hiyerarsi: switch tamligi (exhaustiveness) icin ideal
    sealed interface Sekil permits Daire, Dikdortgen, UcgenGrup {}
    record Daire(double yaricap) implements Sekil {}
    record Dikdortgen(double en, double boy) implements Sekil {}

    // Ic ice record: bir sekil baska sekiller icerir
    record Nokta(double x, double y) {}
    record UcgenGrup(Nokta a, Nokta b, Nokta c) implements Sekil {}

    public static void main(String[] args) {
        System.out.println("=== JAVA 20: RECORD PATTERNS (2. PREVIEW) ===\n");

        List<Sekil> sekiller = List.of(
                new Daire(5),
                new Dikdortgen(4, 6),
                new UcgenGrup(new Nokta(0, 0), new Nokta(4, 0), new Nokta(0, 3))
        );

        for (Sekil s : sekiller) {
            System.out.printf("%-40s alan = %.2f%n", s, alanHesapla(s));
        }

        System.out.println();
        icIceYikimOrnegi();
    }

    /**
     * switch + record pattern: tur eslesmesi ve alan cikarimi tek adimda.
     * sealed arayuz oldugu icin 'default'a gerek yok (tamlik garantili).
     */
    static double alanHesapla(Sekil s) {
        return switch (s) {
            // Daire'nin yaricapini dogrudan yikiyoruz
            case Daire(double r) -> Math.PI * r * r;

            // Dikdortgenin iki kenarini dogrudan yikiyoruz
            case Dikdortgen(double en, double boy) -> en * boy;

            // IC ICE YIKIM: UcgenGrup icindeki uc Nokta'nin alanlarini ayni anda
            case UcgenGrup(Nokta(var x1, var y1),
                           Nokta(var x2, var y2),
                           Nokta(var x3, var y3)) ->
                    Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
        };
    }

    /** Tekil bir ic ice yikim ornegi (instanceof ile). */
    static void icIceYikimOrnegi() {
        System.out.println("--- Ic ice yikim (instanceof) ---");
        Object nesne = new UcgenGrup(new Nokta(1, 1), new Nokta(2, 2), new Nokta(3, 1));

        if (nesne instanceof UcgenGrup(Nokta(var ax, var ay), Nokta b, Nokta c)) {
            // 'a' tamamen yikildi (ax, ay), 'b' ve 'c' butun olarak baglandi
            System.out.println("  Ilk kose: (" + ax + ", " + ay + ")");
            System.out.println("  Ikinci kose: " + b);
            System.out.println("  Ucuncu kose: " + c);
        }
    }
}
