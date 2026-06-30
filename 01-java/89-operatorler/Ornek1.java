// Ornek1: Aritmetik, atama ve ilişkisel operatörler.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // ARİTMETİK: + - * / %
        int a = 17, b = 5;
        System.out.println("17 + 5 = " + (a + b));
        System.out.println("17 - 5 = " + (a - b));
        System.out.println("17 * 5 = " + (a * b));
        System.out.println("17 / 5 = " + (a / b) + "  (int bölme -> 3, ondalık atılır)");
        System.out.println("17 % 5 = " + (a % b) + "  (kalan / mod)");

        // ARTIRMA/AZALTMA: ++ -- (önek vs sonek FARKI)
        int x = 5;
        System.out.println("\nx=5; x++ -> " + (x++) + " (önce kullan, sonra artır), şimdi x=" + x);
        int y = 5;
        System.out.println("y=5; ++y -> " + (++y) + " (önce artır, sonra kullan), şimdi y=" + y);

        // ATAMA operatörleri: = += -= *= /= %=
        int sayac = 10;
        sayac += 5;  System.out.println("\n10 += 5 -> " + sayac);
        sayac *= 2;  System.out.println("    *= 2 -> " + sayac);
        sayac %= 7;  System.out.println("    %= 7 -> " + sayac);

        // İLİŞKİSEL (karşılaştırma): == != < > <= >=  (sonuç boolean)
        System.out.println("\n7 > 3  : " + (7 > 3));
        System.out.println("7 == 7 : " + (7 == 7));
        System.out.println("7 != 3 : " + (7 != 3));

        System.out.println("""

                --- Aritmetik, atama, ilişkisel ---
                Aritmetik: + - * / % (mod=kalan). int/int bölme ondalığı ATAR (17/5=3).
                ++/-- : önek (++x) önce artırır; sonek (x++) önce kullanır sonra artırır.
                Atama: =, ve kısa hali += -= *= /= %= (a += b  ==  a = a + b).
                İlişkisel: == != < > <= >= -> boolean döndürür (koşullarda kullanılır).
                NOT: nesne eşitliğinde == referansa bakar; içerik için .equals() (topic 76).""");
    }
}
