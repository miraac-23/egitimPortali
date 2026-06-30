// Ornek2: reduce, IntStream ve istatistikler.
// Çalıştırma: java Ornek2.java
import java.util.List;
import java.util.IntSummaryStatistics;

public class Ornek2 {

    public static void main(String[] args) {
        List<Integer> sayilar = List.of(4, 8, 15, 16, 23, 42);

        // --- reduce: elemanları tek bir değere indirger ---
        int toplam = sayilar.stream().reduce(0, Integer::sum);
        int carpim = sayilar.stream().reduce(1, (a, b) -> a * b);
        System.out.println("Sayılar: " + sayilar);
        System.out.println("reduce toplam : " + toplam);
        System.out.println("reduce çarpım : " + carpim);

        // --- IntStream: ilkel int akışı, kutulama maliyeti yok + hazır istatistikler ---
        int kareToplam = sayilar.stream()
                .mapToInt(Integer::intValue)   // Stream<Integer> -> IntStream
                .map(n -> n * n)
                .sum();
        System.out.println("\nKarelerin toplamı: " + kareToplam);

        // summaryStatistics: tek geçişte sayı/min/max/ortalama/toplam
        IntSummaryStatistics ist = sayilar.stream().mapToInt(Integer::intValue).summaryStatistics();
        System.out.printf("adet=%d, min=%d, max=%d, ortalama=%.2f, toplam=%d%n",
                ist.getCount(), ist.getMin(), ist.getMax(), ist.getAverage(), ist.getSum());

        // IntStream.range ile sayı üretimi (1..10 toplamı)
        int birdenOna = java.util.stream.IntStream.rangeClosed(1, 10).sum();
        System.out.println("\n1..10 toplamı (IntStream.rangeClosed): " + birdenOna);

        // max ve filtreli işlem
        sayilar.stream().filter(n -> n % 2 == 0).max(Integer::compareTo)
                .ifPresent(m -> System.out.println("En büyük çift: " + m));
    }
}
