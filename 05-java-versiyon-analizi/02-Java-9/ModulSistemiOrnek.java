/*
 * =============================================================================
 *  JAVA 9 - MODÜL SİSTEMİ (JPMS / Project Jigsaw)
 * =============================================================================
 *
 *  Bu dosya, Java 9'un EN ÖNEMLİ özelliği olan Modül Sistemini DERİNLEMESİNE
 *  anlatır. Modül sistemi normalde birden çok klasör + module-info.java
 *  dosyası gerektirir; bu yüzden gerçek modül yapısını YORUM BLOKLARI olarak
 *  veriyoruz. Aşağıdaki "main" metodu ise modül sisteminin RUNTIME tarafını
 *  (ModuleLayer, Module API, ServiceLoader) çalıştırılabilir biçimde gösterir.
 *
 *  NOT: Bu tek dosya, "unnamed module" (isimsiz modül) içinde de derlenip
 *  çalışabilir; çünkü gösterdiğimiz API'ler java.base içindedir.
 *
 *  Derleme/Çalıştırma (tek dosya, classpath):
 *      javac --release 9 ModulSistemiOrnek.java
 *      java ModulSistemiOrnek
 * =============================================================================
 */

import java.lang.module.ModuleDescriptor;   // Modül tanımını okumak için (Java 9+)
import java.util.ServiceLoader;             // uses/provides ile çalışan servis mekanizması

public class ModulSistemiOrnek {

    /*
     * -------------------------------------------------------------------------
     *  BÖLÜM 1: MODÜL NEDİR?  (Kavramsal)
     * -------------------------------------------------------------------------
     *  Java 9'a kadar kodun en üst organizasyon birimi PAKET ve JAR idi.
     *  JAR'lar sadece sıkıştırılmış .class dosyalarıydı; "ben kime bağımlıyım,
     *  hangi paketi dışarı açıyorum" bilgisini TAŞIMIYORDU.
     *
     *  Modül = isimlendirilmiş bir paket grubu + kendini tanımlayan
     *  "module-info.java" dosyası.
     *
     *  Bir modül şunları açıkça beyan eder:
     *    - requires  : hangi modüllere bağımlıyım
     *    - exports   : hangi paketlerimi dışarıya açıyorum (geri kalanı GİZLİ)
     *    - opens     : hangi paketlerimi REFLECTION'a açıyorum (Spring/Jackson vs.)
     *    - uses      : hangi servis arayüzünü kullanacağım (ServiceLoader)
     *    - provides  : bu servis arayüzü için hangi implementasyonu sağlıyorum
     */

    /*
     * -------------------------------------------------------------------------
     *  BÖLÜM 2: ÖRNEK MODÜLER UYGULAMA YAPISI (Dosya yerleşimi)
     * -------------------------------------------------------------------------
     *
     *  Bir bankacılık uygulamasını 3 modüle bölelim:
     *
     *  src/
     *   ├─ com.banka.cekirdek/                  <-- ÇEKİRDEK modül (API + iç mantık)
     *   │   ├─ module-info.java
     *   │   └─ com/banka/cekirdek/
     *   │        ├─ api/HesapServisi.java        (DIŞARI AÇIK paket)
     *   │        ├─ api/Hesap.java               (DIŞARI AÇIK paket)
     *   │        └─ internal/HesapHesaplayici.java  (GİZLİ paket - exports yok!)
     *   │
     *   ├─ com.banka.log/                       <-- SERVİS SAĞLAYICI modül
     *   │   ├─ module-info.java
     *   │   └─ com/banka/log/DosyaLogcu.java     (LogServisi implementasyonu)
     *   │
     *   └─ com.banka.uygulama/                  <-- ANA uygulama modülü
     *       ├─ module-info.java
     *       └─ com/banka/uygulama/Main.java
     */

    /*
     * -------------------------------------------------------------------------
     *  BÖLÜM 3: module-info.java ÖRNEKLERİ
     * -------------------------------------------------------------------------
     *
     *  ====== src/com.banka.cekirdek/module-info.java ======
     *
     *      module com.banka.cekirdek {
     *
     *          // Bu modül JDBC kullanıyor; java.sql modülüne bağımlı.
     *          requires java.sql;
     *
     *          // 'api' paketi DIŞARIYA AÇIK. Başka modüller kullanabilir.
     *          exports com.banka.cekirdek.api;
     *
     *          // DİKKAT: 'internal' paketi EXPORTS EDİLMEDİ.
     *          // Public sınıflar bile içerse, dışarıdan ERİŞİLEMEZ.
     *          // Derleyici, başka modülün buna erişimini REDDEDER. (Güçlü kapsülleme)
     *
     *          // Bir servis arayüzü tanımlayıp dışa açıyoruz:
     *          exports com.banka.cekirdek.spi;       // LogServisi arayüzü burada
     *
     *          // Bu modül 'LogServisi' implementasyonlarını ServiceLoader ile arar:
     *          uses com.banka.cekirdek.spi.LogServisi;
     *      }
     *
     *  ====== src/com.banka.log/module-info.java ======
     *
     *      module com.banka.log {
     *          requires com.banka.cekirdek;          // arayüze ulaşmak için
     *
     *          // LogServisi arayüzünün implementasyonunu SAĞLIYORUZ:
     *          provides com.banka.cekirdek.spi.LogServisi
     *               with com.banka.log.DosyaLogcu;
     *      }
     *
     *  ====== src/com.banka.uygulama/module-info.java ======
     *
     *      module com.banka.uygulama {
     *          requires com.banka.cekirdek;
     *
     *          // Spring/Jackson gibi bir framework bu paketi reflection ile okusun
     *          // istiyorsak (yoksa InaccessibleObjectException alırız):
     *          opens com.banka.uygulama to com.fasterxml.jackson.databind;
     *      }
     *
     *  -------------------------------------------------------------------------
     *  DERLEME / ÇALIŞTIRMA (çok modüllü):
     *
     *      javac -d out --module-source-path src $(find src -name "*.java")
     *      java  --module-path out -m com.banka.uygulama/com.banka.uygulama.Main
     *
     *  jlink ile özel KÜÇÜK runtime üretmek:
     *      jlink --module-path "$JAVA_HOME/jmods:out" \
     *            --add-modules com.banka.uygulama \
     *            --output ozel-runtime
     *      ./ozel-runtime/bin/java -m com.banka.uygulama/com.banka.uygulama.Main
     * -------------------------------------------------------------------------
     */

    /*
     * -------------------------------------------------------------------------
     *  BÖLÜM 4: ESKİ (Java 8 classpath) vs YENİ (Java 9 module)
     * -------------------------------------------------------------------------
     *
     *  ESKİ (Java 8):
     *    - Tüm public sınıflar classpath'teki herkese AÇIK.
     *    - 'internal' diye işaretlemek sadece bir İSİMLENDİRME KONVANSİYONU,
     *      derleyici garantisi YOK.
     *    - Eksik JAR -> RUNTIME'da NoClassDefFoundError.
     *    - sun.misc.Unsafe gibi iç API'ler serbestçe kullanılır.
     *
     *  YENİ (Java 9):
     *    - Sadece 'exports' edilen paketler erişilebilir.
     *    - Eksik 'requires' -> DERLEME/BAŞLANGIÇ anında hata (güvenilir konfig.).
     *    - JDK iç API'leri kapalı; gerekirse --add-exports/--add-opens.
     * -------------------------------------------------------------------------
     */

    public static void main(String[] args) {
        System.out.println("=== JAVA 9 MODÜL SİSTEMİ - RUNTIME TARAFI ===\n");

        // ---------------------------------------------------------------------
        // 1) Çalışan kodun bulunduğu MODÜLÜ öğrenme.
        //    Bu sınıf classpath'ten çalışıyorsa "unnamed module" içindedir.
        // ---------------------------------------------------------------------
        Module buModul = ModulSistemiOrnek.class.getModule();
        System.out.println("Bu sinifin modulu isimli mi? : " + buModul.isNamed());
        System.out.println("Modul adi (isimsizse null/bos): " + buModul.getName());
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) JDK'nın kendisi de modülerdir. 'String' sınıfı 'java.base'
        //    modülündedir. Bunu runtime'da kanıtlayalım.
        // ---------------------------------------------------------------------
        Module stringModulu = String.class.getModule();
        System.out.println("java.lang.String hangi modulde? : " + stringModulu.getName());

        Module listModulu = java.util.List.class.getModule();
        System.out.println("java.util.List hangi modulde?   : " + listModulu.getName());

        Module sqlModulu = java.sql.Connection.class.getModule();
        System.out.println("java.sql.Connection hangi modulde?: " + sqlModulu.getName());
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) Bir modülün TANIMINI (descriptor) okuma.
        //    module-info'daki requires/exports bilgisi runtime'da burada durur.
        // ---------------------------------------------------------------------
        ModuleDescriptor tanim = stringModulu.getDescriptor();
        if (tanim != null) {
            System.out.println("java.base modulunun tanimi:");
            System.out.println("  Ad      : " + tanim.name());
            System.out.println("  Otomatik mi? : " + tanim.isAutomatic());
            System.out.println("  Disa acilan paket sayisi (exports): " + tanim.exports().size());
            // İlk birkaç export edilen paketi gösterelim:
            tanim.exports().stream()
                 .map(ModuleDescriptor.Exports::source)
                 .sorted()
                 .limit(5)
                 .forEach(p -> System.out.println("    exports -> " + p));
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 4) Sistemde yüklü bazı modülleri listeleme (ModuleLayer.boot()).
        //    Java 9'da çalışan tüm modüller "boot layer" içinde bulunur.
        // ---------------------------------------------------------------------
        System.out.println("Boot layer'daki bazi moduller (ilk 8):");
        ModuleLayer.boot().modules().stream()
                .map(Module::getName)
                .sorted()
                .limit(8)
                .forEach(ad -> System.out.println("  - " + ad));
        System.out.println();

        // ---------------------------------------------------------------------
        // 5) uses / provides -> ServiceLoader mantığı.
        //    Modüler dünyada 'uses com.banka.cekirdek.spi.LogServisi' diyen modül,
        //    implementasyonu ServiceLoader ile şöyle bulur (sözde kod):
        //
        //      ServiceLoader<LogServisi> sl = ServiceLoader.load(LogServisi.class);
        //      LogServisi log = sl.findFirst().orElseThrow();
        //
        //    Burada gerçek bir JDK servisini (zaten yüklü) örnekleyelim:
        // ---------------------------------------------------------------------
        System.out.println("ServiceLoader ornegi (JDK'nin kendi servisleri):");
        ServiceLoader<java.nio.file.spi.FileSystemProvider> saglayicilar =
                ServiceLoader.load(java.nio.file.spi.FileSystemProvider.class);
        long adet = 0;
        for (java.nio.file.spi.FileSystemProvider p : saglayicilar) {
            System.out.println("  Bulunan FileSystemProvider: " + p.getScheme());
            adet++;
            if (adet >= 3) break;
        }
        if (adet == 0) {
            System.out.println("  (Bu ortamda ek saglayici bulunamadi - normaldir.)");
        }

        System.out.println("\nOzet: Modul sistemi = guclu kapsulleme + guvenilir konfigurasyon");
        System.out.println("      + olceklenebilir platform (jlink). Java 9'un kalbidir.");
    }
}
