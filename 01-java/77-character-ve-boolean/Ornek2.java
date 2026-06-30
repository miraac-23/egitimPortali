// Ornek2: Boolean sınıfı — ayrıştırma, mantıksal işlemler ve üç-değerli (null) durum.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // parseBoolean: "true" (büyük/küçük fark etmez) -> true; DİĞER HER ŞEY -> false.
        System.out.println("parseBoolean('true') : " + Boolean.parseBoolean("true"));
        System.out.println("parseBoolean('TRUE') : " + Boolean.parseBoolean("TRUE"));
        System.out.println("parseBoolean('evet') : " + Boolean.parseBoolean("evet")); // false!
        System.out.println("parseBoolean('1')    : " + Boolean.parseBoolean("1"));    // false!

        // Statik mantıksal işlemler (okunaklı):
        System.out.println("\nlogicalAnd(true,false): " + Boolean.logicalAnd(true, false));
        System.out.println("logicalOr(true,false) : " + Boolean.logicalOr(true, false));
        System.out.println("logicalXor(true,true) : " + Boolean.logicalXor(true, true));

        // ÜÇ DEĞERLİ MANTIK: Boolean (wrapper) null OLABİLİR -> true/false/null (örn. DB'den gelen).
        Boolean onayli = null;   // "henüz bilinmiyor"
        System.out.println("\nonayli = " + onayli);
        // Tuzak: null Boolean'ı if'te kullanmak (unboxing) NPE atar.
        try {
            if (onayli) System.out.println("onaylı");  // null unboxing -> NPE
        } catch (NullPointerException e) {
            System.out.println("null Boolean if içinde -> NullPointerException!");
        }
        // Güvenli: önce null kontrolü veya Boolean.TRUE.equals
        System.out.println("Güvenli kontrol: " + Boolean.TRUE.equals(onayli)); // false (NPE yok)

        System.out.println("""

                --- Boolean sınıfı ---
                parseBoolean: yalnızca \"true\" (harf duyarsız) -> true; başka HER ŞEY false (\"1\", \"evet\" bile).
                logicalAnd/Or/Xor: statik mantıksal işlemler (okunaklı yardımcılar).
                Boolean WRAPPER null olabilir -> üç-değerli mantık (true/false/bilinmiyor).
                TUZAK: null Boolean'ı if/while koşulunda kullanmak unboxing NPE'si atar.
                  Güvenli yol: Boolean.TRUE.equals(x) veya önce null kontrolü.""");
    }
}
