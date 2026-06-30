// Ornek2: Biçimlendirme/ayrıştırma (DateTimeFormatter) ve zaman dilimleri (ZonedDateTime, Instant).
// Çalıştırma: java Ornek2.java
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Ornek2 {

    public static void main(String[] args) {
        LocalDateTime dt = LocalDateTime.of(2026, 6, 23, 14, 30, 0);

        // BİÇİMLENDİRME: tarih/saat -> okunabilir String (DateTimeFormatter).
        DateTimeFormatter trFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        System.out.println("Biçimli (tr): " + dt.format(trFormat));
        DateTimeFormatter uzun = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", new Locale("tr"));
        System.out.println("Uzun (tr)   : " + dt.format(uzun));
        System.out.println("ISO standart: " + dt.format(DateTimeFormatter.ISO_DATE_TIME));

        // AYRIŞTIRMA: String -> tarih/saat (parse). Format eşleşmeli.
        LocalDateTime ayristirilan = LocalDateTime.parse("15.08.2026 09:45", trFormat);
        System.out.println("\nAyrıştırılan: " + ayristirilan);

        // ZAMAN DİLİMLERİ: ZonedDateTime belirli bir bölgedeki zamanı temsil eder.
        ZonedDateTime istanbul = ZonedDateTime.of(dt, ZoneId.of("Europe/Istanbul"));
        ZonedDateTime newYork = istanbul.withZoneSameInstant(ZoneId.of("America/New_York"));
        System.out.println("\nİstanbul: " + istanbul.format(trFormat) + " " + istanbul.getZone());
        System.out.println("New York: " + newYork.format(trFormat) + " " + newYork.getZone()
                + "  (AYNI an, farklı yerel saat)");

        // Instant: zaman çizgisindeki bir an (UTC, epoch'tan beri). Makineler/zaman damgaları için.
        Instant an = istanbul.toInstant();
        System.out.println("\nInstant (UTC): " + an);
        System.out.println("Epoch saniye : " + an.getEpochSecond());

        System.out.println("""

                --- Biçimlendirme, ayrıştırma ve zaman dilimleri ---
                DateTimeFormatter: ofPattern(\"dd.MM.yyyy HH:mm\") ile biçimlendir (format) ve ayrıştır (parse).
                  Desen harfleri: yyyy yıl, MM ay(sayı), MMMM ay(ad), dd gün, HH saat(24), mm dk, EEEE gün adı.
                ZonedDateTime: belirli bir BÖLGEDEKİ tarih/saat (ZoneId). withZoneSameInstant: aynı anı başka bölgede göster.
                Instant: zaman çizgisindeki mutlak bir an (UTC, epoch tabanlı) — zaman damgaları/loglar için.
                Kural: depolama/iletişimde UTC (Instant) sakla; KULLANICIYA gösterirken yerel bölgeye çevir.""");
    }
}
