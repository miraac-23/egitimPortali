// Ornek2: BitSet ile Eratosthenes Eleği — N'e kadar asal sayıları bulma (gerçek senaryo).
// Çalıştırma: java Ornek2.java
import java.util.BitSet;

public class Ornek2 {

    public static void main(String[] args) {
        int N = 50;

        // bit[i] = true -> i BİLEŞİK (asal değil). Başta hepsi false (asal varsayılır).
        BitSet bilesik = new BitSet(N + 1);
        bilesik.set(0);
        bilesik.set(1); // 0 ve 1 asal değil

        for (int i = 2; (long) i * i <= N; i++) {
            if (!bilesik.get(i)) {                 // i asalsa
                for (int k = i * i; k <= N; k += i) // katlarını bileşik işaretle
                    bilesik.set(k);
            }
        }

        // Asallar = işaretlenMEYEN bitler. nextClearBit ile gez.
        System.out.print(N + "'e kadar asallar: ");
        for (int i = bilesik.nextClearBit(2); i >= 0 && i <= N; i = bilesik.nextClearBit(i + 1)) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("Asal sayısı: " + ((N + 1) - bilesik.cardinality())); // 0..N içinde işaretlenmeyenler

        System.out.println("""

                --- Eratosthenes Eleği (BitSet ile) ---
                Klasik algoritma: 2'den başla, her asalın katlarını "bileşik" işaretle; kalanlar asaldır.
                BitSet burada idealdir: N bit ile N sayının asal/bileşik durumunu kompakt tutar
                (boolean[N]'e göre ~8 kat az bellek) ve nextClearBit ile asalları hızlı gezeriz.
                Bu, BitSet'in "çok sayıda durumu az bellekte tutma ve bit düzeyinde gezme" gücünü gösterir.""");
    }
}
