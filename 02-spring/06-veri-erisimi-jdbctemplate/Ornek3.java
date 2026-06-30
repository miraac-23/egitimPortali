// Ornek3: Exception translation — Spring, teknik SQLException'ları tutarlı DataAccessException'a çevirir.
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Ornek3 {

    public static void main(String[] args) {
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:ex;DB_CLOSE_DELAY=-1", "sa", "");
        ds.setDriverClassName("org.h2.Driver");
        JdbcTemplate jdbc = new JdbcTemplate(ds);

        jdbc.execute("CREATE TABLE kullanici (id INT PRIMARY KEY, eposta VARCHAR(80) UNIQUE)");
        jdbc.update("INSERT INTO kullanici VALUES (1, 'ada@site.com')");

        // --- Çift birincil anahtar -> DataIntegrityViolation/DuplicateKey ---
        // Ham JDBC'de bu, sürücüye özel bir SQLException olurdu (kod/numara ezbere bilinir).
        // Spring bunu ANLAMLI, taşınabilir bir istisnaya çevirir.
        try {
            jdbc.update("INSERT INTO kullanici VALUES (1, 'baska@site.com')"); // id=1 zaten var
        } catch (DuplicateKeyException e) {
            System.out.println("Yakalandı (DuplicateKeyException): birincil anahtar çakışması.");
        } catch (DataAccessException e) {
            System.out.println("Yakalandı (DataAccessException): " + e.getClass().getSimpleName());
        }

        // --- Sonuç bulunamadı -> EmptyResultDataAccessException ---
        try {
            jdbc.queryForObject("SELECT eposta FROM kullanici WHERE id = ?", String.class, 999);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Yakalandı (EmptyResultDataAccessException): kayıt yok.");
        }

        System.out.println("""

                --- Exception translation neden değerli? ---
                Ham JDBC'de hatalar 'checked' SQLException'dır ve veritabanına göre kod/mesaj DEĞİŞİR.
                Spring bunları tek bir 'unchecked' DataAccessException hiyerarşisine çevirir:
                  DuplicateKeyException, DataIntegrityViolationException, EmptyResultDataAccessException...
                Böylece iş kodun, hangi veritabanını kullandığından bağımsız ve temiz kalır.
                (@Repository ile işaretli bean'lerde bu çeviri otomatik devreye girer.)""");
    }
}
