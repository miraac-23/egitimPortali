// Ornek1: DÜZ SPRING (Boot YOK) — her şeyi ELLE yapılandırırız.
// Aynı işi Örnek 2'de Spring Boot ile yapıp farkı göreceğiz.
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class Ornek1 {

    public static void main(String[] args) {
        System.out.println("================ DÜZ SPRING (manuel yapılandırma) ================");

        // 1) Context'i ELLE oluştururuz.
        var ctx = new AnnotationConfigApplicationContext(ManuelConfig.class);

        // 2) Bean'i alıp kullanırız.
        UrunServisi servis = ctx.getBean(UrunServisi.class);
        servis.ekle("Klavye");
        servis.ekle("Mouse");
        System.out.println("Kayıtlı ürün sayısı: " + servis.adet());

        ctx.close();
        System.out.println("""

                --- Düz Spring'de NE yaptık? ---
                - ApplicationContext'i elle kurduk.
                - DataSource'u (sürücü, URL, kullanıcı) elle tanımladık.
                - JdbcTemplate bean'ini elle oluşturduk.
                - Sürücü/sürüm uyumunu, sunucuyu (web olsaydı) elle yönetirdik.
                Çok kontrol var ama çok da boilerplate. Örnek 2'de Boot bunların çoğunu siler.""");
    }

    @Configuration
    static class ManuelConfig {
        // DataSource'u ELLE tanımlıyoruz (Boot bunu classpath'e bakıp otomatik yapardı).
        @Bean
        DataSource dataSource() {
            var ds = new DriverManagerDataSource("jdbc:h2:mem:duz;DB_CLOSE_DELAY=-1", "sa", "");
            ds.setDriverClassName("org.h2.Driver");
            return ds;
        }

        // JdbcTemplate'i ELLE oluşturuyoruz.
        @Bean
        JdbcTemplate jdbcTemplate(DataSource ds) { return new JdbcTemplate(ds); }

        @Bean
        UrunServisi urunServisi(JdbcTemplate jdbc) { return new UrunServisi(jdbc); }
    }

    static class UrunServisi {
        private final JdbcTemplate jdbc;
        UrunServisi(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
            jdbc.execute("CREATE TABLE urun (ad VARCHAR(50))");
        }
        void ekle(String ad) { jdbc.update("INSERT INTO urun VALUES (?)", ad); }
        int adet() { return jdbc.queryForObject("SELECT COUNT(*) FROM urun", Integer.class); }
    }
}
