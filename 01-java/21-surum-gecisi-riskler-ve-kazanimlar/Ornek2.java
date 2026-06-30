// Ornek2: Geçişte karşılaşılan RİSK — eski/güvensiz API'ler ve modern, güvenli karşılıkları.
// Klasik örnek: SimpleDateFormat thread-safe DEĞİLDİR; java.time değişmez ve güvenlidir.
// Çalıştırma: java Ornek2.java
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Ornek2 {

    static final String[] TARIHLER = {
            "2020-01-15", "1999-12-31", "2010-06-20", "2024-03-08",
            "1985-11-02", "2001-09-11", "2015-07-19", "1970-01-01"
    };

    public static void main(String[] args) throws Exception {
        System.out.println("=== RİSK: paylaşımlı SimpleDateFormat (thread-safe değil) ===");
        anomaliTesti();

        System.out.println("\n=== Diğer eski -> modern karşılıklar ===");

        // 1) new Integer(...) DEPRECATED (kaldırılmaya işaretli) -> Integer.valueOf kullan.
        @SuppressWarnings("removal")
        Integer eski = new Integer(42);
        System.out.println("new Integer(42) (deprecated) -> Integer.valueOf(42) = " + Integer.valueOf(42)
                + " | eşit mi: " + eski.equals(42));

        // 2) Date + SimpleDateFormat yerine -> java.time (değişmez, okunaklı, güvenli).
        String eskiStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date(0L));
        String modernStr = LocalDate.of(1970, 1, 1).format(DateTimeFormatter.ISO_DATE);
        System.out.println("SimpleDateFormat: " + eskiStr + "  |  java.time: " + modernStr);

        // 3) Collectors.toList() yerine -> Stream.toList() (Java 16+, daha kısa).
        var liste = java.util.stream.Stream.of(3, 1, 2).sorted().toList();
        System.out.println("Stream.toList() (16+): " + liste);

        System.out.println("\nKural: Geçiş öncesi derleyici uyarılarını (deprecation) ciddiye al;");
        System.out.println("kaldırılan API'ler bir sonraki sürümde kodu DERLENEMEZ hale getirebilir.");
    }

    // Tek bir SimpleDateFormat'ı çok sayıda thread'de PARSE için paylaşmak iç durumu bozar.
    static void anomaliTesti() throws ParseException, InterruptedException {
        // Referans (paylaşılmayan) parse ile beklenen değerleri hesapla.
        SimpleDateFormat ref = new SimpleDateFormat("yyyy-MM-dd");
        long[] beklenen = new long[TARIHLER.length];
        for (int i = 0; i < TARIHLER.length; i++) beklenen[i] = ref.parse(TARIHLER[i]).getTime();

        // 1) Güvensiz: TÜM thread'ler AYNI SimpleDateFormat'ı paylaşır.
        SimpleDateFormat paylasimli = new SimpleDateFormat("yyyy-MM-dd");
        AtomicInteger sdfAnomali = new AtomicInteger();
        calistir(t -> {
            for (int i = 0; i < 3000; i++) {
                try {
                    if (paylasimli.parse(TARIHLER[t]).getTime() != beklenen[t]) sdfAnomali.incrementAndGet();
                } catch (Exception e) {
                    sdfAnomali.incrementAndGet(); // güvensizlik exception da fırlatabilir
                }
            }
        });

        // 2) Güvenli: DateTimeFormatter değişmezdir, paylaşılması sorun değil.
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        AtomicInteger dtfAnomali = new AtomicInteger();
        calistir(t -> {
            for (int i = 0; i < 3000; i++) {
                LocalDate d = LocalDate.parse(TARIHLER[t], dtf);
                if (!d.toString().equals(TARIHLER[t])) dtfAnomali.incrementAndGet();
            }
        });

        System.out.println("Paylaşımlı SimpleDateFormat  -> " + sdfAnomali.get() + " anomali "
                + (sdfAnomali.get() > 0 ? "❗ (sessiz veri bozulması / hata!)" : "(bu sefer görünmedi ama GÜVENSİZ)"));
        System.out.println("Paylaşımlı DateTimeFormatter -> " + dtfAnomali.get() + " anomali (güvenli)");
    }

    // 8 thread'i çalıştırır; her thread'e kendi indeksini (t) verir.
    interface Is { void calistir(int t); }
    static void calistir(Is is) throws InterruptedException {
        Thread[] threadler = new Thread[TARIHLER.length];
        for (int i = 0; i < threadler.length; i++) {
            final int t = i;
            threadler[i] = new Thread(() -> is.calistir(t));
            threadler[i].start();
        }
        for (Thread x : threadler) x.join();
    }
}
