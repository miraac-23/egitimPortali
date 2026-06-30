// Ornek1: TreeMap — anahtarları SIRALI tutar, aralık ve komşuluk sorguları sağlar.
// Çalıştırma: java Ornek1.java
import java.util.TreeMap;

public class Ornek1 {

    public static void main(String[] args) {
        TreeMap<Integer, String> notlar = new TreeMap<>();
        notlar.put(85, "Ada"); notlar.put(60, "Can");
        notlar.put(95, "Derya"); notlar.put(72, "Burak");

        // Anahtarlar her zaman SIRALI (artan) tutulur:
        System.out.println("Sıralı: " + notlar);
        System.out.println("En düşük: " + notlar.firstKey() + ", En yüksek: " + notlar.lastKey());

        // Komşuluk sorguları (navigation):
        System.out.println("floorKey(80)   = " + notlar.floorKey(80));    // <=80 en büyük -> 72
        System.out.println("ceilingKey(80) = " + notlar.ceilingKey(80));  // >=80 en küçük -> 85
        System.out.println("higherKey(85)  = " + notlar.higherKey(85));   // >85 en küçük -> 95
        System.out.println("lowerKey(85)   = " + notlar.lowerKey(85));    // <85 en büyük -> 72

        // Aralık (range) görünümleri:
        System.out.println("headMap(85)  (85'ten küçük): " + notlar.headMap(85));
        System.out.println("tailMap(85)  (85 ve üstü)  : " + notlar.tailMap(85));
        System.out.println("subMap(70,90)(70-90 arası) : " + notlar.subMap(70, 90));

        // Ters sıra:
        System.out.println("descendingMap: " + notlar.descendingMap());

        System.out.println("""

                --- TreeMap ---
                Anahtarları SIRALI tutar (doğal sıra veya verilen Comparator). Erişim O(log n) (kırmızı-siyah ağaç).
                Ekstra güç: firstKey/lastKey, floor/ceiling/higher/lowerKey (komşuluk),
                headMap/tailMap/subMap (aralık), descendingMap (ters).
                Ne zaman: "şu değere en yakın", "şu aralıktakiler", "sıralı gez" gerektiğinde HashMap yetmez.""");
    }
}
