// Ornek1: Anonim sınıf — bir arayüzü/abstract sınıfı isim vermeden, ANINDA uygulamak.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ornek1 {

    interface Selamlayici { String selamla(String ad); }

    abstract static class Hayvan {
        abstract String ses();
        void tanit() { System.out.println("Bu hayvan: " + ses()); }
    }

    public static void main(String[] args) {
        // 1) Bir arayüzü anonim sınıfla uygula (tek kullanımlık).
        Selamlayici resmi = new Selamlayici() {
            @Override public String selamla(String ad) { return "Sayın " + ad + ", hoş geldiniz."; }
        };
        System.out.println(resmi.selamla("Ada"));

        // 2) Bir abstract sınıfı anonim sınıfla uzat (lambda BUNU yapamaz; sınıf gerekir).
        Hayvan kopek = new Hayvan() {
            @Override String ses() { return "Hav hav"; }
        };
        kopek.tanit();

        // 3) Comparator'ı anonim sınıfla (klasik kullanım).
        List<String> sehirler = new ArrayList<>(List.of("İzmir", "Ankara", "Adana"));
        sehirler.sort(new Comparator<String>() {
            @Override public int compare(String a, String b) { return a.compareTo(b); }
        });
        System.out.println("Sıralı: " + sehirler);

        System.out.println("""

                --- Anonim sınıf ---
                İsmi olmayan, BİR KEZ kullanılacak bir sınıfı tanımladığın yerde oluşturursun.
                'new Tip() { ... }' söz dizimi: arayüzü uygular VEYA sınıfı uzatır + nesneyi yaratır.
                Tek soyut metotlu arayüzlerde genelde LAMBDA daha kısadır (sonraki örnek);
                ama abstract SINIF uzatmak veya birden çok metot gerektiğinde anonim sınıf şarttır.""");
    }
}
