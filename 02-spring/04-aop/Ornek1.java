// Ornek1: PROBLEM — kesişen ilgiler (cross-cutting concerns) her metoda elle serpiştirilmiş.
// Loglama ve süre ölçümü, asıl iş mantığının arasına karışmış; her metotta TEKRARLANIYOR.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        var servis = new SiparisServisi();
        servis.siparisOlustur("Klavye");
        servis.siparisIptal(42);

        System.out.println("""

                --- Sorun ---
                Loglama ve süre ölçümü kodu HER metoda elle yazıldı:
                  - aynı kod tekrar tekrar kopyalandı (DRY ihlali),
                  - asıl iş mantığı, log satırlarının arasında kayboldu,
                  - bir gün log formatını değiştirmek istesen, HER metoda dokunman gerekir.
                Loglama, güvenlik, transaction gibi 'kesişen ilgiler' iş mantığından AYRILMALI -> AOP (Örnek 2).""");
    }
}

class SiparisServisi {

    void siparisOlustur(String urun) {
        long t0 = System.currentTimeMillis();           // <-- ölçüm (kesişen ilgi)
        System.out.println("[LOG] siparisOlustur basladi: " + urun); // <-- loglama (kesişen ilgi)

        // ----- ASIL İŞ MANTIĞI (tek satır) -----
        System.out.println("   -> sipariş kaydedildi: " + urun);
        // ---------------------------------------

        System.out.println("[LOG] siparisOlustur bitti (" + (System.currentTimeMillis() - t0) + " ms)");
    }

    void siparisIptal(int id) {
        long t0 = System.currentTimeMillis();           // <-- yine ölçüm
        System.out.println("[LOG] siparisIptal basladi: #" + id); // <-- yine loglama

        // ----- ASIL İŞ MANTIĞI -----
        System.out.println("   -> sipariş iptal edildi: #" + id);
        // ---------------------------

        System.out.println("[LOG] siparisIptal bitti (" + (System.currentTimeMillis() - t0) + " ms)");
    }
}
