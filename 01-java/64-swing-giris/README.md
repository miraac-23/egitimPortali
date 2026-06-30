# Swing'e Giriş

**Swing**, Java'nın AWT üzerine kurulu, daha zengin ve platformdan bağımsız masaüstü GUI
kütüphanesidir (1998). AWT'nin işletim sistemine bağlı "ağır" bileşenlerinin aksine, Swing
bileşenleri **Java ile çizilir** (lightweight); bu yüzden her platformda tutarlı görünür ve
görünümü ("look and feel") değiştirilebilir. `JFrame`, `JButton`, `JTable`, `JTree`, `JTabbedPane`
gibi yüzlerce bileşen sunar.

> **Not:** Portal **headless** çalıştığı için gerçek pencere açılamaz. Ancak Swing'in en önemli
> kavramı olan **MVC model katmanı** ekran gerektirmez — bu örnekler Swing'in `DefaultListModel`/
> `DefaultTableModel` gibi gerçek model sınıflarını çalıştırır. Tam pencere kodu aşağıdadır.

## Swing'in kalbi: MVC ve model-görünüm ayrımı

Swing'in en güçlü tasarım fikri **MVC (Model-View-Controller)**'dir: **veri (model)**, onu gösteren
**görünümden (view)** ayrıdır. Örneğin:

- `JList` bir görünümdür; verisi bir **`ListModel`**'dedir (`DefaultListModel`).
- `JTable` bir görünümdür; verisi bir **`TableModel`**'dedir (`DefaultTableModel`).

Bu ayrımın faydası büyük: veri mantığını GUI'den **bağımsız** tutar ve test edersin (bu örnekler
gibi). Örnek 1 (`./Ornek1.java`) `DefaultListModel` ve `DefaultTableModel` ile veriyi (ekleme,
silme, güncelleme) doğrudan, görsel olmadan işler.

## Model olayları: "veri değişti → görünüm güncellensin"

MVC'nin sihri olay bildirimindedir: model değiştiğinde, kayıtlı **dinleyicilere** bir olay yayılır.
Gerçek uygulamada bu dinleyici **görünümün kendisidir** (`JTable`); olayı alınca ilgili hücreleri
yeniden çizer:

```java
model.addTableModelListener(e -> { /* görünümü güncelle */ });
model.addRow(...);       // -> INSERT olayı yayılır
model.setValueAt(...);   // -> UPDATE olayı yayılır
```

Örnek 2 (`./Ornek2.java`) bir `TableModelListener` ekleyip ekleme/güncelleme/silme olaylarını
gözlemler. Sen sadece modeli güncellersin; senkronizasyonu Swing halleder.

## Gerçek Swing penceresi (kendi makinende)

```java
import javax.swing.*;
import java.awt.*;

public class MerhabaSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {        // GUI işleri EDT'de yapılır
            JFrame f = new JFrame("Swing Penceresi");
            JButton b = new JButton("Tıkla");
            JLabel l = new JLabel("Henüz tıklanmadı");
            b.addActionListener(e -> l.setText("Tıklandı!"));

            JPanel panel = new JPanel(new FlowLayout());
            panel.add(b); panel.add(l);
            f.add(panel);
            f.setSize(300, 150);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
```

Önemli kurallar:

- **EDT (Event Dispatch Thread):** Tüm GUI güncellemeleri tek bir özel thread'de yapılmalıdır
  (`SwingUtilities.invokeLater`). Uzun işleri arka planda (`SwingWorker`) çalıştır, EDT'yi bloklama.
- **Layout yöneticileri:** `FlowLayout`, `BorderLayout`, `GridLayout`, `GridBagLayout` bileşenleri
  konumlandırır (elle koordinat yerine).

## Bugün Swing nerede?

Swing hâlâ masaüstü Java uygulamalarında (IntelliJ IDEA dahil!) yaygındır. Alternatifler:
**JavaFX** (daha modern masaüstü), ve çoğu yeni uygulama için **web** (Spring Boot + bir frontend
— bu portal gibi). Yine de MVC, olay modeli ve model-görünüm ayrımı, öğrendiğinde her UI teknolojisine
aktarılan kavramlardır.

## Özet

Swing'in AWT üzerine kurulu, zengin ve platformdan bağımsız GUI kütüphanesi olduğunu; en önemli
fikri olan **MVC model-görünüm ayrımını** (`DefaultListModel`/`DefaultTableModel`; Örnek 1) ve
**model olaylarıyla** görünümün güncellenmesini (`TableModelListener`; Örnek 2) öğrendik; gerçek
`JFrame` kodunu, EDT kuralını ve layout yöneticilerini gördük. Sırada, sunucu tarafı Java'nın
temeli: **Servlet**.
