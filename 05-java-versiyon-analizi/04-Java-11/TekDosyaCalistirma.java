import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  JAVA 11 - TEK DOSYA KAYNAK KODU CALISTIRMA - JEP 330
 *  (Launch Single-File Source-Code Programs)
 * ============================================================================
 *
 *  NEDIR?
 *  ------
 *  Java 11'den itibaren bir .java dosyasini ONCEDEN DERLEMEDEN (javac
 *  calistirmadan) DOGRUDAN calistirabiliriz:
 *
 *      java TekDosyaCalistirma.java
 *
 *  Bu komut dosyayi BELLEKTE derler ve hemen calistirir. Disk'e .class
 *  dosyasi YAZMAZ. Yani tek adimda kaynak kodu -> calisan program.
 *
 *  NEDEN GELDI? (Hangi problem?)
 *  -----------------------------
 *  Java'ya yeni baslayanlar ve hizli deneme yapmak isteyenler icin iki adimli
 *  surec (once javac, sonra java) gereksiz bir engeldi:
 *      $ javac Merhaba.java     # derle -> Merhaba.class olusur
 *      $ java Merhaba           # calistir
 *  Python/Ruby gibi "yaz-calistir" dillerine kiyasla bu fazladan adim,
 *  ozellikle ogrenme ve kucuk script'lerde zaman kaybiydi. JEP 330 bunu
 *  tek adima indirdi.
 *
 *  NE ISE YARAR / NEREDE KOLAYLIK SAGLAR?
 *  --------------------------------------
 *    - OGRENME: Yeni baslayanlar derleme detayina takilmaz.
 *    - HIZLI PROTOTIPLEME: Kucuk bir fikri saniyeler icinde dener.
 *    - SCRIPT YAZMA: Java'yi shell script gibi kullanabiliriz (shebang ile).
 *    - DENEME/TEST: Bir API'yi hizlica denemek icin gecici dosya.
 *
 *  NASIL CALISTIRILIR?
 *  -------------------
 *      java TekDosyaCalistirma.java
 *      java TekDosyaCalistirma.java arg1 arg2     # argumanlarla
 *
 *  GERCEK HAYAT ORNEGI:
 *  --------------------
 *    Bir gelistirici, "su tarih formatlama mantigi dogru calisiyor mu?" diye
 *    merak eder. Yeni bir proje acmak, IDE baslatmak yerine 20 satirlik bir
 *    .java dosyasi yazip 'java Test.java' ile aninda gorur.
 * ============================================================================
 */
public class TekDosyaCalistirma {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println(" Java 11 Tek Dosya Calistirma (JEP 330)");
        System.out.println("===========================================");
        System.out.println("Bu program 'java TekDosyaCalistirma.java' ile");
        System.out.println("ONCEDEN DERLENMEDEN dogrudan calistirilabilir!");
        System.out.println("Bugunun tarihi: " + LocalDate.now());

        // Komut satiri argumanlarini da kullanabiliriz:
        if (args.length > 0) {
            System.out.println("\nVerilen argumanlar:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("  arg[" + i + "] = " + args[i]);
            }
        } else {
            System.out.println("\n(Arguman verilmedi. Deneyin: "
                    + "java TekDosyaCalistirma.java Ahmet 42)");
        }

        // Java 11 ozelliklerini birlikte gosterelim:
        List<String> isimler = List.of("  ali  ", "veli", "  ", "ayse  ");
        String temiz = isimler.stream()
                .map(String::strip)        // Java 11 strip()
                .filter(s -> !s.isBlank()) // Java 11 isBlank()
                .map(String::toUpperCase)
                .collect(Collectors.joining(", "));
        System.out.println("\nTemizlenmis isimler: " + temiz);
        System.out.println("\nProgram tek dosya olarak basariyla calisti!");
    }
}

/*
 * ============================================================================
 *  ESKI YONTEM vs YENI YONTEM
 * ============================================================================
 *
 *  --- ESKI (Java 11 ONCESI): IKI ADIM ---
 *      $ javac TekDosyaCalistirma.java     # 1. Derle (.class olusur)
 *      $ java TekDosyaCalistirma           # 2. Calistir
 *  Dezavantaj: disk'e .class dosyasi yazilir, iki komut gerekir.
 *
 *  --- YENI (Java 11): TEK ADIM ---
 *      $ java TekDosyaCalistirma.java      # Bellekte derle + calistir
 *  Avantaj: tek komut, disk'e .class yazilmaz, hizli.
 *
 *  NOT: .java uzantisini YAZMAK onemlidir. 'java TekDosyaCalistirma.java'
 *  (uzantili) -> kaynak kodu modu. 'java TekDosyaCalistirma' (uzantisiz)
 *  -> onceden derlenmis .class arar.
 *
 * ============================================================================
 *  SHEBANG (#!) ILE SCRIPT OLARAK CALISTIRMA
 * ============================================================================
 *
 *  JEP 330 ayrica Java dosyalarini Unix/Linux/macOS'ta SCRIPT gibi
 *  calistirmaya izin verir. Bunun icin:
 *
 *  1) Dosyanin ILK SATIRINA shebang eklenir (uzanti .java OLMAMALI, orn:
 *     "merhaba" gibi uzantisiz bir dosya):
 *
 *         #!/usr/bin/java --source 11
 *         public class Merhaba {
 *             public static void main(String[] args) {
 *                 System.out.println("Merhaba script!");
 *             }
 *         }
 *
 *  2) Dosyaya calistirma izni verilir:
 *         $ chmod +x merhaba
 *
 *  3) Dogrudan calistirilir (sanki bash/python script'iymis gibi):
 *         $ ./merhaba
 *         Merhaba script!
 *
 *  ONEMLI: Shebang kullanilan dosya .java UZANTILI OLAMAZ. Shebang yalnizca
 *  uzantisiz (veya .java disi) dosyalarda gecerlidir; .java dosyalari icin
 *  'java X.java' komutu kullanilir. --source 11 ile hangi dil seviyesinde
 *  derlenecegi belirtilir.
 *
 *  KISITLAMALAR:
 *    - Tek dosya: birden fazla kaynak dosyaya bagimli olamaz (sadece JDK +
 *      classpath'teki hazir kutuphaneler kullanilabilir).
 *    - Dosya icindeki ILK sinif main metodunu icermeli.
 *    - Buyuk projeler icin DEGIL; kucuk script/prototip icindir.
 * ============================================================================
 */
