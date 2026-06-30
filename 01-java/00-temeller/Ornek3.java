// Ornek3: printf ile biçimli çıktı ve küçük bir hesaplama.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    public static void main(String[] args) {
        // Gerçek bir programda bu değerler kullanıcıdan (Scanner ile) alınabilir.
        // Çalıştırıcıda girdi olmadığı için burada sabit değerlerle simüle ediyoruz.
        double anapara = 10_000.0; // TL
        double yillikFaiz = 0.18;  // %18
        int yil = 3;

        System.out.println("=== Basit Faiz Hesabı ===");
        // printf: %s metin, %d tam sayı, %f ondalık, %.2f iki basamak, %% gerçek % işareti, %n satır sonu
        System.out.printf("Anapara     : %,.2f TL%n", anapara);
        System.out.printf("Yıllık faiz : %.0f%%%n", yillikFaiz * 100);
        System.out.printf("Süre        : %d yıl%n", yil);

        double faizTutari = anapara * yillikFaiz * yil; // basit faiz formülü
        double toplam = anapara + faizTutari;

        System.out.println("-------------------------------");
        System.out.printf("Faiz tutarı : %,.2f TL%n", faizTutari);
        System.out.printf("Toplam      : %,.2f TL%n", toplam);

        System.out.println("\n=== Dikdörtgen Alan/Çevre ===");
        double en = 4.5, boy = 7.2;
        // String.format, printf ile aynı biçimlendirmeyi yapar ama metni döndürür.
        String satir = String.format("en=%.1f, boy=%.1f -> alan=%.2f, çevre=%.2f",
                en, boy, en * boy, 2 * (en + boy));
        System.out.println(satir);
    }
}
