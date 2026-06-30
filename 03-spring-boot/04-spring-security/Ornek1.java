// Ornek1: Kimlik doğrulama (authentication) — BCrypt parola + HTTP Basic.
// Korumalı endpoint kimlik doğrulamadan erişilemez (401); doğru kimlikle erişilir (200).
// Çalıştırma: portal derleyip gömülü Tomcat + Spring Security ile başlatır.
package com.egitim.springboot.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    @RestController
    static class ApiController {
        @GetMapping("/acik")  String acik()  { return "Bu endpoint herkese açık."; }
        @GetMapping("/gizli") String gizli() { return "Gizli veri (yalnızca doğrulanmış kullanıcı)."; }
    }

    // Parolalar ASLA düz metin saklanmaz; BCrypt ile (tuzlanarak) hash'lenir.
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // Bellek-içi kullanıcı (gerçekte veritabanından gelir).
    @Bean
    UserDetailsService users(PasswordEncoder enc) {
        UserDetails ada = User.withUsername("ada").password(enc.encode("sifre123")).roles("USER").build();
        return new InMemoryUserDetailsManager(ada);
    }

    // Güvenlik kuralları: /acik serbest, geri kalan her şey kimlik doğrulama ister; HTTP Basic.
    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/acik").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    CommandLineRunner selfTest(PasswordEncoder enc) {
        return args -> {
            System.out.println("\n================ KİMLİK DOĞRULAMA (BCrypt + Basic) ================");
            // BCrypt: aynı parola HER hash'lemede FARKLI çıktı verir (rastgele tuz), ama matches doğrular.
            String h1 = enc.encode("sifre123"), h2 = enc.encode("sifre123");
            System.out.println("hash1: " + h1);
            System.out.println("hash2: " + h2 + "  (farklı! tuz sayesinde)");
            System.out.println("matches('sifre123', hash1): " + enc.matches("sifre123", h1));

            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n/acik  (kimliksiz)              -> " + durum(c, "/acik", null, null));
            System.out.println("/gizli (kimliksiz)              -> " + durum(c, "/gizli", null, null) + "  (reddedildi)");
            System.out.println("/gizli (ada:yanlis)             -> " + durum(c, "/gizli", "ada", "yanlis") + "  (reddedildi)");
            System.out.println("/gizli (ada:sifre123)           -> " + durum(c, "/gizli", "ada", "sifre123") + "  (kabul)");
            System.out.println("===================================================================");
        };
    }

    static String durum(RestClient c, String uri, String user, String pass) {
        return c.get().uri(uri)
                .headers(h -> { if (user != null) h.setBasicAuth(user, pass); })
                .exchange((req, res) -> HttpStatus.valueOf(res.getStatusCode().value()).toString());
    }
}
