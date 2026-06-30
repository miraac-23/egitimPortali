// Ornek1: Constructor'lar — varsayılan, parametreli, aşırı yükleme, this() zincirleme.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    static class Urun {
        String ad;
        double fiyat;
        int stok;

        // 1) Parametresiz (no-arg) constructor — varsayılan değerlerle.
        Urun() {
            this("İsimsiz", 0.0, 0);   // this(): BAŞKA bir constructor'ı çağır (zincirleme)
            System.out.println("  no-arg constructor çalıştı");
        }

        // 2) Kısmi parametreli (overloading: aynı ad, farklı imza)
        Urun(String ad, double fiyat) {
            this(ad, fiyat, 0);        // tam constructor'a delege et (kod tekrarını önler)
            System.out.println("  2-arg constructor çalıştı");
        }

        // 3) Tam parametreli — asıl başlatma burada (tek yerde).
        Urun(String ad, double fiyat, int stok) {
            this.ad = ad;
            this.fiyat = fiyat;
            this.stok = stok;
            System.out.println("  3-arg constructor çalıştı");
        }

        @Override public String toString() { return ad + " " + fiyat + "TL stok:" + stok; }
    }

    public static void main(String[] args) {
        System.out.println("new Urun():");
        System.out.println("  -> " + new Urun());

        System.out.println("\nnew Urun(\"Klavye\", 450):");
        System.out.println("  -> " + new Urun("Klavye", 450));

        System.out.println("\nnew Urun(\"Mouse\", 250, 40):");
        System.out.println("  -> " + new Urun("Mouse", 250, 40));

        System.out.println("""

                --- Constructor'lar ---
                Constructor: nesne 'new' ile oluşturulurken çağrılan özel metot (dönüş tipi YOK, sınıf adıyla aynı).
                Görevi: nesneyi GEÇERLİ bir başlangıç durumuna getirmek.
                Hiç constructor yazmazsan derleyici boş bir VARSAYILAN constructor ekler (parametreli yazarsan EKLEMEZ).
                Aşırı yükleme (overloading): farklı parametrelerle birden çok constructor.
                this(...): aynı sınıftaki BAŞKA bir constructor'ı çağırır (zincirleme) -> başlatmayı TEK yerde topla, tekrarı önle.
                  (this(...) constructor'ın İLK satırı olmalı.)""");
    }
}
