# Spring Data JPA

JDBC ve JdbcTemplate ile veritabanına SQL yazarak eriştik. **Spring Data JPA**, bir adım daha
ileri gider: nesneleri tablolara eşler (ORM, Hibernate ile) ve veri erişim kodunun büyük
kısmını **sen hiç yazmadan** üretir. Bir arayüz tanımlarsın, Spring onun implementasyonunu
çalışma anında oluşturur. Bu, Spring Boot uygulamalarında veritabanıyla çalışmanın en yaygın
yoludur.

> Bu bölümün örnekleri Boot'un otomatik yapılandırdığı **gömülü H2** veritabanını kullanır;
> tablolar uygulama açılışında otomatik oluşturulur. Gerçek projede `application.yml` ile
> MySQL/PostgreSQL bağlantısı verilir — kod aynı kalır.

## Entity: nesne ↔ tablo eşlemesi

Bir sınıfı `@Entity` ile işaretlersin; Hibernate onu bir tabloya eşler. `@Id` birincil anahtarı,
`@GeneratedValue` ise id'nin nasıl üretileceğini belirtir:

```java
@Entity
class Gorev {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String baslik;
    private boolean tamamlandi;
}
```

## Repository: yazmadığın kod

Asıl sihir burada. `JpaRepository<Entity, IdTipi>`'yi genişleten bir **arayüz** tanımlarsın —
gövde yok — ve Spring Data sana hazır bir implementasyon verir:

```java
interface GorevRepository extends JpaRepository<Gorev, Long> {}
```

Bu tek satırla `save`, `findAll`, `findById`, `deleteById`, `count` ve daha fazlası gelir. Örnek 1
(`./Ornek1.java`) tek satır SQL yazmadan tam bir CRUD döngüsü çalıştırır.

## Türetilmiş sorgular (derived queries) ve @Query

İhtiyacın hazır metotların ötesine geçtiğinde, Spring Data metot **adından** sorgu üretir:

```java
List<Urun> findByKategori(String kategori);
List<Urun> findByFiyatLessThan(double fiyat);
List<Urun> findByAdContainingIgnoreCase(String parca);
long countByKategori(String kategori);
List<Urun> findTop2ByOrderByFiyatDesc();
```

`findBy`, `And`, `Or`, `LessThan`, `Containing`, `IgnoreCase`, `OrderBy`, `Top/First` gibi anahtar
kelimeleri birleştirebilirsin. Metot adının ifade edemeyeceği karmaşık sorgular için **`@Query`**
ile JPQL (veya `nativeQuery = true` ile saf SQL) yazarsın:

```java
@Query("select u from Urun u where u.fiyat between :min and :max order by u.fiyat")
List<Urun> fiyatAraliginda(@Param("min") double min, @Param("max") double max);
```

Örnek 2 (`./Ornek2.java`) hem türetilmiş sorguları hem de `@Query`'yi canlı gösterir.

## İlişkiler

Gerçek veriler ilişkilidir: bir yazarın çok kitabı, bir siparişin çok kalemi vardır. JPA bunları
anotasyonlarla modeller:

- **`@OneToMany`** — bir → çok (bir yazar, çok kitap)
- **`@ManyToOne`** — çok → bir (her kitap, bir yazar)
- **`@ManyToMany`** — çok → çok (öğrenci ↔ ders)

```java
@Entity class Yazar {
    @OneToMany(mappedBy = "yazar", cascade = CascadeType.ALL) private List<Kitap> kitaplar;
}
@Entity class Kitap {
    @ManyToOne private Yazar yazar;
}
```

`mappedBy` ilişkinin sahibini (foreign key'in hangi tarafta olduğunu), `cascade` ise işlemlerin
ilişkili kayıtlara yayılıp yayılmayacağını belirler (yazarı kaydetmek kitaplarını da kaydeder).
Örnek 3 (`./Ornek3.java`) yazar-kitap ilişkisini kurar, cascade ile birlikte kaydeder ve her
yazarın kitaplarını listeler.

> **Lazy vs Eager + N+1 tuzağı:** İlişkiler varsayılan olarak (`@OneToMany`) **LAZY** yüklenir;
> ilişkili veriye transaction dışında erişirsen `LazyInitializationException` alırsın. Örnekte
> sadelik için `EAGER` kullandık. Üretimde **LAZY + JOIN FETCH** (`@Query ... join fetch`) ya da
> `@EntityGraph` tercih edilir; aksi halde her kayıt için ek sorgu atan **N+1 problemi** doğar.

## JpaRepository hiyerarşisi

`Repository` ← `CrudRepository` (temel CRUD) ← `PagingAndSortingRepository` (sayfalama/sıralama)
← `JpaRepository` (JPA'ya özel toplu işlemler, `flush`, `saveAll`...). Çoğu zaman doğrudan
`JpaRepository` kullanırsın. Sayfalama için metoda bir `Pageable` parametresi ekleyip `Page<T>`
döndürebilirsin.

## Özet

Spring Data JPA ile `@Entity` eşlemesini, `JpaRepository` arayüzünün sağladığı kodsuz CRUD'u
(Örnek 1), türetilmiş sorguları ve `@Query`'yi (Örnek 2) ve `@OneToMany`/`@ManyToOne` ilişkilerini
(Örnek 3) gerçek, çalışan bir uygulamada gördük; lazy/eager ve N+1 tuzağına değindik. Veri
katmanı artık çok güçlü. Sırada, bu API'lere gelen veriyi doğrulamak ve hataları zarifçe yönetmek:
**validation ve global exception handling**.
