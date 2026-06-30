// Ornek2: Çok boyutlu diziler ve deepToString/deepEquals.
// Çalıştırma: java Ornek2.java
import java.util.Arrays;

public class Ornek2 {

    public static void main(String[] args) {
        // 2 boyutlu dizi (matris): satır x sütun
        int[][] matris = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };

        // toString çok boyutluda işe YARAMAZ (iç dizilerin adresini basar); deepToString gerekir.
        System.out.println("toString    : " + Arrays.toString(matris));      // [[I@... çöp
        System.out.println("deepToString: " + Arrays.deepToString(matris));  // içerik

        // Erişim ve gezinme
        System.out.println("\nmatris[1][2] = " + matris[1][2]);
        long kosegen = 0;
        for (int i = 0; i < matris.length; i++) kosegen += matris[i][i];
        System.out.println("Köşegen toplamı: " + kosegen);

        // Çok boyutlu karşılaştırma: deepEquals
        int[][] m2 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        System.out.println("\nequals     : " + Arrays.equals(matris, m2));      // false (iç diziler farklı nesne)
        System.out.println("deepEquals : " + Arrays.deepEquals(matris, m2));    // true (içerik aynı)

        // Düzensiz (jagged) dizi: satırlar farklı uzunlukta olabilir
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{ 1 };
        jagged[1] = new int[]{ 1, 2 };
        jagged[2] = new int[]{ 1, 2, 3 };
        System.out.println("\nJagged: " + Arrays.deepToString(jagged));

        System.out.println("""

                --- Çok boyutlu diziler ---
                Java'da çok boyutlu dizi = "dizilerin dizisi" (int[][] -> her eleman bir int[]).
                Bu yüzden satırlar farklı uzunlukta olabilir (jagged/düzensiz dizi).
                deepToString/deepEquals: iç içe dizileri DOĞRU yazdırır/karşılaştırır (toString/equals yetmez).
                Büyük dizilerde Arrays.parallelSort çok çekirdekte daha hızlı sıralayabilir.""");
    }
}
