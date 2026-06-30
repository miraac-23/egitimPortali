// Ornek2: Değişkenler, veri tipleri, tip dönüşümü ve operatörler.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // --- İlkel (primitive) veri tipleri ---
        int yas = 30;                 // tam sayı (4 byte)
        long nufus = 85_000_000L;      // büyük tam sayı (8 byte), sonuna L
        double fiyat = 1999.99;        // ondalıklı (çift duyarlık)
        float oran = 0.5f;             // ondalıklı (tek duyarlık), sonuna f
        boolean aktifMi = true;        // mantıksal: true / false
        char harf = 'A';               // tek karakter, tek tırnak
        byte kucukSayi = 120;          // -128..127

        // String ilkel değildir; bir nesnedir ama günlük hayatta sık kullanılır.
        String isim = "Ayşe";

        System.out.println("isim=" + isim + ", yas=" + yas + ", nufus=" + nufus);
        System.out.println("fiyat=" + fiyat + ", oran=" + oran);
        System.out.println("aktifMi=" + aktifMi + ", harf=" + harf + ", kucukSayi=" + kucukSayi);

        // --- Tip dönüşümü (casting) ---
        // Küçük tipten büyüğe geçiş otomatiktir (genişletme).
        double yasOndalik = yas;       // int -> double otomatik
        // Büyükten küçüğe geçişte bilgi kaybı olabileceğinden açık cast gerekir.
        int yuvarlanmis = (int) fiyat; // double -> int, ondalık kısım atılır
        System.out.println("\nyasOndalik=" + yasOndalik + ", yuvarlanmis=" + yuvarlanmis);

        // --- Aritmetik operatörler ---
        int a = 17, b = 5;
        System.out.println("\n--- Aritmetik ---");
        System.out.println(a + " + " + b + " = " + (a + b));
        System.out.println(a + " - " + b + " = " + (a - b));
        System.out.println(a + " * " + b + " = " + (a * b));
        System.out.println(a + " / " + b + " = " + (a / b) + "  (tam sayı bölmesi)");
        System.out.println(a + " % " + b + " = " + (a % b) + "  (kalan)");
        System.out.println("Ondalıklı bölme: " + (a / (double) b));

        // --- Karşılaştırma operatörleri (sonuç boolean) ---
        System.out.println("\n--- Karşılaştırma ---");
        System.out.println("a > b ? " + (a > b));
        System.out.println("a == b ? " + (a == b));

        // --- Mantıksal operatörler ---
        boolean kosul1 = a > 10;
        boolean kosul2 = b < 3;
        System.out.println("\n--- Mantıksal ---");
        System.out.println("kosul1 && kosul2 = " + (kosul1 && kosul2)); // VE
        System.out.println("kosul1 || kosul2 = " + (kosul1 || kosul2)); // VEYA
        System.out.println("!kosul1 = " + (!kosul1));                   // DEĞİL
    }
}
