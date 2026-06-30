// Ornek1: Singleton — tek örnek garantisi (eager ve lazy).
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Nereden istersek aynı nesneyi alırız.
        Ayarlar a1 = Ayarlar.getInstance();
        Ayarlar a2 = Ayarlar.getInstance();
        a1.ayarla("tema", "koyu");
        System.out.println("a1 == a2 ? " + (a1 == a2) + "  (tek örnek)");
        System.out.println("a2'den tema: " + a2.oku("tema"));

        // Lazy singleton: ilk getInstance çağrısına kadar OLUŞTURULMAZ.
        System.out.println("\nLazy singleton ilk erişim:");
        Logger.getInstance().log("uygulama başladı");

        System.out.println("""

                --- Singleton (eager vs lazy) ---
                Amaç: bir sınıfın TÜM uygulamada TEK örneği olsun (yapılandırma, log, bağlantı havuzu).
                Anahtar: private constructor (dışarıdan 'new' engellenir) + tek statik erişim noktası.
                EAGER : örnek sınıf yüklenince hemen oluşturulur (basit, thread-safe).
                LAZY  : örnek ilk istendiğinde oluşturulur (kaynak tasarrufu; ama thread güvenliği konusu var -> Örnek 2).""");
    }
}

// EAGER singleton: örnek sınıf yüklenirken oluşturulur. Basit ve thread-safe.
class Ayarlar {
    private static final Ayarlar TEK = new Ayarlar();
    private final java.util.Map<String, String> map = new java.util.HashMap<>();
    private Ayarlar() {}                          // dışarıdan new engellendi
    public static Ayarlar getInstance() { return TEK; }
    public void ayarla(String k, String v) { map.put(k, v); }
    public String oku(String k) { return map.get(k); }
}

// LAZY singleton (tek thread için basit hali): ilk çağrıda oluşturulur.
class Logger {
    private static Logger ornek;                 // başta null
    private Logger() { System.out.println("(Logger oluşturuldu)"); }
    public static Logger getInstance() {
        if (ornek == null) ornek = new Logger();  // ilk çağrıda yarat
        return ornek;
    }
    public void log(String m) { System.out.println("[LOG] " + m); }
}
