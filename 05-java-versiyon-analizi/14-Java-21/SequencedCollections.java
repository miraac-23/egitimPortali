import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.TreeMap;

/**
 * JAVA 21 - SEQUENCED COLLECTIONS (JEP 431) - KALICI
 * ===================================================
 *
 * Bu dosya, Java 21 ile gelen UC yeni arayuzu gosterir:
 *   - SequencedCollection : getFirst/getLast/addFirst/addLast/removeFirst/removeLast/reversed
 *   - SequencedSet        : SequencedCollection + Set
 *   - SequencedMap        : firstEntry/lastEntry/putFirst/putLast/reversed/sequencedKeySet...
 *
 * NEDEN GELDI?
 *   20+ yildir "ilk/son elemana ve ters siraya tutarli erisim" icin ortak bir
 *   API yoktu. List'te get(0)/get(size-1), Deque'te getFirst/getLast, LinkedHashSet'te
 *   ise son elemani almak cok zahmetliydi. Artik hepsinde AYNI API var.
 *
 * --- DERLEME / CALISTIRMA (KALICI; --enable-preview GEREKMEZ) ---
 *   javac SequencedCollections.java
 *   java  SequencedCollections
 */
public class SequencedCollections {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: SEQUENCED COLLECTIONS (KALICI) ===\n");

        listeOrnegi();
        setOrnegi();
        mapOrnegi();
        eskiVsYeni();
    }

    /** List zaten SequencedCollection'dir. */
    static void listeOrnegi() {
        System.out.println("--- SequencedCollection (List) ---");
        SequencedCollection<String> liste = new ArrayList<>(List.of("A", "B", "C"));

        System.out.println("  Ilk eleman  (getFirst): " + liste.getFirst());
        System.out.println("  Son eleman  (getLast) : " + liste.getLast());

        liste.addFirst("BAS");   // basa ekle
        liste.addLast("SON");    // sona ekle
        System.out.println("  Ekleme sonrasi        : " + liste);

        System.out.println("  Ters cevrilmis (reversed): " + liste.reversed());

        liste.removeFirst();
        liste.removeLast();
        System.out.println("  Bas/son silme sonrasi : " + liste + "\n");
    }

    /** LinkedHashSet artik SequencedSet'tir (eklenme sirasini korur). */
    static void setOrnegi() {
        System.out.println("--- SequencedSet (LinkedHashSet) ---");
        SequencedSet<String> sonGoruntulenen = new LinkedHashSet<>();
        sonGoruntulenen.add("Telefon");
        sonGoruntulenen.add("Laptop");
        sonGoruntulenen.add("Kulaklik");

        // ESKIDEN LinkedHashSet'te son elemana erismek cok zordu!
        System.out.println("  En ESKI goruntulenen (getFirst): " + sonGoruntulenen.getFirst());
        System.out.println("  En YENI goruntulenen (getLast) : " + sonGoruntulenen.getLast());
        System.out.println("  Ters sira (en yeni -> en eski) : " + sonGoruntulenen.reversed());
        System.out.println();
    }

    /** LinkedHashMap ve TreeMap artik SequencedMap'tir. */
    static void mapOrnegi() {
        System.out.println("--- SequencedMap (LinkedHashMap) ---");
        SequencedMap<String, Integer> sayfaZiyaretleri = new LinkedHashMap<>();
        sayfaZiyaretleri.put("anasayfa", 1200);
        sayfaZiyaretleri.put("urunler", 850);
        sayfaZiyaretleri.put("iletisim", 300);

        System.out.println("  Ilk giris (firstEntry): " + sayfaZiyaretleri.firstEntry());
        System.out.println("  Son giris (lastEntry) : " + sayfaZiyaretleri.lastEntry());

        sayfaZiyaretleri.putFirst("kampanya", 5000); // basa ekle
        System.out.println("  putFirst sonrasi      : " + sayfaZiyaretleri);
        System.out.println("  Ters sirali map       : " + sayfaZiyaretleri.reversed());

        // TreeMap de SequencedMap'tir (sirali map)
        SequencedMap<String, Integer> sirali = new TreeMap<>(sayfaZiyaretleri);
        System.out.println("  TreeMap ilk anahtar   : " + sirali.firstEntry().getKey());
        System.out.println();
    }

    static void eskiVsYeni() {
        System.out.println("--- ESKI vs YENI ozet ---");
        List<String> l = List.of("ilk", "orta", "son");
        System.out.println("  ESKI son eleman: l.get(l.size()-1) = " + l.get(l.size() - 1));
        System.out.println("  YENI son eleman: l.getLast()        = " + l.getLast());
        System.out.println("""

                  * Tum sirali koleksiyonlarda artik AYNI, tutarli API.
                  * getFirst/getLast bos koleksiyonda NoSuchElementException firlatir.
                  * reversed() bir GORUNUM (view) dondurur; ozgun koleksiyona baglidir.
                """);
    }
}
