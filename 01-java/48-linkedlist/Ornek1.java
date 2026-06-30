// Ornek1: LinkedList — hem List hem Deque (çift uçlu kuyruk) olarak.
// Çalıştırma: java Ornek1.java
import java.util.LinkedList;

public class Ornek1 {

    public static void main(String[] args) {
        LinkedList<String> liste = new LinkedList<>();

        // List gibi: indeksli işlemler
        liste.add("orta");
        liste.addFirst("baş");      // başa ekle (O(1))
        liste.addLast("son");       // sona ekle (O(1))
        System.out.println("Liste: " + liste);
        System.out.println("İlk: " + liste.getFirst() + ", Son: " + liste.getLast());

        // Deque (kuyruk) gibi kullanım: FIFO
        LinkedList<Integer> kuyruk = new LinkedList<>();
        kuyruk.offer(1); kuyruk.offer(2); kuyruk.offer(3); // sona ekle
        System.out.println("\nKuyruk (FIFO): " + kuyruk);
        System.out.println("poll (baştan al): " + kuyruk.poll() + " -> " + kuyruk);
        System.out.println("peek (bakma): " + kuyruk.peek());

        // Stack (yığın) gibi kullanım: LIFO
        LinkedList<Integer> yigin = new LinkedList<>();
        yigin.push(1); yigin.push(2); yigin.push(3); // başa ekle
        System.out.println("\nYığın (LIFO): " + yigin);
        System.out.println("pop (baştan al): " + yigin.pop() + " -> " + yigin);

        System.out.println("""

                --- LinkedList ---
                Çift yönlü bağlı liste: her düğüm önceki+sonrakini tutar.
                Hem List (add/get/remove) hem Deque (addFirst/addLast/offer/poll/push/pop) arayüzlerini uygular.
                Güçlü yan: baş/son ekleme-silme O(1).
                Zayıf yan: indeksli erişim get(i) O(n) (düğümleri tek tek gezer).""");
    }
}
