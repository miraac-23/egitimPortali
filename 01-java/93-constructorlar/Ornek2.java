// Ornek2: super() ile kalıtımda constructor zinciri ve kopya constructor.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    static class Arac {
        String marka;
        Arac(String marka) {
            this.marka = marka;
            System.out.println("  Arac constructor: " + marka);
        }
    }

    static class Otomobil extends Arac {
        int kapiSayisi;

        Otomobil(String marka, int kapiSayisi) {
            super(marka);   // ÜST sınıf constructor'ını çağır — İLK satır olmalı (yazmazsan super() örtük eklenir)
            this.kapiSayisi = kapiSayisi;
            System.out.println("  Otomobil constructor: " + kapiSayisi + " kapı");
        }

        // KOPYA constructor: var olan bir nesneden yeni (bağımsız) bir kopya üretir.
        Otomobil(Otomobil digeri) {
            this(digeri.marka, digeri.kapiSayisi); // alanları kopyala
            System.out.println("  (kopya oluşturuldu)");
        }

        @Override public String toString() { return marka + " (" + kapiSayisi + " kapı)"; }
    }

    public static void main(String[] args) {
        System.out.println("new Otomobil(\"Toyota\", 4):");
        Otomobil o1 = new Otomobil("Toyota", 4);
        System.out.println("  -> " + o1 + "  (önce ÜST, sonra ALT constructor çalıştı)");

        System.out.println("\nKopya constructor:");
        Otomobil o2 = new Otomobil(o1);
        o2.marka = "Toyota (kopya)";
        System.out.println("  orijinal: " + o1);
        System.out.println("  kopya   : " + o2 + "  (bağımsız nesne)");

        System.out.println("""

                --- super() ve kopya constructor ---
                Kalıtımda nesne kurulurken ÖNCE üst sınıf constructor'ı çalışır (taban hazır olmalı).
                super(...): üst sınıf constructor'ını çağırır; constructor'ın İLK satırı olmalı.
                  Yazmazsan derleyici parametresiz super()'i ÖRTÜK ekler (üstte no-arg yoksa HATA -> açıkça çağır).
                Başlatma sırası: üst static -> alt static (bir kez) | üst alan/blok+constructor -> alt alan/blok+constructor.
                Kopya constructor: var olan nesneden yeni BAĞIMSIZ kopya üretir (Cloneable'a göre genelde daha güvenli/açık).
                NOT: record'larda compact/canonical constructor kullanılır (topic 62).""");
    }
}
