# AWT'ye Giriş (Abstract Window Toolkit)

**AWT (Abstract Window Toolkit)**, Java'nın ilk grafik arayüz (GUI) kütüphanesidir (1996). Pencere,
buton, metin kutusu gibi bileşenleri ve bunların olaylarını (tıklama, tuş) yönetir. Bugün masaüstü
GUI'de yerini büyük ölçüde **Swing**'e (sonraki konu) bırakmıştır; ama Swing AWT'nin üzerine
kurulduğu ve **olay modeli** ile **geometri/renk** sınıfları hâlâ ortak kullanıldığı için AWT'yi
anlamak önemlidir.

> **Not:** Bu portal **headless** (ekransız) çalışır; gerçek bir pencere açamaz. Bu yüzden örnekler
> AWT'nin **ekran gerektirmeyen** gerçek sınıflarını (olay modeli, geometri, renk) kullanır. Tam
> pencere kodu aşağıda gösterilir — onu kendi makinende çalıştırabilirsin.

## GUI'nin kalbi: olay-dinleyici modeli

Grafik arayüzler **olay tabanlıdır**: program akışı yukarıdan aşağı değil, kullanıcı etkileşimiyle
(tıklama, tuş, fare) ilerler. Mekanizma şudur: bir bileşene (olay **kaynağı**) bir **dinleyici**
(listener) eklersin; o olay olduğunda dinleyicinin metodu çağrılır.

```java
buton.addActionListener(e -> System.out.println("tıklandı: " + e.getActionCommand()));
```

`ActionListener` tek metotlu (functional) bir arayüzdür; bu yüzden lambda ile yazılır. Örnek 1
(`./Ornek1.java`) bir butonu taklit eden bir kaynak kurar, ona dinleyiciler ekler ve "tıklama"yı
simüle ederek gerçek `ActionEvent`/`ActionListener` akışını gösterir. Bu **gözlemci (observer)
deseni**, tüm GUI çatılarının (AWT, Swing, Android, web) temelidir.

## Geometri ve renk sınıfları

AWT, bileşenleri konumlandırmak ve çizmek için ekran gerektirmeyen veri sınıfları sağlar:

- **`Point`** (x, y konum), **`Dimension`** (genişlik, yükseklik)
- **`Rectangle`** (konum + boyut): `intersects`, `intersection`, `union`, `contains` — çarpışma ve
  yerleşim hesapları (GUI'de ve oyunlarda).
- **`Color`** (RGB renk): bileşen renkleri, çizim; `brighter`/`darker`, hazır sabitler.

Örnek 2 (`./Ornek2.java`) bunları çalıştırır.

## Gerçek AWT penceresi (kendi makinende)

Portal headless olduğu için aşağıdaki kod burada çalışmaz; ama gerçek bir ortamda bir pencere açar:

```java
import java.awt.*;
import java.awt.event.*;

public class MerhabaAWT {
    public static void main(String[] args) {
        Frame pencere = new Frame("AWT Penceresi");
        Button buton = new Button("Tıkla");
        Label etiket = new Label("Henüz tıklanmadı");

        buton.addActionListener(e -> etiket.setText("Tıklandı!"));

        pencere.setLayout(new FlowLayout());
        pencere.add(buton);
        pencere.add(etiket);
        pencere.setSize(300, 150);
        pencere.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { pencere.dispose(); }
        });
        pencere.setVisible(true);
    }
}
```

Buradaki `Frame` (pencere), `Button`, `Label`, `FlowLayout` (yerleşim yöneticisi) ve
`WindowListener` AWT'nin görsel parçalarıdır.

## AWT vs Swing

| | AWT | Swing |
|---|-----|-------|
| Çıkış | 1996 (ilk) | 1998 (AWT üzerine) |
| Bileşenler | İşletim sistemine bağlı (heavyweight) | Java ile çizilen (lightweight) |
| Görünüm | Platforma göre değişir | Tutarlı, "look and feel" değişebilir |
| Bileşen zenginliği | Az (Button, Label, TextField...) | Çok (JTable, JTree, JTabbedPane...) |
| Bugün | Çoğunlukla Swing'e bırakıldı | Masaüstünde yaygın |

## Özet

AWT'nin Java'nın ilk GUI kütüphanesi olduğunu; tüm grafik arayüzlerin temeli olan **olay-dinleyici
modelini** (`ActionListener`/`ActionEvent`; Örnek 1) ve ekran gerektirmeyen **geometri/renk**
sınıflarını (`Point`/`Rectangle`/`Color`; Örnek 2) öğrendik; gerçek pencere kodunu ve AWT↔Swing
farkını gördük. Sırada, AWT'nin üzerine kurulan modern masaüstü GUI kütüphanesi: **Swing**.
