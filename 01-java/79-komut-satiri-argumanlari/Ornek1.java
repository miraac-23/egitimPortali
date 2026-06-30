// Ornek1: Komut satırı argümanları — main(String[] args).
// (Portal argümansız çalıştırır; bu yüzden örnek bir args dizisiyle de gösteriyoruz.)
// Gerçekte: java Ornek1 ada 30 istanbul   -> args = ["ada","30","istanbul"]
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // args: programa komut satırından geçilen String dizisi (program adı DAHİL DEĞİL).
        System.out.println("Gerçek argüman sayısı: " + args.length);
        if (args.length == 0) {
            System.out.println("(Portal argümansız çalıştırır; örnek bir dizi ile devam ediyoruz.)");
        } else {
            for (int i = 0; i < args.length; i++) System.out.println("  args[" + i + "] = " + args[i]);
        }

        // Simüle edilmiş argümanlar:
        String[] ornekArgs = { "ada", "30", "istanbul" };
        System.out.println("\nÖrnek args = [ada, 30, istanbul]:");
        islet(ornekArgs);

        // Eksik argüman kontrolü (gerçek programlarda şart):
        System.out.println("\nEksik argümanla:");
        islet(new String[]{ "burak" });

        System.out.println("""

                --- Komut satırı argümanları ---
                main(String[] args): program çalıştırılırken verilen argümanlar bu diziye gelir.
                'java Program a b c' -> args = ["a","b","c"] (program adı dahil değildir, C'den farklı olarak).
                Argümanlar her zaman String'tir; sayı gerekiyorsa Integer.parseInt ile çevir.
                MUTLAKA uzunluk/format kontrolü yap (eksik argüman -> ArrayIndexOutOfBounds; hatalı sayı -> NumberFormatException).""");
    }

    static void islet(String[] args) {
        if (args.length < 3) {
            System.out.println("  Eksik argüman! Beklenen: <ad> <yas> <sehir> (verilen: " + args.length + ")");
            return;
        }
        String ad = args[0];
        int yas = Integer.parseInt(args[1]); // String -> int
        String sehir = args[2];
        System.out.println("  " + ad + ", " + yas + " yaşında, " + sehir + "'de.");
    }
}
