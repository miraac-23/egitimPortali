// Ornek1: İlkel tip dönüşümleri — genişletme (implicit) ve daraltma (explicit + veri kaybı).
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // GENİŞLETME (widening): küçük tip -> büyük tip. OTOMATİK (veri kaybı yok).
        int i = 100;
        long l = i;          // int -> long (otomatik)
        double d = l;        // long -> double (otomatik)
        System.out.println("Genişletme: int " + i + " -> long " + l + " -> double " + d);

        // DARALTMA (narrowing): büyük tip -> küçük tip. AÇIK cast gerekir; VERİ KAYBI olabilir!
        double pi = 3.99;
        int kesik = (int) pi;       // ondalık ATILIR (yuvarlama değil!) -> 3
        System.out.println("\nDaraltma: (int) 3.99 = " + kesik + "  (ondalık atılır, yuvarlanmaz)");

        long buyuk = 300;
        byte b = (byte) buyuk;      // byte aralığı -128..127 -> taşar/sarmalanır
        System.out.println("(byte) 300 = " + b + "  (byte aralığı taştı -> sarmalandı)");

        // char <-> int: char aslında bir sayıdır (Unicode kodu).
        char harf = 'A';
        int kod = harf;             // char -> int otomatik (genişletme): 65
        char sonraki = (char) (kod + 1); // int -> char için cast gerekir: 'B'
        System.out.println("\n'A' -> int " + kod + ", (char)(65+1) -> '" + sonraki + "'");

        // Aritmetikte otomatik genişletme: int / int = int (ondalık kaybolur!)
        System.out.println("\n5 / 2 = " + (5 / 2) + "  (int bölme -> ondalık YOK)");
        System.out.println("5 / 2.0 = " + (5 / 2.0) + "  (biri double -> sonuç double)");
        System.out.println("(double) 5 / 2 = " + ((double) 5 / 2));

        System.out.println("""

                --- İlkel tip dönüşümleri ---
                Genişletme (widening): byte->short->int->long->float->double. OTOMATİK, güvenli.
                Daraltma (narrowing): ters yön. AÇIK cast '(tip)' gerekir; VERİ KAYBI/taşma olabilir.
                  (int)3.99 -> 3 (ondalık atılır, YUVARLANMAZ); (byte)300 -> taşar.
                char bir sayıdır (Unicode); char<->int dönüşümleri mümkün.
                TUZAK: int/int bölme ondalığı atar; ondalık için en az bir operand double olmalı.""");
    }
}
