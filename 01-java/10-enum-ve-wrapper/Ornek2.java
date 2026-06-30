// Ornek2: Davranışlı enum — alan, constructor ve metot içeren enum.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    // Enum'lar yalnızca sabit değil; alan, constructor ve metot da içerebilir.
    // Her sabit, kendi davranışını uygulayabilir.
    enum Islem {
        TOPLA("+") { public double uygula(double a, double b) { return a + b; } },
        CIKAR("-") { public double uygula(double a, double b) { return a - b; } },
        CARP("*")  { public double uygula(double a, double b) { return a * b; } },
        BOL("/")   { public double uygula(double a, double b) { return a / b; } };

        private final String sembol;

        // Enum constructor'ı private'tır; her sabit kendi sembolünü taşır.
        Islem(String sembol) { this.sembol = sembol; }

        public String sembol() { return sembol; }

        // Soyut enum metodu: her sabit kendi sürümünü verir.
        public abstract double uygula(double a, double b);
    }

    public static void main(String[] args) {
        double a = 12, b = 4;
        System.out.println("a = " + a + ", b = " + b + "\n");

        // Tüm işlemleri tek döngüde uygula.
        for (Islem islem : Islem.values()) {
            System.out.printf("%.1f %s %.1f = %.2f%n", a, islem.sembol(), b, islem.uygula(a, b));
        }
    }
}
