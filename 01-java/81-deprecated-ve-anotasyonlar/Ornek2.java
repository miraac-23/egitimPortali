// Ornek2: @FunctionalInterface, @SafeVarargs ve meta-anotasyonlar.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    // @FunctionalInterface: bu arayüzün TEK soyut metotlu olduğunu garanti eder.
    // İkinci soyut metot eklersen derleyici HATA verir (lambda hedefi bozulmasın diye).
    @FunctionalInterface
    interface Donusturucu {
        int donustur(int x);
        // default void log() {}  // default eklenebilir; ama 2. SOYUT metot eklenemez
    }

    public static void main(String[] args) {
        Donusturucu kare = x -> x * x;       // functional interface -> lambda hedefi
        Donusturucu artir = x -> x + 1;
        System.out.println("kare(5) = " + kare.donustur(5));
        System.out.println("artir(5) = " + artir.donustur(5));

        // @SafeVarargs: jenerik varargs'ın güvenli olduğunu belirtir (uyarıyı bastırır).
        System.out.println("\nilkler: " + ilkUcu("a", "b", "c", "d", "e"));

        System.out.println("""

                --- @FunctionalInterface, @SafeVarargs, meta-anotasyonlar ---
                @FunctionalInterface: arayüzün TEK soyut metotlu kalmasını garanti eder (lambda hedefi). Belge + güvenlik.
                @SafeVarargs: jenerik varargs metotlarındaki 'unchecked/heap pollution' uyarısını güvenle bastırır.
                META-ANOTASYONLAR (anotasyonu tanımlarken kullanılır):
                  @Retention: anotasyon ne kadar yaşar (SOURCE/CLASS/RUNTIME — reflection için RUNTIME).
                  @Target: nereye uygulanabilir (TYPE, METHOD, FIELD...).
                  @Inherited, @Documented, @Repeatable.
                Kendi anotasyonunu tanımlama ve reflection ile okuma -> 'reflection-ve-annotations' konusu.""");
    }

    @SafeVarargs
    static <T> List<T> ilkUcu(T... ogeler) {
        return List.of(ogeler).subList(0, Math.min(3, ogeler.length));
    }
}
