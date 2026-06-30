// Ornek3: Göreli (relative) URL'ler — resolve ve relativize.
// Bir temel adrese göre göreli yolları tam URL'e çevirmek (ve tersi).
// Çalıştırma: java Ornek3.java
import java.net.URI;

public class Ornek3 {

    public static void main(String[] args) {
        URI temel = URI.create("https://site.com/docs/java/");

        // resolve: temel + göreli yol -> tam (mutlak) URL. (HTML'deki <a href="...">, tarayıcı gezinti)
        System.out.println("Temel: " + temel);
        System.out.println("resolve('giris.html')     -> " + temel.resolve("giris.html"));
        System.out.println("resolve('../spring/')     -> " + temel.resolve("../spring/"));
        System.out.println("resolve('/iletisim')      -> " + temel.resolve("/iletisim"));      // kökten
        System.out.println("resolve('https://x.com/y') -> " + temel.resolve("https://x.com/y")); // mutlak -> aynen

        // relativize: iki mutlak URL arasındaki göreli yolu bul (resolve'un tersi).
        URI hedef = URI.create("https://site.com/docs/java/giris.html");
        System.out.println("\nrelativize: " + temel.relativize(hedef) + "  (temele göre göreli yol)");

        // normalize: '.' ve '..' içeren yolu sadeleştir.
        URI karisik = URI.create("https://site.com/a/b/../c/./d");
        System.out.println("normalize('/a/b/../c/./d') -> " + karisik.normalize());

        System.out.println("""

                --- Göreli URL'ler ---
                resolve(göreli): temel adrese göre tam URL üretir — tarayıcıların linkleri çözmesi gibi.
                  'sayfa.html' -> aynı dizinde; '../x' -> üst dizin; '/x' -> kökten; 'http://...' -> aynen.
                relativize(mutlak): iki adres arasındaki göreli yolu bulur (resolve'un tersi).
                normalize(): yoldaki '.' ve '..' parçalarını sadeleştirir.
                Bu işlemler; web tarayıcıları, HTML link çözümleme ve API istemcilerinde sık kullanılır.""");
    }
}
