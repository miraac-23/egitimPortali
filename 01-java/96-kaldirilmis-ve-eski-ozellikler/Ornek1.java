// Ornek1: Kaldırılmış/eski özellikler — çalışan kanıt (Nashorn yok) ve modern karşılıkları.
// Çalıştırma: java Ornek1.java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Ornek1 {

    public static void main(String[] args) {
        // NASHORN (JavaScript motoru): Java 8'de geldi, Java 11'de deprecated, Java 15'te KALDIRILDI.
        ScriptEngineManager yonetici = new ScriptEngineManager();
        ScriptEngine nashorn = yonetici.getEngineByName("nashorn");
        System.out.println("Nashorn JS motoru: " + (nashorn == null ? "YOK (Java 15'te kaldırıldı)" : "var"));
        System.out.println("Yerleşik script motoru sayısı: " + yonetici.getEngineFactories().size()
                + " (Nashorn gidince genelde 0)");
        System.out.println("  -> Modern alternatif: GraalVM JavaScript (ayrı bağımlılık).");

        // finalize(): nesne yok edilmeden önce çağrılırdı; öngörülemez, Java 9'da deprecated.
        System.out.println("\nfinalize(): kullanma! -> try-with-resources (AutoCloseable) veya java.lang.ref.Cleaner.");

        // Eski tarih API'si: Date/Calendar -> java.time (topic 88)
        System.out.println("Date/Calendar -> java.time (LocalDate/LocalDateTime) ile değiştirildi.");

        System.out.println("""

                --- Kaldırılmış ve eski (legacy) özellikler ---
                Java sürümler boyunca bazı özellikleri kullanımdan kaldırır/siler. Bilmen gerekenler:
                  APPLET (java.applet): tarayıcıda çalışan Java; güvenlik nedeniyle terk edildi, JDK'dan KALDIRILDI.
                    -> Yerine: web (Spring Boot + JS), masaüstü için JavaFX/Swing.
                  NASHORN: JDK'nın JavaScript motoru; Java 15'te kaldırıldı. -> GraalVM JS.
                  finalize(): öngörülemez yıkıcı; deprecated. -> try-with-resources / Cleaner.
                  CORBA, Java EE modülleri (JAXB/JAX-WS): Java 11'de kaldırıldı. -> ayrı bağımlılıklar.
                  Security Manager: deprecated (Java 17).
                  Date/Calendar, Vector/Stack/Hashtable: 'çalışır ama eski'; modern karşılıkları tercih edilir.
                Ders: bir özelliğin var olması onu KULLANMAN gerektiği anlamına gelmez; sürüm notlarını izle.""");
    }
}
