// Ornek3: Programatik transaction (TransactionTemplate) — kodla açık kontrol.
// @Transactional bildirimseldir; bazen koddan daha ince kontrol istenir: TransactionTemplate.
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

public class Ornek3 {

    public static void main(String[] args) {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:ptx;DB_CLOSE_DELAY=-1", "sa", "");
        ds.setDriverClassName("org.h2.Driver");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        jdbc.execute("CREATE TABLE stok (urun VARCHAR(20) PRIMARY KEY, adet INT)");
        jdbc.update("INSERT INTO stok VALUES ('Klavye', 10)");

        // TransactionTemplate: transaction sınırını KODLA çizeriz.
        var txTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));

        // 1) Başarılı işlem -> commit
        txTemplate.execute(status -> {
            jdbc.update("UPDATE stok SET adet = adet - 3 WHERE urun = 'Klavye'");
            return null;
        });
        System.out.println("Başarılı işlem sonrası stok: " + stok(jdbc)); // 7

        // 2) İş kuralı ihlali -> setRollbackOnly() ile elle geri al
        txTemplate.execute(status -> {
            jdbc.update("UPDATE stok SET adet = adet - 20 WHERE urun = 'Klavye'"); // negatif olurdu
            Integer adet = stok(jdbc);
            if (adet < 0) {
                System.out.println("(stok negatife düştü: " + adet + " -> rollback işaretlendi)");
                status.setRollbackOnly(); // bu transaction commit edilMEyecek
            }
            return null;
        });
        System.out.println("Rollback sonrası stok: " + stok(jdbc) + "  <- 7 (değişmedi)");

        // 3) Exception -> otomatik rollback
        try {
            txTemplate.execute(status -> {
                jdbc.update("UPDATE stok SET adet = adet - 2 WHERE urun = 'Klavye'");
                throw new RuntimeException("doğrulama hatası");
            });
        } catch (RuntimeException e) {
            System.out.println("Exception sonrası stok: " + stok(jdbc) + "  <- 7 (rollback)");
        }

        System.out.println("""

                --- Deklaratif (@Transactional) vs programatik (TransactionTemplate) ---
                @Transactional   : en yaygın; metoda anotasyon koyarsın, sınırı Spring yönetir (temiz).
                TransactionTemplate: transaction'ı koddan açıkça yönetmen gerektiğinde (ince kontrol).
                Ek kavramlar: PROPAGATION (iç içe transaction davranışı), ISOLATION (eşzamanlılık düzeyi),
                readOnly (salt-okunur optimizasyonu), rollbackFor (checked exception'larda rollback).""");
    }

    static Integer stok(JdbcTemplate jdbc) {
        return jdbc.queryForObject("SELECT adet FROM stok WHERE urun='Klavye'", Integer.class);
    }
}
