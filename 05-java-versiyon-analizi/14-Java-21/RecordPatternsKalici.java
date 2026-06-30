import java.util.List;

/**
 * JAVA 21 - RECORD PATTERNS (JEP 440) - KALICI
 * =============================================
 *
 * Bu dosya, record yikimini (destructuring) KALICI haliyle, ozellikle
 * IC ICE (nested) yikim ve isimsiz desen (_) ile gosterir.
 *
 * EVRIM: Java 19 (1. preview) -> 20 (2. preview) -> 21 (KALICI)
 *
 * NOT: Isimsiz desen / degisken (_) Java 21'de hala PREVIEW'dir (JEP 443),
 *      Java 22'de kalici oldu. Bu dosyada '_' kullanan KISIM ayri bir metotta
 *      tutulmustur ve nasil derleneceği belirtilmistir. Diger her sey KALICIDIR.
 *
 * --- DERLEME / CALISTIRMA ---
 *   KALICI kisimlar icin:
 *     javac RecordPatternsKalici.java
 *     java  RecordPatternsKalici
 *
 *   '_' (isimsiz desen) iceren satirlar Java 21'de PREVIEW gerektirir.
 *   Bu dosyada o ozellik YORUM icinde gosterilmistir ki dosya --enable-preview
 *   olmadan da derlensin. Denemek isterseniz yorumu acip soyle derleyin:
 *     javac --release 21 --enable-preview RecordPatternsKalici.java
 *     java  --enable-preview RecordPatternsKalici
 */
public class RecordPatternsKalici {

    // Ic ice (nested) veri modeli
    record Adres(String sehir, String ulke) {}
    record Kullanici(String ad, Adres adres) {}
    record Siparis(Kullanici musteri, double tutar) {}

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: RECORD PATTERNS (KALICI) ===\n");

        ornek1_basitYikim();
        ornek2_icIceYikim();
        ornek3_switchIleYikim();
        ornek4_isimsizDesenNotu();
    }

    /** Basit yikim: bir record'un alanlarini dogrudan degiskenlere cikar. */
    static void ornek1_basitYikim() {
        System.out.println("--- Ornek 1: Basit yikim ---");
        Object o = new Adres("Istanbul", "Turkiye");

        // ESKI: o instanceof Adres a -> a.sehir(), a.ulke()
        // YENI: dogrudan yikim
        if (o instanceof Adres(String sehir, String ulke)) {
            System.out.println("  Sehir: " + sehir + ", Ulke: " + ulke);
        }
        System.out.println();
    }

    /**
     * IC ICE YIKIM: Record patterns'in en guclu yani.
     * Siparis -> Kullanici -> Adres zincirini TEK desende cozer.
     */
    static void ornek2_icIceYikim() {
        System.out.println("--- Ornek 2: Ic ice yikim (nested destructuring) ---");
        Object o = new Siparis(
                new Kullanici("Ayse", new Adres("Izmir", "Turkiye")),
                1499.90);

        // Tek satirda 4 seviye derinlige inip alanlari cikariyoruz
        if (o instanceof Siparis(Kullanici(String ad, Adres(String sehir, String ulke)),
                                 double tutar)) {
            System.out.printf("  %s adli musteri (%s/%s) %.2f TL siparis verdi.%n",
                    ad, sehir, ulke, tutar);
        }
        System.out.println();
    }

    /** switch + record pattern: tur ayrimi ve yikim bir arada. */
    static void ornek3_switchIleYikim() {
        System.out.println("--- Ornek 3: switch + record pattern ---");
        List<Object> nesneler = List.of(
                new Adres("Ankara", "Turkiye"),
                new Kullanici("Mehmet", new Adres("Bursa", "Turkiye"))
        );

        for (Object o : nesneler) {
            String mesaj = switch (o) {
                case Adres(String s, String u) -> "Adres: " + s + " / " + u;
                case Kullanici(String ad, Adres(String s, String u)) ->
                        "Kullanici: " + ad + " (" + s + ")";
                default -> "bilinmeyen";
            };
            System.out.println("  " + mesaj);
        }
        System.out.println();
    }

    /**
     * ISIMSIZ DESEN (_) - Java 21'de PREVIEW (JEP 443), Java 22'de kalici.
     * Kullanmadigimiz bilesenleri '_' ile gosterip niyeti netlestiririz.
     *
     * Asagidaki kod --enable-preview ile derlenir. Bu dosya varsayilan olarak
     * preview'siz derlensin diye ornek YORUM icinde verilmistir:
     *
     *   if (o instanceof Siparis(Kullanici(String ad, _), double tutar)) {
     *       // adres umurumuzda degil -> '_' ile atladik
     *       System.out.println(ad + " -> " + tutar);
     *   }
     */
    static void ornek4_isimsizDesenNotu() {
        System.out.println("--- Ornek 4: Isimsiz desen (_) NOTU ---");
        System.out.println("""
                  Java 21'de '_' (isimsiz desen/degisken) PREVIEW'dir (JEP 443).
                  Kullanilmayan bilesenleri '_' ile atlamayi saglar; ornegin:
                      o instanceof Siparis(Kullanici(String ad, _), double tutar)
                  Denemek icin: javac --release 21 --enable-preview ...
                  Java 22'de bu ozellik KALICI olmustur.
                """);
    }
}
