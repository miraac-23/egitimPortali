/*
 * =============================================================================
 *  Java 14 - RECORDS (PREVIEW)
 *  JEP 359: Records (Preview)
 * =============================================================================
 *
 *  PREVIEW DURUMU (cok onemli):
 *    - Java 14: Records ILK preview olarak geldi.
 *               Derlemek icin:  javac --enable-preview --release 14 RecordsPreview.java
 *               Calistirmak:    java  --enable-preview RecordsPreview
 *    - Java 15: 2. preview.
 *    - Java 16: ARTIK KALICI! "--enable-preview" gerekmez.
 *
 *  Bu dosya Java 16+ (ornegin Java 21) ile preview bayragi OLMADAN derlenir.
 *
 *  ANA FIKIR:
 *    "record", yalnizca VERI tasimak icin tasarlanmis, degismez (immutable),
 *    ozlu bir siniftir. Tek satirda tanimladiginiz record sizin yerinize sunlari
 *    OTOMATIK uretir:
 *       - private final alanlar
 *       - tum alanlari alan canonical constructor
 *       - her alan icin accessor (getter) metot:  x(), y(), ad() ...
 *       - equals(), hashCode(), toString()
 * =============================================================================
 */
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecordsPreview {

    public static void main(String[] args) {
        System.out.println("=== Java 14: Records (Preview) ===\n");

        // -------------------------------------------------------------------
        // BOLUM 1: ESKI vs YENI -- ayni 2D nokta, ayni davranis
        // -------------------------------------------------------------------
        System.out.println("--- BOLUM 1: ~50 satir POJO vs 1 satir record ---");

        // ESKI: asagidaki NoktaEski sinifi ~50 satir boilerplate icerir.
        NoktaEski eski1 = new NoktaEski(3, 4);
        NoktaEski eski2 = new NoktaEski(3, 4);
        System.out.println("ESKI POJO  toString : " + eski1);
        System.out.println("ESKI POJO  equals   : " + eski1.equals(eski2));
        System.out.println("ESKI POJO  getX     : " + eski1.getX());

        // YENI: ayni isi yapan record TEK SATIR (asagida Nokta tanimi).
        Nokta yeni1 = new Nokta(3, 4);
        Nokta yeni2 = new Nokta(3, 4);
        System.out.println("YENI record toString: " + yeni1);          // Nokta[x=3, y=4]
        System.out.println("YENI record equals  : " + yeni1.equals(yeni2)); // true
        System.out.println("YENI record getter  : " + yeni1.x());      // accessor: x()
        System.out.println("YENI record hashCode esit mi: "
                + (yeni1.hashCode() == yeni2.hashCode()));             // true
        System.out.println();

        // -------------------------------------------------------------------
        // BOLUM 2: Compact constructor ile VALIDASYON
        // -------------------------------------------------------------------
        System.out.println("--- BOLUM 2: Compact constructor ile validasyon ---");
        Para tutar = new Para(150.0, "TRY");
        System.out.println("Gecerli para: " + tutar);
        try {
            new Para(-5.0, "TRY"); // negatif tutar -> compact constructor exception firlatir
        } catch (IllegalArgumentException e) {
            System.out.println("Validasyon yakalandi: " + e.getMessage());
        }
        try {
            new Para(10.0, "EURO"); // gecersiz para birimi kodu (3 harf degil)
        } catch (IllegalArgumentException e) {
            System.out.println("Validasyon yakalandi: " + e.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // BOLUM 3: GERCEK HAYAT - Kullanici DTO + API response modeli
        // -------------------------------------------------------------------
        System.out.println("--- BOLUM 3: Gercek hayat - DTO ve API response ---");

        KullaniciDTO kullanici = new KullaniciDTO(
                42L, "ayse.yilmaz", "ayse@example.com", true);
        System.out.println("Kullanici DTO: " + kullanici);
        System.out.println("Aktif mi?    : " + kullanici.aktif());

        // Record icinde ek (turetilmis) metot da tanimlanabilir:
        System.out.println("Maskelenmis email: " + kullanici.maskeliEmail());

        // Generic record ile tipli API response:
        ApiResponse<KullaniciDTO> response =
                new ApiResponse<>(200, "OK", kullanici);
        System.out.println("API response : " + response);
        System.out.println("Basarili mi? : " + response.basarili());
        System.out.println();

        // -------------------------------------------------------------------
        // BOLUM 4: Record'larin gucu - Map anahtari ve metottan coklu deger
        // -------------------------------------------------------------------
        System.out.println("--- BOLUM 4: Record'lar Map anahtari olarak ---");
        // Otomatik equals/hashCode sayesinde record'lar dogrudan
        // HashMap anahtari olarak GUVENLE kullanilabilir.
        Map<Nokta, String> sehirler = Map.of(
                new Nokta(41, 28), "Istanbul",
                new Nokta(39, 32), "Ankara"
        );
        // Yeni ama ayni degerli bir Nokta ile arama yapsak da bulur:
        System.out.println("(41,28) -> " + sehirler.get(new Nokta(41, 28)));

        // Metottan birden fazla deger dondurmek icin record (tuple yerine):
        MinMax mm = minMaxBul(List.of(7, 2, 9, 4, 1, 8));
        System.out.println("Min/Max: " + mm + " -> min=" + mm.min() + " max=" + mm.max());
    }

    /** Listenin min ve max degerini TEK donus degerinde tasiyan yardimci. */
    static MinMax minMaxBul(List<Integer> sayilar) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int s : sayilar) {
            min = Math.min(min, s);
            max = Math.max(max, s);
        }
        return new MinMax(min, max);
    }

    // =========================================================================
    //  ESKI YOL: Klasik immutable POJO/DTO -- ~50 satir BOILERPLATE
    //  (record olmadan bir 2D nokta yazmak icin tum bunlar gerekir)
    // =========================================================================
    static final class NoktaEski {
        private final int x;
        private final int y;

        public NoktaEski(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NoktaEski that = (NoktaEski) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "NoktaEski{x=" + x + ", y=" + y + "}";
        }
    }

    // =========================================================================
    //  YENI YOL: Yukaridaki ~50 satirin HEPSININ karsiligi -> TEK SATIR
    // =========================================================================
    record Nokta(int x, int y) { }
    //  -> private final int x, y
    //  -> public Nokta(int x, int y) { ... }   (canonical constructor)
    //  -> public int x(), public int y()       (accessor'lar)
    //  -> equals / hashCode / toString          (deger tabanli)

    // =========================================================================
    //  COMPACT CONSTRUCTOR ile validasyon iceren record.
    //  "Para(double tutar, String birim)" yazmadan, parametre listesi
    //  olmayan kompakt yapilandirici icinde dogrulama yapilir; alanlar
    //  atama gerektirmez (Java otomatik atar).
    // =========================================================================
    record Para(double tutar, String birim) {
        Para {  // <-- compact constructor (parametre parantezi YOK)
            if (tutar < 0) {
                throw new IllegalArgumentException("Tutar negatif olamaz: " + tutar);
            }
            if (birim == null || birim.length() != 3) {
                throw new IllegalArgumentException(
                        "Para birimi 3 harfli ISO kodu olmali (orn. TRY, USD): " + birim);
            }
        }
    }

    // =========================================================================
    //  GERCEK HAYAT: Kullanici DTO (REST API'de tasinan veri)
    //  Record icinde ek/turetilmis metot tanimlanabilir.
    // =========================================================================
    record KullaniciDTO(long id, String kullaniciAdi, String email, boolean aktif) {
        /** Email'i loglarda gizlemek icin maskeleyen turetilmis metot. */
        String maskeliEmail() {
            int at = email.indexOf('@');
            if (at <= 1) return "***";
            return email.charAt(0) + "***" + email.substring(at);
        }
    }

    // =========================================================================
    //  GERCEK HAYAT: Generic API response modeli
    // =========================================================================
    record ApiResponse<T>(int statusKodu, String mesaj, T veri) {
        boolean basarili() {
            return statusKodu >= 200 && statusKodu < 300;
        }
    }

    // =========================================================================
    //  Metottan birden fazla deger dondurmek icin kucuk record (tuple yerine)
    // =========================================================================
    record MinMax(int min, int max) { }
}
