// Ornek1: Sınıf nereden yüklendi? — code source, classpath ve kaynak (resource) erişimi.
// (Paketleme: sınıflar bir dizinden mi yoksa JAR'dan mı geliyor — çalışma anında görülebilir.)
// Çalıştırma: java Ornek1.java
import java.security.CodeSource;

public class Ornek1 {

    public static void main(String[] args) {
        // Bu sınıf NEREDEN yüklendi? (bir dizin veya bir .jar dosyası)
        CodeSource cs = Ornek1.class.getProtectionDomain().getCodeSource();
        System.out.println("Bu sınıfın kaynağı: " + (cs != null ? cs.getLocation() : "(bootstrap)"));

        // JDK çekirdek sınıfları modüllerden gelir (jrt: şeması):
        System.out.println("String sınıfı modülü: " + String.class.getModule());

        // Classpath: JVM sınıfları nerede arar?
        System.out.println("\nClasspath: " + System.getProperty("java.class.path"));

        // Kaynak (resource) erişimi: paketlenmiş dosyalar ClassLoader üzerinden okunur.
        // (Gerçek projede: config.properties, şablonlar, görseller JAR içinde paketlenir.)
        var url = Ornek1.class.getResource("Ornek1.class");
        System.out.println("\nKendi .class konumu (getResource): " + url);

        System.out.println("""

                --- Paketleme: sınıf yükleme ve kaynaklar ---
                Derlenen sınıflar ya bir DİZİNDE (geliştirme) ya da bir JAR'da (dağıtım) bulunur.
                getProtectionDomain().getCodeSource(): bir sınıfın nereden (dizin/jar) yüklendiğini söyler.
                java.class.path: JVM'in sınıf/kaynak ararken baktığı yollar.
                Kaynaklar (resource): kod olmayan dosyalar (config, görsel, şablon) JAR'a paketlenir ve
                  ClassLoader.getResource/getResourceAsStream ile okunur (dosya yolu DEĞİL — classpath'ten).
                JDK çekirdeği modüllerdedir (jrt:); senin kodun classpath veya module path'ten yüklenir.""");
    }
}
