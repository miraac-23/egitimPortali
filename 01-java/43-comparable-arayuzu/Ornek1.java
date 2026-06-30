// Ornek1: Comparable — bir nesneye "doğal sıra" (natural ordering) kazandırmak.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class Ornek1 {

    public static void main(String[] args) {
        List<Urun> urunler = new ArrayList<>(List.of(
                new Urun("Monitör", 3200), new Urun("Mouse", 250),
                new Urun("Klavye", 450), new Urun("Kulaklık", 900)));

        // Urun, Comparable uyguladığı için doğrudan sıralanabilir (doğal sıra: fiyata göre).
        Collections.sort(urunler);
        System.out.println("Doğal sıra (fiyat artan):");
        urunler.forEach(u -> System.out.println("  " + u));

        // list.sort(null) da doğal sırayı kullanır:
        urunler.sort(null);

        // TreeSet doğal sırayı kullanarak elemanları SIRALI tutar (Comparable şart).
        TreeSet<Urun> sirali = new TreeSet<>(urunler);
        System.out.println("\nTreeSet (en ucuz): " + sirali.first());
        System.out.println("TreeSet (en pahalı): " + sirali.last());

        System.out.println("""

                --- Comparable (doğal sıra) ---
                Comparable<T> uygulayan sınıf 'doğal' bir sıraya sahip olur: compareTo(o) metodu.
                Dönüş: negatif (bu < o), 0 (eşit), pozitif (bu > o).
                Collections.sort, list.sort(null), TreeSet, TreeMap, Arrays.sort bunu kullanır.
                Tek bir 'varsayılan' sıralama için idealdir (ör. sayılar artan, String alfabetik).""");
    }
}

// Comparable: bu sınıfın nesneleri kendi aralarında karşılaştırılabilir.
class Urun implements Comparable<Urun> {
    private final String ad;
    private final double fiyat;
    Urun(String ad, double fiyat) { this.ad = ad; this.fiyat = fiyat; }

    @Override
    public int compareTo(Urun diger) {
        // Doğal sıra = fiyata göre artan. (Double.compare taşma/NaN güvenli.)
        return Double.compare(this.fiyat, diger.fiyat);
    }

    @Override public String toString() { return ad + " (" + fiyat + " TL)"; }
}
