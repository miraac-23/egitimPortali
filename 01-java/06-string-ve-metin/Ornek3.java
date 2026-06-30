// Ornek3: Metin işleme mini uygulaması — kelime sayma, ters çevirme, palindrom.
// Çalıştırma: java Ornek3.java
public class Ornek3 {

    // Bir cümledeki kelime sayısını döndürür.
    static int kelimeSay(String cumle) {
        String temiz = cumle.trim();
        if (temiz.isEmpty()) return 0;
        return temiz.split("\\s+").length; // bir veya çok boşluğa göre böl
    }

    // Kelimelerin sırasını ters çevirir.
    static String kelimeleriTersCevir(String cumle) {
        String[] kelimeler = cumle.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = kelimeler.length - 1; i >= 0; i--) {
            sb.append(kelimeler[i]);
            if (i > 0) sb.append(" ");
        }
        return sb.toString();
    }

    // Palindrom mu? (boşluk ve büyük/küçük harf yok sayılır)
    static boolean palindromMu(String metin) {
        String s = metin.toLowerCase().replaceAll("[^a-zçğıöşü0-9]", "");
        int sol = 0, sag = s.length() - 1;
        while (sol < sag) {
            if (s.charAt(sol) != s.charAt(sag)) return false;
            sol++;
            sag--;
        }
        return true;
    }

    public static void main(String[] args) {
        String cumle = "Java ile metin islemek kolaydir";
        System.out.println("Cümle: \"" + cumle + "\"");
        System.out.println("Kelime sayısı : " + kelimeSay(cumle));
        System.out.println("Ters sıra     : " + kelimeleriTersCevir(cumle));

        System.out.println("\nPalindrom testleri:");
        for (String kelime : new String[]{"kayak", "ey edip adanada pide ye", "merhaba"}) {
            System.out.printf("  %-28s -> %s%n", "\"" + kelime + "\"",
                    palindromMu(kelime) ? "palindrom" : "değil");
        }
    }
}
