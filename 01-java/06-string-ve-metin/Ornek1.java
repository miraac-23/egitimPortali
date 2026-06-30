// Ornek1: String API turu ve equals vs == farkı.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        String s = "  Java Programlama Dili  ";
        System.out.println("Orijinal: [" + s + "]");

        // Sık kullanılan String metotları (String IMMUTABLE'dır:
        // her metot YENİ bir String döndürür, orijinali değişmez).
        System.out.println("length()      : " + s.length());
        System.out.println("trim()        : [" + s.trim() + "]");
        System.out.println("toUpperCase() : " + s.trim().toUpperCase());
        System.out.println("toLowerCase() : " + s.trim().toLowerCase());
        System.out.println("substring(2,6): " + s.trim().substring(0, 4));
        System.out.println("indexOf('Dili'): " + s.indexOf("Dili"));
        System.out.println("contains('Prog'): " + s.contains("Prog"));
        System.out.println("replace        : " + s.trim().replace("Dili", "Eğitimi"));
        System.out.println("startsWith('  '): " + s.startsWith("  "));

        // split: metni parçalara böler, dizi döndürür.
        String csv = "elma,armut,kiraz";
        String[] meyveler = csv.split(",");
        System.out.println("\nsplit ile " + meyveler.length + " parça:");
        for (String m : meyveler) System.out.println("  - " + m);

        // --- equals vs == ---
        // == referansları (aynı nesne mi?), equals() içeriği (aynı metin mi?) karşılaştırır.
        String a = "merhaba";
        String b = "merhaba";                 // literal havuzundan aynı nesne
        String c = new String("merhaba");      // new ile FARKLI nesne

        System.out.println("\n--- equals vs == ---");
        System.out.println("a == b        : " + (a == b));        // genelde true (string pool)
        System.out.println("a == c        : " + (a == c));        // false: farklı nesne
        System.out.println("a.equals(c)   : " + a.equals(c));     // true: aynı içerik
        // KURAL: String'leri her zaman equals() ile karşılaştır.
    }
}
