// Ornek2: StringBuilder ve performans karşılaştırması.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // StringBuilder DEĞİŞTİRİLEBİLİR bir metin tamponudur.
        StringBuilder sb = new StringBuilder();
        sb.append("Java");
        sb.append(" ").append("çok").append(" güçlü");
        sb.insert(0, ">> ");          // başa ekle
        System.out.println("append/insert: " + sb);
        System.out.println("reverse()    : " + new StringBuilder("tacik").reverse());
        System.out.println("uzunluk      : " + sb.length());

        // --- Performans: döngüde String birleştirme vs StringBuilder ---
        // String IMMUTABLE olduğu için her + işlemi YENİ bir nesne yaratır; bu, çok
        // sayıda birleştirmede yavaştır. StringBuilder tek tampon kullanır.
        int tekrar = 50_000;

        long t1 = System.nanoTime();
        String duz = "";
        for (int i = 0; i < tekrar; i++) {
            duz += i; // her adımda yeni String -> yavaş
        }
        long t2 = System.nanoTime();

        StringBuilder hizli = new StringBuilder();
        for (int i = 0; i < tekrar; i++) {
            hizli.append(i); // aynı tamponu büyütür -> hızlı
        }
        long t3 = System.nanoTime();

        System.out.println("\n" + tekrar + " birleştirme için süre:");
        System.out.printf("  String  +=        : %5d ms%n", (t2 - t1) / 1_000_000);
        System.out.printf("  StringBuilder     : %5d ms%n", (t3 - t2) / 1_000_000);
        System.out.println("Sonuç uzunlukları eşit mi? " + (duz.length() == hizli.length()));
        System.out.println("\nKural: döngüde çok sayıda birleştirme yapıyorsan StringBuilder kullan.");
    }
}
