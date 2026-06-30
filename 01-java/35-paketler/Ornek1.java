// Ornek1: import, tam nitelikli ad (FQN) ve isim çakışması çözümü.
// Paketler, sınıfları gruplar ve isim çakışmalarını önler.
// Çalıştırma: java Ornek1.java
import java.util.ArrayList;   // java.util paketinden ArrayList'i içe aktar
import java.util.List;
import java.util.Date;        // java.util.Date'i içe aktardık

public class Ornek1 {

    public static void main(String[] args) {
        // import sayesinde kısa adla kullanırız:
        List<String> liste = new ArrayList<>();
        liste.add("paket");
        liste.add("import");
        System.out.println("java.util.List: " + liste);

        // import edilmiş java.util.Date kısa adla:
        Date utilTarih = new Date(0L);
        System.out.println("java.util.Date: " + utilTarih.toInstant());

        // İSİM ÇAKIŞMASI: java.sql.Date de var. İkisini aynı anda kısa adla kullanamayız;
        // birini TAM NİTELİKLİ AD (fully-qualified name) ile yazarız.
        java.sql.Date sqlTarih = new java.sql.Date(0L);
        System.out.println("java.sql.Date : " + sqlTarih);

        // Hiç import etmeden de tam adla kullanılabilir (uzun ama net):
        java.math.BigDecimal para = new java.math.BigDecimal("19.99");
        System.out.println("java.math.BigDecimal: " + para);

        System.out.println("""

                --- Paketler ve import ---
                Paket = sınıfları gruplayan bir isim alanı (namespace), örn. java.util, com.egitim.app.
                import: bir sınıfı kısa adıyla kullanmayı sağlar (java.util.List -> List).
                Tam nitelikli ad (FQN): paket + sınıf (java.sql.Date). İsim çakışmasını çözer.
                java.lang paketi (String, System, Math...) otomatik içe aktarılır; import gerekmez.""");
    }
}
