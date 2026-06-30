// Ornek2: Eski (legacy) koleksiyonlar vs modern karşılıkları.
// Çalıştırma: java Ornek2.java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class Ornek2 {

    public static void main(String[] args) {
        // Vector (eski, senkronize) -> ArrayList (modern, senkronsuz/hızlı)
        Vector<Integer> vector = new Vector<>(List.of(1, 2, 3));
        List<Integer> arrayList = new ArrayList<>(List.of(1, 2, 3));
        System.out.println("Vector  : " + vector + "  (her metot synchronized -> yavaş)");
        System.out.println("ArrayList: " + arrayList + "  (senkronsuz -> hızlı, tercih edilen)");

        // Stack (eski, Vector'dan türer) -> ArrayDeque (modern yığın/kuyruk)
        Stack<String> stack = new Stack<>();
        stack.push("a"); stack.push("b");
        Deque<String> deque = new ArrayDeque<>();
        deque.push("a"); deque.push("b");
        System.out.println("\nStack.pop()   : " + stack.pop() + "  (eski)");
        System.out.println("ArrayDeque.pop(): " + deque.pop() + "  (modern yığın, daha hızlı)");

        // Hashtable (eski, senkronize, null kabul etmez) -> HashMap (modern)
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("x", 10); hashMap.put(null, 0); // HashMap null anahtara izin verir
        System.out.println("\nHashMap (null anahtar ok): " + hashMap);

        System.out.println("""

                --- Legacy vs modern koleksiyonlar ---
                Eski (Java 1.0-1.1, hepsi synchronized):  Vector, Stack, Hashtable, Enumeration.
                Modern (Java 1.2+ Collections Framework): ArrayList, ArrayDeque, HashMap, Iterator.
                Neden modern? Eskiler HER metotta kilit alır (tek thread'de gereksiz yavaşlık).
                Eş zamanlılık gerekiyorsa: Collections.synchronizedList(...) veya java.util.concurrent
                (ConcurrentHashMap, CopyOnWriteArrayList) kullan — Vector/Hashtable DEĞİL.
                Kural: yeni kodda ArrayList/ArrayDeque/HashMap tercih et.""");
    }
}
