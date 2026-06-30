// =====================================================================================
//  SealedClassesPreview.java
//  Java 15 - Sealed Classes (Mühürlü Sınıflar) - İLK PREVIEW (Önizleme)
// =====================================================================================
//
//  ÖNEMLİ SÜRÜM NOTU:
//  ------------------
//  - Sealed Classes ilk olarak Java 15'te (Eylül 2020, JEP 360) PREVIEW geldi.
//  - Java 16'da ikinci preview oldu (JEP 397).
//  - Java 17'de (Eylül 2021, JEP 409) KALICI / STANDART hale geldi.
//
//  DERLEME:
//  - Java 15 veya 16'da derlerken/çalıştırırken preview bayrakları GEREKİR:
//        javac --release 15 --enable-preview SealedClassesPreview.java
//        java  --enable-preview SealedClassesPreview
//  - Java 17 ve sonrasında HİÇBİR ÖZEL BAYRAK GEREKMEZ (kalıcı oldu):
//        javac SealedClassesPreview.java
//        java  SealedClassesPreview
//
//  Bu dosya Java 17+ ile preview bayrağı OLMADAN derlenebilecek şekilde yazılmıştır.
//
// =====================================================================================

public class SealedClassesPreview {

    public static void main(String[] args) {
        System.out.println("===== JAVA 15 SEALED CLASSES (PREVIEW) DEMOSU =====\n");

        sekilDemosu();
        odemeDemosu();
    }

    // =====================================================================================
    //  SEALED CLASSES NEDİR? (Kavramsal açıklama)
    // =====================================================================================
    //
    //  "sealed" (mühürlü) bir sınıf veya arayüz, KENDİSİNİ KİMİN extend/implement
    //  EDEBİLECEĞİNİ açıkça belirler. Bunu "permits" anahtar kelimesiyle yapar.
    //
    //  Anahtar kelimeler:
    //  - sealed     : "Bu sınıf/arayüz mühürlüdür, sadece izin verdiklerim alt tip olabilir."
    //  - permits    : "İzin verilen alt tipler şunlardır: ..."
    //  - final      : Alt tip kapatılır, ondan da kimse türeyemez. (Hiyerarşi burada biter.)
    //  - non-sealed : Alt tip mührü AÇAR, artık herkes ondan türeyebilir. (Kontrolü gevşetir.)
    //
    //  Bir mühürlü sınıfı extend eden HER alt sınıf, şu üçünden BİRİNİ seçmek ZORUNDADIR:
    //  final, sealed veya non-sealed.
    //
    // =====================================================================================

    // -------------------------------------------------------------------------------------
    //  ESKİ DÜNYA (Java 14 ve öncesi) - Bir hiyerarşiyi kapatmak NEDEN zordu?
    // -------------------------------------------------------------------------------------
    //  Bir Shape (Şekil) sınıfının SADECE Circle, Square, Triangle tarafından
    //  extend edilmesini istediğimizi düşünelim. Eski Java'da bunun temiz bir yolu YOKTU:
    //
    //  YÖNTEM A) final yapmak -> Hiç kimse extend edemez. Ama biz BAZILARINA izin
    //            vermek istiyoruz; bu çok katı.
    //
    //  YÖNTEM B) package-private constructor (görünür olmayan kurucu) hilesi ->
    //            Sınıfı sadece aynı pakette türetilebilir kılarsın. Ama:
    //              * Farklı paketten geliştirici "Neden extend edemiyorum?" diye şaşırır.
    //              * Aynı pakete sınıf ekleyen herkes yine de türetebilir (gerçek kontrol yok).
    //              * Derleyici "exhaustive" (tüm durumları kapsayan) switch yapamaz.
    //
    //  YÖNTEM C) private constructor + iç içe statik sınıflar -> Çok hantal, esnek değil.
    //
    //  SONUÇ: Eski yöntemlerin hiçbiri derleyiciye "bu hiyerarşi KAPALI ve alt tipler
    //  şunlardır" bilgisini veremiyordu. Sealed Classes tam olarak bunu çözer.
    // -------------------------------------------------------------------------------------


    // =====================================================================================
    //  ÖRNEK 1: Şekil (Sekil) Hiyerarşisi - SADECE Daire, Kare, Ucgen izinli
    // =====================================================================================

    // "sealed" + "permits": Sekil sadece bu üç sınıf tarafından genişletilebilir.
    // Başka bir sınıf "extends Sekil" yazmaya çalışırsa DERLEME HATASI alır.
    sealed static abstract class Sekil
            permits Daire, Kare, Ucgen {
        abstract double alan();
    }

    // "final": Daire hiyerarşinin yaprağıdır, ondan kimse türeyemez.
    static final class Daire extends Sekil {
        final double yaricap;
        Daire(double yaricap) { this.yaricap = yaricap; }
        double alan() { return Math.PI * yaricap * yaricap; }
    }

    // "final": Kare de kapalıdır.
    static final class Kare extends Sekil {
        final double kenar;
        Kare(double kenar) { this.kenar = kenar; }
        double alan() { return kenar * kenar; }
    }

    // "non-sealed": Ucgen mührü AÇAR. Yani isteyen herkes Ucgen'den türeyebilir
    // (örn. Eskenar, Ikizkenar gibi). Bu, hiyerarşinin bir dalını kasıtlı açmak içindir.
    static non-sealed class Ucgen extends Sekil {
        final double taban, yukseklik;
        Ucgen(double taban, double yukseklik) { this.taban = taban; this.yukseklik = yukseklik; }
        double alan() { return (taban * yukseklik) / 2.0; }
    }

    // non-sealed olduğu için Ucgen'den serbestçe türetebiliyoruz:
    static class Eskenar extends Ucgen {
        Eskenar(double kenar) { super(kenar, kenar * Math.sqrt(3) / 2.0); }
    }

    static void sekilDemosu() {
        System.out.println("----- ÖRNEK 1: ŞEKİL HİYERARŞİSİ -----");

        Sekil[] sekiller = {
                new Daire(5),
                new Kare(4),
                new Ucgen(6, 3),
                new Eskenar(2)
        };

        for (Sekil s : sekiller) {
            System.out.printf("%-10s alan = %.2f%n", aciklama(s), s.alan());
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------------------
    //  SEALED + EXHAUSTIVE SWITCH'in GÜCÜ:
    //  Derleyici, Sekil'in TÜM alt tiplerini bildiği için, switch'in tüm durumları
    //  kapsadığını kontrol edebilir. (Java 21'de pattern matching switch ile birleşince
    //  "default" yazmaya bile gerek kalmaz; aşağıda klasik instanceof ile gösterildi
    //  ki Java 17'de de derlensin.)
    // -------------------------------------------------------------------------------------
    static String aciklama(Sekil s) {
        // Java 16+ pattern matching for instanceof kullanıyoruz (Java 17'de kalıcı).
        if (s instanceof Daire d) {
            return "Daire(r=" + d.yaricap + ")";
        } else if (s instanceof Kare k) {
            return "Kare(a=" + k.kenar + ")";
        } else if (s instanceof Ucgen u) {
            return "Ucgen";
        }
        // sealed sayesinde teorik olarak buraya hiç gelinmez; yine de güvenlik için:
        throw new IllegalStateException("Bilinmeyen şekil: " + s);
    }


    // =====================================================================================
    //  ÖRNEK 2: GERÇEK HAYAT - Ödeme Türü Hiyerarşisi
    // =====================================================================================
    //  Bir ödeme sisteminde ödeme yöntemleri SABİT ve BİLİNEN bir küme olmalıdır:
    //  KrediKarti, Havale, Nakit. Dışarıdan yeni bir ödeme türü eklenmesi
    //  (örn. üçüncü partinin "BitcoinÖdeme" eklemesi) iş kuralları açısından TEHLİKELİDİR.
    //  Sealed ile bu kümeyi MÜHÜRLERİZ; sistem her zaman tüm olasılıkları bilir.
    // =====================================================================================

    sealed interface Odeme
            permits KrediKarti, Havale, Nakit {
        double tutar();
    }

    static record KrediKarti(double tutar, String kartNo) implements Odeme {}
    static record Havale(double tutar, String iban) implements Odeme {}
    static record Nakit(double tutar) implements Odeme {}

    static void odemeDemosu() {
        System.out.println("----- ÖRNEK 2: GERÇEK HAYAT - ÖDEME TÜRÜ -----");

        Odeme[] odemeler = {
                new KrediKarti(250.0, "1234-****-****-5678"),
                new Havale(1000.0, "TR12 0001 0000 ..."),
                new Nakit(75.50)
        };

        for (Odeme o : odemeler) {
            System.out.printf("%-12s -> %s%n", o.getClass().getSimpleName(), islemUcreti(o));
        }
        System.out.println();
        System.out.println("AVANTAJ: 'Odeme' mühürlü olduğu için ileride bir geliştirici");
        System.out.println("yeni bir ödeme türü eklerse, islemUcreti() içindeki switch'i");
        System.out.println("güncellemediği takdirde DERLEYİCİ uyarır/hata verir.");
        System.out.println("Böylece 'unutulan durum' hatası daha derleme aşamasında yakalanır.");
    }

    // Mühürlü arayüz + pattern matching switch = exhaustive (tüm durumları kapsayan) kontrol.
    // Tüm Odeme alt tipleri kapsandığı için derleyici 'default' istemez (Java 21).
    static String islemUcreti(Odeme o) {
        // switch ile pattern matching (Java 21 standardı). Daha eski sürümlerde
        // bunu if-instanceof zinciriyle yazmak gerekirdi.
        return switch (o) {
            case KrediKarti k -> "Komisyon %2 -> " + (k.tutar() * 0.02) + " TL";
            case Havale h     -> "Sabit ücret 5 TL";
            case Nakit n      -> "Ücretsiz";
        };
    }
}
