// Ornek3: static ve final.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        System.out.println("Başlangıç üretilen nesne sayısı: " + Urun.getUretilenAdet());

        Urun u1 = new Urun("Klavye", 450.0);
        Urun u2 = new Urun("Mouse", 250.0);
        Urun u3 = new Urun("Monitör", 3200.0);

        // static alan tüm nesneler arasında PAYLAŞILIR; nesneye değil sınıfa aittir.
        System.out.println("Üretilen toplam ürün: " + Urun.getUretilenAdet());

        // static yardımcı metot: nesne gerektirmez, sınıf üzerinden çağrılır.
        System.out.println("KDV'li fiyat (Klavye): " + Urun.kdvEkle(u1.getFiyat()));

        u1.yaz();
        u2.yaz();
        u3.yaz();
    }
}

class Urun {
    // final sabit: bir kez atanır, değişmez. Sınıf düzeyinde paylaşılan bir sabit.
    public static final double KDV_ORANI = 0.20;

    // static alan: sınıfa ait tek bir kopya, tüm nesneler paylaşır.
    private static int uretilenAdet = 0;

    // Örnek (instance) alanları: her nesneye özel.
    private final String ad;   // final: oluşturulduktan sonra değişmez
    private double fiyat;

    public Urun(String ad, double fiyat) {
        this.ad = ad;
        this.fiyat = fiyat;
        uretilenAdet++; // her yeni nesnede paylaşılan sayaç artar
    }

    public static int getUretilenAdet() {
        return uretilenAdet;
    }

    public static double kdvEkle(double tutar) {
        return tutar * (1 + KDV_ORANI);
    }

    public double getFiyat() {
        return fiyat;
    }

    public void yaz() {
        System.out.printf("%-8s -> %.2f TL (KDV'li %.2f TL)%n", ad, fiyat, kdvEkle(fiyat));
    }
}
