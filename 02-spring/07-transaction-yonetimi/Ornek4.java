// Ornek4: Propagation (yayılım) — REQUIRED vs REQUIRES_NEW.
// Dış transaction geri alınınca, iç işlem de geri alınır mı? Propagation'a bağlı.
// @Transactional proxy tabanlıdır; portal bu dosyayı derleyip çalıştırır.
package com.egitim.spring.tx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class Ornek4 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(Config.class);
        SiparisServisi siparis = ctx.getBean(SiparisServisi.class);
        JdbcTemplate jdbc = ctx.getBean(JdbcTemplate.class);

        // --- Senaryo A: denetim logu REQUIRES_NEW ile -> AYRI transaction ---
        // Sipariş başarısız olup geri alınsa bile, denetim logu KALIR (ayrı commit edildi).
        System.out.println("Senaryo A: log REQUIRES_NEW (ayrı transaction)");
        try { siparis.siparisVerLogluAyriTx("Klavye"); } catch (RuntimeException e) {
            System.out.println("  sipariş hata aldı -> geri alındı: " + e.getMessage());
        }
        System.out.println("  sipariş sayısı: " + say(jdbc, "siparis") + " (0 bekleniyor — rollback)");
        System.out.println("  denetim logu  : " + say(jdbc, "denetim") + " (1 bekleniyor — REQUIRES_NEW korudu)");

        // --- Senaryo B: denetim logu REQUIRED ile -> AYNI transaction ---
        // Sipariş geri alınınca log da geri alınır (ikisi tek transaction).
        System.out.println("\nSenaryo B: log REQUIRED (aynı transaction)");
        try { siparis.siparisVerLogluAyniTx("Mouse"); } catch (RuntimeException e) {
            System.out.println("  sipariş hata aldı -> geri alındı: " + e.getMessage());
        }
        System.out.println("  sipariş sayısı: " + say(jdbc, "siparis") + " (yine 0)");
        System.out.println("  denetim logu  : " + say(jdbc, "denetim") + " (hâlâ 1 — yeni log REQUIRED ile geri alındı)");

        ctx.close();
        System.out.println("""

                --- Propagation (yayılım) ---
                REQUIRED (varsayılan): Mevcut transaction varsa ona KATILIR; yoksa yeni açar.
                                        Dış rollback -> iç de geri alınır (hepsi tek transaction).
                REQUIRES_NEW         : Her zaman AYRI bir transaction açar (dışarıdakini askıya alır).
                                        Dış rollback'ten ETKİLENMEZ; bağımsız commit/rollback.
                Tipik kullanım: REQUIRES_NEW ile 'ne olursa olsun kalsın' denetim/log kayıtları.
                Diğer modlar: NESTED, SUPPORTS, MANDATORY, NEVER, NOT_SUPPORTED.""");
    }

    static int say(JdbcTemplate j, String tablo) {
        return j.queryForObject("SELECT COUNT(*) FROM " + tablo, Integer.class);
    }

    @Configuration
    @EnableTransactionManagement
    public static class Config {
        @Bean DataSource ds() {
            var ds = new DriverManagerDataSource("jdbc:h2:mem:prop;DB_CLOSE_DELAY=-1", "sa", "");
            ds.setDriverClassName("org.h2.Driver");
            return ds;
        }
        @Bean JdbcTemplate jdbcTemplate(DataSource ds) { return new JdbcTemplate(ds); }
        @Bean PlatformTransactionManager txm(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean DenetimServisi denetim(JdbcTemplate j) { return new DenetimServisi(j); }
        @Bean SiparisServisi siparis(JdbcTemplate j, DenetimServisi d) { return new SiparisServisi(j, d); }
    }

    // Denetim (audit) servisi — log kaydını farklı propagation'larla yazar.
    public static class DenetimServisi {
        private final JdbcTemplate jdbc;
        DenetimServisi(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
            jdbc.execute("CREATE TABLE denetim (mesaj VARCHAR(100))");
        }
        @Transactional(propagation = Propagation.REQUIRES_NEW) // AYRI transaction
        public void logAyriTx(String mesaj) { jdbc.update("INSERT INTO denetim VALUES (?)", mesaj); }

        @Transactional(propagation = Propagation.REQUIRED)     // AYNI transaction (varsayılan)
        public void logAyniTx(String mesaj) { jdbc.update("INSERT INTO denetim VALUES (?)", mesaj); }
    }

    public static class SiparisServisi {
        private final JdbcTemplate jdbc;
        private final DenetimServisi denetim;
        SiparisServisi(JdbcTemplate jdbc, DenetimServisi denetim) {
            this.jdbc = jdbc; this.denetim = denetim;
            jdbc.execute("CREATE TABLE siparis (urun VARCHAR(50))");
        }

        @Transactional
        public void siparisVerLogluAyriTx(String urun) {
            jdbc.update("INSERT INTO siparis VALUES (?)", urun);
            denetim.logAyriTx("sipariş denendi: " + urun); // REQUIRES_NEW -> ayrı commit
            throw new RuntimeException("ödeme reddedildi");  // dış transaction rollback
        }

        @Transactional
        public void siparisVerLogluAyniTx(String urun) {
            jdbc.update("INSERT INTO siparis VALUES (?)", urun);
            denetim.logAyniTx("sipariş denendi: " + urun);  // REQUIRED -> aynı transaction
            throw new RuntimeException("ödeme reddedildi");  // her şey rollback
        }
    }
}
