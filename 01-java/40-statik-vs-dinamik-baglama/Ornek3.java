// Ornek3: Klasik tuzak — alanlar STATİK, instance metotlar DİNAMİK bağlanır.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        Ata ref = new Cocuk();   // referans tipi: Ata, gerçek tip: Cocuk

        // ALAN: statik bağlanır -> REFERANS tipine (Ata) göre. Tuzak burada!
        System.out.println("ref.tip (alan)    : " + ref.tip);        // "ata" (Ata.tip)

        // INSTANCE METOT: dinamik bağlanır -> GERÇEK tipe (Cocuk) göre.
        System.out.println("ref.tipMetot()    : " + ref.tipMetot()); // "cocuk" (Cocuk.tipMetot)

        // Gerçek tiple erişince alan da Cocuk'unkini verir:
        Cocuk c = new Cocuk();
        System.out.println("c.tip (alan)      : " + c.tip);          // "cocuk"

        System.out.println("""

                --- Alan gizleme (field hiding) tuzağı ---
                Alanlar POLİMORFİK DEĞİLDİR; erişim REFERANS tipine göre çözülür (statik bağlama).
                Instance metotlar ise POLİMORFİKTİR; nesnenin GERÇEK tipine göre çözülür (dinamik bağlama).
                Bu yüzden 'ref.tip' Ata'nınkini, 'ref.tipMetot()' Cocuk'unkini verir — sık karşılaşılan tuzak.
                KURAL: alanları override etmeye çalışma; davranış için metot kullan. Alanları private tut.""");
    }
}

class Ata {
    String tip = "ata";                 // alt sınıfta "gizlenecek" (hide), override değil
    String tipMetot() { return "ata"; } // override edilecek -> dinamik
}
class Cocuk extends Ata {
    String tip = "cocuk";               // ATA.tip'i GİZLER (ayrı bir alan)
    @Override String tipMetot() { return "cocuk"; }
}
