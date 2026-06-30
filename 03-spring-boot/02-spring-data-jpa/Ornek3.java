// Ornek3: İlişkiler — @OneToMany / @ManyToOne (bir yazar, birden çok kitap).
// Çalıştırma: portal derleyip gömülü Tomcat + H2 ile başlatır.
package com.egitim.springboot.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    @Bean
    CommandLineRunner selfTest(YazarRepository yazarlar) {
        return args -> {
            // Bir yazar ve kitapları; cascade sayesinde yazarı kaydetmek kitapları da kaydeder.
            Yazar yazar = new Yazar("Yaşar Kemal");
            yazar.kitapEkle(new Kitap("İnce Memed"));
            yazar.kitapEkle(new Kitap("Yer Demir Gök Bakır"));
            yazarlar.save(yazar);

            Yazar yazar2 = new Yazar("Sabahattin Ali");
            yazar2.kitapEkle(new Kitap("Kürk Mantolu Madonna"));
            yazarlar.save(yazar2);

            System.out.println("\n================ İLİŞKİLER (@OneToMany / @ManyToOne) ================");
            System.out.println("Toplam yazar: " + yazarlar.count());
            yazarlar.findAll().forEach(y -> {
                System.out.println("  " + y.getAd() + " (" + y.getKitaplar().size() + " kitap):");
                y.getKitaplar().forEach(k -> System.out.println("      - " + k.getBaslik()));
            });
            System.out.println("=====================================================================");
            System.out.println("@OneToMany: bir yazarın çok kitabı; @ManyToOne: her kitabın bir yazarı.");
            System.out.println("cascade = ALL: yazarı kaydetmek/silmek kitaplarını da kapsar.");
        };
    }
}

@Entity
class Yazar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ad;

    // Bir yazarın birden çok kitabı. Karşı taraftaki "yazar" alanı sahipliği tutar (mappedBy).
    // Demo basit olsun diye EAGER; üretimde genelde LAZY + JOIN FETCH tercih edilir.
    @OneToMany(mappedBy = "yazar", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Kitap> kitaplar = new ArrayList<>();

    protected Yazar() {}
    Yazar(String ad) { this.ad = ad; }

    void kitapEkle(Kitap k) { k.setYazar(this); kitaplar.add(k); } // iki yönü de bağla
    String getAd() { return ad; }
    List<Kitap> getKitaplar() { return kitaplar; }
}

@Entity
class Kitap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String baslik;

    // Her kitabın bir yazarı (foreign key bu tarafta tutulur).
    @ManyToOne
    private Yazar yazar;

    protected Kitap() {}
    Kitap(String baslik) { this.baslik = baslik; }

    String getBaslik() { return baslik; }
    void setYazar(Yazar y) { this.yazar = y; }
}

interface YazarRepository extends JpaRepository<Yazar, Long> {}
