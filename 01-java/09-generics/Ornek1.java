// Ornek1: Generic sınıf ve generic metot.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Aynı Kutu sınıfını farklı tiplerle, tip güvenli biçimde kullanabiliriz.
        Kutu<String> metinKutu = new Kutu<>("merhaba");
        Kutu<Integer> sayiKutu = new Kutu<>(42);

        System.out.println("metinKutu içeriği: " + metinKutu.al() + " (uzunluk " + metinKutu.al().length() + ")");
        System.out.println("sayiKutu içeriği : " + sayiKutu.al() + " (kare " + (sayiKutu.al() * sayiKutu.al()) + ")");

        // Derleyici tip uyumunu kontrol eder; yanlış tip ataması derlenmez:
        // metinKutu.koy(123);  // <-- hata: String bekleniyor

        // --- Generic metot ---
        // <T> bildirimi, metodun herhangi bir tiple çalışabileceğini söyler.
        Integer[] sayilar = {3, 1, 4, 1, 5};
        String[] kelimeler = {"elma", "armut"};
        System.out.print("\nSayılar: "); yazdir(sayilar);
        System.out.print("Kelimeler: "); yazdir(kelimeler);

        System.out.println("\nİlk eleman (sayilar): " + ilk(sayilar));
        System.out.println("İlk eleman (kelimeler): " + ilk(kelimeler));
    }

    // Generic metot: T herhangi bir tip olabilir.
    static <T> void yazdir(T[] dizi) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dizi.length; i++) {
            sb.append(dizi[i]);
            if (i < dizi.length - 1) sb.append(", ");
        }
        System.out.println(sb.append("]"));
    }

    static <T> T ilk(T[] dizi) {
        return dizi.length > 0 ? dizi[0] : null;
    }
}

// Generic sınıf: T bir tip parametresidir, kullanım anında belirlenir.
class Kutu<T> {
    private T icerik;
    Kutu(T icerik) { this.icerik = icerik; }
    T al() { return icerik; }
    void koy(T yeni) { this.icerik = yeni; }
}
