// =============================================================================
//  TeeingCollector.java
//  Java 12 - Teeing Collector (Collectors.teeing) - JDK-8209685
// =============================================================================
//
//  NEDIR?
//  ----------------------------------------------------------------------------
//  Collectors.teeing(downstream1, downstream2, merger):
//    Bir stream'in elemanlarini AYNI ANDA iki ayri collector'a (downstream)
//    gonderir; her iki collector da kendi sonucunu uretir; en sonda
//    'merger' (birlestirici BiFunction) bu iki sonucu TEK bir sonuca birlestirir.
//
//  Adi "tee" (T borusu) tesisatcilikteki T-baglantisindan gelir: tek bir akis
//  ikiye ayrilir, iki farkli isleme tabi tutulur, sonra tekrar birlestirilir.
//
//  NEDEN GELDI / HANGI PROBLEMI COZER?
//  ----------------------------------------------------------------------------
//  Java 12 oncesinde bir koleksiyondan IKI farkli ozet (orn. hem ortalama
//  hem toplam, hem min hem max) hesaplamak istediginizde ya:
//    (a) stream'i IKI KEZ dolasmaniz (iki ayri stream() cagrisi),
//    (b) ya da ozel/karmasik bir collector yazmaniz gerekiyordu.
//  teeing ile TEK GECISTE iki sonucu birlikte hesaplayip birlestirebiliriz.
//
//  DERLEME / CALISTIRMA:
//    Collectors.teeing Java 12'de KALICI (standard) olarak geldi (preview degil).
//        javac TeeingCollector.java
//        java  TeeingCollector
// =============================================================================

import java.util.List;
import java.util.stream.Collectors;

public class TeeingCollector {

    // Basit bir "Urun" kaydi (ad + fiyat).
    record Urun(String ad, double fiyat) {}

    // teeing'in merger'inin urettigi sonucu tutacak kayit.
    record FiyatOzeti(double ortalama, long adet, double toplam) {}

    public static void main(String[] args) {

        System.out.println("================================================");
        System.out.println(" JAVA 12 - TEEING COLLECTOR ORNEKLERI");
        System.out.println("================================================\n");

        List<Urun> urunler = List.of(
                new Urun("Klavye",   450.0),
                new Urun("Mouse",    250.0),
                new Urun("Monitor", 3200.0),
                new Urun("Kulaklik", 800.0),
                new Urun("Webcam",   600.0)
        );

        // ---------------------------------------------------------------------
        // ESKI YONTEM: Stream'i IKI KEZ dolasarak ortalama ve toplam hesaplama
        // ---------------------------------------------------------------------
        // Dezavantaj: ayni veri seti uzerinde iki ayri stream gecisi yapilir.
        // Buyuk veri setlerinde bu, iki katlik is yuku demektir.
        System.out.println(">>> ESKI YONTEM (iki ayri stream gecisi):");
        double ortalamaEski = urunler.stream()
                .mapToDouble(Urun::fiyat)
                .average()
                .orElse(0.0);
        double toplamEski = urunler.stream()
                .mapToDouble(Urun::fiyat)
                .sum();
        System.out.printf("   Ortalama fiyat: %.2f TL%n", ortalamaEski);
        System.out.printf("   Toplam fiyat  : %.2f TL%n", toplamEski);
        System.out.println();

        // ---------------------------------------------------------------------
        // YENI YONTEM: Collectors.teeing ile TEK GECISTE iki sonuc + birlestirme
        // ---------------------------------------------------------------------
        // 1. downstream  -> ortalama (Collectors.averagingDouble)
        // 2. downstream  -> adet     (Collectors.counting)
        // merger         -> iki sonucu FiyatOzeti'ne birlestirir; ayrica
        //                   ortalama * adet ile toplami da hesaplayip ekleriz.
        System.out.println(">>> YENI YONTEM (Collectors.teeing - tek gecis):");
        FiyatOzeti ozet = urunler.stream().collect(
                Collectors.teeing(
                        Collectors.averagingDouble(Urun::fiyat),  // 1. collector
                        Collectors.counting(),                    // 2. collector
                        (ort, adet) -> new FiyatOzeti(ort, adet, ort * adet) // merger
                )
        );
        System.out.printf("   Ortalama fiyat: %.2f TL%n", ozet.ortalama());
        System.out.printf("   Urun adedi    : %d%n", ozet.adet());
        System.out.printf("   Toplam fiyat  : %.2f TL%n", ozet.toplam());
        System.out.println();

        // ---------------------------------------------------------------------
        // GERCEK HAYAT ORNEGI: Ogrenci notlari -> ortalama + gecen ogrenci sayisi
        // ---------------------------------------------------------------------
        // Tek geciste hem sinif ortalamasini hem de 50 ve uzeri alan
        // (gecen) ogrenci sayisini birlikte hesapliyoruz.
        System.out.println(">>> GERCEK HAYAT: Sinif notlari analizi:");
        List<Integer> notlar = List.of(45, 80, 55, 30, 90, 65, 48, 72, 100, 50);

        record SinifRaporu(double ortalama, long gecenSayisi, int toplamOgrenci) {}

        SinifRaporu rapor = notlar.stream().collect(
                Collectors.teeing(
                        // 1. downstream: tum notlarin ortalamasi
                        Collectors.averagingInt(Integer::intValue),
                        // 2. downstream: 50 ve uzeri alanlari say (gecenler)
                        Collectors.filtering(n -> n >= 50, Collectors.counting()),
                        // merger: iki sonucu birlestir, toplam ogrenciyi de ekle
                        (ort, gecen) -> new SinifRaporu(ort, gecen, notlar.size())
                )
        );
        System.out.printf("   Sinif ortalamasi : %.1f%n", rapor.ortalama());
        System.out.printf("   Gecen ogrenci    : %d / %d%n",
                rapor.gecenSayisi(), rapor.toplamOgrenci());
        System.out.printf("   Basari orani     : %%%.0f%n",
                100.0 * rapor.gecenSayisi() / rapor.toplamOgrenci());
        System.out.println();

        // ---------------------------------------------------------------------
        // EK ORNEK: teeing ile MIN ve MAX'i tek geciste bulup fark hesaplama
        // ---------------------------------------------------------------------
        System.out.println(">>> EK ORNEK: En ucuz / en pahali urun farki:");
        record MinMax(double min, double max, double fark) {}

        MinMax minMax = urunler.stream().collect(
                Collectors.teeing(
                        Collectors.minBy((a, b) -> Double.compare(a.fiyat(), b.fiyat())),
                        Collectors.maxBy((a, b) -> Double.compare(a.fiyat(), b.fiyat())),
                        (minOpt, maxOpt) -> {
                            double min = minOpt.map(Urun::fiyat).orElse(0.0);
                            double max = maxOpt.map(Urun::fiyat).orElse(0.0);
                            return new MinMax(min, max, max - min);
                        }
                )
        );
        System.out.printf("   En ucuz : %.2f TL%n", minMax.min());
        System.out.printf("   En pahali: %.2f TL%n", minMax.max());
        System.out.printf("   Fark    : %.2f TL%n", minMax.fark());

        System.out.println("\n================================================");
        System.out.println(" BITTI.");
        System.out.println("================================================");
    }
}
