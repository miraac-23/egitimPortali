// Ornek1: SpEL (Spring Expression Language) — metin olarak ifadeleri çalışma anında değerlendir.
// Gerçek senaryo: indirim kurallarını koda gömmek yerine, ifade olarak tanımlayıp uygulamak.
// Çalıştırma: portal Spring classpath'iyle çalıştırır.
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class Ornek1 {

    record Sepet(double tutar, int urunAdedi, boolean uyeMi) {}

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();

        // --- Basit ifadeler ---
        System.out.println("2 + 3 * 4      = " + parser.parseExpression("2 + 3 * 4").getValue());
        System.out.println("'merhaba'.length() = " + parser.parseExpression("'merhaba'.length()").getValue());
        System.out.println("{1,2,3,4}.![#this*#this] = "
                + parser.parseExpression("{1,2,3,4}.![#this * #this]").getValue()); // koleksiyon projeksiyonu

        // --- Bir nesne üzerinde ifade değerlendirme (gerçek senaryo) ---
        // İndirim kuralları ARTIK koda gömülü değil; metin/ifade olarak tutuluyor.
        // (Bunlar bir yapılandırma dosyasından/veritabanından da gelebilirdi.)
        String[] kurallar = {
                "tutar > 1000 ? tutar * 0.10 : 0",          // 1000 üstüne %10
                "uyeMi ? tutar * 0.05 : 0",                  // üyeye ek %5
                "urunAdedi >= 5 ? 50 : 0"                    // 5+ ürüne 50 TL
        };

        Sepet sepet = new Sepet(1500, 6, true);
        var ctx = new StandardEvaluationContext(sepet); // kök nesne = sepet
        System.out.println("\nSepet: " + sepet);

        double toplamIndirim = 0;
        for (String kural : kurallar) {
            double indirim = parser.parseExpression(kural).getValue(ctx, Double.class);
            System.out.printf("  kural [%s] -> %.2f TL indirim%n", kural, indirim);
            toplamIndirim += indirim;
        }
        System.out.printf("%nToplam indirim: %.2f TL -> ödenecek: %.2f TL%n",
                toplamIndirim, sepet.tutar() - toplamIndirim);

        System.out.println("""

                --- SpEL nerede kullanılır? ---
                @Value("#{...}"), @PreAuthorize("hasRole('ADMIN')"), Spring Security ifadeleri,
                @Cacheable koşulları, Spring Integration yönlendirmeleri... hepsi SpEL'dir.
                Kuralları koddan ayırıp metin olarak tutmak, onları yeniden derlemeden değiştirmeyi sağlar.""");
    }
}
