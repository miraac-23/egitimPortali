// Ornek3: Wildcard'lar — ? extends ve ? super (PECS prensibi).
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.List;

public class Ornek3 {

    // ? extends Number: "Number veya alt tipi" olan bir listeden OKURUZ (Producer).
    // Buraya List<Integer>, List<Double> vb. gönderebiliriz.
    static double topla(List<? extends Number> liste) {
        double t = 0;
        for (Number n : liste) t += n.doubleValue();
        return t;
    }

    // ? super Integer: "Integer veya üst tipi" olan bir listeye YAZARIZ (Consumer).
    // Buraya List<Integer>, List<Number>, List<Object> gönderebiliriz.
    static void birdenOnaKadarEkle(List<? super Integer> hedef) {
        for (int i = 1; i <= 5; i++) {
            hedef.add(i);
        }
    }

    public static void main(String[] args) {
        List<Integer> tamlar = List.of(1, 2, 3);
        List<Double> ondaliklar = List.of(1.5, 2.5);

        // ? extends ile her ikisinden de okuyabiliriz:
        System.out.println("topla(tamlar)     = " + topla(tamlar));
        System.out.println("topla(ondaliklar) = " + topla(ondaliklar));

        // ? super ile farklı üst-tipli listelere yazabiliriz:
        List<Integer> hedef1 = new ArrayList<>();
        List<Number> hedef2 = new ArrayList<>();
        birdenOnaKadarEkle(hedef1);
        birdenOnaKadarEkle(hedef2);
        System.out.println("\nInteger listesine eklendi: " + hedef1);
        System.out.println("Number listesine eklendi : " + hedef2);

        // PECS: Producer Extends, Consumer Super.
        // OKUYACAKSAN ? extends, YAZACAKSAN ? super kullan.
        System.out.println("\nKural (PECS): Producer-Extends, Consumer-Super.");
    }
}
