# JDBC ile Veritabanı Bağlantısı

Gerçek uygulamalar verilerini kalıcı olarak saklamak ister; işte burada veritabanları devreye
girer. **JDBC (Java Database Connectivity)**, Java'nın ilişkisel veritabanlarıyla konuşmak için
sunduğu standart API'dir. Spring Data JPA gibi yüksek seviyeli araçlar bile altta JDBC kullanır;
bu yüzden temelini anlamak, üst katmanların "ne yaptığını gerçekten anlamak" demektir. Bu
bölümde temelden başlayıp DAO deseni, otomatik anahtarlar, transaction ve ilişkili tablolara
kadar gerçek dünyada kullandığın her şeyi göreceğiz.

> Örnekler **H2 in-memory** veritabanını kullanır; kurulum gerekmez, her çalıştırma temiz bir
> veritabanıyla başlar. Portaldaki "Çalıştır" özelliği H2 sürücüsünü otomatik classpath'e ekler;
> kendi bilgisayarında `java -cp h2-*.jar Ornek1.java` ile çalıştırabilirsin.

## JDBC mimarisi

JDBC birkaç temel arayüz etrafında döner:

- **`DriverManager`** — JDBC URL'ine uygun sürücüyü bulup bağlantı açar.
- **`Connection`** — veritabanıyla açık oturum; işin bitince kapatılmalıdır.
- **`Statement` / `PreparedStatement`** — SQL komutlarını çalıştırır.
- **`ResultSet`** — `SELECT` sonucunu satır satır gezdiğin imleç.

JDBC URL hedefi belirtir: `jdbc:h2:mem:demo`, `jdbc:postgresql://localhost/mydb`,
`jdbc:mysql://...`. Çağrı türleri: `execute` (DDL), `executeUpdate` (INSERT/UPDATE/DELETE,
etkilenen satır sayısını döndürür), `executeQuery` (SELECT, `ResultSet` döndürür).

Örnek 1 (`./Ornek1.java`) gerçekçi bir müşteri tablosu üzerinde bağlanmayı, tablo oluşturmayı,
parametreli ekleme, filtreli sorgu ve toplama (`COUNT`/`AVG`) sorgusunu gösterir. Tüm kaynaklar
`try-with-resources` ile otomatik kapatılır — veritabanı kodunda en sık hata kaynak sızıntısıdır.

## PreparedStatement: güvenli ve parametreli

Kullanıcı verisini doğrudan SQL metnine yapıştırmak (`"... WHERE ad = '" + ad + "'"`) hem
hatalı hem de **SQL injection** açığına davetiyedir. Doğru yol her zaman `PreparedStatement`'tır:
değerleri `?` yer tutucularıyla, tipli biçimde yerleştirir, böylece hem güvenli hem de verimli
olur:

```java
PreparedStatement ps = con.prepareStatement("INSERT INTO musteri (ad, sehir) VALUES (?, ?)");
ps.setString(1, ad);
ps.setString(2, sehir);
ps.executeUpdate();
```

## DAO deseni: veri erişimini düzenlemek

Gerçek projelerde SQL'i kodun her yerine serpiştirmeyiz; her varlık için veri erişimini tek bir
sınıfta toplarız: **DAO (Data Access Object)** veya repository. Bu, iş mantığını SQL
ayrıntısından ayırır ve test edilebilirliği artırır.

Örnek 2 (`./Ornek2.java`) bir `UrunDAO` yazar: `ekle`, `bul`, `hepsi`, `fiyatGuncelle`, `sil` —
yani tam bir **CRUD**. İki önemli teknik içerir:

- **Generated keys:** Veritabanının ürettiği otomatik `id`'yi geri almak:
  ```java
  PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
  ps.executeUpdate();
  ResultSet keys = ps.getGeneratedKeys();
  int id = keys.next() ? keys.getInt(1) : -1;
  ```
- **Row mapping:** Bir `ResultSet` satırını domain nesnesine (burada bir `record`) çevirmek.

Bu desen, Spring Data JPA'nın `JpaRepository`'sinin elle yazılmış halidir; ileride Spring'in bu
tekrarlayan kodu nasıl ortadan kaldırdığını daha iyi takdir edeceksin.

## Transaction, ilişkiler ve batch

Bazı işlemler **bölünmez bir bütün** olmalıdır. Bir sipariş; başlığı (`siparis`) ve kalemleri
(`siparis_kalem`) ile birlikte ya tamamen kaydedilmeli ya da hiç. Ortada bir hata olursa sipariş
yarım kalmamalı. **Transaction** bu "ya hepsi ya hiçbiri" garantisini verir:

```java
con.setAutoCommit(false);
try {
    // birden çok ilişkili INSERT...
    con.commit();    // hepsi başarılı -> kalıcı
} catch (Exception e) {
    con.rollback();  // bir hata -> tümünü geri al
}
```

Örnek 3 (`./Ornek3.java`) bunu uçtan uca gösterir:

- **İlişkili tablolar:** Bir siparişin başlığı ve kalemleri ayrı tablolarda; kalemler
  `siparis_id` ile bağlanır ve `JOIN` ile birlikte sorgulanır.
- **Toplu ekleme (batch):** Çok sayıda kalemi tek tek değil, `addBatch()` + `executeBatch()` ile
  toplu göndermek çok daha verimlidir.
- **Rollback:** Geçersiz bir kalem (adet=0) iş kuralını ihlal edince tüm sipariş geri alınır;
  veritabanında hiçbir iz kalmaz.

## Bağlantı havuzu (connection pooling)

Her istekte yeni bir `Connection` açıp kapatmak pahalıdır. Gerçek uygulamalar bir **bağlantı
havuzu** (HikariCP gibi) kullanır: hazır bağlantıları havuzda tutar, isteyene ödünç verir, geri
alır. Spring Boot bunu otomatik yapılandırır; sen sadece `DataSource`'u kullanırsın.

## JDBC'den ileriye

JDBC güçlüdür ama tekrarlayan "kazan kodu" (boilerplate) çoktur: aç/kapat, `ResultSet`'i nesneye
çevir, exception ele al. Spring bu yükü önce `JdbcTemplate` ile, sonra **Spring Data JPA** ile
büyük ölçüde ortadan kaldırır — ama hepsinin altında işte bu JDBC vardır.

## Özet

JDBC mimarisini ve temel CRUD'u; `PreparedStatement` ile güvenli sorguları; **DAO deseni**,
**generated keys** ve **row mapping** ile düzenli veri erişimini; **transaction**, **ilişkili
tablolar** ve **batch** ile gerçek dünya senaryolarını gördük. Bağlantı havuzuna değindik. Bu,
Java'nın veriyle buluştuğu sağlam zemindir. Sırada, projelerini ve bağımlılıklarını yöneten
araçlar: **Maven ve Gradle**.
