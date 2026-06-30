import java.util.List;

/**
 * JAVA 21 - PATTERN MATCHING FOR SWITCH (JEP 441) - KALICI
 * =========================================================
 *
 * Bu dosya, switch icinde desen eslestirmeyi (KALICI) gosterir:
 *   - tur desenleri (type patterns)
 *   - korumali desenler (guarded patterns: 'when')
 *   - null'in switch icinde ele alinmasi
 *   - sealed (muhurlu) hiyerarsi ile tamlik (exhaustiveness)
 *
 * EVRIM: Java 17 (1.) -> 18 (2.) -> 19 (3.) -> 20 (4.) -> 21 (KALICI)
 *
 * --- DERLEME / CALISTIRMA (KALICI; --enable-preview GEREKMEZ) ---
 *   javac PatternMatchingSwitchKalici.java
 *   java  PatternMatchingSwitchKalici
 */
public class PatternMatchingSwitchKalici {

    // Veri-odakli (data-oriented) modelleme icin sealed hiyerarsi
    sealed interface Odeme permits KrediKarti, Havale, Kripto {}
    record KrediKarti(String numara, double tutar) implements Odeme {}
    record Havale(String iban, double tutar) implements Odeme {}
    record Kripto(String cuzdan, double tutar, String birim) implements Odeme {}

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: PATTERN MATCHING FOR SWITCH (KALICI) ===\n");

        ornek1_turVeGuarded();
        ornek2_nullIsleme();
        ornek3_sealedTamlik();
    }

    /**
     * Tur desenleri + korumali desenler (when).
     * Object alip turune ve degerine gore davranir.
     */
    static void ornek1_turVeGuarded() {
        System.out.println("--- Ornek 1: Tur + korumali (when) desenler ---");
        for (Object o : List.of(42, -7, "Merhaba", 3.14, List.of(1, 2, 3))) {
            String aciklama = switch (o) {
                // korumali desen: tur eslesir VE kosul saglanirsa
                case Integer i when i > 0 -> "pozitif tam sayi: " + i;
                case Integer i            -> "pozitif olmayan tam sayi: " + i;
                case String s             -> "metin (" + s.length() + " karakter): " + s;
                case Double d             -> "ondalik: " + d;
                default                   -> "diger tur: " + o.getClass().getSimpleName();
            };
            System.out.println("  " + aciklama);
        }
        System.out.println();
    }

    /**
     * null artik switch icinde ACIKCA ele alinabilir (case null).
     * ESKIDEN switch(null) -> NullPointerException firlatirdi.
     */
    static void ornek2_nullIsleme() {
        System.out.println("--- Ornek 2: null'in switch icinde ele alinmasi ---");
        for (Object o : java.util.Arrays.asList("dolu", null, 5)) {
            String sonuc = switch (o) {
                case null      -> "deger BOS (null)";
                case String s  -> "metin: " + s;
                default        -> "diger: " + o;
            };
            System.out.println("  " + sonuc);
        }
        System.out.println();
    }

    /**
     * sealed arayuz + switch -> derleyici TAMLIK (exhaustiveness) garantisi verir.
     * Tum alt turler kapsandiginda 'default' GEREKMEZ. Yeni bir alt tur eklenirse
     * derleyici switch'i guncellemeniz icin HATA verir (guvenli evrim).
     */
    static void ornek3_sealedTamlik() {
        System.out.println("--- Ornek 3: sealed hiyerarsi + tamlik (record pattern ile) ---");
        List<Odeme> odemeler = List.of(
                new KrediKarti("1234-5678-...", 250.0),
                new Havale("TR12 0006 ...", 1000.0),
                new Kripto("0xABCD...", 0.05, "BTC")
        );

        for (Odeme odeme : odemeler) {
            // record pattern + switch: tur eslesir, alanlar yikilir, 'default' yok
            String mesaj = switch (odeme) {
                case KrediKarti(String numara, double tutar) ->
                        "Kredi karti odemesi: " + tutar + " TL (kart: " + numara + ")";
                case Havale(String iban, double tutar) ->
                        "Havale: " + tutar + " TL (IBAN: " + iban + ")";
                case Kripto(String cuzdan, double tutar, String birim) ->
                        "Kripto: " + tutar + " " + birim + " (cuzdan: " + cuzdan + ")";
                // sealed + tum alt turler kapsandi -> default GEREKMEZ
            };
            System.out.println("  " + mesaj);
        }
        System.out.println();
    }
}
