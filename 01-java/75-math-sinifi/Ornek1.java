// Ornek1: Math sınıfı — temel matematiksel işlemler.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Mutlak değer, min/max
        System.out.println("abs(-7)    = " + Math.abs(-7));
        System.out.println("max(3, 9)  = " + Math.max(3, 9));
        System.out.println("min(3, 9)  = " + Math.min(3, 9));

        // Üs ve kök
        System.out.println("pow(2, 10) = " + Math.pow(2, 10));   // 2^10 = 1024 (double)
        System.out.println("sqrt(144)  = " + Math.sqrt(144));
        System.out.println("cbrt(27)   = " + Math.cbrt(27));     // küp kök

        // Yuvarlama
        double d = 3.67;
        System.out.println("\nround(3.67) = " + Math.round(d)); // en yakın -> 4
        System.out.println("floor(3.67) = " + Math.floor(d));   // aşağı -> 3.0
        System.out.println("ceil(3.67)  = " + Math.ceil(d));    // yukarı -> 4.0
        System.out.println("round(3.49) = " + Math.round(3.49));// -> 3

        // Sabitler ve geometri
        System.out.printf("%nPI = %.5f, E = %.5f%n", Math.PI, Math.E);
        System.out.println("hypot(3, 4) = " + Math.hypot(3, 4)); // dik üçgen hipotenüsü -> 5.0
        double aci = Math.toRadians(30);
        System.out.printf("sin(30°) = %.2f%n", Math.sin(aci));

        // Rastgele sayı [0.0, 1.0)
        double r = Math.random();
        System.out.println("\nrandom() 0-1 arası mı? " + (r >= 0 && r < 1));
        int zar = (int) (Math.random() * 6) + 1; // 1-6
        System.out.println("zar (1-6): " + zar);

        System.out.println("""

                --- Math sınıfı ---
                Tüm metotları statiktir: Math.abs/max/min/pow/sqrt/cbrt, round/floor/ceil, sin/cos/tan,
                log/exp, hypot, toRadians/toDegrees; sabitler Math.PI ve Math.E.
                round: en yakın tam sayı; floor: aşağı; ceil: yukarı. random(): [0,1) double.
                NOT: pow ve çoğu metot DOUBLE döndürür; tam sayı sonucu için (int) cast veya Math.round.
                Daha iyi rastgelelik için java.util.Random / ThreadLocalRandom; finansal hassasiyet için BigDecimal.""");
    }
}
