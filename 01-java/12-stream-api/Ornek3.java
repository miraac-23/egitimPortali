// Ornek3: Collectors — groupingBy, joining ve gruplu toplamlar.
// Çalıştırma: java Ornek3.java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ornek3 {

    record Urun(String ad, String kategori, double fiyat) {}

    public static void main(String[] args) {
        List<Urun> urunler = List.of(
                new Urun("Klavye", "Aksesuar", 450),
                new Urun("Mouse", "Aksesuar", 250),
                new Urun("Monitör", "Ekran", 3200),
                new Urun("Laptop", "Bilgisayar", 28000),
                new Urun("Kulaklık", "Aksesuar", 900),
                new Urun("TV", "Ekran", 12000)
        );

        // joining: metinleri tek bir String'de birleştir.
        String adListesi = urunler.stream()
                .map(Urun::ad)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Ürünler: " + adListesi);

        // groupingBy: kategoriye göre grupla -> Map<kategori, List<Urun>>
        Map<String, List<Urun>> kategoriyeGore = urunler.stream()
                .collect(Collectors.groupingBy(Urun::kategori));
        System.out.println("\nKategori -> ürün sayısı:");
        kategoriyeGore.forEach((k, v) -> System.out.println("  " + k + " : " + v.size()));

        // groupingBy + summingDouble: kategori bazında toplam fiyat
        Map<String, Double> kategoriToplam = urunler.stream()
                .collect(Collectors.groupingBy(Urun::kategori, Collectors.summingDouble(Urun::fiyat)));
        System.out.println("\nKategori bazında toplam fiyat:");
        kategoriToplam.forEach((k, v) -> System.out.printf("  %-12s : %,.2f TL%n", k, v));

        // partitioningBy: bir koşula göre ikiye ayır (true/false)
        Map<Boolean, List<Urun>> pahaliMi = urunler.stream()
                .collect(Collectors.partitioningBy(u -> u.fiyat() >= 1000));
        System.out.println("\nPahalı (>=1000) ürün sayısı: " + pahaliMi.get(true).size());
        System.out.println("Uygun (<1000) ürün sayısı  : " + pahaliMi.get(false).size());
    }
}
