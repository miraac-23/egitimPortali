// Ornek1: ArrayDeque — hem yığın (stack) hem kuyruk (queue) için hızlı çift uçlu yapı.
// Çalıştırma: java Ornek1.java
import java.util.ArrayDeque;
import java.util.Deque;

public class Ornek1 {

    public static void main(String[] args) {
        // YIĞIN (LIFO) olarak: push/pop/peek. (Eski Stack sınıfının yerine ÖNERİLEN.)
        Deque<String> yigin = new ArrayDeque<>();
        yigin.push("sayfa1"); yigin.push("sayfa2"); yigin.push("sayfa3");
        System.out.println("Yığın: " + yigin);
        System.out.println("pop (geri): " + yigin.pop() + " -> " + yigin); // son giren ilk çıkar
        System.out.println("peek (tepe): " + yigin.peek());

        // KUYRUK (FIFO) olarak: offer/poll/peek.
        Deque<String> kuyruk = new ArrayDeque<>();
        kuyruk.offer("müşteri1"); kuyruk.offer("müşteri2"); kuyruk.offer("müşteri3");
        System.out.println("\nKuyruk: " + kuyruk);
        System.out.println("poll (ilk): " + kuyruk.poll() + " -> " + kuyruk); // ilk giren ilk çıkar

        // ÇİFT UÇLU: iki uçtan da ekle/çıkar.
        Deque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(1); deque.addLast(2); deque.addFirst(0);
        System.out.println("\nDeque (çift uçlu): " + deque);
        System.out.println("ilk=" + deque.peekFirst() + ", son=" + deque.peekLast());

        System.out.println("""

                --- ArrayDeque ---
                Çift uçlu kuyruk (double-ended queue): her iki uçtan ekle/çıkar O(1).
                Yığın olarak: push/pop/peek (LIFO). Kuyruk olarak: offer/poll/peek (FIFO).
                NEDEN ArrayDeque? Eski Stack (Vector tabanlı, senkronize, yavaş) yerine ÖNERİLİR;
                LinkedList'e göre de daha hızlı ve az bellek kullanır. null eleman KABUL ETMEZ.""");
    }
}
