import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  StringJoiner ve String.join - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   - String.join(...)   : Statik bir yardımcı metot. Birden fazla parçayı
 *                          belirli bir ayırıcıyla (delimiter) birleştirir.
 *   - StringJoiner       : Ayırıcı (delimiter), önek (prefix) ve sonek (suffix)
 *                          ile metin parçalarını biriktirip birleştiren sınıf.
 *
 * NEDEN GELDİ? (Hangi problemi çözdü?)
 *   Bir listeyi "a, b, c" gibi virgülle birleştirmek çok yaygın bir ihtiyaçtı
 *   ama Java'da standart bir yolu yoktu. Geliştiriciler genelde döngü içinde
 *   StringBuilder kullanıp "SON elemana virgül koymama" mantığını elle
 *   yazıyordu. Bu, tekrar eden ve hata yapmaya açık (sondaki fazladan virgül
 *   gibi) bir koddu. Java 8 bunu standartlaştırdı.
 *
 * NE İŞE YARAR: CSV satırı üretme, SQL IN listesi, log mesajları, URL query
 *   parametreleri, raporlarda "etiket listesi" gibi yerlerde.
 */
public class StringJoinerOrnekleri {

    public static void main(String[] args) {

        List<String> sehirler = Arrays.asList("Istanbul", "Ankara", "Izmir", "Bursa");

        System.out.println("=== 1. ESKİ YÖNTEM: StringBuilder + manuel ayırıcı ===\n");
        // ESKİ YÖNTEM: son virgülü engellemek için ekstra mantık gerekir
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sehirler.size(); i++) {
            sb.append(sehirler.get(i));
            if (i < sehirler.size() - 1) {   // son elemana virgül KOYMA kontrolü
                sb.append(", ");
            }
        }
        System.out.println("StringBuilder ile: " + sb.toString());

        System.out.println("\n=== 2. YENİ YÖNTEM: String.join ===\n");
        // YENİ YÖNTEM: tek satır, hata riski yok
        String birlesik = String.join(", ", sehirler);
        System.out.println("String.join ile  : " + birlesik);
        // Varargs ile de calisir
        System.out.println("Varargs ile      : " + String.join(" - ", "A", "B", "C"));

        System.out.println("\n=== 3. StringJoiner: ayırıcı + önek + sonek ===\n");
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        sehirler.forEach(sj::add);
        System.out.println("StringJoiner: " + sj.toString());

        System.out.println("\n=== 4. Boş durumda 'emptyValue' ===\n");
        StringJoiner bos = new StringJoiner(", ", "{", "}");
        bos.setEmptyValue("(liste bos)");   // hic add edilmezse bu yazi cikar
        System.out.println("Bos joiner: " + bos.toString());

        System.out.println("\n=== 5. Stream + Collectors.joining (en pratiği) ===\n");
        List<Calisan> calisanlar = Arrays.asList(
                new Calisan("Ahmet", "Yazilim"),
                new Calisan("Zeynep", "Pazarlama"),
                new Calisan("Mehmet", "Finans"));

        // İsimleri tek satırda birleştir
        String isimSatiri = calisanlar.stream()
                .map(c -> c.isim)
                .collect(Collectors.joining(", ", "Calisanlar: ", "."));
        System.out.println(isimSatiri);

        System.out.println("\n=== 6. GERÇEK HAYAT: CSV satırı üretme ===\n");
        for (Calisan c : calisanlar) {
            String csvSatir = new StringJoiner(";")
                    .add(c.isim)
                    .add(c.departman)
                    .toString();
            System.out.println(csvSatir);
        }

        System.out.println("\n=== 7. GERÇEK HAYAT: SQL IN listesi ===\n");
        List<Integer> idler = Arrays.asList(101, 102, 103, 205);
        String inListesi = idler.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "WHERE id IN (", ")"));
        System.out.println(inListesi);

        System.out.println("\n=== 8. GERÇEK HAYAT: URL query string ===\n");
        StringJoiner query = new StringJoiner("&", "?", "");
        query.add("kategori=elektronik");
        query.add("siralama=fiyat");
        query.add("sayfa=1");
        System.out.println("https://magaza.com/urunler" + query);
    }

    static class Calisan {
        String isim, departman;

        Calisan(String isim, String departman) {
            this.isim = isim;
            this.departman = departman;
        }
    }
}
