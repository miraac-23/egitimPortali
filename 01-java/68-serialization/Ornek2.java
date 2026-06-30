// Ornek2: serialVersionUID, iç içe nesneler/koleksiyonlar ve sürüm uyumu.
// Çalıştırma: java Ornek2.java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class Ornek2 {

    static class Adres implements Serializable {
        private static final long serialVersionUID = 1L;
        final String sehir;
        Adres(String sehir) { this.sehir = sehir; }
        @Override public String toString() { return sehir; }
    }

    static class Siparis implements Serializable {
        // serialVersionUID: sınıfın "sürüm kimliği". Serileşmiş veriyle sınıfın uyumunu denetler.
        private static final long serialVersionUID = 1L;
        final String no;
        final List<String> urunler;   // koleksiyonlar da serileştirilebilir (içerikleri uygunsa)
        final Adres teslimat;          // iç içe nesne de serileştirilir (o da Serializable olmalı)
        Siparis(String no, List<String> urunler, Adres teslimat) {
            this.no = no; this.urunler = urunler; this.teslimat = teslimat;
        }
        @Override public String toString() { return "Sipariş " + no + " " + urunler + " -> " + teslimat; }
    }

    public static void main(String[] args) throws Exception {
        Siparis s = new Siparis("SP-100", List.of("Klavye", "Mouse"), new Adres("İzmir"));
        System.out.println("Orijinal: " + s);

        byte[] veri = serialize(s);
        Siparis geri = (Siparis) deserialize(veri);
        System.out.println("Geri yüklenen: " + geri);
        System.out.println("İç içe nesne ve liste korundu mu? "
                + (geri.teslimat.sehir.equals("İzmir") && geri.urunler.size() == 2));

        System.out.println("""

                --- serialVersionUID ve sürüm uyumu ---
                serialVersionUID: sınıfın sürüm kimliği. Serileşmiş veri okunurken sınıfın UID'si ile
                  karşılaştırılır; UYMAZSA InvalidClassException atılır. ELLE tanımla (yoksa derleyici
                  otomatik üretir ve en küçük değişiklikte değişir -> eski veriler okunamaz hale gelir).
                İç içe nesneler ve koleksiyonlar otomatik serileştirilir (hepsi Serializable olmalı).
                MODERN ALTERNATİF: Java serileştirme güvenlik açıklarına ve sürüm kırılganlığına açıktır;
                  servisler/depolama arası genelde JSON (Jackson) veya Protobuf tercih edilir.""");
    }

    static byte[] serialize(Object o) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o); return bos.toByteArray();
        }
    }
    static Object deserialize(byte[] veri) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(veri))) {
            return ois.readObject();
        }
    }
}
