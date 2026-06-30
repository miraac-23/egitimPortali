/*
 * =============================================================================
 *  Java 14 - SWITCH EXPRESSIONS (ARTIK KALICI / STANDART)
 *  JEP 361: Switch Expressions
 * =============================================================================
 *
 *  PREVIEW -> KALICI EVRIMI (cok onemli):
 *    - Java 12: Switch Expressions ILK preview olarak geldi.
 *    - Java 13: 2. preview oldu ("yield" anahtar kelimesi eklendi).
 *    - Java 14: ARTIK KALICI! Standart dilin parcasi.
 *
 *  Bu, su anlama gelir:
 *    -> Java 14 ve sonrasinda switch expressions kullanmak icin
 *       "--enable-preview" bayragina ARTIK GEREK YOKTUR.
 *    -> Normal "javac Dosya.java" ile derlenir.
 *
 *  Derleme/Calistirma (Java 14+):
 *    javac SwitchExpressionsKalici.java
 *    java  SwitchExpressionsKalici
 * =============================================================================
 */
public class SwitchExpressionsKalici {

    // ---- Ornek enum'lar (gercek hayat senaryolari icin) ----
    enum Gun { PAZARTESI, SALI, CARSAMBA, PERSEMBE, CUMA, CUMARTESI, PAZAR }

    enum MusteriSegmenti { STANDART, GUMUS, ALTIN, PLATIN }

    enum IslemTipi { PARA_CEKME, PARA_YATIRMA, HAVALE, EFT, DOVIZ_ALIM }

    public static void main(String[] args) {
        System.out.println("=== Java 14: Switch Expressions (KALICI) ===\n");

        // -------------------------------------------------------------------
        // 1) ESKI YOL vs YENI YOL: Gun -> "hafta ici mi?" bilgisi
        // -------------------------------------------------------------------
        // ESKI (klasik switch deyimi): degisken disarida tanimlanir,
        // her case icinde elle atanir, "break" unutma riski vardir.
        Gun bugun = Gun.CUMARTESI;

        String eskiSonuc;                       // once tanimla
        switch (bugun) {
            case CUMARTESI:
            case PAZAR:
                eskiSonuc = "Hafta sonu";
                break;                          // break unutulursa fall-through!
            default:
                eskiSonuc = "Hafta ici";
                break;
        }
        System.out.println("ESKI switch deyimi -> " + eskiSonuc);

        // YENI (switch EXPRESSION): dogrudan deger DONDURUR, break YOK,
        // fall-through riski YOK. Coklu case virgulle yazilir.
        String yeniSonuc = switch (bugun) {
            case CUMARTESI, PAZAR -> "Hafta sonu";   // -> oku otomatik break saglar
            default               -> "Hafta ici";
        };
        System.out.println("YENI switch expression -> " + yeniSonuc + "\n");

        // -------------------------------------------------------------------
        // 2) Deger dondurme + degiskene atama (ay -> gun sayisi)
        // -------------------------------------------------------------------
        int ay = 2; // Subat
        int gunSayisi = switch (ay) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11           -> 30;
            case 2                     -> 28;   // (artik yil ihmal edildi)
            default -> throw new IllegalArgumentException("Gecersiz ay: " + ay);
        };
        System.out.println(ay + ". ayda " + gunSayisi + " gun var.\n");

        // -------------------------------------------------------------------
        // 3) "yield" ile cok satirli case blogu (deger dondurmek icin)
        // -------------------------------------------------------------------
        // Bir case'de birden fazla satir is yapip SONRA deger dondurmek
        // gerekirse "{ ... yield deger; }" kullanilir.
        Gun gun = Gun.CUMA;
        int kalanIsGunu = switch (gun) {
            case PAZARTESI -> 5;
            case SALI      -> 4;
            case CARSAMBA  -> 3;
            case PERSEMBE  -> 2;
            case CUMA -> {
                // Cok satirli hesaplama yapilabilir...
                System.out.println("  (Cuma gunu: hafta sonuna 1 gun kaldi)");
                int sonuc = 1;
                yield sonuc;            // yield ile deger dondur
            }
            case CUMARTESI, PAZAR -> 0;
        };
        System.out.println("Hafta sonuna kalan is gunu: " + kalanIsGunu + "\n");

        // -------------------------------------------------------------------
        // GERCEK HAYAT ORNEGI 1: E-ticaret indirim orani (musteri segmenti)
        // -------------------------------------------------------------------
        System.out.println("--- Gercek Hayat: E-ticaret indirim orani ---");
        for (MusteriSegmenti s : MusteriSegmenti.values()) {
            double indirim = indirimOrani(s);
            System.out.printf("  %-8s -> %%%.0f indirim%n", s, indirim * 100);
        }
        System.out.println();

        // -------------------------------------------------------------------
        // GERCEK HAYAT ORNEGI 2: Bankacilik islem komisyonu
        // -------------------------------------------------------------------
        System.out.println("--- Gercek Hayat: Banka islem komisyonu (1000 TL islem) ---");
        double tutar = 1000.0;
        for (IslemTipi t : IslemTipi.values()) {
            double komisyon = komisyonHesapla(t, tutar);
            System.out.printf("  %-12s -> %.2f TL komisyon%n", t, komisyon);
        }
    }

    /**
     * Musteri segmentine gore indirim orani dondurur.
     * Switch expression sayesinde tek satirda, fall-through riski olmadan.
     */
    static double indirimOrani(MusteriSegmenti segment) {
        return switch (segment) {
            case STANDART -> 0.00;   // %0
            case GUMUS    -> 0.05;   // %5
            case ALTIN    -> 0.10;   // %10
            case PLATIN   -> 0.20;   // %20
        };
        // NOT: enum'in tum degerleri kapsanmissa "default" gerekmez;
        // derleyici "exhaustiveness" (tamlik) kontrolu yapar.
    }

    /**
     * Islem tipine ve tutara gore komisyon hesaplar.
     * Cok satirli mantik gerektiren case'lerde "yield" kullanilir.
     */
    static double komisyonHesapla(IslemTipi tip, double tutar) {
        return switch (tip) {
            case PARA_YATIRMA -> 0.0;                 // ucretsiz
            case PARA_CEKME   -> 2.0;                 // sabit ucret
            case HAVALE       -> tutar * 0.001;       // binde 1
            case EFT          -> {
                double oran = tutar * 0.002;          // binde 2
                double minUcret = 3.0;                // en az 3 TL
                yield Math.max(oran, minUcret);
            }
            case DOVIZ_ALIM   -> tutar * 0.005;       // binde 5
        };
    }
}
