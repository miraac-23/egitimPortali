// Ornek2: Bir "kütüphane" (yeniden kullanılabilir modül) ve onu kullanan kod.
// Build araçlarının asıl işi, bunun gibi modülleri/bağımlılıkları bir araya getirmektir.
// Burada bağımlılığı tek dosyada simüle ediyoruz.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // Gerçek projede MetinAraclari ayrı bir kütüphane (jar) olurdu ve build aracı
        // (Maven/Gradle) onu projeye eklerdi. Mantık aynı: hazır kodu yeniden kullanmak.
        System.out.println("kapitalize : " + MetinAraclari.kapitalize("merhaba dünya"));
        System.out.println("kisalt     : " + MetinAraclari.kisalt("Bu oldukça uzun bir metindir", 10));
        System.out.println("sluglastir : " + MetinAraclari.sluglastir("Java & Spring Eğitimi"));
        System.out.println("tekrar     : " + MetinAraclari.tekrar("ab", 3));
    }
}

// Yeniden kullanılabilir yardımcı "kütüphane".
class MetinAraclari {
    private MetinAraclari() {} // util sınıfı: nesne oluşturulmaz

    static String kapitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static String kisalt(String s, int maks) {
        return s.length() <= maks ? s : s.substring(0, maks) + "...";
    }

    static String sluglastir(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    static String tekrar(String s, int kez) {
        return s.repeat(Math.max(0, kez));
    }
}
