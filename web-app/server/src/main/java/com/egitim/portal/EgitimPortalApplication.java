package com.egitim.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Eğitim Portalı API — giriş noktası.
 *
 * Bu uygulama iki şeyi sunar:
 *  1) İçerik API'si  : Depodaki Java & Spring eğitim klasörlerini (README + örnek kodlar)
 *                      dosya sisteminden okuyup REST olarak React istemcisine sunar.
 *  2) Task demo API'si: 03-spring ornek-uygulama'daki Görev Yönetimi REST API'sinin
 *                       aynısı — istemcideki "Canlı Demo" ekranının uçtan uca çalışması için.
 */
@SpringBootApplication
public class EgitimPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgitimPortalApplication.class, args);
    }
}
