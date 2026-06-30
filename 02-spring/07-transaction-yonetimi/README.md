# Transaction Yönetimi

Bir veritabanı işlemi çoğu zaman tek bir adım değildir: para transferinde birinden düşülür,
diğerine eklenir; sipariş oluşturulurken başlık ve kalemler birlikte yazılır. Bu adımların
**hepsi başarılı olmalı ya da hiçbiri uygulanmamalı** — aksi halde veri tutarsız kalır (para
buharlaşır, sipariş yarım kalır). İşte **transaction** bu "ya hepsi ya hiçbiri" (atomiklik)
garantisini verir. Spring, transaction yönetimini hem çok temiz (bildirimsel) hem de gerektiğinde
ince ayarlı (programatik) hâle getirir.

## Önce sorunu görelim

Örnek 1 (`./Ornek1.java`) transaction olmadan bir para transferi yapar: Ada'dan 300 TL düşülür,
ama araya bir hata girince Burak'a eklenmeden işlem yarıda kalır. Sonuç: **300 TL kaybolur**,
toplam bakiye tutarsızlaşır.

```java
jdbc.update("UPDATE hesap SET bakiye = bakiye - 300 WHERE ad='Ada'"); // başarılı
throw new RuntimeException("ağ hatası");                              // araya hata
jdbc.update("UPDATE hesap SET bakiye = bakiye + 300 WHERE ad='Burak'"); // hiç çalışmadı
```

İki güncelleme bölünmez bir bütün olmalıydı. Çözüm: onları bir transaction'a sarmak.

## ACID kısaca

Transaction'lar **ACID** garantileri sağlar:

- **Atomicity (atomiklik):** Ya hepsi ya hiçbiri.
- **Consistency (tutarlılık):** Kurallar (kısıtlar) her zaman korunur.
- **Isolation (yalıtım):** Eşzamanlı transaction'lar birbirini bozmaz.
- **Durability (kalıcılık):** Commit edilen değişiklik kalıcıdır.

## Bildirimsel transaction: @Transactional

Spring'in en yaygın yolu, bir metoda `@Transactional` koymaktır. Metot bir transaction'la
**sarılır**: normal biterse **commit**, bir `RuntimeException` fırlarsa **rollback** olur. İş
kodun transaction yönetimini hiç görmez:

```java
@Transactional
public void transfer(String kimden, String kime, int tutar) {
    jdbc.update("... - tutar ... kimden");
    jdbc.update("... + tutar ... kime");
}   // metot biterse commit; hata fırlarsa hepsi geri alınır
```

Bunun için iki şey gerekir: `@EnableTransactionManagement` ve bir `PlatformTransactionManager`
bean'i (JDBC için `DataSourceTransactionManager`). Örnek 2 (`./Ornek2.java`) aynı transferi
`@Transactional` ile yapar: hata enjekte edilen transfer geri alınır (bakiyeler değişmez),
başarılı transfer commit edilir.

> **`@Transactional` proxy tabanlıdır** (AOP). Bu iki önemli sonucu doğurur:
> 1. **Self-invocation tuzağı:** Aynı sınıfın bir metodu, kendi içindeki başka bir
>    `@Transactional` metodu doğrudan çağırırsa proxy devreye girmez; transaction başlamaz.
> 2. **Rollback kuralı:** Varsayılan olarak yalnızca **unchecked** (`RuntimeException`)
>    hatalarda rollback olur. Checked exception'larda rollback istiyorsan
>    `@Transactional(rollbackFor = Exception.class)` belirtmelisin.

## Programatik transaction: TransactionTemplate

Bazen transaction sınırını koddan, daha ince kontrol ederek yönetmek istersin. `TransactionTemplate`
bunu sağlar: bir kod bloğunu transaction içinde çalıştırır; içeride `status.setRollbackOnly()` ile
elle geri alabilir veya exception fırlatabilirsin:

```java
txTemplate.execute(status -> {
    jdbc.update("...");
    if (kuralIhlali) status.setRollbackOnly(); // commit edilmez
    return null;
});
```

Örnek 3 (`./Ornek3.java`) üç senaryoyu gösterir: başarılı commit, iş kuralı ihlalinde
`setRollbackOnly()` ile geri alma ve exception ile otomatik rollback.

## İleri kavramlar

Transaction'ların davranışını ayarlayan parametreler:

- **Propagation (yayılım):** İç içe çağrılan `@Transactional` metotların davranışı. `REQUIRED`
  (varsayılan — varsa mevcut transaction'a katıl), `REQUIRES_NEW` (her zaman yeni transaction),
  `NESTED`, `SUPPORTS` vb.
- **Isolation (yalıtım düzeyi):** Eşzamanlı transaction'ların birbirini ne kadar gördüğü
  (`READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`...). Dirty/non-repeatable/phantom read
  sorunlarını dengeler.
- **readOnly:** Salt-okunur transaction'lar için optimizasyon ipucu.
- **rollbackFor / noRollbackFor:** Hangi istisnalarda rollback olacağını özelleştirir.
- **timeout:** Transaction için zaman aşımı.

## Özet

Transaction olmadan oluşan veri tutarsızlığını (Örnek 1), `@Transactional` ile bildirimsel
"ya hep ya hiç" garantisini (Örnek 2) ve `TransactionTemplate` ile programatik kontrolü (Örnek 3)
gördük; proxy temelli davranışı (self-invocation, rollback kuralı) ve propagation/isolation gibi
ileri kavramları öğrendik. Sırada, kullanıcıdan ve dış dünyadan gelen veriyi güvenle karşılamanın
yolu: **doğrulama (validation)**.
