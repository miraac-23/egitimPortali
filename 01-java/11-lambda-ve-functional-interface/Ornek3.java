// Ornek3: Method reference ve Comparator ile nesne sıralama.
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ornek3 {

    record Kisi(String ad, int yas, String sehir) {}

    public static void main(String[] args) {
        List<Kisi> kisiler = new ArrayList<>(List.of(
                new Kisi("Ada", 30, "İzmir"),
                new Kisi("Burak", 25, "Ankara"),
                new Kisi("Ceren", 30, "Ankara"),
                new Kisi("Deniz", 25, "İzmir")
        ));

        // Method reference: lambda'nın daha da kısa hali.
        // s -> System.out.println(s)  yerine  System.out::println
        System.out.println("Method reference ile yazdırma:");
        kisiler.forEach(k -> System.out.println("  " + k));

        // Comparator.comparing + method reference (Kisi::yas)
        kisiler.sort(Comparator.comparingInt(Kisi::yas));
        System.out.println("\nYaşa göre:");
        kisiler.forEach(k -> System.out.println("  " + k.ad() + " (" + k.yas() + ")"));

        // Çok ölçütlü sıralama: önce yaş, eşitse ada göre (thenComparing)
        kisiler.sort(Comparator.comparingInt(Kisi::yas).thenComparing(Kisi::ad));
        System.out.println("\nYaş, sonra ad:");
        kisiler.forEach(k -> System.out.println("  " + k.ad() + " (" + k.yas() + ")"));

        // Tersten sıralama (reversed) ve şehre göre gruplı görünüm
        kisiler.sort(Comparator.comparing(Kisi::sehir).thenComparing(Comparator.comparingInt(Kisi::yas).reversed()));
        System.out.println("\nŞehir, sonra yaş (azalan):");
        kisiler.forEach(k -> System.out.println("  " + k.sehir() + " - " + k.ad() + " (" + k.yas() + ")"));
    }
}
