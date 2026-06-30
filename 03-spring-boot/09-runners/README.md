# Spring Boot Runners (Başlangıç Görevleri)

Bir uygulama başladığında, ilk istekten önce **bir kez** çalışması gereken işler olur: önbellek
ısıtma, başlangıç verisi yükleme (seed), bağlantı kontrolü, bir kuyruğu dinlemeye başlama, tek
seferlik geçiş (migration). Spring Boot bunun için iki standart arayüz sunar: **`CommandLineRunner`**
ve **`ApplicationRunner`**. İkisi de uygulama tamamen ayağa kalktıktan sonra otomatik çalışır.

## CommandLineRunner

Tek metodu `run(String... args)`'tır; uygulamaya verilen **ham** komut satırı argümanlarını alır:

```java
@Bean
CommandLineRunner init() {
    return args -> {
        System.out.println("Uygulama başladı, hazırlık yapılıyor...");
    };
}
```

## ApplicationRunner

`CommandLineRunner` ile aynı amaca hizmet eder, ama argümanları **ayrıştırılmış** olarak
(`ApplicationArguments`) alır — opsiyonları (`--debug`) ve değerleri ayırır:

```java
@Bean
ApplicationRunner runner() {
    return args -> {
        args.getOptionNames();          // --key biçimindeki opsiyonlar
        args.containsOption("debug");   // --debug verildi mi?
        args.getNonOptionArgs();        // opsiyon olmayan argümanlar
    };
}
```

## Çalışma sırası: @Order

Birden çok runner varsa, sıralarını **`@Order`** belirler (küçük değer önce). Örnek 1
(`./Ornek1.java`) iki `CommandLineRunner` ve bir `ApplicationRunner`'ı `@Order` ile sıralı çalıştırır;
çıktılar uygulama başlangıcında görünür.

| | `CommandLineRunner` | `ApplicationRunner` |
|---|---------------------|----------------------|
| Argüman | Ham `String...` | Ayrıştırılmış `ApplicationArguments` |
| Ne zaman | Uygulama hazır olunca, bir kez | Aynı |
| Sıralama | `@Order` | `@Order` |

## Ne zaman kullanılır, ne zaman kullanılmaz?

**Kullan:** Başlangıç verisi yükleme (geliştirme/test), önbellek ısıtma, başlangıç doğrulamaları,
bir dış sistemi dinlemeye başlama, CLI tarzı toplu işler (batch) için tetikleyici.

**Dikkat / alternatifler:**
- Bir bean oluşturulurken yapılacak başlatma için `@PostConstruct` daha uygundur (runner tüm uygulama
  hazır olunca çalışır, bean'e özel değildir).
- Üretimde şema/veri geçişleri için `CommandLineRunner` yerine **Flyway/Liquibase** kullan (sürümlü,
  güvenli).
- Runner içinde atılan istisna **uygulamanın başlamasını engeller** (bu bazen istenir — "geçersiz
  yapılandırmayla başlama").

## Özet

Spring Boot'ta uygulama başlangıcında bir kez kod çalıştırmanın iki yolunu öğrendik: ham argümanlı
**`CommandLineRunner`** ve ayrıştırılmış argümanlı **`ApplicationRunner`** (Örnek 1); `@Order` ile
sıralama ve ne zaman kullanılıp kullanılmayacağını (Flyway/`@PostConstruct` alternatifleri) gördük.
Sırada, uygulamayı katmanlara ayıran yapı taşı: **servis bileşenleri**.
