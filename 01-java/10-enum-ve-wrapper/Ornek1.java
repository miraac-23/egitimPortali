// Ornek1: Enum temelleri — tanım, switch ve values().
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // Enum: sabit, sınırlı bir değer kümesi. Tip güvenlidir:
    // sadece tanımlı değerler atanabilir, yanlış metin atanamaz.
    enum Yon { KUZEY, GUNEY, DOGU, BATI }

    static String aciklama(Yon yon) {
        // switch ile enum: case'lerde enum adını sade yazarız (Yon.KUZEY değil, KUZEY).
        return switch (yon) {
            case KUZEY -> "yukarı";
            case GUNEY -> "aşağı";
            case DOGU  -> "sağa";
            case BATI  -> "sola";
        };
    }

    public static void main(String[] args) {
        Yon y = Yon.DOGU;
        System.out.println("Seçilen yön: " + y + " -> " + aciklama(y));

        // values(): tüm enum değerlerini dizi olarak verir.
        System.out.println("\nTüm yönler:");
        for (Yon yon : Yon.values()) {
            // ordinal(): tanım sırasındaki indeks; name(): metin adı.
            System.out.printf("  %d) %s -> %s%n", yon.ordinal(), yon.name(), aciklama(yon));
        }

        // valueOf(): metinden enum'a çevirir (tam eşleşme gerekir).
        Yon metindenGelen = Yon.valueOf("BATI");
        System.out.println("\nvalueOf(\"BATI\") = " + metindenGelen);
    }
}
