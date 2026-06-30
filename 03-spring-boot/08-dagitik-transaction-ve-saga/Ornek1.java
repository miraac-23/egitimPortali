// Ornek1: Dağıtık transaction neden zor? — Two-Phase Commit (2PC/XA) simülasyonu.
// Birden çok servis/veritabanı tek bir "ya hepsi ya hiçbiri" işleminde nasıl anlaşır?
// (Tek JVM'de simülasyon; amaç protokolün mantığını ve ZAYIFLIĞINI göstermek.)
// Çalıştırma: java Ornek1.java
import java.util.List;

public class Ornek1 {

    // Bir katılımcı (örn. ayrı bir mikroservis/veritabanı).
    static class Katilimci {
        final String ad;
        final boolean hazirlanabilir; // bu katılımcı prepare aşamasında "evet" diyebilir mi?
        boolean kilitli = false;
        Katilimci(String ad, boolean hazirlanabilir) { this.ad = ad; this.hazirlanabilir = hazirlanabilir; }

        // FAZ 1: prepare — "commit'e hazır mısın?" Kaynakları KİLİTLER ve oy verir.
        boolean prepare() {
            if (hazirlanabilir) {
                kilitli = true; // kaynak kilitlendi; commit/abort gelene kadar BEKLER
                System.out.println("    [" + ad + "] prepare -> EVET (kaynak kilitlendi)");
                return true;
            }
            System.out.println("    [" + ad + "] prepare -> HAYIR (örn. yetersiz stok)");
            return false;
        }
        void commit() { kilitli = false; System.out.println("    [" + ad + "] COMMIT (kilit serbest)"); }
        void abort()  { kilitli = false; System.out.println("    [" + ad + "] ABORT (geri alındı)"); }
    }

    // Koordinatör (transaction manager): 2PC protokolünü yürütür.
    static boolean ikiFazliCommit(List<Katilimci> katilimcilar) {
        // FAZ 1: tüm katılımcılara prepare sor.
        System.out.println("  FAZ 1 (prepare): tüm katılımcılara soruluyor...");
        boolean hepsiHazir = true;
        for (Katilimci k : katilimcilar) {
            if (!k.prepare()) hepsiHazir = false;
        }
        // FAZ 2: oybirliği varsa COMMIT, yoksa hepsini ABORT.
        System.out.println("  FAZ 2: " + (hepsiHazir ? "oybirliği -> COMMIT" : "biri reddetti -> ABORT"));
        for (Katilimci k : katilimcilar) {
            if (hepsiHazir) k.commit(); else k.abort();
        }
        return hepsiHazir;
    }

    public static void main(String[] args) {
        System.out.println("=== Senaryo 1: hepsi hazır -> COMMIT ===");
        boolean s1 = ikiFazliCommit(List.of(
                new Katilimci("StokServisi", true),
                new Katilimci("OdemeServisi", true),
                new Katilimci("KargoServisi", true)));
        System.out.println("  Sonuç: " + (s1 ? "BAŞARILI (tümü commit)" : "iptal"));

        System.out.println("\n=== Senaryo 2: biri hazır değil -> tümü ABORT ===");
        boolean s2 = ikiFazliCommit(List.of(
                new Katilimci("StokServisi", false),  // stok yetersiz -> prepare HAYIR
                new Katilimci("OdemeServisi", true),
                new Katilimci("KargoServisi", true)));
        System.out.println("  Sonuç: " + (s2 ? "başarılı" : "İPTAL (tümü geri alındı)"));

        System.out.println("""

                --- 2PC (XA) neden mikroservislerde tercih edilmez? ---
                + Güçlü tutarlılık (atomiklik) sağlar.
                - BLOKLAMA: katılımcılar prepare ile commit arasında kaynakları KİLİTLER; yavaş servis hepsini bekletir.
                - Koordinatör çökerse katılımcılar belirsizlikte kalır (kilitli, kararsız).
                - Tüm servislerin XA desteği + sıkı bağ gerekir; ölçeklenmez, dayanıklılığı düşürür.
                Bu yüzden modern dağıtık sistemler 2PC yerine SAGA + 'eventual consistency' kullanır (Örnek 2-3).""");
    }
}
