// Ornek2: Neden StringBuilder? — döngüde String birleştirme vs StringBuilder (ölçümle).
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        int N = 50_000;

        // KÖTÜ: döngüde "+=" -> her adımda YENİ String kopyalanır (O(n^2) toplam).
        long t1 = System.currentTimeMillis();
        String s = "";
        for (int i = 0; i < N; i++) s += i;     // her seferinde yeni nesne!
        long sure1 = System.currentTimeMillis() - t1;

        // İYİ: StringBuilder -> aynı tampona ekler (O(n) toplam).
        long t2 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) sb.append(i);
        String sonuc = sb.toString();
        long sure2 = System.currentTimeMillis() - t2;

        System.out.printf("Döngüde birleştirme (%d):%n", N);
        System.out.printf("  String += : ~%4d ms  (uzunluk %d)%n", sure1, s.length());
        System.out.printf("  StringBuilder: ~%4d ms  (uzunluk %d)%n", sure2, sonuc.length());
        System.out.println("  Kat fark ~ " + (sure2 == 0 ? ">>" : (sure1 / Math.max(1, sure2)) + "x"));

        System.out.println("""

                --- Neden döngüde StringBuilder? ---
                String değişmez; "s += i" her turda eski içeriği + yeniyi YENİ bir String'e kopyalar.
                N tur için bu O(n^2) iş demektir -> büyük N'de ciddi yavaşlık.
                StringBuilder aynı genişleyen tampona ekler -> O(n). Döngüde metin kuruyorsan ŞART.
                Not: TEK satırlık sabit birleştirmelerde (a + b + c) derleyici zaten StringBuilder kullanır;
                orada elle StringBuilder gerekmez. Asıl fark DÖNGÜLERDEDİR.

                StringBuffer: StringBuilder ile aynı API ama metotları senkronize (thread-safe).
                Tek thread'de StringBuilder daha hızlıdır; StringBuffer'ı yalnızca paylaşılan
                değiştirilebilir metin GERÇEKTEN gerektiğinde kullan (nadir).""");
    }
}
