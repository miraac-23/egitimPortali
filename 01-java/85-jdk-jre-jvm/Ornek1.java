// Ornek1: Çalışan ortamı tanımak — sürüm, satıcı, kurulum yeri.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        System.out.println("Java sürümü (JDK/JRE): " + System.getProperty("java.version"));
        System.out.println("Çalışma ortamı (JRE) : " + System.getProperty("java.runtime.name"));
        System.out.println("Sanal makine (JVM)   : " + System.getProperty("java.vm.name"));
        System.out.println("Satıcı (vendor)      : " + System.getProperty("java.vendor"));
        System.out.println("Kurulum dizini       : " + System.getProperty("java.home"));

        System.out.println("""

                --- JDK vs JRE vs JVM ---
                JVM (Java Virtual Machine): bytecode'u ÇALIŞTIRAN soyut makine. (Tek başına dağıtılmaz.)
                JRE (Java Runtime Environment): JVM + çalıştırmak için gereken standart KÜTÜPHANELER.
                  -> Sadece çalıştırmak için yeterli (derleyici YOK).
                JDK (Java Development Kit): JRE + GELİŞTİRME araçları (javac derleyici, jar, javadoc, jdb...).
                  -> Geliştirmek (derlemek) için gerekli.
                İç içe geçmedir: JDK ⊃ JRE ⊃ JVM. Bu örnek bir JDK üzerinde çalışıyor (sonraki örnek kanıtlıyor).
                Not: Java 11'den beri ayrı JRE indirmesi resmi olarak kaldırıldı; jlink ile özel çalışma zamanı üretilir.""");
    }
}
