import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * ============================================================================
 *  NASHORN JAVASCRIPT ENGINE - Java 8
 * ============================================================================
 *
 * NEDİR?
 *   Nashorn, Java 8 ile gelen, JVM üzerinde JavaScript kodu çalıştıran yüksek
 *   performanslı bir motordur. Java'dan JavaScript çağırabilir, JavaScript'ten
 *   Java nesnelerine erişebilirsin. javax.script (JSR-223) API'si üzerinden
 *   kullanılır.
 *
 * NEDEN GELDİ?
 *   Java 6'daki eski "Rhino" JavaScript motoru yavaş ve eskimişti. Nashorn,
 *   modern invokedynamic bytecode'unu kullanarak çok daha hızlı çalışır.
 *   Uygulamalara gömülü (embedded) scriptleme yeteneği kazandırmak için geldi:
 *   kullanıcıların iş kurallarını JavaScript ile tanımlaması, dinamik
 *   yapılandırma, kural motorları gibi senaryolar.
 *
 * NE İŞE YARAR: Java uygulamasını yeniden derlemeden dinamik script
 *   çalıştırma; kural motorları; şablon işleme; komut satırı (jjs aracı).
 *
 * ÖNEMLİ NOT (Geleceği):
 *   Nashorn, Java 11'de DEPRECATED (kullanımdan kaldırılması planlandı)
 *   edildi ve Java 15'te TAMAMEN KALDIRILDI. Bu yüzden modern projelerde
 *   GraalVM JavaScript gibi alternatifler tercih edilir. Bu örnek tarihsel
 *   ve Java 8 bağlamında öğretici amaçlıdır.
 *
 * DİKKAT: getEngineByName("nashorn") Java 8-14 arasında çalışır. Daha yeni
 *   JDK'larda engine null dönebilir; kod bu durumu nazikçe ele alır.
 */
public class NashornOrnekleri {

    public static void main(String[] args) throws Exception {

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine motor = manager.getEngineByName("nashorn");

        if (motor == null) {
            System.out.println("Nashorn motoru bu JDK surumunde mevcut DEGIL.");
            System.out.println("(Nashorn Java 15'te kaldirildi. Java 8-14 ile calistirin.)");
            return;
        }

        System.out.println("=== 1. Basit JavaScript çalıştırma ===\n");
        motor.eval("print('JavaScript icinden merhaba!')");
        Object sonuc = motor.eval("10 + 20 * 2");
        System.out.println("JS ifadesinin sonucu: " + sonuc);

        System.out.println("\n=== 2. Java'dan JS'e değişken geçirme ===\n");
        motor.put("isim", "Ahmet");
        motor.put("yas", 30);
        motor.eval("print('Ad: ' + isim + ', Yas: ' + yas)");

        System.out.println("\n=== 3. JS fonksiyonu tanımlayıp Java'dan çağırma ===\n");
        motor.eval("function topla(a, b) { return a + b; }");
        Invocable cagrilabilir = (Invocable) motor;
        Object toplam = cagrilabilir.invokeFunction("topla", 15, 27);
        System.out.println("JS topla(15, 27) = " + toplam);

        System.out.println("\n=== 4. GERÇEK HAYAT: Dinamik iş kuralı (kural motoru) ===\n");
        // Senaryo: Indirim kurali JavaScript ile tanimlanir; Java kodu
        // yeniden derlenmeden kural degistirilebilir.
        String indirimKurali =
                "function indirim(tutar) {" +
                "  if (tutar > 1000) return tutar * 0.85;" + // %15 indirim
                "  else if (tutar > 500) return tutar * 0.95;" + // %5 indirim
                "  else return tutar;" +
                "}";
        motor.eval(indirimKurali);

        double[] sepetler = {300, 700, 1500};
        for (double sepet : sepetler) {
            Object indirimli = cagrilabilir.invokeFunction("indirim", sepet);
            System.out.printf("Sepet %.0f TL -> indirimli %.2f TL%n", sepet,
                    ((Number) indirimli).doubleValue());
        }

        System.out.println("\n=== 5. JS icinden Java sınıfına erişim ===\n");
        // Nashorn, Java.type ile Java siniflarini kullanabilir
        motor.eval(
                "var ArrayList = Java.type('java.util.ArrayList');" +
                "var liste = new ArrayList();" +
                "liste.add('Elma'); liste.add('Armut');" +
                "print('JS icinde olusturulan Java listesi: ' + liste);");
    }
}
