// Ornek2: URL kodlama (encoding) — özel karakterleri ve sorgu parametrelerini güvenli taşımak.
// Çalıştırma: java Ornek2.java
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Ornek2 {

    public static void main(String[] args) {
        // URL'de boşluk, Türkçe karakter, &, = gibi karakterler özeldir; KODLANMALIDIR.
        String aramaMetni = "spring boot & güvenlik";
        String kodlu = URLEncoder.encode(aramaMetni, StandardCharsets.UTF_8);
        System.out.println("Ham   : " + aramaMetni);
        System.out.println("Kodlu : " + kodlu);                       // boşluk->+, ç/ü/&->%XX
        System.out.println("Çözülmüş: " + URLDecoder.decode(kodlu, StandardCharsets.UTF_8));

        // Sorgu (query) string'i parametrelerden güvenle inşa etmek:
        Map<String, String> parametreler = new LinkedHashMap<>();
        parametreler.put("kategori", "eğitim");
        parametreler.put("arama", "java & spring");
        parametreler.put("sayfa", "2");

        String query = parametreler.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                        + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        System.out.println("\nİnşa edilen sorgu: ?" + query);

        // Tam URL:
        String tamUrl = "https://site.com/ara?" + query;
        System.out.println("Tam URL: " + tamUrl);

        // Geri ayrıştırma: query string -> Map
        System.out.println("\nSorguyu ayrıştır:");
        ayristir(query).forEach((k, v) -> System.out.println("  " + k + " = " + v));

        System.out.println("""

                --- Neden URL kodlama? ---
                URL'de yalnızca belirli karakterler güvenlidir. Boşluk, Türkçe harf, &, =, ? gibi
                karakterler '%XX' (yüzde kodlaması) ile temsil edilir; aksi halde URL bozulur/yanlış
                ayrıştırılır. URLEncoder kodlar, URLDecoder çözer. Sorgu parametrelerini HER ZAMAN kodla.""");
    }

    static Map<String, String> ayristir(String query) {
        Map<String, String> sonuc = new LinkedHashMap<>();
        for (String parca : query.split("&")) {
            String[] kv = parca.split("=", 2);
            sonuc.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return sonuc;
    }
}
