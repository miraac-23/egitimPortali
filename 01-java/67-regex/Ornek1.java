// Ornek1: Düzenli ifadeler (regex) — Pattern, Matcher, gruplar.
// Çalıştırma: java Ornek1.java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ornek1 {

    public static void main(String[] args) {
        // matches: TÜM metin kalıba uyuyor mu? (String.matches kısayolu da var)
        System.out.println("'12345' sadece rakam mı? " + "12345".matches("\\d+"));
        System.out.println("'12a45' sadece rakam mı? " + "12a45".matches("\\d+"));

        // find: metin İÇİNDE kalıbı arar (tüm eşleşmeleri gezer).
        Pattern p = Pattern.compile("\\d+");           // bir veya çok rakam
        Matcher m = p.matcher("oda 12, kat 3, daire 45");
        System.out.print("Bulunan sayılar: ");
        while (m.find()) System.out.print(m.group() + " ");
        System.out.println();

        // Gruplar: parantezlerle alt-parçaları yakala.
        Pattern tarih = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4})");
        Matcher tm = tarih.matcher("Doğum: 20.05.1995");
        if (tm.find()) {
            System.out.println("Tüm eşleşme: " + tm.group(0));
            System.out.println("  gün=" + tm.group(1) + ", ay=" + tm.group(2) + ", yıl=" + tm.group(3));
        }

        // İsimli gruplar: okunaklılık
        Pattern email = Pattern.compile("(?<kullanici>\\w+)@(?<alan>[\\w.]+)");
        Matcher em = email.matcher("iletisim: ada@site.com");
        if (em.find()) {
            System.out.println("kullanıcı=" + em.group("kullanici") + ", alan=" + em.group("alan"));
        }

        System.out.println("""

                --- Regex temelleri ---
                Pattern.compile(kalip) -> Matcher ile metinde arama. matches (tümü), find (içinde).
                Sık metakarakterler: \\d rakam, \\w harf/rakam/_, \\s boşluk, . herhangi,
                  + (1+), * (0+), ? (0/1), {n,m} (aralık), ^ baş, $ son, [...] küme, (...) grup.
                group(n): n. yakalama grubu; isimli grup: (?<ad>...) ve group("ad").""");
    }
}
