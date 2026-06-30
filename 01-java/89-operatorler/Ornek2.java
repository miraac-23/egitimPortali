// Ornek2: Mantıksal operatörler, kısa devre (short-circuit), üçlü operatör ve öncelik.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    static boolean kontrol(String ad, boolean deger) {
        System.out.println("    (" + ad + " değerlendirildi)");
        return deger;
    }

    public static void main(String[] args) {
        // MANTIKSAL: && (ve), || (veya), ! (değil) — boolean üzerinde.
        System.out.println("true && false = " + (true && false));
        System.out.println("true || false = " + (true || false));
        System.out.println("!true = " + (!true));

        // KISA DEVRE (short-circuit): && ilk operand false ise İKİNCİYİ DEĞERLENDİRMEZ.
        System.out.println("\n&& kısa devre (ilk false -> ikinci çalışmaz):");
        boolean r1 = kontrol("A", false) && kontrol("B", true);  // B çalışmaz
        System.out.println("  sonuç: " + r1);

        System.out.println("|| kısa devre (ilk true -> ikinci çalışmaz):");
        boolean r2 = kontrol("C", true) || kontrol("D", true);   // D çalışmaz
        System.out.println("  sonuç: " + r2);
        // Pratik fayda: null güvenliği -> (s != null && s.length() > 0)

        // ÜÇLÜ (ternary) operatör: koşul ? doğruysa : yanlışsa  (mini if-else ifadesi)
        int yas = 20;
        String durum = (yas >= 18) ? "yetişkin" : "reşit değil";
        System.out.println("\nÜçlü: yas=" + yas + " -> " + durum);
        int max = (7 > 3) ? 7 : 3;
        System.out.println("max(7,3) = " + max);

        // ÖNCELİK (precedence): * / %, + -'den ÖNCE; karşılaştırma mantıksaldan önce.
        System.out.println("\n2 + 3 * 4 = " + (2 + 3 * 4) + "  (* önce -> 14, parantezsiz)");
        System.out.println("(2 + 3) * 4 = " + ((2 + 3) * 4) + "  (parantez önceliği değiştirir)");

        System.out.println("""

                --- Mantıksal, kısa devre, üçlü, öncelik ---
                Mantıksal: && (ve), || (veya), ! (değil).
                KISA DEVRE: && ilk false -> ikinciyi atlar; || ilk true -> ikinciyi atlar.
                  Faydası: hem performans hem GÜVENLİK -> (s != null && s.isEmpty()) NPE'yi önler.
                Üçlü (?:): 'kosul ? a : b' — bir DEĞER döndüren mini if-else.
                Öncelik: * / % > + - > karşılaştırma > && > || ; ŞÜPHEDEYSEN PARANTEZ kullan (okunabilirlik).""");
    }
}
