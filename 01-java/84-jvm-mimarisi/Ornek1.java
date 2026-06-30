// Ornek1: JVM çalışma zamanı — bellek alanları ve sistem bilgileri.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();

        // JVM'i tanıyalım:
        System.out.println("JVM adı   : " + System.getProperty("java.vm.name"));
        System.out.println("JVM sürümü: " + System.getProperty("java.vm.version"));
        System.out.println("Java sürümü: " + System.getProperty("java.version"));
        System.out.println("Mimari    : " + System.getProperty("os.arch"));

        // Heap (yığın) bellek alanı — nesnelerin yaşadığı yer (GC burada çalışır):
        System.out.println("\nHeap belleği:");
        System.out.println("  maks      : " + mb(rt.maxMemory()) + " MB");
        System.out.println("  ayrılan   : " + mb(rt.totalMemory()) + " MB");
        System.out.println("  kullanılan: " + mb(rt.totalMemory() - rt.freeMemory()) + " MB");

        // İşlemci sayısı (paralellik kararlarında kullanılır):
        System.out.println("\nKullanılabilir çekirdek: " + rt.availableProcessors());

        System.out.println("""

                --- JVM mimarisi: çalışma zamanı ---
                JVM, derlenmiş BYTECODE'u (.class) çalıştıran sanal makinedir. Başlıca bellek alanları:
                  HEAP   : tüm nesnelerin yaşadığı paylaşılan alan (GC burada çalışır; genç/yaşlı kuşak).
                  STACK  : her thread'e özel; metot çağrıları + yerel değişkenler (StackOverflow burada).
                  METASPACE: sınıf meta-verileri (sınıf yapısı, metot bilgisi).
                  PC register & native method stack: yürütme konumu ve yerel (JNI) çağrılar.
                Bileşenler: Class Loader (yükle), Runtime Data Areas (bellek), Execution Engine (yorumlayıcı + JIT + GC).""");
    }

    static long mb(long b) { return b / (1024 * 1024); }
}
