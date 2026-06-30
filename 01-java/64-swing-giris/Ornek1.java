// Ornek1: Swing veri modelleri — DefaultListModel ve DefaultTableModel (ekran gerekmez).
// Swing MVC'dir: VERİ (model) görselden (JList/JTable) ayrıdır. Burada MODEL katmanını gösteriyoruz.
// Çalıştırma: java Ornek1.java
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

public class Ornek1 {

    public static void main(String[] args) {
        // DefaultListModel: bir JList'in arkasındaki veri. (Görsel olmadan da çalışır.)
        DefaultListModel<String> liste = new DefaultListModel<>();
        liste.addElement("Ada");
        liste.addElement("Burak");
        liste.addElement("Can");
        liste.add(1, "Derya");        // araya ekle
        liste.removeElement("Can");
        System.out.println("ListModel boyut: " + liste.size());
        for (int i = 0; i < liste.size(); i++) System.out.println("  " + i + ": " + liste.get(i));

        // DefaultTableModel: bir JTable'ın arkasındaki tablo verisi.
        String[] basliklar = { "Ürün", "Fiyat", "Stok" };
        DefaultTableModel tablo = new DefaultTableModel(basliklar, 0);
        tablo.addRow(new Object[]{ "Klavye", 450, 12 });
        tablo.addRow(new Object[]{ "Mouse", 250, 40 });
        tablo.addRow(new Object[]{ "Monitör", 3200, 5 });

        System.out.println("\nTablo (" + tablo.getRowCount() + " satır, " + tablo.getColumnCount() + " sütun):");
        for (int r = 0; r < tablo.getRowCount(); r++) {
            System.out.printf("  %-8s | %5s TL | stok %s%n",
                    tablo.getValueAt(r, 0), tablo.getValueAt(r, 1), tablo.getValueAt(r, 2));
        }
        // Hücre güncelle (gerçek JTable'da görünüm OTOMATİK yenilenir):
        tablo.setValueAt(15, 0, 2); // Klavye stok -> 15
        System.out.println("Klavye yeni stok: " + tablo.getValueAt(0, 2));

        System.out.println("""

                --- Swing veri modelleri (MVC) ---
                Swing MVC'dir: VERİ (model) ile GÖRÜNÜM (JList/JTable) ayrıdır.
                DefaultListModel -> JList'in verisi; DefaultTableModel -> JTable'ın verisi.
                Model değişince görünüm OTOMATİK güncellenir (model olayları üzerinden — sonraki örnek).
                Bu ayrım sayesinde veri mantığını GUI'den bağımsız test edebilirsin (bu örnek gibi).""");
    }
}
