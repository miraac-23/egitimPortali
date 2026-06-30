// Ornek1: Garbage Collection (çöp toplama) — bellek kullanımı ve WeakReference.
// Çalıştırma: java Ornek1.java
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Ornek1 {

    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();
        System.out.println("Maks bellek : " + mb(rt.maxMemory()) + " MB");
        System.out.println("Başlangıç kullanılan: " + mb(kullanilan(rt)) + " MB");

        // Çok sayıda nesne üret (çöp olacaklar). GC bunları toplayacak.
        List<byte[]> tutucu = new ArrayList<>();
        for (int i = 0; i < 100; i++) tutucu.add(new byte[100_000]); // ~10 MB
        System.out.println("100 blok sonrası   : " + mb(kullanilan(rt)) + " MB");

        tutucu.clear();        // referanslar gitti -> nesneler ARTIK ULAŞILAMAZ (çöp)
        System.gc();           // GC ÖNERİSİ (garanti değil)
        uyu(100);
        System.out.println("clear + gc sonrası : " + mb(kullanilan(rt)) + " MB (GC topladı)");

        // WeakReference: zayıf referans. Nesneye SADECE weak referans kalırsa GC onu toplayabilir.
        Object nesne = new Object();
        WeakReference<Object> zayif = new WeakReference<>(nesne);
        System.out.println("\nweak.get() (güçlü ref varken): " + (zayif.get() != null ? "var" : "null"));
        nesne = null;          // güçlü referansı kaldır -> yalnızca weak kaldı
        System.gc();
        uyu(100);
        System.out.println("weak.get() (güçlü ref yokken): " + (zayif.get() != null ? "var" : "null (GC topladı)"));

        System.out.println("""

                --- Garbage Collection (çöp toplama) ---
                Java'da belleği SEN yönetmezsin (C/C++ gibi free yok); ULAŞILAMAYAN nesneleri GC otomatik toplar.
                Bir nesneye hiçbir güçlü referans kalmadığında çöptür ve GC onu temizleyebilir.
                System.gc() yalnızca ÖNERİDİR (ne zaman/topkanacağı garanti değil); üretimde elle çağırma.
                WeakReference: 'varsa kullan, GC alırsa sorun değil' (önbellekler). SoftReference: bellek baskısında atılır.
                GC türleri: G1 (varsayılan), ZGC/Shenandoah (düşük gecikme - topic 20). Bellek sızıntısı = istemeden
                tutulan referanslar (statik koleksiyonlar, kapanmayan kaynaklar).""");
    }

    static long kullanilan(Runtime rt) { return rt.totalMemory() - rt.freeMemory(); }
    static long mb(long b) { return b / (1024 * 1024); }
    static void uyu(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
