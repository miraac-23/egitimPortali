// Ornek1: PROBLEM (ham JDBC boilerplate) → ÇÖZÜM (Spring JdbcTemplate).
// Aynı sorguyu önce elle JDBC ile, sonra JdbcTemplate ile yazıp farkı görüyoruz.
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Ornek1 {

    public static void main(String[] args) throws Exception {
        // H2 bellek-içi veritabanı için bir DataSource (demo amaçlı basit sürücü kaynağı).
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:jt;DB_CLOSE_DELAY=-1", "sa", "");
        ds.setDriverClassName("org.h2.Driver");

        JdbcTemplate jdbc = new JdbcTemplate(ds);
        // Şema + veri (JdbcTemplate.execute/update tek satır).
        jdbc.execute("CREATE TABLE urun (id INT PRIMARY KEY, ad VARCHAR(50), fiyat DECIMAL(10,2))");
        jdbc.update("INSERT INTO urun VALUES (?, ?, ?)", 1, "Klavye", 450);
        jdbc.update("INSERT INTO urun VALUES (?, ?, ?)", 2, "Mouse", 250);

        // --- PROBLEM: ham JDBC ile okuma (bağlantı aç/kapat, ResultSet, try-catch...) ---
        System.out.println("Ham JDBC ile (uzun ve gürültülü):");
        hamJdbcOku(ds);

        // --- ÇÖZÜM: JdbcTemplate ile aynı iş (tek satır) ---
        System.out.println("\nJdbcTemplate ile (tek satır):");
        jdbc.query("SELECT id, ad, fiyat FROM urun ORDER BY id",
                (rs, satirNo) -> "  #" + rs.getInt("id") + " " + rs.getString("ad") + " - " + rs.getDouble("fiyat"))
            .forEach(System.out::println);

        // Tek değer okuma da çok kısa:
        Integer adet = jdbc.queryForObject("SELECT COUNT(*) FROM urun", Integer.class);
        System.out.println("\nToplam ürün: " + adet);

        System.out.println("""

                --- JdbcTemplate ne kazandırır? ---
                - Bağlantı açma/kapatma, Statement/ResultSet yönetimi, kaynak temizliği -> OTOMATİK.
                - try-catch-finally boilerplate'i ortadan kalkar.
                - SQLException -> Spring'in DataAccessException'ına (unchecked) çevrilir.
                Sen sadece SQL'i ve satır eşlemesini (RowMapper) yazarsın.""");
    }

    // Ham JDBC: her şeyi elle yönetmek zorundayız (kıyas için).
    static void hamJdbcOku(DataSource ds) throws Exception {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, ad, fiyat FROM urun ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("  #" + rs.getInt("id") + " " + rs.getString("ad") + " - " + rs.getDouble("fiyat"));
            }
        }
    }
}
