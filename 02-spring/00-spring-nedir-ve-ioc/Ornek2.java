// Ornek2: KISMİ ÇÖZÜM — arayüzler + ELLE bağımlılık enjeksiyonu. Hâlâ Spring YOK.
// Bağımlılıkları içeride new'lemek yerine, dışarıdan (constructor ile) veriyoruz.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // "Composition root": nesneleri burada kurar ve birbirine bağlarız.
        // Artık hangi uygulamayı seçeceğimize DIŞARIDAN karar veriyoruz.
        Depo depo = new PostgresDepo();            // istersek MySqlDepo da verebilirdik
        Bildirimci bildirimci = new SmsBildirimci(); // istersek e-posta da verebilirdik

        SiparisServisi servis = new SiparisServisi(depo, bildirimci);
        servis.siparisVer("Mouse");

        // Test için sahte (mock) bağımlılık vermek artık çok kolay:
        SiparisServisi testServisi = new SiparisServisi(
                kayit -> System.out.println("  [sahte depo] " + kayit),
                mesaj -> System.out.println("  [sahte bildirim] " + mesaj));
        System.out.println("\nTest senaryosu (sahte bağımlılıklarla):");
        testServisi.siparisVer("Monitör");

        System.out.println("""

                --- Kazandığımız esneklik ---
                Servis artık ARAYÜZLERE bağlı (Depo, Bildirimci); somut sınıfa değil.
                Uygulamayı değiştirmek için SERVİSİ değil, sadece kurulum satırını değiştiriyoruz.
                ANCAK: tüm nesneleri ve bağlantıları HÂLÂ elle yönetmek zorundayız.
                İşte bu 'elle yönetimi' Spring'in IoC container'ı bizim için yapar (Örnek 3).""");
    }
}

// Soyutlamalar (sözleşmeler)
interface Depo { void kaydet(String kayit); }
interface Bildirimci { void gonder(String mesaj); }

class PostgresDepo implements Depo {
    public void kaydet(String k) { System.out.println("  [PostgreSQL] kaydedildi: " + k); }
}
class SmsBildirimci implements Bildirimci {
    public void gonder(String m) { System.out.println("  [SMS] " + m); }
}

// Bağımlılıklar DIŞARIDAN, constructor ile veriliyor (dependency injection — elle).
class SiparisServisi {
    private final Depo depo;
    private final Bildirimci bildirimci;

    SiparisServisi(Depo depo, Bildirimci bildirimci) {
        this.depo = depo;
        this.bildirimci = bildirimci;
    }

    void siparisVer(String urun) {
        depo.kaydet(urun);
        bildirimci.gonder("Siparişiniz alındı: " + urun);
    }
}
