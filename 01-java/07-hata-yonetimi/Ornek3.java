// Ornek3: try-with-resources, AutoCloseable ve multi-catch.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        // --- try-with-resources ---
        // Parantez içinde açılan kaynaklar, blok bitince (hata olsa bile)
        // OTOMATİK kapatılır. Kaynak, AutoCloseable'ı uygulamalıdır.
        System.out.println("--- Normal akış ---");
        try (Baglanti b = new Baglanti("veritabani")) {
            b.sorguCalistir("SELECT * FROM urun");
        }

        System.out.println("\n--- Hata olsa bile kapatılır ---");
        try (Baglanti b = new Baglanti("dosya")) {
            b.sorguCalistir("HATALI");
        } catch (RuntimeException e) {
            System.out.println("Yakalandı: " + e.getMessage());
        }

        // --- multi-catch: birden çok exception'ı tek blokta yakala ---
        System.out.println("\n--- multi-catch ---");
        for (String girdi : new String[]{"10", "abc"}) {
            try {
                int n = Integer.parseInt(girdi);
                System.out.println("100 / " + n + " = " + (100 / n));
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.println("'" + girdi + "' için hata: " + e.getClass().getSimpleName());
            }
        }
    }
}

// AutoCloseable: try-with-resources tarafından otomatik kapatılabilen kaynak.
class Baglanti implements AutoCloseable {
    private final String ad;

    Baglanti(String ad) {
        this.ad = ad;
        System.out.println("[açıldı] " + ad);
    }

    void sorguCalistir(String sorgu) {
        if (sorgu.equals("HATALI")) {
            throw new RuntimeException("sorgu çalıştırılamadı");
        }
        System.out.println("[sorgu]  " + sorgu);
    }

    @Override
    public void close() { // blok bitince otomatik çağrılır
        System.out.println("[kapandı] " + ad);
    }
}
