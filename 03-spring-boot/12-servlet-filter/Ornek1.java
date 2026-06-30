// Ornek1: Servlet Filter — servlet konteyneri seviyesinde her isteği sarmalamak.
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.UUID;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    // Filter: en dışta, TÜM isteklerde (statik dosyalar dahil) çalışır; ham request/response görür.
    static class IzlemeFilter implements Filter {
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest http = (HttpServletRequest) req;
            HttpServletResponse httpRes = (HttpServletResponse) res;

            // Her isteğe bir izleme kimliği (trace id) ekle — log korelasyonu için.
            String izlemeId = UUID.randomUUID().toString().substring(0, 8);
            httpRes.setHeader("X-Izleme-Id", izlemeId);
            long t0 = System.currentTimeMillis();
            System.out.println("  [filter:önce] " + http.getMethod() + " " + http.getRequestURI() + " izlemeId=" + izlemeId);

            chain.doFilter(req, res); // zinciri ilerlet -> isteğin geri kalanı (interceptor, controller) çalışır

            System.out.println("  [filter:sonra] durum=" + httpRes.getStatus()
                    + " süre=" + (System.currentTimeMillis() - t0) + "ms");
        }
    }

    // Filter'ı kaydet + hangi yollara/sırayla uygulanacağını belirle.
    @Bean
    FilterRegistrationBean<IzlemeFilter> izlemeFilter() {
        FilterRegistrationBean<IzlemeFilter> reg = new FilterRegistrationBean<>(new IzlemeFilter());
        reg.addUrlPatterns("/*");   // tüm yollar
        reg.setOrder(1);            // birden çok filter varsa sıra
        return reg;
    }

    @RestController
    static class DemoController {
        @GetMapping("/selam") String selam() { return "merhaba"; }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= SERVLET FILTER SELF-TEST =========");
            var yanit = c.get().uri("/selam").retrieve().toEntity(String.class);
            System.out.println("yanıt gövdesi: " + yanit.getBody());
            System.out.println("Filter'ın eklediği başlık X-Izleme-Id: " + yanit.getHeaders().getFirst("X-Izleme-Id"));
            System.out.println("(Filter her istekte zincirden önce/sonra çalıştı ve başlık ekledi.)");
            System.out.println("============================================");
        };
    }
}
