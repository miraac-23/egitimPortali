// Ornek2: Çok alanlı compareTo ve equals ile tutarlılık.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ornek2 {

    public static void main(String[] args) {
        List<Kisi> kisiler = new ArrayList<>(List.of(
                new Kisi("Yılmaz", "Ada", 30),
                new Kisi("Yılmaz", "Burak", 25),
                new Kisi("Demir", "Ada", 40),
                new Kisi("Demir", "Ada", 22)));

        kisiler.sort(null); // doğal sıra: soyad -> ad -> yaş
        System.out.println("Çok alanlı doğal sıra (soyad, ad, yaş):");
        kisiler.forEach(k -> System.out.println("  " + k));

        System.out.println("""

                --- Çok alanlı compareTo ve sözleşme ---
                Birden çok ölçüte göre sıralamak için compareTo'da ZİNCİRLEME karşılaştırma yap:
                önce soyad; eşitse ad; o da eşitse yaş. İlk sıfır-olmayan sonuç kazanır.
                Sözleşme kuralları:
                  - işaret tutarlı (a<b ise b>a), geçişli (a<b, b<c => a<c),
                  - mümkünse equals ile UYUMLU: compareTo==0 oldugunda equals==true olmalı
                    (TreeSet/TreeMap eşitliği compareTo ile belirler; uyumsuzluk sürprizlere yol açar).""");
    }
}

class Kisi implements Comparable<Kisi> {
    private final String soyad, ad;
    private final int yas;
    Kisi(String soyad, String ad, int yas) { this.soyad = soyad; this.ad = ad; this.yas = yas; }

    @Override
    public int compareTo(Kisi o) {
        int s = this.soyad.compareTo(o.soyad);       // 1) soyada göre
        if (s != 0) return s;
        int a = this.ad.compareTo(o.ad);             // 2) eşitse ada göre
        if (a != 0) return a;
        return Integer.compare(this.yas, o.yas);     // 3) o da eşitse yaşa göre
    }

    // compareTo ile UYUMLU equals/hashCode (aynı üç alan).
    @Override public boolean equals(Object o) {
        return o instanceof Kisi k && yas == k.yas
                && soyad.equals(k.soyad) && ad.equals(k.ad);
    }
    @Override public int hashCode() { return Objects.hash(soyad, ad, yas); }
    @Override public String toString() { return soyad + " " + ad + " (" + yas + ")"; }
}
