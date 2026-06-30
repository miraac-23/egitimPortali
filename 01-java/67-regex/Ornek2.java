// Ornek2: Regex ile doğrulama, değiştirme (replaceAll) ve bölme (split).
// Çalıştırma: java Ornek2.java
import java.util.Arrays;
import java.util.regex.Pattern;

public class Ornek2 {

    // Önceden derlenmiş kalıplar (tekrar kullanılacaksa derleyip sakla -> performans).
    static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    static final Pattern TC_TEL = Pattern.compile("^05\\d{9}$"); // 05XXXXXXXXX

    public static void main(String[] args) {
        // 1) Doğrulama
        for (String e : new String[]{"ada@site.com", "gecersiz", "a@b.co"}) {
            System.out.println("e-posta '" + e + "' geçerli mi? " + EMAIL.matcher(e).matches());
        }
        System.out.println("telefon '05321234567' geçerli mi? " + TC_TEL.matcher("05321234567").matches());

        // 2) replaceAll — geri referansla ($1) maskeleme/biçimleme
        String metin = "Kart: 1234 5678 9012 3456";
        String maskeli = metin.replaceAll("\\d{4} \\d{4} \\d{4} (\\d{4})", "**** **** **** $1");
        System.out.println("\nMaskelenmiş: " + maskeli);

        // Fazla boşlukları teke indir
        String temiz = "çok    fazla     boşluk".replaceAll("\\s+", " ");
        System.out.println("Temizlenmiş: '" + temiz + "'");

        // 3) split — regex'e göre böl
        String csv = "elma, armut ,  kiraz ,muz";
        String[] parcalar = csv.split("\\s*,\\s*"); // virgül + etrafındaki boşluklar
        System.out.println("Bölme: " + Arrays.toString(parcalar));

        System.out.println("""

                --- Doğrulama, değiştirme, bölme ---
                Doğrulama: pattern.matcher(metin).matches() ile format kontrolü (e-posta, telefon...).
                replaceAll(kalip, yenisi): $1, $2 ile yakalanan grupları kullanarak değiştir (maskeleme, biçimleme).
                split(kalip): metni regex'e göre böl (CSV, boşluk vb.).
                İPUCU: tekrar kullanılan kalıpları Pattern.compile ile bir kez derle (her seferinde değil).
                NOT: kullanıcıdan gelen regex'lerde 'catastrophic backtracking' (yavaşlama) riskine dikkat.""");
    }
}
