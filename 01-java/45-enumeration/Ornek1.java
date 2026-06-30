// Ornek1: Enumeration — Iterator'ın atası (eski API'lerde hâlâ karşına çıkar).
// Çalıştırma: java Ornek1.java
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class Ornek1 {

    public static void main(String[] args) {
        // Vector ve Hashtable (eski koleksiyonlar) Enumeration döndüren metotlar sunar.
        Vector<String> sehirler = new Vector<>();
        sehirler.add("Ankara"); sehirler.add("İzmir"); sehirler.add("Bursa");

        // Enumeration: hasMoreElements() + nextElement() (Iterator'ın eski, uzun adlı hali).
        Enumeration<String> e = sehirler.elements();
        System.out.print("Enumeration ile: ");
        while (e.hasMoreElements()) {
            System.out.print(e.nextElement() + " ");
        }
        System.out.println();

        // Aynı Vector'ı modern Iterator ile gezmek (tercih edilen):
        Iterator<String> it = sehirler.iterator();
        System.out.print("Iterator ile   : ");
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // Hashtable da Enumeration verir (anahtarlar / değerler):
        Hashtable<String, Integer> tablo = new Hashtable<>();
        tablo.put("bir", 1); tablo.put("iki", 2);
        Enumeration<String> anahtarlar = tablo.keys();
        System.out.print("Hashtable anahtarları: ");
        while (anahtarlar.hasMoreElements()) System.out.print(anahtarlar.nextElement() + " ");
        System.out.println();

        System.out.println("""

                --- Enumeration vs Iterator ---
                Enumeration (Java 1.0): hasMoreElements() / nextElement(). SİLME yok.
                Iterator (Java 1.2)   : hasNext() / next() / remove(). Daha kısa, silme destekli, fail-fast.
                Bugün Iterator (ve for-each) tercih edilir; Enumeration'ı yalnızca eski API'lerde
                (Vector, Hashtable, StringTokenizer, ServletRequest başlıkları...) görürsün.""");
    }
}
