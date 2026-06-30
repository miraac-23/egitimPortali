// Ornek2: Tek boyutlu diziler — oluşturma, gezinme, istatistik ve sıralama.
// Çalıştırma: java Ornek2.java
import java.util.Arrays;

public class Ornek2 {

    public static void main(String[] args) {
        // Diziyi doğrudan değerlerle oluşturma.
        int[] notlar = {72, 95, 48, 88, 63, 100, 55};

        System.out.println("Dizi: " + Arrays.toString(notlar));
        System.out.println("Eleman sayısı (length): " + notlar.length);

        // for-each ile gezinme (indeks gerekmiyorsa daha temiz).
        int toplam = 0;
        int enBuyuk = notlar[0];
        int enKucuk = notlar[0];
        for (int not : notlar) {
            toplam += not;
            if (not > enBuyuk) enBuyuk = not;
            if (not < enKucuk) enKucuk = not;
        }
        double ortalama = (double) toplam / notlar.length;

        System.out.printf("Toplam=%d, Ortalama=%.2f%n", toplam, ortalama);
        System.out.println("En büyük=" + enBuyuk + ", En küçük=" + enKucuk);

        // Klasik for ile indeks üzerinden gezinme (indeks gerektiğinde).
        System.out.println("\nGeçen notlar (>= 60):");
        for (int i = 0; i < notlar.length; i++) {
            if (notlar[i] >= 60) {
                System.out.println("  index " + i + " -> " + notlar[i]);
            }
        }

        // Arrays.sort diziyi yerinde (in-place) sıralar.
        int[] kopya = Arrays.copyOf(notlar, notlar.length);
        Arrays.sort(kopya);
        System.out.println("\nSıralanmış: " + Arrays.toString(kopya));
        System.out.println("Medyan (ortanca): " + kopya[kopya.length / 2]);
    }
}
