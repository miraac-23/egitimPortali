// Ornek1: Metotlar — parametre, dönüş değeri, aşırı yükleme (overloading) ve varargs.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // Parametre alan ve değer döndüren bir metot.
    static int kare(int x) {
        return x * x;
    }

    // void: değer döndürmez, sadece iş yapar.
    static void selamla(String isim) {
        System.out.println("Merhaba, " + isim + "!");
    }

    // --- Metot aşırı yükleme (overloading) ---
    // Aynı isim, FARKLI parametre listesi. Derleyici çağrıya göre doğrusunu seçer.
    static int topla(int a, int b) {
        return a + b;
    }

    static double topla(double a, double b) {
        return a + b;
    }

    static int topla(int a, int b, int c) {
        return a + b + c;
    }

    // --- varargs: değişken sayıda parametre ---
    // int... bir dizi gibi davranır; 0 veya daha fazla argüman alabilir.
    static int toplamHepsi(int... sayilar) {
        int toplam = 0;
        for (int s : sayilar) {
            toplam += s;
        }
        return toplam;
    }

    public static void main(String[] args) {
        selamla("Deniz");
        System.out.println("kare(7) = " + kare(7));

        System.out.println("\n--- Overloading ---");
        System.out.println("topla(3, 4)       = " + topla(3, 4));
        System.out.println("topla(2.5, 1.5)   = " + topla(2.5, 1.5));
        System.out.println("topla(1, 2, 3)    = " + topla(1, 2, 3));

        System.out.println("\n--- varargs ---");
        System.out.println("toplamHepsi()           = " + toplamHepsi());
        System.out.println("toplamHepsi(5)          = " + toplamHepsi(5));
        System.out.println("toplamHepsi(1,2,3,4,5)  = " + toplamHepsi(1, 2, 3, 4, 5));
    }
}
