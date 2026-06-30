// Ornek1: BitSet — bit dizisi: küçük bellekte çok sayıda boolean bayrak ve küme işlemleri.
// Çalıştırma: java Ornek1.java
import java.util.BitSet;

public class Ornek1 {

    public static void main(String[] args) {
        BitSet bayraklar = new BitSet(64); // 64 bitlik (gerektiğinde büyür)

        // set/get/clear/flip
        bayraklar.set(1);
        bayraklar.set(3);
        bayraklar.set(5);
        System.out.println("BitSet: " + bayraklar + " (set edilen bitler)");
        System.out.println("bit 3 açık mı? " + bayraklar.get(3));
        System.out.println("bit 2 açık mı? " + bayraklar.get(2));
        bayraklar.clear(3);      // kapat
        bayraklar.flip(2);       // tersine çevir (kapalı->açık)
        System.out.println("clear(3)+flip(2) sonrası: " + bayraklar);
        System.out.println("Açık bit sayısı (cardinality): " + bayraklar.cardinality());

        // KÜME İŞLEMLERİ — iki bit kümesi arasında AND/OR/XOR
        BitSet a = new BitSet(); a.set(0); a.set(1); a.set(2);  // {0,1,2}
        BitSet b = new BitSet(); b.set(1); b.set(2); b.set(3);  // {1,2,3}

        BitSet kesisim = (BitSet) a.clone(); kesisim.and(b);     // {1,2}
        BitSet birlesim = (BitSet) a.clone(); birlesim.or(b);    // {0,1,2,3}
        BitSet farkli = (BitSet) a.clone(); farkli.xor(b);       // {0,3}
        System.out.println("\na=" + a + ", b=" + b);
        System.out.println("AND (kesişim): " + kesisim);
        System.out.println("OR  (birleşim): " + birlesim);
        System.out.println("XOR (simetrik fark): " + farkli);

        System.out.println("""

                --- BitSet ---
                Otomatik büyüyen bir BİT dizisidir: her bayrak yalnızca 1 bit yer kaplar (boolean[]'dan çok daha kompakt).
                set/clear/flip/get tek bit; cardinality açık bit sayısı; nextSetBit ile gezinme.
                and/or/xor/andNot ile küme işlemleri (çok hızlı, bit düzeyinde).
                Kullanım: çok sayıda açık/kapalı bayrak, varlık kümeleri, asal eleği (sonraki örnek), izin maskeleri.""");
    }
}
