/*
 * =============================================================================
 *  Java 14 - PATTERN MATCHING FOR instanceof (PREVIEW)
 *  JEP 305: Pattern Matching for instanceof (Preview)
 * =============================================================================
 *
 *  PREVIEW DURUMU (cok onemli):
 *    - Java 14: instanceof pattern matching ILK preview olarak geldi.
 *               Derlemek icin:  javac --enable-preview --release 14 PatternMatchingInstanceof.java
 *               Calistirmak:    java  --enable-preview PatternMatchingInstanceof
 *    - Java 15: 2. preview.
 *    - Java 16: ARTIK KALICI! "--enable-preview" gerekmez.
 *
 *  Bu dosya Java 16+ (ornegin Java 21) ile preview bayragi OLMADAN derlenir.
 *
 *  ANA FIKIR:
 *    Klasik kalip:  if (obj instanceof String) { String s = (String) obj; ... }
 *    Yeni kalip:    if (obj instanceof String s)              { ... }
 *    -> Tip kontrolu + cast + degisken tanimi TEK ADIMDA birlesir.
 *    -> "s" otomatik olarak String tipinde baglanir (binding variable).
 * =============================================================================
 */
public class PatternMatchingInstanceof {

    public static void main(String[] args) {
        System.out.println("=== Java 14: Pattern Matching for instanceof (Preview) ===\n");

        // -------------------------------------------------------------------
        // 1) ESKI vs YENI - aciklayici metin uretme
        // -------------------------------------------------------------------
        Object[] nesneler = {
                "Merhaba Dunya",
                42,
                3.14,
                java.util.List.of("a", "b", "c"),
                true
        };

        System.out.println("--- ESKI yol (gereksiz cast ile) ---");
        for (Object o : nesneler) {
            System.out.println("  " + aciklaEski(o));
        }

        System.out.println("\n--- YENI yol (pattern matching ile) ---");
        for (Object o : nesneler) {
            System.out.println("  " + aciklaYeni(o));
        }
        System.out.println();

        // -------------------------------------------------------------------
        // 2) GERCEK HAYAT: equals() override - en sik kullanim alani
        // -------------------------------------------------------------------
        System.out.println("--- Gercek Hayat: equals() override ---");
        Urun u1 = new Urun("Klavye", 750);
        Urun u2 = new Urun("Klavye", 750);
        Urun u3 = new Urun("Mouse", 300);
        System.out.println("u1.equals(u2) : " + u1.equals(u2)); // true
        System.out.println("u1.equals(u3) : " + u1.equals(u3)); // false
        System.out.println("u1.equals(\"x\"): " + u1.equals("x")); // false
        System.out.println();

        // -------------------------------------------------------------------
        // 3) GERCEK HAYAT: Object iceren bir "JSON benzeri" deger isleme
        // -------------------------------------------------------------------
        System.out.println("--- Gercek Hayat: JSON benzeri deger formatlama ---");
        System.out.println("  " + jsonDeger("isim"));
        System.out.println("  " + jsonDeger(2026));
        System.out.println("  " + jsonDeger(true));
        System.out.println("  " + jsonDeger(null));
        System.out.println("  " + jsonDeger(99.5));
    }

    /**
     * ESKI YOL: instanceof kontrolu + ayri cast + degisken tanimi.
     * Her tip icin 3 satirlik tekrar; cast gurultusu.
     */
    static String aciklaEski(Object o) {
        if (o instanceof String) {
            String s = (String) o;                 // gereksiz, tekrarli cast
            return "Metin uzunlugu: " + s.length();
        } else if (o instanceof Integer) {
            Integer i = (Integer) o;
            return "Tam sayi karesi: " + (i * i);
        } else if (o instanceof Double) {
            Double d = (Double) o;
            return "Ondalik yuvarlanmis: " + Math.round(d);
        } else if (o instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) o;
            return "Liste boyutu: " + list.size();
        }
        return "Bilinmeyen tip: " + o;
    }

    /**
     * YENI YOL: pattern matching - tip kontrolu ve binding tek adimda.
     * Binding degiskeni (s, i, d, list) dogrudan kullanilabilir; cast YOK.
     */
    static String aciklaYeni(Object o) {
        if (o instanceof String s) {                       // s -> String
            return "Metin uzunlugu: " + s.length();
        } else if (o instanceof Integer i) {               // i -> Integer
            return "Tam sayi karesi: " + (i * i);
        } else if (o instanceof Double d) {                // d -> Double
            return "Ondalik yuvarlanmis: " + Math.round(d);
        } else if (o instanceof java.util.List<?> list) {  // list -> List
            return "Liste boyutu: " + list.size();
        }
        return "Bilinmeyen tip: " + o;
    }

    /**
     * JSON benzeri deger formatlama. Pattern matching ile tip-temelli
     * dallanmanin ne kadar temiz oldugunu gosterir.
     */
    static String jsonDeger(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof String s) {
            return "\"" + s + "\"";                 // metinler tirnak icinde
        } else if (o instanceof Boolean b) {
            return b ? "true" : "false";
        } else if (o instanceof Number n) {
            return n.toString();                    // sayilar tirnaksiz
        }
        return "\"" + o + "\"";
    }

    // =========================================================================
    //  GERCEK HAYAT: equals() override - pattern matching'in klasik kullanimi
    //  Tek "&&" kosulunda hem tip kontrolu hem binding hem karsilastirma.
    // =========================================================================
    static final class Urun {
        private final String ad;
        private final int fiyat;

        Urun(String ad, int fiyat) {
            this.ad = ad;
            this.fiyat = fiyat;
        }

        @Override
        public boolean equals(Object o) {
            // ESKI: if (!(o instanceof Urun)) return false; Urun u = (Urun) o; ...
            // YENI: tek satirda kontrol + binding + flow scoping.
            // "&&" sayesinde 'u', sadece instanceof TRUE iken erisilebilir
            // (flow scoping); bu da kodu hem kisaltir hem guvenli kilar.
            return o instanceof Urun u
                    && fiyat == u.fiyat
                    && java.util.Objects.equals(ad, u.ad);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(ad, fiyat);
        }

        @Override
        public String toString() {
            return "Urun{ad='" + ad + "', fiyat=" + fiyat + "}";
        }
    }
}
