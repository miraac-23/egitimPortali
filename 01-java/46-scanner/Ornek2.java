// Ornek2: Scanner — satır okuma, özel ayraç (delimiter) ve nextInt/nextLine tuzağı.
// Çalıştırma: java Ornek2.java
import java.util.Scanner;

public class Ornek2 {

    public static void main(String[] args) {
        // 1) Satır satır okuma: nextLine() + hasNextLine()
        String metin = "birinci satır\nikinci satır\nüçüncü satır";
        Scanner sc = new Scanner(metin);
        int no = 1;
        while (sc.hasNextLine()) {
            System.out.println(no++ + ": " + sc.nextLine());
        }
        sc.close();

        // 2) Özel ayraç: virgülle ayrılmış değerleri (CSV) oku
        Scanner csv = new Scanner("elma,armut,kiraz,muz").useDelimiter(",");
        System.out.print("\nCSV: ");
        while (csv.hasNext()) System.out.print("[" + csv.next() + "] ");
        System.out.println();
        csv.close();

        // 3) KLASİK TUZAK: nextInt()'ten sonra nextLine() boş satır döndürür.
        Scanner t = new Scanner("42\nAda Yılmaz");
        int sayi = t.nextInt();
        t.nextLine();                 // <-- nextInt'in bıraktığı satır sonunu TÜKET
        String isim = t.nextLine();   // şimdi doğru satırı okur
        System.out.println("\nTuzak çözümü -> sayi=" + sayi + ", isim=" + isim);
        t.close();

        System.out.println("""

                --- Satır okuma, ayraç ve tuzak ---
                nextLine(): satır sonuna kadar olan TÜM metni okur (boşluklar dahil).
                useDelimiter(regex): belirteç ayracını değiştirir (örn. ',' ile CSV).
                TUZAK: nextInt()/nextDouble() satır sonunu (\\n) BIRAKIR; hemen sonra nextLine()
                       gelirse o boş kalan satırı okur. Çözüm: arada fazladan bir nextLine() çağır.""");
    }
}
