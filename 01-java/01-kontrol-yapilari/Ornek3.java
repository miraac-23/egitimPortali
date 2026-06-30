// Ornek3: İç içe döngüler, break ve continue.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        // --- continue: bu adımı atla, döngüye devam et ---
        // 1..20 arasındaki TEK sayıları yazdır (çiftleri atla).
        System.out.println("1..20 arasındaki tek sayılar:");
        for (int i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                continue; // çift sayıyı atla
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // --- break: döngüyü tamamen sonlandır ---
        // 30'dan büyük ilk 7'ye tam bölünen sayıyı bul.
        System.out.println("\n30'dan büyük, 7'ye tam bölünen ilk sayı:");
        for (int i = 31; ; i++) {        // koşulsuz döngü; çıkışı break sağlar
            if (i % 7 == 0) {
                System.out.println(i);
                break;
            }
        }

        // --- İç içe döngü: 2..10 arası asal sayılar ---
        System.out.println("\n2..20 arasındaki asal sayılar:");
        for (int sayi = 2; sayi <= 20; sayi++) {
            boolean asal = true;
            for (int bolen = 2; bolen <= sayi / 2; bolen++) {
                if (sayi % bolen == 0) {
                    asal = false;
                    break; // bölen bulundu, iç döngüden çık
                }
            }
            if (asal) {
                System.out.print(sayi + " ");
            }
        }
        System.out.println();

        // --- İç içe döngü ile yıldız deseni ---
        System.out.println("\nYıldız üçgeni:");
        for (int satir = 1; satir <= 5; satir++) {
            for (int yildiz = 1; yildiz <= satir; yildiz++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
