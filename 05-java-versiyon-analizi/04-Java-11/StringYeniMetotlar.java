import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  JAVA 11 - YENI STRING METOTLARI - JEP 327 (Unicode) + String iyilestirmeleri
 * ============================================================================
 *
 *  Java 11 ile String sinifina cok kullanisli metotlar eklendi:
 *    - strip(), stripLeading(), stripTrailing()
 *    - isBlank()
 *    - lines()
 *    - repeat(int)
 *
 *  Ayrica Collection.toArray(IntFunction) yeniligi de bu dosyada gosterilmistir
 *  (List<String> -> String[] donusumu icin).
 *
 *  GERCEK HAYAT ORNEGI:
 *  --------------------
 *    Kullanicidan gelen form verilerini (isim, e-posta) temizlemek, log
 *    dosyalarini satir satir islemek, CSV/metin parse etmek, rapor/tablo
 *    cizmek (repeat ile cizgi/bosluk) gibi gunluk islerde cok ise yarar.
 * ============================================================================
 */
public class StringYeniMetotlar {

    public static void main(String[] args) {

        // ====================================================================
        // 1) strip() vs trim() - EN ONEMLI FARK: UNICODE!
        // ====================================================================
        //
        // NEDIR: strip() bir String'in basindaki ve sonundaki bosluklari siler.
        //
        // NEDEN GELDI: Eski trim() metodu 1997'den beri var ama bir SORUNU var:
        //   trim() yalnizca kod degeri <= U+0020 (space) olan karakterleri siler.
        //   Yani bazi Unicode bosluk karakterlerini (orn. U+2007, U+00A0 - non
        //   breaking space, U+2003 - em space) TANIMAZ ve SILMEZ.
        //   strip() ise Character.isWhitespace() kullanir; tum Unicode bosluk
        //   karakterlerini dogru sekilde temizler.
        //
        System.out.println("=== 1) strip vs trim (Unicode) ===");

        // U+2003 (EM SPACE) ile cevrelenmis metin - trim bunu SILEMEZ:
        String unicodeBosluklu = "  Merhaba  ";

        System.out.println("Orijinal uzunluk      : " + unicodeBosluklu.length());
        System.out.println("trim() sonrasi uzunluk: " + unicodeBosluklu.trim().length()
                + "  (trim Unicode bosluk siremedi!)");
        System.out.println("strip() sonrasi uzunluk: " + unicodeBosluklu.strip().length()
                + "  (strip dogru temizledi)");

        // stripLeading(): sadece bastaki, stripTrailing(): sadece sondaki
        String veri = "   sadece kenarlar   ";
        System.out.println("stripLeading() : '" + veri.stripLeading() + "'");
        System.out.println("stripTrailing(): '" + veri.stripTrailing() + "'");
        System.out.println("strip()        : '" + veri.strip() + "'");

        // ESKI YONTEM: trim() (Unicode-guvensiz) veya replaceAll regex:
        //   String s = veri.replaceAll("^\\s+|\\s+$", "");  // karmasik regex
        // YENI YONTEM: veri.strip();  // temiz ve Unicode-dogru

        // ====================================================================
        // 2) isBlank() - bos VEYA sadece bosluk mu?
        // ====================================================================
        //
        // NEDIR: String bos ("") veya yalnizca bosluk karakterlerinden mi
        //        olusuyor kontrol eder.
        // NEDEN GELDI: isEmpty() sadece "" (sifir uzunluk) kontrol eder; icinde
        //        bosluk olan "   " icin false doner. Form validasyonunda bu sorun.
        //
        System.out.println("\n=== 2) isBlank ===");
        String bosluklu = "    \t  \n  ";
        System.out.println("isEmpty(): " + bosluklu.isEmpty()
                + "  (false - icinde bosluk var)");
        System.out.println("isBlank(): " + bosluklu.isBlank()
                + "  (true - hepsi bosluk)");

        // GERCEK HAYAT: form alani gercekten dolu mu?
        String kullaniciGirdisi = "   ";
        if (kullaniciGirdisi.isBlank()) {
            System.out.println("HATA: Isim alani bos birakilamaz!");
        }
        // ESKI YONTEM: if (s == null || s.trim().isEmpty()) { ... }
        // YENI YONTEM: if (s.isBlank()) { ... }

        // ====================================================================
        // 3) lines() - cok satirli metni Stream<String> olarak gez
        // ====================================================================
        //
        // NEDIR: Cok satirli bir String'i, satir sonlarina gore bolup
        //        Stream<String> dondurur. \n, \r, \r\n hepsini tanir.
        // NEDEN GELDI: Eskiden split("\n") ile bolerdik ama \r\n (Windows)
        //        ve platform farkliliklari sorun cikariyordu. lines() bunlari
        //        dogru ele alir ve tembel (lazy) bir stream verir.
        //
        System.out.println("\n=== 3) lines ===");
        String logMetni = "INFO: baslangic\nWARN: dikkat\nERROR: hata olustu\nINFO: bitis";

        System.out.println("Toplam satir sayisi: " + logMetni.lines().count());
        System.out.println("Sadece ERROR satirlari:");
        logMetni.lines()
                .filter(satir -> satir.startsWith("ERROR"))
                .forEach(satir -> System.out.println("   >> " + satir));

        // ESKI YONTEM: String[] satirlar = logMetni.split("\\r?\\n");
        // YENI YONTEM: logMetni.lines()  // stream olarak, lazy, dogru

        // ====================================================================
        // 4) repeat(int) - String'i N kez tekrarla
        // ====================================================================
        //
        // NEDIR: String'i belirtilen sayida tekrarlayip yeni String dondurur.
        // NEDEN GELDI: Eskiden bir karakteri/metni N kez tekrarlamak icin dongu
        //        veya StringBuilder yazmak gerekirdi. Cok yaygin bir ihtiyac.
        //
        System.out.println("\n=== 4) repeat ===");
        System.out.println("=".repeat(40));        // 40 karakterlik cizgi
        System.out.println("Ya" + "ha ".repeat(3));
        System.out.println("Girinti:" + " ".repeat(4) + "(4 bosluk girinti)");

        // GERCEK HAYAT: konsol tablosu/rapor basligi cizmek
        String baslik = "RAPOR";
        System.out.println("+" + "-".repeat(baslik.length() + 2) + "+");
        System.out.println("| " + baslik + " |");
        System.out.println("+" + "-".repeat(baslik.length() + 2) + "+");

        // ESKI YONTEM:
        //   StringBuilder sb = new StringBuilder();
        //   for (int i = 0; i < 40; i++) sb.append("=");
        //   String cizgi = sb.toString();
        // YENI YONTEM: "=".repeat(40);

        // ====================================================================
        // 5) Collection.toArray(IntFunction) - BONUS Java 11 yeniligi
        // ====================================================================
        //
        // NEDIR: Bir koleksiyonu tipli diziye cevirirken artik bos dizi
        //        olusturup gecmek yerine, method reference verebiliriz.
        //
        System.out.println("\n=== 5) Collection.toArray(IntFunction) ===");
        List<String> isimler = List.of("Ali", "Veli", "Ayse");

        // YENI (Java 11): method reference ile temiz
        String[] dizi = isimler.toArray(String[]::new);
        System.out.println("Diziye cevrildi: " + String.join(", ", dizi));

        // ESKI YONTEM: isimler.toArray(new String[0]);  // veya new String[size]

        // Birlestirilmis ornek: temizle + tekrarsiz + diziye al
        List<String> temizlenmis = List.of("  Ali ", "Veli", "  Ayse  ").stream()
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        String[] sonDizi = temizlenmis.toArray(String[]::new);
        System.out.println("Temizlenmis dizi: " + String.join(" | ", sonDizi));
    }
}
