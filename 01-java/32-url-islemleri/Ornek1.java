// Ornek1: Bir URL'in parçaları — URI ile ayrıştırma (protokol, host, port, yol, sorgu, fragment).
// (URI ayrıştırma için modern, önerilen tiptir; new URL(String) Java 20+ ile deprecated'tır.)
// Çalıştırma: java Ornek1.java
import java.net.URI;

public class Ornek1 {

    public static void main(String[] args) {
        // Tam donanımlı bir URL'i ayrıştıralım.
        URI uri = URI.create("https://ada:gizli@www.site.com:8443/urunler/ara?kategori=java&sayfa=2#sonuclar");

        System.out.println("Tam URL   : " + uri);
        System.out.println("scheme    : " + uri.getScheme());     // https (protokol)
        System.out.println("userInfo  : " + uri.getUserInfo());   // ada:gizli (kimlik)
        System.out.println("host      : " + uri.getHost());       // www.site.com
        System.out.println("port      : " + uri.getPort());       // 8443
        System.out.println("path      : " + uri.getPath());       // /urunler/ara
        System.out.println("query     : " + uri.getQuery());      // kategori=java&sayfa=2
        System.out.println("fragment  : " + uri.getFragment());   // sonuclar

        // Port belirtilmezse -1 döner (varsayılan port: http=80, https=443 kullanılır).
        URI uri2 = URI.create("http://ornek.com/sayfa");
        System.out.println("\nPort yoksa: " + uri2.getPort() + "  (varsayılan: http=80)");

        // URI vs URL: URI bir KAYNAĞI tanımlar/ayrıştırır; URL ona NASIL erişileceğini de içerir.
        // Bağlantı açmak için: uri.toURL() (sonraki konuda kullanacağız).
        System.out.println("\nURI -> URL'e çevrilebilir mi? " + (uri.isAbsolute()));

        System.out.println("""

                --- URL'in parçaları ---
                <scheme>://<userInfo>@<host>:<port><path>?<query>#<fragment>
                Örnek: https://www.site.com:8443/urunler/ara?kategori=java#sonuclar
                scheme=protokol (http/https/ftp), host=sunucu, port=kapı, path=kaynak yolu,
                query=parametreler (?a=1&b=2), fragment=sayfa içi konum (#).
                URI ayrıştırma için; URL ise erişim (bağlantı açma) için kullanılır.""");
    }
}
