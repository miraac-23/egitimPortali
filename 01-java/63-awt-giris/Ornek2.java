// Ornek2: AWT geometri ve renk sınıfları — Point, Dimension, Rectangle, Color (ekran gerekmez).
// GUI yerleşimi ve çizimde kullanılan temel veri tipleri.
// Çalıştırma: java Ornek2.java
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Ornek2 {

    public static void main(String[] args) {
        // Point: konum (x, y). Dimension: boyut (genişlik, yükseklik).
        Point konum = new Point(10, 20);
        Dimension boyut = new Dimension(100, 50);
        System.out.println("Konum: " + konum + ", Boyut: " + boyut);

        // Rectangle: konum + boyut. Çarpışma/kesişim/içerme testleri (GUI ve oyunlarda sık).
        Rectangle r1 = new Rectangle(0, 0, 100, 100);
        Rectangle r2 = new Rectangle(50, 50, 100, 100);
        System.out.println("\nr1 ile r2 kesişiyor mu? " + r1.intersects(r2));
        System.out.println("Kesişim alanı: " + r1.intersection(r2));
        System.out.println("Birleşim: " + r1.union(r2));
        System.out.println("r1, (30,40) noktasını içeriyor mu? " + r1.contains(30, 40));

        // Color: RGB renk. GUI bileşen renkleri ve çizim için.
        Color turuncu = new Color(255, 140, 0);
        System.out.printf("%nTuruncu -> R=%d G=%d B=%d, hex=#%06X%n",
                turuncu.getRed(), turuncu.getGreen(), turuncu.getBlue(), turuncu.getRGB() & 0xFFFFFF);
        Color daha = turuncu.brighter();
        System.out.println("Daha parlak: R=" + daha.getRed() + " G=" + daha.getGreen() + " B=" + daha.getBlue());
        System.out.println("Hazır renk: " + Color.BLUE);

        System.out.println("""

                --- AWT geometri ve renk ---
                Point (konum), Dimension (boyut), Rectangle (konum+boyut), Color (renk) — GUI'nin temel veri tipleri.
                Rectangle: intersects/intersection/union/contains ile çarpışma ve yerleşim hesapları (oyunlarda da).
                Color: RGB değerleri, brighter/darker, hazır sabitler (Color.RED...).
                Bu sınıflar EKRAN gerektirmez (saf veri); bileşenleri konumlandırırken/çizerken kullanılır.""");
    }
}
