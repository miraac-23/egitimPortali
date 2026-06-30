// Ornek1: Character sınıfı — karakter sınıflandırma ve dönüşüm metotları.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Character'ın statik metotları bir karakteri SINIFLANDIRIR/DÖNÜŞTÜRÜR.
        System.out.println("isDigit('7')      : " + Character.isDigit('7'));
        System.out.println("isLetter('A')     : " + Character.isLetter('A'));
        System.out.println("isLetterOrDigit('_'): " + Character.isLetterOrDigit('_'));
        System.out.println("isWhitespace(' ') : " + Character.isWhitespace(' '));
        System.out.println("isUpperCase('a')  : " + Character.isUpperCase('a'));
        System.out.println("toUpperCase('a')  : " + Character.toUpperCase('a'));
        System.out.println("getNumericValue('9'): " + Character.getNumericValue('9'));

        // Gerçek kullanım: bir metni karakter karakter analiz et.
        String metin = "Java 21, harika! (2026)";
        int harf = 0, rakam = 0, bosluk = 0, diger = 0;
        for (char c : metin.toCharArray()) {
            if (Character.isLetter(c)) harf++;
            else if (Character.isDigit(c)) rakam++;
            else if (Character.isWhitespace(c)) bosluk++;
            else diger++;
        }
        System.out.println("\n'" + metin + "' analizi:");
        System.out.printf("  harf=%d, rakam=%d, boşluk=%d, diğer=%d%n", harf, rakam, bosluk, diger);

        // char ↔ int: karakterler aslında sayısal kodlardır (Unicode).
        char harfA = 'A';
        System.out.println("\n'A' kodu: " + (int) harfA + ", 'A'+1 -> " + (char) (harfA + 1));

        System.out.println("""

                --- Character sınıfı ---
                Bir karakteri sınıflandırır/dönüştürür: isDigit/isLetter/isLetterOrDigit/isWhitespace/
                isUpperCase/isLowerCase, toUpperCase/toLowerCase, getNumericValue.
                Girdi doğrulama, ayrıştırma (parser) ve metin analizi için temeldir.
                char bir sayısal koddur (Unicode); int'e cast edilip aritmetik yapılabilir ('A'=65).""");
    }
}
