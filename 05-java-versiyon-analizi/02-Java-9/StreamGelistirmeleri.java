/*
 * =============================================================================
 *  JAVA 9 - STREAM API GELİŞTİRMELERİ
 *  takeWhile / dropWhile / Stream.ofNullable / Stream.iterate(seed,hasNext,next)
 * =============================================================================
 *
 *  Derleme/Çalıştırma:
 *      javac --release 9 StreamGelistirmeleri.java
 *      java StreamGelistirmeleri
 * =============================================================================
 */

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamGelistirmeleri {

    public static void main(String[] args) {

        // =====================================================================
        // 1) takeWhile : koşul DOĞRU olduğu sürece baştan al, ilk YANLIŞta dur.
        // =====================================================================
        System.out.println("--- 1) takeWhile ---");
        // SIRALI bir liste düşünelim (örn. artan ölçüm değerleri).
        List<Integer> olcumler = List.of(2, 4, 6, 8, 5, 10, 1);

        // ESKİ YANLIŞ DENEME: filter tüm elemanlara uygulanır, "ilk eşikte dur" demez.
        List<Integer> eskiFilter = olcumler.stream()
                .filter(x -> x < 8)
                .collect(Collectors.toList());
        System.out.println("Eski filter(x<8) : " + eskiFilter
                + "  <- 5 ve 1 de geldi (istemiyorduk!)");

        // YENİ takeWhile: ilk 8 görünce DURUR.
        List<Integer> yeniTake = olcumler.stream()
                .takeWhile(x -> x < 8)
                .collect(Collectors.toList());
        System.out.println("Yeni takeWhile(x<8): " + yeniTake + "  <- 8'de durdu");

        // =====================================================================
        // 2) dropWhile : koşul DOĞRU olduğu sürece baştan AT, ilk YANLIŞtan
        //                itibaren KALANI al.
        // =====================================================================
        System.out.println("\n--- 2) dropWhile ---");
        List<Integer> yeniDrop = olcumler.stream()
                .dropWhile(x -> x < 8)
                .collect(Collectors.toList());
        System.out.println("dropWhile(x<8) : " + yeniDrop + "  <- ilk 8'den itibaren kalanlar");

        // GERÇEK HAYAT: bir metin dosyasının başlık satırlarını atlayıp gövdeye geçmek.
        List<String> dosya = List.of(
                "# baslik",
                "# yazar: ahmet",
                "# tarih: 2017",
                "veri-satiri-1",
                "veri-satiri-2",
                "# bu yorum govdede"  // dropWhile bunu ATMAZ; ilk yanlistan sonrasi alinir
        );
        List<String> govde = dosya.stream()
                .dropWhile(s -> s.startsWith("#"))
                .collect(Collectors.toList());
        System.out.println("Basliklar atlandi: " + govde);

        // =====================================================================
        // 3) Stream.ofNullable : null ise BOŞ stream, değilse tek elemanlı stream.
        // =====================================================================
        System.out.println("\n--- 3) Stream.ofNullable ---");
        String varOlan = "merhaba";
        String yokOlan = null;

        // ESKİ: null kontrolünü elle yapmak gerekirdi.
        long eskiSayim = (yokOlan == null) ? 0 : 1;
        System.out.println("Eski elle null kontrolu: " + eskiSayim);

        // YENİ:
        System.out.println("ofNullable(varOlan).count() = "
                + Stream.ofNullable(varOlan).count());   // 1
        System.out.println("ofNullable(null).count()    = "
                + Stream.ofNullable(yokOlan).count());    // 0

        // GERÇEK HAYAT: bir map'ten gelebilecek null'ı stream akışında temiz işlemek.
        List<String> sehirler = Arrays.asList("Istanbul", null, "Ankara", null, "Izmir");
        List<String> temiz = sehirler.stream()
                .flatMap(Stream::ofNullable)   // null'lar otomatik elenir
                .collect(Collectors.toList());
        System.out.println("null'lar elendi: " + temiz);

        // =====================================================================
        // 4) Stream.iterate(seed, hasNext, next) : SONLU iterate (for döngüsü gibi).
        // =====================================================================
        System.out.println("\n--- 4) Stream.iterate (3 argumanli, sonlu) ---");

        // ESKİ (Java 8): iterate SONSUZdu, limit ile kesmek gerekirdi.
        List<Integer> eskiIterate = Stream.iterate(1, n -> n * 2)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("Eski iterate+limit: " + eskiIterate);

        // YENİ (Java 9): koşul predicate'i ile, tıpkı 'for' gibi.
        // for(int i=1; i<=100; i*=2) gibi:
        List<Integer> yeniIterate = Stream.iterate(1, n -> n <= 100, n -> n * 2)
                .collect(Collectors.toList());
        System.out.println("Yeni iterate(seed,hasNext,next): " + yeniIterate);

        // GERÇEK HAYAT: sayfalama. 1. sayfadan başla, boş gelene kadar (burada <=50) ilerle.
        System.out.println("\n--- Gercek hayat: sayfalama simulasyonu ---");
        Stream.iterate(1, sayfa -> sayfa <= 50, sayfa -> sayfa + 10)
                .forEach(sayfa -> System.out.println("  Sayfa offset getiriliyor: " + sayfa));
    }
}
