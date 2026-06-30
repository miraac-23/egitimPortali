// Ornek2: Interface, çoklu implementasyon ve default metot.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        Amfibi kurbaga = new Amfibi();

        // Bir sınıf birden çok interface'i aynı anda uygulayabilir.
        kurbaga.yuz();
        kurbaga.yuru();
        kurbaga.tanit(); // interface'ten gelen default metot

        System.out.println();
        Surulebilir araba = new Araba();
        araba.yuru();
        araba.tanit();
    }
}

// Interface: yalnızca "ne yapılacağını" söyler, "nasıl"ı söylemez (genelde).
interface Yuzebilir {
    void yuz(); // soyut: gövdesiz
}

interface Surulebilir {
    void yuru();

    // default metot: interface'e gövdeli, hazır bir davranış ekler (Java 8+).
    // Uygulayan sınıflar isterse ezer, istemezse bu hazır sürümü kullanır.
    default void tanit() {
        System.out.println("Bu nesne karada hareket edebilir.");
    }
}

// Amfibi HEM yüzer HEM yürür: iki interface'i birden uygular.
class Amfibi implements Yuzebilir, Surulebilir {
    @Override public void yuz() { System.out.println("Amfibi suda yüzüyor."); }
    @Override public void yuru() { System.out.println("Amfibi karada yürüyor."); }

    @Override
    public void tanit() { // default metodu eziyoruz
        System.out.println("Ben bir amfibiyim: hem suda hem karada.");
    }
}

class Araba implements Surulebilir {
    @Override public void yuru() { System.out.println("Araba yolda ilerliyor."); }
    // tanit() ezilmedi -> interface'teki default sürüm çalışır.
}
