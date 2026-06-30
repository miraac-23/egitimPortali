// Ornek1: StringBuilder — değiştirilebilir metin: append/insert/delete/reverse/replace.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("Merhaba");

        // append: sona ekleme (her tipi kabul eder)
        sb.append(" ").append("Dünya").append('!').append(" ").append(2026);
        System.out.println("append: " + sb);

        // insert: belirli konuma ekleme
        sb.insert(7, ", güzel");
        System.out.println("insert: " + sb);

        // delete / deleteCharAt
        sb.delete(7, 14);           // eklediğimiz ", güzel"i sil
        System.out.println("delete: " + sb);

        // replace
        sb.replace(0, 7, "Selam");
        System.out.println("replace: " + sb);

        // reverse, length, charAt, setCharAt
        StringBuilder ters = new StringBuilder("abcde").reverse();
        System.out.println("reverse: " + ters);

        // Method chaining (akıcı arayüz): her metot sb'yi döndürür
        String sonuc = new StringBuilder()
                .append("[").append("a").append(",").append("b").append("]")
                .toString();
        System.out.println("chaining: " + sonuc);

        System.out.println("""

                --- StringBuilder ---
                String DEĞİŞMEZDİR (immutable): her "+" yeni bir String nesnesi yaratır.
                StringBuilder DEĞİŞTİRİLEBİLİR: aynı tampon üzerinde ekler/siler/değiştirir -> verimli.
                append/insert/delete/replace/reverse/setCharAt; toString() ile String'e çevrilir.
                Metotlar sb'yi döndürdüğü için zincirleme (chaining) yazılır.""");
    }
}
