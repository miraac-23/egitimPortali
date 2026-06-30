# Veri Erişimi: Spring JDBC ve JdbcTemplate

Neredeyse her kurumsal uygulama bir veritabanıyla konuşur. Java bölümünde ham JDBC'yi öğrendin
ve onun ne kadar "kazan kodu" (boilerplate) gerektirdiğini gördün: bağlantı aç, statement
hazırla, `ResultSet`'i gez, kaynakları kapat, `SQLException`'ı ele al... Hem de her sorguda.
Spring'in **JdbcTemplate**'i bu tekrarlayan yükü ortadan kaldırır; sen yalnızca SQL'i ve satır
eşlemesini yazarsın. Bu bölüm, Spring'in veri erişim felsefesinin giriş kapısıdır.

## Sorun: ham JDBC boilerplate'i

Ham JDBC'de basit bir okuma bile try-with-resources, `Connection`, `PreparedStatement`,
`ResultSet` ve döngü gerektirir. Bu kod her sorguda tekrarlanır, asıl niyeti gölgeler ve kaynak
sızıntısına açıktır. Örnek 1 (`./Ornek1.java`) aynı sorguyu önce ham JDBC ile (uzun), sonra
JdbcTemplate ile (tek satır) yazarak farkı somut gösterir:

```java
// Ham JDBC: bağlantı/statement/resultset + döngü + kapatma...
// JdbcTemplate: tek satır
jdbc.query("SELECT id, ad FROM urun",
           (rs, n) -> rs.getInt("id") + " " + rs.getString("ad"));
```

JdbcTemplate; bağlantı yönetimini, kaynak temizliğini ve istisna çevrimini **senin yerine** yapar.

## DataSource ve JdbcTemplate

Her şey bir **`DataSource`** ile başlar — veritabanı bağlantılarının kaynağı. Üretimde bu, bir
bağlantı havuzudur (HikariCP); örneklerde basit bir `DriverManagerDataSource` kullanıyoruz:

```java
var ds = new DriverManagerDataSource("jdbc:h2:mem:db", "sa", "");
ds.setDriverClassName("org.h2.Driver");
JdbcTemplate jdbc = new JdbcTemplate(ds);
```

Sık kullanılan metotlar:

- `update(sql, params...)` — INSERT/UPDATE/DELETE (etkilenen satır sayısı).
- `query(sql, rowMapper, params...)` — birden çok satır → `List<T>`.
- `queryForObject(sql, type, params...)` — tek değer/satır.
- `execute(sql)` — DDL.

## RowMapper ve DAO

`RowMapper<T>`, bir `ResultSet` satırını domain nesnesine çeviren küçük bir fonksiyondur; bir kez
yazıp her sorguda yeniden kullanırsın. Tüm veri erişimini bir **DAO** (veya repository) sınıfında
toplamak, iş mantığını SQL ayrıntısından ayırır:

```java
private final RowMapper<Musteri> mapper =
    (rs, n) -> new Musteri(rs.getInt("id"), rs.getString("ad"), rs.getString("sehir"));

List<Musteri> hepsi() { return jdbc.query("SELECT * FROM musteri", mapper); }
```

Örnek 2 (`./Ornek2.java`) tam bir `MusteriDao` yazar: ekleme, listeleme, id ile bulma, güncelleme.
Ayrıca **`NamedParameterJdbcTemplate`** ile `?` yerine `:ad` gibi **isimli parametreleri** gösterir
— çok parametreli sorgularda okunabilirliği artırır.

## Exception translation (istisna çevrimi)

Spring veri erişiminin sessiz ama önemli bir gücü: teknik, veritabanına özel `SQLException`'ları
(çoğu `checked` ve sürücüye göre kod/mesajı değişir) tutarlı, taşınabilir ve `unchecked` bir
hiyerarşiye — **`DataAccessException`** — çevirir:

- `DuplicateKeyException` — benzersizlik/birincil anahtar çakışması
- `DataIntegrityViolationException` — kısıt ihlali
- `EmptyResultDataAccessException` — `queryForObject` sonuç bulamadı

```java
try { jdbc.update("INSERT ... (id=1 zaten var)"); }
catch (DuplicateKeyException e) { /* anlamlı, taşınabilir */ }
```

Örnek 3 (`./Ornek3.java`) çift anahtar ve boş sonuç senaryolarında bunu gösterir. Böylece iş
kodun hangi veritabanını kullandığından bağımsız ve temiz kalır. `@Repository` ile işaretli
bean'lerde bu çeviri otomatik devreye girer (bkz. stereotype bölümü).

## JdbcTemplate'ten ORM'e

JdbcTemplate, SQL'i sen yazarken boilerplate'i alır — kontrol sende kalır. İhtiyaç büyüdükçe,
nesne-tablo eşlemesini (ORM) ve sorgu üretimini de otomatikleştiren **Spring Data JPA / Hibernate**
devreye girer; bunu Spring Boot bölümünde, gerçek entity'ler ve `JpaRepository` ile göreceğiz.
Ama altta yine bu JDBC temeli vardır.

## Özet

Ham JDBC'nin boilerplate sorununu (Örnek 1), JdbcTemplate + RowMapper ile DAO yazmayı ve isimli
parametreleri (Örnek 2) ve Spring'in istisna çevrimini (Örnek 3) öğrendik. Veri erişimi artık çok
daha temiz. Sırada, bu veri işlemlerini güvenli ve bütünlüklü kılan kritik konu: **transaction
yönetimi**.
