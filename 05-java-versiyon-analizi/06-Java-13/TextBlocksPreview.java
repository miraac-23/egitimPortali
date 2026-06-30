/*
 * ============================================================================
 *  TextBlocksPreview.java
 * ============================================================================
 *  Java 13 - TEXT BLOCKS (Önizleme / Preview Özelliği)
 *
 *  ÖNEMLİ NOT (Sürüm Bilgisi):
 *  ---------------------------------------------------------------------------
 *  - Text Blocks, Java 13'te (Eylül 2019) PREVIEW (önizleme) olarak geldi.
 *    JEP 355 ile tanıtıldı.
 *  - Java 14'te ikinci bir preview turu yaşadı (JEP 368). Bu turda \ (satır
 *    sonu birleştirme) ve \s (boşluk koruma) kaçış dizileri eklendi.
 *  - Java 15'te KALICI (kararlı/standard) hale geldi (JEP 378).
 *
 *  DERLEME TALİMATI:
 *  ---------------------------------------------------------------------------
 *  * Java 13 veya 14 ile derliyorsanız, önizleme özelliği açık olmalıdır:
 *        javac --enable-preview --release 13 TextBlocksPreview.java
 *        java  --enable-preview TextBlocksPreview
 *
 *    (Java 14 için --release 14 kullanın.)
 *
 *  * Java 15 ve sonrası ile derliyorsanız, Text Blocks artık KALICI olduğu
 *    için herhangi bir özel bayrak GEREKMEZ:
 *        javac TextBlocksPreview.java
 *        java  TextBlocksPreview
 *
 *  Bu dosya, \ ve \s kaçış dizilerini de kullandığı için en sorunsuz şekilde
 *  Java 15+ ile (preview bayrağı olmadan) derlenir. Java 13'te derlemek
 *  isterseniz \ ve \s kullanan satırları Java 13'ün desteklemediğini unutmayın
 *  (bunlar 14'te geldi); o satırlar yorumda ayrıca işaretlenmiştir.
 * ============================================================================
 */

public class TextBlocksPreview {

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("   JAVA 13 - TEXT BLOCKS (Önizleme) ÖRNEKLERİ");
        System.out.println("=================================================\n");

        ornek1_JsonYazimi();
        ornek2_SqlSorgusu();
        ornek3_HtmlYazimi();
        ornek4_GirintiYonetimi();
        ornek5_KacisDizileri();
        ornek6_GercekHayatRaporu();
    }

    /* ========================================================================
     * ÖRNEK 1: JSON YAZIMI
     * ------------------------------------------------------------------------
     * PROBLEM: Java'da çok satırlı JSON metnini elle yazmak işkenceydi.
     * Her satır için tırnak açıp kapatmak, satır sonuna \n koymak, satırları
     * + ile birleştirmek ve JSON içindeki çift tırnakları \" diye kaçırmak
     * gerekiyordu. Buna "kaçış karakteri cehennemi" (escape hell) denir.
     * ====================================================================== */
    static void ornek1_JsonYazimi() {
        System.out.println("--- ÖRNEK 1: JSON YAZIMI ---\n");

        /*
         * ESKİ YÖNTEM (Java 12 ve öncesi):
         * ---------------------------------
         * Dikkat edin:
         *   - Her satır ayrı bir String literal ("...").
         *   - JSON içindeki her " işareti \" olarak kaçırılmış.
         *   - Satır sonlarına \n elle eklenmiş.
         *   - Satırlar + operatörü ile birleştirilmiş.
         * Bu, okunması ve bakımı çok zor, hataya açık bir koddur.
         */
        String eskiJson =
                "{\n" +
                "    \"ad\": \"Ahmet\",\n" +
                "    \"soyad\": \"Yılmaz\",\n" +
                "    \"yas\": 30,\n" +
                "    \"adres\": {\n" +
                "        \"sehir\": \"İstanbul\",\n" +
                "        \"ulke\": \"Türkiye\"\n" +
                "    },\n" +
                "    \"aktif\": true\n" +
                "}";

        System.out.println(">> ESKİ YÖNTEM (kaçış cehennemi):");
        System.out.println(eskiJson);
        System.out.println();

        /*
         * YENİ YÖNTEM (Java 13+ Text Block):
         * -----------------------------------
         * Üç çift tırnak (""") ile başlar ve biter.
         * Açılış """ işaretinden SONRA mutlaka satır başı (yeni satır) gelmeli.
         * İçeride:
         *   - Çift tırnakları kaçırmaya GEREK YOK -> sade " yazabiliriz.
         *   - Satır sonlarına \n koymaya GEREK YOK -> doğal satır sonları korunur.
         *   - + ile birleştirmeye GEREK YOK.
         * Sonuç: JSON metni tam olarak "göründüğü gibi" yazılır.
         */
        String yeniJson = """
                {
                    "ad": "Ahmet",
                    "soyad": "Yılmaz",
                    "yas": 30,
                    "adres": {
                        "sehir": "İstanbul",
                        "ulke": "Türkiye"
                    },
                    "aktif": true
                }""";

        System.out.println(">> YENİ YÖNTEM (Text Block - temiz):");
        System.out.println(yeniJson);
        System.out.println();
    }

    /* ========================================================================
     * ÖRNEK 2: SQL SORGUSU YAZIMI
     * ------------------------------------------------------------------------
     * Gerçek hayatta uygulama kodu içinde uzun SQL sorguları yazmak çok
     * yaygındır. Eski yöntemde her satırın string birleştirmesi yapılır,
     * boşluk koymayı unutmak yüzünden kelimeler birbirine yapışır
     * (örn. "FROMmusteriler" gibi) ve sorgu bozulur.
     * ====================================================================== */
    static void ornek2_SqlSorgusu() {
        System.out.println("--- ÖRNEK 2: SQL SORGUSU YAZIMI ---\n");

        /*
         * ESKİ YÖNTEM (Java 12 ve öncesi):
         * Klasik tuzak: satır sonlarındaki boşluklar. "siparisler" ile "WHERE"
         * arasında boşluk bırakmak için satır sonuna " " eklemek gerekir.
         * Unutursanız "siparislerWHERE" olur ve sorgu çalışmaz.
         */
        String eskiSql =
                "SELECT m.ad, m.soyad, s.tutar, s.tarih " +
                "FROM musteriler m " +
                "INNER JOIN siparisler s ON m.id = s.musteri_id " +
                "WHERE s.tutar > 1000 " +
                "  AND s.tarih >= '2024-01-01' " +
                "ORDER BY s.tutar DESC";

        System.out.println(">> ESKİ YÖNTEM (boşluk tuzaklı string birleştirme):");
        System.out.println(eskiSql);
        System.out.println();

        /*
         * YENİ YÖNTEM (Java 13+ Text Block):
         * Sorgu tıpkı bir SQL editöründeymiş gibi okunabilir biçimde yazılır.
         * Boşluk/satır sonu derdi yok. Bakımı çok kolaydır; yeni bir koşul
         * eklemek için sadece yeni bir satır yazarsınız.
         */
        String yeniSql = """
                SELECT m.ad, m.soyad, s.tutar, s.tarih
                FROM musteriler m
                INNER JOIN siparisler s ON m.id = s.musteri_id
                WHERE s.tutar > 1000
                  AND s.tarih >= '2024-01-01'
                ORDER BY s.tutar DESC
                """;

        System.out.println(">> YENİ YÖNTEM (Text Block - okunaklı SQL):");
        System.out.println(yeniSql);
        System.out.println();
    }

    /* ========================================================================
     * ÖRNEK 3: HTML YAZIMI
     * ------------------------------------------------------------------------
     * HTML içinde hem çift tırnak (attribute'lar: class="...") hem de iç içe
     * yapı çoktur. Eski yöntemde bu tam bir kabustu.
     * ====================================================================== */
    static void ornek3_HtmlYazimi() {
        System.out.println("--- ÖRNEK 3: HTML YAZIMI ---\n");

        // ESKİ YÖNTEM: tüm tırnaklar \" ile kaçırılmış, çok zor okunur.
        String eskiHtml =
                "<html>\n" +
                "    <body>\n" +
                "        <h1 class=\"baslik\">Merhaba Dünya</h1>\n" +
                "        <p id=\"aciklama\">Bu bir <b>Text Block</b> örneğidir.</p>\n" +
                "    </body>\n" +
                "</html>";

        System.out.println(">> ESKİ YÖNTEM:");
        System.out.println(eskiHtml);
        System.out.println();

        // YENİ YÖNTEM: tırnaklar olduğu gibi, yapı tam HTML gibi görünüyor.
        String yeniHtml = """
                <html>
                    <body>
                        <h1 class="baslik">Merhaba Dünya</h1>
                        <p id="aciklama">Bu bir <b>Text Block</b> örneğidir.</p>
                    </body>
                </html>""";

        System.out.println(">> YENİ YÖNTEM (Text Block):");
        System.out.println(yeniHtml);
        System.out.println();
    }

    /* ========================================================================
     * ÖRNEK 4: GİRİNTİ YÖNETİMİ (INCIDENTAL WHITESPACE)
     * ------------------------------------------------------------------------
     * Text Block'larda kodun girintisi (sol taraftaki boşluklar) ile metnin
     * gerçek girintisini ayırmak gerekir. Derleyici, "tesadüfi boşluk"
     * (incidental whitespace) dediğimiz, sadece kodu hizalamak için var olan
     * boşlukları otomatik olarak siler.
     *
     * KURAL: Tüm satırlar (kapanış """ dahil) içinde EN SOLDA başlayan satır
     * referans alınır. O satırın solundaki boşluk miktarı kadar boşluk tüm
     * satırlardan kırpılır. Kapanış """ konumu bu referansı belirlemede
     * çok etkilidir.
     * ====================================================================== */
    static void ornek4_GirintiYonetimi() {
        System.out.println("--- ÖRNEK 4: GİRİNTİ YÖNETİMİ ---\n");

        /*
         * Burada kapanış """ işareti, metnin başlamasını istediğimiz sütunda.
         * Bu yüzden satırların başındaki "kod hizalama" boşlukları silinir,
         * metin sola dayalı çıkar.
         */
        String solaDayali = """
                Satir 1
                Satir 2
                Satir 3
                """;
        System.out.println(">> Sola dayalı (incidental whitespace silindi):");
        System.out.println(solaDayali);

        /*
         * Şimdi bilerek girinti EKLİYORUZ. Kapanış """ işaretini daha sola
         * çekersek, ona göre satırların başındaki fazladan boşluklar KORUNUR.
         * Aşağıda "    Girintili" satırı, kapanış """ ye göre 4 boşluk içeride.
         */
        String girintili = """
                    Girintili satir
                Normal satir
                """;
        System.out.println(">> Bilinçli girinti korundu:");
        System.out.println(girintili);
    }

    /* ========================================================================
     * ÖRNEK 5: KAÇIŞ DİZİLERİ (\ satır devamı ve \s boşluk koruma)
     * ------------------------------------------------------------------------
     * DİKKAT: \ (satır birleştirme) ve \s (boşluk koruma) kaçış dizileri
     * Java 14'te (ikinci preview) eklendi. Java 13'te BU İKİSİ YOKTUR.
     * Bu yüzden bu örnek en güvenli şekilde Java 14+ ile derlenir.
     * ====================================================================== */
    static void ornek5_KacisDizileri() {
        System.out.println("--- ÖRNEK 5: \\ ve \\s KAÇIŞ DİZİLERİ (Java 14+) ---\n");

        /*
         * \ (ters bölü + satır sonu): O noktadaki YENİ SATIR karakterini
         * iptal eder. Yani kaynak kodda satırı bölersiniz ama çıktıda tek
         * satır olur. Çok uzun tek satırlık metinleri okunaklı yazmak için
         * idealdir.
         */
        String tekSatir = """
                Bu cumle kaynak kodda \
                bolundu ama ciktida \
                tek satir olarak gorunur.""";
        System.out.println(">> \\ ile satır devamı (çıktıda tek satır):");
        System.out.println(tekSatir);
        System.out.println();

        /*
         * \s: Tam olarak bir boşluk (space) karakterini temsil eder ve
         * kendisinden önceki sondaki boşlukların (trailing whitespace)
         * kırpılmasını ENGELLER. Sondaki boşlukların korunması gerektiğinde
         * (örn. sabit genişlikli sütunlar) kullanılır.
         */
        String bosluklar = """
                Ad     \s
                Soyad  \s
                """;
        System.out.println(">> \\s ile sondaki boşluklar korundu (| ile gösterim):");
        for (String satir : bosluklar.split("\n")) {
            System.out.println("|" + satir + "|");
        }
        System.out.println();
    }

    /* ========================================================================
     * ÖRNEK 6: GERÇEK HAYAT - DİNAMİK RAPOR / E-POSTA ŞABLONU
     * ------------------------------------------------------------------------
     * Text Block + String.formatted() (Java 15'te eklendi, formatlı metin için)
     * birlikte kullanılarak şablonlu çıktı üretmek çok temiz olur.
     * Burada formatted yerine taşınabilirlik için klasik String.format
     * kullanıyoruz; ikisi de aynı işi yapar.
     * ====================================================================== */
    static void ornek6_GercekHayatRaporu() {
        System.out.println("--- ÖRNEK 6: GERÇEK HAYAT - E-POSTA ŞABLONU ---\n");

        String musteriAdi = "Zeynep Kaya";
        String siparisNo = "SP-2024-00917";
        double tutar = 1499.90;

        String sablon = """
                Sayın %s,

                %s numaralı siparişiniz başarıyla alınmıştır.
                Toplam tutar: %.2f TL

                Bizi tercih ettiğiniz için teşekkür ederiz.
                İyi günler dileriz.

                ABC Mağazacılık A.Ş.
                """;

        String email = String.format(sablon, musteriAdi, siparisNo, tutar);

        System.out.println(">> Üretilen e-posta:");
        System.out.println(email);
    }
}
