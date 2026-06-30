// Ornek1: Java'nın güvenlik modeli — C++'tan farklı davranan noktalar (çalışan kanıt).
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // 1) DİZİ SINIR DENETİMİ: Java sınır aşımını YAKALAR (exception). C++'ta bu tanımsız davranıştır (UB).
        int[] dizi = { 10, 20, 30 };
        try {
            int x = dizi[5]; // sınır dışı
            System.out.println(x);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Dizi sınır denetimi: yakalandı -> " + e.getMessage());
            System.out.println("  (C++'ta bu sessizce bozuk belleğe erişirdi - UB)");
        }

        // 2) OTOMATİK BELLEK: 'new' var ama 'delete/free' YOK. Çöp toplayıcı (GC) halleder.
        for (int i = 0; i < 1000; i++) {
            String gecici = new String("nesne-" + i); // serbest bırakmaya gerek yok
        }
        System.out.println("\nOtomatik bellek: 1000 nesne yaratıldı, delete/free YOK (GC toplar).");

        // 3) REFERANSLAR, İŞARETÇİ DEĞİL: işaretçi aritmetiği yok; null güvenli kontrol edilir.
        String s = null;
        try {
            s.length(); // null referansa erişim -> kontrollü exception (segfault değil)
        } catch (NullPointerException e) {
            System.out.println("\nnull erişimi: NullPointerException (C++'ta çökme/segfault olabilirdi)");
        }

        System.out.println("""

                --- Java vs C++: güvenlik modeli ---
                Java, C++'a kıyasla GÜVENLİĞİ önceler:
                  - Dizi sınır denetimi: aşım exception atar (C++: tanımsız davranış/bozuk bellek).
                  - Otomatik bellek (GC): new var, delete/free yok (C++: elle yönetim -> sızıntı/çökme riski).
                  - Referanslar (işaretçi aritmetiği yok): bellek bozma ve segfault'lar engellenir.
                Bedeli: C++ donanıma daha yakın ve (dikkatli yazılırsa) daha hızlı olabilir; Java taşınabilir ve güvenlidir.""");
    }
}
