// Ornek2: Bayrak/seçenek (flag/option) ayrıştırma — --ad değer, -v gibi.
// Çalıştırma: java Ornek2.java
import java.util.HashMap;
import java.util.Map;

public class Ornek2 {

    public static void main(String[] args) {
        // Gerçek CLI araçları gibi: --çıktı dosya.txt --tekrar 3 -v girdi.txt
        String[] ornek = { "--cikti", "rapor.txt", "--tekrar", "3", "-v", "girdi.txt" };
        System.out.println("Argümanlar: " + String.join(" ", ornek));

        Map<String, String> secenekler = new HashMap<>();
        java.util.List<String> serbest = new java.util.ArrayList<>();
        boolean ayrintili = false;

        for (int i = 0; i < ornek.length; i++) {
            String a = ornek[i];
            if (a.equals("-v")) {
                ayrintili = true;                       // değersiz bayrak (flag)
            } else if (a.startsWith("--")) {
                String anahtar = a.substring(2);
                String deger = (i + 1 < ornek.length) ? ornek[++i] : ""; // sonraki = değer
                secenekler.put(anahtar, deger);          // --anahtar değer
            } else {
                serbest.add(a);                          // konumsal (positional) argüman
            }
        }

        System.out.println("\nSeçenekler (--): " + secenekler);
        System.out.println("Bayrak -v (ayrıntılı): " + ayrintili);
        System.out.println("Serbest argümanlar: " + serbest);
        System.out.println("Tekrar sayısı (int): " + Integer.parseInt(secenekler.getOrDefault("tekrar", "1")));

        System.out.println("""

                --- Bayrak/seçenek ayrıştırma ---
                CLI araçları üç tür argüman alır:
                  bayrak (flag): -v, --verbose  (değersiz, var/yok)
                  seçenek (option): --cikti dosya.txt  (anahtar + değer)
                  konumsal (positional): girdi.txt  (sırasıyla anlamlı)
                Basit ayrıştırma elle yazılabilir; karmaşık CLI için picocli, JCommander, Apache Commons CLI
                gibi kütüphaneler (otomatik yardım, tip dönüşümü, doğrulama) tercih edilir.""");
    }
}
