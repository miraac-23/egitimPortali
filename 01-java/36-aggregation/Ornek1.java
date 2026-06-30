// Ornek1: Aggregation (gevşek HAS-A) — bir nesne, bağımsız yaşayabilen başka nesnelere SAHİPTİR.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.List;

public class Ornek1 {

    public static void main(String[] args) {
        // Oyuncular takımdan BAĞIMSIZ var olabilir (önce oluşturulur).
        Oyuncu o1 = new Oyuncu("Ada");
        Oyuncu o2 = new Oyuncu("Burak");

        Takim takim = new Takim("Yıldızlar");
        takim.oyuncuEkle(o1);   // takım, var olan oyuncuya REFERANS tutar (aggregation)
        takim.oyuncuEkle(o2);
        takim.kadroYaz();

        // Takım dağılsa bile oyuncular yaşamaya devam eder (başka takıma geçebilir).
        Takim digerTakim = new Takim("Şimşekler");
        digerTakim.oyuncuEkle(o1); // aynı oyuncu başka takımda da olabilir
        System.out.println("\n" + o1.ad() + " iki takımda da var: aggregation gevşek bir bağdır.");

        System.out.println("""

                --- Aggregation (gevşek HAS-A) ---
                "Sahip olma" ilişkisi ama bağ GEVŞEK: parça, bütünden bağımsız var olabilir.
                Takım <>-- Oyuncu: takım oyunculara sahiptir ama oyuncular takım olmadan da yaşar.
                Tipik: nesne dışarıdan verilir (constructor/metot ile referans alınır).""");
    }
}

record Oyuncu(String ad) {}

class Takim {
    private final String ad;
    private final List<Oyuncu> oyuncular = new ArrayList<>();
    Takim(String ad) { this.ad = ad; }

    // Var olan bir oyuncuya referans tutar (oyuncuyu burada YARATMAZ).
    void oyuncuEkle(Oyuncu o) { oyuncular.add(o); }
    void kadroYaz() {
        System.out.println(ad + " kadrosu: " + oyuncular.stream().map(Oyuncu::ad).toList());
    }
}
