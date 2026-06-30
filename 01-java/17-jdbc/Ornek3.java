// Ornek3: Transaction + ilişkili tablolar + toplu ekleme (batch).
// Gerçek senaryo: bir sipariş ve kalemleri TEK bir transaction'da kaydedilir;
// bir hata olursa HER ŞEY geri alınır (sipariş yarım kalmaz).
// Çalıştırma: java -cp h2-*.jar Ornek3.java  (portalda otomatik)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Ornek3 {

    record Kalem(String urun, int adet, double birimFiyat) {}

    static final String URL = "jdbc:h2:mem:siparis;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        try (Connection con = DriverManager.getConnection(URL, "sa", "")) {
            semaOlustur(con);

            // 1) Başarılı sipariş: sipariş başlığı + kalemler tek transaction'da.
            int siparisId = siparisOlustur(con, "Ada", List.of(
                    new Kalem("Klavye", 2, 450),
                    new Kalem("Mouse", 1, 250),
                    new Kalem("Mousepad", 3, 80)
            ));
            System.out.println("Sipariş #" + siparisId + " başarıyla oluşturuldu (commit).");
            siparisYazdir(con, siparisId);

            // 2) Hatalı sipariş: bir kalemde geçersiz adet -> tüm sipariş geri alınır (rollback).
            System.out.println("\n--- Geçersiz sipariş deneniyor (adet=0) ---");
            try {
                siparisOlustur(con, "Burak", List.of(
                        new Kalem("Monitör", 1, 3200),
                        new Kalem("Kablo", 0, 50)   // geçersiz: adet 0
                ));
            } catch (RuntimeException e) {
                System.out.println("Hata: " + e.getMessage() + " -> rollback yapıldı.");
            }

            // Rollback çalıştıysa Burak'ın siparişi HİÇ oluşmamış olmalı.
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM siparis")) {
                rs.next();
                System.out.println("Toplam sipariş sayısı: " + rs.getInt(1) + " (Burak'ınki kaydedilmedi)");
            }
        }
    }

    static void semaOlustur(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE siparis (id INT AUTO_INCREMENT PRIMARY KEY, musteri VARCHAR(40), toplam DECIMAL(10,2))");
            st.execute("""
                CREATE TABLE siparis_kalem (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    siparis_id  INT,
                    urun        VARCHAR(60),
                    adet        INT,
                    tutar       DECIMAL(10,2)
                )
                """);
        }
    }

    // Sipariş + kalemleri TEK transaction'da kaydeder; hata olursa tümünü geri alır.
    static int siparisOlustur(Connection con, String musteri, List<Kalem> kalemler) throws SQLException {
        con.setAutoCommit(false); // transaction başlat
        try {
            // İş kuralı: adet pozitif olmalı. Değilse transaction'ı iptal et.
            for (Kalem k : kalemler) {
                if (k.adet() <= 0) throw new RuntimeException("geçersiz adet: " + k.urun());
            }

            double toplam = kalemler.stream().mapToDouble(k -> k.adet() * k.birimFiyat()).sum();

            // Sipariş başlığını ekle, üretilen id'yi al.
            int siparisId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO siparis (musteri, toplam) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, musteri);
                ps.setDouble(2, toplam);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    siparisId = keys.getInt(1);
                }
            }

            // Kalemleri TOPLU (batch) ekle — tek seferde, daha verimli.
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO siparis_kalem (siparis_id, urun, adet, tutar) VALUES (?, ?, ?, ?)")) {
                for (Kalem k : kalemler) {
                    ps.setInt(1, siparisId);
                    ps.setString(2, k.urun());
                    ps.setInt(3, k.adet());
                    ps.setDouble(4, k.adet() * k.birimFiyat());
                    ps.addBatch();   // sıraya ekle
                }
                ps.executeBatch();   // hepsini tek seferde gönder
            }

            con.commit();            // her şey başarılı -> kalıcı yap
            return siparisId;
        } catch (RuntimeException | SQLException e) {
            con.rollback();          // herhangi bir hata -> hiçbir şey kaydedilmesin
            throw new RuntimeException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    // JOIN ile siparişi ve kalemlerini birlikte yazdırır.
    static void siparisYazdir(Connection con, int siparisId) throws SQLException {
        String sql = """
            SELECT s.musteri, s.toplam, k.urun, k.adet, k.tutar
            FROM siparis s JOIN siparis_kalem k ON k.siparis_id = s.id
            WHERE s.id = ?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, siparisId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean baslik = false;
                while (rs.next()) {
                    if (!baslik) {
                        System.out.printf("  Müşteri: %s | Sipariş toplamı: %,.2f TL%n",
                                rs.getString("musteri"), rs.getDouble("toplam"));
                        baslik = true;
                    }
                    System.out.printf("    - %-10s x%d = %,.2f TL%n",
                            rs.getString("urun"), rs.getInt("adet"), rs.getDouble("tutar"));
                }
            }
        }
    }
}
