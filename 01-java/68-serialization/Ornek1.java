// Ornek1: Serialization — nesneyi byte'lara çevirip geri kurma; transient.
// Çalıştırma: java Ornek1.java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Ornek1 {

    // Serileştirilebilir bir sınıf: Serializable işaretleyici (marker) arayüzü.
    static class Kullanici implements Serializable {
        private final String ad;
        private final int yas;
        private transient String parola; // transient -> serileştirilMEZ (gizli/geçici)

        Kullanici(String ad, int yas, String parola) { this.ad = ad; this.yas = yas; this.parola = parola; }
        @Override public String toString() { return "Kullanici{ad=" + ad + ", yas=" + yas + ", parola=" + parola + "}"; }
    }

    public static void main(String[] args) throws Exception {
        Kullanici orijinal = new Kullanici("Ada", 30, "gizli123");
        System.out.println("Orijinal : " + orijinal);

        // SERIALIZE: nesne -> byte[] (dosyaya/ağa yazılabilir)
        byte[] veri;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(orijinal);
            veri = bos.toByteArray();
        }
        System.out.println("Serileşmiş boyut: " + veri.length + " byte");

        // DESERIALIZE: byte[] -> nesne
        Kullanici geri;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(veri))) {
            geri = (Kullanici) ois.readObject();
        }
        System.out.println("Geri yüklenen: " + geri);
        System.out.println("Parola neden null? -> transient alan serileştirilmedi.");

        System.out.println("""

                --- Serialization (serileştirme) ---
                Bir nesneyi byte dizisine çevirme (serialize) ve geri kurma (deserialize) işlemidir.
                Şart: sınıf 'implements Serializable' olmalı (içindeki alanlar da serileştirilebilir olmalı).
                transient: bu alan serileştirilMEZ (parola, önbellek, türetilmiş veri gibi geçici/gizli alanlar).
                Kullanım: nesneyi dosyaya kaydetme, ağ üzerinden gönderme, oturum (session) saklama.""");
    }
}
