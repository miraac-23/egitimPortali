// Ornek1: HashMap — anahtar-değer temel işlemleri ve gezinme.
// Çalıştırma: java Ornek1.java
import java.util.HashMap;
import java.util.Map;

public class Ornek1 {

    public static void main(String[] args) {
        Map<String, Integer> stok = new HashMap<>();

        // Ekleme/güncelleme (aynı anahtar -> değer güncellenir)
        stok.put("klavye", 10);
        stok.put("mouse", 25);
        stok.put("monitör", 5);
        stok.put("klavye", 12);   // var olan anahtarı GÜNCELLER
        System.out.println("Stok: " + stok);

        // Erişim
        System.out.println("mouse: " + stok.get("mouse"));
        System.out.println("kulaklık: " + stok.get("kulaklık"));              // yoksa null
        System.out.println("kulaklık (varsayılan): " + stok.getOrDefault("kulaklık", 0)); // null yerine 0

        // Var mı? / sil
        System.out.println("klavye var mı? " + stok.containsKey("klavye"));
        System.out.println("5 değeri var mı? " + stok.containsValue(5));
        stok.remove("monitör");
        System.out.println("Silme sonrası: " + stok);

        // Gezinme: entrySet (anahtar+değer birlikte — en verimli)
        System.out.println("\nTüm kayıtlar:");
        for (Map.Entry<String, Integer> e : stok.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        // keySet / values ayrı ayrı da gezilebilir; forEach de var:
        stok.forEach((k, v) -> System.out.println("  forEach: " + k + "=" + v));

        System.out.println("""

                --- HashMap temelleri ---
                Anahtar -> değer eşlemesi; anahtarlar BENZERSİZdir (aynı anahtar değeri günceller).
                put/get/getOrDefault/containsKey/remove en sık metotlardır; erişim ortalama O(1).
                SIRA GARANTİSİ YOKTUR (hash sırası). Ekleme sırası için LinkedHashMap, sıralı için TreeMap.
                Gezinme: entrySet() (anahtar+değer), keySet(), values(), forEach.""");
    }
}
