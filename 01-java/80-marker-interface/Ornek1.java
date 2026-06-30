// Ornek1: Marker (işaretleyici) interface — metodu olmayan, sınıfı "etiketleyen" arayüz.
// Çalıştırma: java Ornek1.java
import java.util.List;

public class Ornek1 {

    // Marker interface: METODU YOK. Amacı, bir sınıfa "yetenek/izin" etiketi koymaktır.
    interface Arsivlenebilir {}    // "bu nesne arşivlenebilir" etiketi

    record Fatura(String no) implements Arsivlenebilir {}
    record Log(String mesaj) implements Arsivlenebilir {}
    record GeciciDosya(String ad) {}   // arşivlenemez (etiket yok)

    // Arşiv servisi: yalnızca işaretli (Arsivlenebilir) nesneleri kabul eder.
    static void arsivle(Object nesne) {
        if (nesne instanceof Arsivlenebilir) {   // etiket KONTROLÜ
            System.out.println("  arşivlendi: " + nesne);
        } else {
            System.out.println("  REDDEDİLDİ (Arsivlenebilir değil): " + nesne);
        }
    }

    public static void main(String[] args) {
        List<Object> nesneler = List.of(
                new Fatura("F-100"), new Log("giriş yapıldı"), new GeciciDosya("temp.tmp"));
        for (Object n : nesneler) arsivle(n);

        System.out.println("""

                --- Marker (işaretleyici) interface ---
                Metodu olmayan, yalnızca bir sınıfı "işaretleyen/etiketleyen" arayüzdür.
                instanceof ile kontrol edilir: "bu nesne şu yeteneğe/izne sahip mi?"
                JDK örnekleri: Serializable (serileştirilebilir), Cloneable (klonlanabilir), RandomAccess (hızlı indeks).
                Derleyici/çalışma zamanı bu etikete göre davranış değiştirir (örn. ObjectOutputStream Serializable arar).""");
    }
}
