// Ornek1: Scanner — metni parçalara ayırıp tip tip okumak.
// (Portal etkileşimsiz çalışır; bu yüzden System.in yerine bir String'i kaynak alıyoruz.
//  Gerçek uygulamada: new Scanner(System.in) ile klavyeden okunur — README'ye bak.)
// Çalıştırma: java Ornek1.java
import java.util.Scanner;

public class Ornek1 {

    public static void main(String[] args) {
        // Scanner herhangi bir kaynaktan okuyabilir: System.in, File, String...
        String girdi = "Ada 30 175.5 true";
        Scanner sc = new Scanner(girdi);

        // Boşlukla ayrılmış belirteçleri (token) TİPİNE göre oku:
        String ad = sc.next();
        int yas = sc.nextInt();
        double boy = sc.nextDouble();
        boolean uyeMi = sc.nextBoolean();
        System.out.printf("ad=%s, yas=%d, boy=%.1f, uye=%b%n", ad, yas, boy, uyeMi);
        sc.close();

        // hasNextX() ile güvenli okuma: tip uymazsa atla/yönet.
        Scanner sc2 = new Scanner("10 abc 20 xyz 30");
        int toplam = 0;
        while (sc2.hasNext()) {
            if (sc2.hasNextInt()) toplam += sc2.nextInt(); // sayıysa topla
            else sc2.next();                                // değilse atla
        }
        System.out.println("Sadece sayıların toplamı: " + toplam);
        sc2.close();

        System.out.println("""

                --- Scanner ---
                Bir kaynaktan (System.in, File, String) okuyup belirteçlere ayırır.
                nextInt/nextDouble/next/nextLine/nextBoolean ile tip tip okunur; varsayılan ayraç boşluktur.
                hasNextInt()/hasNext() ile okumadan ÖNCE kontrol et (tip uyuşmazlığı InputMismatchException atar).
                Klavyeden okuma: Scanner sc = new Scanner(System.in); int x = sc.nextInt();""");
    }
}
