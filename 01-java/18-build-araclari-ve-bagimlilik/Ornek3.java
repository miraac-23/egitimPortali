// Ornek3: Elle bağımlılık enjeksiyonu (manual dependency injection).
// Modülleri birbirine bağlamak (wiring) build/uygulama kurulumunun kalbidir.
// Spring bunu otomatik yapar; burada elle yaparak fikri görüyoruz.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        // "Composition root": bağımlılıkları burada kurar ve birbirine bağlarız.
        // Üst seviye (SiparisServisi) alt seviyelere ARAYÜZ üzerinden bağlıdır;
        // somut sınıfları bilmez. Bu, gevşek bağlılık (loose coupling) demektir.
        MesajGonderici eposta = new EpostaGonderici();
        Depo depo = new BellekDepo();

        SiparisServisi servis = new SiparisServisi(depo, eposta);
        servis.siparisOlustur("Klavye");
        servis.siparisOlustur("Mouse");

        System.out.println("\nToplam sipariş: " + depo.adet());
        System.out.println("\nNot: Spring'de bu bağlama işini @Autowired / IoC container yapar.");
    }
}

// Soyutlamalar (sözleşmeler)
interface MesajGonderici { void gonder(String mesaj); }
interface Depo { void kaydet(String kayit); int adet(); }

// Somut uygulamalar
class EpostaGonderici implements MesajGonderici {
    public void gonder(String mesaj) { System.out.println("  [e-posta] " + mesaj); }
}
class BellekDepo implements Depo {
    private int sayac = 0;
    public void kaydet(String kayit) { sayac++; System.out.println("  [depo] kaydedildi: " + kayit); }
    public int adet() { return sayac; }
}

// Üst seviye servis: bağımlılıkları CONSTRUCTOR ile dışarıdan alır (injection).
class SiparisServisi {
    private final Depo depo;
    private final MesajGonderici gonderici;

    SiparisServisi(Depo depo, MesajGonderici gonderici) {
        this.depo = depo;
        this.gonderici = gonderici;
    }

    void siparisOlustur(String urun) {
        depo.kaydet(urun);
        gonderici.gonder("Siparişiniz alındı: " + urun);
    }
}
