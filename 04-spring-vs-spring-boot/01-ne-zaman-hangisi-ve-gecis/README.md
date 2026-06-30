# Ne Zaman Hangisi ve Geçiş

Temel farkları gördükten sonra pratik soru şu: yeni bir projede hangisini seçmeli, var olan düz
Spring projesini Boot'a taşımalı mı, taşırsan nasıl? Bu bölüm bu kararları netleştirir.

## Hangisini seçmeli?

Günümüzde **yeni projelerin büyük çoğunluğu için doğru cevap Spring Boot'tur.** Kurulum yükünü
kaldırır, üretim araçlarını hazır getirir ve seni iş mantığına odaklar. Yine de tabloyu bilmek iyidir:

| Durum | Öneri |
|-------|-------|
| Yeni web/REST uygulaması, mikroservis | **Spring Boot** (neredeyse her zaman) |
| Hızlı prototip / MVP | **Spring Boot** (Initializr ile dakikalar) |
| Standart altyapı (DB, web, güvenlik) | **Spring Boot** (auto-config) |
| Mevcut, büyük, özel yapılandırılmış legacy sistem | Düz Spring'de kalmak mantıklı olabilir |
| Çok özel/sıra dışı altyapı, tam manuel kontrol şart | Düz Spring (veya Boot + bol özelleştirme) |
| Spring olmadan, minimal bağımlılık | Belki ikisi de değil (saf Java/küçük kütüphane) |

> Kritik nokta: **Spring Boot, Spring'in yerine geçmez; onu kolaylaştırır.** Boot'u seçmek
> Spring'in gücünden vazgeçmek değildir.

## Kontrol kaybı korkusu: yersiz

Yeni başlayanların en büyük endişesi: "Boot her şeyi otomatik yaparsa kontrolü kaybeder miyim?"
Hayır. Spring Boot **fikir sahibidir (opinionated) ama dayatmacı değildir**:

- Bir bean tanımlamazsan Boot makul bir **varsayılan** sağlar.
- Sen kendi bean'ini tanımlarsan **seninki kazanır** — auto-config `@ConditionalOnMissingBean`
  ile geri çekilir.
- İstemediğin bir auto-config'i `@SpringBootApplication(exclude = ...)` ile **kapatabilirsin**.
- Her ayarı `application.yml` veya `@Bean` ile **değiştirebilirsin**.

Örnek 1 (`./Ornek1.java`) bunu gösterir: kendi `SelamlamaStratejisi` bean'ini tanımlarız ve
Boot'un varsayılanı değil bizimki kullanılır. Yani Boot'a geçmek, Spring kontrolünü kaybetmek
değildir.

## Düz Spring'den Spring Boot'a geçiş

Var olan bir Spring projesini Boot'a taşımak kademeli yapılabilir:

1. **Bağımlılıkları starter'lara çevir:** Tek tek Spring bağımlılıkları yerine
   `spring-boot-starter-*` kullan; sürüm yönetimini Boot BOM'una bırak.
2. **Bir `@SpringBootApplication` giriş noktası ekle:** `main` + `SpringApplication.run(...)`.
3. **Manuel yapılandırmayı azalt:** Elle yazdığın `DataSource`, `JdbcTemplate`, `EntityManager`,
   `DispatcherServlet` gibi bean'leri sil; Boot bunları otomatik kurar. Yalnızca gerçekten özel
   olanları `@Bean` olarak bırak.
4. **XML'i Java config'e taşı:** `application.yml` + `@Configuration` kullan.
5. **Gömülü sunucuya geç:** Harici Tomcat/WAR yerine gömülü sunucu + `java -jar` (executable jar).
6. **Üretim araçlarını ekle:** Actuator, profiller, dışsal yapılandırma.
7. **Adım adım doğrula:** Her aşamada testleri çalıştır; davranış değişikliklerini yakala.

> Tipik kazanım: yüzlerce satır yapılandırma kodu ve XML, birkaç satır + `application.yml`'e iner.
> Bağımlılık sürüm çatışmaları büyük ölçüde ortadan kalkar.

## Özet: bir bütünün iki yüzü

Bu eğitim boyunca önce Spring çekirdeğini (IoC/DI, AOP, veri, transaction, validation), sonra
Spring Boot'u (auto-config, REST, JPA, güvenlik, üretim, test) öğrendik. Bu son bölümde ikisini
karşılaştırdık ve gördük ki:

- **Spring**, kurumsal Java'nın sağlam temelidir; her şeyi yapabilirsin ama her şeyi sen kurarsın.
- **Spring Boot**, bu temelin üzerine "akıllı varsayılanlar" koyar; hızlı, üretime hazır ve hâlâ
  tümüyle özelleştirilebilir.

Doğru zihniyet: **"Spring'i öğren, Spring Boot ile uygula."** Boot'un sihrini anlamak, aslında
altındaki Spring'i anlamaktır — ki bu eğitimde tam olarak onu yaptık.

Tebrikler: Java'dan Spring'e, Spring Boot'tan üretim ve teste kadar uçtan uca bir yolculuğu
tamamladın. 🎉
