// Ornek1: InetAddress — makineleri ve IP adreslerini tanımak.
// Ağ programlamanın ilk adımı: "kiminle konuşacağım?" — adres çözümleme.
// Çalıştırma: java Ornek1.java
import java.net.InetAddress;

public class Ornek1 {

    public static void main(String[] args) {
        // Loopback (127.0.0.1 / ::1): her zaman "bu makinenin kendisi". İnternet gerektirmez.
        InetAddress loopback = InetAddress.getLoopbackAddress();
        System.out.println("Loopback adresi   : " + loopback.getHostAddress());

        try {
            // localhost adını çöz (loopback'e karşılık gelir).
            InetAddress localhost = InetAddress.getByName("localhost");
            System.out.println("localhost -> IP   : " + localhost.getHostAddress());

            // Bu makinenin ağ adı ve IP'si.
            InetAddress benim = InetAddress.getLocalHost();
            System.out.println("Bu makine (ad)    : " + benim.getHostName());
            System.out.println("Bu makine (IP)    : " + benim.getHostAddress());

            // isReachable: hedefe ulaşılabilir mi? (loopback her zaman ulaşılabilir.)
            System.out.println("Loopback erişilebilir mi? " + loopback.isReachable(1000));

            // IPv4/IPv6 ayrımı:
            System.out.println("\nLoopback IPv4 mü? " + (loopback instanceof java.net.Inet4Address));
        } catch (Exception e) {
            System.out.println("Adres çözümleme uyarısı: " + e.getMessage());
        }

        System.out.println("""

                --- InetAddress ne işe yarar? ---
                Ağda her makine bir IP adresiyle (IPv4: 192.168.1.5, IPv6: 2001:db8::1) tanınır.
                İnsanlar adları (örn. example.com) kullanır; DNS bunları IP'ye çevirir.
                InetAddress: ad<->IP çözümleme, loopback/localhost, ulaşılabilirlik testi sağlar.
                (Bu örnek internet GEREKTİRMEZ; loopback ve yerel makineyle çalışır.)""");
    }
}
