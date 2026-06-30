// Ornek1: List (ArrayList) — ekleme, erişim, gezinme, sıralama.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ornek1 {

    public static void main(String[] args) {
        // List sıralı ve tekrar eden elemana izin veren bir koleksiyondur.
        List<String> sehirler = new ArrayList<>();
        sehirler.add("İstanbul");
        sehirler.add("Ankara");
        sehirler.add("İzmir");
        sehirler.add("Ankara"); // tekrar serbest

        System.out.println("Liste: " + sehirler);
        System.out.println("Eleman sayısı: " + sehirler.size());
        System.out.println("1. indeks: " + sehirler.get(1));
        System.out.println("'İzmir' içeriyor mu? " + sehirler.contains("İzmir"));

        sehirler.remove("Ankara"); // ilk eşleşeni siler
        System.out.println("Bir 'Ankara' silindi: " + sehirler);

        // Sıralama: doğal sıra (alfabetik)
        Collections.sort(sehirler);
        System.out.println("Alfabetik: " + sehirler);

        // Comparator ile özel sıralama: uzunluğa göre
        sehirler.sort(Comparator.comparingInt(String::length));
        System.out.println("Uzunluğa göre: " + sehirler);

        // for-each ile gezinme
        System.out.println("\nGezinme:");
        for (String s : sehirler) {
            System.out.println("  - " + s + " (" + s.length() + " harf)");
        }

        // Sayısal liste üzerinde toplam
        List<Integer> sayilar = List.of(5, 3, 8, 1, 9);
        int toplam = 0;
        for (int n : sayilar) toplam += n;
        System.out.println("\nSayılar " + sayilar + " toplam = " + toplam
                + ", max = " + Collections.max(sayilar));
    }
}
