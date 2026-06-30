// Ornek1: Çalışma zamanı sürümünü tanıma ve sürüme göre davranış.
// Geçişlerde "hangi JDK üzerinde çalışıyorum?" sorusunu programatik yanıtlamak gerekir.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Runtime.version() (Java 9+): yapısal sürüm bilgisi.
        Runtime.Version v = Runtime.version();
        int major = v.feature(); // ana sürüm: 17, 21, 25...

        System.out.println("Çalışan JDK");
        System.out.println("  Tam sürüm   : " + v);
        System.out.println("  Ana sürüm   : " + major);
        System.out.println("  java.version: " + System.getProperty("java.version"));
        System.out.println("  Sağlayıcı   : " + System.getProperty("java.vendor"));

        // LTS mi? (8, 11, 17, 21, 25 ...)
        boolean lts = switch (major) {
            case 8, 11, 17, 21, 25 -> true;
            default -> false;
        };
        System.out.println("  LTS sürüm   : " + (lts ? "evet" : "hayır (kısa destekli)"));

        // Sürüme göre özellik kullanılabilirliği (feature flag mantığı).
        System.out.println("\nBu JDK'da kullanılabilir özellikler:");
        ozellik("Lambda / Stream / Optional (8+)", major >= 8);
        ozellik("var, HttpClient, tek dosya çalıştırma (11+)", major >= 11);
        ozellik("record, sealed, pattern matching (17+)", major >= 17);
        ozellik("Virtual threads, record patterns (21+)", major >= 21);
        ozellik("Stream Gatherers, sade main, modül import (25+)", major >= 25);

        // Pratik: yeni bir API yoksa eski yola düş (graceful degradation).
        if (major >= 21) {
            System.out.println("\nÖneri: Sanal thread'leri kullanabilirsin (yüksek eşzamanlılık).");
        } else {
            System.out.println("\nÖneri: Sabit boyutlu thread havuzu kullan (sanal thread henüz yok).");
        }
    }

    static void ozellik(String ad, boolean varMi) {
        System.out.println("  [" + (varMi ? "✓" : "×") + "] " + ad);
    }
}
