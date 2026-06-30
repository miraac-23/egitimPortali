// Ornek2: JDK mi JRE mi? — derleyicinin varlığını kontrol ederek kanıtla.
// Çalıştırma: java Ornek2.java
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Ornek2 {

    public static void main(String[] args) {
        // ToolProvider.getSystemJavaCompiler(): JDK'da DERLEYİCİ vardır -> non-null.
        // Salt JRE'de derleyici YOKTUR -> null döner. Yani bu, JDK/JRE ayrımının somut kanıtıdır.
        JavaCompiler derleyici = ToolProvider.getSystemJavaCompiler();

        if (derleyici != null) {
            System.out.println("Sistem derleyicisi MEVCUT -> bir JDK üzerindeyiz.");
            System.out.println("Derleyici sınıfı: " + derleyici.getClass().getName());
            System.out.println("Desteklenen kaynak sürümleri: " + derleyici.getSourceVersions().size() + " adet");
        } else {
            System.out.println("Sistem derleyicisi YOK -> salt JRE üzerindeyiz (yalnızca çalıştırma).");
        }

        System.out.println("""

                --- JDK = JRE + geliştirme araçları ---
                javax.tools.JavaCompiler: JDK'nın getirdiği javac derleyicisine programatik erişim.
                  Bir JDK üzerindeysen non-null (derleyici var); salt JRE'de null olurdu.
                JDK araçları: javac (derle), java (çalıştır), jar (paketle), javadoc (dok), jdb (debug),
                  jlink/jpackage (dağıt), jshell (REPL). Bu portal da örnekleri 'java Dosya.java' ile derleyip çalıştırır.
                Pratik: geliştirme makinende JDK kurarsın; üretim için jlink ile küçük, özel bir çalışma zamanı üretebilirsin.""");
    }
}
