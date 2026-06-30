// Ornek1: java.time temelleri — LocalDate, LocalTime, LocalDateTime, Period, Duration.
// Çalıştırma: java Ornek1.java
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class Ornek1 {

    public static void main(String[] args) {
        // LocalDate: yalnızca tarih (saat yok). of(...) ile sabit, now() ile bugün.
        LocalDate tarih = LocalDate.of(2026, Month.JUNE, 23);
        System.out.println("Tarih: " + tarih + " | gün: " + tarih.getDayOfWeek());

        // Aritmetik: DEĞİŞMEZ (immutable) -> her işlem YENİ nesne döndürür.
        System.out.println("10 gün sonra : " + tarih.plusDays(10));
        System.out.println("2 ay önce    : " + tarih.minusMonths(2));
        System.out.println("Gelecek yıl  : " + tarih.plusYears(1));

        // LocalTime ve LocalDateTime
        LocalTime saat = LocalTime.of(14, 30, 0);
        LocalDateTime tam = LocalDateTime.of(tarih, saat);
        System.out.println("\nSaat: " + saat + " | Tam: " + tam);

        // Karşılaştırma
        LocalDate dun = tarih.minusDays(1);
        System.out.println("\ndün, bugünden önce mi? " + dun.isBefore(tarih));

        // Period: TARİH farkı (yıl/ay/gün). Duration: ZAMAN farkı (saat/dakika/saniye).
        LocalDate dogum = LocalDate.of(1995, 5, 20);
        Period yas = Period.between(dogum, tarih);
        System.out.println("\nYaş: " + yas.getYears() + " yıl " + yas.getMonths() + " ay " + yas.getDays() + " gün");
        System.out.println("Toplam gün (ChronoUnit): " + ChronoUnit.DAYS.between(dogum, tarih));

        Duration sure = Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 30));
        System.out.println("Mesai süresi: " + sure.toHours() + " saat " + (sure.toMinutes() % 60) + " dk");

        System.out.println("""

                --- java.time temelleri (Java 8+) ---
                LocalDate (tarih), LocalTime (saat), LocalDateTime (ikisi) — hepsi DEĞİŞMEZ ve thread-safe.
                of(...) sabit oluşturur; now() şu anı verir; plus/minus YENİ nesne döndürür (orijinali değişmez).
                Period: tarih farkı (yıl/ay/gün). Duration: zaman farkı (saat/dk/sn). ChronoUnit.between: tek birimde fark.
                Eski Date/Calendar'a göre: değişmez, açık, hatasız. (Date değişebilir ve thread-safe değildi.)""");
    }
}
