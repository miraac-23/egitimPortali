// Ornek1: JWT (JSON Web Token) ile durumsuz (stateless) kimlik — token üret, doğrula, koru.
// OAuth2'nin "bearer token" modelinin çekirdeği. (jjwt kütüphanesi classpath'te mevcut.)
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // İmzalama anahtarı (gerçekte gizli, ortam değişkeninden gelir; HS256 için >=256 bit).
    static final SecretKey ANAHTAR = Keys.hmacShaKeyFor(
            "egitim-portali-cok-gizli-jwt-anahtari-256bit!!".getBytes());

    @RestController
    static class AuthController {

        // /login: kimlik doğrulanır (burada basit), bir JWT üretilir ve döndürülür.
        @PostMapping("/login")
        public Map<String, String> login(@RequestParam String kullanici) {
            String token = Jwts.builder()
                    .subject(kullanici)                       // kime ait (sub)
                    .claim("roller", List.of("USER"))          // özel iddia (claim)
                    .issuedAt(new Date())                      // ne zaman üretildi
                    .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 saat geçerli
                    .signWith(ANAHTAR)                         // imzala (kurcalanırsa doğrulama bozulur)
                    .compact();
            return Map.of("token", token);
        }

        // /profil: korumalı uç — geçerli bir 'Authorization: Bearer <token>' ister.
        @GetMapping("/profil")
        public ResponseEntity<?> profil(@RequestHeader(value = "Authorization", required = false) String auth) {
            if (auth == null || !auth.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("hata", "token yok"));
            }
            try {
                Claims c = Jwts.parser().verifyWith(ANAHTAR).build()
                        .parseSignedClaims(auth.substring(7)).getPayload(); // imza+süre doğrula
                return ResponseEntity.ok(Map.of("kullanici", c.getSubject(), "roller", c.get("roller")));
            } catch (Exception e) {
                return ResponseEntity.status(401).body(Map.of("hata", "geçersiz/expired token"));
            }
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= JWT SELF-TEST =========");

            // 1) Login -> token al
            Map<?,?> giris = c.post().uri("/login?kullanici=ada").retrieve().body(Map.class);
            String token = (String) giris.get("token");
            System.out.println("LOGIN  -> token (ilk 30): " + token.substring(0, 30) + "...");

            // 2) Token ile korumalı uca eriş -> 200
            String profil = c.get().uri("/profil").header("Authorization", "Bearer " + token)
                    .retrieve().body(String.class);
            System.out.println("PROFİL (token ile)   -> " + profil);

            // 3) Token olmadan -> 401
            try {
                c.get().uri("/profil").retrieve().body(String.class);
            } catch (Exception e) {
                System.out.println("PROFİL (token YOK)   -> reddedildi (401)");
            }
            // 4) Sahte token -> 401
            try {
                c.get().uri("/profil").header("Authorization", "Bearer sahte.token.degeri")
                        .retrieve().body(String.class);
            } catch (Exception e) {
                System.out.println("PROFİL (sahte token) -> reddedildi (401)");
            }
            System.out.println("=================================");
        };
    }
}
