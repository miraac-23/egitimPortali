// Ornek2: static import — bir sınıfın statik üyelerini kısa adla kullanmak.
// Çalıştırma: java Ornek2.java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

public class Ornek2 {

    public static void main(String[] args) {
        // static import sayesinde Math.PI / Math.sqrt yerine doğrudan PI / sqrt yazarız.
        double yaricap = 3;
        System.out.println("Daire alanı (PI * r^2): " + (PI * pow(yaricap, 2)));
        System.out.println("Karekök 144          : " + sqrt(144));

        // Collectors.toList -> toList (static import)
        List<Integer> kareler = Stream.of(1, 2, 3, 4).map(n -> n * n).collect(toList());
        System.out.println("Kareler: " + kareler);

        System.out.println("""

                --- static import ---
                Normal import: bir SINIFI içe aktarır (Math).
                static import: bir sınıfın STATİK üyelerini (PI, sqrt) doğrudan kullanmayı sağlar.
                Faydası: matematik/yardımcı/test (assertEquals) kodunda okunabilirlik.
                Dikkat: aşırı kullanım kafa karıştırır (bu metot nereden geliyor?); ölçülü kullan.""");
    }
}
