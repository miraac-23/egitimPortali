package com.egitim.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS yapılandırması — React geliştirme sunucusunun (Vite) API'yi çağırabilmesi için.
 *
 * NOT: {@code allowedOriginPatterns} kullanılır (sabit {@code allowedOrigins} değil).
 * Vite bazen 5173 yerine 5174/5175 portunu seçebilir; ayrıca istek Vite proxy'sinden
 * geçtiğinde tarayıcının Origin başlığı (örn. http://localhost:5174) backend'e iletilir.
 * Pattern'ler herhangi bir localhost/127.0.0.1 portuna izin vererek "Invalid CORS request"
 * (403) hatasını önler. İzin verilen pattern'ler {@code egitim.cors.allowed-origins} ile
 * değiştirilebilir.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOriginPatterns;

    public WebConfig(@Value("${egitim.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*}") String origins) {
        this.allowedOriginPatterns = origins.split("\\s*,\\s*");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        registry.addMapping("/actuator/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET");
    }
}
