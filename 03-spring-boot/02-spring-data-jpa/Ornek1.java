// Ornek1: Spring Data JPA temelleri — @Entity ve JpaRepository ile sıfır SQL yazmadan CRUD.
// Gömülü H2 veritabanı Boot tarafından otomatik yapılandırılır.
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

@SpringBootApplication
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    @Bean
    CommandLineRunner selfTest(GorevRepository repo) {
        return args -> {
            System.out.println("\n================ SPRING DATA JPA ================");

            // CREATE — save() (tek satır, SQL yok)
            Gorev g1 = repo.save(new Gorev("Spring Boot öğren"));
            repo.save(new Gorev("JPA öğren"));
            Gorev g3 = repo.save(new Gorev("REST API yaz"));
            System.out.println("Kaydedildi. Toplam: " + repo.count() + " görev. İlkinin id'si: " + g1.getId());

            // READ — findAll, findById
            System.out.println("Tüm görevler:");
            repo.findAll().forEach(g -> System.out.println("  " + g));
            System.out.println("findById(" + g3.getId() + "): " + repo.findById(g3.getId()).orElse(null));

            // UPDATE — nesneyi değiştir, save ile kaydet
            g1.setTamamlandi(true);
            repo.save(g1);
            System.out.println("Güncellendi: " + repo.findById(g1.getId()).orElse(null));

            // DELETE
            repo.deleteById(g3.getId());
            System.out.println("Silme sonrası toplam: " + repo.count());
            System.out.println("=================================================");
            System.out.println("Dikkat: tek satır SQL yazmadık. JpaRepository tüm CRUD'u sağladı.");
        };
    }
}

// @Entity: bu sınıf bir veritabanı tablosuna eşlenir (Hibernate ile).
@Entity
class Gorev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id'yi veritabanı üretir
    private Long id;
    private String baslik;
    private boolean tamamlandi;

    protected Gorev() {} // JPA boş constructor ister
    Gorev(String baslik) { this.baslik = baslik; }

    Long getId() { return id; }
    String getBaslik() { return baslik; }
    boolean isTamamlandi() { return tamamlandi; }
    void setTamamlandi(boolean t) { this.tamamlandi = t; }

    @Override public String toString() {
        return "Gorev{id=" + id + ", baslik='" + baslik + "', tamamlandi=" + tamamlandi + "}";
    }
}

// Repository: SADECE bir arayüz! Spring Data JPA implementasyonu otomatik üretir.
// JpaRepository<Entity, IdTipi> ile save/findAll/findById/deleteById/count hazır gelir.
interface GorevRepository extends JpaRepository<Gorev, Long> {}
