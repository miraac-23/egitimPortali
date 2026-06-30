// Ornek2: ListIterator — çift yönlü gezinti + gezerken güncelleme/ekleme.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Ornek2 {

    public static void main(String[] args) {
        List<Integer> sayilar = new ArrayList<>(List.of(1, 2, 3, 4));

        // ListIterator yalnızca List'lerde vardır; ileri+geri gidebilir, set/add yapabilir.
        ListIterator<Integer> it = sayilar.listIterator();

        // İleri giderken her elemanı karesiyle DEĞİŞTİR (set).
        while (it.hasNext()) {
            int deger = it.next();
            it.set(deger * deger);   // mevcut elemanı güncelle
        }
        System.out.println("Kareler (set ile): " + sayilar);

        // Şimdi GERİ giderek yazdır (çift yönlülük).
        System.out.print("Geri gezinti: ");
        while (it.hasPrevious()) {
            System.out.print(it.previous() + " ");
        }
        System.out.println();

        // İleri giderken araya eleman EKLE (add).
        ListIterator<Integer> it2 = sayilar.listIterator();
        while (it2.hasNext()) {
            int d = it2.next();
            if (d == 4) it2.add(-1);   // 4'ten (kare: 16... burada 1,4,9,16) sonra -1 ekle
        }
        System.out.println("Ekleme sonrası: " + sayilar);
        System.out.println("Index bilgisi: nextIndex=" + it2.nextIndex());

        System.out.println("""

                --- ListIterator ---
                Iterator'ın List'lere özel, güçlü sürümü:
                  - çift yönlü: hasNext/next ve hasPrevious/previous,
                  - gezerken set(e) ile GÜNCELLEME, add(e) ile EKLEME,
                  - nextIndex()/previousIndex() ile konum bilgisi.
                Bir listeyi gezerken yerinde değiştirmen gerekiyorsa ListIterator idealdir.""");
    }
}
