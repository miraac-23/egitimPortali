// Ornek2: HashMap güçlü metotları — merge, compute, computeIfAbsent, putIfAbsent.
// Gerçek senaryo: kelime frekansı ve gruplama.
// Çalıştırma: java Ornek2.java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ornek2 {

    public static void main(String[] args) {
        String metin = "elma armut elma kiraz armut elma muz kiraz elma";

        // 1) Kelime frekansı — merge: anahtar yoksa 1 koy, varsa eskiyle topla.
        Map<String, Integer> frekans = new HashMap<>();
        for (String kelime : metin.split(" ")) {
            frekans.merge(kelime, 1, Integer::sum);
        }
        System.out.println("Frekanslar: " + frekans);

        // getOrDefault ile aynı iş (alternatif):
        Map<String, Integer> frekans2 = new HashMap<>();
        for (String k : metin.split(" ")) {
            frekans2.put(k, frekans2.getOrDefault(k, 0) + 1);
        }
        System.out.println("getOrDefault ile: " + frekans2);

        // 2) Gruplama — computeIfAbsent: anahtar yoksa boş liste oluştur, sonra ekle.
        Map<Integer, List<String>> uzunlugaGore = new HashMap<>();
        for (String k : new String[]{"at", "kuş", "fil", "kedi", "kuğu"}) {
            uzunlugaGore.computeIfAbsent(k.length(), uzunluk -> new ArrayList<>()).add(k);
        }
        System.out.println("Uzunluğa göre grup: " + uzunlugaGore);

        // 3) putIfAbsent / compute
        frekans.putIfAbsent("elma", 999);  // 'elma' zaten var -> DEĞİŞMEZ
        frekans.compute("muz", (k, v) -> v == null ? 1 : v * 10); // 'muz' -> 1*10
        System.out.println("putIfAbsent/compute sonrası: elma=" + frekans.get("elma") + ", muz=" + frekans.get("muz"));

        System.out.println("""

                --- HashMap güçlü metotları ---
                merge(k, val, fn)        : yoksa val koy, varsa fn(eski,val) ile birleştir. (sayaç için ideal)
                computeIfAbsent(k, fn)   : yoksa fn ile üret-koy. (Map<K, List<V>> gruplama için ideal)
                compute(k, fn)/computeIfPresent: değeri (eski+anahtar)'a göre yeniden hesapla.
                putIfAbsent(k, val)      : yalnızca anahtar yoksa koy.
                ÖNEMLİ: anahtar nesnelerin equals() ve hashCode()'u doğru tanımlanmalı (aksi halde bulunamaz).""");
    }
}
