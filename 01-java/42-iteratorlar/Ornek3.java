// Ornek3: Kendi Iterable'ını yaz — sınıfın for-each ile gezilebilsin.
// Çalıştırma: java Ornek3.java
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Ornek3 {

    public static void main(String[] args) {
        // Kendi koleksiyon benzeri sınıfımız (1..n aralığı). Iterable olduğu için for-each çalışır.
        Aralik aralik = new Aralik(1, 5);
        System.out.print("for-each ile: ");
        for (int sayi : aralik) {
            System.out.print(sayi + " ");
        }
        System.out.println();

        // Iterable olduğu için Stream/forEach gibi araçlara da köprü kurulabilir.
        long toplam = 0;
        for (int s : aralik) toplam += s;
        System.out.println("Toplam: " + toplam);

        System.out.println("""

                --- Kendi Iterable'ını yazmak ---
                Bir sınıf Iterable<T> uygularsa (iterator() metodu), for-each döngüsüyle gezilebilir.
                iterator(), Iterator<T> döndürür: hasNext() + next() (gerekirse remove()).
                Bu, kendi veri yapılarını (ağaç, grafik, sayfa akışı...) dilin doğal döngüsüne bağlamanı sağlar.
                JDK'daki List/Set/Map.values() hepsi bu sözleşmeyi uygular.""");
    }
}

// Iterable<Integer>: for-each ile gezilebilir bir tam sayı aralığı.
class Aralik implements Iterable<Integer> {
    private final int bas, son;
    Aralik(int bas, int son) { this.bas = bas; this.son = son; }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int simdiki = bas;
            @Override public boolean hasNext() { return simdiki <= son; }
            @Override public Integer next() {
                if (!hasNext()) throw new NoSuchElementException();
                return simdiki++;
            }
        };
    }
}
