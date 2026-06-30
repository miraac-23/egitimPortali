/*
 * =============================================================================
 *  JAVA 9 - PRIVATE INTERFACE METOTLARI
 *  (private ve private static)
 * =============================================================================
 *
 *  NEDİR?  Interface içinde 'private' ve 'private static' metot tanımlamak.
 *          Bu metotlar dışarıdan GÖRÜNMEZ; sadece aynı interface'in
 *          default/static metotları tarafından kullanılır.
 *
 *  NEDEN?  Java 8 default metotları getirdi. Ama iki default metot ortak mantık
 *          paylaşmak isteyince ya kodu KOPYALAMAK ya da ortak kodu 'public default'
 *          yapıp istemeden API'ye SIZDIRMAK gerekiyordu. private metotlar bunu çözer.
 *
 *  Derleme/Çalıştırma:
 *      javac --release 9 PrivateInterfaceMetot.java
 *      java PrivateInterfaceMetot
 * =============================================================================
 */

import java.time.LocalDateTime;
import java.util.List;

public class PrivateInterfaceMetot {

    // =========================================================================
    // GERÇEK HAYAT: Bir "Logger" interface'i. Birden çok default metot ortak
    // formatlama/zaman damgası mantığını paylaşıyor. Bu ortak mantık private
    // metotlara konularak API kirletilmiyor.
    // =========================================================================
    interface Logger {

        // --- DIŞA AÇIK (public) default metotlar ---
        default void bilgi(String mesaj) {
            // Ortak mantığı private metottan çağırıyoruz:
            yaz(seviyeEkle("BILGI", mesaj));
        }

        default void uyari(String mesaj) {
            yaz(seviyeEkle("UYARI", mesaj));
        }

        default void hata(String mesaj) {
            yaz(seviyeEkle("HATA", mesaj));
        }

        // --- PRIVATE (örnek) metot: 3 default metodun ORTAK formatlaması ---
        // Dışarıdan çağrılamaz; sadece interface içinde kullanılır.
        private String seviyeEkle(String seviye, String mesaj) {
            return zamanDamgasi() + " [" + seviye + "] " + mesaj;
        }

        // --- PRIVATE STATIC metot: durum gerektirmeyen ortak yardımcı ---
        private static String zamanDamgasi() {
            return LocalDateTime.now().toString();
        }

        // Bu, implementasyona bırakılan TEK soyut metot (gerçek çıktı yeri).
        void yaz(String satir);
    }

    // =========================================================================
    // Interface'i uygulayan somut sınıf. Sadece 'yaz' metodunu doldurur.
    // bilgi/uyari/hata zaten default; ortak mantık private metotlarda.
    // =========================================================================
    static class KonsolLogger implements Logger {
        @Override
        public void yaz(String satir) {
            System.out.println(satir);
        }
    }

    // =========================================================================
    // ESKİ (Java 8) YAKLAŞIMI - kıyas için (yorum içinde):
    //
    //   interface EskiLogger {
    //       // Ortak kodu private yapamadığımız için ya kopyalıyorduk:
    //       default void bilgi(String m) {
    //           yaz(LocalDateTime.now() + " [BILGI] " + m); // kopya format mantığı
    //       }
    //       default void uyari(String m) {
    //           yaz(LocalDateTime.now() + " [UYARI] " + m); // yine kopya
    //       }
    //       // ...ya da ortak metodu 'public default' yapıp API'yi kirletiyorduk:
    //       default String seviyeEkle(String s, String m) { ... } // İSTEMEDEN public!
    //   }
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Private Interface Metotlari ===\n");

        Logger log = new KonsolLogger();
        log.bilgi("Uygulama basladi");
        log.uyari("Disk doluluk orani %85");
        log.hata("Veritabani baglantisi koptu");

        System.out.println("\nNot: 'seviyeEkle' ve 'zamanDamgasi' metotlari private oldugu");
        System.out.println("icin disaridan (log.seviyeEkle(...)) cagrilamaz -> derleme hatasi olurdu.");

        // İspat: KonsolLogger nesnesinin dışarıya açık tek ekstra davranışı
        // bilgi/uyari/hata; private metotlar arayüzde gizli.
        List<String> disaAcikDavranislar = List.of("bilgi", "uyari", "hata", "yaz");
        System.out.println("Disa acik davranislar: " + disaAcikDavranislar);
    }
}
