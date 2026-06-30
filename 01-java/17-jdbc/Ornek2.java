// Ornek2: DAO (Data Access Object) deseni — veri erişimini temiz bir sınıfta toplamak.
// Spring Data JPA'nın Repository'lerinin elle yazılmış hali. Tam bir CRUD + generated keys.
// Çalıştırma: java -cp h2-*.jar Ornek2.java  (portalda otomatik)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Ornek2 {

    // Domain nesnesi (immutable record).
    record Urun(Integer id, String ad, double fiyat, int stok) {}

    public static void main(String[] args) throws SQLException {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:dao;DB_CLOSE_DELAY=-1", "sa", "")) {
            UrunDAO dao = new UrunDAO(con);
            dao.tabloOlustur();

            // CREATE — ekleme generated key (otomatik id) döndürür.
            int id1 = dao.ekle(new Urun(null, "Klavye", 450, 20));
            int id2 = dao.ekle(new Urun(null, "Mouse", 250, 35));
            dao.ekle(new Urun(null, "Monitör", 3200, 8));
            System.out.println("Eklendi. İlk iki id: " + id1 + ", " + id2);

            // READ
            System.out.println("\nTüm ürünler:");
            dao.hepsi().forEach(u -> System.out.println("  " + u));

            System.out.println("\nid=" + id2 + " ürün: " + dao.bul(id2).orElse(null));
            System.out.println("id=999 ürün     : " + dao.bul(999).map(Object::toString).orElse("(bulunamadı)"));

            // UPDATE — fiyat güncelle
            dao.fiyatGuncelle(id1, 399);
            System.out.println("\nGüncel Klavye: " + dao.bul(id1).orElse(null));

            // DELETE
            dao.sil(id2);
            System.out.println("\nSilme sonrası ürün sayısı: " + dao.hepsi().size());
        }
    }

    // DAO: belirli bir nesne için tüm SQL erişimini tek yerde toplar.
    static class UrunDAO {
        private final Connection con;
        UrunDAO(Connection con) { this.con = con; }

        void tabloOlustur() throws SQLException {
            try (Statement st = con.createStatement()) {
                st.execute("CREATE TABLE urun (id INT AUTO_INCREMENT PRIMARY KEY, ad VARCHAR(80), fiyat DECIMAL(10,2), stok INT)");
            }
        }

        // RETURN_GENERATED_KEYS: veritabanının ürettiği id'yi geri alırız.
        int ekle(Urun u) throws SQLException {
            String sql = "INSERT INTO urun (ad, fiyat, stok) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, u.ad());
                ps.setDouble(2, u.fiyat());
                ps.setInt(3, u.stok());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return keys.getInt(1);
                }
            }
        }

        Optional<Urun> bul(int id) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM urun WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(map(rs)) : Optional.empty();
                }
            }
        }

        List<Urun> hepsi() throws SQLException {
            List<Urun> liste = new ArrayList<>();
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM urun ORDER BY id")) {
                while (rs.next()) liste.add(map(rs));
            }
            return liste;
        }

        void fiyatGuncelle(int id, double yeniFiyat) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement("UPDATE urun SET fiyat = ? WHERE id = ?")) {
                ps.setDouble(1, yeniFiyat);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        }

        void sil(int id) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM urun WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }

        // ResultSet satırını domain nesnesine çevirir (row mapping).
        private Urun map(ResultSet rs) throws SQLException {
            return new Urun(rs.getInt("id"), rs.getString("ad"), rs.getDouble("fiyat"), rs.getInt("stok"));
        }
    }
}
