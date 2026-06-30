// Ornek1: Optional oluşturma ve değere güvenli erişim.
// Çalıştırma: java Ornek1.java
import java.util.Optional;

public class Ornek1 {

    public static void main(String[] args) {
        // Optional, "değer olabilir ya da olmayabilir" durumunu açıkça temsil eder.
        Optional<String> dolu = Optional.of("merhaba");        // değer kesin var
        Optional<String> bos = Optional.empty();               // değer yok
        Optional<String> belkiNull = Optional.ofNullable(null); // null ise empty olur

        System.out.println("dolu.isPresent()  : " + dolu.isPresent());
        System.out.println("bos.isPresent()   : " + bos.isPresent());
        System.out.println("bos.isEmpty()     : " + bos.isEmpty());

        // ifPresent: değer varsa çalışır, yoksa hiçbir şey yapmaz (null kontrolüne gerek yok).
        dolu.ifPresent(v -> System.out.println("\nDeğer var: " + v));
        bos.ifPresent(v -> System.out.println("Bu satır çalışmaz"));

        // orElse: değer yoksa varsayılan döndür.
        System.out.println("\ndolu.orElse(...)      : " + dolu.orElse("varsayılan"));
        System.out.println("bos.orElse(...)       : " + bos.orElse("varsayılan"));
        System.out.println("belkiNull.orElse(...) : " + belkiNull.orElse("null geldi"));

        // orElseGet: varsayılanı SADECE gerektiğinde üret (tembel; pahalı hesaplar için).
        String sonuc = bos.orElseGet(() -> "hesaplanmış varsayılan");
        System.out.println("bos.orElseGet(...)    : " + sonuc);

        // ifPresentOrElse: var/yok için iki ayrı davranış.
        bos.ifPresentOrElse(
                v -> System.out.println("değer: " + v),
                () -> System.out.println("\nDeğer yok, alternatif iş yapıldı.")
        );
    }
}
