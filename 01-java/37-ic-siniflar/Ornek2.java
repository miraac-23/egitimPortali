// Ornek2: Static nested (statik iç) sınıf ve yerel (local) sınıf.
// Çalıştırma: java Ornek2.java
public class Ornek2 {

    public static void main(String[] args) {
        // Static nested sınıf: DIŞ nesne GEREKMEZ; doğrudan oluşturulur.
        Dis.StatikIc s = new Dis.StatikIc("statik iç");
        s.yaz();

        // Builder deseni genelde static nested sınıfla kurulur:
        Mesaj m = new Mesaj.Builder().kime("ada@site.com").konu("Selam").build();
        System.out.println(m);

        // Yerel (local) sınıf: bir METODUN içinde tanımlanır, yalnızca orada görünür.
        System.out.println("\nYerel sınıf:");
        yerelSinifOrnegi();
    }

    static void yerelSinifOrnegi() {
        String onek = "[LOG] "; // yerel sınıf, metodun (effectively final) değişkenlerine erişir
        class Loglayici {
            void log(String mesaj) { System.out.println(onek + mesaj); }
        }
        Loglayici l = new Loglayici();
        l.log("yerel sınıf çalıştı");
    }
}

class Dis {
    static class StatikIc {        // static nested: dış nesneye bağlı DEĞİL
        private final String ad;
        StatikIc(String ad) { this.ad = ad; }
        void yaz() { System.out.println("StatikIc: " + ad); }
    }
}

class Mesaj {
    private final String kime, konu;
    private Mesaj(Builder b) { this.kime = b.kime; this.konu = b.konu; }
    @Override public String toString() { return "Mesaj{kime=" + kime + ", konu=" + konu + "}"; }

    // Static nested Builder — dış sınıfın bir örneğine ihtiyaç duymadan çalışır.
    static class Builder {
        private String kime = "", konu = "";
        Builder kime(String k) { this.kime = k; return this; }
        Builder konu(String k) { this.konu = k; return this; }
        Mesaj build() { return new Mesaj(this); }
    }
}
