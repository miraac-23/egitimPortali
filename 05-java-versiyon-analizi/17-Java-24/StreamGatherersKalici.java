import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * ============================================================================
 *  Java 24 - Stream Gatherers (JEP 485) ARTIK KALICI / STANDART
 * ============================================================================
 *
 *  ONEMLI: Java 22 ve 23'te Stream Gatherers "preview" idi ve derleme/calistirma
 *  icin "--enable-preview" bayragi GEREKIYORDU. JAVA 24 ile KALICILASTI:
 *
 *      javac StreamGatherersKalici.java     // --enable-preview YOK !
 *      java  StreamGatherersKalici          // --enable-preview YOK !
 *
 *  Gatherer NEDIR?
 *  --------------
 *  Collector terminal (sonlandirici) islemler icin ne yapiyorsa, Gatherer da
 *  ARA (intermediate) islemler icin onu yapar. stream.gather(...) ile cagrilir.
 *  Durum tutabilir (stateful), kisa devre (short-circuit) yapabilir, paralel
 *  calisabilir ve tamamen ozellestirilebilir.
 *
 *  Gatherer'in 4 bileseni:
 *    - initializer : durum (state) nesnesi olusturur   (opsiyonel)
 *    - integrator  : her elemani isler, downstream'e iter, kisa devre yapabilir
 *    - combiner    : paralel akista durumlari birlestirir (opsiyonel)
 *    - finisher    : akis bitince kalan elemanlari iter   (opsiyonel)
 *
 *  Bu dosya gercek hayat senaryolariyla 5 ornek icerir.
 * ============================================================================
 */
public class StreamGatherersKalici {

    public static void main(String[] args) {
        System.out.println("=== Java 24 Stream Gatherers (KALICI - preview degil) ===\n");

        ornek1_windowFixedBatchIsleme();
        ornek2_windowSlidingHareketliOrtalama();
        ornek3_scanKosanBakiye();
        ornek4_ozelGathererArdisikTekillestirme();
        ornek5_ozelGathererKisaDevreIlkNTaneEsiktenBuyuk();
    }

    // ------------------------------------------------------------------------
    // ORNEK 1: windowFixed -> Gercek hayat: gelen odeme islemlerini 100'erli
    //          (burada 3'erli) paketler halinde toplu API'ye gondermek icin
    //          sabit boyutlu pencerelere bolme (batch'leme).
    // ------------------------------------------------------------------------
    static void ornek1_windowFixedBatchIsleme() {
        System.out.println("--- ORNEK 1: windowFixed (toplu / batch islem) ---");

        // Diyelim ki gercek zamanli olarak gelen 8 odeme islemi var.
        List<String> odemeler = List.of(
                "ODM-001", "ODM-002", "ODM-003",
                "ODM-004", "ODM-005", "ODM-006",
                "ODM-007", "ODM-008"
        );

        // Bunlari 3'erli gruplar halinde paketleyip her grubu toplu gonderelim.
        List<List<String>> paketler = odemeler.stream()
                .gather(Gatherers.windowFixed(3)) // sabit boyutlu pencere
                .toList();

        int paketNo = 1;
        for (List<String> paket : paketler) {
            // Gercekte burada: toplaApiyeGonder(paket);
            System.out.println("  Paket #" + (paketNo++) + " gonderiliyor -> " + paket);
        }
        System.out.println();
    }

    // ------------------------------------------------------------------------
    // ORNEK 2: windowSliding -> Gercek hayat: sensor/telemetri verisinden
    //          kayan pencere ile HAREKETLI ORTALAMA hesaplama.
    // ------------------------------------------------------------------------
    static void ornek2_windowSlidingHareketliOrtalama() {
        System.out.println("--- ORNEK 2: windowSliding (hareketli ortalama) ---");

        // Sicaklik sensorunden gelen olcumler.
        List<Integer> olcumler = List.of(20, 22, 21, 25, 30, 28, 26);

        // 3'lu kayan pencere ile her pencerenin ortalamasini al.
        List<Double> hareketliOrtalama = olcumler.stream()
                .gather(Gatherers.windowSliding(3)) // 3 elemanli kayan pencere
                .map(pencere -> pencere.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0))
                .toList();

        System.out.println("  Olcumler         : " + olcumler);
        System.out.println("  3'lu hareketli ort: " + hareketliOrtalama);
        System.out.println();
    }

    // ------------------------------------------------------------------------
    // ORNEK 3: scan -> Gercek hayat: bir hesabin islem listesinden KOSAN BAKIYE
    //          (running balance) uretme. scan, kumulatif/birikimli hesaptir.
    // ------------------------------------------------------------------------
    static void ornek3_scanKosanBakiye() {
        System.out.println("--- ORNEK 3: scan (kosan/birikimli bakiye) ---");

        // Pozitif = para yatirma, Negatif = para cekme.
        List<Integer> hareketler = List.of(100, -30, 50, -20, -10);

        // scan: baslangic degeri 0, her adimda yeni bakiye = onceki + hareket.
        List<Integer> kosanBakiye = hareketler.stream()
                .gather(Gatherers.scan(() -> 0, (bakiye, hareket) -> bakiye + hareket))
                .toList();

        System.out.println("  Hareketler  : " + hareketler);
        System.out.println("  Kosan bakiye: " + kosanBakiye);
        System.out.println();
    }

    // ------------------------------------------------------------------------
    // ORNEK 4: OZEL Gatherer -> Ardisik (consecutive) tekrarlari teklestirme.
    //          Yani sadece bir onceki elemanla ayni olani atla.
    //          ("distinct" tum akista benzersiz yaparken, bu sadece ARDISIK
    //           tekrarlari kaldirir.)
    //
    //          Gatherer.ofSequential ile durum tutan ozel bir ara islem yazariz.
    // ------------------------------------------------------------------------
    static void ornek4_ozelGathererArdisikTekillestirme() {
        System.out.println("--- ORNEK 4: ozel Gatherer (ardisik tekillestirme) ---");

        List<String> girdi = List.of("A", "A", "B", "B", "B", "A", "C", "C");

        List<String> sonuc = girdi.stream()
                .gather(ardisikTekilGatherer())
                .toList();

        System.out.println("  Girdi : " + girdi);
        System.out.println("  Sonuc : " + sonuc);
        System.out.println();
    }

    /**
     * Ardisik tekrarlari eleyen ozel Gatherer.
     * Durum: bir onceki gorulen eleman (mutable kutu olarak Object[]).
     */
    static <T> Gatherer<T, ?, T> ardisikTekilGatherer() {
        return Gatherer.<T, Object[], T>ofSequential(
                // initializer: durum nesnesi - tek elemanli dizi (mutable kutu)
                () -> new Object[]{null, Boolean.FALSE}, // [0]=onceki, [1]=baslatildi mi
                // integrator: her elemani isle
                Gatherer.Integrator.ofGreedy((state, eleman, downstream) -> {
                    boolean baslatildi = (Boolean) state[1];
                    Object onceki = state[0];
                    if (!baslatildi || !java.util.Objects.equals(onceki, eleman)) {
                        state[0] = eleman;
                        state[1] = Boolean.TRUE;
                        return downstream.push(eleman); // downstream'e ilet
                    }
                    return true; // ardisik tekrar -> atla, akisa devam
                })
        );
    }

    // ------------------------------------------------------------------------
    // ORNEK 5: OZEL Gatherer + KISA DEVRE -> Esik degerin uzerindeki ilk N
    //          elemani bulunca akisi durdurma (short-circuit).
    //          Gercek hayat: bir log akisinda ilk 2 "kritik" olayi yakalayinca
    //          islemeyi durdurmak (gereksiz veriyi taramamak).
    // ------------------------------------------------------------------------
    static void ornek5_ozelGathererKisaDevreIlkNTaneEsiktenBuyuk() {
        System.out.println("--- ORNEK 5: ozel Gatherer + kisa devre (ilk N kritik) ---");

        // Cok buyuk/sonsuz bir akisi simule edelim.
        Stream<Integer> sonsuzAkis = Stream.iterate(1, n -> n + 1);

        int esik = 50;
        int kacTane = 2;

        List<Integer> ilkKritikler = sonsuzAkis
                .gather(ilkNTaneEsiktenBuyukGatherer(esik, kacTane))
                .toList();

        System.out.println("  Esik=" + esik + ", istenen adet=" + kacTane);
        System.out.println("  Bulunanlar: " + ilkKritikler
                + "  (akis erken durduruldu -> sonsuz akista bile takilmadi)");
        System.out.println();
    }

    /**
     * Esik degerinden buyuk ilk "limit" adet elemani gecirir, sonra akisi keser.
     * Kisa devre: integrator false donerse akis durur.
     */
    static Gatherer<Integer, ?, Integer> ilkNTaneEsiktenBuyukGatherer(int esik, int limit) {
        return Gatherer.<Integer, int[], Integer>ofSequential(
                () -> new int[]{0}, // durum: kac tane gecirdik
                (state, eleman, downstream) -> {
                    if (eleman > esik) {
                        boolean devam = downstream.push(eleman);
                        state[0]++;
                        if (state[0] >= limit) {
                            return false; // YETERLI -> kisa devre, akisi durdur
                        }
                        return devam;
                    }
                    return true; // esigin altinda -> atla, devam et
                }
        );
    }

    // ------------------------------------------------------------------------
    // Yardimci: Optional ornegi sustur (kullanilmiyor ama import temizligi icin)
    // ------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private static Optional<String> kullanilmayan() {
        return Optional.empty();
    }

    // not: ArrayList importu, ileride genisletme/elle ornek icin korunuyor
    @SuppressWarnings("unused")
    private static List<String> bos() {
        return new ArrayList<>();
    }
}
