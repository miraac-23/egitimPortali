/*
 * =============================================================================
 *  Java 14 - HELPFUL NULLPOINTEREXCEPTIONS
 *  JEP 358: Helpful NullPointerExceptions
 * =============================================================================
 *
 *  DURUM:
 *    - Java 14: Geldi. Etkinlestirme bayragi:
 *               java -XX:+ShowCodeDetailsInExceptionMessages HelpfulNullPointer
 *    - Java 15 ve sonrasi: VARSAYILAN olarak ACIK (bayraga gerek YOK).
 *
 *  Bu ornek Java 15+ (ornegin Java 21) ile dogrudan calistiginda yardimci
 *  NPE mesajini gosterir. Java 14'te yukaridaki bayrak gerekir.
 *
 *  PROBLEM:
 *    a.getB().getC().getD() gibi ZINCIRLEME erisimlerde, eski NPE mesaji
 *    sadece satir numarasi verirdi. O satirda birden cok "null adayi" varsa
 *    HANGISININ null oldugunu tahmin etmeniz gerekirdi -> saatlerce debug.
 *
 *  COZUM:
 *    Java 14+ NPE mesaji TAM OLARAK hangi parcanin null oldugunu soyler:
 *      'Cannot invoke "Sehir.getPostaKodu()" because the return value of
 *       "Adres.getSehir()" is null'
 * =============================================================================
 */
public class HelpfulNullPointer {

    public static void main(String[] args) {
        System.out.println("=== Java 14: Helpful NullPointerExceptions ===\n");
        System.out.println("(Java 14'te calistirmak icin: "
                + "java -XX:+ShowCodeDetailsInExceptionMessages HelpfulNullPointer)");
        System.out.println("(Java 15+ varsayilan ACIK; ek bayrak gerekmez)\n");

        // -------------------------------------------------------------------
        // GERCEK HAYAT: Ic ice nesne grafigi
        //   Kullanici -> Adres -> Sehir -> postaKodu
        // Bilerek bir halkayi null birakiyoruz.
        // -------------------------------------------------------------------

        // SENARYO 1: Sehir.getPostaKodu() null degil ama Adres.getSehir() null.
        // Yani zincir ORTASINDA bir halka kopuk.
        System.out.println("--- SENARYO 1: Adres var, ama Sehir null ---");
        Kullanici k1 = new Kullanici(
                "Ayse",
                new Adres("Bagdat Cad. No:10", /* sehir = */ null) // <-- sehir NULL!
        );
        try {
            // Zincirleme erisim: k1.adres().sehir().postaKodu()
            String postaKodu = k1.adres().sehir().postaKodu();
            System.out.println("Posta kodu: " + postaKodu);
        } catch (NullPointerException e) {
            // Java 14+ ile mesaj TAM olarak hangi parcanin null oldugunu soyler.
            System.out.println("NPE yakalandi -> " + e.getMessage());
            System.out.println("  (Eski Java'da burada SADECE satir numarasi olurdu,"
                    + " mesaj BOS olurdu.)");
        }
        System.out.println();

        // SENARYO 2: Kullanicinin adresi tamamen null.
        // Zincirin ILK halkasi kopuk.
        System.out.println("--- SENARYO 2: Kullanicinin adresi null ---");
        Kullanici k2 = new Kullanici("Mehmet", /* adres = */ null); // <-- adres NULL!
        try {
            String postaKodu = k2.adres().sehir().postaKodu();
            System.out.println("Posta kodu: " + postaKodu);
        } catch (NullPointerException e) {
            System.out.println("NPE yakalandi -> " + e.getMessage());
        }
        System.out.println();

        // SENARYO 3: Tum zincir saglam -> hata yok.
        System.out.println("--- SENARYO 3: Zincir saglam (hata yok) ---");
        Kullanici k3 = new Kullanici(
                "Zeynep",
                new Adres("Ataturk Bulv. No:5", new Sehir("Ankara", "06000"))
        );
        String postaKodu = k3.adres().sehir().postaKodu();
        System.out.println("Posta kodu: " + postaKodu);
        System.out.println();

        // -------------------------------------------------------------------
        // BONUS: Dizi/alan erisiminde de yardimci mesaj
        // -------------------------------------------------------------------
        System.out.println("--- BONUS: Dizi elemani null ---");
        Sehir[] sehirler = new Sehir[3]; // tum elemanlar null
        try {
            String ad = sehirler[1].ad(); // sehirler[1] null
            System.out.println(ad);
        } catch (NullPointerException e) {
            System.out.println("NPE yakalandi -> " + e.getMessage());
        }

        System.out.println("\nSONUC: Helpful NPE, ZINCIRLEME erisimlerde hangi"
                + " halkanin koptugunu");
        System.out.println("       dogrudan soyleyerek debugging suresini dramatik"
                + " olarak kisaltir.");
    }

    // =========================================================================
    //  Ic ice nesne grafigi modeli (gercek hayatta DTO/entity zinciri gibi)
    // =========================================================================
    static final class Kullanici {
        private final String ad;
        private final Adres adres;
        Kullanici(String ad, Adres adres) { this.ad = ad; this.adres = adres; }
        String ad()    { return ad; }
        Adres  adres() { return adres; }
    }

    static final class Adres {
        private final String sokak;
        private final Sehir sehir;
        Adres(String sokak, Sehir sehir) { this.sokak = sokak; this.sehir = sehir; }
        String sokak() { return sokak; }
        Sehir  sehir() { return sehir; }
    }

    static final class Sehir {
        private final String ad;
        private final String postaKodu;
        Sehir(String ad, String postaKodu) { this.ad = ad; this.postaKodu = postaKodu; }
        String ad()        { return ad; }
        String postaKodu() { return postaKodu; }
    }
}
