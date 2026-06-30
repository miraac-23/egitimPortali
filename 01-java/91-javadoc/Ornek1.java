// Ornek1: Javadoc — kodu belgeleyen özel yorumlar (/** ... */) ve etiketler.
// Çalıştırma: java Ornek1.java   (sınıf çalışır; Javadoc yorumları 'javadoc' aracıyla HTML olur)
public class Ornek1 {

    /**
     * İki tam sayıyı güvenli biçimde böler.
     * <p>Bu metot, sıfıra bölme durumunda bir istisna fırlatır.</p>
     *
     * @param bolunen bölünecek sayı (pay)
     * @param bolen   bölen sayı (payda); {@code 0} olamaz
     * @return {@code bolunen / bolen} tam sayı bölümü
     * @throws ArithmeticException {@code bolen} sıfır ise
     * @see #carp(int, int)
     */
    public static int bol(int bolunen, int bolen) {
        if (bolen == 0) throw new ArithmeticException("sıfıra bölme");
        return bolunen / bolen;
    }

    /**
     * İki sayıyı çarpar.
     *
     * @param a ilk çarpan
     * @param b ikinci çarpan
     * @return çarpım {@code a * b}
     */
    public static int carp(int a, int b) {
        return a * b;
    }

    public static void main(String[] args) {
        System.out.println("bol(20, 4) = " + bol(20, 4));
        System.out.println("carp(6, 7) = " + carp(6, 7));
        try {
            bol(5, 0);
        } catch (ArithmeticException e) {
            System.out.println("bol(5, 0) -> " + e.getMessage() + " (Javadoc'ta @throws ile belgelendi)");
        }

        System.out.println("""

                --- Javadoc temelleri ---
                /** ... */ (üç değil ÇİFT yıldızla başlar) -> Javadoc yorumu; 'javadoc' aracı bunları HTML API dokümanına çevirir.
                İlk cümle ÖZETtir (listelerde görünür). Blok etiketleri:
                  @param ad açıklama   -> her parametre
                  @return açıklama     -> dönüş değeri
                  @throws Tip açıklama -> fırlatılan istisnalar
                  @see / @since / @deprecated / @author / @version
                Satır içi etiketler: {@code kod}, {@link Sınıf#metot}, {@literal}.
                İyi Javadoc: NE yaptığını ve sözleşmesini (ön/son koşullar) anlatır; NASIL yapıldığını değil.""");
    }
}
