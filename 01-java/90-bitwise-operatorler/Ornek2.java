// Ornek2: Kaydırma operatörleri (<< >> >>>) ve gerçek kullanım — izin maskeleri, RGB.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    // İzinler bit bayrağı olarak: her izin bir bit.
    static final int OKU  = 1;       // 0001
    static final int YAZ  = 1 << 1;  // 0010 (2)
    static final int SIL  = 1 << 2;  // 0100 (4)
    static final int YONET= 1 << 3;  // 1000 (8)

    public static void main(String[] args) {
        // KAYDIRMA: << sola (her kayma *2), >> sağa (işaretli, /2), >>> sağa (işaretsiz)
        System.out.println("1 << 4 = " + (1 << 4) + "  (sola 4 -> 16, yani 2^4)");
        System.out.println("32 >> 2 = " + (32 >> 2) + "  (sağa 2 -> 8, yani /4)");
        System.out.println("-8 >>> 28 = " + (-8 >>> 28) + "  (işaretsiz sağ kaydırma)");

        // İZİN MASKESİ: tek bir int'te birden çok bayrak tut (| ile birleştir, & ile kontrol et).
        int editorIzin = OKU | YAZ;           // okuma + yazma
        int adminIzin  = OKU | YAZ | SIL | YONET; // hepsi
        System.out.println("\nEditör izni (ikilik): " + Integer.toBinaryString(editorIzin));
        System.out.println("Editör YAZabilir mi? " + ((editorIzin & YAZ) != 0));   // bit açık mı?
        System.out.println("Editör SİLebilir mi? " + ((editorIzin & SIL) != 0));
        editorIzin |= SIL;                    // silme iznini EKLE (bit aç)
        System.out.println("SİL eklendi -> silebilir mi? " + ((editorIzin & SIL) != 0));
        editorIzin &= ~YAZ;                   // yazma iznini KALDIR (bit kapat)
        System.out.println("YAZ kaldırıldı -> yazabilir mi? " + ((editorIzin & YAZ) != 0));

        // RGB renk paketleme: 3 byte'ı tek int'e sığdır (bit kaydırma + maske).
        int r = 255, g = 140, b = 0;
        int rgb = (r << 16) | (g << 8) | b;   // paketle
        System.out.printf("%nRGB(255,140,0) -> #%06X%n", rgb);
        System.out.println("Geri çöz: r=" + ((rgb >> 16) & 0xFF)
                + ", g=" + ((rgb >> 8) & 0xFF) + ", b=" + (rgb & 0xFF));

        System.out.println("""

                --- Kaydırma ve gerçek kullanım ---
                << sola kaydır (her adım *2), >> işaretli sağa (/2), >>> işaretsiz sağa (üst bitlere 0).
                İZİN/BAYRAK MASKESİ: her izin bir bit; | ile EKLE, & ~ ile KALDIR, & ile KONTROL ET
                  -> birçok boolean'ı tek int'te kompakt tutar (EnumSet de içte bunu yapar).
                RGB paketleme: << ve | ile 3 byte'ı tek int'e sığdır; >> ve & 0xFF ile geri çöz.
                Bit hileleri performans-kritik kod, grafik, ağ protokolleri ve gömülü sistemlerde yaygındır.""");
    }
}
