// Ornek1: Properties — anahtar-değer yapılandırması; yükleme ve kaydetme.
// Çalıştırma: java Ornek1.java
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

public class Ornek1 {

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();

        // Değer ayarlama (her anahtar ve değer String'tir)
        config.setProperty("db.url", "jdbc:postgresql://localhost/app");
        config.setProperty("db.user", "admin");
        config.setProperty("server.port", "8080");

        // Okuma — getProperty + varsayılan değer
        System.out.println("db.url: " + config.getProperty("db.url"));
        System.out.println("server.port: " + config.getProperty("server.port"));
        System.out.println("timeout (yok, varsayılan): " + config.getProperty("timeout", "30"));

        // KAYDET (store): .properties metin formatında yaz (gerçekte dosyaya/akışa)
        StringWriter sw = new StringWriter();
        config.store(sw, "Uygulama Yapılandırması");
        String metin = sw.toString();
        System.out.println("\n--- store() çıktısı (.properties formatı) ---");
        // İlk satır tarih yorumu olur; onu atlayıp gösterelim
        metin.lines().filter(l -> !l.startsWith("#")).forEach(l -> System.out.println(l));

        // YÜKLE (load): .properties metnini geri oku
        Properties yuklenen = new Properties();
        yuklenen.load(new StringReader("db.url=jdbc:mysql://db/app\ndb.user=root\ncache.enabled=true"));
        System.out.println("\nYüklenen db.url: " + yuklenen.getProperty("db.url"));
        System.out.println("cache.enabled: " + yuklenen.getProperty("cache.enabled"));

        System.out.println("""

                --- Properties ---
                Anahtar-değer (her ikisi de String) yapılandırma deposudur; Hashtable'dan türer.
                setProperty/getProperty (getProperty varsayılan değer alabilir).
                store(writer): .properties metin formatında yaz; load(reader): geri oku.
                .properties formatı: 'anahtar=değer', '#' yorum. XML için storeToXML/loadFromXML de var.
                Kullanım: uygulama ayarları, i18n metinleri. (Spring Boot bunun üzerine application.properties/yml sunar.)""");
    }
}
