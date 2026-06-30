// Ornek1: Unicode — karakter kodlari, u-kacis dizileri ve "char" vs "code point" farki.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // char 16 bitlik bir Unicode kod birimidir. (uXXXX biçimli kaçışla kod yazılır.)
        char a = 'A';
        char turkceC = 'ç';  // 'ç' (U+00E7)
        System.out.println("'A' kodu: " + (int) a + " (U+0041)");
        System.out.println("\\u00E7 -> '" + turkceC + "' (ç)");
        System.out.println("'\\u011F' -> '" + 'ğ' + "' (ğ)");

        // String'de Türkçe karakterler tek char'a sığar (BMP içinde):
        String s = "Çiğdem";
        System.out.println("\n'" + s + "' -> length()=" + s.length() + " (her harf 1 char)");

        // ASTRAL karakterler (emoji gibi, U+FFFF üstü) İKİ char ile temsil edilir (surrogate pair)!
        String emoji = "😀";  // 😀 (U+1F600) — tek "kod noktası" ama 2 char
        System.out.println("\nEmoji 😀:");
        System.out.println("  length() (char sayısı)     : " + emoji.length() + " (surrogate pair -> 2!)");
        System.out.println("  codePointCount (gerçek harf): " + emoji.codePointCount(0, emoji.length()));

        // Kod noktalarını DOĞRU saymak/gezmek için codePoints() kullan (char değil):
        String karisik = "ab😀cd"; // a b 😀 c d -> 5 kod noktası, 6 char
        System.out.println("\n'ab😀cd':");
        System.out.println("  length()       = " + karisik.length() + " (char)");
        System.out.println("  kod noktası say = " + karisik.codePoints().count() + " (gerçek karakter)");

        System.out.println("""

                --- Unicode: char vs code point ---
                Unicode her karaktere bir KOD NOKTASI (code point) atar: 'A'=U+0041, 'ç'=U+00E7, '😀'=U+1F600.
                Java'da char 16 bittir; U+0000..U+FFFF (BMP) tek char'a sığar.
                AMA U+FFFF ÜSTÜ (emoji, bazı semboller) İKİ char ile (surrogate pair) temsil edilir!
                Bu yüzden string.length() KARAKTER sayısı DEĞİL, char (kod birimi) sayısıdır.
                Gerçek karakter sayısı için codePointCount / codePoints() kullan. \\uXXXX ile kod yazılır.""");
    }
}
