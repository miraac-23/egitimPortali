/*
 * =============================================================================
 *  JAVA 9 - OPTIONAL GELİŞTİRMELERİ
 *  ifPresentOrElse / or / stream
 * =============================================================================
 *
 *  Derleme/Çalıştırma:
 *      javac --release 9 OptionalGelistirmeleri.java
 *      java OptionalGelistirmeleri
 * =============================================================================
 */

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalGelistirmeleri {

    public static void main(String[] args) {

        // =====================================================================
        // 1) ifPresentOrElse : varsa şunu yap, YOKSA şunu yap.
        // =====================================================================
        System.out.println("--- 1) ifPresentOrElse ---");
        Optional<String> dolu = Optional.of("Ahmet");
        Optional<String> bos = Optional.empty();

        // ESKİ (Java 8): çirkin if/else, çünkü ifPresent'in "else"i yoktu.
        if (dolu.isPresent()) {
            System.out.println("Eski: kullanici = " + dolu.get());
        } else {
            System.out.println("Eski: kullanici yok");
        }

        // YENİ (Java 9): tek ifade, hem varlık hem yokluk eylemi.
        dolu.ifPresentOrElse(
                ad -> System.out.println("Yeni: kullanici = " + ad),
                () -> System.out.println("Yeni: kullanici yok")
        );
        bos.ifPresentOrElse(
                ad -> System.out.println("Yeni: kullanici = " + ad),
                () -> System.out.println("Yeni: kullanici yok (bos optional)")
        );

        // =====================================================================
        // 2) or : boşsa BAŞKA BİR OPTIONAL ile fallback (yeni Optional döner).
        // =====================================================================
        System.out.println("\n--- 2) or ---");

        // GERÇEK HAYAT: önce cache, yoksa veritabanı, yoksa varsayılan.
        System.out.println("Sonuc: " + kullaniciBul("42").orElse("BULUNAMADI"));
        System.out.println("Sonuc: " + kullaniciBul("999").orElse("BULUNAMADI"));

        // orElse vs or farkı:
        //   orElse(T)        -> doğrudan DEĞER döner
        //   or(Supplier<Optional>) -> başka bir OPTIONAL döner, zincirlenebilir
        Optional<String> zincir = bos
                .or(() -> Optional.empty())          // hala boş
                .or(() -> Optional.of("varsayilan")); // burada dolar
        System.out.println("or zinciri sonucu: " + zincir.get());

        // =====================================================================
        // 3) stream : Optional'ı 0 veya 1 elemanlı Stream'e çevirir.
        //    flatMap ile boş olanları eler -> Optional listesini temizlemenin
        //    en şık yolu.
        // =====================================================================
        System.out.println("\n--- 3) Optional.stream ---");

        List<Optional<String>> optionalListesi = List.of(
                Optional.of("a"),
                Optional.empty(),
                Optional.of("b"),
                Optional.empty(),
                Optional.of("c")
        );

        // ESKİ (Java 8): filter(isPresent) + map(get) gerekirdi.
        List<String> eski = optionalListesi.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        System.out.println("Eski (filter+map): " + eski);

        // YENİ (Java 9): tek flatMap, hem temizler hem açar.
        List<String> yeni = optionalListesi.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        System.out.println("Yeni (flatMap stream): " + yeni);
    }

    // Cache'i simüle eden basit metot.
    private static Optional<String> cachedenBul(String id) {
        if ("42".equals(id)) return Optional.of("Cache: Mehmet");
        return Optional.empty();
    }

    // Veritabanını simüle eden metot.
    private static Optional<String> veritabanindanBul(String id) {
        if ("100".equals(id)) return Optional.of("DB: Ayse");
        return Optional.empty();
    }

    // Java 9 'or' ile temiz fallback zinciri.
    private static Optional<String> kullaniciBul(String id) {
        return cachedenBul(id)
                .or(() -> veritabanindanBul(id));
    }
}
