// =============================================================================
//  PatternMatchingKalici.java
//  JAVA 16 - Pattern Matching for instanceof ARTIK KALICI (FINAL)
// =============================================================================
//
//  NEDİR?
//  "instanceof için kalıp eşleştirme", bir nesnenin tipini kontrol ederken
//  AYNI ANDA o tipte bir değişkene atamayı (binding) sağlar. Java 14'te
//  preview, Java 15'te ikinci preview, JAVA 16 ile KALICI hale geldi.
//
//  NEDEN GELDİ / NE İŞE YARAR?
//  Klasik yaklaşımda her zaman ŞU üç adımı yapardık:
//    1) instanceof ile tip kontrolü
//    2) açıkça (explicit) cast
//    3) yeni bir değişkene atama
//  Bu hem tekrar (boilerplate) hem de cast hatasına açık bir kalıptı.
//  Pattern matching bu üç adımı TEK İFADEDE birleştirir.
//
//  Derlemek için: javac PatternMatchingKalici.java
//  Çalıştırmak  : java PatternMatchingKalici
// =============================================================================

import java.util.List;

public class PatternMatchingKalici {

    public static void main(String[] args) {

        System.out.println("=== JAVA 16: Pattern Matching for instanceof (KALICI) ===\n");

        // ---------------------------------------------------------------------
        // 1) ESKİ YÖNTEM vs YENİ YÖNTEM
        // ---------------------------------------------------------------------
        Object nesne = "Merhaba Java 16";

        // ESKİ: kontrol + cast + atama (3 adım)
        System.out.println("--- ESKİ yöntem ---");
        if (nesne instanceof String) {
            String s = (String) nesne;          // ayrı cast satırı
            System.out.println("Uzunluk (eski): " + s.length());
        }

        // YENİ: instanceof içinde "s" bağlanır (binding variable)
        System.out.println("--- YENİ yöntem ---");
        if (nesne instanceof String s) {        // cast otomatik, s hazır
            System.out.println("Uzunluk (yeni): " + s.length());
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) KOŞULLARLA BİRLEŞTİRME (binding değişkeni && ile kullanılabilir)
        // ---------------------------------------------------------------------
        System.out.println("--- Koşulla birleştirme ---");
        Object veri = "kurumsal";
        if (veri instanceof String s && s.length() > 5) {
            // s hem cast edilmiş hem de uzunluk kontrolünden geçmiş halde
            System.out.println("Uzun metin: " + s.toUpperCase());
        }

        // Negatif kontrolde (scope flow) binding değişkeni dışarıda da kullanılabilir:
        if (!(veri instanceof String s2)) {
            System.out.println("String değil");
        } else {
            // else dalında s2 erişilebilir çünkü buraya ancak String ise gelinir
            System.out.println("else dalında erişilebilir: " + s2);
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) GERÇEK HAYAT: equals() metodu yazmak çok kolaylaşır
        // ---------------------------------------------------------------------
        System.out.println("--- Gerçek hayat: equals() implementasyonu ---");
        var p1 = new Personel(1, "Ali");
        var p2 = new Personel(1, "Ali");
        var p3 = new Personel(2, "Veli");
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("p1.equals(p3): " + p1.equals(p3)); // false
        System.out.println();

        // ---------------------------------------------------------------------
        // 4) GERÇEK HAYAT: heterojen listede tip-bazlı işlem
        // ---------------------------------------------------------------------
        System.out.println("--- Gerçek hayat: karışık tipli liste işleme ---");
        List<Object> kayitlar = List.of(
                "metin verisi",
                42,
                3.14,
                List.of("a", "b", "c")
        );

        for (Object kayit : kayitlar) {
            String aciklama = acikla(kayit);
            System.out.println("  " + aciklama);
        }
    }

    // Pattern matching ile sade, okunabilir tip yönlendirmesi
    static String acikla(Object o) {
        if (o instanceof String s) {
            return "Metin (" + s.length() + " karakter): " + s;
        } else if (o instanceof Integer i) {
            return "Tam sayı, karesi = " + (i * i);
        } else if (o instanceof Double d) {
            return "Ondalık, yuvarlanmış = " + Math.round(d);
        } else if (o instanceof List<?> liste) {
            return "Liste (" + liste.size() + " eleman)";
        }
        return "Bilinmeyen tip";
    }

    // =========================================================================
    //  GERÇEK HAYAT: equals() yazarken pattern matching kullanımı
    // =========================================================================
    static final class Personel {
        private final int id;
        private final String ad;

        Personel(int id, String ad) {
            this.id = id;
            this.ad = ad;
        }

        @Override
        public boolean equals(Object o) {
            // ESKİ yöntemde:
            //   if (!(o instanceof Personel)) return false;
            //   Personel that = (Personel) o;
            //   return ...
            // YENİ yöntemde tek satırda kontrol + cast + binding:
            return o instanceof Personel p
                    && this.id == p.id
                    && this.ad.equals(p.ad);
        }

        @Override
        public int hashCode() {
            return id * 31 + ad.hashCode();
        }
    }
}
