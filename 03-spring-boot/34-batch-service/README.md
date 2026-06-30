# Spring Boot Batch (Toplu İşleme)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Spring Batch starter + bir veritabanı (iş
> meta-verisi için) gerektirir; portalda çalışmaz. Kod tanıtım amaçlıdır.

Bazı işler kullanıcı isteğiyle değil, **büyük veri kümeleri üzerinde toplu** olarak çalışır: gece
çalışan raporlar, milyonlarca kaydın işlenmesi, CSV içe/dışa aktarma, veri taşıma, fatura üretimi.
Bunları basit bir döngüyle yazmak; yeniden başlatma, hata yönetimi, ilerleme takibi, paralellik
ihtiyaçlarında yetersiz kalır. **Spring Batch**, dayanıklı toplu işleme için tasarlanmış bir
çerçevedir.

## Temel kavramlar

- **Job:** Bir toplu iş (örn. "günlük rapor üret"). Bir veya çok step'ten oluşur.
- **Step:** İşin bir aşaması. İki tür: **chunk-oriented** (oku-işle-yaz, parça parça) veya
  **tasklet** (tek bir görev).
- **ItemReader:** Veriyi okur (dosya, DB, kuyruk).
- **ItemProcessor:** Her öğeyi işler/dönüştürür (opsiyonel).
- **ItemWriter:** Sonucu yazar (DB, dosya).
- **JobRepository:** İşlerin durumunu (başladı/bitti/hata, hangi kayda kadar işlendi) bir
  veritabanında saklar — bu sayede **yeniden başlatılabilir**.

```
Job
└─ Step (chunk: 100'erlik)
   ItemReader → ItemProcessor → ItemWriter  (100 oku, 100 işle, 100 yaz, tekrarla)
```

## Kurulum

```gradle
implementation 'org.springframework.boot:spring-boot-starter-batch'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'   // JobRepository için DB
```

## Chunk-oriented step örneği

```java
@Bean
Job kullaniciAktarimJob(JobRepository repo, Step step) {
    return new JobBuilder("kullaniciAktarim", repo).start(step).build();
}

@Bean
Step aktarimStep(JobRepository repo, PlatformTransactionManager tx,
                 ItemReader<Kullanici> reader, ItemProcessor<Kullanici,Kullanici> processor,
                 ItemWriter<Kullanici> writer) {
    return new StepBuilder("aktarim", repo)
        .<Kullanici, Kullanici>chunk(100, tx)   // 100'erlik parçalar
        .reader(reader)
        .processor(processor)                    // örn. doğrulama/dönüşüm
        .writer(writer)
        .build();
}

// CSV oku -> doğrula/dönüştür -> DB'ye yaz (reader/processor/writer bean'leri)
```

`chunk(100)` her 100 kayıtta bir transaction commit eder; bir hata olursa o parçaya kadar olanlar
güvende kalır.

## Spring Batch'in çözdüğü zorluklar

- **Yeniden başlatma:** İş yarıda kalırsa, kaldığı yerden devam eder (JobRepository'deki duruma
  göre) — milyonlarca kaydı baştan işlemezsin.
- **Hata yönetimi:** Belirli hataları atla (skip), yeniden dene (retry), eşik aşılınca durdur.
- **Ölçek:** Çok thread'li step, paralel step, uzaktan parçalama (remote chunking/partitioning).
- **İzleme:** Her job/step'in durumu, işlenen/atlanan kayıt sayıları kalıcı kayıtlıdır.
- **Tetikleme:** Zamanlanmış (`@Scheduled` — topic 05), olay üzerine veya komut satırından.

## Ne zaman Spring Batch?

- **Evet:** Büyük hacimli, periyodik, dayanıklılık/yeniden başlatma gereken toplu işler (ETL,
  raporlama, veri taşıma, mutabakat).
- **Hayır:** Küçük/anlık işler için aşırı kalır — basit bir `@Scheduled` metot veya bir kuyruk
  tüketicisi yeter.

## Alternatifler

- **Spring Batch + Spring Cloud Data Flow:** Büyük ölçekli veri hatları orkestrasyon.
- **Hafif zamanlı işler:** `@Scheduled` (topic 05).
- **Akış (streaming) işleme:** Kafka Streams / Spark (sürekli veri için).

## Özet

Spring Batch'in büyük hacimli toplu işleme için dayanıklı bir çerçeve olduğunu öğrendik: Job/Step/
ItemReader-Processor-Writer kavramları, chunk-oriented işleme (parça parça commit), JobRepository
ile yeniden başlatma ve çözdüğü zorluklar (hata yönetimi, ölçek, izleme); ne zaman kullanılıp
kullanılmayacağı. Sırada, veritabanı şemasını sürümleme: **Flyway**.
