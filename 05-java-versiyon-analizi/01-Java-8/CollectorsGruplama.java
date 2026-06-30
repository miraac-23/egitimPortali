import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  COLLECTORS ve GRUPLAMA (groupingBy / partitioningBy / joining) - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Collectors, Stream'in collect() terminal işleminde kullanılan HAZIR
 *   "toplayıcı reçeteleridir". Bir akışın elemanlarını listeye, kümeye,
 *   haritaya (Map) toplamayı; gruplamayı; saymayı; ortalama almayı; metne
 *   birleştirmeyi sağlar.
 *
 * NEDEN GELDİ?
 *   Eskiden "ürünleri kategoriye göre grupla" gibi bir iş için elle Map
 *   oluşturup, her eleman için "anahtar var mı, yoksa yeni liste aç" mantığını
 *   tekrar tekrar yazmak gerekiyordu. Çok tekrar eden, hataya açık koddu.
 *   Collectors bunları tek satırlık deklaratif ifadelere indirger.
 *
 * NE İŞE YARAR / NEREDE: Raporlama, istatistik, kategorize etme, özetleme.
 *   Örn: departman bazlı maaş ortalaması, kategori bazlı stok, müşteri
 *   segmentasyonu.
 */
public class CollectorsGruplama {

    public static void main(String[] args) {

        List<Calisan> calisanlar = Arrays.asList(
                new Calisan("Ahmet", "Yazilim", 45000, 28),
                new Calisan("Zeynep", "Yazilim", 62000, 35),
                new Calisan("Mehmet", "Pazarlama", 38000, 41),
                new Calisan("Ayse", "Pazarlama", 52000, 30),
                new Calisan("Burak", "Finans", 71000, 45),
                new Calisan("Elif", "Yazilim", 48000, 26));

        System.out.println("=== 1. toList / toSet / toMap ===\n");
        List<String> isimler = calisanlar.stream()
                .map(c -> c.isim)
                .collect(Collectors.toList());
        System.out.println("Isimler (List): " + isimler);

        // toMap: isim -> maas haritası
        Map<String, Double> isimMaas = calisanlar.stream()
                .collect(Collectors.toMap(c -> c.isim, c -> c.maas));
        System.out.println("Isim->Maas (Map): " + isimMaas);

        System.out.println("\n=== 2. groupingBy : Anahtara göre gruplama ===\n");
        // ESKİ YÖNTEM elle Map yönetmek gerekirdi; YENİ tek satır:
        Map<String, List<Calisan>> departmana = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman));
        departmana.forEach((dep, liste) -> {
            System.out.println(dep + ":");
            liste.forEach(c -> System.out.println("   " + c.isim));
        });

        System.out.println("\n=== 3. groupingBy + counting : Grup sayıları ===\n");
        Map<String, Long> departmanSayilari = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman, Collectors.counting()));
        System.out.println("Departman bazli calisan sayisi: " + departmanSayilari);

        System.out.println("\n=== 4. groupingBy + averagingDouble : Grup ortalaması ===\n");
        Map<String, Double> departmanMaasOrt = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman,
                        Collectors.averagingDouble(c -> c.maas)));
        departmanMaasOrt.forEach((dep, ort) ->
                System.out.printf("%-12s ortalama maas: %.2f TL%n", dep, ort));

        System.out.println("\n=== 5. groupingBy + summingDouble / mapping ===\n");
        // Departman bazlı toplam maaş
        Map<String, Double> toplamMaas = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman,
                        Collectors.summingDouble(c -> c.maas)));
        System.out.println("Departman toplam maas: " + toplamMaas);

        // groupingBy + mapping: departmana göre SADECE isimleri topla
        Map<String, List<String>> departmanIsimleri = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman,
                        Collectors.mapping(c -> c.isim, Collectors.toList())));
        System.out.println("Departman isimleri: " + departmanIsimleri);

        System.out.println("\n=== 6. partitioningBy : İkiye bölme (true/false) ===\n");
        // Maaşı 50000'den yüksek olanlar / olmayanlar
        Map<Boolean, List<Calisan>> bolme = calisanlar.stream()
                .collect(Collectors.partitioningBy(c -> c.maas >= 50000));
        System.out.println("Yuksek maaslilar (>=50000):");
        bolme.get(true).forEach(c -> System.out.println("   " + c.isim + " - " + c.maas));
        System.out.println("Dusuk maaslilar (<50000):");
        bolme.get(false).forEach(c -> System.out.println("   " + c.isim + " - " + c.maas));

        System.out.println("\n=== 7. joining : Metinleri birleştirme ===\n");
        String tumIsimler = calisanlar.stream()
                .map(c -> c.isim)
                .collect(Collectors.joining(", ", "[", "]")); // ayirici, onek, sonek
        System.out.println("Birlestirilmis: " + tumIsimler);

        System.out.println("\n=== 8. summarizingDouble : Tek seferde istatistik ===\n");
        DoubleSummaryStatistics maasIstatistik = calisanlar.stream()
                .collect(Collectors.summarizingDouble(c -> c.maas));
        System.out.println("Calisan sayisi : " + maasIstatistik.getCount());
        System.out.println("Toplam maas    : " + maasIstatistik.getSum());
        System.out.println("Ortalama       : " + maasIstatistik.getAverage());
        System.out.println("En yuksek      : " + maasIstatistik.getMax());
        System.out.println("En dusuk       : " + maasIstatistik.getMin());

        System.out.println("\n=== 9. GERÇEK HAYAT: Çok seviyeli gruplama ===\n");
        // Departman -> (40 yaş üstü mü?) -> isimler
        Map<String, Map<Boolean, List<String>>> cokSeviyeli = calisanlar.stream()
                .collect(Collectors.groupingBy(
                        c -> c.departman,
                        Collectors.groupingBy(
                                c -> c.yas >= 40,
                                Collectors.mapping(c -> c.isim, Collectors.toList()))));
        cokSeviyeli.forEach((dep, yasMap) -> {
            System.out.println(dep + ":");
            System.out.println("   40+ : " + yasMap.getOrDefault(true, Arrays.asList()));
            System.out.println("   <40 : " + yasMap.getOrDefault(false, Arrays.asList()));
        });

        System.out.println("\n=== 10. Sıralı grup (TreeMap supplier) ===\n");
        // groupingBy ile harita tipini TreeMap olarak belirleyip alfabetik sirala
        Map<String, Long> sirali = calisanlar.stream()
                .collect(Collectors.groupingBy(c -> c.departman,
                        TreeMap::new, Collectors.counting()));
        System.out.println("Alfabetik sirali departmanlar: " + sirali);
    }

    static class Calisan {
        String isim, departman;
        double maas;
        int yas;

        Calisan(String isim, String departman, double maas, int yas) {
            this.isim = isim;
            this.departman = departman;
            this.maas = maas;
            this.yas = yas;
        }
    }
}
