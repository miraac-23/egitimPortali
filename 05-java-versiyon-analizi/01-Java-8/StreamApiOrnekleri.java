import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * ============================================================================
 *  STREAM API - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Koleksiyonlar (List, Set vb.) üzerinde DEKLARATİF (ne istediğini söyle,
 *   nasıl yapılacağını değil) tarzda işlem yapmayı sağlayan bir API'dir.
 *   Bir "veri akışı" boru hattı kurarsın: kaynak -> ara işlemler -> sonlandırıcı.
 *
 *   Yapısı:
 *     kaynak.stream()
 *           .filter(...)   // ara işlem (lazy)
 *           .map(...)      // ara işlem (lazy)
 *           .collect(...)  // sonlandırıcı işlem (terminal) -> akışı tetikler
 *
 * NEDEN GELDİ? (Hangi problemi çözdü?)
 *   Eskiden koleksiyon işlemek için elle for döngüleri yazıyorduk. Filtrele +
 *   dönüştür + topla gibi işlemler iç içe döngüler ve geçici listelerle dolu,
 *   hata yapmaya açık, okunması zor kodlar üretiyordu. Stream API bu işlemleri
 *   tek bir akıcı (fluent) zincire indirger.
 *
 * ÖNEMLİ KAVRAMLAR:
 *   - LAZY (tembel): Ara işlemler, terminal işlem çağrılana kadar çalışmaz.
 *   - Stream tek kullanımlıktır: Tüketildikten sonra tekrar kullanılamaz.
 *   - PARALEL stream: .parallelStream() ile işlemler çoklu çekirdekte dağıtılır.
 *
 * NE İŞE YARAR / NEREDE KOLAYLIK: Filtreleme, dönüştürme, gruplama, toplama,
 *   istatistik çıkarma, raporlama -> e-ticaret, banka, log analizi vb.
 */
public class StreamApiOrnekleri {

    public static void main(String[] args) {

        List<Urun> urunler = Arrays.asList(
                new Urun("Telefon", "Elektronik", 15000, 12),
                new Urun("Laptop", "Elektronik", 32000, 5),
                new Urun("Kulaklik", "Elektronik", 800, 40),
                new Urun("Kitap", "Yayin", 120, 200),
                new Urun("Defter", "Yayin", 35, 500),
                new Urun("Kahve", "Gida", 250, 80));

        System.out.println("=== 1. ESKİ YÖNTEM vs filter+map+collect ===\n");

        // ESKİ YÖNTEM (Java 7): elle döngü + geçici liste
        List<String> pahaliElektronikEski = new ArrayList<>();
        for (Urun u : urunler) {
            if (u.kategori.equals("Elektronik") && u.fiyat > 1000) {
                pahaliElektronikEski.add(u.ad.toUpperCase());
            }
        }
        System.out.println("Eski yontem : " + pahaliElektronikEski);

        // YENİ YÖNTEM (Java 8 Stream): okunabilir, deklaratif
        List<String> pahaliElektronikYeni = urunler.stream()
                .filter(u -> u.kategori.equals("Elektronik"))
                .filter(u -> u.fiyat > 1000)
                .map(u -> u.ad.toUpperCase())
                .collect(Collectors.toList());
        System.out.println("Stream API  : " + pahaliElektronikYeni);

        System.out.println("\n=== 2. map : Dönüştürme ===\n");
        List<Double> kdvliFiyatlar = urunler.stream()
                .map(u -> u.fiyat * 1.20)
                .collect(Collectors.toList());
        System.out.println("KDV'li fiyatlar: " + kdvliFiyatlar);

        System.out.println("\n=== 3. reduce : İndirgeme (tek değere düşürme) ===\n");
        // Tüm stoğun toplam değeri (fiyat * adet)
        double toplamStokDegeri = urunler.stream()
                .map(u -> u.fiyat * u.stokAdedi)
                .reduce(0.0, Double::sum);   // başlangıç 0, ardışık topla
        System.out.println("Toplam stok degeri: " + toplamStokDegeri + " TL");

        // reduce ile en pahalı ürünü bulma
        Optional<Urun> enPahali = urunler.stream()
                .reduce((u1, u2) -> u1.fiyat > u2.fiyat ? u1 : u2);
        enPahali.ifPresent(u -> System.out.println("En pahali urun: " + u.ad));

        System.out.println("\n=== 4. flatMap : İç içe yapıları düzleştirme ===\n");
        // Her siparişin birden fazla ürünü var; tüm ürünleri tek listede topla
        List<Siparis> siparisler = Arrays.asList(
                new Siparis("S1", Arrays.asList("Telefon", "Kulaklik")),
                new Siparis("S2", Arrays.asList("Laptop")),
                new Siparis("S3", Arrays.asList("Kitap", "Defter", "Kahve")));

        List<String> tumUrunler = siparisler.stream()
                .flatMap(s -> s.urunler.stream())   // List<List<String>> -> Stream<String>
                .collect(Collectors.toList());
        System.out.println("Tum siparis kalemleri: " + tumUrunler);

        // Benzersiz ürünler
        List<String> benzersiz = siparisler.stream()
                .flatMap(s -> s.urunler.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Benzersiz (sirali)   : " + benzersiz);

        System.out.println("\n=== 5. Sayısal Stream'ler ve İstatistik ===\n");
        IntSummaryStatistics istatistik = urunler.stream()
                .mapToInt(u -> u.stokAdedi)
                .summaryStatistics();
        System.out.println("Toplam stok adedi : " + istatistik.getSum());
        System.out.println("Ortalama stok     : " + istatistik.getAverage());
        System.out.println("En cok stok       : " + istatistik.getMax());
        System.out.println("En az stok        : " + istatistik.getMin());

        System.out.println("\n=== 6. limit / skip / sorted / count ===\n");
        // En pahalı 3 ürün
        System.out.println("En pahali 3 urun:");
        urunler.stream()
                .sorted((a, b) -> Double.compare(b.fiyat, a.fiyat))
                .limit(3)
                .forEach(u -> System.out.println("   " + u.ad + " - " + u.fiyat));

        long elektronikSayisi = urunler.stream()
                .filter(u -> u.kategori.equals("Elektronik"))
                .count();
        System.out.println("Elektronik urun sayisi: " + elektronikSayisi);

        System.out.println("\n=== 7. anyMatch / allMatch / noneMatch ===\n");
        boolean herseyStokta = urunler.stream().allMatch(u -> u.stokAdedi > 0);
        boolean pahaliVar = urunler.stream().anyMatch(u -> u.fiyat > 30000);
        boolean bedavaYok = urunler.stream().noneMatch(u -> u.fiyat == 0);
        System.out.println("Hepsi stokta mi?  " + herseyStokta);
        System.out.println("30000 ustu var mi? " + pahaliVar);
        System.out.println("Bedava urun yok mu? " + bedavaYok);

        System.out.println("\n=== 8. Stream.iterate / generate / IntStream.range ===\n");
        // İlk 5 çift sayı
        List<Integer> ciftler = Stream.iterate(0, n -> n + 2)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("Ilk 5 cift sayi: " + ciftler);

        // IntStream.range ile toplam
        int kareToplam = IntStream.rangeClosed(1, 5)
                .map(n -> n * n)
                .sum();
        System.out.println("1..5 karelerinin toplami: " + kareToplam);

        System.out.println("\n=== 9. PARALEL STREAM (çoklu çekirdek) ===\n");
        // Büyük veri için işlem çekirdeklere dağıtılır. DİKKAT: küçük veride
        // ek yük (overhead) nedeniyle daha yavaş olabilir.
        long buyukToplam = IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .mapToLong(i -> (long) i)
                .sum();
        System.out.println("1..1.000.000 paralel toplam: " + buyukToplam);
        System.out.println("Kullanilan cekirdek sayisi : "
                + Runtime.getRuntime().availableProcessors());
    }

    static class Urun {
        String ad, kategori;
        double fiyat;
        int stokAdedi;

        Urun(String ad, String kategori, double fiyat, int stokAdedi) {
            this.ad = ad;
            this.kategori = kategori;
            this.fiyat = fiyat;
            this.stokAdedi = stokAdedi;
        }

        @Override
        public String toString() {
            return ad + " [" + kategori + "] " + fiyat + " TL x" + stokAdedi;
        }
    }

    static class Siparis {
        String no;
        List<String> urunler;

        Siparis(String no, List<String> urunler) {
            this.no = no;
            this.urunler = urunler;
        }
    }
}
