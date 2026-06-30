// Ornek1: Sınıf ve nesne — alanlar ve metotlar.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Sınıf bir "şablon"dur; nesne ise o şablondan üretilen somut bir örnektir.
        // new anahtar kelimesi yeni bir nesne (instance) oluşturur.
        Araba a1 = new Araba();
        a1.marka = "Toyota";
        a1.model = "Corolla";
        a1.hiz = 0;

        Araba a2 = new Araba();
        a2.marka = "Honda";
        a2.model = "Civic";
        a2.hiz = 0;

        // Her nesnenin kendi alanları (durumu) vardır; biri diğerini etkilemez.
        a1.hizlan(40);
        a1.hizlan(20);
        a2.hizlan(100);
        a1.bilgiYaz();
        a2.bilgiYaz();

        a1.frenYap(30);
        a1.bilgiYaz();
    }
}

// Aynı dosyada birden fazla top-level sınıf olabilir; yalnızca biri public olur.
class Araba {
    // Alanlar (fields) = nesnenin durumu.
    String marka;
    String model;
    int hiz;

    // Metotlar (methods) = nesnenin davranışı.
    void hizlan(int miktar) {
        hiz += miktar;
        System.out.println(marka + " " + miktar + " km/s hızlandı.");
    }

    void frenYap(int miktar) {
        hiz = Math.max(0, hiz - miktar);
        System.out.println(marka + " " + miktar + " km/s yavaşladı.");
    }

    void bilgiYaz() {
        System.out.println("-> " + marka + " " + model + " | hız: " + hiz + " km/s");
    }
}
