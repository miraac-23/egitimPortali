# Spring AOP (Aspect-Oriented Programming)

Her ciddi uygulamada, asıl iş mantığının yanında tekrar eden "yan görevler" vardır: loglama,
güvenlik kontrolü, transaction yönetimi, performans ölçümü, önbellekleme. Bunlara **kesişen
ilgiler (cross-cutting concerns)** denir; çünkü tek bir yere ait değildirler, uygulamanın her
köşesine yayılırlar. AOP (Aspect-Oriented Programming), bu yan görevleri iş mantığından **ayırıp**
tek bir yerde toplamanın yoludur. Spring'in en güçlü ve en çok kullanılan özelliklerinden
biridir — `@Transactional` ve method security bile altta AOP'tur.

## Önce sorunu görelim

Örnek 1 (`./Ornek1.java`) bir `SiparisServisi`'nin her metoduna loglama ve süre ölçümü kodunun
elle serpiştirildiği hâlini gösterir:

```java
void siparisOlustur(String urun) {
    long t0 = System.currentTimeMillis();
    System.out.println("[LOG] başladı");
    // ... asıl iş (tek satır) ...
    System.out.println("[LOG] bitti " + (System.currentTimeMillis() - t0));
}
```

Bu yaklaşımın dertleri açıktır: aynı kod her metoda kopyalanır (DRY ihlali), asıl iş mantığı log
satırlarının arasında kaybolur ve log formatını değiştirmek istediğinde onlarca metoda dokunman
gerekir. Loglama, iş mantığının **sorumluluğu değildir** — ayrılmalıdır.

## AOP terimleri

AOP'u konuşmak için ortak bir sözlük gerekir:

- **Aspect:** Kesişen ilgiyi içeren modül (ör. `LoglamaAspect`).
- **Advice:** Aspect'in *ne zaman* çalışacağı. Türleri: `@Before`, `@After`, `@AfterReturning`,
  `@AfterThrowing`, `@Around`.
- **Pointcut:** Aspect'in *nerede* (hangi metotlarda) çalışacağını tanımlayan ifade.
- **JoinPoint:** Advice'ın uygulandığı an/nokta (çalışan metot, argümanları vb.).

### Pointcut ifadeleri

Pointcut'lar çeşitli biçimlerde yazılır:

```java
@Before("execution(* com.ornek.servis.*.*(..))")  // paket/metot imzasına göre
@Before("bean(siparisServisi)")                    // bean adına göre (Spring'e özgü, pratik)
@Before("@annotation(com.ornek.Loglanabilir)")     // belirli bir anotasyona göre
```

## Çözüm: @Before / @After

Örnek 2 (`./Ornek2.java`) loglamayı `LoglamaAspect` adlı bir aspect'e taşır. İş mantığı artık
tertemizdir; loglama tek bir yerdedir ve `@Before`/`@After` ile ilgili tüm metotlara **otomatik**
uygulanır:

```java
@Aspect
class LoglamaAspect {
    @Before("bean(siparisServisi)")
    void once(JoinPoint jp) { System.out.println("[LOG] başlıyor: " + jp.getSignature().getName()); }
    @After("bean(siparisServisi)")
    void sonra(JoinPoint jp) { System.out.println("[LOG] bitti"); }
}
```

> **Spring AOP nasıl çalışır? (Proxy)** Spring, aspect uygulanan bean'in etrafına bir **proxy**
> (vekil) nesne sarar. Bean'i container'dan aldığında aslında proxy'yi alırsın; bir metot
> çağırdığında önce proxy araya girer, advice'ı çalıştırır, sonra gerçek metoda geçer. Bu yüzden
> AOP yalnızca **bean üzerinden, dışarıdan** yapılan çağrılarda devreye girer; aynı sınıfın kendi
> içinden (self-invocation) yaptığı çağrılarda devreye girmez — sık karşılaşılan bir tuzaktır.

## En güçlüsü: @Around

`@Around`, metodu tamamen **sarar**: `proceed()` çağrısının öncesinde ve sonrasında kod
çalıştırabilir, hatta `proceed()`'i hiç çağırmayarak metodu **engelleyebilir**. Örnek 3
(`./Ornek3.java`) tek bir aspect'le hem süre ölçümü yapar hem yetki kontrolü uygular:

```java
@Around("bean(bankaServisi)")
Object denetle(ProceedingJoinPoint pjp) throws Throwable {
    if (!yetkili()) return null;          // proceed() yok -> metot ENGELLENDİ
    long t0 = System.nanoTime();
    Object sonuc = pjp.proceed();         // asıl metodu çalıştır
    log(System.nanoTime() - t0);
    return sonuc;
}
```

Yetkili kullanıcı transferi yapabilir ve süresi ölçülür; yetkisiz kullanıcının çağrısı ise hiç
çalıştırılmadan reddedilir. İş mantığı (`BankaServisi`) bunların hiçbirini bilmez.

## AOP'un gerçek dünyadaki yeri

Spring'in günlük olarak kullandığın birçok özelliği aslında AOP üzerine kuruludur:

- **`@Transactional`** → metodu bir transaction'la sarar (commit/rollback).
- **`@Cacheable`** → metot sonucunu önbelleğe alır, ikinci çağrıda metoda hiç girmez.
- **Method security** (`@PreAuthorize`) → çağrıdan önce yetki kontrolü.
- **`@Async`** → metodu ayrı bir thread'de çalıştırır.

Hepsi `@Around` mantığıyla, proxy üzerinden çalışır. Bu yüzden AOP'u anlamak, bu özelliklerin
neden bazen "beklenmedik" davrandığını (ör. self-invocation tuzağı) anlamanı da sağlar.

## Özet

Kesişen ilgilerin yarattığı kod kirliliğini (Örnek 1), bunları `@Before`/`@After` ile bir
aspect'e taşımayı (Örnek 2) ve `@Around` ile metodu sarıp ölçme/engelleme gücünü (Örnek 3)
gördük; Spring AOP'un proxy temelli çalıştığını ve self-invocation tuzağını öğrendik. Bu, hem
kendi kesişen ilgilerini temizlemeni hem de Spring'in `@Transactional`/`@Cacheable` gibi
özelliklerini bilinçli kullanmanı sağlar. Sırada, Spring çekirdeğinin son durağı: **SpEL, olay
mekanizması (events) ve profiller**.
