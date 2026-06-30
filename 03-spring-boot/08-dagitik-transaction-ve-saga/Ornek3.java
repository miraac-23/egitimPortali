// Ornek3: SAGA PATTERN — Choreography (koreografi, olay tabanlı).
// Merkezi orkestratör YOKTUR; her servis olayları dinler, işini yapar ve YENİ olay yayınlar.
// Hata olunca telafi de olaylarla tetiklenir. (Tek JVM'de basit bir olay veriyolu simülasyonu.)
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Ornek3 {

    // --- Olaylar (servisler arası mesajlar) ---
    sealed interface Olay permits SiparisOlusturuldu, StokRezerveEdildi, OdemeAlindi, OdemeReddedildi, Gonderildi {}
    record SiparisOlusturuldu(String urun) implements Olay {}
    record StokRezerveEdildi(String urun) implements Olay {}
    record OdemeAlindi(String urun) implements Olay {}
    record OdemeReddedildi(String urun, String sebep) implements Olay {}
    record Gonderildi(String urun) implements Olay {}

    // --- Basit olay veriyolu (gerçekte Kafka/RabbitMQ) ---
    static class OlayVeriyolu {
        private final Map<Class<?>, List<Consumer<Olay>>> aboneler = new HashMap<>();
        <T extends Olay> void abone(Class<T> tip, Consumer<Olay> handler) {
            aboneler.computeIfAbsent(tip, k -> new ArrayList<>()).add(handler);
        }
        void yayinla(Olay olay) {
            System.out.println("  >> olay: " + olay.getClass().getSimpleName()
                    + (olay instanceof OdemeReddedildi r ? " (" + r.sebep() + ")" : ""));
            aboneler.getOrDefault(olay.getClass(), List.of()).forEach(h -> h.accept(olay));
        }
    }

    public static void main(String[] args) {
        // odemeBasarili=true -> mutlu yol; false -> ödeme reddedilir, telafi tetiklenir.
        System.out.println("=== Senaryo 1: ödeme başarılı (mutlu yol) ===");
        akisiKur(true).yayinla(new SiparisOlusturuldu("Klavye"));

        System.out.println("\n=== Senaryo 2: ödeme reddedildi -> telafi (stok iadesi) ===");
        akisiKur(false).yayinla(new SiparisOlusturuldu("Mouse"));

        System.out.println("""

                --- Saga: Choreography ---
                Merkezi koordinatör YOK; her servis ilgili olayı dinleyip işini yapar ve yeni olay yayınlar.
                Akış: SiparisOlusturuldu -> StokRezerveEdildi -> OdemeAlindi -> Gonderildi.
                Hata: OdemeReddedildi olayını Stok servisi dinler ve rezervasyonu geri alır (telafi).
                Artı: gevşek bağlılık, tek hata noktası yok, kolay ölçeklenir.
                Eksi: akışı uçtan uca izlemek zorlaşır (olaylar dağınık). Bu yüzden 'outbox pattern' ve
                izleme (distributed tracing) kullanılır.""");
    }

    // Servisleri olaylara abone ederek koreografiyi kurar.
    static OlayVeriyolu akisiKur(boolean odemeBasarili) {
        OlayVeriyolu bus = new OlayVeriyolu();

        // Stok servisi: sipariş gelince rezerve eder; ödeme reddedilirse rezervasyonu geri alır.
        bus.abone(SiparisOlusturuldu.class, o -> {
            String urun = ((SiparisOlusturuldu) o).urun();
            System.out.println("     [Stok] " + urun + " rezerve edildi");
            bus.yayinla(new StokRezerveEdildi(urun));
        });
        bus.abone(OdemeReddedildi.class, o -> {
            OdemeReddedildi r = (OdemeReddedildi) o;
            System.out.println("     [Stok] TELAFİ: " + r.urun() + " rezervasyonu iptal edildi (stok iadesi)");
        });

        // Ödeme servisi: stok rezerve olunca ücreti tahsil etmeye çalışır.
        bus.abone(StokRezerveEdildi.class, o -> {
            String urun = ((StokRezerveEdildi) o).urun();
            if (odemeBasarili) {
                System.out.println("     [Ödeme] " + urun + " için ücret tahsil edildi");
                bus.yayinla(new OdemeAlindi(urun));
            } else {
                System.out.println("     [Ödeme] " + urun + " için tahsilat BAŞARISIZ");
                bus.yayinla(new OdemeReddedildi(urun, "yetersiz bakiye"));
            }
        });

        // Kargo servisi: ödeme alınınca gönderir.
        bus.abone(OdemeAlindi.class, o -> {
            String urun = ((OdemeAlindi) o).urun();
            System.out.println("     [Kargo] " + urun + " gönderildi");
            bus.yayinla(new Gonderildi(urun));
        });

        return bus;
    }
}
