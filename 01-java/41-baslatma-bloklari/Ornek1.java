// Ornek1: Instance initializer block (örnek başlatma bloğu) ve çalışma sırası.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        System.out.println(">> İlk nesne oluşturuluyor:");
        new Urun("Klavye");
        System.out.println("\n>> İkinci nesne oluşturuluyor:");
        new Urun("Mouse");

        System.out.println("""

                --- Başlatma sırası (nesne oluşturulurken) ---
                1) (yalnızca İLK kez) static alanlar + static bloklar — sınıf yüklenince, BİR KEZ.
                2) Her 'new'de: üst sınıf kurulur, sonra:
                   - instance alan başlatmaları + instance initializer blokları (yazıldıkları SIRADA),
                   - en son CONSTRUCTOR gövdesi.
                Instance initializer bloğu, birden çok constructor'ın PAYLAŞTIĞI başlatma kodunu
                tek yerde toplamak için kullanılır (her constructor'dan önce çalışır).""");
    }
}

class Urun {
    // 1) static blok: sınıf ilk yüklendiğinde BİR KEZ çalışır.
    static int uretilenSayisi;
    static { uretilenSayisi = 0; System.out.println("  [static blok] Urun sınıfı yüklendi"); }

    // 2) instance alan başlatma + instance initializer blok: HER new'de, constructor'dan ÖNCE.
    private final String ad;
    private final long olusturmaZamani = System.nanoTime();
    { // instance initializer block
        uretilenSayisi++;
        System.out.println("  [instance blok] sayac=" + uretilenSayisi);
    }

    // 3) constructor: en son çalışır.
    Urun(String ad) {
        this.ad = ad;
        System.out.println("  [constructor] ad=" + this.ad + ", zaman kaydı alındı=" + (olusturmaZamani > 0));
    }
}
