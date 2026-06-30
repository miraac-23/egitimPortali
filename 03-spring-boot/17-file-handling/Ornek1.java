// Ornek1: Dosya yükleme (MultipartFile) ve indirme (ResponseEntity<byte[]>).
// Çalıştırma: portal gömülü Tomcat ile başlatır, self-test çıktısını alır.
package com.egitim.springboot.dosya;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ornek1 {

    public static void main(String[] args) { SpringApplication.run(Ornek1.class, args); }

    @RestController
    @RequestMapping("/dosya")
    static class DosyaController {
        private final Map<String, byte[]> depo = new ConcurrentHashMap<>(); // bellek-içi (gerçekte disk/S3)

        // YÜKLEME: multipart/form-data -> MultipartFile
        @PostMapping("/yukle")
        public Map<String, Object> yukle(@RequestParam("dosya") MultipartFile dosya) {
            depo.put(dosya.getOriginalFilename(), getBytes(dosya));
            return Map.of("ad", dosya.getOriginalFilename(),
                    "boyut", dosya.getSize(),
                    "tip", String.valueOf(dosya.getContentType()));
        }

        // İNDİRME: byte[] + Content-Disposition (tarayıcı "indir" diye algılar)
        @GetMapping("/indir/{ad}")
        public ResponseEntity<byte[]> indir(@PathVariable String ad) {
            byte[] icerik = depo.get(ad);
            if (icerik == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ad + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(icerik);
        }

        private static byte[] getBytes(MultipartFile f) {
            try { return f.getBytes(); } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    @Bean
    CommandLineRunner selfTest() {
        return args -> {
            RestClient c = RestClient.create("http://localhost:8080");
            System.out.println("\n========= DOSYA YÜKLE/İNDİR SELF-TEST =========");

            // YÜKLE: multipart gövde oluştur (dosya adı + içerik).
            byte[] icerik = "Merhaba, bu bir test dosyasıdır.".getBytes(StandardCharsets.UTF_8);
            MultiValueMap<String, Object> govde = new LinkedMultiValueMap<>();
            govde.add("dosya", new ByteArrayResource(icerik) {
                @Override public String getFilename() { return "rapor.txt"; } // dosya adı şart
            });
            String yuklemeSonuc = c.post().uri("/dosya/yukle")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(govde).retrieve().body(String.class);
            System.out.println("YÜKLE  -> " + yuklemeSonuc);

            // İNDİR: yüklenen dosyayı geri al.
            byte[] inen = c.get().uri("/dosya/indir/rapor.txt").retrieve().body(byte[].class);
            System.out.println("İNDİR  -> içerik: '" + new String(inen, StandardCharsets.UTF_8) + "'");
            System.out.println("İçerik aynı mı? " + java.util.Arrays.equals(icerik, inen));
            System.out.println("===============================================");
        };
    }
}
