// Ornek3: Gerçekçi senaryo — List + Map ile basit öğrenci yönetimi.
// Çalıştırma: java Ornek3.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ornek3 {

    record Ogrenci(String ad, String bolum, double notOrt) {}

    public static void main(String[] args) {
        List<Ogrenci> ogrenciler = new ArrayList<>(List.of(
                new Ogrenci("Ada", "Bilgisayar", 3.6),
                new Ogrenci("Burak", "Elektrik", 2.9),
                new Ogrenci("Ceren", "Bilgisayar", 3.9),
                new Ogrenci("Deniz", "Elektrik", 3.2),
                new Ogrenci("Emir", "Bilgisayar", 2.5)
        ));

        // Not ortalamasına göre azalan sırada sırala (Comparator zinciri).
        ogrenciler.sort(Comparator.comparingDouble(Ogrenci::notOrt).reversed());
        System.out.println("Başarı sırası:");
        for (Ogrenci o : ogrenciler) {
            System.out.printf("  %-6s %-12s %.2f%n", o.ad(), o.bolum(), o.notOrt());
        }

        // Bölüme göre grupla: Map<bölüm, öğrenci listesi>
        Map<String, List<Ogrenci>> bolumeGore = new LinkedHashMap<>();
        for (Ogrenci o : ogrenciler) {
            bolumeGore.computeIfAbsent(o.bolum(), k -> new ArrayList<>()).add(o);
        }

        System.out.println("\nBölüm bazında ortalama:");
        for (var giris : bolumeGore.entrySet()) {
            double ort = giris.getValue().stream().mapToDouble(Ogrenci::notOrt).average().orElse(0);
            System.out.printf("  %-12s : %.2f  (%d öğrenci)%n",
                    giris.getKey(), ort, giris.getValue().size());
        }
    }
}
