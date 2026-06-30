// Ornek3: Davranışsal (behavioral) desenler — Strategy, Observer, Command.
// Gerçek senaryo: kargo ücreti stratejisi, stok takibi (bildirim), geri alınabilir komutlar.
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Ornek3 {

    public static void main(String[] args) {
        // --- STRATEGY: algoritmayı (kargo ücreti) çalışma zamanında değiştir ---
        double sepet = 800;
        System.out.println("Sepet: " + sepet + " TL");
        System.out.println("  Standart : " + KargoUcreti.STANDART.hesapla(sepet) + " TL");
        System.out.println("  Ekspres  : " + KargoUcreti.EKSPRES.hesapla(sepet) + " TL");
        // Strateji bir lambda da olabilir (functional interface):
        KargoStratejisi kampanya = tutar -> tutar >= 500 ? 0 : 40;
        System.out.println("  Kampanya (500+ ücretsiz): " + kampanya.hesapla(sepet) + " TL");

        // --- OBSERVER: stok değişince ilgili aboneler otomatik haberdar olsun ---
        System.out.println("\nObserver — stok takibi:");
        StokTakip stok = new StokTakip("Klavye");
        stok.aboneOl(haber -> System.out.println("  [müşteri] " + haber));
        stok.aboneOl(haber -> System.out.println("  [analitik] kaydedildi: " + haber));
        stok.stokGuncelle(0);   // tükendi -> herkese haber
        stok.stokGuncelle(15);  // geldi -> herkese haber

        // --- COMMAND: işlemleri nesneleştir; geri alma (undo) mümkün olsun ---
        System.out.println("\nCommand — geri alınabilir metin düzenleyici:");
        Editor editor = new Editor();
        KomutYoneticisi yonetici = new KomutYoneticisi();
        yonetici.calistir(new YazKomutu(editor, "Merhaba"));
        yonetici.calistir(new YazKomutu(editor, " dünya"));
        System.out.println("  Metin: '" + editor.metin() + "'");
        yonetici.geriAl();  // son yazmayı geri al
        System.out.println("  Geri al -> '" + editor.metin() + "'");
        yonetici.geriAl();
        System.out.println("  Geri al -> '" + editor.metin() + "'");
    }
}

// --- Strategy ---
@FunctionalInterface
interface KargoStratejisi { double hesapla(double sepetTutari); }
enum KargoUcreti implements KargoStratejisi {
    STANDART { public double hesapla(double t) { return t >= 1000 ? 0 : 30; } },
    EKSPRES  { public double hesapla(double t) { return 60; } }
}

// --- Observer ---
@FunctionalInterface
interface StokAbonesi { void bildirimAl(String haber); }
class StokTakip {
    private final String urun;
    private final List<StokAbonesi> aboneler = new ArrayList<>();
    StokTakip(String urun) { this.urun = urun; }
    void aboneOl(StokAbonesi a) { aboneler.add(a); }
    void stokGuncelle(int adet) {
        String haber = adet == 0 ? urun + " tükendi" : urun + " stoğa geldi (" + adet + " adet)";
        aboneler.forEach(a -> a.bildirimAl(haber));
    }
}

// --- Command ---
interface Komut { void calistir(); void geriAl(); }
class Editor {
    private final StringBuilder sb = new StringBuilder();
    void ekle(String s) { sb.append(s); }
    void sil(int uzunluk) { sb.delete(sb.length() - uzunluk, sb.length()); }
    String metin() { return sb.toString(); }
}
class YazKomutu implements Komut {
    private final Editor editor;
    private final String metin;
    YazKomutu(Editor editor, String metin) { this.editor = editor; this.metin = metin; }
    public void calistir() { editor.ekle(metin); }
    public void geriAl() { editor.sil(metin.length()); } // yazılanı geri al
}
class KomutYoneticisi {
    private final Deque<Komut> gecmis = new ArrayDeque<>();
    void calistir(Komut k) { k.calistir(); gecmis.push(k); }
    void geriAl() { if (!gecmis.isEmpty()) gecmis.pop().geriAl(); }
}
