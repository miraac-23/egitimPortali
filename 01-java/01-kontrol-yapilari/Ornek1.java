// Ornek1: Koşul ifadeleri — if/else, ternary ve switch expression.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // --- if / else if / else ---
        int puan = 82; // gerçek programda kullanıcıdan gelebilir; burada sabit
        char harf;
        if (puan >= 90) {
            harf = 'A';
        } else if (puan >= 80) {
            harf = 'B';
        } else if (puan >= 70) {
            harf = 'C';
        } else if (puan >= 60) {
            harf = 'D';
        } else {
            harf = 'F';
        }
        System.out.println("Puan " + puan + " -> Harf notu: " + harf);

        // --- Ternary (üçlü) operatör: kısa if/else ---
        // kosul ? doğruysa : yanlışsa
        String durum = (harf == 'F') ? "Kaldı" : "Geçti";
        System.out.println("Durum: " + durum);

        // --- switch expression (Java 14+) ---
        // Ok (->) sözdizimi: break gerekmez, doğrudan değer üretebilir.
        int gunNo = 3;
        String gunAdi = switch (gunNo) {
            case 1 -> "Pazartesi";
            case 2 -> "Salı";
            case 3 -> "Çarşamba";
            case 4 -> "Perşembe";
            case 5 -> "Cuma";
            case 6, 7 -> "Hafta sonu";
            default -> "Geçersiz gün";
        };
        System.out.println("Gün " + gunNo + " -> " + gunAdi);

        // switch ile birden çok etiketi gruplayabiliriz (6, 7 -> ...).
        String tip = switch (gunNo) {
            case 6, 7 -> "Tatil";
            default -> "İş günü";
        };
        System.out.println(gunAdi + " bir " + tip);
    }
}
