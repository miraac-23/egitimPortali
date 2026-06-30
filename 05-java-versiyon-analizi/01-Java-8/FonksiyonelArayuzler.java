import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * ============================================================================
 *  FONKSİYONEL ARAYÜZLER (FUNCTIONAL INTERFACES) - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   İçinde TEK BİR soyut metot bulunan arayüzdür (Single Abstract Method - SAM).
 *   Lambda ifadeleri ve method reference'lar, ancak bir fonksiyonel arayüz
 *   tipine atanabilir. Yani lambda'nın "tipi" daima bir fonksiyonel arayüzdür.
 *
 *   @FunctionalInterface anotasyonu derleyiciye "bu arayüzde tam olarak 1 soyut
 *   metot olmalı" der; yanlışlıkla ikinci bir soyut metot eklenirse DERLEME
 *   HATASI verir. (Zorunlu değildir ama tavsiye edilir.)
 *
 * NEDEN GELDİ?
 *   Java zaten Runnable, Comparator, Callable gibi tek metotlu arayüzlere
 *   sahipti ama her ihtiyaç için yeni arayüz tanımlamak zahmetliydi. Java 8,
 *   java.util.function paketinde HAZIR, genel amaçlı fonksiyonel arayüzler
 *   getirdi. Böylece kendi arayüzünü yazmadan davranış geçirebilirsin.
 *
 * EN ÇOK KULLANILANLAR:
 *   - Function<T,R>      : T alır, R döndürür           (apply)
 *   - Consumer<T>        : T alır, hiçbir şey döndürmez  (accept)
 *   - Supplier<T>        : Parametre almaz, T üretir     (get)
 *   - Predicate<T>       : T alır, boolean döndürür       (test)
 *   - BiFunction<T,U,R>  : İki girdi (T,U), R döndürür    (apply)
 *   - UnaryOperator<T>   : T alır, T döndürür             (Function özel hali)
 *   - BinaryOperator<T>  : İki T alır, T döndürür         (BiFunction özel hali)
 */
public class FonksiyonelArayuzler {

    public static void main(String[] args) {

        System.out.println("=== 1. Function<T,R> : Dönüştürme ===\n");
        // Bir String'i uzunluğuna dönüştür
        Function<String, Integer> uzunluk = s -> s.length();
        System.out.println("'Merhaba' uzunlugu: " + uzunluk.apply("Merhaba"));

        // andThen / compose ile fonksiyon zincirleme
        Function<Integer, Integer> ikiyleCarp = x -> x * 2;
        Function<Integer, Integer> birEkle = x -> x + 1;
        // önce çarp sonra ekle:  (5*2)+1 = 11
        System.out.println("andThen (carp->ekle): " + ikiyleCarp.andThen(birEkle).apply(5));
        // önce ekle sonra çarp:  (5+1)*2 = 12
        System.out.println("compose (ekle->carp): " + ikiyleCarp.compose(birEkle).apply(5));

        System.out.println("\n=== 2. Consumer<T> : Tüketme (yan etki) ===\n");
        Consumer<String> yazdir = s -> System.out.println("Tuketildi: " + s);
        yazdir.accept("Java 8");
        // andThen ile iki consumer'ı sırayla çalıştır
        Consumer<String> buyukYaz = s -> System.out.println("BUYUK: " + s.toUpperCase());
        yazdir.andThen(buyukYaz).accept("zincir");

        System.out.println("\n=== 3. Supplier<T> : Üretme ===\n");
        Supplier<Double> rastgele = () -> Math.random();
        System.out.println("Rastgele uretildi: " + rastgele.get());
        // Lazy (tembel) değer üretiminde çok kullanışlıdır.

        System.out.println("\n=== 4. Predicate<T> : Koşul testi ===\n");
        Predicate<Integer> ciftMi = n -> n % 2 == 0;
        Predicate<Integer> pozitifMi = n -> n > 0;
        System.out.println("4 cift mi?          " + ciftMi.test(4));
        // and / or / negate ile birleştirme
        System.out.println("6 cift VE pozitif?  " + ciftMi.and(pozitifMi).test(6));
        System.out.println("-3 cift DEGIL mi?   " + ciftMi.negate().test(-3));

        System.out.println("\n=== 5. BiFunction<T,U,R> : İki girdi ===\n");
        BiFunction<Integer, Integer, Integer> topla = (a, b) -> a + b;
        System.out.println("3 + 4 = " + topla.apply(3, 4));
        BiFunction<String, String, String> birlestir = (a, b) -> a + " " + b;
        System.out.println(birlestir.apply("Merhaba", "Dunya"));

        System.out.println("\n=== 6. UnaryOperator & BinaryOperator ===\n");
        UnaryOperator<String> kucult = s -> s.toLowerCase();
        System.out.println("Kucult: " + kucult.apply("JAVA"));
        BinaryOperator<Integer> maks = (a, b) -> a > b ? a : b;
        System.out.println("Maks(10, 25): " + maks.apply(10, 25));

        System.out.println("\n=== 7. GERÇEK HAYAT: Ürün Filtreleme (E-ticaret) ===\n");
        List<Urun> urunler = Arrays.asList(
                new Urun("Telefon", 15000, true),
                new Urun("Kulaklik", 800, false),
                new Urun("Laptop", 32000, true),
                new Urun("Mouse", 350, true));

        // Predicate'leri yeniden kullanılabilir kurallar olarak tanımla
        Predicate<Urun> stokta = u -> u.stoktaVar;
        Predicate<Urun> pahali = u -> u.fiyat > 1000;

        System.out.println("Stokta OLAN ve PAHALI urunler:");
        for (Urun u : urunler) {
            // İş kurallarını birleştir: stokta VE pahalı
            if (stokta.and(pahali).test(u)) {
                System.out.println("   " + u);
            }
        }

        // Function ile vergi hesaplama kuralı
        Function<Urun, Double> kdvliFiyat = u -> u.fiyat * 1.20;
        System.out.println("\nKDV'li fiyatlar:");
        urunler.forEach(u ->
                System.out.printf("   %-10s : %.2f TL%n", u.ad, kdvliFiyat.apply(u)));

        System.out.println("\n=== 8. ÖZEL @FunctionalInterface Tanımlama ===\n");
        IndirimKurali yuzde10 = fiyat -> fiyat * 0.90;
        IndirimKurali sabit50 = fiyat -> fiyat - 50;
        System.out.println("1000 TL'ye %10 indirim: " + yuzde10.uygula(1000));
        System.out.println("1000 TL'ye 50 TL indirim: " + sabit50.uygula(1000));
    }

    /**
     * Kendi fonksiyonel arayüzümüz. @FunctionalInterface sayesinde ikinci bir
     * soyut metot eklersek derleyici hata verir (güvenlik).
     */
    @FunctionalInterface
    interface IndirimKurali {
        double uygula(double fiyat);
    }

    static class Urun {
        String ad;
        double fiyat;
        boolean stoktaVar;

        Urun(String ad, double fiyat, boolean stoktaVar) {
            this.ad = ad;
            this.fiyat = fiyat;
            this.stoktaVar = stoktaVar;
        }

        @Override
        public String toString() {
            return ad + " - " + fiyat + " TL (stok: " + (stoktaVar ? "var" : "yok") + ")";
        }
    }
}
