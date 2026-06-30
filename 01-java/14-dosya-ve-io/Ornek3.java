// Ornek3: Log dosyası yazma ve Files.lines + Stream ile analiz.
// Çalıştırma: java Ornek3.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ornek3 {

    public static void main(String[] args) throws IOException {
        Path log = Files.createTempFile("ornek-log-", ".log");

        // Basit bir log dosyası oluştur (seviye + mesaj).
        List<String> kayitlar = List.of(
                "INFO  uygulama başladı",
                "WARN  bellek %80",
                "INFO  istek alındı",
                "ERROR veritabanı bağlantısı koptu",
                "INFO  istek tamamlandı",
                "ERROR zaman aşımı",
                "WARN  yavaş sorgu"
        );
        Files.write(log, kayitlar);
        System.out.println("Log dosyası yazıldı (" + kayitlar.size() + " kayıt).\n");

        // Files.lines bir Stream<String> döndürür; try-with-resources ile kapatılmalı.
        // Seviyelere göre kaç kayıt var?
        try (Stream<String> satirlar = Files.lines(log)) {
            Map<String, Long> seviyeSayisi = satirlar
                    .map(s -> s.split("\\s+")[0])      // ilk kelime = seviye
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
            System.out.println("Seviye dağılımı: " + seviyeSayisi);
        }

        // Sadece ERROR satırlarını süz.
        try (Stream<String> satirlar = Files.lines(log)) {
            System.out.println("\nHata kayıtları:");
            satirlar.filter(s -> s.startsWith("ERROR"))
                    .forEach(s -> System.out.println("  " + s));
        }

        Files.deleteIfExists(log);
        System.out.println("\nGeçici log dosyası silindi.");
    }
}
