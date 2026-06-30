import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * ============================================================================
 *  LAMBDA İFADELERİ (LAMBDA EXPRESSIONS) - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Lambda ifadesi, isimsiz (anonim) bir fonksiyondur. Kısaca "bir metodu,
 *   bir nesneye sarmadan, doğrudan davranış olarak geçirme" yöntemidir.
 *
 *   Sözdizimi:  (parametreler) -> { gövde }
 *   Örnek:      (a, b) -> a + b
 *
 * NEDEN GELDİ? (Hangi problemi çözdü?)
 *   Java 8 öncesinde bir davranışı (kod parçasını) bir metoda geçirmek için
 *   ANONİM İÇ SINIF (anonymous inner class) yazmak zorundaydık. Bu çok
 *   gürültülü (boilerplate) koda yol açıyordu. 1 satırlık iş için 5-6 satır
 *   tören kodu yazmak gerekiyordu.
 *
 * NE İŞE YARAR / NEREDE KOLAYLIK SAĞLAR?
 *   - Koleksiyonları sıralama (Comparator)
 *   - Olay dinleyicileri (event listener)
 *   - Stream API ile birlikte filtreleme/dönüştürme
 *   - Çok kısa, okunabilir, fonksiyonel kod
 */
public class LambdaOrnekleri {

    public static void main(String[] args) {

        System.out.println("=== 1. ESKİ YÖNTEM vs YENİ YÖNTEM: Comparator ===\n");

        List<String> isimler = new ArrayList<>(
                Arrays.asList("Zeynep", "Ahmet", "Mehmet", "Ayse", "Burak"));

        // ESKİ YÖNTEM (Java 7 ve öncesi): Anonim iç sınıf
        // Sadece "iki ismi karşılaştır" demek için 5 satır boilerplate kod.
        Collections.sort(isimler, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        System.out.println("Eski yöntemle siralandi : " + isimler);

        // YENİ YÖNTEM (Java 8): Lambda ifadesi - tek satır
        Collections.sort(isimler, (a, b) -> b.compareTo(a)); // ters sıralama
        System.out.println("Lambda ile siralandi    : " + isimler);

        System.out.println("\n=== 2. Lambda Sözdizimi Çeşitleri ===\n");

        // Parametresiz lambda
        Runnable selam = () -> System.out.println("Merhaba Lambda dunyasi!");
        selam.run();

        // Tek parametreli (parantez opsiyonel)
        Function<Integer, Integer> kare = x -> x * x;
        System.out.println("5'in karesi: " + kare.apply(5));

        // Çok parametreli + çok satırlı gövde (blok)
        Hesaplama topla = (a, b) -> {
            int sonuc = a + b;
            System.out.println(a + " + " + b + " hesaplaniyor...");
            return sonuc;
        };
        System.out.println("Sonuc: " + topla.hesapla(7, 8));

        System.out.println("\n=== 3. GERÇEK HAYAT ÖRNEĞİ: Çalışan Listesi Sıralama ===\n");

        List<Calisan> calisanlar = Arrays.asList(
                new Calisan("Ahmet", "Yazilim", 45000),
                new Calisan("Zeynep", "Pazarlama", 52000),
                new Calisan("Mehmet", "Yazilim", 38000),
                new Calisan("Ayse", "Finans", 61000));

        // Maaşa göre artan sıralama (lambda + Comparator)
        calisanlar.sort((c1, c2) -> Double.compare(c1.maas, c2.maas));
        System.out.println("Maasa gore artan:");
        calisanlar.forEach(c -> System.out.println("   " + c));

        // Comparator.comparing ile daha da okunabilir (method reference önizleme)
        calisanlar.sort(Comparator.comparing((Calisan c) -> c.departman)
                .thenComparing(c -> c.isim));
        System.out.println("\nDepartman, sonra isme gore:");
        calisanlar.forEach(c -> System.out.println("   " + c));

        System.out.println("\n=== 4. forEach ile Lambda (Iterable.forEach) ===\n");
        // ESKİ YÖNTEM: for döngüsü
        for (Calisan c : calisanlar) {
            System.out.println("Eski for: " + c.isim);
        }
        // YENİ YÖNTEM: forEach + lambda
        calisanlar.forEach(c -> System.out.println("Lambda forEach: " + c.isim));

        System.out.println("\n=== 5. Effectively Final (Kapanış / Closure) ===\n");
        // Lambda, dışarıdaki yerel değişkenleri kullanabilir; ancak bu değişkenler
        // "effectively final" (etkin olarak değişmez) olmalıdır.
        String onek = ">> "; // değiştirilmiyor -> effectively final
        calisanlar.forEach(c -> System.out.println(onek + c.isim));
    }

    /** Lambda'nın hedefi olacak fonksiyonel arayüz (tek soyut metot). */
    @FunctionalInterface
    interface Hesaplama {
        int hesapla(int a, int b);
    }

    /** Gerçek hayat domain sınıfı: Çalışan. */
    static class Calisan {
        String isim;
        String departman;
        double maas;

        Calisan(String isim, String departman, double maas) {
            this.isim = isim;
            this.departman = departman;
            this.maas = maas;
        }

        @Override
        public String toString() {
            return isim + " (" + departman + ", " + maas + " TL)";
        }
    }
}
