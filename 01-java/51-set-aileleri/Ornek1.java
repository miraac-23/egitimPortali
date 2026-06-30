// Ornek1: HashSet (benzersizlik + küme işlemleri) ve LinkedHashSet (ekleme sırası).
// Çalıştırma: java Ornek1.java
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Ornek1 {

    public static void main(String[] args) {
        // Set: TEKRAR yok. Aynı eleman ikinci kez eklenince yok sayılır.
        Set<String> kume = new HashSet<>();
        kume.add("elma"); kume.add("armut"); kume.add("elma"); // ikinci "elma" yok sayılır
        System.out.println("HashSet: " + kume + " (boyut: " + kume.size() + ")");

        // Yinelenenleri (duplicate) temizlemenin en hızlı yolu:
        List<Integer> tekrarli = List.of(1, 2, 2, 3, 3, 3, 4);
        Set<Integer> benzersiz = new HashSet<>(tekrarli);
        System.out.println("Yinelenensiz: " + benzersiz);

        // KÜME İŞLEMLERİ (set algebra):
        Set<Integer> a = new HashSet<>(Set.of(1, 2, 3, 4));
        Set<Integer> b = new HashSet<>(Set.of(3, 4, 5, 6));

        Set<Integer> birlesim = new HashSet<>(a); birlesim.addAll(b);     // A ∪ B
        Set<Integer> kesisim = new HashSet<>(a);  kesisim.retainAll(b);    // A ∩ B
        Set<Integer> fark = new HashSet<>(a);     fark.removeAll(b);       // A \ B
        System.out.println("Birleşim (A∪B): " + birlesim);
        System.out.println("Kesişim (A∩B): " + kesisim);
        System.out.println("Fark (A\\B)   : " + fark);

        // LinkedHashSet: benzersizlik + EKLEME sırasını korur.
        Set<String> sirali = new LinkedHashSet<>();
        sirali.add("zebra"); sirali.add("at"); sirali.add("kuş"); sirali.add("at");
        System.out.println("\nLinkedHashSet (ekleme sırası): " + sirali);

        System.out.println("""

                --- HashSet ve LinkedHashSet ---
                Set: benzersiz elemanlar topluluğu. contains/add/remove ortalama O(1) (hash tabanlı).
                En yaygın kullanım: yinelenenleri temizlemek ve "üyelik" (var mı?) testi.
                Küme işlemleri: addAll=birleşim, retainAll=kesişim, removeAll=fark.
                HashSet sırasız; LinkedHashSet ekleme sırasını korur; TreeSet sıralı (sonraki örnek).
                NOT: eleman sınıflarının equals()/hashCode()'u doğru olmalı (benzersizlik buna dayanır).""");
    }
}
