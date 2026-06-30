import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ============================================================================
 *  Java 10 - DEGISTIRILEMEZ (IMMUTABLE) KOLEKSIYON GELISTIRMELERI
 * ============================================================================
 *
 *  Java 10 iki onemli yetenek ekledi:
 *
 *  1) Collectors.toUnmodifiableList / toUnmodifiableSet / toUnmodifiableMap
 *     -> Stream sonucunu DOGRUDAN degistirilemez bir koleksiyon olarak toplar.
 *
 *  2) List.copyOf / Set.copyOf / Map.copyOf
 *     -> VAR OLAN bir koleksiyondan degistirilemez bir KOPYA olusturur.
 *
 *  --------------------------------------------------------------------------
 *  NEDEN GELDI? (Hangi problem?)
 *  --------------------------------------------------------------------------
 *  Java 9 "List.of(...)" gibi factory metotlar getirdi. Ama bunlar SIFIRDAN
 *  eleman sayarak immutable koleksiyon olusturur. Elimizde ZATEN bir
 *  koleksiyon varsa (orn. bir metoda parametre olarak geldi) ve bunun
 *  degistirilemez bir kopyasini istiyorsak Java 9'da kolay bir yol yoktu;
 *  eski "Collections.unmodifiableList(new ArrayList<>(...))" kalibini
 *  kullanmak zorundaydik.
 *
 *  --------------------------------------------------------------------------
 *  List.of (Java 9) ile List.copyOf (Java 10) FARKI
 *  --------------------------------------------------------------------------
 *  - List.of(a, b, c)   : elemanlari TEK TEK verirsin, sifirdan olusturur.
 *  - List.copyOf(coll)  : VAR OLAN bir koleksiyonu alir, ondan degistirilemez
 *                         bir KOPYA cikarir. Orijinal degisse bile kopya
 *                         etkilenmez (gercek defensive copy).
 *
 *  Her ikisi de SONUC olarak degistirilemez koleksiyon verir; add/remove/set
 *  cagirirsan UnsupportedOperationException firlatir.
 * ============================================================================
 */
public class ImmutableCollections {

    public static void main(String[] args) {

        System.out.println("=== 1) Collectors.toUnmodifiableList/Set/Map ===");
        unmodifiableCollectors();

        System.out.println("\n=== 2) List.copyOf / Set.copyOf / Map.copyOf ===");
        copyOfOrnegi();

        System.out.println("\n=== 3) List.of (Java 9) vs List.copyOf (Java 10) ===");
        ofVsCopyOf();

        System.out.println("\n=== 4) ESKI YONTEM vs YENI YONTEM ===");
        eskiVsYeni();

        System.out.println("\n=== 5) GERCEK HAYAT: DEFENSIVE COPY senaryosu ===");
        defensiveCopySenaryo();

        System.out.println("\n=== 6) Degistirilemezligin KANITI ===");
        degistirilemezlikKaniti();
    }

    /* ------------------------------------------------------------------ */
    /* 1) Collectors.toUnmodifiable*                                       */
    /* ------------------------------------------------------------------ */
    static void unmodifiableCollectors() {

        // ----- ESKI (Java 9): once toList, sonra unmodifiableList sar -----
        List<String> eski = Collections.unmodifiableList(
                Stream.of("elma", "armut", "kiraz")
                        .map(String::toUpperCase)
                        .collect(Collectors.toList()));

        // ----- YENI (Java 10): dogrudan degistirilemez listeye topla -----
        var yeniListe = Stream.of("elma", "armut", "kiraz")
                .map(String::toUpperCase)
                .collect(Collectors.toUnmodifiableList());

        var yeniSet = Stream.of(1, 2, 2, 3, 3, 3)
                .collect(Collectors.toUnmodifiableSet());

        var yeniMap = Stream.of("bir", "iki", "uc")
                .collect(Collectors.toUnmodifiableMap(
                        kelime -> kelime,          // anahtar
                        String::length));          // deger (uzunluk)

        System.out.println("eski (unmodifiableList) = " + eski);
        System.out.println("yeniListe = " + yeniListe);
        System.out.println("yeniSet   = " + yeniSet);
        System.out.println("yeniMap   = " + yeniMap);
    }

    /* ------------------------------------------------------------------ */
    /* 2) copyOf metotlari                                                 */
    /* ------------------------------------------------------------------ */
    static void copyOfOrnegi() {
        // Var olan, DEGISTIRILEBILIR bir koleksiyon:
        var kaynakListe = new ArrayList<>(List.of("a", "b", "c"));

        // copyOf ile degistirilemez bir KOPYA:
        var kopyaListe = List.copyOf(kaynakListe);

        var kaynakMap = new LinkedHashMap<String, Integer>();
        kaynakMap.put("x", 1);
        kaynakMap.put("y", 2);
        var kopyaMap = Map.copyOf(kaynakMap);

        var kopyaSet = Set.copyOf(List.of(10, 20, 30, 30)); // tekrar elenir

        System.out.println("kopyaListe = " + kopyaListe);
        System.out.println("kopyaMap   = " + kopyaMap);
        System.out.println("kopyaSet   = " + kopyaSet);
    }

    /* ------------------------------------------------------------------ */
    /* 3) of vs copyOf farkini KANITLA                                     */
    /* ------------------------------------------------------------------ */
    static void ofVsCopyOf() {
        var orijinal = new ArrayList<>(List.of("ilk", "ikinci"));

        // copyOf: orijinalin O ANKI halinin BAGIMSIZ kopyasi
        var kopya = List.copyOf(orijinal);

        // Orijinali degistirelim:
        orijinal.add("sonradan eklendi");

        System.out.println("orijinal (degisti) = " + orijinal);
        System.out.println("kopya (etkilenmedi)= " + kopya
                + "  <-- copyOf bagimsiz kopya aldigi icin degismedi");

        // List.of ise zaten sifirdan olusturur; elemanlari tek tek veririz:
        var sifirdan = List.of("p", "q", "r");
        System.out.println("List.of ile sifirdan = " + sifirdan);
    }

    /* ------------------------------------------------------------------ */
    /* 4) ESKI vs YENI - ayni isi yapan iki yaklasim                       */
    /* ------------------------------------------------------------------ */
    static void eskiVsYeni() {
        var girdi = Arrays.asList("kirmizi", "yesil", "mavi");

        // ----- ESKI YONTEM (Java 8 / 9) -----
        // 1) Yeni bir ArrayList olustur (kopya), 2) unmodifiable ile sar.
        List<String> eskiSonuc =
                Collections.unmodifiableList(new ArrayList<>(girdi));

        // ----- YENI YONTEM (Java 10) - tek satir -----
        var yeniSonuc = List.copyOf(girdi);

        System.out.println("ESKI: Collections.unmodifiableList(new ArrayList<>(...)) = "
                + eskiSonuc);
        System.out.println("YENI: List.copyOf(...) = " + yeniSonuc);
        System.out.println("Ikisi de icerikce esit mi? " + eskiSonuc.equals(yeniSonuc));
    }

    /* ------------------------------------------------------------------ */
    /* 5) GERCEK HAYAT: DEFENSIVE COPY                                     */
    /* ------------------------------------------------------------------ */
    /*
     * Senaryo: Bir "Siparis" sinifi disaridan bir urun listesi aliyor.
     * Eger listeyi DOGRUDAN saklarsak, disaridaki kod sonradan listeyi
     * degistirip nesnemizin ic durumunu bozabilir. Bunu engellemek icin
     * List.copyOf ile DEGISTIRILEMEZ bir KOPYA sakliyoruz (defensive copy).
     */
    static void defensiveCopySenaryo() {
        var disaridanGelenler = new ArrayList<>(List.of("Klavye", "Mouse"));

        var siparis = new Siparis("SIP-001", disaridanGelenler);

        // Disaridaki kod listeyi degistirmeye calisir:
        disaridanGelenler.add("Korsanlik denemesi!");

        System.out.println("Disaridaki liste = " + disaridanGelenler);
        System.out.println("Siparis urunleri (KORUNDU) = " + siparis.getUrunler());
    }

    /** Defensive copy ornegi icin saglam (immutable) Siparis sinifi. */
    static final class Siparis {
        private final String no;
        private final List<String> urunler; // degistirilemez kopya

        Siparis(String no, List<String> urunler) {
            this.no = no;
            // KRITIK: gelen referansi DOGRUDAN saklamak yerine kopyala.
            this.urunler = List.copyOf(urunler);
        }

        String getNo() { return no; }

        // Geri donerken zaten degistirilemez liste donuyoruz; ek koruma gerekmez.
        List<String> getUrunler() { return urunler; }
    }

    /* ------------------------------------------------------------------ */
    /* 6) Degistirilemezligin KANITI                                       */
    /* ------------------------------------------------------------------ */
    static void degistirilemezlikKaniti() {
        var liste = List.copyOf(List.of("a", "b"));
        try {
            liste.add("c"); // UnsupportedOperationException firlatir
        } catch (UnsupportedOperationException e) {
            System.out.println("Beklenen hata yakalandi: add() yapilamaz "
                    + "(UnsupportedOperationException). Liste degistirilemez.");
        }
    }
}
