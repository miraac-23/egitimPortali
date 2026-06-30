// Ornek1: Yerleşik anotasyonlar — @Override, @Deprecated, @SuppressWarnings.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    static class EskiApi {
        // @Deprecated: "kullanma, daha iyisi var". since/forRemoval ile detaylandırılır.
        @Deprecated(since = "2.0", forRemoval = true)
        public int hesaplaEski(int x) { return x * 2; }

        // Yeni, önerilen metot:
        public int hesapla(int x) { return x * 2; }
    }

    static class Temel {
        public String selam() { return "Temel selam"; }
    }
    static class Turetilmis extends Temel {
        // @Override: bu metot üst sınıftakini EZİYOR (derleyici doğrular; imza yanlışsa hata verir).
        @Override
        public String selam() { return "Türetilmiş selam"; }
    }

    public static void main(String[] args) {
        EskiApi api = new EskiApi();
        // Deprecated metodu çağırmak DERLENİR ama derleyici UYARI verir (kullanmaman gerektiğini söyler).
        System.out.println("eski (deprecated): " + api.hesaplaEski(5));
        System.out.println("yeni (önerilen)  : " + api.hesapla(5));

        System.out.println("override: " + new Turetilmis().selam());

        // @SuppressWarnings: belirli derleyici uyarılarını bastırır (bilinçli kullan).
        eskiKullan();

        System.out.println("""

                --- Yerleşik anotasyonlar ---
                @Override: bir metodun üst tipi EZDİĞİNİ belirtir; imza uyuşmazsa derleyici HATA verir (güvenlik ağı).
                @Deprecated(since, forRemoval): "artık kullanma". forRemoval=true -> gelecekte SİLİNECEK.
                  Çağıran kod derlenir ama uyarı alır; IDE üstü çizili gösterir.
                @SuppressWarnings(\"...\"): belirli uyarıları bastırır (deprecation, unchecked...). Dikkatli kullan.
                Anotasyonlar koda META-VERİ ekler; derleyici, araçlar ve framework'ler bunları okur.""");
    }

    @SuppressWarnings("deprecation") // bilinçli olarak deprecated API çağrısının uyarısını bastır
    static void eskiKullan() {
        System.out.println("@SuppressWarnings ile: " + new EskiApi().hesaplaEski(10));
    }
}
