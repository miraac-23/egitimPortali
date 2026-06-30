// =============================================================================
//  CompactNumberFormat.java
//  Java 12 - Compact Number Formatting (JEP 357 / JDK-8188147)
// =============================================================================
//
//  NEDIR?
//  ----------------------------------------------------------------------------
//  NumberFormat.getCompactNumberInstance(...) Java 12'de geldi.
//  Buyuk sayilari, insanlarin kolayca okuyabilecegi KISA (kompakt) bicimde
//  gosterir:
//        1.000      -> "1K"   (Ingilizce, SHORT)  /  "1B" (Turkce, SHORT)
//        1.000.000  -> "1M"   (Ingilizce)         /  "1Mn" (Turkce)
//        1.000      -> "1 thousand" (Ingilizce, LONG) / "1 bin" (Turkce, LONG)
//
//  NEDEN GELDI / HANGI PROBLEMI COZER?
//  ----------------------------------------------------------------------------
//  Onceden sosyal medya beğeni, takipci, goruntuleme sayisi gibi degerleri
//  "1.2K", "3.4M" seklinde gostermek icin EL ile bolme + string birlestirme
//  yapmak gerekiyordu. Bu kod:
//    - locale'e (dile) gore DOGRU ekleri (K/M/B/Bn) bilmez,
//    - cogullasmayi/yuvarlamayi yanlis yapar,
//    - her uygulamada tekrar tekrar yazilir.
//  Compact Number Formatting bu isi locale-aware (dile duyarli) ve standart
//  bicimde halleder.
//
//  STILLER:
//    - NumberFormat.Style.SHORT : "1K", "1M", "1B"
//    - NumberFormat.Style.LONG  : "1 thousand", "1 million", "1 bin"
//
//  DERLEME / CALISTIRMA (Java 12'de KALICI; preview degil):
//        javac CompactNumberFormat.java
//        java  CompactNumberFormat
// =============================================================================

import java.text.NumberFormat;
import java.util.Locale;

public class CompactNumberFormat {

    public static void main(String[] args) {

        System.out.println("================================================");
        System.out.println(" JAVA 12 - COMPACT NUMBER FORMATTING ORNEKLERI");
        System.out.println("================================================\n");

        long[] sayilar = {123, 1_000, 1_500, 12_345, 1_000_000, 2_300_000, 1_000_000_000L};

        // ---------------------------------------------------------------------
        // ESKI YONTEM: Manuel bolme + string birlestirme
        // ---------------------------------------------------------------------
        // Dezavantajlari:
        //   - Dil/locale farkliliklarini bilmez (hep "K","M" yazar).
        //   - Yuvarlama, ondalik ve cogul kurallari elle yonetilir, hataya acik.
        //   - Her projede yeniden yazilir.
        System.out.println(">>> ESKI YONTEM (manuel bolme + string birlestirme):");
        for (long s : sayilar) {
            System.out.println("   " + s + " -> " + manuelKisalt(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // YENI YONTEM - INGILIZCE (US) locale, SHORT stil
        // ---------------------------------------------------------------------
        System.out.println(">>> YENI YONTEM - Ingilizce (US), SHORT stil:");
        NumberFormat enShort = NumberFormat.getCompactNumberInstance(
                Locale.US, NumberFormat.Style.SHORT);
        for (long s : sayilar) {
            System.out.println("   " + s + " -> " + enShort.format(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // YENI YONTEM - INGILIZCE (US) locale, LONG stil
        // ---------------------------------------------------------------------
        System.out.println(">>> YENI YONTEM - Ingilizce (US), LONG stil:");
        NumberFormat enLong = NumberFormat.getCompactNumberInstance(
                Locale.US, NumberFormat.Style.LONG);
        for (long s : sayilar) {
            System.out.println("   " + s + " -> " + enLong.format(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // YENI YONTEM - TURKCE (tr-TR) locale, SHORT stil
        // ---------------------------------------------------------------------
        System.out.println(">>> YENI YONTEM - Turkce (tr-TR), SHORT stil:");
        Locale tr = Locale.forLanguageTag("tr-TR");
        NumberFormat trShort = NumberFormat.getCompactNumberInstance(
                tr, NumberFormat.Style.SHORT);
        for (long s : sayilar) {
            System.out.println("   " + s + " -> " + trShort.format(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // YENI YONTEM - TURKCE (tr-TR) locale, LONG stil
        // ---------------------------------------------------------------------
        System.out.println(">>> YENI YONTEM - Turkce (tr-TR), LONG stil:");
        NumberFormat trLong = NumberFormat.getCompactNumberInstance(
                tr, NumberFormat.Style.LONG);
        for (long s : sayilar) {
            System.out.println("   " + s + " -> " + trLong.format(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // ONDALIK HASSASIYET: setMaximumFractionDigits ile "1.5K" gibi gosterim
        // ---------------------------------------------------------------------
        // Varsayilan olarak compact format ondalik gostermez (1.500 -> "2K").
        // Daha hassas gostermek icin maksimum ondalik basamak sayisini artiririz.
        System.out.println(">>> ONDALIK HASSASIYET (1 ondalik basamak, US/SHORT):");
        NumberFormat hassas = NumberFormat.getCompactNumberInstance(
                Locale.US, NumberFormat.Style.SHORT);
        hassas.setMaximumFractionDigits(1);
        long[] hassasSayilar = {1_500, 12_345, 2_300_000};
        for (long s : hassasSayilar) {
            System.out.println("   " + s + " -> " + hassas.format(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // GERCEK HAYAT ORNEGI 1: Sosyal medya begeni / takipci gosterimi
        // ---------------------------------------------------------------------
        System.out.println(">>> GERCEK HAYAT: Sosyal medya istatistikleri (tr-TR):");
        NumberFormat sosyal = NumberFormat.getCompactNumberInstance(
                tr, NumberFormat.Style.SHORT);
        sosyal.setMaximumFractionDigits(1);
        System.out.println("   Takipci  : " + sosyal.format(1_234_567));
        System.out.println("   Begeni   : " + sosyal.format(45_900));
        System.out.println("   Goruntulenme: " + sosyal.format(12_300_000));
        System.out.println();

        // ---------------------------------------------------------------------
        // GERCEK HAYAT ORNEGI 2: Dosya boyutu (byte -> kisa gosterim)
        // ---------------------------------------------------------------------
        System.out.println(">>> GERCEK HAYAT: Dosya boyutu gosterimi (US/SHORT):");
        NumberFormat boyut = NumberFormat.getCompactNumberInstance(
                Locale.US, NumberFormat.Style.SHORT);
        boyut.setMaximumFractionDigits(1);
        long[] boyutlar = {850, 2_048, 5_242_880, 1_073_741_824L};
        for (long b : boyutlar) {
            System.out.println("   " + b + " byte -> " + boyut.format(b) + "B");
        }

        System.out.println("\n================================================");
        System.out.println(" BITTI.");
        System.out.println("================================================");
    }

    // -------------------------------------------------------------------------
    // ESKI yontemi temsil eden manuel kisaltma metodu (locale'i bilmez!).
    // -------------------------------------------------------------------------
    private static String manuelKisalt(long sayi) {
        if (sayi < 1_000) {
            return String.valueOf(sayi);
        } else if (sayi < 1_000_000) {
            return (sayi / 1_000) + "K";
        } else if (sayi < 1_000_000_000) {
            return (sayi / 1_000_000) + "M";
        } else {
            return (sayi / 1_000_000_000) + "B";
        }
    }
}
