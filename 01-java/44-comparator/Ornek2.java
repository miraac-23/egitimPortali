// Ornek2: Çok seviyeli sıralama (thenComparing) ve null güvenliği.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Ornek2 {

    record Calisan(String departman, String ad, int maas) {}

    public static void main(String[] args) {
        List<Calisan> liste = new ArrayList<>(List.of(
                new Calisan("Yazılım", "Ada", 50000),
                new Calisan("Yazılım", "Burak", 60000),
                new Calisan("Satış", "Can", 40000),
                new Calisan("Yazılım", "Ada", 55000)));

        // Çok seviyeli: önce departman (artan), eşitse maaş (AZALAN), eşitse ad.
        Comparator<Calisan> sira = Comparator
                .comparing(Calisan::departman)
                .thenComparing(Comparator.comparingInt(Calisan::maas).reversed())
                .thenComparing(Calisan::ad);

        liste.sort(sira);
        System.out.println("Departman ↑, maaş ↓, ad ↑:");
        liste.forEach(c -> System.out.println("  " + c));

        // null güvenliği: nullsFirst / nullsLast
        List<String> adlar = new ArrayList<>(Arrays.asList("Zeynep", null, "Ahmet", null, "Berk"));
        adlar.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("\nnull'lar önce: " + adlar);

        adlar.sort(Comparator.nullsLast(Comparator.reverseOrder()));
        System.out.println("null'lar sonda, ters: " + adlar);

        System.out.println("""

                --- thenComparing ve null güvenliği ---
                thenComparing: ilk ölçüt eşitse İKİNCİ ölçüte geç (zincir). İstediğin kadar seviye ekleyebilirsin.
                Her seviyeyi ayrı ayrı .reversed() ile ters çevirebilirsin (örn. departman ↑ ama maaş ↓).
                naturalOrder()/reverseOrder(): hazır doğal/ters sıra.
                nullsFirst/nullsLast: null içeren listelerde NullPointerException'ı önler.""");
    }
}
