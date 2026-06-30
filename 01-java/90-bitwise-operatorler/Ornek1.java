// Ornek1: Bit operatörleri — & (ve), | (veya), ^ (xor), ~ (değil).
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // Bir sayıyı 8 bitlik ikilik gösterimle yazdırır.
    static String bin(int n) {
        return String.format("%8s", Integer.toBinaryString(n & 0xFF)).replace(' ', '0');
    }

    public static void main(String[] args) {
        int a = 0b1100; // 12
        int b = 0b1010; // 10

        System.out.println("a = " + bin(a) + " (" + a + ")");
        System.out.println("b = " + bin(b) + " (" + b + ")");

        // & (AND): her iki bit de 1 ise 1
        System.out.println("\na & b = " + bin(a & b) + " (" + (a & b) + ")  AND: ikisi de 1");
        // | (OR): bitlerden biri 1 ise 1
        System.out.println("a | b = " + bin(a | b) + " (" + (a | b) + ")  OR: en az biri 1");
        // ^ (XOR): bitler FARKLI ise 1
        System.out.println("a ^ b = " + bin(a ^ b) + " (" + (a ^ b) + ")  XOR: farklıysa 1");
        // ~ (NOT): tüm bitleri ters çevir (tamamlayan)
        System.out.println("~a    = ...(" + (~a) + ")  NOT: tüm bitler ters (~12 = -13)");

        // Tek/çift kontrolü bit ile: son bit 1 ise tek
        int sayi = 7;
        System.out.println("\n" + sayi + " tek mi? " + ((sayi & 1) == 1) + "  ((n & 1) ile)");

        System.out.println("""

                --- Bit operatörleri ---
                Sayıları BİT düzeyinde işler (her bir 0/1 üzerinde):
                  &  AND : ikisi de 1 ise 1   (maske uygulama, "bu bit açık mı?")
                  |  OR  : en az biri 1 ise 1 (bit açma)
                  ^  XOR : farklıysa 1        (bit ters çevirme, basit şifreleme, takas)
                  ~  NOT : tüm bitleri ters çevir
                Hızlı ve düşük seviyeli: bayrak/izin maskeleri, donanım, grafik, performans-kritik kod.
                (Mantıksal && || ile karıştırma: onlar boolean+kısa devre; bunlar bit düzeyinde.)""");
    }
}
