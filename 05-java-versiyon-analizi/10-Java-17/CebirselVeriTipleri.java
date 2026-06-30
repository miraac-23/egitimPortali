// =============================================================================
//  CebirselVeriTipleri.java
//  JAVA 17 (LTS) - Records + Sealed + Pattern Matching = ALGEBRAIC DATA TYPES
//  (Cebirsel Veri Tipleri) - MODERN JAVA'NIN KALBİ
// =============================================================================
//
//  !!! Bu dosya, switch pattern matching (Java 17 PREVIEW) kullanır. Derlerken:
//
//      javac --release 17 --enable-preview CebirselVeriTipleri.java
//      java  --enable-preview CebirselVeriTipleri
//
//  NEDİR? — CEBİRSEL VERİ TİPLERİ (ADT)
//  Fonksiyonel dillerde (Haskell, Scala, Rust...) çok güçlü bir modelleme aracı
//  vardır: bir tip, sonlu sayıda "varyant"ın TOPLAMI olarak tanımlanır.
//    Sekil = Daire | Kare | Dikdortgen           ("ya o ya bu" -> sum type)
//    Nokta = (x VE y)                             ("hem o hem bu" -> product type)
//  Java 17'de bu modeli üç özellikle birebir kurarız:
//    - SEALED  : tipin SONLU varyant kümesini tanımlar (sum type)
//    - RECORD  : her varyantın alanlarını taşır (product type)
//    - PATTERN MATCHING (switch) : varyantlara göre güvenli dallanma
//
//  NEDEN BU KADAR ÖNEMLİ / GÜCÜ NEREDE?
//   1) TİP GÜVENLİĞİ: Geçersiz durumlar TEMSİL EDİLEMEZ hale gelir.
//   2) EKSİKSİZLİK (exhaustiveness): Yeni bir varyant eklediğinizde, onu
//      işlemeyen her switch DERLEME HATASI verir. "Bir yeri güncellemeyi
//      unutma" hatası ortadan kalkar. Bu, bakım açısından devrimseldir.
//   3) OKUNABİLİRLİK: Domain'i kodda neredeyse İngilizce/Türkçe gibi ifade ederiz.
//
//  Aşağıda 3 GERÇEK HAYAT senaryosu var:
//   A) Ödeme yöntemleri (ödeme sistemi)
//   B) Geometrik şekiller (alan/çevre hesabı)
//   C) JSON düğüm tipleri (parser/serializer)
//   + Bonus: Result tipi (Basari | Hata) — fonksiyonel hata yönetimi
// =============================================================================

import java.util.List;
import java.util.Map;

public class CebirselVeriTipleri {

    public static void main(String[] args) {

        System.out.println("=== JAVA 17 (LTS): CEBİRSEL VERİ TİPLERİ (Records+Sealed+Pattern) ===\n");

        // =====================================================================
        //  SENARYO A: ÖDEME SİSTEMİ
        //  Bir ödeme YA kredi kartı, YA havale, YA da kapıda ödeme olabilir.
        //  Her birinin FARKLI alanları (ve farklı komisyonu) vardır.
        // =====================================================================
        System.out.println("####### SENARYO A: ÖDEME SİSTEMİ #######");
        List<Odeme> odemeler = List.of(
                new KrediKarti("4111111111111111", 250.0),
                new Havale("TR12 0001 ...", 1000.0),
                new KapidaOdeme(150.0, true)
        );
        for (Odeme o : odemeler) {
            System.out.printf("  %-12s | tutar=%.2f | komisyon=%.2f | %s%n",
                    o.getClass().getSimpleName(),
                    tutar(o), komisyon(o), aciklama(o));
        }
        System.out.println();

        // =====================================================================
        //  SENARYO B: GEOMETRİK ŞEKİLLER
        // =====================================================================
        System.out.println("####### SENARYO B: GEOMETRİK ŞEKİLLER #######");
        List<Sekil> sekiller = List.of(
                new Daire(5),
                new Kare(4),
                new Ucgen(3, 4, 5)
        );
        for (Sekil s : sekiller) {
            System.out.printf("  %-12s | alan=%.2f | çevre=%.2f%n",
                    s.getClass().getSimpleName(), alan(s), cevre(s));
        }
        System.out.println();

        // =====================================================================
        //  SENARYO C: JSON DÜĞÜM TİPLERİ
        //  Bir JSON değeri: null | boolean | sayı | metin | dizi | nesne
        //  Bu, ADT için klasik bir örnektir (özyinelemeli/recursive yapı).
        // =====================================================================
        System.out.println("####### SENARYO C: JSON SERILEŞTIRME #######");
        Json belge = new JObje(Map.of(
                "ad", new JMetin("Ahmet"),
                "yas", new JSayi(30),
                "aktif", new JBool(true),
                "roller", new JDizi(List.of(new JMetin("admin"), new JMetin("editor")))
        ));
        System.out.println("  JSON çıktısı:");
        System.out.println("  " + json(belge));
        System.out.println();

        // =====================================================================
        //  BONUS: RESULT TİPİ (Basari | Hata) - fonksiyonel hata yönetimi
        //  Exception fırlatmak yerine, sonucu TİP olarak döndürürüz.
        // =====================================================================
        System.out.println("####### BONUS: RESULT TİPİ (hata yönetimi) #######");
        System.out.println("  10 / 2 = " + sonucMetni(bol(10, 2)));
        System.out.println("  10 / 0 = " + sonucMetni(bol(10, 0)));
        System.out.println();

        System.out.println("=== Yeni bir varyant eklerseniz, ilgili her switch DERLEME HATASI verir. ===");
        System.out.println("=== İşte bu yüzden ADT'ler kurumsal kodda devasa bir güvenlik sağlar. ===");
    }

    // =========================================================================
    //  A) ÖDEME — sealed hiyerarşi + record varyantlar
    // =========================================================================
    sealed interface Odeme permits KrediKarti, Havale, KapidaOdeme {}
    record KrediKarti(String kartNo, double tutar) implements Odeme {}
    record Havale(String iban, double tutar) implements Odeme {}
    record KapidaOdeme(double tutar, boolean nakit) implements Odeme {}

    // switch pattern matching ile tutar çıkarımı (record deconstruction yerine
    // basit alan erişimi; Java 17 preview record pattern içermez, o Java 21'dedir)
    static double tutar(Odeme o) {
        return switch (o) {
            case KrediKarti k -> k.tutar();
            case Havale h     -> h.tutar();
            case KapidaOdeme p-> p.tutar();
        };
    }

    // Her ödeme tipinin farklı komisyon kuralı — exhaustiveness sayesinde
    // bir tipi unutursak derleyici uyarır.
    static double komisyon(Odeme o) {
        return switch (o) {
            case KrediKarti k -> k.tutar() * 0.02;   // %2
            case Havale h     -> 0.0;                 // ücretsiz
            case KapidaOdeme p -> p.nakit() ? 0.0 : 5.0; // kartla kapıda +5
        };
    }

    static String aciklama(Odeme o) {
        return switch (o) {
            case KrediKarti k -> "Kart **** " + k.kartNo().substring(k.kartNo().length() - 4);
            case Havale h     -> "IBAN: " + h.iban();
            case KapidaOdeme p -> p.nakit() ? "Kapıda nakit" : "Kapıda kart";
        };
    }

    // =========================================================================
    //  B) GEOMETRİK ŞEKİLLER
    // =========================================================================
    sealed interface Sekil permits Daire, Kare, Ucgen {}
    record Daire(double r) implements Sekil {}
    record Kare(double kenar) implements Sekil {}
    record Ucgen(double a, double b, double c) implements Sekil {}

    static double alan(Sekil s) {
        return switch (s) {
            case Daire d -> Math.PI * d.r() * d.r();
            case Kare k  -> k.kenar() * k.kenar();
            case Ucgen u -> {
                double yp = (u.a() + u.b() + u.c()) / 2; // yarı çevre
                yield Math.sqrt(yp * (yp - u.a()) * (yp - u.b()) * (yp - u.c())); // Heron
            }
        };
    }

    static double cevre(Sekil s) {
        return switch (s) {
            case Daire d -> 2 * Math.PI * d.r();
            case Kare k  -> 4 * k.kenar();
            case Ucgen u -> u.a() + u.b() + u.c();
        };
    }

    // =========================================================================
    //  C) JSON — özyinelemeli (recursive) ADT
    // =========================================================================
    sealed interface Json permits JNull, JBool, JSayi, JMetin, JDizi, JObje {}
    record JNull() implements Json {}
    record JBool(boolean deger) implements Json {}
    record JSayi(double deger) implements Json {}
    record JMetin(String deger) implements Json {}
    record JDizi(List<Json> elemanlar) implements Json {}
    record JObje(Map<String, Json> alanlar) implements Json {}

    // Özyinelemeli serileştirme — switch + pattern matching ile temiz çözüm
    static String json(Json j) {
        return switch (j) {
            case JNull n  -> "null";
            case JBool b  -> Boolean.toString(b.deger());
            case JSayi s  -> (s.deger() == Math.floor(s.deger()))
                    ? Long.toString((long) s.deger())
                    : Double.toString(s.deger());
            case JMetin m -> "\"" + m.deger() + "\"";
            case JDizi d  -> {
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < d.elemanlar().size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(json(d.elemanlar().get(i)));
                }
                yield sb.append("]").toString();
            }
            case JObje o  -> {
                StringBuilder sb = new StringBuilder("{");
                boolean ilk = true;
                for (var e : o.alanlar().entrySet()) {
                    if (!ilk) sb.append(",");
                    sb.append("\"").append(e.getKey()).append("\":").append(json(e.getValue()));
                    ilk = false;
                }
                yield sb.append("}").toString();
            }
        };
    }

    // =========================================================================
    //  BONUS) RESULT TİPİ: Basari | Hata
    //  Fonksiyonel hata yönetimi: exception yerine sonucu tip olarak döndür.
    // =========================================================================
    sealed interface Sonuc permits Basari, Hata {}
    record Basari(int deger) implements Sonuc {}
    record Hata(String mesaj) implements Sonuc {}

    static Sonuc bol(int a, int b) {
        if (b == 0) return new Hata("Sıfıra bölme!");
        return new Basari(a / b);
    }

    static String sonucMetni(Sonuc s) {
        return switch (s) {
            case Basari b -> "Başarılı: " + b.deger();
            case Hata h   -> "Hata: " + h.mesaj();
        };
    }
}

// =============================================================================
//  NEDEN ADT'LER KURUMSAL KODDA KRİTİK?
//
//  Klasik OOP'de "yeni bir alt tip ekleme" kolaydır ama "yeni bir işlem ekleme"
//  zordur (her sınıfa metot eklenir). ADT + switch yaklaşımında "yeni işlem
//  ekleme" kolaydır (yeni bir switch metodu yazılır) ve EN ÖNEMLİSİ: yeni bir
//  VARYANT eklediğinizde, onu işlemeyen TÜM switch'ler DERLEME ZAMANINDA hata
//  verir. Yani "bir yeri güncellemeyi unutma" sınıfı hatalar imkânsız olur.
//
//  Bu yüzden ödeme sistemleri, durum makineleri, parser'lar, kural motorları
//  gibi "sonlu ama net varyantlı" domainlerde Records+Sealed+Pattern üçlüsü
//  modern Java'nın en güçlü tasarım aracıdır.
// =============================================================================
