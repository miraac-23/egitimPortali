// Ornek2: Set ve Map — tekilleştirme ve anahtar-değer eşlemesi.
// Çalıştırma: java Ornek2.java
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;

public class Ornek2 {

    public static void main(String[] args) {
        // --- Set: tekrar etmeyen elemanlar ---
        Set<String> etiketler = new HashSet<>();
        etiketler.add("java");
        etiketler.add("spring");
        etiketler.add("java"); // yok sayılır (zaten var)
        System.out.println("Set (tekilleştirilmiş): " + etiketler);
        System.out.println("Eleman sayısı: " + etiketler.size());

        // --- Map: anahtar -> değer eşlemesi ---
        Map<String, Integer> stok = new HashMap<>();
        stok.put("klavye", 12);
        stok.put("mouse", 30);
        stok.put("monitor", 5);
        stok.put("klavye", 15); // aynı anahtar -> değeri günceller

        System.out.println("\nStok haritası:");
        for (Map.Entry<String, Integer> e : stok.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        System.out.println("klavye stoğu: " + stok.get("klavye"));
        System.out.println("kulaklik var mı? " + stok.containsKey("kulaklik"));
        System.out.println("kulaklik (yoksa 0): " + stok.getOrDefault("kulaklik", 0));

        // --- Pratik: kelime frekansı (Map + getOrDefault) ---
        String metin = "kara kara kar kara kar kus";
        Map<String, Integer> frekans = new TreeMap<>(); // TreeMap: anahtarları sıralı tutar
        for (String kelime : metin.split(" ")) {
            frekans.put(kelime, frekans.getOrDefault(kelime, 0) + 1);
        }
        System.out.println("\nKelime frekansı (sıralı): " + frekans);
    }
}
