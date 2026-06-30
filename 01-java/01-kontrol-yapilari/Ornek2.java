// Ornek2: Döngüler — for, while ve do-while.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // --- for döngüsü: başlangıç; koşul; artış ---
        System.out.println("5'in çarpım tablosu (for):");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("5 x %2d = %2d%n", i, 5 * i);
        }

        // --- while döngüsü: koşul önce kontrol edilir ---
        // 1'den 5'e kadar olan sayıların toplamı.
        System.out.println("\n1..5 toplamı (while):");
        int sayi = 1;
        int toplam = 0;
        while (sayi <= 5) {
            toplam += sayi; // toplam = toplam + sayi
            sayi++;
        }
        System.out.println("Toplam = " + toplam);

        // --- do-while döngüsü: gövde EN AZ bir kez çalışır, koşul sonra kontrol edilir ---
        System.out.println("\n5! (faktöriyel, do-while):");
        int n = 5;
        long faktoriyel = 1;
        int k = 1;
        do {
            faktoriyel *= k;
            k++;
        } while (k <= n);
        System.out.println(n + "! = " + faktoriyel);

        // for ile geriye doğru sayma
        System.out.println("\nGeri sayım:");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println("Başla!");
    }
}
