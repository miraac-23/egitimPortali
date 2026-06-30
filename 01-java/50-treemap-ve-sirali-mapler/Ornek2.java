// Ornek2: LinkedHashMap (ekleme/erişim sırası, LRU önbellek) ve EnumMap.
// Çalıştırma: java Ornek2.java
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ornek2 {

    enum Gun { PZT, SAL, CAR, PER, CUM }

    public static void main(String[] args) {
        // LinkedHashMap: EKLEME sırasını korur (HashMap'in aksine).
        LinkedHashMap<String, Integer> sirali = new LinkedHashMap<>();
        sirali.put("ilk", 1); sirali.put("ikinci", 2); sirali.put("üçüncü", 3);
        System.out.println("LinkedHashMap (ekleme sırası): " + sirali);

        // LRU önbellek: accessOrder=true + removeEldestEntry ile en az kullanılanı atar.
        Map<String, String> lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, String> e) {
                return size() > 3;   // en fazla 3 eleman tut
            }
        };
        lru.put("a", "A"); lru.put("b", "B"); lru.put("c", "C");
        lru.get("a");               // 'a'ya eriş -> en yeni kullanılan olur
        lru.put("d", "D");          // kapasite aşıldı -> en eski (b) atılır
        System.out.println("LRU önbellek (a erişildi, d eklendi): " + lru.keySet());

        // EnumMap: anahtar bir enum ise çok hızlı ve kompakt (içte dizi kullanır).
        EnumMap<Gun, String> program = new EnumMap<>(Gun.class);
        program.put(Gun.PZT, "Toplantı");
        program.put(Gun.CUM, "Demo");
        System.out.println("EnumMap (enum sırasında): " + program);

        System.out.println("""

                --- LinkedHashMap ve EnumMap ---
                LinkedHashMap: HashMap hızı + EKLEME sırasını koruma. accessOrder=true ile ERİŞİM sırası ->
                  removeEldestEntry ile kolay LRU (Least Recently Used) önbellek.
                EnumMap: anahtarlar bir enum ise; içte dizi kullanır -> HashMap'ten daha hızlı ve kompakt,
                  anahtarları enum tanım sırasında tutar.
                Özet: sırasız+hızlı -> HashMap, ekleme sırası -> LinkedHashMap, sıralı -> TreeMap, enum -> EnumMap.""");
    }
}
