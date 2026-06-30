// Ornek2: Yetkilendirme (authorization) — rol bazlı erişim kontrolü.
// Aynı kimlikle giriş yapan farklı roldeki kullanıcılar farklı endpoint'lere erişebilir.
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek2 {

    public static void main(String[] args) { SpringApplication.run(Ornek2.class, args); }

    @RestController
    static class ApiController {
        @GetMapping("/acik")      String acik()      { return "herkese açık"; }
        @GetMapping("/kullanici") String kullanici() { return "kullanıcı alanı (USER veya ADMIN)"; }
        @GetMapping("/yonetici")  String yonetici()  { return "yönetici alanı (yalnızca ADMIN)"; }
    }

    @Bean PasswordEncoder enc() { return new BCryptPasswordEncoder(); }

    // İki kullanıcı: 'ada' USER rolünde, 'admin' ADMIN rolünde.
    @Bean
    UserDetailsService users(PasswordEncoder enc) {
        return new InMemoryUserDetailsManager(
                User.withUsername("ada").password(enc.encode("123")).roles("USER").build(),
                User.withUsername("admin").password(enc.encode("123")).roles("ADMIN").build());
    }

    // Rol bazlı kurallar: hangi yol hangi role açık?
    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/acik").permitAll()
                        .requestMatchers("/yonetici").hasRole("ADMIN")            // sadece ADMIN
                        .requestMatchers("/kullanici").hasAnyRole("USER", "ADMIN") // USER veya ADMIN
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ ROL BAZLI YETKİLENDİRME ================");
            System.out.println("ada(USER)    -> /kullanici : " + durum(c, "/kullanici", "ada", "123") + "  (izin)");
            System.out.println("ada(USER)    -> /yonetici  : " + durum(c, "/yonetici", "ada", "123") + "  (YASAK)");
            System.out.println("admin(ADMIN) -> /kullanici : " + durum(c, "/kullanici", "admin", "123") + "  (izin)");
            System.out.println("admin(ADMIN) -> /yonetici  : " + durum(c, "/yonetici", "admin", "123") + "  (izin)");
            System.out.println("=========================================================");
            System.out.println("401 = kim olduğun bilinmiyor (authentication). 403 = kimliğin var ama yetkin yok (authorization).");
        };
    }

    static String durum(RestClient c, String uri, String user, String pass) {
        return c.get().uri(uri).headers(h -> h.setBasicAuth(user, pass))
                .exchange((req, res) -> HttpStatus.valueOf(res.getStatusCode().value()).toString());
    }
}
