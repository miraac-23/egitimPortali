// Ornek2: ArrayList vs LinkedList — hangisi ne zaman? (basit ölçümle)
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        int N = 100_000;

        // 1) BAŞA ekleme: LinkedList hızlı (O(1)), ArrayList yavaş (her seferinde kaydırma O(n)).
        long t1 = sure(() -> {
            List<Integer> l = new ArrayList<>();
            for (int i = 0; i < N; i++) l.add(0, i);   // başa ekle
        });
        long t2 = sure(() -> {
            LinkedList<Integer> l = new LinkedList<>();
            for (int i = 0; i < N; i++) l.addFirst(i); // başa ekle
        });
        System.out.printf("BAŞA ekleme (%d):  ArrayList ~%4d ms | LinkedList ~%4d ms%n", N, t1, t2);

        // 2) RASTGELE erişim: ArrayList hızlı (O(1)), LinkedList yavaş (O(n)).
        List<Integer> arr = new ArrayList<>();
        LinkedList<Integer> lin = new LinkedList<>();
        for (int i = 0; i < N; i++) { arr.add(i); lin.add(i); }
        long t3 = sure(() -> { long s = 0; for (int i = 0; i < N; i += 100) s += arr.get(i); });
        long t4 = sure(() -> { long s = 0; for (int i = 0; i < N; i += 100) s += lin.get(i); });
        System.out.printf("RASTGELE erişim:  ArrayList ~%4d ms | LinkedList ~%4d ms%n", t3, t4);

        System.out.println("""

                --- ArrayList vs LinkedList ---
                ArrayList : indeksli erişim O(1), sona ekleme amortize O(1) — ÇOĞU durumda en iyi seçim.
                LinkedList: baş/son ekleme-silme O(1) ama indeksli erişim O(n), bellek/işaretçi maliyeti yüksek.
                Pratik kural: VARSAYILAN olarak ArrayList kullan.
                Kuyruk/yığın gerekiyorsa LinkedList yerine genelde ArrayDeque daha hızlıdır (sonraki konu).""");
    }

    static long sure(Runnable r) {
        long t = System.currentTimeMillis();
        r.run();
        return System.currentTimeMillis() - t;
    }
}
