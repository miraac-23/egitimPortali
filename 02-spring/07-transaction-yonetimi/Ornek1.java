// Ornek1: PROBLEM — transaction olmadan, çok adımlı bir işlem yarıda kalırsa veri TUTARSIZ olur.
// Para transferi: borçlandırma başarılı, alacaklandırma başarısız -> para "buharlaşır".
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Ornek1 {

    public static void main(String[] args) {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:notx;DB_CLOSE_DELAY=-1", "sa", "");
        ds.setDriverClassName("org.h2.Driver");
        JdbcTemplate jdbc = new JdbcTemplate(ds);

        jdbc.execute("CREATE TABLE hesap (ad VARCHAR(20) PRIMARY KEY, bakiye INT)");
        jdbc.update("INSERT INTO hesap VALUES ('Ada', 1000), ('Burak', 500)");
        System.out.println("Başlangıç: " + bakiyeler(jdbc));

        // Transaction YOK: iki güncelleme birbirinden bağımsız çalışır.
        try {
            jdbc.update("UPDATE hesap SET bakiye = bakiye - 300 WHERE ad = 'Ada'");   // adım 1 (başarılı)
            if (true) throw new RuntimeException("ağ hatası! (alacaklandırmadan önce)"); // araya hata
            jdbc.update("UPDATE hesap SET bakiye = bakiye + 300 WHERE ad = 'Burak'");  // adım 2 (hiç çalışmadı)
        } catch (RuntimeException e) {
            System.out.println("Hata: " + e.getMessage());
        }

        System.out.println("Sonra    : " + bakiyeler(jdbc));
        System.out.println("""

                --- SORUN ---
                Ada'dan 300 düştü ama Burak'a eklenmedi -> 300 TL KAYBOLDU; veri tutarsız.
                İki güncelleme 'ya hepsi ya hiçbiri' olmalıydı. Bunu transaction sağlar (Örnek 2).""");
    }

    static String bakiyeler(JdbcTemplate jdbc) {
        Integer ada = jdbc.queryForObject("SELECT bakiye FROM hesap WHERE ad='Ada'", Integer.class);
        Integer burak = jdbc.queryForObject("SELECT bakiye FROM hesap WHERE ad='Burak'", Integer.class);
        return "Ada=" + ada + ", Burak=" + burak + " (toplam " + (ada + burak) + ")";
    }
}
