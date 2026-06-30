// Ornek2: Sistem özellikleri (System Properties) ve ortam değişkenleri.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // System.getProperty: JVM/işletim sistemi hakkında hazır özellikler.
        System.out.println("Java sürümü   : " + System.getProperty("java.version"));
        System.out.println("Java sağlayıcı : " + System.getProperty("java.vendor"));
        System.out.println("İşletim sistemi: " + System.getProperty("os.name"));
        System.out.println("Kullanıcı dizini: " + System.getProperty("user.dir"));
        System.out.println("Dosya ayracı   : '" + System.getProperty("file.separator") + "'");
        System.out.println("Satır ayracı   : " + System.lineSeparator().replace("\n", "\\n"));

        // Varsayılan değerle güvenli okuma:
        System.out.println("Özel ayar (yok): " + System.getProperty("benim.ayarim", "varsayılan"));

        // Kendi sistem özelliğini ayarlama (JVM ömrü boyunca):
        System.setProperty("uygulama.modu", "uretim");
        System.out.println("Ayarlanan: " + System.getProperty("uygulama.modu"));

        // Ortam değişkenleri (environment variables) — işletim sisteminden:
        String path = System.getenv("PATH");
        System.out.println("\nPATH var mı? " + (path != null && !path.isEmpty()));
        System.out.println("HOME: " + System.getenv().getOrDefault("HOME", "(tanımsız)"));

        System.out.println("""

                --- Sistem özellikleri ve ortam değişkenleri ---
                System.getProperty(ad[, varsayilan]): JVM özellikleri (java.version, os.name, user.dir...).
                  -Dadi=deger ile komut satırından da verilir: java -Duygulama.modu=test ...
                System.setProperty: çalışma anında ayarlama (yalnızca bu JVM için).
                System.getenv(ad): işletim sistemi ortam değişkenleri (PATH, HOME...) — salt okunur.
                Fark: system property JVM'e özel ve değiştirilebilir; env var OS'ten gelir, salt okunur.""");
    }
}
