# Caching, Async ve Scheduling

Bir uygulamayı hızlı ve verimli kılmanın üç güçlü aracını ele alıyoruz: tekrarlayan pahalı
işlemleri **önbelleğe almak** (`@Cacheable`), uzun süren işleri **arka plana atmak** (`@Async`)
ve belirli aralıklarla otomatik **görev çalıştırmak** (`@Scheduled`). Üçü de Spring'in AOP/proxy
mekanizması üzerine kuruludur — yani bir anotasyon eklersin, gerisini Spring halleder.

## @Cacheable — önbellekleme

Aynı veriyi tekrar tekrar hesaplamak (veritabanı sorgusu, dış API çağrısı, ağır hesap) israftır.
`@Cacheable`, bir metodun sonucunu anahtarına göre saklar; aynı argümanla ikinci çağrıda metot
**hiç çalışmaz**, sonuç doğrudan önbellekten döner:

```java
@EnableCaching // altyapıyı aç
@Cacheable("urunler")
public String detayGetir(Long id) { /* pahalı işlem */ }
```

Örnek 1 (`./Ornek1.java`) aynı id ile ilk çağrının ~600 ms, ikinci çağrının ~0 ms sürdüğünü
(önbellekten) canlı ölçer. Farklı id, anahtarı değiştirdiğinden tekrar hesaplanır.

> Spring Boot, bir cache sağlayıcısı yoksa basit bir bellek-içi (`ConcurrentMapCacheManager`)
> önbellek yapılandırır. Üretimde **Redis**, **Caffeine** gibi sağlayıcılarla dağıtık/gelişmiş
> önbellek kullanılır. İlgili anotasyonlar: `@CacheEvict` (önbelleği temizle), `@CachePut`
> (güncelle), `@Cacheable(condition=..., unless=...)`.

## @Async — asenkron çalıştırma

Bir e-posta göndermek veya rapor üretmek için kullanıcıyı bekletmek istemezsin. `@Async`, bir
metodu **ayrı bir thread'de**, çağrıyı bloklamadan çalıştırır:

```java
@EnableAsync
@Async
public CompletableFuture<String> raporUret(int no) { /* uzun iş */ }
```

Metot çağrılır çağrılmaz hemen döner; iş arka planda sürer. `CompletableFuture` döndürerek
sonucu sonra toplayabilirsin. Örnek 2 (`./Ornek2.java`) üç raporu aynı anda tetikler ve toplam
sürenin ~500 ms (sıralı olsaydı ~1500 ms) olduğunu gösterir — işler paralel koştu.

> Dikkat: `@Async` da proxy tabanlıdır; **self-invocation** (aynı sınıf içi çağrı) asenkron
> çalışmaz. Ayrıca üretimde kendi `TaskExecutor` (thread havuzu) yapılandırmanı tanımlamak
> iyidir (varsayılan havuz sınırlıdır).

## @Scheduled — zamanlanmış görevler

Periyodik işler (gece yarısı rapor, her 5 dakikada sağlık kontrolü, eski kayıt temizliği)
`@Scheduled` ile tanımlanır:

```java
@EnableScheduling
@Scheduled(fixedRate = 1500)            // her 1.5 sn
public void periyodikGorev() { ... }

@Scheduled(cron = "0 0 3 * * *")        // her gün saat 03:00 (cron ifadesi)
public void geceBakimi() { ... }
```

- `fixedRate` — sabit aralıkla (önceki bitişi beklemeden).
- `fixedDelay` — önceki bitişten itibaren belirli gecikmeyle.
- `initialDelay` — ilk çalışmadan önceki gecikme.
- `cron` — takvim bazlı ifadeyle (saat/gün/ay).

Örnek 3 (`./Ornek3.java`) iki zamanlanmış görevi başlatır; uygulama açık kaldığı birkaç saniye
boyunca tikler **arka planda otomatik** üretilir (bu örnekte çıktı, self-test'ten değil
zamanlayıcıdan gelir).

## Özet

`@Cacheable` ile pahalı işlemleri önbelleğe almayı (Örnek 1), `@Async` ile işleri arka plana
atıp paralel çalıştırmayı (Örnek 2) ve `@Scheduled` ile periyodik görevleri (Örnek 3) gerçek,
çalışan uygulamalarla gördük. Üçü de proxy tabanlıdır (self-invocation tuzağı geçerli). Bu
araçlar, uygulamanın yanıt süresini ve verimliliğini ciddi biçimde iyileştirir. Sırada,
uygulamayı izlemek ve üretime hazırlamak: **Actuator ve üretim araçları**.
