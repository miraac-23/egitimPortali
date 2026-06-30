// Ornek2: PriorityQueue — öncelik kuyruğu (heap). En öncelikli eleman önce çıkar.
// Çalıştırma: java Ornek2.java
import java.util.Comparator;
import java.util.PriorityQueue;

public class Ornek2 {

    record Gorev(String ad, int oncelik) {}

    public static void main(String[] args) {
        // Varsayılan: min-heap (en KÜÇÜK önce çıkar).
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.add(50); minHeap.add(10); minHeap.add(30); minHeap.add(20);
        System.out.print("Min-heap çıkış sırası: ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " "); // 10 20 30 50
        System.out.println();

        // Max-heap: Comparator ile ters (en BÜYÜK önce).
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.addAll(java.util.List.of(50, 10, 30, 20));
        System.out.print("Max-heap çıkış sırası: ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " "); // 50 30 20 10
        System.out.println();

        // Gerçek senaryo: görev zamanlayıcı — düşük 'oncelik' sayısı = daha acil.
        PriorityQueue<Gorev> zamanlayici = new PriorityQueue<>(Comparator.comparingInt(Gorev::oncelik));
        zamanlayici.add(new Gorev("e-posta gönder", 5));
        zamanlayici.add(new Gorev("sistem çökmesi!", 1));
        zamanlayici.add(new Gorev("rapor üret", 3));
        System.out.println("\nGörevler önceliğe göre işleniyor:");
        while (!zamanlayici.isEmpty()) {
            Gorev g = zamanlayici.poll();
            System.out.println("  [" + g.oncelik() + "] " + g.ad());
        }

        System.out.println("""

                --- PriorityQueue (öncelik kuyruğu) ---
                Elemanları öncelik sırasına göre verir (FIFO değil!). İçte 'heap' (yığın ağacı) kullanır.
                add/offer O(log n), peek O(1), poll O(log n). peek/poll HER ZAMAN en öncelikliyi verir.
                Varsayılan min-heap (en küçük); Comparator ile max-heap veya özel öncelik.
                Kullanım: görev/iş zamanlama, Dijkstra/A* gibi algoritmalar, 'en yakın K eleman'.
                NOT: kuyruğu gezmek (iterator) SIRALI değildir; sıra yalnızca poll ile ortaya çıkar.""");
    }
}
