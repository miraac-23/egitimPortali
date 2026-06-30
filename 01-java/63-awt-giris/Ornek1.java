// Ornek1: AWT olay modeli — ActionListener / ActionEvent (gerçek AWT sınıfları, ekran gerekmez).
// GUI'nin kalbi olan "olay-dinleyici" (event-listener) mekanizmasını gösterir.
// Çalıştırma: java Ornek1.java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Ornek1 {

    // Gerçek bir butonu taklit eden basit kaynak: dinleyicileri tutar, tıklanınca olay yayar.
    static class Buton {
        private final String etiket;
        private final List<ActionListener> dinleyiciler = new ArrayList<>();
        Buton(String etiket) { this.etiket = etiket; }

        // Gerçek AWT'deki gibi: addActionListener
        void addActionListener(ActionListener l) { dinleyiciler.add(l); }

        // "Tıklama" -> kayıtlı tüm dinleyicilere ActionEvent gönder.
        void tikla() {
            ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, etiket);
            for (ActionListener l : dinleyiciler) l.actionPerformed(e);
        }
    }

    public static void main(String[] args) {
        Buton kaydet = new Buton("Kaydet");

        // Dinleyici eklemek = "bu olunca şunu yap". Lambda ile (ActionListener functional interface):
        kaydet.addActionListener(e -> System.out.println("1. dinleyici: '" + e.getActionCommand() + "' tıklandı"));
        kaydet.addActionListener(e -> System.out.println("2. dinleyici: veriler kaydediliyor..."));

        // Kullanıcı tıkladı (simülasyon) -> tüm dinleyiciler tetiklenir.
        System.out.println(">> Kaydet butonu tıklandı:");
        kaydet.tikla();

        System.out.println("""

                --- AWT olay modeli (event-listener) ---
                GUI olay tabanlıdır: bir bileşene (kaynak) DİNLEYİCİ eklersin; olay olunca dinleyici çağrılır.
                Gerçek AWT'de: buton.addActionListener(e -> ...); kullanıcı tıklayınca actionPerformed çalışır.
                ActionListener tek metotlu (functional) arayüzdür -> lambda ile yazılır.
                Bu gözlemci (observer) deseni, tüm GUI çatılarının (AWT, Swing, Android, web) temelidir.""");
    }
}
