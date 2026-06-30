// Ornek1: Arrays yardımcı sınıfı — sıralama, arama, kopyalama, karşılaştırma.
// Çalıştırma: java Ornek1.java
import java.util.Arrays;

public class Ornek1 {

    public static void main(String[] args) {
        int[] sayilar = { 5, 2, 8, 1, 9, 3 };

        // toString: diziyi okunaklı yazdır (dizilerin kendi toString'i işe yaramaz!)
        System.out.println("Ham: " + Arrays.toString(sayilar));

        // sort: yerinde sıralar
        Arrays.sort(sayilar);
        System.out.println("Sıralı: " + Arrays.toString(sayilar));

        // binarySearch: SIRALI dizide hızlı arama (O(log n)) — dizinin sıralı olması ŞART
        System.out.println("8'in indeksi: " + Arrays.binarySearch(sayilar, 8));

        // copyOf / copyOfRange: kopya/dilim (boyut değiştirebilir)
        int[] kopya = Arrays.copyOf(sayilar, 4);            // ilk 4
        int[] dilim = Arrays.copyOfRange(sayilar, 1, 4);    // [1,4) aralığı
        System.out.println("copyOf(4): " + Arrays.toString(kopya));
        System.out.println("copyOfRange(1,4): " + Arrays.toString(dilim));

        // fill: tümünü bir değerle doldur
        int[] sifirlar = new int[5];
        Arrays.fill(sifirlar, 7);
        System.out.println("fill(7): " + Arrays.toString(sifirlar));

        // equals: içerik karşılaştırması (== referans karşılaştırır, yanıltıcı!)
        int[] a = {1, 2, 3}, b = {1, 2, 3};
        System.out.println("a == b ? " + (a == b) + " | Arrays.equals(a,b) ? " + Arrays.equals(a, b));

        // asList: diziyi List'e (sabit boyutlu görünüm); stream ile işleme
        String[] kelimeler = { "elma", "armut", "kiraz" };
        System.out.println("asList: " + Arrays.asList(kelimeler));
        int toplam = Arrays.stream(sayilar).sum();
        System.out.println("stream().sum(): " + toplam);

        System.out.println("""

                --- Arrays yardımcı sınıfı ---
                Diziler ilkel/sabit boyutludur ve metotları yoktur; Arrays sınıfı yardımcıları sağlar.
                sort (yerinde sırala), binarySearch (SIRALI dizide hızlı ara), copyOf/copyOfRange (kopya/dilim),
                fill (doldur), equals (İÇERİK karşılaştır — '==' referansa bakar!), toString (yazdır),
                asList (List görünümü), stream (akışa çevir). Çok boyutlu için deepToString/deepEquals (sonraki örnek).""");
    }
}
