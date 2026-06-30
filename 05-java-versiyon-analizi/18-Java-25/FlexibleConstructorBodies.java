// ============================================================================
//  Java 25 - Flexible Constructor Bodies (JEP 513 - KALICI)
// ============================================================================
//  Flexible Constructor Bodies, Java 25 ile birlikte KALICI (standart) oldu.
//  Artik --enable-preview gerekmez.
//
//  TARIHCE (DURUST OZET):
//    - Java 22 (JEP 447): Statements before super(...) (preview)
//    - Java 23 (JEP 482): Flexible Constructor Bodies (2. preview)
//    - Java 24 (JEP 492): Flexible Constructor Bodies (3. preview)
//    - Java 25 (JEP 513): KALICI (standard)
//
//  NEDIR:
//    Eskiden bir yapilandiricida (constructor) super(...) veya this(...)
//    cagrisi MUTLAKA ilk satir olmak zorundaydi. Artik bu cagridan ONCE
//    "prologue" (on bolum) adi verilen ifadeler calistirilabilir:
//      - alan dogrulama (validation)
//      - argumanlar uzerinde on hesaplama / donusum
//      - super'a gonderilecek degeri hazirlama
//    KISIT: super()/this() cagrisindan ONCE, henuz olusmakta olan nesnenin
//    alanlarina/metotlarina ERISILEMEZ (this hala tam kurulmadi).
//
//  DERLEME / CALISTIRMA (Java 25):
//    javac FlexibleConstructorBodies.java
//    java  FlexibleConstructorBodies
// ============================================================================

public class FlexibleConstructorBodies {

    public static void main(String[] args) {
        System.out.println("=== Java 25: Flexible Constructor Bodies (KALICI) ===\n");

        eskiVsYeniAciklama();

        System.out.println("\n--- 1) Alan dogrulama (super'dan ONCE) ---");
        try {
            new Calisan("Ahmet", -5000); // gecersiz maas -> super'a gitmeden hata
        } catch (IllegalArgumentException e) {
            System.out.println("Beklenen hata: " + e.getMessage());
        }
        Calisan c = new Calisan("Zeynep", 45000);
        System.out.println("Olusturuldu: " + c);

        System.out.println("\n--- 2) On hesaplama / donusum (super'dan ONCE) ---");
        Cember cember = new Cember(2.0);
        System.out.println("Cember -> " + cember);

        System.out.println("\n--- 3) this(...) oncesi calismayla varsayilan uretme ---");
        Dikdortgen kare = new Dikdortgen(5.0);   // tek argumanli -> kare
        Dikdortgen dik  = new Dikdortgen(3.0, 4.0);
        System.out.println("Kare       -> " + kare);
        System.out.println("Dikdortgen -> " + dik);
    }

    static void eskiVsYeniAciklama() {
        System.out.println(">> ESKI YONTEM (Java 24 ve oncesi):");
        System.out.println("   super(...) ILK satir olmak zorundaydi. Dogrulama yapmak icin");
        System.out.println("   genelde STATIK YARDIMCI METOT hilesine basvurulurdu:\n");
        System.out.println("       public Calisan(String ad, int maas) {");
        System.out.println("           super(dogrula(maas));   // hile: static metot ile dogrula");
        System.out.println("           this.ad = ad;");
        System.out.println("       }");
        System.out.println("       private static int dogrula(int m) {");
        System.out.println("           if (m < 0) throw new IllegalArgumentException(...);");
        System.out.println("           return m;");
        System.out.println("       }\n");
        System.out.println(">> YENI YONTEM (Java 25 - KALICI):");
        System.out.println("       public Calisan(String ad, int maas) {");
        System.out.println("           if (maas < 0)                       // super'dan ONCE!");
        System.out.println("               throw new IllegalArgumentException(...);");
        System.out.println("           super(maas);                        // dogrudan, hile yok");
        System.out.println("           this.ad = ad;");
        System.out.println("       }");
        System.out.println("   -> Daha okunabilir, ekstra static metot YOK, niyet acik.\n");
    }
}

// ----------------------------------------------------------------------------
//  Ust sinif: bir maas alanini tutar.
// ----------------------------------------------------------------------------
class Personel {
    protected final int maas;
    Personel(int maas) {
        this.maas = maas;
    }
}

// ----------------------------------------------------------------------------
//  1) ALAN DOGRULAMA: super(...) cagrisindan ONCE dogrulama yapiyoruz.
//     Yeni Java 25 ile bu tamamen yasal ve okunabilir.
// ----------------------------------------------------------------------------
class Calisan extends Personel {
    private final String ad;

    Calisan(String ad, int maas) {
        // super(...) cagrisindan ONCE calisan ifadeler (prologue):
        if (ad == null || ad.isBlank()) {
            throw new IllegalArgumentException("Ad bos olamaz");
        }
        if (maas < 0) {
            throw new IllegalArgumentException("Maas negatif olamaz: " + maas);
        }
        // Dogrulama gectikten sonra ust yapilandiriciyi cagiriyoruz:
        super(maas);
        // super()'dan SONRA artik this'e erisebiliriz:
        this.ad = ad;
    }

    @Override
    public String toString() {
        return "Calisan{ad='" + ad + "', maas=" + maas + "}";
    }
}

// ----------------------------------------------------------------------------
//  2) ON HESAPLAMA: super'a gonderilecek degeri, cagri oncesinde hesapliyoruz.
// ----------------------------------------------------------------------------
class Sekil {
    protected final double alan;
    Sekil(double alan) {
        this.alan = alan;
    }
}

class Cember extends Sekil {
    private final double yaricap;

    Cember(double yaricap) {
        // Yaricaptan alani super'a gondermeden ONCE hesapliyoruz.
        if (yaricap <= 0) {
            throw new IllegalArgumentException("Yaricap pozitif olmali");
        }
        double hesaplananAlan = Math.PI * yaricap * yaricap;
        super(hesaplananAlan);
        this.yaricap = yaricap;
    }

    @Override
    public String toString() {
        return String.format("Cember{yaricap=%.1f, alan=%.4f}", yaricap, alan);
    }
}

// ----------------------------------------------------------------------------
//  3) this(...) ONCESI calisma: argumanlardan varsayilan turetip
//     baska yapilandiriciya yonlendirme.
// ----------------------------------------------------------------------------
class Dikdortgen {
    final double en;
    final double boy;

    // Asil yapilandirici
    Dikdortgen(double en, double boy) {
        this.en = en;
        this.boy = boy;
    }

    // Tek argumanli: kare olusturmak icin. this(...) ONCESI dogrulama yapiyoruz.
    Dikdortgen(double kenar) {
        if (kenar <= 0) {
            throw new IllegalArgumentException("Kenar pozitif olmali");
        }
        // this(...) cagrisindan once ifade calistirabiliyoruz (Java 25).
        this(kenar, kenar);
    }

    @Override
    public String toString() {
        return String.format("Dikdortgen{en=%.1f, boy=%.1f, alan=%.1f}", en, boy, en * boy);
    }
}
