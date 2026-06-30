// ============================================================================
//  Java 25 - Compact Source Files & Instance Main Methods (JEP 512 - KALICI)
// ============================================================================
//  Bu dosya, Java 25 ile birlikte KALICI (final/standard) hale gelen
//  "Compact Source Files" ve "Instance Main Methods" ozelligini anlatir.
//
//  ONEMLI TARIHCE (DURUST OZET):
//    - Java 21 (JEP 445): "Unnamed Classes and Instance Main Methods" (preview)
//    - Java 22 (JEP 463): "Implicitly Declared Classes..." (2. preview)
//    - Java 23 (JEP 477): "Implicitly Declared Classes and Instance Main
//                          Methods" (3. preview) + otomatik java.io importlari
//    - Java 24 (JEP 495): "Simple Source Files and Instance Main Methods"
//                          (4. preview)
//    - Java 25 (JEP 512): "Compact Source Files and Instance Main Methods"
//                          -> NIHAYET KALICI! Artik --enable-preview gerekmez.
//
//  Bu dosyanin KENDISI klasik bir sinif icinde main barindirir, cunku bir
//  ".java" dosyasinda hem aciklama yapip hem de "compact" sentaksini ayni
//  dosyada gostermek istiyoruz. Compact source ornekleri YORUM olarak
//  asagida sunulmustur; ayrica calistirilabilir bir karsilastirma da var.
// ============================================================================

public class CompactSourceMainMethods {

    // ------------------------------------------------------------------------
    //  1) KLASIK (Java 1.0 - 24) YONTEM
    // ------------------------------------------------------------------------
    //  Yeni baslayan biri "Merhaba Dunya" yazmak icin sunlari OGRENMEK zorundaydi:
    //    - "public" niye var?         (erisim belirleyici)
    //    - "static" niye var?         (nesne olmadan cagrilabilirlik)
    //    - "void" niye var?           (donus tipi)
    //    - "String[] args" niye var?  (komut satiri argumanlari)
    //    - "class" sarmali niye var?  (her seyin bir sinifta olmasi)
    //  Bu 5 kavram, ilk satir kodu yazmadan once kafa karistirir.
    //
    //  Klasik hali:
    //
    //      public class Merhaba {
    //          public static void main(String[] args) {
    //              System.out.println("Merhaba Dunya");
    //          }
    //      }

    public static void main(String[] args) {
        System.out.println("=== Java 25: Compact Source & Instance Main ===\n");

        klasikYontemAciklama();
        yeniYontemAciklama();
        otomatikImportAciklama();
        ogrenmeEgrisiKarsilastirmasi();

        System.out.println("\n--- Calistirilabilir mini gosterim ---");
        // Asagidaki instance-stili metodu, klasik dosyada normal bir nesne
        // uretip cagirarak "instance main" mantigini simule ediyoruz.
        CompactSourceMainMethods uygulama = new CompactSourceMainMethods();
        uygulama.calis();
    }

    // ------------------------------------------------------------------------
    //  2) YENI (Java 25, JEP 512) YONTEM - COMPACT SOURCE FILE
    // ------------------------------------------------------------------------
    static void yeniYontemAciklama() {
        System.out.println(">> YENI YONTEM (Java 25 - KALICI):");
        System.out.println("   Bir .java dosyasinin tamami su kadar olabilir:\n");
        System.out.println("       void main() {");
        System.out.println("           IO.println(\"Merhaba Dunya\");");
        System.out.println("       }\n");
        System.out.println("   Aciklama:");
        System.out.println("   - 'class' bildirimi YOK -> derleyici gizli (compact) bir");
        System.out.println("     sinif olusturur. Buna 'compact source file' denir.");
        System.out.println("   - main artik 'static' OLMAK ZORUNDA DEGIL -> instance main.");
        System.out.println("   - 'String[] args' parametresi ZORUNLU DEGIL -> argumansiz main.");
        System.out.println("   - 'public' belirleyicisi ZORUNLU DEGIL.");
        System.out.println("   - IO.println(...) otomatik kullanilabilir (java.io.IO).\n");
    }

    static void klasikYontemAciklama() {
        System.out.println(">> KLASIK YONTEM (Java 1.0 - 24):");
        System.out.println("       public class Merhaba {");
        System.out.println("           public static void main(String[] args) {");
        System.out.println("               System.out.println(\"Merhaba Dunya\");");
        System.out.println("           }");
        System.out.println("       }");
        System.out.println("   -> Yeni baslayan 5 farkli kavrami ayni anda ogrenmek zorunda.\n");
    }

    // ------------------------------------------------------------------------
    //  3) OTOMATIK IMPORT EDILEN IO
    // ------------------------------------------------------------------------
    static void otomatikImportAciklama() {
        System.out.println(">> OTOMATIK IO (java.io.IO):");
        System.out.println("   Compact source dosyalarinda asagidaki gibi kisa metotlar");
        System.out.println("   otomatik kullanilabilir (ek import gerekmeden):");
        System.out.println("       IO.println(\"...\");   // ekrana yazar");
        System.out.println("       IO.print(\"...\");     // satir sonu eklemeden yazar");
        System.out.println("       String ad = IO.readln(\"Adiniz: \"); // kullanicidan okur");
        System.out.println("   Bu sayede System.out.println / Scanner ezberi ilk gunden");
        System.out.println("   gerekmez; ogrenci hizlica girdi-cikti yapabilir.\n");
    }

    // ------------------------------------------------------------------------
    //  4) OGRENME EGRISI KARSILASTIRMASI
    // ------------------------------------------------------------------------
    static void ogrenmeEgrisiKarsilastirmasi() {
        System.out.println(">> NEDEN GELDI / NE ISE YARAR:");
        System.out.println("   - Egitimde 'ilk program' bariyerini dusurur.");
        System.out.println("   - Kavramlari ASAMALI ogretmeyi mumkun kilar:");
        System.out.println("       Asama 1: void main() { ... }           (en sade)");
        System.out.println("       Asama 2: void main(String[] args)      (argumanlar)");
        System.out.println("       Asama 3: static void main(...)         (statiklik)");
        System.out.println("       Asama 4: public class ... { ... }      (sinif/erisim)");
        System.out.println("   - Kurumsal kodda da kucuk script/araclar icin sadelik saglar.");
        System.out.println("   - DIKKAT: Buyuk projelerde hala klasik sinif yapisi kullanilir;");
        System.out.println("     bu ozellik onu YOK ETMEZ, alternatif/giris yolu sunar.\n");
    }

    // ------------------------------------------------------------------------
    //  Instance main mantigini simule eden ornek (klasik dosyada nesne ile)
    // ------------------------------------------------------------------------
    void calis() {
        // Java 25'te compact source dosyasinda bu metot dogrudan 'void main()'
        // olarak yazilabilir ve nesneyi JVM kendisi olusturur.
        System.out.println("Instance metot calisti (compact source'ta 'void main()' olurdu).");
    }
}

/*
 ============================================================================
  GERCEK COMPACT SOURCE ORNEGI (ayri bir dosyaya kopyalanarak denenebilir)
 ============================================================================
  Asagidaki icerigi "Selam.java" adli AYRI bir dosyaya yazip
  Java 25 ile soyle calistirabilirsiniz:

      java Selam.java        (kaynak dosyasini dogrudan calistirma)

  --- Selam.java icerigi (compact source - Java 25) ---

      void main() {
          IO.println("Merhaba Dunya - Compact Source!");
          String ad = IO.readln("Adiniz nedir? ");
          IO.println("Hos geldin, " + ad + "!");
      }

  --- BU ORNEK NIYE ONEMLI ---
  - Toplam 4 satir. 'class', 'public', 'static', 'String[] args',
    'System.out', 'import' YOK.
  - Java 25'te KALICI oldugu icin --enable-preview GEREKMEZ.

  NOT (DURUSTLUK): Compact source dosyasinda alanlar ve baska metotlar da
  tanimlanabilir; bunlar gizli sinifin uyeleri olur. Birden fazla
  "compact" dosyayi normal sekilde import edip kullanmak ise klasik
  modulariteyi gerektirir.
 ============================================================================
*/
