// Ornek1: PROBLEM — sıkı bağlılık (tight coupling). Spring YOK; saf Java.
// Bu kod ÇALIŞIR ama esnek değildir: bir sınıf, bağımlılıklarını kendi içinde "new" ile yaratır.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Servisi oluşturuyoruz ama aslında neye bağımlı olduğunu DIŞARIDAN göremiyoruz.
        // Bağımlılıklar sınıfın İÇİNDE gizli; bu, değiştirmeyi ve test etmeyi zorlaştırır.
        SiparisServisi servis = new SiparisServisi();
        servis.siparisVer("Klavye");

        System.out.println("""

                --- Bu tasarımın sorunları ---
                1) SiparisServisi, MySqlDepo ve EpostaGonderici'ye SIKICA bağlı.
                2) Depoyu PostgreSQL'e ya da bildirimi SMS'e çevirmek için SINIFI değiştirmek gerekir.
                3) Test ederken sahte (mock) bağımlılık veremeyiz; gerçek e-posta/DB devreye girer.
                Çözüm: bağımlılıkları içeride 'new'lemek yerine DIŞARIDAN vermek (sonraki örnekler).""");
    }
}

// Somut bağımlılıklar
class MySqlDepo {
    void kaydet(String kayit) { System.out.println("  [MySQL] kaydedildi: " + kayit); }
}
class EpostaGonderici {
    void gonder(String mesaj) { System.out.println("  [e-posta] " + mesaj); }
}

// SORUN: Servis, somut sınıfları KENDİSİ yaratıyor (new). Bu, sıkı bağlılıktır.
class SiparisServisi {
    private final MySqlDepo depo = new MySqlDepo();              // <-- sıkıca bağlı
    private final EpostaGonderici gonderici = new EpostaGonderici(); // <-- sıkıca bağlı

    void siparisVer(String urun) {
        depo.kaydet(urun);
        gonderici.gonder("Siparişiniz alındı: " + urun);
    }
}
