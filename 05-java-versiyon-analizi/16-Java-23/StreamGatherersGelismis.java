// =============================================================================
//  Java 23 - Stream Gatherers (Gelismis Ornekler)  -  JEP 473 (2. Preview)
// =============================================================================
//
//  Stream Gatherers, Stream API'sine OZEL ARA (intermediate) operasyonlar
//  eklemenizi saglar. Collectors'in terminal operasyonlar icin yaptigini,
//  Gatherer ara operasyonlar icin yapar.
//
//  Bu dosya PREVIEW ozelligi kullanir. Derlemek ve calistirmak icin:
//
//    javac --release 23 --enable-preview StreamGatherersGelismis.java
//    java  --enable-preview StreamGatherersGelismis
//
//  (Tek dosya modunda: java --release 23 --enable-preview StreamGatherersGelismis.java)
//
//  Yerlesik (built-in) gatherer fabrikalari (java.util.stream.Gatherers):
//    - windowFixed(n)     : ardisik, ortusmeyen n'lik pencereler
//    - windowSliding(n)   : 1 kayan, ortusen n'lik pencereler
//    - fold(...)          : tek bir sonuca katlar (terminal benzeri)
//    - scan(...)          : her adimda biriken (running) sonucu yayar
//    - mapConcurrent(...) : sinirli paralellikle map
//
//  Ayrica Gatherer arayuzunu uygulayarak KENDI gatherer'inizi yazabilirsiniz.
// =============================================================================

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class StreamGatherersGelismis {

    // -------------------------------------------------------------------------
    //  main: tum ornekleri sirayla calistirir
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("=== Java 23 Stream Gatherers - Gelismis Ornekler ===\n");

        ornek1_hareketliOrtalama();
        ornek2_calisanToplam_scan();
        ornek3_sabitPencere_batchleme();
        ornek4_ozelDedup();
        ornek5_ardisikGruplama();
        ornek6_limitliAlma();

        System.out.println("\n=== Tum ornekler tamamlandi ===");
    }

    // =========================================================================
    //  ORNEK 1: HAREKETLI ORTALAMA (Moving Average)  -  windowSliding
    // -------------------------------------------------------------------------
    //  Senaryo: Bir hissenin gunluk kapanis fiyatlari var. 3 gunluk hareketli
    //  ortalamayi (moving average) hesaplamak istiyoruz. Bu, zaman serisi
    //  analizinde cok yaygindir (trendi yumusatmak icin).
    //
    //  windowSliding(3): [g1,g2,g3], [g2,g3,g4], [g3,g4,g5] ... pencereleri verir.
    // =========================================================================
    static void ornek1_hareketliOrtalama() {
        System.out.println("--- Ornek 1: 3 Gunluk Hareketli Ortalama (windowSliding) ---");

        List<Double> fiyatlar = List.of(100.0, 102.0, 101.0, 105.0, 110.0, 108.0, 112.0);

        List<Double> hareketliOrtalama = fiyatlar.stream()
                // 3'luk kayan pencereler olustur: her pencere bir List<Double>
                .gather(Gatherers.windowSliding(3))
                // her pencerenin ortalamasini al
                .map(pencere -> pencere.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0))
                .toList();

        System.out.println("Fiyatlar          : " + fiyatlar);
        System.out.println("3 gunluk ort.     : " + hareketliOrtalama);
        System.out.println();
    }

    // =========================================================================
    //  ORNEK 2: CALISAN TOPLAM (Running Total)  -  scan
    // -------------------------------------------------------------------------
    //  Senaryo: Bir banka hesabinin gunluk islem tutarlari var. Her islemden
    //  SONRAKI bakiyeyi (kumulatif toplam) gormek istiyoruz.
    //
    //  scan(baslangic, biriktirici): her elemanda biriken sonucu YAYAR.
    //  fold'dan farki: fold tek bir nihai sonuc verir, scan ara sonuclari da verir.
    // =========================================================================
    static void ornek2_calisanToplam_scan() {
        System.out.println("--- Ornek 2: Calisan Toplam / Kumulatif Bakiye (scan) ---");

        List<Integer> islemler = List.of(100, -30, 50, -20, 200); // + yatirma, - cekme

        List<Integer> bakiyeler = islemler.stream()
                .gather(Gatherers.scan(
                        () -> 0,                       // baslangic bakiyesi
                        (bakiye, islem) -> bakiye + islem)) // her adimda biriktir
                .toList();

        System.out.println("Islemler          : " + islemler);
        System.out.println("Bakiye gecmisi    : " + bakiyeler); // [100, 70, 120, 100, 300]
        System.out.println();
    }

    // =========================================================================
    //  ORNEK 3: SABIT PENCERE / BATCH'LEME  -  windowFixed
    // -------------------------------------------------------------------------
    //  Senaryo: 1000'lerce kaydi toplu (batch) olarak isleyecegiz. Veritabanina
    //  ya da bir API'ye tek tek degil, 4'luk gruplar halinde gondermek istiyoruz
    //  (ag turunu / commit sayisini azaltmak icin).
    //
    //  windowFixed(4): [1,2,3,4], [5,6,7,8], [9,10] (son grup kismi olabilir).
    // =========================================================================
    static void ornek3_sabitPencere_batchleme() {
        System.out.println("--- Ornek 3: Toplu (Batch) Isleme (windowFixed) ---");

        List<Integer> kayitlar = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<List<Integer>> gruplar = kayitlar.stream()
                .gather(Gatherers.windowFixed(4))
                .toList();

        System.out.println("Tum kayitlar      : " + kayitlar);
        int no = 1;
        for (List<Integer> grup : gruplar) {
            System.out.println("  Batch " + (no++) + " gonderiliyor: " + grup);
        }
        System.out.println();
    }

    // =========================================================================
    //  ORNEK 4: OZEL GATHERER - SIRAYI KORUYAN TEKILLESTIRME (distinct)
    // -------------------------------------------------------------------------
    //  Senaryo: Bir log akisindan tekrar eden kullanici ID'lerini ayikliyoruz
    //  ama ilk gorulme sirasini korumak istiyoruz.
    //
    //  Burada KENDI gatherer'imizi yaziyoruz. Durumlu (stateful) bir gatherer:
    //  daha once gorulen elemanlari bir Set'te tutar, yeni olani downstream'e iletir.
    //
    //  Gatherer.ofSequential(initializer, integrator):
    //    - initializer : durum nesnesini (Set) olusturur
    //    - integrator  : her eleman icin calisir; downstream.push(...) ile yayar
    // =========================================================================
    static void ornek4_ozelDedup() {
        System.out.println("--- Ornek 4: Ozel Gatherer ile Sira Koruyan Dedup ---");

        List<String> kullaniciAkisi = List.of(
                "ali", "veli", "ali", "ayse", "veli", "ali", "fatma");

        List<String> tekil = kullaniciAkisi.stream()
                .gather(siraKoruyanTekil())
                .toList();

        System.out.println("Ham akis          : " + kullaniciAkisi);
        System.out.println("Tekil (sirali)    : " + tekil); // [ali, veli, ayse, fatma]
        System.out.println();
    }

    /**
     * Ilk gorulme sirasini koruyarak tekrar edenleri eleyen ozel gatherer.
     * (Standart .distinct() ile ayni sonucu verir ama gatherer yazimini gosterir.)
     */
    static <T> Gatherer<T, ?, T> siraKoruyanTekil() {
        return Gatherer.ofSequential(
                // 1) Durum (state): daha once gorulen elemanlar
                LinkedHashSet::new,
                // 2) Integrator: her eleman icin calisir
                (Set<T> gorulenler, T eleman, Gatherer.Downstream<? super T> downstream) -> {
                    if (gorulenler.add(eleman)) {     // ilk kez goruluyorsa true doner
                        return downstream.push(eleman); // asagiya ilet
                    }
                    return true; // tekrar eden -> iletme, ama akisa devam et
                });
    }

    // =========================================================================
    //  ORNEK 5: OZEL GATHERER - ARDISIK ESIT ELEMANLARI GRUPLA (run-length)
    // -------------------------------------------------------------------------
    //  Senaryo: Bir sensorden gelen durum akisinda, ARDISIK ayni degerleri
    //  tek bir grupta toplamak istiyoruz (run-length encoding mantigi).
    //  Ornek: [A,A,B,B,B,A,C] -> [[A,A],[B,B,B],[A],[C]]
    //
    //  Bu gatherer durumludur (acik grubu tutar) ve AKIS SONUNDA kalan grubu
    //  yaymak icin bir "finisher" kullanir.
    // =========================================================================
    static void ornek5_ardisikGruplama() {
        System.out.println("--- Ornek 5: Ozel Gatherer ile Ardisik Gruplama ---");

        List<String> durumlar = List.of("A", "A", "B", "B", "B", "A", "C", "C");

        List<List<String>> gruplar = durumlar.stream()
                .gather(ardisikGrupla())
                .toList();

        System.out.println("Durum akisi       : " + durumlar);
        System.out.println("Ardisik gruplar   : " + gruplar);
        System.out.println();
    }

    /**
     * Ardisik (consecutive) esit elemanlari ayni gruba toplar.
     * Durum: o anki acik grup (List). Eleman onceki ile esitse gruba ekler,
     * degilse onceki grubu yayar ve yeni grup baslatir. Sonda kalan grup
     * finisher ile yayilir.
     */
    static <T> Gatherer<T, ?, List<T>> ardisikGrupla() {
        // Durumu tutmak icin tek elemanli bir tasiyici dizi kullaniyoruz
        // (lambda icinde mutable referans icin pratik bir yontem).
        return Gatherer.<T, List<List<T>>, List<T>>ofSequential(
                // initializer: tek elemanli liste; index 0 = "o anki acik grup"
                () -> {
                    List<List<T>> kutu = new ArrayList<>();
                    kutu.add(new ArrayList<>()); // acik grup
                    return kutu;
                },
                // integrator
                (kutu, eleman, downstream) -> {
                    List<T> acikGrup = kutu.get(0);
                    if (acikGrup.isEmpty() || acikGrup.get(0).equals(eleman)) {
                        // ilk eleman ya da onceki ile ayni -> mevcut gruba ekle
                        acikGrup.add(eleman);
                    } else {
                        // farkli -> mevcut grubu yay, yeni grup baslat
                        boolean devam = downstream.push(List.copyOf(acikGrup));
                        List<T> yeniGrup = new ArrayList<>();
                        yeniGrup.add(eleman);
                        kutu.set(0, yeniGrup);
                        if (!devam) return false;
                    }
                    return true;
                },
                // finisher: akis bitince son acik grubu yay
                (kutu, downstream) -> {
                    List<T> acikGrup = kutu.get(0);
                    if (!acikGrup.isEmpty()) {
                        downstream.push(List.copyOf(acikGrup));
                    }
                });
    }

    // =========================================================================
    //  ORNEK 6: OZEL GATHERER - KOSULA GORE ERKEN KESME (short-circuit)
    // -------------------------------------------------------------------------
    //  Senaryo: Bir sensorden gelen sonsuz/uzun bir akista, toplam belirli bir
    //  esigi gecene KADAR elemanlari al, esik asilinca akisi DURDUR.
    //
    //  Gatherer integrator'unde downstream.push(...) FALSE dondurursek ya da
    //  biz false dondururek akisi kisa devre (short-circuit) yaptirabiliriz.
    //  Bu, sonsuz Stream'lerle bile guvenli calisir.
    // =========================================================================
    static void ornek6_limitliAlma() {
        System.out.println("--- Ornek 6: Ozel Gatherer ile Toplam Esigine Kadar Al ---");

        // Sonsuz bir akis: 1, 2, 3, 4, ... (sadece esik mantigini test icin)
        List<Integer> alinanlar = Stream.iterate(1, n -> n + 1)
                .gather(toplamEsigineKadar(15)) // toplam 15'i gecince dur
                .toList();

        System.out.println("Toplam <= 15 olana kadar alinanlar: " + alinanlar);
        // 1+2+3+4+5 = 15 -> 5'e kadar alir, 6 eklenince 21>15 olur, durur.
        System.out.println();
    }

    /**
     * Elemanlari toplaya toplaya esige kadar alir; esik asildiginda akisi keser.
     * Durum: bir int dizisi [toplam] (mutable sayac).
     */
    static Gatherer<Integer, ?, Integer> toplamEsigineKadar(int esik) {
        return Gatherer.ofSequential(
                () -> new int[]{0}, // durum: toplam
                (toplam, eleman, downstream) -> {
                    if (toplam[0] + eleman > esik) {
                        return false; // esik asildi -> akisi durdur (short-circuit)
                    }
                    toplam[0] += eleman;
                    return downstream.push(eleman);
                });
    }
}
