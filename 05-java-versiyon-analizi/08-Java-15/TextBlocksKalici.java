// =====================================================================================
//  TextBlocksKalici.java
//  Java 15 - Text Blocks (Metin Blokları) ARTIK KALICI (STANDART) ÖZELLİK
// =====================================================================================
//
//  ÖNEMLİ SÜRÜM NOTU:
//  ------------------
//  - Text Blocks ilk olarak Java 13'te PREVIEW (önizleme) olarak geldi (JEP 355).
//  - Java 14'te yine PREVIEW olarak devam etti (JEP 368, küçük iyileştirmelerle).
//  - Java 15'te (Eylül 2020, JEP 378) artık KALICI / STANDART hale geldi!
//
//  Bunun pratik anlamı:
//  - Java 13 ve 14'te bu özelliği kullanmak için derlerken ve çalıştırırken
//    "--enable-preview" bayrağı GEREKİYORDU.
//  - Java 15 ve sonrasında HİÇBİR ÖZEL BAYRAK GEREKMİYOR. Tıpkı normal bir
//    String gibi doğrudan kullanılabilir.
//
//  DERLEME (Java 15+):
//      javac TextBlocksKalici.java
//      java  TextBlocksKalici
//  (Dikkat: --enable-preview YOK. Çünkü artık kalıcı bir dil özelliği.)
//
// =====================================================================================

public class TextBlocksKalici {

    public static void main(String[] args) {

        System.out.println("===== JAVA 15 TEXT BLOCKS (KALICI) DEMOSU =====\n");

        // -----------------------------------------------------------------------------
        // 1) TEXT BLOCK NEDİR?
        // -----------------------------------------------------------------------------
        // Text Block, üç adet çift tırnakla (""") başlayıp biten, ÇOK SATIRLI bir
        // string yazma biçimidir. Açılış """ işaretinden sonra MUTLAKA yeni satıra
        // geçilmelidir. İçerikteki satır sonları (\n) otomatik olarak eklenir.
        //
        // ESKİDEN (klasik String) çok satırlı metin yazmak "kaçış karakteri cehennemi"
        // demekti: her satır için \n, her tırnak için \" yazmak gerekiyordu.

        ornek1_BasitKarsilastirma();
        ornek2_JsonOrnegi();
        ornek3_SqlOrnegi();
        ornek4_OzelDavranislar();
        ornek5_GercekHayat();
    }

    // =====================================================================================
    //  ÖRNEK 1: ESKİ vs YENİ - BASİT KARŞILAŞTIRMA
    // =====================================================================================
    static void ornek1_BasitKarsilastirma() {
        System.out.println("----- ÖRNEK 1: ESKİ vs YENİ -----");

        // ---- ESKİ YÖNTEM (Java 12 ve öncesi) - "kaçış karakteri cehennemi" ----
        // Her satır sonunda \n, satırları birleştirmek için + operatörü.
        String eskiYontem = "Merhaba Dünya!\n" +
                            "Bu metin\n" +
                            "birden fazla\n" +
                            "satırdan oluşuyor.\n";

        // ---- YENİ YÖNTEM (Java 15 - kalıcı Text Block) ----
        // Açılış """ işaretinden sonra yeni satıra geçildi. \n YOK, + YOK.
        // Çok daha okunabilir ve metnin gerçek hali nasılsa kodda da öyle görünür.
        String yeniYontem = """
                Merhaba Dünya!
                Bu metin
                birden fazla
                satırdan oluşuyor.
                """;

        System.out.println(">> Eski yöntem çıktısı:");
        System.out.print(eskiYontem);
        System.out.println(">> Yeni yöntem çıktısı:");
        System.out.print(yeniYontem);

        // İki yöntem aynı metni üretir mi? Evet!
        System.out.println(">> İki metin eşit mi? " + eskiYontem.equals(yeniYontem));
        System.out.println();
    }

    // =====================================================================================
    //  ÖRNEK 2: JSON YAZIMI - Text Block'un parladığı yer
    // =====================================================================================
    static void ornek2_JsonOrnegi() {
        System.out.println("----- ÖRNEK 2: JSON ÖRNEĞİ -----");

        // ---- ESKİ YÖNTEM: JSON içindeki her çift tırnak için \" yazmak zorundayız ----
        String eskiJson = "{\n" +
                          "  \"ad\": \"Ahmet\",\n" +
                          "  \"soyad\": \"Yılmaz\",\n" +
                          "  \"yas\": 30,\n" +
                          "  \"adres\": {\n" +
                          "    \"sehir\": \"İstanbul\",\n" +
                          "    \"ulke\": \"Türkiye\"\n" +
                          "  }\n" +
                          "}";

        // ---- YENİ YÖNTEM: Çift tırnaklar olduğu gibi yazılır, kaçış gerekmez! ----
        // JSON, kopyala-yapıştır ile doğrudan kullanılabilir hale gelir.
        String yeniJson = """
                {
                  "ad": "Ahmet",
                  "soyad": "Yılmaz",
                  "yas": 30,
                  "adres": {
                    "sehir": "İstanbul",
                    "ulke": "Türkiye"
                  }
                }""";

        System.out.println(">> Yeni Text Block ile JSON:");
        System.out.println(yeniJson);
        System.out.println();
        System.out.println("(Eski yöntemde her \" karakterini \\\" olarak kaçırmak gerekirdi.)");
        System.out.println();
    }

    // =====================================================================================
    //  ÖRNEK 3: SQL YAZIMI - Çok satırlı sorgular okunaklı olur
    // =====================================================================================
    static void ornek3_SqlOrnegi() {
        System.out.println("----- ÖRNEK 3: SQL ÖRNEĞİ -----");

        // ---- ESKİ YÖNTEM: + ile satır birleştirme, boşluklara dikkat etmek gerek ----
        String eskiSql = "SELECT m.ad, m.soyad, d.ad AS departman " +
                        "FROM musteriler m " +
                        "INNER JOIN departmanlar d ON m.departman_id = d.id " +
                        "WHERE m.aktif = true " +
                        "ORDER BY m.soyad ASC";

        // ---- YENİ YÖNTEM: Sorgu, bir SQL editöründeki gibi hizalı ve okunaklı ----
        String yeniSql = """
                SELECT m.ad, m.soyad, d.ad AS departman
                FROM musteriler m
                INNER JOIN departmanlar d ON m.departman_id = d.id
                WHERE m.aktif = true
                ORDER BY m.soyad ASC""";

        System.out.println(">> Yeni Text Block ile SQL sorgusu:");
        System.out.println(yeniSql);
        System.out.println();
    }

    // =====================================================================================
    //  ÖRNEK 4: TEXT BLOCK ÖZEL DAVRANIŞLARI (incelik detayları)
    // =====================================================================================
    static void ornek4_OzelDavranislar() {
        System.out.println("----- ÖRNEK 4: ÖZEL DAVRANIŞLAR -----");

        // (a) "Incidental white space" (Tesadüfi boşluk) silinmesi:
        // Java, en soldaki ortak girintiyi (en az girintili satıra göre) otomatik siler.
        // Kapanış """ işaretinin konumu da girinti referansını etkiler.
        String girintiOrnegi = """
                    Satır 1
                    Satır 2
                """;
        System.out.println(">> Girinti otomatik temizlenir:");
        System.out.print(girintiOrnegi);

        // (b) Satır sonu birleştirme: Satır sonunda \ kullanılırsa o satır,
        // bir SONRAKİ satırla BİRLEŞTİRİLİR (yeni satır eklenmez).
        // Çok uzun tek satırlık metinleri kodda bölmek için harikadır.
        String tekSatir = """
                Bu çok uzun bir cümle \
                ama tek satır olarak \
                birleştirilecek.""";
        System.out.println(">> Satır sonu '\\' ile birleştirme:");
        System.out.println(tekSatir);

        // (c) Sonda boşluk korumak için \s kaçış dizisi (bir boşluk anlamına gelir):
        String bosluk = """
                Sonunda boşluk var:\s
                İkinci satır""";
        System.out.println(">> \\s ile sondaki boşluk korunur:");
        System.out.println("[" + bosluk + "]");

        // (d) Text Block normal bir String'dir; tüm String metotları çalışır.
        String upper = """
                küçük harf metin
                """.toUpperCase();
        System.out.println(">> Text Block üzerinde .toUpperCase(): " + upper);
        System.out.println();
    }

    // =====================================================================================
    //  ÖRNEK 5: GERÇEK HAYAT ÖRNEĞİ - HTML E-Posta Şablonu Üretimi
    // =====================================================================================
    static void ornek5_GercekHayat() {
        System.out.println("----- ÖRNEK 5: GERÇEK HAYAT - HTML E-POSTA ŞABLONU -----");

        // Senaryo: Bir e-ticaret sistemi, sipariş onayı için HTML e-posta gönderiyor.
        // Eskiden bu tür HTML şablonlarını Java string olarak yazmak korkunçtu
        // (her tırnak kaçırılır, her satır +'lı, her \n elle eklenirdi).
        // Text Block ile HTML, bir .html dosyasındaki gibi doğal görünür.

        String musteriAdi = "Zeynep Kaya";
        String siparisNo   = "SP-2026-00891";
        double tutar       = 1499.90;

        // Not: %s ve %.2f yer tutucularını String.formatted (Java 15'te String'e eklendi)
        // ile dolduruyoruz. formatted(), String.format ile aynı işi yapar ama zincirlenebilir.
        String htmlEposta = """
                <html>
                  <body style="font-family: Arial, sans-serif;">
                    <h2>Sipariniz Alındı!</h2>
                    <p>Sayın <b>%s</b>,</p>
                    <p>Sipariş numaranız: <code>%s</code></p>
                    <p>Toplam tutar: <b>%.2f TL</b></p>
                    <hr/>
                    <p style="color: gray;">Bizi tercih ettiğiniz için teşekkürler.</p>
                  </body>
                </html>
                """.formatted(musteriAdi, siparisNo, tutar);

        System.out.println(">> Üretilen HTML e-posta:");
        System.out.println(htmlEposta);

        System.out.println("AÇIKLAMA: Aynı HTML'i eski yöntemle yazsaydık, onlarca \\\" ve");
        System.out.println("\\n ile dolu, okunması imkansız bir string elde ederdik.");
        System.out.println("\n===== DEMO SONU =====");
    }
}
