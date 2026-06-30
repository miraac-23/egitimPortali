// Ornek1: Functional interface ve lambda — anonim sınıftan lambda'ya.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // Functional interface: TEK soyut metodu olan interface.
    // @FunctionalInterface bunu derleyiciye doğrulattırır.
    @FunctionalInterface
    interface Hesap {
        int uygula(int a, int b);
    }

    static int islemYap(int a, int b, Hesap h) {
        return h.uygula(a, b);
    }

    public static void main(String[] args) {
        // ESKİ yol: anonim iç sınıf (uzun ve gürültülü).
        Hesap toplaAnonim = new Hesap() {
            @Override
            public int uygula(int a, int b) {
                return a + b;
            }
        };

        // YENİ yol: lambda (aynı şey, çok daha kısa).
        Hesap topla = (a, b) -> a + b;
        Hesap carp  = (a, b) -> a * b;
        Hesap fark  = (a, b) -> Math.abs(a - b);

        System.out.println("anonim toplama : " + islemYap(7, 3, toplaAnonim));
        System.out.println("lambda toplama : " + islemYap(7, 3, topla));
        System.out.println("lambda çarpma  : " + islemYap(7, 3, carp));
        System.out.println("lambda fark    : " + islemYap(7, 3, fark));

        // Lambda'yı doğrudan argüman olarak da geçebiliriz:
        System.out.println("doğrudan lambda: " + islemYap(10, 4, (a, b) -> a / b));
    }
}
