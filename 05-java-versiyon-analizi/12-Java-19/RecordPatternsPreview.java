/**
 * JAVA 19 - RECORD PATTERNS (JEP 405) - 1. PREVIEW
 * =================================================
 *
 * Bu dosya, Record Patterns'in Java 19'daki ILK preview halini gosterir.
 * (Daha gelismis / kalici ornekler: 13-Java-20 ve 14-Java-21 klasorleri.)
 *
 * NEDIR?
 *   Bir record'u eslestirirken (instanceof / switch), bilesenlerini DOGRUDAN
 *   degiskenlere "yikma" (destructuring) imkani. Tur eslesmesi ve alan
 *   cikarimi tek adimda olur.
 *
 * NEDEN GELDI?
 *   Pattern matching (Java 16) ile turu ogrenip bir degiskene baglayabiliyorduk,
 *   ancak alanlara yine tek tek erismek gerekiyordu. Record patterns bu adimi
 *   ortadan kaldirir.
 *
 * --- DERLEME / CALISTIRMA (Java 19'da PREVIEW) ---
 *   javac --release 19 --enable-preview RecordPatternsPreview.java
 *   java  --enable-preview RecordPatternsPreview
 *
 *   NOT: Java 21'de bu ozellik KALICI oldu; orada --enable-preview gerekmez.
 */
public class RecordPatternsPreview {

    // Basit record'lar
    record Nokta(int x, int y) {}
    record Cizgi(Nokta baslangic, Nokta bitis) {}

    public static void main(String[] args) {
        System.out.println("=== JAVA 19: RECORD PATTERNS (1. PREVIEW) ===\n");

        Object n = new Nokta(3, 4);
        Object c = new Cizgi(new Nokta(0, 0), new Nokta(10, 5));

        eskiYontem(n);
        yeniYontem(n);
        icIceYikim(c);
    }

    /** ESKI: once instanceof ile tur, sonra alanlara erisim metoduyla. */
    private static void eskiYontem(Object nesne) {
        System.out.println("--- ESKI yontem (alanlara tek tek erisim) ---");
        if (nesne instanceof Nokta nk) {
            int x = nk.x();
            int y = nk.y();
            System.out.println("  Nokta: x=" + x + ", y=" + y);
        }
        System.out.println();
    }

    /** YENI: record pattern ile yikim. Tur + alan cikarimi tek satirda. */
    private static void yeniYontem(Object nesne) {
        System.out.println("--- YENI yontem (record pattern / yikim) ---");
        // x ve y dogrudan burada tanimlanir
        if (nesne instanceof Nokta(int x, int y)) {
            System.out.println("  Nokta: x=" + x + ", y=" + y);
        }
        System.out.println();
    }

    /**
     * IC ICE YIKIM (nested destructuring): Record patterns en cok burada parlar.
     * Cizgi'nin icindeki iki Nokta'nin alanlarini tek desende cikariyoruz.
     */
    private static void icIceYikim(Object nesne) {
        System.out.println("--- Ic ice yikim (nested record pattern) ---");
        if (nesne instanceof Cizgi(Nokta(int x1, int y1), Nokta(int x2, int y2))) {
            int dx = x2 - x1;
            int dy = y2 - y1;
            double uzunluk = Math.sqrt(dx * dx + dy * dy);
            System.out.printf("  Cizgi (%d,%d)->(%d,%d), uzunluk=%.2f%n",
                    x1, y1, x2, y2, uzunluk);
        }
        System.out.println();
    }
}
