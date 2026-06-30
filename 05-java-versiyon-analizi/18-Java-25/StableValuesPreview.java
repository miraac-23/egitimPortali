// ============================================================================
//  Java 25 - Stable Values (JEP 502 - PREVIEW)
// ============================================================================
//  DIKKAT: Stable Values, Java 25'te HALA PREVIEW asamasindadir (JEP 502).
//  Yani API ileride degisebilir ve calistirmak icin --enable-preview gerekir.
//
//  DERLEME / CALISTIRMA (Java 25 - PREVIEW):
//      javac --release 25 --enable-preview StableValuesPreview.java
//      java  --enable-preview StableValuesPreview
//
//  NEDIR:
//    StableValue<T>, bir defaya mahsus (en fazla bir kez) atanan, ondan
//    sonra DEGISMEZ (immutable) hale gelen bir "tutucu"dur. Asil amaci:
//    LAZY (tembel/gec) baslatma -> deger ilk ihtiyac aninda hesaplanir,
//    sonra cache'lenir. JIT derleyici, deger atandiktan sonra onu adeta
//    'final' gibi optimize edebilir (constant folding). Yani:
//      "final'in performansi + lazy'nin esnekligi"
//
//  NEDEN GELDI:
//    - 'final' alanlar yapilandiricida HEMEN atanmak zorundadir (lazy degil).
//    - Lazy yapmak icin tarihsel olarak "double-checked locking" gibi hata
//      yapmaya cok acik desenler kullanilirdi.
//    - StableValue, bu deseni guvenli, kisa ve performansli sekilde sunar.
// ============================================================================

import java.util.function.Supplier;

public class StableValuesPreview {

    public static void main(String[] args) {
        System.out.println("=== Java 25: Stable Values (PREVIEW - JEP 502) ===\n");
        System.out.println("(Calistirma: java --enable-preview StableValuesPreview)\n");

        eskiDoubleCheckedLockingAciklama();

        System.out.println("\n--- StableValue ile lazy init ornegi ---");
        Konfigurasyon konfig = new Konfigurasyon();

        System.out.println("Nesne olusturuldu; agir kaynak HENUZ hesaplanmadi.");
        System.out.println("(Tembellik: ilk get() cagrilana kadar hesaplama yok.)\n");

        // Ilk erisim -> hesaplama burada bir kez yapilir:
        System.out.println("1. erisim: " + konfig.getBaglantiDizesi());
        // Ikinci erisim -> cache'ten gelir, tekrar hesaplanmaz:
        System.out.println("2. erisim: " + konfig.getBaglantiDizesi());
        System.out.println("3. erisim: " + konfig.getBaglantiDizesi());

        System.out.println("\nDikkat: 'AGIR HESAPLAMA' yazisi sadece BIR KEZ gorunmeli.");
    }

    static void eskiDoubleCheckedLockingAciklama() {
        System.out.println(">> ESKI YONTEM: Double-Checked Locking (hata yapmaya acik)");
        System.out.println("       private volatile Agir nesne;");
        System.out.println("       public Agir get() {");
        System.out.println("           Agir sonuc = nesne;");
        System.out.println("           if (sonuc == null) {");
        System.out.println("               synchronized (this) {");
        System.out.println("                   sonuc = nesne;");
        System.out.println("                   if (sonuc == null)");
        System.out.println("                       nesne = sonuc = new Agir();");
        System.out.println("               }");
        System.out.println("           }");
        System.out.println("           return sonuc;");
        System.out.println("       }");
        System.out.println("   Sorunlar: volatile unutulursa BUG; cok satirli, okunmasi zor;");
        System.out.println("   JIT 'final' kadar iyi optimize edemez.\n");

        System.out.println(">> YENI YONTEM: StableValue (PREVIEW)");
        System.out.println("       private final StableValue<Agir> nesne = StableValue.of();");
        System.out.println("       public Agir get() {");
        System.out.println("           return nesne.orElseSet(() -> new Agir());");
        System.out.println("       }");
        System.out.println("   Avantajlar: tek satir, thread-safe, JIT 'final' gibi optimize");
        System.out.println("   edebilir (constant folding). volatile/synchronized derdi yok.\n");
        System.out.println("   NOT: API isimleri PREVIEW oldugu icin (StableValue.of /");
        System.out.println("   orElseSet vb.) surumler arasi degisebilir; resmi JEP'e bakin.");
    }

    // ------------------------------------------------------------------------
    //  GERCEK HAYAT: Konfigurasyon icindeki pahali bir baglanti dizesini
    //  yalnizca ilk ihtiyac aninda, bir kez hesaplayip cache'lemek.
    // ------------------------------------------------------------------------
    static class Konfigurasyon {

        // StableValue tutucusu: bir kez set edilir, sonra degismez.
        // PREVIEW API'si: StableValue.of() bos bir tutucu olusturur.
        private final StableValue<String> baglantiDizesi = StableValue.of();

        String getBaglantiDizesi() {
            // orElseSet: deger yoksa Supplier ile uretir+cache'ler, varsa cache'i dondurur.
            // Supplier en fazla BIR KEZ calisir (thread-safe garantili).
            return baglantiDizesi.orElseSet(this::agirHesaplama);
        }

        // Pahali hesaplamayi temsil eden metot.
        private String agirHesaplama() {
            System.out.println("   [AGIR HESAPLAMA calisiyor... bir kez]");
            try {
                Thread.sleep(300); // pahali I/O'yu simule et
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "jdbc://veritabani:5432/uygulama?havuz=20";
        }
    }

    // ------------------------------------------------------------------------
    //  Referans: StableValue'yu bir Supplier gibi de kullanabilirsiniz.
    //  Asagidaki yardimci, klasik Supplier imzasiyla nasil sarilabilecegini
    //  gosterir (gercek API farkli yardimci metotlar sunabilir).
    // ------------------------------------------------------------------------
    @SuppressWarnings("unused")
    static <T> Supplier<T> tembelSupplier(Supplier<T> kaynak) {
        StableValue<T> sv = StableValue.of();
        return () -> sv.orElseSet(kaynak);
    }
}
