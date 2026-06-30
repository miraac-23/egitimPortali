// Ornek2: SAGA PATTERN — Orchestration (orkestrasyon).
// Dağıtık bir işlem, her biri kendi yerel transaction'ı olan adımlara bölünür.
// Bir adım başarısız olursa, tamamlanmış adımlar TERS sırada TELAFİ (compensate) edilir.
// (Tek JVM simülasyonu; gerçekte her adım ayrı mikroservis çağrısıdır.)
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.List;

public class Ornek2 {

    // Bir saga adımı: ileri işlem + telafi (geri alma) işlemi.
    interface SagaAdimi {
        String ad();
        void calistir();      // yerel transaction (ileri)
        void telafiEt();      // compensating transaction (geri al)
    }

    // Orkestratör: adımları sırayla çalıştırır; hata olursa tamamlananları TERS sırada telafi eder.
    static class Orkestrator {
        boolean calistir(List<SagaAdimi> adimlar) {
            List<SagaAdimi> tamamlanan = new ArrayList<>();
            try {
                for (SagaAdimi adim : adimlar) {
                    System.out.println("  -> " + adim.ad() + " çalışıyor...");
                    adim.calistir();
                    tamamlanan.add(adim);
                }
                System.out.println("  ✓ Saga başarıyla tamamlandı (tüm adımlar commit).");
                return true;
            } catch (RuntimeException hata) {
                System.out.println("  ✗ HATA: " + hata.getMessage() + " -> telafi başlıyor (ters sıra):");
                // Tamamlanan adımları SON yapılandan başlayarak geri al.
                for (int i = tamamlanan.size() - 1; i >= 0; i--) {
                    System.out.println("     telafi: " + tamamlanan.get(i).ad());
                    tamamlanan.get(i).telafiEt();
                }
                System.out.println("  Saga geri alındı (tutarlı duruma dönüldü).");
                return false;
            }
        }
    }

    // Sipariş sagası adımları: stok rezerve -> ödeme -> kargo.
    static SagaAdimi adim(String ad, boolean basarili, Runnable ileri, Runnable telafi) {
        return new SagaAdimi() {
            public String ad() { return ad; }
            public void calistir() {
                ileri.run();
                if (!basarili) throw new RuntimeException(ad + " başarısız");
            }
            public void telafiEt() { telafi.run(); }
        };
    }

    public static void main(String[] args) {
        Orkestrator orkestrator = new Orkestrator();

        System.out.println("=== Senaryo 1: tüm adımlar başarılı ===");
        orkestrator.calistir(List.of(
                adim("StokRezervasyonu", true,
                        () -> System.out.println("       stok 1 adet düşüldü"),
                        () -> System.out.println("       stok iade edildi")),
                adim("OdemeAlma", true,
                        () -> System.out.println("       450 TL tahsil edildi"),
                        () -> System.out.println("       450 TL iade edildi")),
                adim("KargoPlanlama", true,
                        () -> System.out.println("       kargo oluşturuldu"),
                        () -> System.out.println("       kargo iptal edildi"))));

        System.out.println("\n=== Senaryo 2: KARGO adımı başarısız -> telafi ===");
        orkestrator.calistir(List.of(
                adim("StokRezervasyonu", true,
                        () -> System.out.println("       stok 1 adet düşüldü"),
                        () -> System.out.println("       stok iade edildi")),
                adim("OdemeAlma", true,
                        () -> System.out.println("       450 TL tahsil edildi"),
                        () -> System.out.println("       450 TL iade edildi")),
                adim("KargoPlanlama", false,  // <-- burada patlar
                        () -> System.out.println("       kargo deneniyor..."),
                        () -> System.out.println("       (telafi gerekmez, tamamlanmadı)"))));

        System.out.println("""

                --- Saga: Orchestration ---
                Merkezi bir ORKESTRATÖR adımları yönetir ve hata olunca telafileri tetikler.
                Her adım kendi YEREL transaction'ıdır; 2PC gibi dağıtık kilit YOKTUR.
                Telafi (compensation): yapılanı geri alan iş işlemi (ödeme iadesi, stok iadesi...).
                Sıra: ileri 1->2->3; hata olursa telafi 3<-2<-1 (ters).
                Artı: net akış, kolay izleme. Eksi: orkestratör merkezî bir bağımlılıktır.
                Sonuç 'eventual consistency'dir: anlık değil, kısa süre sonra tutarlı.""");
    }
}
