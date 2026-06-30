import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ============================================================================
 *  Java 10 - YEREL DEGISKEN TIP CIKARIMI (Local Variable Type Inference)
 *  JEP 286: "var" anahtar kelimesi
 * ============================================================================
 *
 *  NEDIR?
 *  ------
 *  Java 10 ile gelen "var", YEREL degiskenlerin tipini derleyicinin
 *  (compiler) sag taraftaki ifadeden (initializer) otomatik cikarmasini
 *  saglar. Boylece tipi iki kez yazmak zorunda kalmayiz.
 *
 *  ONEMLI: "var" bir anahtar kelime (keyword) DEGILDIR, "ayrilmis tip adi"
 *  (reserved type name) olarak tanimlanmistir. Bu sayede daha once
 *  degisken/metot adi olarak "var" kullanan eski kodlar bozulmaz.
 *
 *  NEDEN GELDI? (Hangi problem?)
 *  -----------------------------
 *  Java cok "verbose" (gereksiz uzun) bir dil olmakla elestiriliyordu.
 *  Ozellikle generic tipler cok uzadiginda tip adini SOL ve SAG tarafta
 *  iki kez yazmak hem yoruyor hem de okunabilirligi dusuruyordu:
 *
 *      Map<String, List<Map<Integer, String>>> veri =
 *          new HashMap<String, List<Map<Integer, String>>>();
 *
 *  Bu tekrar "boilerplate" (kalip kod) olarak goruluyordu. var bunu cozer.
 *
 *  NE ISE YARAR?
 *  -------------
 *  - Kod tekrarini azaltir (tip adini bir kez yazariz).
 *  - Uzun generic tiplerde okunabilirligi ARTIRIR.
 *  - Degisken ADINA ve degerin ANLAMINA odaklanmamizi saglar.
 *
 *  NEREDE KOLAYLIK SAGLAR?
 *  -----------------------
 *  - Karmasik generic tipler
 *  - Stream/Collectors zincirleri
 *  - for-each ve klasik for donguleri
 *  - try-with-resources
 *
 *  DIKKAT: var, tip GUVENLIGINI (type safety) BOZMAZ. Bu JavaScript'teki
 *  "var" gibi DINAMIK tip DEGILDIR. Tip derleme aninda sabittir; sadece
 *  yazmiyoruz. Calisma aninda (runtime) hicbir ek maliyet yoktur.
 * ============================================================================
 */
public class VarTipCikarimi {

    public static void main(String[] args) {

        System.out.println("=== 1) TEMEL KULLANIM (ESKI vs YENI) ===");
        temelKullanim();

        System.out.println("\n=== 2) UZUN GENERIC TIPLER ===");
        uzunGenericTipler();

        System.out.println("\n=== 3) FOR-EACH ve KLASIK FOR DONGUSU ===");
        donguler();

        System.out.println("\n=== 4) TRY-WITH-RESOURCES ===");
        tryWithResources();

        System.out.println("\n=== 5) STREAM / COLLECTORS ZINCIRI ===");
        streamOrnegi();

        System.out.println("\n=== 6) NEREDE KULLANILAMAZ (yorum icinde) ===");
        nereDeKullanilamaz();

        System.out.println("\n=== 7) OKUNABILIRLIK: IYI vs KOTU KULLANIM ===");
        okunabilirlik();
    }

    /* ------------------------------------------------------------------ */
    /* 1) TEMEL KULLANIM                                                   */
    /* ------------------------------------------------------------------ */
    static void temelKullanim() {

        // ----- ESKI YONTEM (Java 9 ve oncesi) -----
        String mesajEski = "Merhaba";
        int sayiEski = 42;
        double oranEski = 3.14;
        ArrayList<String> listeEski = new ArrayList<String>();

        // ----- YENI YONTEM (Java 10+) -----
        // Derleyici sag taraftan tipi cikarir:
        var mesaj = "Merhaba";          // String olarak cikarilir
        var sayi = 42;                  // int olarak cikarilir
        var oran = 3.14;                // double olarak cikarilir
        var liste = new ArrayList<String>(); // ArrayList<String> cikarilir

        // DIKKAT: var ile tanimlanan tip DEGISMEZ. Asagidaki satir
        // DERLENMEZ cunku 'sayi' artik int'tir, String atanamaz:
        //     sayi = "yazi"; // HATA: incompatible types

        System.out.println("mesaj=" + mesaj + ", sayi=" + sayi
                + ", oran=" + oran + ", liste boyutu=" + liste.size());
        System.out.println("Eski yontem cikti: " + mesajEski + " " + sayiEski
                + " " + oranEski + " " + listeEski.size());
    }

    /* ------------------------------------------------------------------ */
    /* 2) UZUN GENERIC TIPLER - var'in en parladigi yer                    */
    /* ------------------------------------------------------------------ */
    static void uzunGenericTipler() {

        // ----- ESKI: tip adini iki kez yazmak zorundayiz -----
        Map<String, List<Integer>> haritaEski =
                new HashMap<String, List<Integer>>();

        // ----- YENI: tek satir, tek tip -----
        var harita = new HashMap<String, List<Integer>>();
        harita.put("asal", List.of(2, 3, 5, 7));
        harita.put("cift", List.of(2, 4, 6, 8));

        System.out.println("harita = " + harita);
        System.out.println("haritaEski boyutu = " + haritaEski.size());
    }

    /* ------------------------------------------------------------------ */
    /* 3) DONGULER (for-each ve klasik for)                                */
    /* ------------------------------------------------------------------ */
    static void donguler() {

        var sehirler = List.of("Ankara", "Istanbul", "Izmir", "Bursa");

        // for-each icinde var (element tipi String olarak cikarilir)
        System.out.println("for-each:");
        for (var sehir : sehirler) {
            System.out.println("  - " + sehir);
        }

        // Klasik for dongusunde var (i, int olarak cikarilir)
        System.out.println("klasik for:");
        for (var i = 0; i < sehirler.size(); i++) {
            System.out.println("  [" + i + "] " + sehirler.get(i));
        }
    }

    /* ------------------------------------------------------------------ */
    /* 4) TRY-WITH-RESOURCES                                               */
    /* ------------------------------------------------------------------ */
    static void tryWithResources() {
        // var, try-with-resources icinde de kullanilabilir.
        // Burada ornek olarak kendi AutoCloseable kaynagimizi kullaniyoruz.
        try (var kaynak = new BasitKaynak("VeritabaniBaglantisi")) {
            kaynak.kullan();
        }
        // try blogu bitince kaynak otomatik kapatilir (close cagrilir).
    }

    /** try-with-resources demosu icin basit kapatilabilir kaynak. */
    static class BasitKaynak implements AutoCloseable {
        private final String ad;
        BasitKaynak(String ad) {
            this.ad = ad;
            System.out.println("  ACILDI: " + ad);
        }
        void kullan() {
            System.out.println("  KULLANILIYOR: " + ad);
        }
        @Override
        public void close() {
            System.out.println("  KAPATILDI: " + ad);
        }
    }

    /* ------------------------------------------------------------------ */
    /* 5) STREAM / COLLECTORS ZINCIRI                                      */
    /* ------------------------------------------------------------------ */
    static void streamOrnegi() {
        // Stream ara/son sonuclarini var ile tutmak okunabilirligi artirir.
        var sayilar = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // var ile sonuc (List<Integer>)
        var ciftKareler = sayilar
                .filter(n -> n % 2 == 0)   // ciftleri sec
                .map(n -> n * n)           // karesini al
                .collect(Collectors.toList());

        System.out.println("Cift sayilarin kareleri = " + ciftKareler);
    }

    /* ------------------------------------------------------------------ */
    /* 6) NEREDE KULLANILAMAZ?  (Hepsi yorum icinde - dosya derlensin diye) */
    /* ------------------------------------------------------------------ */
    static void nereDeKullanilamaz() {
        System.out.println("Asagidaki yasakli kullanimlar yorum icinde aciklandi.");

        // -------------------------------------------------------------
        // KURAL: var SADECE bir baslatici (initializer) ile YEREL
        // degiskende kullanilabilir. Asagidakilerin HICBIRI DERLENMEZ:
        // -------------------------------------------------------------

        // (a) BASLATMASIZ degisken - tip cikarilamaz:
        //     var x;            // HATA: cannot infer type
        //     x = 10;

        // (b) NULL ile baslatma - null'in tipi belirsizdir:
        //     var y = null;     // HATA: variable initializer is 'null'

        // (c) LAMBDA atamasi - hedef tip (target type) gerekir:
        //     var f = () -> System.out.println("hi"); // HATA
        //     (Lambda hangi functional interface? Derleyici bilemez.)

        // (d) METOT REFERANSI - yine hedef tip gerekir:
        //     var g = System.out::println; // HATA

        // (e) DIZI baslaticisi (kosesi parantez kisayolu):
        //     var dizi = { 1, 2, 3 };   // HATA: array initializer needs type
        //     (Ancak  var dizi = new int[]{1,2,3};  GECERLIDIR.)

        // (f) Ayni satirda BIRDEN COK degisken:
        //     var a = 1, b = 2;  // HATA: 'var' is not allowed in a compound declaration

        // -------------------------------------------------------------
        // var ASLA su yerlerde KULLANILAMAZ (yerel degisken degiller):
        // -------------------------------------------------------------
        // (g) SINIF ALANI (field):
        //         private var alan = 5;       // HATA
        // (h) METOT PARAMETRESI:
        //         void m(var p) { }           // HATA
        // (i) METOT DONUS TIPI:
        //         var hesapla() { return 1; } // HATA
        // (j) CATCH parametresi:
        //         catch (var e) { }           // HATA
        // (k) Java 10'da LAMBDA PARAMETRESI:
        //         (var s) -> s.length()       // Java 10'da YOK
        //     NOT: Lambda parametresinde var, Java 11 (JEP 323) ile geldi.

        // GECERLI olan ozel durum: var ile new int[]{...}
        var gecerliDizi = new int[]{1, 2, 3};
        System.out.println("  Gecerli dizi uzunlugu = " + gecerliDizi.length);
    }

    /* ------------------------------------------------------------------ */
    /* 7) OKUNABILIRLIK - IYI vs KOTU KULLANIM (BEST PRACTICE)             */
    /* ------------------------------------------------------------------ */
    static void okunabilirlik() {

        // ----- IYI KULLANIM: tip sag taraftan ACIKCA bellidir -----
        var musteri = new Musteri("Ayse", 30);          // tip net: Musteri
        var siparisler = new ArrayList<String>();       // tip net
        var toplamTutar = hesaplaTutar();                // metot adi anlamli

        System.out.println("IYI: " + musteri + ", siparis=" + siparisler.size()
                + ", tutar=" + toplamTutar);

        // ----- KOTU KULLANIM ORNEKLERI (aciklama) -----
        //
        // (1) Tip belirsiz kalir, sag taraf yeterince acik degil:
        //         var x = getir();   // getir() ne donduruyor? Okuyan bilemez!
        //     Bu durumda ACIK tip yazmak daha iyidir: BigDecimal x = getir();
        //
        // (2) Sayisal literal tuzaklari:
        //         var oran = 5;      // bu int! ama belki double istiyorduk
        //     Eger double istiyorsak ya  var oran = 5.0;  ya da  double oran = 5;
        //
        // (3) "Programci anlasin diye" anlamli degisken adi SECMEZ ve var
        //     kullanirsak okuyan kisi tipi tahmin etmek zorunda kalir.
        //
        // ALTIN KURAL:
        //   - var, tip APACIK ise (new X(), literal, anlamli metot adi) KULLAN.
        //   - var, tip belirsizlestiriyorsa (kisa/anlamsiz metot adi) KULLANMA.
        //   - Degisken adini var ile birlikte DAHA ANLAMLI sec.

        // Sayisal tuzak demosu:
        var oranYanlis = 5;     // int -> tam sayi bolmesi riski
        var oranDogru = 5.0;    // double -> ondalik
        System.out.println("oranYanlis/2 = " + (oranYanlis / 2)
                + "  (int bolme!)   oranDogru/2 = " + (oranDogru / 2));
    }

    static java.math.BigDecimal hesaplaTutar() {
        return new java.math.BigDecimal("199.90");
    }

    /** Okunabilirlik ornegi icin basit veri sinifi. */
    static class Musteri {
        final String ad;
        final int yas;
        Musteri(String ad, int yas) { this.ad = ad; this.yas = yas; }
        @Override public String toString() { return ad + "(" + yas + ")"; }
    }
}
