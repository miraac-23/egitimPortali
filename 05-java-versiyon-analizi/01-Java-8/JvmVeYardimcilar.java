import java.util.Arrays;
import java.util.Random;

/**
 * ============================================================================
 *  JVM DEĞİŞİKLİKLERİ ve YARDIMCI YENİLİKLER - Java 8
 * ============================================================================
 *
 * Bu dosya iki konuyu örnekler:
 *   A) Arrays.parallelSort  : Büyük dizileri çoklu çekirdekte sıralama
 *   B) PermGen -> Metaspace  : JVM bellek modeli değişikliği (anlatım + kanıt)
 *
 * ----------------------------------------------------------------------------
 * A) Arrays.parallelSort
 *   NEDİR? Diziyi paralel (fork/join havuzu ile) sıralayan metot.
 *   NEDEN GELDİ? Arrays.sort tek iş parçacığında çalışır. Çok büyük dizilerde
 *     çok çekirdekli işlemcileri kullanmak için parallelSort eklendi.
 *   NE İŞE YARAR: Büyük veri kümelerinde sıralamayı hızlandırır.
 *   DİKKAT: Küçük dizilerde ek yük nedeniyle normal sort daha hızlı olabilir.
 *
 * ----------------------------------------------------------------------------
 * B) PermGen Kaldırıldı -> Metaspace Geldi (JVM iç değişikliği)
 *   ESKİ (Java 7 ve öncesi): PermGen (Permanent Generation) adında SABİT
 *     boyutlu bir bellek alanı vardı. Sınıf metadata'sı (yüklenen sınıf
 *     bilgileri) burada tutulurdu. Çok sayıda sınıf yükleyen uygulamalarda
 *     (özellikle uygulama sunucuları, sık deploy yapan sistemler) sıkça
 *     "java.lang.OutOfMemoryError: PermGen space" hatası alınırdı.
 *     -XX:MaxPermSize ile boyut elle ayarlanırdı; ayarlamak zordu.
 *
 *   YENİ (Java 8): PermGen tamamen KALDIRILDI. Yerine METASPACE geldi.
 *     - Metaspace, JVM heap'inde DEĞİL, yerel (native) bellekte tutulur.
 *     - Varsayılan olarak boyutu OTOMATİK büyür (sınırı yalnızca işletim
 *       sistemi belleğidir; -XX:MaxMetaspaceSize ile sınırlanabilir).
 *     - Sonuç: "PermGen space" hatası tarihe karıştı; sınıf metadata yönetimi
 *       daha esnek ve daha az sorunlu hale geldi.
 */
public class JvmVeYardimcilar {

    public static void main(String[] args) {

        System.out.println("=== A) Arrays.parallelSort vs Arrays.sort ===\n");

        int boyut = 2_000_000;
        int[] dizi1 = rastgeleDizi(boyut);
        int[] dizi2 = Arrays.copyOf(dizi1, dizi1.length); // ayni veri

        // ESKİ/Klasik: tek iş parçacıklı sort
        long t1 = System.currentTimeMillis();
        Arrays.sort(dizi1);
        long sureSort = System.currentTimeMillis() - t1;

        // YENİ: paralel sort (fork/join)
        long t2 = System.currentTimeMillis();
        Arrays.parallelSort(dizi2);
        long sureParalel = System.currentTimeMillis() - t2;

        System.out.println("Dizi boyutu        : " + boyut);
        System.out.println("Arrays.sort        : " + sureSort + " ms");
        System.out.println("Arrays.parallelSort: " + sureParalel + " ms");
        System.out.println("Cekirdek sayisi    : " + Runtime.getRuntime().availableProcessors());
        System.out.println("Iki sonuc esit mi? : " + Arrays.equals(dizi1, dizi2));
        System.out.println("(Not: kucuk dizilerde paralel daha YAVAS olabilir.)");

        System.out.println("\n=== B) PermGen -> Metaspace (JVM bellek bilgisi) ===\n");
        // Çalışılan JVM sürümü hakkında bilgi - Java 8'de PermGen yoktur
        System.out.println("Java surumu        : " + System.getProperty("java.version"));
        System.out.println("JVM adi            : " + System.getProperty("java.vm.name"));

        long maxHeap = Runtime.getRuntime().maxMemory();
        System.out.printf("Max heap (yaklasik): %.0f MB%n", maxHeap / (1024.0 * 1024.0));

        System.out.println();
        System.out.println("Java 7'de sinif metadata'si SABIT boyutlu PermGen'de tutulurdu");
        System.out.println("ve 'OutOfMemoryError: PermGen space' sik gorulurdu.");
        System.out.println("Java 8'de PermGen kaldirildi; metadata artik native bellekteki");
        System.out.println("METASPACE'te tutulur ve otomatik buyur. Bu hata tarihe karisti.");
    }

    static int[] rastgeleDizi(int boyut) {
        Random r = new Random(42); // sabit tohum -> tekrarlanabilir
        int[] dizi = new int[boyut];
        for (int i = 0; i < boyut; i++) {
            dizi[i] = r.nextInt();
        }
        return dizi;
    }
}
