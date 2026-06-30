import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * ============================================================================
 *  YENİ TARİH/SAAT API (java.time) - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   java.time paketi; LocalDate, LocalTime, LocalDateTime, ZonedDateTime,
 *   Duration, Period, Instant gibi MODERN tarih/saat sınıfları getirir.
 *   (JSR-310, Joda-Time kütüphanesinden ilham alınarak tasarlandı.)
 *
 * NEDEN GELDİ? (Eski Date/Calendar'ın sorunları neydi?)
 *   1) DEĞİŞTİRİLEBİLİR (mutable): java.util.Date nesnesi sonradan değişebilir
 *      -> çoklu iş parçacığında (thread) güvensiz, beklenmedik hatalar.
 *   2) KAFA KARIŞTIRAN API: Calendar'da AY 0'DAN BAŞLAR (Ocak = 0!). Yıl
 *      1900 tabanlıydı. Çok sayıda hataya yol açtı.
 *   3) Zaman dilimi (timezone) yönetimi zayıf ve hataya açıktı.
 *   4) Tarih aritmetiği (3 gün ekle, iki tarih farkı) çok zahmetliydi.
 *
 *   java.time bunların hepsini çözer: DEĞİŞMEZ (immutable), thread-safe,
 *   akıcı (fluent) ve insan-okunabilir API.
 *
 * NE İŞE YARAR: Doğum günü hesaplama, fatura kesim tarihleri, rezervasyon
 *   süreleri, zaman dilimi dönüşümleri, yaş hesaplama vb.
 */
public class YeniTarihSaatApi {

    public static void main(String[] args) {

        System.out.println("=== 1. ESKİ Date/Calendar SORUNLARI ===\n");
        // ESKİ YÖNTEM: 24 Haziran 2026 olusturmak istiyoruz
        Calendar cal = Calendar.getInstance();
        cal.set(2026, 5, 24); // DİKKAT: ay 5 = HAZIRAN (0'dan basliyor!) -> kafa karistirici
        Date eskiTarih = cal.getTime();
        System.out.println("Eski Calendar (ay 0-tabanli!): " + eskiTarih);
        System.out.println("   -> 'cal.set(2026, 5, 24)' aslinda HAZIRAN demek, MAYIS degil!");

        System.out.println("\n=== 2. YENİ YÖNTEM: LocalDate ===\n");
        // YENİ YÖNTEM: ay numaralari INSAN MANTIGIYLA (Haziran = 6)
        LocalDate bugun = LocalDate.of(2026, 6, 24);
        System.out.println("LocalDate.of(2026, 6, 24): " + bugun + "  (Haziran = 6, net!)");
        System.out.println("Su anki tarih (now)      : " + LocalDate.now());
        System.out.println("Ay        : " + bugun.getMonth() + " (" + bugun.getMonthValue() + ")");
        System.out.println("Haftanin gunu: " + bugun.getDayOfWeek());
        System.out.println("Yilin kacinci gunu: " + bugun.getDayOfYear());
        System.out.println("Arti gecmis mi (leap year)? " + bugun.isLeapYear());

        System.out.println("\n=== 3. Tarih Aritmetiği (immutable - yeni nesne döner) ===\n");
        LocalDate gelecek = bugun.plusDays(10).plusMonths(2).minusYears(1);
        System.out.println("bugun + 10 gun + 2 ay - 1 yil = " + gelecek);
        System.out.println("Orijinal degismedi (immutable): " + bugun);

        System.out.println("\n=== 4. LocalTime ve LocalDateTime ===\n");
        LocalTime saat = LocalTime.of(14, 30, 0);
        System.out.println("Saat: " + saat);
        LocalDateTime randevu = LocalDateTime.of(2026, Month.JULY, 1, 9, 15);
        System.out.println("Randevu: " + randevu);

        System.out.println("\n=== 5. Period : İki TARİH arası fark (yıl/ay/gün) ===\n");
        LocalDate dogumGunu = LocalDate.of(1990, 3, 15);
        Period yas = Period.between(dogumGunu, bugun);
        System.out.printf("Yas: %d yil, %d ay, %d gun%n",
                yas.getYears(), yas.getMonths(), yas.getDays());
        long toplamGun = ChronoUnit.DAYS.between(dogumGunu, bugun);
        System.out.println("Dogumdan bu yana toplam gun: " + toplamGun);

        System.out.println("\n=== 6. Duration : İki ZAMAN arası fark (saat/dakika/sn) ===\n");
        LocalTime baslangic = LocalTime.of(9, 0);
        LocalTime bitis = LocalTime.of(17, 30);
        Duration mesai = Duration.between(baslangic, bitis);
        System.out.println("Mesai suresi: " + mesai.toHours() + " saat "
                + (mesai.toMinutes() % 60) + " dakika");

        System.out.println("\n=== 7. ZonedDateTime : Zaman Dilimi Yönetimi ===\n");
        ZonedDateTime istanbul = ZonedDateTime.now(ZoneId.of("Europe/Istanbul"));
        ZonedDateTime newYork = istanbul.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime tokyo = istanbul.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println("Istanbul: " + istanbul);
        System.out.println("New York: " + newYork);
        System.out.println("Tokyo   : " + tokyo);

        System.out.println("\n=== 8. Formatlama (DateTimeFormatter) ===\n");
        DateTimeFormatter trFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        System.out.println("Formatli: " + randevu.format(trFormat));
        // Metinden tarihe çevirme (parse)
        LocalDate parsed = LocalDate.parse("2026-12-31");
        System.out.println("Parse edildi: " + parsed);

        System.out.println("\n=== 9. Instant : Makine zamanı (epoch) ===\n");
        Instant simdi = Instant.now();
        System.out.println("Instant (UTC): " + simdi);
        System.out.println("Epoch saniye : " + simdi.getEpochSecond());

        System.out.println("\n=== 10. GERÇEK HAYAT: Fatura kesim ve vade ===\n");
        LocalDate faturaTarihi = LocalDate.of(2026, 6, 1);
        LocalDate sonOdeme = faturaTarihi.plusDays(30);
        System.out.println("Fatura tarihi : " + faturaTarihi);
        System.out.println("Son odeme     : " + sonOdeme);
        long kalanGun = ChronoUnit.DAYS.between(bugun, sonOdeme);
        System.out.println("Bugune gore kalan gun: " + kalanGun);

        // Bir sonraki Pazartesi (TemporalAdjuster mantığı)
        LocalDate sonrakiPazartesi = bugun;
        do {
            sonrakiPazartesi = sonrakiPazartesi.plusDays(1);
        } while (sonrakiPazartesi.getDayOfWeek() != DayOfWeek.MONDAY);
        System.out.println("Bir sonraki Pazartesi: " + sonrakiPazartesi);
    }
}
