// Ornek2: Sayısal tuzaklar — kayan nokta hatası, tam sayı taşması ve çözümleri.
// Çalıştırma: java Ornek2.java
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Ornek2 {

    public static void main(String[] args) {
        // 1) KAYAN NOKTA HATASI: 0.1 + 0.2 != 0.3 (double ikili tabanda kesirleri tam tutamaz)
        double toplam = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + toplam + "  (0.3 BEKLENİRDİ!)");
        System.out.println("== 0.3 ? " + (toplam == 0.3));

        // Çözüm: para/hassas hesap için BigDecimal (String constructor ile!)
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        System.out.println("BigDecimal: " + a.add(b) + "  (tam 0.3)");
        BigDecimal fiyat = new BigDecimal("19.99").multiply(new BigDecimal("3"))
                .setScale(2, RoundingMode.HALF_UP);
        System.out.println("19.99 * 3 = " + fiyat);

        // 2) TAM SAYI TAŞMASI: int max'ı aşınca sessizce sarmalanır (negatife döner!)
        int max = Integer.MAX_VALUE;
        System.out.println("\nInteger.MAX_VALUE = " + max);
        System.out.println("max + 1 = " + (max + 1) + "  (TAŞTI -> negatif!)");

        // Çözüm: Math.addExact taşmada exception atar (sessiz hata yerine)
        try {
            Math.addExact(max, 1);
        } catch (ArithmeticException e) {
            System.out.println("Math.addExact -> " + e.getMessage() + " (taşma yakalandı)");
        }
        // veya long kullan:
        System.out.println("long ile: " + ((long) max + 1));

        System.out.println("""

                --- Sayısal tuzaklar ---
                Kayan nokta: double/float ikili tabanda 0.1 gibi kesirleri TAM tutamaz -> yuvarlama hatası.
                  Para/finans hesabında BigDecimal kullan (üstelik new BigDecimal(\"0.1\") gibi STRING ile).
                Tam sayı taşması: int/long sınırı aşılınca SESSİZCE sarmalanır (negatife döner).
                  Math.addExact/multiplyExact taşmada exception atar; ya da daha geniş tip (long) kullan.
                Kural: eşitlik için double'ı '==' ile karşılaştırma; küçük bir tolerans (epsilon) kullan.""");
    }
}
