// Ornek1: Iterator — bir koleksiyonu güvenle gezmek ve gezerken eleman silmek.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Ornek1 {

    public static void main(String[] args) {
        List<String> liste = new ArrayList<>(List.of("elma", "armut", "", "kiraz", "", "muz"));

        // Iterator: hasNext() -> next() döngüsü. for-each'in arkasındaki mekanizma budur.
        Iterator<String> it = liste.iterator();
        System.out.print("Gezinti: ");
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        // GEZERKEN SİLME: for-each içinde liste.remove(...) -> ConcurrentModificationException!
        // Doğrusu: iterator.remove() ile güvenli silme.
        Iterator<String> it2 = liste.iterator();
        while (it2.hasNext()) {
            if (it2.next().isEmpty()) {
                it2.remove();   // güvenli: iterator üzerinden sil
            }
        }
        System.out.println("Boşlar silindikten sonra: " + liste);

        // Aynı işi modern yol: removeIf (iç tarafta iterator kullanır)
        liste.removeIf(s -> s.length() > 4);
        System.out.println("4+ harfliler silindikten sonra: " + liste);

        System.out.println("""

                --- Iterator ---
                Iterator, bir koleksiyonu tipinden bağımsız gezmenin standart yoludur (hasNext/next/remove).
                for-each döngüsü aslında arka planda Iterator kullanır.
                Gezerken eleman silmek için MUTLAKA iterator.remove() kullan; koleksiyonu doğrudan
                değiştirmek 'fail-fast' davranışıyla ConcurrentModificationException atar.
                Pratikte silme için 'removeIf' en temiz yoldur.""");
    }
}
