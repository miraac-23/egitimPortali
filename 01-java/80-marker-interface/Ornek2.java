// Ornek2: Marker interface vs marker ANNOTATION (modern alternatif).
// Çalıştırma: java Ornek2.java
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Ornek2 {

    // Modern alternatif: işaretlemeyi bir ANOTASYONLA yapmak.
    @Retention(RetentionPolicy.RUNTIME) // çalışma zamanında okunabilsin
    @interface Denetlenebilir {}

    @Denetlenebilir
    static class OdemeServisi {}
    static class YardimServisi {}   // işaretsiz

    public static void main(String[] args) {
        // Anotasyon etiketi reflection ile kontrol edilir (interface'te instanceof'un karşılığı).
        kontrol(new OdemeServisi());
        kontrol(new YardimServisi());

        System.out.println("""

                --- Marker interface vs marker annotation ---
                İkisi de "etiketleme" yapar; farkları:
                MARKER INTERFACE:
                  + tip güvenliği: yalnızca işaretli tipler bir metoda parametre olabilir (derleyici zorlar).
                  + instanceof ile hızlı kontrol.
                  - bir kez işaretlenince alt tiplere de geçer; esnek değil; tek kalıtım kısıtına dahil.
                MARKER ANNOTATION:
                  + daha esnek: metot/alan/parametreye de uygulanabilir; meta-veri (alanlar) taşıyabilir.
                  + tipe bağlı değil; reflection ile okunur.
                  - derleme-zamanı tip güvenliği YOK; kontrol çalışma zamanında yapılır.
                Modern Java/Spring çoğunlukla ANOTASYON kullanır (@Entity, @Component...). Yine de
                Serializable gibi yerleşik marker interface'ler hâlâ yaygındır.""");
    }

    static void kontrol(Object nesne) {
        boolean isaretli = nesne.getClass().isAnnotationPresent(Denetlenebilir.class);
        System.out.println("  " + nesne.getClass().getSimpleName()
                + " denetlenebilir mi? " + isaretli);
    }
}
