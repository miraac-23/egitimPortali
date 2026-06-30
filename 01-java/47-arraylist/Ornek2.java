// Ornek2: ArrayList — toplu işlemler, sıralama, subList, toArray, dönüşümler.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        List<Integer> sayilar = new ArrayList<>(List.of(5, 3, 9, 1, 7, 3, 8));

        // Toplu işlemler
        sayilar.addAll(List.of(10, 2));
        System.out.println("addAll sonrası: " + sayilar);
        sayilar.removeIf(n -> n % 2 == 0);   // çiftleri sil (koşullu silme)
        System.out.println("Tekler: " + sayilar);

        // Sıralama
        sayilar.sort(Comparator.naturalOrder());
        System.out.println("Sıralı: " + sayilar);
        Collections.reverse(sayilar);
        System.out.println("Ters: " + sayilar);
        System.out.println("Max/Min: " + Collections.max(sayilar) + " / " + Collections.min(sayilar));

        // Alt liste (görünüm) ve diziye çevirme
        List<Integer> ilkUc = sayilar.subList(0, 3);
        System.out.println("subList(0,3): " + ilkUc);
        Integer[] dizi = sayilar.toArray(new Integer[0]);
        System.out.println("toArray uzunluk: " + dizi.length);

        // Stream ile dönüşüm
        List<String> etiketler = sayilar.stream().map(n -> "#" + n).toList();
        System.out.println("map sonucu: " + etiketler);

        System.out.println("""

                --- ArrayList toplu işlemler ---
                addAll/removeAll/retainAll: toplu ekleme/silme/kesişim.
                removeIf(predicate): koşullu silme (güvenli, iç tarafta iterator).
                Sıralama: list.sort(cmp) veya Collections.sort; Collections.reverse/max/min.
                subList(from,to): listenin bir GÖRÜNÜMÜ (değişiklik ana listeyi etkiler).
                Performans: rastgele erişim hızlı; ortaya/başa ekleme-silme elemanları kaydırır (O(n)).""");
    }
}
