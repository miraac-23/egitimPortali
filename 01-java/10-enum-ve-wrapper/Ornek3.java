// Ornek3: Wrapper sınıfları, autoboxing/unboxing, parsing ve Integer cache tuzağı.
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.List;

public class Ornek3 {

    public static void main(String[] args) {
        // Wrapper: ilkel tiplerin nesne karşılığı (int->Integer, double->Double...).
        // Koleksiyonlar ilkel tip tutamaz, wrapper tutar.
        List<Integer> sayilar = new ArrayList<>();

        // --- Autoboxing: int -> Integer otomatik ---
        sayilar.add(5);       // 5 (int) otomatik Integer'a kutulanır
        sayilar.add(10);

        // --- Unboxing: Integer -> int otomatik ---
        int ilk = sayilar.get(0); // Integer otomatik int'e açılır
        System.out.println("Liste: " + sayilar + ", ilk eleman + 1 = " + (ilk + 1));

        // --- Parsing: metinden sayıya ---
        int x = Integer.parseInt("123");
        double d = Double.parseDouble("3.14");
        boolean b = Boolean.parseBoolean("true");
        System.out.println("parseInt(\"123\")=" + x + ", parseDouble(\"3.14\")=" + d + ", parseBoolean=" + b);

        // Wrapper sabitleri ve yardımcı metotlar:
        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("Integer.max(7, 3) = " + Integer.max(7, 3));

        // --- Integer cache tuzağı ---
        // Java, -128..127 arası Integer'ları önbelleğe alır; bu aralıkta == aynı nesneyi verir.
        Integer a1 = 100, a2 = 100;       // önbellek aralığında
        Integer b1 = 200, b2 = 200;       // önbellek dışında
        System.out.println("\n--- Integer == tuzağı ---");
        System.out.println("100 == 100 (Integer) : " + (a1 == a2)); // true (önbellek)
        System.out.println("200 == 200 (Integer) : " + (b1 == b2)); // false (farklı nesne!)
        System.out.println("200.equals(200)      : " + b1.equals(b2)); // true (doğrusu bu)
        System.out.println("\nKural: Wrapper'ları DEĞER olarak karşılaştırırken equals() kullan.");
    }
}
