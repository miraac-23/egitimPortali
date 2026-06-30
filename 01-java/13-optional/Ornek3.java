// Ornek3: Gerçekçi senaryo — repository'den arama ve orElseThrow.
// Çalıştırma: java Ornek3.java
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Ornek3 {

    record Urun(int id, String ad, double fiyat) {}

    // Basit bir "repository": id'ye göre ürün bulur, bulamazsa Optional.empty döner.
    // Optional döndürmek, "null dönebilir" demekten çok daha açık ve güvenlidir.
    static class UrunDeposu {
        private final List<Urun> urunler = List.of(
                new Urun(1, "Klavye", 450),
                new Urun(2, "Mouse", 250),
                new Urun(3, "Monitör", 3200)
        );

        Optional<Urun> bul(int id) {
            return urunler.stream().filter(u -> u.id() == id).findFirst();
        }
    }

    public static void main(String[] args) {
        UrunDeposu depo = new UrunDeposu();

        // Bulunan ürün: map ile alanına eriş.
        String ad = depo.bul(2).map(Urun::ad).orElse("bilinmiyor");
        System.out.println("id=2 ürün adı: " + ad);

        // Bulunamayan ürün: orElse ile varsayılan.
        double fiyat = depo.bul(99).map(Urun::fiyat).orElse(0.0);
        System.out.println("id=99 fiyat (yoksa 0): " + fiyat);

        // orElseThrow: değer yoksa anlamlı bir hata fırlat.
        try {
            Urun u = depo.bul(99)
                    .orElseThrow(() -> new NoSuchElementException("Ürün bulunamadı: id=99"));
            System.out.println(u);
        } catch (NoSuchElementException e) {
            System.out.println("\nİstisna: " + e.getMessage());
        }

        // Zincirleme: bul -> indirim uygula -> biçimlendir
        String mesaj = depo.bul(3)
                .filter(u -> u.fiyat() > 1000)
                .map(u -> u.ad() + " indirimli fiyat: " + (u.fiyat() * 0.9))
                .orElse("İndirime uygun ürün yok");
        System.out.println("\n" + mesaj);
    }
}
