// Ornek2: JIT derleyici — "ısınma" (warmup) ve neden elle mikro-benchmark GÜVENİLMEZ.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    // Sıcak (hot) metot: çok çağrılınca JIT bunu yerel makine koduna derler.
    static long hesapla(int n) {
        long toplam = 0;
        for (int i = 0; i < n; i++) toplam += (i * 31L) ^ (i >> 2);
        return toplam;
    }

    public static void main(String[] args) {
        int N = 2_000_000;
        long kontrol = 0;

        // Aynı işi arka arkaya birkaç tur ölç. İLK turlar genelde yavaştır (kod yorumlanıyor,
        // JIT henüz derlememiş); sonraki turlar JIT derlemesi + önbellek ile genelde hızlanır.
        // ANCAK sayılar tura/makineye göre DEĞİŞİR ve gürültülüdür — asıl ders budur.
        System.out.println("Tur tur çalıştırma süreleri (µs):");
        long[] sureler = new long[8];
        for (int tur = 0; tur < sureler.length; tur++) {
            long t0 = System.nanoTime();
            kontrol += hesapla(N);
            sureler[tur] = (System.nanoTime() - t0) / 1_000;
            System.out.printf("  tur %d: ~%5d µs%n", tur + 1, sureler[tur]);
        }

        System.out.println("(kontrol toplamı kullanıldı: " + (kontrol != 0) + ")");
        System.out.printf("İlk tur ~%d µs, son tur ~%d µs%n", sureler[0], sureler[sureler.length - 1]);

        System.out.println("""

                --- JIT (Just-In-Time) derleyici ---
                JVM kodu önce BYTECODE olarak YORUMLAR; sık çalışan ("hot") metotları çalışma anında
                YERLEŞİK MAKİNE KODUNA derler (JIT) -> büyük hızlanma. Bu yüzden 'ısınma' (warmup) vardır:
                ilk turlar genelde yavaş, sonraki turlar hızlıdır.
                AMA yukarıdaki sayılara dikkat: tutarlı bir 'hep azalan' eğri GÖREMEYEBİLİRSİN!
                Çünkü elle ölçüm GÜRÜLTÜLÜDÜR: GC duraklamaları, OS zamanlaması, JIT'in farklı anlarda
                devreye girmesi, ölü-kod eleme ve inlining sonucu çarpıtır.
                İŞTE ASIL DERS: güvenilir performans ölçümü elle System.nanoTime ile YAPILMAZ.
                JMH (Java Microbenchmark Harness) kullan — warmup, çoklu iterasyon ve istatistik onunla doğru yapılır.""");
    }
}
