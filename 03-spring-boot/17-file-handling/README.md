# Spring Boot Dosya İşleme (Upload / Download)

Birçok uygulama dosya alıp verir: profil fotoğrafı yükleme, CSV/Excel içe aktarma, PDF/rapor
indirme, belge yönetimi. Spring Boot, dosya yükleme (multipart) ve indirme işlemlerini basit
anotasyonlarla destekler. Bu konu, dosya yükleme ve indirmeyi ve üretimdeki dikkat noktalarını
ele alır.

## Dosya yükleme: MultipartFile

Tarayıcı/istemci, dosyayı `multipart/form-data` olarak gönderir; Spring bunu **`MultipartFile`**'a
bağlar:

```java
@PostMapping("/yukle")
public Map<String,Object> yukle(@RequestParam("dosya") MultipartFile dosya) {
    dosya.getOriginalFilename();   // istemcideki ad
    dosya.getSize();               // byte cinsinden boyut
    dosya.getContentType();        // MIME tipi (image/png ...)
    byte[] icerik = dosya.getBytes();         // bellekteki içerik
    // dosya.transferTo(hedefYol);  // doğrudan diske yaz
    ...
}
```

Birden çok dosya: `@RequestParam("dosyalar") MultipartFile[] dosyalar` veya `List<MultipartFile>`.
Örnek 1 (`./Ornek1.java`) bir dosyayı yükler (bellek-içi depo) ve meta bilgisini döndürür.

## Dosya indirme: ResponseEntity<byte[]>

İndirme için içeriği ve uygun başlıkları döndürürsün. **`Content-Disposition: attachment`**
tarayıcıya "bunu indir" der:

```java
@GetMapping("/indir/{ad}")
public ResponseEntity<byte[]> indir(@PathVariable String ad) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ad + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(icerik);
}
```

Büyük dosyalar için `byte[]` yerine **`Resource`** (`InputStreamResource`/`FileSystemResource`)
döndürmek belleği korur (akış halinde gönderir). Örnek 1 yüklenen dosyayı geri indirir ve içeriğin
aynı olduğunu doğrular.

## Yapılandırma

`application.properties` ile yükleme sınırları:

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=20MB
spring.servlet.multipart.enabled=true
```

## Üretim pratikleri ve güvenlik

- **Boyut sınırı koy:** Aksi halde büyük dosyalar belleği/diski tüketir (DoS riski). Yukarıdaki
  ayarları kullan.
- **Dosya türünü doğrula:** Yalnızca uzantıya güvenme; MIME/içerik doğrula. Çalıştırılabilir/
  tehlikeli türleri reddet.
- **Dosya adını güvenli yap:** İstemci adını doğrudan disk yoluna koyma (path traversal: `../../`
  saldırısı). Ad temizle veya rastgele üret.
- **Bellekte tutma:** Büyük dosyaları `getBytes()` ile belleğe almak yerine `transferTo`/akış
  kullan veya doğrudan bir nesne deposuna (S3, MinIO) yükle.
- **Depolama:** Üretimde dosyaları uygulama diskinde değil, **nesne deposunda** (S3/GCS) veya
  ayrı bir dosya servisinde sakla (ölçeklenme + dayanıklılık).

## Özet

Spring Boot'ta dosya işlemeyi öğrendik: `multipart/form-data` ile **yükleme** (`MultipartFile`) ve
`Content-Disposition` ile **indirme** (`ResponseEntity<byte[]>`/`Resource`; Örnek 1); boyut
sınırı yapılandırması ve güvenlik (tür doğrulama, path traversal, nesne deposu) pratikleri. Sırada,
uygulamayı farklı dillere uyarlamak: **uluslararasılaştırma (i18n)**.
