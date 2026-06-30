import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 *  OPTIONAL SINIFI - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Optional<T>, "bir değer olabilir VEYA olmayabilir" durumunu temsil eden
 *   bir KAPSAYICIDIR (container). İçinde ya bir değer vardır ya da boştur.
 *   Amaç: null yerine, "değer yok" durumunu TİP SEVİYESİNDE açıkça belirtmek.
 *
 * NEDEN GELDİ? (Hangi problemi çözdü?)
 *   NullPointerException (NPE), Java'nın en sık görülen hatasıdır. Bir metot
 *   null döndürdüğünde, çağıran taraf bunu kontrol etmeyi unutursa program
 *   çöker. Tony Hoare null'ı "milyar dolarlık hata" diye adlandırmıştır.
 *   Optional, bir metodun "değer döndürmeyebilirim" demesini imza seviyesinde
 *   zorunlu kılar; böylece derleme zamanında dikkatli olmaya iter.
 *
 * NE İŞE YARAR: NPE riskini azaltır, kodun niyetini netleştirir, varsayılan
 *   değer / alternatif akış yönetimini kolaylaştırır.
 *
 * DİKKAT: Optional alan (field) olarak veya metot parametresi olarak
 *   kullanılması TAVSİYE EDİLMEZ. Esasen DÖNÜŞ TİPİ olarak tasarlanmıştır.
 */
public class OptionalOrnekleri {

    static List<Kullanici> kullanicilar = Arrays.asList(
            new Kullanici(1, "Ahmet", "ahmet@mail.com"),
            new Kullanici(2, "Zeynep", null),          // e-postası yok
            new Kullanici(3, "Mehmet", "mehmet@mail.com"));

    public static void main(String[] args) {

        System.out.println("=== 1. ESKİ YÖNTEM: null kontrolü ===\n");
        // ESKİ YÖNTEM: metot null döndürür, çağıran null kontrolü yapmak zorunda
        Kullanici k = kullaniciBulEski(2);
        if (k != null) {                       // unutulursa -> NPE
            if (k.email != null) {             // iç içe null kontrolleri
                System.out.println("Email: " + k.email.toUpperCase());
            } else {
                System.out.println("Email yok (eski yontem)");
            }
        }

        System.out.println("\n=== 2. YENİ YÖNTEM: Optional ile ===\n");
        Optional<Kullanici> opt = kullaniciBul(2);
        // isPresent / get (eski tarz, tavsiye edilmez ama mümkün)
        if (opt.isPresent()) {
            System.out.println("Bulundu: " + opt.get().isim);
        }

        System.out.println("\n=== 3. ifPresent : Değer varsa çalıştır ===\n");
        kullaniciBul(1).ifPresent(u -> System.out.println("ifPresent -> " + u.isim));
        kullaniciBul(99).ifPresent(u -> System.out.println("Bu satir CALISMAZ"));
        System.out.println("99 numarali kullanici icin ifPresent calismadi (deger yok).");

        System.out.println("\n=== 4. orElse / orElseGet / orElseThrow ===\n");
        // orElse: değer yoksa varsayılan döndür
        Kullanici varsayilan = kullaniciBul(99)
                .orElse(new Kullanici(0, "Misafir", "yok@mail.com"));
        System.out.println("orElse -> " + varsayilan.isim);

        // orElseGet: varsayılanı LAZY üret (yalnızca gerekirse hesaplanır)
        Kullanici lazy = kullaniciBul(99)
                .orElseGet(() -> {
                    System.out.println("   (pahali varsayilan uretiliyor...)");
                    return new Kullanici(0, "Uretildi", null);
                });
        System.out.println("orElseGet -> " + lazy.isim);

        // orElseThrow: değer yoksa istisna fırlat
        try {
            kullaniciBul(99).orElseThrow(() ->
                    new RuntimeException("Kullanici bulunamadi!"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow -> " + e.getMessage());
        }

        System.out.println("\n=== 5. map / flatMap : Zincirleme dönüşüm ===\n");
        // E-postayı güvenle büyük harfe çevir; yoksa varsayılan yaz
        String email = kullaniciBul(1)
                .map(u -> u.email)             // Optional<String>
                .map(String::toUpperCase)      // değer varsa dönüştür
                .orElse("EMAIL YOK");
        System.out.println("Kullanici 1 email: " + email);

        // E-postası olmayan kullanıcı (Zeynep) -> zincir güvenle "EMAIL YOK"
        String email2 = kullaniciBul(2)
                .map(u -> u.email)
                .map(String::toUpperCase)
                .orElse("EMAIL YOK");
        System.out.println("Kullanici 2 email: " + email2 + "  (null guvenle ele alindi)");

        System.out.println("\n=== 6. filter ile koşullu Optional ===\n");
        // Sadece mail.com uzantılı e-postaları kabul et
        String gecerli = kullaniciBul(3)
                .map(u -> u.email)
                .filter(e -> e != null && e.endsWith("mail.com"))
                .orElse("GECERSIZ");
        System.out.println("Gecerli email kontrolu: " + gecerli);

        System.out.println("\n=== 7. Optional Oluşturma Yöntemleri ===\n");
        Optional<String> dolu = Optional.of("deger");          // null OLAMAZ
        Optional<String> bos = Optional.empty();               // bilerek boş
        Optional<String> belkiNull = Optional.ofNullable(null);// null olabilir
        System.out.println("of()         -> " + dolu);
        System.out.println("empty()      -> " + bos);
        System.out.println("ofNullable() -> " + belkiNull);

        System.out.println("\n=== 8. GERÇEK HAYAT: Banka hesap bakiyesi ===\n");
        // Hesap bulunamazsa 0 bakiye varsay, varsa bakiyeyi formatla
        double bakiye = hesapBul("TR0001")
                .map(h -> h.bakiye)
                .orElse(0.0);
        System.out.printf("TR0001 bakiye: %.2f TL%n", bakiye);

        double bakiye2 = hesapBul("YOK")
                .map(h -> h.bakiye)
                .orElse(0.0);
        System.out.printf("YOK   bakiye: %.2f TL (hesap yok, guvenli varsayilan)%n", bakiye2);
    }

    /** ESKİ tarz: null döndürebilen metot (riskli). */
    static Kullanici kullaniciBulEski(int id) {
        for (Kullanici u : kullanicilar) {
            if (u.id == id) return u;
        }
        return null; // çağıran null kontrolü yapmazsa NPE riski
    }

    /** YENİ tarz: Optional döndüren metot (güvenli, niyet açık). */
    static Optional<Kullanici> kullaniciBul(int id) {
        return kullanicilar.stream()
                .filter(u -> u.id == id)
                .findFirst();   // Stream zaten Optional döndürür
    }

    static Optional<Hesap> hesapBul(String iban) {
        if (iban.equals("TR0001")) {
            return Optional.of(new Hesap("TR0001", 12500.75));
        }
        return Optional.empty();
    }

    static class Kullanici {
        int id;
        String isim;
        String email; // null olabilir

        Kullanici(int id, String isim, String email) {
            this.id = id;
            this.isim = isim;
            this.email = email;
        }
    }

    static class Hesap {
        String iban;
        double bakiye;

        Hesap(String iban, double bakiye) {
            this.iban = iban;
            this.bakiye = bakiye;
        }
    }
}
