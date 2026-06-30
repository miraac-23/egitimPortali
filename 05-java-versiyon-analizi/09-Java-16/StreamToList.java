// =============================================================================
//  StreamToList.java
//  JAVA 16 - Stream.toList() metodu
// =============================================================================
//
//  NEDİR?
//  Java 16 ile Stream arayüzüne doğrudan toList() metodu eklendi. Bir stream'i
//  List'e dönüştürmenin en kısa yolu artık budur.
//
//  NEDEN GELDİ?
//  Java 8'den beri stream'i listeye çevirmek için
//      .collect(Collectors.toList())
//  yazmak zorundaydık. Bu hem uzun hem de:
//    - Collectors sınıfını import etmeyi gerektiriyordu,
//    - Dönen listenin mutable/immutable olup olmadığı GARANTİ değildi
//      (implementasyona bağlıydı, genelde ArrayList dönerdi).
//  Stream.toList() ise DEĞİŞMEZ (unmodifiable) bir liste döndürür ve daha sade.
//
//  Derlemek için: javac StreamToList.java
//  Çalıştırmak  : java StreamToList
// =============================================================================

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamToList {

    public static void main(String[] args) {

        System.out.println("=== JAVA 16: Stream.toList() ===\n");

        var sayilar = List.of(5, 12, 8, 130, 44, 3);

        // ---------------------------------------------------------------------
        // ESKİ YÖNTEM (Java 8 - 15): Collectors.toList()
        // ---------------------------------------------------------------------
        List<Integer> eski = sayilar.stream()
                .filter(s -> s > 10)
                .map(s -> s * 2)
                .collect(Collectors.toList());   // uzun, import gerektirir
        System.out.println("ESKİ (collect(Collectors.toList())): " + eski);

        // ---------------------------------------------------------------------
        // YENİ YÖNTEM (Java 16+): Stream.toList()
        // ---------------------------------------------------------------------
        List<Integer> yeni = sayilar.stream()
                .filter(s -> s > 10)
                .map(s -> s * 2)
                .toList();                        // kısa, import yok, immutable
        System.out.println("YENİ (toList())                    : " + yeni);
        System.out.println();

        // ---------------------------------------------------------------------
        // ÖNEMLİ FARK: toList() DEĞİŞMEZ (unmodifiable) liste döndürür
        // ---------------------------------------------------------------------
        System.out.println("--- toList() değişmezdir ---");
        try {
            yeni.add(999); // UnsupportedOperationException fırlatır
        } catch (UnsupportedOperationException e) {
            System.out.println("toList() listesine ekleme yapılamaz (immutable). OK.");
        }

        // collect(Collectors.toList()) genelde MUTABLE ArrayList döner:
        eski.add(999);
        System.out.println("collect(toList()) listesine eklenebildi: " + eski);
        System.out.println();

        // ---------------------------------------------------------------------
        // GERÇEK HAYAT: Bir kullanıcı listesinden aktif olanların adlarını çıkarma
        // ---------------------------------------------------------------------
        System.out.println("--- Gerçek hayat senaryosu ---");
        record Kullanici(String ad, boolean aktif) {}

        var kullanicilar = List.of(
                new Kullanici("Ayşe", true),
                new Kullanici("Burak", false),
                new Kullanici("Cem", true),
                new Kullanici("Deniz", true)
        );

        List<String> aktifAdlar = kullanicilar.stream()
                .filter(Kullanici::aktif)
                .map(Kullanici::ad)
                .sorted()
                .toList();

        System.out.println("Aktif kullanıcılar: " + aktifAdlar);
        System.out.println();

        // ---------------------------------------------------------------------
        // NE ZAMAN ESKİ YÖNTEMİ KULLANMALI?
        //  - Eğer dönen listeyi SONRADAN değiştirmeniz gerekiyorsa
        //    (add/remove), o zaman collect(Collectors.toList()) veya
        //    collect(Collectors.toCollection(ArrayList::new)) kullanın.
        //  - Aksi halde her zaman .toList() tercih edin: kısa ve güvenli.
        // ---------------------------------------------------------------------
        Stream<String> harfler = Stream.of("c", "a", "b");
        System.out.println("Sıralı liste: " + harfler.sorted().toList());
    }
}
