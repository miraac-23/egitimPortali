// =============================================================================
//  SealedClassesKalici.java
//  JAVA 17 (LTS) - Sealed Classes (Mühürlü Sınıflar) ARTIK KALICI (FINAL)
// =============================================================================
//
//  NEDİR?
//  Sealed (mühürlü) sınıf/arayüz, KENDİSİNDEN HANGİ SINIFLARIN türeyebileceğini
//  AÇIKÇA kısıtlar. "permits" listesinde olmayan hiçbir sınıf bu tipi extends
//  veya implements edemez. Java 15-16'da preview, JAVA 17 ile KALICI oldu.
//
//  NEDEN GELDİ?
//  Java'da bir sınıf ya tamamen "final" (kimse türetemez) ya da tamamen "açık"
//  (herkes türetebilir) olabiliyordu. Arada bir seçenek yoktu. Bazen şunu
//  istiyoruz: "Bu tipten SADECE şu 3 sınıf türeyebilsin, başkası türetemesin."
//  Sealed sınıflar tam olarak bunu sağlar -> KONTROLLÜ, KAPALI bir tip hiyerarşisi.
//
//  NE İŞE YARAR / GÜCÜ NEREDE?
//  Mühürlü hiyerarşi SONLU ve BİLİNEN bir alt-tip kümesi tanımlar. Bu sayede:
//    - Domain modelini (etki alanı modelini) net biçimde ifade ederiz.
//    - Pattern matching (switch) ile EKSİKSİZLİK (exhaustiveness) kontrolü
//      yapılabilir: derleyici tüm olası alt tiplerin ele alındığını DOĞRULAR.
//    - Records + Sealed + Pattern Matching = "Algebraic Data Types" (bkz.
//      CebirselVeriTipleri.java)
//
//  ANAHTAR KELİMELER:
//    sealed     -> mühürlü; alt tipleri permits listesiyle sınırlar
//    permits    -> hangi sınıfların türeyebileceğini listeler
//    final      -> alt tip artık kimseye kapalı (yaprak/leaf)
//    non-sealed -> alt tip mührü AÇAR; ondan herkes türeyebilir
//
//  Derlemek için: javac SealedClassesKalici.java
//  Çalıştırmak  : java SealedClassesKalici
// =============================================================================

public class SealedClassesKalici {

    public static void main(String[] args) {

        System.out.println("=== JAVA 17 (LTS): SEALED CLASSES (KALICI) ===\n");

        // ---------------------------------------------------------------------
        // 1) Sadece izin verilen alt tipler oluşturulabilir
        // ---------------------------------------------------------------------
        System.out.println("--- 1) Mühürlü hiyerarşinin üyeleri ---");
        Sekil[] sekiller = {
                new Daire(5),
                new Kare(4),
                new Dikdortgen(3, 6)
        };
        for (Sekil s : sekiller) {
            System.out.printf("  %-22s alan = %.2f%n",
                    s.getClass().getSimpleName(), s.alan());
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) Exhaustiveness (eksiksizlik): switch tüm alt tipleri kapsamalı
        //    permits listesi kapalı olduğundan derleyici default'a İHTİYAÇ
        //    duymadan tüm dalların kapsandığını doğrulayabilir.
        // ---------------------------------------------------------------------
        System.out.println("--- 2) Mühürlü tip üzerinde güvenli sınıflandırma ---");
        for (Sekil s : sekiller) {
            System.out.println("  " + sinifla(s));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) non-sealed örneği: mührü açan bir dal
        // ---------------------------------------------------------------------
        System.out.println("--- 3) non-sealed dal (genişletilebilir) ---");
        Odeme krediKarti = new KrediKarti("1234");
        Odeme ozelOdeme  = new OzelKurumsalOdeme(); // non-sealed'dan türemiş
        System.out.println("  " + krediKarti.aciklama());
        System.out.println("  " + ozelOdeme.aciklama());
    }

    // Klasik if-else ama mühürlü tip sayesinde tüm olasılıkları bildiğimizden emin oluruz
    static String sinifla(Sekil s) {
        if (s instanceof Daire d) {
            return "Daire (yarıçap " + d.yaricap() + ")";
        } else if (s instanceof Kare k) {
            return "Kare (kenar " + k.kenar() + ")";
        } else if (s instanceof Dikdortgen r) {
            return "Dikdörtgen (" + r.en() + "x" + r.boy() + ")";
        }
        // permits listesi kapalı; başka alt tip mümkün değil
        return "Bilinmeyen";
    }

    // =========================================================================
    //  MÜHÜRLÜ HİYERARŞİ: Geometrik şekiller
    //  - Sekil "sealed interface"; sadece Daire, Kare, Dikdortgen türeyebilir.
    //  - Alt tipler record olduğundan otomatik olarak "final"dır (geçerli).
    //  - Mühürlü bir tipin tüm izinli alt tipleri AYNI modülde / aynı pakette
    //    (tek dosyada aynı paket) tanımlanmalıdır.
    // =========================================================================
    sealed interface Sekil permits Daire, Kare, Dikdortgen {
        double alan();
    }

    // record'lar örtük final olduğu için permits için geçerli yapraklardır
    record Daire(double yaricap) implements Sekil {
        public double alan() { return Math.PI * yaricap * yaricap; }
    }

    record Kare(double kenar) implements Sekil {
        public double alan() { return kenar * kenar; }
    }

    record Dikdortgen(double en, double boy) implements Sekil {
        public double alan() { return en * boy; }
    }

    // =========================================================================
    //  final / non-sealed kullanımının gösterimi
    //  Mühürlü bir tipin HER alt sınıfı şu üçünden BİRİ olmak ZORUNDADIR:
    //    - final       : artık kapalı (kimse türetemez)
    //    - sealed      : kendisi de mühürlü (permits ile devam eder)
    //    - non-sealed  : mührü tekrar açar (herkes türetebilir)
    // =========================================================================
    sealed interface Odeme permits KrediKarti, Havale, KurumsalOdeme {
        String aciklama();
    }

    // final yaprak
    record KrediKarti(String sonDortHane) implements Odeme {
        public String aciklama() { return "Kredi kartı **** " + sonDortHane; }
    }

    // final yaprak
    record Havale(String iban) implements Odeme {
        public String aciklama() { return "Havale/EFT: " + iban; }
    }

    // non-sealed: bu dalın altı herkese AÇIK (genişletilebilir uzantı noktası)
    non-sealed interface KurumsalOdeme extends Odeme {}

    // KurumsalOdeme non-sealed olduğu için serbestçe türetilebilir
    static final class OzelKurumsalOdeme implements KurumsalOdeme {
        public String aciklama() { return "Özel kurumsal ödeme yöntemi"; }
    }
}

// =============================================================================
//  KURALLAR ÖZETİ — SEALED CLASSES
//  1) sealed sınıf/arayüz "permits X, Y, Z" ile izinli alt tipleri belirtir.
//     (Aynı dosyadaysa permits opsiyoneldir; derleyici çıkarımı yapabilir.)
//  2) Her izinli alt tip final, sealed VEYA non-sealed olmak ZORUNDADIR.
//  3) Tüm izinli alt tipler, sealed tip ile aynı modül (veya named module yoksa
//     aynı paket) içinde olmalıdır.
//  4) record'lar örtük final'dır; mühürlü hiyerarşilerde mükemmel yapraktır.
//
//  AVANTAJLARI:
//   - Domain modeli kapalı ve nettir (kim türeyebilir bellidir).
//   - Pattern matching switch'te exhaustiveness (default gerekmez).
//   - API tasarımında kötüye kullanımı önler.
//
//  GERÇEK HAYAT KULLANIMI:
//   - Durum makineleri (state machine)
//   - Ödeme yöntemleri, mesaj tipleri, AST düğümleri, JSON node tipleri
//   - Sonuç tipleri: Result = Basari | Hata  (bkz. CebirselVeriTipleri.java)
// =============================================================================
