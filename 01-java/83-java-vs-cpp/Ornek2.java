// Ornek2: Java vs C++ — nesne modeli farkları (tek kalıtım + interface, operatör aşırı yükleme yok).
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    // Java: TEK sınıf kalıtımı, ama ÇOKLU interface uygulanabilir (C++ çoklu kalıtıma izin verir).
    interface Yuzebilir { default String yuz() { return "yüzüyor"; } }
    interface Ucabilir { default String uc() { return "uçuyor"; } }

    static class Hayvan { String ad; Hayvan(String ad) { this.ad = ad; } }

    // Bir üst sınıf + birden çok interface (C++ tarzı çoklu kalıtımın güvenli karşılığı)
    static class Ordek extends Hayvan implements Yuzebilir, Ucabilir {
        Ordek() { super("Ördek"); }
    }

    public static void main(String[] args) {
        Ordek o = new Ordek();
        System.out.println(o.ad + ": " + o.yuz() + ", " + o.uc());

        // Java: operatör aşırı yükleme YOK. '+' yalnızca sayı ve String için (dilin sabiti).
        // İki nesneyi toplamak istersen METOT yazarsın (C++'ta operator+ tanımlanabilir).
        Para a = new Para(1050), b = new Para(500);
        Para toplam = a.ekle(b);          // a + b DEĞİL; .ekle(b)
        System.out.println("Para toplama (metotla): " + toplam);

        // Java: serbest fonksiyon yok; HER ŞEY bir sınıfın içinde (statik metot). C++'ta global fonksiyon olabilir.
        System.out.println("Statik yardımcı: " + topla(3, 4));

        System.out.println("""

                --- Java vs C++: nesne modeli ---
                - Kalıtım: Java TEK sınıf kalıtımı + çoklu INTERFACE (C++ çoklu sınıf kalıtımı -> 'elmas problemi').
                - Operatör aşırı yükleme: Java'da YOK (yalnızca + sayı/String). C++'ta operator+ vb. tanımlanabilir.
                - Her şey sınıf içinde: Java'da serbest (global) fonksiyon yok; statik metotlar kullanılır.
                - Header/cpp ayrımı yok, çoklu dosya derleme JVM'e bırakılmış; bellek modeli yönetilen (managed).
                Felsefe: Java sadelik+güvenlik+taşınabilirlik; C++ esneklik+düşük seviye kontrol+ham performans.""");
    }

    record Para(long kurus) {
        Para ekle(Para d) { return new Para(this.kurus + d.kurus); }
        @Override public String toString() { return (kurus / 100.0) + " TL"; }
    }
    static int topla(int a, int b) { return a + b; }
}
