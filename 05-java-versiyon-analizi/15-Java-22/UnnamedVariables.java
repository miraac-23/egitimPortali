// =============================================================================
//  Java 22 - Unnamed Variables & Patterns  (JEP 456 - KALICI / STABLE)
// =============================================================================
//
//  ÖNEMLİ NOT:
//  -----------
//  Bu özellik Java 21'de preview, JAVA 22'de KALICI (final/standart) hale geldi.
//  Yani --enable-preview bayrağına GEREK YOKTUR.
//
//  DERLEME:
//      javac --release 22 UnnamedVariables.java
//  ÇALIŞTIRMA:
//      java UnnamedVariables
//
//  ÖZET:
//  Tek alt çizgi  _  artık "isimsiz değişken / isimsiz pattern" anlamına gelir.
//  Kullanmayacağımız ama söz dizimi gereği bildirmek zorunda olduğumuz
//  değişkenleri  _  ile işaretleriz. Bu hem kodu netleştirir ("bunu bilerek
//  kullanmıyorum" niyetini gösterir) hem de derleyici/IDE uyarılarını azaltır.
//
//  KULLANIM YERLERİ:
//   1) Atama / yerel değişken     :  var _ = pahaliHesap();
//   2) for döngüsü sayacı / değişkeni
//   3) catch bloğu parametresi     :  catch (Exception _) { ... }
//   4) try-with-resources          :  try (var _ = ac()) { ... }
//   5) Lambda parametresi          :  (k, _) -> ...
//   6) Pattern matching - unnamed pattern  :  case Point(int x, _)
//
//  NOT: Aynı kapsamda BİRDEN FAZLA  _  kullanılabilir (çakışma vermez),
//  çünkü  _  bir isim değildir; ona erişmek mümkün değildir.
// =============================================================================

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

public class UnnamedVariables {

    // ---- Pattern matching örnekleri için kayıt (record) tipleri ----
    sealed interface Sekil permits Nokta, Daire, Dikdortgen {}
    record Nokta(int x, int y) implements Sekil {}
    record Daire(Nokta merkez, double yaricap) implements Sekil {}
    record Dikdortgen(Nokta solUst, Nokta sagAlt) implements Sekil {}

    public static void main(String[] args) {
        System.out.println("=== Java 22 Unnamed Variables & Patterns ===\n");

        ornek1_yerelDegisken();
        ornek2_forDongusu();
        ornek3_catchBlogu();
        ornek4_tryWithResources();
        ornek5_lambdaParametresi();
        ornek6_unnamedPattern();
        ornek7_iciceUnnamedPattern();

        System.out.println("\n=== Tüm örnekler tamamlandı ===");
    }

    // -------------------------------------------------------------------------
    //  1) ISIMSIZ YEREL DEĞİŞKEN
    //     Bir metodun yan etkisi için çağrıldığı, dönüş değerinin
    //     gerçekten gerekmediği durumlar.
    // -------------------------------------------------------------------------
    private static void ornek1_yerelDegisken() {
        System.out.println("--- 1) Isimsiz yerel değişken ---");

        // queue.poll() yan etki olarak elemanı çıkarır; değeri umursamıyoruz.
        Queue<String> kuyruk = new LinkedList<>(List.of("a", "b", "c"));

        // ESKİ: int unused = ...; veya değeri gereksiz yere değişkene atama
        // YENİ: _ ile "bu değeri bilerek kullanmıyorum" deriz.
        var _ = kuyruk.poll();   // ilk elemanı at, sonucu umursama
        System.out.println("  Bir eleman atıldı. Kalan kuyruk: " + kuyruk);
    }

    // -------------------------------------------------------------------------
    //  2) FOR DÖNGÜSÜNDE ISIMSIZ DEĞİŞKEN
    //     Sadece "N kere yap" demek istediğimizde sayacı kullanmıyoruz.
    // -------------------------------------------------------------------------
    private static void ornek2_forDongusu() {
        System.out.println("--- 2) for döngüsünde isimsiz değişken ---");

        List<String> kelimeler = List.of("elma", "armut", "kiraz");

        // enhanced-for: elemanı kullanmadan sadece sayım yapıyoruz.
        int adet = 0;
        for (var _ : kelimeler) {
            adet++;   // eleman değerini kullanmıyoruz, sadece sayıyoruz
        }
        System.out.println("  Liste eleman sayısı (eleman kullanılmadan): " + adet);
    }

    // -------------------------------------------------------------------------
    //  3) CATCH BLOĞUNDA ISIMSIZ DEĞİŞKEN
    //     Exception nesnesini hiç kullanmayacaksak _ ile yakalarız.
    // -------------------------------------------------------------------------
    private static void ornek3_catchBlogu() {
        System.out.println("--- 3) catch bloğunda isimsiz değişken ---");

        String girdi = "abc";
        int deger;
        try {
            deger = Integer.parseInt(girdi);
        } catch (NumberFormatException _) {   // exception nesnesini kullanmıyoruz
            deger = -1;   // geçersiz girdi -> varsayılan
        }
        System.out.println("  \"" + girdi + "\" -> " + deger + " (parse başarısızsa -1)");
    }

    // -------------------------------------------------------------------------
    //  4) TRY-WITH-RESOURCES İÇİNDE ISIMSIZ DEĞİŞKEN
    //     Kaynağı sadece açıp kapatmak (kilit/scope) için kullanıyoruz;
    //     referansa hiç dokunmuyoruz.
    // -------------------------------------------------------------------------
    private static void ornek4_tryWithResources() {
        System.out.println("--- 4) try-with-resources içinde isimsiz kaynak ---");

        // AutoCloseable bir kaynak: kapanınca mesaj yazsın.
        try (var _ = acKapatLog("VeritabaniBaglantisi")) {
            System.out.println("  Kaynak kullanılıyor (ama referansına ihtiyaç yok)...");
        }
        // try bloğundan çıkınca close() otomatik çağrılır.
    }

    private static AutoCloseable acKapatLog(String ad) {
        System.out.println("  [AÇILDI] " + ad);
        return () -> System.out.println("  [KAPANDI] " + ad);
    }

    // -------------------------------------------------------------------------
    //  5) LAMBDA PARAMETRESİNDE ISIMSIZ DEĞİŞKEN
    //     Bir parametreyi kullanmayacağımızda _ ile geçeriz.
    // -------------------------------------------------------------------------
    private static void ornek5_lambdaParametresi() {
        System.out.println("--- 5) lambda parametresinde isimsiz değişken ---");

        Map<String, Integer> stok = Map.of("kalem", 10, "defter", 5, "silgi", 0);

        // Map.forEach(BiConsumer): value'yu kullanmadan sadece key'leri yaz.
        System.out.print("  Ürünler: ");
        List<String> urunler = new ArrayList<>();
        stok.forEach((urun, _) -> urunler.add(urun));   // miktarı umursamıyoruz
        System.out.println(urunler);

        // Birden fazla _ aynı lambda içinde de olabilir (örnek amaçlı):
        // (a, _) gibi -> sadece ilk parametreyi kullan.
    }

    // -------------------------------------------------------------------------
    //  6) UNNAMED PATTERN (pattern matching içinde)
    //     Record desktructuring sırasında ilgilenmediğimiz bileşeni
    //     _ ile geçeriz. Tip bile yazmamıza gerek yok.
    // -------------------------------------------------------------------------
    private static void ornek6_unnamedPattern() {
        System.out.println("--- 6) Unnamed pattern (record bileşeni atlama) ---");

        Sekil[] sekiller = {
                new Nokta(3, 7),
                new Daire(new Nokta(0, 0), 5.0),
                new Dikdortgen(new Nokta(0, 10), new Nokta(10, 0))
        };

        for (Sekil s : sekiller) {
            String aciklama = switch (s) {
                // Nokta'nın sadece x'iyle ilgileniyoruz, y'yi _ ile atlıyoruz.
                case Nokta(int x, _) -> "Nokta, x = " + x + " (y göz ardı edildi)";

                // Daire'nin sadece yarıçapıyla ilgileniyoruz, merkezi atlıyoruz.
                case Daire(var _, double r) -> "Daire, yarıçap = " + r;

                // Dikdörtgen'in iki köşesini de istemiyoruz -> her ikisi de _.
                case Dikdortgen(var _, var _) -> "Dikdörtgen (köşeler göz ardı edildi)";
            };
            System.out.println("  " + aciklama);
        }
    }

    // -------------------------------------------------------------------------
    //  7) İÇ İÇE (NESTED) UNNAMED PATTERN
    //     Derin yapıların içinde sadece ihtiyacımız olan kısmı çekeriz.
    // -------------------------------------------------------------------------
    private static void ornek7_iciceUnnamedPattern() {
        System.out.println("--- 7) İç içe unnamed pattern ---");

        Object nesne = new Daire(new Nokta(4, 9), 2.5);

        // Daire'nin merkezinin SADECE y koordinatını istiyoruz.
        // merkez Nokta'sının içine girip x'i _ ile atlıyoruz.
        if (nesne instanceof Daire(Nokta(_, int merkezY), double r)) {
            System.out.println("  Dairenin merkez Y'si = " + merkezY
                    + ", yarıçap = " + r + " (merkez X ve diğerleri atlandı)");
        }
    }
}
