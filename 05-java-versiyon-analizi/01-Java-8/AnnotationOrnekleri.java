import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  REPEATABLE ANNOTATIONS ve TYPE ANNOTATIONS - Java 8
 * ============================================================================
 *
 * 1) REPEATABLE ANNOTATIONS (Tekrarlanabilir Anotasyonlar)
 *    NEDİR? Aynı anotasyonu aynı eleman üzerinde BİRDEN FAZLA kez kullanabilme.
 *    NEDEN GELDİ? Java 8 öncesinde bir anotasyon bir elemana sadece BİR kez
 *      konabilirdi. Birden fazla değer için elle bir "kapsayıcı (container)"
 *      anotasyon dizisi yazmak gerekiyordu (örn: @Schedules({@Schedule(...),
 *      @Schedule(...)})). Bu çirkin ve zahmetliydi. @Repeatable bunu temizler.
 *
 * 2) TYPE ANNOTATIONS (Tip Anotasyonları)
 *    NEDİR? Anotasyonların artık SADECE bildirimlere değil, TİPİN KULLANILDIĞI
 *      her yere konabilmesi (ElementType.TYPE_USE).
 *    NEDEN GELDİ? Daha güçlü statik analiz / null güvenliği araçları (örn:
 *      Checker Framework) için. Örn: @NonNull String, List<@NonNull String>.
 */
public class AnnotationOrnekleri {

    public static void main(String[] args) throws Exception {

        System.out.println("=== 1. REPEATABLE ANNOTATION ===\n");
        // Gorev sinifindaki tekrarlanan @Zamanlama anotasyonlarini oku
        Zamanlama[] zamanlamalar = Gorev.class.getAnnotationsByType(Zamanlama.class);
        System.out.println("Gorev uzerindeki zamanlamalar:");
        for (Zamanlama z : zamanlamalar) {
            System.out.println("   gun=" + z.gun() + ", saat=" + z.saat());
        }

        System.out.println("\n=== 2. TYPE ANNOTATION (TYPE_USE) ===\n");
        // @NotNull bir tip kullanımı üzerinde; çalışma zamanında özel bir etki
        // yapmaz ama statik analiz araçları bunu okur. Burada sadece derlenip
        // kod içinde kullanılabildiğini gösteriyoruz.
        @NotNull String mesaj = "Bu deger null olmamali (statik analiz ipucu)";
        List<@NotNull String> liste = new ArrayList<>();
        liste.add("eleman1");
        liste.add("eleman2");
        System.out.println("TYPE_USE annotation ile tanimlanan deger: " + mesaj);
        System.out.println("List<@NotNull String> icerigi: " + liste);

        System.out.println("\n=== 3. GERÇEK HAYAT: Cron benzeri zamanlama ===\n");
        // Tekrarlanabilir anotasyonlar, bir gorevin birden fazla calisma
        // zamanini deklaratif olarak tanimlamak icin idealdir.
        System.out.println("Rapor gorevi su zamanlarda calisacak:");
        for (Zamanlama z : Gorev.class.getAnnotationsByType(Zamanlama.class)) {
            System.out.println("   -> Her " + z.gun() + " gunu saat " + z.saat());
        }
    }

    // --- Repeatable annotation tanımı ---
    // @Repeatable, kapsayıcı (container) anotasyonu işaret eder.
    @Repeatable(Zamanlamalar.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Zamanlama {
        String gun();
        String saat();
    }

    // Kapsayıcı (container) anotasyon - derleyici tekrarları buraya toplar
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Zamanlamalar {
        Zamanlama[] value();
    }

    // --- Type annotation tanımı (TYPE_USE) ---
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface NotNull {
    }

    // Aynı anotasyonu birden fazla kez kullanıyoruz (Java 8 öncesi imkansızdı)
    @Zamanlama(gun = "Pazartesi", saat = "09:00")
    @Zamanlama(gun = "Carsamba", saat = "14:00")
    @Zamanlama(gun = "Cuma", saat = "17:30")
    static class Gorev {
    }
}
