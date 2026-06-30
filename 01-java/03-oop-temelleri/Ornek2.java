// Ornek2: Kapsülleme (encapsulation), getter/setter, constructor ve this.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // Constructor ile nesne oluşturulurken başlangıç değerleri verilir.
        BankaHesabi hesap = new BankaHesabi("Selin", 100.0);
        System.out.println(hesap.getSahip() + " hesabı açıldı. Bakiye: " + hesap.getBakiye());

        hesap.paraYatir(250.0);
        hesap.paraCek(80.0);
        System.out.println("Güncel bakiye: " + hesap.getBakiye());

        // Kapsülleme sayesinde geçersiz işlemler engellenir:
        hesap.paraCek(10_000.0);   // yetersiz bakiye
        hesap.paraYatir(-5.0);     // geçersiz tutar

        // Bakiyeye doğrudan erişemeyiz (private); sadece metotlar üzerinden değişir.
        // hesap.bakiye = 1_000_000;  // <-- derlenmez: bakiye private
    }
}

class BankaHesabi {
    // private alanlar dışarıdan doğrudan erişilemez = kapsülleme.
    private String sahip;
    private double bakiye;

    // Constructor: nesne kurulurken çalışır. this, "şu anki nesne" demektir;
    // parametre ile alan aynı isimde olduğunda ayrımı sağlar.
    public BankaHesabi(String sahip, double baslangic) {
        this.sahip = sahip;
        this.bakiye = Math.max(0, baslangic);
    }

    // Getter: değeri okumak için.
    public String getSahip() {
        return sahip;
    }

    public double getBakiye() {
        return bakiye;
    }

    // Davranışı metotlarla kontrol ediyoruz; böylece kurallar tek yerde toplanır.
    public void paraYatir(double tutar) {
        if (tutar <= 0) {
            System.out.println("Hata: yatırılan tutar pozitif olmalı.");
            return;
        }
        bakiye += tutar;
        System.out.println(tutar + " TL yatırıldı.");
    }

    public void paraCek(double tutar) {
        if (tutar <= 0) {
            System.out.println("Hata: çekilen tutar pozitif olmalı.");
            return;
        }
        if (tutar > bakiye) {
            System.out.println("Hata: yetersiz bakiye (istenen " + tutar + ", mevcut " + bakiye + ").");
            return;
        }
        bakiye -= tutar;
        System.out.println(tutar + " TL çekildi.");
    }
}
