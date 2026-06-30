// Ornek2: Static initializer block — sınıf düzeyinde tek seferlik, karmaşık başlatma.
// Çalıştırma: java Ornek2.java
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Ornek2 {

    public static void main(String[] args) {
        // Statik tablo, sınıf yüklenince static blokta bir kez kurulmuştu.
        System.out.println("USD kuru: " + DovizTablosu.kur("USD"));
        System.out.println("EUR kuru: " + DovizTablosu.kur("EUR"));
        System.out.println("Bilinmeyen: " + DovizTablosu.kur("XXX"));
        System.out.println("Toplam para birimi: " + DovizTablosu.adet());

        System.out.println("""

                --- static initializer block ---
                static { ... } bloğu, sınıf İLK yüklendiğinde BİR KEZ çalışır (tüm static alanlardan sonra,
                herhangi bir nesne/static metot kullanılmadan önce).
                Kullanım: tek satırda atanamayan KARMAŞIK statik başlatma — sabit tablolar/haritalar kurmak,
                kaynak/yapılandırma yüklemek, hesaplanmış sabitler üretmek.
                Basit atamalar için static blok gerekmez (alan başlatması yeter): static int X = 5;""");
    }
}

class DovizTablosu {
    private static final Map<String, Double> KURLAR;

    // Karmaşık başlatma: bir Map'i doldurup değiştirilemez yap. Tek satıra sığmaz -> static blok.
    static {
        Map<String, Double> m = new HashMap<>();
        m.put("USD", 32.50);
        m.put("EUR", 35.10);
        m.put("GBP", 41.20);
        KURLAR = Collections.unmodifiableMap(m);
        System.out.println("[static blok] döviz tablosu yüklendi (" + m.size() + " kayıt)");
    }

    static double kur(String kod) { return KURLAR.getOrDefault(kod, 0.0); }
    static int adet() { return KURLAR.size(); }
}
