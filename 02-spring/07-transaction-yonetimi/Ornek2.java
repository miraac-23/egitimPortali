// Ornek2: ÇÖZÜM — @Transactional (deklaratif). İşlem "ya hepsi ya hiçbiri" olur.
// @Transactional proxy tabanlıdır; bu yüzden portal bu dosyayı derleyip çalıştırır.
package com.egitim.spring.tx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class Ornek2 {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(TxConfig.class);
        BankaServisi banka = ctx.getBean(BankaServisi.class);

        System.out.println("Başlangıç: " + banka.bakiyeler());

        // Hata enjekte edilen transfer: @Transactional sayesinde TÜMÜ geri alınır (rollback).
        System.out.println("\n300 TL transfer (araya hata) deneniyor...");
        try {
            banka.transfer("Ada", "Burak", 300, true); // hata fırlatır
        } catch (RuntimeException e) {
            System.out.println("Hata: " + e.getMessage() + " -> transaction geri alındı.");
        }
        System.out.println("Sonra    : " + banka.bakiyeler() + "  <- DEĞİŞMEDİ (tutarlı)");

        // Başarılı transfer: commit.
        System.out.println("\n200 TL transfer (başarılı)...");
        banka.transfer("Ada", "Burak", 200, false);
        System.out.println("Sonra    : " + banka.bakiyeler());

        ctx.close();
        System.out.println("""

                --- @Transactional ---
                Metot bir transaction'la SARILIR: normal biterse COMMIT, RuntimeException fırlarsa ROLLBACK.
                Dikkat: varsayılan olarak yalnızca unchecked (RuntimeException) hatalarda rollback olur;
                checked exception'larda rollback için @Transactional(rollbackFor = ...) gerekir.
                Ayrıca proxy tabanlı olduğu için 'self-invocation' (aynı sınıf içi çağrı) transaction'ı tetiklemez.""");
    }

    @Configuration
    @EnableTransactionManagement // @Transactional'ı etkinleştirir (proxy auto-creator)
    public static class TxConfig {
        @Bean DataSource dataSource() {
            var ds = new DriverManagerDataSource("jdbc:h2:mem:tx;DB_CLOSE_DELAY=-1", "sa", "");
            ds.setDriverClassName("org.h2.Driver");
            return ds;
        }
        @Bean JdbcTemplate jdbcTemplate(DataSource ds) { return new JdbcTemplate(ds); }
        // Transaction yöneticisi: commit/rollback'i bu yönetir.
        @Bean PlatformTransactionManager txManager(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean BankaServisi bankaServisi(JdbcTemplate jdbc) { return new BankaServisi(jdbc); }
    }

    public static class BankaServisi {
        private final JdbcTemplate jdbc;
        BankaServisi(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
            jdbc.execute("CREATE TABLE hesap (ad VARCHAR(20) PRIMARY KEY, bakiye INT)");
            jdbc.update("INSERT INTO hesap VALUES ('Ada', 1000), ('Burak', 500)");
        }

        // Tüm metot tek bir transaction; içindeki güncellemeler 'ya hep ya hiç'.
        @Transactional
        public void transfer(String kimden, String kime, int tutar, boolean hataYarat) {
            jdbc.update("UPDATE hesap SET bakiye = bakiye - ? WHERE ad = ?", tutar, kimden);
            if (hataYarat) throw new RuntimeException("ağ hatası! (transfer ortasında)");
            jdbc.update("UPDATE hesap SET bakiye = bakiye + ? WHERE ad = ?", tutar, kime);
        }

        public String bakiyeler() {
            Integer ada = jdbc.queryForObject("SELECT bakiye FROM hesap WHERE ad='Ada'", Integer.class);
            Integer burak = jdbc.queryForObject("SELECT bakiye FROM hesap WHERE ad='Burak'", Integer.class);
            return "Ada=" + ada + ", Burak=" + burak + " (toplam " + (ada + burak) + ")";
        }
    }
}
