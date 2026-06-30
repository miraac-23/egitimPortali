// Ornek2: Sınıf yükleme ve bytecode — Class nesnesi, JVM bir sınıfı nasıl temsil eder.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    static class Ornek { int x; void selam() {} void hesapla(int n) {} }

    public static void main(String[] args) {
        // Her yüklenen sınıf, JVM'de bir Class nesnesiyle temsil edilir (meta-veri).
        Class<?> sinif = Ornek.class;
        System.out.println("Sınıf adı     : " + sinif.getName());
        System.out.println("Basit ad      : " + sinif.getSimpleName());
        System.out.println("Metot sayısı  : " + sinif.getDeclaredMethods().length);
        System.out.println("Alan sayısı   : " + sinif.getDeclaredFields().length);

        // Sınıfı kim yükledi? (Class Loader hiyerarşisi)
        System.out.println("\nOrnek2'yi yükleyen ClassLoader: " + Ornek2.class.getClassLoader());
        // JDK çekirdek sınıflarını (String) bootstrap class loader yükler -> null görünür.
        System.out.println("String'i yükleyen ClassLoader  : " + String.class.getClassLoader()
                + " (null = bootstrap loader)");

        // Bytecode: Java kaynağı önce .class (bytecode) olur, sonra JVM çalıştırır.
        System.out.println("\nBu sınıf bytecode olarak yüklendi ve yorumlanıp JIT ile derlenir.");

        System.out.println("""

                --- Sınıf yükleme ve bytecode ---
                Akış: .java --(javac)--> .class (BYTECODE) --(JVM)--> çalıştırma.
                Bytecode platformdan BAĞIMSIZDIR; aynı .class her işletim sisteminde çalışır ('write once, run anywhere').
                Her yüklü sınıf bir Class<?> nesnesiyle temsil edilir (reflection bunu kullanır - topic 16).
                Class Loader hiyerarşisi (gecikmeli/lazy yükler): Bootstrap (JDK çekirdeği) -> Platform -> Application.
                JVM gerçek bir CPU'yu taklit eder: yığın tabanlı (stack-based) bir komut seti yürütür.""");
    }
}
