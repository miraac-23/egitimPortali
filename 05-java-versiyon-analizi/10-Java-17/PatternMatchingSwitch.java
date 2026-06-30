// =============================================================================
//  PatternMatchingSwitch.java
//  JAVA 17 (LTS) - Pattern Matching for switch (JEP 406) - PREVIEW
// =============================================================================
//
//  !!! ÖNEMLİ: Bu özellik Java 17'de PREVIEW'dir. Derlerken/çalıştırırken
//  preview bayrakları GEREKİR:
//
//      javac --release 17 --enable-preview PatternMatchingSwitch.java
//      java  --enable-preview PatternMatchingSwitch
//
//  (Özellik Java 21'de KALICI oldu. Java 17 LTS ile öğrenirken bu kalıbı
//   önizleme olarak görüyoruz; üretimde Java 21'e geçince bayrak gerekmez.)
//
//  NEDİR?
//  switch ifadesinde artık SADECE sabit değerlere (int, String, enum) değil,
//  bir nesnenin TİPİNE göre de dallanabiliyoruz. instanceof pattern matching'in
//  switch'e taşınmış halidir: "case Tip degisken ->" şeklinde.
//
//  NEDEN GELDİ / GÜCÜ NEREDE?
//  Uzun if-else-if instanceof zincirleri yerine SADE, OKUNABİLİR ve EKSİKSİZLİK
//  (exhaustiveness) doğrulaması yapılabilen switch yazarız. Sealed tiplerle
//  birleşince derleyici TÜM olası tiplerin ele alındığını GARANTİ eder.
//
//  ÜÇLÜ GÜÇ: sealed + switch + pattern matching
//   - sealed: alt tipler sonlu ve bilinir
//   - switch pattern matching: tipe göre dallan
//   - exhaustiveness: derleyici eksik dalı YAKALAR (default gerekmeyebilir)
// =============================================================================

public class PatternMatchingSwitch {

    public static void main(String[] args) {

        System.out.println("=== JAVA 17 (LTS): Pattern Matching for switch (preview) ===\n");

        // ---------------------------------------------------------------------
        // 1) ESKİ if-else-if instanceof zinciri vs YENİ switch
        // ---------------------------------------------------------------------
        Object[] veriler = { "metin", 42, 3.14, 100L, "Java" };

        System.out.println("--- 1) ESKİ yöntem (if-else-if) ---");
        for (Object o : veriler) {
            System.out.println("  " + bicimlendirEski(o));
        }

        System.out.println("--- 1) YENİ yöntem (switch pattern matching) ---");
        for (Object o : veriler) {
            System.out.println("  " + bicimlendirYeni(o));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) GUARDED PATTERN: "case Tip g when koşul ->" (ek koşul)
        //    (Java 17 preview'de bu sözdizimi "case Tip g && koşul" idi;
        //     standartlaşırken "when" anahtar kelimesi benimsendi. Burada
        //     Java 17+ ile uyumlu olması için 'when' kullanıyoruz.)
        // ---------------------------------------------------------------------
        System.out.println("--- 2) Guarded pattern (when ile koşullu dal) ---");
        for (Object o : new Object[] { 5, 50, 500, -3 }) {
            System.out.println("  " + buyuklukSinifi(o));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) null'ın switch içinde ele alınması
        //    Eskiden switch'e null gelirse NPE atardı; artık "case null" yazılabilir.
        // ---------------------------------------------------------------------
        System.out.println("--- 3) null güvenli switch ---");
        System.out.println("  " + nullGuvenli("dolu"));
        System.out.println("  " + nullGuvenli(null));
        System.out.println();

        // ---------------------------------------------------------------------
        // 4) GERÇEK HAYAT: sealed tip + switch ile EKSİKSİZ sınıflandırma
        // ---------------------------------------------------------------------
        System.out.println("--- 4) Gerçek hayat: sealed Trafik + switch ---");
        Trafik[] durumlar = { new Kirmizi(), new Sari(2), new Yesil() };
        for (Trafik t : durumlar) {
            System.out.println("  " + komut(t));
        }
    }

    // ESKİ: tip kontrolleri için uzun if-else-if
    static String bicimlendirEski(Object o) {
        if (o instanceof Integer i) {
            return "int: " + i;
        } else if (o instanceof Long l) {
            return "long: " + l;
        } else if (o instanceof Double d) {
            return "double: " + d;
        } else if (o instanceof String s) {
            return "String(" + s.length() + "): " + s;
        } else {
            return "bilinmeyen";
        }
    }

    // YENİ: switch pattern matching - daha sade ve okunabilir
    static String bicimlendirYeni(Object o) {
        return switch (o) {
            case Integer i -> "int: " + i;
            case Long l    -> "long: " + l;
            case Double d  -> "double: " + d;
            case String s  -> "String(" + s.length() + "): " + s;
            default        -> "bilinmeyen";
        };
    }

    // Guarded pattern: aynı tip için ek koşullara göre dallanma
    static String buyuklukSinifi(Object o) {
        return switch (o) {
            case Integer i when i < 0   -> i + " -> negatif";
            case Integer i when i < 10  -> i + " -> küçük";
            case Integer i when i < 100 -> i + " -> orta";
            case Integer i             -> i + " -> büyük";
            default                    -> "sayı değil";
        };
    }

    // null güvenli switch
    static String nullGuvenli(String s) {
        return switch (s) {
            case null     -> "değer yok (null)";
            case "dolu"   -> "dolu değer";
            default       -> "diğer: " + s;
        };
    }

    // -------------------------------------------------------------------------
    //  GERÇEK HAYAT: trafik ışığı durum makinesi (sealed + switch)
    // -------------------------------------------------------------------------
    sealed interface Trafik permits Kirmizi, Sari, Yesil {}
    record Kirmizi() implements Trafik {}
    record Sari(int kalanSaniye) implements Trafik {}
    record Yesil() implements Trafik {}

    // sealed olduğu için switch EKSİKSİZ; default gerekmez (derleyici doğrular)
    static String komut(Trafik t) {
        return switch (t) {
            case Kirmizi k       -> "DUR";
            case Sari s          -> "HAZIRLAN (" + s.kalanSaniye() + " sn)";
            case Yesil y         -> "GEÇ";
        };
    }
}
