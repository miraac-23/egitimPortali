// Ornek2: Autoboxing/unboxing ve iki klasik tuzak (Integer cache, null unboxing).
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        // AUTOBOXING: ilkel -> wrapper otomatik. UNBOXING: wrapper -> ilkel otomatik.
        Integer kutulu = 5;        // autoboxing: int 5 -> Integer
        int ilkel = kutulu;        // unboxing: Integer -> int
        List<Integer> liste = new ArrayList<>();
        liste.add(10);             // autoboxing (List nesne tutar)
        int ilk = liste.get(0);    // unboxing
        System.out.println("autobox/unbox: " + kutulu + ", " + ilkel + ", " + ilk);

        // TUZAK 1: Integer cache. -128..127 arası Integer'lar ÖNBELLEKTEN gelir (aynı nesne);
        // bu aralık dışı her valueOf YENİ nesne üretir. Bu yüzden '==' yanıltıcıdır!
        Integer a1 = 100, a2 = 100;     // önbellekte -> aynı nesne
        Integer b1 = 200, b2 = 200;     // önbellek dışı -> farklı nesne
        System.out.println("\n100 == 100 ? " + (a1 == a2) + "   (önbellek -> true)");
        System.out.println("200 == 200 ? " + (b1 == b2) + "   (önbellek dışı -> false!)");
        System.out.println("200 .equals 200 ? " + b1.equals(b2) + "  (DOĞRU yol: equals)");

        // TUZAK 2: null unboxing -> NullPointerException
        Integer belkiNull = null;
        try {
            int patlar = belkiNull;   // unboxing: null.intValue() -> NPE
            System.out.println(patlar);
        } catch (NullPointerException e) {
            System.out.println("\nnull unboxing -> NullPointerException (wrapper null olabilir!)");
        }

        System.out.println("""

                --- Autoboxing tuzakları ---
                Autoboxing/unboxing kolaylıktır ama iki tuzağı vardır:
                1) Integer'ları '=='  ile KARŞILAŞTIRMA! -128..127 önbellekten (==true), dışı yeni nesne (==false).
                   Wrapper karşılaştırmasında HER ZAMAN .equals() kullan.
                2) null wrapper'ı unbox etmek NPE atar (Integer x = null; int y = x;).
                   Olası null wrapper'ları unbox etmeden önce kontrol et.
                Performans: döngülerde gereksiz autoboxing (Long toplam += ...) yavaşlatır; ilkel tip kullan.""");
    }
}
