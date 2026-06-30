// Ornek1: try-catch-finally ve yaygın exception türleri.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // --- try-catch: hatayı yakala, program çökmesin ---
        try {
            int sonuc = 10 / 0; // ArithmeticException fırlatır
            System.out.println(sonuc);
        } catch (ArithmeticException e) {
            System.out.println("Yakalandı (aritmetik): " + e.getMessage());
        }

        // --- NullPointerException ---
        try {
            String s = null;
            s.length(); // null üzerinde metot çağrısı
        } catch (NullPointerException e) {
            System.out.println("Yakalandı (null): null bir nesnenin metodu çağrıldı.");
        }

        // --- ArrayIndexOutOfBoundsException ---
        try {
            int[] dizi = {1, 2, 3};
            System.out.println(dizi[5]); // geçersiz indeks
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Yakalandı (indeks): dizi sınırı aşıldı -> " + e.getMessage());
        }

        // --- finally: hata olsa da olmasa da HER ZAMAN çalışır ---
        // Kaynak kapatma / temizlik için idealdir.
        try {
            System.out.println("\ntry bloğu çalışıyor...");
            throw new RuntimeException("örnek hata");
        } catch (RuntimeException e) {
            System.out.println("catch: " + e.getMessage());
        } finally {
            System.out.println("finally: temizlik yapıldı (her durumda çalışır).");
        }

        System.out.println("\nProgram çökmeden devam etti.");
    }
}
