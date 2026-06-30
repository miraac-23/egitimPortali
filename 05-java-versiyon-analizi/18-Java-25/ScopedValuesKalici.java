// ============================================================================
//  Java 25 - Scoped Values (JEP 506 - KALICI)
// ============================================================================
//  Scoped Values, Java 25 ile birlikte KALICI (standart) hale geldi.
//  Artik --enable-preview gerekmez.
//
//  TARIHCE (DURUST OZET):
//    - Java 20 (JEP 429): Scoped Values (incubator)
//    - Java 21 (JEP 446): preview
//    - Java 22 (JEP 464): 2. preview
//    - Java 23 (JEP 481): 3. preview
//    - Java 24 (JEP 487): 4. preview
//    - Java 25 (JEP 506): KALICI (standard)
//
//  NEDIR:
//    ScopedValue<T>, bir is parcacigi (thread) ve onun olusturdugu alt
//    gorevler boyunca DEGISMEZ (immutable) bir baglam degeri tasimanin
//    modern, guvenli yoludur. ThreadLocal'in yerine gecen, sanal thread
//    (virtual thread) dostu bir alternatiftir.
//
//  DERLEME / CALISTIRMA (Java 25):
//    javac ScopedValuesKalici.java
//    java  ScopedValuesKalici
//    (KALICI oldugu icin --enable-preview GEREKMEZ.)
// ============================================================================

import java.util.concurrent.Callable;

public class ScopedValuesKalici {

    // ------------------------------------------------------------------------
    //  ScopedValue tanimi: tum uygulama boyunca paylasilan, ama degeri sadece
    //  belirli bir "scope" (kapsam) icinde gecerli olan baglam.
    //  Tipik kullanim: oturum acmis kullanicinin kimligi, istek (request) ID'si,
    //  dil/lokal bilgisi, yetki seviyesi gibi "baglamsal" veriler.
    // ------------------------------------------------------------------------
    private static final ScopedValue<Kullanici> AKTIF_KULLANICI = ScopedValue.newInstance();
    private static final ScopedValue<String> ISTEK_ID = ScopedValue.newInstance();

    // Basit bir kullanici modeli (record ile)
    record Kullanici(String kullaniciAdi, String rol) {}

    public static void main(String[] args) {
        System.out.println("=== Java 25: Scoped Values (KALICI) ===\n");

        threadLocalVsScopedValueAciklama();

        System.out.println("\n--- GERCEK HAYAT SENARYOSU: Web istegi baglami ---");
        // Bir web sunucusunun gelen istegi isledigini hayal edin.
        // Her istek icin kullaniciyi ve istek ID'sini baglama koyuyoruz.
        gelenIstegiIsle(new Kullanici("ayse", "ADMIN"), "REQ-1001");
        System.out.println();
        gelenIstegiIsle(new Kullanici("mehmet", "USER"), "REQ-1002");

        System.out.println("\n--- KAPSAM DISI ERISIM (hata yonetimi) ---");
        try {
            // Scope disinda deger okumak istisna firlatir.
            System.out.println(AKTIF_KULLANICI.get());
        } catch (Exception e) {
            System.out.println("Beklenen hata: scope disinda deger okunamaz -> "
                    + e.getClass().getSimpleName());
        }
    }

    // ------------------------------------------------------------------------
    //  THREADLOCAL vs SCOPEDVALUE
    // ------------------------------------------------------------------------
    static void threadLocalVsScopedValueAciklama() {
        System.out.println(">> ESKI YONTEM: ThreadLocal");
        System.out.println("   ThreadLocal<Kullanici> tl = new ThreadLocal<>();");
        System.out.println("   tl.set(kullanici);   // DEGISTIRILEBILIR (mutable)");
        System.out.println("   ... is bitince ...");
        System.out.println("   tl.remove();          // UNUTULURSA bellek sizintisi!");
        System.out.println("   Sorunlar:");
        System.out.println("   - remove() unutulursa thread havuzlarinda sizinti olur.");
        System.out.println("   - Her yerden set() ile degistirilebilir -> izlenmesi zor.");
        System.out.println("   - Milyonlarca sanal thread'de hafiza maliyeti yuksek.\n");

        System.out.println(">> YENI YONTEM: ScopedValue");
        System.out.println("   ScopedValue.where(AKTIF_KULLANICI, kullanici)");
        System.out.println("              .run(() -> { ... });  // sadece bu blokta gecerli");
        System.out.println("   Avantajlar:");
        System.out.println("   - DEGISMEZ: blok icinde deger degistirilemez -> guvenli.");
        System.out.println("   - OTOMATIK temizlik: blok bitince deger gecersizlesir,");
        System.out.println("     remove() cagirmaya gerek YOK.");
        System.out.println("   - Sanal thread dostu: alt gorevlere otomatik miras kalir.");
        System.out.println("   - Kapsam acikca bellidir (lexical scope).\n");
    }

    // ------------------------------------------------------------------------
    //  GERCEK HAYAT: Gelen bir istegi isleme
    // ------------------------------------------------------------------------
    static void gelenIstegiIsle(Kullanici kullanici, String istekId) {
        // ScopedValue.where(...).run(...) ile baglami SADECE bu blok ve
        // bu bloktan cagrilan tum alt metotlar icin gecerli kiliyoruz.
        ScopedValue.where(AKTIF_KULLANICI, kullanici)
                   .where(ISTEK_ID, istekId)
                   .run(() -> {
                       System.out.println("[" + istekId + "] Istek alindi.");
                       // Alt katmanlar baglami parametre tasimadan okuyabilir:
                       servisKatmaniCagir();
                   });
    }

    // Bu metot kullanici parametresi ALMADI ama baglamdan okuyabiliyor.
    static void servisKatmaniCagir() {
        String istekId = ISTEK_ID.get();
        System.out.println("[" + istekId + "] Servis katmani calisiyor...");
        veriErisimKatmaniCagir();
    }

    static void veriErisimKatmaniCagir() {
        // En alt katman bile, parametre zinciri olmadan aktif kullaniciya erisir.
        Kullanici k = AKTIF_KULLANICI.get();
        String istekId = ISTEK_ID.get();
        System.out.println("[" + istekId + "] Veri erisimi: kullanici="
                + k.kullaniciAdi() + ", rol=" + k.rol());

        // Yetki kontrolu ornegi:
        if ("ADMIN".equals(k.rol())) {
            System.out.println("[" + istekId + "] -> ADMIN: tum kayitlara erisim verildi.");
        } else {
            System.out.println("[" + istekId + "] -> USER: sadece kendi kayitlarina erisim.");
        }
    }

    // ------------------------------------------------------------------------
    //  Deger DONDUREN bir scope ornegi (call ile) - referans amacli
    // ------------------------------------------------------------------------
    @SuppressWarnings("unused")
    static <T> T baglamdaCalistir(Kullanici k, Callable<T> gorev) throws Exception {
        // run(Runnable) deger dondurmez; deger dondurmek icin call(Callable) kullanilir.
        return ScopedValue.where(AKTIF_KULLANICI, k).call(gorev);
    }
}
