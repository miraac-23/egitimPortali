// Ornek1: HandlerInterceptor — controller'a girmeden önce/sonra araya girmek.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // Interceptor: Spring MVC seviyesinde, controller metodu ÇAĞRILMADAN önce ve sonra çalışır.
    static class ZamanlamaInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
            req.setAttribute("baslangic", System.currentTimeMillis());
            System.out.println("  [interceptor:preHandle] " + req.getMethod() + " " + req.getRequestURI());
            // false dönerse istek controller'a HİÇ ulaşmaz (örn. yetki yoksa burada kesilir).
            return true;
        }
        @Override
        public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
            long sure = System.currentTimeMillis() - (long) req.getAttribute("baslangic");
            System.out.println("  [interceptor:afterCompletion] durum=" + res.getStatus() + ", süre=" + sure + "ms");
        }
    }

    // Interceptor'ı kaydet (hangi yollara uygulanacağını belirle).
    @Bean
    WebMvcConfigurer interceptorConfig() {
        return new WebMvcConfigurer() {
            @Override public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new ZamanlamaInterceptor())
                        .addPathPatterns("/api/**");   // yalnızca /api/** yollarına uygula
            }
        };
    }

    @RestController
    @RequestMapping("/api")
    static class DemoController {
        @GetMapping("/selam") String selam() { return "merhaba"; }
        @GetMapping("/veri") java.util.Map<String,Object> veri() { return java.util.Map.of("deger", 42); }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= INTERCEPTOR SELF-TEST =========");
            System.out.println("İstek 1: GET /api/selam");
            System.out.println("  yanıt -> " + c.get().uri("/api/selam").retrieve().body(String.class));
            System.out.println("İstek 2: GET /api/veri");
            System.out.println("  yanıt -> " + c.get().uri("/api/veri").retrieve().body(String.class));
            System.out.println("(Her istekte interceptor pre/afterCompletion çalıştı.)");
            System.out.println("=========================================");
        };
    }
}
