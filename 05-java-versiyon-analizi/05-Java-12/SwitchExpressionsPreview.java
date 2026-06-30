// =============================================================================
//  SwitchExpressionsPreview.java
//  Java 12 - Switch Expressions (Preview Özelliği - JEP 325)
// =============================================================================
//
//  ÖNEMLI NOT (DERLEME / ÇALIŞTIRMA):
//  ----------------------------------------------------------------------------
//  - Switch Expressions, Java 12'de "PREVIEW" (önizleme) olarak geldi.
//    Java 12 ile derlemek için şu komut gerekiyordu:
//        javac --release 12 --enable-preview SwitchExpressionsPreview.java
//        java  --enable-preview SwitchExpressionsPreview
//    (--enable-preview olmadan Java 12'de bu söz dizimi DERLENMEZDİ.)
//
//  - Özellik Java 14'te (JEP 361) KALICI (standard) hale geldi.
//    Java 14 ve sonrasında (örneğin sizin sisteminizdeki Java 21'de)
//    bu dosya HİÇBİR ek bayrağa gerek kalmadan normal şekilde derlenir:
//        javac SwitchExpressionsPreview.java
//        java  SwitchExpressionsPreview
//
//  Bu yüzden aşağıdaki kod, "preview" döneminde yazılmış söz dizimini
//  kullanır; ancak Java 14+ ile --enable-preview olmadan da çalışır.
//  Aşağıdaki yorumlarda hangi parçanın preview'a özgü olduğu açıklanmıştır.
// =============================================================================

public class SwitchExpressionsPreview {

    public static void main(String[] args) {

        System.out.println("================================================");
        System.out.println(" JAVA 12 - SWITCH EXPRESSIONS (PREVIEW) ORNEKLERI");
        System.out.println("================================================\n");

        // ---------------------------------------------------------------------
        // BÖLÜM 1: ESKİ YÖNTEM (Klasik switch STATEMENT)
        // ---------------------------------------------------------------------
        // Java 12 öncesinde switch bir "ifade (expression)" değil, bir
        // "deyim (statement)" idi. Yani doğrudan bir DEĞER ÜRETEMEZDİ;
        // değeri ancak yan etki (bir değişkene atama) ile elde edebilirdik.
        //
        // PROBLEMLER:
        //   1) Her case sonunda 'break' yazmayı UNUTURSAK "fall-through"
        //      (bir alttaki case'e düşme) hatası oluşur. Sessiz bir bug'tır.
        //   2) Değer üretmek için ekstra bir değişken tanımlamak gerekir.
        //   3) Çok satırlı, gürültülü ve hataya açık koddur.
        System.out.println(">>> ESKI YONTEM (klasik switch statement):");
        for (int gun = 1; gun <= 7; gun++) {
            int calismaSaatiEski;
            switch (gun) {
                case 1:   // Pazartesi
                case 2:   // Sali
                case 3:   // Carsamba
                case 4:   // Persembe
                case 5:   // Cuma
                    calismaSaatiEski = 8;   // hafta ici tam mesai
                    break;                  // <-- break unutulursa fall-through!
                case 6:   // Cumartesi
                    calismaSaatiEski = 4;   // yarim gun
                    break;
                case 7:   // Pazar
                    calismaSaatiEski = 0;   // tatil
                    break;
                default:
                    calismaSaatiEski = -1;  // gecersiz gun
            }
            System.out.println("   Gun " + gun + " -> " + calismaSaatiEski + " saat");
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // BÖLÜM 2: YENİ YÖNTEM (Switch EXPRESSION + '->' oku)
        // ---------------------------------------------------------------------
        // Java 12'de gelen yeni '->' (ok) söz dizimi:
        //   - Ok'un sağındaki ifade ÇALIŞIR ve DEĞER ÜRETİR.
        //   - 'break' YOKTUR; otomatik olarak fall-through OLMAZ. Güvenli!
        //   - Birden çok etiketi tek case'te virgülle yazabiliriz:
        //         case 1, 2, 3, 4, 5 ->
        //   - switch'in tamamı bir DEĞER döndürür; doğrudan atayabiliriz.
        System.out.println(">>> YENI YONTEM (switch expression, '->' oku):");
        for (int gun = 1; gun <= 7; gun++) {
            int calismaSaatiYeni = switch (gun) {
                // Cogu etiketi tek satirda birlestirebiliyoruz (coklu case):
                case 1, 2, 3, 4, 5 -> 8;   // hafta ici
                case 6            -> 4;    // cumartesi
                case 7            -> 0;    // pazar
                default           -> -1;   // gecersiz
            };  // <-- DİKKAT: expression olduğu için sonunda ';' var.
            System.out.println("   Gun " + gun + " -> " + calismaSaatiYeni + " saat");
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // BÖLÜM 3: 'yield' İLE ÇOK SATIRLI CASE BLOĞU
        // ---------------------------------------------------------------------
        // Bir case'in sağında tek bir ifade değil de birkaç satır işlem
        // yapmamız gerekiyorsa { } blok açarız ve değeri 'yield' ile döndürürüz.
        // ('yield', switch expression'dan değer döndürmenin yoludur;
        //  metottan değer döndüren 'return' ile karıştırılmamalıdır.)
        System.out.println(">>> 'yield' ile cok satirli case blogu:");
        for (int gun = 1; gun <= 7; gun++) {
            String aciklama = switch (gun) {
                case 1, 2, 3, 4, 5 -> {
                    int saat = 8;
                    // ... burada loglama, hesaplama vs. yapilabilir ...
                    yield "Hafta ici - " + saat + " saat mesai";  // <-- yield
                }
                case 6 -> {
                    yield "Cumartesi - yarim gun (4 saat)";
                }
                case 7 -> "Pazar - tatil";   // tek ifade -> yield gerekmez
                default -> "Gecersiz gun!";
            };
            System.out.println("   Gun " + gun + " -> " + aciklama);
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // BÖLÜM 4: GERÇEK HAYAT ÖRNEĞİ - HTTP DURUM KODU -> MESAJ
        // ---------------------------------------------------------------------
        System.out.println(">>> GERCEK HAYAT: HTTP durum kodu -> kullanici mesaji:");
        int[] kodlar = {200, 201, 301, 404, 500, 418, 999};
        for (int kod : kodlar) {
            String mesaj = httpDurumMesaji(kod);
            System.out.println("   HTTP " + kod + " -> " + mesaj);
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // BÖLÜM 5: ESKİ '\' (iki nokta) söz dizimini de switch EXPRESSION
        //          olarak kullanabilirsiniz (eski stil ':' + yield).
        // ---------------------------------------------------------------------
        // Not: Java 12 ':' ile de expression yazmaya izin verir; ancak bu
        // durumda fall-through riski geri gelir. '->' kullanmak tercih edilir.
        System.out.println(">>> Eski ':' stiliyle expression (yield ile):");
        int puan = 85;
        String harfNotu = switch (puan / 10) {
            case 10, 9 : yield "AA";
            case 8     : yield "BA";
            case 7     : yield "BB";
            case 6     : yield "CC";
            default    : yield "FF (kaldi)";
        };
        System.out.println("   Puan " + puan + " -> Harf notu: " + harfNotu);

        System.out.println("\n================================================");
        System.out.println(" BITTI.");
        System.out.println("================================================");
    }

    // -------------------------------------------------------------------------
    // Yardımcı metot: HTTP durum kodunu '->' switch expression ile mesaja çevirir.
    // -------------------------------------------------------------------------
    private static String httpDurumMesaji(int kod) {
        return switch (kod) {
            case 200, 201, 204 -> "Basarili";
            case 301, 302      -> "Yonlendirme";
            case 400           -> "Hatali istek";
            case 401, 403      -> "Yetkisiz erisim";
            case 404           -> "Bulunamadi";
            case 500, 502, 503 -> "Sunucu hatasi";
            default            -> "Bilinmeyen durum kodu (" + kod + ")";
        };
    }
}
