// Ornek2: Javadoc — sınıf/alan dokümantasyonu, @since/@deprecated ve satır içi etiketler.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    /**
     * Basit bir banka hesabını temsil eder.
     * <p>
     * Bakiye her zaman sıfır veya pozitiftir; {@link #cek(double)} bu kuralı korur.
     *
     * @author Eğitim Portalı
     * @since 1.0
     */
    static class Hesap {
        /** Hesabın güncel bakiyesi (TL). Asla negatif olmaz. */
        private double bakiye;

        /**
         * Hesaba para yatırır.
         *
         * @param tutar yatırılacak tutar; pozitif olmalı
         */
        public void yatir(double tutar) {
            if (tutar > 0) bakiye += tutar;
        }

        /**
         * Bakiyeyi kontrol ederek para çeker.
         *
         * @param tutar çekilecek tutar
         * @return işlem başarılıysa {@code true}, yetersiz bakiyede {@code false}
         */
        public boolean cek(double tutar) {
            if (tutar > 0 && tutar <= bakiye) { bakiye -= tutar; return true; }
            return false;
        }

        /**
         * Bakiyeyi kuruş cinsinden döndürür.
         *
         * @return kuruş bakiye
         * @deprecated Bunun yerine {@link #getBakiye()} kullanın (TL döndürür).
         */
        @Deprecated(since = "2.0")
        public long getBakiyeKurus() { return (long) (bakiye * 100); }

        /** @return TL cinsinden güncel bakiye */
        public double getBakiye() { return bakiye; }
    }

    public static void main(String[] args) {
        Hesap h = new Hesap();
        h.yatir(1000);
        System.out.println("çek(400) -> " + h.cek(400) + ", bakiye=" + h.getBakiye());
        System.out.println("çek(800) -> " + h.cek(800) + " (yetersiz), bakiye=" + h.getBakiye());

        System.out.println("""

                --- Sınıf dokümantasyonu, @deprecated, satır içi etiketler ---
                Sınıf/alan/metot HEPSİ Javadoc ile belgelenebilir. HTML (<p>, <ul>) kullanılabilir.
                @since: hangi sürümde eklendi. @deprecated: neden kullanılmamalı + alternatif (genelde @Deprecated ile birlikte).
                Satır içi {@link #metot}: başka bir öğeye tıklanabilir bağlantı (IDE'de gezinme).
                ÜRETİM: 'javadoc -d docs *.java' ile HTML dok üretilir; Maven/Gradle eklentileriyle otomatikleşir.
                Spring/JDK API dokümanları (docs.oracle.com) bu Javadoc'lardan üretilir.""");
    }
}
