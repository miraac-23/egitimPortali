// Ornek2: Hazır functional interface'ler — Predicate, Function, Consumer, Supplier.
// Çalıştırma: java Ornek2.java
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Ornek2 {

    public static void main(String[] args) {
        // --- Predicate<T>: bir koşulu test eder, boolean döndürür ---
        Predicate<Integer> ciftMi = n -> n % 2 == 0;
        System.out.println("ciftMi(4) = " + ciftMi.test(4));
        System.out.println("ciftMi(7) = " + ciftMi.test(7));
        // Predicate'ler birleştirilebilir:
        Predicate<Integer> pozitifVeCift = ciftMi.and(n -> n > 0);
        System.out.println("pozitifVeCift(-4) = " + pozitifVeCift.test(-4));

        // --- Function<T,R>: T alır, R döndürür ---
        Function<String, Integer> uzunluk = s -> s.length();
        System.out.println("\nuzunluk(\"merhaba\") = " + uzunluk.apply("merhaba"));
        // andThen ile zincirleme: önce uzunluk, sonra kare
        Function<String, Integer> uzunlukKare = uzunluk.andThen(n -> n * n);
        System.out.println("uzunlukKare(\"abc\") = " + uzunlukKare.apply("abc"));

        // --- Consumer<T>: T alır, bir şey döndürmez (yan etki yapar) ---
        Consumer<String> yazdir = s -> System.out.println("  -> " + s);
        System.out.println("\nConsumer:");
        yazdir.accept("merhaba");
        yazdir.accept("dünya");

        // --- Supplier<T>: girdi almaz, bir değer üretir ---
        Supplier<String> selamUret = () -> "Günaydın!";
        System.out.println("\nSupplier: " + selamUret.get());

        // --- BiFunction<T,U,R>: iki girdi, bir çıktı ---
        BiFunction<Integer, Integer, Integer> topla = (a, b) -> a + b;
        System.out.println("BiFunction topla(3,5) = " + topla.apply(3, 5));
    }
}
