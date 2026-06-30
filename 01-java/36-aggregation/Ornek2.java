// Ornek2: Composition (güçlü HAS-A) — parça, bütün olmadan var olamaz; ömrü bütüne bağlıdır.
// Çalıştırma: java Ornek2.java
import java.util.List;

public class Ornek2 {

    public static void main(String[] args) {
        // Ev oluşturulduğunda odaları da ONUNLA BİRLİKTE oluşur (içeride yaratılır).
        Ev ev = new Ev(3);
        ev.bilgiYaz();
        System.out.println("Toplam alan: " + ev.toplamAlan() + " m²");
        // Odalar evin İÇİNDE yaratıldı; ev yok olursa odalar da anlamını yitirir (composition).

        System.out.println("""

                --- Composition (güçlü HAS-A) ---
                Parça, bütüne SIKICA bağlıdır ve ömrü onunla başlar/biter.
                Ev *-- Oda: odalar ev oluşturulurken İÇERİDE yaratılır; ev olmadan oda olmaz.
                Tipik: parça nesneler sınıfın İÇİNDE 'new' ile yaratılır ve dışarı sızdırılmaz.""");
    }
}

class Ev {
    private final List<Oda> odalar;

    Ev(int odaSayisi) {
        // Odaları DIŞARIDAN almıyoruz; evin parçası olarak burada YARATIYORUZ.
        var liste = new java.util.ArrayList<Oda>();
        for (int i = 1; i <= odaSayisi; i++) liste.add(new Oda("Oda-" + i, 12 + i));
        this.odalar = List.copyOf(liste);
    }

    void bilgiYaz() {
        System.out.println("Ev (" + odalar.size() + " oda):");
        odalar.forEach(o -> System.out.println("  " + o.ad() + " - " + o.metrekare() + " m²"));
    }
    int toplamAlan() { return odalar.stream().mapToInt(Oda::metrekare).sum(); }
}

// Oda yalnızca Ev'in içinde anlamlı (bu örnekte dışarı sızdırılmıyor).
record Oda(String ad, int metrekare) {}
