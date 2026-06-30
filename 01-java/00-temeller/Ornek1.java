// Ornek1: İlk programın ve çalıştığın ortamı tanıman.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Java'da her program bir main metodundan başlar.
        // System.out.println bir satır yazıp alt satıra geçer.
        System.out.println("Merhaba! Java dünyasına hoş geldin.");
        System.out.println("Bu, çalıştırdığın ilk Java programın.");

        System.out.println();
        System.out.println("--- Çalışma Ortamı ---");

        // System.getProperty ile JVM'in ve işletim sisteminin bilgilerini okuyoruz.
        // Bu değerler programın derlenip çalıştığı ortamı anlatır.
        System.out.println("Java sürümü      : " + System.getProperty("java.version"));
        System.out.println("Java sağlayıcısı : " + System.getProperty("java.vendor"));
        System.out.println("İşletim sistemi  : " + System.getProperty("os.name"));

        System.out.println();
        // Aynı kaynak kodu Windows, Linux ve macOS üzerinde değiştirmeden çalışır.
        // Java'nın bu özelliğine "Write Once, Run Anywhere" (bir kez yaz, her yerde çalıştır) denir.
        System.out.println("Bu program herhangi bir değişiklik yapmadan");
        System.out.println("Windows, Linux veya macOS üzerinde de çalışır.");
    }
}
