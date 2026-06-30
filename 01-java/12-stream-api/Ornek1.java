// Ornek1: Stream temelleri — filter, map, sorted, collect.
// Çalıştırma: java Ornek1.java
import java.util.List;
import java.util.stream.Collectors;

public class Ornek1 {

    public static void main(String[] args) {
        List<String> isimler = List.of("ada", "burak", "ceren", "deniz", "emir", "ayse");

        // Bir stream pipeline'ı: kaynak -> ara işlemler -> sonlandırıcı.
        // 'a' ile başlayanları al, büyük harfe çevir, sıralı bir listeye topla.
        List<String> sonuc = isimler.stream()
                .filter(ad -> ad.startsWith("a"))   // ara işlem: filtrele
                .map(String::toUpperCase)           // ara işlem: dönüştür
                .sorted()                           // ara işlem: sırala
                .collect(Collectors.toList());      // sonlandırıcı: topla

        System.out.println("Kaynak  : " + isimler);
        System.out.println("Sonuç   : " + sonuc);

        // forEach: her elemanı tüket (yan etki).
        System.out.println("\nUzunluğu 5 olanlar:");
        isimler.stream()
                .filter(ad -> ad.length() == 5)
                .forEach(ad -> System.out.println("  - " + ad));

        // count, anyMatch, allMatch gibi sonlandırıcılar:
        long aIleBaslayan = isimler.stream().filter(a -> a.startsWith("a")).count();
        boolean hepsiKisa = isimler.stream().allMatch(a -> a.length() <= 6);
        System.out.println("\n'a' ile başlayan sayısı: " + aIleBaslayan);
        System.out.println("Hepsi <= 6 harf mi?    : " + hepsiKisa);

        // distinct + limit
        System.out.println("\nİlk 3 benzersiz uzunluk: " +
                isimler.stream().map(String::length).distinct().limit(3).collect(Collectors.toList()));
    }
}
