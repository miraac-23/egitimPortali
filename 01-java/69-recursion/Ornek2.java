// Ornek2: Özyineleme ile ağaç gezme + StackOverflow ve iteratif alternatif.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.List;

public class Ornek2 {

    // İç içe (ağaç) yapı: bir klasör, alt klasörler ve dosyalar içerir.
    static class Klasor {
        String ad;
        long dosyaBoyutu;
        List<Klasor> altKlasorler = new ArrayList<>();
        Klasor(String ad, long boyut) { this.ad = ad; this.dosyaBoyutu = boyut; }
        Klasor ekle(Klasor k) { altKlasorler.add(k); return this; }
    }

    // Özyinelemeli toplam: bu klasör + tüm alt klasörlerin boyutu.
    static long toplamBoyut(Klasor k) {
        long toplam = k.dosyaBoyutu;
        for (Klasor alt : k.altKlasorler) {
            toplam += toplamBoyut(alt);     // her alt klasör için kendini çağır
        }
        return toplam;
    }

    // Özyinelemeli yazdırma (girintiyle ağaç görünümü).
    static void yazdir(Klasor k, int derinlik) {
        System.out.println("  ".repeat(derinlik) + "📁 " + k.ad + " (" + k.dosyaBoyutu + ")");
        for (Klasor alt : k.altKlasorler) yazdir(alt, derinlik + 1);
    }

    public static void main(String[] args) {
        Klasor kok = new Klasor("proje", 0)
                .ekle(new Klasor("src", 100)
                        .ekle(new Klasor("main", 500))
                        .ekle(new Klasor("test", 300)))
                .ekle(new Klasor("docs", 200));

        System.out.println("Klasör ağacı:");
        yazdir(kok, 0);
        System.out.println("Toplam boyut: " + toplamBoyut(kok));

        // Derin özyineleme StackOverflowError'a yol açabilir; iteratif çözüm güvenlidir.
        System.out.println("\nDerin özyineleme riski:");
        try {
            sonsuzDerin(0);
        } catch (StackOverflowError e) {
            System.out.println("  StackOverflowError yakalandı! Çok derin özyineleme yığını taşırdı.");
        }
        // İteratif faktöriyel (özyinelemesiz) — yığın taşma riski yok:
        long f = 1; for (int i = 1; i <= 20; i++) f *= i;
        System.out.println("  iteratif 20! = " + f);

        System.out.println("""

                --- Ağaç gezme + özyineleme sınırları ---
                Özyineleme, iç içe (ağaç/grafik) yapılar için DOĞAL araçtır: her düğüm için kendini çağır.
                ANCAK her çağrı yığında (stack) yer kaplar; çok derin/sonsuz özyineleme StackOverflowError verir.
                Java tail-call optimizasyonu YAPMAZ; çok derin durumlarda İTERATİF çözüm (döngü+yığın) tercih edilir.
                Kural: özyineleme okunabilirlik kazandırır; derinlik kontrol edilemiyorsa iteratif yaz.""");
    }

    static void sonsuzDerin(int n) { sonsuzDerin(n + 1); } // taban durum YOK -> taşar
}
