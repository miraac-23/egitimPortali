// =====================================================================================
//  RecordSealedBirlikte.java
//  Java 15 - Records + Sealed Classes BİRLİKTE (Cebirsel Veri Tipleri / ADT)
// =====================================================================================
//
//  ÖNEMLİ SÜRÜM NOTU:
//  ------------------
//  - Records:        Java 14'te ilk preview, Java 15'te İKİNCİ preview (JEP 384),
//                    Java 16'da KALICI (JEP 395).
//  - Sealed Classes: Java 15'te İLK preview (JEP 360), Java 17'de KALICI (JEP 409).
//
//  Yani Java 15'te HEM records HEM sealed classes PREVIEW durumundaydı. O zaman
//  bu dosyayı derlemek için şu gerekiyordu:
//        javac --release 15 --enable-preview RecordSealedBirlikte.java
//        java  --enable-preview RecordSealedBirlikte
//
//  Java 17 ve sonrasında her ikisi de kalıcı olduğu için HİÇBİR BAYRAK gerekmez:
//        javac RecordSealedBirlikte.java
//        java  RecordSealedBirlikte
//
//  Bu dosya Java 17+ (ve switch pattern matching için Java 21+) ile derlenecek
//  şekilde yazılmıştır.
//
// =====================================================================================
//
//  CEBİRSEL VERİ TİPLERİ (Algebraic Data Types - ADT) NEDİR?
//  ---------------------------------------------------------
//  Fonksiyonel dillerden (Haskell, Scala, Kotlin) gelen bir kavram. Bir tip,
//  "şunlardan BİRİ olabilir" şeklinde KAPALI bir seçenek kümesiyle tanımlanır:
//
//      sealed interface  -> "bu tip şu seçeneklerden biridir" (toplam / sum type)
//      record            -> "her seçenek şu alanlardan oluşur" (çarpım / product type)
//
//  Bu ikilinin Java 15 ile birleşmesi, Java'ya MODERN, GÜVENLİ ve KISA bir
//  modelleme aracı kazandırdı:
//    - sealed -> derleyici tüm olasılıkları bilir (exhaustive switch).
//    - record -> az kodla değişmez (immutable) veri taşıyıcıları.
//    - pattern matching -> her seçeneği tek tek, güvenle ayrıştırma.
//
// =====================================================================================

public class RecordSealedBirlikte {

    public static void main(String[] args) {
        System.out.println("===== JAVA 15: RECORDS + SEALED (ADT) DEMOSU =====\n");
        geometriDemosu();
        ifadeAgaciDemosu();
    }

    // =====================================================================================
    //  ÖRNEK 1: GEOMETRİK ŞEKİLLER - sealed interface + record'lar
    // =====================================================================================
    //
    //  "Sekil" mühürlü bir arayüzdür ve SADECE Daire, Dikdortgen, Ucgen olabilir.
    //  Her seçenek bir record'dur: kısa, değişmez ve otomatik equals/hashCode/toString'li.
    //
    //  Bu klasik ADT desenidir:
    //      sealed interface Sekil permits Daire, Dikdortgen, Ucgen
    //      record Daire(double yaricap) implements Sekil
    //      ...
    //
    // =====================================================================================

    sealed interface Sekil permits Daire, Dikdortgen, Ucgen {}

    record Daire(double yaricap) implements Sekil {}
    record Dikdortgen(double en, double boy) implements Sekil {}
    record Ucgen(double taban, double yukseklik) implements Sekil {}

    // Alan hesabı: sealed + record + switch pattern matching birleşimi.
    // 'Sekil' mühürlü olduğu için switch TÜM olasılıkları kapsar; 'default' GEREKMEZ.
    // Eğer yarın 'permits' listesine yeni bir şekil eklenirse ama bu switch
    // güncellenmezse, DERLEYİCİ HATA verir. İşte ADT'nin güvenlik gücü budur.
    static double alan(Sekil s) {
        return switch (s) {
            // 'Record deconstruction' (kayıt ayrıştırma): record'un alanlarını
            // doğrudan değişkenlere açıyoruz. (Java 21 record pattern özelliği.)
            case Daire(double r)             -> Math.PI * r * r;
            case Dikdortgen(double en, double boy) -> en * boy;
            case Ucgen(double t, double h)   -> (t * h) / 2.0;
        };
    }

    static void geometriDemosu() {
        System.out.println("----- ÖRNEK 1: GEOMETRİK ŞEKİLLER (ADT) -----");

        Sekil[] sekiller = {
                new Daire(3),
                new Dikdortgen(4, 5),
                new Ucgen(6, 2)
        };

        for (Sekil s : sekiller) {
            // record'ların otomatik toString'i sayesinde nesneler okunaklı yazılır.
            System.out.printf("%-30s alan = %.2f%n", s, alan(s));
        }
        System.out.println();
        System.out.println("Not: 'switch' içinde 'default' yok çünkü 'Sekil' mühürlü");
        System.out.println("ve derleyici tüm alt tiplerin kapsandığını biliyor.");
        System.out.println();
    }


    // =====================================================================================
    //  ÖRNEK 2: GERÇEK HAYAT - İFADE AĞACI (Expression Tree) ile HESAP MAKİNESİ
    // =====================================================================================
    //
    //  ADT'nin en klasik ve güçlü örneği: bir matematik ifadesini ağaç olarak modellemek.
    //  Bir "İfade" ya bir sayıdır, ya iki ifadenin toplamıdır, ya da çarpımıdır.
    //  Bu YİNELEMELİ (recursive) yapıyı sealed + record ile çok temiz ifade ederiz:
    //
    //      sealed interface Ifade permits Sayi, Toplama, Carpma
    //      record Sayi(double deger)             implements Ifade
    //      record Toplama(Ifade sol, Ifade sag)  implements Ifade
    //      record Carpma(Ifade sol, Ifade sag)   implements Ifade
    //
    //  Örnek ifade:  (2 + 3) * 4   ->  Carpma(Toplama(Sayi(2), Sayi(3)), Sayi(4))
    //
    // =====================================================================================

    sealed interface Ifade permits Sayi, Toplama, Carpma {}

    record Sayi(double deger) implements Ifade {}
    record Toplama(Ifade sol, Ifade sag) implements Ifade {}
    record Carpma(Ifade sol, Ifade sag) implements Ifade {}

    // İfadeyi yineleyerek (recursive) hesaplar. Her durumda record alanları ayrıştırılır.
    static double hesapla(Ifade ifade) {
        return switch (ifade) {
            case Sayi(double d)            -> d;
            case Toplama(Ifade sol, Ifade sag) -> hesapla(sol) + hesapla(sag);
            case Carpma(Ifade sol, Ifade sag)  -> hesapla(sol) * hesapla(sag);
        };
    }

    // İfadeyi insan-okur metne çevirir (parantezli). Yine sealed + pattern matching gücü.
    static String yazdir(Ifade ifade) {
        return switch (ifade) {
            case Sayi(double d)            -> (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            case Toplama(Ifade sol, Ifade sag) -> "(" + yazdir(sol) + " + " + yazdir(sag) + ")";
            case Carpma(Ifade sol, Ifade sag)  -> "(" + yazdir(sol) + " * " + yazdir(sag) + ")";
        };
    }

    static void ifadeAgaciDemosu() {
        System.out.println("----- ÖRNEK 2: GERÇEK HAYAT - İFADE AĞACI -----");

        // (2 + 3) * 4  =  20
        Ifade ifade1 = new Carpma(
                new Toplama(new Sayi(2), new Sayi(3)),
                new Sayi(4)
        );

        // 10 + (5 * 6)  =  40
        Ifade ifade2 = new Toplama(
                new Sayi(10),
                new Carpma(new Sayi(5), new Sayi(6))
        );

        System.out.println(yazdir(ifade1) + " = " + hesapla(ifade1));
        System.out.println(yazdir(ifade2) + " = " + hesapla(ifade2));

        System.out.println();
        System.out.println("NEDEN GÜÇLÜ?");
        System.out.println("- sealed: 'Ifade' sadece Sayi/Toplama/Carpma olabilir (kapalı küme).");
        System.out.println("- record: Her düğüm 1 satırda; immutable, eşitlik otomatik.");
        System.out.println("- switch pattern matching: Her durumu güvenle, default'suz işleriz.");
        System.out.println("- Yeni bir işlem (örn. Cikarma) eklenince derleyici tüm switch'leri");
        System.out.println("  güncellememizi ZORUNLU kılar -> unutulan durum = derleme hatası.");
        System.out.println("\n===== DEMO SONU =====");
    }
}
