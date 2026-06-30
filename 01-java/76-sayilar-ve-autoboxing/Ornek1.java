// Ornek1: Wrapper (sarmalayıcı) sınıflar — Integer, Long, Double... ve yardımcı metotları.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // İlkel tip (int) vs sarmalayıcı (Integer): wrapper bir NESNEDİR, metotları/sabitleri vardır.

        // Parse: String -> sayı
        int sayi = Integer.parseInt("123");
        double ondalik = Double.parseDouble("3.14");
        System.out.println("parseInt: " + sayi + ", parseDouble: " + ondalik);

        // valueOf: String/ilkel -> wrapper nesnesi
        Integer kutulu = Integer.valueOf(42);
        System.out.println("valueOf: " + kutulu);

        // Sınırlar
        System.out.println("\nInteger MIN/MAX: " + Integer.MIN_VALUE + " .. " + Integer.MAX_VALUE);
        System.out.println("Long MAX        : " + Long.MAX_VALUE);
        System.out.println("Double MAX      : " + Double.MAX_VALUE);

        // Taban dönüşümleri
        System.out.println("\n255 ikilik : " + Integer.toBinaryString(255));
        System.out.println("255 onaltılık: " + Integer.toHexString(255));
        System.out.println("'1010' (ikilik) -> " + Integer.parseInt("1010", 2));

        // Karşılaştırma ve yardımcılar
        System.out.println("\ncompare(3,9): " + Integer.compare(3, 9));
        System.out.println("max(3,9): " + Integer.max(3, 9) + ", sum: " + Integer.sum(3, 9));
        System.out.println("'A' karakter kodu (int): " + Integer.valueOf('A')); // 'A' kodu 65

        System.out.println("""

                --- Wrapper (sarmalayıcı) sınıflar ---
                Her ilkel tipin bir nesne karşılığı vardır: int->Integer, long->Long, double->Double,
                  boolean->Boolean, char->Character, byte/short/float->Byte/Short/Float.
                Neden? Koleksiyonlar/jenerikler yalnızca NESNE tutar (List<Integer>, Map<...>); null olabilir.
                Yararlı statikler: parseXxx (String->ilkel), valueOf, MIN/MAX_VALUE, toBinaryString/toHexString,
                  compare/sum/max. Sonraki örnek: otomatik kutulama (autoboxing) ve tuzakları.""");
    }
}
