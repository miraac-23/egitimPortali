// Ornek1: Comparator — sınıfı değiştirmeden, dışarıdan farklı sıralamalar tanımlamak.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ornek1 {

    record Urun(String ad, double fiyat, int stok) {}

    public static void main(String[] args) {
        List<Urun> urunler = new ArrayList<>(List.of(
                new Urun("Monitör", 3200, 5),
                new Urun("Mouse", 250, 40),
                new Urun("Klavye", 450, 12),
                new Urun("Kulaklık", 900, 12)));

        // Comparator dışarıdan tanımlanır; aynı listeyi farklı ölçütlerle sıralayabiliriz.

        // 1) Ada göre (alfabetik) — Comparator.comparing + anahtar çıkarıcı
        urunler.sort(Comparator.comparing(Urun::ad));
        System.out.println("Ada göre   : " + urunler.stream().map(Urun::ad).toList());

        // 2) Fiyata göre artan
        urunler.sort(Comparator.comparingDouble(Urun::fiyat));
        System.out.println("Fiyat artan: " + urunler.stream().map(Urun::ad).toList());

        // 3) Fiyata göre AZALAN — reversed()
        urunler.sort(Comparator.comparingDouble(Urun::fiyat).reversed());
        System.out.println("Fiyat azalan: " + urunler.stream().map(Urun::ad).toList());

        // 4) Lambda ile elle: stoğa göre
        urunler.sort((a, b) -> Integer.compare(a.stok(), b.stok()));
        System.out.println("Stok artan : " + urunler.stream().map(Urun::ad).toList());

        System.out.println("""

                --- Comparator ---
                Comparator, sıralama mantığını sınıfın DIŞINDA tutar; aynı tip için BİRDEN ÇOK sıra tanımlanır.
                compare(a,b): negatif (a<b), 0 (eşit), pozitif (a>b).
                Kısa yollar: Comparator.comparing(anahtar), comparingInt/Double, .reversed().
                Sınıfı hiç değiştirmeden, ihtiyaca göre kâh ada kâh fiyata göre sıralarsın.""");
    }
}
