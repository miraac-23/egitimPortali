/**
 * ============================================================================
 *  INTERFACE'LERDE DEFAULT ve STATIC METOTLAR - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Java 8 ile arayüzler (interface), gövdesi olan iki yeni metot türü
 *   içerebilir:
 *     - default metot : Bir varsayılan uygulama (implementation) sağlar.
 *                       Uygulayan sınıflar override etmek zorunda DEĞİLDİR.
 *     - static metot  : Arayüze bağlı yardımcı (utility) metot. Örnek üzerinden
 *                       değil, arayüz adıyla çağrılır.
 *
 * NEDEN GELDİ? (Hangi problemi çözdü?)
 *   "Interface Evolution" (arayüz evrimi) problemi: Bir arayüzü yayınladıktan
 *   sonra yeni bir metot eklersen, o arayüzü uygulayan TÜM mevcut sınıflar
 *   derlenemez hale gelirdi (her birine metodu eklemek gerekirdi). Bu, geriye
 *   dönük uyumluluğu bozardı.
 *
 *   Asıl tetikleyici: Java 8'in Stream API'si. List, Collection gibi mevcut
 *   arayüzlere forEach(), stream(), removeIf() gibi metotlar eklenmesi
 *   gerekiyordu. Eğer bunlar normal (soyut) metot olsaydı, dünyadaki tüm
 *   Collection uygulamaları bozulurdu. DEFAULT metotlar sayesinde bu metotlar
 *   varsayılan gövdeyle eklendi ve hiçbir mevcut kod bozulmadı.
 *
 * NE İŞE YARAR: Kütüphane geliştiricilerine, mevcut kullanıcıları kırmadan
 *   arayüzlere yeni yetenek ekleme imkânı verir.
 */
public class DefaultStaticMetotlar {

    public static void main(String[] args) {

        System.out.println("=== 1. default Metot: Varsayılan Uygulama ===\n");
        Arac araba = new Araba();
        araba.calistir();        // Araba'nin kendi (override) uygulamasi
        araba.kornaCal();        // arayuzden gelen DEFAULT uygulama

        Arac bisiklet = new Bisiklet();
        bisiklet.calistir();
        bisiklet.kornaCal();     // Bisiklet bunu OVERRIDE etti

        System.out.println("\n=== 2. static Metot: Arayüz Yardımcısı ===\n");
        // Arayüz adıyla çağrılır, örnek gerekmez
        System.out.println(Arac.aracTipiAciklamasi());

        System.out.println("\n=== 3. GERÇEK HAYAT: Ödeme yöntemleri ===\n");
        OdemeYontemi krediKarti = new KrediKarti();
        OdemeYontemi havale = new Havale();

        krediKarti.ode(1000);
        // logla() -> arayuzden gelen ortak default davranis (her ikisi de kullanir)
        krediKarti.logla(1000);
        havale.ode(500);
        havale.logla(500);

        // static metot: komisyon hesaplama yardımcısı
        System.out.println("1000 TL icin standart komisyon: "
                + OdemeYontemi.komisyonHesapla(1000) + " TL");

        System.out.println("\n=== 4. Çoklu Kalıtım Çakışması (Diamond Problem) ===\n");
        // A ve B arayüzlerinin ikisinde de aynı isimli default metot var.
        // C sınıfı bunu çözmek için override etmek ZORUNDADIR (derleyici zorlar).
        C c = new C();
        c.selam();  // C'nin cozumledigi versiyon
    }

    /** default ve static metot içeren arayüz. */
    interface Arac {
        // Soyut metot (uygulayan sınıf MUTLAKA yazmalı)
        void calistir();

        // DEFAULT metot: gövdeli, override zorunlu değil
        default void kornaCal() {
            System.out.println("   Bip bip! (varsayilan korna)");
        }

        // STATIC metot: arayuz duzeyinde yardimci
        static String aracTipiAciklamasi() {
            return "Bu bir kara tasiti arayuzudur.";
        }
    }

    static class Araba implements Arac {
        @Override
        public void calistir() {
            System.out.println("Araba motoru calisti (vroom).");
        }
        // kornaCal override edilmedi -> default kullanilir
    }

    static class Bisiklet implements Arac {
        @Override
        public void calistir() {
            System.out.println("Bisiklet pedallari donuyor.");
        }
        @Override
        public void kornaCal() {
            System.out.println("   Cling cling! (bisiklet zili - override)");
        }
    }

    interface OdemeYontemi {
        void ode(double tutar);

        // Ortak loglama davranışı - tüm ödeme yöntemleri ücretsiz miras alır
        default void logla(double tutar) {
            System.out.println("   [LOG] " + tutar + " TL odeme kaydedildi.");
        }

        static double komisyonHesapla(double tutar) {
            return tutar * 0.015; // %1.5 standart komisyon
        }
    }

    static class KrediKarti implements OdemeYontemi {
        @Override
        public void ode(double tutar) {
            System.out.println("Kredi karti ile " + tutar + " TL odendi.");
        }
    }

    static class Havale implements OdemeYontemi {
        @Override
        public void ode(double tutar) {
            System.out.println("Havale/EFT ile " + tutar + " TL gonderildi.");
        }
    }

    // --- Diamond problem örneği ---
    interface A {
        default void selam() { System.out.println("A'dan selam"); }
    }
    interface B {
        default void selam() { System.out.println("B'den selam"); }
    }
    // Hem A hem B'yi uygular; iki default metot çakışır -> override ZORUNLU
    static class C implements A, B {
        @Override
        public void selam() {
            A.super.selam(); // istersek belirli birini çağırabiliriz
            B.super.selam();
            System.out.println("C, cakismayi cozumledi.");
        }
    }
}
