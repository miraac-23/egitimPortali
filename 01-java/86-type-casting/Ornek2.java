// Ornek2: Referans (nesne) dönüşümleri — upcasting, downcasting, instanceof.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    static class Hayvan { String ses() { return "..."; } }
    static class Kopek extends Hayvan { @Override String ses() { return "Hav"; } String getir() { return "topu getirdi"; } }
    static class Kedi extends Hayvan { @Override String ses() { return "Miyav"; } }

    public static void main(String[] args) {
        // UPCASTING: alt tip -> üst tip. OTOMATİK ve her zaman güvenli.
        Hayvan h = new Kopek();          // Kopek -> Hayvan (implicit)
        System.out.println("Upcasting: " + h.ses() + " (gerçek tip Kopek -> dinamik bağlama)");

        // DOWNCASTING: üst tip -> alt tip. AÇIK cast gerekir; YANLIŞSA ClassCastException.
        if (h instanceof Kopek) {
            Kopek k = (Kopek) h;         // güvenli (gerçekten Kopek)
            System.out.println("Downcasting: " + k.getir());
        }

        // Yanlış downcast -> ClassCastException (çalışma zamanı hatası)
        Hayvan kedi = new Kedi();
        try {
            Kopek yanlis = (Kopek) kedi; // Kedi, Kopek DEĞİL
            System.out.println(yanlis);
        } catch (ClassCastException e) {
            System.out.println("\nYanlış downcast -> ClassCastException (Kedi, Kopek değil)");
        }

        // MODERN: pattern matching for instanceof (kontrol + cast tek adımda)
        List<Hayvan> hayvanlar = List.of(new Kopek(), new Kedi(), new Kopek());
        for (Hayvan a : hayvanlar) {
            if (a instanceof Kopek kopek) {   // kontrol + güvenli cast + bağlama
                System.out.println("Köpek özel: " + kopek.getir());
            } else {
                System.out.println("Diğer hayvan sesi: " + a.ses());
            }
        }

        System.out.println("""

                --- Referans (nesne) dönüşümleri ---
                Upcasting (alt -> üst): otomatik, her zaman güvenli (Kopek bir Hayvandır).
                Downcasting (üst -> alt): açık cast '(Tip)' gerekir; nesne gerçekten o tip DEĞİLSE ClassCastException.
                GÜVENLİ yol: önce 'instanceof' ile kontrol et. EN İYİSİ: pattern matching 'if (x instanceof Tip t)'
                  -> kontrol + cast + değişken bağlama tek adımda (Java 16+).
                Not: birbiriyle ilgisiz tipler arasında cast DERLENMEZ (örn. String'i Integer'a cast edemezsin).""");
    }
}
