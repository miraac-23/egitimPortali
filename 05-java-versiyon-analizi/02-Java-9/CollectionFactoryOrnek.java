/*
 * =============================================================================
 *  JAVA 9 - COLLECTION FACTORY METOTLARI
 *  List.of / Set.of / Map.of / Map.ofEntries
 * =============================================================================
 *
 *  NEDİR?  Küçük, DEĞİŞTİRİLEMEZ (immutable) koleksiyonları tek satırda
 *          oluşturan statik fabrika metotları.
 *
 *  NEDEN?  Java 8'de sabit koleksiyon yaratmak hem uzun hem de sonuç çoğu zaman
 *          DEĞİŞTİRİLEBİLİR kalıyordu (hata kaynağı). "Double-brace" idiomu ise
 *          gizli iç sınıf üretip bellek sızıntısına yol açıyordu.
 *
 *  Derleme/Çalıştırma:
 *      javac --release 9 CollectionFactoryOrnek.java
 *      java CollectionFactoryOrnek
 * =============================================================================
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionFactoryOrnek {

    public static void main(String[] args) {

        // =====================================================================
        // 1) LISTE OLUŞTURMA - ESKİ vs YENİ
        // =====================================================================
        System.out.println("--- 1) List olusturma ---");

        // ESKİ YÖNTEM 1: Arrays.asList -> SABİT BOYUT ama eleman set edilebilir,
        //                ayrıca add/remove UnsupportedOperationException atar (yarı mutable).
        List<String> eskiAsList = Arrays.asList("elma", "armut", "kiraz");

        // ESKİ YÖNTEM 2: Gerçekten immutable istiyorsak sarmalamak gerekiyordu:
        List<String> eskiImmutable =
                Collections.unmodifiableList(new ArrayList<>(Arrays.asList("elma", "armut")));

        // YENİ YÖNTEM (Java 9): tek satır, tam immutable:
        List<String> yeni = List.of("elma", "armut", "kiraz");

        System.out.println("Eski (asList)    : " + eskiAsList);
        System.out.println("Eski (unmodifiable): " + eskiImmutable);
        System.out.println("Yeni (List.of)   : " + yeni);

        // =====================================================================
        // 2) IMMUTABLE DAVRANIŞI - değiştirmeye çalışınca ne olur?
        // =====================================================================
        System.out.println("\n--- 2) Immutable davranisi ---");
        try {
            yeni.add("muz"); // UnsupportedOperationException bekleniyor
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of() degistirilemez -> add() denemesi: "
                    + e.getClass().getSimpleName());
        }

        // =====================================================================
        // 3) NULL KABUL ETMEME
        // =====================================================================
        System.out.println("\n--- 3) null kabul etmeme ---");
        // ESKİ: Arrays.asList(null) sorunsuz null eleman alabilir (gizli hata kaynağı).
        List<String> nullIcerenEski = Arrays.asList("a", null, "c");
        System.out.println("Eski asList null kabul etti: " + nullIcerenEski);

        // YENİ: List.of null elemanı REDDEDER.
        try {
            List<String> patlar = List.of("a", null, "c");
            System.out.println(patlar); // buraya gelmez
        } catch (NullPointerException e) {
            System.out.println("List.of() null reddetti -> NullPointerException");
        }

        // =====================================================================
        // 4) SET.of - tekrar eden eleman REDDEDİLİR
        // =====================================================================
        System.out.println("\n--- 4) Set.of ---");
        Set<Integer> kume = Set.of(1, 2, 3, 4);
        System.out.println("Set.of(1,2,3,4) = " + kume);
        try {
            Set<Integer> hata = Set.of(1, 2, 2); // tekrar eden eleman!
            System.out.println(hata);
        } catch (IllegalArgumentException e) {
            System.out.println("Set.of() tekrar eden eleman reddetti -> IllegalArgumentException");
        }

        // =====================================================================
        // 5) MAP.of ve MAP.ofEntries - ESKİ vs YENİ
        // =====================================================================
        System.out.println("\n--- 5) Map olusturma ---");

        // ESKİ YÖNTEM: tek tek put
        Map<String, Integer> eskiMap = new HashMap<>();
        eskiMap.put("bir", 1);
        eskiMap.put("iki", 2);
        eskiMap.put("uc", 3);

        // ESKİ "double-brace" idiomu (KÖTÜ pratik - anonim iç sınıf + bellek sızıntısı):
        @SuppressWarnings("serial")
        Map<String, Integer> doubleBrace = new HashMap<String, Integer>() {{
            put("bir", 1);
            put("iki", 2);
        }};

        // YENİ: Map.of (en fazla 10 anahtar-değer çifti destekler)
        Map<String, Integer> yeniMap = Map.of("bir", 1, "iki", 2, "uc", 3);

        // YENİ: Map.ofEntries -> 10'dan fazla giriş veya daha okunaklı yapı için
        Map<String, Integer> ofEntries = Map.ofEntries(
                Map.entry("bir", 1),
                Map.entry("iki", 2),
                Map.entry("uc", 3),
                Map.entry("dort", 4)
        );

        System.out.println("Eski (put)       : " + eskiMap);
        System.out.println("Eski (doubleBrace): " + doubleBrace);
        System.out.println("Yeni (Map.of)    : " + yeniMap);
        System.out.println("Yeni (ofEntries) : " + ofEntries);

        // Map.of de immutable ve null reddeder:
        try {
            yeniMap.put("dort", 4);
        } catch (UnsupportedOperationException e) {
            System.out.println("Map.of() degistirilemez -> put() : "
                    + e.getClass().getSimpleName());
        }

        // =====================================================================
        // 6) GERÇEK HAYAT ÖRNEĞİ: Sabit HTTP hata kodu tablosu
        // =====================================================================
        System.out.println("\n--- 6) Gercek hayat: sabit lookup tablosu ---");
        Map<Integer, String> httpKodlari = Map.of(
                200, "OK",
                404, "Bulunamadi",
                500, "Sunucu Hatasi"
        );
        int gelenKod = 404;
        System.out.println("HTTP " + gelenKod + " => "
                + httpKodlari.getOrDefault(gelenKod, "Bilinmeyen"));
    }
}
