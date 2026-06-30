// Ornek1: CommandLineRunner ve ApplicationRunner — uygulama başlarken kod çalıştırma.
// Çalıştırma: portal derleyip gömülü Tomcat ile başlatır; runner çıktıları başlangıçta görünür.
package com.egitim.springboot.runners;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) {
        SpringApplication.run(Ornek1.class, args);
    }

    // CommandLineRunner: ham String[] argümanları alır. Uygulama hazır olunca BİR KEZ çalışır.
    @Bean
    @Order(1) // çalışma sırası (küçük önce)
    CommandLineRunner ilkGorev() {
        return args -> {
            System.out.println("\n>>> [CommandLineRunner @Order(1)] uygulama başladı, hazırlık yapılıyor...");
            System.out.println("    ham argümanlar: " + java.util.Arrays.toString(args));
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner ikinciGorev() {
        return args -> System.out.println(">>> [CommandLineRunner @Order(2)] önbellek ısıtıldı / bağlantılar kontrol edildi");
    }

    // ApplicationRunner: argümanları ApplicationArguments olarak (opsiyon/değer ayrıştırılmış) alır.
    @Bean
    @Order(3)
    ApplicationRunner uygulamaRunner() {
        return (ApplicationArguments args) -> {
            System.out.println(">>> [ApplicationRunner @Order(3)] opsiyon adları: " + args.getOptionNames());
            System.out.println("    --debug verildi mi? " + args.containsOption("debug"));
            System.out.println("    başlangıç görevleri tamamlandı.\n");
        };
    }
}
