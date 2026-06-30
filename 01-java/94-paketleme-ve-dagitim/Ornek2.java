// Ornek2: Çalışma zamanı sürümü — Multi-Release JAR'ların dayandığı Runtime.version().
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // Runtime.version() (Java 9+): çalışan JDK'nın sürümünü yapısal olarak verir.
        Runtime.Version v = Runtime.version();
        System.out.println("Çalışan sürüm: " + v);
        System.out.println("  feature (ana sürüm): " + v.feature());   // 21, 17 ...
        System.out.println("  interim            : " + v.interim());
        System.out.println("  update             : " + v.update());

        // Multi-Release JAR mantığı: aynı JAR, FARKLI Java sürümleri için farklı sınıf sunar.
        // Çalışma anında sürüme göre uygun uygulamayı seçmek (basit simülasyon):
        int feature = v.feature();
        String strateji;
        if (feature >= 21) strateji = "Java 21+ yolu (ör. sanal thread'ler)";
        else if (feature >= 17) strateji = "Java 17 yolu (sealed/records)";
        else strateji = "eski yol (uyumluluk)";
        System.out.println("\nSürüme göre seçilen strateji: " + strateji);

        // Koşullu özellik kullanımı için sürüm karşılaştırma:
        boolean modern = v.feature() >= 17;
        System.out.println("Modern (17+) özellikler kullanılabilir mi? " + modern);

        System.out.println("""

                --- Multi-Release JAR ve sürüm tespiti ---
                Runtime.version(): çalışan JDK sürümünü yapısal verir (feature/interim/update/patch).
                MULTI-RELEASE JAR (MR-JAR, Java 9+): tek bir JAR içinde, farklı Java sürümleri için
                  FARKLI derlenmiş sınıflar bulunabilir (META-INF/versions/17/, .../21/ ...).
                  JVM, çalışırken kendi sürümüne uygun sınıfı OTOMATİK seçer.
                Faydası: tek artefakt hem eski hem yeni JDK'da en uygun kodu çalıştırır (kütüphaneler için ideal).
                MANIFEST'te: Multi-Release: true. Üretimi: 'jar --release 21 ...' veya build eklentileri.""");
    }
}
