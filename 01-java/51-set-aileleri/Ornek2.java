// Ornek2: TreeSet (sıralı + komşuluk/aralık) ve EnumSet (enum için hızlı küme).
// Çalıştırma: java Ornek2.java
import java.util.EnumSet;
import java.util.TreeSet;

public class Ornek2 {

    enum Yetki { OKU, YAZ, SIL, YONET }

    public static void main(String[] args) {
        // TreeSet: elemanları SIRALI tutar; navigation metotları sağlar.
        TreeSet<Integer> sayilar = new TreeSet<>(java.util.List.of(50, 20, 80, 10, 65, 35));
        System.out.println("TreeSet (sıralı): " + sayilar);
        System.out.println("first/last: " + sayilar.first() + " / " + sayilar.last());
        System.out.println("floor(40)   = " + sayilar.floor(40));    // <=40 -> 35
        System.out.println("ceiling(40) = " + sayilar.ceiling(40));  // >=40 -> 50
        System.out.println("headSet(50) = " + sayilar.headSet(50));  // <50
        System.out.println("tailSet(50) = " + sayilar.tailSet(50));  // >=50
        System.out.println("subSet(20,65)= " + sayilar.subSet(20, 65));

        // EnumSet: enum değerlerinden oluşan, bit-maske kadar hızlı/kompakt küme.
        EnumSet<Yetki> editor = EnumSet.of(Yetki.OKU, Yetki.YAZ);
        EnumSet<Yetki> admin = EnumSet.allOf(Yetki.class);
        EnumSet<Yetki> saltOkur = EnumSet.of(Yetki.OKU);
        System.out.println("\nEditor yetkileri: " + editor);
        System.out.println("Admin yetkileri : " + admin);
        System.out.println("Editor YAZ yapabilir mi? " + editor.contains(Yetki.YAZ));
        System.out.println("Tümleyen (editor değil): " + EnumSet.complementOf(editor));

        System.out.println("""

                --- TreeSet ve EnumSet ---
                TreeSet: benzersiz + SIRALI. first/last, floor/ceiling/higher/lower (komşuluk),
                  headSet/tailSet/subSet (aralık). Erişim O(log n). "Sıralı benzersiz" gerektiğinde.
                EnumSet: yalnızca enum elemanları için; içte bit-maske -> çok hızlı ve kompakt.
                  Yetki/bayrak (flag) kümeleri için idealdir (of/allOf/noneOf/complementOf).
                Özet: hızlı -> HashSet, ekleme sırası -> LinkedHashSet, sıralı -> TreeSet, enum -> EnumSet.""");
    }
}
