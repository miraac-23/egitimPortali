// Ornek2: SPRING BOOT — aynı veri işi, ama DataSource ve veri katmanı OTOMATİK gelir.
// Sen yalnızca @Entity ve JpaRepository arayüzünü yazarsın; gerisini Boot yapar.
// Çalıştırma: portal derleyip gömülü Tomcat + H2 ile başlatır.
package com.egitim.karsilastirma;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringBootApplication
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    // DİKKAT: DataSource, EntityManager, transaction yöneticisi... HİÇBİRİNİ tanımlamadık.
    // Boot, classpath'te H2 + Spring Data JPA görüp bunların HEPSİNİ otomatik yapılandırır.
    // Bizim yazdığımız tek şey: bir @Entity ve bir repository ARAYÜZÜ.
    @Bean
    CommandLineRunner selfTest(UrunRepo repo) {
        return args -> {
            System.out.println("\n================ SPRING BOOT (otomatik yapılandırma) ================");
            repo.save(new Urun("Klavye"));
            repo.save(new Urun("Mouse"));
            System.out.println("Kayıtlı ürün sayısı: " + repo.count());
            System.out.println("Ürünler: " + repo.findAll().stream().map(Urun::getAd).toList());
            System.out.println("""

                    --- Spring Boot'ta NE yapmadık? ---
                    - DataSource TANIMLAMADIK (Boot H2'yi görüp otomatik kurdu).
                    - EntityManager / transaction yöneticisi TANIMLAMADIK (otomatik geldi).
                    - Repository implementasyonu YAZMADIK (Spring Data üretti).
                    Sadece bir @Entity + bir arayüz yazdık. İşte Boot'un 'convention over configuration' farkı.""");
        };
    }
}

@Entity
class Urun {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ad;
    protected Urun() {}
    Urun(String ad) { this.ad = ad; }
    String getAd() { return ad; }
}

interface UrunRepo extends JpaRepository<Urun, Long> {}
