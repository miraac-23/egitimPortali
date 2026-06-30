// Ornek2: Thread-safe lazy singleton — double-checked locking ve holder idiom.
// Çalıştırma: java Ornek2.java
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Ornek2 {

    public static void main(String[] args) throws InterruptedException {
        // Birden çok thread aynı anda getInstance çağırsa bile TEK örnek olmalı.
        Set<Integer> kimlikler = new CopyOnWriteArraySet<>();
        Runnable is = () -> kimlikler.add(System.identityHashCode(Baglanti.getInstance()));

        Thread[] t = new Thread[20];
        for (int i = 0; i < t.length; i++) { t[i] = new Thread(is); t[i].start(); }
        for (Thread x : t) x.join();

        System.out.println("20 thread'in gördüğü FARKLI örnek sayısı: " + kimlikler.size() + " (1 olmalı)");

        // Holder idiom singleton da tek örnek verir:
        System.out.println("Holder singleton aynı mı? "
                + (Onbellek.getInstance() == Onbellek.getInstance()));
        Onbellek.getInstance().koy("k", "v");
        System.out.println("Holder'dan: " + Onbellek.getInstance().al("k"));

        System.out.println("""

                --- Thread-safe lazy singleton ---
                Tek thread'li lazy (Örnek 1) çok thread'de RISKLIDIR: ikisi aynı anda 'null' görüp İKİ örnek yaratabilir.
                Çözüm 1 — double-checked locking: volatile alan + iki kez null kontrolü + senkron blok.
                Çözüm 2 — holder idiom (EN ZARİF): iç static sınıf, JVM'in sınıf yükleme garantisini kullanır;
                          örnek ilk erişimde, thread-safe ve kilitsiz oluşur.""");
    }
}

// Double-checked locking: volatile + çift kontrol.
class Baglanti {
    private static volatile Baglanti ornek;        // volatile: görünürlük garantisi
    private Baglanti() {}
    public static Baglanti getInstance() {
        if (ornek == null) {                       // 1. kontrol (kilitsiz, hızlı)
            synchronized (Baglanti.class) {
                if (ornek == null) {               // 2. kontrol (kilit içinde)
                    ornek = new Baglanti();
                }
            }
        }
        return ornek;
    }
}

// Holder idiom: en temiz lazy + thread-safe yöntem.
class Onbellek {
    private final Map<String, String> map = new ConcurrentHashMap<>();
    private Onbellek() {}
    private static class Holder { static final Onbellek ORNEK = new Onbellek(); }
    public static Onbellek getInstance() { return Holder.ORNEK; } // Holder ilk erişimde yüklenir
    public void koy(String k, String v) { map.put(k, v); }
    public String al(String k) { return map.get(k); }
}
