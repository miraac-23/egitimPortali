// Ornek3: Enum singleton — en güvenli ve en kısa singleton (önerilen).
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        // Enum sabiti tek örnektir; erişim her zaman aynı nesneyi verir.
        VeriTabani.INSTANCE.baglan();
        VeriTabani.INSTANCE.sorgu("SELECT * FROM urun");
        System.out.println("Aynı örnek mi? " + (VeriTabani.INSTANCE == VeriTabani.INSTANCE));

        System.out.println("""

                --- Enum singleton (önerilen) ---
                'public enum X { INSTANCE; ... }' en güvenli singleton'dır (Joshua Bloch önerisi).
                Avantajları:
                  - Thread-safe ve lazy (JVM enum'ları güvenle yükler), ekstra kod yok.
                  - Serileştirmeye karşı GÜVENLİ: deserialize tek örneği bozmaz.
                  - Reflection ile ikinci örnek YARATILAMAZ (enum constructor çağrılamaz).
                Dezavantaj: bir sınıfı uzatamaz (enum zaten Enum'u uzatır). Çoğu durumda en iyi seçimdir.
                Not: Spring kullanıyorsan bean'ler zaten singleton'dır; bu deseni elle yazmana gerek kalmaz.""");
    }
}

enum VeriTabani {
    INSTANCE;                                  // tek örnek — işte bu kadar

    private int sorguSayisi = 0;
    VeriTabani() { System.out.println("(VeriTabani başlatıldı)"); }

    public void baglan() { System.out.println("veritabanına bağlanıldı"); }
    public void sorgu(String sql) {
        sorguSayisi++;
        System.out.println("sorgu #" + sorguSayisi + ": " + sql);
    }
}
