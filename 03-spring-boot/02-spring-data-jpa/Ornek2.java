// Ornek2: Türetilmiş sorgular (derived queries) ve @Query ile özel JPQL.
// Spring Data, metot ADINDAN sorgu üretir; karmaşık durumlar için @Query yazarsın.
// Çalıştırma: portal derleyip gömülü Tomcat + H2 ile başlatır.
package com.egitim.springboot.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SpringBootApplication
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    @Bean
    CommandLineRunner selfTest(UrunRepository repo) {
        return args -> {
            repo.save(new Urun("Klavye", "aksesuar", 450));
            repo.save(new Urun("Mouse", "aksesuar", 250));
            repo.save(new Urun("Monitör", "ekran", 3200));
            repo.save(new Urun("Laptop", "bilgisayar", 28000));
            repo.save(new Urun("Mousepad", "aksesuar", 80));

            System.out.println("\n================ DERIVED QUERIES + @Query ================");
            // Metot adından üretilen sorgular:
            System.out.println("findByKategori('aksesuar')       -> " + adlar(repo.findByKategori("aksesuar")));
            System.out.println("findByFiyatLessThan(500)         -> " + adlar(repo.findByFiyatLessThan(500)));
            System.out.println("findByAdContainingIgnoreCase('mo') -> " + adlar(repo.findByAdContainingIgnoreCase("mo")));
            System.out.println("countByKategori('aksesuar')      -> " + repo.countByKategori("aksesuar"));
            System.out.println("findTop2ByOrderByFiyatDesc       -> " + adlar(repo.findTop2ByOrderByFiyatDesc()));

            // @Query ile özel JPQL:
            System.out.println("fiyatAraliginda(100, 1000)       -> " + adlar(repo.fiyatAraliginda(100, 1000)));
            System.out.println("ortalamaFiyat()                  -> " + repo.ortalamaFiyat());
            System.out.println("==========================================================");
            System.out.println("Metot adı (findBy...) basit sorgular için; @Query karmaşık/özel sorgular için.");
        };
    }

    static String adlar(List<Urun> liste) { return liste.stream().map(Urun::getAd).toList().toString(); }
}

@Entity
class Urun {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ad;
    private String kategori;
    private double fiyat;

    protected Urun() {}
    Urun(String ad, String kategori, double fiyat) { this.ad = ad; this.kategori = kategori; this.fiyat = fiyat; }
    String getAd() { return ad; }
}

interface UrunRepository extends JpaRepository<Urun, Long> {
    // Metot adından otomatik sorgu (derived query):
    List<Urun> findByKategori(String kategori);
    List<Urun> findByFiyatLessThan(double fiyat);
    List<Urun> findByAdContainingIgnoreCase(String parca);
    long countByKategori(String kategori);
    List<Urun> findTop2ByOrderByFiyatDesc();

    // Karmaşık/özel sorgu için JPQL (@Query):
    @Query("select u from Urun u where u.fiyat between :min and :max order by u.fiyat")
    List<Urun> fiyatAraliginda(@Param("min") double min, @Param("max") double max);

    @Query("select avg(u.fiyat) from Urun u")
    Double ortalamaFiyat();
}
