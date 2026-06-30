import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  METHOD REFERENCES (Metot Referansları) - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Lambda ifadesinin daha da kısaltılmış halidir. Eğer bir lambda SADECE
 *   var olan bir metodu çağırıyorsa, lambda yerine doğrudan o metodun
 *   referansını verebiliriz. "::" operatörü kullanılır.
 *
 *   s -> System.out.println(s)   ==   System.out::println
 *
 * NEDEN GELDİ?
 *   Lambda zaten kısaydı ama "x -> Sinif.metot(x)" gibi yalnızca bir metoda
 *   delege eden lambdalar hâlâ gürültülüydü. Method reference bunu temizler;
 *   niyeti ("şu metodu kullan") açıkça gösterir, okunabilirliği artırır.
 *
 * DÖRT TÜRÜ VARDIR:
 *   1) Static metot referansı           ->  Sinif::statikMetot
 *   2) Belirli bir nesnenin metodu       ->  nesne::ornekMetot
 *   3) Rastgele bir nesnenin örnek metodu->  Sinif::ornekMetot
 *   4) Yapıcı (constructor) referansı    ->  Sinif::new
 */
public class MethodReferences {

    public static void main(String[] args) {

        List<String> kelimeler = Arrays.asList("elma", "Armut", "kiraz", "Banana");

        System.out.println("=== TÜR 1: Static Metot Referansı (Sinif::statikMetot) ===\n");
        // LAMBDA:  s -> Integer.parseInt(s)
        // REF   :  Integer::parseInt
        List<String> sayilar = Arrays.asList("10", "25", "3", "47");
        int toplam = sayilar.stream()
                .mapToInt(Integer::parseInt)   // static metot referansı
                .sum();
        System.out.println("Stringlerin toplami: " + toplam);

        // Kendi static metodumuza referans
        Function<String, String> selamla = MethodReferences::selamVer;
        System.out.println(selamla.apply("Ahmet"));

        System.out.println("\n=== TÜR 2: Belirli Nesnenin Metodu (nesne::metot) ===\n");
        // LAMBDA:  s -> System.out.println(s)
        // REF   :  System.out::println   (System.out belirli bir nesnedir)
        kelimeler.forEach(System.out::println);

        // Belirli bir nesneye bağlı
        String onek = "Onek:";
        Function<String, Boolean> baslarMi = onek::startsWith; // 'onek' nesnesinin metodu
        System.out.println("'Onek:Test'.startsWith uygulamasi -> " + baslarMi.apply("Onek"));

        System.out.println("\n=== TÜR 3: Rastgele Nesnenin Örnek Metodu (Sinif::metot) ===\n");
        // LAMBDA:  s -> s.toUpperCase()    (her eleman için kendi metodu)
        // REF   :  String::toUpperCase
        List<String> buyukler = kelimeler.stream()
                .map(String::toUpperCase)   // her String'in kendi toUpperCase'i
                .collect(Collectors.toList());
        System.out.println("Buyuk harfli: " + buyukler);

        // Sıralamada çok yaygın: String::compareToIgnoreCase
        List<String> sirali = kelimeler.stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        System.out.println("Buyuk/kucuk farketmeden sirali: " + sirali);

        System.out.println("\n=== TÜR 4: Yapıcı (Constructor) Referansı (Sinif::new) ===\n");
        // LAMBDA:  ad -> new Kullanici(ad)
        // REF   :  Kullanici::new
        Function<String, Kullanici> kullaniciUret = Kullanici::new;
        Kullanici k = kullaniciUret.apply("Zeynep");
        System.out.println("Olusturulan kullanici: " + k);

        // Stream ile toplu nesne üretimi
        List<Kullanici> kullanicilar = Arrays.asList("Ali", "Veli", "Deli").stream()
                .map(Kullanici::new)    // her isim için yeni Kullanici
                .collect(Collectors.toList());
        System.out.println("Toplu uretim: " + kullanicilar);

        // İki argümanlı constructor referansı (BiFunction)
        BiFunction<String, Integer, Kullanici> yasliKullanici = Kullanici::new;
        System.out.println("Yasli kullanici: " + yasliKullanici.apply("Hasan", 65));

        // Supplier ile parametresiz constructor
        Supplier<StringBuilder> sbUret = StringBuilder::new;
        System.out.println("Bos StringBuilder uretildi, uzunluk: " + sbUret.get().length());

        System.out.println("\n=== GERÇEK HAYAT: Çalışanları metot referanslarıyla işleme ===\n");
        List<Calisan> calisanlar = Arrays.asList(
                new Calisan("Mehmet", 42000),
                new Calisan("Ayse", 55000),
                new Calisan("Burak", 38000));

        // Comparator.comparing + method reference
        calisanlar.stream()
                .sorted(Comparator.comparing(Calisan::getMaas))  // TÜR 3 referans
                .map(Calisan::getIsim)                            // TÜR 3 referans
                .forEach(System.out::println);                    // TÜR 2 referans
    }

    /** TÜR 1 örneği için static metot. */
    static String selamVer(String isim) {
        return "Merhaba " + isim + "!";
    }

    static class Kullanici {
        String ad;
        int yas;

        Kullanici(String ad) {
            this.ad = ad;
            this.yas = 0;
        }

        Kullanici(String ad, int yas) {
            this.ad = ad;
            this.yas = yas;
        }

        @Override
        public String toString() {
            return ad + (yas > 0 ? "(" + yas + ")" : "");
        }
    }

    static class Calisan {
        private String isim;
        private double maas;

        Calisan(String isim, double maas) {
            this.isim = isim;
            this.maas = maas;
        }

        String getIsim() { return isim; }
        double getMaas() { return maas; }
    }
}
