// Ornek2: Swing model olayları — TableModelListener (model değişince görünüm nasıl haberdar olur).
// Çalıştırma: java Ornek2.java
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class Ornek2 {

    public static void main(String[] args) {
        DefaultTableModel model = new DefaultTableModel(new String[]{ "Görev", "Durum" }, 0);

        // Gerçek JTable, modele bir dinleyici ekler ve değişiklikte kendini çizer.
        // Burada o dinleyiciyi BİZ ekliyoruz; modelin yaydığı olayları görelim.
        model.addTableModelListener(new TableModelListener() {
            @Override public void tableChanged(TableModelEvent e) {
                String tur = switch (e.getType()) {
                    case TableModelEvent.INSERT -> "EKLEME";
                    case TableModelEvent.DELETE -> "SİLME";
                    case TableModelEvent.UPDATE -> "GÜNCELLEME";
                    default -> "DİĞER";
                };
                System.out.println("  [model olayı] " + tur + " (satır " + e.getFirstRow() + ")");
            }
        });

        System.out.println(">> addRow:");
        model.addRow(new Object[]{ "Tasarım", "Bekliyor" });
        model.addRow(new Object[]{ "Kodlama", "Bekliyor" });
        System.out.println(">> setValueAt (güncelle):");
        model.setValueAt("Tamamlandı", 0, 1);
        System.out.println(">> removeRow:");
        model.removeRow(1);

        System.out.println("Son durum: " + model.getValueAt(0, 0) + " -> " + model.getValueAt(0, 1));

        System.out.println("""

                --- Swing model olayları ---
                Model değiştiğinde (ekleme/silme/güncelleme), kayıtlı dinleyicilere bir TableModelEvent yayılır.
                Gerçek uygulamada bu dinleyici JTable'ın KENDİSİDİR; olayı alınca ilgili hücreleri yeniden çizer.
                Bu, MVC'nin "model değişti -> görünüm güncellensin" akışıdır (observer deseni).
                Sen sadece modeli güncellersin; senkronizasyonu Swing halleder.""");
    }
}
