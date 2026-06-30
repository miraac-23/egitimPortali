import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * ============================================================================
 *  JAVA 11 - LAMBDA PARAMETRELERINDE 'var' KULLANIMI - JEP 323
 * ============================================================================
 *
 *  NEDIR?
 *  ------
 *  Java 10 ile yerel degisken tip cikarimi (Local Variable Type Inference)
 *  geldi: "var x = 10;". ANCAK Java 10'da 'var' lambda parametrelerinde
 *  KULLANILAMIYORDU. Java 11 (JEP 323) bunu mumkun kildi:
 *      (var a, var b) -> a + b
 *
 *  NEDEN GELDI? (Hangi problem cozuldu?)
 *  -------------------------------------
 *  Tek basina "lambda parametresine var yazmak" cok da kisaltma saglamaz;
 *  zaten implicit (tip yazmadan) lambda yazabiliyoruz: (a, b) -> a + b.
 *  ASIL FAYDA: lambda parametrelerine ANNOTATION (ek aciklama, orn. @NonNull)
 *  ekleyebilmek icin bir tipe ihtiyac vardir. 'var' bu tipi saglar:
 *      (@NonNull var a, @NonNull var b) -> a + b
 *  Eskiden annotation eklemek icin TAM tip yazmak zorundaydik:
 *      (@NonNull Integer a, @NonNull Integer b) -> a + b   // uzun
 *  'var' ile annotation'i tutuyoruz ama uzun tip adini yazmiyoruz.
 *  Ayrica tutarlilik saglar: artik 'var' her yerde (yerel, lambda) calisir.
 *
 *  KURALLAR (cok onemli):
 *  ----------------------
 *    1) YA HEPSI YA HICBIRI: Bir lambdada parametrelerin ya HEPSI 'var'
 *       olmali ya da HICBIRI. Karistirilamaz.
 *       GECERSIZ:  (var a, b) -> ...
 *       GECERSIZ:  (var a, Integer b) -> ...   // var + explicit tip karisik
 *       GECERLI:   (var a, var b) -> ...
 *    2) PARANTEZ ZORUNLU: Tek parametre bile olsa parantez sart.
 *       GECERSIZ:  var x -> x * 2
 *       GECERLI:   (var x) -> x * 2
 *
 *  GERCEK HAYAT ORNEGI:
 *  --------------------
 *    Null-safety annotation'lari (Checker Framework, Spring @NonNull) kullanan
 *    kurumsal projelerde, lambda parametrelerine de bu annotation'lari koyup
 *    statik analiz aracina "bu parametre null olamaz" demek icin idealdir.
 * ============================================================================
 */
public class VarLambdaParametre {

    public static void main(String[] args) {

        // ====================================================================
        // 1) Temel kullanim: (var a, var b) -> ...
        // ====================================================================
        System.out.println("=== 1) Temel var lambda ===");

        // YENI (Java 11): var ile
        BiFunction<Integer, Integer, Integer> topla = (var a, var b) -> a + b;
        System.out.println("topla(3, 5) = " + topla.apply(3, 5));

        // Bu YENI yontem, asagidaki ESKI yontemlerle ESDEGER:
        //   ESKI A (implicit): (a, b) -> a + b
        //   ESKI B (explicit): (Integer a, Integer b) -> a + b

        // ====================================================================
        // 2) Tek parametreyle - PARANTEZ ZORUNLU
        // ====================================================================
        System.out.println("\n=== 2) Tek parametre (parantez zorunlu) ===");
        java.util.function.Function<String, String> buyut = (var s) -> s.toUpperCase();
        System.out.println("buyut(\"merhaba\") = " + buyut.apply("merhaba"));
        // DIKKAT: var s -> ...  YAZILAMAZ (derlenmez). (var s) -> ... olmali.

        // ====================================================================
        // 3) Stream icinde var lambda (gercekci kullanim)
        // ====================================================================
        System.out.println("\n=== 3) Stream icinde var ===");
        List<String> kelimeler = List.of("elma", "armut", "kiraz", "incir");

        String sonuc = kelimeler.stream()
                .map((var k) -> k.toUpperCase())            // var parametre
                .filter((var k) -> k.length() > 4)          // var parametre
                .collect(Collectors.joining(", "));
        System.out.println("Sonuc: " + sonuc);

        // ====================================================================
        // 4) ASIL FAYDA: annotation ekleme (yorum icinde)
        // ====================================================================
        // Eger projede bir @NonNull annotation'i olsaydi soyle yazabilirdik:
        //
        //   BinaryOperator<Integer> carp =
        //       (@NonNull var x, @NonNull var y) -> x * y;
        //
        // Burada @NonNull ekleyebilmek icin bir tipe ihtiyac var; 'var' bunu
        // saglar. Annotation'siz haliyle gosterelim:
        BinaryOperator<Integer> carp = (var x, var y) -> x * y;
        System.out.println("\n=== 4) Annotation hazirligi ===");
        System.out.println("carp(4, 6) = " + carp.apply(4, 6));
        System.out.println("(Gercekte: (@NonNull var x, @NonNull var y) -> x * y yazilabilir)");

        // ====================================================================
        // 5) GECERSIZ ORNEKLER (sadece yorum - DERLENMEZ!)
        // ====================================================================
        // BiFunction<Integer,Integer,Integer> kotu1 = (var a, b) -> a + b;       // HATA: karisik
        // BiFunction<Integer,Integer,Integer> kotu2 = (var a, Integer b) -> a+b; // HATA: var + tip
        // Function<Integer,Integer> kotu3 = var x -> x * 2;                       // HATA: parantezsiz

        System.out.println("\nProgram bitti.");
    }
}

/*
 * ============================================================================
 *  ESKI (Java 10) vs YENI (Java 11) OZET
 * ============================================================================
 *  | Durum                          | Java 10        | Java 11       |
 *  |--------------------------------|----------------|---------------|
 *  | var yerel degisken             | VAR            | VAR           |
 *  | var lambda parametresi         | YOK (hata)     | VAR           |
 *  | lambda parametresine annotation| tam tip lazim  | var + annot.  |
 *
 *  Java 10:  (a, b) -> a + b           // var YAZILAMAZ
 *  Java 11:  (var a, var b) -> a + b   // var YAZILABILIR
 * ============================================================================
 */
