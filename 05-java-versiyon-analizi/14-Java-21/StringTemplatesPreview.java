/**
 * JAVA 21 - STRING TEMPLATES (JEP 430) - 1. PREVIEW
 * ==================================================
 *
 * Bu dosya, String Templates'in Java 21'deki preview halini gosterir.
 *
 * !!! COK ONEMLI UYARI !!!
 *   String Templates Java 21 ve 22'de PREVIEW olarak kaldi; Java 23'te
 *   tasarimdan KALDIRILDI (geri cekildi) ve yeniden tasarlanmak uzere
 *   ertelendi. Bu yuzden URETIMDE KULLANMAYIN. Kavramsal olarak bilin.
 *   Ileride farkli bir bicimde geri donmesi beklenir.
 *
 * NEDIR?
 *   STR."... \{ifade} ..." sozdizimi ile guvenli, okunabilir string
 *   interpolasyonu. Ayrica SQL/HTML gibi baglamlarda kacis (escaping) ve
 *   dogrulama yapabilen sablon islemcileri (template processors) sunar.
 *
 * NEDEN GELDI?
 *   - "+" ile birlestirme daginiktir.
 *   - String.format argum sirasi hatalarina aciktir.
 *   - Hicbiri enjeksiyon guvenligi (SQL injection / XSS) saglamaz.
 *
 * --- DERLEME / CALISTIRMA (PREVIEW oldugu icin GEREKLI) ---
 *   javac --release 21 --enable-preview StringTemplatesPreview.java
 *   java  --enable-preview StringTemplatesPreview
 *
 *   NOT: Java 23+ ile bu sozdizimi KALDIRILDIGI icin o surumlerde derlenmez.
 *        Bu ornek Java 21/22 icindir.
 */

import static java.lang.StringTemplate.STR;

public class StringTemplatesPreview {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: STRING TEMPLATES (1. PREVIEW) ===\n");

        String isim = "Ayse";
        int yas = 30;
        double bakiye = 1499.90;

        // --- ESKI yontemler ---
        System.out.println("--- ESKI ---");
        String eski1 = "Ad: " + isim + ", Yas: " + yas;
        String eski2 = String.format("Ad: %s, Yas: %d", isim, yas);
        System.out.println("  + ile      : " + eski1);
        System.out.println("  format ile : " + eski2);
        System.out.println();

        // --- YENI: STR sablon islemcisi (preview) ---
        System.out.println("--- YENI (STR, preview) ---");
        String yeni = STR."Ad: \{isim}, Yas: \{yas}";
        System.out.println("  STR ile    : " + yeni);

        // Ifadeler de gomulebilir (sadece degisken degil)
        String hesap = STR."\{isim} icin yillik bakiye: \{bakiye * 12} TL";
        System.out.println("  Ifade ile  : " + hesap);

        // Cok satirli sablon
        String rapor = STR."""
                === MUSTERI RAPORU ===
                Ad     : \{isim}
                Yas    : \{yas}
                Bakiye : \{bakiye} TL
                """;
        System.out.println(rapor);

        System.out.println("""
                --- UYARI ---
                Bu ozellik Java 23'te KALDIRILDI. Sadece Java 21/22'de --enable-preview
                ile calisir. Uretimde kullanmayin; ileride yeniden tasarlanacaktir.
                """);
    }
}
