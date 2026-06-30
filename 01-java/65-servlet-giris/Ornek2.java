// Ornek2: Servlet yaşam döngüsü (init/service/destroy) ve filtre zinciri SİMÜLASYONU.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    // Servlet yaşam döngüsü: konteyner servlet'i bir kez init eder, her istekte service çağırır, sonda destroy.
    static abstract class Servlet {
        void init() { System.out.println("  [init] servlet başlatıldı (bir kez)"); }
        abstract void service(String istek);
        void destroy() { System.out.println("  [destroy] servlet kapatıldı (bir kez)"); }
    }

    // Filtre: istek servlet'e ULAŞMADAN önce/sonra araya girer (kimlik, log, sıkıştırma...).
    interface Filtre { void uygula(String istek, Runnable sonraki); }

    public static void main(String[] args) {
        Servlet servlet = new Servlet() {
            @Override void service(String istek) { System.out.println("  [service] işleniyor: " + istek); }
        };

        // Filtre zinciri: log -> kimlik -> (servlet). Her filtre 'sonraki'yi çağırarak zinciri ilerletir.
        List<Filtre> filtreler = List.of(
                (istek, sonraki) -> { System.out.println("  [filtre:log] istek geldi: " + istek); sonraki.run(); },
                (istek, sonraki) -> {
                    if (istek.contains("token")) { System.out.println("  [filtre:kimlik] OK"); sonraki.run(); }
                    else System.out.println("  [filtre:kimlik] 401 - reddedildi");
                }
        );

        // Yaşam döngüsü:
        System.out.println(">> Konteyner servlet'i başlatıyor:");
        servlet.init();

        System.out.println("\n>> İstek 1 (token var):");
        zinciriCalistir(filtreler, "GET /panel?token=abc", () -> servlet.service("GET /panel"));

        System.out.println("\n>> İstek 2 (token yok):");
        zinciriCalistir(filtreler, "GET /panel", () -> servlet.service("GET /panel"));

        System.out.println("\n>> Konteyner kapanıyor:");
        servlet.destroy();

        System.out.println("""

                --- Servlet yaşam döngüsü + filtreler ---
                Yaşam döngüsü: init() (bir kez, başlangıçta) -> service() (HER istekte) -> destroy() (bir kez, sonda).
                Filtre (Filter): istek servlet'e ulaşmadan/yanıt dönmeden araya giren ara katman
                  (loglama, kimlik doğrulama, sıkıştırma, CORS...). Zincir halinde çalışır.
                Spring'in interceptor'ları ve filtreleri bu modelin üzerine kuruludur.""");
    }

    static void zinciriCalistir(List<Filtre> filtreler, String istek, Runnable servletCagrisi) {
        // Filtreleri sırayla iç içe çağıran özyinelemeli zincir.
        Runnable zincir = servletCagrisi;
        for (int i = filtreler.size() - 1; i >= 0; i--) {
            Filtre f = filtreler.get(i);
            Runnable sonraki = zincir;
            zincir = () -> f.uygula(istek, sonraki);
        }
        zincir.run();
    }
}
