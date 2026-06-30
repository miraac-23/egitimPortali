package com.egitim.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Portalın KENDİ güvenlik yapılandırması.
 *
 * Sınıf yoluna spring-boot-starter-security eklendi (eğitim Spring Security örneklerini
 * çalıştırabilmek için). Ancak Spring Security varsayılan olarak tüm endpoint'leri kilitler;
 * bu, portalın açık API'sini (/api/**, /actuator/**) bozar. Bu yüzden portal için TÜM
 * isteklere izin veren bir filter chain tanımlıyoruz. (Eğitim güvenlik örnekleri ayrı bir
 * JVM'de, kendi SecurityFilterChain'leriyle çalışır; bu yapılandırma onları etkilemez.)
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain portalSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
