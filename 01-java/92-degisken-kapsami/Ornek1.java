// Ornek1: Değişken kapsamları — yerel, örnek (instance), statik ve blok kapsamı.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    static int statikSayac = 0;   // STATİK alan: sınıfa ait, TÜM nesneler paylaşır (tek kopya).

    int ornekDeger;               // ÖRNEK (instance) alanı: her nesnenin KENDİ kopyası var.

    Ornek1(int deger) {
        this.ornekDeger = deger;
        statikSayac++;            // her nesne oluşturulduğunda paylaşılan sayaç artar
    }

    void metot() {
        int yerel = 100;          // YEREL değişken: yalnızca bu metot içinde yaşar.
        System.out.println("  yerel=" + yerel + ", ornekDeger=" + ornekDeger + ", statikSayac=" + statikSayac);

        // BLOK kapsamı: { } içinde tanımlanan değişken yalnızca o blokta görünür.
        for (int i = 0; i < 3; i++) {
            int blokDegiskeni = i * 10;   // her tur yeniden oluşur, döngü dışında ERİŞİLEMEZ
            System.out.print("    blok: i=" + i + " blokDegiskeni=" + blokDegiskeni + "; ");
        }
        System.out.println();
        // System.out.println(i);          // HATA: i döngü bloğunun dışında görünmez
        // System.out.println(blokDegiskeni); // HATA: blok dışında erişilemez
    }

    public static void main(String[] args) {
        Ornek1 a = new Ornek1(10);
        Ornek1 b = new Ornek1(20);

        System.out.println("a.ornekDeger=" + a.ornekDeger + " (a'ya özel)");
        System.out.println("b.ornekDeger=" + b.ornekDeger + " (b'ye özel — ayrı kopya)");
        System.out.println("statikSayac=" + Ornek1.statikSayac + " (paylaşılan: 2 nesne yaratıldı)");

        System.out.println("\na.metot():"); a.metot();

        System.out.println("""

                --- Değişken kapsamları (scope) ---
                YEREL (local): metot/yapıcı içinde; yalnızca orada yaşar; her çağrıda yeniden oluşur; varsayılan değeri YOK (atanmalı).
                BLOK: { } içinde (for/if/while gövdesi); yalnızca o blokta görünür.
                ÖRNEK (instance) alanı: her NESNENİN kendi kopyası; nesne yaşadıkça yaşar; varsayılan değer alır (0/null/false).
                STATİK alan: SINIFA ait, tüm nesneler PAYLAŞIR (tek kopya); program boyunca yaşar.
                Kural: en dar kapsamı kullan; değişkeni gerektiği yere en yakın tanımla (okunabilirlik + hata azalır).""");
    }
}
