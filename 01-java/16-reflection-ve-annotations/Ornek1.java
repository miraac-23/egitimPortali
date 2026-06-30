// Ornek1: Reflection ile genel amaçlı bir "nesne inceleyici" ve serializer.
// Gerçek hayatta loglama, JSON üretme ve ORM kütüphaneleri tam olarak bunu yapar:
// bir nesnenin alanlarını çalışma zamanında okuyup işler.
// Çalıştırma: java Ornek1.java
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ornek1 {

    public static void main(String[] args) {
        Urun urun = new Urun(101, "Mekanik Klavye", 1450.0, true);

        // 1) Sınıf hakkında üst düzey bilgi (metadata).
        Class<?> c = urun.getClass();
        System.out.println("Sınıf      : " + c.getSimpleName());
        System.out.println("Üst sınıf  : " + c.getSuperclass().getSimpleName());
        System.out.println("Alan sayısı: " + c.getDeclaredFields().length);

        // 2) Herhangi bir nesneyi alanlarıyla Map'e çevir (mini serializer).
        // JSON/loglama kütüphanelerinin temel fikri budur.
        System.out.println("\nnesneToMap:");
        Map<String, Object> harita = nesneToMap(urun);
        harita.forEach((k, v) -> System.out.println("  " + k + " = " + v));

        // 3) Genel amaçlı bir toString üreticisi (her sınıf için çalışır).
        System.out.println("\nGenel toString:");
        System.out.println("  " + genelToString(urun));
        System.out.println("  " + genelToString(new Musteri("Ada", "ada@site.com")));
    }

    // Reflection ile tüm alanları okuyup Map'e koyar — tip ne olursa olsun çalışır.
    static Map<String, Object> nesneToMap(Object nesne) {
        Map<String, Object> sonuc = new LinkedHashMap<>();
        for (Field f : nesne.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue; // static alanları atla
            f.setAccessible(true); // private alanlara eriş
            try {
                sonuc.put(f.getName(), f.get(nesne));
            } catch (IllegalAccessException e) {
                sonuc.put(f.getName(), "<erişilemedi>");
            }
        }
        return sonuc;
    }

    static String genelToString(Object nesne) {
        StringBuilder sb = new StringBuilder(nesne.getClass().getSimpleName()).append("{");
        Field[] alanlar = nesne.getClass().getDeclaredFields();
        for (int i = 0; i < alanlar.length; i++) {
            alanlar[i].setAccessible(true);
            try {
                sb.append(alanlar[i].getName()).append("=").append(alanlar[i].get(nesne));
            } catch (IllegalAccessException ignored) {}
            if (i < alanlar.length - 1) sb.append(", ");
        }
        return sb.append("}").toString();
    }
}

class Urun {
    private int id;
    private String ad;
    private double fiyat;
    private boolean stokta;
    Urun(int id, String ad, double fiyat, boolean stokta) {
        this.id = id; this.ad = ad; this.fiyat = fiyat; this.stokta = stokta;
    }
}

class Musteri {
    private String ad;
    private String eposta;
    Musteri(String ad, String eposta) { this.ad = ad; this.eposta = eposta; }
}
