// Ornek2: JdbcTemplate ile DAO — RowMapper, queryForObject, update ve NamedParameterJdbcTemplate.
// Çalıştırma: portal Spring + H2 classpath'iyle çalıştırır.
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Optional;

public class Ornek2 {

    record Musteri(int id, String ad, String sehir) {}

    public static void main(String[] args) {
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:dao;DB_CLOSE_DELAY=-1", "sa", "");
        ds.setDriverClassName("org.h2.Driver");

        MusteriDao dao = new MusteriDao(new JdbcTemplate(ds));
        dao.tabloOlustur();
        dao.ekle(new Musteri(1, "Ada", "İzmir"));
        dao.ekle(new Musteri(2, "Burak", "Ankara"));
        dao.ekle(new Musteri(3, "Ceren", "İzmir"));

        System.out.println("Tüm müşteriler:");
        dao.hepsi().forEach(m -> System.out.println("  " + m));

        System.out.println("\nid=2: " + dao.bul(2).orElse(null));
        System.out.println("İzmir'dekiler (named params): " + dao.sehirdekiler("İzmir").size() + " müşteri");

        dao.sehirGuncelle(2, "İstanbul");
        System.out.println("Güncelleme sonrası id=2: " + dao.bul(2).orElse(null));
    }

    // DAO: tüm veri erişimini JdbcTemplate ile tek sınıfta toplar.
    static class MusteriDao {
        private final JdbcTemplate jdbc;
        private final NamedParameterJdbcTemplate namedJdbc;

        // ResultSet satırını Musteri'ye çeviren yeniden kullanılabilir eşleyici.
        private final RowMapper<Musteri> mapper =
                (rs, n) -> new Musteri(rs.getInt("id"), rs.getString("ad"), rs.getString("sehir"));

        MusteriDao(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
            this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        }

        void tabloOlustur() {
            jdbc.execute("CREATE TABLE musteri (id INT PRIMARY KEY, ad VARCHAR(50), sehir VARCHAR(40))");
        }

        void ekle(Musteri m) {
            jdbc.update("INSERT INTO musteri (id, ad, sehir) VALUES (?, ?, ?)", m.id(), m.ad(), m.sehir());
        }

        List<Musteri> hepsi() {
            return jdbc.query("SELECT * FROM musteri ORDER BY id", mapper);
        }

        Optional<Musteri> bul(int id) {
            var liste = jdbc.query("SELECT * FROM musteri WHERE id = ?", mapper, id);
            return liste.stream().findFirst();
        }

        // İsimli parametreler: ? yerine :ad kullanır, okunaklılığı artırır.
        List<Musteri> sehirdekiler(String sehir) {
            var params = new MapSqlParameterSource("sehir", sehir);
            return namedJdbc.query("SELECT * FROM musteri WHERE sehir = :sehir", params, mapper);
        }

        void sehirGuncelle(int id, String yeniSehir) {
            jdbc.update("UPDATE musteri SET sehir = ? WHERE id = ?", yeniSehir, id);
        }
    }
}
