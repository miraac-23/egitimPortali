// Ornek3: Comparable + Comparator birlikte; stream.sorted ve TreeMap ile özel sıra.
// Çalıştırma: java Ornek3.java
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class Ornek3 {

    // Sınıfın DOĞAL sırası (Comparable): puana göre azalan (liderlik tablosu mantığı).
    record Oyuncu(String ad, int puan, int sure) implements Comparable<Oyuncu> {
        @Override public int compareTo(Oyuncu o) { return Integer.compare(o.puan, this.puan); } // azalan
    }

    public static void main(String[] args) {
        List<Oyuncu> oyuncular = List.of(
                new Oyuncu("Ada", 90, 120),
                new Oyuncu("Burak", 90, 95),
                new Oyuncu("Can", 75, 60),
                new Oyuncu("Derya", 88, 140));

        // 1) Doğal sıra (Comparable): puan azalan — stream.sorted() argümansız.
        System.out.println("Doğal sıra (puan ↓):");
        oyuncular.stream().sorted().forEach(o -> System.out.println("  " + o));

        // 2) Comparator ile EZ: puan azalan, eşitse süre artan (daha hızlı bitiren önde).
        Comparator<Oyuncu> liderlik = Comparator
                .comparingInt(Oyuncu::puan).reversed()
                .thenComparingInt(Oyuncu::sure);
        System.out.println("\nÖzel sıra (puan ↓, süre ↑):");
        oyuncular.stream().sorted(liderlik).forEach(o -> System.out.println("  " + o));

        // 3) TreeMap'e özel Comparator vererek anahtarları o sıraya göre tut (uzun string önce).
        TreeMap<String, Integer> harita = new TreeMap<>(Comparator.comparingInt(String::length).reversed()
                .thenComparing(Comparator.naturalOrder()));
        oyuncular.forEach(o -> harita.put(o.ad(), o.puan()));
        System.out.println("\nTreeMap (ada göre uzunluk ↓): " + harita.keySet());

        System.out.println("""

                --- Comparable + Comparator birlikte ---
                Comparable: tipin tek 'varsayılan' sırası (stream.sorted() argümansız bunu kullanır).
                Comparator: o anki ihtiyaca göre, varsayılanı EZEN esnek sıralar (stream.sorted(cmp)).
                Comparator ayrıca TreeSet/TreeMap'e verilebilir; koleksiyon elemanlarını o sıraya göre tutar.
                Pratik kural: tek doğal sıra -> Comparable; çok/özel sıra -> Comparator.""");
    }
}
