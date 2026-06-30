// Ornek2: JAVA 9-11 — koleksiyon fabrikaları, var, yeni String metotları, Stream eklentileri.
// "Öncesi vs sonrası" karşılaştırmalarıyla 9, 10 ve 11'in getirdikleri.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ornek2 {

    public static void main(String[] args) {
        // ============ JAVA 9: değişmez koleksiyon fabrikaları ============
        // ESKİ: liste oluştur, doldur, unmodifiable'a sar (3 adım)
        List<String> eski = new ArrayList<>();
        eski.add("a"); eski.add("b"); eski.add("c");
        List<String> eskiDegismez = Collections.unmodifiableList(eski);
        // JAVA 9: tek satır
        List<String> yeni = List.of("a", "b", "c");
        System.out.println("Koleksiyon fabrikası (eski vs List.of): " + eskiDegismez.equals(yeni));

        // ============ JAVA 10: var ile yerel değişken tip çıkarımı ============
        // ESKİ: tip iki kez yazılır
        List<String> acikTip = new ArrayList<>();
        // JAVA 10: tip çıkarılır (sağ taraftan)
        var cikarilanTip = new ArrayList<String>();
        acikTip.add("x"); cikarilanTip.add("x");
        System.out.println("var (açık tip vs var): " + acikTip.equals(cikarilanTip));

        // ============ JAVA 11: yeni String metotları ============
        String metin = "  merhaba  ";
        System.out.println("\n--- Java 11 String metotları ---");
        System.out.println("strip()  : '" + metin.strip() + "'  (trim'den Unicode-doğru hali)");
        System.out.println("isBlank(): " + "   ".isBlank());
        System.out.println("repeat(3): " + "ab".repeat(3));
        System.out.println("lines()  : " + "a\nb\nc".lines().count() + " satır");

        // ============ JAVA 9: Stream eklentileri (takeWhile / dropWhile / iterate) ============
        System.out.println("\n--- Java 9 Stream eklentileri ---");
        List<Integer> sayilar = List.of(1, 2, 3, 4, 5, 1, 2);
        // takeWhile: koşul bozulana kadar al
        System.out.println("takeWhile(<4): " + sayilar.stream().takeWhile(n -> n < 4).collect(Collectors.toList()));
        // dropWhile: koşul bozulana kadar atla
        System.out.println("dropWhile(<4): " + sayilar.stream().dropWhile(n -> n < 4).collect(Collectors.toList()));
        // iterate (3 argümanlı, Java 9): klasik for gibi
        System.out.println("iterate      : " + Stream.iterate(1, n -> n <= 16, n -> n * 2).collect(Collectors.toList()));

        System.out.println("""

                --- Java 9-11 NE getirdi? ---
                Java 9 : Modül sistemi (JPMS), List/Map.of, jshell, Stream eklentileri.
                Java 10: var (yerel tip çıkarımı).
                Java 11 (LTS): yeni String metotları, standart HttpClient, TEK DOSYA çalıştırma (java Dosya.java).
                Geçiş tuzağı: Java 11'de Java EE/CORBA modülleri KALDIRILDI (JAXB/JAX-WS ayrı eklenir).""");
    }
}
