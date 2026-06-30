// Ornek1: Özyineleme (recursion) — kendini çağıran metotlar.
// Çalıştırma: java Ornek1.java
import java.util.HashMap;
import java.util.Map;

public class Ornek1 {

    // Faktöriyel: n! = n * (n-1)!  — taban durum: 0! = 1
    static long faktoriyel(int n) {
        if (n <= 1) return 1;          // TABAN DURUM (recursion'ı durdurur)
        return n * faktoriyel(n - 1);  // ÖZYİNELEMELİ ADIM (probleme daha küçük hali)
    }

    // Fibonacci (naive): üstel, çok yavaş -> aynı alt problemleri tekrar tekrar hesaplar.
    static long fibNaive(int n) {
        if (n < 2) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    // Fibonacci (memoization): hesaplananı sakla -> doğrusal, hızlı.
    static long fibMemo(int n, Map<Integer, Long> bellek) {
        if (n < 2) return n;
        if (bellek.containsKey(n)) return bellek.get(n);
        long sonuc = fibMemo(n - 1, bellek) + fibMemo(n - 2, bellek);
        bellek.put(n, sonuc);
        return sonuc;
    }

    public static void main(String[] args) {
        System.out.println("5! = " + faktoriyel(5));
        System.out.println("10! = " + faktoriyel(10));

        System.out.println("\nFibonacci(40):");
        long t1 = System.currentTimeMillis();
        System.out.println("  naive  = " + fibNaive(40) + "  (~" + (System.currentTimeMillis() - t1) + " ms)");
        long t2 = System.currentTimeMillis();
        System.out.println("  memo   = " + fibMemo(40, new HashMap<>()) + "  (~" + (System.currentTimeMillis() - t2) + " ms)");

        System.out.println("""

                --- Özyineleme (recursion) ---
                Bir metodun kendini çağırmasıdır. İki parçası vardır:
                  TABAN DURUM: özyinelemeyi durduran koşul (yoksa sonsuz döngü -> StackOverflowError).
                  ÖZYİNELEMELİ ADIM: problemi daha küçük bir haline indirgeyip kendini çağırma.
                Naive fibonacci üsteldir (aynı işi tekrar yapar); MEMOIZATION ile doğrusala iner.
                Özyineleme; ağaç/grafik gezme, böl-ve-fethet, geri izleme (backtracking) için doğaldır.""");
    }
}
