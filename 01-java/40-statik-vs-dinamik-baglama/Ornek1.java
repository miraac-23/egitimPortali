// Ornek1: Statik bağlama (static/early binding) — DERLEME anında çözülür.
// Overloading, static/private/final metotlar derleme anında, REFERANS tipine göre seçilir.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // Metot AŞIRI YÜKLEME (overloading): hangi yaz(...) çağrılacağı DERLEME anında, argüman tipine göre.
    static void yaz(int x)    { System.out.println("int sürümü: " + x); }
    static void yaz(double x) { System.out.println("double sürümü: " + x); }
    static void yaz(String x) { System.out.println("String sürümü: " + x); }

    public static void main(String[] args) {
        // Derleyici, argümanın STATİK (derleme-zamanı) tipine bakarak sürümü seçer.
        yaz(5);        // int
        yaz(5.0);      // double
        yaz("beş");    // String

        // static metotlar da REFERANS tipine göre çözülür (statik bağlama):
        Ata a = new Cocuk();
        a.statikMetot();   // "Ata.statikMetot" — referans tipi Ata olduğu için (DİNAMİK değil!)

        System.out.println("""

                --- Statik bağlama (early binding) ---
                Çağrılacak metot DERLEME anında, REFERANS tipine göre belirlenir. Şunlar statik bağlanır:
                  - overloaded metotlar (argüman tipine göre seçim),
                  - static metotlar,
                  - private metotlar,
                  - final metotlar.
                Bunlar polimorfik DEĞİLDİR; alt sınıf 'ezemez' (override değil, gizleme/farklı metot).""");
    }
}

class Ata {
    static void statikMetot() { System.out.println("Ata.statikMetot"); }
}
class Cocuk extends Ata {
    static void statikMetot() { System.out.println("Cocuk.statikMetot"); } // gizleme (hiding), override değil
}
