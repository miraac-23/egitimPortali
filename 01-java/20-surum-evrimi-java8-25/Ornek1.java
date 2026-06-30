// Ornek1: JAVA 8 DEVRİMİ — "öncesi vs sonrası" karşılaştırması.
// Aynı işleri Java 8 öncesi stil ve Java 8 stiliyle yan yana yazıp farkı görüyoruz.
// (İki stil de JDK 21'de çalışır; amaç değişimi göstermektir.)
// Çalıştırma: java Ornek1.java
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Ornek1 {

    record Urun(String ad, double fiyat) {}

    public static void main(String[] args) {
        List<Urun> urunler = List.of(
                new Urun("Klavye", 450), new Urun("Mouse", 250),
                new Urun("Monitör", 3200), new Urun("Kulaklık", 900));

        // ============ 1) Sıralama: anonim sınıf (eski) vs lambda (Java 8) ============
        List<Urun> eskiSirali = new ArrayList<>(urunler);
        eskiSirali.sort(new Comparator<Urun>() {           // ESKİ: anonim iç sınıf, 5 satır gürültü
            @Override public int compare(Urun a, Urun b) {
                return Double.compare(a.fiyat(), b.fiyat());
            }
        });
        List<Urun> yeniSirali = new ArrayList<>(urunler);
        yeniSirali.sort(Comparator.comparingDouble(Urun::fiyat)); // JAVA 8: tek satır lambda/method ref

        System.out.println("Sıralama sonucu aynı mı? "
                + eskiSirali.equals(yeniSirali) + "  (anonim sınıf vs lambda)");

        // ============ 2) Filtreleme+toplama: döngü (eski) vs Stream (Java 8) ============
        // ESKİ: elle döngü, geçici liste
        double eskiToplam = 0;
        for (Urun u : urunler) {
            if (u.fiyat() >= 500) eskiToplam += u.fiyat();
        }
        // JAVA 8: bildirimsel stream pipeline
        double yeniToplam = urunler.stream()
                .filter(u -> u.fiyat() >= 500)
                .mapToDouble(Urun::fiyat)
                .sum();
        System.out.println("500+ ürün cirosu (döngü vs stream): " + eskiToplam + " == " + yeniToplam);

        // İsim listesi: döngü vs stream+collect
        List<String> adlar = urunler.stream().map(Urun::ad).collect(Collectors.toList());
        System.out.println("Adlar (stream): " + adlar);

        // ============ 3) null kontrolü: elle (eski) vs Optional (Java 8) ============
        Urun bulunan = bul(urunler, "Mouse");
        String eskiAd = (bulunan != null) ? bulunan.ad() : "yok";              // ESKİ: elle null kontrol
        String yeniAd = Optional.ofNullable(bul(urunler, "Yok")).map(Urun::ad).orElse("yok"); // JAVA 8
        System.out.println("null güvenliği: eski=" + eskiAd + ", optional=" + yeniAd);

        // ============ 4) Tarih: Date/SimpleDateFormat (eski) vs java.time (Java 8) ============
        // ESKİ: Date + SimpleDateFormat (değişebilir, thread-safe değil)
        String eskiTarih = new SimpleDateFormat("dd.MM.yyyy").format(new Date(0L));
        // JAVA 8: java.time (değişmez, güvenli, okunaklı)
        String yeniTarih = LocalDate.of(1970, 1, 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        System.out.println("tarih (Date vs java.time): " + eskiTarih + " == " + yeniTarih);

        System.out.println("""

                --- Java 8 NE getirdi? (en kritik sürüm) ---
                Lambda + Stream + Optional + java.time + interface default metotları.
                Etki: anonim sınıf gürültüsü gitti, döngüler bildirimsel oldu, null ve tarih güvenli hale geldi.
                Bugün yazdığın modern Java'nın temeli bu sürümle atıldı.""");
    }

    static Urun bul(List<Urun> liste, String ad) {
        return liste.stream().filter(u -> u.ad().equals(ad)).findFirst().orElse(null);
    }
}
