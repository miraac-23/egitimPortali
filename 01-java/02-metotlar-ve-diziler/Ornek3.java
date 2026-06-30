// Ornek3: Çok boyutlu diziler (matris) ve metotlarla işleme.
// Çalıştırma: java Ornek3.java
import java.util.Arrays;

public class Ornek3 {

    // İki matrisi toplayan metot. Dizileri parametre olarak alır ve yeni dizi döndürür.
    static int[][] matrisTopla(int[][] a, int[][] b) {
        int satir = a.length;
        int sutun = a[0].length;
        int[][] sonuc = new int[satir][sutun];
        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                sonuc[i][j] = a[i][j] + b[i][j];
            }
        }
        return sonuc;
    }

    // Matrisi düzgün hizalı yazdıran yardımcı metot.
    static void yazdir(String baslik, int[][] m) {
        System.out.println(baslik);
        for (int[] satir : m) {
            for (int deger : satir) {
                System.out.printf("%4d", deger);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // 2 boyutlu dizi = satırlardan oluşan dizi.
        int[][] a = {
                {1, 2, 3},
                {4, 5, 6}
        };
        int[][] b = {
                {10, 20, 30},
                {40, 50, 60}
        };

        yazdir("A matrisi:", a);
        yazdir("\nB matrisi:", b);

        int[][] toplam = matrisTopla(a, b);
        yazdir("\nA + B:", toplam);

        // Her satırın toplamını hesapla.
        System.out.println("\nToplam matrisinin satır toplamları:");
        for (int i = 0; i < toplam.length; i++) {
            int satirToplam = Arrays.stream(toplam[i]).sum();
            System.out.println("  Satır " + i + " -> " + satirToplam);
        }
    }
}
