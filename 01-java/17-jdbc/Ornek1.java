// Ornek1: JDBC temelleri — gerçekçi bir müşteri tablosu üzerinde bağlan, oluştur, ekle, sorgula.
// H2 in-memory veritabanı kullanılır (kurulum gerekmez). Portaldaki "Çalıştır" H2'yi
// otomatik classpath'e ekler; kendi bilgisayarında: java -cp h2-*.jar Ornek1.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Ornek1 {

    static final String URL = "jdbc:h2:mem:magaza;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        // try-with-resources: Connection blok bitince otomatik kapanır.
        try (Connection con = DriverManager.getConnection(URL, "sa", "")) {
            System.out.println("Bağlanıldı: " + con.getMetaData().getDatabaseProductName()
                    + " " + con.getMetaData().getDatabaseProductVersion());

            // DDL: şema oluştur.
            try (Statement st = con.createStatement()) {
                st.execute("""
                    CREATE TABLE musteri (
                        id      INT AUTO_INCREMENT PRIMARY KEY,
                        ad      VARCHAR(60) NOT NULL,
                        sehir   VARCHAR(40),
                        bakiye  DECIMAL(10,2) DEFAULT 0
                    )
                    """);
            }
            System.out.println("Tablo oluşturuldu.\n");

            // PreparedStatement ile parametreli ekleme (güvenli yol).
            String ekle = "INSERT INTO musteri (ad, sehir, bakiye) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(ekle)) {
                ekle(ps, "Ada Yılmaz", "İzmir", 1500);
                ekle(ps, "Burak Demir", "Ankara", 300);
                ekle(ps, "Ceren Kaya", "İzmir", 2750);
                ekle(ps, "Deniz Ak", "İstanbul", 50);
            }
            System.out.println("4 müşteri eklendi.");

            // Filtreli sorgu: bakiyesi 1000'den fazla olanlar.
            System.out.println("\nBakiyesi 1000+ olan müşteriler:");
            String sorgu = "SELECT id, ad, sehir, bakiye FROM musteri WHERE bakiye > ? ORDER BY bakiye DESC";
            try (PreparedStatement ps = con.prepareStatement(sorgu)) {
                ps.setDouble(1, 1000);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("  #%d %-12s %-10s %,.2f TL%n",
                                rs.getInt("id"), rs.getString("ad"), rs.getString("sehir"), rs.getDouble("bakiye"));
                    }
                }
            }

            // Toplama (aggregate) sorgusu.
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) adet, AVG(bakiye) ort FROM musteri")) {
                if (rs.next()) {
                    System.out.printf("%nToplam %d müşteri, ortalama bakiye %,.2f TL%n",
                            rs.getInt("adet"), rs.getDouble("ort"));
                }
            }
        }
    }

    static void ekle(PreparedStatement ps, String ad, String sehir, double bakiye) throws SQLException {
        ps.setString(1, ad);
        ps.setString(2, sehir);
        ps.setDouble(3, bakiye);
        ps.executeUpdate();
    }
}
