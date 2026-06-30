// ============================================================================
//  Java 25 - Structured Concurrency (JEP 505 - HALA PREVIEW)
// ============================================================================
//  DURUSTLUK NOTU: Structured Concurrency, Java 25'te HALA PREVIEW
//  asamasindadir (JEP 505 - 5. preview). KALICI DEGILDIR. Bu nedenle:
//      - --enable-preview ile derlemek/calistirmak gerekir,
//      - API ileride degisebilir.
//
//  ONEMLI: API, son preview'larda DEGISTI. Eski "StructuredTaskScope.fork()
//  + ShutdownOnFailure" yaklasimi yerine, Java 25'te (JEP 505) StructuredTaskScope
//  bir "Joiner" (birlestirici) ile, genelde StructuredTaskScope.open(...) sekilde
//  acilir. Asagidaki ornek, kavrami gostermek icin GUNCEL preview API tarzini
//  kullanir. Surumunuzdeki KESIN imzalar icin resmi JEP 505'e bakin.
//
//  DERLEME / CALISTIRMA (Java 25 - PREVIEW):
//      javac --release 25 --enable-preview StructuredConcurrencyGuncel.java
//      java  --enable-preview StructuredConcurrencyGuncel
//
//  NEDIR:
//    Structured Concurrency, ayni mantiksal isi yapan birden cok es zamanli
//    alt gorevi TEK BIR birim gibi ele alir. Anladigimiz "yapisal programlama"
//    mantigini (bir blok girilir, blok bitince her sey toparlanir) es zamanli
//    gorevlere tasir:
//      - Alt gorevler bir scope icinde acilir (fork edilir).
//      - Biri basarisiz olursa digerleri OTOMATIK iptal edilir.
//      - Scope bitmeden ana akis devam ETMEZ (sizan thread olmaz).
// ============================================================================

import java.util.concurrent.StructuredTaskScope;
import java.time.Duration;

public class StructuredConcurrencyGuncel {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 25: Structured Concurrency (PREVIEW - JEP 505) ===\n");
        System.out.println("(Calistirma: java --enable-preview StructuredConcurrencyGuncel)\n");

        eskiYontemAciklama();

        System.out.println("\n--- 1) BASARILI senaryo: birden cok servisten paralel veri ---");
        try {
            KullaniciProfili profil = profilGetir(42);
            System.out.println("Birlesik profil -> " + profil);
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }

        System.out.println("\n--- 2) HATA YAYILIMI / IPTAL senaryosu ---");
        try {
            profilGetirHataliServisIle(99);
        } catch (Exception e) {
            System.out.println("Beklenen davranis: bir gorev patlayinca digerleri iptal");
            System.out.println("edildi ve hata yukari yayildi -> "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static void eskiYontemAciklama() {
        System.out.println(">> ESKI YONTEM: ExecutorService + Future (yapisiz)");
        System.out.println("   var ex = Executors.newFixedThreadPool(2);");
        System.out.println("   Future<A> f1 = ex.submit(...);");
        System.out.println("   Future<B> f2 = ex.submit(...);");
        System.out.println("   A a = f1.get();   // f1 patlarsa f2 IPTAL EDILMEZ -> sizinti");
        System.out.println("   B b = f2.get();   // iptal/zaman asimi yonetimi elle yapilir");
        System.out.println("   Sorunlar: iptal yayilimi elle; thread sizintisi riski; hata");
        System.out.println("   yonetimi dagilmis; iliski (parent/child) kaybolur.\n");

        System.out.println(">> YENI YONTEM: StructuredTaskScope (PREVIEW)");
        System.out.println("   try (var scope = StructuredTaskScope.open(...)) {");
        System.out.println("       var t1 = scope.fork(() -> servisA());");
        System.out.println("       var t2 = scope.fork(() -> servisB());");
        System.out.println("       scope.join();          // ikisini birlikte bekle");
        System.out.println("       return birlestir(t1.get(), t2.get());");
        System.out.println("   } // blok bitince TUM alt gorevler toparlanir");
        System.out.println("   Avantaj: biri patlarsa digeri otomatik iptal; sizinti yok;");
        System.out.println("   hata ve iptal yapisal, okunabilir.\n");
    }

    // Basit modeller
    record Temel(int id, String ad) {}
    record SiparisOzeti(int adet, double toplam) {}
    record KullaniciProfili(int id, String ad, int siparisAdet, double siparisToplam) {}

    // ------------------------------------------------------------------------
    //  1) BASARILI: iki servisten paralel veri cekip birlestirme.
    //     NOT: Asagidaki StructuredTaskScope.open() ve Joiner kullanimi
    //     Java 25 (JEP 505) preview API tarzini yansitir.
    // ------------------------------------------------------------------------
    static KullaniciProfili profilGetir(int kullaniciId) throws Exception {
        // open(): tum alt gorevler basarili olursa devam, biri patlarsa hepsini iptal eder.
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<Object>allSuccessfulOrThrow())) {

            // Iki alt gorevi paralel baslat (sanal thread'lerde):
            StructuredTaskScope.Subtask<Temel> temelGorev =
                    scope.fork(() -> temelBilgiServisi(kullaniciId));
            StructuredTaskScope.Subtask<SiparisOzeti> siparisGorev =
                    scope.fork(() -> siparisServisi(kullaniciId));

            // Hepsinin bitmesini bekle; biri patlarsa burada istisna firlar.
            scope.join();

            // Buraya gelindiyse ikisi de basarili:
            Temel temel = temelGorev.get();
            SiparisOzeti siparis = siparisGorev.get();
            return new KullaniciProfili(
                    temel.id(), temel.ad(), siparis.adet(), siparis.toplam());
        }
    }

    // ------------------------------------------------------------------------
    //  2) HATA YAYILIMI: siparis servisi patlar -> temel servis iptal edilir,
    //     hata yukari yayilir.
    // ------------------------------------------------------------------------
    static KullaniciProfili profilGetirHataliServisIle(int kullaniciId) throws Exception {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<Object>allSuccessfulOrThrow())) {

            scope.fork(() -> temelBilgiServisi(kullaniciId));
            scope.fork(() -> hataliSiparisServisi(kullaniciId)); // patlar

            scope.join(); // burada istisna firlar
            throw new IllegalStateException("buraya ulasilmamali");
        }
    }

    // --- Sahte servisler (paralel I/O simulasyonu) ---

    static Temel temelBilgiServisi(int id) throws InterruptedException {
        System.out.println("   [temel] cagrildi...");
        Thread.sleep(Duration.ofMillis(200)); // ag gecikmesi simulasyonu
        System.out.println("   [temel] tamam.");
        return new Temel(id, "Kullanici-" + id);
    }

    static SiparisOzeti siparisServisi(int id) throws InterruptedException {
        System.out.println("   [siparis] cagrildi...");
        Thread.sleep(Duration.ofMillis(300));
        System.out.println("   [siparis] tamam.");
        return new SiparisOzeti(3, 1499.90);
    }

    static SiparisOzeti hataliSiparisServisi(int id) throws InterruptedException {
        System.out.println("   [siparis-HATALI] cagrildi...");
        Thread.sleep(Duration.ofMillis(100));
        throw new RuntimeException("Siparis servisi cevap veremedi (HTTP 503)");
    }
}
