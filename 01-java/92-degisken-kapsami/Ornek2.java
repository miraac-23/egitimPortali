// Ornek2: Gölgeleme (shadowing) — aynı isimli yerel/parametre değişkeni, alanı "gölgeler".
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    static class Kisi {
        private String ad;   // örnek alanı
        private int yas;

        // Parametre adları alanlarla AYNI -> parametreler alanları GÖLGELER.
        Kisi(String ad, int yas) {
            // 'ad' burada PARAMETREyi gösterir (alanı değil). Alana erişmek için 'this.' ŞART.
            this.ad = ad;     // this.ad = alan, ad = parametre
            this.yas = yas;
        }

        void yanlisAtama(int yas) {
            // this OLMADAN: 'yas = yas' kendine atama -> alan DEĞİŞMEZ (sık yapılan hata!)
            yas = yas; // etkisiz
        }

        void dogruAtama(int yas) {
            this.yas = yas;   // this ile alan güncellenir
        }

        @Override public String toString() { return ad + " (" + yas + ")"; }
    }

    public static void main(String[] args) {
        Kisi k = new Kisi("Ada", 30);
        System.out.println("Başlangıç: " + k);

        k.yanlisAtama(99);
        System.out.println("yanlisAtama(99) sonrası: " + k + "  (this yok -> DEĞİŞMEDİ)");

        k.dogruAtama(99);
        System.out.println("dogruAtama(99) sonrası : " + k + "  (this ile -> güncellendi)");

        // Blok gölgeleme: Java'da yerel değişken, başka bir YEREL değişkeni gölgeleyemez (derleme hatası);
        // ancak yerel değişken bir ALANI gölgeleyebilir.
        System.out.println("""

                --- Gölgeleme (shadowing) ---
                Bir yerel değişken/parametre, aynı isimli bir ALANLA aynı kapsamdaysa onu 'gölgeler':
                  o isim artık YEREL olanı gösterir; alana erişmek için 'this.alan' kullanılır.
                KLASİK HATA: constructor/setter'da 'yas = yas' -> parametreyi kendine atar, ALAN değişmez!
                  Doğrusu: 'this.yas = yas;'. (IDE'ler bunu uyarır.)
                NOT: iki YEREL değişken aynı kapsamda aynı ada sahip OLAMAZ (derleme hatası); gölgeleme yalnızca alan-yerel arasında olur.
                İyi pratik: gölgelemeyi bilinçli kullan (constructor parametreleri) ve her zaman 'this.' ile netleştir.""");
    }
}
