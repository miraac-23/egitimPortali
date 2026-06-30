// Ornek2: Yapısal (structural) desenler — Adapter, Decorator, Facade.
// Gerçek senaryo: eski bir ödeme API'sini uyarlama, kahve fiyatı süsleme, sipariş cephesi.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // --- ADAPTER: uyumsuz bir arayüzü, beklediğimiz arayüze uyarla ---
        // Yeni kodumuz OdemeServisi bekliyor ama elimizde eski, farklı imzalı bir API var.
        OdemeServisi odeme = new EskiOdemeAdapter(new EskiOdemeApi());
        System.out.println("Adapter ile ödeme:");
        odeme.ode(250.0, "TRY");

        // --- DECORATOR: bir nesneye, sınıfını değiştirmeden davranış/özellik ekle ---
        // Temel kahveyi sarmalayarak süt ve şeker "ekliyoruz"; her sarmalayıcı fiyatı artırır.
        System.out.println("\nDecorator ile kahve:");
        Kahve kahve = new SekerEkle(new SutEkle(new SadeKahve()));
        System.out.printf("  %s = %.2f TL%n", kahve.aciklama(), kahve.fiyat());

        // --- FACADE: karmaşık bir alt sistemi tek, basit bir arayüzün arkasına gizle ---
        // İstemci stok/ödeme/kargo ayrıntılarıyla uğraşmaz; tek bir siparisVer() çağırır.
        System.out.println("\nFacade ile sipariş:");
        SiparisFacade siparis = new SiparisFacade();
        siparis.siparisVer("Klavye", 250.0);
    }
}

// --- Adapter ---
interface OdemeServisi { void ode(double tutar, String paraBirimi); }

class EskiOdemeApi { // değiştiremediğimiz eski/3. parti API
    void makePayment(int kurus, String currency) {
        System.out.println("  [eski API] " + kurus + " kuruş (" + currency + ") tahsil edildi.");
    }
}

class EskiOdemeAdapter implements OdemeServisi { // eskiyi yeni arayüze uyarlar
    private final EskiOdemeApi eski;
    EskiOdemeAdapter(EskiOdemeApi eski) { this.eski = eski; }
    public void ode(double tutar, String paraBirimi) {
        eski.makePayment((int) Math.round(tutar * 100), paraBirimi); // TL -> kuruş dönüşümü
    }
}

// --- Decorator ---
interface Kahve { String aciklama(); double fiyat(); }
class SadeKahve implements Kahve {
    public String aciklama() { return "Kahve"; }
    public double fiyat() { return 30; }
}
abstract class KahveSusleyici implements Kahve { // ortak sarmalayıcı taban
    protected final Kahve ic;
    KahveSusleyici(Kahve ic) { this.ic = ic; }
}
class SutEkle extends KahveSusleyici {
    SutEkle(Kahve ic) { super(ic); }
    public String aciklama() { return ic.aciklama() + " + süt"; }
    public double fiyat() { return ic.fiyat() + 8; }
}
class SekerEkle extends KahveSusleyici {
    SekerEkle(Kahve ic) { super(ic); }
    public String aciklama() { return ic.aciklama() + " + şeker"; }
    public double fiyat() { return ic.fiyat() + 3; }
}

// --- Facade ---
class StokServisi { boolean dus(String urun) { System.out.println("  [stok] " + urun + " stoktan düşüldü."); return true; } }
class KargoServisi { void planla(String urun) { System.out.println("  [kargo] " + urun + " için kargo planlandı."); } }

class SiparisFacade {
    private final StokServisi stok = new StokServisi();
    private final KargoServisi kargo = new KargoServisi();
    void siparisVer(String urun, double tutar) {
        // Karmaşık adımları tek çağrının ardına gizler.
        if (stok.dus(urun)) {
            new EskiOdemeAdapter(new EskiOdemeApi()).ode(tutar, "TRY");
            kargo.planla(urun);
            System.out.println("  Sipariş tamamlandı: " + urun);
        }
    }
}
