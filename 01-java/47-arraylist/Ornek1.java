// Ornek1: ArrayList — temel işlemler (ekle, eriş, güncelle, sil, ara).
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.List;

public class Ornek1 {

    public static void main(String[] args) {
        List<String> gorevler = new ArrayList<>();

        // Ekleme
        gorevler.add("Kahvaltı");          // sona ekle
        gorevler.add("Spor");
        gorevler.add("Toplantı");
        gorevler.add(1, "Kod yaz");        // belirli indekse ekle
        System.out.println("Liste: " + gorevler);

        // Erişim ve güncelleme
        System.out.println("0. eleman: " + gorevler.get(0));
        gorevler.set(0, "Geç kahvaltı");   // indeksteki elemanı değiştir
        System.out.println("Güncel 0: " + gorevler.get(0));

        // Arama
        System.out.println("'Spor' var mı? " + gorevler.contains("Spor"));
        System.out.println("'Spor' indeksi: " + gorevler.indexOf("Spor"));
        System.out.println("Boyut: " + gorevler.size());

        // Silme: indekse göre VE değere göre
        gorevler.remove(1);                 // indeks 1
        gorevler.remove("Spor");            // değer "Spor"
        System.out.println("Silmeler sonrası: " + gorevler);

        // Gezinme (for-each)
        System.out.print("Kalan görevler: ");
        for (String g : gorevler) System.out.print(g + "; ");
        System.out.println();

        System.out.println("""

                --- ArrayList temelleri ---
                İçte dinamik bir dizi tutar: indeksli erişim O(1), sona ekleme amortize O(1).
                add/get/set/remove/contains/indexOf/size en sık kullanılan metotlardır.
                DİKKAT: remove(int) indeksi siler, remove(Object) değeri siler. Integer listede
                remove(2) -> indeks 2; remove(Integer.valueOf(2)) -> değer 2.""");
    }
}
