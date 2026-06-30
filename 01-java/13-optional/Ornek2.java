// Ornek2: Optional dönüşümleri — map, filter, flatMap.
// Çalıştırma: java Ornek2.java
import java.util.Optional;

public class Ornek2 {

    public static void main(String[] args) {
        // map: değer varsa dönüştürür; yoksa empty kalır (null kontrolü gerekmez).
        Optional<String> ad = Optional.of("  Ada Lovelace  ");
        Optional<String> temiz = ad.map(String::trim).map(String::toUpperCase);
        System.out.println("map zinciri: " + temiz.orElse("(yok)"));

        Optional<String> bos = Optional.empty();
        System.out.println("boş.map(...): " + bos.map(String::trim).orElse("(yok)"));

        // filter: koşulu sağlamıyorsa empty'ye düşürür.
        Optional<Integer> yas = Optional.of(20);
        String durum = yas.filter(y -> y >= 18)
                .map(y -> "Reşit (" + y + ")")
                .orElse("Reşit değil veya bilinmiyor");
        System.out.println("\nfilter sonucu: " + durum);

        // flatMap: dönüşümün kendisi Optional döndürüyorsa iç içe Optional'ı düzleştirir.
        Kullanici k = new Kullanici("ada@site.com");
        Kullanici eksik = new Kullanici(null);

        System.out.println("\nflatMap ile e-posta alanı:");
        System.out.println("  k     -> " + k.eposta().map(e -> e.split("@")[1]).orElse("alan yok"));
        System.out.println("  eksik -> " + eksik.eposta().map(e -> e.split("@")[1]).orElse("alan yok"));
    }

    record Kullanici(String adres) {
        // Alan (adres) null olabilir; dışarıya Optional olarak sunmak daha güvenlidir.
        Optional<String> eposta() {
            return Optional.ofNullable(adres);
        }
    }
}
