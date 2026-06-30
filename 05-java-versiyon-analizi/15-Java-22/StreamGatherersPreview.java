// =============================================================================
//  Java 22 - Stream Gatherers  (JEP 461 - PREVIEW / ÖNİZLEME)
// =============================================================================
//
//  ÖNEMLİ NOT:
//  -----------
//  Stream Gatherers, Java 22'de PREVIEW (önizleme) özelliğidir.
//  Bu nedenle derlerken VE çalıştırırken --enable-preview GEREKLİDİR.
//  (Java 23'te ikinci preview, Java 24'te kalıcı hale gelmiştir; ama bu
//   dosya Java 22 hedefiyle hazırlanmıştır.)
//
//  DERLEME:
//      javac --release 22 --enable-preview StreamGatherersPreview.java
//  ÇALIŞTIRMA:
//      java --enable-preview StreamGatherersPreview
//
//  (NOT: --enable-preview olmadan derleme/çalıştırma HATA verir.)
//
//  STREAM GATHERERS NEDİR?
//  -----------------------
//  Stream API'de  filter / map / flatMap gibi sabit bir ara (intermediate)
//  operasyon kümesi vardı; KENDİ ara operasyonunu yazmak mümkün değildi.
//  Gatherer arayüzü bunu çözer:  Stream.gather(Gatherer) ile ÖZEL ara
//  operasyonlar tanımlayabiliriz. Terminal taraftaki Collector'ın
//  "ara operasyon" karşılığı gibidir.
//
//  HAZIR (built-in) GATHERER'LAR (java.util.stream.Gatherers):
//    - windowFixed(n)   : ardışık n'li bloklara böler (kayan değil)
//    - windowSliding(n) : n boyutlu KAYAN pencereler üretir
//    - fold(init, fn)   : tek bir sonuca katlar (akış sonunda 1 eleman yayar)
//    - scan(init, fn)   : kümülatif/running ara sonuçları yayar
//
//  Gatherer'ın 4 bileşeni vardır:
//    initializer() : (opsiyonel) durum (state) nesnesi oluşturur
//    integrator()  : her elemanı işler, downstream'e eleman yayabilir
//    combiner()    : (opsiyonel) paralel akışta state'leri birleştirir
//    finisher()    : (opsiyonel) akış bitince kalan state'i yayar
// =============================================================================

import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class StreamGatherersPreview {

    public static void main(String[] args) {
        System.out.println("=== Java 22 Stream Gatherers (PREVIEW) ===\n");

        ornek1_windowFixed();
        ornek2_windowSliding();
        ornek3_fold();
        ornek4_scan();
        ornek5_ozelGatherer_distinctArdisik();
        ornek6_ozelGatherer_limitWhile();

        System.out.println("\n=== Tüm gatherer örnekleri tamamlandı ===");
    }

    // -------------------------------------------------------------------------
    //  1) windowFixed(n) : ardışık SABİT bloklar
    // -------------------------------------------------------------------------
    private static void ornek1_windowFixed() {
        System.out.println("--- 1) Gatherers.windowFixed(3) ---");

        // Gerçek hayat: 8 ölçüm değerini 3'erli partilere böl (batch işleme).
        List<List<Integer>> bloklar = Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                .gather(Gatherers.windowFixed(3))
                .toList();

        System.out.println("  3'erli bloklar: " + bloklar);
        // -> [[1,2,3],[4,5,6],[7,8]]  (son blok eksik olabilir)
    }

    // -------------------------------------------------------------------------
    //  2) windowSliding(n) : KAYAN pencere
    // -------------------------------------------------------------------------
    private static void ornek2_windowSliding() {
        System.out.println("--- 2) Gatherers.windowSliding(3) ---");

        // Gerçek hayat: hareketli ortalama (moving average) için 3'lü kayan
        // pencereler oluştur.
        List<Double> hareketliOrtalama = Stream.of(10, 20, 30, 40, 50)
                .gather(Gatherers.windowSliding(3))
                .map(pencere -> pencere.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0))
                .toList();

        System.out.println("  3'lü kayan pencere ortalamaları: " + hareketliOrtalama);
        // pencereler: [10,20,30],[20,30,40],[30,40,50] -> [20.0, 30.0, 40.0]
    }

    // -------------------------------------------------------------------------
    //  3) fold(init, fn) : tek bir sonuca katla (akış sonunda 1 eleman)
    // -------------------------------------------------------------------------
    private static void ornek3_fold() {
        System.out.println("--- 3) Gatherers.fold ---");

        // Gerçek hayat: kelimeleri tek bir cümleye katla.
        String cumle = Stream.of("Java", "22", "Stream", "Gatherers")
                .gather(Gatherers.fold(
                        () -> "",                                  // başlangıç değeri
                        (acc, kelime) -> acc.isEmpty()
                                ? kelime
                                : acc + " " + kelime))             // birleştirme
                .findFirst()
                .orElse("");

        System.out.println("  fold sonucu: \"" + cumle + "\"");
    }

    // -------------------------------------------------------------------------
    //  4) scan(init, fn) : kümülatif (running) sonuçları yay
    // -------------------------------------------------------------------------
    private static void ornek4_scan() {
        System.out.println("--- 4) Gatherers.scan (kümülatif toplam) ---");

        // Gerçek hayat: aylık gelirlerin kümülatif (yıl-içi birikimli) toplamı.
        List<Integer> kumulatif = Stream.of(100, 200, 300, 400)
                .gather(Gatherers.scan(() -> 0, (toplam, deger) -> toplam + deger))
                .toList();

        System.out.println("  Kümülatif toplamlar: " + kumulatif);
        // -> [100, 300, 600, 1000]
    }

    // -------------------------------------------------------------------------
    //  5) ÖZEL GATHERER: ardışık tekrarları temizle (distinctArdisik)
    //     [1,1,2,2,2,3,1] -> [1,2,3,1]
    //     (Stream.distinct globaldir; bu sadece ARDIŞIK tekrarı atar.)
    // -------------------------------------------------------------------------
    private static void ornek5_ozelGatherer_distinctArdisik() {
        System.out.println("--- 5) ÖZEL Gatherer: ardışık tekrarları at ---");

        List<Integer> sonuc = Stream.of(1, 1, 2, 2, 2, 3, 1, 1)
                .gather(ardisikTekrarsiz())
                .toList();

        System.out.println("  Ardışık tekrarsız: " + sonuc);
        // -> [1, 2, 3, 1]
    }

    /**
     * State'li (durum tutan) özel bir Gatherer.
     * Bir önceki elemanı hatırlar; yeni eleman ondan farklıysa downstream'e yayar.
     *
     *  T tipi:  giriş ve çıkış elemanları (aynı tip)
     *  state :  son görülen değeri tutan tek elemanlı dizi (mutable kapsül)
     */
    private static <T> Gatherer<T, ?, T> ardisikTekrarsiz() {
        return Gatherer.ofSequential(
                // initializer: state = "henüz eleman görülmedi" işaretçili kapsül
                () -> new Object[]{ false, null },   // [gorulduMu, sonDeger]
                // integrator: her eleman için çalışır
                Gatherer.Integrator.ofGreedy((state, eleman, downstream) -> {
                    boolean gorulduMu = (boolean) state[0];
                    @SuppressWarnings("unchecked")
                    T sonDeger = (T) state[1];

                    if (!gorulduMu || !java.util.Objects.equals(sonDeger, eleman)) {
                        state[0] = true;
                        state[1] = eleman;
                        // downstream.push: elemanı bir sonraki aşamaya yay.
                        // false dönerse akış erken sonlanmış demektir.
                        return downstream.push(eleman);
                    }
                    // Ardışık tekrar -> yayma, akışa devam et.
                    return true;
                })
        );
    }

    // -------------------------------------------------------------------------
    //  6) ÖZEL GATHERER: koşul sağlandığı sürece al (limitWhile / takeWhile benzeri)
    //     Burada downstream.push'in false dönmesiyle ERKEN sonlandırmayı gösteriyoruz.
    // -------------------------------------------------------------------------
    private static void ornek6_ozelGatherer_limitWhile() {
        System.out.println("--- 6) ÖZEL Gatherer: toplam belli eşiği aşana kadar al ---");

        // Gerçek hayat: kümülatif toplam 100'ü aşana KADAR elemanları al,
        // aşan elemandan SONRA akışı erken durdur.
        List<Integer> sonuc = Stream.of(30, 40, 50, 60, 70)
                .gather(toplamEsigeKadar(100))
                .toList();

        System.out.println("  Kümülatif toplam 100'ü aşana kadar: " + sonuc);
        // 30 (top=30), 40 (top=70), 50 (top=120 -> bunu da al ama sonra dur)
        // -> [30, 40, 50]
    }

    private static Gatherer<Integer, ?, Integer> toplamEsigeKadar(int esik) {
        return Gatherer.ofSequential(
                () -> new int[]{ 0 },   // state: kümülatif toplam
                Gatherer.Integrator.of((state, eleman, downstream) -> {
                    state[0] += eleman;
                    boolean devam = downstream.push(eleman);
                    if (state[0] > esik) {
                        // Eşik aşıldı: bu elemanı yaydık ama akışı SONLANDIR.
                        return false;   // false -> üst akışı erken bitir
                    }
                    return devam;
                })
        );
    }
}
