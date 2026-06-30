// Ornek2: Bounded type (sınırlı tip) — <T extends Number>.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    // T yalnızca Number ve alt tipleri (Integer, Double, Long...) olabilir.
    // Bu sınır sayesinde T üzerinde doubleValue() gibi Number metotlarını çağırabiliriz.
    static <T extends Number> double toplam(List<T> liste) {
        double t = 0;
        for (T eleman : liste) {
            t += eleman.doubleValue();
        }
        return t;
    }

    static <T extends Number> double ortalama(List<T> liste) {
        return liste.isEmpty() ? 0 : toplam(liste) / liste.size();
    }

    // Birden çok sınır da konabilir: <T extends Number & Comparable<T>>
    static <T extends Number & Comparable<T>> T enBuyuk(List<T> liste) {
        T max = liste.get(0);
        for (T e : liste) {
            if (e.compareTo(max) > 0) max = e;
        }
        return max;
    }

    public static void main(String[] args) {
        List<Integer> tamSayilar = List.of(10, 20, 30, 40);
        List<Double> ondaliklar = List.of(1.5, 2.5, 3.0);

        System.out.println("Tam sayılar: " + tamSayilar);
        System.out.println("  toplam   = " + toplam(tamSayilar));
        System.out.println("  ortalama = " + ortalama(tamSayilar));
        System.out.println("  en büyük = " + enBuyuk(tamSayilar));

        System.out.println("\nOndalıklar: " + ondaliklar);
        System.out.println("  toplam   = " + toplam(ondaliklar));
        System.out.println("  ortalama = " + ortalama(ondaliklar));

        // toplam(List.of("a","b"));  // <-- derlenmez: String, Number değil
    }
}
