/**
 * JAVA 21 - UNNAMED CLASSES & INSTANCE MAIN METHODS (JEP 445) - PREVIEW
 * =====================================================================
 *
 * Bu dosya, yeni baslayanlar icin BASITLESTIRILMIS main yazimini anlatir.
 *
 * NEDIR?
 *   Java'da en basit programi bile yazmak icin tum seremoniyi (sinif bildirimi,
 *   public, static, void, String[] args) ogrenmek gerekiyordu. JEP 445 bunu
 *   gevsetir:
 *     - INSTANCE main: 'static' olmayan, parametresiz 'void main()' yazilabilir.
 *     - UNNAMED CLASS: bir dosyada sinif govdesi yazmadan dogrudan metot/alan
 *       tanimlanabilir; derleyici gorunmez bir sinif olusturur.
 *
 * NEDEN GELDI?
 *   Egitimde ilk engeli kaldirmak. Ogrenci "Merhaba Dunya" icin OOP, static,
 *   dizi gibi kavramlari bilmeden basit kod yazabilsin.
 *
 * !!! ONEMLI: BU DOSYA NORMAL (ISIMLI) BIR SINIFTIR !!!
 *   Gercek "unnamed class" ozelligi, sinif bildirimi OLMAYAN bir kaynak dosya
 *   gerektirir (asagida aciklandi). Bu dosya derlenebilir kalsin diye normal
 *   sinif olarak yazilmistir; preview sozdizimi YORUM icinde gosterilmistir.
 *
 * --- DERLEME / CALISTIRMA (bu normal dosya icin; preview gerekmez) ---
 *   javac UnnamedMainMethod.java
 *   java  UnnamedMainMethod
 *
 * --- GERCEK UNNAMED CLASS'I DENEMEK ICIN (preview) ---
 *   Asagidaki ornegi 'Merhaba.java' adli AYRI bir dosyaya, sinif bildirimi
 *   OLMADAN yazin:
 *
 *       void main() {
 *           System.out.println("Merhaba Dunya");
 *       }
 *
 *   Sonra Java 21'de soyle calistirin:
 *       java --release 21 --enable-preview --source 21 Merhaba.java
 *   veya tek dosya kaynak modunda:
 *       java --enable-preview --source 21 Merhaba.java
 *
 *   EVRIM: Java 21 (preview) -> 22, 23 (gelisen preview) -> Java 25 (KALICI,
 *          "Compact Source Files and Instance Main Methods" olarak).
 */
public class UnnamedMainMethod {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: UNNAMED CLASSES & INSTANCE MAIN (PREVIEW) ===\n");

        System.out.println("--- ESKI (tam seremoni) ---");
        System.out.println("""
                  public class Merhaba {
                      public static void main(String[] args) {
                          System.out.println("Merhaba Dunya");
                      }
                  }
                """);

        System.out.println("--- YENI (preview, ayri 'Merhaba.java' dosyasinda) ---");
        System.out.println("""
                  void main() {
                      System.out.println("Merhaba Dunya");
                  }
                """);

        System.out.println("--- ARA ADIM: instance main (sinif var, static yok) ---");
        System.out.println("""
                  public class Merhaba {
                      void main() {                 // 'static' ve 'String[] args' YOK
                          System.out.println("Merhaba Dunya");
                      }
                  }
                """);

        System.out.println("""
                --- AVANTAJ / RISK ---
                * Avantaj : Yeni baslayan dostu; ogrenme egrisini yumusatir.
                * Risk    : Java 21'de PREVIEW; sozdizimi surumler arasi degisti.
                            Kalici hali Java 25'tedir. Egitim/deneme disinda kullanmayin.
                """);
    }
}
