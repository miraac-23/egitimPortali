// Ornek3: JWT (JSON Web Token) ile durumsuz (stateless) güvenlik.
// /giris kimlik doğrular ve imzalı bir token verir; korumalı endpoint bu token'ı ister.
// Çalıştırma: portal derleyip gömülü Tomcat + Spring Security + jjwt ile başlatır.
package com.egitim.springboot.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek3 {

    public static void main(String[] args) { SpringApplication.run(Ornek3.class, args); }

    // Token'ı imzalamak/doğrulamak için gizli anahtar (HS256 için >= 256 bit).
    static final SecretKey KEY = Keys.hmacShaKeyFor("egitim-portal-cok-gizli-jwt-anahtari-256bit!".getBytes());

    static String tokenUret(String kullanici) {
        return Jwts.builder().subject(kullanici)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 saat geçerli
                .signWith(KEY).compact();
    }

    static String tokenDogrula(String token) { // geçersizse exception fırlatır
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @RestController
    static class AuthController {
        // Basit kullanıcı deposu (gerçekte veritabanı + BCrypt).
        private final Map<String, String> kullanicilar = Map.of("ada", "123");

        @PostMapping("/giris")
        public ResponseEntity giris(@RequestBody Map<String, String> body) {
            String k = body.get("kullanici"), p = body.get("parola");
            if (kullanicilar.containsKey(k) && kullanicilar.get(k).equals(p)) {
                return ResponseEntity.ok(tokenUret(k)); // token döndür
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz kimlik");
        }

        @GetMapping("/profil")
        public String profil() {
            // SecurityContext'teki (JWT filtresinin koyduğu) kullanıcı.
            return "Profil sahibi: " + SecurityContextHolder.getContext().getAuthentication().getName();
        }
    }

    // Her istekte Authorization: Bearer <token> başlığını okuyup doğrulayan filtre.
    static class JwtFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                throws java.io.IOException, jakarta.servlet.ServletException {
            String header = req.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String kullanici = tokenDogrula(header.substring(7));
                    var auth = new UsernamePasswordAuthenticationToken(
                            kullanici, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(auth); // doğrulandı
                } catch (Exception e) {
                    // geçersiz token -> kimlik atanmaz, istek korumalı endpoint'te 401 alır
                }
            }
            chain.doFilter(req, res);
        }
    }

    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                // Stateless: sunucu oturum (session) tutmaz; her istek token taşır.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/giris").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n================ JWT (durumsuz güvenlik) ================");

            // 1) Giriş -> token al
            String token = c.post().uri("/giris").contentType(MediaType.APPLICATION_JSON)
                    .body("{\"kullanici\":\"ada\",\"parola\":\"123\"}").retrieve().body(String.class);
            System.out.println("POST /giris -> token alındı: " + token.substring(0, 30) + "...");

            // 2) Token'sız korumalı endpoint -> 401
            String tokensiz = c.get().uri("/profil")
                    .exchange((req, res) -> HttpStatus.valueOf(res.getStatusCode().value()).toString());
            System.out.println("GET /profil (token'sız)  -> " + tokensiz + "  (reddedildi)");

            // 3) Token ile -> 200
            String cevap = c.get().uri("/profil").header("Authorization", "Bearer " + token)
                    .retrieve().body(String.class);
            System.out.println("GET /profil (token ile)  -> 200 OK | " + cevap);
            System.out.println("=========================================================");
            System.out.println("JWT: sunucu oturum tutmaz; kimlik, imzalı token'da taşınır (mikroservis dostu).");
        };
    }
}
