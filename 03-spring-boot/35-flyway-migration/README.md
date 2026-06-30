# Spring Boot Flyway (Veritabanı Şema Migration'ı)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Flyway bağımlılığı + bir veritabanı gerektirir;
> portalda çalışmaz. Kod/yapı tanıtım amaçlıdır.

Uygulama kodunu Git ile sürümlersin — peki **veritabanı şeması**? Bir tablo eklemek, sütun
değiştirmek gerektiğinde bunu elle SQL çalıştırarak yapmak; ekipte tutarsızlık, "hangi ortamda hangi
değişiklik yapıldı?" kaosu ve geri alma zorluğu yaratır. **Flyway**, veritabanı şemasını **sürümlü,
otomatik ve tekrarlanabilir** biçimde yöneten bir migration aracıdır. Spring Boot ile otomatik
entegredir.

## Sorun: şema değişikliklerini yönetmek

```
Geliştirici A: 'kullanici' tablosuna 'telefon' sütunu ekledi (kendi DB'sinde elle)
Geliştirici B: aynı değişiklikten habersiz...
Test ortamı:   sütun yok -> uygulama patlıyor
   -> "Bende çalışıyor" / şema sürüklenmesi (schema drift)
```

## Çözüm: sürümlü migration dosyaları

Flyway, `src/main/resources/db/migration/` altındaki **versiyonlu SQL dosyalarını** sırayla
uygular:

```
db/migration/
├── V1__ilk_sema.sql
├── V2__kullanici_telefon_ekle.sql
└── V3__siparis_tablosu.sql
```

```sql
-- V1__ilk_sema.sql
CREATE TABLE kullanici (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ad VARCHAR(100) NOT NULL,
    eposta VARCHAR(150) UNIQUE NOT NULL
);

-- V2__kullanici_telefon_ekle.sql
ALTER TABLE kullanici ADD COLUMN telefon VARCHAR(20);
```

Dosya adı kuralı: `V{sürüm}__{açıklama}.sql` (çift alt çizgi). Flyway her dosyayı **bir kez**,
sürüm sırasına göre uygular.

## Kurulum (Spring Boot ile otomatik)

```gradle
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-database-postgresql'   // veritabanına göre
```

Bağımlılığı eklemen **yeter**: Spring Boot, uygulama başlarken Flyway'i otomatik çalıştırır;
bekleyen migration'ları uygular.

## Nasıl çalışır?

1. Flyway, veritabanında bir **`flyway_schema_history`** tablosu tutar — hangi migration'ların
   uygulandığını kaydeder.
2. Uygulama başlarken, `db/migration`'daki dosyaları bu geçmişle karşılaştırır.
3. **Bekleyen** (henüz uygulanmamış) migration'ları sürüm sırasına göre uygular.
4. Her migration'ın **checksum**'ını saklar — uygulanmış bir dosya sonradan değiştirilirse hata
   verir (geçmişin değişmezliğini korur).

## Migration türleri

- **Versiyonlu (`V`):** Bir kez uygulanır (şema değişiklikleri). En yaygın.
- **Tekrarlanabilir (`R`):** Checksum değişince yeniden uygulanır (view, stored procedure, seed
  veri).
- **Geri alma (undo):** Pro sürümde; topluluk sürümünde "ileri-only" felsefe önerilir (geri almak
  için yeni bir migration yaz).

## İyi uygulamalar

- **Migration'ları asla değiştirme:** Uygulanmış bir dosyayı düzeltmek için **yeni** bir migration
  yaz (geçmiş değişmez olmalı).
- **Her değişiklik bir migration:** Şema değişikliği koddan ayrı, sürümlü ve gözden geçirilebilir
  (PR) olsun.
- **`ddl-auto=validate`:** Üretimde Hibernate'in şema üretmesini kapat (`spring.jpa.hibernate.ddl-auto=validate`);
  şemayı **Flyway yönetsin**, JPA sadece doğrulasın.
- **Ortam tutarlılığı:** Aynı migration'lar dev/test/prod'da aynı şemayı garanti eder.

## Flyway vs Liquibase

İkisi de migration aracıdır. **Flyway:** SQL-merkezli, basit, sürüm dosyaları. **Liquibase:**
XML/YAML/JSON/SQL changelog, veritabanı-bağımsız soyutlama, daha fazla özellik. Spring Boot ikisini
de otomatik destekler; tercih ekip/proje tarzına bağlıdır.

## Özet

Flyway'in veritabanı şemasını **sürümlü migration dosyalarıyla** (`V1__...sql`) otomatik, tutarlı ve
tekrarlanabilir yönettiğini öğrendik: Spring Boot ile otomatik çalışma, `flyway_schema_history` ile
takip, migration türleri ve iyi uygulamalar (değiştirme→yeni migration, `ddl-auto=validate`); Flyway
vs Liquibase. Bu, "kod gibi veritabanı" (database as code) pratiğinin temelidir. Sırada, e-posta
gönderimi (Spring Boot odaklı): **Sending Email**.
