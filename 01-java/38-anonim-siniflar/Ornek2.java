// Ornek2: Anonim sınıf vs lambda — ne zaman hangisi?
// Çalıştırma: java Ornek2.java
import java.util.function.Function;

public class Ornek2 {

    public static void main(String[] args) {
        // Tek soyut metotlu (functional) arayüz: anonim sınıf VE lambda ile yazılabilir.

        // a) Anonim sınıf (uzun)
        Function<Integer, Integer> kareAnonim = new Function<Integer, Integer>() {
            @Override public Integer apply(Integer x) { return x * x; }
        };

        // b) Lambda (aynı şey, kısa) — functional interface'lerde TERCİH EDİLEN.
        Function<Integer, Integer> kareLambda = x -> x * x;

        System.out.println("anonim: " + kareAnonim.apply(5) + ", lambda: " + kareLambda.apply(5));

        // 'this' farkı:
        // - Anonim sınıfta 'this' ANONİM nesneyi gösterir.
        // - Lambda'da 'this' ÇEVRELEYEN nesneyi gösterir (lambda ayrı bir kapsam açmaz).
        new Ornek2().thisFarki();

        System.out.println("""

                --- Anonim sınıf mı, lambda mı? ---
                LAMBDA kullan (kısa, okunaklı) eğer:
                  - hedef TEK soyut metotlu bir functional interface ise.
                ANONİM SINIF kullan eğer:
                  - bir ABSTRACT SINIFI uzatman gerekiyorsa, VEYA
                  - birden çok metot/ek alan (durum) gerekiyorsa, VEYA
                  - 'this' anonim nesneyi göstermeli ise.
                Tarihsel olarak anonim sınıflar yaygındı; Java 8 lambda'larla çoğu yerde sadeleşti.""");
    }

    void thisFarki() {
        Runnable r = () -> System.out.println("\nlambda içindeki this -> çevreleyen sınıf: "
                + this.getClass().getSimpleName());
        r.run();
    }
}
