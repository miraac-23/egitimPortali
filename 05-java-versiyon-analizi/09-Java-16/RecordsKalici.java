// =============================================================================
//  RecordsKalici.java
//  JAVA 16 - Records (Kayıtlar) ARTIK KALICI (FINAL) ÖZELLİK
// =============================================================================
//
//  Records, Java 14'te preview olarak geldi, Java 15'te ikinci preview oldu
//  ve JAVA 16 ile birlikte KALICI (stabil) hale geldi. Artık preview bayrağı
//  olmadan üretimde güvenle kullanılabilir.
//
//  NEDİR?
//  Record, "sadece veri taşıyan" (data carrier) sınıflar yazmak için gelen
//  özel bir sınıf türüdür. Java'da on yıllardır yazdığımız "POJO" / "DTO"
//  sınıflarındaki tekrar eden kalıp kod (boilerplate) ortadan kalkar:
//    - private final alanlar
//    - constructor
//    - getter metotları (record'da alanAdi() şeklinde)
//    - equals(), hashCode()
//    - toString()
//  Bunların hepsi derleyici tarafından OTOMATİK üretilir.
//
//  Derlemek için (Java 16+):   javac RecordsKalici.java
//  Çalıştırmak için:           java RecordsKalici
// =============================================================================

import java.util.List;
import java.util.Objects;

public class RecordsKalici {

    public static void main(String[] args) {

        System.out.println("=== JAVA 16: RECORDS KALICI ÖZELLİK ===\n");

        // ---------------------------------------------------------------------
        // 1) ESKİ YÖNTEM vs YENİ YÖNTEM
        // ---------------------------------------------------------------------
        System.out.println("--- 1) Eski POJO vs Record karşılaştırması ---");

        // Eski yöntemde (aşağıdaki KlasikNokta sınıfına bakın) onlarca satır yazardık.
        KlasikNokta kn1 = new KlasikNokta(3, 4);
        KlasikNokta kn2 = new KlasikNokta(3, 4);
        System.out.println("Klasik POJO toString : " + kn1);
        System.out.println("Klasik POJO equals   : " + kn1.equals(kn2));

        // Yeni yöntem: TEK SATIR (aşağıdaki Nokta record'una bakın)
        Nokta n1 = new Nokta(3, 4);
        Nokta n2 = new Nokta(3, 4);
        System.out.println("Record toString      : " + n1);          // Otomatik
        System.out.println("Record equals        : " + n1.equals(n2)); // Otomatik (true)
        System.out.println("Record hashCode eşit : " + (n1.hashCode() == n2.hashCode()));
        System.out.println("Record getter (x)    : " + n1.x());      // getX() değil, x()
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) COMPACT CONSTRUCTOR (Sıkıştırılmış Yapıcı) ile DOĞRULAMA
        // ---------------------------------------------------------------------
        System.out.println("--- 2) Compact constructor ile doğrulama ---");
        try {
            // Geçerli bir aralık
            TarihAraligi gecerli = new TarihAraligi(1, 10);
            System.out.println("Geçerli aralık oluşturuldu: " + gecerli);

            // Geçersiz aralık -> compact constructor exception fırlatır
            TarihAraligi gecersiz = new TarihAraligi(10, 1);
            System.out.println(gecersiz); // buraya ulaşılmaz
        } catch (IllegalArgumentException e) {
            System.out.println("Doğrulama hatası yakalandı: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) RECORD İÇİNDE CUSTOM METOT, STATIC METOT, EK CONSTRUCTOR
        // ---------------------------------------------------------------------
        System.out.println("--- 3) Record'a ek davranış eklemek ---");
        Para tl = Para.lira(150.0);                 // static factory metodu
        Para indirimli = tl.indirimUygula(0.20);    // custom örnek metodu
        System.out.println("Orijinal fiyat : " + tl.formatli());
        System.out.println("İndirimli fiyat: " + indirimli.formatli());
        System.out.println();

        // ---------------------------------------------------------------------
        // 4) GERÇEK HAYAT: API yanıtı / veri taşıma nesnesi olarak record
        // ---------------------------------------------------------------------
        System.out.println("--- 4) Gerçek hayat: bir e-ticaret sipariş özeti ---");
        var siparis = new SiparisOzeti(
                "SP-2026-0042",
                "Ahmet Yılmaz",
                List.of(
                        new SiparisKalemi("Klavye", 2, 450.0),
                        new SiparisKalemi("Mouse", 1, 250.0)
                )
        );
        System.out.println("Sipariş No  : " + siparis.siparisNo());
        System.out.println("Müşteri     : " + siparis.musteriAdi());
        System.out.println("Kalem sayısı: " + siparis.kalemler().size());
        System.out.println("Toplam tutar: " + siparis.toplamTutar() + " TL");
        System.out.println();

        // ---------------------------------------------------------------------
        // 5) İÇ İÇE (NESTED) RECORD ve LOCAL RECORD
        // ---------------------------------------------------------------------
        System.out.println("--- 5) Local record (metot içinde tanımlı) ---");
        // Java 16 ile metot içinde bile record tanımlanabilir (local record).
        record Cift(String ad, int puan) {}
        var ogrenciler = List.of(
                new Cift("Zeynep", 95),
                new Cift("Mehmet", 80)
        );
        ogrenciler.forEach(o ->
                System.out.println("  " + o.ad() + " -> " + o.puan()));
        System.out.println();

        System.out.println("=== Records, veri modellemeyi sade ve güvenli kılar. ===");
    }

    // =========================================================================
    //  ESKİ YÖNTEM: Klasik POJO (boilerplate dolu)
    //  Sadece 2 alan için ~40 satır kod gerekiyor!
    // =========================================================================
    static final class KlasikNokta {
        private final int x;
        private final int y;

        KlasikNokta(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KlasikNokta that = (KlasikNokta) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "KlasikNokta{x=" + x + ", y=" + y + '}';
        }
    }

    // =========================================================================
    //  YENİ YÖNTEM: Aynı işi yapan record - TEK SATIR
    //  - x ve y "components" (bileşen) olarak adlandırılır.
    //  - Otomatik: private final alanlar, canonical constructor,
    //    x()/y() erişimcileri, equals/hashCode/toString.
    // =========================================================================
    record Nokta(int x, int y) {}

    // =========================================================================
    //  COMPACT CONSTRUCTOR örneği
    //  - Parametre listesi YAZILMAZ; sadece doğrulama/normalizasyon yapılır.
    //  - Atama (this.baslangic = baslangic) otomatik eklenir.
    // =========================================================================
    record TarihAraligi(int baslangic, int bitis) {
        TarihAraligi {  // <-- compact constructor: parantez yok!
            if (baslangic > bitis) {
                throw new IllegalArgumentException(
                        "baslangic (" + baslangic + ") bitisten (" + bitis + ") büyük olamaz");
            }
        }
    }

    // =========================================================================
    //  Record içinde: custom metot + static factory + ek davranış
    //  Record'lar IMMUTABLE (değişmez) olduğundan, "değiştirme" metotları
    //  YENİ bir record örneği döndürür (with-pattern).
    // =========================================================================
    record Para(double tutar, String birim) {

        // Static factory metodu
        static Para lira(double tutar) {
            return new Para(tutar, "TL");
        }

        // Custom örnek metodu: yeni Para döndürür (immutability korunur)
        Para indirimUygula(double oran) {
            return new Para(tutar * (1 - oran), birim);
        }

        // Formatlama yardımcı metodu
        String formatli() {
            return String.format("%.2f %s", tutar, birim);
        }
    }

    // =========================================================================
    //  GERÇEK HAYAT: iç içe record'lar + hesaplanmış (derived) değer
    // =========================================================================
    record SiparisKalemi(String urunAdi, int adet, double birimFiyat) {
        double araToplam() {
            return adet * birimFiyat;
        }
    }

    record SiparisOzeti(String siparisNo, String musteriAdi, List<SiparisKalemi> kalemler) {

        // Compact constructor ile defensive copy (savunmacı kopya)
        // Record bileşeni List ise dıştan değiştirilmemesi için kopyalanır.
        SiparisOzeti {
            kalemler = List.copyOf(kalemler); // değişmez kopya
        }

        // Hesaplanmış toplam tutar (alan değil, metot)
        double toplamTutar() {
            return kalemler.stream()
                    .mapToDouble(SiparisKalemi::araToplam)
                    .sum();
        }
    }
}

// =============================================================================
//  ÖNEMLİ NOTLAR — RECORD'LARDA NELER YAPILABİLİR / YAPILAMAZ
// =============================================================================
//
//  YAPILABİLİR:
//   - Custom örnek metotları, static metotlar, static alanlar eklemek
//   - Compact constructor veya ek (overloaded) constructor tanımlamak
//   - Interface implement etmek (implements)
//   - Generic record tanımlamak: record Kutu<T>(T deger) {}
//   - Otomatik erişimcileri (accessor) override etmek
//   - Annotation kullanmak
//   - Nested ve local record tanımlamak
//
//  YAPILAMAZ:
//   - Başka bir sınıftan extends ile türetmek (record örtük olarak final ve
//     java.lang.Record'tan türer)
//   - Record'u extends ile genişletmek (record örtük final'dır)
//   - Ek instance (örnek) alanı eklemek (tüm state, bileşenlerle tanımlanır)
//   - Bileşenlere ait alanları mutable yapmak (hepsi private final'dır)
//
//  NEREDE KOLAYLIK SAĞLAR:
//   - DTO / API request-response modelleri
//   - Map anahtarları (equals/hashCode hazır)
//   - Stream pipeline'larında ara veri yapıları
//   - Sealed sınıflarla birlikte "algebraic data types" (Java 17 örneklerine bakın)
// =============================================================================
